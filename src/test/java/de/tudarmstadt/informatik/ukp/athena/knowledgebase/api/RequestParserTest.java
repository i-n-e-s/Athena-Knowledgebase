package de.tudarmstadt.informatik.ukp.athena.knowledgebase.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.NumberAttributeNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.NumberNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.RequestEntityNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.RequestHierarchyNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.RequestNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.StringAttributeNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.StringNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.exception.SyntaxException;

public class RequestParserTest {
	@Test
	public void testParseCorrectRequest() {
		try {
			//syntactically correct - this doesn't make sense semantically, but this is syntactical verification!
			RequestNode actual = new RequestParser(new RequestScanner("/paper:author=Daniel+Klingbein&topic=vogonpoetry").scan()).parse();
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
		catch(SyntaxException e) {
			fail("Syntactically correct request shouldn't throw a syntax exception");
		}
	}

	@Test
	public void testParseWithNumberAttribute()
	{
		try {
			RequestNode actual = new RequestParser(new RequestScanner("/paper:releaseDate=2018+02+29").scan()).parse();
			RequestNode expected = new RequestNode(0);

			RequestHierarchyNode theOneAndOnly = new RequestHierarchyNode(0);
			RequestEntityNode entityNode = new RequestEntityNode(1);
			StringNode entityNodeName = new StringNode(1);
			NumberAttributeNode releaseDateAttr = new NumberAttributeNode(7);
			StringNode releaseDateAttrName = new StringNode(7);
			NumberNode releaseDateAttrYear = new NumberNode(19);
			NumberNode releaseDateAttrMonth = new NumberNode(24);
			NumberNode releaseDateAttrDay = new NumberNode(27);

			releaseDateAttrDay.setNumber(29);
			releaseDateAttrMonth.setNumber(2);
			releaseDateAttrYear.setNumber(2018);
			releaseDateAttrName.setString("releaseDate");
			//order for the next three is important, as this is the order the parse will receive the numbers in
			releaseDateAttr.addNumber(releaseDateAttrYear);
			releaseDateAttr.addNumber(releaseDateAttrMonth);
			releaseDateAttr.addNumber(releaseDateAttrDay);
			releaseDateAttr.setName(releaseDateAttrName);
			entityNodeName.setString("paper");
			entityNode.setEntityName(entityNodeName);
			entityNode.addAttributeNode(releaseDateAttr);
			theOneAndOnly.setEntity(entityNode);
			expected.addHierarchyNode(theOneAndOnly);
			assertEquals("ASTs are not the same!", expected, actual);
		}
		catch(SyntaxException e) {
			fail("Syntactically correct request shouldn't throw a syntax exception");
		}
	}

	@Test
	public void testParseWithStringAttributeWithNumber()
	{
		//one attribute value part
		try {
			RequestNode actual = new RequestParser(new RequestScanner("/paper:topic=foo42").scan()).parse();
			RequestNode expected = new RequestNode(0);

			RequestHierarchyNode theOneAndOnly = new RequestHierarchyNode(0);
			RequestEntityNode entityNode = new RequestEntityNode(1);
			StringNode entityNodeName = new StringNode(1);
			StringAttributeNode stringAttr = new StringAttributeNode(7);
			StringNode stringAttrName = new StringNode(7);
			StringNode stringAttrValue = new StringNode(13);

			stringAttrValue.setString("foo42");
			stringAttrName.setString("topic");
			stringAttr.setValue(stringAttrValue);
			stringAttr.setName(stringAttrName);
			entityNodeName.setString("paper");
			entityNode.setEntityName(entityNodeName);
			entityNode.addAttributeNode(stringAttr);
			theOneAndOnly.setEntity(entityNode);
			expected.addHierarchyNode(theOneAndOnly);
			assertEquals("ASTs are not the same!", expected, actual);
		}
		catch(SyntaxException e) {
			fail("Syntactically correct request shouldn't throw a syntax exception");
		}

		//two attribue value parts should still result in a single string, separated by a space
		try {
			RequestNode actual = new RequestParser(new RequestScanner("/paper:topic=foo+42").scan()).parse();
			RequestNode expected = new RequestNode(0);

			RequestHierarchyNode theOneAndOnly = new RequestHierarchyNode(0);
			RequestEntityNode entityNode = new RequestEntityNode(1);
			StringNode entityNodeName = new StringNode(1);
			StringAttributeNode stringAttr = new StringAttributeNode(7);
			StringNode stringAttrName = new StringNode(7);
			StringNode stringAttrValue = new StringNode(13);

			stringAttrValue.setString("foo 42");
			stringAttrName.setString("topic");
			stringAttr.setValue(stringAttrValue);
			stringAttr.setName(stringAttrName);
			entityNodeName.setString("paper");
			entityNode.setEntityName(entityNodeName);
			entityNode.addAttributeNode(stringAttr);
			theOneAndOnly.setEntity(entityNode);
			expected.addHierarchyNode(theOneAndOnly);
			assertEquals("ASTs are not the same!", expected, actual);
		}
		catch(SyntaxException e) {
			fail("Syntactically correct request shouldn't throw a syntax exception");
		}
	}

	@Test
	public void testParseWithEntityLast() {
		try {
			RequestNode actual = new RequestParser(new RequestScanner("/paper:topic=foo42/person").scan()).parse();
			RequestNode expected = new RequestNode(0);

			RequestHierarchyNode paperHierarchy = new RequestHierarchyNode(0);
			RequestEntityNode paperNode = new RequestEntityNode(1);
			StringNode paperNodeName = new StringNode(1);
			StringAttributeNode stringAttr = new StringAttributeNode(7);
			StringNode stringAttrName = new StringNode(7);
			StringNode stringAttrValue = new StringNode(13);
			RequestHierarchyNode personHierarchy = new RequestHierarchyNode(18);
			RequestEntityNode personNode = new RequestEntityNode(19);
			StringNode personNodeName = new StringNode(19);

			stringAttrValue.setString("foo42");
			stringAttrName.setString("topic");
			stringAttr.setValue(stringAttrValue);
			stringAttr.setName(stringAttrName);
			paperNodeName.setString("paper");
			paperNode.setEntityName(paperNodeName);
			paperNode.addAttributeNode(stringAttr);
			paperHierarchy.setEntity(paperNode);
			expected.addHierarchyNode(paperHierarchy);
			personNodeName.setString("person");
			personNode.setEntityName(personNodeName);
			personHierarchy.setEntity(personNode);
			expected.addHierarchyNode(personHierarchy);
			assertEquals("ASTs are not the same!", expected, actual);
		}
		catch(SyntaxException e) {
			fail("Syntactically correct request shouldn't throw a syntax exception");
		}
	}

	@Test(expected = SyntaxException.class)
	public void testParseWithMissingAttributeValue() throws SyntaxException {
		new RequestParser(new RequestScanner("/paper:topic=&releaseDate=2018+02+29").scan()).parse();
	}

	@Test(expected = SyntaxException.class)
	public void testParseWithIncorrectSyntax() throws SyntaxException {
		//syntactically incorrect, unknown symbol right here ---------------------v
		new RequestParser(new RequestScanner("/paper:author=Daniel+Klingbein&topic|vogonpoetry").scan()).parse();
	}

	@Test(expected = SyntaxException.class)
	public void testParseWithUnexpectedToken() throws SyntaxException {
		//syntactically incorrect, symbol at incorrect position (& instead of = right here ------|
		//                                                                        v--------------|)
		new RequestParser(new RequestScanner("/paper:author=Daniel+Klingbein&topic&vogonpoetry").scan()).parse();
	}
}
