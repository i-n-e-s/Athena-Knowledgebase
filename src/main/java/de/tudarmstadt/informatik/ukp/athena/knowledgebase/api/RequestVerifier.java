package de.tudarmstadt.informatik.ukp.athena.knowledgebase.api;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.AttributeNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.NumberAttributeNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.RequestEntityNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.RequestFunction;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.RequestHierarchyNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.RequestNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.StringAttributeNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Conference;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Hierarchy;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Institution;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Paper;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Person;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Session;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.SessionPart;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Workshop;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.exception.VerificationFailedException;

public class RequestVerifier {
	//<entity,	field name>
	private static final Map<String,List<String>> ATTRIBUTES = new HashMap<>(); //denotes which fields (value) an entity (key) has
	//<entity, <field name, number amount>>
	private static final Map<String,Map<String, Integer>> NUMERICAL_ATTRIBUTES = new HashMap<>(); //denotes which fields (value) of an entity (key) only accept numerical values
	//<entity, <entity being stored, field name>>
	private static final Map<String,Map<String, String>> SET_ATTRIBUTES = new HashMap<>(); //denotes which fields (value) of an entity (key) are sets
	private String resultEntity;

	static { //preprocessing of attributes for verification, this code only runs once
		Class<?>[] models = {
				Conference.class,
				Institution.class,
				Paper.class,
				Person.class,
				Session.class,
				SessionPart.class,
				Workshop.class
		};

		for(Class<?> clazz : models) {
			String entityName = clazz.getSimpleName().toLowerCase();
			List<String> attributeList = new ArrayList<>();
			Map<String,Integer> numberAttributeMap = new HashMap<>();
			Map<String,String> setAttributeMap = new HashMap<>();

			//search through the fields in the above entity...
			for(Field field : clazz.getDeclaredFields()) {
				String fieldTypeName = field.getType().getName();

				//...to see which one is a column in the database
				if(field.isAnnotationPresent(Column.class)) {
					String columnName = field.getAnnotation(Column.class).name();

					attributeList.add(columnName.isEmpty() ? field.getName() : columnName); //if isEmpty(), name of Column has not been set in the annotation. as per javadoc of Column the column name in this case is the field name

					//special cases needed for verification and request generation
					if(fieldTypeName.equals(java.time.LocalDateTime.class.getName()))
						numberAttributeMap.put(field.getName(), 5);
					else if(fieldTypeName.equals(java.time.LocalDate.class.getName()))
						numberAttributeMap.put(field.getName(), 3);
					else if(fieldTypeName.equals(de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.SessionCategory.class.getName()) || fieldTypeName.equals("long"))
						numberAttributeMap.put(field.getName(), 1);
				}
				//custom annotation to manage hierarchy between entities and collections
				else if(field.isAnnotationPresent(Hierarchy.class)) {
					if(fieldTypeName.equals(java.util.Set.class.getName()))
						setAttributeMap.put(field.getAnnotation(Hierarchy.class).entityName(), field.getName());
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
	 * @param tree The abstract syntax tree that depicts the API request, non-null
	 * @throws VerificationFailedException If the verification fails
	 */ //longer than 40 lines due to comments
	public void verify(RequestNode tree) throws VerificationFailedException {
		String previousEntityName = null;

		//loop through the joins to get to the attributes
		for(RequestHierarchyNode hierarchyEntry : tree.getHierarchy()) {
			RequestEntityNode entity = hierarchyEntry.getEntity();

			if(entity == null)
				throw new VerificationFailedException("Entity at index " + hierarchyEntry.tokenIndex + " is null! Perhaps your request ends in a slash (it shouldn't)?");

			String entityName = entity.getEntityName().getString();

			//checking if the entity exists
			if(!ATTRIBUTES.containsKey(entityName))
				throw new VerificationFailedException("Unkown entity " + entityName + "!");
			//check if the hierarchy is valid
			else if(previousEntityName != null && (!SET_ATTRIBUTES.containsKey(previousEntityName) || !SET_ATTRIBUTES.get(previousEntityName).containsKey(entityName)))
				throw new VerificationFailedException("Entity " + previousEntityName + " does not have a hierarchical relationship with entity " + entityName + "!");

			previousEntityName = entityName;

			//loop through the attributes and check each attribute's value of validity
			for(AttributeNode attr : entity.getAttributes()) {
				String attrName = attr.getName().getString();

				//check if attribute exists
				if(!ATTRIBUTES.get(entityName).contains(attrName))
					throw new VerificationFailedException("Unknown attribute " + attrName + " for entity " + entityName + "!");

				//check correct value
				if(attr instanceof NumberAttributeNode) {
					//numerical attribute found, but should be a string attribute
					if(!entityContainsNumericalField(entityName, attrName))
						throw new VerificationFailedException("Expected a string for attribute " + attrName + " but got " + ((NumberAttributeNode)attr).valuesToString());
					//incorrect amount of numbers
					else if(((NumberAttributeNode)attr).getNumbers().size() != NUMERICAL_ATTRIBUTES.get(entityName).get(attrName))
						throw new VerificationFailedException("Unexpected amount of numbers given for attribute " + attrName + ". " + "Got " + ((NumberAttributeNode)attr).getNumbers().size() + ", need " + NUMERICAL_ATTRIBUTES.get(entityName).get(attrName));
				}
				else if(attr instanceof StringAttributeNode) {
					//string attribute found, but should be a numerical attribute
					if(entityContainsNumericalField(entityName, attrName))
						throw new VerificationFailedException("Expected " + NUMERICAL_ATTRIBUTES.get(entityName).get(attrName) +" number(s) for attribute " + attrName + " but got " + ((StringAttributeNode)attr).getValue().getString());
				}
			}
		}

		resultEntity = previousEntityName;


		if(tree.getFunction() == RequestFunction.ENHANCE && !resultEntity.equals("paper") && !resultEntity.equals("person"))
			throw new VerificationFailedException("Entity " + resultEntity + " cannot be enhanced with Semantic Scholar data.");
	}

	/**
	 * Checks whether the given field of the given entity is a numerical field
	 * @param entity The name of the entity to check the field of, non-null
	 * @param theField The name of the field of the entity to check, non-null
	 * @return true if the given field is numerical and a member of the given entity, false otherwhise
	 */
	public static boolean entityContainsNumericalField(String entity, String theField) {
		for(String fieldName : NUMERICAL_ATTRIBUTES.get(entity).keySet()) {
			if(fieldName.equals(theField))
				return true;
		}

		return false;
	}

	/**
	 * @return The name of the entity that this request will return
	 */
	public String getResultEntity()
	{
		return resultEntity;
	}
}
