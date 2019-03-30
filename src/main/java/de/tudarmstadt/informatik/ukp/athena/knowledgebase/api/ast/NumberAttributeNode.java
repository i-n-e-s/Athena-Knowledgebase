package de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an attribute note that has integer values
 */
public class NumberAttributeNode extends AttributeNode {
	private List<NumberNode> numbers = new ArrayList<>();

	/**
	 * @see AttributeNode#AttributeNode(int)
	 */
	public NumberAttributeNode(int tokenIndex) {
		super(tokenIndex);
	}

	/**
	 * @return the values (previously seperated by + in the request) of this node
	 */
	public List<NumberNode> getNumbers() {
		return numbers;
	}

	/**
	 * Adds an integer to the list of numbers of this attribute's value
	 * @param node The node to add
	 */
	public void addNumber(NumberNode node) {
		numbers.add(node);
	}

	/**
	 * Generates a string of all values of this node
	 * @return All values of this node concatenated with a +
	 */
	public String valuesToString() {
		String result = "";

		for(NumberNode node : numbers) {
			result += node.getNumber() + "+";
		}

		return result.substring(0, result.length() - 1); //remove last +
	}

	@Override
	public String toString() {
		String result = "<" + tokenIndex +">" + name + "=";

		for(NumberNode node : numbers) {
			result += node.toString() + "+";
		}

		return result.substring(0, result.length() - 1); //remove last +
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && obj instanceof NumberAttributeNode && numbers.equals(((NumberAttributeNode)obj).numbers);
	}
}
