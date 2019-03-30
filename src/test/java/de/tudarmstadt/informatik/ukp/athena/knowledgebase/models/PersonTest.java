package de.tudarmstadt.informatik.ukp.athena.knowledgebase.models;

import org.junit.*;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.JPATestdatabase;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Person;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@SuppressWarnings("javadoc")
public class PersonTest {

	static JPATestdatabase testDB;

	static ConfigurableApplicationContext ctx;

	@BeforeClass
	public static void setUpDatabase() {
		ctx = SpringApplication.run(JPATestdatabase.class, "");
		testDB = new JPATestdatabase();
		testDB.createDB();
	}

	@Before
	public void resetDB() {
		testDB.createDB(); //Performance hungry if done before every test
	}

	@AfterClass
	public static void shutdownDatabase() {
		ctx.close();
	}

	@Test
	public void personFindOrCreateTest() {
		Person query = new Person();
		query.setFullName("Author 5");
		assertEquals("0", String.valueOf(query.getPersonID()));
		Person uut = Person.findOrCreate(query);
		assertEquals("Prefix" + (5 % 2), String.valueOf(uut.getPrefix()));
		assertEquals("0", String.valueOf(query.getPersonID()));
		assertEquals("Prefix" + (5 % 2), String.valueOf(uut.getPrefix()));
	}

	@Test
	public void personFindOrCreateStrStrTest() {
		Person uut = Person.findOrCreate(null, "Author 5");
		assertEquals("37455290", String.valueOf(uut.getSemanticScholarID()));

		uut = Person.findOrCreate("12365871", null);
		assertEquals("Author 15", String.valueOf(uut.getFullName()));
		uut = Person.findOrCreate("37455290", "Author 5");
		assertEquals("Prefix1", String.valueOf(uut.getPrefix()));

		uut = Person.findOrCreate("44946348", "Author 5");
		assertEquals("Prefix0", String.valueOf(uut.getPrefix()));
		uut = Person.findOrCreate(null, null);
		assertNull(uut.getPrefix());
	}

	@Test
	public void personFindOrCreateDbOrListTest() {
		ArrayList<Person> testList = new ArrayList<>();
		Person testPerson = new Person();
		testPerson.setFullName("Lorem Ips");
		testPerson.setSemanticScholarID("23");
		testList.add(testPerson);

		Person uut = Person.findOrCreateDbOrList(null, "Author 5", testList);
		assertEquals("37455290", uut.getSemanticScholarID());

		uut = Person.findOrCreateDbOrList(null, "Lorem Ips", testList);
		assertEquals("23", uut.getSemanticScholarID());

		uut = Person.findOrCreateDbOrList(null, "Peter Pan", testList);
		assertNull(uut.getSemanticScholarID());
	}

	@Test
	public void addInfluencedTest() {
		Person uut = new Person();
		Person influenced = new Person();
		influenced.setFullName("Influ Enced");
		uut.addInfluenced(influenced);
		assertTrue(uut.getTop5influenced().contains(influenced));
	}

	@Test
	public void addInfluencedByTest() {
		Person uut = new Person();
		Person influencer = new Person();
		influencer.setFullName("Influ Encer");
		uut.addInfluencedBy(influencer);
		assertTrue(uut.getTop5influencedBy().contains(influencer));
	}

}


