package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access.CommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Person;

public class PersonJPAAccess implements CommonAccess<Person> {

	/**
	 * {@inheritDoc}
	 */
	@Override
	/**
	 * Looks for DB entries with matching SemanticScholar ID
	 *
	 * @author Philipp Emmer
	 * @param semanticScholarID The semanticScholarID of the wanted person object to search
	 * @return DB Entry of Person with matching S2ID or null
	 */
	public Person getBySemanticScholarID( String semanticScholarID ) {
		List<Person> matches = null;
		//1. Try to find matching SemanticScholarID
		if( semanticScholarID != null ) {
			matches = getBy("semanticScholarID", semanticScholarID);
		}
		//Return first result of author with matching S2ID
		if( matches != null && matches.size() > 0 ) { return matches.get(0); }
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
}
