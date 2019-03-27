package de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.semanticscholarapi;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import javax.net.ssl.HttpsURLConnection;

import org.json.JSONException;
import org.json.JSONObject;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.exception.NotAvailableException;

public abstract class SemanticScholarAPIRequest {

	/**
	 * Notes to save you time:
	 * <p>
	 * To test HTTP requests: nc -l -p 8080
	 * Sets up local HTTP server on port 8080 to monitor all incoming requests, showing headers, body etc.
	 * <p>
	 * TODO Optional: Wait 3s between two requests
	 * <p>
	 * Then set semanticScholarInternalApiUrl = "http://localhost:8080/api/1/search";
	 */

	public static final String semanticScholarInternalApiUrl = "https://www.semanticscholar.org/api/1";
	public static final String userAgentString = "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) snap Chromium/70.0.3538.110 Chrome/70.0.3538.110 Safari/537.36";
	public static final String semanticScholarPublicApiUrl = "https://api.semanticscholar.org/";
	private static final short allowedConnectionFailuresInSafeRun = 5;

	protected String httpResponseCode = null;
	protected String rawResponse = null;              //Response as received from the server
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
	 * Returns the HTTP status code of the request
	 * @return The HTTP status code of the request
	 */
	public final String getHTTPResponseCode() {
		return httpResponseCode;
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
	 * Returns the server's response as a parsed {@link org.json.JSONObject}
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
	public final void safeRun() throws IOException {
		short failedTries = 0;
		while ( failedTries < allowedConnectionFailuresInSafeRun ) {    //Repeat until limit is reached
			try {
				this.run();
				return;             //If run successful, return
			} catch ( IOException e ) {
				failedTries++;      //If run not successful, increase counter and retry
			}
		}
		throw new IOException("HTTP Request failed "+failedTries+" times"); //Only reached if no run completed successfully
	}

	/**
	 * Rawly writes a string to a connection
	 *
	 * @param value      String to be written to the server
	 * @param connection Connection to send the string to
	 * @throws IOException if the HTTP connection to the server fails
	 */
	protected final static void writeStringToServer(String value, HttpsURLConnection connection) throws IOException {
		OutputStreamWriter osw = new OutputStreamWriter(connection.getOutputStream());
		osw.write(String.format(value));
		osw.flush();
		osw.close();
	}

	/**
	 * Parses the servers response input stream to a String
	 *
	 * @param connection Connection object to read the input stream from
	 * @return The response as a string
	 * @throws IOException if the HTTP connection to the server fails
	 */
	protected final static String readResponseInputStreamToString(HttpsURLConnection connection) throws IOException {
		//1. Create in-/output streams
		BufferedInputStream bis = new BufferedInputStream(connection.getInputStream());
		ByteArrayOutputStream buf = new ByteArrayOutputStream();

		//2. Read the input stream into the buffer
		int result2 = bis.read();
		while (result2 != -1) {
			buf.write((byte) result2);
			result2 = bis.read();
		}

		//3. Create and return String from the buffer
		return buf.toString();
	}


}
