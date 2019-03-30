package de.tudarmstadt.informatik.ukp.athena.knowledgebase.api.ast;

/**
 * Represents the parts of the request separated by /
 */
public class RequestHierarchyNode extends BaseNode {
	private RequestEntityNode entity;

	/**
	 * @see BaseNode#BaseNode(int)
	 */
	public RequestHierarchyNode(int tokenIndex) {
		super(tokenIndex);
	}

	/**
	 * Sets this node's entity
	 * @param entity The entity
	 */
	public void setEntity(RequestEntityNode entity) {
		this.entity = entity;
	}

	/**
	 * @return The entity including its attributes
	 */
	public RequestEntityNode getEntity() {
		return entity;
	}

	@Override
	public String toString() {
		if(entity == null)
			return "<" + tokenIndex +">/ NULL_ENTITY";
		else return "<" + tokenIndex +">/" + entity.toString();
	}

	@Override
	public boolean equals(Object obj) {
		return super.equals(obj) && obj instanceof RequestHierarchyNode && entity.equals(((RequestHierarchyNode)obj).entity);
	}
}
