package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access.PersonCommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Person;

/**
 * Implementation of PersonCommonAccess with Hibernate.
 *
 * @author Tristan Wettich, Daniel Lehmann
 */
@Deprecated
public class PersonHibernateAccess implements PersonCommonAccess {

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
		return getBy("birthdate", HibernateUtils.toTimestamp(year, month, day));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Person> getByObit(Integer year, Integer month, Integer day) {
		return getBy("obit", HibernateUtils.toTimestamp(year, month, day));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Person> getByInstitutionID(long institutionID) { //TODO implement this
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
	 * Common code used by all get methods above
	 * @param name The name of the column to restrict
	 * @param value The value to restrict the selection to
	 * @return A List of all persons with the given restriction
	 */
	private List<Person> getBy(String name, Object value) {
		Session session = HibernateUtils.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(Person.class);
		criteria.add(Restrictions.eq(name, value));
		List<Person> result = criteria.list();
		session.close();
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Person data) {
		Session session = HibernateUtils.getSessionFactory().openSession();
		session.beginTransaction();
		session.save(data);
		session.getTransaction().commit();
		session.close();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(Person data) {
		Session session = HibernateUtils.getSessionFactory().openSession();
		session.beginTransaction();
		session.update(data);
		session.getTransaction().commit();
		session.close();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(Person data) {
		Session session = HibernateUtils.getSessionFactory().openSession();
		session.beginTransaction();
		session.remove(data);
		session.getTransaction().commit();
		session.close();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Person> get() {
		Session session = HibernateUtils.getSessionFactory().openSession();
		List<Person> result = session.createCriteria(Person.class).list();
		session.close();
		return result;
	}
}
