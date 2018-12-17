package de.tudarmstadt.informatik.ukp.athenakp.database.access;

import java.util.List;

import de.tudarmstadt.informatik.ukp.athenakp.database.models.Workshop;

/**
 * @author Daniel Lehmann
 */
public interface WorkshopCommonAccess extends CommonAccess<Workshop> {
	/**
	 * Get all workshops with specified workshop id.
	 * @param id The workshop's id.
	 * @return A List of all workshops with the specified workshop id.
	 */
	public List<Workshop> getById(Long id);

	/**
	 * Get all workshops with specified conference.
	 * @param conference The workshop's conference's name.
	 * @return A List of all workshops with the specified conference.
	 */
	public List<Workshop> getByConference(String conference);

	/**
	 * Get all workshops with specified start date.
	 * @param year The conference's start year.
	 * @param month The conference's start month.
	 * @param day The conference's start day.
	 * @return A List of all workshops with the specified start date.
	 */
	public List<Workshop> getByDate(Integer year, Integer month, Integer day);

	/**
	 * Get all workshops with specified start time.
	 * @param hour The conference's start hour.
	 * @param minute The conference's start minute.
	 * @return A List of all workshops with the specified end time.
	 */
	public List<Workshop> getByStartTime(Integer hour, Integer minute);

	/**
	 * Get all workshops with specified end time.
	 * @param hour The conference's end hour.
	 * @param minute The conference's end minute.
	 * @return A List of all workshops with the specified end time.
	 */
	public List<Workshop> getByEndTime(Integer hour, Integer minute);

	/**
	 * Get all workshops with specified title.
	 * @param title The workshop's title.
	 * @return A List of all workshops with the specified title.
	 */
	public List<Workshop> getByTitle(String title);

	/**
	 * Get all workshops with specified description.
	 * @param description The workshop's description.
	 * @return A List of all workshops with the specified description.
	 */
	public List<Workshop> getByDescription(String description);
}
