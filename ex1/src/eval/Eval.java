package eval;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
		Model model = loadModel(model_file);

		// read text and calculate preplexity. (for each line? for whole text?)
//		String text = null;
		List<Double> proplexities = model.calculateProplexity(input);
	}

	public enum SECTION {
		DATA, N_GRAM, UNKNOWN;
	}

	// TODO complete implementation
	private static Model loadModel(String model_file) {
		Map<Integer, Map<Ngram, Integer>> counters = new HashMap<Integer, Map<Ngram, Integer>>();
		int vucabelary_size = 0;
		SMOOTHING smoothing_type = null;
		double LAMBDA = 0;
		int max_n = 0;
		try (BufferedReader br = new BufferedReader(new FileReader(model_file))) {
			String line;
			int n = 0;
			SECTION section = null;
			while ((line = br.readLine()) != null) {
				if (line.equals("")){
					continue;
				}
				// Recognize current section				
				if (line.charAt(0) == '\\') {
					if (line.equals("\\data\\")) {
						section = SECTION.DATA;
						continue;
					} else if (line.equals("\\SOME OTHER DATA")) { // TODO

						continue;
					} else {
						String n_gram_pattern = ".(\\d+)-grams:";
						Pattern pattern = Pattern.compile(n_gram_pattern);
						Matcher matcher = pattern.matcher(line);
						if (matcher.matches()) {
							n = Integer.parseInt(matcher.group(1));
							if (n > max_n) {
								max_n = n;
							}
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
				//insert data by section
				switch (section) {
				case DATA:
					String unigram_pattern="ngram 1=(\\d+)";
					Pattern pattern = Pattern.compile(unigram_pattern);
					Matcher matcher = pattern.matcher(line);
					if (matcher.matches()) {
						vucabelary_size = Integer.parseInt(matcher.group(1));
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
								+ " words, under section " + n + "-grams "+line);
						
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

		return new Model(counters, vucabelary_size, max_n, smoothing_type,
				LAMBDA);
	}
}