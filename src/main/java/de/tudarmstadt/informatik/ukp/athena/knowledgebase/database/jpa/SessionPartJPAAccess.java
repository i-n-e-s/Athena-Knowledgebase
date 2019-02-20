package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import java.util.List;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access.SessionPartCommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.SessionPart;

/**
 * @author Daniel Lehmann
 */
public class SessionPartJPAAccess implements SessionPartCommonAccess {
	/**
	 * Common code used by all get methods which filter by simple column values.
	 * @param name The name of the column to restrict
	 * @param value The value to restrict the selection to
	 * @return A List of all session parts with the given restriction
	 */
	private List<SessionPart> getBy(String name, Object value) {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<SessionPart> criteriaQuery = builder.createQuery(SessionPart.class);
		Root<SessionPart> root = criteriaQuery.from(SessionPart.class);
		criteriaQuery
		.select(root)
		.where(builder.equal(root.get(name), value));
		List<SessionPart> result = entityManager.createQuery(criteriaQuery).getResultList();
		return result;
	}

	@Override
	public List<SessionPart> getBySessionPartId(Long id) {
		return getBy("sessionPartID", id);
	}

	@Override
	public List<SessionPart> getByTitle(String title) {
		return getBy("title", title);
	}

	@Override
	public List<SessionPart> getByDescription(String description) {
		return getBy("description", description);
	}

	@Override
	public List<SessionPart> getByPlace(String place) {
		return getBy("place", place);
	}

	@Override
	public void add(SessionPart data) {
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
	public void update(SessionPart data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		entityManager.merge(data);
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
