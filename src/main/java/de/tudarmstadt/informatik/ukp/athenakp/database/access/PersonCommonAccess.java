package de.tudarmstadt.informatik.ukp.athenakp.database.access;

import java.util.List;

import de.tudarmstadt.informatik.ukp.athenakp.database.models.Person;

/**
 * This interface is meant to hide the database layer. For any new use case involving
 * storing or querying persons this should be extended first.
 *
 * @author Tristan Wettich
 */
public interface PersonCommonAccess extends CommonAccess<Person> {

	/**
	 * Get all persons with specified person id.
	 * @param personID The person's person id.
	 * @return A List of all persons with the specified person id.
	 */
	public List<Person> getByPersonID(Long personID);

	/**
	 * Get all persons with specified prefix.
	 * @param prefix The person's prefix.
	 * @return A List of all persons with the specified prefix.
	 */
	public List<Person> getByPrefix(String prefix);

	/**
	 * Get all persons with specified full Name.
	 * @param fullName The person's full name.
	 * @return A List of all persons with the specified full (complete) name.
	 */
	public List<Person> getByFullName(String fullName);

	/**
	 * Get all persons with specified birthdate.
	 * @param year The person's birthdate year.
	 * @param month The person's birthdate month.
	 * @param day The person's birthdate day.
	 * @return A List of all persons with the specified birthdate.
	 */
	public List<Person> getByBirthdate(Integer year, Integer month, Integer day);

	/**
	 * Get all persons with specified obit.
	 * @param year The person's birthdate year.
	 * @param month The person's birthdate month.
	 * @param day The person's birthdate day.
	 * @return A List of all persons with the specified obit.
	 */
	public List<Person> getByObit(Integer year, Integer month, Integer day);

	/**
	 * Get all persons with specified institution's id.
	 * @param institutionID The person's institution's id.
	 * @return A List of all persons with the specified institution's id.
	 */
	public List<Person> getByInstitutionID(String institutionID);
}
