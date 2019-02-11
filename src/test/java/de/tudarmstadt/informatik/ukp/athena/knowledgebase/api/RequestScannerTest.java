package de.tudarmstadt.informatik.ukp.athena.knowledgebase.api;

import static org.junit.Assert.assertEquals;

import java.util.ArrayDeque;
import java.util.Deque;

import org.junit.Before;
import org.junit.Test;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.RequestScanner;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.RequestToken;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.RequestToken.RequestTokenType;

public class RequestScannerTest {
	private RequestScanner uut1, uut2, uut3;

	@Before
	public void setup() {
		uut1 = new RequestScanner("/paper:author=Daniel+Klingbein&topic=vogonpoetry"); //syntactically correct
		uut2 = new RequestScanner("/paper:author=Daniel+Klingbein&topic|vogonpoetry"); //syntactically incorrect, unknown symbol in the middle (|)
		uut3 = new RequestScanner("/paper:author=Daniel+Klingbein&topic&vogonpoetry"); //syntactically incorrect, symbol at incorrect position (& instead of = in the middle)
	}

	@Test
	public void testScan() {
		Deque<RequestToken> expected = new ArrayDeque<>();
		Deque<RequestToken> actual = uut1.scan();
		int i = 0;

		expected.add(new RequestToken(RequestTokenType.HIERARCHY_SEPERATOR, "/", 0));
		expected.add(new RequestToken(RequestTokenType.NAME, "paper", 1));
		expected.add(new RequestToken(RequestTokenType.ATTR_SPECIFIER, ":", 6));
		expected.add(new RequestToken(RequestTokenType.NAME, "author", 7));
		expected.add(new RequestToken(RequestTokenType.ATTR_EQ, "=", 13));
		expected.add(new RequestToken(RequestTokenType.NAME, "Daniel", 14));
		expected.add(new RequestToken(RequestTokenType.SPACE, "+", 20));
		expected.add(new RequestToken(RequestTokenType.NAME, "Klingbein", 21));
		expected.add(new RequestToken(RequestTokenType.ATTR_SEPERATOR, "&", 30));
		expected.add(new RequestToken(RequestTokenType.NAME, "topic", 31));
		expected.add(new RequestToken(RequestTokenType.ATTR_EQ, "=", 36));
		expected.add(new RequestToken(RequestTokenType.NAME, "vogonpoetry", 37));
		expected.add(new RequestToken(RequestTokenType.END, "<end>", 48));

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
		expected.add(new RequestToken(RequestTokenType.NAME, "author", 7));
		expected.add(new RequestToken(RequestTokenType.ATTR_EQ, "=", 13));
		expected.add(new RequestToken(RequestTokenType.NAME, "Daniel", 14));
		expected.add(new RequestToken(RequestTokenType.SPACE, "+", 20));
		expected.add(new RequestToken(RequestTokenType.NAME, "Klingbein", 21));
		expected.add(new RequestToken(RequestTokenType.ATTR_SEPERATOR, "&", 30));
		expected.add(new RequestToken(RequestTokenType.NAME, "topic", 31));
		expected.add(new RequestToken(RequestTokenType.ERROR, "|", 36));
		expected.add(new RequestToken(RequestTokenType.NAME, "vogonpoetry", 37));
		expected.add(new RequestToken(RequestTokenType.END, "<end>", 48));

		assertEquals("Actual size is not equal to expected size!", expected.size(), actual.size());

		while(expected.peek() != null) {
			assertEquals("Element at position " + i++ + " is not the same!", expected.poll(), actual.poll());
		}

		////////////

		expected = new ArrayDeque<>();
		actual = uut3.scan();
		expected.add(new RequestToken(RequestTokenType.HIERARCHY_SEPERATOR, "/", 0));
		expected.add(new RequestToken(RequestTokenType.NAME, "paper", 1));
		expected.add(new RequestToken(RequestTokenType.ATTR_SPECIFIER, ":", 6));
		expected.add(new RequestToken(RequestTokenType.NAME, "author", 7));
		expected.add(new RequestToken(RequestTokenType.ATTR_EQ, "=", 13));
		expected.add(new RequestToken(RequestTokenType.NAME, "Daniel", 14));
		expected.add(new RequestToken(RequestTokenType.SPACE, "+", 20));
		expected.add(new RequestToken(RequestTokenType.NAME, "Klingbein", 21));
		expected.add(new RequestToken(RequestTokenType.ATTR_SEPERATOR, "&", 30));
		expected.add(new RequestToken(RequestTokenType.NAME, "topic", 31));
		expected.add(new RequestToken(RequestTokenType.ATTR_SEPERATOR, "&", 36));
		expected.add(new RequestToken(RequestTokenType.NAME, "vogonpoetry", 37));
		expected.add(new RequestToken(RequestTokenType.END, "<end>", 48));

		assertEquals("Actual size is not equal to expected size!", expected.size(), actual.size());

		while(expected.peek() != null) {
			assertEquals("Element at position " + i++ + " is not the same!", expected.poll(), actual.poll());
		}
	}
}
