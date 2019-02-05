package de.tudarmstadt.informatik.ukp.athenakp.crawler;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import de.tudarmstadt.informatik.ukp.athenakp.database.models.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * A class, which holds the capability to return a List of all authors, which
 * wrote a paper in the frame of the ACL'18 conference
 *
 * @author Jonas Hake, Julian Steitz, Daniel Lehmann
 */
class ACLWebCrawler extends AbstractCrawler {

	private String startURLAuthors;
	private String startURLPaper;
	private String schedulePage = "https://acl2018.org/programme/schedule/";
	private String aboutPage = "https://acl2018.org/";
	private String workshopPage = "https://acl2018.org/workshops/";

	/**
	 * Only parses in the given year range. If only one year is needed, use the same input for both
	 * @param beginYear The first year to get data from
	 * @param endYear The last year to get data from
	 */
	public ACLWebCrawler(String beginYear, String endYear) {
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

	@Override
	public ArrayList<Person> getAuthors() throws IOException {
		return extractAuthors(fetchWebpages(startURLAuthors));
	}

	/**
	 * Extracts all authors from a given list of webpages, which are in the ACL
	 * search form (e.g. <a href="https://aclanthology.coli.uni-saarland.de/catalog/facet/author?commit=facet.page%3D1&facet.page=1">here</a>)
	 *
	 * @param webpages a list of webpages
	 * @return a list of authors with the name field set
	 */
	private ArrayList<Person> extractAuthors(ArrayList<Document> webpages) {
		ArrayList<Person> authors = new ArrayList<>();
		// extract the authors from all webpages
		for (Document doc : webpages) {
			Elements authorListElements = doc.select("li");// authors are the only <li> Elements on the Page
			for (Element elmnt : authorListElements) {
				Person author = new Person();

				author.setFullName(elmnt.child(0).ownText());
				authors.add(author);
			}
		}
		return authors;
	}

	@Override
	public ArrayList<Paper> getPaperTitles() throws IOException {
		return extractPapers(fetchWebpages(startURLPaper));
	}

	/**
	 * Extracts all papers from a given list of webpages, which are in the ACL search
	 * form (e.g. <a href="https://aclanthology.coli.uni-saarland.de/catalog/facet/author?commit=facet.page%3D1&facet.page=1">here</a>)
	 *
	 * @param webpages a list of webpages
	 * @return a list of papers
	 */
	private ArrayList<Paper> extractPapers(ArrayList<Document> webpages) {
		ArrayList<Paper> paperList = new ArrayList<>();
		// extract the authors from all webpages
		for (Document doc : webpages) {
			Elements paperListElements = doc.select("h5.index_title");// papers are all <h5 class = "index_title">
			for (Element elmnt : paperListElements) {
				if (!elmnt.text().contains("VOLUME")) {// VOLUMES/Overview-Pdfs are also part of the search-result and removed here
					Paper paper = new Paper();

					paper.setTitle(elmnt.text());
					paperList.add(paper);
				}
			}
		}
		return paperList;
	}

	@Override
	public ArrayList<Paper> getPaperAuthor() throws IOException {
		System.out.println("Fetching webpages...");
		List<Document> webpages = fetchWebpages(startURLPaper);
		System.out.println("Preparing data and starting 4 scraper threads...");
		//in the following lines the list gets split into 4 roughly equal parts so that each list part can be handled in a seperate thread (it's faster this way)
		int quarterSize = (int)Math.ceil(webpages.size() / 4);
		List<Document> input1 = webpages.subList(0, quarterSize);
		List<Document> input2 = webpages.subList(quarterSize, quarterSize * 2);
		List<Document> input3 = webpages.subList(quarterSize * 2, quarterSize * 3);
		List<Document> input4 = webpages.subList(quarterSize * 3, webpages.size());
		ArrayList<Paper> result = new ArrayList<>();
		//setup and start those threads
		ExecutorService executor = Executors.newFixedThreadPool(4);
		Future<ArrayList<Paper>> f1 = executor.submit(() -> extractPaperAuthor(input1));
		Future<ArrayList<Paper>> f2 = executor.submit(() -> extractPaperAuthor(input2));
		Future<ArrayList<Paper>> f3 = executor.submit(() -> extractPaperAuthor(input3));
		Future<ArrayList<Paper>> f4 = executor.submit(() -> extractPaperAuthor(input4));
		System.out.println("Waiting for thread results...");

		//wait for the thread results and add all of those to the result list (.get() is blocking)
		try {
			result.addAll(f1.get());
			result.addAll(f2.get());
			result.addAll(f3.get());
			result.addAll(f4.get());
			System.out.println("Gathered all results!");
		}
		catch(InterruptedException | ExecutionException e) { //thread exceptions
			System.err.println("Error while gathering results!");
			e.printStackTrace();
		}

		executor.shutdown();
		return result;
	}

	/**
	 * Extracts all papers and authors from a given list of webpages, which are in
	 * the ACL search form (e.g. <a href="https://aclanthology.coli.uni-saarland.de/catalog/facet/author?commit=facet.page%3D1&facet.page=1">here</a>)
	 *
	 * @param webpages a list of webpages
	 * @return a list of papers
	 */
	private ArrayList<Paper> extractPaperAuthor(List<Document> webpages) {
		ArrayList<Paper> paperList = new ArrayList<>();
		for (Document doc : webpages) {
			Elements paperListElements = doc.select("h5.index_title");
			for (Element elmnt : paperListElements) {
				if (!elmnt.text().contains("VOLUME")) {
					// add Paper info
					Paper paper = new Paper();
					// clean up the titles in the form of [C18-1017] Simple Neologism Based Domain Independe...
					// C18-1017 would be the anthology - we remove [] because the rest API dislikes the characters and they
					// convey no meaning
					String rawTitle = elmnt.text();
					String[] splitRawTitle = rawTitle.split(" ", 2);
					String paperTitle = splitRawTitle[1];
					String anthology = splitRawTitle[0].replace("[", "").replace("]", "");

					paper.setTitle(paperTitle);
					paper.setAnthology(anthology);
					paper.setRemoteLink("http://aclweb.org/anthology/" + anthology); //wow that was easy
					extractPaperRelease(elmnt, paper);

					// find authors and add them to a list
					Elements authorElements = elmnt.parent().parent().children().select("span").select("a");
					for (Element authorEl : authorElements) {
						Person author = new Person();

						// because acl2018 seems to not employ prefixes (e.g. Prof. Dr.), we do not need to scan them
						// scanning them might make for a good user story
						author.setFullName(authorEl.text());				// Both following statements seem necessary for the author_paper table but lead to Hibernate
						// access returning an object (paper) as often as a relation in author_paper exists
						// looking into the tables themselves, duplicate papers (even with the same PaperID) do not exist
						// set paper - author relation
						paper.addAuthor(author);
						// set author - paper relation
						author.addPaper(paper);
					}
					paperList.add(paper);
				}
			}
		}
		return paperList;
	}

	/**
	 * Extracts the release year + month of the given paper web element and stores it in the given paper object
	 * @param paper The web element of the paper to get the release year+month of
	 * @param thePaper The {@link Paper} object to save the release year+month in
	 */
	private void extractPaperRelease(Element paper, Paper thePaper) {
		try {
			Document doc = Jsoup.connect("https://aclanthology.coli.uni-saarland.de" + paper.select("a").attr("href")).get();
			ArrayList<Element> data = doc.select(".dl-horizontal").get(0).children(); //somewhere in those children is the date
			String year = "0";
			String month = "0";

			//find the different parts of the date
			for(int i = 0; i < data.size(); i++) {
				if(data.get(i).text().startsWith("Month")) { //the line contains the month
					month = data.get(i + 1).text();

					if(month.contains("-")) //some papers have a release month of e.g. "October-November", assume the first month as the release month
						month = month.split("-")[0];

					month = "" + CrawlerToolset.getMonthIndex(month);

					if(month.equals("-1"))
						month = "1"; //resort to january if no month is found
				}
				else if(data.get(i).text().startsWith("Year")) { //the line contains the year
					year = data.get(i + 1).text().substring(0, 4); //hope that every year is given in 1234 format
				}
			}

			thePaper.setRelease(LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1));
		}
		catch(IOException e) { //jsoup exception
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

		currentConference.setBegin(conferenceStartDate);
		currentConference.setEnd(conferenceEndDate);

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
	public ArrayList<Event> getSchedule() throws IOException {
		System.out.println();
		ArrayList<Event> result = new ArrayList<>();
		System.out.println("Preparing data and starting 5 scraper threads...");
		Element schedule = Jsoup.connect(schedulePage).get().select("#schedule").get(0);
		Elements days = schedule.select(".day-schedule");
		//threading :DD - takes about 1 minute 20 seconds without, 30 seconds with
		ExecutorService executor = Executors.newFixedThreadPool(5);
		Future<ArrayList<Event>> f1 = executor.submit(() -> parseFirstDay(days.get(0), new ArrayList<Event>()));
		Future<ArrayList<Event>> f2 = executor.submit(() -> parseOtherDays(days.get(1), new ArrayList<Event>()));
		Future<ArrayList<Event>> f3 = executor.submit(() -> parseOtherDays(days.get(2), new ArrayList<Event>()));
		Future<ArrayList<Event>> f4 = executor.submit(() -> parseOtherDays(days.get(3), new ArrayList<Event>()));
		//		Future<ArrayList<Event>> f5 = executor.submit(() -> parseWorkshops(new ArrayList<Event>()));
		System.out.println("Waiting for thread results...");

		try {
			result.addAll(f1.get());
			result.addAll(f2.get());
			result.addAll(f3.get());
			result.addAll(f4.get());
			//			result.addAll(f5.get());
			System.out.println("Gathered all results!");
		}
		catch(InterruptedException | ExecutionException e) {
			System.err.println("Error while gathering results!");
			e.printStackTrace();
		}

		executor.shutdown();
		return result;
	}

	/**
	 * Parses ACL 2018's first days' schedule (seperate method because it contains a special case)
	 * @param day The day element of the website
	 * @param result The resulting arraylist with the complete events of the first day
	 */
	private ArrayList<Event> parseFirstDay(Element day, ArrayList<Event> result) {
		String[] monthDay = day.selectFirst(".day").text().split(":")[1].trim().split(" "); //the text has the form of "Sunday: July 15"
		Elements tr = day.select("tr");

		//looping through all table rows, each contains an event
		for(int i = 0; i < tr.size(); i++) {
			Element el = tr.get(i);
			//conference, date, begin time, end time, title, (host,) place, description, category, list of sessions
			Event event = new Event();

			addGeneralEventInfo(el, event, monthDay);

			//special case
			if(i + 1 < tr.size() && tr.get(i + 1).hasClass("poster-session-row")) {
				Element row = tr.get(++i);
				Elements tutorials = row.select(".poster-name");

				//the table row might contain several tutorials in the same timeframe, so loop through those
				for(Element sessionEl : tutorials) {
					Session session = new Session();

					session.setTitle(sessionEl.text());
					event.addSession(session);
				}
			}

			result.add(event);
		}

		return result;
	}

	/**
	 * Parses ACL 2018's other days' schedule
	 * @param day The day element of the website
	 * @param result The resulting arraylist with the complete events of the given day
	 */
	private ArrayList<Event> parseOtherDays(Element day, ArrayList<Event> result) {
		String[] monthDay = day.selectFirst(".day").text().split(":")[1].trim().split(" "); //the text has the form of "Sunday: July 15"
		Elements tr = day.select("tr");

		//looping through all table rows, each contains an event
		for(int i = 0; i < tr.size(); i++) {
			Element el = tr.get(i);
			//conference, date, begin time, end time, title, (host,) place, description, category, list of sessions
			Event event = new Event();

			addGeneralEventInfo(el, event, monthDay);

			if(event.getCategory() == EventCategory.PRESENTATION)
				addOralPresentationInfo(tr.get(++i).select(".conc-session"), tr.get(++i).select(".session-location"), tr.get(++i).select(".session-details"), event);
			else if(event.getCategory() == EventCategory.SESSION)
				addPosterSessionInfo(tr.get(++i).select(".poster-sub-session"), event);

			result.add(event);
		}

		return result;
	}

	/**
	 * Parses ACL 2018's workshop schedule.
	 * Some of this is hardcoded because why not
	 * @param result The resulting arraylist with the complete workshop data
	 */
	private ArrayList<Event> parseWorkshops(ArrayList<Event> result) {
		try {
			Document doc = Jsoup.connect(workshopPage).get();
			Elements content = doc.select(".post-content");
			Elements days = content.select("ul");

			//looping through both of the days at which workshops are happening
			for(int i = 0; i < days.size(); i++) {
				Element day = days.get(i);
				Elements workshops = day.select("li");

				//looping through all workshop elements in the current day
				for(Element workshop : workshops) {
					Event event = new Event();
					String[] dayMonth = content.select("h4").get(i).text().split(" ", 2)[1].split(" ");
					String[] titleRoom = workshop.text().split(": ");
					String description = workshop.selectFirst("a").attr("href");//just the workshop link for now

					event.setConferenceName("ACL 2018");
					event.setBegin(LocalDateTime.of(2018, CrawlerToolset.getMonthIndex(dayMonth[1]), Integer.parseInt(dayMonth[0]), 9, 0));
					event.setEnd(LocalDateTime.of(2018, CrawlerToolset.getMonthIndex(dayMonth[1]), Integer.parseInt(dayMonth[0]), 17, 0)); //assume 5pm, because the schedule table is not 100% proportional
					event.setTitle(titleRoom[0]);
					event.setPlace(titleRoom[1]);
					event.setDescription(description);
					event.setCategory(EventCategory.WORKSHOP);
					result.add(event);
				}
			}
		}
		catch(IOException e) {
			e.printStackTrace();
		}

		return result;
	}

	/**
	 * Adds general information about an event, such as name, timeframe, location etc.
	 * @param el The event header element of the website
	 * @param event The arraylist with the resulting event's information
	 * @param monthDay The month (index 0) and day (index 1) where this event happens
	 */
	private void addGeneralEventInfo(Element el, Event event, String[] monthDay) {
		event.setConferenceName("ACL 2018");

		//only try to extract the information when the table row is the header of an event and is not the more detailed description
		//the header is something like "09:00-10:00 		Welcome Session & Presidential Address 			PLENARY, MCEC"
		if(el.id().startsWith("session")) {
			//start extracting the data from the table row
			String[] time = el.select(".session-times").text().split("–"); //NOT A HYPHEN!!! IT'S AN 'EN DASH'
			String[] begin = time[0].split(":");
			String[] end = time[1].split(":");
			String title = el.select(".session-name").text();
			//sometimes there is a suffix (after a ':'), use it as the event description
			//e.g. Oral Presentations [title]: Long Papers and TACL Papers) [suffix aka description]
			String desc = el.select(".session-suffix").text();
			Elements place = el.select(".session-location");
			EventCategory category = null;

			//the title string contains everything, so remove the description to avoid duplicate data
			if(!desc.isEmpty())
				title = title.replace(desc, "");

			//set the extracted data
			event.setBegin(LocalDateTime.of(2018, CrawlerToolset.getMonthIndex(monthDay[0]), Integer.parseInt(monthDay[1]), Integer.parseInt(begin[0]), Integer.parseInt(begin[1])));
			event.setEnd(LocalDateTime.of(2018, CrawlerToolset.getMonthIndex(monthDay[0]), Integer.parseInt(monthDay[1]), Integer.parseInt(end[0]), Integer.parseInt(end[1])));
			event.setTitle(title);
			event.setPlace(place.isEmpty() ? "?" : (place.get(0).text().isEmpty() ? "?" : place.get(0).text()));
			event.setDescription(desc);
			title = title.toLowerCase(); //easier to work with this way

			//decide which kind of category this event belongs to
			if(title.startsWith("tutorial"))
				category = EventCategory.TUTORIAL;
			else if(title.contains("welcome"))
				category = EventCategory.WELCOME;
			else if(title.startsWith("lunch") || title.contains("break"))
				category = EventCategory.BREAK;
			else if(title.contains("oral"))
				category = EventCategory.PRESENTATION;
			else if(title.contains("poster"))
				category = EventCategory.SESSION;
			else if(title.contains("recruitment"))
				category = EventCategory.RECRUITMENT;
			else if(title.contains("talk"))
				category = EventCategory.TALK;
			else if(title.contains("meeting"))
				category = EventCategory.MEETING;
			else if(title.contains("social"))
				category = EventCategory.SOCIAL;
			else if(title.contains("award") || title.contains("achievement"))
				category = EventCategory.CEREMONY;

			event.setCategory(category);
		}
	}

	/**
	 * Adds all available information about an oral presentation section
	 * @param sessions The elements containing session information
	 * @param rooms The elements containing room information per session
	 * @param presentations The elements containing the presentations per session
	 * @param event The arraylist with the resulting oral presentation's information
	 */
	private void addOralPresentationInfo(Elements sessions, Elements rooms, Elements presentations, Event event) {

		//looping through the different columns of the OP table
		for(int i = 0; i < presentations.size(); i++) { //seems like sessions, rooms, and presentations all have the same size, always
			Element sessEl = sessions.get(i);
			String sessTitle = sessEl.selectFirst(".conc-session-name").text();
			String sessPlace = rooms.get(i).text();

			//looping through the rows of the current column
			for(Element subEl : presentations.get(i).select(".talk")) {
				Session session = new Session();
				String[] sessTime = subEl.selectFirst(".talk-time").text().split(":");
				LocalDateTime sessStart = LocalDateTime.of(event.getBegin().toLocalDate(), LocalTime.of(Integer.parseInt(sessTime[0]), Integer.parseInt(sessTime[1])));
				LocalDateTime sessEnd = sessStart.plusMinutes(25);
				String sessDesc = subEl.selectFirst(".talk-title").text();

				session.setTitle(sessTitle);
				session.setDescription(sessDesc);
				session.setBegin(sessStart);
				session.setEnd(sessEnd);
				session.setPlace(sessPlace);
				event.addSession(session);
			}
		}
	}

	/**
	 * Adds all available information about a poster session
	 * @param sessions The elements containing the "sub"session information
	 * @param event The arraylist with the resulting poster session's information
	 */
	private void addPosterSessionInfo(Elements sessions, Event event) {
		//looping through the poster sessions
		for(Element sessEl : sessions) {
			Session session = new Session();
			String[] sessTitleDesc = sessEl.selectFirst(".poster-session-name").text().split(":");
			String sessTitle = sessTitleDesc[0].trim();
			String sessDesc = sessTitleDesc[1].trim();

			//looping through all papers that are part of this PS
			for(Element subEl : sessEl.select(".poster-name")) {
				String posterTitle = subEl.select("a").get(1).text().trim(); //let's hope it's always the second :D

				session.addPaperTitle(posterTitle);
			}

			session.setTitle(sessTitle);
			session.setDescription(sessDesc);
			session.setBegin(event.getBegin());
			session.setEnd(event.getEnd());
			session.setPlace(event.getPlace());
			event.addSession(session);
		}
	}
}