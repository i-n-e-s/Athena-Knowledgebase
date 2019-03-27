package de.tudarmstadt.informatik.ukp.athena.knowledgebase.models;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.Test;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Conference;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Model;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Paper;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Person;

public class ModelsTest{
	/*Branchcoverage can't be reached yet, because we don't have fields which are accessible
	Also catch (IllegalArgumentException | IllegalAccessException e) should never be reached because it's checked before */
	Model uut;

	Model testData;


	@Test
	public void equalsWithoutIDReflectivTest1() {
		Person uut = new Person();
		uut.setFullName("testName");
		uut.setPrefix("prefix");
		assertTrue(uut.equalsWithoutID(uut));
	}

	@Test
	public void equalsWithoutIDSymmetricTest1() {
		Person uut = new Person();
		Person testAuthor1 = new Person();
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
		Person uut = new Person();
		Person testAuthor1 = new Person();
		uut.setFullName("test1");
		testAuthor1.setFullName("test1");
		testAuthor1.setBirth(LocalDate.of(12, 12, 12));
		assertFalse(uut.equalsWithoutID(testAuthor1));
		testAuthor1.setBirth(null);
		uut.setBirth(LocalDate.of(11, 11, 11));
		assertFalse(uut.equalsWithoutID(testAuthor1));
	}

	@Test
	public void equalsWithoutIDWrongGivenClassTest() {
		Person uut = new Person();
		Paper paper1 = new Paper();
		assertFalse(uut.equalsWithoutID(paper1));
	}

	@Test
	public void equalsWithoutIDNull() {
		Person uut = new Person();
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
		testConference1.setBegin(LocalDate.of(12, 12, 12));
		assertFalse(uut.equalsWithoutID(testConference1));
		testConference1.setBegin(null);
		uut.setBegin(LocalDate.of(11, 11, 11));
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
		Person uut = new Person();
		uut.setFullName(testname);
		Person author1 = new Person();
		author1.setFullName(testname);
		Paper testpaper = new Paper();
		testpaper.setTitle("testtitle");
		testpaper.addAuthor(author1);
		author1.addPaper(testpaper);
		assertTrue(uut.equalsWithoutID(author1));
	}

	@Test
	public void equalsNullAsWildcardNullTest() {
		uut = new Person();
		assertFalse(uut.equalsNullAsWildcard(null));
	}

	@Test
	public void equalsNullAsWildcardWrongGivenClassTest() {
		Person uut = new Person();
		Paper paper1 = new Paper();
		assertFalse(uut.equalsWithoutID(paper1));
	}

	@Test
	public void equalsNullAsWildcardNullFieldTest() {
		Conference uut = new Conference();
		Conference testConference1 = new Conference();
		uut.setName("test1");
		testConference1.setName("test1");
		testConference1.setBegin(LocalDate.of(12, 12, 12));
		assertTrue(uut.equalsNullAsWildcard(testConference1));
		testConference1.setBegin(null);
		uut.setBegin(LocalDate.of(11, 11, 11));
	}

	@Test
	public void equalsNullAsWildcardDifferentDataTest() {
		uut = new Person();
		((Person)uut).setFullName("testName");
		Person author1 = new Person();
		author1.setFullName("differentName");
		assertFalse(uut.equalsNullAsWildcard(author1));
		author1.setFullName(((Person)uut).getFullName());
		assertTrue(uut.equalsNullAsWildcard(author1));
	}

	@Test
	public void equalsNullAsWildcardDifferentClass() {
		uut = new Person();
		Paper paper1 = new Paper();
		assertFalse(uut.equalsNullAsWildcard(paper1));
	}

	@Test
	public void connectAuthorPaperTest() {
		uut = new Person();
		Paper testPaper = new Paper();
		boolean connected = Model.connectAuthorPaper((Person) uut, testPaper);
		assertTrue(connected);
		assertTrue( testPaper.getAuthors().contains( uut) );
		assertTrue( ((Person) uut).getPapers().contains(testPaper) );
		assertEquals(1, ((Person) uut).getPapers().size() );
		assertEquals(1, testPaper.getAuthors().size() );
		connected = Model.connectAuthorPaper((Person) uut, testPaper);
		assertFalse(connected);
		assertEquals(1, ((Person) uut).getPapers().size() );
		assertEquals(1, testPaper.getAuthors().size() );
	}
}
