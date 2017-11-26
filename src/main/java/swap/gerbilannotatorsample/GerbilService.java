/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package swap.gerbilannotatorsample;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Inet4Address;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.restlet.Component;
import org.restlet.data.Protocol;

import info.debatty.java.stringsimilarity.JaroWinkler;
import info.debatty.java.stringsimilarity.NormalizedLevenshtein;

/**
 *
 * @author pierpaolo
 */
public class GerbilService {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) 
    {
        try 
        {
        	System.out.println(Inet4Address.getLocalHost().getHostAddress());
            // Create a new Component.
            Component component = new Component();
            
           
            if(args.length > 1)
            {
            	
            
            // Add a new HTTP server listening.
            	component.getServers().add(Protocol.HTTP, args[0], Integer.parseInt(args[1]));
            }
            else
            	component.getServers().add(Protocol.HTTP, "localhost", 8980);
            
            //Test server prof
            
            //GET
            /*
            URL url = new URL("http://193.204.187.35:9100/nesim/sim/5981816/77877");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            BufferedReader read = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            String line = read.readLine();
            System.out.println(line);
            read.close();
            */
         
			
            
            
            // Attach the sample application.
            component.getDefaultHost().attach("/gerbil", new GerbilApplication());

            // Start the component.
            component.start();
        } catch (Exception ex) {
            Logger.getLogger(GerbilService.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
