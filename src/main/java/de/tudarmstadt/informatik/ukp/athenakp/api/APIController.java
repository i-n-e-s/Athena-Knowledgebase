package de.tudarmstadt.informatik.ukp.athenakp.api;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import de.tudarmstadt.informatik.ukp.athenakp.api.ast.AttributeNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.NumberAttributeNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.RequestEntityNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.RequestHierarchyNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.RequestNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.StringAttributeNode;
import de.tudarmstadt.informatik.ukp.athenakp.exception.SyntaxException;
import de.tudarmstadt.informatik.ukp.athenakp.exception.VerificationFailedException;

@RestController
public class APIController {
	//					     entity		field name   number amount
	private static final Map<String,Map<String		,Integer		>> NUMERICAL_FIELDS = new HashMap<>(); //denotes which fields (value) of an entity (key) only accept numerical values

	//TODO use "most efficient" map
	static { //preprocessing of number attributes, everything else accepts strings
		Map<String,Integer> conferenceMap = new HashMap<>();
		Map<String,Integer> eventMap = new HashMap<>();
		Map<String,Integer> paperMap = new HashMap<>();
		Map<String,Integer> personMap = new HashMap<>();
		Map<String,Integer> subsessionMap = new HashMap<>();

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
		subsessionMap.put("begin", 5);
		subsessionMap.put("end", 5);
		NUMERICAL_FIELDS.put("subsession", subsessionMap);
	}

	@RequestMapping("/**") //matches the complete path (containing all subpaths), just make sure that there are no ? in there!!
	public Object apiConnector(HttpServletRequest request) { //the argument contains everything that was not matched to any other argument

		try {
			//scan and parse the request
			RequestNode tree = new RequestParser(new RequestScanner(request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString()).scan()).parse();

			try {
				verifyRequest(tree);
			}
			catch(VerificationFailedException e) {
				return e.getMessage();
			}

			return tree.toString();
		}
		catch(SyntaxException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}

	/**
	 * Checks whether the attribue values in the request are correct. If there is no exception thrown, the verification was successful
	 * @param tree The abstract syntax tree that depicts the API request
	 * @throws VerificationFailedException If the verification fails
	 */
	public void verifyRequest(RequestNode tree) throws VerificationFailedException {
		//loop through the joins to get to the attributes
		for(RequestHierarchyNode hierarchyEntry : tree.getHierarchy()) {
			for(RequestEntityNode entity : hierarchyEntry.getEntities()) {
				String entityName = entity.getEntityName().getValue();

				//loop through the attributes and check each attribute's value of validity
				for(AttributeNode attr : entity.getAttributes()) {
					if(attr instanceof NumberAttributeNode && NUMERICAL_FIELDS.containsKey(entityName)) {
						if(!entityContainsNumericalField(entityName, attr.getName().getValue()))
							throw new VerificationFailedException("Expected a string for attribute " + attr.getName().getValue() + " but got " + ((NumberAttributeNode)attr).valuesToString());
						else if(((NumberAttributeNode)attr).getValues().size() != NUMERICAL_FIELDS.get(entityName).get(attr.getName().getValue()))
							throw new VerificationFailedException("Unexpected amount of numbers given for attribute " + attr.getName().getValue() + ". " +
									"Got " + ((NumberAttributeNode)attr).getValues().size() + ", need " + NUMERICAL_FIELDS.get(entityName).get(attr.getName().getValue()));
					}
					else if(attr instanceof StringAttributeNode && NUMERICAL_FIELDS.containsKey(entityName)) {
						if(entityContainsNumericalField(entityName, attr.getName().getValue()))
							throw new VerificationFailedException("Expected (multiple) number(s) for attribute " + attr.getName().getValue() + " but got " + ((StringAttributeNode)attr).getValue().getValue());
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
	private boolean entityContainsNumericalField(String entity, String theField) {
		for(String fieldName : NUMERICAL_FIELDS.get(entity).keySet()) {
			if(fieldName.equals(theField))
				return true;
		}

		return false;
	}
}
