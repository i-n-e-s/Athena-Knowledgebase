package de.tudarmstadt.informatik.ukp.athenakp.api;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.JoinTable;

import de.tudarmstadt.informatik.ukp.athenakp.api.ast.AttributeNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.NumberAttributeNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.RequestEntityNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.RequestHierarchyNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.RequestNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.StringAttributeNode;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Author;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Conference;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Event;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Hierarchy;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Institution;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Paper;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Person;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Session;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Subsession;
import de.tudarmstadt.informatik.ukp.athenakp.exception.VerificationFailedException;

public class RequestVerifier {
	//					     entity		field name
	private static final Map<String,List<String>> ATTRIBUTES = new HashMap<>(); //denotes which fields (value) an entity (key) has
	//					     entity		field name	 number amount
	private static final Map<String,Map<String		,Integer		>> NUMERICAL_ATTRIBUTES = new HashMap<>(); //denotes which fields (value) of an entity (key) only accept numerical values
	//					     entity		entity being stored	 field name
	private static final Map<String,Map<String				,String		>> SET_ATTRIBUTES = new HashMap<>(); //denotes which fields (value) of an entity (key) are sets

	static { //preprocessing of attributes for verification, this code only runs once
		Class<?>[] models = {
				Author.class,
				Conference.class,
				Event.class,
				Institution.class,
				Paper.class,
				Person.class,
				Session.class,
				Subsession.class
		};

		for(Class<?> clazz : models) {
			String entityName = clazz.getSimpleName().toLowerCase();
			List<String> attributeList = new ArrayList<>();
			Map<String,Integer> numberAttributeMap = new HashMap<>();
			Map<String,String> setAttributeMap = new HashMap<>();

			for(Field field : clazz.getDeclaredFields()) {
				String fieldTypeName = field.getType().getName();

				if(field.isAnnotationPresent(Column.class)) {
					String columnName = field.getAnnotation(Column.class).name();

					attributeList.add(columnName.isEmpty() ? field.getName().toLowerCase() : columnName.toLowerCase()); //if isEmpty(), name of Column has not been set in the annotation. as per javadoc of Column the column name in this case is the field name

					//special cases needed for verification and request generation
					if(fieldTypeName.equals(java.time.LocalDateTime.class.getName()))
						numberAttributeMap.put(field.getName(), 5);
					else if(fieldTypeName.equals(java.time.LocalDate.class.getName()))
						numberAttributeMap.put(field.getName(), 3);
					else if(fieldTypeName.equals(de.tudarmstadt.informatik.ukp.athenakp.database.models.EventCategory.class.getName()))
						numberAttributeMap.put(field.getName(), 1);
				}
				else if(field.isAnnotationPresent(JoinTable.class) && field.isAnnotationPresent(Hierarchy.class)) {
					if(fieldTypeName.equals(java.util.Set.class.getName()))
						setAttributeMap.put(field.getName(), field.getAnnotation(Hierarchy.class).entityName());
				}
			}

			//check not empty to make containsKey checks possible
			if(!attributeList.isEmpty())
				ATTRIBUTES.put(entityName, attributeList);

			if(!numberAttributeMap.isEmpty())
				NUMERICAL_ATTRIBUTES.put(entityName, numberAttributeMap);

			if(!setAttributeMap.isEmpty())
				SET_ATTRIBUTES.put(entityName, setAttributeMap);
		}
	}

	/**
	 * Checks whether the attribue values in the request are correct. If there is no exception thrown, the verification was successful
	 * @param tree The abstract syntax tree that depicts the API request
	 * @throws VerificationFailedException If the verification fails
	 */
	public static void verify(RequestNode tree) throws VerificationFailedException {
		String previousEntityName = null;
		//loop through the joins to get to the attributes
		for(RequestHierarchyNode hierarchyEntry : tree.getHierarchy()) {
			for(RequestEntityNode entity : hierarchyEntry.getEntities()) {
				String entityName = entity.getEntityName().getString();

				//checking if the entity exists
				if(!ATTRIBUTES.containsKey(entityName))
					throw new VerificationFailedException("Unkown entity " + entityName + "!");
				//check if the hierarchy is valid
				else if(previousEntityName != null && (!SET_ATTRIBUTES.containsKey(previousEntityName) || !SET_ATTRIBUTES.get(previousEntityName).containsKey(entityName)))
					throw new VerificationFailedException("Entity " + previousEntityName + " does not have a hierarchical relationship with entity " + entityName + "!");

				boolean hasNumericalFields = NUMERICAL_ATTRIBUTES.containsKey(entityName);

				//loop through the attributes and check each attribute's value of validity
				for(AttributeNode attr : entity.getAttributes()) {
					//check if attribute exists
					if(!ATTRIBUTES.get(entityName).contains(attr.getName().getString()))
						throw new VerificationFailedException("Unknown attribute " + attr.getName().getString() + " for entity " + entityName + "!");

					//check correct value
					if(!hasNumericalFields && attr instanceof NumberAttributeNode)
						throw new VerificationFailedException("Expected a string for attribute " + attr.getName().getString() + " but got " + ((NumberAttributeNode)attr).valuesToString());
					else if(hasNumericalFields && attr instanceof NumberAttributeNode) {
						//numerical attribute found, but should be a string attribute
						if(!entityContainsNumericalField(entityName, attr.getName().getString()))
							throw new VerificationFailedException("Expected a string for attribute " + attr.getName().getString() + " but got " + ((NumberAttributeNode)attr).valuesToString());
						//incorrect amount of numbers
						else if(((NumberAttributeNode)attr).getNumbers().size() != NUMERICAL_ATTRIBUTES.get(entityName).get(attr.getName().getString()))
							throw new VerificationFailedException("Unexpected amount of numbers given for attribute " + attr.getName().getString() + ". " +
									"Got " + ((NumberAttributeNode)attr).getNumbers().size() + ", need " + NUMERICAL_ATTRIBUTES.get(entityName).get(attr.getName().getString()));
					}
					else if(hasNumericalFields && attr instanceof StringAttributeNode) {
						//string attribute found, but should be a numerical attribute
						if(entityContainsNumericalField(entityName, attr.getName().getString()))
							throw new VerificationFailedException("Expected " + NUMERICAL_ATTRIBUTES.get(entityName).get(attr.getName().getString()) +" number(s) for attribute " + attr.getName().getString() + " but got " + ((StringAttributeNode)attr).getValue().getString());
					}
				}
			}
		}
	}

	/**
	 * Checks whether the given field of the given entity is a numerical field
	 * @param entity The name of the entity to check the field of
	 * @param theField The name of the field of the entity to check
	 * @return true if the given field is numerical and a member of the given entity, false otherwhise
	 */
	private static boolean entityContainsNumericalField(String entity, String theField) {
		for(String fieldName : NUMERICAL_ATTRIBUTES.get(entity).keySet()) {
			if(fieldName.equals(theField))
				return true;
		}

		return false;
	}
}
