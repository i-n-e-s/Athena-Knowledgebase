package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

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
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Paper;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Person;

public class PaperJPAAccessTest {

	static JPATestdatabase testDB;
	static PaperJPAAccess uut;
	static Paper testValue;
	static Person testAuthor1;
	static Person testAuthor2;

	static ConfigurableApplicationContext ctx;

	@BeforeClass
	public static void setUpDatabase() {
		ctx = SpringApplication.run(JPATestdatabase.class,"");
		testDB = new JPATestdatabase();
		uut = new PaperJPAAccess();
		testDB.createDB();
	}

	@AfterClass
	public static void shutdownDatabase() {
		ctx.close();
	}

	public void resetValues() {
		testValue = new Paper();
		testAuthor1 = new Person();
		testAuthor1.setFullName("testAuthor1");
		testAuthor2 = new Person();
		testAuthor2.setFullName("testAuthor2");
		testValue.setTitle("TestPaperTitleTest");
		testValue.addAuthor(testAuthor1);
		testValue.addAuthor(testAuthor2);
		testValue.setReleaseDate(LocalDate.of(1234, 12, 3));
		testValue.setTopic("TestTopicTest");
		testValue.setRemoteLink("TestLinkTest");
		testValue.setPdfFileSize(1234321);
		testValue.setAnthology("TestAnthology");

	}

	@Before
	public void resetDB() {
		resetValues();
		testDB.createDB(); //Performance hungry if done before every test
	}


	@Test
	public void getTest() {
		List<Paper> resultList = uut.get();
		assertTrue(testDB.getPaperQuantity() == resultList.size());
		List<String> resultTitles = new ArrayList<String>();
		resultList.stream().forEach((Paper p) -> resultTitles.add(p.getTitle()));;
		for (int i = 0; i < testDB.getPaperQuantity(); i++) {
			assertTrue(resultTitles.contains("Title"+ i));
		}
	}

	@Test
	public void addAndDeleteTest() {//Could be wrong, if getByPaperID is broken
		uut.add(testValue);
		List<Paper> returnValue = getByPaperID(testValue.getPaperID());
		if(returnValue.size() == 0) fail("return of existing Database is empty");
		if(returnValue.size() > 1) fail("more than one return value");
		assertTrue(testValue.equalsWithoutID(returnValue.get(0)));
		for (Person authorReturn : returnValue.get(0).getAuthors()) {
			boolean containsAuthor = false;
			for (Person authorTestVal : testValue.getAuthors()) {
				if(authorTestVal.getFullName().equals(authorReturn.getFullName())
						&& containsAuthor) fail("duplicate author entry in return");
				if(authorTestVal.getFullName().equals(authorReturn.getFullName()))
					containsAuthor = true;
			}
			assertTrue(containsAuthor);
		}
		uut.delete(testValue);
		assertTrue(getByPaperID(testValue.getPaperID()).size() == 0);
		testDB.createDB();//If delete is broken don't pollute DB
	}

	@Test
	public void updateTest() {
		uut.add(testValue);
		testValue.setTitle("UpdatedTitle");
		List<Paper> returnValues = getByPaperID(testValue.getPaperID());
		if(returnValues.size() == 0) fail("return is empty");
		if(returnValues.size() > 1) fail("more than one return value");
		assertTrue(testValue.equalsWithoutID(returnValues.get(0)));
		testDB.createDB();//Don't pollute the Database
	}

	public List<Paper> getByPaperID(long id) {
		return PersistenceManager.getEntityManager().createQuery(String.format("SELECT p FROM Paper AS p WHERE p.paperID = '%s'",Long.toString(id)), Paper.class).getResultList();
	}
}
