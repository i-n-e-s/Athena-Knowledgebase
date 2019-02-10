package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access;

import java.util.List;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Institution;

/**
 * @author Daniel Lehmann
 */
public interface InstitutionCommonAccess extends CommonAccess<Institution> {
	/**
	 * Get all institutions with specified institution id.
	 * @param id The institution's id.
	 * @return A List of all institutions with the specified institution id.
	 */
	public List<Institution> getByInstitutionID(Long id);

	/**
	 * Get all institutions with specified name.
	 * @param name The institution's name.
	 * @return A List of all institutions with the specified institution name.
	 */
	public List<Institution> getByName(String name);

	/**
	 * Get all institutions with institution person.
	 * @param personID The institution's person's ID.
	 * @return A List of all institutions with the specified person.
	 */
	public List<Institution> getByPersonID(long personID);
}
