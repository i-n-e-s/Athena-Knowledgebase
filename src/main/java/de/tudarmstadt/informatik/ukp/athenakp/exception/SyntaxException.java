package de.tudarmstadt.informatik.ukp.athenakp.exception;

public class SyntaxException extends Exception {
	/**
	 * @param index The point in the request at which the error occured
	 * @param actual The actual token at the index
	 */
	public SyntaxException(int index, String actual) {
		super("Syntax error in request index " + index + ": " + actual);
	}
}
