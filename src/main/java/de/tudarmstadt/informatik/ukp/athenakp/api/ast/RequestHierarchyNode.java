package de.tudarmstadt.informatik.ukp.athenakp.api.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the parts of the request separated by /
 */
public class RequestHierarchyNode extends BaseNode {
	/**Join tables from left to right*/ //TODO: does this make sense with more than two '$'?
	private final List<RequestEntityNode> entities = new ArrayList<>();

	/**
	 * @see {@link BaseNode#BaseNode(int) BaseNode}
	 */
	public RequestHierarchyNode(int tokenIndex) {
		super(tokenIndex);
	}

	/**
	 * Adds a new entity node to join to the list
	 * @param node The node to add
	 */
	public void addEntity(RequestEntityNode node) {
		entities.add(node);
	}

	/**
	 * @return The parts of the request that were previously seperated by a $
	 */
	public List<RequestEntityNode> getEntities() {
		return entities;
	}

	@Override
	public String toString() {
		String result = "<" + tokenIndex +">/";

		for(RequestEntityNode node : entities) {
			result += node.toString() + "$";
		}

		return result.substring(0, result.length() - 1); //remove last $
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && obj instanceof RequestHierarchyNode && entities.equals(((RequestHierarchyNode)obj).entities);
	}
}
