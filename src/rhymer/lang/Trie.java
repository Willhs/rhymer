package rhymer.lang;

import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Trie implements Iterable<Sentence>{

	private TrieNode root;
	private int size;
	
	public Trie(){
		size = 0;
		root = new TrieNode();
	}
	
	public void put(List<? extends Phone> keys, Sentence value){
		TrieNode node = root;
		for (int i = 0; i < keys.size(); i++){
			Phone key = keys.get(i);
			if (node.hasChild(key))
				node = node.getChild(key);
			else {
				node = node.createChild(key);
			}
		}
		TrieNode dest = node;
		boolean added = dest.addValue(value);
		
		if (added)
			size++;
	}
	
	private TrieNode getNode(List<? extends Phone> keys){
		TrieNode node = root;
		for (int i = 0; i < keys.size(); i++){
			Phone key = keys.get(i);
			if (node.hasChild(key))
				node = node.getChild(key);
			else {
				return null;
			}
		}
		return node;
	}
	
	public Set<Sentence> getValuesBelow(List<? extends Phone> keys){
		TrieNode node = getNode(keys);
		return node.getValuesBelow();
	}
	
	public Iterator<Sentence> iterator(){
		return root.getValuesBelow().iterator();
	}
	
	public int size(){
		return size;
	}
	
	public String toString(){
		return root.toString(0);
	}
	
	public int rootNodeChildren(){
		return root.numChildren();
	}
}
