package de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast;

/**
 * Represents and attribute note that has a string value
 */
public class StringAttributeNode extends AttributeNode {
	private StringNode value;

	/**
	 * @see {@link AttributeNode #AttributeNode(int, StringNode) AttributeNode}
	 */
	public StringAttributeNode(int tokenIndex) {
		super(tokenIndex);
	}

	/**
	 * @return This node's attribute value
	 */
	public StringNode getValue() {
		return value;
	}

	/**
	 * Sets this node's attribute value
	 * @param node The attribute value to set
	 */
	public void setValue(StringNode node) {
		this.value = node;
	}

	@Override
	public String toString() {
		return "<" + tokenIndex +">" + name + "=" + value.toString();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && obj instanceof StringAttributeNode && value.equals(((StringAttributeNode)obj).value);
	}
}
