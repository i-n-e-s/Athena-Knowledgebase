package de.tudarmstadt.informatik.ukp.athenakp.database.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;

import de.tudarmstadt.informatik.ukp.athenakp.database.Testdatabase;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Conference;

public class ConferenceHibernateAccessIntegrationTest {
	
	static Testdatabase testdb;
	static ConferenceHibernateAccess uut;
	@BeforeClass
	public static void setUpDatabase() {
		SpringApplication.run(Testdatabase.class,"");
		testdb = new Testdatabase();
		uut = new ConferenceHibernateAccess();
	}
	
	@Before
	public void resetDB() {
		testdb.createDB();
	}

	@Test
	public void getByNameTest1() {
		List<Conference> returnValue = uut.getByName("Conference0");
		assertNotNull(returnValue);
		if(returnValue.size() > 1) Assert.fail("More then one return value");
		assertEquals("Conference0",returnValue.get(0).getName());
		assertEquals("Testadress0", returnValue.get(0).getAddress());
		assertEquals("Testcity0", returnValue.get(0).getCity());
		assertEquals("Testcountry0", returnValue.get(0).getCountry());
		assertEquals(LocalDate.of(1960,01,02), returnValue.get(0).getEndDate());
		assertEquals(LocalDate.of(1960,01,01), returnValue.get(0).getStartDate());	
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
		if(returnValue.size() > 1) Assert.fail("More then one return value");
		assertEquals("Conference0",returnValue.get(0).getName());
		assertEquals("Testadress0", returnValue.get(0).getAddress());
		assertEquals("Testcity0", returnValue.get(0).getCity());
		assertEquals("Testcountry0", returnValue.get(0).getCountry());
		assertEquals(LocalDate.of(1960,01,02), returnValue.get(0).getEndDate());
		assertEquals(LocalDate.of(1960,01,01), returnValue.get(0).getStartDate());	
	}
	
	@Test 
	public void getByStartDateTest2() {
		List<Conference> returnValue = uut.getByStartDate(1961, 02, 02);
		assertNotNull(returnValue);
		if(returnValue.size() > 1) Assert.fail("More then one return value");
		assertEquals("Conference1",returnValue.get(0).getName());
		assertEquals("Testadress1", returnValue.get(0).getAddress());
		assertEquals("Testcity1", returnValue.get(0).getCity());
		assertEquals("Testcountry1", returnValue.get(0).getCountry());
		assertEquals(LocalDate.of(1961,02,03), returnValue.get(0).getEndDate());
		assertEquals(LocalDate.of(1961,02,02), returnValue.get(0).getStartDate());	
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
	
	@Test (expected = DateTimeException.class)
	public void getByStartDateInvalidDateTest1() {
		uut.getByStartDate(1961, 13, 02);
	}
	
	@Test (expected = DateTimeException.class)
	public void getByStartDateInvalidDateTest2() {
		uut.getByStartDate(1961, 2, 32);
	}
	
	@Test 
	public void getByEndDateTest1() {
		List<Conference> returnValue = uut.getByEndDate(1960, 01, 02);
		assertNotNull(returnValue);
		if(returnValue.size() > 1) Assert.fail("More then one return value");
		assertEquals("Conference0",returnValue.get(0).getName());
		assertEquals("Testadress0", returnValue.get(0).getAddress());
		assertEquals("Testcity0", returnValue.get(0).getCity());
		assertEquals("Testcountry0", returnValue.get(0).getCountry());
		assertEquals(LocalDate.of(1960,01,02), returnValue.get(0).getEndDate());
		assertEquals(LocalDate.of(1960,01,01), returnValue.get(0).getStartDate());		
	}
	
	@Test 
	public void getByEndDateTest2() {
		List<Conference> returnValue = uut.getByEndDate(1961, 02, 03);
		assertNotNull(returnValue);
		if(returnValue.size() > 1) Assert.fail("More then one return value");
		assertEquals("Conference1",returnValue.get(0).getName());
		assertEquals("Testadress1", returnValue.get(0).getAddress());
		assertEquals("Testcity1", returnValue.get(0).getCity());
		assertEquals("Testcountry1", returnValue.get(0).getCountry());
		assertEquals(LocalDate.of(1961,02,03), returnValue.get(0).getEndDate());
		assertEquals(LocalDate.of(1961,02,02), returnValue.get(0).getStartDate());		
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
	
	@Test (expected = DateTimeException.class)
	public void getByEndDateInvalidDateTest1() {
		uut.getByEndDate(1961, 13, 02);
	}
	
	@Test (expected = DateTimeException.class)
	public void getByEndDateInvalidDateTest2() {
		uut.getByEndDate(1961, 2, 32);
	}
}
