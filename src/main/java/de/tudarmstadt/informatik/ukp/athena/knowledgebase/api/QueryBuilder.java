package de.tudarmstadt.informatik.ukp.athena.knowledgebase.api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.AttributeNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.NumberAttributeNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.NumberNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.RequestEntityNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.RequestFunction;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.RequestHierarchyNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.RequestNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.StringAttributeNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.PersistenceManager;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.EventCategory;

//code in here is not tested because javax.persistence.Query does not provide a way to see if the built query looks like intended
public class QueryBuilder {
	private EntityManager entityManager = PersistenceManager.getEntityManager();

	/**
	 * Sanitizes user input and builds the JPQL query
	 * @param tree The request tree to build the request from, non-null
	 * @return The generated query
	 */ //size of 40 lines is exceeded in favor of readability (=> normalEntityName, entityName, entityVar etc. could be removed to meet the requirement)
//	public Query build(RequestNode tree) {
	public ArrayList build(RequestNode tree) {
		List<String> queryList = new ArrayList<>();
		Map<String,Object> jpqlVars = new HashMap<>(); //replace key with value later, this is user input
		String previousEntityVar = null; //used for hierarchical relationship

		queryList.add("SELECT");

		if(tree.getFunction() == RequestFunction.COUNT)
			queryList.add("count(:entityVar)");
		else
			queryList.add(":entityVar"); //when initially building the query, it's not known which entity is placed last in the request

		queryList.add("FROM");

		//build the FROM statement
		for(RequestHierarchyNode hierarchyNode : tree.getHierarchy()) {
			String normalEntityName = hierarchyNode.getEntity().getEntityName().getString();
			String entityName = capitalizeFirstLetter(normalEntityName);
			String entityVar = normalEntityName.equals("eventpart") ? "ep" : normalEntityName.substring(0, 2);

			jpqlVars.put(":entityVar", "" + entityVar); //the last one will be in the output

			//if it's the first entity, there shouldn't be a join
			if(previousEntityVar == null)
				queryList.add(entityName + " " + entityVar);
			else //normal entity name because it's the name of the field
				queryList.add("JOIN " + previousEntityVar + "." + normalEntityName + "s" + " " + entityVar); //TODO: this s is for the plural form, might want to rename the corresponding columns to singular just to not have to handle multiple plural forms in the future
			
			previousEntityVar = entityVar;
		}

		if(tree.getHierarchy().get(0).getEntity().getAttributes().size() > 0) //this is only the case if the request is not something like /paper to get all the papers
			queryList.add("WHERE");

		//now set the attributes
		for(RequestHierarchyNode hierarchyNode : tree.getHierarchy()) {
			RequestEntityNode entityNode = hierarchyNode.getEntity();
			String entityVar = entityNode.getEntityName().getString().equals("eventpart") ? "ep" : entityNode.getEntityName().getString().substring(0, 2);

			//loop through the attributes (if any)
			for(AttributeNode attr : entityNode.getAttributes()) {
				String attrName = attr.getName().getString();
				String jpqlVar = entityVar + "_" + attrName; //used later to replace with actual user input after it was automatically sanitized

				queryList.add(entityVar + "." + attrName + "=:" + jpqlVar);
				
				setAttributeCorrectly(attr, jpqlVars, jpqlVar);
				queryList.add("and");
			}
		}

		if(queryList.get(queryList.size() - 1).equals("and")) //remove the last and if there is one
			queryList.remove(queryList.size() - 1);

		ArrayList ret = new ArrayList();
		ret.add(queryList);
		ret.add(jpqlVars);
		return ret; 
	}

	/**
	 * Sets the JPQL variable for the given attribute to the correct value
	 * @param attr The attribute, non-null
	 * @param jpqlVars The data structure to store the JPQL variable -> value mapping in, non-null
	 * @param jpqlVar The JPQL variable name to use, non-null
	 */
	private void setAttributeCorrectly(AttributeNode attr, Map<String,Object> jpqlVars, String jpqlVar) {
		//nothing extra needs to be done for a string node other than assigning its value to the the jpql var
		if(attr instanceof StringAttributeNode) {
			String val = ((StringAttributeNode)attr).getValue().getString();
			jpqlVars.put(jpqlVar, val); // ((StringAttributeNode)attr).getValue().getString());
		}
		//construct the jpql value for the number node from the numbers
		else if(attr instanceof NumberAttributeNode) {
			List<NumberNode> numbers = ((NumberAttributeNode)attr).getNumbers();

			//yes, vars can be any object
			switch(numbers.size()) {
				case 5:
					jpqlVars.put(jpqlVar, LocalDateTime.of(numbers.get(0).getNumber(), numbers.get(1).getNumber(), numbers.get(2).getNumber(), numbers.get(3).getNumber(), numbers.get(4).getNumber()));
					break;
				case 3:
					jpqlVars.put(jpqlVar, LocalDate.of(numbers.get(0).getNumber(), numbers.get(1).getNumber(), numbers.get(2).getNumber()));
					break;
				case 1:
					//differentiate between long and category
					if(attr.getName().getString().toLowerCase().contains("category"))
						jpqlVars.put(jpqlVar, EventCategory.values()[numbers.get(0).getNumber()]);
					else
						jpqlVars.put(jpqlVar, new Long(numbers.get(0).getNumber())); //needs to be in a wrapper class or else it doesn't work
					break;
			}
		}
	}

	/**
	 * Creates a query that is ready to be sent to the database
	 * @param queryList The broken down query string. Will be concatenated with spaces, non-null
	 * @param jpqlVars The variable -> value mappings for JPQL variables. The values are user input and will be sanitized by this method, non-null
	 * @return The created query
	 */
//	private Query createQuery(List<String> queryList,  Map<String,Object> jpqlVars) {
	Query createQuery(List<String> queryList,  Map<String,Object> jpqlVars) {
		String qlString = "";

		for(String s : queryList) { //build the complete query string
			qlString += s + " ";
		}

		//set the :entityVar variable manually as parameters are not supported in the SELECT part
		if(jpqlVars.containsKey(":entityVar"))
			qlString = qlString.replace(":entityVar", (String)jpqlVars.remove(":entityVar")); //remove returns the previously associated value as well
			
		Query query = entityManager.createQuery(qlString); //create the base query

		//sanitize user input
		for(String key : jpqlVars.keySet()) {
			query = query.setParameter(key, jpqlVars.get(key));
		}

		return query;
	}

	/**
	 * Capitalizes the first letter of a string
	 * @param string The string
	 * @return The string as it was fed into the method, but with its first character capitalized. If the string is null or empty, an empty string will be returned.
	 */
	private String capitalizeFirstLetter(String string) {
		if(string == null || string.isEmpty())
			return "";
		else return Character.toUpperCase(string.charAt(0)) + string.substring(1);
	}
}
