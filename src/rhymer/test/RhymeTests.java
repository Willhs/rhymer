package rhymer.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import rhymer.Rhymer;
import rhymer.lang.Rhyme;
import rhymer.lang.Word;

public class RhymeTests {

	Map<String, Word> dict;

	@Before
	public void readDict(){
		Rhymer.readPhoneSet();
		dict = Rhymer.readDictionary(new String[]{"cmudict" + File.separator + "cmudict-0.7b.txt"});
	}

	/**
	 * Tests single syllable words pairs which rhyme in my (NZ) accent
	 */
	@Test
	public void singleSyllableTrueAccent(){
		String[][] matches = new String[][]{
				{"dog","frog"},
				{"bog", "frog"},
				{"hell","bell"},
				{"sprint", "mint"},
				{"cat","bat"}
		};

		int pass = 0;
		for (String[] match : matches){
			if (rhymes(match)){
				pass++;
			}
			else {
				printWords(match);
			}
		}

		assertEquals(matches.length, pass);
	}

	/**
	 * Tests single syllable word pairs which don't rhyme in my (NZ) accent
	 */
	@Test
	public void singleSyllableFalseAccent(){
		String[][] antiMatches = new String[][]{
				{"men","frog"},
				{"drew", "frog"},
				{"pope","bell"},
				{"john", "mint"},
				{"wet","bat"},
				{"you","you"},
				{"you", "me"}
		};

		int pass = 0;
		for (String[] match : antiMatches){
			if (!rhymes(match)){
				pass++;
			}
			else {
				printWords(match);
			}
		}

		assertEquals(antiMatches.length, pass);
	}

	@Test
	public void multiSyllableTrueAccent(){

	}


	private void printWords(String[] words){

		System.out.println("Should rhyme: ");

		for (String wordStr : words){
			Word w = dict.get(wordStr);
			if (w == null){
				System.out.println(wordStr + " is not a word");
			}
			else {
				System.out.println(w.getSyllablePhones());
			}
		}
	}

	private boolean rhymes(String[] strings){
		String s1 = strings[0];
		String s2 = strings[1];
		Rhymer rhymer = new Rhymer();
		Set<Rhyme> rhymes = rhymer.extractRhymes(s1 + "." + s2);

		return rhymes.size() == 1;
	}

}
