package de.tudarmstadt.informatik.ukp.athenakp.api;

import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import de.tudarmstadt.informatik.ukp.athenakp.database.access.hibernate.InstitutionHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Institution;

/**
 * @author Daniel Lehmann
 */
@RestController
@RequestMapping("/institutions")
public class InstitutionController {
	private final InstitutionHibernateAccess access = new InstitutionHibernateAccess();

	@RequestMapping("") //default
	public List<Institution> getAllInstitutions() {
		return access.get();
	}

	@RequestMapping("/byInstitutionID/{value}")
	public List<Institution> byInstitutionID(@PathVariable("value")Long value) {
		return access.getByInstitutionID(value);
	}

	@RequestMapping("/byName/{value}")
	public List<Institution> byName(@PathVariable("value")String value) {
		return access.getByName(value);
	}

	@RequestMapping("/byPerson/{value}")
	public List<Institution> byPerson(@PathVariable("value")String value) {
		return access.getByPerson(value);
	}
}
