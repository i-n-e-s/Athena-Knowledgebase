package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

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

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.JPATestdatabase;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Institution;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Person;

public class InstitutionJPAAccessTest {

	static JPATestdatabase testDB;
	static InstitutionJPAAccess uut;
	static Institution testValue;
	static Person testPerson1;
	static Person testPerson2;

	static ConfigurableApplicationContext ctx;

	@BeforeClass
	public static void setUpDatabase() {
		ctx = SpringApplication.run(JPATestdatabase.class,"");
		testDB = new JPATestdatabase();
		uut = new InstitutionJPAAccess();
		testDB.createDB();
	}

	@AfterClass
	public static void shutdownDatabase() {
		ctx.close();
	}

	public static void resetValues() {
		testValue = new Institution();
		testValue.setName("TestInstitutionTest");
		testPerson1 = new Person();
		testPerson2 = new Person();
		testValue.addPerson(testPerson1);
		testValue.addPerson(testPerson2);
	}

	@Before
	public void resetDB() {
		resetValues();
		testDB.createDB(); //Performance hungry if done before every test
	}

	@Test
	public void addAndDeleteTest() {
		uut.add(testValue);
		List<Institution> returnValue = getByName(testValue.getName());
		if(returnValue.size() == 0) fail("return value is empty");
		if(returnValue.size() > 1) fail("return value is to large");
		assertTrue(testValue.equalsWithoutID(returnValue.get(0)));
		uut.delete(testValue);
		returnValue = getByName(testValue.getName());
		assertTrue(returnValue.size() == 0);
	}

	@Test
	public void updateTest() {
		String oldName = testValue.getName();
		uut.add(testValue);
		testValue.setName("TestNameUpdate");
		PersistenceManager.getEntityManager().getTransaction().begin();
		//This Transaction is just necessary to make the change persistent, normally the update would take place at the next persistent access to the Database
		List<Institution> returnValue = getByName(testValue.getName());
		PersistenceManager.getEntityManager().getTransaction().commit();
		if(returnValue.size() == 0) fail("returnValue empty");
		if(returnValue.size() > 1) fail("return list to big");
		assertTrue(testValue.equalsWithoutID(returnValue.get(0)));
		assertEquals("TestNameUpdate", returnValue.get(0).getName());
		assertTrue(testValue.getPersons().size() == returnValue.get(0).getPersons().size());
		returnValue = getByName(oldName);
		assertEquals(0,returnValue.size());
	}

	@Test
	public void getTest() {
		testDB.setDefaultParameters();
		testDB.createDB();
		List<Institution> returnValues = uut.get();
		assertEquals(returnValues.size(),testDB.getInstitutionQuantity());
	}

	public List<Institution> getByName(String name) {
		return PersistenceManager.getEntityManager().createQuery(String.format("SELECT i FROM Institution i WHERE i.name = '%s'",name), Institution.class).getResultList();
	}
}
