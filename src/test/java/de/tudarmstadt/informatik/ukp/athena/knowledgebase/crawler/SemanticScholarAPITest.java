package de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.junit.Assert;
import org.junit.Test;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.semanticscholarapi.S2APIFunctions;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.semanticscholarapi.S2AuthorSearch;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.semanticscholarapi.S2GeneralSearch;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.semanticscholarapi.S2PaperSearch;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler.semanticscholarapi.SemanticScholarAPIRequest;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Paper;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Person;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.exception.NotAvailableException;

/**
 * Tests for the SemanticScholar API connection classes
 *
 * @author Philipp Emmer
 */
public class SemanticScholarAPITest {

	private SemanticScholarAPIRequest s2con;


	/**
	 * Tests whether the amount of citations of a paper can be retrieved
	 *
	 * Hardcoded to expect 49 citations of the Paper:
	 * "Temporal Language Models for the Disclosure of Historical Text"
	 *
	 */
	@Test
	public void getCitationAmountByPaperTest() {
		Paper testPaper = new Paper();
		testPaper.setTitle("Temporal Language Models for the Disclosure of Historical Text");
		int amount;
		try {
			amount = S2APIFunctions.getCitationAmountByPaper( testPaper );
			Assert.assertTrue( amount >= 0 );   //Make sure amount is not -1
			assertEquals(49, amount);
		}
		catch( JSONException e ) {
			System.err.println( e.toString() );
			Assert.fail();
		}
		catch( IOException e ) {
			System.err.println( e.toString() );
			Assert.fail();
		}
	}

	/**
	 * Tests whether a List of all papers published by an author and listed on SemanticScholar can be returned
	 */
	@Test
	public void getAllPapersByAuthorTest() {
		Person uut = new Person();
		uut.setFullName("Iryna Gurevych");

		List<Paper> allPapers = null;

		try {
			allPapers = S2APIFunctions.getAllPapersByAuthor(uut);
			Assert.assertTrue( allPapers.size() > 0 );
			assertEquals( 0, allPapers.stream().filter(paper -> paper.getTitle() == null).collect(Collectors.toList()).size());
		} catch( IOException e ) {
			System.err.println("Connection Error");
			Assert.fail();
		} catch ( JSONException e ) {
			System.err.println("Response Parsing Error");
			Assert.fail();
		}

		//Test null value
		uut = new Person();
		uut.setFullName(null);
		try {
			S2APIFunctions.getAllPapersByAuthor(uut);
			Assert.fail();
		} catch ( IOException | JSONException e ) { assertEquals(JSONException.class, e.getClass()); }
	}

	/**
	 * Tests whether the SemanticScholar ID of an Author can be found
	 */
	@Test
	public void getAuthorS2IDTest() {
		Paper p = new Paper();
		System.out.println(p.getPaperID());

		Person testAuthor = new Person();
		System.out.println(testAuthor.getPersonID());
		String result;
		testAuthor.setFullName("Iryna Gurevych");
		try {
			result = S2APIFunctions.getAuthorsS2ID(testAuthor);
			assertEquals("1730400", result);
		}
		catch( Exception e ) {
			System.err.println(e.toString());
			Assert.fail();
		}
	}

	/**
	 * Tests whether a HTTPS connection can be established to SemanticScholars internal API
	 * and whether the servers response has a SUCCESSful status Code
	 */
	@Test
	public void generalSearchConnectionTest() {
		SemanticScholarAPIRequest uut = new S2GeneralSearch();
		boolean result = successfulHTTPResponseTest( uut, "Iryna" );
		Assert.assertTrue( result );
	}

	/**
	 * Tests whether the servers response to a general search can be parsed as JSON
	 */
	@Test
	public void generalSearchValidJSONTest() {
		SemanticScholarAPIRequest uut = new S2GeneralSearch();
		boolean result = validJSONResponseTest( uut, "Iryna" );
		Assert.assertTrue( result );
	}

	/**
	 * Tests whether a HTTPS connection can be established to SemanticScholars public API
	 * and whether the servers response has a SUCCESSful status Code
	 */
	@Test
	public void paperSearchConnectionTest() {
		SemanticScholarAPIRequest uut = new S2PaperSearch();
		boolean result = successfulHTTPResponseTest( uut, "32125496" );
		try { uut.getRawResponse(); }
		catch(NotAvailableException e) {
			System.err.println("Not available");
			Assert.fail();
		}
		Assert.assertTrue( result );
	}

	/**
	 * Tests whether the servers response to a paper search can be parsed as JSON
	 */
	@Test
	public void paperSearchValidJSONTest() {
		SemanticScholarAPIRequest uut = new S2PaperSearch();
		boolean result = validJSONResponseTest( uut, "32125496" );
		Assert.assertTrue( result );
	}

