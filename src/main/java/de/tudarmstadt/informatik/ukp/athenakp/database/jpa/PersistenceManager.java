package de.tudarmstadt.informatik.ukp.athenakp.database.jpa;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.PersistenceUnit;

public class PersistenceManager {

    /**
     * This field should be configurable via commandline arguments or configuration files to support multiple persistence units.
     */
    @PersistenceUnit
    private static final EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("hibernate");

    /**
     * Get the Hibernate-SessionFactory.
     * @return the SessionFactory
     */
    public static EntityManager getEntityManager() {
        return entityManagerFactory.createEntityManager();
    }

    /**
     * Shutdown SessionFactory.
     * Warning: Since the field is final this cannot be undone at runtime.
     */
    public static void closeEntityManagerFactory(){
        entityManagerFactory.close();
    }

}
