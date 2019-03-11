package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import static org.junit.Assert.assertEquals;
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
		testValue.setAddress("TestAddressTest");
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
		List<Conference> returnValue = getByName("TestConferenceTest");
		if(returnValue.size() == 0) fail("return value is empty");
		if(returnValue.size() > 1) fail("return value is too large");
		assertTrue(testValue.equalsWithoutID(returnValue.get(0)));
		uut.delete(testValue);
		returnValue = getByName("TestConferenceTest");
		assertTrue(returnValue.size() == 0);
	}
	
	@Test
	public void updateTest() {
		List<Conference> returnValue;
		uut.add(testValue);
		String altAdress = "TestAddressTestCorrected";
		testValue.setAddress(altAdress);
		returnValue = getByName(testValue.getName());
		if(returnValue.size() == 0) fail("return value is empty");
		if(returnValue.size() > 1) fail("multiple return entries");
		assertEquals("TestAddressTestCorrected", returnValue.get(0).getAddress());
		uut.delete(testValue);
	}
	
	public List<Conference> getByName(String name) {
		return PersistenceManager.getEntityManager().createQuery(String.format("SELECT c FROM Conference AS c WHERE c.name = '%s'",name), Conference.class).getResultList();
	}
}
