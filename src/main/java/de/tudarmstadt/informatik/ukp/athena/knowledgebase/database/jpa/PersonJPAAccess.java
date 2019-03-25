package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access.CommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Person;

public class PersonJPAAccess implements CommonAccess<Person> {
	private static Logger logger = LogManager.getLogger(PersonJPAAccess.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Person data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		try {
			entityManager.persist(data);
		}catch(EntityExistsException e) { //branch not tested because exception shouldn't be thrown again just so junit can test for it
			logger.warn("{} already exists in the database. Maybe try update", data.getID());
		}
		entityManager.getTransaction().commit();
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
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Person> get() {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		List<Person> result = entityManager.createQuery("FROM Person").getResultList();
		return result;
	}

	/**
	 * Finds a matching DB entry by the attributes of a given person object, null is seen as wildcard
	 * Currently only uses (decreasing priority): S2ID, Name
	 * If no entry with matching S2ID AND name is found, look for matching S2ID, if nothing found either look for name, etc
	 * @param toFind Person object to get the search constraints from
	 * @return An object from the DB with matching attributes
	 */
	public List<Person> getByKnownAttributes(Person toFind) {

		EntityManager entityManager = PersistenceManager.getEntityManager();
		String query = "SELECT c FROM Person c WHERE ";
		boolean addedConstraint = false;
		if( toFind.getSemanticScholarID() != null ) {
			query = query + "c.semanticScholarID LIKE '"+toFind.getSemanticScholarID() + "'";
			addedConstraint = true;
		}
		if ( toFind.getFullName() != null && toFind.getFullName() != "" ) {
			if (addedConstraint) { query = query + " and "; }
			query = query + "c.fullName = '"+toFind.getFullName().replace("'", "\\'") + "'";
			addedConstraint = true;
		}

		logger.info(query);

		List<Person> result = entityManager.createQuery(query).getResultList();

		if( result.size() > 0 ) { return result; }
		if( toFind.getSemanticScholarID() != null ) {
			query = "SELECT c FROM Person c WHERE c.semanticScholarID LIKE '"+toFind.getSemanticScholarID() + "'";
			result = entityManager.createQuery(query).getResultList();
			if( result.size() > 0 ) { return result; }
		}
		if ( toFind.getFullName() != null && toFind.getFullName() != "" ) {
			query = "SELECT c FROM Person c WHERE c.fullName = '"+toFind.getFullName().replace("'", "\\'") + "'";
			result = entityManager.createQuery(query).getResultList();
			if( result.size() > 0 ) { return result; }
		}
		return null;
	}

	/**
	 * Looks for DB entries with matching Semantic Scholar ID
	 *
	 * @author Philipp Emmer
	 * @param semanticScholarID The Semantic Scholar ID of the wanted person object to search
	 * @return DB entry of person with matching S2ID, null if not found
	 */
	public Person getBySemanticScholarID( String semanticScholarID ) {

		if( semanticScholarID == null ) { return null; }
		String query = "SELECT c FROM Person c WHERE c.semanticScholarID = '"+semanticScholarID.replace("'","''") + "'";
		logger.info(query);
		EntityManager entityManager = PersistenceManager.getEntityManager();
		List<Person> matches = entityManager.createQuery(query).getResultList();

		return (matches.size() > 0) ? matches.get(0) : null;
	}

	/**
	 * Looks for DB entries with matching name
	 *
	 * @author Philipp Emmer
	 * @param name The name of the wanted person object to search
	 * @return DB entry of person with matching S2ID or null
	 */
	public Person getByFullName( String name ) {
		if( name == null ) { return null; }
		String query = "SELECT c FROM Person c WHERE c.fullName = '"+name.replace("'","''") + "'";
		logger.info(query);
		EntityManager entityManager = PersistenceManager.getEntityManager();
		List<Person> matches = entityManager.createQuery(query).getResultList();

		return (matches.size() > 0) ? matches.get(0) : null;
	}
}
