package utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Ngram {
	
	
	public static String START="<S>";
	public static String END="</S>";
	
	private List<String> words;
	private int n;
	public List<String> get() {
		return words;
	}

	/**
	 * constractor
	 * @param w1
	 * @param rest_of_words
	 */
	public Ngram(String w1, String... rest_of_words) {
		words = new ArrayList<String>();
		words.add(w1);
		for (String w : rest_of_words) {
			words.add(w);
		}
		n=words.size();
	}

	public Ngram(List<String> ngram_words) {
		words = ngram_words; // Shallow clone/deep clone?
		n = ngram_words.size();
	}
	
	// return a new Ngram with a new word at the end
	public Ngram(Ngram shorter, String word) {
		words = new ArrayList<String>(shorter.get());
		words.add(word);
		n = shorter.n + 1;
	}

	public Ngram() {
		words = new ArrayList<String>();
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String w : words) {
			sb.append(w + " ");
		}

		return sb.substring(0, sb.length() - 1);
	}

	
	// Read more:
		// http://javarevisited.blogspot.com/2011/02/how-to-write-equals-method-in-java.html#ixzz3Yma1aSuk
	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		Ngram guest = (Ngram) obj;
		return words.equals(guest.words);				
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((words == null) ? 0 : words.hashCode());
		return result;
	}

	/**
	 * 
	 * @return Ngram with first n-1 words of this Ngram
	 */
	public Ngram remove_last_word() {
		List<String> words_1= new ArrayList<String>(words);
		words_1.remove(words.size()-1);
		return new Ngram(words_1);
	}

	public int n() {
		if (n==0){
			n=words.size();
		}
		return n;
	}

	public void add_word(String string) {
		words.add(string);
	}

	
}