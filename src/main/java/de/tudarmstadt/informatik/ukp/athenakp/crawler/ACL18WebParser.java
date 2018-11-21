package de.tudarmstadt.informatik.ukp.athenakp.crawler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

	String startURL = "https://aclanthology.coli.uni-saarland.de/catalog/facet/author?"// get a list of all authors
			+ "commit=facet.page=1&"// get first page of search
			+ "facet.sort=index&" // sort author list alphabetically
			+ "range[publish_date][begin]=2018&range[publish_date][end]=2018";// limits date of publishing

	/**
	 * fetch the given website, and follows the Link, which contains 'Next' as long
	 * there is a Link containing 'Next' The method returns a list of all visited
	 * websites
	 *
	 * @param startURL the URL of the website, where the crawler starts
	 * @return the list of visited websites in form of a Jsoup document
	 */
	private ArrayList<Document> fetchNameWebpages(String startURL) throws IOException {
		ArrayList<Document> docs = new ArrayList<Document>();
		docs.add(Jsoup.connect(startURL).get());
		// find Link to next Page, if not found end loop
		boolean nextSiteExist = true;
		while (nextSiteExist) {// TODO Remove limiter 'cnt <= 1'
			nextSiteExist = false;
			Elements links = docs.get(docs.size() - 1).select("a[href]");
			List<String> linkTexts = links.eachText();
			int idxOfLink = -1;
			for (String lnktxt : linkTexts) {
				if (lnktxt.contains("Next")) {
					nextSiteExist = true;
					idxOfLink = linkTexts.indexOf(lnktxt);
				}
			}
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
	private ArrayList<String> extractAuthors(ArrayList<Document> webPages) {
		ArrayList<String> authors = new ArrayList<String>();
		for (Document doc : webPages) {
			Elements authorListElements = doc.select("li");
			for (Element elmnt : authorListElements) {
				String tmp = elmnt.child(0).ownText();
				authors.add(tmp);
			}
		}
		return authors;
	}

	/**
	 * Returns all Authors, which published in the year 2018
	 *
	 * @return a list of all authors
	 */
	public ArrayList<String> getAuthors() throws IOException {
		return extractAuthors(fetchNameWebpages(startURL));
	}

}