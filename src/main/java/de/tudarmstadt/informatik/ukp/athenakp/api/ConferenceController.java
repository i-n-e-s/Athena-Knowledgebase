package de.tudarmstadt.informatik.ukp.athenakp.api;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import de.tudarmstadt.informatik.ukp.athenakp.database.access.hibernate.ConferenceHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Conference;

/**
 * @author Daniel Lehmann
 */
//@RestController disabled because conferences are not stored in the database yet
@RequestMapping("/conferences")
public class ConferenceController {
	private final ConferenceHibernateAccess access = new ConferenceHibernateAccess();

	@RequestMapping("") //default
	public List<Conference> getAllConferences() {
		return access.get();
	}

	@RequestMapping("/byName/{value}")
	public List<Conference> byName(@PathVariable("value")String value) {
		return access.getByName(value);
	}

	@RequestMapping("/byStartDate/{year}/{month}/{day}")
	public List<Conference> byStartDate(@PathVariable("year")Integer year, @PathVariable("month")Integer month, @PathVariable("day")Integer day) {
		return access.getByStartDate(year, month, day);
	}

	@RequestMapping("/byEndDate/{year}/{month}/{day}")
	public List<Conference> byEndDate(@PathVariable("year")Integer year, @PathVariable("month")Integer month, @PathVariable("day")Integer day) {
		return access.getByEndDate(year, month, day);
	}

	@RequestMapping("/byAuthor/{value}")
	public List<Conference> byAuthor(@PathVariable("value")String value) {
		return access.getByAuthor(value);
	}

	@RequestMapping("/byPaper/{value}")
	public List<Conference> byPaper(@PathVariable("value")String value) {
		return access.getByPaper(value);
	}
}
