package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access;

import java.util.List;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Session;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.SessionCategory;

/**
 * @author Daniel Lehmann
 */
public interface SessionCommonAccess extends CommonAccess<Session> {
	/**
	 * Get all sessions with specified session id.
	 * @param id The session's id.
	 * @return A List of all sessions with the specified session id.
	 */
	public List<Session> getBySessionId(Long id);

	/**
	 * Get all session with specified start time.
	 * @param year The session's start year.
	 * @param month The session's start month.
	 * @param day The session's start day.
	 * @param hour The session's start hour.
	 * @param minute The session's start minute.
	 * @return A List of all sessions with the specified end time.
	 */
	public List<Session> getByStartTime(Integer year, Integer month, Integer day, Integer hour, Integer minute);

	/**
	 * Get all session with specified end time.
	 * @param year The session's start year.
	 * @param month The session's start month.
	 * @param day The session's start day.
	 * @param hour The session's end hour.
	 * @param minute The session's end minute.
	 * @return A List of all sessions with the specified end time.
	 */
	public List<Session> getByEndTime(Integer year, Integer month, Integer day, Integer hour, Integer minute);

	/**
	 * Get all sessions with specified place.
	 * @param place The session's place.
	 * @return A List of all sessions with the specified place.
	 */
	public List<Session> getByPlace(String place);

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
	 * Get all sessions with specified category.
	 * @param category The session's category.
	 * @return A List of all sessions with the specified category.
	 */
	public List<Session> getByCategory(SessionCategory category);
}
