package de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the complete request
 */
public class RequestNode extends BaseNode {
	/**Contains all entities incl. their attributes*/
	private final List<RequestHierarchyNode> hierarchy = new ArrayList<>();
	/**Can be added infront of a request*/
	private RequestFunction function = RequestFunction.NONE;

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
	 * Sets this hierarchy node's function
	 * @param function The function to use
	 */
	public void setFunction(RequestFunction function) {
		this.function = function;
	}

	/**
	 * @return true This hierarchy node's function
	 */
	public RequestFunction getFunction() {
		return function;
	}

	@Override
	public String toString() {
		String result = "<0>";

		if(function != RequestFunction.NONE)
			result += "/" + function.name().toLowerCase();

		for(RequestHierarchyNode node : hierarchy) {
			result += node.toString();
		}

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && obj instanceof RequestNode && ((RequestNode)obj).function == function && hierarchy.equals(((RequestNode)obj).hierarchy);
	}
}
