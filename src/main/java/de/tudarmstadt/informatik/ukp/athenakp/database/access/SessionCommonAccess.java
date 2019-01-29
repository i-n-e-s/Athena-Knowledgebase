package de.tudarmstadt.informatik.ukp.athenakp.database.access;

import java.util.List;

import de.tudarmstadt.informatik.ukp.athenakp.database.models.Session;

/**
 * @author Daniel Lehmann
 */
public interface SessionCommonAccess extends CommonAccess<Session> {
	/**
	 * Get all sessions with specified session id.
	 * @param id The session's id.
	 * @return A List of all sessions with the specified session id.
	 */
	public List<Session> getById(Long id);

	/**
	 * Get all sessions with specified title.
	 * @param title The session's title.
	 * @return A List of all sessions with the specified title.
	 */
	public List<Session> getByTitle(String title);

	/**
	 * Get all sessions with specified description.
	 * @param description The session's description.
	 * @return A List of all sessions with the specified description.
	 */
	public List<Session> getByDescription(String description);

	/**
	 * Get all sessions with specified place.
	 * @param place The session's place.
	 * @return A List of all sessions with the specified place.
	 */
	public List<Session> getByPlace(String place);
}
