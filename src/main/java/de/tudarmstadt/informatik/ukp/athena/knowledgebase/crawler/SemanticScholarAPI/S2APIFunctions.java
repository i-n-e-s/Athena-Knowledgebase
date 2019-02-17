package de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.SemanticScholarAPI;


import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.hibernate.PaperHibernateAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.hibernate.PersonHibernateAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.PaperJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.PersonJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Model;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Paper;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Person;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * This class offers functions to easily access the SemanticScholarAPI
 *
 * @author Philipp Emmer
 */
public class S2APIFunctions {

    /**
     * Returns all papers published by the given author listed on SemanticScholar
     * Performs 2 requests to SemanticScholars APIs:
     * 1. General search on the internal API to find SemanticScholars authorID
     * 2. Paper search on the public API
     * <p>
     * Important: Result of paperSearch doesn't always include the exact publication Date, often only the Year
     * -> current workaround: 1st January as date if only year is known; Year 0 if nothing is known
     * <p>
     * Deprecated: AuthorSearch delivers more precise information about Papers then PaperSearch
     * Use completeAuthorInformationByAuthorSearch instead
     *
     * @param author The author to be searched
     * @return List of all papers published by author
     * @throws IOException   if the HTTP connection to the server fails
     * @throws JSONException if the servers response can't be properly parsed as JSON
     */
    @Deprecated
    public static ArrayList<Paper> getAllPapersByAuthor(Person author) throws IOException, JSONException {

        //1. Check if SemanticScholar authorID is available
        if (author.getSemanticScholarID() == null) {
            String id = getAuthorsS2ID(author);
            author.setSemanticScholarID(id);
        }

        //2. Perform paper search
        SemanticScholarAPIrequest paperSearch = new S2PaperSearch();
        paperSearch.setQuery(author.getSemanticScholarID());
        paperSearch.run();

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

        //5. Return List
        return paperList;
    }

    /**
     * Helping method called in getAllPapersByAuthor
     * Gets a List of Papers as JSONArray and returns an ArrayList of Paper Objects
     *
     * @param paperJSONarr The JSONArray to be parsed
     * @return ArrayList of Paper Objects
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

        //5. Return List
        return paperList;

    }

    /**
     * Retrieves the authors SemanticScholarID by performing a general search of his name
     * choosing the most relevant result.
     * Note:
     * In the S2GeneralSearch request, we ask the Server to sort the results by relevance
     *
     * @param author The author to be searched
     * @throws IOException   If something goes wrong communicating with the Server
     * @throws JSONException If the server doesn't respond with valid JSON
     */
    public static String getAuthorsS2ID(Person author) throws IOException, JSONException {

        //1. Perform general search
        SemanticScholarAPIrequest generalSearch = new S2GeneralSearch();
        generalSearch.setQuery(author.getFullName());
        generalSearch.run();

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
     * Performs an author Search of the authors SemanticScholar ID (tries to find ID by Full Name if ID not available)
     * and adds all found Information to the author Object
     * <p>
     * Information is added in-place, so the given Object is altered
     * If overwrite == false: Only unset attributes will be overwritten
     * <p>
     * In worst case 2 API accesses will happen, taking about 3s
     *
     * @param author    The author to be looked up
     * @param overwrite True if attributes should be overwritten with SemanticScholars Data
     * @return false If no Information could be found on SemanticScholar
     * @throws IOException   If something goes wrong communicating with the Server
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
        authorSearch.run();

        //2. Parse the JSON response
        JSONObject response;
        try {
            if (authorSearch.getRawResponse() == "") { return false; }  //If author not found on S2
            response = authorSearch.getParsedJSONResponse();
        } catch (NotAvailableException e) {       //Never thrown, because called after request
            System.err.println("Response not ready");
            e.printStackTrace();
            return false;
        }

        //3.1 Parse JSONObject to temp Author TODO overwrite?
        parseAddS2InternalAPIAuthorJSON(author, overwrite, response);

        //3.2 Add Information from temp Author to original Author
        //author.complementBy(temp);

        return true;
    }

