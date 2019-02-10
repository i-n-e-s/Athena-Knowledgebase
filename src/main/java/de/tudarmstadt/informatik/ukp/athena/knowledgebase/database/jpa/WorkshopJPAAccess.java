package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access.WorkshopCommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Workshop;

/**
 * @author Daniel Lehmann
 */
public class WorkshopJPAAccess implements WorkshopCommonAccess {
	/**
	 * Common code used by all get methods which filter by simple column values.
	 * @param name The name of the column to restrict
	 * @param value The value to restrict the selection to
	 * @return A List of all workshops with the given restriction
	 */
	private List<Workshop> getBy(String name, Object value) {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Workshop> criteriaQuery = builder.createQuery(Workshop.class);
		Root<Workshop> root = criteriaQuery.from(Workshop.class);
		criteriaQuery
		.select(root)
		.where(builder.equal(root.get(name), value));
		List<Workshop> result = entityManager.createQuery(criteriaQuery).getResultList();
		entityManager.close();
		return result;
	}

	@Override
	public List<Workshop> getById(Long id) {
		return getBy("id", id);
	}

	@Override
	public List<Workshop> getByConferenceName(String conferenceName) {
		return getBy("conferenceName", conferenceName);
	}

	@Override
	public List<Workshop> getByStartTime(Integer year, Integer month, Integer day, Integer hour, Integer minute) {
		LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, minute);
		return getBy("begin", localDateTime);
	}

	@Override
	public List<Workshop> getByEndTime(Integer year, Integer month, Integer day, Integer hour, Integer minute) {
		LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, minute);
		return getBy("end", localDateTime);
	}

	@Override
	public List<Workshop> getByPlace(String place) {
		return getBy("place", place);
	}

	@Override
	public List<Workshop> getByTitle(String title) {
		return getBy("title", title);
	}

	@Override
	public List<Workshop> getByAbbreviation(String abbreviation) {
		return getBy("abbreviation", abbreviation);
	}

	@Override
	public void add(Workshop data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		entityManager.persist(data);
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@Override
	public void update(Workshop data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		entityManager.merge(data);
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@Override
	public void delete(Workshop data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		entityManager.remove(data);
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@Override
	public List<Workshop> get() {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		List<Workshop> result = entityManager.createQuery("FROM Workshop").getResultList();
		entityManager.close();
		return result;
	}
}
