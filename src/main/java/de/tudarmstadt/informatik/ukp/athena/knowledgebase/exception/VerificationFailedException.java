package de.tudarmstadt.informatik.ukp.athena.knowledgebase.exception;

/**
 * Used when the verification (correct attribute values) of an API request fails
 */
public class VerificationFailedException extends Exception {
	/**
	 * @param message The message to use as an API reply
	 */
	public VerificationFailedException(String message) {
		super(message);
	}
}
