package de.tudarmstadt.informatik.ukp.athenakp.api;

import java.util.Deque;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import de.tudarmstadt.informatik.ukp.athenakp.api.ast.RequestNode;
import de.tudarmstadt.informatik.ukp.athenakp.exception.SyntaxException;
import de.tudarmstadt.informatik.ukp.athenakp.exception.VerificationFailedException;

@RestController
public class APIController {
	@RequestMapping("/**") //matches the complete path (containing all subpaths), just make sure that there are no ? in there!!
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

			QueryBuilder queryManager = new QueryBuilder();
			List<?> result = queryManager.buildAndSend(tree);

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
