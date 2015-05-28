package rhymer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import rhymer.extract.SentenceExtractor;
import rhymer.lang.Phone;
import rhymer.lang.Phone.Sound;
import rhymer.lang.Rhyme;
import rhymer.lang.Sentence;
import rhymer.lang.Word;

public class Rhymer {

	private final Map<String, Word> dictionary;
	public static Map<Sound, String> phoneTypes;

	private static final String DICT_PATH = "dict" + File.separator;

	public Rhymer(){
		readPhoneSet();
		dictionary = readDictionary(new String[]{
				"cmudict" + File.separator + "cmudict-0.7b.txt",
				"will" + File.separator + "willdict.txt"
		});
	}

	public Set<Rhyme> extractRhymes(String rawContent){
		Set<Sentence> sentences = SentenceExtractor.extractSentences(rawContent, dictionary);
		Set<Rhyme> rhymes = findRhymingSentences(sentences);
		return rhymes;
	}

	/**
	 * Reads words from a dictionary file into word objects
	 * REQUIRES: phoneSet to be populated
	 * @param dictFilenames the filenames of dictionary files. These must be in the cmudict format
	 * @return a mapping of string to word objects (a dictionary)
	 */
	public static Map<String, Word> readDictionary(String[] dictFilenames) {
		long startTime = System.currentTimeMillis();
		Map<String, Word> dict = new HashMap<>();

		for (String dictFilename : dictFilenames){
			try {
				BufferedReader reader = new BufferedReader(new FileReader(DICT_PATH + dictFilename));
				// skip comments
				while (reader.readLine().startsWith(";;;"));
				// start reading words
				String line;
				while ((line = reader.readLine()) != null){
					String[] parts = line.split("  "); // two spaces
					String strWord = parts[0]; // two spaces
					String[] phoneStrings = parts[1].split(" ");
					// parse phones
					Phone[] phones = new Phone[phoneStrings.length];
					for (int i = 0 ; i < phones.length; i++){
						phones[i] = new Phone(phoneStrings[i]);
					}
					dict.put(strWord, new Word(strWord, phones));
				}
				reader.close();

			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		System.out.println("time taken to read dictionary: " + ((System.currentTimeMillis() - startTime)/1000.0));

		return dict;
	}

	public static void readPhoneSet(){
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(DICT_PATH + "cmudict" + File.separator + "cmudict-0.7b.phones"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		Map<Sound, String> phoneTypes = new HashMap<>();

		String line;
		try {
			while ((line = reader.readLine()) != null){
				String[] parts = line.split("\t");
				String phone = parts[0];
				String type = parts[1];

				phoneTypes.put(Sound.valueOf(phone), type);
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
		Rhymer.phoneTypes = phoneTypes;
	}

	/**
	 * @param sound
	 * @return if the sound is a syllable nucleus
	 */
	public static boolean isSyllableNuc(Sound sound) {
		return phoneTypes.get(sound).equals("vowel");
	}

	private Set<Rhyme> findRhymingSentences(Set<Sentence> sentences) {
		System.out.println("Finding rhyming sentences (" + sentences.size() + " sentences)");
		long startTime = System.currentTimeMillis();
		Set<Rhyme> rhyming = new HashSet<>();
		int i = 0; 
		long totalTime = 0; 

		for (Sentence s1 : sentences){
			//Set<Sentence> rhymesWith = sentences.getValuesBelow(s1.getSyllablePhonesReversed());
			for (Sentence s2 : sentences){
				long startCompTime = System.nanoTime();
				if (s1.equals(s2)) continue; // skip itself
				int rhymeScore = s1.perfectRhymeScore(s2);

				if (rhymeScore > 0){
					Set<Sentence> rhymes = new HashSet<>();
					rhymes.add(s1);
					rhymes.add(s2);
					Rhyme rhyme = new Rhyme(rhymes, rhymeScore);
					rhyming.add(rhyme);
				}
				long compTime = System.nanoTime() - startCompTime;
				totalTime += compTime;
				i++;
			}
		}
		long timeTaken = System.currentTimeMillis() - startTime;
		System.out.println("Found " + rhyming.size() + " rhyming sentences in " + timeTaken + "ms");
		//System.out.println("Average comp time: " + ((double)totalTime/i));
		return rhyming;
	}

	/**
	 * Prints out rhyming sentences. Prints 0 - TO_SHOW rhymes
	 * @param rhymes
	 */
	public void printRhymes(Set<Rhyme> rhymes, int numToShow) {

		if (rhymes.size() == 0){
			System.out.println("No rhymes found!");
			return;
		}

		// sort rhymes by their score
		List<Rhyme> sortedRhymes = new ArrayList<>(rhymes);
		Collections.sort(sortedRhymes, new Comparator<Rhyme>() {
			public int compare(Rhyme r1, Rhyme r2){
				return Integer.compare(r1.getScore(), r2.getScore());
			}
		});
		Collections.reverse(sortedRhymes);

		PrintWriter printer = null;
		try {
			printer = new PrintWriter(new FileWriter("rhymes.txt"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		int maxRhymes = Math.min(numToShow, sortedRhymes.size());

		for (int i = 0; i < maxRhymes; i++){
			Rhyme r = sortedRhymes.get(i);
			printer.println("-------------------");
			for (Sentence s : r){
				printer.println(s.toString());
				System.out.println(s.toString());
			}
			printer.println("Score: " + r.getScore());
			printer.println("-------------------");
		}
		System.out.println("Wrote top " + maxRhymes + " rhyming sentences to file");
	}

	public static boolean isConstonant(Sound sound) {
		return !phoneTypes.get(sound).equals("vowel");
	}

	public Map<String, Word> getDictionary() {
		return dictionary;
	}
}
