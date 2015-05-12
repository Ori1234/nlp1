package utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import utils.ProbabilityCalculators.LindstonProbabilityCalculator;
import utils.ProbabilityCalculators.ProbabilityCalculator;
import utils.ProbabilityCalculators.WrittenBellProbabilityCalculator;

public class Model {
	ProbabilityCalculator pc; // (lidstone/WB)
	int n;
	Map<Ngram, Double> probablities;

	
	public Model(int n, ProbabilityCalculator pc){
		this.n=n;
		this.pc = pc;
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
	
	public List<Double> calculateProplexity(String text, List<Integer> indexes) {
		try (BufferedReader br = new BufferedReader(new FileReader(text))) {
			ArrayList<String> lines = new ArrayList<String>();
			Iterator<String> iter = br.lines().iterator();//.collect(Collectors.toList());
			Integer i = 0;
			if (indexes == null) {
				return calculateProplexityForLines(lines);
			}
			while (iter.hasNext()) {
				if (indexes.contains(i++)) {
					lines.add(iter.next());
				} else {
					iter.next();
				}
			}
			return calculateProplexityForLines(lines);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public List<Double> calculateProplexity(String text) {
		try (BufferedReader br = new BufferedReader(new FileReader(text))) {
			List<String> lines = br.lines().collect(Collectors.toList());
			return calculateProplexityForLines(lines);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public List<Double> calculateProplexityForLines(List<String> lines) {
		double sumOfLogs = 0;
		List<Double> proplexities = new ArrayList<Double>();
		double perplexity;
			int counter=0;
			
			for (String line : lines) {
				System.out.println(counter++);
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
		return proplexities;
	}
}