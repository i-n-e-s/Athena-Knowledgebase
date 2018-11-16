package de.tudarmstadt.informatik.ukp.athenakp.database;

import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;

/**
 * Class holding the hibernate session factory.
 *
 * @author Tristan Wettich
 */
public class HibernateUtils {

    private static final SessionFactory sessionFactory = buildSessionFactory();

    /**
     * Creates the Hibernate-SessionFactory.
     * @return hibernate SessionFactory
     */
    private static SessionFactory buildSessionFactory() {
        try {
            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder()
                    .configure("hibernate.cfg.xml").build();

            Metadata metadata = new MetadataSources(serviceRegistry).getMetadataBuilder().build();

            return metadata.getSessionFactoryBuilder().build();
        } catch (Throwable ex) {
            System.err.println("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    /**
     * Get the Hibernate-SessionFactory.
     * @return the SessionFactory
     */
    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    /**
     * Shutdown SessionFactory.
     * Warning: Since the field is final this cannot be undone at runtime.
     */
    public static void closeSessionFactory(){
        getSessionFactory().close();
    }

}
