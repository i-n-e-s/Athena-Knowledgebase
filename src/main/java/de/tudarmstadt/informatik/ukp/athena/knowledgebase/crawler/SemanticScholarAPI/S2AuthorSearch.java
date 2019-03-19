package de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.SemanticScholarAPI;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.ParsedDataInserter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URL;

/**
 * This class performs an author search on the SemanticScholar API.
 * An authorSearch returns all information about the chosen author, including:
 * influences (both directions), citations, papers, coAuthors, etc
 * <p>
 * An author search requires either SemanticScholars internal authorID of a selected author
 * or his name. If only his name is given, the most relevant author with a matching name is chosen.
 *
 * TODO verify, that 'resultType: "AUTHOR_RESULT"' is set in JSON response
 *
 * @author Philipp Emmer
 */
public class S2AuthorSearch extends SemanticScholarAPIrequest {
    private static Logger logger = LogManager.getLogger(ParsedDataInserter.class);

    private String s2id = null;
    private String name = null;
    private int expectedAmountOfPapers = -1;

    private String rawResponse = null;              //Response as received from Server
    private boolean validDataIsReady = false;       //True if response is ready

    private String HTTPResponseCode = null;

    /**
     * Sets the name of the Author to be looked up.
     * Has a lower priority than the S2AuthorID
     *
     * @param name The name of the selected author
     */
    @Override
    public void setQuery(String name) {
        this.name = name;
    }

    /**
     * Sets the authorID of the selected Author
     * The authorID can be found out by performing a general search on the Name
     * Has a higher priority than the name.
     *
     * @param s2authorID The SemanticScholar authorID of the selected author
     */
    public void setS2id(String s2authorID) {
        this.s2id = s2authorID;
    }


    /**
     * Gathers all required Information for the AuthorSearch by performing generalSearch on the name
     * Sets s2id, name and expectedAmountOfPapers of author
     *
     * @throws IOException when some HTTP stuff goes wrong
     * @throws NotAvailableException if the information could not be found
     */
    public void prepare() throws IOException, NotAvailableException {
        SemanticScholarAPIrequest preparationRequest;
        JSONObject result;
        if ( this.s2id != null ) {      //If S2ID is known: Prepare by using AuthorSearch
            this.expectedAmountOfPapers = 1;
            this.run();
            //Parse the JSON response
            try { result = this.getParsedJSONResponse(); }
            catch (NotAvailableException e) { return; }    //Never thrown, because called after request is run
            //set expected Amount of papers
            logger.info("Lookup name: "+String.valueOf(this.name)+" S2ID: "+String.valueOf(this.s2id));
            this.expectedAmountOfPapers = result.getJSONObject("author").getJSONObject("papers").getInt("totalResults");
            //Reset this request
            this.validDataIsReady = false;
            this.rawResponse = null;
            this.HTTPResponseCode = null;
        }
        else if ( this.name != null ) { //Otherwise: Prepare by using GeneralSearch
            preparationRequest = new S2GeneralSearch();
            preparationRequest.setQuery( this.name );
            preparationRequest.run();

            //Parse the JSON response
            try { result = preparationRequest.getParsedJSONResponse(); }
            catch (NotAvailableException e) { return; }    //Never thrown, because called after request is run

            //If multiple results match the name, choose most relevant one
            logger.info("Lookup name: "+String.valueOf(this.name)+" S2ID: "+String.valueOf(this.s2id));
            logger.info(result.toString());
            JSONArray matchingAuthors = result.getJSONArray("matchedAuthors");
            JSONObject chosenAuthor = (JSONObject) matchingAuthors.get(0);

            //Set ID
            this.s2id = chosenAuthor.getString("id");

            //S2 returns expectedAmountOfPapers in separate list of stats, so the matching author has to be found
            this.expectedAmountOfPapers = -1;
            JSONArray statsAuthors = result.getJSONObject("stats").getJSONArray("authors");

            for ( int i = 0; i < statsAuthors.length(); i++ ) {     //Find matching Author in statsList
                if ( statsAuthors.getJSONObject(i).getString("value").equals(chosenAuthor.getString("name")) ) {
                    this.expectedAmountOfPapers = statsAuthors.getJSONObject(i).getInt("documentCount");
                    break;
                }
            }
        }
        else { throw new NotAvailableException(); }

        //Throw exception if search failed
        if ( this.expectedAmountOfPapers < 0 ) {
            throw new NotAvailableException();
        }


    }

    /**
     * Establishes an HTTPS Connection to SemanticScholarAPI and POSTs a request
     * Automatically runs .prepare() to gather the s2id and expectedAmountOfPapers first, if not already set
     *
     * @throws IOException when some HTTP stuff goes wrong
     */
    public void run() throws IOException {

        //Prepare (find ID & expectedAmountOfPapers) if necessary
        if ( this.s2id == null || this.expectedAmountOfPapers < 0 ) {
            try { this.prepare(); }
            catch( NotAvailableException e ) {  //Author not found on semanticScholar
                this.validDataIsReady = true;
                this.rawResponse = "";
                return;
            }
        }

        //Create Data Payload of POST request, containing Search parameters
        String searchPayLoad = createSearchPayLoad();

        //Create URL for general search request
        String href = SemanticScholarInternalAPIURL + "/author/" + this.s2id;

        //Create Connection and set basic parameters
        URL url = new URL(href);
        HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
        connection.setDoOutput(true);
        connection.setInstanceFollowRedirects(false);
        connection.setConnectTimeout(30 * 1000);        //30s
        connection.setUseCaches(false);                 //Don't cache anything

        //Set Connection Headers
        connection.setRequestMethod("GET");
        connection.setRequestProperty("charset", "utf-8");
        connection.setRequestProperty("origin", "https://www.semanticscholar.org");
        connection.setRequestProperty("accept-language", "de-DE,de;q=0.9,en-DE;q=0.8,en;q=0.7,en-US;q=0.6");
        connection.setRequestProperty("Accept", "*/*");
        connection.setRequestProperty("cache-control", "no-cache,no-store,must-revalidate,max-age=-1");
        connection.setRequestProperty("content-type", "application/json");
        connection.setRequestProperty("User-Agent", UserAgentString);

        //Write SearchPayload to Server (BODY of POST request)
        writeStringToServer(searchPayLoad, connection);

        //Convert received JSON to String
        this.rawResponse = readResponseInputStreamToString(connection);

        this.HTTPResponseCode = Integer.toString(connection.getResponseCode());
        this.validDataIsReady = true;
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
     * Returns the HTTP Status Code of the request
     *
     * @return The HTTP Status Code of the request
     */
    @Override
    public String getHTTPResponseCode() {
        return HTTPResponseCode;
    }


    /**
     * Creates the Data Payload for the POST request of the Search
     * Look here for fine tuning of the Search parameters
     *
     * @return The JSON formatted PayLoad String
     */
    private String createSearchPayLoad() {

        String searchPayLoad = "{\"queryString\":\"\","
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
        return searchPayLoad;
    }

}
