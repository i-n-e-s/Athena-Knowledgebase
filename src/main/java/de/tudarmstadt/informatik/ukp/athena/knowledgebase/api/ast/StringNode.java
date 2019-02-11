package de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast;

/**
 * Represents the name of an entity or attribute or the value of an attribute
 */
public class StringNode extends BaseNode {
	private String string;

	/**
	 * @see {@link BaseNode#BaseNode(int) BaseNode}
	 */
	public StringNode(int tokenIndex) {
		super(tokenIndex);
	}

	/**
	 * @return This node's value
	 */
	public String getString() {
		return string;
	}

	/**
	 * Sets this node's value
	 * @param val The value to set
	 */
	public void setString(String val) {
		this.string = val;
	}

	@Override
	public String toString() {
		return "<" + tokenIndex +">" + string;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && obj instanceof StringNode && string.equals(((StringNode)obj).string);
	}
}
