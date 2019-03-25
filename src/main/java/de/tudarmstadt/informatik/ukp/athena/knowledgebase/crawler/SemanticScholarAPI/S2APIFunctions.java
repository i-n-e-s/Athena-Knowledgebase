package de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.semanticscholarapi;


import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.ParsedDataInserter;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Model;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Paper;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Person;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.exception.NotAvailableException;

/**
 * This class offers functions to easily access the Semantic Scholar API
 *
 * @author Philipp Emmer
 */
public class S2APIFunctions {
	private static Logger logger = LogManager.getLogger(ParsedDataInserter.class);


	/**
	 * Returns all papers published by the given author listed on Semantic Scholar
	 * Performs two requests to Semantic Scholar's APIs:
	 * 1. General search on the internal API to find Semantic Scholar's authorID
	 * 2. Paper search on the public API
	 * <p>
	 * Important: Result of the paper search doesn't always include the exact publication date, often only the year
	 * -> current workaround: 1st January as date if only year is known; Year 0 if nothing is known
	 * <p>
	 * @deprecated An author search delivers more precise information about papers than a paper search
	 * Use completeAuthorInformationByAuthorSearch instead
	 *
	 * @param author The author to be searched
	 * @return List of all papers published by author
	 * @throws IOException   if the HTTP connection to the server fails
	 * @throws JSONException if the server's response can't be properly parsed as JSON
	 */
	@Deprecated
	public static ArrayList<Paper> getAllPapersByAuthor(Person author) throws IOException, JSONException {

		//1. Check if Semantic Scholar authorID is available
		if (author.getSemanticScholarID() == null) {
			String id = getAuthorsS2ID(author);
			author.setSemanticScholarID(id);
		}

		//2. Perform paper search
		SemanticScholarAPIRequest paperSearch = new S2PaperSearch();
		paperSearch.setQuery(author.getSemanticScholarID());
		paperSearch.safeRun();

		//3. Parse JSON response
		JSONObject response;
		try {
			response = paperSearch.getParsedJSONResponse();
		}//Since the method is called after performing the request
		catch (NotAvailableException e) {
			return null;
		}       //the NotAvailableException should not be thrown

		//4. Create ArrayList of papers from the JSON response
		JSONArray searchResults = response.getJSONArray("papers");
		ArrayList<Paper> paperList = parseS2PaperSearchJSONArrayToPaperArrayList(searchResults);

		//5. Return list
		return paperList;
	}

	/**
	 * Helper method called in {@link S2APIFunctions#getAllPapersByAuthor}
	 * Gets a list of papers as JSONArray and returns an ArrayList of paper objects
	 *
	 * @param paperJSONarr The JSONArray to be parsed
	 * @return ArrayList of paper objects
	 * @deprecated See {@link S2APIFunctions#getAllPapersByAuthor}
	 */
	@Deprecated
	private static ArrayList<Paper> parseS2PaperSearchJSONArrayToPaperArrayList(JSONArray paperJSONarr) {
		ArrayList<Paper> paperList = new ArrayList<>();

		for (int i = 0; i < paperJSONarr.length(); i++) {
			Paper currPaper = new Paper();
			JSONObject cSR = (JSONObject) paperJSONarr.get(i);
			if (!cSR.getString("paperId").equals("null")) {
				currPaper.setSemanticScholarID(cSR.getString("paperId"));
			}
			if (!cSR.getString("title").equals("null")) {
				currPaper.setTitle(cSR.getString("title"));
			}
			if (!cSR.getString("url").equals("null")) {
				currPaper.setRemoteLink(cSR.getString("url"));
			}
			if (cSR.getString("year") != null && !cSR.getString("year").equals("null")) {
				currPaper.setReleaseDate(LocalDate.of(Integer.parseInt(cSR.getString("year")), 1, 1));
			} else {
				currPaper.setReleaseDate(LocalDate.of(0, 1, 1));
			}
			paperList.add(currPaper);
		}

		//5. Return list
		return paperList;

	}

