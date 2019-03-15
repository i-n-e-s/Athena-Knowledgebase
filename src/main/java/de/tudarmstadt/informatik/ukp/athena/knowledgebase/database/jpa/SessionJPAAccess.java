package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access.CommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Session;

/**
 * @author Daniel Lehmann
 */
public class SessionJPAAccess implements CommonAccess<Session> {
	private static Logger logger = LogManager.getLogger(SessionJPAAccess.class);

	@Override
	public void add(Session data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		try {
			entityManager.persist(data);
		}catch(EntityExistsException e) { //branch not tested because exception shouldn't be thrown again just so junit can test for it
			logger.warn("{} already exists in the database. Maybe try update", data.getID());
		}
		entityManager.getTransaction().commit();
	}

	@Override
	public void delete(Session data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		entityManager.remove(data);
		entityManager.getTransaction().commit();
	}

	@Override
	public List<Session> get() {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		List<Session> result = entityManager.createQuery("FROM Session").getResultList();
		return result;
	}
}
