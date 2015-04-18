package rhymer.lang;

import rhymer.Rhymer;

/**
 * @author will
 * A small part of a word in speech
 */
public class Phone {

	/**
	 * @author will
	 * all possible phones
	 */
	public static enum Sound {
		AA, AE, AH, AO, AW, AY, B, CH, D, DH, EH, ER, EY, F, G, HH, IH, IY, JH, K, L, M,
		N, NG, OW, OY, P, R, S, SH, T, TH, UH, UW, V, W, Y, Z, ZH
	};

	private Sound phone;
	/**
	 * how much vocal emphasis to place on this phone
	 */
	private int stress;
	/**
	 * can this phone be a whole syllable
	 */
	private boolean isSyllable;

	/**
	 * Parses phoneStrings from the CMU dictionary format
	 * @param phoneString
	 */
	public Phone(String phoneString){
		if (!Character.isDigit(phoneString.charAt(phoneString.length()-1))){
			this.phone = Sound.valueOf(phoneString);
		}
		else {
			this.phone = Sound.valueOf(phoneString.substring(0, phoneString.length()-1));
			this.stress = Integer.parseInt(phoneString.charAt(phoneString.length()-1)+"");
		}

		this.isSyllable = Rhymer.isSyllableNuc(phone);
	}

	/**
	 * @return the sound
	 */
	public Sound getSound() {
		return phone;
	}

	/**
	 * @return the stress
	 */
	public int getStress() {
		return stress;
	}

	public boolean isSyllable(){
		return this.isSyllable;
	}

	public String toString(){
		return phone.toString();
	}

	public String toStringStress(){
		return phone.toString() + stress;
	}
}
