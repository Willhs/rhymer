package rhymer.lang;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TrieNode {
	
	private Map<Phone, TrieNode> children;
	
	private Set<Sentence> values;
	
	public TrieNode(){
		children = new HashMap<>();
		values = new HashSet<>();
	}
	
	public boolean addValue(Sentence value){
		return values.add(value);
	}
	
	public TrieNode getChild(Phone key){
		return children.get(key);
	}
	
	public Set<Sentence> getValues(){
		return values;
	}
	
	public boolean isLeaf(){
		return children.size() == 0;
	}

	public boolean hasChild(Phone key) {
		return children.containsKey(key);
	}

	public TrieNode createChild(Phone key) {
		TrieNode child = new TrieNode();
		children.put(key, child);
		return child;
	}
	
	public Set<Sentence> getValuesBelow(){
		Set<Sentence> values = new HashSet<>();
		values.addAll(this.values);
		for (TrieNode child : children.values()){
			values.addAll(child.getValuesBelow());
		}
		return values;
	}
	
	public String toString(int level){
		String s = "";
		String indent = "";
		for (int l = 0; l < level; l++){
			indent += " ";
		}
		s += indent;
		if (hasValue()){
			for (Sentence sen : values){
				s += sen.getSyllablePhones()+ " ";
			}
		}
		else {
			s += "-";
		}
		s += "\n";
		
		for (TrieNode child : children.values()){
			if (hasValue() || level == 0)
				s += child.toString(level + 1);
			else s += child.toString(level);
		}
		return s;
	}
	
	public boolean hasValue(){
		return !values.isEmpty();
	}
	
	public boolean hasChildren(){
		return !children.isEmpty();
	}
	
	public int numChildren(){
		return children.size();
	}
}
