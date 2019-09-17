package de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
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

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.ConferenceJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.EventJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.PaperJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.PersonJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.TagJPAAccess;

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
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Tag;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.TagCategory;

import java.io.FileReader;
import java.util.Iterator;
 
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


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
    private Map<String, Paper> papers = new HashMap<>();
    private int beginYear = 0;
    private int endYear = 0;

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
     * Extracts all papers authors and events from a given list of webpages, which are in
     * the ACL search form (e.g. <a href=
     * "https://aclanthology.coli.uni-saarland.de/catalog/facet/author?commit=facet.page%3D1&facet.page=1">here</a>)
     *
     * @param webpages a list of webpages
     * @return a list of papers
     */
    public ArrayList<Conference> getPaperAuthorEvent() throws IOException {
        //TODO sachen schon vorher einspeichern und in vier teile splitten
        Set<String> uniqueURL = get_links("https://aclweb.org/anthology/events/");
        System.out.println("Projekt läuft!");
        HashSet<String> uniqueConverenceURLs = uniqueURL.stream()
                .filter(p -> p.contains("https://aclweb.org/anthology/events/")).collect(Collectors.toCollection(HashSet::new));
        ArrayList<String> converenceURLs = selector(uniqueConverenceURLs, this.conferences, this.beginYear, this.endYear).stream().collect(Collectors.toCollection(ArrayList::new));;
        String[] array = converenceURLs.stream().toArray(n -> new String[n]);
        System.out.println(array.length);
        System.out.println(Arrays.toString(array));
        ArrayList<ArrayList<String>> eventsPerConference = new ArrayList<ArrayList<String>>();
        int i = 0;
        for (String s : array) {
            i++;
            eventsPerConference.add(get_links(s).stream()
                    .filter(p -> p.contains("volumes") && !p.contains(".bib")).collect(Collectors.toCollection(ArrayList::new)));
            System.out.println("#Events " + i);
        }
        System.out.println(eventsPerConference.size());
        ArrayList<ArrayList<HashSet<String>>> paperPerEventPerConference = new ArrayList<ArrayList<HashSet<String>>>();
        for (ArrayList<String> events : eventsPerConference) {
            ArrayList<HashSet<String>> urlsPerEvent = new ArrayList<HashSet<String>>();
            for (String l : events) {
                urlsPerEvent.add(get_links(l).stream().filter(p -> p.contains("https://aclweb.org/anthology/papers/") && !p.contains(".bib")).collect(Collectors.toCollection(HashSet::new)));
                System.out.println("UrlsPerEvent:" + urlsPerEvent.size());
            }
            paperPerEventPerConference.add(urlsPerEvent);
        }
        System.out.println(paperPerEventPerConference.size());
        ArrayList<Conference> conferencesList = new ArrayList<Conference>();
        for (int x = 0; x < eventsPerConference.size(); x++) { // geändert, war vorher uniqueUrlsperCOnverence.size()
            Document conferenceSite = null;
            try {
                conferenceSite = Jsoup.connect(converenceURLs.get(x)).get();
            } catch (IOException e) {
                continue;
            }
            Elements confernceTitleElement = conferenceSite.select("#title");
            String conferenceTitle = confernceTitleElement.get(0).text();//splitRawTitle[1];
            Conference conference = Conference.findOrCreate(conferenceTitle);
            conference.setName(conferenceTitle);
            for (int y = 0; y < eventsPerConference.get(x).size(); y++) {
                Document eventDocument = null;
                try {
                    eventDocument = Jsoup.connect(eventsPerConference.get(x).get(y)).get();
                } catch (IOException e) {
                    continue;
                }
                Elements id = eventDocument.select("#main > div.row.acl-paper-details > div.col.col-lg-10.order-2 > dl > dd");
                Elements titel = eventDocument.select("#title");
                String titleString = titel.get(0).text();//splitRawTitle[1];
                String date = "2018-01-01";

//    			try {
//                String monthString = id.get(1).text();//splitRawTitle[1];
//                String yearString = id.get(2).text();//splitRawTitle[1];
//                String date = null;
//    			int monthInt =monthToInt(monthString);
//    			int yearInt=Integer.parseInt(yearString);
//    			if(monthInt!=0) {
//    				date = LocalDate.of(yearInt, monthInt, 1);
//    				event.setBegin(date.atStartOfDay());
//    				event.setEnd(date.atStartOfDay());
//    			}
//    			}catch(NumberFormatException e){
//    				System.out.println("yearString: "+yearString);
//    			}
                String locationString = id.get(3).text();//splitRawTitle[1];
                String[] locationArray = locationString.split(", ");
                if (y == 0) {
                    if (locationArray.length == 2) {
                        conference.setCity(locationArray[0]);
                        conference.setCountry(locationArray[1]);
                    }
                    //conference.setId(id);
                    if (date != null) {
              //          conference.setBegin(date);
               //         conference.setEnd(date);
                    }
                }
                //String idString = id.get(0).text();//splitRawTitle[1];
                //String cityString = id.get(3).text();//splitRawTitle[1];
                //event.setId(idString);
                Event event = Event.findOrCreate(titleString);
              //  event.setBegin("2018-01-01");
              //  event.setEnd("2018-01-01");
                EventCategory category = getWorkshopType(titleString);
                if (category != null) {
                    event.setCategory(category);
                }
                //event.setConferenceName(conferenceTitel);
                for (String s : paperPerEventPerConference.get(x).get(y)) {
                    System.out.println(s);
                    Document doc = null;
                    try {
                        doc = Jsoup.connect(s).get();
                    } catch (IOException e) {
                        continue;
                    }
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
                        Elements titleElement = doc.select("#title > a");
                        String paperTitle = titleElement.get(0).text();//doc.title();// splitRawTitle[1];
                        String anthology = paperInformationElements.get(0).text();// splitRawTitle[0].replace("[",
                        // "").replace("]", "");
                        Paper paper = Paper.findOrCreate(null, paperTitle);
                        paper.setTitle(paperTitle);
                        paper.setAnthology(anthology);
                        String remoteLink = "http://aclweb.org/anthology/" + anthology;
                        paper.setRemoteLink(remoteLink); // wow that was easy
        				paper.setReleaseDate(null);
//        				paper.setReleaseDate(extractPaperRelease(doc));
                        /**try {
                         ExtractedMetadata meDa = scienceParse(parser, new URL(remoteLink));
                         if(meDa == null) continue;
                         String plaintext = "";
                         for (org.allenai.scienceparse.Section sec : meDa.sections) {
                         plaintext = plaintext + sec.text;
                         }
                         paper.setPaperPlainText(plaintext);
                         paper.setPaperAbstract(meDa.abstractText);
                         } catch (MalformedURLException e) {
                         System.out.println("Parser abgestuerzt. Leere PDF-File? ");
                         System.out.println("Fehlerhafter Link: " + remoteLink);
                         e.printStackTrace();
                         }**/
                        // find authors and add them to a list
                        Elements authorElements = doc.select("#main > p> a");// elmnt.parent().parent().children().select("span").select("a");
                        for (Element authorEl : authorElements) {
                            Person author = Person.findOrCreate(null, authorEl.text());

                            // because acl2018 seems to not employ prefixes (e.g. Prof. Dr.), we do not need
                            // to scan them
                            String linkAuthor = authorEl.attr("abs:href");
                            Document docAuthor = null;
                            try {
                                docAuthor = Jsoup.connect(linkAuthor).get();
                            } catch (IOException e) {
                                continue;
                            }
                            Elements authorFirstNameElement = docAuthor.select("#title > span.font-weight-normal");
                            Elements authorLastNameElement = docAuthor.select("#title > span.font-weight-bold");
                            String firstName = authorFirstNameElement.text();
                            String lastName = authorLastNameElement.text();
                            author.setFirstName(firstName);
                            author.setLastName(lastName);
                            author.setFullName(authorEl.text());
                            // set paper - author relation
                            System.out.println("Set paper author relationship");
                            paper.addAuthor(author);
                            // set author - paper relation
                            author.addPaper(paper);
                            //event.addPaper(paper);
                            //paperList.add(paper);
                        }
                        event.addPaper(paper);
                        System.out.println("Event Paper added");
                    }
                }

                conference.addEvent(event);
                System.out.println("Events added to conference");
            }
            conferencesList.add(conference);
        }
        return conferencesList;
    }


    private int monthToInt(String month) {
        switch (month) {
            case "January":
                return 1;
            case "February":
                return 2;
            case "March":
                return 3;
            case "April":
                return 4;
            case "May":
                return 5;
            case "June":
                return 6;
            case "July":
                return 7;
            case "August":
                return 8;
            case "September":
                return 9;
            case "October":
                return 10;
            case "November":
                return 11;
            case "December":
                return 12;
            default:
                return 0;
        }
    }

    private EventCategory getWorkshopType(String workshopTitle) {
        if (workshopTitle.toLowerCase().contains("BREAK")) {
            return EventCategory.BREAK;
        }
        if (workshopTitle.toLowerCase().contains("CEREMONY")) {
            return EventCategory.CEREMONY;
        }
        if (workshopTitle.toLowerCase().contains("MEETING")) {
            return EventCategory.MEETING;
        }
        if (workshopTitle.toLowerCase().contains("PRESENTATION")) {
            return EventCategory.PRESENTATION;
        }
        if (workshopTitle.toLowerCase().contains("RECRUITMENT")) {
            return EventCategory.RECRUITMENT;
        }
        if (workshopTitle.toLowerCase().contains("SESSION")) {
            return EventCategory.SESSION;
        }
        if (workshopTitle.toLowerCase().contains("SOCIAL")) {
            return EventCategory.SOCIAL;
        }
        if (workshopTitle.toLowerCase().contains("TALK")) {
            return EventCategory.TALK;
        }
        if (workshopTitle.toLowerCase().contains("TUTORIAL")) {
            return EventCategory.TUTORIAL;
        }
        if (workshopTitle.toLowerCase().contains("WELCOME")) {
            return EventCategory.WELCOME;
        }
        if (workshopTitle.toLowerCase().contains("WORKSHOP")) {
            return EventCategory.WORKSHOP;
        }

        return null;
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
            int i = Integer.parseInt(year);
        } catch (NumberFormatException n) {
            year = Integer.toString(0);

        }
        // }
        // }

        return LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
    }

    
    
    
    
    
    
    @Override
    public ArrayList<Conference> getConferenceACL2018() throws IOException {
    	
    	ArrayList<Conference> conferencesList = new ArrayList<Conference>();

    	ArrayList<Person> addedPersons=new ArrayList<>();
    	ArrayList<Paper> addedPapers=new ArrayList<>();
    	
    	Conference conference = Conference.findOrCreate("ACL nicht gesezt");
    	
    	String description = "The 56th Annual Meeting of the Association for Computational Linguistics was held in Melbourne, Australia at the Melbourne Convention and Exhibition Centre from July 15th to 20th, 2018.";
    	
    	LocalDate begin = LocalDate.parse("2018-07-15"); // date: 15-20 July 2018
    	LocalDate end = LocalDate.parse("2018-07-20");
    	
    	String long_sub_deadline = LocalDate.parse("2018-02-22").toString();
    	String short_sub_deadline = LocalDate.parse("2018-02-22").toString();
    	LocalDate review_notification = LocalDate.parse("2018-04-20"); // April 20th, 2018
    	
    	conference.setDescription(description);
    	conference.setBegin(begin);
    	conference.setBegin(end);
    	//conference.setSubmissionDeadlineLongPaper(long_sub_deadline);
    	//conference.setSubmissionDeadlineShortPaper(short_sub_deadline);
    	conference.setReviewNotification(review_notification);
    	//conference.setName("ACL 2018");
    	
    	// TUTORIAL

    	Document tutorialPage = Jsoup.connect("https://acl2018.org/tutorials/").get();

    	Elements tutorialInfos = tutorialPage.select("body > main > div > article > div");

    	Elements authorInfos = tutorialPage.getElementsByClass("tutorial-presenters");
    	Elements titleInfos = tutorialPage.getElementsByClass("tute-title");
    	Elements timeInfos = tutorialPage.getElementsByClass("tutorial-time");
    	Elements placeInfos = tutorialPage.getElementsByClass("tutorial-location");

    	ArrayList<Event> tutorialsList = new ArrayList<Event>();
    	
    	for (int i = 0; i < authorInfos.size(); i++) {

    		String title = titleInfos.get(i).text();

    		Event tutorial = Event.findOrCreate(title);
    		
    		tutorial.setCategory(EventCategory.TUTORIAL);
    		
    		String abstr = titleInfos.get(i).select("h3").attr("title");
    		String place = placeInfos.get(i).text();
    		String time = timeInfos.get(i).text();
    		String[] timeArray =time.split(" – ");
    		LocalDateTime tutorilTimeStart=LocalDateTime.of(2018, 7, 15,Integer.parseInt(timeArray[0].split(":")[0]),Integer.parseInt(timeArray[0].split(":")[1]));
    		LocalDateTime tutorilTimeEnd=LocalDateTime.of(2018, 7, 15,Integer.parseInt(timeArray[1].split(":")[0]),Integer.parseInt(timeArray[1].split(":")[1]));


    		String author = authorInfos.get(i).text();
    		String speaker = author;
    		if (author.contains(",")) {
    			System.out.println("MULTIPLE AUTHORS");
    			speaker = author.substring(0, author.indexOf(","));
    		}
    		else if (author.contains(" and ")) {
    			System.out.println("MULTIPLE AUTHORS");
    			speaker = author.substring(0, author.indexOf("and"));
    		}
    		Person authorObjekt=Person.findOrCreate(null,speaker);
    		addedPersons.add(authorObjekt);
    		
    		tutorial.setPerson(authorObjekt);
    		tutorial.setTitle(title);
    		tutorial.setDescription(abstr); // oder hier eine extra Column "Abstract" in Event anlegen ???
    		tutorial.setPlace(place);
    		tutorial.setBegin(tutorilTimeStart);
    		tutorial.setEnd(tutorilTimeEnd);
    		tutorialsList.add(tutorial);
    	}
    	
    	// WORKSHOP

    	Document workshopPage = Jsoup.connect("https://acl2018.org/workshops/").get();
    	Elements workshopInfos = workshopPage.select("body > main > div > article > div > ul> li");
    	
//    	Elements workshopDates = workshopPage.select("body > main > div > article > div > h4");
//    	String date1 = workshopDates.get(0).attr("id");
//    	String date2 = workshopDates.get(1).attr("id");
    	LocalDate date1 = LocalDate.parse("2018-07-19"); // dates hardcoded for now...
    	LocalDate date2 = LocalDate.parse("2018-07-20");

    	ArrayList<Event> workshopsList = new ArrayList<Event>();

    	for (int i = 0; i < workshopInfos.size(); i++) {
    		String name = workshopInfos.get(i).text().split(": ")[0];

    		Event workshop = Event.findOrCreate(name);
    	
    		workshop.setCategory(EventCategory.WORKSHOP);
    		
//    		String place = workshopInfos.get(i).text().split(": ")[1];
    		
    		if (i < 8) {
    			workshop.setDate(date1);
    			workshop.setBegin(LocalDateTime.of(2018, 7,19,9,0));
    		} else {
    			workshop.setDate(date2);
    			workshop.setBegin(LocalDateTime.of(2018, 7,20,9,0));

    		}
    		
    		Elements linkInfos = workshopPage.select("body > main > div > article > div > ul >li >a");
    		String link = linkInfos.get(i).attr("href");
    		
    		workshop.setTitle(name);
    		workshop.setLink(link);

    		workshopsList.add(workshop);
    	}
    	
    	
    	
    	//SESSION
        
        Document programmePage = Jsoup.connect("https://acl2018.org/programme/schedule/").get();
        Elements sessionInfos= programmePage.select("tr.conc-session-indiv-row");//getElementsByClass("session-row session-name-row conc-session-indiv-row");
        Elements subSessionInfos=programmePage.select("tr.conc-session-details-row");//getElementsByClass("session-row session-name-row conc-session-indiv-row");

        ArrayList<Event> sessionList=new ArrayList<Event>();
        
       
        

    	for(int slotN=0;slotN<9;slotN++ ) {
    		
    		
    		//Time:	    	    
    	    String time = programmePage.select("td.conc-session-shared-name").get(slotN).select("div.session-times").text();
    		String[] timeArray =time.split("–");
    		LocalDateTime sessionTimeStart=LocalDateTime.of(2018, 7, 15,Integer.parseInt(timeArray[0].split(":")[0]),Integer.parseInt(timeArray[0].split(":")[1]));
    		LocalDateTime sessionTimeEnd=LocalDateTime.of(2018, 7, 15,Integer.parseInt(timeArray[1].split(":")[0]),Integer.parseInt(timeArray[1].split(":")[1]));

    		
    		
    		for(int sessionN=0;sessionN<6;sessionN++ ) {
    			
    			
    			Event session=Event.findOrCreate(sessionInfos.get(slotN).select("div.conc-session-name").get(sessionN).text());
    			session.setCategory(EventCategory.SESSION);
    			
    			
    			session.setBegin(sessionTimeStart);
    			session.setEnd(sessionTimeEnd);
    			
    			//Chair:
    		    String chairName=sessionInfos.get(slotN).select("div.speakers").get(sessionN).text();
    		    Person chair=Person.findOrCreate(null, chairName);
    		    //TODO: vielleicht ein extra Field chair?
    		    session.setPerson(chair);
    		    //addedPersons.add(chair);
    		    
    		    //Title:
    		    session.setTitle(sessionInfos.get(slotN).select("div.conc-session-name").get(sessionN).text());
    		    //Place:
    		    session.setPlace(programmePage.select("tr.conc-session-loc-row").get(slotN).select("td.conc-session-location").get(sessionN).text());
    		    
    		   		
    			int number=4;
    		     
    			if(slotN==8) {
    				number=5;
    			}
    			
    			for(int partN=0;partN<number;partN++) {
    				
    				//Subsession-Title:
    				String title=subSessionInfos.get(slotN).select("td.talk-sessions").get(sessionN).select("div.talk").get(partN).select("div.talk-title").text(); 
    				
    				
    				
    				EventPart subSession=EventPart.findOrCreate(title);
    				
    				      				
    				subSession.setTitle(title);
    			    //Subsession-Time:
    				
    				int day=0;
    				int length=25;
    				switch(sessionN) {
    				case 0:
    					day= 16;
    				case 1:	
    					day=16;
    				case 2:
    					day=16;

    				case 3:	
    					day=17;

    				case 4:	
    					day=17;
    					length=15;

    				case 5:
    					day=17;

    				case 6:
    					day=18;

    				case 7:
    					day=18;
    					length=15;
    				}
    				
    				
    				
    				String[] subSessionTimeArray=subSessionInfos.get(slotN).select("td.talk-sessions").get(sessionN).select("div.talk").get(partN).select("div.times").text().split(":");
    				
    				
    				String eins=subSessionTimeArray[0];
    				String zwei=subSessionTimeArray[1];
    				int einsI=Integer.parseInt(eins);
    				int zweiI=Integer.parseInt(zwei);

    				
    				LocalDateTime subSessionTimeStart=LocalDateTime.of(2018, 7, day,einsI,zweiI);
    				
    				
    				subSession.setBegin(subSessionTimeStart);
    				subSession.setEnd(subSessionTimeStart.plusMinutes(length));
    				
    				String subSessionDescription;
    				String speakerName;
    				
    				String[] authors;
    				
    			    if(subSessionInfos.get(slotN).select("td.talk-sessions").get(sessionN).select("div.talk").get(partN).attr("title").split("ABSTRACT: ").length>1){
    	   			//Subsession-Speaker:
    			    authors =subSessionInfos.get(slotN).select("td.talk-sessions").get(sessionN).select("div.talk").get(partN).attr("title").replace("Chair: ","").split(". ABSTRACT:")[0].split("; ");
    			    
    			    speakerName=authors[0];
    			    
    			    Person speaker = Person.findOrCreate(null, speakerName);
    			    subSession.setPerson(speaker);
    			    //addedPersons.add(speaker);
    			    
    			    subSessionDescription=subSessionInfos.get(slotN).select("td.talk-sessions").get(sessionN).select("div.talk").get(partN).attr("title").split("ABSTRACT: ")[1];
    			    //Subsession-Abstract:
    	   			subSession.setDescription(subSessionDescription);
    	   			
    			    
    			    
    			    }else {
    			    	
    			    	
    			    	
    			    	String link=subSessionInfos.get(slotN).select("td.talk-sessions").get(sessionN).select("div.talk-title").get(partN).select("a").get(2).attr("href");
    			    	Document transacl = Jsoup.connect(link).get();

    			    	
    			    	
    			       Elements partAuthor= transacl.select("#authorString");
    			       Elements partAbstract= transacl.select("#articleAbstract > div > p");
    			       System.out.println(partAuthor.get(0).text());
    			       
    			       authors =partAuthor.get(0).text().replace("Chair: ","").split(". ABSTRACT:")[0].split(", ");
       			       speakerName=authors[0];
    			       
    			       
    			       subSessionDescription=partAbstract.text();
    			       subSession.setDescription(subSessionDescription);
    			      
    			       
    			    }
    			    
    			    
    			    
    			    
    			    System.out.println(speakerName);
    			    
    			    
    			    title=title.replace("'","''");
    			    System.out.println("Title: "+title);
    			    Paper paper=Paper.findOrCreate(null, title);
    			    
    			    
    			    int i=0;
    			    
    			    for(String speakerN: authors) {
    			    	
        			    Person speaker = Person.findOrCreate(null, speakerN);
         			    
        			    if(i==0) {
        			    subSession.setPerson(speaker);
        			    }
        			    
        			    if(paper.getPaperAbstract()==null) {
        			    paper.addAuthor(speaker);
        			    }
         			    i++;
        			    }
    			    
    			    if(paper.getPaperAbstract()==null) {
    			    	
        			    paper.setPaperAbstract(subSessionDescription);	
        			    }
    			    
    			    Tag test=Tag.findOrCreate("test lol");
    			    test.setCategory(TagCategory.METHOD);
    			    
    			    paper.addTag(test);
    			    
    			    
    			    
    			    subSession.addPaper(paper);
    			    
    			    session.addEventPart(subSession);
    			    			
    				sessionList.add(session);
    			}
    		
    			if(slotN==8) {
    				break;
    			}
    		}
    		
    		
    		
    	}
      
    	//Postersession
    	
    	ArrayList<Event> posterSessionList=new ArrayList<Event>();
    	
    	for(int posterSessionN=2;posterSessionN<5;posterSessionN++) {
    		
    		System.out.println("************************");
    	 //Time:
    	       //nimmt die Elemente mit Unterpunkten
    	    String posterSessionTime=programmePage.select("tr.conc-session-name-row").get(3).select("div.session-times").text();
    	      
    	    
    	    int day=16;
    	    if(posterSessionN==3) {
    	    	day=17;
    	    	
    	    }
    	    if(posterSessionN==4) {
    	    	day=18;
    	    }
    	    
    	    String[] timeArray =posterSessionTime.split("–");
    	    LocalDateTime sessionTimeStart=LocalDateTime.of(2018, 7, day,Integer.parseInt(timeArray[0].split(":")[0]),Integer.parseInt(timeArray[0].split(":")[1]));
    	    LocalDateTime sessionTimeEnd=LocalDateTime.of(2018, 7, day,Integer.parseInt(timeArray[0].split(":")[0]),Integer.parseInt(timeArray[0].split(":")[1]));
    	    
    	       
    		
    		for(int subSessionN=0;subSessionN<7;subSessionN++) {
    			
    		Event posterSession =Event.findOrCreate(programmePage.select("tr.poster-session-row").get(posterSessionN).select("div.poster-sub-session").get(subSessionN).select("div.poster-session-name").text());
    		posterSession.setCategory(EventCategory.POSTERSESSION);
    		
    		
    		posterSession.setBegin(sessionTimeStart);
    		posterSession.setEnd(sessionTimeEnd);
    			System.out.println("'*'*''*'*'*''*''*'");
    		//POSTER SESSION
    		       
    		       //Session Name:
    		       posterSession.setTitle(programmePage.select("tr.poster-session-row").get(posterSessionN).select("div.poster-sub-session").get(subSessionN).select("div.poster-session-name").text());
    		      
    			
    			
    			for(Element poster: programmePage.select("tr.poster-session-row").get(posterSessionN).select("div.poster-sub-session").get(subSessionN).select("span.poster-name")) {
    			 System.out.println("'''''''''''''''''''''''");
    			 		       
    		       
    			 String posterAbstract;
    			 
    			 String[] authors;
    			 String posterSpeakerName;
    			 
    			    if(poster.attr("title").split("ABSTRACT: ").length>1){

    		       
    		       //Author:
    			    	
    			   authors= 	poster.attr("title").split(". ABSTRACT: ")[0].split(";");
    			   
    			   posterSpeakerName=authors[0];
    			   
    			   System.out.println("Name: "+posterSpeakerName);
    			   Person posterSpeaker=Person.findOrCreate(null, posterSpeakerName);
    			   posterSession.setPerson(posterSpeaker);
    			   
    		       //Abstract:
    		       
    		       posterAbstract=poster.attr("title").split("ABSTRACT: ")[1];
    			    }else {
    			    	

    			    	String link=poster.select("a").get(1).attr("href");
    			    	System.out.println(link);
    			    	Document transacl = Jsoup.connect(link).get();

    			    	
    			    	
    			       Elements partAuthor= transacl.select("#authorString");
    			       
    			       
    			       authors= partAuthor.text().split(", ");
    			       
    			       posterSpeakerName=authors[0];
    			       
    			       Person posterSpeaker=Person.findOrCreate(null, posterSpeakerName);
        			   posterSession.setPerson(posterSpeaker);
        			   
    			       
    			       System.out.println("876:    sdfdf    : "+posterSpeakerName);
    			       
    			       Element partAbstract= transacl.select("head > meta").get(9);//#articleAbstract > div > p");
    			       
    			       //head > meta:nth-child(12)
    			       
    			       posterAbstract=partAbstract.attr("content");
    				    	
    			    }
    			    
    			    
    			    
    			    String title=poster.text().replace("'","''" );
 			       Paper paper=Paper.findOrCreate(null, title);
 			      
    			    
    			    
 			       	int i=0;
    			    
    			    for(String speakerN: authors) {
    			    	
        			    Person speaker = Person.findOrCreate(null, speakerN);
         			    
        			    if(i==0) {
        			    posterSession.setPerson(speaker);
        			    }
        			    
        			    if(paper.getPaperAbstract()==null) {
        			    paper.addAuthor(speaker);
        			    }
         			    i++;
        			    }
    			    
    			    if(paper.getPaperAbstract()==null) {
  			    	   
   			    	  paper.setPaperAbstract(posterAbstract);
   			    	   
   			       }
    			    
    			    
    			
    			    
    			    
    			    
    			  //Paper Name:
    		       	
    			   // System.out.println("Postertitle: "+poster.text());
                   // Paper paper = Paper.findOrCreate(null, "test paper lol");//poster.text());

    			    
    			       
    			       posterSession.addPaper(paper);
    			 
    			    }
    		    posterSessionList.add(posterSession);	

    			
    		}
    		
    		
    	}
    	
    	for(Event e:tutorialsList) {
    	 conference.addEvent(e);
    	}
    	
    	for(Event e:workshopsList) {
    		 conference.addEvent(e);
    		}
    	for(Event e:sessionList) {
    		 conference.addEvent(e);
    		}
    	for(Event e:posterSessionList) {
    		 conference.addEvent(e);
    		}
    	
    	conferencesList.add(conference);

    	
    	return conferencesList;
    	
    	}
    	
    
    
    
    @Override
    public ArrayList<Paper> getTags() throws IOException {
    	
    	
		TagJPAAccess tagFiler = new TagJPAAccess();

    	ArrayList<Paper> paperList=new ArrayList<Paper>();

	    ArrayList<JSONObject> json=new ArrayList<JSONObject>();
	    JSONObject obj;
	    // The name of the file to open.
	    String fileName = "C:\\Users\\Ich\\Desktop\\Uni\\NLP Projekt\\Gemeinsam\\src\\main\\resources\\myTestFilesProcessed.json";
	     
	    // This will reference one line at a time
	    String line = null;

	    try {
	        // FileReader reads text files in the default encoding.
	        FileReader fileReader = new FileReader(fileName);

	        // Always wrap FileReader in BufferedReader.
	        BufferedReader bufferedReader = new BufferedReader(fileReader);

	        while((line = bufferedReader.readLine()) != null) {
	           
	            System.out.println("Nächste Line!");

	        	obj = (JSONObject) new JSONParser().parse(line);
	            json.add(obj);
	            
	           
	            String paperID=(String) obj.get("doc_key");
	            
	            Paper paper =Paper.findById(paperID);
	            
	            if(paper==null) {
	            paper=new Paper();	
	            paper.setTitle("Not fount: "+paperID);
	            	
	            }
	            
	            
	            
	            
	            
System.out.println("generic");
	            
	           JSONArray taskList = (JSONArray) obj.get("generic");
	            Iterator<JSONArray> iterator = taskList.iterator();
	            
	            while (iterator.hasNext()) {
		            String name="";

	                Iterator<String> iterator2 =iterator.next().iterator();
		            while (iterator2.hasNext()) {
		            name=name+iterator2.next();
		            }
		            
		            System.out.println(name);
		            Tag tag =Tag.findOrCreate(name);
		            tag.setCategory(TagCategory.GENERIC);
		    		tagFiler.add(tag);

		            
		            paper.addTag(tag);
	                
	            }
	            
	            System.out.println("task");
	            
		            taskList = (JSONArray) obj.get("task");
		            iterator = taskList.iterator();
		            
		            while (iterator.hasNext()) {
			            String name="";

		                Iterator<String> iterator2 =iterator.next().iterator();
			            while (iterator2.hasNext()) {
			            name=name+iterator2.next();
			            }
			            
			            System.out.println(name);
			            Tag tag =Tag.findOrCreate(name);
			            tag.setCategory(TagCategory.TASK);
			    		tagFiler.add(tag);

			            
			            paper.addTag(tag);
		                
		            }
	            
		            System.out.println("metric");
		            
		            taskList = (JSONArray) obj.get("metric");
		            iterator = taskList.iterator();
		            
		            while (iterator.hasNext()) {
			            String name="";

		                Iterator<String> iterator2 =iterator.next().iterator();
			            while (iterator2.hasNext()) {
			            name=name+iterator2.next();
			            }
			            
			            System.out.println(name);
			            Tag tag =Tag.findOrCreate(name);
			            tag.setCategory(TagCategory.METRIC);
			    		tagFiler.add(tag);

			            
			            paper.addTag(tag);
		                
		            }
		            
		            System.out.println("material");
		            
		            taskList = (JSONArray) obj.get("material");
		            iterator = taskList.iterator();
		            
		            while (iterator.hasNext()) {
			            String name="";

		                Iterator<String> iterator2 =iterator.next().iterator();
			            while (iterator2.hasNext()) {
			            name=name+iterator2.next();
			            }
			            
			            System.out.println(name);
			            Tag tag =Tag.findOrCreate(name);
			            tag.setCategory(TagCategory.MATERIAL);
			    		tagFiler.add(tag);

			            
			            paper.addTag(tag);
		                
		            }
		            
		            System.out.println("otherscientificterm");
		            
		            taskList = (JSONArray) obj.get("otherscientificterm");
		            iterator = taskList.iterator();
		            
		            while (iterator.hasNext()) {
			            String name="";

		                Iterator<String> iterator2 =iterator.next().iterator();
			            while (iterator2.hasNext()) {
			            name=name+iterator2.next();
			            }
			            
			            System.out.println(name);
			            Tag tag =Tag.findOrCreate(name);
			            tag.setCategory(TagCategory.OTHERSCIENTIFICTERM);
			    		tagFiler.add(tag);

			            
			            paper.addTag(tag);
		                
		            }
		            
System.out.println("method");
		            
		            taskList = (JSONArray) obj.get("method");
		            iterator = taskList.iterator();
		            
		            while (iterator.hasNext()) {
			            String name="";

		                Iterator<String> iterator2 =iterator.next().iterator();
			            while (iterator2.hasNext()) {
			            name=name+iterator2.next();
			            }
			            
			            System.out.println(name);
			            Tag tag =Tag.findOrCreate(name);
			            tag.setCategory(TagCategory.METHOD);
			    		tagFiler.add(tag);

			            
			            paper.addTag(tag);
		                
		            }
	            
	        paperList.add(paper);    
	            
	        }
	        // Always close files.
	        bufferedReader.close();  
	        
	    }
	    catch(FileNotFoundException ex) {
	        System.out.println("Unable to open file '" + fileName + "'");                
	    }
	    catch(IOException ex) {
	        System.out.println("Error reading file '" + fileName + "'");                  
	        // Or we could just do this: 
	        // ex.printStackTrace();
	    } catch (ParseException e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	    }
	        
	        

return paperList;	
    	
    }
    
    
    
    
    
    
    /**
     * A method which returns a conference instance with its name, location and
     * start and end date set. Scrapes the about page of ACL2018 for its information
     * and employs String conversion found in CrawlerToolset.
     *
     * @return a conference instance with its name, location and start and end date
     * set, an empty conference instance if an IOException occured
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
//		##############################################
        String conferenceStartDate = CrawlerToolset.acl2018ConvertStringToDateRange(dateAndLocationString)[0];
        String conferenceEndDate = CrawlerToolset.acl2018ConvertStringToDateRange(dateAndLocationString)[1];
//		LocalDate conferenceStartDate = CrawlerToolset.acl2018ConvertStringToDateRange(dateAndLocationString)[0];
//		LocalDate conferenceEndDate = CrawlerToolset.acl2018ConvertStringToDateRange(dateAndLocationString)[1];
        // Maybe we need to look at a timezone api? Probably not feasible to keep it
        // free, which is why it is set as
        // manual for now
        // TODO: talk about timezones and how to handle them
        // ZoneId timeZone = ZoneId.of("GMT+11");

        //currentConference.setBegin(conferenceStartDate);
        //currentConference.setEnd(conferenceEndDate);

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
//			####################################################
          //event.setBegin(begin[1]);
          //event.setEnd(end[1]);
//			event.setBegin(LocalDateTime.of(2018, CrawlerToolset.getMonthIndex(monthDay[0]),
//					Integer.parseInt(monthDay[1]), Integer.parseInt(begin[0]), Integer.parseInt(begin[1])));
//			event.setEnd(LocalDateTime.of(2018, CrawlerToolset.getMonthIndex(monthDay[0]),
//					Integer.parseInt(monthDay[1]), Integer.parseInt(end[0]), Integer.parseInt(end[1])));
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
//				#############################################
           //     String sessStart = event.getBegin();
           //     String sessEnd = sessStart;
//				LocalDateTime sessStart = LocalDateTime.of(event.getBegin().toLocalDate(),
//						LocalTime.of(Integer.parseInt(sessTime[0]), Integer.parseInt(sessTime[1])));
//				LocalDateTime sessEnd = sessStart.plusMinutes(25);
                String sessPaperTitle = subEl.selectFirst(".talk-title").text();

                // set the data
                eventPart.setTitle(evTitle);
                event.addPaper(papers.get(sessPaperTitle));
            //    eventPart.setBegin(sessStart);
            //    eventPart.setEnd(sessEnd);
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