package eval;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import utils.Model;
import utils.Ngram;
import utils.SMOOTHING;
import utils.Utils;

public class Eval {

	public static void main(String[] args) {
		Map<String, String> params = Utils.parseARGS(args); // sets glabals

		String input = params.get("-i");
		if (input == null) {
			throw new IllegalArgumentException("missing required flag -i");
		}
		String model_file = params.get("-m");
		if (model_file == null) {
			throw new IllegalArgumentException("missing required flag -m");
		}

		// read model to counters.
		Model model = loadModel(model_file, input);
		
		// read text and calculate preplexity. (for each line? for whole text?)
		// String text = null;
		List<Double> proplexities = model.calculateProplexity(input);
		System.out.println(model_file);
		for (double p : proplexities)
			System.out.println(p);
		System.out.println();
		System.out.println(proplexities.stream().mapToDouble(Double::doubleValue).sum()/proplexities.size());

	}

	public enum SECTION {
		DATA, N_GRAM, UNKNOWN;
	}

	// TODO complete implementation
	private static Model loadModel(String model_file, String test_file) {
		Map<Integer, Map<Ngram, Integer>> counters = new HashMap<Integer, Map<Ngram, Integer>>();
		Map<String, String> data = new HashMap<String, String>();
		int max_n = 0;
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

		return new Model(counters,
				Integer.parseInt(data.get("vucabulary size")),
				max(counters.keySet()), (data.get("smoothing").equals(
						SMOOTHING.LIDSTONE.toString()) ? SMOOTHING.LIDSTONE
						: SMOOTHING.WB), (data.get("smoothing").equals(
								SMOOTHING.LIDSTONE.toString())) ? Double.parseDouble(data
						.get("lidstone labmda")) : 1, test_file);
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