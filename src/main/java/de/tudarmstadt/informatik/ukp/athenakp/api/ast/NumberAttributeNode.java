package de.tudarmstadt.informatik.ukp.athenakp.api.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an attribute note that has integer values
 */
public class NumberAttributeNode extends AttributeNode {
	private List<NumberNode> values = new ArrayList<>();

	/**
	 * @see {@link AttributeNode#AttributeNode(int, StringNode) AttributeNode}
	 */
	public NumberAttributeNode(int tokenIndex) {
		super(tokenIndex);
	}

	/**
	 * @return the values (previously seperated by + in the request) of this node
	 */
	public List<NumberNode> getValues() {
		return values;
	}

	/**
	 * Adds an integer to the list of numbers of this attribute's value
	 * @param val The node to add
	 */
	public void addValue(NumberNode node) {
		values.add(node);
	}

	@Override
	public String toString() {
		String result = "<" + tokenIndex +">" + name + "=";

		for(NumberNode node : values) {
			result += node.toString() + "+";
		}

		return result.substring(0, result.length() - 1); //remove last +
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && obj instanceof NumberAttributeNode && values.equals(((NumberAttributeNode)obj).values);
	}
}
