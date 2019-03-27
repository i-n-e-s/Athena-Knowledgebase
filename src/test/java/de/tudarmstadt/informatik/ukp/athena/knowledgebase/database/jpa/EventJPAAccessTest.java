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
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Event;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.EventCategory;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.EventPart;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Paper;

public class EventJPAAccessTest {

	static JPATestdatabase testDB;
	static EventJPAAccess uut;
	static Event testValue;
	static EventPart testEP1;
	static EventPart testEP2;
	static Paper testP1;
	static Paper testP2;

	static ConfigurableApplicationContext ctx;

	@BeforeClass
	public static void setUpDatabase() {
		ctx = SpringApplication.run(JPATestdatabase.class,"");
		testDB = new JPATestdatabase();
		uut = new EventJPAAccess();
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
		testValue = new Event();
		testValue.setTitle("TestEventTitle");
		testValue.setCategory(EventCategory.BREAK);
		testEP1 = new EventPart();

		testEP1.setTitle("TestTitle1");
		testEP2 = new EventPart();
		testEP2.setTitle("TestTitle2");

		testP1 = new Paper();
		testP1.setTitle("TestPaperTitle1");
		testP2 = new Paper();
		testP2.setTitle("TestPaperTitle2");

		testValue.addEventPart(testEP1);
		testValue.addEventPart(testEP2);
		testValue.addPaper(testP1);
		testValue.addPaper(testP2);
	}

	@Test
	public void addAndDeleteTest() {
		uut.add(testValue);
		List<Event> returnValues = getByTitle(testValue.getTitle());
		if(returnValues.size() == 0) fail("return of existing Database is empty");
		if(returnValues.size() > 1) fail("more than one returnValue ");
		assertTrue(testValue.equalsWithoutID(returnValues.get(0)));
		uut.delete(testValue);
		assertTrue(getByTitle(testValue.getTitle()).size() == 0);
	}

	@Test
	public void getTest() {
		List<Event> resultList = uut.get();
		assertTrue(testDB.getEventQuantity() == resultList.size());
		List<String> resultTitles = new ArrayList<String>();
		resultList.stream().forEach((Event s) -> resultTitles.add(s.getTitle()));;
		for (int i = 0; i < testDB.getEventQuantity(); i++) {
			assertTrue(resultTitles.contains("Title"+ i));
		}
	}

	@Test
	public void updateTest() {
		uut.add(testValue);
		testValue.setCategory(EventCategory.WORKSHOP);
		List<Event> returnValues = getByTitle(testValue.getTitle());
		if(returnValues.size() == 0) fail("return is empty");
		if(returnValues.size() > 1) fail("more than one return value");
		assertEquals(EventCategory.WORKSHOP, returnValues.get(0).getCategory());
		testDB.createDB();//Don't pollute the Database
	}

	private List<Event> getByTitle(String title) {
		return PersistenceManager.getEntityManager().createQuery(String.format("SELECT e FROM Event e WHERE e.title = '%s'",title), Event.class).getResultList();
	}
}
