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
		String result = "<0>"; //the index of this kind of node is always 0

		if(function != RequestFunction.NONE) //if there is a request function, it has to be added infront of the actual request
			result += "/" + function.name().toLowerCase(); //the name of the enum value is what gets written infront of the request by the user

		for(RequestHierarchyNode node : hierarchy) { //build the rest of the request
			result += node.toString();
		}

		return result;
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && obj instanceof RequestNode && ((RequestNode)obj).function == function && hierarchy.equals(((RequestNode)obj).hierarchy);
	}
}
