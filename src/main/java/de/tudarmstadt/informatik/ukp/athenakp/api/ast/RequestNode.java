package de.tudarmstadt.informatik.ukp.athenakp.api.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the complete request
 */
public class RequestNode extends BaseNode {
	/**Join tables from left to right*/ //TODO: does this make sense with more than two '$'?
	private final List<RequestJoinNode> joins = new ArrayList<>();

	/**
	 * @see {@link BaseNode#BaseNode(int) BaseNode}
	 */
	public RequestNode(int tokenIndex) {
		super(tokenIndex);
	}

	/**
	 * Adds a new node to join to the list
	 * @param node The node to add
	 */
	public void addJoin(RequestJoinNode node) {
		joins.add(node);
	}

	@Override
	public String toString() {
		String result = "<" + tokenIndex +">" + "/";

		for(RequestJoinNode node : joins) {
			result += node.toString() + "$";
		}

		return result.substring(0, result.length() - 1); //remove last $
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && obj instanceof RequestNode && joins.equals(((RequestNode)obj).joins);
	}
}
