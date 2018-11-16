package de.tudarmstadt.informatik.ukp.athenakp.api;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.tudarmstadt.informatik.ukp.athenakp.database.access.PersonHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Person;

/**
 * @author Daniel Lehmann
 */
@RestController
@RequestMapping("/persons")
public class PersonController
{
	private final PersonHibernateAccess access = new PersonHibernateAccess();

	@RequestMapping("") //default
	public List<Person> getAllPersons()
	{
		return access.get();
	}

	@RequestMapping("/byPersonID/{value}")
	public List<Person> byPersonID(@PathVariable("value")Long value)
	{
		return access.getByPersonID(value);
	}

	@RequestMapping("/byPrefix/{value}")
	public List<Person> byPrefix(@PathVariable("value")String value)
	{
		return access.getByPrefix(value);
	}

	@RequestMapping("/byFirstName/{value}")
	public List<Person> byFirstName(@PathVariable("value")String value)
	{
		return access.getByFirstName(value);
	}

	@RequestMapping("/byMiddleName/{value}")
	public List<Person> byMiddleName(@PathVariable("value")String value)
	{
		return access.getByMiddleName(value);
	}

	@RequestMapping("/byLastName/{value}")
	public List<Person> byLastName(@PathVariable("value")String value)
	{
		return access.getByLastName(value);
	}

	@RequestMapping("/byBirthdate/{year}/{month}/{day}")
	public List<Person> byBirthdate(@PathVariable("year")Integer year, @PathVariable("month")Integer month, @PathVariable("day")Integer day)
	{
		return access.getByBirthdate(year, month, day);
	}

	@RequestMapping("/byObit/{year}/{month}/{day}")
	public List<Person> byObit(@PathVariable("year")Integer year, @PathVariable("month")Integer month, @PathVariable("day")Integer day)
	{
		return access.getByObit(year, month, day);
	}

	@RequestMapping("/byInstitution/{value}")
	public List<Person> byInstitution(@PathVariable("value")String value)
	{
		return access.getByInstitutionID(value);
	}
}
