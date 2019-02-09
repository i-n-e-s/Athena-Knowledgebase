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
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.RequestHierarchyNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.RequestNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.StringAttributeNode;
import de.tudarmstadt.informatik.ukp.athenakp.database.jpa.PersistenceManager;

public class RequestBuilder {
	/**
	 * Sanitizes user input, builds the SQL query and sends the request to the database
	 * @param tree The request tree to build the request from
	 * @return The result list of the query
	 */ //requirement of a length of 40 lines cannot be held due to this being one operation (building the sql query string)
	public static List build(RequestNode tree) {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		String previousEntity = null;
		List<String> queryList = new ArrayList<>();
		Map<String,Object> sqlVars = new HashMap<>(); //replace key with value later, this is user input
		String qlString = "";
		Query query = null;
		List result = null;

		for(RequestHierarchyNode hierarchyEntry : tree.getHierarchy()) {
			for(RequestEntityNode entity : hierarchyEntry.getEntities()) {
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

						//TODO: localdatetime does not work :(
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
		}

		for(String s : queryList) { //build the complete query string
			qlString += s + " ";
		}

		query = entityManager.createNativeQuery(qlString); //create the base query

		for(String key : sqlVars.keySet()) { //sanitize user input
			query = query.setParameter(key, sqlVars.get(key));
		}

		result = query.getResultList();
		entityManager.close();
		return result;
	}
}
