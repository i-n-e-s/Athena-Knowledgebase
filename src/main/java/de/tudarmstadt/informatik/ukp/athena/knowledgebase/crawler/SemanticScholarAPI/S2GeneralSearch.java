package de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.SemanticScholarAPI;


import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This class can perform a general search on the SemanticScholar API
 * A general search on SemanticScholar returns all matching information to the String,
 * e.g. matching Author names, paper names, etc. as a JSON Object.
 * This class offers several possibilities to access this information
 *
 * @author Philipp Emmer
 */

public class S2GeneralSearch extends SemanticScholarAPIrequest {

    private String rawResponse = null;              //Response as received from Server
    private boolean validDataIsReady = false;       //True if response is ready

    private String searchQuery = null;
    private String HTTPResponseCode = null;


    /**
     * Sets the search query
     *
     * @param query The new Search Query
     */
    @Override
    public void setQuery(String query) {
        searchQuery = query;
    }

    /**
     * Returns the HTTP Status Code of the request
     *
     * @return The HTTP Status Code of the request
     */
    @Override
    public String getHTTPResponseCode() {
        return HTTPResponseCode;
    }

    /**
     * Returns the Response as the String we got from the Server
     *
     * @return Servers RESPONSE String
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
     * Returns the Response as a parsed JSON
     *
     * @return Servers RESPONSE as an org.json.JSONObject
     */
    @Override
    public JSONObject getParsedJSONResponse() throws NotAvailableException, JSONException {
        if (!this.validDataIsReady) { throw new NotAvailableException(); }

        return new JSONObject( rawResponse );
    }


    /**
     * Establishes an HTTPS Connection to SemanticScholarAPI and POSTs a request
     *
     * @throws IOException when some HTTP stuff goes wrong
     */
    @Override
    public void run() throws IOException {

        //Create Data Payload of POST request, containing Search parameters
        String searchPayLoad = createSearchPayLoad(10);

        //Create URL for general search request
        String href = SemanticScholarInternalAPIURL + "/search";

        //Create Connection and set basic parameters
        URL url = new URL(href);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setConnectTimeout(30 * 1000);        //30s
        connection.setUseCaches(false);                 //Don't cache anything

        //Set Connection Headers
        connection.setRequestMethod("POST");
        connection.setRequestProperty("charset", "utf-8");
        connection.setRequestProperty("origin", "https://www.semanticscholar.org");
        connection.setRequestProperty("accept-language", "de-DE,de;q=0.9,en-DE;q=0.8,en;q=0.7,en-US;q=0.6");
        connection.setRequestProperty("Accept", "*/*");
        connection.setRequestProperty("cache-control", "no-cache,no-store,must-revalidate,max-age=-1");
        connection.setRequestProperty("content-type", "application/json");
        connection.setRequestProperty("authority", "www.semanticscholar.org");
        connection.setRequestProperty("dnt", "1");
        connection.setRequestProperty("User-Agent", UserAgentString);

        //Write SearchPayload to Server (BODY of POST request)
        writeStringToServer(searchPayLoad, connection);

        //Convert received JSON to String
        this.rawResponse = readResponseInputStreamToString(connection);

        this.HTTPResponseCode = Integer.toString(connection.getResponseCode());
        this.validDataIsReady = true;
    }

    /**
     * Creates the Data Payload for the POST request of the Search
     * Look here for fine tuning of the Search parameters
     *
     * @param numOfResults The Amount of results we want the Server to give us
     * @return The JSON formatted PayLoad String
     */
    private String createSearchPayLoad(int numOfResults) {

        //If no query has been set yet, use empty String
        String query = (this.searchQuery == null) ? "" : this.searchQuery;

        String searchPayLoad = "{\"queryString\":\"\\\"" + query + "\\\"\","
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
        return searchPayLoad;
    }


}
