package de.tudarmstadt.informatik.ukp.athenakp.api;

/**
 * Represents a token that is part of the request string
 */
public class RequestToken {
	/**The type of this token*/
	public final RequestTokenType type;
	/*The actual token as it was found in the request string**/
	public final String actual;
	public final int index;

	public RequestToken(RequestTokenType type, String actual) {
	public RequestToken(RequestTokenType type, String actual, int index) {
		this.type = type;
		this.actual = actual;
		this.index = index;
	}

	@Override
	public String toString() {
		return "[" + type + ", " + actual + ", " + index + "]";
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof RequestToken && type == ((RequestToken)obj).type && actual.equals(((RequestToken)obj).actual) && index == ((RequestToken)obj).index;
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
