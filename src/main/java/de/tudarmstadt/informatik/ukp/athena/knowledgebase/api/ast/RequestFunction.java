package de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast;

public enum RequestFunction
{
	/**
	 * No function given, return the raw data
	 */
	NONE,
	/**
	 * API call returns the size of the data entries that would be returned by this call
	 */
	COUNT,
	/**
	 * Calls the Semantic Scholar API, enhances the data and returns the updated data, only works for authors and papers
	 */
	ENHANCE
}
