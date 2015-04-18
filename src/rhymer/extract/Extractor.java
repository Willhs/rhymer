package rhymer.extract;

import java.net.MalformedURLException;
import java.net.URL;

import de.l3s.boilerpipe.BoilerpipeProcessingException;
import de.l3s.boilerpipe.extractors.ArticleExtractor;

public class Extractor {

	public static String extractText(URL url){
		String text = "";
		try {
			text = ArticleExtractor.INSTANCE.getText(url);
		} catch (BoilerpipeProcessingException e) {
			e.printStackTrace();
		}
		return text;
	}
	
	public static void main(String[] args){
		String urlText = "http://en.wikipedia.org/wiki/Andrew_Johnson";
		String text = "";
		try {
			text = extractText(new URL(urlText));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		System.out.println(text);
	}
}
