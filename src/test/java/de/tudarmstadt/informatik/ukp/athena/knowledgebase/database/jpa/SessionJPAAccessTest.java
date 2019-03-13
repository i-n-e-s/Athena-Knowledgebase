package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.JPATestdatabase;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Session;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.SessionCategory;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.SessionPart;

@SuppressWarnings("javadoc")
public class SessionJPAAccessTest {

	static JPATestdatabase testDB;
	static SessionJPAAccess uut;
	static Session testValue;
	static SessionPart testSP1;
	static SessionPart testSP2;

	static ConfigurableApplicationContext ctx;

	@BeforeClass
	public static void setUpDatabase() {
		ctx = SpringApplication.run(JPATestdatabase.class,"");
		testDB = new JPATestdatabase();
		uut = new SessionJPAAccess();
		testDB.createDB();
	}

	@Before
	public void resetDB() {
		resetValues();
		testDB.createDB(); //Performance hungry if done before every test
	}

	@AfterClass
	public static void shutdownDatabase() {
		ctx.close();
	}

	public void resetValues() {
		testValue = new Session();
		testValue.setTitle("TestSessionTitle");
		testValue.setCategory(SessionCategory.BREAK);
		testSP1 = new SessionPart();

		testSP1.setTitle("TestTitle1");
		testSP2 = new SessionPart();
		testSP2.setTitle("TestTitle2");

		testValue.addSessionPart(testSP1);
		testValue.addSessionPart(testSP2);
		testValue.addPaperTitle("PaperTitle1");
		testValue.addPaperTitle("PaperTitle2");
	}

	@Test
	public void addAndDeleteTest() {
		uut.add(testValue);
		List<Session> returnValues = getByTitle(testValue.getTitle());
		if(returnValues.size() == 0) fail("return of existing Database is empty");
		if(returnValues.size() > 1) fail("more than one returnValue ");
		assertTrue(testValue.equalsWithoutID(returnValues.get(0)));
		uut.delete(testValue);
		assertTrue(getByTitle(testValue.getTitle()).size() == 0);
	}

	@Test
	public void getTest() {
		List<Session> resultList = uut.get();
		assertTrue(testDB.getSessionQuantity() == resultList.size());
		List<String> resultTitles = new ArrayList<String>();
		resultList.stream().forEach((Session s) -> resultTitles.add(s.getTitle()));;
		for (int i = 0; i < testDB.getSessionQuantity(); i++) {
			assertTrue(resultTitles.contains("Title"+ i));
		}
	}

	@Test
	public void updateTest() {
		uut.add(testValue);
		testValue.setCategory(SessionCategory.WORKSHOP);
		List<Session> returnValues = getByTitle(testValue.getTitle());
		if(returnValues.size() == 0) fail("return is empty");
		if(returnValues.size() > 1) fail("more than one return value");
		assertEquals(SessionCategory.WORKSHOP, returnValues.get(0).getCategory());
		testDB.createDB();//Don't pollute the Database
	}

	private List<Session> getByTitle(String title) {
		return PersistenceManager.getEntityManager().createQuery(String.format("SELECT s FROM Session s WHERE s.title = '%s'",title), Session.class).getResultList();
	}
}
