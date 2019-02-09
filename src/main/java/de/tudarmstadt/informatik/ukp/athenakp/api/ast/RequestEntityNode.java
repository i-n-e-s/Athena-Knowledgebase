package de.tudarmstadt.informatik.ukp.athenakp.api.ast;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents attribute information about a specific entity (the parts separated by $)
 */
public class RequestEntityNode extends BaseNode {
	private StringNode entityName;
	private final List<AttributeNode> attributes = new ArrayList<>();

	/**
	 * @see {@link BaseNode#BaseNode(int) BaseNode}
	 */
	public RequestEntityNode(int tokenIndex) {
		super(tokenIndex);
	}

	/**
	 * Gets the node for the name of the entity that gets specified here
	 * @return The node for the name of the entity that gets specified here
	 */
	public StringNode getEntityName() {
		return entityName;
	}

	/**
	 * Sets the node for the name of the entity that gets specified here
	 * @param node The name to set in this node
	 */
	public void setEntityName(StringNode node) {
		this.entityName = node;
	}

	/**
	 * Adds an attribute node to this node
	 * @param node The node to add
	 */
	public void addAttributeNode(AttributeNode node) {
		attributes.add(node);
	}

	/**
	 * @return The attributes of this node
	 */
	public List<AttributeNode> getAttributes() {
		return attributes;
	}

	@Override
	public String toString() {
		String result = "<" + tokenIndex +">" + entityName.toString() + ":";

		for(AttributeNode node : attributes) {
			result += node.toString() + "&";
		}

		return result.substring(0, result.length() - 1); //remove last &
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && obj instanceof RequestEntityNode && entityName.equals(((RequestEntityNode)obj).entityName) && attributes.equals(((RequestEntityNode)obj).attributes);
	}
}
