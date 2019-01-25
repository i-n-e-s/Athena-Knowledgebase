package de.tudarmstadt.informatik.ukp.athenakp.database.jpa;

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
import de.tudarmstadt.informatik.ukp.athenakp.database.models.EventCategory;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Session;

public class EventJPAAccessIntegrationTest {
	static Testdatabase testDB;
	static EventJPAAccess uut;
	static Event testValue;
	static Session testSession1;
	static Session testSession2;

	@BeforeClass
	public static void setUpDatabase() {
		SpringApplication.run(Testdatabase.class, "");
		testDB = new Testdatabase();
		uut = new EventJPAAccess();
		testDB.createDB();
	}

	public void resetValues() {
		java.util.Set<Session> sessions = new HashSet<Session>();
		testValue = new Event();
		testSession1 = new Session();
		testSession1.setTitle("TestTitleTest1");
		testSession1.setDescription("TestDescriptionTest1");
		testSession1.setPlace("TestPlaceTest1");
		testSession2 = new Session();
		testSession2.setTitle("TestTitleTest2");
		testSession2.setDescription("TestDescriptionTest2");
		testSession2.setPlace("TestPlaceTest2");
		testValue.setConferenceName("TestConferenceTest");
		testValue.setBegin(LocalDateTime.of(1234, 12, 3, 4, 5));
		testValue.setEnd(LocalDateTime.of(1234, 12, 3, 5, 6));
		testValue.setPlace("TestPlaceTest");
		testValue.setTitle("TestTitleTest");
		testValue.setDescription("TestDescriptionTest");
		testValue.setCategory(EventCategory.CEREMONY);
		sessions.add(testSession1);
		sessions.add(testSession2);
		testValue.setSessions(sessions);
	}

	@Before
	public void resetDB() {
		resetValues();
	}

	@Test
	public void getByConferenceTest() {
		List<Event> returnValue = uut.getByConferenceName("Conference0");
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
		List<Event> returnValue = uut.getByStartTime(2018, (9%12)+1, (9%28)+1, 9%24, 9%60);
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
		List<Event> returnValue = uut.getByEndTime(2018, (11%12)+1, (11%28)+1, (11%24)+1, 11%60);
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
		List<Event> returnValue = uut.getByPlace("Place2");
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
		List<Event> returnValue = uut.getByTitle("Title5");
		if(returnValue.size() == 0) fail("return of existing Database is empty");
		if(returnValue.size() > 1) fail("more than one returnValue ");
		assertEquals("Title5", returnValue.get(0).getTitle());
	}

	@Test
	public void getByTitleInvalidTitleTest() {
		assertTrue(uut.getByTitle("InvalidPlace").size() == 0);
	}

	@Test
	public void getByDescriptionTest() {
		List<Event> returnValue = uut.getByDescription("Description10");
		if(returnValue.size() == 0) fail("return of existing Database is empty");
		if(returnValue.size() > 1) fail("more than one returnValue ");
		assertEquals("Description10", returnValue.get(0).getDescription());
	}

	@Test
	public void getByDescriptionInvalidDescriptionTest() {
		assertTrue(uut.getByDescription("InvalidDescription").size() == 0);
	}

	@Test
	public void getByCategoryTest() {
		List<Event> returnValue = uut.getByCategory(EventCategory.WELCOME);
		if(returnValue.size() == 0) fail("return of existing Database is empty");
		if(returnValue.size() > 1) fail("more than one returnValue ");
		assertEquals(EventCategory.WELCOME, returnValue.get(0).getCategory());
	}

	@Test
	public void addAndDeleteTest() {
		uut.add(testValue);
		List<Event> returnValue = uut.getByEventId(testValue.getId());
		if(returnValue.size() == 0) fail("return of existing Database is empty");
		if(returnValue.size() > 1) fail("more than one return value");
		assertEquals(testValue.getConferenceName(), returnValue.get(0).getConferenceName());
		assertEquals(testValue.getBegin(), returnValue.get(0).getBegin());
		assertEquals(testValue.getEnd(), returnValue.get(0).getEnd());
		assertEquals(testValue.getPlace(), returnValue.get(0).getPlace());
		assertEquals(testValue.getTitle(), returnValue.get(0).getTitle());
		assertEquals(testValue.getDescription(), returnValue.get(0).getDescription());
		assertEquals(testValue.getCategory(), returnValue.get(0).getCategory());
		int correctSessions = 2; //will be 0 if both test sessions have been found
		for(Session sessionReturn : returnValue.get(0).getSessions()) {
			for(Session sessionTestVal : testValue.getSessions()) {
				if(sessionReturn.getTitle().equals(sessionTestVal.getTitle()) && sessionReturn.getDescription().equals(sessionTestVal.getDescription()) && sessionReturn.getPlace().equals(sessionTestVal.getPlace()))
					correctSessions--;
			}
		}
		assertEquals(0, correctSessions);
		uut.delete(testValue);
		assertTrue(uut.getByEventId(testValue.getId()).size() == 0);
		testDB.createDB();//If delete is broken don't pollute DB
	}

	@Test
	public void updateTest() {
		uut.add(testValue);
		testValue.setTitle("UpdatedTitle");
		uut.update(testValue);
		List<Event> returnValues = uut.getByEventId(testValue.getId());
		if(returnValues.size() == 0) fail("return is empty");
		if(returnValues.size() > 1) fail("more than one return value");
		assertEquals("UpdatedTitle", returnValues.get(0).getTitle());
		testDB.createDB();//Don't pollute the Database
	}
}
