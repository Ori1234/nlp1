package utils;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import utils.ProbabilityCalculators.ProbabilityCalculator;

public class Model {
	ProbabilityCalculator pc; // (lidstone/WB)
	int n;
	Map<Ngram, Double> probablities;

	/**
	 * class constructor
	 * @param n : ngr 
	 * @param pc 
	 */
	public Model(int n, ProbabilityCalculator pc) {
		this.n = n;
		this.pc = pc;
	}

	
	/**
	 * get probability of Ngram after smoothing
	 * Lazy implementaion.
	 * 
	 * @param ngram
	 * @return smoothed model probability
	 */
	public double getProbability(Ngram ngram) {
		Double a;
		if ((a = probablities.get(ngram)) == null) {
			probablities.put(ngram, (a = pc.calculateProbability(ngram)));
		}
		return a;
	}


	
	public double calculateProplexity(String text_line) {
		double sumOfLogs = 0;
		String pattern = "[\\p{Punct}\\s]+";
		String[] line_words = text_line.split(pattern);
		int len = line_words.length;
		sumOfLogs = 0;

		//iterate all ngrams in text_line
		for (int i = -1; i < len + 1; i++) {
			
			Ngram curr_ngram = new Ngram();
			for (int j = i - this.n + 1; j <= i; j++) { // +1
				if (j < 0) {
					curr_ngram.add_word(Ngram.START);
				} else if (j == len) {
					curr_ngram.add_word(Ngram.END);
				} else {
					curr_ngram.add_word(line_words[j]);
				}
			}
			Double calculateProbability = pc
					.calculateProbability(curr_ngram);
//			System.out.println(curr_ngram + " " + calculateProbability);
			sumOfLogs += Math.log(calculateProbability);

		}
		
		double logPerplexity = -(sumOfLogs / len + 2);

		double perplexity = Math.pow(Math.E, logPerplexity);
		if (Double.isInfinite(perplexity) || Double.isNaN(perplexity)) {
			System.out.println(sumOfLogs);
			System.err.println("perplexity =  " + perplexity);
			System.err.println();
			System.err.println();
		}
		return perplexity;
	}

}