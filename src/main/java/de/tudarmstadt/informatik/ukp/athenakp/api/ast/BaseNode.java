package de.tudarmstadt.informatik.ukp.athenakp.api.ast;

//TODO context analysis
public abstract class BaseNode {
	public final int tokenIndex;

	/**
	 * @param tokenIndex The index in the request string that the token represented by this node starts at (used for error handling)
	 */
	protected BaseNode(int tokenIndex) {
		this.tokenIndex = tokenIndex;
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof BaseNode && tokenIndex == ((BaseNode)obj).tokenIndex;
	}
}
