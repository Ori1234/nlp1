package eval;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import utils.Model;
import utils.Ngram;
import utils.SMOOTHING;
import utils.Utils;
import utils.ProbabilityCalculators.LindstonProbabilityCalculator;
import utils.ProbabilityCalculators.ProbabilityCalculator;
import utils.ProbabilityCalculators.WrittenBellProbabilityCalculator;

public class Eval {

	public static void main(String[] args) {

		Map<String, String> params = Utils.parseARGS(args);

		String input = params.get("-i");
		if (input == null) {
			throw new IllegalArgumentException("missing required flag -i");
		}
		String model_file = params.get("-m");
		if (model_file == null) {
			throw new IllegalArgumentException("missing required flag -m");
		}

		double averagePerplexity = evalTextByModel(input, model_file);
		System.out.println("average proplexity on " + input + " lines= "
				+ averagePerplexity);
	}

	public static double evalTextByModel(String input, String model_file) {
		// read model from model file
		
		System.out.println("loading model file "+ model_file + "...");
		Model model = loadModel(model_file);

		System.out.println("calculating perplexities "+ input);
		List<Double> proplexities = new ArrayList<Double>();
		// read input text and calculate perplexity for each line in text.
		try (BufferedReader br = new BufferedReader(new FileReader(input))) {
			String line;
			while ((line = br.readLine()) != null) {
				
				proplexities.add(model.calculateProplexity(line));
				System.out.print(".");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// System.out.println(model_file);

		// Average all perplexity values for model evaluation.
		OptionalDouble average_proplexity = proplexities.stream()
				.mapToDouble(Double::doubleValue).average();

		System.out.println(average_proplexity);

		return average_proplexity.getAsDouble();
	}

	public enum SECTION {
		DATA, N_GRAM, UNKNOWN;
	}

	private static Model loadModel(String model_file) {
		Map<Integer, Map<Ngram, Integer>> counters = new HashMap<Integer, Map<Ngram, Integer>>();
		Map<String, String> data = new HashMap<String, String>();
		try (BufferedReader br = new BufferedReader(new FileReader(model_file))) {
			String line;
			int n = 0;
			SECTION section = null;
			while ((line = br.readLine()) != null) {
				if (line.equals("")) {
					continue;
				}
				// Recognize current section (DATA / N-GRAMS)
				if (line.charAt(0) == '\\') {
					if (line.equals("\\data\\")) {
						section = SECTION.DATA;
						continue;
					} else {
						String n_gram_pattern = ".(\\d+)-grams:";
						Pattern pattern = Pattern.compile(n_gram_pattern);
						Matcher matcher = pattern.matcher(line);
						if (matcher.matches()) {
							n = Integer.parseInt(matcher.group(1));
							section = SECTION.N_GRAM;
							counters.put(n, new HashMap<Ngram, Integer>());
							continue;
						} else {
							section = SECTION.UNKNOWN;
							System.out
									.println("unkonwn section in model file :"
											+ line);
							continue;
						}
					}
				}
				// insert data by section
				switch (section) {
				case DATA:

					String[] split = line.split("\\s*=\\s*");
					if (split.length != 2) {
						System.out.println("wrong data format:" + line);
					} else {
						data.put(split[0], split[1]);
					}

					break;
				case N_GRAM:
					String[] line_words = line.split(" ");
					int count = Integer.parseInt(line_words[0]);// TODO add
																// try/catch
																// declaration
					Ngram ngram = new Ngram();
					for (int i = 1; i < line_words.length; i++) {
						ngram.add_word(line_words[i]);
					}
					if (ngram.n() != n) {// sanity
						System.out.println("model data incorrect:" + ngram.n()
								+ " words, under section " + n + "-grams "
								+ line);

						continue;
					}
					counters.get(n).put(ngram, count);
					break;
				default:
					break;
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// TODO check that data have required fields
		ProbabilityCalculator pc;
		if (data.get("smoothing").equals(SMOOTHING.LIDSTONE.toString())) {
			pc = new LindstonProbabilityCalculator(counters,
					Integer.parseInt(data.get("vucabulary size")),
					Double.parseDouble(data.get("lidstone labmda")));
		} else {
			List<Double> lambdas = new ArrayList<Double>();
			for (String l : data.get("wb labmdas").split(" ")) {
				lambdas.add(Double.parseDouble(l));
			}
			pc = new WrittenBellProbabilityCalculator(counters,
					Integer.parseInt(data.get("vucabulary size")), lambdas);
		}

		return new Model(max(counters.keySet()), pc);
	}

	private static int max(Set<Integer> keySet) {
		int res = 0;
		for (int i : keySet) {
			if (i > res) {
				res = i;
			}
		}
		return res;
	}
}