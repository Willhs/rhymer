package rhymer.lang;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import rhymer.Rhymer;
import rhymer.lang.Phone.Sound;


public class Word {

	private final String word;
	private final Phone[] phones;
	private final int numSyllables;
	private String contextWord; // the word as seen in the text (not the plain word on it's own)

	public Word(String word, Phone[] phones){
		this.word = word;
		this.phones = phones;

		int totalSyllables = 0;
		for (Phone phone : phones){
			if (phone.isSyllable()){
				totalSyllables++;
			}
		}
		this.numSyllables = totalSyllables;
	}

	/**
	 * @return the word
	 */
	public String getWord() {
		return word;
	}

	/**
	 * @return the phones
	 */
	public Phone[] getPhones() {
		return phones;
	}

	public int getNumSyllables() {
		return numSyllables;
	}

	public Phone[] getSyllablePhones() {
		List<Phone> syllables = new ArrayList<>();

		for (Phone phone : phones){
			if (phone.isSyllable())
				syllables.add(phone);
		}
		return syllables.toArray(new Phone[0]);
	}

	public void setContextWord(String contextWord){
		this.contextWord = contextWord;
	}

	public String getContextWord(){
		return contextWord;
	}

	/**
	 * @param other must be same number of syllables as this word 
	 * @return if not valid perfect rhyme: 0, else the number of rhyming syllables 
	 */
	public int rhymeScoreWith(Word other){
		
		// if violate precondition
		if (this.numSyllables != other.numSyllables){
			System.err.println("different num of syllables: " + Arrays.toString(this.getPhones()) + ", " + Arrays.toString(other.getPhones()));
			throw new IllegalArgumentException();
		}
		// if the start articulations are the same
		if (!this.diffStartArticulation(other)){
			//System.err.println("same start articulation\n" + this.toStringPhones() + "\n" + other.toStringPhones());
			return 0;
		}
		
		// else 
		int numSyllables = numPerfectRhymingSyllables(other);
		
				// if the primary stresses are different 
		if (!this.samePrimaryStresses(other, numSyllables)){
		//	System.err.println("different primary stress\n" + this.toStringPhonesStress() + "\n" + other.toStringPhonesStress());
			return 0;			
		}
		
		return numSyllables;
	}
	
	/**
	 * Splits a word after a syllable
	 * @param splitAfter how many syllables before the split
	 * @return two sub words of this word on either side of the split
	 */
	public Word[] splitWord(int splitAfter){
		if (splitAfter > this.getSyllablePhones().length)
			throw new IllegalArgumentException();
		
		int syllable = 0;
		int stopIndex = -1;
		for (int i = 0; i < phones.length; i++){
			Phone p = phones[i];

			if (Rhymer.isSyllableNuc(p.getSound()))
				syllable++;
							
			if (syllable == splitAfter){
				stopIndex = i;
				break;
			}
		}
		int phoneSplit = stopIndex + 1; // so first word will include vowel
		
		// if there's another 2 constonants after the vowel, add the first one to the first word
		// e.g. split sublime into sub lime rather than su blime 
		if (stopIndex < this.getPhones().length-2){
			if (!Rhymer.isSyllableNuc(phones[stopIndex+1].getSound())
			 && !Rhymer.isSyllableNuc(phones[stopIndex+2].getSound())){
				phoneSplit ++; // so first word includes first constonant
			}
		}
		
		Word[] splitWords = new Word[]{ new Word(word + " *front*", Arrays.copyOfRange(phones, 0, phoneSplit)),
							new Word(word + " *end*", Arrays.copyOfRange(phones, phoneSplit, phones.length))
		};
		
//		System.out.println("--- split ---");
//		System.out.println(splitAfter);
//		for (Word w : splitWords){
//			System.out.println(w.toStringPhones());
//		}
//		System.out.println("-------------");
		return splitWords;
	}
	
	/**
	 * @param other word with same number of syllables
	 * @return number of syllables which rhyme while both words have
	 * same phones
	 */
	private int numPerfectRhymingSyllables(Word other){
		List<Phone> w1Phones = Arrays.asList(this.getPhones().clone());
		List<Phone> w2Phones = Arrays.asList(other.getPhones().clone());
		
		Collections.reverse(w1Phones);
		Collections.reverse(w2Phones);
		
		int syllableCount = 0;
		
		for (int p = 0; p < Math.min(w1Phones.size(), w2Phones.size()); p++){
			Sound s1 = w1Phones.get(p).getSound();
			Sound s2 = w2Phones.get(p).getSound();
			
			// phones must have same sound and have the same stress
			if (s1 != s2){
					break; // perfect rhyme is over
			}
			if (Rhymer.isSyllableNuc(s1))
				syllableCount ++;
		}
		return syllableCount;
	}
	
	/**
	 * @param other
	 * @return whether this and other have different beginning articulations
	 */
	private boolean diffStartArticulation(Word other){
		// check start articulations
		for (int i = 0; i < Math.min(this.getPhones().length, other.getPhones().length); i++){
			Sound s1 = this.getPhones()[i].getSound();
			Sound s2 = other.getPhones()[i].getSound();
			
			if (this.isFirstSyllableNucleus(s1) &&
					other.isFirstSyllableNucleus(s2))
				return false; // the start articulations are the same
			
			if (s1 != s2)
				return true;
		}
		return true;
	}

	/**
	 * @param other
	 * @return true if both words have the same primary stresses
	 */
	public boolean samePrimaryStresses(Word other, int rhymingSyllables){
   		for (int i = 0; i < rhymingSyllables; i++){
			Phone p1 = this.getSyllablePhones()[i]; 
			Phone p2 = other.getSyllablePhones()[i];
			
			if ((p1.getStress() == 1 && p2.getStress() != 1)
			 || (p2.getStress() == 1 && p1.getStress() != 1))
				return false;
		}
		return true;
	}
	
	/**
	 * @param sound
	 * @return if the first syllable nucleus is equal to sound
	 */
	public boolean isFirstSyllableNucleus(Sound sound){
		return this.getSyllablePhones()[0].getSound() == sound; 
	}

	@Override
	public String toString(){
		return word;
	}
	public String toStringContextWord(){
		return contextWord;
	}
	public String toStringPhones(){
		String phoneString = "";
		for (Phone phone : phones){
			phoneString += phone;
		}
		return phoneString;
	}
	public String toStringPhonesStress(){
		String phoneString = "";
		for (Phone phone : phones){
			phoneString += phone.toStringStress();
		}
		return phoneString;
	}
	
	@Override
	public boolean equals(Object o){
		if (!(o instanceof Word))
			return false;
		
		return this.word.equals(((Word)o).getWord());
	}
}
