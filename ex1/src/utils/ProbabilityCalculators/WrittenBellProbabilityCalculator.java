package utils.ProbabilityCalculators;

import java.util.List;
import java.util.Map;

import utils.Ngram;

public class WrittenBellProbabilityCalculator extends ProbabilityCalculator {
	int vocabulary_size;
	List<Double> lambdas;
	
	public WrittenBellProbabilityCalculator(
			Map<Integer, Map<Ngram, Integer>> counters, int vocabulary_size,
			List<Double> lambdas) {
		// TODO Auto-generated constructor stub
		this.vocabulary_size = vocabulary_size;
		this.counters = counters;
		this.lambdas = lambdas;
	}

	@Override
	public Double calculateProbability(Ngram ngram) {
		Integer count = 0;
		int index = 0;
		double prob = 0;
		int numerator;
		int denominator;
		while (ngram.n() >= 0) {
			if (ngram.n() == 1) {
				if (counters.get(ngram.n()).get(ngram) != null) {
					count = 0;
					for (Ngram oneGram : counters.get(1).keySet()) {
						count += counters.get(1).get(oneGram);
					}
					prob += (counters.get(ngram.n()).get(ngram) / (double)count) * lambdas.get(index);
				}
			} else {
				int nt;
				int tee = Tee(ngram.remove_last_word());
				if (counters.get(ngram.n()).get(ngram.remove_last_word()) == null) {
					nt = tee;
				} else {
					nt = (counters.get(ngram.n()).get(ngram.remove_last_word()) + tee);
				}
				if ((count = counters.get(ngram.n()).get(ngram)) == null) {
					numerator = tee;
					denominator = (vocabulary_size - tee) * nt;
				} else {
					numerator = count;
					denominator = nt;
				}
				if (numerator != 0) {
					prob += lambdas.get(index) * (double)numerator / (double)denominator;
				}
			}
			index++;
			ngram = ngram.remove_last_word();
		}
		return prob;
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
