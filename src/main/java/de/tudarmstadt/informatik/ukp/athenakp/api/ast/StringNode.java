package de.tudarmstadt.informatik.ukp.athenakp.api.ast;

/**
 * Represents the name of an entity or attribute or the value of an attribute
 */
public class StringNode extends BaseNode {
	private String val;

	/**
	 * @see {@link BaseNode#BaseNode(int) BaseNode}
	 */
	public StringNode(int tokenIndex) {
		super(tokenIndex);
	}

	/**
	 * @return This node's value
	 */
	public String getValue() {
		return val;
	}

	/**
	 * Sets this node's value
	 * @param val The value to set
	 */
	public void setValue(String val) {
		this.val = val;
	}

	@Override
	public String toString() {
		return "<" + tokenIndex +">" + val;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && obj instanceof StringNode && val.equals(((StringNode)obj).val);
	}
}
