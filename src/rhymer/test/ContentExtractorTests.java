package rhymer.test;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import rhymer.Rhymer;
import rhymer.extract.SentenceExtractor;
import rhymer.extract.WebExtractor;
import rhymer.lang.Sentence;
import rhymer.lang.Word;

public class ContentExtractorTests {
	
	private static final double SCORE_THRESHOLD = 0.7;
	public static String TEST_PATH = "test" + File.separator;
	private Map<String, Word> dictionary;

	@Before
	public void init(){
		Rhymer rhymer = new Rhymer();
		dictionary = rhymer.getDictionary();
	}
	
	@Test
	public void wiki1(){
		String extractedContent = getExtractedContent("http://en.wikipedia.org/wiki/GameCube");
		double score = compareContent(extractedContent, new File(TEST_PATH + "wiki_gamecube_content.txt"));
		
		if (score < SCORE_THRESHOLD)
			fail("Score too low");
	}

	@Test
	public void wiki2(){
		String extractedContent = getExtractedContent("http://en.wikipedia.org/wiki/Bengal_Tiger");
		double score = compareContent(extractedContent, new File(TEST_PATH + "wiki_bengal_tiger_content.txt"));
		
		if (score < SCORE_THRESHOLD)
			fail("Score too low");
	}
	
	@Test
	public void gnu(){
		String extractedContent = getExtractedContent("https://gnu.org/");
		double score = compareContent(extractedContent, new File(TEST_PATH + "gnu_content.txt"));
		
		if (score < SCORE_THRESHOLD)
			fail("Score too low");
	}	
	
	@Test
	public void stuff(){
		String extractedContent = getExtractedContent("http://www.stuff.co.nz/technology/digital-living/68383556/microsofts-long-road-to-being-cool-again");
		double score = compareContent(extractedContent, new File(TEST_PATH + "stuff_content.txt"));
		
		if (score < SCORE_THRESHOLD)
			fail("Score too low");
	}
	
	//@Test
	public void guardian(){
		String extractedContent = getExtractedContent("http://www.theguardian.com/politics/2015/may/10/miliband-made-terrible-mistake-in-ditching-new-labour-says-mandelson");
		double score = compareContent(extractedContent, new File(TEST_PATH + "guardian_content.txt"));
		
		if (score < SCORE_THRESHOLD)
			fail("Score too low");
	}
	
	//@Test
	public void rt(){
		String extractedContent = getExtractedContent("http://rt.com/news/257237-yemen-truce-saudi-arabia/");
		double score = compareContent(extractedContent, new File(TEST_PATH + "rt_content.txt"));
		
		if (score < SCORE_THRESHOLD)
			fail("Score too low");
	}
	
	@Test
	public void bbc(){
		String extractedContent = getExtractedContent("http://www.bbc.com/news/election-2015-scotland-32680698");
		double score = compareContent(extractedContent, new File(TEST_PATH + "bbc_content.txt"));
		
		if (score < SCORE_THRESHOLD)
			fail("Score too low");
	}
	

	private String getExtractedContent(String urlString){
		URL url = null;
		try {
			url = new URL(urlString);
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return WebExtractor.extractTextFromWebPage(url);
	}

	private double compareContent(String extracted, File testFile) {
		Scanner scan = null;
		try {
			scan = new Scanner(testFile);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// read file into a string
		String actualContentStr = "";
		while (scan.hasNext()){
			actualContentStr += scan.nextLine() +"\n";
		}
		scan.close();
		
		double recall = findRecall(extracted, actualContentStr);
		double precision = findPrecision(extracted, actualContentStr);
		double score = (recall + precision) / 2;
		
		System.out.println("Testing " + testFile.getName());
		System.out.println("Recall: " + recall);
		System.out.println("Precision: " + precision);
		System.out.println("Score: " + score);
		System.out.println();
		
		
		return score;
	}
	
	/**
	 * How much of the actual content is in the extracted content (recall)
	 * @param extractedText
	 * @param actualText
	 * @return recall
	 */
	private double findRecall(String extractedText, String actualText){
		Set<Sentence> actual = SentenceExtractor.extractSentences(actualText, dictionary);
		Set<Sentence> extracted = SentenceExtractor.extractSentences(extractedText, dictionary);

		// what proportion of the test sentences are in the extracted content
		int found = 0;
		for (Sentence actualSentence : actual){
			if (extracted.contains(actualSentence))
				found++;
		}

		return (double)found/actual.size();
	}
	
	/**
	 * How much of the extracted content is actual content (precision)
	 * @param extracted
	 * @param actual
	 * @return precision
	 */
	private double findPrecision(String extractedText, String actualText){
		Set<Sentence> actual = SentenceExtractor.extractSentences(actualText, dictionary);
		Set<Sentence> extracted = SentenceExtractor.extractSentences(extractedText, dictionary);
		
		int correct = 0;
		for (Sentence sentence : extracted){
			if (actual.contains(sentence))
				correct++;
		}
		
		return (double)correct/extracted.size();
	}
}
