package utils.ProbabilityCalculators;

import java.util.Map;

import utils.Ngram;

public class LindstonProbabilityCalculator extends ProbabilityCalculator {
	int vucabelary_size; // is this the total number of different words?
	double LAMBDA;

	public LindstonProbabilityCalculator(
			Map<Integer, Map<Ngram, Integer>> counters, int vucabelary_size2,
			double lAMBDA2) {
		// TODO Auto-generated constructor stub
		this.vucabelary_size = vucabelary_size2;
		this.counters = counters;
		this.LAMBDA = lAMBDA2;
	}

	@Override
	public Double calculateProbability(Ngram ngram) {
		Integer ngram_count = counters.get(ngram.n()).get(ngram);
		double numenator = (ngram_count == null ? 0 : ngram_count) + LAMBDA;
		Integer ngram_n_1_count = counters.get(ngram.n() - 1).get(
				ngram.remove_last_word());
		double denomenator = (ngram_n_1_count == null ? 0 : ngram_n_1_count)
				+ LAMBDA * vucabelary_size;
		return numenator / denomenator;
	}

}
