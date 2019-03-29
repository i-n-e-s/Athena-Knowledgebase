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
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.EventPart;

public class EventPartJPAAccessTest {

	static JPATestdatabase testDB;
	static EventPartJPAAccess uut;
	static EventPart testValue;

	static ConfigurableApplicationContext ctx;

	@BeforeClass
	public static void setUpDatabase() {
		ctx = SpringApplication.run(JPATestdatabase.class,"");
		testDB = new JPATestdatabase();
		uut = new EventPartJPAAccess();
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
		testValue = new EventPart();
		testValue.setTitle("TestTitle");
		testValue.setPlace("TestPlace");
	}

	@Test
	public void addAndDeleteTest() {
		uut.add(testValue);
		List<EventPart> returnValues = getByTitle(testValue.getTitle());
		if(returnValues.size() == 0) fail("return of existing Database is empty");
		if(returnValues.size() > 1) fail("more than one returnValue ");
		assertTrue(testValue.equalsWithoutID(returnValues.get(0)));
		uut.delete(testValue);
		assertTrue(getByTitle(testValue.getTitle()).size() == 0);
	}

	@Test
	public void getTest() {
		List<EventPart> resultList = uut.get();
		assertTrue(testDB.getEventPartQuantity() == resultList.size());
		List<String> resultTitles = new ArrayList<String>();
		resultList.stream().forEach((EventPart s) -> resultTitles.add(s.getTitle()));;
		for (int i = 0; i < testDB.getEventPartQuantity(); i++) {
			assertTrue(resultTitles.contains("Title"+ i));
		}
	}

	@Test
	public void updateTest() {
		uut.add(testValue);
		testValue.setPlace("UpdatedPlace");
		List<EventPart> returnValues = getByTitle(testValue.getTitle());
		if(returnValues.size() == 0) fail("return is empty");
		if(returnValues.size() > 1) fail("more than one return value");
		assertEquals("UpdatedPlace", returnValues.get(0).getPlace());
		testDB.createDB();//Don't pollute the Database
	}

	private List<EventPart> getByTitle(String title) {
		return PersistenceManager.getEntityManager().createQuery(String.format("SELECT e FROM EventPart e WHERE e.title = '%s'",title), EventPart.class).getResultList();
	}
}
