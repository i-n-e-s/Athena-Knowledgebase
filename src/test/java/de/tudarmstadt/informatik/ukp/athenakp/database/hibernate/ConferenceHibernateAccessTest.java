package de.tudarmstadt.informatik.ukp.athenakp.database.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;

import de.tudarmstadt.informatik.ukp.athenakp.database.Testdatabase;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Conference;

public class ConferenceHibernateAccessTest {
	
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
}
