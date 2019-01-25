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
	 * @param conferenceName The workshop's conference's name.
	 * @return A List of all workshops with the specified conference.
	 */
	public List<Workshop> getByConferenceName(String conferenceName);

	/**
	 * Get all workshops with specified start time.
	 * @param year The workshop's start year.
	 * @param month The workshop's start month.
	 * @param day The workshop's start day.
	 * @param hour The workshop's start hour.
	 * @param minute The workshop's start minute.
	 * @return A List of all workshops with the specified end time.
	 */
	public List<Workshop> getByStartTime(Integer year, Integer month, Integer day, Integer hour, Integer minute);

	/**
	 * Get all workshops with specified end time.
	 * @param year The workshop's end year.
	 * @param month The workshop's end month.
	 * @param day The workshop's end day.
	 * @param hour The workshop's end hour.
	 * @param minute The workshop's end minute.
	 * @return A List of all workshops with the specified end time.
	 */
	public List<Workshop> getByEndTime(Integer year, Integer month, Integer day, Integer hour, Integer minute);

	/**
	 * Get all workshops with specified place.
	 * @param place The workshop's place.
	 * @return A List of all workshops with the specified place.
	 */
	public List<Workshop> getByPlace(String place);

	/**
	 * Get all workshops with specified title.
	 * @param title The workshop's title.
	 * @return A List of all workshops with the specified title.
	 */
	public List<Workshop> getByTitle(String title);

	/**
	 * Get all workshops with specified abbreviation.
	 * @param description The workshop's abbreviation.
	 * @return A List of all workshops with the specified abbreviation.
	 */
	public List<Workshop> getByAbbreviation(String abbreviation);
}
