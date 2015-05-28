package rhymer.extract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import rhymer.lang.Phone;
import rhymer.lang.Sentence;
import rhymer.lang.Word;

public class SentenceExtractor {

	Map<String, Word> dictionary;
	
	public static String SPLIT_REGEX = "\\.|\\\n";

	private SentenceExtractor(Map<String, Word> dictionary){
		this.dictionary = dictionary;
	}

	public static Set<Sentence> extractSentences(String content, Map<String, Word> dictionary) {
		SentenceExtractor extractor = new SentenceExtractor(dictionary);
		return extractor.extract(content);
	}

	private Set<Sentence> extract(String content){
		// split sentences
		String[] strSentences = content.split(SPLIT_REGEX);
		//Trie sentences = new Trie();
		Set<Sentence> sentences = new HashSet<>();

		for (String strSentence : strSentences){
			Sentence sentence = extractWords(strSentence);

			if (keepSentence(sentence)){
				sentences.add(sentence);
				
			}
		}

		System.out.println("Kept " + sentences.size() + "/" + strSentences.length + " sentences");
		return sentences;
	}

	/**
	 * Ensures that useless sentences aren't added to the collection of sentences
	 * @param dict
	 * @return true if the words should be saved in a sentence
	 */
	private boolean keepSentence(Sentence sentence) {
		if (/*sentence.getNumSyllables() > 1
			&& */sentence.getScore() > 0.6)
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
	
	public static String[] splitIntoSentences(String string){
		return string.split(SPLIT_REGEX);
	}

}
