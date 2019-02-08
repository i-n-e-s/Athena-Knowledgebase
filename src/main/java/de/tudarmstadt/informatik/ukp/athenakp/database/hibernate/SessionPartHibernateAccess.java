package de.tudarmstadt.informatik.ukp.athenakp.database.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import de.tudarmstadt.informatik.ukp.athenakp.database.access.SessionPartCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.SessionPart;

/**
 * @author Daniel Lehmann
 */
@Deprecated
public class SessionPartHibernateAccess implements SessionPartCommonAccess {
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

	/**
	 * Common code used by all get methods above
	 * @param name The name of the column to restrict
	 * @param value The value to restrict the selection to
	 * @return A List of all session parts with the given restriction
	 */
	private List<SessionPart> getBy(String name, Object value) {
		org.hibernate.Session session = HibernateUtils.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(SessionPart.class);
		List<SessionPart> result;

		criteria.add(Restrictions.eq(name, value));
		result = criteria.list();
		session.close();
		return result;
	}

	@Override
	public void add(SessionPart data) {
		org.hibernate.Session session = HibernateUtils.getSessionFactory().openSession();

		session.beginTransaction();
		session.save(data);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public void update(SessionPart data) {
		org.hibernate.Session session = HibernateUtils.getSessionFactory().openSession();

		session.beginTransaction();
		session.update(data);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public void delete(SessionPart data) {
		org.hibernate.Session session = HibernateUtils.getSessionFactory().openSession();

		session.beginTransaction();
		session.remove(data);
		session.getTransaction().commit();
		session.close();
	}

	@Override
	public List<SessionPart> get() {
		org.hibernate.Session session = HibernateUtils.getSessionFactory().openSession();
		List<SessionPart> result = session.createCriteria(SessionPart.class).list();

		session.close();
		return result;
	}
}
