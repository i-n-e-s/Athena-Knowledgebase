package de.tudarmstadt.informatik.ukp.athena.knowledgebase.api;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access.ConferenceCommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.ConferenceJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Conference;

/**
 * Serves as a REST API definition for accessing conferences. Any mapping defined by a
 * method in this class has to be prepended with "/conferences" since this class is annotated
 * with a RequestMapping.
 * @author Daniel Lehmann
 */
@RestController
@RequestMapping("/conferences")
public class ConferenceController {
	private final ConferenceCommonAccess access = new ConferenceJPAAccess();

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
	 *
	 * @param value The country the conference is in
	 * @return	A List of conferences in the country
	 */
	@RequestMapping("/byCountry/{value}")
	public List<Conference> byCountry(@PathVariable("value")String value) { return access.getByCountry(value);
	}

	/**
	 * What about that share the same name? maybe disable or link with country?
	 * TODO: how to combine queries e.g. limit by both country and city with the hibernate access /not filtering locally
	 * @param value The city the conference is in
	 * @return	A list of conferences in the city
	 */
	@RequestMapping("/byCity/{value}")
	public List<Conference> byCity(@PathVariable("value")String value) { return access.getByCity(value);
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
	public List<Conference> byAuthor(@PathVariable("value")long value) {
		return access.getByAuthor(value);
	}

	/**
	 * @param value The name of a paper that was shown at a conference
	 * @return The conferences where the specified paper was shown at
	 */
	@RequestMapping("/byPaper/{value}") //TODO: perhaps arguments for finer control?
	public List<Conference> byPaper(@PathVariable("value")long value) {
		return access.getByPaper(value);
	}
}
