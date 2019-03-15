package de.tudarmstadt.informatik.ukp.athena.knowledgebase.api;

import java.util.Deque;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.RequestNode;
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
	public Object apiConnector(HttpServletRequest request) { //the argument contains everything that was not matched to any other argument
		RequestNode tree = null;

		try {
			//scan and parse the request
			String apiRequest = request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString();
			RequestScanner scanner = new RequestScanner(apiRequest);
			Deque<RequestToken> tokens = scanner.scan();
			RequestParser parser = new RequestParser(tokens);

			tree = parser.parse();
			RequestVerifier.verify(tree); //if no exception is thrown, the verification was successful

			QueryBuilder queryManager = new QueryBuilder(tree.isCountFunction());
			Object result = queryManager.buildAndSend(tree);

			queryManager.close();
			return result;
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
}
