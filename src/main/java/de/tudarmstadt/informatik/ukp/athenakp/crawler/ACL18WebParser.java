package de.tudarmstadt.informatik.ukp.athenakp.crawler;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.tudarmstadt.informatik.ukp.athenakp.crawler.CrawlerToolset.PaperStore;
import de.tudarmstadt.informatik.ukp.athenakp.database.models.Conference;

/**
 * A class, which holds the capability to return a List of all authors, which
 * wrote a paper in the frame of the ACL'18 conference
 *
 * @author Jonas Hake, Julian Steitz, Daniel Lehmann
 */
class ACL18WebParser extends AbstractCrawler{

	private String startURLAuthors;
	private String startURLPaper;
	private String schedulePage = "https://acl2018.org/programme/schedule/";
	private String aboutPage = "https://acl2018.org/";

	/**
	 * Only parses in the given year range. If only one year is needed, use the same input for both
	 * @param beginYear The first year to get data from
	 * @param endYear The last year to get data from
	 */
	public ACL18WebParser(String beginYear, String endYear)
	{
		startURLAuthors = String.format("https://aclanthology.coli.uni-saarland.de/catalog/facet/author?"// get a list of all authors
				+ "commit=facet.page=1&"// get first page of search
				+ "facet.sort=index&" // sort author list alphabetically
				+ "range[publish_date][begin]=%s&range[publish_date][end]=%s",// limits date of publishing
				beginYear, endYear);
		startURLPaper = String.format("https://aclanthology.coli.uni-saarland.de/catalog?per_page=100&range[publish_date][begin]=%s&range[publish_date][end]=%s&search_field=title", beginYear, endYear);
	}

	/**
	 * Fetches the given webpage, and follows the link, which contains 'Next' as long as
	 * there is one. The method returns a list of all visited webpages
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
		System.out.println("Done fetching webpages.");
		return docs;
	}

	/**
	 * Extracts all authors from a given list of webpages, which are in the ACL
	 * search form (e.g. {@link here
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
	 * Extracts all papers from a given list of webpages, which are in the ACL search
	 * form (e.g. {@link here
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
	 * Extracts all papers and authors from a given list of webpages, which are in
	 * the ACL search form (e.g. {@link here
	 * https://aclanthology.coli.uni-saarland.de/catalog/facet/author?commit=facet.page%3D1&facet.page=1})
	 *
	 * @param a list of webpages
	 * @return a list of names
	 */
	private ArrayList<ArrayList<String>> extractPaperAuthor(List<Document> webpages) {
		ArrayList<ArrayList<String>> paperList = new ArrayList<ArrayList<String>>();
		for (Document doc : webpages) {
			Elements paperListElements = doc.select("h5.index_title");
			for (Element elmnt : paperListElements) {
				ArrayList<String> paperAuthorList = new ArrayList<String>();// VOLUMES/Overview-PDFs are also part of the search-result and removed here
				if (!elmnt.text().contains("VOLUME")) {
					// add Paper info
					PaperStore store = new PaperStore();

					store.title = elmnt.text();
					extractPaperRelease(elmnt, store);
					paperAuthorList.add(store.toString());
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

	/**
	 * Extracts the release year + month of the given paper and stores it in the given PaperStore
	 * @param paper The web element of the paper to get the release year+month of
	 * @param store The {@link PaperStore} object to save the release year+month in
	 */
	private void extractPaperRelease(Element paper, PaperStore store) {
		try {
			Document doc = Jsoup.connect("https://aclanthology.coli.uni-saarland.de" + paper.select("a").attr("href")).get();
			ArrayList<Element> data = doc.select(".dl-horizontal").get(0).children();

			for(int i = 0; i < data.size(); i++) {
				if(data.get(i).text().startsWith("Month")) {
					store.month = data.get(i + 1).text();

					if(store.month.contains("-")) //some papers have a release month of e.g. "October-November", assume the first month as the release month
						store.month = store.month.split("-")[0];

					store.month = "" + CrawlerToolset.getMonthIndex(store.month);

					if(store.month.equals("-1"))
						store.month = "1"; //resort to january if no month is found
				}
				else if(data.get(i).text().startsWith("Year")) {
					store.year = data.get(i + 1).text().substring(0, 4); //hope that every year is given in 1234 format
				}
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * A method which returns a Conference instance with its name, location and start and end date set
	 * scrapes the aboutPage of ACL2018 for its information and employs String conversion found in CrawlerToolset
	 * if an IO Exception occurs, it returns an empty Conference instance
	 * @return a Conference instance with its name, location and start and end date set
	 * @throws IOException if Jsoup.connect fails
	 * @author Julian Steitz
	 */
	@Override
	public Conference getConferenceInformation() throws IOException {
		Conference currentConference = new Conference();
		Document aboutPage = Jsoup.connect(this.aboutPage).get();
		String conferenceName = aboutPage.select(".site-title a").text();
		currentConference.setName(conferenceName);

		/*		Useful for people who want to incorporate exact times
		String conferenceStartTimeInformation = schedulePage.select(".day-wrapper:nth-child(1) " +
				".overview-item:nth-child(1) .start-time").text();
		String conferenceEndTimeInformation = schedulePage.select(".day-wrapper:nth-child(6) " +
				".overview-item~ .overview-item+ .overview-item .start-time").text();

		LocalTime conferenceStartTime = crawlerToolset.acl2018ConvertStringToTime(conferenceStartTimeInformation);
		LocalTime conferenceEndTime = crawlerToolset.acl2018ConvertStringToTime(conferenceEndTimeInformation);*/

		String cityCountryInformation = aboutPage.select("p:nth-child(1) a:nth-child(1)").text();
		String dateAndLocationString = aboutPage.select(".sub-title-extra").text();
		LocalDate conferenceStartDate = CrawlerToolset.acl2018ConvertStringToDateRange(dateAndLocationString)[0];
		LocalDate conferenceEndDate = CrawlerToolset.acl2018ConvertStringToDateRange(dateAndLocationString)[1];
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
		System.out.println("Fetching webpages...");
		List<Document> webpages = fetchWebpages(startURLPaper);
		System.out.println("Preparing data and starting 4 scraper threads...");
		int quarterSize = (int)Math.ceil(webpages.size() / 4);
		List<Document> input1 = webpages.subList(0, quarterSize);
		List<Document> input2 = webpages.subList(quarterSize, quarterSize * 2);
		List<Document> input3 = webpages.subList(quarterSize * 2, quarterSize * 3);
		List<Document> input4 = webpages.subList(quarterSize * 3, webpages.size());
		ArrayList<ArrayList<String>> result = new ArrayList<>();
		ExecutorService executor = Executors.newFixedThreadPool(4);
		Future<ArrayList<ArrayList<String>>> f1 = executor.submit(() -> {return extractPaperAuthor(input1);});
		Future<ArrayList<ArrayList<String>>> f2 = executor.submit(() -> {return extractPaperAuthor(input2);});
		Future<ArrayList<ArrayList<String>>> f3 = executor.submit(() -> {return extractPaperAuthor(input3);});
		Future<ArrayList<ArrayList<String>>> f4 = executor.submit(() -> {return extractPaperAuthor(input4);});
		System.out.println("Waiting for thread results...");

		try {
			result.addAll(f1.get());
			result.addAll(f2.get());
			result.addAll(f3.get());
			result.addAll(f4.get());
			System.out.println("Gathered all results!");
		}
		catch(InterruptedException | ExecutionException e) {
			System.err.println("Error while gathering results!");
			e.printStackTrace();
		}

		executor.shutdown();
		return result;
	}
}
