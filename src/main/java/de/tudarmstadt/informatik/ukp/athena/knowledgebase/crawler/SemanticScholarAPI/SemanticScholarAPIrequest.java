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
     * TODO wait 3s between two requests
     * <p>
     * Then set SemanticScholarInternalAPIURL = "http://localhost:8080/api/1/search";
     */

    public static final String SemanticScholarInternalAPIURL = "https://www.semanticscholar.org/api/1";
    public static final String UserAgentString = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) snap Chromium/70.0.3538.110 Chrome/70.0.3538.110 Safari/537.36";
    public static final String SemanticScholarPublicAPIURL = "https://api.semanticscholar.org/";
    private static final short allowedConnectionFailuresInSafeRun = 5;

    public abstract void setQuery(String query);

    public abstract void run() throws IOException;

    public abstract String getRawResponse() throws NotAvailableException;

    public abstract String getHTTPResponseCode();

    public abstract JSONObject getParsedJSONResponse() throws NotAvailableException, JSONException;


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
     * @throws IOException
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
