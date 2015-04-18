package rhymer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
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
import rhymer.lang.Word;

public class Rhymer {

	private Map<String, Word> dictionary;
	public static Map<Sound, String> phoneType;

	private static final String DICT_PATH = "dict" + File.separator;

	public Rhymer(String rawContent){
		phoneType = readPhoneSyllables();
		dictionary = readDictionaryWords(new String[]{"cmudict" + File.separator + "cmudict-0.7b.txt", "willdict.txt"});

		List<Sentence> sentences = extractSentences(rawContent);

		Set<Rhyme> rhymes = findRhymingSentences(sentences);

		printRhymes(rhymes, 50);
	}

	/**
	 * reads words from a dictionary file into word objects
	 * @param dictFilenames the filenames of dictionary files. These must be in the cmudict format (word, 2 spaces,
	 * @return a mapping of string to word objects (a dictionary)
	 */
	private Map<String, Word> readDictionaryWords(String[] dictFilenames) {
		long start = System.currentTimeMillis();
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

		System.out.println("time taken to read dictionary: " + ((System.currentTimeMillis() - start)/1000.0));

		return dict;
	}

	private Map<Sound, String> readPhoneSyllables(){
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

	private List<Sentence> extractSentences(String content) {
		// split sentences
		String[] strSentences = content.split("\\.|\\\n");
		List<Sentence> sentences = new ArrayList<>();

		for (String strSentence : strSentences){
			//System.out.println(strSentence);
			List<Word> words = extractWords(strSentence);
			if (keepSentence(words))
				sentences.add(new Sentence(words.toArray(new Word[0])));
		}
		return sentences;
	}

	/**
	 * Ensures that useless sentences arent added to the collection of sentences 
	 * @param words
	 * @return true if the words should be saved in a sentence
	 */
	private boolean keepSentence(List<Word> words) {
		int syllables = 0;
		for (Word w : words){
			syllables += w.getNumSyllables();
		}
		if (syllables < 1)
			return false;
		return true;
	}

	// TODO: make more accurate, allow acronyms and abbreviations etc.
	private List<Word> extractWords(String sentence) {
		Scanner scan = new Scanner(sentence);
		List<Word> words = new ArrayList<>();
		while (scan.hasNext()){

			String strWord = scan.next();
			if (shouldSplitWord(strWord)){
				String[] splitWords = splitWord(strWord);
				for (String splitWord : splitWords){
					cleanAndAddWord(splitWord, words);
				}
			}
			else {
				cleanAndAddWord(strWord, words);
			}
		}
		scan.close();

		return words;
	}

	/**
	 * Cleans a word and then adds it to the words list
	 * @param strWord uncleaned word
	 * @param words
	 */
	private void cleanAndAddWord(String strWord, List<Word> words) {
		String cleaned = cleanWord(strWord);
		Word word = dictionary.get(cleaned);
		// if word is found in the dict
		if (word != null){
			word.setContextWord(strWord);
			words.add(word);
		}
		else {
			// word wasn't found
			System.out.println("unknown word: " + strWord);
		}
	}


	private boolean shouldSplitWord(String word){
		if (isHyphenated(word))
			return true;
		if (isAcronym(word))
			return true;

		return false;
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

	private String[] splitWord(String word){
		// split around hyphen
		if (isHyphenated(word))
			return word.split("\\-");

		// split acronym into words for each letter
		else if (isAcronym(word)){
			char[] charArray = word.toCharArray();
			String[] letters = new String[word.length()];

			if (word.endsWith("'s")){
				letters = new String[word.length()-1];
				for (int i = 0; i < word.length()-2; i++){
					letters[i] = charArray[i] + "";
				}
				// add the 's as one word
				letters[letters.length-1] = "'s";
			}
			else {
				for (int i = 0; i < word.length(); i++){
					letters[i] = charArray[i] + "";
				}
			}

			return letters;
		}
		else throw new IllegalArgumentException();
	}

	/**
	 * Cleaning: remove all trailing WS, stray punctuation
	 * and then capitilise (so it can match words in the CMU dictionary)
	 * @param word
	 * @return cleaned word
	 */
	private String cleanWord(String word) {
		//System.out.println("dirty word:\t\t" + word);
		word = word.trim();

		String[] dirtyChars = new String[]{ "\"", ".", ",", ";", ":", "-",
				"[", "]", "(", ")", "{", "}", "!", };

		for (String dirtyChar : dirtyChars){
			word = word.replace(dirtyChar, ""); // using charsequence args (so not regex)
		}

		word = word.toUpperCase();
		//System.out.println("clean word:\t\t" + word);
		return word;
	}

	/**
	 * @param sound
	 * @return if the sound is a syllable nucleus
	 */
	public static boolean isSyllableNuc(Sound sound) {
		return phoneType.get(sound).equals("vowel");
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

		int maxRhymes = Math.min(numToShow, sortedRhymes.size());

		for (int i = 0; i < maxRhymes; i++){
			Rhyme rhymes = sortedRhymes.get(i);
			System.out.println("-------------------");
			for (Sentence s : rhymes){
				System.out.println(s.toString());
				//System.out.println(s.toStringPhones());
			}
			System.out.println("Score: " + rhymes.getScore());
			System.out.println("-------------------");
		}
	}

	private Set<Rhyme> findRhymingSentences(List<Sentence> sentences) {
		Set<Rhyme> rhyming = new HashSet<>();

		for (Sentence s1 : sentences){
			for (Sentence s2 : sentences){
				if (s1.equals(s2)) continue; // skip itself
				int rhymeScore = s1.perfectRhymeScore(s2);

				if (rhymeScore > 0){
					Set<Sentence> rhymes = new HashSet<>();
					rhymes.add(s1);
					rhymes.add(s2);
					Rhyme rhyme = new Rhyme(rhymes, rhymeScore);
					rhyming.add(rhyme);
				}
			}
		}
		return rhyming;
	}

	public static boolean isConstonant(Sound sound) {
		return !phoneType.get(sound).equals("vowel");
	}
}
