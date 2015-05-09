package rhymer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import rhymer.lang.Phone;
import rhymer.lang.Phone.Sound;
import rhymer.lang.Rhyme;
import rhymer.lang.Sentence;
import rhymer.lang.Trie;
import rhymer.lang.Word;

public class Rhymer {

	private Map<String, Word> dictionary;
	public static Map<Sound, String> phoneTypes;

	private static final String DICT_PATH = "dict" + File.separator;

	public Rhymer(String rawContent){
		phoneTypes = readPhoneSet();
		dictionary = readDictionary(new String[]{
				"cmudict" + File.separator + "cmudict-0.7b.txt", 
				"will" + File.separator + "willdict.txt"
		});

		Set<Sentence> sentences = extractSentences(rawContent);

		Set<Rhyme> rhymes = findRhymingSentences(sentences);
		
//		System.out.println("Trie size " + sentences.size());
//		System.out.println("root node children: " + sentences.rootNodeChildren());
//		System.out.println(sentences);

		printRhymes(rhymes, 50);
	}

	/**
	 * Reads words from a dictionary file into word objects
	 * @param dictFilenames the filenames of dictionary files. These must be in the cmudict format
	 * @return a mapping of string to word objects (a dictionary)
	 */
	private Map<String, Word> readDictionary(String[] dictFilenames) {
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

	public static Map<Sound, String> readPhoneSet(){
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
		return phoneTypes;
	}

	private Set<Sentence> extractSentences(String content) {
		// split sentences
		String[] strSentences = content.split("\\.|\\\n");
		//Trie sentences = new Trie();
		Set<Sentence> sentences = new HashSet<>();

		for (String strSentence : strSentences){
			//System.out.println(strSentence);
			Sentence sentence = extractWords(strSentence);
			
			if (keepSentence(sentence)){
				//sentences.put(sentence.getSyllablePhonesReversed(), sentence);
				sentences.add(sentence);
			}
		}
		
		System.out.println("Kept " + sentences.size() + "/" + strSentences.length + " sentences");
		return sentences;
	}

	/**
	 * Ensures that useless sentences arent added to the collection of sentences 
	 * @param words
	 * @return true if the words should be saved in a sentence
	 */
	private boolean keepSentence(Sentence sentence) {
		if (sentence.getNumSyllables() > 1
			&& sentence.getScore() > 0.6)
			return true;
		return false;
	}

	private Sentence extractWords(String sentence) {
		Scanner scan = new Scanner(sentence);
		List<Word> words = new ArrayList<>();
		int unknownWords = 0;
		while (scan.hasNext()){

			String strWord = scan.next();
			String cleanedWord = cleanWord(strWord);
			if (wordInDict(cleanedWord)){
				words.add(dictionary.get(cleanedWord));
			}
			else if (isAcronym(strWord)){
				Word acro = makeAcronymWord(strWord);
				if (acro != null){
					words.add(acro);
				} else
					unknownWords ++;
			}
			else if (isHyphenated(strWord)){
				Word hyphenated = makeHyphenatedWord(strWord);
				if (hyphenated != null) 
					words.add(hyphenated);
				else unknownWords ++;
			}
			else {
				unknownWords++;
			}
		}
		scan.close();
		double score = words.size() != 0 ? 1 - (unknownWords / (double)words.size()) : 0;

		return new Sentence(words.toArray(new Word[0]), score);
	}
	
	private boolean wordInDict(String word){
		return dictionary.containsKey(word);
	}

	private boolean isAcronym(String word) {
		if ((StringUtils.isAllUpperCase(word)
			|| (word.endsWith("'s") && StringUtils.isAllUpperCase(word.substring(0, word.length()-2))))
			&& word.length() <= 5)
			return true;
		return false;
	}


	private boolean isHyphenated(String word){
		if (word.contains("-")
				&& !word.startsWith("-")
				&& !word.endsWith("-"))
			return true;
		return false;
	}

	/**
	 * Precondition: acro must be an acronym
	 * @param acro
	 * @return a word made out of  letters of {@paramref acro}
	 */
	private Word makeAcronymWord(String acro){
		acro = cleanWord(acro);
		// split acronym into words for each letter (and for 's if needed)
		List<String> parts = new ArrayList<>();
		int letters = acro.endsWith("'s") ? acro.length() - 2 : acro.length();
		for (int i = 0; i < letters; i++){
			parts.add(acro.charAt(i)+"");
		}
		// add the 's as one word
		if (acro.endsWith("'s"))
			parts.add("'s".toUpperCase());
		
		List<Phone> phones = new ArrayList<>();
		
		for (String part : parts){
			Word letter = dictionary.get(part);
			try{
				phones.addAll(Arrays.asList(letter.getPhones()));
			} catch (NullPointerException e){
				//System.err.println("weird character in: " + acro + " parts size " + parts.size());
				return null; // bad acroynm
			}
		}
		
		return new Word(acro, phones.toArray(new Phone[0]));
	}
	
	/**
	 * Make a word from two words which are hyphenated together
	 * @param hypenated
	 * @return a word if the two split words are recognised. Otherwise return null
	 */
	private Word makeHyphenatedWord(String hypenated){
		// split around hyphen
		String[] split = hypenated.split("\\-");
		
		List<Phone> phones = new ArrayList<>();
		
		for (String s : split){
			if (wordInDict(s))
				phones.addAll(Arrays.asList(dictionary.get(s).getPhones()));
			else return null;
		}
		
		return new Word(hypenated.toUpperCase(), phones.toArray(new Phone[0]));
	}

	/**
	 * Cleaning: remove all trailing WS and all punctuation
	 * and then capitilise (so it can match words in the CMU dictionary)
	 * @param word
	 * @return cleaned word
	 */
	private String cleanWord(String word) {
//		System.out.println("dirty : " + word);
		word = word.trim();

		// could use regex
		String[] dirtyChars = new String[]{ "\"", ".", ",", ";", ":", "-",
				"[", "]", "(", ")", "{", "}", "!", "?" };

		for (String dirtyChar : dirtyChars){
			word = word.replace(dirtyChar, ""); // using charsequence args (so not regex)
		}
		
		// fixes weird bug
		if (word.length() == 1 && !Character.isAlphabetic(word.charAt(0)))
			return "";
		
		word = word.toUpperCase();
//		System.out.println("clean : " + word);
		return word;
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
	 * @param rhyming
	 */
	private void printRhymes(Set<Rhyme> rhyming, int numToShow) {
		
		if (rhyming.size() == 0){
			System.out.println("No rhymes found!");
			return;
		}
		
		// sort rhymes by their score
		List<Rhyme> sortedRhymes = new ArrayList<>(rhyming);
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
			Rhyme rhymes = sortedRhymes.get(i);
			printer.println("-------------------");
			for (Sentence s : rhymes){
				printer.println(s.toString());
				//System.out.println(s.toStringPhones());
			}
			printer.println("Score: " + rhymes.getScore());
			printer.println("-------------------");
		}
		System.out.println("Wrote top " + maxRhymes + " rhyming sentences to file");
	}

	public static boolean isConstonant(Sound sound) {
		return !phoneTypes.get(sound).equals("vowel");
	}
}