	/**
	 * Retrieves the author's Semantic Scholar id by performing a general search of his name
	 * choosing the most relevant result.
	 * Note:
	 * In the S2GeneralSearch request, we ask the server to sort the results by relevance
	 *
	 * @param author The author to be searched
	 * @throws IOException   If something goes wrong while communicating with the server
	 * @throws JSONException If the server doesn't respond with valid JSON
	 */
	public static String getAuthorsS2ID(Person author) throws IOException, JSONException {

		//1. Perform general search
		SemanticScholarAPIRequest generalSearch = new S2GeneralSearch();
		generalSearch.setQuery(author.getFullName());
		generalSearch.safeRun();

		//2. Parse the JSON response
		JSONObject response;
		try {
			response = generalSearch.getParsedJSONResponse();
		} catch (NotAvailableException e) {
			return null;
		}    //Should never be thrown, because called after request

		//3. If multiple results match the name, choose most relevant one
		JSONArray matchingAuthors = response.getJSONArray("matchedAuthors");
		JSONObject chosenAuthor = (JSONObject) matchingAuthors.get(0);

		//4. Return ID
		return chosenAuthor.getString("id");

	}

	/**
	 * Performs an author search of the author's Semantic Scholar ID (tries to find ID by full name if ID not available)
	 * and adds all found information to the author object
	 * <p>
	 * Information is added in-place, so the given object is altered
	 * If overwrite == false: Only unset attributes will be overwritten
	 * <p>
	 * In worst case, two API accesses will happen + 1 for every person with unknown s2id, connected to a paper already connected to the searched author
	 *
	 *
	 * @param author    The author to be looked up
	 * @param overwrite true if already set attributes should be overwritten with Semantic Scholar's data, false to only set null attributes
	 * @return false if no information could be found on Semantic Scholar, true otherwhise
	 * @throws IOException   If something goes wrong while communicating with the server
	 * @throws JSONException If the server doesn't respond with valid JSON
	 */
	public static boolean completeAuthorInformationByAuthorSearch(Person author, boolean overwrite) throws IOException, JSONException {
		//1. Perform author search
		if( author.getFullName() == null && author.getSemanticScholarID() == null ) {   //Make sure name or S2ID is known
			return false;
		}
		S2AuthorSearch authorSearch = new S2AuthorSearch();
		authorSearch.setQuery(author.getFullName());
		if (author.getSemanticScholarID() != null) {
			authorSearch.setS2id(author.getSemanticScholarID());
		}
		authorSearch.safeRun();

		//2. Parse the JSON response
		JSONObject response;
		try {
			if (authorSearch.getRawResponse() == "") { return false; }  //If author not found on S2
			response = authorSearch.getParsedJSONResponse();
		} catch (NotAvailableException e) {       //Never thrown, because called after request
			logger.info("Response not ready");
			e.printStackTrace();
			return false;
		}

		//3.1 Parse JSONObject to temp Author TODO overwrite?
		logger.info("Start to parse:\n\n"+response.toString());
		parseAddS2InternalAPIAuthorJSON(author, overwrite, response);
		logger.info("\n\ngot:\n"+author.toString()+"\n\n");

		//3.2 Add information from temp author to original author
		//author.complementBy(temp);

		return true;
	}

