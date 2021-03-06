package lm;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;

import utils.Model;
import utils.Ngram;
import utils.SMOOTHING;
import utils.Utils;
import utils.ProbabilityCalculators.WrittenBellProbabilityCalculator;

public class Lm {

	private static final double HELDOUT_PERCENTEGE = 0.01;
	private static final int NUM_OF_DRAWS_FOR_WB_LAMBDAS = 1000;
	static Random random = new Random();

	/*
	 * -i data/en_text.corp -o data/model.lm -n 3 -s wb
	 */

	public static void main(String[] args) {

		Map<String, String> params = Utils.parseARGS(args); // sets glabals

		String n_str = params.get("-n");
		if (n_str == null) {
			throw new IllegalArgumentException("missing required flag -n");
		}
		int n = Integer.parseInt(n_str);
		String input = params.get("-i");
		if (input == null) {
			throw new IllegalArgumentException("missing required flag -i");
		}
		String output = params.get("-o");
		if (output == null) {
			throw new IllegalArgumentException("missing required flag -o");
		}
		if (params.get("-s") == null) {
			throw new IllegalArgumentException("missing required flag -s");
		}
		SMOOTHING smoothing = (params.get("-s").equals("ls") ? SMOOTHING.LIDSTONE
				: SMOOTHING.WB);
		double lidstone_LAMBDA;
		if (params.get("-lmbd") == null) {
			lidstone_LAMBDA = 1;
		} else {
			lidstone_LAMBDA = Double.parseDouble(params.get("-lmbd"));
		}

		// read file and count ngrams
		Map<Integer, Map<Ngram, Integer>> counts = new HashMap<Integer, Map<Ngram, Integer>>();
		List<Integer> heldout = null;
		if (smoothing == SMOOTHING.WB) {
			heldout = new ArrayList<Integer>();
		}
		
		System.out.println("Counting n-grams in corpus...");
		for (int curr_n = n; curr_n > 0; curr_n--) {
			System.out.println("N = "+curr_n);
			Map<Ngram, Integer> counters;
			if (curr_n == n){
				counters = countNgrams(curr_n, input, heldout);
			}else {
				counters = countNgrams(curr_n, input, null);
			}
			counts.put(curr_n, counters);
		}
		// write model
		try (PrintWriter writer = new PrintWriter(output, "UTF-8")) {

			// Print DATA
			writer.println("\\data\\");
			for (int curr_n = n; curr_n > 0; curr_n--) {
				writer.format("ngram %d=%d\n", curr_n, counts.get(curr_n)
						.size());
			}
			writer.format("vucabulary size=%d\n", counts.get(1).size());
			writer.format("smoothing=%s\n", smoothing);

			if (smoothing == SMOOTHING.LIDSTONE) {
				writer.format("lidstone labmda=%s\n", lidstone_LAMBDA);
			}

			if (smoothing == SMOOTHING.WB) {
				// generate lamdas
				
				System.out
				.println("calculating lambdas for WB smoothing on heldout text from corpus...");
				System.out.println("size of heldout text:"+heldout.size()+" lines of text");
				
				List<Double> b_lambdas = findBestWBParams(n, counts, heldout,
						input);
				
				System.out.println();
				
				writer.print("wb labmdas=");
				System.out
						.println("calculated lambdas:");
				int decs=n;
				for (double l : b_lambdas) {
					writer.print(" " + l);
					System.out.println("lambd"+decs--+":"+l);
				}
				writer.println();
			}
			writer.println();
			for (int curr_n = n; curr_n > 0; curr_n--) {
				write_ngrams(curr_n, counts.get(curr_n), writer);
				writer.println();

			}

			System.out.println("Created model at:" + output);
			System.out.println("DONE");
			
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private static List<Double> findBestWBParams(int n,
			Map<Integer, Map<Ngram, Integer>> counts, List<Integer> heldout,
			String input_corpus) {
		List<Double> b_lambdas = new ArrayList<Double>();
		List<Double> lambdas;
		double best = Double.POSITIVE_INFINITY;
		double left_sum = 1;
		Random rand = new Random();
		WrittenBellProbabilityCalculator pc = new WrittenBellProbabilityCalculator(
				counts, counts.get(1).size(), b_lambdas);
		Model model = new Model(n, pc);
		for (int i = 0; i < NUM_OF_DRAWS_FOR_WB_LAMBDAS; i++) {
			System.out.print(".");
			//draw random lambda values to test
			lambdas = new ArrayList<Double>();
			left_sum = 1;

			for (int k = 0; k < n - 1; k++) {
				double random = rand.nextDouble();
				random *= left_sum;
				left_sum -= random;
				lambdas.add(random);
			}
			lambdas.add(left_sum);
			pc.setLambdas(lambdas);

			List<Double> perplexities = new ArrayList<Double>();
			try (BufferedReader br = new BufferedReader(new FileReader(
					input_corpus))) {
				String line;
				int counter = 0;
				while ((line = br.readLine()) != null) {
					if (heldout.contains(counter++)){
						if (i==0){
							System.out.print(".");
						}
						perplexities.add(model.calculateProplexity(line));
					}
				}

				double prop = perplexities.stream()
						/*.filter(new Predicate<Double>() {
							@Override
							public boolean test(Double t) {
								if (Double.isInfinite(t)) {
									return false;
								}
								return true;
							}
						})*/
						.mapToDouble(Double::doubleValue).sum();
				if (prop < best) {
					best = prop;
					b_lambdas = lambdas;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return b_lambdas;
	}

	/**
	 * write ngram sections to the model file.
	 * 
	 * @param n
	 * @param counters
	 * @param writer
	 */
	private static void write_ngrams(int n, Map<Ngram, Integer> counters,
			PrintWriter writer) {
		writer.format("\\%d-grams:\n", n);

		for (Entry<Ngram, Integer> pair : counters.entrySet()) {

			int count = pair.getValue();
			writer.println(count + " " + pair.getKey().toString());
		}
		writer.println();
	}

	private static Map<Ngram, Integer> countNgrams(int n, String input,
			List<Integer> heldout) {
		Map<Ngram, Integer> counters = new HashMap<>();

		try (BufferedReader br = new BufferedReader(new FileReader(input))) {
			String line;
			Integer line_nu = 0;
			while ((line = br.readLine()) != null) {

				if (heldout != null && random.nextDouble() < HELDOUT_PERCENTEGE) {
					heldout.add(line_nu++);
					continue;
				}
				line_nu++;
				String pattern = "[\\p{Punct}\\s]+";
				line = line.replaceFirst(pattern, ""); // needed because java's
														// split returns an
														// empty string at
														// beginning of split if
														// line starts with
														// pattern.
				String[] line_words = line.split(pattern);
				int len = line_words.length;
				for (int i = -1; i < len + 1; i++) {
					Ngram curr_ngram = new Ngram();
					// what happens if the line is too short?
					for (int j = i - n + 1; j <= i; j++) { // +1
						if (j < 0) {
							curr_ngram.add_word(Ngram.START);
						} else if (j == len) {
							curr_ngram.add_word(Ngram.END);
						} else {
							curr_ngram.add_word(line_words[j]);
						}
					}

					if (curr_ngram.n() != n) {
						System.out.println("huston we have a problem");
					}
					Integer count;
					if ((count = counters.get(curr_ngram)) == null) {
						counters.put(curr_ngram, 1);
					} else {
						counters.put(curr_ngram, count + 1);
					}
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block //TODO print to user bad input
			// file name
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return counters;
	}
}
