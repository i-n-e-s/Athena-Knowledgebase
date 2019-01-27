package de.tudarmstadt.informatik.ukp.athenakp.api;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

import de.tudarmstadt.informatik.ukp.athenakp.exception.SyntaxException;

@RestController
public class APIController {
	@RequestMapping("/**") //matches the complete path (containing all subpaths), just make sure that there are no ? in there!!
	public String/*List<Model>*/ apiConnector(HttpServletRequest request){ //the argument contains everything that was not matched to any other argument

		try {
			return new RequestParser(new RequestScanner(request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString()).scan()).parse().toString();
		}
		catch(SyntaxException e) {
			e.printStackTrace();
			return e.getMessage();
		}
	}
}
