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
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.NeedlemanWunsch;

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
	public Object apiConnector(HttpServletRequest request) { //the argument contains everything that was not matched to any other argument

		RequestNode tree = null;

		try {
			//scan the request
			String apiRequest = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();
			RequestScanner scanner = new RequestScanner(apiRequest); 
			Deque<RequestToken> tokens = scanner.scan();
			RequestParser parser = new RequestParser(tokens);
			RequestVerifier verifier = new RequestVerifier();
			//prepare query and query builder
			QueryBuilder queryBuilder = new QueryBuilder();
			Query query;

			tree = parser.parse(); //parse the request
			verifier.verify(tree); //if no exception is thrown, the verification was successful
			ArrayList queryoutput = queryBuilder.build(tree);
			List<String> queryList = (List<String>) queryoutput.get(0);
			Map<String,Object> jpqlVars = (Map<String, Object>) queryoutput.get(1);
			query = queryBuilder.createQuery(queryList, jpqlVars);
			
			ArrayList ret = (ArrayList) tree.getFunction().getFunction().apply(query, verifier.getResultEntity()); //call the request function
			
			if (ret.isEmpty()) {
				ret = searchNearsetNeighbor(queryList, jpqlVars, tree, verifier);
			}
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

	private static ArrayList searchNearsetNeighbor(List<String> queryList, Map<String, Object> jpqlVars,
			RequestNode tree, RequestVerifier verifier) {

		String searchTerm = queryList.get(queryList.size() - 1);
		String attr = "";
		attr = searchTerm.substring(searchTerm.indexOf(".") + 1, searchTerm.indexOf("="));

		ArrayList geq = getNN(queryList, attr, jpqlVars, tree, verifier, "min");
		ArrayList leq = getNN(queryList, attr, jpqlVars, tree, verifier, "max");

		String result1 = extractResult(geq);
		String result2 = extractResult(leq);

		String key = searchTerm.substring(searchTerm.indexOf("=") + 2);
		String search = (String) jpqlVars.get(key);

		if (result1.contains(search))
			return geq;
		if (result2.contains(search))
			return leq;

		NeedlemanWunsch nw1 = new NeedlemanWunsch(search, result1);
		int dist1 = nw1.getScore();

		NeedlemanWunsch nw2 = new NeedlemanWunsch(search, result2);
		int dist2 = nw2.getScore();

		int threshold = 0; // decide for which distance to the search the result should be kept
		if (dist1 < threshold && dist2 < threshold) { // if both distances are smaller than threshold: search with
														// wildcards
			return searchDBwild(queryList, jpqlVars, tree, verifier);
		}
		if (dist1 > dist2)
			return geq; // geq -> result1 --> dist1
		else
			return leq;
	}

	private static ArrayList getNN(List<String> queryList, String attr, Map<String, Object> jpqlVars, RequestNode tree,
			RequestVerifier verifier, String mode) {

		QueryBuilder queryBuilder = new QueryBuilder();
		Integer listlen = queryList.size() - 1;
		String searchTerm = queryList.get(listlen);
		String newSearchTerm = "";

		if (mode == "min") {
			queryList.set(1, "MIN(" + attr + ")");
			newSearchTerm = searchTerm.replace("=", ">=");
		}
		if (mode == "max") {
			queryList.set(1, "MAX(" + attr + ")");
			newSearchTerm = searchTerm.replace(">=", "<=");
		}

		queryList.set(listlen, newSearchTerm);
		Query query = queryBuilder.createQuery(queryList, jpqlVars);
		ArrayList nn = (ArrayList) tree.getFunction().getFunction().apply(query, verifier.getResultEntity());
		return nn;

	}

	private static String extractResult(ArrayList nn) {
		String result = "";
		if (nn.toString().contains(":")) {
			result = nn.toString().substring(nn.toString().indexOf(":") + 1, nn.toString().indexOf(","));
		} else {
			result = nn.toString();
		}
		return result;
	}

	private static ArrayList searchDBwild(List<String> queryList, Map<String, Object> jpqlVars, RequestNode tree,
			RequestVerifier verifier) {

		QueryBuilder queryBuilder = new QueryBuilder();
		Map<String, Object> jpqlCopy = new HashMap<String, Object>(jpqlVars);

		String likeStat = queryList.get(queryList.size() - 1);
		String dbObj = likeStat.substring(0, likeStat.indexOf("=") - 1);
		String searchObj = likeStat.substring(likeStat.indexOf("=") + 2);
		queryList.remove(queryList.size() - 1);
		queryList.add(dbObj);
		queryList.add("LIKE");
		queryList.add(":" + searchObj);

		for (String key : jpqlVars.keySet()) {
			Object val = jpqlVars.get(key);
			String searchTerm = val.toString().substring(0, val.toString().length());
			searchTerm = "%" + searchTerm + "%";
			jpqlCopy.put(key, searchTerm);
		}

		Query query = queryBuilder.createQuery(queryList, jpqlCopy);
		ArrayList ret = (ArrayList) tree.getFunction().getFunction().apply(query, verifier.getResultEntity());
		return ret;
	}
}
