package de.tudarmstadt.informatik.ukp.athenakp.api;

/**
 * Represents a token that is part of the request string
 */
public class RequestToken {
	public final RequestTokenType type;
	public final String actual;
	public final int index;

	/**
	 * @param type The type of this token
	 * @param actual The actual token as it was found in the request string
	 * @param index The index in the request string that this token starts at (used for error handling)
	 */
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
		ATTR_EQ, // =
		ATTR_SEPERATOR, // &
		ATTR_SPECIFIER, // :
		END, //the end of the world (well, more like the request)
		ERROR, //an error (duh!)
		HIERARCHY_SEPERATOR, // /
		NAME, //an entity or attribute name
		NUMBER, //a number (duh!)
		SPACE; // +
	}
}
