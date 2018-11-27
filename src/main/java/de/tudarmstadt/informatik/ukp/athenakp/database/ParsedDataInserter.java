package de.tudarmstadt.informatik.ukp.athenakp.database;

import de.tudarmstadt.informatik.ukp.athenakp.Application;
import de.tudarmstadt.informatik.ukp.athenakp.crawler.ACL18WebParser;
import de.tudarmstadt.informatik.ukp.athenakp.database.access.PaperCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.access.hibernate.PaperHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Author;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Paper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.IOException;
import java.util.ArrayList;


@SpringBootApplication
/*
    a class which is meant to be run only once, which is why it is seperate from application. Starts Spring and adds
    data to an sql Database via hibernate
    contains methods which reformat ParserData into a hibernate digestible format
    @author Julian Steitz
 */
public class ParsedDataInserter {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        ParsedDataInserter parsedDataInserter = new ParsedDataInserter();
        try {
            parsedDataInserter.aclstorePapersandAuthors();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**Constructs Author and Paper Objects from ACL18Webparser().getPaperAuthor() and adds them to the database
     * see its documentation for its makeup
     * @throws IOException if jsoup was interrupted in the scraping process (during getPaperAuthor())
     * @author Julian Steitz
     * TODO: implement saveandupdate in Common Access? Otherwise implement check if entry exist. Expensive?
     * TODO: recognise asian names and reverse name order? Or is that taken care of in the website? Check with Thomas
     */
    private void aclstorePapersandAuthors() throws IOException {
        ACL18WebParser acl18WebParser = new ACL18WebParser();
        System.out.println("Scraping, this can take a couple of minutes..");
        ArrayList<ArrayList<String>> listofPaperAuthor = acl18WebParser.getPaperAuthor();
        PaperCommonAccess paperfiler = new PaperHibernateAccess();
        // PersonCommonAccess personfiler = new PersonHibernateAccess();

        for (ArrayList<String> paperandauthors:listofPaperAuthor) {
            // only one Paper per paperandauthors
            Paper paper = new Paper();
            // clean up the titles in the form of [C18-1017] Simple Neologism Based Domain Independe...
            // C18-1017 would be the anthology - we remove [] because the rest API dislikes the characters and they
            // convey no meaning
            String rawtitle = paperandauthors.get(0);
            String[] splitrawtitle = rawtitle.split(" ", 2);
            String papertitle = splitrawtitle[1];
            String anthology = splitrawtitle[0].replace("[", "").replace("]", "");
            paper.setTitle(papertitle);
            paper.setAnthology(anthology);
            // we ignore the first entry, since it is a Paper's title
            for (int i = 1; i < paperandauthors.size(); i++){
                String authorname = paperandauthors.get(i);

                // build author name
                // makes the sane? assumption, that doubled last names "Schmidt Müller" are more common than
                // doubled middle names
                String[] splitauthorname = authorname.split(" ", 3);
                Author author = new Author();
                switch(splitauthorname.length){

                    case 1: author.setLastName(splitauthorname[0]);
                        break;
                    case 2: author.setFirstName(splitauthorname[0]);
                        author.setLastName(splitauthorname[1]);
                        break;
                    case 3: author.setFirstName(splitauthorname[0]);
                        author.setMiddleName(splitauthorname[1]);
                        author.setLastName(splitauthorname[2]);
                        break;
                }
                // Both following statements seem necessary for the author_paper table but lead to Hibernate
                // access returning an object (paper) as often as a relation in author_paper exists
                // looking into the tables themselves, duplicate papers (even with the same PaperID) do not exist
                // TODO: fix whatever causes the multiple Hibernate Accesses (returning the same object)
                // TODO: when calling the API (my guess: paper_author relation)
                // set paper - author relation
                paper.addAuthor(author);
                // set author - paper relation
                author.addPaper(paper);
                // add author to database + paper included
                // personfiler.add(author);
            }
            // adding the paper automatically adds the corresponding authors - realisation that took hours
            paperfiler.add(paper);
        }
    }
}
