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
import org.allenai.scienceparse.ExtractedMetadata;
import org.allenai.scienceparse.Parser;
import org.allenai.scienceparse.Section;
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
                return uniqueURL;
            }
            links.stream().map((link) -> link.attr("abs:href")).forEachOrdered((this_url) -> {
                        uniqueURL.add(this_url);
                    }
            );
        } catch (IOException ex) {
            System.out.print(ex);
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
        Set<String> uniqueURL = get_links("https://aclweb.org/anthology/events/");
        System.out.println("Projekt läuft!");
        org.allenai.scienceparse.Parser parser = null;
        PDFTextStripper stripper = null;
        de.tudarmstadt.informatik.ukp.athena.knowledgebase.PDFParser.Parser myparse = new de.tudarmstadt.informatik.ukp.athena.knowledgebase.PDFParser.Parser();

        try {
            parser = Parser.getInstance();
            stripper = new PDFTextStripper();
        } catch (Exception e) {
            e.printStackTrace();
        }
        HashSet<String> uniqueConverenceURLs = uniqueURL.stream()
                .filter(p -> p.contains("https://aclweb.org/anthology/events/")).collect(Collectors.toCollection(HashSet::new));
        ArrayList<String> converenceURLs = selector(uniqueConverenceURLs, this.conferences, this.beginYear, this.endYear).stream().collect(Collectors.toCollection(ArrayList::new));;
        String[] array = converenceURLs.stream().toArray(n -> new String[n]);
        System.out.println(Arrays.toString(array));
        ArrayList<ArrayList<String>> eventsPerConference = new ArrayList<ArrayList<String>>();
        int i = 0;
        for (String s : array) {
            i++;
            eventsPerConference.add(get_links(s).stream()
                    .filter(p -> p.contains("volumes") && !p.contains(".bib")).collect(Collectors.toCollection(ArrayList::new)));
            System.out.println("#Events " + i);
            if(i > 1) break;
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
        for (int x = 0; x < eventsPerConference.size(); x++) { // geändert, war vorher  eventsPerConference.size()
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
                        conference.setBegin(LocalDate.parse(date));
                        conference.setEnd(LocalDate.parse(date));
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
                        if (conferences.length != 0)
                            continue;
                        Elements titleElement = doc.select("#title > a");
                        String paperTitle = titleElement.get(0).text();//doc.title();// splitRawTitle[1];
                        String anthology = paperInformationElements.get(0).text();// splitRawTitle[0].replace("[",
                        // "").replace("]", "");
                        if(paperTitle.contains("'")) paperTitle.replace("'", "X");
                        Paper paper = Paper.findOrCreate(null, paperTitle);
                        paper.setTitle(paperTitle);
                        paper.setAnthology(anthology);
                        String remoteLink = "http://aclweb.org/anthology/" + anthology;
                        paper.setRemoteLink(remoteLink);
        				paper.setReleaseDate(null);
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

    private LocalDate extractPaperRelease(Document doc) {
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
            Integer.parseInt(year);
        } catch (NumberFormatException n) {
            year = Integer.toString(0);
        }
        return LocalDate.of(Integer.parseInt(year), Integer.parseInt(month), 1);
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

    @Override
    public void close() {
        papers.clear();
    }
}