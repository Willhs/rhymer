package rhymer.extract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

import de.l3s.boilerpipe.BoilerpipeExtractor;
import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;
import de.l3s.boilerpipe.extractors.CommonExtractors;
import de.l3s.boilerpipe.sax.HTMLHighlighter;

public class Extractor {

	public static String extractTextFromWebPage(URL url){
		long startTime = System.currentTimeMillis();
		System.out.print("Extracting content from " + url.getHost() + "... ");
		
		String text = "";
		
		// choose from a set of useful BoilerpipeExtractors...
	//	final BoilerpipeExtractor extractor = CommonExtractors.ARTICLE_EXTRACTOR;
		final BoilerpipeExtractor extractor = CommonExtractors.DEFAULT_EXTRACTOR;
	//	final BoilerpipeExtractor extractor = CommonExtractors.CANOLA_EXTRACTOR;
	//	final BoilerpipeExtractor extractor = CommonExtractors.LARGEST_CONTENT_EXTRACTOR;
		
		try {
			// choose the operation mode (i.e., highlighting or extraction)
			final HTMLHighlighter hh = HTMLHighlighter.newHighlightingInstance();

			text = hh.process(url, extractor);
			
			// write highlighted output
			PrintWriter out = new PrintWriter("highlighted/" + url.getHost() + ".html", "UTF-8");
			out.println("<base href=\"" + url + "\" >");
			out.println("<meta http-equiv=\"Content-Type\" content=\"text-html; charset=utf-8\" />");
			out.println(text);
			out.close();
			
		} catch (BoilerpipeProcessingException e) {
			e.printStackTrace();
		} catch (Exception e){
			long timeTaken = System.currentTimeMillis() - startTime;
			System.err.println(e.getLocalizedMessage() + " after: " + timeTaken + "ms");
		} 
		
		long timeTaken = System.currentTimeMillis() - startTime;
		System.out.println("done in " + (timeTaken)+ "ms");
		
		return text;
	}
	
	private static void outputHighlightedText(URL url) throws Exception {
		// choose from a set of useful BoilerpipeExtractors...
	//	final BoilerpipeExtractor extractor = CommonExtractors.ARTICLE_EXTRACTOR;
		final BoilerpipeExtractor extractor = CommonExtractors.DEFAULT_EXTRACTOR;
	//  final BoilerpipeExtractor extractor = CommonExtractors.CANOLA_EXTRACTOR;
	//	final BoilerpipeExtractor extractor = CommonExtractors.LARGEST_CONTENT_EXTRACTOR;

		// choose the operation mode (i.e., highlighting or extraction)
		final HTMLHighlighter hh = HTMLHighlighter.newHighlightingInstance();
//		final HTMLHighlighter hh = HTMLHighlighter.newExtractingInstance();
		
		PrintWriter out = new PrintWriter("highlighted/" + url.getHost() + ".html", "UTF-8");
		out.println("<base href=\"" + url + "\" >");
		out.println("<meta http-equiv=\"Content-Type\" content=\"text-html; charset=utf-8\" />");
		out.println(hh.process(url, extractor));
		out.close();
	}
	
	public static void main(String[] args){

		List<URL> queryURLs = parseGoogleSearchResults("dogs", 100);
		
		String contentString = "";
		for (URL url : queryURLs){
			contentString += extractTextFromWebPage(url);
		}
	}
	
	/**
	 * Google search url guide: http://moz.com/ugc/the-ultimate-guide-to-the-google-search-parameters
	 * @param query
	 * @param numResults must be <= 100
	 * @return search result URLs
	 */
	public static List<URL> parseGoogleSearchResults(String query, int numResults){
		long startTime = System.currentTimeMillis();
		// format query
		String formattedQuery = query.replace(" ", "+");
		String google = "http://www.google.com";
		String urlString = google + "/search?q=" + formattedQuery + "&num=" + numResults;
		Document doc = null;
		try {
			 HttpURLConnection httpCon = (HttpURLConnection) new URL(urlString).openConnection();
	            //httpCon.addRequestProperty("User-Agent", System.getProperty("http.agent"));
			 	httpCon.addRequestProperty("User-Agent", "Chrome/42.0.2311.90"); // Chrome/20 worked too

	            BufferedReader in = new BufferedReader(new InputStreamReader(
	                    httpCon.getInputStream()));

	            String responseString = "";
	            String line = null;
	            while ((line = in.readLine()) != null) {
	                responseString += line + "\n";
	            }
	            doc = Jsoup.parse(responseString);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		List<URL> resultURLs = new ArrayList<>();
		
		Elements resultHeadings = doc.select(".r");
		
		for (Element resultHeading : resultHeadings){
			if (resultHeading.childNodeSize() == 0)
				System.err.println("Result heading has no link!");
			
			Node linkElem = resultHeading.childNode(0);
			String href = linkElem.attr("href");
			String prefix = "/url?q=";
			if (href.startsWith(prefix)){
				try {
					String trashStart = "&sa=U&ei"; // indicates useless end of href
					String link = href.substring(prefix.length(), href.indexOf(trashStart));
					System.out.println("link: "  + link);
					resultURLs.add(new URL(link));
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
			}
		}
		long timeSpent = System.currentTimeMillis() - startTime;
		System.out.println("Fetched " + resultURLs.size() + " URLS from google in " + timeSpent + "ms");
		return resultURLs;
	}
}
