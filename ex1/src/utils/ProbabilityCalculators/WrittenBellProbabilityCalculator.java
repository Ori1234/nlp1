package utils.ProbabilityCalculators;

import java.util.Map;

import utils.Ngram;

public class WrittenBellProbabilityCalculator extends ProbabilityCalculator {
	int vocabulary_size;
	double lambda;
	
	public WrittenBellProbabilityCalculator(
			Map<Integer, Map<Ngram, Integer>> counters, int vocabulary_size,
			double lambda) {
		// TODO Auto-generated constructor stub
		this.vocabulary_size = vocabulary_size;
		this.counters = counters;
		this.lambda = lambda;
	}

	@Override
	public Double calculateProbability(Ngram ngram) {
		Integer count;
		int numerator;
		int denominator;
		int tee = Tee(ngram.remove_last_word());
		int nt = (counters.get(ngram.n()).get(ngram.remove_last_word()) + tee);
		if ((count = counters.get(ngram.n()).get(ngram)) == null) {
			numerator = tee;
			denominator = (vocabulary_size - tee) * nt;
		} else {
			numerator = count;
			denominator = nt;
		}
		return (double)numerator / (double)denominator;
	}
	
	private int Tee(Ngram ngram) {
		int i = 0;
		String word;
		Ngram tempGram;
		// run over all the words in the corpus
		for (Ngram oneGram : counters.get(1).keySet()) {
			word = oneGram.get().get(1);
			tempGram = new Ngram(ngram, word);
			// check if the new ngram with the new word is in the table
			if (counters.get(tempGram.n()).get(tempGram) != null) {
				i += 1;
			}
		}
		return i;
	}

}
