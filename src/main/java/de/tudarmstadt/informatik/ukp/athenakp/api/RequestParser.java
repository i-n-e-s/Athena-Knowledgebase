package de.tudarmstadt.informatik.ukp.athenakp.api;

import java.util.Deque;

import de.tudarmstadt.informatik.ukp.athenakp.api.RequestToken.RequestTokenType;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.AttributeNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.NumberAttributeNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.NumberNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.RequestEntityNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.RequestHierarchyNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.RequestNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.StringAttributeNode;
import de.tudarmstadt.informatik.ukp.athenakp.api.ast.StringNode;
import de.tudarmstadt.informatik.ukp.athenakp.exception.SyntaxException;

public class RequestParser {
	private final Deque<RequestToken> tokens;
	private RequestToken currentToken;

	/**
	 * @param tokens The tokens to parse as constructed by the {@link RequestScanner}
	 */
	public RequestParser(Deque<RequestToken> tokens) {
		this.tokens = tokens;
		currentToken = tokens.poll();
	}

	/**
	 * Parses the tokens given to this parser
	 * @return An abstract syntax tree
	 * @throws SyntaxException When an ERROR or unexpected token appears
	 */
	public RequestNode parse() throws SyntaxException {
		RequestNode root = new RequestNode(currentToken.index);

		while(currentToken.type != RequestTokenType.END) {
			root.addHierarchyNode(parseHierarchyEntry());
		}

		return root;
	}

	/**
	 * Parses a part of a hierarchy (/x:y=z(...)$a:b=c(...))
	 * @return The abstract syntax tree representing this construct
	 * @throws SyntaxException When an ERROR or unexpected token appears
	 */
	private RequestHierarchyNode parseHierarchyEntry() throws SyntaxException {
		RequestHierarchyNode node = new RequestHierarchyNode(currentToken.index);

		accept();

		if(currentToken.type == RequestTokenType.NAME) {
			node.addEntity(parseRequestEntity());
		}

		while(currentToken.type == RequestTokenType.JOIN) {
			accept();
			node.addEntity(parseRequestEntity());
		}

		return node;
	}

	/**
	 * Parses a part of a join ("entity:attr=val&(...)")
	 * @return The abstract syntax tree representing this construct
	 * @throws SyntaxException When an ERROR or unexpected token appears
	 */
	private RequestEntityNode parseRequestEntity() throws SyntaxException {
		RequestEntityNode node = new RequestEntityNode(currentToken.index);
		StringNode stringNode = new StringNode(currentToken.index);
		String name = accept(RequestTokenType.NAME);

		stringNode.setValue(name);
		node.setEntityName(stringNode);
		accept(RequestTokenType.ATTR_SPECIFIER);

		node.addAttributeNode(parseAttribute()); //need this here so the first attribute gets parsed, otherwhise the parser would check for /name:&attr=val

		while(currentToken.type == RequestTokenType.ATTR_SEPERATOR) {
			accept(RequestTokenType.ATTR_SEPERATOR);
			node.addAttributeNode(parseAttribute());
		}

		return node;
	}

	/**
	 * Parses an attribute ("foo=bar" or "foo=b+a+r" or "foo=123" or "foo=1+2+3")
	 * @return The abstract syntax tree representing this construct
	 * @throws SyntaxException When an ERROR or unexpected token appears
	 */
	private AttributeNode parseAttribute() throws SyntaxException {
		int attrIndex = currentToken.index;
		AttributeNode node;
		StringNode stringNode = new StringNode(attrIndex);
		String name = accept(RequestTokenType.NAME);

		stringNode.setValue(name);
		accept(RequestTokenType.ATTR_EQ);

		switch(currentToken.type) {
			case NAME: node = parseStringAttribute(attrIndex); break;
			case NUMBER: node = parseNumberAttribute(attrIndex); break;
			default: throw new SyntaxException(currentToken.index, currentToken.actual);
		}

		node.setName(stringNode);
		return node;
	}

	/**
	 * Parses a string attribute ("foo=bar" or "foo=b+a+r")
	 * @param The attribute's index, as there was an accept() call before the call of this method (the name of the attr)
	 * @return The abstract syntax tree representing this construct
	 * @throws SyntaxException When an ERROR or unexpected token appears
	 */
	private StringAttributeNode parseStringAttribute(int attrIndex) throws SyntaxException {
		StringAttributeNode node = new StringAttributeNode(attrIndex);
		StringNode stringNode = new StringNode(currentToken.index);
		String value = "";

		while(currentToken.type == RequestTokenType.NAME) {
			value += accept(RequestTokenType.NAME) + " ";

			if(currentToken.type == RequestTokenType.SPACE) //if it's not a SPACE, then it will be something else and be catched by the while condition
				accept();
		}

		stringNode.setValue(value.trim()); //trim off the last space
		node.setValue(stringNode);
		return node;
	}

	/**
	 * Parses a number attribute ("foo=123" or "foo=1+2+3")
	 * @param The attribute's index, as there was an accept() call before the call of this method (the name of the attr)
	 * @return The abstract syntax tree representing this construct
	 * @throws SyntaxException When an ERROR or unexpected token appears
	 */
	private NumberAttributeNode parseNumberAttribute(int attrIndex) throws SyntaxException {
		NumberAttributeNode node = new NumberAttributeNode(attrIndex);

		while(currentToken.type == RequestTokenType.NUMBER) {
			NumberNode numberNode = new NumberNode(currentToken.index);
			int val = Integer.parseInt(accept(RequestTokenType.NUMBER));

			numberNode.setValue(val);
			node.addValue(numberNode); //parseInt won't throw a NumberFormatException as the scanner already took care of checking whether the contents are digits

			if(currentToken.type == RequestTokenType.SPACE) //if it's not a SPACE, then it will be something else and be catched by the while condition
				accept();
		}

		return node;
	}

	/**
	 * Accepts the current token, checks for an error and queues the next one
	 * @throws SyntaxException When an ERROR or unexpected token appears
	 */
	private void accept() throws SyntaxException {
		currentToken = tokens.poll();

		if(currentToken.type == RequestTokenType.ERROR)
			throw new SyntaxException(currentToken.index, currentToken.actual);
	}

	/**
	 * Only accepts a token of the given type, else throws a syntax exception
	 * @param tokenType The token type to accept
	 * @return The actual token as defined in {@link RequestToken}
	 * @throws SyntaxException When an ERROR or unexpected token appears
	 */
	private String accept(RequestTokenType tokenType) throws SyntaxException {
		RequestToken token = currentToken;

		if(token.type != tokenType)
			throw new SyntaxException(currentToken.index, currentToken.actual);

		accept();
		return token.actual;
	}
}
