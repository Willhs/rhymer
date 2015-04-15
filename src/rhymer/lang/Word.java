package rhymer.lang;

import java.util.ArrayList;
import java.util.List;


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
}
