package de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.allenai.scienceparse.ExtractedMetadata;
import org.allenai.scienceparse.Parser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.JsoupHelper;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Conference;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Event;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.EventCategory;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.EventPart;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Paper;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Person;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.ScheduleEntry;

/**
 * A class, which holds the capability to return a list of all authors, who
 * wrote a paper in the frame of the ACL'18 conference
 *
 * @author Jonas Hake, Julian Steitz, Daniel Lehmann
 */
class ACLWebCrawler extends AbstractCrawler {

	private static Logger logger = LogManager.getLogger(ACLWebCrawler.class);
	private String startURLAuthors;
	private String startURLPaper;
	private String schedulePage = "https://acl2018.org/programme/schedule/";
	private String aboutPage = "https://acl2018.org/";
	private String[] conferences;
	private Map<String, Paper> papers = new HashMap<>(); // title, corresponding paper
	private List<Paper> createdPapers = new ArrayList<>();
	private List<Person> createdPersons = new ArrayList<>();
	private int beginYear = 0;
	private int endYear = 0;

	// If this is set true: Before any new paper or person is created, it is checked
	// whether a paper/person
	// with the same title/name already exists in the DB. If a match is found, reuse
	// the paper/person from the DB
	// To prevent interferences between threads, parallelization is disabled
	// This decelerates the parsing process significantly and may be quite unstable.
	// Use with caution
	private boolean runWithDuplicateAvoidance = false;

	/**
	 * Only parses in the given year range. If only one year is needed, use the same
	 * input for both
	 * 
	 * @param beginYear   The first year to get data from
	 * @param endYear     The last year to get data from
	 * @param conferences The abbreviations (see {@link https://aclanthology.info/})
	 *                    of the conferences to scrape papers/authors from. null to
	 *                    scrape all. Does not work when only scraping authors
	 */
	public ACLWebCrawler(int beginYear, int endYear, String... conferences) {
		startURLAuthors = String.format("https://aclanthology.coli.uni-saarland.de/catalog/facet/author?"// get a list
																											// of all
																											// authors
				+ "commit=facet.page=1&"// get first page of search
				+ "facet.sort=index&" // sort author list alphabetically
				+ "range[publish_date][begin]=%s&range[publish_date][end]=%s", // limits date of publishing
				beginYear, endYear);
		startURLPaper = "paper";// String.format("https://aclanthology.coli.uni-saarland.de/catalog?per_page=100&range[publish_date][begin]=%s&range[publish_date][end]=%s&search_field=title",
								// beginYear, endYear);

		if (beginYear != 0) {
			this.beginYear = beginYear;
		}
		if (endYear != 0) {
			this.endYear = endYear;
		}

		if (conferences != null)
			this.conferences = conferences;
		else
			this.conferences = new String[0];
	}

	private Set<String> get_links(String url) {
		Set<String> uniqueURL = new HashSet<String>();

		try {
			Document doc = Jsoup.connect(url).userAgent("Mozilla").get();
			Elements links = doc.select("a");

			if (links.isEmpty()) {

				// TODO: Exeption einfügen
				return uniqueURL;
			}

			links.stream().map((link) -> link.attr("abs:href")).forEachOrdered((this_url) -> {

				uniqueURL.add(this_url);
			}
			// System.out.println(this_url);
			// if (add && this_url.contains(my_site)) {
			// System.out.println(this_url);
			// get_links(this_url);
			// }
			);

		} catch (IOException ex) {
			System.out.print("Exeption");
		}
		return uniqueURL;
	}

	private static boolean stringContainsItemFromList(String inputStr, String[] items) {
		return Arrays.stream(items).parallel().anyMatch(inputStr::contains);
	}

	private Set<String> selector(Set<String> all, String[] conferences, int from, int to) {

		Set<String> selection = new HashSet<String>();

		if (from == 0) {
			from = 2018;
		}
		if (to == 0) {
			to = 2018;
		}

		final int from2 = from;
		final int to2 = to;

		for (int i = 0; i < conferences.length; i++) {

			conferences[i] = conferences[i] + "-";
		}

		if (conferences.length != 0) {
			selection = all.stream()
					.filter(p -> stringContainsItemFromList(p, conferences) && containsYear(p, from2, to2))
					.collect(Collectors.toSet());

		} else {
			selection = all.stream().filter(p -> containsYear(p, from2, to2)).collect(Collectors.toSet());

		}
		return selection;

	}

	private boolean containsYear(String url, int from, int to) {
		for (int i = from; i <= to; i++) {

			if (url.contains(Integer.toString(i))) {
				return true;
			}
		}
		return false;

	}

