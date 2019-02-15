package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.JPATestdatabase;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Conference;

@SuppressWarnings("javadoc")
public class ConferenceJPAAccessTest {

	static JPATestdatabase testDB;
	static ConferenceJPAAccess uut;
	static Conference testValue;
	
	static ConfigurableApplicationContext ctx;
	
	@BeforeClass
	public static void setUpDatabase() {
		ctx = SpringApplication.run(JPATestdatabase.class,"");
		testDB = new JPATestdatabase();
		uut = new ConferenceJPAAccess();
		testDB.createDB();
	}

	@AfterClass
	public static void shutdownDatabase() {
		ctx.close();
	}
	
	public static void resetValues() {
		testValue = new Conference();
		testValue.setName("TestConferenceTest");
		testValue.setAddress("TestAdressTest");
		testValue.setCity("TestCityTest");
		testValue.setCountry("TestCountryTest");
		testValue.setEnd(LocalDate.of(1234, 1, 2));
		testValue.setBegin(LocalDate.of(1234, 1, 1));
	}
	
	@Before
	public void resetDB() {
		resetValues();
		//testDB.createDB(); //Performance hungry if done before every test
	}
	
	@Test 
	public void getTest() {
		testDB.setDefaultParameters();
		testDB.createDB();
		List<Conference> returnValues = uut.get();
		if(returnValues.size() != testDB.getConferenceQuantity()) fail("TestDatabase is not the expected size");
	}
	
	@Test
	public void addanddeleteTest() {
		uut.add(testValue);
		List<Conference> returnValue = uut.getByName("TestConferenceTest");
		if(returnValue.size() == 0) fail("return value is empty");
		if(returnValue.size() > 1) fail("return value is to large");
		assertTrue(testValue.equalsWithoutID(returnValue.get(0)));
		uut.delete(testValue);
		returnValue = uut.getByName("TestConferenceTest");
		assertTrue(returnValue.size() == 0);
	}
	
	@Test
	public void updateTest() {
		List<Conference> returnValue;
		uut.add(testValue);
		/*testValue.setName("TestConferenceTestCorrected");
		uut.update(testValue);
		uut.getByName("TestConferenceTest");*///Name is ID and can't be changed
		testValue.setAddress("TestAdressTestCorrected");
		uut.update(testValue);
		returnValue = uut.getByName("TestConferenceTest");
		if(returnValue.size() == 0) fail("return value is empty");
		if(returnValue.size() > 1) fail("multiple retrun entries");
		assertEquals("TestAdressTestCorrected", returnValue.get(0).getAddress());
		uut.delete(testValue);
	}

	@Test
	public void getByNameTest1() {
		List<Conference> returnValue = uut.getByName("Conference0");
		assertNotNull(returnValue);
		if(returnValue.size() == 0) fail("return value is empty");
		if(returnValue.size() > 1) fail("More then one return value");
		assertEquals("Conference0",returnValue.get(0).getName());
		assertEquals("Testadress0", returnValue.get(0).getAddress());
		assertEquals("Testcity0", returnValue.get(0).getCity());
		assertEquals("Testcountry0", returnValue.get(0).getCountry());
		assertEquals(LocalDate.of(1960,01,02), returnValue.get(0).getEnd());
		assertEquals(LocalDate.of(1960,01,01), returnValue.get(0).getBegin());	
	}

	@Test
	public void getByNameTest2() {
		List<Conference> returnValue = uut.getByName("Conference4");
		assertTrue(returnValue.size() == 0);
	}

	@Test 
	public void getByStartDateTest1() {
		List<Conference> returnValue = uut.getByStartDate(1960, 01, 01);
		assertNotNull(returnValue);
		if(returnValue.size() == 0) fail("return value is empty");
		if(returnValue.size() > 1) fail("More then one return value");
		assertEquals("Conference0",returnValue.get(0).getName());
		assertEquals("Testadress0", returnValue.get(0).getAddress());
		assertEquals("Testcity0", returnValue.get(0).getCity());
		assertEquals("Testcountry0", returnValue.get(0).getCountry());
		assertEquals(LocalDate.of(1960,01,02), returnValue.get(0).getEnd());
		assertEquals(LocalDate.of(1960,01,01), returnValue.get(0).getBegin());	
	}

	@Test 
	public void getByStartDateTest2() {
		List<Conference> returnValue = uut.getByStartDate(1961, 02, 02);
		assertNotNull(returnValue);
		if(returnValue.size() == 0) fail("return value is empty");
		if(returnValue.size() > 1) fail("More then one return value");
		assertEquals("Conference1",returnValue.get(0).getName());
		assertEquals("Testadress1", returnValue.get(0).getAddress());
		assertEquals("Testcity1", returnValue.get(0).getCity());
		assertEquals("Testcountry1", returnValue.get(0).getCountry());
		assertEquals(LocalDate.of(1961,02,03), returnValue.get(0).getEnd());
		assertEquals(LocalDate.of(1961,02,02), returnValue.get(0).getBegin());	
	}

	@Test
	public void getByStartDateNonExistentDateTest() {
		List<Conference> returnValue = uut.getByStartDate(2334, 02, 02);
		assertTrue(returnValue.size() == 0);
		returnValue = uut.getByStartDate(1961, 12, 02);
		assertTrue(returnValue.size() == 0);
		returnValue = uut.getByStartDate(1961, 02, 24);
		assertTrue(returnValue.size() == 0);
	}