	/**
	 * Helper method called in {@link S2APIFunctions#completeAuthorInformationByAuthorSearch}
	 * Gets the JSONObject returned by the author search and adds the attributes to the given person object
	 * @param author The object to add the info to
	 * @param overwrite true if already set attributes should be overwritten with Semantic Scholar's data, false to only set null attributes
	 * @param authorSearchResponse response to an author search as JSONObject
	 */
	private static void parseAddS2InternalAPIAuthorJSON(Person author, boolean overwrite, JSONObject authorSearchResponse) {
		//1 Set authors S2ID
		if( author.getSemanticScholarID() == null || overwrite ) {
			String foundS2ID = authorSearchResponse.getJSONObject("author").getString("id");
			author.setSemanticScholarID(foundS2ID);
		}

		//2 Add all papers found on S2
		JSONArray papersJSON = authorSearchResponse.getJSONObject("author").getJSONObject("papers").getJSONArray("results");
		for (int i = 0; i < papersJSON.length(); i++) {   //Add all found papers

			String title = papersJSON.getJSONObject(i).getJSONObject("title").getString("text");
			logger.info("Parse paper "+title+"\ti="+i);

			Paper currPaper = null;

			//Check if paper is already connected to author
			for( Paper authorsPaper : author.getPapers() ) {
				if( authorsPaper.getTitle().equals(title) ) { currPaper = authorsPaper; break; }
			}

			//If not, search for paper title in DB
			if( currPaper == null ) { currPaper = Paper.findOrCreate(null, title); }

			//Always connect this author with paper
			Model.connectAuthorPaper(author, currPaper);    //TODO check duplicates

			parseAddS2InternalAPIPaperJSON(papersJSON.getJSONObject(i), false, currPaper);

		}

		//3 Set top 5 authors influenced by this one the most
		if ( author.getTop5influenced() == null || author.getTop5influenced().size() == 0 || overwrite ) {
			JSONArray influenced = authorSearchResponse.getJSONObject("author").getJSONObject("statistics").getJSONObject("influence").getJSONArray("influenced");
			author.setTop5influenced( parseS2AuthorSearchInfluenceJSONArrayToAuthorArrayList(influenced, true));
		}

		//4 Set top 5 authors with highest influence on this author
		if ( author.getTop5influencedBy() == null || author.getTop5influencedBy().size() == 0 || overwrite ) {
			JSONArray influencedBy = authorSearchResponse.getJSONObject("author").getJSONObject("statistics").getJSONObject("influence").getJSONArray("influencedBy");
			author.setTop5influencedBy(parseS2AuthorSearchInfluenceJSONArrayToAuthorArrayList(influencedBy, true));
		}
	}

	/**
	 * Helper method called in {@link S2APIFunctions#completeAuthorInformationByAuthorSearch}
	 * Gets a list of either influenced or influencing authors to this one
	 * as JSONArray and returns an ArrayList of person objects
	 *
	 * @param influenceJSON The JSON object to be parsed
	 * @param overwrite true if already set attributes should be overwritten with Semantic Scholar's data, false to only set null attributes
	 * @return the parsed list of authors
	 */
	private static ArrayList<Person> parseS2AuthorSearchInfluenceJSONArrayToAuthorArrayList(JSONArray influenceJSON, boolean overwrite) {
		ArrayList<Person> result = new ArrayList<>();
		for (int i = 0; i < influenceJSON.length() && i < 5; i++) { //Iterate through list
			JSONObject jsonAuthorInfo = influenceJSON.getJSONObject(i).getJSONObject("author");
			String s2id = jsonAuthorInfo.getJSONArray("ids").getString(0);

			//Check if DB entry of person with matching S2ID exists
			Person query = new Person();
			query.setFullName(jsonAuthorInfo.getString("name"));
			query.setSemanticScholarID(s2id);
			logger.info("Query ID: "+query.getPersonID());
			Person currInfl = Person.findOrCreate( query );     //If no matching DB-entry is found, create new person

			//Set attributes:
			if ( currInfl.getFullName() == null || overwrite ) {
				currInfl.setFullName(jsonAuthorInfo.getString("name"));
			}
			if ( currInfl.getSemanticScholarID() == null || overwrite ) {
				currInfl.setSemanticScholarID(jsonAuthorInfo.getJSONArray("ids").getString(0));
			}
			result.add(currInfl);
		}
		return result;
	}

