package de.tudarmstadt.informatik.ukp.athenakp.database.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import de.tudarmstadt.informatik.ukp.athenakp.database.Testdatabase;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Author;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Paper;

@SuppressWarnings("javadoc")
public class PaperHibernateAccessIntegrationTest {
	
	static Testdatabase testDB;
	static PaperHibernateAccess uut;
	static Paper testValue;
	static Author testAuthor1;
	static Author testAuthor2;
	
	static ConfigurableApplicationContext ctx;
	
	@BeforeClass
	public static void setUpDatabase() {
		ctx = SpringApplication.run(Testdatabase.class,"");
		testDB = new Testdatabase();
		uut = new PaperHibernateAccess();
		testDB.createDB();
	}
	
	@AfterClass
	public static void shutdownDatabase() {
		ctx.close();
	}	

	public void resetValues() {
		testValue = new Paper();
		testAuthor1 = new Author();
		testAuthor1.setFullName("testAuthor1");
		testAuthor2 = new Author();
		testAuthor2.setFullName("testAuthor2");
		testValue.setTitle("TestPapertitleTest");
		testValue.addAuthor(testAuthor1);
		testValue.addAuthor(testAuthor2);
		testValue.setReleaseDate(LocalDate.of(1234, 12, 3));
		testValue.setTopic("TestTopicTest");
		testValue.setHref("TestLinktTest");
		testValue.setPdfFileSize(1234321);
		testValue.setAnthology("TestAnthology");
		
	}
	
	@Before
	public void resetDB() {
		resetValues();
		//testDB.createDB(); //Performance hungry if done before every test
	}
	//TODO Add getByPaperIDTest
	@Test
	public void getByAuthorTest() {
		List<Paper> returnValue;
		returnValue = uut.getByAuthor("Author 0");
		if(returnValue.size() == 0) fail("return of existing Database is empty");
		if(returnValue.size() > 1) fail("more than one returnValue ");
		assertEquals("Title0", returnValue.get(0).getTitle());
	}
	
	@Test
	public void getByAuthorInvalidAuthorTest() {
		assertTrue(uut.getByAuthor("InvalidAuthor").size() == 0);
	}
	
	@Test 
	public void getByReleaseDate() {
		List<Paper> returnValue;
		returnValue = uut.getByReleaseDate(63,63%12, 63%28);
		if(returnValue.size() == 0) fail("return of existing Database is empty");
		if(returnValue.size() > 1) fail("more than one returnValue ");
		assertEquals("Title63", returnValue.get(0).getTitle());
	}
	
	@Test
	public void getByAuthorInvalidDateTest() {
		uut.getByReleaseDate(-1,-1,-1);
	}
	
	@Test 
	public void getByReleaseRangeTest() {
		List<Paper> returnValue = uut.getByReleaseRange(87,1,94,12);
		List<String> expectedResultTitle = Arrays.asList("Title87","Title88","Title89","Title90","Title91","Title92","Title93","Title94");
		for (Paper returnPaper :  returnValue){
			assertTrue(expectedResultTitle.contains(returnPaper.getTitle()));
		}
		assertTrue(expectedResultTitle.size() == returnValue.size());
	}
	
	@Test
	public void getByHrefTest() {
		List<Paper> returnValue = uut.getByHref("Link.test/16");
		if(returnValue.size() == 0) fail("return of existing Database is empty");
		if(returnValue.size() > 1) fail("more than one returnValue ");
		assertEquals("Title16", returnValue.get(0).getTitle());
	}
	
	@Test
	public void getByPdfFileSize() {
		List<Paper> returnValue = uut.getByPdfFileSize(148);
		if(returnValue.size() == 0) fail("return of existing Database is empty");
		if(returnValue.size() > 1) fail("more than one returnValue ");
		assertEquals("Title48", returnValue.get(0).getTitle());
	}
	
	@Test
	public void getByAntholoyTest() {
		List<Paper> returnValue = uut.getByAnthology("Ant5");
		if(returnValue.size() < 2) fail("return of existing Database is to small empty");
		if(returnValue.size() > 2) fail("more than two return values ");
		List<String> expectedResultTitles = Arrays.asList("Title5","Title30");
		for (Paper returnedPaper : returnValue) {
			assertTrue(expectedResultTitles.contains(returnedPaper.getTitle()));
		}
		assertTrue(expectedResultTitles.size() == returnValue.size());
	}
	
	@Test
	public void getTest() {
		List<Paper> resultList = uut.get();
		assertTrue(testDB.getPaperQuantity() == resultList.size());
		List<String> resultTitles = new ArrayList();
		resultList.stream().forEach((Paper p) -> resultTitles.add(p.getTitle()));;
		for (int i = 0; i < testDB.getPaperQuantity(); i++) {
			resultTitles.contains("Title"+ i);
		}
	}
	
	@Test
	public void addAndDeleteTest() {//Could be wrong, if getByPaperID is broken
		uut.add(testValue);
		List<Paper> returnValue = uut.getByPaperID(testValue.getPaperID());
		if(returnValue.size() == 0) fail("return of existing Database is empty");
		if(returnValue.size() > 1) fail("more than one return value");
		assertTrue(testValue.equalsWithoutID(returnValue.get(0)));
		for (Author authorReturn : returnValue.get(0).getAuthors()) {
			boolean containsAuthor = false;
			for (Author authorTestVal : testValue.getAuthors()) {
				if(authorTestVal.getFullName().equals(authorReturn.getFullName()) 
						&& containsAuthor) fail("duplicate author entry in return");
				if(authorTestVal.getFullName().equals(authorReturn.getFullName())) 
					containsAuthor = true; 
			}
			assertTrue(containsAuthor);
		}
		uut.delete(testValue);
		assertTrue(uut.getByPaperID(testValue.getPaperID()).size() == 0);
		testDB.createDB();//If delete is broken don't pollute DB
	}
	
	@Test
	public void updateTest() {
		uut.add(testValue);
		testValue.setTitle("UpdatedTitle");
		uut.update(testValue);
		List<Paper> returnValues = uut.getByPaperID(testValue.getPaperID());
		if(returnValues.size() == 0) fail("return is empty");
		if(returnValues.size() > 1) fail("more than one return value");
		assertTrue(testValue.equalsWithoutID(returnValues.get(0)));
		testDB.createDB();//Don't pollute the Database
	}
	
}