package de.tudarmstadt.informatik.ukp.athena.knowledgebase.PDFParser;

import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.InstitutionJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.jpa.PaperJPAAccess;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Institution;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Paper;
import de.tudarmstadt.informatik.ukp.athena.knowledgebase.database.models.Person;
import org.allenai.scienceparse.ExtractedMetadata;
import org.allenai.scienceparse.Section;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Parser {

	
	/**
	 * Uses the allenAI parser to parse a pdf file of a paper given by a url to an
	 * ExtractedMetadata Object which contains the sections of the paper as plain text. 
	 * e.g. List<Section> sections = ExMe.getSections();
	 * section.getText();
	 * @param parser an allenAI parser (parser = Parser.getInstance();)
	 * @param url the url of the paper pdf
	 * @return an ExtractedMetadata Object containing the papers sections
	 */
    public ExtractedMetadata scienceParse(org.allenai.scienceparse.Parser parser, URL url) {
        ExtractedMetadata em = null;
        try {
            InputStream inputStream = getConnectionFromURL(url).getInputStream();
            System.out.println("URL Für Stream: " + url);
            em = parser.doParse(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return em;
    }
    
    
    
    /**
	 * Uses the pdfBox to parse a pdf file of a paper given by a url to 
	 * a plain text.
	 * @param stripper Instance of the PDFTextStripper (PDFTextStripper stripper= new PDFTextStripper();)
	 * @param urli the url of the paper pdf
	 * @return The paper plain text as string
	 */
    public String plainParse(PDFTextStripper stripper, URL urli){
        String doc = null;
        try {
            InputStream inputStream = getConnectionFromURL(urli).getInputStream();
            PDDocument pddDocument = PDDocument.load(inputStream);
            doc = stripper.getText(pddDocument);
            pddDocument.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    return doc;
    }

    
    /**
	 * Creates a connection to the specified url
	 * @param url the url the connection is established to 
	 * @return an HttpURLConnection object that can be transformed to an input stream by .getInputSream()
	 */

    public static HttpURLConnection getConnectionFromURL(URL url) throws IOException {
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

    
    public static void parseInstitution(){
        PaperJPAAccess paperAccess = new PaperJPAAccess();
        InstitutionJPAAccess instAccess = new InstitutionJPAAccess();
        List<Paper> papers = paperAccess.get();
        List<Institution> institutions = instAccess.get();
        HashMap<String, Institution> institutionNames = new HashMap<>();
        for(Institution i : institutions) {
            institutionNames.put(i.getName(), i);
        }

        String pt;
        ArrayList<Institution> institutionsToAdd= new ArrayList();
        for(Paper paper : papers) {
            pt = paper.getPaperPlainText();
            Set<Person> authors = paper.getAuthors();
            HashMap<String, Person> paperAuthorNames = new HashMap<>();
            if (authors.isEmpty()) {
                System.out.println("no Authors or Institutions found for paper" + paper.getTitle());
                continue;
            }
            for (Person a : authors) {
                paperAuthorNames.put(a.getFullName(), a);
            }

            String[] columns = pt.split("\n");
            int oldi = 0;
            int starti = 0;
            String instiName = "";
            ArrayList<Person> authorCandidates = new ArrayList();
            for (int i = 0; i < columns.length && i < 100; i++) {
                if (columns[i].contains("@")) { //Email in Text
                    for (int k = starti + 1; k < i; k++) {
                        instiName = instiName + columns[k];
                    }
                    if (!institutionNames.containsKey(instiName.trim())) {
                        Institution insti = new Institution();
                        insti.setName(instiName);
                        for (Person a : authorCandidates) {
                            insti.addPerson(a);
                            a.setInstitution(insti);
                            System.out.println(insti);
                        }
                    } else {
                        for (Person a : authorCandidates) {
                            Institution insti = institutionNames.get(instiName);
                            if (a.getInstitution().equals(insti)) continue;
                            insti.addPerson(a);
                            a.setInstitution(insti);
                            System.out.println(insti);
                        }
                    }
                }
            else if (paperAuthorNames.containsKey(columns[i].trim())) {
                starti = i;
                authorCandidates.add(paperAuthorNames.get(columns[i]));
            }// author name eine der columns --> namen speichern und i neu setzen
        }
        }

    }
}