    /**
     * Helping method called in completeAuthorInformationByAuthorSearch
     * Gets the JSONObject returned by the AuthorSearch and adds the attributes to the given
     * @param author The Object to add the info to
     * @param overwrite true if Attributes should be overwritten
     * @param AuthorSearchResponse response to an AuthorSearch as JSONObject
     */
    private static void parseAddS2InternalAPIAuthorJSON(Person author, boolean overwrite, JSONObject AuthorSearchResponse) {
        //1 Set Top 5 authors influenced by this one the most TODO overwrite?
        if ( author.getTop5influenced() == null || author.getTop5influenced().size() == 0 || overwrite ) {
            JSONArray influenced = AuthorSearchResponse.getJSONObject("author").getJSONObject("statistics").getJSONObject("influence").getJSONArray("influenced");
            author.setTop5influenced( parseS2AuthorSearchInfluenceJSONArrayToAuthorArrayList(influenced, true));
        }
        for ( Person a : author.getTop5influenced() ) {
            System.out.println("Influenced: " + (a.getSemanticScholarID() == null ? "null" : a.getSemanticScholarID()) + " " + a.getFullName());
        }

        //2 Set Top 5 authors with highest influence on this author
        if ( author.getTop5influencedBy() == null || author.getTop5influencedBy().size() == 0 || overwrite ) {
            JSONArray influencedBy = AuthorSearchResponse.getJSONObject("author").getJSONObject("statistics").getJSONObject("influence").getJSONArray("influencedBy");
            author.setTop5influencedBy(parseS2AuthorSearchInfluenceJSONArrayToAuthorArrayList(influencedBy, true));
        }

        //3 Add all papers found on S2
        PaperJPAAccess filer = new PaperJPAAccess();
        JSONArray papersJSON = AuthorSearchResponse.getJSONObject("author").getJSONObject("papers").getJSONArray("results");
        for (int i = 0; i < papersJSON.length(); i++) {   //Add all found papers


            //Search for paper title in DB
            String title = papersJSON.getJSONObject(i).getJSONObject("title").getString("text");
            List<Paper> matchingPapersInDB = filer.getByTitle( title );
            Paper currPaper;

            //If matching paper is found, choose existing, else create new
            if( matchingPapersInDB != null && matchingPapersInDB.size() > 0 ) {
                currPaper = matchingPapersInDB.get(0);
            } else {
                currPaper = new Paper();
            }

            parseAddS2InternalAPIPaperJSON(papersJSON.getJSONObject(i), false, currPaper);

            Model.connectAuthorPaper(author, currPaper);    //TODO check duplicates

        }

        //4 Set authors S2ID
        if( author.getSemanticScholarID() == null || overwrite ) {
            String foundS2ID = AuthorSearchResponse.getJSONObject("author").getString("id");
            author.setSemanticScholarID(foundS2ID);
        }
    }

