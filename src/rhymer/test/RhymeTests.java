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
	Rhymer rhymer; 

	@Before
	public void init(){
		rhymer = new Rhymer();
		dict = rhymer.getDictionary();
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
				{"cat","bat"},
				{"bear","lear"},
				{"beer","lear"},
		};
		testRhymes(matches);
	}
	
	/**
	 * Tests single syllable words pairs which rhyme in my (NZ) accent
	 */
	@Test
	public void singleSyllableTrue(){
		String[][] matches = new String[][]{
				{"bog", "frog"},
				{"hell","bell"},
				{"sprint", "mint"},
				{"cat","bat"},
				{"free", "glee"},
		};
		testRhymes(matches);
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
				{"tree", "the"}
		};
		testAntiRhymes(antiMatches);
	}
	
	/**
	 * Tests single syllable word pairs which don't rhyme in my (NZ) accent
	 */
	@Test
	public void singleSyllableFalse(){
		String[][] antiMatches = new String[][]{
				{"men","frog"},
				{"drew", "frog"},
				{"pope","bell"},
				{"john", "mint"},
				{"wet","bat"},
				{"you","you"},
				{"you", "me"}
		};
		testAntiRhymes(antiMatches);
	}
	
	/**
	 * Multi-syllable words which rhyme
	 */
	@Test
	public void multiSyllableWordTrue(){
		String[][] matches = new String[][]{
				{"FLOCKED","SHOCKED"},
				{"later", "greater"},
				{"highly","shyly"},
				{"sublime", "rhyme"},
				{"writer","biter"},
				{"nightly","slightly"},
				{"monkey","chunky"},
				{"gravel","travel"},
		};
		testRhymes(matches);
	}

	/**
	 * Multi-syllable words which don't rhyme
	 */
	@Test
	public void multiSyllableWordFalse(){
		String[][] antiMatches = new String[][]{
				{"menopause","frog"},
				{"andrew", "frog"},
				{"pope","menu"},
				{"tragedy", "can"},
				{"pottery","tree"},
				{"universe","you"},
				{"human", "meal"},
				{"happy", "super"},
				{"lolly", "lolly"},
				{"jewelery", "jewelery"},
		};
		testAntiRhymes(antiMatches);
	}
	
	/**
	 * Multi-syllable words which rhyme in dictionary 
	 */
	@Test
	public void multiWordTrueAccent(){
		String[][] matches = new String[][]{
				{"hello there","jello pear"},
				{"prancing queen", "dancing bean"},
				{"great bag","straight tag"},
				{"hi there","bear"},
				{"hi there","beer"},
				{"blues loose", "choose goose"},
				{"red rocket", "bed locket"},
		};
		testRhymes(matches);
	}

	/**
	 * Multi-syllable words which rhyme in dictionary 
	 */
	@Test
	public void multiWordTrue(){
		String[][] matches = new String[][]{
				{"MEGA blight","GIGA BYTE"},
				{"frown lie","brown eye"},
				{"blues loose", "choose goose"},
				{"monkey see","funky bee"},
				{"super man", "looper pan"},
				{"read books","feed rooks"},
				{"happy night","scrappy blight"},
				{"tree hugger","glee mugger"},
		};
		testRhymes(matches);
	}
	
	/**
	 * Multi-syllable words which don't rhyme in dictionary
	 */
	@Test
	public void multiWordFalse(){
		String[][] antiMatches = new String[][]{
				{"menu pause","menu clap"},
				{"super man", "spider man"},
				{"hello hello","bye bye"},
				{"tragedy in wellington", "huge earthquake"},
				{"massive loan","crippling debt"},
				{"incredible hulk","silver surfer"},
				{"queen ant", "giant ant"},
				{"long day", "super"},
				{"jewelery thief", "jewelery thief"},
		};
		testAntiRhymes(antiMatches);
	}
	
	/**
	 * Helper methods to test a set of matches
	 * @param rhymes
	 */
	private void testRhymes(String[][] rhymes) {
		int pass = 0;
		for (String[] match : rhymes){
			if (rhymes(match)){
				pass++;
			}
			else {
				printWords(match);
			}
		}
		assertEquals(rhymes.length, pass);
	}
	
	/**
	 * Helper methods to test a set of anit-matches (these strings should rhymes
	 * @param antiRhymes
	 */
	private void testAntiRhymes(String[][] antiRhymes) {
		int pass = 0;
		for (String[] match : antiRhymes){
			if (!rhymes(match)){
				pass++;
			}
			else {
				printWords(match);
			}
		}
		assertEquals(antiRhymes.length, pass);
	}

	private void printWords(String[] words){
		System.out.println("Should rhyme: ");
		for (String wordStr : words){
			System.out.println(wordStr);
			Word w = dict.get(wordStr);
			if (w == null){
			}
			else {
				//System.out.println(w.getSyllablePhones());
			}
		}
	}

	private boolean rhymes(String[] strings){
		String s1 = strings[0];
		String s2 = strings[1];
		Set<Rhyme> rhymes = rhymer.extractRhymes(s1 + "." + s2);
		return rhymes.size() == 1;
	}

}