	/**
	 * Parses JSON paper-objects from Semantic Scholar's internal API and
	 * adds information inplace to the given paper object
	 *
	 * This function is longer than the target maximum length defined in the style convention,
	 * due to its nature as a parsing function. Splitting it up further would decrease the readability
	 *
	 * @param paperJSON The JSON Object to be parsed
	 * @param overwrite true if already set attributes should be overwritten with Semantic Scholar's data, false to only set null attributes
	 * @param dest      The paper object to add the information to
	 */
	private static void parseAddS2InternalAPIPaperJSON(JSONObject paperJSON, boolean overwrite, Paper dest) {
		//Title
		if (overwrite || dest.getTitle() == null) {
			try {
				String title = paperJSON.getJSONObject("title").getString("text");
				dest.setTitle(title);
			} catch (JSONException e) {
				return;
			}
		}
		//S2ID
		if (overwrite || dest.getSemanticScholarID() == null) {
			try {
				String S2ID = paperJSON.getString("id");
				dest.setSemanticScholarID(S2ID);
			} catch (JSONException e) {
				return;
			}
		}
		//citations
		if (overwrite || dest.getAmountOfCitations() == null) {
			try {
				int foundCitations = Integer.parseInt(paperJSON.getJSONObject("citationStats").getString("numCitations"));
				dest.setAmountOfCitations(foundCitations);
			} catch (JSONException e) {
			}
		}

		//url
		if (overwrite || dest.getRemoteLink() == null) {
			try {
				JSONObject link = paperJSON.getJSONArray("links").getJSONObject(0);
				dest.setRemoteLink(link.getString("url"));
			} catch (JSONException e) {
			}
		}
		//abstract
		if (overwrite || dest.getPaperAbstract() == null) {
			try {
				String text = paperJSON.getJSONObject("paperAbstract").getString("text");
				dest.setPaperAbstract(text);
			} catch (JSONException e) {
			}
		}

		//releaseDate
		if (overwrite || dest.getReleaseDate() == null) {
			try {
				String releaseYearString = paperJSON.getJSONObject("year").getString("text");
				try {
					int releaseYear = Integer.parseInt(releaseYearString);
					if (releaseYear > 1000 && releaseYear < 2100) {
						dest.setReleaseDate(LocalDate.of(releaseYear, 1, 1));
					}
				} catch (NumberFormatException e) {
				}
			} catch (JSONException e) {
			}
		}

		//authors
		if (overwrite) {
			dest.setAuthors(new HashSet<>());   //If overwrite is set, reset the authors
		}

		//1. Fetch S2ID for all currently connected Authors to minimize duplicates
		for( Person a : dest.getAuthors() ) {
			if( a.getSemanticScholarID() != null && a.getSemanticScholarID() != "") { continue; }   //Skip retrieving already known S2IDs
			try { a.setSemanticScholarID(getAuthorsS2ID(a)); }
			catch( IOException | JSONException e ) {}
		}

		//Add authors
		JSONArray authorsJSON = paperJSON.getJSONArray("authors");
		for (int i = 0; i < authorsJSON.length(); i++) {                         //Add every author to the paper

			String name, s2id;
			try {
				JSONObject cAJSON = authorsJSON.getJSONArray(i).getJSONObject(0);
				name = cAJSON.getString("name");
				s2id = cAJSON.getJSONArray("ids").getString(0);
			} catch ( JSONException e ) {
				continue;       //If name or S2ID unknown, skip the author
			}
			logger.info("Want to add author "+name+" to paper "+dest.getTitle());

            Person authorObjToBeAdded = null;
            //Check if author is already connected to paper
            for ( Person papersKnownAuthor : dest.getAuthors() ) {
               if ( (papersKnownAuthor.getSemanticScholarID() != null && papersKnownAuthor.getSemanticScholarID().equals(s2id)) || papersKnownAuthor.getFullName().equals(name)) {
                    authorObjToBeAdded = papersKnownAuthor;
                    logger.info("Found and reuse "+authorObjToBeAdded.getFullName()+"("+authorObjToBeAdded.getPersonID()+")");
                    break;
                } else { logger.info( name + " " + s2id+ " does not equal " + papersKnownAuthor.getFullName()+ " "+ papersKnownAuthor.getSemanticScholarID()); }
            }

			//If not already connected, check if author is in DB
			if ( authorObjToBeAdded == null ) {
				Person query = new Person();
				query.setSemanticScholarID(s2id);
				query.setFullName(name);
				authorObjToBeAdded = Person.findOrCreate(query);
			}

			if( overwrite || authorObjToBeAdded.getFullName() == null ) { authorObjToBeAdded.setFullName(name); }
			if( overwrite || authorObjToBeAdded.getSemanticScholarID() == null ) { authorObjToBeAdded.setSemanticScholarID(s2id); }

			Model.connectAuthorPaper(authorObjToBeAdded, dest);
		}

	}

