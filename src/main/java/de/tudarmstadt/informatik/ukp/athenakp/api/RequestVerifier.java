package de.tudarmstadt.informatik.ukp.athenakp.api;

import java.util.HashMap;
import java.util.Map;

import de.tudarmstadt.informatik.ukp.athenakp.api.ast.AttributeNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.NumberAttributeNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.RequestEntityNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.RequestHierarchyNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.RequestNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.StringAttributeNode;
import de.tudarmstadt.informatik.ukp.athenakp.exception.VerificationFailedException;

public class RequestVerifier {
	//					     entity		field name   number amount
	private static final Map<String,Map<String		,Integer		>> NUMERICAL_FIELDS = new HashMap<>(); //denotes which fields (value) of an entity (key) only accept numerical values

	static { //preprocessing of number attributes, everything else accepts strings
		Map<String,Integer> conferenceMap = new HashMap<>();
		Map<String,Integer> eventMap = new HashMap<>();
		Map<String,Integer> paperMap = new HashMap<>();
		Map<String,Integer> personMap = new HashMap<>();

		conferenceMap.put("startDate", 3);
		conferenceMap.put("endDate", 3);
		NUMERICAL_FIELDS.put("conference", conferenceMap);
		eventMap.put("begin", 5);
		eventMap.put("end", 5);
		NUMERICAL_FIELDS.put("event", eventMap);
		paperMap.put("releaseDate", 3);
		NUMERICAL_FIELDS.put("paper", paperMap);
		personMap.put("birthday", 3);
		personMap.put("obit", 3);
		NUMERICAL_FIELDS.put("person", personMap);
		NUMERICAL_FIELDS.put("author", personMap);
	}

	/**
	 * Checks whether the attribue values in the request are correct. If there is no exception thrown, the verification was successful
	 * @param tree The abstract syntax tree that depicts the API request
	 * @throws VerificationFailedException If the verification fails
	 */
	public static void verify(RequestNode tree) throws VerificationFailedException {
		//loop through the joins to get to the attributes
		for(RequestHierarchyNode hierarchyEntry : tree.getHierarchy()) {
			for(RequestEntityNode entity : hierarchyEntry.getEntities()) {
				String entityName = entity.getEntityName().getString();
				boolean hasNumericalFields = NUMERICAL_FIELDS.containsKey(entityName);


				//loop through the attributes and check each attribute's value of validity
				for(AttributeNode attr : entity.getAttributes()) {
					//check correct name

					//check correct value
					if(!hasNumericalFields && attr instanceof NumberAttributeNode)
						throw new VerificationFailedException("Expected a string for attribute " + attr.getName().getString() + " but got " + ((NumberAttributeNode)attr).valuesToString());
					else if(hasNumericalFields && attr instanceof NumberAttributeNode) {
						if(!entityContainsNumericalField(entityName, attr.getName().getString()))
							throw new VerificationFailedException("Expected a string for attribute " + attr.getName().getString() + " but got " + ((NumberAttributeNode)attr).valuesToString());
						else if(((NumberAttributeNode)attr).getNumbers().size() != NUMERICAL_FIELDS.get(entityName).get(attr.getName().getString()))
							throw new VerificationFailedException("Unexpected amount of numbers given for attribute " + attr.getName().getString() + ". " +
									"Got " + ((NumberAttributeNode)attr).getNumbers().size() + ", need " + NUMERICAL_FIELDS.get(entityName).get(attr.getName().getString()));
					}
					else if(hasNumericalFields && attr instanceof StringAttributeNode) {
						if(entityContainsNumericalField(entityName, attr.getName().getString()))
							throw new VerificationFailedException("Expected " + NUMERICAL_FIELDS.get(entityName).get(attr.getName().getString()) +" number(s) for attribute " + attr.getName().getString() + " but got " + ((StringAttributeNode)attr).getValue().getString());
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
		for(String fieldName : NUMERICAL_FIELDS.get(entity).keySet()) {
			if(fieldName.equals(theField))
				return true;
		}

		return false;
	}
}
