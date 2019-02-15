package de.tudarmstadt.informatik.ukp.athenakp.database.hibernate;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import de.tudarmstadt.informatik.ukp.athenakp.database.access.PersonCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Person;

/**
 * Implementation of PersonCommonAccess with Hibernate.
 *
 * @author Tristan Wettich, Daniel Lehmann
 */
@Deprecated
public class PersonHibernateAccess implements PersonCommonAccess {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Person> getByPersonID(Long personID) {
		return getBy("personID", personID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Person> getByPrefix(String prefix) {
		return getBy("prefix", prefix);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Person> getByFullName(String fullName) {
		return getBy("fullName", fullName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Person> getByBirthdate(Integer year, Integer month, Integer day) {
		return getBy("birthdate", HibernateUtils.toTimestamp(year, month, day));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Person> getByObit(Integer year, Integer month, Integer day) {
		return getBy("obit", HibernateUtils.toTimestamp(year, month, day));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Person> getByInstitutionID(long institutionID) { //TODO implement this
		return getBy("institutionID", institutionID);
	}

	/**
	 * Common code used by all get methods above
	 * @param name The name of the column to restrict
	 * @param value The value to restrict the selection to
	 * @return A List of all persons with the given restriction
	 */
	private List<Person> getBy(String name, Object value) {
		Session session = HibernateUtils.getSessionFactory().openSession();
		Criteria criteria = session.createCriteria(Person.class);
		criteria.add(Restrictions.eq(name, value));
		List<Person> result = criteria.list();
		session.close();
		return result;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void add(Person data) {
		Session session = HibernateUtils.getSessionFactory().openSession();
		session.beginTransaction();
		session.save(data);
		session.getTransaction().commit();
		session.close();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void update(Person data) {
		Session session = HibernateUtils.getSessionFactory().openSession();
		session.beginTransaction();
		session.update(data);
		session.getTransaction().commit();
		session.close();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void delete(Person data) {
		Session session = HibernateUtils.getSessionFactory().openSession();
		session.beginTransaction();
		session.remove(data);
		session.getTransaction().commit();
		session.close();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Person> get() {
		Session session = HibernateUtils.getSessionFactory().openSession();
		List<Person> result = session.createCriteria(Person.class).list();
		session.close();
		return result;
	}
}