	/**
	 * Fetches the given webpage, and follows the link, which contains 'Next' as
	 * long as there is one. The method returns a list of all visited webpages
	 *
	 * Works only with a search site from aclanthology.coli.uni-saarland.de
	 *
	 * @param startURL the URL of the webpage, where the crawler starts
	 * @return the list of visited webpages in form of a Jsoup document
	 * @throws IOException in case the connection is faulty and / or not present
	 */
	private ArrayList<Document> fetchWebpages(String startURL) throws IOException {

		HashSet<String> allURLs = new HashSet<String>();
		Set<String> selectedURLs = new HashSet<String>();

		ArrayList<Document> webPages = new ArrayList<Document>();

		Set<String> conferenceLinks = get_links("https://aclweb.org/anthology/events/");
		Set<String> converenceURLs = conferenceLinks.stream()
				.filter(p -> p.contains("https://aclweb.org/anthology/events/")).collect(Collectors.toSet());

		converenceURLs = selector(converenceURLs, this.conferences, this.beginYear, this.endYear);

		for (String s : converenceURLs) {

			allURLs.addAll(get_links(s));

			System.out.println(allURLs.size());
		}

		if (startURL.equals("paper")) {
			selectedURLs = allURLs.stream()
					.filter(p -> p.contains("https://aclweb.org/anthology/papers/") && !p.endsWith(".bib"))
					.collect(Collectors.toSet());

		} else {

			selectedURLs = allURLs.stream().filter(p -> p.contains("https://aclweb.org/anthology/people/"))
					.collect(Collectors.toSet());

		}

		for (String s : selectedURLs) {
        	webPages.add( Jsoup.connect(s).get());
//			Connection.Response resp = Jsoup.connect(s) //
//					.timeout(20000) //
//					.method(Connection.Method.GET) //
//					.execute();
//			webPages.add(((Connection) resp).get());
		}
		return webPages;

		/**
		 * Jsoup.connect
		 * 
		 * logger.info("Fetching webpages starting from \"{}\"...", startURL);
		 * ArrayList<Document> docs = new ArrayList<>();
		 * docs.add(JsoupHelper.connect(startURL)); // find link to next page, if not
		 * found end loop boolean nextSiteExist = true; while (nextSiteExist) {
		 * nextSiteExist = false; // find the link to the next page Elements links =
		 * docs.get(docs.size() - 1).select("a[href]"); List<String> linkTexts =
		 * links.eachText(); int idxOfLink = -1; for (String lnktxt : linkTexts) { if
		 * (lnktxt.contains("Next")) { nextSiteExist = true; idxOfLink =
		 * linkTexts.indexOf(lnktxt); break; } } // add next page to doc list if
		 * (nextSiteExist) { Document nxtDoc =
		 * JsoupHelper.connect(links.get(idxOfLink).absUrl("href")); docs.add(nxtDoc); }
		 * } logger.info("Done fetching webpages!"); return docs;
		 **/
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<Person> getAuthors() throws IOException {
		logger.info("Gathering all authors in the given year range...");
		ArrayList<Person> persons = extractAuthors(fetchWebpages(startURLAuthors));
		logger.info("Done!");
		return persons;
	}

	/**
	 * Extracts all authors from a given list of webpages, which are in the ACL
	 * search form (e.g. <a href=
	 * "https://aclanthology.coli.uni-saarland.de/catalog/facet/author?commit=facet.page%3D1&facet.page=1">here</a>)
	 *
	 * @param webpages a list of webpages
	 * @return a list of authors with the name field set
	 */
	private ArrayList<Person> extractAuthors(ArrayList<Document> webpages) {
		logger.info("Scraping author pages...");
		ArrayList<Person> authors = new ArrayList<>();
		// extract the authors from all webpages
		for (Document doc : webpages) {

			Element authorNameElement = doc.select("#title").get(0);

			// Elements authorListElements = doc.select("li");// authors are the only <li>
			// elements on the Page

			// for (Element elmnt : authorListElements) {

			Person author = runWithDuplicateAvoidance
					? Person.findOrCreateDbOrList(null, authorNameElement.text(), createdPersons)
					: new Person();
			if (runWithDuplicateAvoidance) {
				createdPersons.add(author);
			}

			author.setFullName(authorNameElement.text());
			authors.add(author);
			// }
		}
		logger.info("Done scraping!");
		return authors;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<Paper> getPapers() throws IOException {
		logger.info("Gathering all papers in the given year range...");
		ArrayList<Paper> papers = extractPapers(fetchWebpages(startURLPaper));
		logger.info("Done!");
		return papers;
	}

	/**
	 * Extracts all papers from a given list of webpages, which are in the ACL
	 * search form (e.g. <a href=
	 * "https://aclanthology.coli.uni-saarland.de/catalog/facet/author?commit=facet.page%3D1&facet.page=1">here</a>)
	 *
	 * @param webpages a list of webpages
	 * @return a list of papers
	 */
	private ArrayList<Paper> extractPapers(ArrayList<Document> webpages) {
		logger.info("Scraping paper pages...");
		ArrayList<Paper> paperList = new ArrayList<>();
		// extract the authors from all webpages
		for (Document doc : webpages) {
			// if no conferences were given, let the papers through. else see if the
			// conference of the paper is given in the launch arg
			// Elements paperListElements = doc.select("h5.index_title");// papers are all
			// <h5 class = "index_title">
			// innerLoop: for (Element elmnt : paperListElements) {
			if (!doc.title().contains("VOLUME")) {// VOLUMES/Overview-Pdfs are also part of the search-result and
													// removed here
				// check is not earlier because the elmnt is needed
				if (conferences.length != 0 && !shouldSavePaper(doc)) // TODO: verstehen was sie hier mit den
																		// Konferenzen wollen
					continue; // innerLoop; //label is not needed necessarily, but helps readability

				Paper paper = runWithDuplicateAvoidance ? Paper.findOrCreateDbOrList(null, doc.title(), createdPapers)
						: new Paper();
				;
				if (runWithDuplicateAvoidance) {
					createdPapers.add(paper);
				}

			    Elements titleElement=doc.select("#title > a");


				paper.setTitle(titleElement.get(0).text());
				paperList.add(paper);
			}
		}

		logger.info("Done scraping!");
		return paperList;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<Paper> getPaperAuthor() throws IOException {
		logger.info("Gathering all paper author relationships...");
		List<Document> webpages = fetchWebpages(startURLPaper);
		logger.info("Preparing data and starting 4 scraper threads...");
		// in the following lines the list gets split into 4 roughly equal parts so that
		// each list part can be handled in a seperate thread (it's faster this way)
		int quarterSize = (int) Math.ceil(webpages.size() / 4);
		List<Document> input1 = webpages.subList(0, quarterSize);
		List<Document> input2 = webpages.subList(quarterSize, quarterSize * 2);
		List<Document> input3 = webpages.subList(quarterSize * 2, quarterSize * 3);
		List<Document> input4 = webpages.subList(quarterSize * 3, webpages.size());
		ArrayList<Paper> result = new ArrayList<>();

		// If duplicate avoidance is enabled, do not use threading, as the separate
		// threads would interfere each other
		if (runWithDuplicateAvoidance) {
			try {
				result.addAll(extractPaperAuthor(input1));
				logger.info("Finished 1 / 4");
				result.addAll(extractPaperAuthor(input2));
				logger.info("Finished 2 / 4");
				result.addAll(extractPaperAuthor(input3));
				logger.info("Finished 3 / 4");
				result.addAll(extractPaperAuthor(input4));
				logger.info("Finished 4 / 4");
			} catch (Exception e) { // thread exceptions
				logger.error("Error while gathering results!", e);
			}
			return result;
		}

		// setup and start those threads
		ExecutorService executor = Executors.newFixedThreadPool(4);
		Future<ArrayList<Paper>> f1 = executor.submit(() -> extractPaperAuthor(input1));
		Future<ArrayList<Paper>> f2 = executor.submit(() -> extractPaperAuthor(input2));
		Future<ArrayList<Paper>> f3 = executor.submit(() -> extractPaperAuthor(input3));
		Future<ArrayList<Paper>> f4 = executor.submit(() -> extractPaperAuthor(input4));
		logger.info("Waiting for thread results...");

		// wait for the thread results and add all of those to the result list (.get()
		// is blocking)
		try {
			result.addAll(f1.get());
			result.addAll(f2.get());
			result.addAll(f3.get());
			result.addAll(f4.get());
			logger.info("Done gathering all paper and author results!");
		} catch (Exception e) { // thread exceptions
			logger.error("Error while gathering results!", e);
		}

		executor.shutdown();
		return result;
	}

	/**
	 * Extracts all papers and authors from a given list of webpages, which are in
	 * the ACL search form (e.g. <a href=
	 * "https://aclanthology.coli.uni-saarland.de/catalog/facet/author?commit=facet.page%3D1&facet.page=1">here</a>)
	 *
	 * @param webpages a list of webpages
	 * @return a list of papers
	 */
	private ArrayList<Paper> extractPaperAuthor(List<Document> webpages) {
		logger.info("Scraping webpages for paper author relationships...");
		ArrayList<Paper> paperList = new ArrayList<>();
		org.allenai.scienceparse.Parser parser = null;
		PDFTextStripper stripper = null;
		de.tudarmstadt.informatik.ukp.athena.knowledgebase.PDFParser.Parser myparse = new de.tudarmstadt.informatik.ukp.athena.knowledgebase.PDFParser.Parser();

		try {
			//parser = Parser.getInstance();
			stripper = new PDFTextStripper();
		} catch (Exception e) {
			e.printStackTrace();
		}
		for (Document doc : webpages) {
			// Elements paperListElements = doc.select("h5.index_title");
			// innerLoop: for (Element elmnt : paperListElements) {
			Elements paperInformationElements = doc.select("#main > div > div.col.col-lg-10.order-2 > dl > dd");
			if (!doc.title().contains("VOLUME")) {
				// check is not earlier because the elmnt is needed
				if (conferences.length != 0 && !shouldSavePaper(doc))
					continue; // innerLoop; //label is not needed necessarily, but helps readability

				// add paper info
				// clean up the titles in the form of [C18-1017] Simple Neologism Based Domain
				// Independe...
				// C18-1017 would be the anthology - we remove [] because they convey no meaning
				// String rawTitle = elmnt.text();
				// String[] splitRawTitle = rawTitle.split(" ", 2);

			    Elements titleElement=doc.select("#title > a");

				String paperTitle = titleElement.get(0).text();// splitRawTitle[1];
				String anthology = paperInformationElements.get(0).text();// splitRawTitle[0].replace("[",
																			// "").replace("]", "");

				Paper paper = runWithDuplicateAvoidance ? Paper.findOrCreateDbOrList(null, paperTitle, createdPapers)
						: new Paper();
				if (runWithDuplicateAvoidance) {
					createdPapers.add(paper);
				}

				paper.setTitle(paperTitle);
				paper.setAnthology(anthology);
				String remoteLink = "http://aclweb.org/anthology/" + anthology;
				paper.setRemoteLink(remoteLink); // wow that was easy
				paper.setReleaseDate(extractPaperRelease(doc));
				try {
					URL urli = new URL(remoteLink);
					ExtractedMetadata meDa = myparse.scienceParse(parser, urli);
					String plainText = myparse.plainParse(stripper, urli);
					//if(meDa == null) continue;
					paper.setPaperPlainText(plainText);
					paper.setPaperAbstract(meDa.abstractText);
				} catch (MalformedURLException e) {
					System.out.println("Parser abgestuerzt. Leere PDF-File? ");
					System.out.println("Fehlerhafter Link: " + remoteLink);
					e.printStackTrace();
				}
				// find authors and add them to a list

				Elements authorElements = doc.select("#main > p> a");// elmnt.parent().parent().children().select("span").select("a");
				for (Element authorEl : authorElements) {
					Person author = runWithDuplicateAvoidance
							? Person.findOrCreateDbOrList(null, authorEl.text(), createdPersons)
							: new Person();
					if (runWithDuplicateAvoidance) {
						createdPersons.add(author);
					}

					// because acl2018 seems to not employ prefixes (e.g. Prof. Dr.), we do not need
					// to scan them
					author.setFullName(authorEl.text());
					// set paper - author relation
					paper.addAuthor(author);
					// set author - paper relation
					author.addPaper(paper);
				}
				paperList.add(paper);
				papers.put(paper.getTitle(), paper);
				// }
			}
		}
		logger.info("Done scraping!");
		return paperList;
	}


	private ArrayList<Paper> getPaperAuthorEvent() throws IOException {
		ArrayList<Paper> paperList=new ArrayList<Paper>();


        Set<String> uniqueURL=get_links("https://aclweb.org/anthology/events/");
        System.out.println("Projekt läuft!");
        HashSet<String> uniqueConverenceURLs = uniqueURL.stream()
        	    .filter(p -> p.contains("https://aclweb.org/anthology/events/")).collect(Collectors.toCollection(HashSet::new));

        ArrayList<String> converenceURLs= selector(uniqueConverenceURLs,this.conferences,this.beginYear,this.endYear).stream().collect(Collectors.toCollection(ArrayList::new));;
        String[] array = converenceURLs.stream().toArray(n -> new String[n]);
        System.out.println(array.length);
        System.out.println(Arrays.toString(array));

        ArrayList<ArrayList<String>> eventsPerConference=new ArrayList<ArrayList<String>>();

        int i=0;
        for(String s : array) {
        	i++;
        eventsPerConference.add(get_links(s).stream()
        	    .filter(p -> p.contains("volumes")&&!p.contains(".bib")).collect(Collectors.toCollection(ArrayList::new)));

        if(i==1000) {
        break;
        }}

    	System.out.println(eventsPerConference.size());


        ArrayList<ArrayList<HashSet<String>>> paperPerEventPerConference=new ArrayList<ArrayList<HashSet<String>>>();


        for(ArrayList<String> events :eventsPerConference) {


        	ArrayList<HashSet<String>> urlsPerEvent  =new ArrayList<HashSet<String>>();
        	for(String l: events) {


        		urlsPerEvent.add(get_links(l).stream().filter(p -> p.contains("https://aclweb.org/anthology/papers/")&& !p.contains(".bib")).collect(Collectors.toCollection(HashSet::new)));
        	System.out.println("UrlsPerEvent:"+urlsPerEvent.size());
        	}


        	paperPerEventPerConference.add(urlsPerEvent);


        }


    	System.out.println(paperPerEventPerConference.size());


    	for(int x=0;x<converenceURLs.size();x++ ) {
    		Conference conference=new Conference();

        	Document conferenceSite=Jsoup.connect(converenceURLs.get(x)).get();


    		Elements confernceTitleElement= conferenceSite.select("#title");

			String conferenceTitle = confernceTitleElement.get(0).text();//splitRawTitle[1];


    		conference.setName(conferenceTitle);

    		for(int y=0; y<eventsPerConference.get(x).size();y++) {
    			Event event=new Event();//über eventsPerConference.get(x) scrapen

    			Document eventDocument=Jsoup.connect(eventsPerConference.get(x).get(y)).get();

    			Elements id= eventDocument.select("#main > div.row.acl-paper-details > div.col.col-lg-10.order-2 > dl > dd");



        		Elements titel= eventDocument.select("#title");

    			String titleString = titel.get(0).text();//splitRawTitle[1];

    			String monthString = id.get(1).text();//splitRawTitle[1];

    			String yearString = id.get(2).text();//splitRawTitle[1];

    			LocalDate date= null;

    			try {

    			int monthInt =monthToInt(monthString);
    			int yearInt=Integer.parseInt(yearString);

    			if(monthInt!=0) {
    				date = LocalDate.of(yearInt, monthInt, 1);
    				event.setBegin(date.atStartOfDay());
    				event.setEnd(date.atStartOfDay());
    			}

    			}catch(NumberFormatException e){
    				System.out.println("yearString: "+yearString);

    			}


    			String locationString = id.get(3).text();//splitRawTitle[1];
    			String[] locationArray=locationString.split(", ");



    			if(y==0) {


    			if(locationArray.length==2) {
    			conference.setCity(locationArray[0]);
    			conference.setCountry(locationArray[1]);
    			}

    			//conference.setId(id);

    			if(date !=null) {
    				conference.setBegin(date);
    				conference.setEnd(date);

    			}


    			}





    			//String idString = id.get(0).text();//splitRawTitle[1];
    			//String cityString = id.get(3).text();//splitRawTitle[1];



    			//event.setId(idString);
    			event.setTitle(titleString);
    			EventCategory category=getWorkshopType(titleString);
    			if(category!=null) {
    			event.setCategory(category);
    			}

    			//event.setConferenceName(conferenceTitel);



    			for(String s: paperPerEventPerConference.get(x).get(y)) {

    	        	try {
    	        		System.out.println(s);

						Document doc= Jsoup.connect(s).get();

						Elements paperInformationElements = doc.select("#main > div > div.col.col-lg-10.order-2 > dl > dd");
						if (!doc.title().contains("VOLUME")) {
							// check is not earlier because the elmnt is needed
							if (conferences.length != 0 && !shouldSavePaper(doc))
								continue; // innerLoop; //label is not needed necessarily, but helps readability

							// add paper info
							// clean up the titles in the form of [C18-1017] Simple Neologism Based Domain
							// Independe...
							// C18-1017 would be the anthology - we remove [] because they convey no meaning
							// String rawTitle = elmnt.text();
							// String[] splitRawTitle = rawTitle.split(" ", 2);


						    Elements titleElement=doc.select("#title > a");


							String paperTitle = titleElement.get(0).text();//doc.title();// splitRawTitle[1];
							String anthology = paperInformationElements.get(0).text();// splitRawTitle[0].replace("[",
																						// "").replace("]", "");

							Paper paper = runWithDuplicateAvoidance ? Paper.findOrCreateDbOrList(null, paperTitle, createdPapers)
									: new Paper();
							if (runWithDuplicateAvoidance) {
								createdPapers.add(paper);
							}

							paper.setTitle(paperTitle);
							paper.setAnthology(anthology);
							String remoteLink = "http://aclweb.org/anthology/" + anthology;
							paper.setRemoteLink(remoteLink); // wow that was easy
							paper.setReleaseDate(extractPaperRelease(doc));
							/**try {
								ExtractedMetadata meDa = scienceParse(parser, new URL(remoteLink));
								if(meDa == null) continue;
								String plaintext = "";
								for (org.allenai.scienceparse.Section sec : meDa.sections) {
									plaintext = plaintext + sec.text;
								}
								paper.setPaperPlainText(plaintext);
								paper.setPaperAbstract(meDa.abstractText);
								//im allenai parser zwischenergebnisse abfangen und pdfs schließen
								//treffen Mittwoch 10:00
							} catch (MalformedURLException e) {
								System.out.println("Parser abgestuerzt. Leere PDF-File? ");
								System.out.println("Fehlerhafter Link: " + remoteLink);
								e.printStackTrace();
							}**/
							// find authors and add them to a list

							Elements authorElements = doc.select("#main > p> a");// elmnt.parent().parent().children().select("span").select("a");
							for (Element authorEl : authorElements) {
								Person author = runWithDuplicateAvoidance
										? Person.findOrCreateDbOrList(null, authorEl.text(), createdPersons)
										: new Person();
								if (runWithDuplicateAvoidance) {
									createdPersons.add(author);
								}

								// because acl2018 seems to not employ prefixes (e.g. Prof. Dr.), we do not need
								// to scan them

								String linkAuthor=authorEl.attr("abs:href");

								try {
								Document docAuthor= Jsoup.connect(linkAuthor).get();

								Elements authorFirstNameElement = docAuthor.select("#title > span.font-weight-normal");
								Elements authorLastNameElement = docAuthor.select("#title > span.font-weight-bold");

								String firstName= authorFirstNameElement.text();
								String lastName= authorLastNameElement.text();

								author.setFirstName(firstName);
								author.setLastName(lastName);

								}catch(IOException e) {


								}

								author.setFullName(authorEl.text());
								// set paper - author relation
								paper.addAuthor(author);
								// set author - paper relation
								author.addPaper(paper);
								event.addPaper(paper);
			    	        	//event.addPaper(paper);
			    	        	paperList.add(paper);

							}

							// }
						}







    	        	} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

    			}


    			conference.addEvent(event);

    		}


    	}

		return paperList;
	}



	private int monthToInt(String month) {
		switch(month) {
		case "January": return 1;
		case "February": return 2;
		case "March": return 3;
		case "April": return 4;
		case "May": return 5;
		case "June": return 6;
		case "July": return 7;
		case "August": return 8;
		case "September": return 9;
		case "October": return 10;
		case "November": return 11;
		case "December": return 12;
			default: return 0;


		}

	}


	private EventCategory getWorkshopType(String workshopTitle) {
		if(workshopTitle.toLowerCase().contains("BREAK")) {return EventCategory.BREAK;}
		if(workshopTitle.toLowerCase().contains("CEREMONY")) {return EventCategory.CEREMONY;}
		if(workshopTitle.toLowerCase().contains("MEETING")) {return EventCategory.MEETING;}
		if(workshopTitle.toLowerCase().contains("PRESENTATION")) {return EventCategory.PRESENTATION;}
		if(workshopTitle.toLowerCase().contains("RECRUITMENT")) {return EventCategory.RECRUITMENT;}
		if(workshopTitle.toLowerCase().contains("SESSION")) {return EventCategory.SESSION;}
		if(workshopTitle.toLowerCase().contains("SOCIAL")) {return EventCategory.SOCIAL;}
		if(workshopTitle.toLowerCase().contains("TALK")) {return EventCategory.TALK;}
		if(workshopTitle.toLowerCase().contains("TUTORIAL")) {return EventCategory.TUTORIAL;}
		if(workshopTitle.toLowerCase().contains("WELCOME")) {return EventCategory.WELCOME;}
		if(workshopTitle.toLowerCase().contains("WORKSHOP")) {return EventCategory.WORKSHOP;}

		return null;
	}






	private ExtractedMetadata scienceParse(Parser parser, URL url) {
		ExtractedMetadata em = null;
		try {
			InputStream inputStream = getConnectionFromURL(url).getInputStream();
			em = parser.doParse(inputStream);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return em;
	}

	private static HttpURLConnection getConnectionFromURL(URL url) throws IOException {
		HttpURLConnection con = (HttpURLConnection) url.openConnection();
		con.connect();
		int responseCode = con.getResponseCode();
		if (responseCode < 400 && responseCode > 299) {
			String redirectUrl = con.getHeaderField("Location");
			try {
				URL newUrl = new URL(redirectUrl);
				con = getConnectionFromURL(newUrl);
			} catch (MalformedURLException e) {
				System.out.println(e.getMessage());
			}
		}
		return con;
	}

	/**
	 * Checks with the given {@link conferences} whether or not to save this paper
	 * into the database
	 * 
	 * @param paper The web element of the paper to check
	 * @return true if the paper should be saved into the database
	 */
	private boolean shouldSavePaper(Document doc) {

		/**
		 * Document doc =
		 * JsoupHelper.connect("https://aclanthology.coli.uni-saarland.de" +
		 * paper.select("a").attr("href")); ArrayList<Element> data =
		 * doc.select(".dl-horizontal").get(0).children(); //somewhere in those children
		 * is the venue with which to filter
		 * 
		 * //find it for(int i = 0; i < data.size(); i++) {
		 * if(data.get(i).text().startsWith("Venue")) { //the next line contains the
		 * venue String text = data.get(i + 1).text(); boolean contains = false;
		 * 
		 * //needed because some papers are published in multiple conferences innerLoop:
		 * for(String c : conferences) { if(text.contains(c)) { contains = true; break
		 * innerLoop; //no further processing needed } }
		 * 
		 * return contains; } }
		 **/
		return true;// false;
	}

	/**
	 * Extracts the release year + month of the given paper web element
	 * 
	 * @param paper The web element of the paper to get the release year+month of
	 * @return The paper's release date, null if errored
	 */
	private LocalDate extractPaperRelease(Document doc) {// Element paper) {
		// Document doc =
		// JsoupHelper.connect("https://aclanthology.coli.uni-saarland.de" +
		// paper.select("a").attr("href"));
		// ArrayList<Element> data = doc.select(".dl-horizontal").get(0).children();
		// //somewhere in those children is the date
		String year = "0";
		String month = "0";

		// find the different parts of the date
		// for(int i = 0; i < data.size(); i++) {
		// if(data.get(i).text().startsWith("Month")) { //the line contains the month
		Elements paperInformationElements = doc.select("#main > div > div.col.col-lg-10.order-2 > dl > dd");

		month = paperInformationElements.get(2).text();

		if (month.contains("-")) // some papers have a release month of e.g. "October-November", assume the first
									// month as the release month
			month = month.split("-")[0];

		month = "" + CrawlerToolset.getMonthIndex(month);

		if (month.equals("-1"))
			month = "1"; // resort to january if no month is found
		// }
		// else if(d.get(i).text().startsWith("Year")) { //the line contains the year
		try {
	        year = paperInformationElements.get(3).text().substring(0, 4); //hope that every year is given in 1234 format
	        int i=Integer.parseInt(year);
	        }catch(NumberFormatException n) {
	        year=Integer.toString(0);  
	          
	        }		
		// }
		// }
		
		return LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
	}

	/**
	 * A method which returns a conference instance with its name, location and
	 * start and end date set. Scrapes the about page of ACL2018 for its information
	 * and employs String conversion found in CrawlerToolset.
	 * 
	 * @return a conference instance with its name, location and start and end date
	 *         set, an empty conference instance if an IOException occured
	 * @throws IOException if Jsoup.connect fails
	 * @author Julian Steitz
	 */
	@Override
	public Conference getConferenceInformation() throws IOException {
		logger.info("Scraping conference information...");
		Conference currentConference = new Conference();
		Document aboutPage = JsoupHelper.connect(this.aboutPage);
		String conferenceName = aboutPage.select(".site-title a").text();
		currentConference.setName(conferenceName);

		/*
		 * Useful for people who want to incorporate exact times String
		 * conferenceStartTimeInformation =
		 * schedulePage.select(".day-wrapper:nth-child(1) " +
		 * ".overview-item:nth-child(1) .start-time").text(); String
		 * conferenceEndTimeInformation =
		 * schedulePage.select(".day-wrapper:nth-child(6) " +
		 * ".overview-item~ .overview-item+ .overview-item .start-time").text();
		 * 
		 * LocalTime conferenceStartTime =
		 * CrawlerToolset.acl2018ConvertStringToTime(conferenceStartTimeInformation);
		 * LocalTime conferenceEndTime =
		 * CrawlerToolset.acl2018ConvertStringToTime(conferenceEndTimeInformation);
		 */

		String cityCountryInformation = aboutPage.select("p:nth-child(1) a:nth-child(1)").text();
		String dateAndLocationString = aboutPage.select(".sub-title-extra").text();
		LocalDate conferenceStartDate = CrawlerToolset.acl2018ConvertStringToDateRange(dateAndLocationString)[0];
		LocalDate conferenceEndDate = CrawlerToolset.acl2018ConvertStringToDateRange(dateAndLocationString)[1];
		// Maybe we need to look at a timezone api? Probably not feasible to keep it
		// free, which is why it is set as
		// manual for now
		// TODO: talk about timezones and how to handle them
		// ZoneId timeZone = ZoneId.of("GMT+11");

		currentConference.setBegin(conferenceStartDate);
		currentConference.setEnd(conferenceEndDate);

		String[] cityCountry = cityCountryInformation.split(", ");
		String conferenceCity = cityCountry[0];
		String conferenceCountry = cityCountry[1];
		currentConference.setCity(conferenceCity);
		currentConference.setCountry(conferenceCountry);

		String conferenceAddress = aboutPage.select("p a+ a").text();
		currentConference.setAddress(conferenceAddress);

		logger.info("Done scraping!");
		return currentConference;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ArrayList<ScheduleEntry> getSchedule() throws IOException {
		logger.info("Scraping conference schedule...");
		ArrayList<ScheduleEntry> result = new ArrayList<>();
		logger.info("Preparing data and starting 5 scraper threads...");
		Element schedule = JsoupHelper.connect(schedulePage).select("#schedule").get(0);
		Elements days = schedule.select(".day-schedule");
		// threading :DD - takes about 1 minute 20 seconds without, 30 seconds with
		ExecutorService executor = Executors.newFixedThreadPool(5);
		Future<ArrayList<ScheduleEntry>> f1 = executor
				.submit(() -> parseFirstDay(days.get(0), new ArrayList<ScheduleEntry>()));
		Future<ArrayList<ScheduleEntry>> f2 = executor
				.submit(() -> parseOtherDays(days.get(1), new ArrayList<ScheduleEntry>()));
		Future<ArrayList<ScheduleEntry>> f3 = executor
				.submit(() -> parseOtherDays(days.get(2), new ArrayList<ScheduleEntry>()));
		Future<ArrayList<ScheduleEntry>> f4 = executor
				.submit(() -> parseOtherDays(days.get(3), new ArrayList<ScheduleEntry>()));
		Future<ArrayList<ScheduleEntry>> f5 = executor.submit(ACL18WorkshopParser::parseWorkshops);
		logger.info("Waiting for thread results...");

		try {
			result.addAll(f1.get());
			result.addAll(f2.get());
			result.addAll(f3.get());
			result.addAll(f4.get());
			result.addAll(f5.get());
			logger.info("Done scraping!");
		} catch (InterruptedException | ExecutionException e) {
			logger.error("Error collecting results!", e);
		}

		executor.shutdown();
		return result;
	}

	/**
	 * Parses ACL 2018's first days' schedule (seperate method because it contains a
	 * special case)
	 * 
	 * @param day    The day element of the website
	 * @param result The resulting arraylist with the complete events of the first
	 *               day
	 * @return An ArrayList containing the first days' schedule
	 */
	private ArrayList<ScheduleEntry> parseFirstDay(Element day, ArrayList<ScheduleEntry> result) {
		String[] monthDay = day.selectFirst(".day").text().split(":")[1].trim().split(" "); // the text has the form of
																							// "Sunday: July 15"
		Elements tr = day.select("tr");

		// looping through all table rows, each contains an event
		for (int i = 0; i < tr.size(); i++) {
			Element el = tr.get(i);
			Event event = new Event();

			addGeneralEventInfo(el, event, monthDay);

			// special case
			if (i + 1 < tr.size() && tr.get(i + 1).hasClass("poster-session-row")) {
				Element row = tr.get(++i);
				Elements tutorials = row.select(".poster-name");

				// the table row might contain several tutorials in the same timeframe, so loop
				// through those
				for (Element eventEl : tutorials) {
					EventPart eventPart = new EventPart();

					eventPart.setTitle(eventEl.text());
					event.addEventPart(eventPart);
				}
			}

			result.add(event);
		}

		return result;
	}

	/**
	 * Parses ACL 2018's other days' schedule
	 * 
	 * @param day    The day element of the website
	 * @param result The arraylist to write the data into
	 * @return The resulting arraylist with the complete events of the given day
	 */
	private ArrayList<ScheduleEntry> parseOtherDays(Element day, ArrayList<ScheduleEntry> result) {
		String[] monthDay = day.selectFirst(".day").text().split(":")[1].trim().split(" "); // the text has the form of
																							// "Sunday: July 15"
		Elements tr = day.select("tr");

		// looping through all table rows, each contains an event
		for (int i = 0; i < tr.size(); i++) {
			Element el = tr.get(i);
			Event event = new Event();

			addGeneralEventInfo(el, event, monthDay);

			if (event.getCategory() == EventCategory.PRESENTATION)
				addOralPresentationInfo(tr.get(++i).select(".conc-session"), tr.get(++i).select(".session-location"),
						tr.get(++i).select(".session-details"), event);
			else if (event.getCategory() == EventCategory.SESSION)
				addPosterSessionInfo(tr.get(++i).select(".poster-sub-session"), event);

			result.add(event);
		}

		return result;
	}

	/**
	 * Adds general information about an event, such as name, timeframe, location
	 * etc.
	 * 
	 * @param el       The event header element of the website
	 * @param event    The event to write the information to
	 * @param monthDay The month (index 0) and day (index 1) where this event
	 *                 happens
	 */ // more than 40 lines because this method does one thing (add general
		// information about an even) and splitting it up would worsen readability
	private void addGeneralEventInfo(Element el, Event event, String[] monthDay) {
		// only try to extract the information when the table row is the header of an
		// event and is not the more detailed description
		// the header is something like "09:00-10:00 Welcome Session & Presidential
		// Address PLENARY, MCEC"
		if (el.id().startsWith("session")) {
			// start extracting the data from the table row
			String[] time = el.select(".session-times").text().split("–"); // NOT A HYPHEN!!! IT'S AN 'EN DASH'
			String[] begin = time[0].split(":");
			String[] end = time[1].split(":");
			String title = el.select(".session-name").text();
			// sometimes there is a suffix (after a ':'), use it as the event description
			// e.g. Oral Presentations [title]: Long Papers and TACL Papers) [suffix aka
			// description]
			String desc = el.select(".session-suffix").text();
			Elements place = el.select(".session-location");
			EventCategory category = null;

			// the title string contains everything, so remove the description to avoid
			// duplicate data
			if (!desc.isEmpty())
				title = title.replace(desc, "");

			// set the extracted data
			event.setBegin(LocalDateTime.of(2018, CrawlerToolset.getMonthIndex(monthDay[0]),
					Integer.parseInt(monthDay[1]), Integer.parseInt(begin[0]), Integer.parseInt(begin[1])));
			event.setEnd(LocalDateTime.of(2018, CrawlerToolset.getMonthIndex(monthDay[0]),
					Integer.parseInt(monthDay[1]), Integer.parseInt(end[0]), Integer.parseInt(end[1])));
			event.setTitle(title);
			event.setPlace(place.isEmpty() ? "?" : (place.get(0).text().isEmpty() ? "?" : place.get(0).text()));
			event.setDescription(desc);
			title = title.toLowerCase(); // easier to work with this way

			// decide which kind of category this event belongs to
			if (title.startsWith("tutorial"))
				category = EventCategory.TUTORIAL;
			else if (title.contains("welcome"))
				category = EventCategory.WELCOME;
			else if (title.startsWith("lunch") || title.contains("break"))
				category = EventCategory.BREAK;
			else if (title.contains("oral"))
				category = EventCategory.PRESENTATION;
			else if (title.contains("poster"))
				category = EventCategory.SESSION;
			else if (title.contains("recruitment"))
				category = EventCategory.RECRUITMENT;
			else if (title.contains("talk"))
				category = EventCategory.TALK;
			else if (title.contains("meeting"))
				category = EventCategory.MEETING;
			else if (title.contains("social"))
				category = EventCategory.SOCIAL;
			else if (title.contains("award") || title.contains("achievement"))
				category = EventCategory.CEREMONY;

			event.setCategory(category);
		}
	}

	/**
	 * Adds all available information about an oral presentation section
	 * 
	 * @param eventParts    The elements containing event part information
	 * @param rooms         The elements containing room information per event part
	 * @param presentations The elements containing the presentations per event part
	 * @param event         The event to write the information to
	 */
	private void addOralPresentationInfo(Elements eventParts, Elements rooms, Elements presentations, Event event) {
		// looping through the different columns of the OP table
		for (int i = 0; i < presentations.size(); i++) { // seems like event parts, rooms, and presentations all have
															// the same size, always
			Element evEl = eventParts.get(i);
			String evTitle = evEl.selectFirst(".conc-session-name").text();
			String evPlace = rooms.get(i).text();

			// looping through the rows of the current column
			for (Element subEl : presentations.get(i).select(".talk")) {
				EventPart eventPart = new EventPart();
				String[] sessTime = subEl.selectFirst(".talk-time").text().split(":");
				LocalDateTime sessStart = LocalDateTime.of(event.getBegin().toLocalDate(),
						LocalTime.of(Integer.parseInt(sessTime[0]), Integer.parseInt(sessTime[1])));
				LocalDateTime sessEnd = sessStart.plusMinutes(25);
				String sessPaperTitle = subEl.selectFirst(".talk-title").text();

				// set the data
				eventPart.setTitle(evTitle);
				event.addPaper(papers.get(sessPaperTitle));
				eventPart.setBegin(sessStart);
				eventPart.setEnd(sessEnd);
				eventPart.setPlace(evPlace);
				event.addEventPart(eventPart);
			}
		}
	}

	/**
	 * Adds all available information about a poster session
	 * 
	 * @param eventParts The elements containing the event part information
	 * @param event      The event to add the information to
	 */
	private void addPosterSessionInfo(Elements eventParts, Event event) {
		// looping through the poster sessions
		for (Element sessEl : eventParts) {
			EventPart eventPart = new EventPart();
			String[] evTitleDesc = sessEl.selectFirst(".poster-session-name").text().split(":");
			String evTitle = evTitleDesc[0].trim();
			String evDesc = evTitleDesc[1].trim();

			// looping through all papers that are part of this PS
			for (Element subEl : sessEl.select(".poster-name")) {
				String paperTitle = subEl.select("a").get(1).text().trim(); // let's hope it's always the second :D

				event.addPaper(papers.get(paperTitle));
			}

			// set the data
			eventPart.setTitle(evTitle);
			eventPart.setDescription(evDesc);
			eventPart.setBegin(event.getBegin());
			eventPart.setEnd(event.getEnd());
			eventPart.setPlace(event.getPlace());
			event.addEventPart(eventPart);
		}
	}

	@Override
	public void close() {
		papers.clear();
	}
}