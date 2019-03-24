package de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.SemanticScholarAPI;

import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

/**
 * This class can perform a paper search on the SemanticScholar API
 * A paper search requires the SemanticScholars internal authorID of a selected author
 * and returns a full list of all papers published by the author
 *
 * @author Philipp Emmer
 */
public class S2PaperSearch extends SemanticScholarAPIrequest {


    private String rawResponse = null;              //Response as received from Server
    private boolean validDataIsReady = false;       //True if response is ready

    private String authorID = null;

    /**
     * Sets the authorID of the selected Author
     * The authorID can be found out by performing a general search on the Name
     *
     * @param s2authorID The SemanticScholar authorID of the selected author
     */
    @Override
    public void setQuery(String s2authorID) {
        if( s2authorID != null ) { authorID = s2authorID; }
        else { authorID = ""; }
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
     * @throws JSONException if Response could not be parsed as JSON
     * @throws NotAvailableException if called before a Response as arrived
     */
    @Override
    public JSONObject getParsedJSONResponse() throws JSONException, NotAvailableException {
        if (!this.validDataIsReady) { throw new NotAvailableException(); }

        return new JSONObject(rawResponse);
    }

    /**
     * Establishes an HTTPS Connection to SemanticScholarAPI and POSTs a request
     *
     * @throws IOException when some HTTP stuff goes wrong
     */
    @Override
    public void run() throws IOException {


        //Create Data Payload of GET request, containing Search parameters
        String searchRequestURL = SemanticScholarPublicAPIURL + "v1" + "/author/" + authorID;

        //Create Connection and set basic parameters
        URL url = new URL(searchRequestURL);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setConnectTimeout(30 * 1000);        //30s
        connection.setUseCaches(false);                 //Don't cache anything

        //Set Connection Headers
        connection.setRequestMethod("GET");
        connection.setRequestProperty("charset", "utf-8");
        //connection.setRequestProperty("origin", "https://www.semanticscholar.org");
        connection.setRequestProperty("accept-language", "de-DE,de;q=0.9,en-DE;q=0.8,en;q=0.7,en-US;q=0.6");
        connection.setRequestProperty("Accept", "*/*");
        connection.setRequestProperty("cache-control", "no-cache,no-store,must-revalidate,max-age=-1");
        connection.setRequestProperty("content-type", "application/json");
        //connection.setRequestProperty("authority", "www.semanticscholar.org");
        //connection.setRequestProperty("dnt", "1");
        connection.setRequestProperty("User-Agent", UserAgentString);

        //Convert received JSON to String
        this.rawResponse = readResponseInputStreamToString(connection);


        this.HTTPResponseCode = Integer.toString(connection.getResponseCode());
        this.validDataIsReady = true;
    }


}
