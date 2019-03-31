package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

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

import static org.junit.Assert.*;

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
		testDB.createDB(); //Performance hungry if done before every test
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
	public void addAndDeleteTest() {
		uut.add(testValue);
		List<Person> returnValues = getByFullName(testValue.getFullName());
		if(returnValues.size() == 0) fail("return of existing Database is empty");
		if(returnValues.size() > 1) fail("more than one returnValue ");
		assertTrue(testValue.equalsWithoutID(returnValues.get(0)));
		uut.delete(testValue);
		assertTrue(getByFullName(testValue.getFullName()).size() == 0);
	}

	@Test
	public void getTest() {
		List<Person> resultList = uut.get();
		assertTrue(testDB.getAuthorQuantity() == resultList.size());
		List<String> resultTitles = new ArrayList<>();
		resultList.stream().forEach((Person p) -> resultTitles.add(p.getFullName()));;
		for (int i = 0; i < testDB.getAuthorQuantity(); i++) {
			assertTrue(resultTitles.contains("Author "+ i));
		}
	}

	@Test
	public void getByKnownAttributesTest() {
		Person testQuery = new Person();
		testQuery.setFullName("Author 3");
		System.out.println("ID of testQuery: "+testQuery.getPersonID());
		List<Person> resultList = uut.getByKnownAttributes(testQuery);
		if(resultList.size() == 0) fail("return is empty");
		assertEquals("22473174", resultList.get(0).getSemanticScholarID());
	}

	@Test
	public void getBySemanticScholarIDTest() {
		Person testPerson = uut.getBySemanticScholarID("22473174");
		assertEquals("Author 3", testPerson.getFullName());
		testPerson = uut.getBySemanticScholarID("bla");
		assertNull( testPerson );
	}

	@Test
	public void getByFullNameTest() {
		Person testPerson = uut.getByFullName("Author 3");
		assertEquals("22473174", testPerson.getSemanticScholarID());
		testPerson = uut.getByFullName("bla");
		assertNull( testPerson );
	}


	@Test
	public void updateTest() {
		uut.add(testValue);
		testValue.setPrefix("UpdatedPrefix");
		List<Person> returnValues = getByFullName(testValue.getFullName());
		if(returnValues.size() == 0) fail("return is empty");
		if(returnValues.size() > 1) fail("more than one return value");
		assertEquals("UpdatedPrefix", returnValues.get(0).getPrefix());
		testDB.createDB();//Don't pollute the Database
	}

	private List<Person> getByFullName(String name) {
		return PersistenceManager.getEntityManager().createQuery(String.format("SELECT p FROM Person p WHERE p.fullName = '%s'",name), Person.class).getResultList();
	}
}
