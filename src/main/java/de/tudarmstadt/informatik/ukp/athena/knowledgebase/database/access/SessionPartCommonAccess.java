package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access;

import java.util.List;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.SessionPart;

/**
 * @author Daniel Lehmann
 */
public interface SessionPartCommonAccess extends CommonAccess<SessionPart> {
	/**
	 * Get all session parts with specified session part id.
	 * @param id The session part's id.
	 * @return A list of all session parts with the specified session part id.
	 */
	public List<SessionPart> getBySessionPartId(Long id);

	/**
	 * Get all session parts with specified title.
	 * @param title The session part's title.
	 * @return A list of all session parts with the specified title.
	 */
	public List<SessionPart> getByTitle(String title);

	/**
	 * Get all session parts with specified description.
	 * @param description The session part's description.
	 * @return A list of all session parts with the specified description.
	 */
	public List<SessionPart> getByDescription(String description);

	/**
	 * Get all session parts with specified place.
	 * @param place The session part's place.
	 * @return A list of all session parts with the specified place.
	 */
	public List<SessionPart> getByPlace(String place);
}
