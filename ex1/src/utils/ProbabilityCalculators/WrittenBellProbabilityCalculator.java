package utils.ProbabilityCalculators;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import utils.Ngram;

public class WrittenBellProbabilityCalculator extends ProbabilityCalculator {
	int vocabulary_size;
	List<Double> lambdas;
	int corpus_size;
	Map<Ngram, Integer> Tees;
	
	public WrittenBellProbabilityCalculator(
			Map<Integer, Map<Ngram, Integer>> counters, int vocabulary_size,
			List<Double> lambdas) {
		// TODO Auto-generated constructor stub
		this.vocabulary_size = vocabulary_size;
		this.counters = counters;
		this.lambdas = lambdas;
		int count = 0;
		for (Ngram oneGram : counters.get(1).keySet()) {
			count += counters.get(1).get(oneGram);
		}
		this.corpus_size = count;
		this.Tees = new HashMap<Ngram, Integer>();
	}

	
	
	@Override
	public Double calculateProbability(Ngram ngram) {
		double res=0;
		for (int i=0;i<ngram.n();i++){
			res+=lambdas.get(i)*calculateProbabilityBeforeInterpulation(ngram);
			ngram=ngram.remove_first_word();
		}
		return res;
	}
		private Double calculateProbabilityBeforeInterpulation(Ngram ngram) {
			Integer count = 0;
			int numerator;
			int denominator;
			if (ngram.n() == 1) {
				if (counters.get(1).get(ngram) != null)  {
					return ((double)(counters.get(1).get(ngram)) / (double)corpus_size);
				} else {
					return (double) 0;
				}
			} else {				
				Integer N =  counters.get(ngram.n()-1).get(ngram.remove_last_word());
				if (N==null){
					return 0.0;
				}
								
				int T = Tee(ngram.remove_last_word());
				int Z = vocabulary_size - T;
				
				if ((count = counters.get(ngram.n()).get(ngram)) == null) {
					numerator = T;
					denominator = Z * (N + T);
				} else {
					numerator = count;
					denominator = N + T;
				}
				return ((double)numerator / (double)denominator);
			}
		}
		
	
	private int Tee(Ngram ngram) {
		if (Tees.get(ngram) != null) {
			System.out.print(".");
			return Tees.get(ngram);
			
		}
		System.out.print("/");
		int i = 0;
		String word;
		Ngram tempGram;
		// run over all the words in the corpus
		for (Ngram oneGram : counters.get(1).keySet()) {
			word = oneGram.get().get(0);
			tempGram = new Ngram(ngram, word);
			// check if the new ngram with the new word is in the table
			if (counters.get(tempGram.n()).get(tempGram) != null) {
				i += 1;
			}
		}
		Tees.put(ngram, i);
		return i;
	}

	public void setLambdas(List<Double> lambdas2) {
		this.lambdas=lambdas2;		
	}

}
