package de.tudarmstadt.informatik.ukp.athena.knowledgebase.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.util.ArrayDeque;
import java.util.Deque;

import org.junit.Test;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.RequestToken.RequestTokenType;

public class RequestScannerTest {
	@Test
	public void testCorrectScan() {
		Deque<RequestToken> expected = new ArrayDeque<>();
		//syntactically correct
		Deque<RequestToken> actual = new RequestScanner("/paper:releaseDate=2018+02+29&topic=vogonpoetry").scan();
		int i = 0;

		expected.add(new RequestToken(RequestTokenType.HIERARCHY_SEPARATOR, "/", 0));
		expected.add(new RequestToken(RequestTokenType.NAME, "paper", 1));
		expected.add(new RequestToken(RequestTokenType.ATTR_SPECIFIER, ":", 6));
		expected.add(new RequestToken(RequestTokenType.NAME, "releaseDate", 7));
		expected.add(new RequestToken(RequestTokenType.ATTR_EQ, "=", 18));
		expected.add(new RequestToken(RequestTokenType.NUMBER, "2018", 19));
		expected.add(new RequestToken(RequestTokenType.SPACE, "+", 23));
		expected.add(new RequestToken(RequestTokenType.NUMBER, "02", 24));
		expected.add(new RequestToken(RequestTokenType.SPACE, "+", 26));
		expected.add(new RequestToken(RequestTokenType.NUMBER, "29", 27));
		expected.add(new RequestToken(RequestTokenType.ATTR_SEPARATOR, "&", 29));
		expected.add(new RequestToken(RequestTokenType.NAME, "topic", 30));
		expected.add(new RequestToken(RequestTokenType.ATTR_EQ, "=", 35));
		expected.add(new RequestToken(RequestTokenType.NAME, "vogonpoetry", 36));
		expected.add(new RequestToken(RequestTokenType.END, "<end>", 47));

		assertEquals("Actual size is not equal to expected size!", expected.size(), actual.size());

		while(expected.peek() != null) {
			RequestToken actualToken = actual.poll();

			assertNotEquals("Element at position " + i + " is ERROR. This shouldn't happen with a correct request string.", RequestTokenType.ERROR, actualToken.type);
			assertEquals("Element at position " + i + " is not the same!", expected.poll(), actualToken);
			i++;
		}

	}

	@Test
	public void testScanUnkownSymbol() {
		Deque<RequestToken> expected = new ArrayDeque<>();
		//syntactically incorrect, unknown symbol right here -------------------------------v
		Deque<RequestToken> actual = new RequestScanner("/paper:releaseDate=2018+02+29&topic|vogonpoetry").scan();
		int i = 0;

		expected.add(new RequestToken(RequestTokenType.HIERARCHY_SEPARATOR, "/", 0));
		expected.add(new RequestToken(RequestTokenType.NAME, "paper", 1));
		expected.add(new RequestToken(RequestTokenType.ATTR_SPECIFIER, ":", 6));
		expected.add(new RequestToken(RequestTokenType.NAME, "releaseDate", 7));
		expected.add(new RequestToken(RequestTokenType.ATTR_EQ, "=", 18));
		expected.add(new RequestToken(RequestTokenType.NUMBER, "2018", 19));
		expected.add(new RequestToken(RequestTokenType.SPACE, "+", 23));
		expected.add(new RequestToken(RequestTokenType.NUMBER, "02", 24));
		expected.add(new RequestToken(RequestTokenType.SPACE, "+", 26));
		expected.add(new RequestToken(RequestTokenType.NUMBER, "29", 27));
		expected.add(new RequestToken(RequestTokenType.ATTR_SEPARATOR, "&", 29));
		expected.add(new RequestToken(RequestTokenType.NAME, "topic", 30));
		expected.add(new RequestToken(RequestTokenType.ERROR, "|", 35));
		expected.add(new RequestToken(RequestTokenType.NAME, "vogonpoetry", 36));
		expected.add(new RequestToken(RequestTokenType.END, "<end>", 47));

		assertEquals("Actual size is not equal to expected size!", expected.size(), actual.size());

		while(expected.peek() != null) {
			assertEquals("Element at position " + i + " is not the same!", expected.poll(), actual.poll());
			i++;
		}
	}

	@Test
	public void testScanIncorrectlyPlacedSymbol() {
		Deque<RequestToken> expected = new ArrayDeque<>();
		//syntactically incorrect, symbol at incorrect position (& instead of = right here -v)
		Deque<RequestToken> actual = new RequestScanner("/paper:releaseDate=2018+02+29&topic&vogonpoetry").scan();
		int i = 0;

		expected.add(new RequestToken(RequestTokenType.HIERARCHY_SEPARATOR, "/", 0));
		expected.add(new RequestToken(RequestTokenType.NAME, "paper", 1));
		expected.add(new RequestToken(RequestTokenType.ATTR_SPECIFIER, ":", 6));
		expected.add(new RequestToken(RequestTokenType.NAME, "releaseDate", 7));
		expected.add(new RequestToken(RequestTokenType.ATTR_EQ, "=", 18));
		expected.add(new RequestToken(RequestTokenType.NUMBER, "2018", 19));
		expected.add(new RequestToken(RequestTokenType.SPACE, "+", 23));
		expected.add(new RequestToken(RequestTokenType.NUMBER, "02", 24));
		expected.add(new RequestToken(RequestTokenType.SPACE, "+", 26));
		expected.add(new RequestToken(RequestTokenType.NUMBER, "29", 27));
		expected.add(new RequestToken(RequestTokenType.ATTR_SEPARATOR, "&", 29));
		expected.add(new RequestToken(RequestTokenType.NAME, "topic", 30));
		expected.add(new RequestToken(RequestTokenType.ATTR_SEPARATOR, "&", 35));
		expected.add(new RequestToken(RequestTokenType.NAME, "vogonpoetry", 36));
		expected.add(new RequestToken(RequestTokenType.END, "<end>", 47));

		assertEquals("Actual size is not equal to expected size!", expected.size(), actual.size());

		while(expected.peek() != null) {
			RequestToken actualToken = actual.poll();

			assertNotEquals("Element at position " + i + " is ERROR. This shouldn't happen when a known symbol is misplaced (-> Parser).", RequestTokenType.ERROR, actualToken.type);
			assertEquals("Element at position " + i + " is not the same!", expected.poll(), actualToken);
			i++;
		}
	}
}
