package rhymer.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import rhymer.Rhymer;
import rhymer.lang.Phone.PhoneType;

public class Sentence {

	private final Word[] words;
	private final int numSyllables;

	/**
	 * @param words gotta be non-null
	 */
	public Sentence(Word[] words) {
		this.words = words;

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

		return Arrays.equals(words, ((Sentence)o).getWords());
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
	 * @return
	 */
	public int tailRhymesWith(Sentence s2) {
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

		/*if (pRType.equals("double") || pRType.equals("dactylic")){
			System.out.println("---- " + pRType + "----" + "\n" + this + "\n" + s2);
		}*/

		int score = syllableCount;

		return score;
	}

	private List<Phone> getAllSyllables(){
		List<Phone> syllables = new ArrayList<>();
		for (Word word : words){
			syllables.addAll(Arrays.asList(word.getSyllablePhones()));
		}
		return syllables;
	}

	private List<Phone> getAllPhones(){
		List<Phone> syllables = new ArrayList<>();
		for (Word word : words){
			syllables.addAll(Arrays.asList(word.getPhones()));
		}
		return syllables;
	}

}
