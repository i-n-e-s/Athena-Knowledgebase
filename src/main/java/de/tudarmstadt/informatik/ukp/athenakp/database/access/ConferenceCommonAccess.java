package de.tudarmstadt.informatik.ukp.athenakp.database.access;

import java.util.List;

import de.tudarmstadt.informatik.ukp.athenakp.database.models.Conference;

/**
 * @author Daniel Lehmann
 */
public interface ConferenceCommonAccess extends CommonAccess<Conference>
{
	/**
	 * Get all conferences with specified conference name.
	 * @param name The conference's name.
	 * @return A List of all conferences with the specified conference name.
	 */
	public List<Conference> getByName(String name);

	/**
	 * Get all conferences with specified start date.
	 * @param year The conference's start year.
	 * @param month The conference's start month.
	 * @param day The conference's start day.
	 * @return A List of all conferences with the specified start date.
	 */
	public List<Conference> getByStartDate(Integer year, Integer month, Integer day);

	/**
	 * Get all conferences with specified end date.
	 * @param year The conference's end year.
	 * @param month The conference's end month.
	 * @param day The conference's end day.
	 * @return A List of all conferences with the specified end date.
	 */
	public List<Conference> getByEndDate(Integer year, Integer month, Integer day);

	/**
	 * Get all conferences with specified author.
	 * @param author The conference's author.
	 * @return A List of all conferences with the specified author.
	 */
	public List<Conference> getByAuthor(String author);

	/**
	 * Get all conferences with specified paper.
	 * @param paper The conference's paper.
	 * @return A List of all conferences with the specified paper.
	 */
	public List<Conference> getByPaper(String paper);
}
