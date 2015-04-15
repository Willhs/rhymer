package rhymer;

import java.util.HashSet;
import java.util.Set;

public class RhymeSet<Sentence> extends HashSet<Sentence> implements Comparable{

	private int score;

	public RhymeSet(){
		super();
		score = 0;
	}

	public boolean addRhyming(Set<Sentence> sentences, int score){
		this.score = score;
		for(Sentence s : sentences){
			if (!super.add(s))
				return false;
		}
		return true;
	}

	public int getScore(){
		return score;
	}


	@SuppressWarnings("unchecked")
	@Override
	public int compareTo(Object o) {
		return Integer.compare(score, ((RhymeSet<Sentence>)o).getScore());
	}
}
