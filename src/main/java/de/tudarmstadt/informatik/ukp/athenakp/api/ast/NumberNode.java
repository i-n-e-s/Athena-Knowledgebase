package de.tudarmstadt.informatik.ukp.athenakp.api.ast;

/**
 * Represents a numeric value (whole number)
 */
public class NumberNode extends BaseNode {
	private int val;

	/**
	 * @see {@link BaseNode#BaseNode(int) BaseNode}
	 */
	public NumberNode(int tokenIndex) {
		super(tokenIndex);
	}

	/**
	 * @return This node's value
	 */
	public int getValue() {
		return val;
	}

	/**
	 * Sets this node's value
	 * @param val The value to set
	 */
	public void setValue(int val) {
		this.val = val;
	}

	@Override
	public String toString() {
		return "<" + tokenIndex +">" + val;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && obj instanceof NumberNode && val == ((NumberNode)obj).val;
	}
}
