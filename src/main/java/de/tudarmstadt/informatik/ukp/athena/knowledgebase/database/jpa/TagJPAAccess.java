package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.CommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Event;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Tag;

/**
 * @author Daniel Lehmann
 */
public class TagJPAAccess implements CommonAccess<Tag> {
	private static Logger logger = LogManager.getLogger(TagJPAAccess.class);

	@Override
	public void add(Tag data) {
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
	public void commitChanges(Tag data){
		EntityManager entityManager = PersistenceManager.getEntityManager();
		EntityTransaction trans = entityManager.getTransaction();
		if(!trans.isActive()) entityManager.getTransaction().begin();
		entityManager.getTransaction().commit();
	}

	@Override
	public boolean alreadyExists(String identifier){
		String query = "SELECT e FROM Tag e WHERE e.name = '"+identifier.replace("'","''") + "'";
		EntityManager entityManager = PersistenceManager.getEntityManager();

		//Execute query
		List<Event> matches = entityManager.createQuery(query).getResultList();

		//If results are found, return them. Otherwise return null
		return (matches.size() > 0) ? true : false;
	}

	public Tag getByName(String identifier){
		String query = "SELECT e FROM Tag e WHERE e.name = '"+identifier.replace("'","''") + "'";
		EntityManager entityManager = PersistenceManager.getEntityManager();

		//Execute query
		List<Tag> matches = entityManager.createQuery(query).getResultList();

		//If results are found, return the first. Otherwise return null
		return (matches.size() > 0) ? matches.get(0) : null;
	}


	@Override
	public void delete(Tag data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		EntityTransaction trans = entityManager.getTransaction();
		if(!trans.isActive()) entityManager.getTransaction().begin();
		entityManager.remove(data);
		entityManager.getTransaction().commit();
	}

	@Override
	public List<Tag> get() {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		List<Tag> result = entityManager.createQuery("FROM Tag").getResultList();
		return result;
	}
}
