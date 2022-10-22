package finalproject;
 
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Arrays;
 
public class SearchEngine {
	public HashMap<String, ArrayList<String> > wordIndex;   // this will contain a set of pairs (String, LinkedList of Strings)	
	public MyWebGraph internet;
	public XmlParser parser;
 
	public SearchEngine(String filename) throws Exception{
		this.wordIndex = new HashMap<String, ArrayList<String>>();
		this.internet = new MyWebGraph();
		this.parser = new XmlParser(filename);
	}
	
	/* 
	 * This does a graph traversal of the web, starting at the given url.
	 * For each new page seen, it updates the wordIndex, the web graph,
	 * and the set of visited vertices.
	 * 
	 * 	This method will fit in about 30-50 lines (or less)
	 */
	public void crawlAndIndex(String url) throws Exception {
		ArrayList<String> neighborUrls = this.parser.getLinks(url);
		/* ArrayList of vertices (that connect to the given url)
		 * => to be added to MyWebGraph 
		 */
		
 
		/* ArrayList of words in the web page located at the given url 
		 * UPDATE wordIndex:
		 * key = a different word found on the url
		 * value = arrayList of urls that contain that word
		 * */
		// Set url = been visited
		
		this.internet.addVertex(url);		// Add the given url as a vertex to the web graph
		this.internet.setVisited(url, true);
		
		
		for (String each_url : neighborUrls) {		// Visit each neigbor url for the given url
			this.internet.addVertex(each_url);
			this.internet.addEdge(url, each_url);
			if (this.internet.getVisited(each_url) == false) {
				crawlAndIndex(each_url);
			}
		}
		ArrayList<String> content = this.parser.getContent(url);
		for (String eachWord : content) {
				ArrayList<String> list;
				// wordIndex does not contain this word yet
				if (this.wordIndex.containsKey(eachWord.toLowerCase()) == false) {
					list = new ArrayList<String>();
					list.add(url);
					this.wordIndex.put(eachWord.toLowerCase(),list);
				} 
				// wordIndex already contains this word
				else if (this.wordIndex.containsKey(eachWord.toLowerCase()) == true) {
						list = this.wordIndex.get(eachWord.toLowerCase());
						if (this.wordIndex.get(eachWord.toLowerCase()) != null && this.wordIndex.get(eachWord.toLowerCase()).contains(url) == false) {
							list.add(url);
						}
				}
		}
	}
	
	
	
	/* 
	 * This computes the pageRanks for every vertex in the web graph.
	 * It will only be called after the graph has been constructed using
	 * crawlAndIndex(). 
	 * To implement this method, refer to the algorithm described in the 
	 * assignment pdf. 
	 * 
	 * This method will probably fit in about 30 lines.
	 */
	public void assignPageRanks(double epsilon) {
		// TODO : Add code here
		ArrayList<String> vertices = this.internet.getVertices();
		int numOfVertices = this.internet.getVertices().size();
		
		// Initalize the page rank of each url to 1.0
		for (String eachUrl : vertices) {
			this.internet.setPageRank(eachUrl, 1.0);
		}
		// Initialize the rank of each url to 0 first
		double[] pr = new double[numOfVertices];
		ArrayList<Double> tmp = computeRanks(vertices);
		
		double[] temp = new double[numOfVertices];
		for (int k = 0; k < numOfVertices ; k++) {
			temp[k] = tmp.get(k);
			this.internet.setPageRank(this.internet.getVertices().get(k), temp[k]);
		}
 
		ArrayList<Double> tmp2 = computeRanks(vertices);
		
		for (int i = 0; i < 4; i++) {
			double diff = Math.abs(temp[i] - pr[i]);	
			
			while (diff <= epsilon) {
				i++;
				diff = Math.abs(temp[i] - pr[i]);
				if (diff <= epsilon && i == numOfVertices -1) {
					break;
				}
			}
			while (diff > epsilon) {
				tmp2 = computeRanks(vertices);
				for (int h = 0; h < numOfVertices; h++) {
					pr[h] = temp[h];
					temp[h] = tmp2.get(h);
					this.internet.setPageRank(this.internet.getVertices().get(h), temp[h]);
				}
				diff = Math.abs(temp[i] - pr[i]);	
			}
		} 
	}
 
	/*
	 * The method takes as input an ArrayList<String> representing the urls in the web graph 
	 * and returns an ArrayList<double> representing the newly computed ranks for those urls. 
	 * Note that the double in the output list is matched to the url in the input list using 
	 * their position in the list.
	 */
	public ArrayList<Double> computeRanks(ArrayList<String> vertices) {
		// TODO : Add code here
		ArrayList<Double> urlsPageRankList = new ArrayList<Double>();
		double pr = 0.0;
		int j = 0;
		for (String eachUrl : vertices) {
			double funcOfpr = 0.0;
			ArrayList<String> neighbors = this.internet.getEdgesInto(eachUrl);
			if (neighbors.size() == 0) {
				pr = 0.5;
			} else {
				for (int i = 0; i <= neighbors.size()-1; i++) {
					funcOfpr = funcOfpr + this.internet.getPageRank(neighbors.get(i)) / 
							this.internet.getOutDegree(neighbors.get(i));
					pr = (1.0 / 2.0) + (1.0 / 2.0) * funcOfpr;
				}
			}
			urlsPageRankList.add(j, pr);
			j++;
		}
		return urlsPageRankList;
	}
 
	
	/* Returns a list of urls containing the query, ordered by rank
	 * Returns an empty list if no web site contains the query.
	 * 
	 * This method should take about 25 lines of code.
	 */
	public ArrayList<String> getResults(String query) {
		// TODO: Add code here
		if (this.wordIndex.get(query) == null) {
			return null;
		} else {
			ArrayList<String> urlsList = this.wordIndex.get(query);
			HashMap<String,Double> rankOfUrls = new HashMap<String,Double>();
			for (String eachUrl : urlsList) {
				rankOfUrls.put(eachUrl, this.internet.getPageRank(eachUrl));
			}
			ArrayList<String> result = Sorting.slowSort(rankOfUrls);
			return result;
		}
	}
}
