package de.tudarmstadt.informatik.ukp.athena.knowledgebase.api;

import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.RequestNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Person;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.exception.SyntaxException;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.exception.VerificationFailedException;

@RestController
public class APIController {
	/**
	 * This method catches all requests made to the API that are not specified in a different request mapping.
	 * If there are no questionmarks (?), request will contain the complete path (including all subpaths).
	 * This method then calls various worker classes to validate and verify the request string and make sure that it's correct.
	 * If that is the case, the request will be sent to the database and the result will be returned to the user.
	 * If an error occurs, it will be returned to the user as well.
	 * @param request A HttpServletRequest usually received through REST
	 * @return The result list of the query, or an error message.
	 */
	@RequestMapping("/**")
	public Object apiConnector(HttpServletRequest request) { // String request) { //  //the argument contains everything that was not matched to any other argument
		RequestNode tree = null;

		try {
			// http://compjour.ukp.informatik.tu-darmstadt.de:8080/person:fullName=mark+finlayson/paper:paperID=1/person
			//scan the request
			String apiRequest = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();
//			request = request.substring(request.indexOf("8080")+4, request.length());
//			System.out.println("Request: " + request);
			RequestScanner scanner = new RequestScanner(apiRequest); // request); // 
			Deque<RequestToken> tokens = scanner.scan();
			System.out.println(tokens);
			RequestParser parser = new RequestParser(tokens);
			RequestVerifier verifier = new RequestVerifier();
			//prepare query and query builder
			QueryBuilder queryBuilder = new QueryBuilder();
			Query query;

			tree = parser.parse(); //parse the request
			verifier.verify(tree); //if no exception is thrown, the verification was successful
//			System.out.println("tree verified");
			ArrayList queryoutput = queryBuilder.build(tree);
			List<String> queryList = (List<String>) queryoutput.get(0);
			Map<String,Object> jpqlVars = (Map<String, Object>) queryoutput.get(1);
			query = queryBuilder.createQuery(queryList, jpqlVars);
			
//			long startTime = System.nanoTime();
			ArrayList ret = (ArrayList) tree.getFunction().getFunction().apply(query, verifier.getResultEntity()); //call the request function
//			long endTime = System.nanoTime();
//			long duration = (endTime - startTime)/1000000;  //divide by 1000000 to get milliseconds.
//			System.out.println("EXECUTION TIME = " + duration);
			
			System.out.println(ret);
//			if (ret.isEmpty()) {
//				System.out.println("no result found -> search 'wild'");
//				startTime = System.nanoTime();
//				//ret = searchDBwild(queryList, jpqlVars, tree, verifier);
//				ret = searchNearsetNeighbor(queryList, jpqlVars, tree, verifier);
//				endTime = System.nanoTime();
//				duration = (endTime - startTime)/1000000;
//				System.out.println("EXECUTION TIME WILD SEARCH = " + duration);
////				ret = (ArrayList) tree.getFunction().getFunction().apply(query, verifier.getResultEntity());
//				System.out.println(ret);
//			}
			return ret;
		}
		catch(SyntaxException | VerificationFailedException e) {
			String errorMessage = "<h4>" + e.getMessage() + "</h4>"
					+ (tree == null ? "" : tree.toString())
					+ "<br><br>"
					+ "<u>Stacktrace:</u>"
					+ "<br>"
					+ "<div style=\"padding-left:20px\">"
					+ e.toString()
					+ "<br>"
					+ "<div style=\"padding-left:20px\">";

			for(StackTraceElement ste : e.getStackTrace()) {
				errorMessage += "at " + ste.toString() + "<br>";
			}

			return errorMessage + "</div></div>";
		}
	}
	
	
private static ArrayList searchNearsetNeighbor(List<String> queryList, Map<String, Object> jpqlVars, RequestNode tree, RequestVerifier verifier) {

		QueryBuilder queryBuilder = new QueryBuilder();
		
		Integer listlen = queryList.size()-1;
		String searchTerm = queryList.get(listlen);
		String newSearchTerm = "";
		String attr = searchTerm.substring(0, searchTerm.indexOf("="));
		newSearchTerm = searchTerm.replace("=", ">=");
		queryList.set(listlen, newSearchTerm);
		queryList.add("ORDER BY " + attr + " ASC");
	
		Query query = queryBuilder.createQuery(queryList, jpqlVars);
		query.setMaxResults(1);
		ArrayList geq = (ArrayList) tree.getFunction().getFunction().apply(query, verifier.getResultEntity());
		
		newSearchTerm = searchTerm.replace("=", "<=");
		queryList.set(listlen, newSearchTerm);
		queryList.set(queryList.size()-1, "ORDER BY " + attr + " DESC");
		query = queryBuilder.createQuery(queryList, jpqlVars);
		query.setMaxResults(1);
		ArrayList leq = (ArrayList) tree.getFunction().getFunction().apply(query, verifier.getResultEntity());
		
		System.out.println(geq.toString());
		String name1 = geq.toString().substring(geq.toString().indexOf(":")+1, geq.toString().indexOf(","));
		String name2 = leq.toString().substring(leq.toString().indexOf(":")+1, leq.toString().indexOf(","));

		String key = searchTerm.substring(newSearchTerm.indexOf("=")+1);
		String name = (String) jpqlVars.get(key);
		System.out.println(name1);
		System.out.println(name2);

		Integer dist1 = levenshteinDistance(name1, name);
		Integer dist2 = levenshteinDistance(name2, name);
		System.out.println(dist1);
		System.out.println(dist2);
		
		if (dist1 < dist2) return geq;
		else return leq;
	}
	
