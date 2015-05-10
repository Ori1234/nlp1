package utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
							curr_ngram.add_word(Ngram.START_END);
						}else if(j==len){
							curr_ngram.add_word(Ngram.START_END);						
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
				System.out.println(perplexity);
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