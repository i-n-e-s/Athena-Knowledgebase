package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.CommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Person;

public class PersonJPAAccess implements CommonAccess<Person> {
	private static Logger logger = LogManager.getLogger(PersonJPAAccess.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Person data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		EntityTransaction trans = entityManager.getTransaction();
		if(!trans.isActive()) entityManager.getTransaction().begin();
		try {
			entityManager.persist(data);
		}catch(EntityExistsException e) { //branch not tested because exception shouldn't be thrown again just so junit can test for it
			logger.warn("{} already exists in the database. Maybe try update", data.getID());
		}
	}

	@Override
	public void commitChanges(Person data){
		EntityManager entityManager = PersistenceManager.getEntityManager();
		EntityTransaction trans = entityManager.getTransaction();
		if(!trans.isActive()) entityManager.getTransaction().begin();
		entityManager.getTransaction().commit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(Person data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		EntityTransaction trans = entityManager.getTransaction();
		if(!trans.isActive()) entityManager.getTransaction().begin();
		entityManager.remove(data);
		entityManager.getTransaction().commit();
	}

	@Override
	public boolean alreadyExists(String identifier){
		String query = "SELECT p FROM person p WHERE p.fullname = '"+identifier.replace("'","''") + "'";
		EntityManager entityManager = PersistenceManager.getEntityManager();
		//Execute query
		List<Person> matches = entityManager.createQuery(query).getResultList();
		//If results are found, return true.
		return (matches.size() > 0) ? true : false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Person> get() {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		List<Person> result = entityManager.createQuery("FROM Person").getResultList();
		System.out.println("Hello again");
		return result;
	}

	/**
	 * Finds a matching DB entry by the attributes of a given person object, null is seen as wildcard
	 * If no attribute is specified, return null
	 * Currently only uses (decreasing priority): S2ID, Name
	 * If no entry with matching S2ID AND name is found, look for matching S2ID, if nothing found either look for name, etc
	 * @param toFind Person object to get the search constraints from
	 * @return An object from the DB with matching attributes
	 */
	public List<Person> getByKnownAttributes(Person toFind) {
		//1. Get entityManager
		EntityManager entityManager = PersistenceManager.getEntityManager();

		//2. Build JPQL query string
		String query = "SELECT c FROM Person c WHERE ";
		boolean addedConstraint = false;
		if( toFind.getSemanticScholarID() != null ) {	//if s2id is known, add it to the query phrase
			query = query + "c.semanticScholarID LIKE '"+toFind.getSemanticScholarID() + "'";
			addedConstraint = true;
		}
		if ( toFind.getFullName() != null && toFind.getFullName() != "" ) {	//if name is known, add it to query phrase
			if (addedConstraint) { query = query + " and "; }
			query = query + "c.fullName = '"+toFind.getFullName().replace("'", "''") + "'";
			
			addedConstraint = true;
		}

		if ( !addedConstraint ) { return null; }	//if no attributes added to searchquery, return null
		logger.info(query);

		//3. Execute query
		List<Person> result = entityManager.createQuery(query).getResultList();

		//4. If results are found, return them
		if( result.size() > 0 ) { return result; }

		//5. If not, repeat search, but only use s2id
		if( toFind.getSemanticScholarID() != null ) {
			query = "SELECT c FROM Person c WHERE c.semanticScholarID LIKE '"+toFind.getSemanticScholarID() + "'";
			result = entityManager.createQuery(query).getResultList();
			if( result.size() > 0 ) { return result; }		//if search delivered results, break up and return them
		}

		//6. If still no results found, repeat search but use full name as only search filter
		if ( toFind.getFullName() != null && toFind.getFullName() != "" ) {
			query = "SELECT c FROM Person c WHERE c.fullName = '"+toFind.getFullName().replace("'", "''") + "'";
			result = entityManager.createQuery(query).getResultList();
			if( result.size() > 0 ) { return result; }		///if search delivered results, break up and return them
		}
 
		//7. If no search succeeded, return null
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

		//Build JPQL query
		String query = "SELECT c FROM Person c WHERE c.semanticScholarID = '"+semanticScholarID.replace("'","''") + "'";
		logger.info(query);
		EntityManager entityManager = PersistenceManager.getEntityManager();

		//Execute query
		List<Person> matches = entityManager.createQuery(query).getResultList();

		//If results are found, return them. Otherwise return null
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

		//Build query string
		String query = "SELECT c FROM Person c WHERE c.fullName = '"+name.replace("'","''") + "'";
		logger.info(query);
		EntityManager entityManager = PersistenceManager.getEntityManager();

		//Execute query
		List<Person> matches = entityManager.createQuery(query).getResultList();

		//If results are found, return them. Otherwise return null
		return (matches.size() > 0) ? matches.get(0) : null;
	}
	
}
