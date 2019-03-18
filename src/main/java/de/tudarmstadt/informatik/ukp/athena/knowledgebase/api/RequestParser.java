package de.tudarmstadt.informatik.ukp.athena.knowledgebase.api;

import java.util.Deque;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.RequestToken.RequestTokenType;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.AttributeNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.NumberAttributeNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.NumberNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.RequestEntityNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.RequestHierarchyNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.RequestNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.StringAttributeNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast.StringNode;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.exception.SyntaxException;

public class RequestParser {
	private final Deque<RequestToken> tokens;
	private RequestToken currentToken;

	/**
	 * @param tokens The tokens to parse as constructed by the {@link RequestScanner}
	 */
	public RequestParser(Deque<RequestToken> tokens) {
		this.tokens = tokens;
		currentToken = tokens.poll(); //load the first token
	}

	/**
	 * Parses the tokens given to this parser
	 * @return An abstract syntax tree
	 * @throws SyntaxException When an ERROR or unexpected token appears
	 */
	public RequestNode parse() throws SyntaxException {
		RequestNode root = new RequestNode(currentToken.index);

		//as long as there are tokens in the queue
		while(currentToken != null && currentToken.type != RequestTokenType.END) { //can be null if the last thing was an entity name (before the :), poll returns null if the deque is empty
			if(root.getHierarchy().size() == 0 && tokens.peek().actual.equals("count")) //peek because the currentToken is / and not count
			{
				accept(RequestTokenType.HIERARCHY_SEPARATOR);
				accept(RequestTokenType.NAME);
				root.setIsCountFunction(true);
			}
			else
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

		accept(RequestTokenType.HIERARCHY_SEPARATOR);

		//the first part of a request is always an entity name
		if(currentToken.type == RequestTokenType.NAME)
			node.setEntity(parseRequestEntity());

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

		stringNode.setString(name);
		node.setEntityName(stringNode);
		//up until this point the name got saved to the node

		if(currentToken.type == RequestTokenType.ATTR_SPECIFIER)
		{
			accept(); //accept the :

			//now parse the first attribute
			node.addAttributeNode(parseAttribute()); //need this here so the first attribute gets parsed, otherwhise the parser would check for /name:&attr=val

			//parse any other attributes
			while(currentToken.type == RequestTokenType.ATTR_SEPARATOR) {
				accept(RequestTokenType.ATTR_SEPARATOR);
				node.addAttributeNode(parseAttribute());
			}
		}
		else
			accept(RequestTokenType.END); //if there are no attributes, it should be the end of the request

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

		stringNode.setString(name);
		accept(RequestTokenType.ATTR_EQ);

		//attributes either consist of strings or numbers
		switch(currentToken.type) {
			case NAME: node = parseStringAttribute(attrIndex); break;
			case NUMBER: node = parseNumberAttribute(attrIndex); break;
			default: throw new SyntaxException(currentToken.index, currentToken.actual);
		}

		node.setName(stringNode); //there's no way to know the node type beforehand, so setting the name will happen here
		return node;
	}

	/**
	 * Parses a string attribute ("foo=bar" or "foo=b+a+r")
	 * @param attrIndex The attribute's index, as there was an accept() call before the call of this method (the name of the attr)
	 * @return The abstract syntax tree representing this construct
	 * @throws SyntaxException When an ERROR or unexpected token appears
	 */
	private StringAttributeNode parseStringAttribute(int attrIndex) throws SyntaxException {
		StringAttributeNode node = new StringAttributeNode(attrIndex);
		StringNode stringNode = new StringNode(currentToken.index);
		String value = "";

		//while there is a string...
		while(currentToken.type == RequestTokenType.NAME || currentToken.type == RequestTokenType.NUMBER || currentToken.type == RequestTokenType.ESCAPE) { //ACL+2018 should be considered a string token as well
			//...add it to the attribute existing value...
			if(currentToken.type == RequestTokenType.NAME)
				value += accept(RequestTokenType.NAME);
			else if(currentToken.type == RequestTokenType.NUMBER)
				value += accept(RequestTokenType.NUMBER);
			else if(currentToken.type == RequestTokenType.ESCAPE)
			{
				accept(); //accept the escape character
				value += currentToken.actual; //add whatever comes next to the escape character
				accept(); //accept whatever comes next
			}

			//...and check for more parts in the attribute
			if(currentToken.type == RequestTokenType.SPACE) //if it's not a SPACE, then it will be something else and be catched by the while condition
			{
				accept();
				value += " ";
			}
		}

		stringNode.setString(value.trim()); //trim off the last space
		node.setValue(stringNode);
		return node;
	}

	/**
	 * Parses a number attribute ("foo=123" or "foo=1+2+3")
	 * @param attrIndex The attribute's index, as there was an accept() call before the call of this method (the name of the attr)
	 * @return The abstract syntax tree representing this construct
	 * @throws SyntaxException When an ERROR or unexpected token appears
	 */
	private NumberAttributeNode parseNumberAttribute(int attrIndex) throws SyntaxException {
		NumberAttributeNode node = new NumberAttributeNode(attrIndex);

		//while there is a number...
		while(currentToken.type == RequestTokenType.NUMBER) {
			//...make a number node...
			NumberNode numberNode = new NumberNode(currentToken.index);
			int val = Integer.parseInt(accept(RequestTokenType.NUMBER)); //parseInt won't throw a NumberFormatException as the scanner already took care of checking whether the contents are digits

			numberNode.setNumber(val);
			node.addNumber(numberNode);

			//...and check for more parts in the attribute
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
		currentToken = tokens.poll(); //get the next token

		//if it's an error, throw an exception
		if(currentToken != null && currentToken.type == RequestTokenType.ERROR)
			throw new SyntaxException(currentToken.index, currentToken.actual);
	}

	/**
	 * Only accepts a token of the given type, else throws a syntax exception
	 * @param tokenType The token type to accept
	 * @return The actual token as defined in {@link RequestToken}
	 * @throws SyntaxException When an ERROR or unexpected token appears
	 */
	private String accept(RequestTokenType tokenType) throws SyntaxException {
		RequestToken token = currentToken; //save the current token so its actual can be returned

		if(token.type != tokenType) //if the token does not match the expected type, throw an exception
			throw new SyntaxException(currentToken.index, currentToken.actual);

		accept();
		return token.actual;
	}
}
