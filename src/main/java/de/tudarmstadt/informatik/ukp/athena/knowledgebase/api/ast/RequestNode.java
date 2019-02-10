package de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the complete request
 */
public class RequestNode extends BaseNode {
	/**Join tables from left to right*/ //TODO: does this make sense with more than two '$'?
	private final List<RequestHierarchyNode> hierarchy = new ArrayList<>();

	/**
	 * @see {@link BaseNode#BaseNode(int) BaseNode}
	 */
	public RequestNode(int tokenIndex) {
		super(tokenIndex);
	}

	/**
	 * Adds an attribute node to this node
	 * @param node The node to add
	 */
	public void addHierarchyNode(RequestHierarchyNode node) {
		hierarchy.add(node);
	}

	/**
	 * @return The parts of the request that were previously separated by /
	 */
	public List<RequestHierarchyNode> getHierarchy() {
		return hierarchy;
	}

	@Override
	public String toString() {
		String result = "<0>";

		for(RequestHierarchyNode node : hierarchy) {
			result += node.toString();
		}

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && obj instanceof RequestNode && hierarchy.equals(((RequestNode)obj).hierarchy);
	}
}
