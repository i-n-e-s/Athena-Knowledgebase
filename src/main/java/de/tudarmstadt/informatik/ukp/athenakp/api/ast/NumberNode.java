package de.tudarmstadt.informatik.ukp.athenakp.api.ast;

/**
 * Represents a numeric value (whole number)
 */
public class NumberNode extends BaseNode {
	private int number;

	/**
	 * @see {@link BaseNode#BaseNode(int) BaseNode}
	 */
	public NumberNode(int tokenIndex) {
		super(tokenIndex);
	}

	/**
	 * @return This node's value
	 */
	public int getNumber() {
		return number;
	}

	/**
	 * Sets this node's value
	 * @param val The value to set
	 */
	public void setNumber(int val) {
		this.number = val;
	}

	@Override
	public String toString() {
		return "<" + tokenIndex +">" + number;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && obj instanceof NumberNode && number == ((NumberNode)obj).number;
	}
}
