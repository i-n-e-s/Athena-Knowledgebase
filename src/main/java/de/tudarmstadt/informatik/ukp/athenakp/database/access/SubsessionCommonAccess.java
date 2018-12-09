package de.tudarmstadt.informatik.ukp.athenakp.database.access;

import java.util.List;

import de.tudarmstadt.informatik.ukp.athenakp.database.models.Subsession;

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
	 * @param hour The conference's start hour.
	 * @param minute The conference's start minute.
	 * @return A List of all subsessions with the specified end time.
	 */
	public List<Subsession> getByStartTime(Integer hour, Integer minute);

	/**
	 * Get all subsessions with specified end time.
	 * @param hour The conference's end hour.
	 * @param minute The conference's end minute.
	 * @return A List of all subsessions with the specified end time.
	 */
	public List<Subsession> getByEndTime(Integer hour, Integer minute);

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
