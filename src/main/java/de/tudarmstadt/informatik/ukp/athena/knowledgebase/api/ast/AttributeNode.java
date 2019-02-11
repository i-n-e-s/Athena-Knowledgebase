package de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast;

/**
 * Represents an attribue name+value, child classes implement its value
 */
public class AttributeNode extends BaseNode {
	protected StringNode name;

	/**
	 * @see {@link BaseNode#BaseNode(int) BaseNode}
	 */
	protected AttributeNode(int tokenIndex) {
		super(tokenIndex);
	}

	/**
	 * @return This node's attribute name
	 */
	public StringNode getName() {
		return name;
	}

	/**
	 * Sets this node's attribute name
	 * @param node The attribute name to set
	 */
	public void setName(StringNode node) {
		this.name = node;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && obj instanceof AttributeNode && name.equals(((AttributeNode)obj).name);
	}
}
