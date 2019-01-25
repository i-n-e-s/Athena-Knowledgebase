package de.tudarmstadt.informatik.ukp.athenakp.database.models;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.Test;

@SuppressWarnings("javadoc")
public class ModelsTest {//Branchcoverage can't be reached yet, because we don't hava fields which are accessible 

	Model uut;

	Model testData;


	@Test
	public void equalsWithoutIDReflectivTest1() {
		Author uut = new Author();
		uut.setFullName("testName");
		uut.setPrefix("prefix");
		assertTrue(uut.equalsWithoutID(uut));
	}

	@Test 
	public void equalsWithoutIDSymmetricTest1() {
		Author uut = new Author();
		Author testAuthor1 = new Author();
		uut.equalsWithoutID(testAuthor1);
		uut.setFullName("test1");
		testAuthor1.setFullName("test1");
		assertTrue(uut.equalsWithoutID(uut));
		assertTrue(uut.equalsWithoutID(testAuthor1));
		assertTrue(testAuthor1.equalsWithoutID(uut));
		testAuthor1.setFullName("test2");
		assertFalse(uut.equalsWithoutID(testAuthor1));
		assertFalse(testAuthor1.equalsWithoutID(uut));
	}

	@Test 
	public void equalsWithoutIDNullTest() {
		Author uut = new Author();
		Author testAuthor1 = new Author();
		uut.setFullName("test1");
		testAuthor1.setFullName("test1");
		testAuthor1.setBirthdate(LocalDate.of(12, 12, 12));
		assertFalse(uut.equalsWithoutID(testAuthor1));
		testAuthor1.setBirthdate(null);
		uut.setBirthdate(LocalDate.of(11, 11, 11));
		assertFalse(uut.equalsWithoutID(testAuthor1));
	}

	@Test
	public void equalsWithoutIDWrongGivenClassTest() {
		Author uut = new Author();
		Paper paper1 = new Paper();
		assertFalse(uut.equalsWithoutID(paper1));
	}

	@Test
	public void equalsWithoutIDNull() {
		Author uut = new Author();
		uut.equalsWithoutID(null);
	}

	@Test
	public void equalsWithoutIDReflectivTest2() {
		Conference uut = new Conference();
		uut.setName("testName");
		uut.setCity("testcity");
		assertTrue(uut.equalsWithoutID(uut));
	}

	@Test 
	public void equalsWithoutIDSymmetricTest2() {
		Conference uut = new Conference();
		Conference testConference1 = new Conference();
		uut.equalsWithoutID(testConference1);
		uut.setName("test1");
		testConference1.setName("test1");
		assertTrue(uut.equalsWithoutID(uut));
		assertTrue(uut.equalsWithoutID(testConference1));
		assertTrue(testConference1.equalsWithoutID(uut));
		testConference1.setName("test2");
		assertFalse(uut.equalsWithoutID(testConference1));
		assertFalse(testConference1.equalsWithoutID(uut));
	}

	@Test 
	public void equalsWithoutIDNullTest2() {
		Conference uut = new Conference();
		Conference testConference1 = new Conference();
		uut.setName("test1");
		testConference1.setName("test1");
		testConference1.setStartDate(LocalDate.of(12, 12, 12));
		assertFalse(uut.equalsWithoutID(testConference1));
		testConference1.setStartDate(null);
		uut.setStartDate(LocalDate.of(11, 11, 11));
		assertFalse(uut.equalsWithoutID(testConference1));
	}

	@Test
	public void equalsWithoutIDWrongGivenClassTest2() {
		Conference uut = new Conference();
		Paper paper1 = new Paper();
		assertFalse(uut.equalsWithoutID(paper1));
	}

	@Test
	public void equalsWithoutIDNull2() {
		Conference uut = new Conference();
		uut.equalsWithoutID(null);
	}
	
	@Test
	public void equalWithoutIDSetRelevanceTest() {
		String testname = "testname";
		Author uut = new Author();
		uut.setFullName(testname);
		Author author1 = new Author();
		author1.setFullName(testname);
		Paper testpaper = new Paper();
		testpaper.setTitle("testtitle");
		testpaper.addAuthor(author1);
		author1.addPaper(testpaper);
		assertTrue(uut.equalsWithoutID(author1));
	}

}
