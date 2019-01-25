package de.tudarmstadt.informatik.ukp.athenakp.database.models;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;

import org.junit.Test;

public class ModelsTest {
	
	Model uut;
	
	Model testData;
	
	public void setTestDataAuthor() {
		testData = new Author();
	} 
	
	@Test
	public void equalsWithoutIDReflectivTest1() {
		Author uut = new Author();
		uut.setFullName("testName");
		uut.setPrefix("prefix");
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
}
