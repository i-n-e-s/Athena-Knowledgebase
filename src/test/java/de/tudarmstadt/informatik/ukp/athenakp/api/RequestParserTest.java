package de.tudarmstadt.informatik.ukp.athenakp.api;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import de.tudarmstadt.informatik.ukp.athenakp.api.ast.NumberAttributeNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.NumberNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.RequestJoinNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.RequestNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.StringAttributeNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.StringNode;
import de.tudarmstadt.informatik.ukp.athenakp.exception.SyntaxException;

public class RequestParserTest {
	private RequestParser uut1 = new RequestParser(new RequestScanner("/paper:Author=Daniel+Klingbein&Topic=vogonpoetry$author:Obit=1993+05+22").scan()); //syntactically correct
	private RequestParser uut2 = new RequestParser(new RequestScanner("/paper:Author=Daniel+Klingbein&Topic|vogonpoetry$author:Obit=1993+05+22").scan()); //syntactically incorrect, unknown symbol in the middle (|)
	private RequestParser uut3 = new RequestParser(new RequestScanner("/paper:Author=Daniel+Klingbein&Topic&vogonpoetry$author:Obit=1993+05+22").scan()); //syntactically incorrect, symbol at incorrect position (& instead of = in the middle)

	@Test
	public void testCorrectParse() throws SyntaxException {
		RequestNode actual = uut1.parse();
		RequestNode expected = new RequestNode(1);

		RequestJoinNode left = new RequestJoinNode(1);
		StringNode leftJoinNodeName = new StringNode(1);
		StringAttributeNode authorAttr = new StringAttributeNode(7);
		StringNode authorAttrName = new StringNode(7);
		StringNode authorAttrVal = new StringNode(14);
		StringAttributeNode topicAttr = new StringAttributeNode(31);
		StringNode topicAttrName = new StringNode(31);
		StringNode topicAttrVal = new StringNode(37);

		RequestJoinNode right = new RequestJoinNode(49);
		StringNode rightJoinNodeName = new StringNode(49);
		NumberAttributeNode obitAttr = new NumberAttributeNode(56);
		StringNode obitAttrName = new StringNode(56);
		NumberNode obitAttrVal1 = new NumberNode(61);
		NumberNode obitAttrVal2 = new NumberNode(66);
		NumberNode obitAttrVal3 = new NumberNode(69);

		obitAttrVal3.setValue(22);
		obitAttrVal2.setValue(5);
		obitAttrVal1.setValue(1993);
		obitAttrName.setValue("Obit");
		obitAttr.addValue(obitAttrVal1);
		obitAttr.addValue(obitAttrVal2);
		obitAttr.addValue(obitAttrVal3);
		obitAttr.setName(obitAttrName);
		right.addAttributeNode(obitAttr);
		rightJoinNodeName.setValue("author");
		right.setEntityName(rightJoinNodeName);

		topicAttrVal.setValue("vogonpoetry");
		topicAttrName.setValue("Topic");
		topicAttr.setValue(topicAttrVal);
		topicAttr.setName(topicAttrName);
		authorAttrVal.setValue("Daniel Klingbein");
		authorAttrName.setValue("Author");
		authorAttr.setValue(authorAttrVal);
		authorAttr.setName(authorAttrName);
		left.addAttributeNode(authorAttr);
		left.addAttributeNode(topicAttr);
		leftJoinNodeName.setValue("paper");
		left.setEntityName(leftJoinNodeName);

		expected.addJoin(left);
		expected.addJoin(right);
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
}
