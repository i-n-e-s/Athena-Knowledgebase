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
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Workshop;

@SuppressWarnings("javadoc")
public class WorkshopJPAAccessTest {

	static JPATestdatabase testDB;
	static WorkshopJPAAccess uut;
	static Workshop testValue;
	static Event testEvent1;
	static Event testEvent2;

	static ConfigurableApplicationContext ctx;

	@BeforeClass
	public static void setUpDatabase() {
		ctx = SpringApplication.run(JPATestdatabase.class,"");
		testDB = new JPATestdatabase();
		uut = new WorkshopJPAAccess();
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
		testValue = new Workshop();
		testValue.setTitle("TestWorkshopTitle");
		testValue.setAbbreviation("TestAbbr");
		testEvent1 = new Event();

		testEvent1.setTitle("TestTitle1");
		testEvent2 = new Event();
		testEvent2.setTitle("TestTitle2");

		testValue.addEvent(testEvent1);
		testValue.addEvent(testEvent2);
	}

	@Test
	public void addAndDeleteTest() {
		uut.add(testValue);
		List<Workshop> returnValues = getByTitle(testValue.getTitle());
		if(returnValues.size() == 0) fail("return of existing Database is empty");
		if(returnValues.size() > 1) fail("more than one returnValue ");
		assertTrue(testValue.equalsWithoutID(returnValues.get(0)));
		uut.delete(testValue);
		assertTrue(getByTitle(testValue.getTitle()).size() == 0);
	}

	@Test
	public void getTest() {
		List<Workshop> resultList = uut.get();
		assertTrue(testDB.getWorkshopQuantity() == resultList.size());
		List<String> resultTitles = new ArrayList<>();
		resultList.stream().forEach((Workshop s) -> resultTitles.add(s.getTitle()));;
		for (int i = 0; i < testDB.getWorkshopQuantity(); i++) {
			assertTrue(resultTitles.contains("Title"+ i));
		}
	}

	@Test
	public void updateTest() {
		uut.add(testValue);
		testValue.setAbbreviation("UpdatedAbbr");
		List<Workshop> returnValues = getByTitle(testValue.getTitle());
		if(returnValues.size() == 0) fail("return is empty");
		if(returnValues.size() > 1) fail("more than one return value");
		assertEquals("UpdatedAbbr", returnValues.get(0).getAbbreviation());
		testDB.createDB();//Don't pollute the Database
	}

	private List<Workshop> getByTitle(String title) {
		return PersistenceManager.getEntityManager().createQuery(String.format("SELECT w FROM Workshop w WHERE w.title = '%s'",title), Workshop.class).getResultList();
	}
}
