package de.tudarmstadt.informatik.ukp.athenakp.api;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.HandlerMapping;

@RestController
public class APIConnector {
	@RequestMapping("/**") //matches the complete path (containing all subpaths), just make sure that there are no ? in there!!
	public String/*List<Model>*/ apiConnector(HttpServletRequest request){ //the argument contains everything that was not matched to any other argument
		return request.getAttribute(HandlerMapping.PATH_WITHIN_HANDLER_MAPPING_ATTRIBUTE).toString(); //returns the complete url sans the domain
	}
}
