package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access;

import java.util.List;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Conference;

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
	 * @param personID The conference's author's ID.
	 * @return A List of all conferences with the specified author.
	 */
	public List<Conference> getByAuthor(long personID);

	/**
	 * Get all conferences with specified paper.
	 * @param paperID The conference's paper's ID.
	 * @return A List of all conferences with the specified paper.
	 */
	public List<Conference> getByPaper(long paperID);

	/**
	 * Get all conferences in the specified country
	 * @param country The conference's country
	 * @return A list of all conferences in the specified country
	 */
	public List<Conference> getByCountry(String country);

	/**
	 * Get all conferences in the specified city
	 * @param city The conference's city
	 * @return A list of all conferences in the specified city
	 */
	public List<Conference> getByCity(String city);
}
