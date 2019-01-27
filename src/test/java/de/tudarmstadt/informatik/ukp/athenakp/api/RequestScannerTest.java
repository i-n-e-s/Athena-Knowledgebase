package de.tudarmstadt.informatik.ukp.athenakp.api;

import static org.junit.Assert.assertEquals;

import java.util.ArrayDeque;
import java.util.Deque;

import org.junit.Before;
import org.junit.Test;

import de.tudarmstadt.informatik.ukp.athenakp.api.RequestToken.RequestTokenType;

public class RequestScannerTest {
	private RequestScanner uut1, uut2;

	@Before
	public void setup() {
		uut1 = new RequestScanner("/paper:Author=Daniel+Klingbein&Topic=vogonpoetry$author:Obit=1993+05+22"); //syntactically correct
		uut2 = new RequestScanner("/paper:Author=Daniel+Klingbein&Topic|vogonpoetry$author:Obit=1993+05+22"); //syntactically incorrect, unknown symbol in the middle (|)
	}

	@Test
	public void testScan() {
		Deque<RequestToken> expected = new ArrayDeque<>();
		Deque<RequestToken> actual = uut1.scan();
		int i = 0;

		expected.add(new RequestToken(RequestTokenType.HIERARCHY_SEPERATOR, "/", 0));
		expected.add(new RequestToken(RequestTokenType.NAME, "paper", 1));
		expected.add(new RequestToken(RequestTokenType.ATTR_SPECIFIER, ":", 6));
		expected.add(new RequestToken(RequestTokenType.NAME, "Author", 7));
		expected.add(new RequestToken(RequestTokenType.ATTR_EQ, "=", 13));
		expected.add(new RequestToken(RequestTokenType.NAME, "Daniel", 14));
		expected.add(new RequestToken(RequestTokenType.SPACE, "+", 20));
		expected.add(new RequestToken(RequestTokenType.NAME, "Klingbein", 21));
		expected.add(new RequestToken(RequestTokenType.ATTR_SEPERATOR, "&", 30));
		expected.add(new RequestToken(RequestTokenType.NAME, "Topic", 31));
		expected.add(new RequestToken(RequestTokenType.ATTR_EQ, "=", 36));
		expected.add(new RequestToken(RequestTokenType.NAME, "vogonpoetry", 37));
		expected.add(new RequestToken(RequestTokenType.JOIN, "$", 48));
		expected.add(new RequestToken(RequestTokenType.NAME, "author", 49));
		expected.add(new RequestToken(RequestTokenType.ATTR_SPECIFIER, ":", 55));
		expected.add(new RequestToken(RequestTokenType.NAME, "Obit", 56));
		expected.add(new RequestToken(RequestTokenType.ATTR_EQ, "=", 60));
		expected.add(new RequestToken(RequestTokenType.NUMBER, "1993", 61));
		expected.add(new RequestToken(RequestTokenType.SPACE, "+", 65));
		expected.add(new RequestToken(RequestTokenType.NUMBER, "05", 66));
		expected.add(new RequestToken(RequestTokenType.SPACE, "+", 68));
		expected.add(new RequestToken(RequestTokenType.NUMBER, "22", 69));
		expected.add(new RequestToken(RequestTokenType.END, "<end>", 71));

		assertEquals("Actual size is not equal to expected size!", expected.size(), actual.size());

		while(expected.peek() != null) {
			assertEquals("Element at position " + i++ + " is not the same!", expected.poll(), actual.poll());
		}

		////////////

		expected = new ArrayDeque<>();
		actual = uut2.scan();
		expected.add(new RequestToken(RequestTokenType.HIERARCHY_SEPERATOR, "/", 0));
		expected.add(new RequestToken(RequestTokenType.NAME, "paper", 1));
		expected.add(new RequestToken(RequestTokenType.ATTR_SPECIFIER, ":", 6));
		expected.add(new RequestToken(RequestTokenType.NAME, "Author", 7));
		expected.add(new RequestToken(RequestTokenType.ATTR_EQ, "=", 13));
		expected.add(new RequestToken(RequestTokenType.NAME, "Daniel", 14));
		expected.add(new RequestToken(RequestTokenType.SPACE, "+", 20));
		expected.add(new RequestToken(RequestTokenType.NAME, "Klingbein", 21));
		expected.add(new RequestToken(RequestTokenType.ATTR_SEPERATOR, "&", 30));
		expected.add(new RequestToken(RequestTokenType.NAME, "Topic", 31));
		expected.add(new RequestToken(RequestTokenType.ERROR, "|", 36));
		expected.add(new RequestToken(RequestTokenType.NAME, "vogonpoetry", 37));
		expected.add(new RequestToken(RequestTokenType.JOIN, "$", 48));
		expected.add(new RequestToken(RequestTokenType.NAME, "author", 49));
		expected.add(new RequestToken(RequestTokenType.ATTR_SPECIFIER, ":", 55));
		expected.add(new RequestToken(RequestTokenType.NAME, "Obit", 56));
		expected.add(new RequestToken(RequestTokenType.ATTR_EQ, "=", 60));
		expected.add(new RequestToken(RequestTokenType.NUMBER, "1993", 61));
		expected.add(new RequestToken(RequestTokenType.SPACE, "+", 65));
		expected.add(new RequestToken(RequestTokenType.NUMBER, "05", 66));
		expected.add(new RequestToken(RequestTokenType.SPACE, "+", 68));
		expected.add(new RequestToken(RequestTokenType.NUMBER, "22", 69));
		expected.add(new RequestToken(RequestTokenType.END, "<end>", 71));

		assertEquals("Actual size is not equal to expected size!", expected.size(), actual.size());

		while(expected.peek() != null) {
			assertEquals("Element at position " + i++ + " is not the same!", expected.poll(), actual.poll());
		}
	}
}
