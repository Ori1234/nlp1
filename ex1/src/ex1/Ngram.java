package ex1;

import java.util.ArrayList;
import java.util.List;

public class Ngram {
	private List<String> words;

	public List<String> get() {
		return words;
	}

	public Ngram(String w1, String... rest_of_words) {
		words = new ArrayList<String>();
		words.add(w1);
		for (String w : rest_of_words) {
			words.add(w);
		}
	}

	public Ngram(List<String> ngram_words) {
		words = ngram_words; // Shallow clone/deep clone?
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String w : words) {
			sb.append(w + " ");
		}

		return sb.substring(0, sb.length() - 1);
	}

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

	// Read more:
	// http://javarevisited.blogspot.com/2011/02/how-to-write-equals-method-in-java.html#ixzz3Yma1aSuk
}