package utils;

import java.util.Map;

import utils.ProbabilityCalculators.LindstonProbabilityCalculator;
import utils.ProbabilityCalculators.ProbabilityCalculator;
import utils.ProbabilityCalculators.WrittenBellProbabilityCalculator;

public class Model {
	ProbabilityCalculator pc; // (lidstone/WB)
	int n;
	Map<Ngram, Double> probablities;

	
	public Model(Map<Integer,Map<Ngram,Integer>> counters,int vucabelary_size,int n,SMOOTHING smoothing_type, double LAMBDA){
		if (smoothing_type==SMOOTHING.LIDSTONE){
			pc=new LindstonProbabilityCalculator(counters,vucabelary_size,LAMBDA);
		}else{
			pc = new WrittenBellProbabilityCalculator(counters, vucabelary_size, LAMBDA);
		}
		this.n=n;
	}
	
	// Lazy implementation
	/**
	 * get probability of Ngram after smoothing before interpulation
	 * @param ngram
	 * @return
	 */
	public double getProbability(Ngram ngram) {
		Double a;
		if ((a = probablities.get(ngram)) == null) {
			probablities.put(ngram, (a = pc.calculateProbability(ngram)));
		}
		return a;
	}

	public double getInterpulationProbability(Ngram ngram) {		
		//TODO what about Lambdas????
		double result=getProbability(ngram);
		for(int i=n;i>0;i--){
			Ngram curr_iteration_ngram = ngram.remove_last_word();
			result+=getProbability(curr_iteration_ngram);
		}
		return result;
	}

	public void calculateProplexity(String text) {
		// TODO Auto-generated method stub
		
	}
}