    /**
     * Helping method called in completeAuthorInformationByAuthorSearch
     * Gets a list of either influenced or influencing Authors to this one
     * as JSONArray and returns an ArrayList of Author Objects
     *
     * @param influenceJSON The JSON object to be parsed
     * @param overwrite true if attributes should be overwritten
     * @return the parsed list of authors
     */
    private static ArrayList<Person> parseS2AuthorSearchInfluenceJSONArrayToAuthorArrayList(JSONArray influenceJSON, boolean overwrite) {
        ArrayList<Person> result = new ArrayList<>();
        PersonJPAAccess filer = new PersonJPAAccess();
        for (int i = 0; i < influenceJSON.length() && i < 5; i++) { //Iterate through list
            JSONObject jsonAuthorInfo = influenceJSON.getJSONObject(i).getJSONObject("author");
            String s2id = jsonAuthorInfo.getJSONArray("ids").getString(0);

            //Check if DB entry of Person with matching S2ID exists
            Person currInfl = filer.getBySemanticScholarID( s2id );
            //If not, search for entry with matching Name
            if ( currInfl == null ) {
                List<Person> currInflList = filer.getByFullName(jsonAuthorInfo.getString("name"));
                if ( currInflList != null && currInflList.size() > 0 ) {
                    currInfl = currInflList.get(0);
                }
            }
            //If neither way matches were found, create new
            if ( currInfl == null ) { currInfl = new Person(); }

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
     * Parses JSON paper-objects from SemanticScholars Internal API and
     * adds information inplace to the given paper object
     *
     * @param paperJSON The JSON Object to be parsed
     * @param overwrite true if existing data should be overwritten
     * @param dest      The Paper object to add the information to
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
        //authors
        if (overwrite) {
            dest.setAuthors(new HashSet<>());
        }    //If overwrite is set, reset the authors

        JSONArray authorsJSON = paperJSON.getJSONArray("authors");
        for (int i = 0; i < authorsJSON.length(); i++) {                         //Add every Author to paper
            JSONObject cAJSON = authorsJSON.getJSONArray(i).getJSONObject(0);
            Person authorObjToBeAdded = new Person();
            try {
                authorObjToBeAdded.setFullName(cAJSON.getString("name"));
                authorObjToBeAdded.setSemanticScholarID(cAJSON.getJSONArray("ids").getString(0));
            } catch (JSONException e) {
                continue;
            }   //If name or S2ID unknown, skip the Author

            dest.addAuthor(authorObjToBeAdded);
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
    }


    /**
     * Performs a general Search of the papers Name and adds all found Information to the paper Object
     * <p>
     * Information are added in-place, so the given Object is altered
     * Only unset attributes will be overwritten
     * <p>
     * TODO Search for s2Id of paper if known
     * TODO releaseDate will be set to 1.1. of release Year, SemanticScholar doesn't tell exact date
     *
     * @param paper     The paper to be looked up
     * @param overwrite True if attributes should be overwritten with SemanticScholars Data
     * @throws IOException   If something goes wrong communicating with the Server
     * @throws JSONException If the server doesn't respond with valid JSON
     */
    public static void completePaperInformationByGeneralSearch(Paper paper, boolean overwrite) throws IOException, JSONException {

        //1. General Search of full Paper name
        SemanticScholarAPIrequest generalSearch = new S2GeneralSearch();
        generalSearch.setQuery(paper.getTitle());
        generalSearch.run();

        //2. Parse answer
        JSONObject response;
        try {
            response = generalSearch.getParsedJSONResponse();
        } catch (NotAvailableException e) {
            return;
        }   //Never thrown, because called after the request

        //3. If multiple results match the name, choose most relevant one
        JSONArray matchingPapers = response.getJSONArray("results");
        JSONObject chosenPaper = (JSONObject) matchingPapers.get(0);

        //4. Parse JSON response and add results to the given Paper Object
        parseAddS2InternalAPIPaperJSON(chosenPaper, overwrite, paper);
    }


    /**
     * Retrieves the amount of citations of a specific paper
     * Performs a general search of the papers name on SemanticScholar and chooses most relevant matching paper
     * <p>
     * Assuming that papers don't have duplicate names
     * <p>
     * Deprecated: use completePaperInformationByGeneralSearch(...) instead
     *
     * @param paper Paper which's amount of citation is to be retrieved
     * @return Amount of citations
     * @throws IOException   If something goes wrong communicating with the Server
     * @throws JSONException If the server doesn't respond with valid JSON
     */
    public static int getCitationAmountByPaper(Paper paper) throws IOException, JSONException {
        //1. General Search of full Paper name
        SemanticScholarAPIrequest generalSearch = new S2GeneralSearch();
        generalSearch.setQuery(paper.getTitle());
        generalSearch.run();

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

        //4. Get citationAmount
        return Integer.parseInt(chosenPaper.getJSONObject("citationStats").getString("numCitations"));
    }


}
