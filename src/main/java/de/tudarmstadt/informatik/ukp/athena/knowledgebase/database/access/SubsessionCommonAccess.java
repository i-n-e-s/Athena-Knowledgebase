package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access;

import java.util.List;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Subsession;

/**
 * @author Daniel Lehmann
 */
public interface SubsessionCommonAccess extends CommonAccess<Subsession> {
	/**
	 * Get all subsessions with specified subsession id.
	 * @param id The subsession's id.
	 * @return A List of all subsessions with the specified subsession id.
	 */
	public List<Subsession> getById(Long id);

	/**
	 * Get all subsessions with specified start time.
	 * @param year The subsession's start year.
	 * @param month The subsession's start month.
	 * @param day The subsession's start day.
	 * @param hour The subsession's start hour.
	 * @param minute The subsession's start minute.
	 * @return A List of all subsessions with the specified end time.
	 */
	public List<Subsession> getByStartTime(Integer year, Integer month, Integer day, Integer hour, Integer minute);

	/**
	 * Get all subsessions with specified end time.
	 * @param year The subsession's start year.
	 * @param month The subsession's start month.
	 * @param day The subsession's start day.
	 * @param hour The subsession's end hour.
	 * @param minute The subsession's end minute.
	 * @return A List of all subsessions with the specified end time.
	 */
	public List<Subsession> getByEndTime(Integer year, Integer month, Integer day, Integer hour, Integer minute);

	/**
	 * Get all subsessions with specified title.
	 * @param title The subsession's title.
	 * @return A List of all subsessions with the specified title.
	 */
	public List<Subsession> getByTitle(String title);

	/**
	 * Get all subsessions with specified description.
	 * @param description The subsession's description.
	 * @return A List of all subsessions with the specified description.
	 */
	public List<Subsession> getByDescription(String description);
}
