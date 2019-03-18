package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access.CommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Person;

public class PersonJPAAccess implements CommonAccess<Person> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Person data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		try {
			entityManager.persist(data);
		}catch(EntityExistsException e) {
			System.out.println(data.getID()+" already exists in the Database");
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
	 * Executes given JPQL query and returns results
	 *
	 */


	/**
	 * Finds a matching DB Entry by the attributes of a given Person Object, null is seen as wildcard
	 * Currently Only uses (decreasing priority): S2ID, Name
	 * @param toFind Person Object to get the search constraints from
	 * @return An Object from the DB with matching attributes
	 */
	public List<Person> getByKnownAttributes(Person toFind) {

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

		System.out.println(query);

		EntityManager entityManager = PersistenceManager.getEntityManager();
		List<Person> result = entityManager.createQuery(query).getResultList();
		return result;

	}

	/**
	 * Looks for DB entries with matching SemanticScholar ID
	 *
	 * @author Philipp Emmer
	 * @param semanticScholarID The semanticScholarID of the wanted person object to search
	 * @return DB Entry of Person with matching S2ID or null
	 */
	public Person getBySemanticScholarID( String semanticScholarID ) {

		if( semanticScholarID == null ) { return null; }
		String query = "SELECT c FROM Person c WHERE c.semanticScholarID = '"+semanticScholarID.replace("'","''") + "'";
		System.out.println(query);
		EntityManager entityManager = PersistenceManager.getEntityManager();
		List<Person> matches = entityManager.createQuery(query).getResultList();

		return (matches.size() > 0) ? matches.get(0) : null;
	}

	/**
	 * Looks for DB entries with matching name
	 *
	 * @author Philipp Emmer
	 * @param name The name of the wanted person object to search
	 * @return DB Entry of Person with matching S2ID or null
	 */
	public Person getByFullName( String name ) {
		if( name == null ) { return null; }
		String query = "SELECT c FROM Person c WHERE c.fullName = '"+name.replace("'","''") + "'";
		System.out.println(query);
		EntityManager entityManager = PersistenceManager.getEntityManager();
		List<Person> matches = entityManager.createQuery(query).getResultList();

		return (matches.size() > 0) ? matches.get(0) : null;
	}
}
