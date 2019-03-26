package de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast;

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
