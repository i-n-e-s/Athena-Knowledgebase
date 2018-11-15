package de.tudarmstadt.informatik.ukp.athenakp.database.hibernate;

import de.tudarmstadt.informatik.ukp.athenakp.database.access.CommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.access.PersonCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Person;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import java.util.List;

/**
 * Implementation of PersonCommonAccess with Hibernate.
 *
 * @author Tristan Wettich
 */
public class PersonHibernateAccess implements PersonCommonAccess {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Person> getByLastName(String name) {
        Session session = HibernateUtils.getSessionFactory().openSession();
        Criteria criteria = session.createCriteria(Person.class);
        criteria.add(Restrictions.eq("lastName", name));
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
