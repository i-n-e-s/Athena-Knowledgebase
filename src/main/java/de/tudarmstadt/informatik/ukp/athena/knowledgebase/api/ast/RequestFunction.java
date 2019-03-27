package de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast;

import java.io.IOException;
import java.util.List;
import java.util.function.BiFunction;

import javax.persistence.Query;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.semanticscholarapi.S2APIFunctions;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Paper;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Person;

/**
 * Defines the different function types that an api call can have and what they will do/return when called
 */
public enum RequestFunction
{
	/**
	 * No function given, return the raw data
	 */
	NONE((query, entity) -> query.getResultList()),
	/**
	 * API call returns the size of the data entries that would be returned by this call
	 */
	COUNT((query, entity) -> "{\"count\": " + query.getSingleResult() + "}"),
	/**
	 * Calls the Semantic Scholar API, enhances the data and returns the updated data, only works for authors and papers
	 */
	ENHANCE((query, entity) -> {
		List<?> resultList = query.getResultList(); //get results to be enhanced

		switch(entity) { //decide which result it is so it can be enhanced correctly
			case "paper":
				for(Object o : resultList) { //enhance each result
					try {
						S2APIFunctions.completePaperInformationByGeneralSearch((Paper)o, true);
					}
					catch(IOException e) {}
				}

				break;
			case "person":
				for(Object o : resultList) { //enhance each result
					try {
						S2APIFunctions.completeAuthorInformationByAuthorSearch((Person)o, true);
					}
					catch(IOException e) {}
				}

				break;
		}

		return resultList; //the models were updated in-place
	});

	private BiFunction<Query,String,Object> function;

	/**
	 * @param function Gets called when the query is done. This returns the result set, or works with the result set before returning it.
	 * 					The first type argument is the query itself, the second one is the name of the entity that the query will return. The third one is the return type.
	 */
	RequestFunction(BiFunction<Query,String,Object> function)
	{
		this.function = function;
	}

	/**
	 * @return This request function's query handler
	 */
	public BiFunction<Query, String, Object> getFunction()
	{
		return function;
	}
}
