package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.CommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Conference;

public class ConferenceJPAAccess implements CommonAccess<Conference> {
	private static Logger logger = LogManager.getLogger(ConferenceJPAAccess.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Conference data) {
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
	public void commitChanges(Conference data){
		EntityManager entityManager = PersistenceManager.getEntityManager();
		EntityTransaction trans = entityManager.getTransaction();
		if(!trans.isActive()) entityManager.getTransaction().begin();
		entityManager.getTransaction().commit();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(Conference data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		EntityTransaction trans = entityManager.getTransaction();
		if(!trans.isActive()) entityManager.getTransaction().begin();
		entityManager.remove(data);
		entityManager.getTransaction().commit();
	}

	@Override
	public boolean alreadyExists(String identifier){
		String query = "SELECT c FROM Conference c WHERE c.name = '"+identifier.replace("'","''") + "'";
		EntityManager entityManager = PersistenceManager.getEntityManager();

		//Execute query
		List<Conference> matches = entityManager.createQuery(query).getResultList();

		//If results are found, return them. Otherwise return null
		return (matches.size() > 0) ? true : false;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Conference> get() {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		List<Conference> result = entityManager.createQuery("FROM Conference").getResultList();
		return result;
	}

	public Conference getByName( String name ) {
		//Build query string

		String query = "SELECT c FROM Conference c WHERE c.name = '"+name.replace("'","''") + "'";
		System.out.println(query);
		EntityManager entityManager = PersistenceManager.getEntityManager();

		//Execute query
		List<Conference> matches = entityManager.createQuery(query).getResultList();

		//If results are found, return them. Otherwise return null
		return (matches.size() > 0) ? matches.get(0) : null;
	}

}
