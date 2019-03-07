package de.tudarmstadt.informatik.ukp.athena.knowledgebase.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.RequestEntityNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.RequestHierarchyNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.RequestNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.StringAttributeNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.StringNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.exception.SyntaxException;

public class RequestParserTest {
	private RequestParser uut1 = new RequestParser(new RequestScanner("/paper:author=Daniel+Klingbein&topic=vogonpoetry").scan()); //syntactically correct
	private RequestParser uut2 = new RequestParser(new RequestScanner("/paper:author=Daniel+Klingbein&topic|vogonpoetry").scan()); //syntactically incorrect, unknown symbol in the middle (|)
	private RequestParser uut3 = new RequestParser(new RequestScanner("/paper:author=Daniel+Klingbein&topic&vogonpoetry").scan()); //syntactically incorrect, symbol at incorrect position (& instead of = in the middle)

	@Test
	public void testCorrectParse() throws SyntaxException {
		RequestNode actual = uut1.parse();
		RequestNode expected = new RequestNode(0);

		RequestHierarchyNode theOneAndOnly = new RequestHierarchyNode(0);
		RequestEntityNode left = new RequestEntityNode(1);
		StringNode leftJoinNodeName = new StringNode(1);
		StringAttributeNode authorAttr = new StringAttributeNode(7);
		StringNode authorAttrName = new StringNode(7);
		StringNode authorAttrVal = new StringNode(14);
		StringAttributeNode topicAttr = new StringAttributeNode(31);
		StringNode topicAttrName = new StringNode(31);
		StringNode topicAttrVal = new StringNode(37);

		topicAttrVal.setString("vogonpoetry");
		topicAttrName.setString("topic");
		topicAttr.setValue(topicAttrVal);
		topicAttr.setName(topicAttrName);
		authorAttrVal.setString("Daniel Klingbein");
		authorAttrName.setString("author");
		authorAttr.setValue(authorAttrVal);
		authorAttr.setName(authorAttrName);
		left.addAttributeNode(authorAttr);
		left.addAttributeNode(topicAttr);
		leftJoinNodeName.setString("paper");
		left.setEntityName(leftJoinNodeName);

		theOneAndOnly.setEntity(left);
		expected.addHierarchyNode(theOneAndOnly);
		assertEquals("ASTs are not the same!", expected, actual);
	}

	@Test(expected = SyntaxException.class)
	public void testIncorrectSyntaxParse() throws SyntaxException {
		uut2.parse();
	}

	@Test(expected = SyntaxException.class)
	public void testUnexpectedTokenParse() throws SyntaxException {
		uut3.parse();
	}

	@Test
	public void testCountFunction() {
		try {
			RequestNode actual = new RequestParser(new RequestScanner("/count/paper").scan()).parse();
			RequestNode expected = new RequestNode(0);

			RequestHierarchyNode theOneAndOnly = new RequestHierarchyNode(6);
			RequestEntityNode entityNode = new RequestEntityNode(7);
			StringNode entityNodeName = new StringNode(7);

			entityNodeName.setString("paper");
			entityNode.setEntityName(entityNodeName);
			theOneAndOnly.setEntity(entityNode);
			expected.addHierarchyNode(theOneAndOnly);
			expected.setIsCountFunction(true);
			assertEquals("ASTs are not the same!", expected, actual);
		}
		catch(SyntaxException e) {
			fail("Syntactically correct request shouldn't throw a syntax exception");
		}
	}
}
