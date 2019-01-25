package de.tudarmstadt.informatik.ukp.athenakp.database.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;

import de.tudarmstadt.informatik.ukp.athenakp.database.Testdatabase;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Event;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Workshop;

@Deprecated
public class WorkshopHibernateAccessIntegrationTest {
	static Testdatabase testDB;
	static WorkshopHibernateAccess uut;
	static Workshop testValue;
	static Event testEvent1;
	static Event testEvent2;

	@BeforeClass
	public static void setUpDatabase() {
		SpringApplication.run(Testdatabase.class, "");
		testDB = new Testdatabase();
		uut = new WorkshopHibernateAccess();
		testDB.createDB();
	}

	public void resetValues() {
		java.util.Set<Event> events = new HashSet<Event>();
		testValue = new Workshop();
		testEvent1 = new Event();
		testEvent1.setTitle("TestTitleTest1");
		testEvent2 = new Event();
		testEvent2.setTitle("TestTitleTest2");
		testValue.setConferenceName("TestConferenceTest");
		testValue.setBegin(LocalDateTime.of(1234, 12, 3, 4, 5));
		testValue.setEnd(LocalDateTime.of(1234, 12, 3, 5, 6));
		testValue.setPlace("TestPlaceTest");
		testValue.setTitle("TestTitleTest");
		testValue.setAbbreviation("TestAbbreviationTest");
		events.add(testEvent1);
		events.add(testEvent2);
		testValue.setEvents(events);
	}

	@Before
	public void resetDB() {
		resetValues();
	}

	@Test
	public void getByConferenceTest() {
		List<Workshop> returnValue = uut.getByConferenceName("Conference0");
		if(returnValue.size() == 0) fail("return of existing Database is empty");
		if(returnValue.size() > 1) fail("more than one returnValue ");
		assertEquals("Conference0", returnValue.get(0).getConferenceName());
	}

	@Test
	public void getByConferenceInvalidConferenceTest() {
		assertTrue(uut.getByConferenceName("InvalidConference").size() == 0);
	}

	@Test
	public void getByStartTimeTest() {
		List<Workshop> returnValue = uut.getByStartTime(2018, (9%12)+1, (9%28)+1, 9%24, 9%60);
		if(returnValue.size() == 0) fail("return of existing Database is empty");
		if(returnValue.size() > 1) fail("more than one returnValue ");
		assertEquals(LocalDateTime.of(2018, (9%12)+1, (9%28)+1, 9%24, 9%60), returnValue.get(0).getBegin());
	}

	@Test
	public void getByStartTimeInvalidStartTimeTest() {
		assertTrue(uut.getByStartTime(1, 1, 1, 1, 1).size() == 0);
	}

	@Test
	public void getByEndTimeTest() {
		List<Workshop> returnValue = uut.getByEndTime(2018, (11%12)+1, (11%28)+1, (11%24)+1, 11%60);
		if(returnValue.size() == 0) fail("return of existing Database is empty");
		if(returnValue.size() > 1) fail("more than one returnValue ");
		assertEquals(LocalDateTime.of(2018, (11%12)+1, (11%28)+1, (11%24)+1, 11%60), returnValue.get(0).getEnd());
	}

	@Test
	public void getByEndTimeInvalidStartTimeTest() {
		assertTrue(uut.getByEndTime(1, 1, 1, 1, 1).size() == 0);
	}

	@Test
	public void getByPlaceTest() {
		List<Workshop> returnValue = uut.getByPlace("Place2");
		if(returnValue.size() == 0) fail("return of existing Database is empty");
		if(returnValue.size() > 1) fail("more than one returnValue ");
		assertEquals("Place2", returnValue.get(0).getPlace());
	}

	@Test
	public void getByPlaceInvalidPlaceTest() {
		assertTrue(uut.getByPlace("InvalidPlace").size() == 0);
	}

	@Test
	public void getByTitleTest() {
		List<Workshop> returnValue = uut.getByTitle("Title5");
		if(returnValue.size() == 0) fail("return of existing Database is empty");
		if(returnValue.size() > 1) fail("more than one returnValue ");
		assertEquals("Title5", returnValue.get(0).getTitle());
	}

	@Test
	public void getByTitleInvalidTitleTest() {
		assertTrue(uut.getByTitle("InvalidPlace").size() == 0);
	}

	@Test
	public void getByAbbreviationTest() {
		List<Workshop> returnValue = uut.getByAbbreviation("Abbreviation10");
		if(returnValue.size() == 0) fail("return of existing Database is empty");
		if(returnValue.size() > 1) fail("more than one returnValue ");
		assertEquals("Abbreviation10", returnValue.get(0).getAbbreviation());
	}

	@Test
	public void getByAbbreviationInvalidAbbreviationTest() {
		assertTrue(uut.getByAbbreviation("InvalidAbbreviation").size() == 0);
	}

	@Test
	public void addAndDeleteTest() {
		uut.add(testValue);
		List<Workshop> returnValue = uut.getById(testValue.getId());
		if(returnValue.size() == 0) fail("return of existing Database is empty");
		if(returnValue.size() > 1) fail("more than one return value");
		assertEquals(testValue.getConferenceName(), returnValue.get(0).getConferenceName());
		assertEquals(testValue.getBegin(), returnValue.get(0).getBegin());
		assertEquals(testValue.getEnd(), returnValue.get(0).getEnd());
		assertEquals(testValue.getPlace(), returnValue.get(0).getPlace());
		assertEquals(testValue.getTitle(), returnValue.get(0).getTitle());
		assertEquals(testValue.getAbbreviation(), returnValue.get(0).getAbbreviation());
		int correctEvents = 2; //will be 0 if both test events have been found
		for(Event eventReturn : returnValue.get(0).getEvents()) {
			for(Event eventTestVal : testValue.getEvents()) {
				if(eventReturn.getTitle().equals(eventTestVal.getTitle()))
					correctEvents--;
			}
		}
		assertEquals(0, correctEvents);
		uut.delete(testValue);
		assertTrue(uut.getById(testValue.getId()).size() == 0);
		testDB.createDB();//If delete is broken don't pollute DB
	}

	@Test
	public void updateTest() {
		uut.add(testValue);
		testValue.setTitle("UpdatedTitle");
		uut.update(testValue);
		List<Workshop> returnValues = uut.getById(testValue.getId());
		if(returnValues.size() == 0) fail("return is empty");
		if(returnValues.size() > 1) fail("more than one return value");
		assertEquals("UpdatedTitle", returnValues.get(0).getTitle());
		testDB.createDB();//Don't pollute the Database
	}
}
