package de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa;

import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access.EventCommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Event;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.EventCategory;

/**
 * @author Daniel Lehmann
 */
public class EventJPAAccess implements EventCommonAccess {
	/**
	 * Common code used by all get methods which filter by simple column values.
	 * @param name The name of the column to restrict
	 * @param value The value to restrict the selection to
	 * @return A List of all events with the given restriction
	 */
	private List<Event> getBy(String name, Object value) {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		CriteriaBuilder builder = entityManager.getCriteriaBuilder();
		CriteriaQuery<Event> criteriaQuery = builder.createQuery(Event.class);
		Root<Event> root = criteriaQuery.from(Event.class);
		criteriaQuery
		.select(root)
		.where(builder.equal(root.get(name), value));
		List<Event> result = entityManager.createQuery(criteriaQuery).getResultList();
		entityManager.close();
		return result;
	}

	@Override
	public List<Event> getByEventId(Long id) {
		return getBy("eventID", id);
	}

	@Override
	public List<Event> getByConferenceName(String conferenceName) {
		return getBy("conferenceName", conferenceName);
	}

	@Override
	public List<Event> getByStartTime(Integer year, Integer month, Integer day, Integer hour, Integer minute) {
		LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, minute);
		return getBy("begin", localDateTime);
	}

	@Override
	public List<Event> getByEndTime(Integer year, Integer month, Integer day, Integer hour, Integer minute) {
		LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour, minute);
		return getBy("end", localDateTime);
	}

	@Override
	public List<Event> getByPlace(String place) {
		return getBy("place", place);
	}

	@Override
	public List<Event> getByTitle(String title) {
		return getBy("title", title);
	}

	@Override
	public List<Event> getByDescription(String description) {
		return getBy("description", description);
	}

	@Override
	public List<Event> getByCategory(EventCategory category) {
		return getBy("category", category);
	}

	@Override
	public void add(Event data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		entityManager.persist(data);
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@Override
	public void update(Event data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		entityManager.merge(data);
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@Override
	public void delete(Event data) {
		EntityManager entityManager = PersistenceManager.getEntityManager();

		entityManager.getTransaction().begin();
		entityManager.remove(data);
		entityManager.getTransaction().commit();
		entityManager.close();
	}

	@Override
	public List<Event> get() {
		EntityManager entityManager = PersistenceManager.getEntityManager();
		List<Event> result = entityManager.createQuery("FROM Event").getResultList();
		entityManager.close();
		return result;
	}
}
