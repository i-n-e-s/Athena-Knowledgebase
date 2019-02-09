package de.tudarmstadt.informatik.ukp.athenakp.api;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import de.tudarmstadt.informatik.ukp.athenakp.api.ast.AttributeNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.NumberAttributeNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.NumberNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.RequestEntityNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.RequestNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.StringAttributeNode;
import de.tudarmstadt.informatik.ukp.athenakp.database.jpa.PersistenceManager;

public class QueryManager {
	private EntityManager entityManager = PersistenceManager.getEntityManager();

	/**
	 * Sanitizes user input, builds the SQL query and sends the request to the database
	 * @param tree The request tree to build the request from
	 * @return The result list of the query
	 */
	public List<?> manage(RequestNode tree) {
		if(tree.getHierarchy().size() == 1)
			return buildSimpleQuery(tree);
		return null;
	}

	/**
	 * Creates a request without joins or hierarchy
	 * @see QueryManager#manage(RequestNode)
	 */
	private List<?> buildSimpleQuery(RequestNode tree) {
		List<String> queryList = new ArrayList<>();
		Map<String,Object> sqlVars = new HashMap<>(); //replace key with value later, this is user input

		for(RequestEntityNode entity : tree.getHierarchy().get(0).getEntities()) {
			String entityName = entity.getEntityName().getString();

			queryList.add("SELECT * FROM " + entityName + " WHERE");

			//loop through the attributes
			for(AttributeNode attr : entity.getAttributes()) {
				String attrName = attr.getName().getString();
				String sqlVar = entityName + "_" + attrName;

				queryList.add(attrName + "=:" + sqlVar); //sqlVar is used later to replace with actual user input after it was automatically sanitized

				//nothing extra needs to be done for a string node other than assigning its value to the the sql var
				if(attr instanceof StringAttributeNode)
					sqlVars.put(sqlVar, ((StringAttributeNode)attr).getValue().getString());
				//construct the sql value for the number node out of the numbers
				else if(attr instanceof NumberAttributeNode) {
					List<NumberNode> numbers = ((NumberAttributeNode)attr).getNumbers();

					//TODO: localdatetime does not work because of colons :(
					switch(numbers.size()) {
						case 5:
							sqlVars.put(sqlVar, LocalDateTime.of(numbers.get(0).getNumber(), numbers.get(1).getNumber(), numbers.get(2).getNumber(), numbers.get(3).getNumber(), numbers.get(4).getNumber()));
							break;
						case 3:
							sqlVars.put(sqlVar, LocalDate.of(numbers.get(0).getNumber(), numbers.get(1).getNumber(), numbers.get(2).getNumber()).toString());
							break;
						case 1:
							sqlVars.put(sqlVar, numbers.get(0).getNumber());
							break;
					}
				}
			}
		}

		return createQuery(queryList, sqlVars).getResultList();
	}

	/**
	 * Creates a query that is ready to be sent to the database
	 * @param queryList The broken down query string. Will be concatenated with spaces
	 * @param sqlVars The variable -> value mappings for SQL variables. The values are user input and will be sanitized by this method
	 * @return The created query
	 */
	private Query createQuery(List<String> queryList,  Map<String,Object> sqlVars) {
		String qlString = "";

		for(String s : queryList) { //build the complete query string
			qlString += s + " ";
		}

		Query query = entityManager.createNativeQuery(qlString); //create the base query

		for(String key : sqlVars.keySet()) { //sanitize user input
			query = query.setParameter(key, sqlVars.get(key));
		}

		return query;
	}

	/**
	 * Closes the entity manager of this query manager. Cannot be undone.
	 */
	public void close() {
		entityManager.close();
	}
}
