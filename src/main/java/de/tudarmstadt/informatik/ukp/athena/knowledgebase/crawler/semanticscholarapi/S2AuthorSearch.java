package de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.semanticscholarapi;

import java.io.IOException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.exception.NotAvailableException;

/**
 * This class performs an author search on the Semantic Scholar API.
 * An author search returns all information about the chosen author, including:
 * influences (both directions), citations, papers, co-authors, etc
 * <p>
 * An author search requires either Semantic Scholar's internal authorID of a selected author
 * or their name. If only the name is given, the most relevant author with a matching name is chosen.
 *
 * TODO verify, that 'resultType: "AUTHOR_RESULT"' is set in JSON response
 *
 * @author Philipp Emmer
 */
public class S2AuthorSearch extends SemanticScholarAPIRequest {
	private static Logger logger = LogManager.getLogger(S2AuthorSearch.class);

	private String s2id = null;
	private String name = null;
	private int expectedAmountOfPapers = -1;


	/**
	 * Sets the name of the author to be looked up.
	 * Has a lower priority than the s2authorID (see set23id)
	 *
	 * @param name The name of the selected author
	 */
	@Override
	public void setQuery(String name) {
		this.name = name;
	}

	/**
	 * Sets the authorID of the selected author
	 * The authorID can be found out by performing a general search on the name
	 * Has a higher priority than the name.
	 *
	 * @param s2authorID The Semantic Scholar authorID of the selected author
	 */
	public void setS2id(String s2authorID) {
		this.s2id = s2authorID;
	}


	/**
	 * Gathers all required information for the author search by performing a general search on the name
	 * Sets s2id, name and expectedAmountOfPapers of author
	 *
	 * @throws IOException if the HTTP connection to the server fails
	 * @throws NotAvailableException if the information could not be found
	 */
	private void prepare() throws IOException, NotAvailableException {
		if ( this.s2id != null ) {      //If S2ID is known: Prepare by using an author search
			prepareByS2Id();
		}
		else if ( this.name != null ) { //Otherwise: Prepare by using a general search
			prepareByName();
		}
		else { throw new NotAvailableException(); }

		//Throw exception if search failed
		if ( this.expectedAmountOfPapers < 0 ) {
			throw new NotAvailableException();
		}

	}

	/**
	 * Helper method, called by .prepare()
	 * Runs a small author search to find out the information needed to retrieve all available information by the actual request
	 * @throws IOException If an error occurs during the HTTP request
	 */
	private void prepareByS2Id() throws IOException {
		JSONObject result;

		this.expectedAmountOfPapers = 1;
		this.run();

		//Parse the JSON response
		try { result = this.getParsedJSONResponse(); }
		catch (NotAvailableException e) { return; }    //Never thrown, because called after request is run

		//set expected amount of papers
		logger.info("Lookup name: {} S2ID: {}", String.valueOf(this.name), String.valueOf(this.s2id));
		this.expectedAmountOfPapers = result.getJSONObject("author").getJSONObject("papers").getInt("totalResults");

		//Reset this request
		this.validDataIsReady = false;
		this.rawResponse = null;
		this.httpResponseCode = null;
	}

	/**
	 *  Helper method, called by .prepare()
	 *  Runs a general search to find out the information needed to retrieve all available information by the actual request
	 *  @throws IOException If an error occurs during the HTTP request
	 */
	private void prepareByName() throws IOException {
		SemanticScholarAPIRequest preparationRequest = new S2GeneralSearch();
		preparationRequest.setQuery( this.name );
		preparationRequest.run();

		//Parse the JSON response
		JSONObject result;
		try { result = preparationRequest.getParsedJSONResponse(); }
		catch (NotAvailableException e) { return; }    //Never thrown, because called after request is run

		//If multiple results match the name, choose most relevant one
		logger.info("Lookup name: {} S2ID: {}", String.valueOf(this.name), String.valueOf(this.s2id));
		logger.info(result.toString());
		JSONArray matchingAuthors = result.getJSONArray("matchedAuthors");
		JSONObject chosenAuthor = (JSONObject) matchingAuthors.get(0);

		//Set ID
		this.s2id = chosenAuthor.getString("id");

		//S2 returns expectedAmountOfPapers in separate list of stats, so the matching author has to be found
		this.expectedAmountOfPapers = -1;
		JSONArray statsAuthors = result.getJSONObject("stats").getJSONArray("authors");

		for ( int i = 0; i < statsAuthors.length(); i++ ) {     //Find matching author in statsList
			if ( statsAuthors.getJSONObject(i).getString("value").equals(chosenAuthor.getString("name")) ) {
				this.expectedAmountOfPapers = statsAuthors.getJSONObject(i).getInt("documentCount");
				break;
			}
		}
	}

	/**
	 * Establishes an HTTPS connection to Semantic Scholar's API and POSTs a request
	 * Automatically runs .prepare() to gather the s2id and expectedAmountOfPapers first, if not already set
	 *
	 * @throws IOException if the HTTP connection to the server fails
	 */
	@Override
	public void run() throws IOException {

		//Prepare (find ID & expectedAmountOfPapers) if necessary
		if ( this.s2id == null || this.expectedAmountOfPapers < 0 ) {
			try { this.prepare(); }
			catch( NotAvailableException e ) {  //Author not found on Semantic Scholar
				this.validDataIsReady = true;
				this.rawResponse = "";
				return;
			}
		}

		//Create data payload of POST request, containing search parameters
		String searchPayLoad = createSearchPayload();

		//Create URL for general search request
		String href = semanticScholarInternalApiUrl + "/author/" + this.s2id;

		//Create connection and set basic parameters
		URL url = new URL(href);
		HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setInstanceFollowRedirects(false);
		connection.setConnectTimeout(30 * 1000);        //30s
		connection.setUseCaches(false);                 //Don't cache anything

		//Set connection headers
		connection.setRequestMethod("GET");
		connection.setRequestProperty("charset", "utf-8");
		connection.setRequestProperty("origin", "https://www.semanticscholar.org");
		connection.setRequestProperty("accept-language", "de-DE,de;q=0.9,en-DE;q=0.8,en;q=0.7,en-US;q=0.6");
		connection.setRequestProperty("Accept", "*/*");
		connection.setRequestProperty("cache-control", "no-cache,no-store,must-revalidate,max-age=-1");
		connection.setRequestProperty("content-type", "application/json");
		connection.setRequestProperty("User-Agent", userAgentString);

		//Write search payload to server (BODY of POST request)
		writeStringToServer(searchPayLoad, connection);

		//Convert received JSON to String
		this.rawResponse = readResponseInputStreamToString(connection);

		this.httpResponseCode = Integer.toString(connection.getResponseCode());
		this.validDataIsReady = true;
	}

	/**
	 * Creates the Data Payload for the POST request of the search
	 * Look here for fine tuning of the search parameters
	 *
	 * @return The JSON formatted payload String
	 */
	private String createSearchPayload() {

		return "{\"queryString\":\"\","
				+ "\"page\":1,"
				+ "\"pageSize\":" + this.expectedAmountOfPapers + ","
				+ "\"sort\":\"influence\","
				+ "\"authors\":[],"
				+ "\"coAuthors\":[],"
				+ "\"venues\":[],"
				//+ "\"facets\":{},"
				+ "\"yearFilter\":null,"
				+ "\"requireViewablePdf\":false,"
				+ "\"publicationTypes\":[]}";
	}

}
