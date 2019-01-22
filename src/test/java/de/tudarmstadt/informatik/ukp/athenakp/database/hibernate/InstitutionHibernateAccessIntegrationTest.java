package de.tudarmstadt.informatik.ukp.athenakp.database.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;

import de.tudarmstadt.informatik.ukp.athenakp.database.Testdatabase;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Institution;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Person;

public class InstitutionHibernateAccessIntegrationTest {
	
	static Testdatabase testDB;
	static InstitutionHibernateAccess uut;
	static Institution testValue;
	
	@BeforeClass
	public static void setUpDatabase() {
		SpringApplication.run(Testdatabase.class,"");
		testDB = new Testdatabase();
		uut = new InstitutionHibernateAccess();
		testDB.createDB();
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