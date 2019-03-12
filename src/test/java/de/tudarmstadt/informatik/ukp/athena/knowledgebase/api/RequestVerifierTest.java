package de.tudarmstadt.informatik.ukp.athena.knowledgebase.api;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.exception.SyntaxException;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.exception.VerificationFailedException;

public class RequestVerifierTest
{
	//GENERAL NOTE:
	//by definition of RequestVerifier#verify, no exception means that the verification was successful

	@Test
	public void testVerfiyCorrect() {
		try {
			RequestVerifier.verify(new RequestParser(new RequestScanner("/paper:releaseDate=2018+02+29&topic=vogonpoetry").scan()).parse());
		}
		catch(SyntaxException e) {
			fail("Syntactically correct request shouldn't throw a syntax exception");
		}
		catch(VerificationFailedException e) {
			fail("Verification shouldn't fail with correct request");
		}
	}

	@Test
	public void testVerfiyCorrectHierarchy() {
		try {
			//by definition of RequestVerifier#verify, no exception means that the verification was successful
			RequestVerifier.verify(new RequestParser(new RequestScanner("/paper:releaseDate=2018+02+29&topic=vogonpoetry/person:fullName=Daniel+Klingbein").scan()).parse());
		}
		catch(SyntaxException e) {
			fail("Syntactically correct request shouldn't throw a syntax exception");
		}
		catch(VerificationFailedException e) {
			fail("Verification shouldn't fail with correct request");
		}
	}

	@Test(expected = VerificationFailedException.class)
	public void testIncorrectEntity() throws VerificationFailedException {
		try {
			RequestVerifier.verify(new RequestParser(new RequestScanner("/alien:planet=Kepler+22b").scan()).parse());
		}
		catch(SyntaxException e) {
			fail("Syntactically correct request shouldn't throw a syntax exception");
		}
	}

	@Test(expected = VerificationFailedException.class)
	public void testIncorrectHierarchicalRelationship() throws VerificationFailedException {
		try {
			RequestVerifier.verify(new RequestParser(new RequestScanner("/person:fullName=John+Smith/session").scan()).parse());
		}
		catch(SyntaxException e) {
			fail("Syntactically correct request shouldn't throw a syntax exception");
		}
	}

	@Test(expected = VerificationFailedException.class)
	public void testUnknownEntityAttribute() throws VerificationFailedException {
		try {
			RequestVerifier.verify(new RequestParser(new RequestScanner("/person:planet=Kepler+22b").scan()).parse());
		}
		catch(SyntaxException e) {
			fail("Syntactically correct request shouldn't throw a syntax exception");
		}
	}

	@Test(expected = VerificationFailedException.class)
	public void testStringAttributeWithNumberValueHasNumericalFields() throws VerificationFailedException {
		try {
			RequestVerifier.verify(new RequestParser(new RequestScanner("/person:fullName=42+69+1337").scan()).parse());
		}
		catch(SyntaxException e) {
			fail("Syntactically correct request shouldn't throw a syntax exception");
		}
	}

	@Test(expected = VerificationFailedException.class)
	public void testNumberAttributeWithIncorrectSize() throws VerificationFailedException {
		try {
			RequestVerifier.verify(new RequestParser(new RequestScanner("/paper:releaseDate=1+2+3+4+5").scan()).parse());
		}
		catch(SyntaxException e) {
			fail("Syntactically correct request shouldn't throw a syntax exception");
		}
	}

	@Test(expected = VerificationFailedException.class)
	public void testNumberAttributeWithStringValue() throws VerificationFailedException {
		try {
			RequestVerifier.verify(new RequestParser(new RequestScanner("/paper:releaseDate=twenty+ninth+february+two+thousand+eighteen").scan()).parse());
		}
		catch(SyntaxException e) {
			fail("Syntactically correct request shouldn't throw a syntax exception");
		}
	}

	@Test
	public void testEntityContainsNumericalField() {
		assertTrue(RequestVerifier.entityContainsNumericalField("paper", "releaseDate"));
		assertFalse(RequestVerifier.entityContainsNumericalField("paper", "title"));
		assertTrue(RequestVerifier.entityContainsNumericalField("institution", "institutionID"));
		assertFalse(RequestVerifier.entityContainsNumericalField("institution", "name"));
		assertTrue(RequestVerifier.entityContainsNumericalField("session", "begin"));
		assertTrue(RequestVerifier.entityContainsNumericalField("session", "category"));
		assertFalse(RequestVerifier.entityContainsNumericalField("session", "löjkfsd"));
		assertFalse(RequestVerifier.entityContainsNumericalField("person", "begin"));
	}
}
