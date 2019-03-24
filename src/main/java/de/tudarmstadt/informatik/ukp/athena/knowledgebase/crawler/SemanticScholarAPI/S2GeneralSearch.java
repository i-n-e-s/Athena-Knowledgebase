package de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.semanticscholarapi;

import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.exception.NotAvailableException;

/**
 * This class can perform a general search on the Semantic Scholar API
 * A general search on Semantic Scholar returns all matching information to the String,
 * e.g. matching author names, paper names, etc. as a {@link org.json.JSONObject JSONObject}.
 * This class offers several possibilities to access this information
 *
 * @author Philipp Emmer
 */

public class S2GeneralSearch extends SemanticScholarAPIRequest {

	private String rawResponse = null;              //Response as received from the server
	private boolean validDataIsReady = false;       //True if response is ready

	private String searchQuery = null;
	private String httpResponseCode = null;


	/**
	 * Sets the search query
	 *
	 * @param query The new search query
	 */
	@Override
	public void setQuery(String query) {
		searchQuery = query;
	}

	/**
	 * Returns the HTTP status code of the request
	 *
	 * @return The HTTP status code of the request
	 */
	@Override
	public String getHTTPResponseCode() {
		return httpResponseCode;
	}

	/**
	 * Returns the response as the String we got from the server
	 *
	 * @return The server's RESPONSE String
	 * @throws NotAvailableException if no valid response is available
	 */
	@Override
	public String getRawResponse() throws NotAvailableException {
		if (this.validDataIsReady) {
			return rawResponse;
		} else {
			throw new NotAvailableException();
		}
	}

	/**
	 * Returns the response as a parsed JSON
	 *
	 * @return The server's RESPONSE as a {@link org.json.JSONObject JSONObject}
	 */
	@Override
	public JSONObject getParsedJSONResponse() throws NotAvailableException, JSONException {
		if (!this.validDataIsReady) { throw new NotAvailableException(); }

		return new JSONObject( rawResponse );
	}


	/**
	 * Establishes an HTTPS connection to SemanticScholarAPI and POSTs a request
	 *
	 * @throws IOException when some HTTP stuff goes wrong
	 */
	@Override
	public void run() throws IOException {

		//Create data payload of POST request, containing search parameters
		String searchPayload = createSearchPayload(10);

		//Create URL for general search request
		String href = semanticScholarInternalAPIURL + "/search";

		//Create connection and set basic parameters
		URL url = new URL(href);
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setConnectTimeout(30 * 1000);        //30s
		connection.setUseCaches(false);                 //Don't cache anything

		//Set connection headers
		connection.setRequestMethod("POST");
		connection.setRequestProperty("charset", "utf-8");
		connection.setRequestProperty("origin", "https://www.semanticscholar.org");
		connection.setRequestProperty("accept-language", "de-DE,de;q=0.9,en-DE;q=0.8,en;q=0.7,en-US;q=0.6");
		connection.setRequestProperty("Accept", "*/*");
		connection.setRequestProperty("cache-control", "no-cache,no-store,must-revalidate,max-age=-1");
		connection.setRequestProperty("content-type", "application/json");
		connection.setRequestProperty("authority", "www.semanticscholar.org");
		connection.setRequestProperty("dnt", "1");
		connection.setRequestProperty("User-Agent", userAgentString);

		//Write search payload to server (BODY of POST request)
		writeStringToServer(searchPayload, connection);

		//Convert received JSON to String
		this.rawResponse = readResponseInputStreamToString(connection);

		this.httpResponseCode = Integer.toString(connection.getResponseCode());
		this.validDataIsReady = true;
	}

	/**
	 * Creates the data payload for the POST request of the search
	 * Look here for fine tuning of the search parameters
	 *
	 * @param numOfResults The amount of results we want the server to give us, greater than 0x
	 * @return The JSON formatted payload string
	 */
	private String createSearchPayload(int numOfResults) {

		//If no query has been set yet, use empty String
		String query = (this.searchQuery == null) ? "" : this.searchQuery;

		return "{\"queryString\":\"\\\"" + query + "\\\"\","
		+ "\"page\":1,"
		+ "\"pageSize\":" + numOfResults + ","
		+ "\"sort\":\"relevance\","
		+ "\"authors\":[],"
		+ "\"coAuthors\":[],"
		+ "\"venues\":[],"
		//+ "\"facets\":{},"        Necessary at first, but now leading to HTTP 400
		+ "\"yearFilter\":null,"
		+ "\"requireViewablePdf\":false,"
		+ "\"publicationTypes\":[]}";
	}


}
