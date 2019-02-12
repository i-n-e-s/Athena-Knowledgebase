package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access.PersonCommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Person;

public class PersonJPAAccess implements PersonCommonAccess {

	/**
	 * Common code used by all get methods which filter by simple column values.
	 * @param name The name of the column to restrict
	 * @param value The value to restrict the selection to
	 * @return A List of all persons with the given restriction
	 */
	private List<Person> getBy(String name, Object value) {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Person> criteriaQuery = builder.createQuery(Person.class);
		Root<Person> root = criteriaQuery.from(Person.class);
		criteriaQuery
		.select(root)
		.where(builder.equal(root.get(name), value));
		List<Person> result = entityManager.createQuery(criteriaQuery).getResultList();
		entityManager.close();
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Person> getByPersonID(Long personID) {
		return getBy("personID", personID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Person> getByPrefix(String prefix) {
		return getBy("prefix", prefix);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Person> getByFullName(String fullName) {
		return getBy("fullName", fullName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Person> getByBirthdate(Integer year, Integer month, Integer day) {
		return getBy("birthdate", Timestamp.valueOf(String.format("%s-%s-%s 00:00:00", year, month, day)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Person> getByObit(Integer year, Integer month, Integer day) {
		return getBy("obit", Timestamp.valueOf(String.format("%s-%s-%s 00:00:00", year, month, day)));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Person> getByInstitutionID(long institutionID) {
		return getBy("institutionID", institutionID);
	}

	/**
	 * Looks for equal attribute DB entries of Person and returns the matching Person Object
	 * If multiple Occurences are found in DB, return the first result
	 *
	 * @author Philipp Emmer
	 * @param personToFind The person object to search
	 * @return The first DB Entry of Person with matching Attributes or null
	 */
	public Person lookUpPerson( Person personToFind ) {
		List<Person> matches = null;
		//1. Try to find matching SemanticScholarID
		if( personToFind.getSemanticScholarID() != null ) {
			matches = getBy("semanticScholarID", personToFind.getSemanticScholarID());
		}
		//Return first result of author with matching S2ID
		if( matches != null && matches.size() > 0 ) { return matches.get(0); }

		//2. If no results, search for name
		matches = getByFullName(personToFind.getFullName());
		for ( Person namesake : matches ) {
			//Choose the first one with matching attributes
			if ( namesake.equalsNullAsWildcard(personToFind) ) { return namesake; }
		}

		//3. If nothing found, return null
		return null;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Person data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		entityManager.persist(data);
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(Person data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		entityManager.merge(data);
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(Person data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		entityManager.remove(data);
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Person> get() {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		List<Person> result = entityManager.createQuery("FROM Person").getResultList();
		entityManager.close();
		return result;
	}
}
