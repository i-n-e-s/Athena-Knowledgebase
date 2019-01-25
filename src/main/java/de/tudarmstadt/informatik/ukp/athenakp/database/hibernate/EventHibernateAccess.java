package de.tudarmstadt.informatik.ukp.athenakp.database.hibernate;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import de.tudarmstadt.informatik.ukp.athenakp.database.access.EventCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Event;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.EventCategory;

/**
 * @author Daniel Lehmann
 */
@Deprecated
public class EventHibernateAccess implements EventCommonAccess {
	@Override
	public List<Event> getById(Long id) {
		return getBy("eventID", id);
	}

	@Override
	public List<Event> getByConference(String conference) {
		return getBy("conference", conference);
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

	/**
	 * Common code used by all get methods above
	 * @param name The name of the column to restrict
	 * @param value The value to restrict the selection to
	 * @return A List of all persons with the given restriction
	 */
	private List<Event> getBy(String name, Object value) {
		Session session = HibernateUtils.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(Event.class);
		List<Event> result;

		criteria.add(Restrictions.eq(name, value));
		result = criteria.list();
		session.close();
		return result;
	}

	@Override
	public void add(Event data) {
		Session session = HibernateUtils.getSessionFactory().openSession();

		session.beginTransaction();
		session.save(data);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public void update(Event data) {
		Session session = HibernateUtils.getSessionFactory().openSession();

		session.beginTransaction();
		session.update(data);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public void delete(Event data) {
		Session session = HibernateUtils.getSessionFactory().openSession();

		session.beginTransaction();
		session.remove(data);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public List<Event> get() {
		Session session = HibernateUtils.getSessionFactory().openSession();
		List<Event> result = session.createCriteria(Event.class).list();

		session.close();
		return result;
	}
}
