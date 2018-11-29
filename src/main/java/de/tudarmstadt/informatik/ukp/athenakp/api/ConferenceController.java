package de.tudarmstadt.informatik.ukp.athenakp.api;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.tudarmstadt.informatik.ukp.athenakp.database.hibernate.ConferenceHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Conference;

/**
 * Serves as a REST API definition for accessing conferences . Any mapping defined by a
 * method in this class has to be prepended with "/conferences" since this class is annotated
 * with a RequestMapping.
 * @author Daniel Lehmann
 */
@RestController
@RequestMapping("/conferences")
public class ConferenceController {
	private final ConferenceHibernateAccess access = new ConferenceHibernateAccess();

	/**
	 * @return All conferences in the database
	 */
	@RequestMapping("") //default
	public List<Conference> getAllConferences() {
		return access.get();
	}

	/**
	 * @param value The conference name
	 * @return The conferences with the specified name, if existing
	 */
	@RequestMapping("/byName/{value}")
	public List<Conference> byName(@PathVariable("value")String value) {
		return access.getByName(value);
	}

	/**
	 * @param year The year in which the conference started
	 * @param month The month in which the conference started
	 * @param day The day on which the conference started
	 * @return The conferences with the specified start date, if existing
	 */
	@RequestMapping("/byStartDate/{year}/{month}/{day}")
	public List<Conference> byStartDate(@PathVariable("year")Integer year, @PathVariable("month")Integer month, @PathVariable("day")Integer day) {
		return access.getByStartDate(year, month, day);
	}

	/**
	 * @param year The year in which the conference ended
	 * @param month The month in which the conference ended
	 * @param day The day on which the conference ended
	 * @return The conferences with the specified end date, if existing
	 */
	@RequestMapping("/byEndDate/{year}/{month}/{day}")
	public List<Conference> byEndDate(@PathVariable("year")Integer year, @PathVariable("month")Integer month, @PathVariable("day")Integer day) {
		return access.getByEndDate(year, month, day);
	}

	/**
	 * @param value The name of an author that spoke at a conference
	 * @return The conferences where the specified author spoke at
	 */
	@RequestMapping("/byAuthor/{value}") //TODO: perhaps arguments for finer control?
	public List<Conference> byAuthor(@PathVariable("value")String value) {
		return access.getByAuthor(value);
	}

	/**
	 * @param value The name of a paper that was shown at a conference
	 * @return The conferences where the specified paper was shown at
	 */
	@RequestMapping("/byPaper/{value}") //TODO: perhaps arguments for finer control?
	public List<Conference> byPaper(@PathVariable("value")String value) {
		return access.getByPaper(value);
	}
}
