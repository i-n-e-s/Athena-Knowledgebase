package de.tudarmstadt.informatik.ukp.athenakp.api;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.tudarmstadt.informatik.ukp.athenakp.database.hibernate.InstitutionHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Institution;

/**
 * Serves as a REST API definition for accessing institutions. Any mapping defined by a
 * method in this class has to be prepended with "/institutions" since this class is annotated
 * with a RequestMapping.
 * @author Daniel Lehmann
 */
@RestController
@RequestMapping("/institutions")
public class InstitutionController {
	private final InstitutionHibernateAccess access = new InstitutionHibernateAccess();

	/**
	 * @return All institutions in the database
	 */
	@RequestMapping("") //default
	public List<Institution> getAllInstitutions() {
		return access.get();
	}

	/**
	 * @param value The institution ID
	 * @return The institutions with the specified id, if existing
	 */
	@RequestMapping("/byInstitutionID/{value}")
	public List<Institution> byInstitutionID(@PathVariable("value")Long value) {
		return access.getByInstitutionID(value);
	}

	/**
	 * @param value The institution's name
	 * @return The institutions with the specified name, if existing
	 */
	@RequestMapping("/byName/{value}")
	public List<Institution> byName(@PathVariable("value")String value) {
		return access.getByName(value);
	}

	/**
	 * @param value The name of a person that can be part of an institution
	 * @return The institutions where the specified person works, if existing
	 */
	@RequestMapping("/byPerson/{value}") //TODO: perhaps arguments for finer control?
	public List<Institution> byPerson(@PathVariable("value")Long value) {
		return access.getByPersonID(value);
	}
}
