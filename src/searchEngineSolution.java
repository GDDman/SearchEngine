// JOSH LIU ID: 260612384

import java.util.*;
import java.io.*;

// This class implements a google-like search engine
public class searchEngineSolution {

	public HashMap<String, LinkedList<String> > wordIndex; // this will contain a set of pairs (String, LinkedList of Strings)
	public directedGraph internet; // this is our internet graph

	// Constructor initializes everything to empty data structures
	// It also sets the location of the internet files
	
	searchEngineSolution() {
		// Below is the directory that contains all the internet files
		htmlParsing.internetFilesLocation = "internetFiles";
		wordIndex = new HashMap<String, LinkedList<String> > ();
		internet = new directedGraph();
	}

	// Returns a String description of a searchEngine
	public String toString() {
		return "wordIndex:\n" + wordIndex + "\ninternet:\n" + internet;
	}

	// This does a graph traversal of the internet, starting at the given url.
	// For each new vertex seen, it updates the wordIndex, the internet graph,
	// and the set of visited vertices. Recursive.

	void traverseInternet(String url) throws Exception {
		
		// add the url to the directed graph and set is as visited
		internet.addVertex(url);
		internet.setVisited(url, true);
		
		// creates lists for the urls connected to the current url and the url content (words in link)
		LinkedList<String> links = htmlParsing.getLinks(url);
		LinkedList<String> content = htmlParsing.getContent(url);
		
		// iterator to go through content and link list
		Iterator<String> i = content.iterator();
		
		// loop through content array 
		while (i.hasNext()) {
			String word = i.next();
			// The word has already been indexed
			if (wordIndex.containsKey(word)) {
				// add in the url in the Hashmap with the word as the key unless the url has already been added
				if (!wordIndex.get(word).contains(url)) {
					wordIndex.get(word).addLast(url);
				}
			} else {
				// the word has no associated url, so add a new list to store the new url(s)
				LinkedList<String> templist = new LinkedList<String>();
				templist.addLast(url);
				wordIndex.put(word, templist);
			}
		}
		
		// loop through links array
		i = links.iterator();		
		while (i.hasNext()) {
			String link = i.next();
			internet.addEdge(url, link);
			// if the link has not already been visited, recursively go through that one
			if (!internet.getVisited(link)) traverseInternet(link);
		}
	}

	/* This computes the pageRanks for every vertex in the internet graph.
	It will only be called after the internet graph has been constructed using
	traverseInternet.
	Use the iterative procedure described in the text of the assignment to
	compute the pageRanks for every vertices in the graph.
	
	This method will probably fit in about 30 lines.
	*/
	
	void computePageRanks() {

		// get a list of vertices of your graph
		LinkedList<String> vertices = internet.getVertices();
		Iterator<String> i = vertices.iterator();
		
		// set all page ranks to 1 initially
		while (i.hasNext()) internet.setPageRank(i.next(), 1);
		
		// 100 iterations is sufficient
		for (int x = 0; x < 100; x++) {
			// set vertices iterator back to the beginning 
			i = vertices.iterator();
			// loop through vertices
			while (i.hasNext()) {
				String vertex = i.next();
				// initial page rank value
				double pagerank = 0.5;
				
				// does it have mentions?
				Iterator<String> mentions = internet.getEdgesInto(vertex).iterator();
				while (mentions.hasNext()) {
					String next = mentions.next();
					// add to the page rank with the given formula
					pagerank += 0.5*(internet.getPageRank(next)/internet.getOutDegree(next));
				}
				// set the pr
				internet.setPageRank(vertex, pagerank);
				System.out.println("page rank of "+ vertex + " is " + pagerank + " with " + internet.getEdgesInto(vertex).size() + " mentions.");
			}
		}
	}
		
	/* Returns the URL of the page with the high page-rank containing the query word
	Returns the String "" if no web site contains the query.
	This method can only be called after the computePageRanks method has been executed.
	Start by obtaining the list of URLs containing the query word. Then return the URL
	with the highest pageRank.
	This method should take about 25 lines of code.
	*/
	
	String getBestURL(String query) {

		// List of sites that contain the query word
		LinkedList<String> sites;
		
		// put the input word to lowercase
		query = query.toLowerCase();
		
		// check if the query word exists and get the url list if it does
		if (wordIndex.containsKey(query)) sites = wordIndex.get(query);
		else return new String("Word not found in any url.");
		
		Iterator<String> i = sites.iterator();
		String besturl = "";
		double highestpr = -1;
		// go through urls and get their ranks, find highest one
		while (i.hasNext()) {
			String url = i.next();
			double urlpr = internet.getPageRank(url);
			if (urlpr > highestpr) {
				highestpr = urlpr;
				besturl = url;
			}
		}
		System.out.println("The best url is: "+ besturl + " with a page rank of " + highestpr + ".");
		return besturl;
	}
		
	// main method
	public static void main(String args[]) throws Exception {
		
		searchEngineSolution mySearchEngine = new searchEngineSolution();
		// to debug your program, start with.
		//mySearchEngine.traverseInternet("http://www.cs.mcgill.ca/~blanchem/250/a.html");
		// When your program is working on the small example, move on to
		mySearchEngine.traverseInternet("http://www.cs.mcgill.ca");
		// this is just for debugging purposes
		System.out.println(mySearchEngine);
		mySearchEngine.computePageRanks();
		// this is just for debugging purposes
		System.out.println(mySearchEngine);
		BufferedReader stndin = new BufferedReader(new InputStreamReader(System.in));
		String query;
		
		do {
			System.out.print("Enter query: ");
			query = stndin.readLine();
			if (query != null && query.length() > 0) {
				System.out.println("Best site = " + mySearchEngine.getBestURL(query));
			}
		} while (query != null && query.length() > 0);
	}
}