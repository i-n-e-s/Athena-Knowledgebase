package de.tudarmstadt.informatik.ukp.athenakp.crawler;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import de.tudarmstadt.informatik.ukp.athenakp.database.access.PaperCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.access.PersonCommonAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.access.hibernate.PaperHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.access.hibernate.PersonHibernateAccess;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Author;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Paper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * A class, which holds the capability to return a List of all authors, which
 * wrote a paper in the frame of the ACL'18 conference
 *
 * @author Jonas Hake
 */
public class ACL18WebParser {

	String startURLAuthors = "https://aclanthology.coli.uni-saarland.de/catalog/facet/author?"// get a list of all authors
			+ "commit=facet.page=1&"// get first page of search
			+ "facet.sort=index&" // sort author list alphabetically
			+ "range[publish_date][begin]=2018&range[publish_date][end]=2018";// limits date of publishing

	String startURLPaper = "https://aclanthology.coli.uni-saarland.de/catalog?per_page=100&range[publish_date][begin]=2018&range[publish_date][end]=&search_field=title";

	/**
	 * fetch the given webpage, and follows the Link, which contains 'Next' as long
	 * there is a Link containing 'Next' The method returns a list of all visited
	 * webpages
	 * 
	 * Works only with a search site from aclanthology.coli.uni-saarland.de
	 * 
	 * @param startURL the URL of the webpage, where the crawler starts
	 * @return the list of visited webpages in form of a Jsoup document
	 * @throws IOException
	 */
	private ArrayList<Document> fetchWebpages(String startURL) throws IOException {
		ArrayList<Document> docs = new ArrayList<Document>();
		docs.add(Jsoup.connect(startURL).get());
		// find Link to next Page, if not found end loop
		boolean nextSiteExist = true;
		while (nextSiteExist) {
			nextSiteExist = false;
			// find the Link to the next page 
			Elements links = docs.get(docs.size() - 1).select("a[href]");
			List<String> linkTexts = links.eachText();
			int idxOfLink = -1;
			for (String lnktxt : linkTexts) {
				if (lnktxt.contains("Next")) {
					nextSiteExist = true;
					idxOfLink = linkTexts.indexOf(lnktxt);
					break;
				}
			}
			// add next page to docList
			if (nextSiteExist) {
				Document nxtDoc = Jsoup.connect(links.get(idxOfLink).absUrl("href")).get();
				docs.add(nxtDoc);
			}
		}
		return docs;
	}

	/**
	 * extract all authors from a given List of webpages, which are in the ACL
	 * search form(e.g. {@link here
	 * https://aclanthology.coli.uni-saarland.de/catalog/facet/author?commit=facet.page%3D1&facet.page=1})
	 *
	 * @param a list of webpages
	 * @return a list of names
	 */
	private ArrayList<String> extractAuthors(ArrayList<Document> webpages) {
		ArrayList<String> authors = new ArrayList<String>();
		//extract the authors from all webpages
		for (Document doc : webpages) {
			Elements authorListElements = doc.select("li");//authors are the only <li> Elements on the Page
			for (Element elmnt : authorListElements) {
				String tmp = elmnt.child(0).ownText();
				authors.add(tmp);
			}
		}
		return authors;
	}

	/**
	 * extract all papers from a given List of webpages, which are in the ACL
	 * search form(e.g. {@link here
	 * https://aclanthology.coli.uni-saarland.de/catalog/facet/author?commit=facet.page%3D1&facet.page=1})
	 * 
	 * @param a list of webpages
	 * @return a list of names
	 */
	private ArrayList<String> extractPapers(ArrayList<Document> webpages){
		ArrayList<String> paperList = new ArrayList<String>();
		//extract the authors from all webpages
		for (Document doc : webpages) {
			Elements paperListElements = doc.select("h5.index_title");//papers are all <h5 class = "index_title">
			for (Element elmnt : paperListElements) {
				if(elmnt.text().contains("VOLUME"));//VOLUMES/Overview-Pdfs are also part of the search-result and removed here
				else paperList.add(elmnt.text());
			}
		}
		return paperList;
	}

