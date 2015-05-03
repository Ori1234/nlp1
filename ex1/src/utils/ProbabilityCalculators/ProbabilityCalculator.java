package utils.ProbabilityCalculators;

import java.util.Map;

import utils.Ngram;

public abstract class ProbabilityCalculator {
	Map<Integer, Map<Ngram, Integer>> counters; // keyed by n
	public abstract Double calculateProbability(Ngram ngram);
}
