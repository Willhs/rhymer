package rhymer.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import rhymer.Rhymer;

public class Sentence {

	private final Word[] words;
	private final int numSyllables;
	/**
	 * A fitness value based on how few words had to be removed because they were unknown
	 */
	private final double score;

	/**
	 * @param words gotta be non-null
	 */
	public Sentence(Word[] words, double score) {
		this.words = words;
		this.score = score;

		int totalSyllables = 0;
		for (Word w : words){
			totalSyllables += w.getNumSyllables();
		}
		this.numSyllables = totalSyllables;
	}

	/**
	 * @return the sentence
	 */
	public Word[] getWords() {
		return words;
	}

	/**
	 * @return the syllables
	 */
	public int getNumSyllables() {
		return numSyllables;
	}

	public double getScore(){
		return score;
	}

	public String toString(){
		String sentence = "";
		for (Word w : words){
			sentence += w + " ";
		}
		return sentence;
	}

	public String toStringPhones(){
		String sentence = "";
		for (Word word : words){
			sentence += word.toStringPhones() + " ";
		}
		return sentence;
	}

	public String toStringPhonesStress(){
		String sentence = "";
		for (Word word : words){
			sentence += word.toStringPhonesStress() + " ";
		}
		return sentence;
	}

	@Override
	public boolean equals(Object o){
		if (!(o instanceof Sentence))
			return false;

		return Arrays.equals(this.words, ((Sentence)o).getWords());
	}

	@Override
	public int hashCode(){
		return words.length * numSyllables;
	}


	/**
	 * checks if there is a tail rhyme with both sentences
	 * ==== CURRENTLY ONLY ACCEPTS PERFECT RHYMES =====
	 * TODO: accept other types of rhymes
	 * @param s2
	 * @return rhymescore (-1 if no rhymes)
	 */
	public int perfectRhymeScore(Sentence s2) {

		System.out.println("testing: \n" + this.toStringPhones() + "\n" + s2.toStringPhones());

		// put both sentences in an array (to reuse code)
		// could be extended to rhyme multiple sentences
		Sentence[] s = new Sentence[]{ this, s2 };
		int numSentences = s.length;

		// get all words from sentences
		Queue<Word>[] words = new Queue[2];
		for (int i = 0; i < numSentences; i++){
			List<Word> wordList = Arrays.asList(s[i].getWords().clone());
			Collections.reverse(wordList);
			words[i] = new LinkedList<>(wordList);
		}

		Word[] currentWords = new Word[numSentences];

		int rhymingSyllables = 0;
		boolean findingRhyme = true;

		// go until out of words or rhyming has ended
		rhymeFinder: while (findingRhyme){

			// add next word to be compared
			for (int i = 0; i < numSentences; i++){
				if (currentWords[i] == null){
					if (words[i].isEmpty())
						break rhymeFinder;
					currentWords[i] = words[i].poll();
				}
			}
			// get min syllable count of each word to rhyme
			int minSyllables = Integer.MAX_VALUE;
			for (int i = 0; i < numSentences; i++){
				int syllables = currentWords[i].getNumSyllables();
				if (syllables < minSyllables)
					minSyllables = syllables;
			}
			// split words if they have more than minSyllables syllables
			Word[] toRhyme = new Word[numSentences];
			for (int i = 0; i < numSentences; i++){
				Word word = currentWords[i];
				// need to split?
				if (word.getNumSyllables() > minSyllables){
					Word[] subWords = word.splitWord(word.getNumSyllables() - minSyllables);
					currentWords[i] = subWords[0];
					toRhyme[i] = subWords[1];
				}
				else {
					currentWords[i] = null;
					toRhyme[i] = word;
				}
			}

			// ASSUMES THERE ARE ONLY 2 SENTENCES
			int wordRhymingSyllables = toRhyme[0].rhymeScoreWith(toRhyme[1]);
			rhymingSyllables += wordRhymingSyllables;

			if (wordRhymingSyllables == 0)
				findingRhyme = false;
		}

//		System.out.println("==============================");
//		System.out.println(words[0]);
//		System.out.println(words[1]);
//		System.out.println("==============================");

		int score = rhymingSyllables;

		return score;
	}

	public List<Phone> getSyllablePhones() {
		List<Phone> syllablePhones = new ArrayList<>();
		for (Word w : words){
			syllablePhones.addAll(Arrays.asList(w.getSyllablePhones()));
		}
		return syllablePhones;
	}
	public List<Phone> getSyllablePhonesReversed() {
		List<Phone> syllablePhones = new ArrayList<>();
		for (Word w : words){
			syllablePhones.addAll(Arrays.asList(w.getSyllablePhones().clone()));
		}
		Collections.reverse(syllablePhones);
		return syllablePhones;
	}

	/*
	public int numRhymingSyllables(Sentence s2) {
		// get all syllables from sentences
		List<Phone> phones1 = this.getAllPhones();
		List<Phone> phones2 = s2.getAllPhones();

		Collections.reverse(phones1);
		Collections.reverse(phones2);

		// still determining rhyme
		boolean determiningRhyme = true;
		int syllableCount = 0; // how many syllables have passed

		int stressPos = -1; // which syllable is stressed (must be stressed on both the same syllable)

		for (int phone = 0; phone < Math.min(phones1.size(), phones2.size()) && determiningRhyme; phone++){
			Phone p1 = phones1.get(phone);
			Phone p2 = phones2.get(phone);

			if (p1.getPhoneType() != p2.getPhoneType()){
				determiningRhyme = false;
			}

			if (Rhymer.isSyllable(p1.getPhoneType()) && Rhymer.isSyllable(p2.getPhoneType()))
				syllableCount ++;

			if (p1.getStress() > 0 && p2.getStress() > 0)
				stressPos = syllableCount;
		}

		// perfect rhyme type
		String pRType = stressPos == 1 ? "single"
				: stressPos == 2 ? "double"
				: stressPos == 3 ? "dactylic"
				: "none";

		//if (pRType.equals("double") || pRType.equals("dactylic")){
		//	System.out.println("---- " + pRType + "----" + "\n" + this + "\n" + s2);
		//}

		int score = syllableCount;

		return score;
	}*/

}
