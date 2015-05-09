package rhymer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Set;

import javax.swing.JOptionPane;

import rhymer.extract.WebExtractor;
import rhymer.lang.Rhyme;

public class Main {

	private static final String TEXT_PATH = "text" + File.separator;

	public static void main(String[] args){

		String query = JOptionPane.showInputDialog("Search for rhymes about...");
		int numPages = 35;
		List<URL> queryURLs = WebExtractor.parseGoogleSearchResults(query, numPages);

		String contentString = "";

		for (URL url : queryURLs){
			contentString += WebExtractor.extractTextFromWebPage(url);
		}

		//String contentString = extractFromFile(TEXT_PATH + "computer-wiki.txt");

		System.out.println("Extracted content with: " + contentString.length() + " characters");

		Rhymer r = new Rhymer();
		Set<Rhyme> rhymes = r.extractRhymes(contentString);
		r.printRhymes(rhymes, 50);
	}

	private static String extractFromFile(String filename) {
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(filename));
		} catch (FileNotFoundException e){
			e.printStackTrace();
		}

		String contentString = "";
		String line;
		try {
			while ((line = reader.readLine()) != null){
				contentString += line;
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return contentString;
	}
}
