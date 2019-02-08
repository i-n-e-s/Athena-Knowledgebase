package de.tudarmstadt.informatik.ukp.athenakp.database.hibernate;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import de.tudarmstadt.informatik.ukp.athenakp.database.access.SessionCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Session;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.SessionCategory;

/**
 * @author Daniel Lehmann
 */
@Deprecated
public class SessionHibernateAccess implements SessionCommonAccess {
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

	/**
	 * Common code used by all get methods above
	 * @param name The name of the column to restrict
	 * @param value The value to restrict the selection to
	 * @return A List of all sessions with the given restriction
	 */
	private List<Session> getBy(String name, Object value) {
		org.hibernate.Session session = HibernateUtils.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(Session.class);
		List<Session> result;

		criteria.add(Restrictions.eq(name, value));
		result = criteria.list();
		session.close();
		return result;
	}

	@Override
	public void add(Session data) {
		org.hibernate.Session session = HibernateUtils.getSessionFactory().openSession();

		session.beginTransaction();
		session.save(data);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public void update(Session data) {
		org.hibernate.Session session = HibernateUtils.getSessionFactory().openSession();

		session.beginTransaction();
		session.update(data);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public void delete(Session data) {
		org.hibernate.Session session = HibernateUtils.getSessionFactory().openSession();

		session.beginTransaction();
		session.remove(data);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public List<Session> get() {
		org.hibernate.Session session = HibernateUtils.getSessionFactory().openSession();
		List<Session> result = session.createCriteria(Session.class).list();

		session.close();
		return result;
	}
}
