package de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the complete request
 */
public class RequestNode extends BaseNode {
	/**Contains all entities incl. their attributes*/
	private final List<RequestHierarchyNode> hierarchy = new ArrayList<>();
	private boolean isCountFunction = false;

	/**
	 * @see {@link BaseNode#BaseNode(int) BaseNode}
	 */
	public RequestNode(int tokenIndex) {
		super(tokenIndex);
	}

	/**
	 * Adds a hierarchy node to this node
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

	/**
	 * Sets whether this node is a count function (/count/)
	 * @param isCountFunction true if this node is a count function, false otherwhise
	 */
	public void setIsCountFunction(boolean isCountFunction) {
		this.isCountFunction = isCountFunction;
	}

	/**
	 * @return true if this hierarchy node is a count function (/count/), false otherwhise
	 */
	public boolean isCountFunction() {
		return isCountFunction;
	}

	@Override
	public String toString() {
		String result = "<0>";

		if(isCountFunction)
			result += "/count";

		for(RequestHierarchyNode node : hierarchy) {
			result += node.toString();
		}

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && obj instanceof RequestNode && ((RequestNode)obj).isCountFunction == isCountFunction && hierarchy.equals(((RequestNode)obj).hierarchy);
	}
}