	private static int levenshteinDistance (CharSequence lhs, CharSequence rhs) {                          
    int len0 = lhs.length() + 1;                                                     
    int len1 = rhs.length() + 1;                                                     
                                                                                      
    int[] cost = new int[len0];                                                     
    int[] newcost = new int[len0];                                                  
                                                                                    
    for (int i = 0; i < len0; i++) cost[i] = i;                                     
                                                                                      
    for (int j = 1; j < len1; j++) {                                                
        newcost[0] = j;                                                              
        for(int i = 1; i < len0; i++) {                                            
            int match = (lhs.charAt(i - 1) == rhs.charAt(j - 1)) ? 0 : 1;             
                                                                                     
            int cost_replace = cost[i - 1] + match;                                 
            int cost_insert  = cost[i] + 1;                                         
            int cost_delete  = newcost[i - 1] + 1;                                  
                                                                                          
            newcost[i] = Math.min(Math.min(cost_insert, cost_delete), cost_replace);
        }                                                                            
        int[] swap = cost; cost = newcost; newcost = swap;                          
    }                                                                               
    return cost[len0 - 1];                                                          
}


	private static ArrayList searchDBwild(List<String> queryList, Map<String, Object> jpqlVars, RequestNode tree, RequestVerifier verifier) {
		
		ArrayList ret = new ArrayList();
		Integer pos = 0;
		QueryBuilder queryBuilder = new QueryBuilder();
		Map<String, Object> jpqlCopy = new HashMap<String, Object>(jpqlVars);
		
		while (ret.isEmpty()) {
			System.out.println("in loop");
			
			for (String key : jpqlVars.keySet()) {
				Object val = jpqlVars.get(key);
				String searchTerm = val.toString().substring(0, val.toString().length()-1);
				Integer len = searchTerm.length();
				if (pos >= searchTerm.length())
					return ret;
				if (pos < 1) {
					searchTerm = "%" + searchTerm + "%";
				} else {
//					searchTerm = "%" + searchTerm.substring(0, pos) + "%"
//							+ searchTerm.substring(pos + 1, len) + "%";
					searchTerm = searchTerm.substring(0, len-pos) + "%"
							+ searchTerm.substring(len-pos + 1, searchTerm.length()) + "%";
				}
				jpqlCopy.put(key, searchTerm);
			}

			Query query = queryBuilder.createQuery(queryList, jpqlCopy);
			ret = (ArrayList) tree.getFunction().getFunction().apply(query, verifier.getResultEntity());
			pos += 1;
			
		}

		return ret;
	}
}