	@Test 
	public void getByStartDateInvalidDateTest1() {
		uut.getByStartDate(1961, 13, 02);
	}

	@Test
	public void getByStartDateInvalidDateTest2() {
		uut.getByStartDate(1961, 2, 32);
	}

	@Test 
	public void getByEndDateTest1() {
		List<Conference> returnValue = uut.getByEndDate(1960, 01, 02);
		assertNotNull(returnValue);
		if(returnValue.size() == 0) fail("return value is empty");
		if(returnValue.size() > 1) fail("More then one return value");
		assertEquals("Conference0",returnValue.get(0).getName());
		assertEquals("Testadress0", returnValue.get(0).getAddress());
		assertEquals("Testcity0", returnValue.get(0).getCity());
		assertEquals("Testcountry0", returnValue.get(0).getCountry());
		assertEquals(LocalDate.of(1960,01,02), returnValue.get(0).getEnd());
		assertEquals(LocalDate.of(1960,01,01), returnValue.get(0).getBegin());		
	}

	@Test 
	public void getByEndDateTest2() {
		List<Conference> returnValue = uut.getByEndDate(1961, 02, 03);
		assertNotNull(returnValue);
		if(returnValue.size() == 0) fail("return value is empty");
		if(returnValue.size() > 1) fail("More then one return value");
		assertEquals("Conference1",returnValue.get(0).getName());
		assertEquals("Testadress1", returnValue.get(0).getAddress());
		assertEquals("Testcity1", returnValue.get(0).getCity());
		assertEquals("Testcountry1", returnValue.get(0).getCountry());
		assertEquals(LocalDate.of(1961,02,03), returnValue.get(0).getEnd());
		assertEquals(LocalDate.of(1961,02,02), returnValue.get(0).getBegin());		
	}

	@Test
	public void getByEndDateNonExistentDateTest() {
		List<Conference> returnValue = uut.getByEndDate(2334, 02, 03);
		assertTrue(returnValue.size() == 0);
		returnValue = uut.getByEndDate(1961, 12, 03);
		assertTrue(returnValue.size() == 0);
		returnValue = uut.getByEndDate(1961, 02, 24);
		assertTrue(returnValue.size() == 0);
	}

	@Test
	public void getByEndDateInvalidDateTest1() {
		uut.getByEndDate(1961, 13, 02);
	}

	@Test
	public void getByEndDateInvalidDateTest2() {
		uut.getByEndDate(1961, 2, 32);
	}
/*
	@Test
	public void getByAdressTest() {
		List<Conference> returnValue = uut.getByAdress("Testadress1");
		assertNotNull(returnValue);
		if(returnValue.size() == 0) fail("return value is empty");
		if(returnValue.size() > 1) fail("More then one return value");
		assertEquals("Conference1",returnValue.get(0).getName());
		assertEquals("Testadress1", returnValue.get(0).getAddress());
		assertEquals("Testcity1", returnValue.get(0).getCity());
		assertEquals("Testcountry1", returnValue.get(0).getCountry());
		assertEquals(LocalDate.of(1961,02,03), returnValue.get(0).getEndDate());
		assertEquals(LocalDate.of(1961,02,02), returnValue.get(0).getStartDate());
	}

	@Test
	public void getByAdressInvalidAdressTest() {
		List<Conference> returnValue = uut.getByAdress("NonExistingCityCity");
		assertTrue(returnValue.size() == 0);
	}
*///TODO Uncomment if getByAdress is implemented
	@Test
	public void getByCityTest() {
		List<Conference> returnValue = uut.getByCity("Testcity1");
		assertNotNull(returnValue);
		if(returnValue.size() == 0) fail("return value is empty");
		if(returnValue.size() > 1) fail("More then one return value");
		assertEquals("Conference1",returnValue.get(0).getName());
		assertEquals("Testadress1", returnValue.get(0).getAddress());
		assertEquals("Testcity1", returnValue.get(0).getCity());
		assertEquals("Testcountry1", returnValue.get(0).getCountry());
		assertEquals(LocalDate.of(1961,02,03), returnValue.get(0).getEnd());
		assertEquals(LocalDate.of(1961,02,02), returnValue.get(0).getBegin());
	}

	@Test
	public void getByCityInvalidCityTest() {
		List<Conference> returnValue = uut.getByCity("NonExistingCityCity");
		assertTrue(returnValue.size() == 0);
	}

	@Test
	public void getByCountryTest() {
		List<Conference> returnValue = uut.getByCountry("Testcountry1");
		assertNotNull(returnValue);
		if(returnValue.size() == 0) fail("return value is empty");
		if(returnValue.size() > 1) fail("More then one return value");
		assertEquals("Conference1", returnValue.get(0).getName());
		assertEquals("Testadress1", returnValue.get(0).getAddress());
		assertEquals("Testcity1", returnValue.get(0).getCity());
		assertEquals("Testcountry1", returnValue.get(0).getCountry());
		assertEquals(LocalDate.of(1961,02,03), returnValue.get(0).getEnd());
		assertEquals(LocalDate.of(1961,02,02), returnValue.get(0).getBegin());
	}

	@Test
	public void getByCountryInvalidCountryTest() {
		List<Conference> returnValue = uut.getByCountry("NonExistingCountryCountry");
		assertTrue(returnValue.size() == 0);
	}
}
