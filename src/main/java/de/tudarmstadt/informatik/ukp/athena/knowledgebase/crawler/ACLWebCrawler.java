package de.tudarmstadt.informatik.ukp.athena.knowledgebase.crawler;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.TagJPAAccess;

import org.allenai.scienceparse.ExtractedMetadata;
import org.allenai.scienceparse.Parser;
import org.allenai.scienceparse.Section;
import org.apache.pdfbox.text.PDFTextStripper;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Conference;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Event;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.EventCategory;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.EventPart;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Paper;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Person;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Tag;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.TagCategory;

import java.io.FileReader;
import java.util.Iterator;
 
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import org.json.simple.parser.ParseException;


/**
 * A class, which holds the capability to return a list of all conferences,  
 * published on aclanthology.info and specifically scrape all information
 * available about the ACL'18 conference on acl2018.org.
 * 
 * @author Jonas Hake, Julian Steitz, Daniel Lehmann, Tim Schmidt, Anke Unger, Ines Zelch
 */
class ACLWebCrawler extends AbstractCrawler {
	
    private String[] conferences; //conferences to be scraped from aclanthology.org
    private int beginYear = 0; //begin year of conferences to be scraped from aclanthology.org
    private int endYear = 0; //end year of conferences to be scraped from aclanthology.org
    private boolean parsePdf=false;
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
    public ACLWebCrawler(int beginYear, int endYear,boolean parsePdf, String... conferences) {
        
        if (beginYear != 0) {
            this.beginYear = beginYear;
        }
        if (endYear != 0) {
            this.endYear = endYear;
        }

        this.parsePdf=parsePdf;
        if (conferences != null)
            this.conferences = conferences;
        else
            this.conferences = new String[0];
    }
    

    
    /**
     * Returns all the links contained by a single web page specified by url.
     * @param url url of website to be accesed
     * @return Set of links that are available on this web site.
     */
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
                    
            );

        } catch (IOException ex) {
            System.out.print("Exeption");
        }
        return uniqueURL;
    }

    /*Returns true if a string contains one of the strings provided by an array of Strings.
     *Used to filter urls. 
     * @param inputStr The string to be checked
     * @param items The elements that are compared to the inputStr 
     * @return true if one or more elements are containt in the inputStr, false otherwise
     * */
    private static boolean stringContainsItemFromList(String inputStr, String[] items) {
        return Arrays.stream(items).parallel().anyMatch(inputStr::contains);
    }
    
    
    /*Given a list of links and a list of conferences and a time frame it selects
     *the links leading to valid aclanthology.info conference entries.
     * @param allLinks a list of links to be further selected
     * @param conferences the string identifier of conferences to be selected
     * @param from start year of the time range the conferences can be from
     * @param to end year of the time range the conferences can be from
     * @return a set of links to valid aclanthology.info entries.
     * */
    private Set<String> selector(Set<String> allLinks, String[] conferences, int from, int to) {

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
        	//matches the occurrence of the conference identifier in the url (eg. acl-2018)
            conferences[i] = conferences[i] + "-";
        }

        if (conferences.length != 0) {
            selection = allLinks.stream()
                    .filter(p -> stringContainsItemFromList(p, conferences) && containsYear(p, from2, to2))
                    .collect(Collectors.toSet());

        } else {
            selection = allLinks.stream().filter(p -> containsYear(p, from2, to2)).collect(Collectors.toSet());

        }
        return selection;

    }

    
    /*Checks if a string contains one of two numbers or a integer value in between.
     * Usaly used to check if a conference url is in a specific range of years.
     * @param url input string - usaly a conference url 
     * @param from start year of the time range the conferences can be from
     * @param to end year of the time range the conferences can be from
     * @return true if the url contains number in the range, false otherwise
     * */
    private boolean containsYear(String url, int from, int to) {
        for (int i = from; i <= to; i++) {

            if (url.contains(Integer.toString(i))) {
                return true;
            }
        }
        return false;

    }

    /*
     * {@inheritDoc}
     * 
     * */
    public ArrayList<Conference> getPaperAuthorEvent() throws IOException {
    	//get links of all conferences on aclweb.org
    	Set<String> uniqueURL = get_links("https://aclweb.org/anthology/events/");
        
    	// setup for the pdf parser
        org.allenai.scienceparse.Parser parser = null;
        PDFTextStripper stripper = null;
        de.tudarmstadt.informatik.ukp.athena.knowledgebase.PDFParser.Parser myparse = new de.tudarmstadt.informatik.ukp.athena.knowledgebase.PDFParser.Parser();

        if(parsePdf) {
        try {
            parser = Parser.getInstance();
            stripper = new PDFTextStripper();
        } catch (Exception e) {
            e.printStackTrace();
        }
        }
        
        //select only valied conference urls 
        HashSet<String> uniqueConverenceURLs = uniqueURL.stream()
                .filter(p -> p.contains("https://aclweb.org/anthology/events/")).collect(Collectors.toCollection(HashSet::new));
        //select only the conference urls of the specified years and conferences
        ArrayList<String> converenceURLs = selector(uniqueConverenceURLs, this.conferences, this.beginYear, this.endYear).stream().collect(Collectors.toCollection(ArrayList::new));
        String[] array = converenceURLs.stream().toArray(n -> new String[n]);
        
        //selects the event links for all selected conferences
        ArrayList<ArrayList<String>> eventsPerConference = new ArrayList<ArrayList<String>>();
        for (String s : array) {
            eventsPerConference.add(get_links(s).stream()
                    .filter(p -> p.contains("volumes") && !p.contains(".bib")).collect(Collectors.toCollection(ArrayList::new)));
        }
        
        //select the links to the papers for each selected conference
        ArrayList<ArrayList<HashSet<String>>> paperPerEventPerConference = new ArrayList<ArrayList<HashSet<String>>>();
        for (ArrayList<String> events : eventsPerConference) {
            ArrayList<HashSet<String>> urlsPerEvent = new ArrayList<HashSet<String>>();
            for (String l : events) {
                urlsPerEvent.add(get_links(l).stream().filter(p -> p.contains("https://aclweb.org/anthology/papers/") && !p.contains(".bib")).collect(Collectors.toCollection(HashSet::new)));
                System.out.println("UrlsPerEvent:" + urlsPerEvent.size());
            }
            paperPerEventPerConference.add(urlsPerEvent);
        }
        
        //create and fill a conference object for each conference url
        ArrayList<Conference> conferencesList = new ArrayList<Conference>();
        for (int x = 0; x < eventsPerConference.size(); x++) { 
            Document conferenceSite = null;
            try {
                conferenceSite = Jsoup.connect(converenceURLs.get(x)).get();
            } catch (IOException e) {
                continue;
            }
            Elements confernceTitleElement = conferenceSite.select("#title");
            String conferenceTitle = confernceTitleElement.get(0).text();//splitRawTitle[1];
            //search for a conference of that name in the database if non found create a new one
            Conference conference = Conference.findOrCreate(conferenceTitle);
            conference.setName(conferenceTitle);
            //create the event object for the conference
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

                Event event = Event.findOrCreate(titleString);

                String yearString =null;
                LocalDate date = null;

                try {
                String monthString = id.get(1).text();//splitRawTitle[1];
                yearString = id.get(2).text();//splitRawTitle[1];
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
                String[] locationArray = locationString.split(", ");
                if (y == 0) {
                    if (locationArray.length == 2) {
                        conference.setCity(locationArray[0]);
                        conference.setCountry(locationArray[1]);
                    }
                    //conference.setId(id);
                    if (date != null) {
                       conference.setBegin(date);
                        conference.setEnd(date);
                    }
                }
                //String idString = id.get(0).text();//splitRawTitle[1];
                String cityString = id.get(3).text();//splitRawTitle[1];
                //event.setId(idString);
             
                EventCategory category = getEventType(titleString);
                if (category != null) {
                    event.setCategory(category);
                }
                
                //create paper objects and add them to the event
                for (String s : paperPerEventPerConference.get(x).get(y)) {
                    Document doc = null;
                    try {
                        doc = Jsoup.connect(s).get();
                    } catch (IOException e) {
                        continue;
                    }
                    Elements paperInformationElements = doc.select("#main > div > div.col.col-lg-10.order-2 > dl > dd");
                    if (!doc.title().contains("VOLUME")) {
                        // check is not earlier because the elmnt is needed
                        if (conferences.length != 0) //
                            continue; // innerLoop; //label is not needed necessarily, but helps readability
                        
                        Elements titleElement = doc.select("#title > a");
                        String paperTitle = titleElement.get(0).text();
                        String anthology = paperInformationElements.get(0).text();
                        
                        //search the database for a paper of this name if none found create a new one
                        Paper paper = Paper.findOrCreate(null, paperTitle);
                        paper.setTitle(paperTitle);
                        paper.setAnthology(anthology);
                        String remoteLink = "http://aclweb.org/anthology/" + anthology;
                        paper.setRemoteLink(remoteLink); 
        				paper.setReleaseDate(null);
        				//TODO: Dates!!!!!!
        				paper.setReleaseDate(extractPaperRelease(doc));
        				
        
        				//Pdf Parser
        				if(parsePdf) {
        				ExtractedMetadata meDa = null;
                        try {
                            URL urli = new URL(remoteLink);
                            meDa = myparse.scienceParse(parser, urli);
                            String plainText = myparse.plainParse(stripper, urli);
                            paper.setPaperPlainText(plainText);

                        } catch (MalformedURLException e) {
                            System.out.println("Parser abgestuerzt. Leere PDF-File? ");
                            System.out.println("Fehlerhafter Link: " + remoteLink);
                            e.printStackTrace();
                        }

                        if(meDa == null || meDa.getSections() == null)continue;
                        paper.setPaperAbstract(meDa.abstractText);
                        List<Section> sections = meDa.getSections();
                        for(Section sec : sections) {
                            if (sec == null || sec.getHeading() == null) continue;
                            String h = sec.getHeading().trim().toLowerCase();
                            if (h.contains("introduction")) paper.setIntroduction(sec.getText());
                            else if (h.contains("related work")) paper.setRelatedWork(sec.getText());
                            else if (h.contains("results")) paper.setResult(sec.getText());
                            else if (h.contains("discussion")) paper.setDiscussion(sec.getText());
                            else if (h.contains("conclusion")) paper.setConclusion(sec.getText());
                            else if (h.contains("datasets")) paper.setDataset(sec.getText());
                        }
        				}
                        // find authors and add them to a list
                        Elements authorElements = doc.select("#main > p> a");// elmnt.parent().parent().children().select("span").select("a");
                        for (Element authorEl : authorElements) {
                        	//search database of author of same name if nor found create new person object
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
                            paper.addAuthor(author);
                            // set author - paper relation
                            author.addPaper(paper);
                            
                        }
                        event.addPaper(paper);
                    }
                }
                conference.addEvent(event);
            }
            conferencesList.add(conference);
        }
        return conferencesList;
    }


    /*Transforms the name representation of a month to an integer between 1 and 12
     *@param month The month to be transformed
     *@return an integer between 1 and 12 for the respective month, 0 if no valid sting is parsed. 
     * */
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

    
    /*The method is used for searching the name of an event for
     * an event category.
     * @param workshopTitle a String to be searched through for a event category
     * @return a event category mentioned in the event title. null if none is found.
     * 
     * */
    private EventCategory getEventType(String eventTitle) {
        if (eventTitle.toLowerCase().contains("BREAK")) {
            return EventCategory.BREAK;
        }
        if (eventTitle.toLowerCase().contains("CEREMONY")) {
            return EventCategory.CEREMONY;
        }
        if (eventTitle.toLowerCase().contains("MEETING")) {
            return EventCategory.MEETING;
        }
        if (eventTitle.toLowerCase().contains("PRESENTATION")) {
            return EventCategory.PRESENTATION;
        }
        if (eventTitle.toLowerCase().contains("RECRUITMENT")) {
            return EventCategory.RECRUITMENT;
        }
        if (eventTitle.toLowerCase().contains("SESSION")) {
            return EventCategory.SESSION;
        }
        if (eventTitle.toLowerCase().contains("SOCIAL")) {
            return EventCategory.SOCIAL;
        }
        if (eventTitle.toLowerCase().contains("TALK")) {
            return EventCategory.TALK;
        }
        if (eventTitle.toLowerCase().contains("TUTORIAL")) {
            return EventCategory.TUTORIAL;
        }
        if (eventTitle.toLowerCase().contains("WELCOME")) {
            return EventCategory.WELCOME;
        }
        if (eventTitle.toLowerCase().contains("WORKSHOP")) {
            return EventCategory.WORKSHOP;
        }

        return null;
    }

    
    /**
     * Extracts the release year + month of the given paper web element
     *
     * @param paper The web element of the paper to get the release year and month of
     * @return The paper's release date, null if the extraction failed
     */
    private LocalDate extractPaperRelease(Document doc) {// Element paper) {
        
        String year = "0";
        String month = "0";
        
        Elements paperInformationElements = doc.select("#main > div > div.col.col-lg-10.order-2 > dl > dd");

        month = paperInformationElements.get(2).text();

        if (month.contains("-")) // some papers have a release month of e.g. "October-November", assume the first
            // month as the release month
            month = month.split("-")[0];

        month = "" + CrawlerToolset.getMonthIndex(month);

        if (month.equals("-1"))
            month = "1"; // resort to january if no month is found

        try {
            year = paperInformationElements.get(3).text().substring(0, 4); //hope that every year is given in 1234 format

        } catch (NumberFormatException n) {
            year = Integer.toString(0);

        }

        return LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
    }
  
    /*
     * {@inheritDoc}
     * 
     * */
    @Override
    public ArrayList<Conference> getConferenceACL2018() throws IOException {
    	
    	ArrayList<Conference> conferencesList = new ArrayList<Conference>();
    	
    	Conference conference = Conference.findOrCreate("Annual Meeting of the Association for Computational Linguistics (2018)");
    	
    	String description = "The 56th Annual Meeting of the Association for Computational Linguistics was held in Melbourne, Australia at the Melbourne Convention and Exhibition Centre from July 15th to 20th, 2018.";
    	
    	LocalDate begin = LocalDate.parse("2018-07-15"); // date: 15-20 July 2018
    	LocalDate end = LocalDate.parse("2018-07-20");
    	
    	LocalDate long_sub_deadline = LocalDate.parse("2018-02-22");
    	LocalDate short_sub_deadline = LocalDate.parse("2018-02-22");
    	LocalDate review_notification = LocalDate.parse("2018-04-20"); // April 20th, 2018
    	
    	conference.setDescription(description);
    	conference.setBegin(begin);
    	conference.setBegin(end);
    	
    	conference.setSubmissionDeadlineLongPaper(long_sub_deadline);
    	conference.setSubmissionDeadlineShortPaper(short_sub_deadline);
    	conference.setReviewNotification(review_notification);
    	conference.setName("Annual Meeting of the Association for Computational Linguistics (2018)");
    	
    	
    	// Adding the tutorial events

    	Document tutorialPage = Jsoup.connect("https://acl2018.org/tutorials/").get();

    	Elements tutorialInfos = tutorialPage.select("body > main > div > article > div");

    	Elements authorInfos = tutorialPage.getElementsByClass("tutorial-presenters");
    	Elements titleInfos = tutorialPage.getElementsByClass("tute-title");
    	Elements timeInfos = tutorialPage.getElementsByClass("tutorial-time");
    	Elements placeInfos = tutorialPage.getElementsByClass("tutorial-location");

    	ArrayList<Event> tutorialsList = new ArrayList<Event>();
    	
    	for (int i = 0; i < authorInfos.size(); i++) {

    		String title = titleInfos.get(i).text();
    		
    		//search if event already exists in the database or crate new one
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
    		//check if multiple author, select first as speaker
    		if (author.contains(",")) {
    			speaker = author.substring(0, author.indexOf(","));
    		}
    		else if (author.contains(" and ")) {
    			speaker = author.substring(0, author.indexOf("and"));
    		}
    		
    		//find or create speaker and add it to the event
    		Person authorObjekt=Person.findOrCreate(null,speaker);
    		
    		tutorial.setPerson(authorObjekt);
    		tutorial.setTitle(title);
    		tutorial.setDescription(abstr); // oder hier eine extra Column "Abstract" in Event anlegen ???
    		tutorial.setPlace(place);
    		tutorial.setBegin(tutorilTimeStart);
    		tutorial.setEnd(tutorilTimeEnd);
    		tutorialsList.add(tutorial);
    	}
    	
    	// Adding the workshop events

    	Document workshopPage = Jsoup.connect("https://acl2018.org/workshops/").get();
    	Elements workshopInfos = workshopPage.select("body > main > div > article > div > ul> li");
    	
 //   	Elements workshopDates = workshopPage.select("body > main > div > article > div > h4");
 //   	String date1 = workshopDates.get(0).attr("id");
//    	String date2 = workshopDates.get(1).attr("id");
    	LocalDate date1 = LocalDate.parse("2018-07-19"); // dates hardcoded for now...
    	LocalDate date2 = LocalDate.parse("2018-07-20");

    	ArrayList<Event> workshopsList = new ArrayList<Event>();

    	for (int i = 0; i < workshopInfos.size(); i++) {
    		String name = workshopInfos.get(i).text().split(": ")[0];

    		Event workshop = Event.findOrCreate(name);
    	
    		workshop.setCategory(EventCategory.WORKSHOP);
    		    		
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
    	
    	
    	
    	// Adding the session events
        
        Document programmePage = Jsoup.connect("https://acl2018.org/programme/schedule/").get();
        Elements sessionInfos= programmePage.select("tr.conc-session-indiv-row");//getElementsByClass("session-row session-name-row conc-session-indiv-row");
        Elements subSessionInfos=programmePage.select("tr.conc-session-details-row");//getElementsByClass("session-row session-name-row conc-session-indiv-row");

        ArrayList<Event> sessionList=new ArrayList<Event>();       
        
        //time block
    	for(int slotN=0;slotN<9;slotN++ ) {
    		
    		
    		//Time:	    	    
    	    String time = programmePage.select("td.conc-session-shared-name").get(slotN).select("div.session-times").text();
    		String[] timeArray =time.split("–");
    		LocalDateTime sessionTimeStart=LocalDateTime.of(2018, 7, 15,Integer.parseInt(timeArray[0].split(":")[0]),Integer.parseInt(timeArray[0].split(":")[1]));
    		LocalDateTime sessionTimeEnd=LocalDateTime.of(2018, 7, 15,Integer.parseInt(timeArray[1].split(":")[0]),Integer.parseInt(timeArray[1].split(":")[1]));
    		
    		//slot in time blo
    		for(int sessionN=0;sessionN<6;sessionN++ ) {
    			
    			
    			Event session=Event.findOrCreate(sessionInfos.get(slotN).select("div.conc-session-name").get(sessionN).text());
    			session.setCategory(EventCategory.SESSION);
    			
    			
    			session.setBegin(sessionTimeStart);
    			session.setEnd(sessionTimeEnd);
    			
    			//Chair of the event
    		    String chairName=sessionInfos.get(slotN).select("div.speakers").get(sessionN).text();
    		    Person chair=Person.findOrCreate(null, chairName);
    		    session.setPerson(chair);
    		    
    		    //Title:
    		    session.setTitle(sessionInfos.get(slotN).select("div.conc-session-name").get(sessionN).text());
    		    //Place:
    		    session.setPlace(programmePage.select("tr.conc-session-loc-row").get(slotN).select("td.conc-session-location").get(sessionN).text());
    		    
    		   	//how many eventparts one session slot has (usually 4)	
    			int number=4;
    		    //execpt for session 8 
    			if(slotN==8) {
    				number=5;
    			}
    			
    			//creating the eventparts for the sub sessions
    			for(int partN=0;partN<number;partN++) {
    				
    				//Subsession-Title:
    				String title=subSessionInfos.get(slotN).select("td.talk-sessions").get(sessionN).select("div.talk").get(partN).select("div.talk-title").text(); 
    				
    				EventPart subSession=EventPart.findOrCreate(title);
    				      				
    				subSession.setTitle(title);
    			    //Subsession-Time (for the diffenent sessions):
    				
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
    				
    				
    				String timeOne=subSessionTimeArray[0];
    				String timeTwo=subSessionTimeArray[1];
    				int timeOneInt=Integer.parseInt(timeOne);
    				int timeTwoInt=Integer.parseInt(timeTwo);

    				
    				LocalDateTime subSessionTimeStart=LocalDateTime.of(2018, 7, day,timeOneInt,timeTwoInt);
    				
    				
    				subSession.setBegin(subSessionTimeStart);
    				subSession.setEnd(subSessionTimeStart.plusMinutes(length));
    				
    				String subSessionDescription;
    				String speakerName;
    				
    				String[] authors;
    				
    				//if subsession entry does not link to www.transacl.org
    			    if(subSessionInfos.get(slotN).select("td.talk-sessions").get(sessionN).select("div.talk").get(partN).attr("title").split("ABSTRACT: ").length>1){
    	   			//Subsession-Speaker:
    			    //selects the authors of the sub session's respective paper	and saves the first author as speaker
    			    	
    			    authors =subSessionInfos.get(slotN).select("td.talk-sessions").get(sessionN).select("div.talk").get(partN).attr("title").replace("Chair: ","").split(". ABSTRACT:")[0].split("; ");		    
    			    speakerName=authors[0];
    			    
    			    Person speaker = Person.findOrCreate(null, speakerName);
    			    subSession.setPerson(speaker);
    			    
    			    subSessionDescription=subSessionInfos.get(slotN).select("td.talk-sessions").get(sessionN).select("div.talk").get(partN).attr("title").split("ABSTRACT: ")[1];
    			    //Subsession-Abstract:
    	   			subSession.setDescription(subSessionDescription);
    	   			
    			    
    	   			
    				//if subsession entry does  link to www.transacl.org
    			    }else {
    			    	
    			    	
    			    	
    			    	String link=subSessionInfos.get(slotN).select("td.talk-sessions").get(sessionN).select("div.talk-title").get(partN).select("a").get(2).attr("href");
    			    	Document transacl = Jsoup.connect(link).get();

    			    	
    			    	
    			       Elements partAuthor= transacl.select("#authorString");
    			       Elements partAbstract= transacl.select("#articleAbstract > div > p");
    			       System.out.println(partAuthor.get(0).text());
    			       
    			       //get the first author as speaker
    			       authors =partAuthor.get(0).text().replace("Chair: ","").split(". ABSTRACT:")[0].split(", ");
       			       speakerName=authors[0];
    			       
    			       
    			       subSessionDescription=partAbstract.text();
    			       subSession.setDescription(subSessionDescription);
    			      
    			       
    			    }
    			    
    			    
    			    //find paper in the database or create a new one     			  
    			    title=title.replace("'","''"); //solves Problem with apostrophe breaking sql syntax
    			    Paper paper=Paper.findOrCreate(null, title);
    			    
    			    //add the authors of the paper
    			    int i=0;
    			    //adding all the authors to the paper
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
    			      			    
    			    subSession.addPaper(paper); 
    			    session.addEventPart(subSession);
    				sessionList.add(session);
    			}
    		
    			if(slotN==8) {
    				break;
    			}
    		}
    		
    		
    		
    	}
      
    	// Adding the poster session events
    	
    	ArrayList<Event> posterSessionList=new ArrayList<Event>();
    	
    	for(int posterSessionN=2;posterSessionN<5;posterSessionN++) {
    		
    		//Time:
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
    	    
    	       
    		//adding the events for each time slot
    		for(int subSessionN=0;subSessionN<7;subSessionN++) {
    			
    		Event posterSession =Event.findOrCreate(programmePage.select("tr.poster-session-row").get(posterSessionN).select("div.poster-sub-session").get(subSessionN).select("div.poster-session-name").text());
    		posterSession.setCategory(EventCategory.POSTERSESSION);
    		
    		
    		posterSession.setBegin(sessionTimeStart);
    		posterSession.setEnd(sessionTimeEnd);
    		//POSTER SESSION
    		       
    		       //Session Name:
    		       posterSession.setTitle(programmePage.select("tr.poster-session-row").get(posterSessionN).select("div.poster-sub-session").get(subSessionN).select("div.poster-session-name").text());
    		      
    			
    			//adding all papers to the poster sessions
    			for(Element poster: programmePage.select("tr.poster-session-row").get(posterSessionN).select("div.poster-sub-session").get(subSessionN).select("span.poster-name")) {
    			 		       
    		       
    			 String posterAbstract;
    			 
    			 String[] authors;
    			 String posterSpeakerName;
    			 
    			 
 				//if poster does not link to www.transacl.org

    			    if(poster.attr("title").split("ABSTRACT: ").length>1){

    		       
    			    	
    			   authors= 	poster.attr("title").split(". ABSTRACT: ")[0].split(";");
    			   
    			   
    		       //Abstract:
    		       
    		       posterAbstract=poster.attr("title").split("ABSTRACT: ")[1];
    		       //if subsession entry does  link to www.transacl.org
    			    }else {
    			    	
    			    	//get the infos from the www.transacl.org site

    			    	String link=poster.select("a").get(1).attr("href");
    			    	Document transacl = Jsoup.connect(link).get();

    			    	
    			    	
    			       Elements partAuthor= transacl.select("#authorString");
    			       
    			       
    			       authors= partAuthor.text().split(", ");
    			       
    			       posterSpeakerName=authors[0];
    			       
    			       Person posterSpeaker=Person.findOrCreate(null, posterSpeakerName);
        			   posterSession.setPerson(posterSpeaker);
        			   
    			           			       
    			       Element partAbstract= transacl.select("head > meta").get(9);//#articleAbstract > div > p");
    			       
    			       
    			       posterAbstract=partAbstract.attr("content");
    				    	
    			    }
    			    
    			    //serch for paper in the database or create new one
    			    String title=poster.text().replace("'","''" ); //prevent errors with apostrophes and sql queries
 			       Paper paper=Paper.findOrCreate(null, title);
 			      
    			    
    			    
 			       	int i=0;
    			    // fill new created paper with authors
    			    for(String speakerN: authors) {
    			    	
        			    Person author = Person.findOrCreate(null, speakerN);
         			    
        			    
        			    
        			    if(paper.getPaperAbstract()==null) {
        			    paper.addAuthor(author);
        			    }
         			    i++;
        			    }
    			    
    			    if(paper.getPaperAbstract()==null) {
  			    	   
   			    	  paper.setPaperAbstract(posterAbstract);
   			    	   
   			       }
    			       
    			       posterSession.addPaper(paper);
    			 
    			    }
    		    posterSessionList.add(posterSession);	

    			
    		}
    		
    		
    	}
    	
    	//add events to conference
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
    	
    
    
    /*
     * {@inheritDoc}
     * 
     * */
    @Override
    public ArrayList<Paper> getTags() throws IOException {
    	
    	
		TagJPAAccess tagFiler = new TagJPAAccess();

    	ArrayList<Paper> paperList=new ArrayList<Paper>();
    	//read the json tag file
	    ArrayList<JSONObject> json=new ArrayList<JSONObject>();
	    JSONObject obj;
	    //TODO: adjust
	    // The name of the file to open.
	   
	    String fileName = "..\\main\\resources\\outputProcessed.json\\";
	    // This will reference one line at a time
	    String line = null;

	    try {
	        // FileReader reads text files in the default encoding.
	        FileReader fileReader = new FileReader(fileName);

	        // Always wrap FileReader in BufferedReader.
	        BufferedReader bufferedReader = new BufferedReader(fileReader);

	        while((line = bufferedReader.readLine()) != null) {
	           

	        	obj = (JSONObject) new JSONParser().parse(line);
	            json.add(obj);
	            
	            //get paper by id
	            String paperID=(String) obj.get("doc_key");
	            Paper paper =Paper.findById(paperID);
	            //skip if not found 
	            if(paper==null) {
	            continue;
	            	
	            }
	          	            
	            //add tags of the type generic
	           JSONArray taskList = (JSONArray) obj.get("generic");
	            Iterator<JSONArray> iterator = taskList.iterator();
	            
	            while (iterator.hasNext()) {
		            String name="";

	                Iterator<String> iterator2 =iterator.next().iterator();
		            while (iterator2.hasNext()) {
		            name=name+iterator2.next();
		            }
		            
		            Tag tag =Tag.findOrCreate(name);
		            tag.setCategory(TagCategory.GENERIC);
		    		tagFiler.add(tag);

		            
		            paper.addTag(tag);
	                
	            }
	            
	            //add tags of the type task
	            
		            taskList = (JSONArray) obj.get("task");
		            iterator = taskList.iterator();
		            
		            while (iterator.hasNext()) {
			            String name="";

		                Iterator<String> iterator2 =iterator.next().iterator();
			            while (iterator2.hasNext()) {
			            name=name+iterator2.next();
			            }
			            
			            Tag tag =Tag.findOrCreate(name);
			            tag.setCategory(TagCategory.TASK);
			    		tagFiler.add(tag);

			            
			            paper.addTag(tag);
		                
		            }		            
		            
		            //add tags of the type metric

		            taskList = (JSONArray) obj.get("metric");
		            iterator = taskList.iterator();
		            
		            while (iterator.hasNext()) {
			            String name="";

		                Iterator<String> iterator2 =iterator.next().iterator();
			            while (iterator2.hasNext()) {
			            name=name+iterator2.next();
			            }
			            
			            Tag tag =Tag.findOrCreate(name);
			            tag.setCategory(TagCategory.METRIC);
			    		tagFiler.add(tag);

			            
			            paper.addTag(tag);
		                
		            }
		            		            
		            
		            //add tags of the type material

		            taskList = (JSONArray) obj.get("material");
		            iterator = taskList.iterator();
		            
		            while (iterator.hasNext()) {
			            String name="";

		                Iterator<String> iterator2 =iterator.next().iterator();
			            while (iterator2.hasNext()) {
			            name=name+iterator2.next();
			            }
			            
			            Tag tag =Tag.findOrCreate(name);
			            tag.setCategory(TagCategory.MATERIAL);
			    		tagFiler.add(tag);

			            
			            paper.addTag(tag);
		                
		            }
		            
		            //add tags of the type otherscientificterm

		            taskList = (JSONArray) obj.get("otherscientificterm");
		            iterator = taskList.iterator();
		            
		            while (iterator.hasNext()) {
			            String name="";

		                Iterator<String> iterator2 =iterator.next().iterator();
			            while (iterator2.hasNext()) {
			            name=name+iterator2.next();
			            }
			            
			            Tag tag =Tag.findOrCreate(name);
			            tag.setCategory(TagCategory.OTHERSCIENTIFICTERM);
			    		tagFiler.add(tag);

			            
			            paper.addTag(tag);
		                
		            }
		            		        
		            //add tags of the type method

		            taskList = (JSONArray) obj.get("method");
		            iterator = taskList.iterator();
		            
		            while (iterator.hasNext()) {
			            String name="";

		                Iterator<String> iterator2 =iterator.next().iterator();
			            while (iterator2.hasNext()) {
			            name=name+iterator2.next();
			            }
			            
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
    
   
    
    
    @Override
    public void close() {
    }
}