	/**
	 * Performs a general search of the paper's name and adds all found information to the paper object
	 * <p>
	 * Information is added in-place, so the given object is altered
	 * Only unset attributes will be overwritten
	 * <p>
	 * Optional search for s2Id of paper if known
	 * TODO releaseDate will be set to 1.1. of release year, Semantic Scholar doesn't tell exact date
	 *
	 * @param paper     The paper to be looked up
	 * @param overwrite true if already set attributes should be overwritten with Semantic Scholar's data, false to only set null attributes
	 * @throws IOException   If something goes wrong communicating with the server
	 * @throws JSONException If the server doesn't respond with valid JSON
	 */
	public static void completePaperInformationByGeneralSearch(Paper paper, boolean overwrite) throws IOException, JSONException {

		//1. General search of full paper name
		SemanticScholarAPIRequest generalSearch = new S2GeneralSearch();
		generalSearch.setQuery(paper.getTitle());
		generalSearch.safeRun();

		//2. Parse answer
		JSONObject response = null;
		try {
			response = generalSearch.getParsedJSONResponse();
		} catch (NotAvailableException e) {
			e.printStackTrace();
		}   //Never thrown, because called after the request

		//3. If multiple results match the name, choose most relevant one
		JSONArray matchingPapers = response.getJSONArray("results");
		JSONObject chosenPaper = (JSONObject) matchingPapers.get(0);

		//4. Parse JSON response and add results to the given paper object
		parseAddS2InternalAPIPaperJSON(chosenPaper, overwrite, paper);
	}


	/**
	 * Retrieves the amount of citations of a specific paper
	 * Performs a general search of the paper's name on Semantic Scholar and chooses the most relevant matching paper
	 * <p>
	 * Assumes that papers don't have duplicate names
	 * <p>
	 * @deprecated Use {@link S2APIFucntions#completePaperInformationByGeneralSearch()} instead, it retrieves more information using the same amount of requests
	 *
	 * @param paper Paper whose amount of citation is to be retrieved
	 * @return Amount of citations
	 * @throws IOException   If something goes wrong while communicating with the server
	 * @throws JSONException If the server doesn't respond with valid JSON
	 */
	@Deprecated
	public static int getCitationAmountByPaper(Paper paper) throws IOException, JSONException {
		//1. General search of full paper name
		SemanticScholarAPIRequest generalSearch = new S2GeneralSearch();
		generalSearch.setQuery(paper.getTitle());
		generalSearch.safeRun();

		//2. Parse answer
		JSONObject response;
		try {
			response = generalSearch.getParsedJSONResponse();
		} catch (NotAvailableException e) {
			return -1;
		}   //Never thrown, because called after the request

		//3. If multiple results match the name, choose most relevant one
		JSONArray matchingPapers = response.getJSONArray("results");
		JSONObject chosenPaper = (JSONObject) matchingPapers.get(0);

		//4. Get citation amount
		return Integer.parseInt(chosenPaper.getJSONObject("citationStats").getString("numCitations"));
	}


}
