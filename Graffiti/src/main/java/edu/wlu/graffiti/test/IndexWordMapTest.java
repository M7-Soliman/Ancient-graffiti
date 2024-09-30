package edu.wlu.graffiti.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class IndexWordMapTest {
	
	public static void main(String args[]) {
		String l = getLatinLemma("abc");
		System.out.println(l);
	}

	/**
	 * Returns the lemma for a given word using Exeter's Latin WordNet
	 * 
	 * If a lemma is not found, then the empty string is returned
	 */
	private static String getLatinLemma(String word) {
		String lemma = "";
		String morpho;
		String uri;
		String url = "https://latinwordnet.exeter.ac.uk/lemmatize/" + word + "/?format=json";
		try {
			// Read in the json information from the given url
			BufferedReader in = new BufferedReader(new InputStreamReader(new URL(url).openStream()));
			String json = "";
			String input;
	        while ((input = in.readLine()) != null) {json += "\n" +(input);}
	        in.close();
	        // Convert the grabbed json string to a tree of nodes
	        json = json.substring(2, json.length()-1); 
	        JsonNode rootNode = new ObjectMapper().readTree(json);
	        JsonNode lemmaNode = rootNode.path("lemma");
	        lemma = lemmaNode.path("lemma").asText();
	        morpho = lemmaNode.path("morpho").asText();
	        uri = lemmaNode.path("uri").asText();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return lemma;
	}
}

