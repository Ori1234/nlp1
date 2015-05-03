package utils.ProbabilityCalculators;

import java.util.Map;

import utils.Ngram;

public class LindstonProbabilityCalculator extends ProbabilityCalculator {
	int vucabelary_size;  //is this the total number of different words?
	double LAMBDA;
	
	
	

	
	public LindstonProbabilityCalculator(
			Map<Integer, Map<Ngram, Integer>> counters, int vucabelary_size2,
			double lAMBDA2) {
		// TODO Auto-generated constructor stub
		this.vucabelary_size=vucabelary_size2;
		this.counters=counters;
		this.LAMBDA=lAMBDA2;
	}





	@Override
	public Double calculateProbability(Ngram ngram) {
		// TODO Auto-generated method stub
		double numenator = counters.get(ngram.n()).get(ngram) + LAMBDA;
		double denomenator = counters.get(ngram.n()).get(ngram.remove_last_word()) + LAMBDA * vucabelary_size;
		return numenator / denomenator;
	}
	
}
