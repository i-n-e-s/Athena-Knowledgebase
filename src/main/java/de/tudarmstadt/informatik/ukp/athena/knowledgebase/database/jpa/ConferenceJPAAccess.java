package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;

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
	public void delete(Conference data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		entityManager.remove(data);
		entityManager.getTransaction().commit();
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
}
