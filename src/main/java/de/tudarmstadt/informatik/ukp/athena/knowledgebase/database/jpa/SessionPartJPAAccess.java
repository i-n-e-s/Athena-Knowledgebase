package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access.CommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.SessionPart;

/**
 * @author Daniel Lehmann
 */
public class SessionPartJPAAccess implements CommonAccess<SessionPart> {
	private static Logger logger = LogManager.getLogger(SessionPartJPAAccess.class);

	@Override
	public void add(SessionPart data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		try {
			entityManager.persist(data);
		}catch(EntityExistsException e) {
			logger.warn("{} already exists in the database. Maybe try update", data.getID());
		}
		entityManager.getTransaction().commit();
	}

	@Override
	public void delete(SessionPart data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		entityManager.remove(data);
		entityManager.getTransaction().commit();
	}

	@Override
	public List<SessionPart> get() {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		List<SessionPart> result = entityManager.createQuery("FROM SessionPart").getResultList();
		return result;
	}
}
