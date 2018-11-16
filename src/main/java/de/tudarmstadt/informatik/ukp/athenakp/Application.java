package de.tudarmstadt.informatik.ukp.athenakp;

import de.tudarmstadt.informatik.ukp.athenakp.database.access.PersonCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.access.PersonHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Person;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

        Person p = new Person();
        p.setFirstName("Rumpo");
        p.setLastName("Derpel");
        p.setBirthdate(new Date(2010, 10, 10));

        PersonCommonAccess pca = new PersonHibernateAccess();
        pca.add(p);
        Person p2 = pca.getByLastName("Derpel").get(0);
        System.out.println("Here is " + p2.getFirstName() + " " + p2.getLastName());
    }
}