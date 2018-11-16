package de.tudarmstadt.informatik.ukp.athenakp.database.access;

import java.util.Date;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import de.tudarmstadt.informatik.ukp.athenakp.database.HibernateUtils;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Person;

/**
 * Implementation of PersonCommonAccess with Hibernate.
 *
 * @author Tristan Wettich, Daniel Lehmann
 */
public class PersonHibernateAccess implements PersonCommonAccess {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Person> getByPersonID(Long personID)
	{
		return getByRestriction("personID", personID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Person> getByPrefix(String prefix)
	{
		return getByRestriction("prefix", prefix);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Person> getByFirstName(String firstName)
	{
		return getByRestriction("firstName", firstName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Person> getByMiddleName(String middleName)
	{
		return getByRestriction("middleName", middleName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Person> getByLastName(String lastName)
	{
		return getByRestriction("lastName", lastName);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Person> getByBirthdate(Date birthdate)
	{
		return getByRestriction("birthdate", birthdate);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Person> getByObit(Date obit)
	{
		return getByRestriction("obit", obit);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Person> getByInstitutionID(String institutionID) //TODO
	{
		return null;
	}

	/**
	 * Common code used by all get methods above
	 * @param name The name of the column to restrict
	 * @param value The value to restrict the selection to
	 * @return A List of all persons with the given restriction
	 */
	private List<Person> getByRestriction(String name, Object value)
	{
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
