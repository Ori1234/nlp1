package scripts;

import java.util.HashMap;
import java.util.Map;
import java.util.OptionalDouble;

import eval.Eval;
import lm.Lm;

public class script {
	public static void main(String[] args) {

		String[] langs = { "en", "es", "ca" };
		
		
		Map<String, Map<Integer, Map<Double, Double>>> results2 = new HashMap<String, Map<Integer, Map<Double, Double>>>();
		for (String lang : langs) {
			Map<Integer, Map<Double, Double>> map = new HashMap<Integer, Map<Double, Double>>();

			String input_corpus = "data\\" + lang + "_text.corp";
			String input_test = "data\\" + lang + ".test";

			String model = lang + "_model.lm";

			for (int n = 2; n < 5; n++) {
				Map<Double, Double> res_for_lmbd = new HashMap<Double, Double>();
				for (double lmbd = 0.01; lmbd <= 1; lmbd += 1.0 / 20) {

					String[] a1 = { "-i", input_corpus, "-o", model, "-n",
							Integer.toString(n), "-s", "ls","-lmbd",Double.toString(lmbd)};
					String[] a2 = { "-i", input_test, "-m", model };

					System.out.println("####calc model " + lang + " " + n + " " + lmbd);
					Lm.main(a1);
					System.out.println("####eval test");
					double aver_proplex = Eval.evalTextByModel(input_test, model);
					res_for_lmbd.put(lmbd, aver_proplex);
					System.out.println();
				}
				map.put(n, res_for_lmbd);
			}

			results2.put(lang, map);
		}

		
		System.out.println();
		System.out.println();
		System.out.println("RESULTS");
		
		// print table:
		System.out.println("using ls smoothing");

		
		System.out.print("n:");
		for (double lmbd = 0.01; lmbd <= 1; lmbd += 1.0 / 20) {
			System.out.print(lmbd+":");
		}
		System.out.println();

		for (String lang : langs) {
			System.out.println("lang=" + lang);
			for (int n = 2; n < 5; n++) {
				System.out.print(n + ":");
				for (double lmbd = 0.01; lmbd <= 1; lmbd += 1.0 / 20) {
					System.out.print(results2.get(lang).get(n).get(lmbd)+":");
				}
				System.out.println();
			}
			System.out.println();
			System.out.println();

		}
		
		
		Map<String, Map<Integer, Double>> results = new HashMap<String, Map<Integer, Double>>();

		
		for (String lang : langs) {
			Map<Integer, Double> map = new HashMap<Integer, Double>();

			String input_corpus = "data\\" + lang + "_text.corp";
			String input_test = "data\\" + lang + ".test";

			String model = lang + "_model.lm";

			for (int n = 2; n < 5; n++) {

				String[] a1 = { "-i", input_corpus, "-o", model, "-n",
						Integer.toString(n), "-s", "wb" };
				String[] a2 = { "-i", input_test, "-m", model };

				System.out.println("****calc model " + lang + " " + n);
				Lm.main(a1);
				System.out.println("****eval test");
				double aver_proplex = Eval.evalTextByModel(input_test, model);
				map.put(n, aver_proplex);
			}
			results.put(lang, map);
		}
		System.out.println();
		System.out.println();
		System.out.println("RESULTS");
		
		// print table:
		System.out.println("using wb smoothing");
		for (String lang : langs) {
			System.out.println("lang=" + lang);
			for (int n = 2; n < 5; n++) {
				System.out.println(n + ":" + results.get(lang).get(n));
			}
			System.out.println();
		}

	}
}
