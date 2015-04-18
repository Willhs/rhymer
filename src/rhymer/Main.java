package rhymer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.List;

import rhymer.extract.Extractor;

public class Main {

	private static final String TEXT_PATH = "text" + File.separator;

	public static void main(String[] args){

		List<URL> queryURLs = Extractor.googleSearchResultsParser("dogs", 10);
		
		String contentString = "";
		
		for (URL url : queryURLs){
			contentString += Extractor.extractTextFromWebPage(url);
		}

		System.out.println("Extracted content with: " + contentString.length() + " characters");
		
		new Rhymer(contentString);
	}
	
	private static String extractContent(String contentFN) {
		BufferedReader reader = null;

		try {
			reader = new BufferedReader(new FileReader(contentFN));
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