	@Test
	public void authorSearchValidConnectionTest() {
		SemanticScholarAPIRequest uut = new S2AuthorSearch();
		boolean result = successfulHTTPResponseTest( uut, "Iryna Gurevych" );
		try { uut.getRawResponse(); }
		catch(NotAvailableException e) {
			System.err.println("Not available");
			Assert.fail();
		}
		Assert.assertTrue( result );
	}

	/**
	 * Tests whether the servers response to an author search can be parsed as JSON
	 */
	@Test
	public void authorSearchValidJSONTest() {
		SemanticScholarAPIRequest uut = new S2AuthorSearch();
		boolean result = validJSONResponseTest( uut, "Iryna Gurevych" );
		Assert.assertTrue( result );
	}

	@Test
	public void completeAuthorInformationByAuthorSearchTest() {

		Person uut = new Person();
		uut.setFullName("Iryna Gurevych");
		try {
			S2APIFunctions.completeAuthorInformationByAuthorSearch(uut, false);
			assertEquals( "1730400", uut.getSemanticScholarID() );
		} catch ( IOException e ) {
			System.err.println(e.toString());
			System.err.println("Some HTTP stuff went wrong");
			Assert.fail();
		} catch ( JSONException e ) {
			System.err.println(e.toString());
			e.printStackTrace();
			Assert.fail();
		}

		uut = new Person();
		uut.setSemanticScholarID("1730400");
		try {
			S2APIFunctions.completeAuthorInformationByAuthorSearch(uut, false);
			assertEquals( "Iryna Gurevych", uut.getFullName() );
		} catch ( IOException e ) {
			System.err.println(e.toString());
			System.err.println("Some HTTP stuff went wrong");
			Assert.fail();
		} catch ( JSONException e ) {
			System.err.println(e.toString());
			e.printStackTrace();
			Assert.fail();
		}

	}

	@Test
	public void completePaperInformationByGeneralSearchTest() {
		Paper uut = new Paper();
		uut.setTitle("A Monolingual Tree-based Translation Model for Sentence Simplification");
		uut.setSemanticScholarID("test");
		try {
			S2APIFunctions.completePaperInformationByGeneralSearch(uut, true);
			assertEquals( "5a4f7c894f4560c3205978d9277d744a910560f6", uut.getSemanticScholarID() );
		}  catch ( IOException e ) {
			System.err.println(e.toString());
			System.err.println("Some HTTP stuff went wrong");
			Assert.fail();
		} catch ( JSONException e ) {
			System.err.println(e.toString());
			e.printStackTrace();
			Assert.fail();
		}

		uut = new Paper();
		uut.setTitle("A Monolingual Tree-based Translation Model for Sentence Simplification");
		uut.setSemanticScholarID("test");
		try {
			S2APIFunctions.completePaperInformationByGeneralSearch(uut, false);
			assertEquals( "test", uut.getSemanticScholarID() );
		}  catch ( IOException e ) {
			System.err.println(e.toString());
			System.err.println("Some HTTP stuff went wrong");
			Assert.fail();
		} catch ( JSONException e ) {
			System.err.println(e.toString());
			e.printStackTrace();
			Assert.fail();
		}
	}

	/**
	 * Performs the actual testing of the JSON response. Helping-method to decrease redundancy
	 * @param request The API request to be tested
	 * @param query The search query used in the test
	 * @return true if test succeeded
	 */
	private boolean validJSONResponseTest( SemanticScholarAPIRequest request, String query ) {
		String rawText = "";
		s2con = request;
		try {
			s2con.setQuery(query);
			s2con.run();
			s2con.getParsedJSONResponse();
			rawText = s2con.getRawResponse();
		} catch( IOException e) {
			System.err.println( e.toString() );
			return false;
		} catch( NotAvailableException e ) {
			System.err.println("Not ready yet");
			return false;
		} catch( JSONException e ) {
			System.err.println("Response is not JSONObject");
			System.out.println(rawText);
			return false;
		}
		return true;
	}

	/**
	 * Performs the actual testing of the HTTP response. Helping-method to decrease redundancy
	 * @param request The API request to be tested
	 * @param query The search query used in the test
	 * @return true if test succeeded
	 */
	private boolean successfulHTTPResponseTest( SemanticScholarAPIRequest request, String query ) {

		s2con = request;
		try {
			s2con.setQuery(query);
			s2con.run();
			return s2con.getHTTPResponseCode().substring(0,1).equals("2");
		} catch( IOException e) {
			System.err.println(e.toString());
			return false;
		}

	}
}
