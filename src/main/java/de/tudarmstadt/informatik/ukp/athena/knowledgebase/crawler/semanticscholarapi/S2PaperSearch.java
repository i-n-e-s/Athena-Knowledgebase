package de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.semanticscholarapi;

import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * This class can perform a paper search on the Semantic Scholar API
 * A paper search requires Semantic Scholar's internal authorID of a selected author
 * and returns a full list of all papers published by the author
 *
 * @author Philipp Emmer
 */
public class S2PaperSearch extends SemanticScholarAPIRequest {

	private String authorID = null;


	/**
	 * Sets the authorID of the selected author
	 * The authorID can be found out by performing a general search on the name
	 *
	 * @param s2authorID The Semantic Scholar authorID of the selected author
	 */
	@Override
	public void setQuery(String s2authorID) {
		if( s2authorID != null ) { authorID = s2authorID; }
		else { authorID = ""; }
	}

	/**
	 * Establishes an HTTPS connection to Semantic Scholar's API and POSTs a request
	 *
	 * @throws IOException if the HTTP connection to the server fails
	 */
	@Override
	public void run() throws IOException {


		//Create data payload of GET request, containing search parameters
		String searchRequestURL = semanticScholarPublicApiUrl + "v1" + "/author/" + authorID;

		//Create connection and set basic parameters
		URL url = new URL(searchRequestURL);
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setConnectTimeout(30 * 1000);        //30s
		connection.setUseCaches(false);                 //Don't cache anything

		//Set connection headers
		connection.setRequestMethod("GET");
		connection.setRequestProperty("charset", "utf-8");
		//connection.setRequestProperty("origin", "https://www.semanticscholar.org");
		connection.setRequestProperty("accept-language", "de-DE,de;q=0.9,en-DE;q=0.8,en;q=0.7,en-US;q=0.6");
		connection.setRequestProperty("Accept", "*/*");
		connection.setRequestProperty("cache-control", "no-cache,no-store,must-revalidate,max-age=-1");
		connection.setRequestProperty("content-type", "application/json");
		//connection.setRequestProperty("authority", "www.semanticscholar.org");
		//connection.setRequestProperty("dnt", "1");
		connection.setRequestProperty("User-Agent", userAgentString);

		//Convert received JSON to String
		this.rawResponse = readResponseInputStreamToString(connection);


		this.httpResponseCode = Integer.toString(connection.getResponseCode());
		this.validDataIsReady = true;
	}


}
