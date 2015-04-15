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
	public static enum PhoneType {
		AA, AE, AH, AO, AW, AY, B, CH, D, DH, EH, ER, EY, F, G, HH, IH, IY, JH, K, L, M,
		N, NG, OW, OY, P, R, S, SH, T, TH, UH, UW, V, W, Y, Z, ZH
	};

	private PhoneType phone;
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
			this.phone = PhoneType.valueOf(phoneString);
		}
		else {
			this.phone = PhoneType.valueOf(phoneString.substring(0, phoneString.length()-1));
			this.stress = Integer.parseInt(phoneString.charAt(phoneString.length()-1)+"");
		}

		this.isSyllable = Rhymer.isSyllable(phone);
	}

	/**
	 * @return the phone
	 */
	public PhoneType getPhoneType() {
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
