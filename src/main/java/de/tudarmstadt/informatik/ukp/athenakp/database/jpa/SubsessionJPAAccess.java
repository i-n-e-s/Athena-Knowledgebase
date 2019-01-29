package de.tudarmstadt.informatik.ukp.athenakp.database.jpa;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.tudarmstadt.informatik.ukp.athenakp.database.access.SubsessionCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Subsession;

/**
 * @author Daniel Lehmann
 */
public class SubsessionJPAAccess implements SubsessionCommonAccess {
	/**
	 * Common code used by all get methods which filter by simple column values.
	 * @param name The name of the column to restrict
	 * @param value The value to restrict the selection to
	 * @return A List of all subsessions with the given restriction
	 */
	private List<Subsession> getBy(String name, Object value) {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Subsession> criteriaQuery = builder.createQuery(Subsession.class);
		Root<Subsession> root = criteriaQuery.from(Subsession.class);
		criteriaQuery
		.select(root)
		.where(builder.equal(root.get(name), value));
		List<Subsession> result = entityManager.createQuery(criteriaQuery).getResultList();
		entityManager.close();
		return result;
	}

	@Override
	public List<Subsession> getById(Long id) {
		return getBy("subsessionID", id);
	}

	@Override
	public List<Subsession> getByStartTime(Integer year, Integer month, Integer day, Integer hour, Integer minute) {
		LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, minute);
		return getBy("begin", localDateTime);
	}

	@Override
	public List<Subsession> getByEndTime(Integer year, Integer month, Integer day, Integer hour, Integer minute) {
		LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, minute);
		return getBy("end", localDateTime);
	}

	@Override
	public List<Subsession> getByTitle(String title) {
		return getBy("title", title);
	}

	@Override
	public List<Subsession> getByDescription(String description) {
		return getBy("description", description);
	}

	@Override
	public void add(Subsession data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		entityManager.persist(data);
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@Override
	public void update(Subsession data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		entityManager.merge(data);
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@Override
	public void delete(Subsession data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		entityManager.remove(data);
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@Override
	public List<Subsession> get() {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		List<Subsession> result = entityManager.createQuery("FROM Subsession").getResultList();
		entityManager.close();
		return result;
	}
}
