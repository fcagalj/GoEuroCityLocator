/**
 * @author fcagalj
 * GoEuro City Locator
 */
package com.goeuro.citylocator;

import dep.com.google.gson.JsonArray;
import dep.com.google.gson.JsonElement;
import dep.com.google.gson.JsonIOException;
import dep.com.google.gson.JsonObject;
import dep.com.google.gson.JsonParser;
import dep.com.google.gson.JsonSyntaxException;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * GoEuroCityLocator
 Class enable querying the GoEuro API for town geo informations. 
 * Town name is provided as first command line attribute.
 * Expected response is JSON array of objects representing matching towns.
 * Formatted output is saved to output file goeuro_towns_res.csv 
 */
public class GoEuroCityLocator {

    /**
     * @param args command line arguments:
     *      args[0] - town name
     */
    public static void main(String[] args) {
        
        if (args.length < 1) {
            System.err.println("First command line argument must be town name!");
            System.exit(1);
        }

        try {
            
            String sURL = "http://api.goeuro.com/api/v2/position/suggest/en/" + args[0];
            
            // Connect to the URL
            URL url = new URL(sURL);
            HttpURLConnection request = (HttpURLConnection) url.openConnection();
            request.connect();
            
            // Convert response to JSON object
            JsonParser jp = new JsonParser();
            JsonElement holder = jp.parse(new InputStreamReader((InputStream) request.getContent()));
            JsonArray towns = holder.getAsJsonArray();
            
            // Prepaire file output
            Iterator o = towns.iterator();
            String output = "";
            while (o.hasNext()) {
                JsonObject town = (JsonObject) o.next();
                output += town.get("_id").toString() + ",";
                output += town.get("name").toString() + ",";
                output += town.get("type").toString() + ",";
                output += town.getAsJsonObject("geo_position").get("latitude").toString() + ",";
                output += town.getAsJsonObject("geo_position").get("longitude").toString() + "\n";
            }
            
            // Output to file
            writeToFile("goeuro_towns_res.csv", output);
            
        } catch (IOException | JsonIOException | JsonSyntaxException e) {
            System.err.println("Error fetching JSON: " + e.getMessage());
        } 
    }
    
    /**
     * Save output file. 
     * @param fileName
     * @param content
     * @throws FileNotFoundException 
     */
    private static void writeToFile(String fileName, String content) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(fileName));
            writer.write(content);
            writer.close();
        } catch (IOException e) {
            System.err.println("Error writing to file " + fileName + " " + e.getMessage());
        }
    }
}
