package rhymer.test;

import org.junit.Before;
import org.junit.Test;

import rhymer.Rhymer;
import rhymer.lang.Phone;
import rhymer.lang.Sentence;
import rhymer.lang.Trie;
import rhymer.lang.Word;

public class TrieTest {

	@Before
	public void before(){
		Rhymer.phoneTypes = Rhymer.readPhoneSet();
	}
	
	@Test
	public void testPut(){
		Trie trie = new Trie();
		
		Sentence sen1 = new Sentence(new Word[]{ 
				new Word("be", new Phone[]{new Phone("B"), new Phone("IY") }) 
			}, 1);
		
		Sentence sen2 = new Sentence(new Word[]{ 
				new Word("tee", new Phone[]{new Phone("T"), new Phone("IY") }) 
			}, 1);
		
		Sentence sen3 = new Sentence(new Word[]{ 
				new Word("lee", new Phone[]{new Phone("L"), new Phone("IY") }) 
			}, 1);
		
		Sentence sen4 = new Sentence(new Word[]{ 
				new Word("tah", new Phone[]{new Phone("T"), new Phone("AH") }) 
			}, 1);
		
		trie.put(sen1.getSyllablePhonesReversed(), sen1);
		trie.put(sen2.getSyllablePhonesReversed(), sen2);
		trie.put(sen3.getSyllablePhonesReversed(), sen3);
		trie.put(sen4.getSyllablePhonesReversed(), sen4);
		
		System.out.println(trie.toString());
	}
	
}
