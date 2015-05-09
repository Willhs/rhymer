package rhymer.lang;

import java.util.HashSet;
import java.util.Set;

/**
 * @author hardwiwill
 *
 * Contains two or more rhyming sentences
 *
 * @param <Sentence>
 */
public class Rhyme extends HashSet<Sentence> {

	private int score;

	public Rhyme(Set<Sentence> sentences, int score){
		super();
		super.addAll(sentences);
		this.score = score;
	}

	public int getScore(){
		return score;
	}

}
