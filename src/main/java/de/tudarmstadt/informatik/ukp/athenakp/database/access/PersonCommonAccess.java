package de.tudarmstadt.informatik.ukp.athenakp.database.access;

import de.tudarmstadt.informatik.ukp.athenakp.database.models.Person;

import java.util.List;

/**
 * This interface is meant to hide the database layer. For any new use case involving
 * storing or querying persons this should be extended first.
 *
 * @author Tristan Wettich
 */
public interface PersonCommonAccess extends CommonAccess<Person> {

    /**
     * Get all persons with specified last name.
     * @param name The person's last name.
     * @return A List of all persons with the specified last name.
     */
    public List<Person> getByLastName(String name);

}
