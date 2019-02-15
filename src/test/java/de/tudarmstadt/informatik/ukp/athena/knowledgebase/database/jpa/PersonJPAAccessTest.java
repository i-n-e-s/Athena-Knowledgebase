package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.JPATestdatabase;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Institution;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Paper;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Person;

@SuppressWarnings("javadoc")
public class PersonJPAAccessTest {

	static JPATestdatabase testDB;
	static PersonJPAAccess uut;
	static Person testValue;
	static Paper testPaper1;
	static Paper testPaper2;
	static Institution testInstitution;
	
	static ConfigurableApplicationContext ctx;

	@BeforeClass
	public static void setUpDatabase() {
		ctx = SpringApplication.run(JPATestdatabase.class,"");
		testDB = new JPATestdatabase();
		uut = new PersonJPAAccess();
		testDB.createDB();
	}

	@Before
	public void resetDB() {
		resetValues();
		//testDB.createDB(); //Performance hungry if done before every test
	}
	
	@AfterClass
	public static void shutdownDatabase() {
		ctx.close();
	}

	public void resetValues() {
		testValue = new Person();
		testPaper1 = new Paper();

		testPaper1.setTitle("TestTitle1");
		testPaper2 = new Paper();
		testPaper2.setTitle("TestTitle2");

		testValue.setPrefix("TestPrefix");
		testValue.setFullName("TestValueName");

		testInstitution = new Institution();
		testInstitution.setName("TestInstitution");
		testValue.setInstitution(testInstitution);

		testValue.setBirth(LocalDate.of(123, 12,3));
	}
	
	@Test
	public void getByPersonID() {
		uut.add(testValue);
		List<Person> returnValues = uut.getByPersonID(testValue.getPersonID());
		if(returnValues.size() == 0) fail("return of existing Database is empty");
		if(returnValues.size() > 1) fail("more than one returnValue ");
		assertTrue(testValue.equalsWithoutID(returnValues.get(0)));
		testDB.createDB();
	}
	
	@Test 
	public void getByPrefix() {
		List<Person> returnValues = uut.getByPrefix("Prefix0");
		assertTrue(returnValues.stream().allMatch((Person p) -> p.getPrefix().equals("Prefix0")));
		assertEquals(50,returnValues.size());	
		int cnt = 0;
		for (Person person : returnValues) {
			assertEquals("Author "+ cnt*2, person.getFullName());
			cnt++;
		}
	}
	
	@Test
	public void getByFullNameTest() {
		List<Person> returnValues = uut.getByFullName("Author 8");
		if(returnValues.size() == 0) fail("return of existing Database is empty");
		if(returnValues.size() > 1) fail("more than one returnValue ");
		assertEquals("Prefix0", returnValues.get(0).getPrefix());
		assertEquals("Author 8", returnValues.get(0).getFullName());
		assertEquals(LocalDate.of(1938,9,9), returnValues.get(0).getBirth());
		assertEquals("Institution8", returnValues.get(0).getInstitution().getName());
	}
	
	@Test
	public void getByBirthdayTest() {
		List<Person> returnValues = uut.getByBirthdate(1991,2,6);
		if(returnValues.size() == 0) fail("return of existing Database is empty");
		if(returnValues.size() > 1) fail("more than one returnValue ");
		assertEquals("Prefix1", returnValues.get(0).getPrefix());
		assertEquals("Author 61", returnValues.get(0).getFullName());
		assertEquals(LocalDate.of(1991,2,6), returnValues.get(0).getBirth());
		assertEquals("Institution1", returnValues.get(0).getInstitution().getName());
	}
	
	@Test
	public void getByInstitute() {
		List<Person> returnValues = uut.getByInstitutionID(803);
		assertNotNull(returnValues);
		if(returnValues.size() < 10) fail("return of existing Database is to small or empty");
		if(returnValues.size() > 10) fail("more than expected returnValues");
		for (Person person : returnValues) {
			assertEquals("Institution2",person.getInstitution());
		}
	}
	
	@Test
	public void addAndDeleteTest() {
		uut.add(testValue);
		List<Person> returnValues = uut.getByPersonID(testValue.getPersonID());
		if(returnValues.size() == 0) fail("return of existing Database is empty");
		if(returnValues.size() > 1) fail("more than one returnValue ");
		assertTrue(testValue.equalsWithoutID(returnValues.get(0)));
		uut.delete(testValue);
		assertTrue(uut.getByPersonID(testValue.getPersonID()).size() == 0);
		testDB.createDB();
	}
	
	public void getTest() {
		List<Person> resultList = uut.get();
		assertTrue(testDB.getAuthorQuantity() == resultList.size());
		List<String> resultTitles = new ArrayList<String>();
		resultList.stream().forEach((Person p) -> resultTitles.add(p.getFullName()));;
		for (int i = 0; i < testDB.getAuthorQuantity(); i++) {
			resultTitles.contains("Person"+ i);
		}
	};
	
	public void updateTest() {
		uut.add(testValue);
		testValue.setFullName("UpdatedName");
		uut.update(testValue);
		List<Person> returnValues = uut.getByPersonID(testValue.getPersonID());
		if(returnValues.size() == 0) fail("return is empty");
		if(returnValues.size() > 1) fail("more than one return value");
		assertEquals("UpdatedName", returnValues.get(0).getFullName());
		testDB.createDB();//Don't pollute the Database
	}
}
