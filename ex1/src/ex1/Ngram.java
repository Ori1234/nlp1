package ex1;

import java.util.ArrayList;
import java.util.List;

public class Ngram {
	private List<String> words;
	public List<String> get(){
		return words;
	}
	
	public Ngram(String w1,String ... rest_of_words){
		words=new ArrayList<String>();
		words.add(w1);
		for (String w: rest_of_words){
			words.add(w);
		}
	}

	public Ngram(List<String> ngram_words) {
		words=ngram_words;   //Shallow clone/deep clone?
	}
	
	@Override
	public String toString(){
		StringBuilder sb=new StringBuilder();
		for (String w:words){
			sb.append(w+" ");
		}
		
		return sb.substring(0,sb.length()-1);
	}
}