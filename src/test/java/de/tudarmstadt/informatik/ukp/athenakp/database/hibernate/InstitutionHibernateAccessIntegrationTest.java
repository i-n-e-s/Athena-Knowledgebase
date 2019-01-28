package de.tudarmstadt.informatik.ukp.athenakp.database.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import de.tudarmstadt.informatik.ukp.athenakp.database.HibernateTestdatabase;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Institution;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Person;

public class InstitutionHibernateAccessIntegrationTest {
	
	static HibernateTestdatabase testDB;
	static InstitutionHibernateAccess uut;
	static Institution testValue;
	
	static ConfigurableApplicationContext ctx;
	
	@BeforeClass
	public static void setUpDatabase() {
		ctx = SpringApplication.run(HibernateTestdatabase.class,"");
		testDB = new HibernateTestdatabase();
		uut = new InstitutionHibernateAccess();
		testDB.createDB();
	}
	
	@AfterClass
	public static void shutdownDatabase() {
		ctx.close();
	}
	
	public static void resetValues() {
		testValue = new Institution();
		testValue.setName("TestInstitutionTest");
	}
	
	@Before
	public void resetDB() {
		resetValues();
		//testDB.createDB(); //Performance hungry if done before every test
	}
	
	@Test
	public static void addAndDeleteTest() {
		uut.add(testValue);
		List<Institution> returnValue = uut.getByName(testValue.getName());
		if(returnValue.size() == 0) fail("return value is empty");
		if(returnValue.size() > 1) fail("return value is to large");
		assertTrue(testValue.equalsWithoutID(returnValue.get(0)));
		uut.delete(testValue);
		returnValue = uut.getByName(testValue.getName());
		assertTrue(returnValue.size() == 0);
	}
	
	@Test
	public static void updateTest() {
		testValue.setName("TestNameUpdate");
		List<Institution> returnValue = uut.getByInstitutionID(testValue.getInstitutionID());
		if(returnValue.size() == 0) fail("returnValue empty");
		if(returnValue.size() > 1) fail("return list to big");
		assertEquals(testValue.getInstitutionID(), returnValue.get(0).getInstitutionID());
		assertEquals("TestNameUpdate", returnValue.get(0).getName());
		for (Person p : testValue.getPersons()) {
			assertTrue(returnValue.get(0).getPersons().contains(p));
		}
		assertTrue(testValue.getPersons().size() == returnValue.get(0).getPersons().size());
	}
	
	@Test 
	public static void getByNameTest() {
		List<Institution> returnValue = uut.getByName("Institution6");
		if(returnValue.size() == 0) fail("return is empty");
		if(returnValue.size() > 1) fail("More than one return");
		//TODO add person assertion here, when added to Testbench
		uut.add(testValue);
		returnValue = uut.getByName(testValue.getName());
		if(returnValue.size() == 0) fail("return is empty");
		if(returnValue.size() > 1) fail("More than one return");
		for (Person p : testValue.getPersons()) {
			assertTrue(returnValue.get(0).getPersons().contains(p));
		}
		assertTrue(testValue.getPersons().size() == returnValue.get(0).getPersons().size());
		uut.delete(testValue);
	}
} 