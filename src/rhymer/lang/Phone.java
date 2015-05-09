package rhymer.lang;

import java.util.Arrays;

import rhymer.Rhymer;

/**
 * @author will
 * A small part of a word in speech
 */
public class Phone {

	/**
	 * @author will
	 * all possible phone sounds
	 */
	public static enum Sound {
		AA, AE, AH, AO, AW, AY, B, CH, D, DH, EH, ER, EY, F, G, HH, IH, IY, JH, K, L, M,
		N, NG, OW, OY, P, R, S, SH, T, TH, UH, UW, V, W, Y, Z, ZH
	};

	private Sound sound;
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
			this.sound = Sound.valueOf(phoneString);
		}
		else {
			this.sound = Sound.valueOf(phoneString.substring(0, phoneString.length()-1));
			this.stress = Integer.parseInt(phoneString.charAt(phoneString.length()-1)+"");
		}

		this.isSyllable = Rhymer.isSyllableNuc(sound);
	}

	/**
	 * @return the sound
	 */
	public Sound getSound() {
		return sound;
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
		return sound.toString();
	}

	public String toStringStress(){
		return sound.toString() + stress;
	}
	
	@Override
	public boolean equals(Object other){
		if (!(other instanceof Phone))
			return false;
		Phone o = (Phone)other;
		
		return sound == o.getSound() && stress == o.getStress();
	}
	
	@Override
	public int hashCode(){
		return Arrays.asList(Sound.values()).indexOf(sound) * (stress == 0 ? 1 : stress);
	}
}
