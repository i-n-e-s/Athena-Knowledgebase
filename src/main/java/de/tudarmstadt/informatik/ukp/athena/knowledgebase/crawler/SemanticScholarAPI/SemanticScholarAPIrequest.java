package de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.SemanticScholarAPI;

import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public abstract class SemanticScholarAPIrequest {

    /**
     * Notes to save you time:
     * <p>
     * To test HTTP requests: nc -l -p 8080
     * Sets up local HTTP Server on port 8080 to monitor all incoming Requests, showing Headers, Body etc.
     * <p>
     * TODO Optional: wait 3s between two requests
     * <p>
     * Then set SemanticScholarInternalAPIURL = "http://localhost:8080/api/1/search";
     */

    public static final String SemanticScholarInternalAPIURL = "https://www.semanticscholar.org/api/1";
    public static final String UserAgentString = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) snap Chromium/70.0.3538.110 Chrome/70.0.3538.110 Safari/537.36";
    public static final String SemanticScholarPublicAPIURL = "https://api.semanticscholar.org/";
    private static final short allowedConnectionFailuresInSafeRun = 5;

    protected String HTTPResponseCode = null;
    protected String rawResponse = null;              //Response as received from Server
    protected boolean validDataIsReady = false;       //True if response is ready


    /**
     * Sets the query of the request
     *
     * @param query The query to be searched
     */
    public abstract void setQuery(String query);

    /**
     * Executes the request and saves the response to be accessed by the get methods
     * @throws IOException If the HTTP connection fails
     */
    public abstract void run() throws IOException;

    /**
     * Returns the HTTP Status Code of the request
     * @return The HTTP Status Code of the request
     */
    public final String getHTTPResponseCode() {
        return HTTPResponseCode;
    }

    /**
     * Returns the server's response to the request as a String
     * @return The server's response
     * @throws NotAvailableException If no response is available, e.g. because the request has not been run yet
     */
    public final String getRawResponse() throws NotAvailableException {
        if (this.validDataIsReady) {
            return rawResponse;
        } else {
            throw new NotAvailableException();
        }
    }

    /**
     * Returns the server's response as a parsed JSONObject
     * @return The JSONObject parsed from the response
     * @throws NotAvailableException If no response is available, e.g. because the request has not been run yet
     * @throws JSONException If the JSONObject could not be parsed as JSON
     */
    public final JSONObject getParsedJSONResponse() throws NotAvailableException, JSONException {
        if (!this.validDataIsReady) { throw new NotAvailableException(); }

        return new JSONObject( rawResponse );
    }


    /**
     * Runs the .run() method and retries up to 5 times in case an IOException occurs
     *
     * @throws IOException If the .run() method failed 5 times throwing an IOException
     */
    public void safeRun() throws IOException {
        short failedTries = 0;
        while ( failedTries < allowedConnectionFailuresInSafeRun ) {
            try {
                this.run();
                return;
            } catch ( IOException e ) {
                failedTries++;
            }
        }
        throw new IOException("HTTP Request failed "+failedTries+" times");
    }

    /**
     * Rawly writes a string to a connection
     *
     * @param value      String to be written to the server
     * @param connection Connection to send the string to
     * @throws IOException If some HTTP stuff goes wrong
     */
    protected static void writeStringToServer(String value, HttpsURLConnection connection) throws IOException {
        OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
        osw.write(String.format(value));
        osw.flush();
        osw.close();
    }

    /**
     * Parses the servers response inputStream to a String
     *
     * @param connection Connection Object to read the inputStream from
     * @return The response as a string
     * @throws IOException If some HTTP stuff goes wrong
     */
    protected static String readResponseInputStreamToString(HttpsURLConnection connection) throws IOException {
        BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result2 = bis.read();
        while (result2 != -1) {
            buf.write((byte) result2);
            result2 = bis.read();
        }
        return buf.toString();
    }


}
