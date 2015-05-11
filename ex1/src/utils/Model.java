package utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;

import utils.ProbabilityCalculators.LindstonProbabilityCalculator;
import utils.ProbabilityCalculators.ProbabilityCalculator;
import utils.ProbabilityCalculators.WrittenBellProbabilityCalculator;

public class Model {
	ProbabilityCalculator pc; // (lidstone/WB)
	int n;
	Map<Ngram, Double> probablities;

	
	public Model(Map<Integer,Map<Ngram,Integer>> counters,int vucabelary_size,int n,SMOOTHING smoothing_type, double LAMBDA, String test_file){
		if (smoothing_type==SMOOTHING.LIDSTONE){
			pc=new LindstonProbabilityCalculator(counters,vucabelary_size,LAMBDA);
		}else{
			List<Double> b_lambdas = new ArrayList<Double>();
			List<Double> lambdas;
			double best = Double.POSITIVE_INFINITY;
			double left_sum = 1;
			Random rand = new Random();
			for (int i = 0; i < 50; i++) {
				lambdas = new ArrayList<Double>();
				left_sum = 1;
				for (int k = 0; k < n-1; k++) {
					double random = rand.nextDouble();
					random *= left_sum;
					left_sum -= random;
					lambdas.add(random);
				}
				lambdas.add(left_sum);
				pc = new WrittenBellProbabilityCalculator(counters, vucabelary_size, lambdas);
				double prop = calculateProplexity(test_file).stream().filter(new Predicate<Double>() {
					@Override
					public boolean test(Double t) {
						if (Double.isInfinite(t)) {
							return false;
						}
						return true;
					}}).mapToDouble(Double::doubleValue).sum();
				System.out.println("prop = " + prop);
				if (prop < best) {
					best = prop;
					b_lambdas = lambdas;
				}
			}
			pc = new WrittenBellProbabilityCalculator(counters, vucabelary_size, b_lambdas);
		}
		this.n=n;
	}
	
	// Lazy implementation
	/**
	 * get probability of Ngram after smoothing
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

	public List<Double> calculateProplexity(String text) {
		double sumOfLogs = 0;
		List<Double> proplexities = new ArrayList<Double>();
		double perplexity;
		try (BufferedReader br = new BufferedReader(new FileReader(text))) {
			String line;

			while ((line = br.readLine()) != null) {
				String pattern = "[\\p{Punct}\\s]+";
				String[] line_words = line.split(pattern);
				int len = line_words.length;
				sumOfLogs = 0;
				// does this go over ngrams in different lines? should it?
				for (int i = -1; i < len+1; i++) {
					Ngram curr_ngram = new Ngram();
					
					// what happens if the line is too short?  //shouldn't be a problem
					for (int j = i -n +1 ; j <= i; j++) {   //+1
						if (j<0){
							curr_ngram.add_word(Ngram.START);
						}else if(j==len){
							curr_ngram.add_word(Ngram.END);						
						}else{
							curr_ngram.add_word(line_words[j]);
						}
					}
					
					if (curr_ngram.n()!=n){
						System.out.println("huston we have a problem");
					}
					sumOfLogs += Math.log(pc.calculateProbability(curr_ngram));
				}
				perplexity = Math.pow(Math.pow(Math.E, sumOfLogs), -(1/(double)len));
//				System.out.println(perplexity);
				proplexities.add(perplexity);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block //TODO print to user bad input
			// file name
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return proplexities;
	}
}