package de.tudarmstadt.informatik.ukp.athenakp;

import java.util.Date;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.tudarmstadt.informatik.ukp.athenakp.database.access.PersonCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.jpa.PersonJPAAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Author;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Person;

@SpringBootApplication
public class JPASandBox {

	public static void main(String[] args) {
		SpringApplication.run(JPASandBox.class, args);

		PersonCommonAccess pca = new PersonJPAAccess();

		Author dummyAuthor = new Author();
		dummyAuthor.setFullName("Rumpo Derpel");
		dummyAuthor.setBirthdate(new Date(2010, 10, 10));
		pca.add(dummyAuthor);

		List<Person> authors = pca.getByFullName("Rumpo Derpel");

		System.out.println("Authors: " + authors.toString());
	}
}
