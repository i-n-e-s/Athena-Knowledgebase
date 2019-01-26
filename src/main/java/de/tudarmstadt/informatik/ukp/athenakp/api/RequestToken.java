package de.tudarmstadt.informatik.ukp.athenakp.api;

/**
 * Represents a token that is part of the request string
 */
public class RequestToken {
	/**The type of this token*/
	public final RequestTokenType type;
	/*The actual token as it was found in the request string**/
	public final String actual;

	public RequestToken(RequestTokenType type, String actual) {
		this.type = type;
		this.actual = actual;
	}

	@Override
	public String toString() {
		return "[" + type + ", " + actual +"]";
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof RequestToken && type == ((RequestToken)obj).type && actual.equals(((RequestToken)obj).actual);
	}

	public enum RequestTokenType {
		ATTR_EQ,
		ATTR_SEPERATOR,
		ATTR_SPECIFIER,
		ERROR,
		HIERARCHY_SEPERATOR,
		JOIN,
		NAME,
		NUMBER,
		SPACE;
	}
}
