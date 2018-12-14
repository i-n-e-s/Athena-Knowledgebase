package de.tudarmstadt.informatik.ukp.athenakp.api;

import java.util.List;
import java.util.Set;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.tudarmstadt.informatik.ukp.athenakp.database.hibernate.PersonHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Author;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Paper;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Person;

/**
 * Serves as a REST API definition for accessing persons and authors. Any mapping defined by a
 * method in this class has to be prepended with "/persons" since this class is annotated
 * with a RequestMapping.
 * @author Daniel Lehmann
 */
@RestController
@RequestMapping("/persons")
public class PersonController {
	private final PersonHibernateAccess access = new PersonHibernateAccess();

	/**
	 * @return All persons in the database
	 */
	@RequestMapping("") //default
	public List<Person> getAllPersons() {
		return access.get();
	}

	/**
	 * @param value The person ID
	 * @return The persons with the specified id, if existing
	 */
	@RequestMapping("/byPersonID/{value}")
	public List<Person> byPersonID(@PathVariable("value")Long value) {
		return access.getByPersonID(value);
	}

	/**
	 * @param value The person ID
	 * @return All papers that the person with the specified ID wrote, if any
	 */
	@RequestMapping("/byPersonID/{value}/getPapers")
	public Set<Paper> getAuthors(@PathVariable("value")Long value) {
		List<Person> persons = byPersonID(value);

		if(persons.size() > 0 && persons.get(0) instanceof Author)
			return ((Author)persons.get(0)).getPapers();
		else return null;
	}

	/**
	 * @param value The person's prefix (Prof., Dr., Ing., etc)
	 * @return All persons with the specified prefix
	 */
	@RequestMapping("/byPrefix/{value}")
	public List<Person> byPrefix(@PathVariable("value")String value) {
		return access.getByPrefix(value);
	}

	/**
	 * @param value The full name of the person
	 * @return All persons with the specified full name
	 */
	@RequestMapping("/byFullName/{value}")
	public List<Person> byFirstName(@PathVariable("value")String value) {
		return access.getByFullName(value);
	}

	/**
	 * @param year The year in which the person was born
	 * @param month The month in which the person was born
	 * @param day The day on which the person was born
	 * @return The persons with the specified birth date, if existing
	 */
	@RequestMapping("/byBirthdate/{year}/{month}/{day}")
	public List<Person> byBirthdate(@PathVariable("year")Integer year, @PathVariable("month")Integer month, @PathVariable("day")Integer day) {
		return access.getByBirthdate(year, month, day);
	}

	/**
	 * @param year The year in which the person died
	 * @param month The month in which the person died
	 * @param day The day on which the person died
	 * @return The persons with the specified day of death, if existing
	 */
	@RequestMapping("/byObit/{year}/{month}/{day}")
	public List<Person> byObit(@PathVariable("year")Integer year, @PathVariable("month")Integer month, @PathVariable("day")Integer day) {
		return access.getByObit(year, month, day);
	}

	/**
	 * @param value The name of the institution the person works at
	 * @return All persons working at the given institution
	 */
	@RequestMapping("/byInstitution/{value}") //TODO: perhaps arguments for finer control?
	public List<Person> byInstitution(@PathVariable("value")long value) {
		return access.getByInstitutionID(value);
	}
}
