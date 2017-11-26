/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package swap.gerbilannotatorsample;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.aksw.gerbil.transfer.nif.Document;
import org.aksw.gerbil.transfer.nif.Marking;
import org.aksw.gerbil.transfer.nif.Span;
import org.aksw.gerbil.transfer.nif.TurtleNIFDocumentCreator;
import org.aksw.gerbil.transfer.nif.TurtleNIFDocumentParser;
import org.aksw.gerbil.transfer.nif.data.NamedEntity;
import org.aksw.gerbil.transfer.nif.data.SpanImpl;
import org.restlet.representation.Representation;
import org.restlet.resource.Post;
import org.restlet.resource.ServerResource;

import com.google.gson.GsonBuilder;

import it.cnr.isti.hpc.dexter.common.Field;
import it.cnr.isti.hpc.dexter.common.MultifieldDocument;
import it.cnr.isti.hpc.dexter.rest.client.DexterRestClient;
import it.cnr.isti.hpc.dexter.rest.domain.AnnotatedDocument;
import it.cnr.isti.hpc.dexter.rest.domain.AnnotatedSpot;

/**
 *
 * @author pierpaolo
 */
public class GerbilAnnotator extends ServerResource {

    private static final Logger LOG = Logger.getLogger(GerbilAnnotator.class.getName());

    private final TurtleNIFDocumentParser parser = new TurtleNIFDocumentParser();

    private final TurtleNIFDocumentCreator creator = new TurtleNIFDocumentCreator();
    

    @Post
    public String accept(Representation request) {
        Reader inputReader;
        try {
            inputReader = request.getReader();
        } catch (IOException ex) {
            LOG.log(Level.SEVERE, "Exception to read request", ex);
            return "";
        }
        Document document;
        try {
            document = parser.getDocumentFromNIFReader(inputReader);
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, "Exception to read request.", ex);
            return "";
        }
        String text = document.getText();
        System.out.println(text + "\n");
        
        
        List<Marking> mark = document.getMarkings();
        String s = "";
        for(Iterator<Marking> it = mark.iterator(); it.hasNext(); )
        {
        	Marking temp = it.next();
        	SpanImpl n = (SpanImpl) temp;
        	int start = n.getStartPosition();
        	int len = n.getLength();
        	String mention = text.substring(start, (start + len)).toLowerCase();
        	String add = mention + ":" + start + "," + (start + len) + ";";
        	
        	/*
        	if(add.equalsIgnoreCase(mention+":"+";"))
        	{
        		Scanner in = new Scanner(System.in);
        		String userInput = in.nextLine();
        		in.close();
        	}
        	 */
        	s = s + add;
        }
        
        System.out.println(s);
        System.out.println("Fine menzioni Gerbil");
        
        /*
        try 
        {
        	FileOutputStream prova = new FileOutputStream("/Users/giovanniizzi/Desktop/dexter.txt", true);
            PrintStream scrivi = new PrintStream(prova);
            scrivi.println(text);
            scrivi.println();
            scrivi.println(s);
            scrivi.println();
            scrivi.println();
            scrivi.close();
        }
        catch (IOException e)
        {
            System.out.println("Errore: " + e);
        }
        */
        
        if (s == "")
        {
        	String nifDocument = creator.getDocumentAsNIFString(document);
            return nifDocument;
        }
        
        
        
        DexterRestClient client;
        try 
        {
			client = new DexterRestClient("http://localhost:8080/dexter-webapp/api/rest");
		} 
        catch (URISyntaxException e) 
        {
			e.printStackTrace();
			return null;
		}
        
        MultifieldDocument multiDocument = new MultifieldDocument();
        multiDocument.addField(new Field("body", s));
        
        client.setSpotter("nif");
        client.setDisambiguator("genetic");
		
		client.setLinkProbability(0.03);
		client.setWikinames(true);

		AnnotatedDocument sd = client.annotate(multiDocument);
		System.out.println(new GsonBuilder().setPrettyPrinting().create().toJson(sd));
		System.out.println("Fine stampa gsonBuilder");
		
		List<AnnotatedSpot> annSpot = sd.getSpots();
		
        
        //TO DO annotate text
        //List<Span> esl = document.getMarkings(Span.class);
		
        // Annotator should return marking
        List<Marking> entities=new ArrayList<>();
		
        // example of marking (start, length, uri)
        //entities.add(new NamedEntity(43, 10, "uri"));
        
        String baseUri = "http://dbpedia.org/resource/";
        //String baseUri = "http://en.wikipedia.org/wiki/";
        for(Iterator<AnnotatedSpot> it = annSpot.iterator(); it.hasNext(); )
        {
        	AnnotatedSpot as = it.next();
        	String uri = baseUri + as.getWikiname();
        	int len = as.getEnd() - as.getStart();
        	entities.add(new NamedEntity(as.getStart(), len, uri));
        	
        }
        
        document.setMarkings(entities);
        
        
        //return document
        String nifDocument = creator.getDocumentAsNIFString(document);
        return nifDocument;
    }

}
