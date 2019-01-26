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
		uut2 = new RequestScanner("/paper:Author=Daniel+Klingbein&Topic?vogonpoetry$author:Obit=1993+05+22"); //syntactically incorrect, unknown symbol in the middle (?)
	}

	@Test
	public void testScan() {
		Deque<RequestToken> expected = new ArrayDeque<>();
		Deque<RequestToken> actual = uut1.scan();
		int i = 0;

		expected.add(new RequestToken(RequestTokenType.HIERARCHY_SEPERATOR, "/"));
		expected.add(new RequestToken(RequestTokenType.NAME, "paper"));
		expected.add(new RequestToken(RequestTokenType.ATTR_SPECIFIER, ":"));
		expected.add(new RequestToken(RequestTokenType.NAME, "Author"));
		expected.add(new RequestToken(RequestTokenType.ATTR_EQ, "="));
		expected.add(new RequestToken(RequestTokenType.NAME, "Daniel"));
		expected.add(new RequestToken(RequestTokenType.SPACE, "+"));
		expected.add(new RequestToken(RequestTokenType.NAME, "Klingbein"));
		expected.add(new RequestToken(RequestTokenType.ATTR_SEPERATOR, "&"));
		expected.add(new RequestToken(RequestTokenType.NAME, "Topic"));
		expected.add(new RequestToken(RequestTokenType.ATTR_EQ, "="));
		expected.add(new RequestToken(RequestTokenType.NAME, "vogonpoetry"));
		expected.add(new RequestToken(RequestTokenType.JOIN, "$"));
		expected.add(new RequestToken(RequestTokenType.NAME, "author"));
		expected.add(new RequestToken(RequestTokenType.ATTR_SPECIFIER, ":"));
		expected.add(new RequestToken(RequestTokenType.NAME, "Obit"));
		expected.add(new RequestToken(RequestTokenType.ATTR_EQ, "="));
		expected.add(new RequestToken(RequestTokenType.NUMBER, "1993"));
		expected.add(new RequestToken(RequestTokenType.SPACE, "+"));
		expected.add(new RequestToken(RequestTokenType.NUMBER, "05"));
		expected.add(new RequestToken(RequestTokenType.SPACE, "+"));
		expected.add(new RequestToken(RequestTokenType.NUMBER, "22"));

		assertEquals("Actual size is not equal to expected size!", expected.size(), actual.size());

		while(expected.peek() != null) {
			assertEquals("Element at position " + i++ + " is not the same!", expected.poll(), actual.poll());
		}

		////////////

		expected = new ArrayDeque<>();
		actual = uut2.scan();
		expected.add(new RequestToken(RequestTokenType.HIERARCHY_SEPERATOR, "/"));
		expected.add(new RequestToken(RequestTokenType.NAME, "paper"));
		expected.add(new RequestToken(RequestTokenType.ATTR_SPECIFIER, ":"));
		expected.add(new RequestToken(RequestTokenType.NAME, "Author"));
		expected.add(new RequestToken(RequestTokenType.ATTR_EQ, "="));
		expected.add(new RequestToken(RequestTokenType.NAME, "Daniel"));
		expected.add(new RequestToken(RequestTokenType.SPACE, "+"));
		expected.add(new RequestToken(RequestTokenType.NAME, "Klingbein"));
		expected.add(new RequestToken(RequestTokenType.ATTR_SEPERATOR, "&"));
		expected.add(new RequestToken(RequestTokenType.NAME, "Topic"));
		expected.add(new RequestToken(RequestTokenType.ERROR, "?"));
		expected.add(new RequestToken(RequestTokenType.NAME, "vogonpoetry"));
		expected.add(new RequestToken(RequestTokenType.JOIN, "$"));
		expected.add(new RequestToken(RequestTokenType.NAME, "author"));
		expected.add(new RequestToken(RequestTokenType.ATTR_SPECIFIER, ":"));
		expected.add(new RequestToken(RequestTokenType.NAME, "Obit"));
		expected.add(new RequestToken(RequestTokenType.ATTR_EQ, "="));
		expected.add(new RequestToken(RequestTokenType.NUMBER, "1993"));
		expected.add(new RequestToken(RequestTokenType.SPACE, "+"));
		expected.add(new RequestToken(RequestTokenType.NUMBER, "05"));
		expected.add(new RequestToken(RequestTokenType.SPACE, "+"));
		expected.add(new RequestToken(RequestTokenType.NUMBER, "22"));

		assertEquals("Actual size is not equal to expected size!", expected.size(), actual.size());

		while(expected.peek() != null) {
			assertEquals("Element at position " + i++ + " is not the same!", expected.poll(), actual.poll());
		}
	}
}
