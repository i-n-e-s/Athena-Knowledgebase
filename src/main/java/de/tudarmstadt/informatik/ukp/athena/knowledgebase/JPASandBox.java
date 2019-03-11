package de.tudarmstadt.informatik.ukp.athena.knowledgebase;

import java.time.LocalDate;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.access.CommonAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.PaperJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.PersonJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Paper;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Person;

@SpringBootApplication
public class JPASandBox {
	private static Logger logger = LogManager.getLogger(JPASandBox.class);

	public static void main(String[] args) {
		SpringApplication.run(JPASandBox.class, args);

		CommonAccess<Person> pca = new PersonJPAAccess();

		Person dummyAuthor = new Person();
		dummyAuthor.setFullName("Rumpo Derpel");
		dummyAuthor.setBirth(LocalDate.of(2010, 10, 10));
		pca.add(dummyAuthor);

        logger.debug("Authors: {}", authors.toString());

		CommonAccess<Paper> paca = new PaperJPAAccess();
		Paper dummyPaper1 = new Paper();

		dummyPaper1.setReleaseDate(LocalDate.of(2018, 8, 1));
		Paper dummyPaper2 = new Paper();
		dummyPaper2.setReleaseDate(LocalDate.of(2018, 9, 1));
		Paper dummyPaper3 = new Paper();
		dummyPaper3.setReleaseDate(LocalDate.of(2018, 10, 1));
		Paper dummyPaper4 = new Paper();
		dummyPaper4.setReleaseDate(LocalDate.of(2018, 11, 1));
		Paper dummyPaper5 = new Paper();
		dummyPaper5.setReleaseDate(LocalDate.of(2018, 12, 1));
		paca.add(dummyPaper1);
		paca.add(dummyPaper2);
		paca.add(dummyPaper3);
		paca.add(dummyPaper4);
		paca.add(dummyPaper5);

		logger.debug(Arrays.toString(paca.getByReleaseRange(2018, 9, 2018, 11).toArray()));
	}
}