	/**
	 * extract all papers and Authors from a given List of webpages, which are in the ACL
	 * search form(e.g. {@link here
	 * https://aclanthology.coli.uni-saarland.de/catalog/facet/author?commit=facet.page%3D1&facet.page=1})
	 * 
	 * @param a list of webpages
	 * @return a list of names
	 */
	private ArrayList<ArrayList<String>> extractPaperAuthor(ArrayList<Document> webpages){
		ArrayList<ArrayList<String>> paperList = new ArrayList<ArrayList<String>>();
		for (Document doc : webpages) {
			Elements paperListElements = doc.select("h5.index_title");
			for (Element elmnt : paperListElements) {
				ArrayList<String> paperAuthorList = new ArrayList<String>();
				if(elmnt.text().contains("VOLUME"));//VOLUMES/Overview-PDFs are also part of the search-result and removed here
				else {	
					//add Paper Title 
					paperAuthorList.add(elmnt.text());
					//find authors and add them to a list
					Elements authorElements = elmnt.parent().parent().children().select("span").select("a");
					for (Element author : authorElements) {
						paperAuthorList.add(author.text());
					}
					paperList.add(paperAuthorList);
					;}
			}
		}
		return paperList;
	}

	/**
	 * Returns all Authors, which published in the year 2018
	 *
	 * @return a list of all authors
	 * @throws IOException
	 */
	public ArrayList<String> getAuthors() throws IOException {
		return extractAuthors(fetchWebpages(startURLAuthors));
	}

	/**
	 * Returns all Papers, which were published in the year 2018
	 *
	 * @return a list of all paper titles
	 * @throws IOException
	 */
	public ArrayList<String> getPaperTitles() throws IOException {
		return extractPapers(fetchWebpages(startURLPaper));
	}

	/**
	 * 
	 * Returns a List of List. Each Sublist represent a published Paper from ACL'18. 
	 * The Sublists are in the Form: Title, Author1, Author2, ... 
	 * 
	 * @return A List of Lists of Papertitle and associated Author.
	 * @throws IOException
	 */
	public ArrayList<ArrayList<String>> getPaperAuthor() throws IOException {
		return extractPaperAuthor(fetchWebpages(startURLPaper));
	}

	/**Constructs Author and Paper Objects from getPaperAuthor() and adds them to the database
	 * see its documentation for its makeup
	 * @throws IOException if jsoup was interrupted in the scraping process (during getPaperAuthor())
	 * @author Julian Steitz
	 * TODO: implement saveandupdate in Common Access? Otherwise implement check if entry exist. Expensive?
	 * TODO: recognise asian names and reverse name order? Or is that taken care of in the website? Check with Thomas
	 */
	public void storePapersandAuthors() throws IOException {
		ArrayList<ArrayList<String>> listofPaperAuthor = getPaperAuthor();
		PaperCommonAccess paperfiler = new PaperHibernateAccess();
		PersonCommonAccess personfiler = new PersonHibernateAccess();

		for (ArrayList<String> paperandauthors:listofPaperAuthor) {
			// only one Paper per paperandauthors
			Paper paper = new Paper();
			// more elegant to do it in the if loop, but that screws with PaperID
			paper.setTitle(paperandauthors.get(0));
			// only one set of authors that worked on the paper
			Set<Author> authorset = new HashSet<>();

			for (String name : paperandauthors){
				if (paperandauthors.indexOf(name) == 0){
					break;
				}
				else{
					Author author = new Author();
					// makes the sane? assumption, that doubled last names "Schmidt Müller" are more common than
					// doubled middle names
					String[] splitname = name.split(" ", 3);
					switch(splitname.length){

						case 1: author.setLastName(splitname[0]);
							break;
						case 2: author.setFirstName(splitname[0]);
							author.setLastName(splitname[1]);
							break;
						case 3: author.setFirstName(splitname[0]);
							author.setMiddleName(splitname[1]);
							author.setLastName(splitname[2]);
							break;
					}
					// does not check for duplicates (between sets) since even authors can share the same name
					//TODO: replace with paper.addAuthor after merge?
					authorset.add(author);
					personfiler.add(author);
				}

			}
			// set author relation
			paper.setAuthors(authorset);
			// finally add the paper
			// TODO: they contain an identifier (e.g. [C18-1021]) maybe add as a separate table to Papers? eg. Anthology
			paperfiler.add(paper);
		}
	}
}
