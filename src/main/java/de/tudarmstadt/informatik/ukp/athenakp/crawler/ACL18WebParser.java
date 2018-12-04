package de.tudarmstadt.informatik.ukp.athenakp.crawler;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.tudarmstadt.informatik.ukp.athenakp.database.models.Conference;

/**
 * A class, which holds the capability to return a List of all authors, which
 * wrote a paper in the frame of the ACL'18 conference
 *
 * @author Jonas Hake
 */
class ACL18WebParser extends AbstractCrawler{

	private String startURLAuthors = "https://aclanthology.coli.uni-saarland.de/catalog/facet/author?"// get a list of all authors
			+ "commit=facet.page=1&"// get first page of search
			+ "facet.sort=index&" // sort author list alphabetically
			+ "range[publish_date][begin]=2018&range[publish_date][end]=2018";// limits date of publishing

	private String startURLPaper = "https://aclanthology.coli.uni-saarland.de/catalog?per_page=100&range[publish_date][begin]=2018&range[publish_date][end]=&search_field=title";

	private String schedulePage = "https://acl2018.org/programme/schedule/";

	private String aboutPage = "https://acl2018.org/";
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
		// extract the authors from all webpages
		for (Document doc : webpages) {
			Elements authorListElements = doc.select("li");// authors are the only <li> Elements on the Page
			for (Element elmnt : authorListElements) {
				String tmp = elmnt.child(0).ownText();
				authors.add(tmp);
			}
		}
		return authors;
	}

	/**
	 * extract all papers from a given List of webpages, which are in the ACL search
	 * form(e.g. {@link here
	 * https://aclanthology.coli.uni-saarland.de/catalog/facet/author?commit=facet.page%3D1&facet.page=1})
	 *
	 * @param a list of webpages
	 * @return a list of names
	 */
	private ArrayList<String> extractPapers(ArrayList<Document> webpages) {
		ArrayList<String> paperList = new ArrayList<String>();
		// extract the authors from all webpages
		for (Document doc : webpages) {
			Elements paperListElements = doc.select("h5.index_title");// papers are all <h5 class = "index_title">
			for (Element elmnt : paperListElements) {
				if (!elmnt.text().contains("VOLUME"))// VOLUMES/Overview-Pdfs are also part of the search-result and removed here
					paperList.add(elmnt.text());
			}
		}
		return paperList;
	}

	/**
	 * extract all papers and Authors from a given List of webpages, which are in
	 * the ACL search form(e.g. {@link here
	 * https://aclanthology.coli.uni-saarland.de/catalog/facet/author?commit=facet.page%3D1&facet.page=1})
	 *
	 * @param a list of webpages
	 * @return a list of names
	 */
	private ArrayList<ArrayList<String>> extractPaperAuthor(ArrayList<Document> webpages) {
		ArrayList<ArrayList<String>> paperList = new ArrayList<ArrayList<String>>();
		for (Document doc : webpages) {
			Elements paperListElements = doc.select("h5.index_title");
			for (Element elmnt : paperListElements) {
				ArrayList<String> paperAuthorList = new ArrayList<String>();// VOLUMES/Overview-PDFs are also part of the search-result and removed here
				if (!elmnt.text().contains("VOLUME")) {
					// add Paper Title
					paperAuthorList.add(elmnt.text());
					// find authors and add them to a list
					Elements authorElements = elmnt.parent().parent().children().select("span").select("a");
					for (Element author : authorElements) {
						paperAuthorList.add(author.text());
					}
					paperList.add(paperAuthorList);
				}
			}
		}
		return paperList;
	}

	@Override
	public Conference getConferenceInformation() throws IOException {
		Conference currentConference = new Conference();
		Document aboutPage = Jsoup.connect(this.aboutPage).get();
		String conferenceName = aboutPage.select(".site-title a").text();
		currentConference.setName(conferenceName);
		CrawlerToolset crawlerToolset = new CrawlerToolset();

		/*		Useful for people who want to incorporate exact times
		String conferenceStartTimeInformation = schedulePage.select(".day-wrapper:nth-child(1) " +
				".overview-item:nth-child(1) .start-time").text();
		String conferenceEndTimeInformation = schedulePage.select(".day-wrapper:nth-child(6) " +
				".overview-item~ .overview-item+ .overview-item .start-time").text();

		LocalTime conferenceStartTime = crawlerToolset.acl2018ConvertStringToTime(conferenceStartTimeInformation);
		LocalTime conferenceEndTime = crawlerToolset.acl2018ConvertStringToTime(conferenceEndTimeInformation);*/

		String cityCountryInformation = aboutPage.select("p:nth-child(1) a:nth-child(1)").text();
		String dateAndLocationString = aboutPage.select(".sub-title-extra").text();
		LocalDate conferenceStartDate = crawlerToolset.acl2018ConvertStringToDateRange(dateAndLocationString)[0];
		LocalDate conferenceEndDate = crawlerToolset.acl2018ConvertStringToDateRange(dateAndLocationString)[1];
		// Maybe we need to look at a timezone api? Probably not feasible to keep it free, which is why it is set as
		// manual for now
		// TODO: talk about timezones and how to handle them
		// ZoneId timeZone = ZoneId.of("GMT+11");

		currentConference.setStartDate(conferenceStartDate);
		currentConference.setEndDate(conferenceEndDate);

		String[] cityCountry = cityCountryInformation.split(", ");
		String conferenceCity = cityCountry[0];
		String conferenceCountry = cityCountry[1];
		currentConference.setCity(conferenceCity);
		currentConference.setCountry(conferenceCountry);

		String conferenceAddress = aboutPage.select("p a+ a").text();
		currentConference.setAddress(conferenceAddress);

		return currentConference;
	}

	@Override
	public ArrayList<String> getAuthors() throws IOException {
		return extractAuthors(fetchWebpages(startURLAuthors));
	}

	@Override
	public ArrayList<String> getPaperTitles() throws IOException {
		return extractPapers(fetchWebpages(startURLPaper));
	}

	@Override
	public ArrayList<ArrayList<String>> getPaperAuthor() throws IOException {
		return extractPaperAuthor(fetchWebpages(startURLPaper));
	}
}
