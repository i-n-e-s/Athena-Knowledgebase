package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access.SessionCommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Session;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.SessionCategory;

/**
 * @author Daniel Lehmann
 */
public class SessionJPAAccess implements SessionCommonAccess {
	/**
	 * Common code used by all get methods which filter by simple column values.
	 * @param name The name of the column to restrict
	 * @param value The value to restrict the selection to
	 * @return A List of all sessions with the given restriction
	 */
	private List<Session> getBy(String name, Object value) {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Session> criteriaQuery = builder.createQuery(Session.class);
		Root<Session> root = criteriaQuery.from(Session.class);
		criteriaQuery
		.select(root)
		.where(builder.equal(root.get(name), value));
		List<Session> result = entityManager.createQuery(criteriaQuery).getResultList();
		return result;
	}

	@Override
	public List<Session> getBySessionId(Long id) {
		return getBy("sessionID", id);
	}

	@Override
	public List<Session> getByStartTime(Integer year, Integer month, Integer day, Integer hour, Integer minute) {
		LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, minute);
		return getBy("begin", localDateTime);
	}

	@Override
	public List<Session> getByEndTime(Integer year, Integer month, Integer day, Integer hour, Integer minute) {
		LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, minute);
		return getBy("end", localDateTime);
	}

	@Override
	public List<Session> getByPlace(String place) {
		return getBy("place", place);
	}

	@Override
	public List<Session> getByTitle(String title) {
		return getBy("title", title);
	}

	@Override
	public List<Session> getByDescription(String description) {
		return getBy("description", description);
	}

	@Override
	public List<Session> getByCategory(SessionCategory category) {
		return getBy("category", category);
	}

	@Override
	public void add(Session data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		try {
			entityManager.persist(data);
		}catch(EntityExistsException e) {
			System.out.println(data.getID()+"already exist in the Database. Maybe try update");
		}
		entityManager.getTransaction().commit();
	}

	@Override
	public void update(Session data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		entityManager.merge(data);
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
