package lm;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import ex1.Ngram;

public class main {

	private static String flag_i; // input path
	private static String flag_o; // output path
	private static int flag_GAMA; // smoothing paramater <optinoal>
	private static int flag_n; // n- gram
	private static String flag_s; // choose smoothing

	public static void main(String[] args) {

		parseARGS(args); // sets glabals

		int n = flag_n;
		String input = flag_i;
		String output = flag_o;

		// read file and count ngrams
		//TODO count both in one pass. and also count lines.
		Map<Ngram, Integer> counters = countNgrams(n,input);
		Map<Ngram, Integer> counters_1 = countNgrams(n-1,input);
		
		//a really dirty fix.
		List<String> Ngram_start_start = new ArrayList<String>();
		for (int i=0;i<n-1;i++)
			Ngram_start_start.add(Ngram.START_END); 
		Integer num_of_lines=count_lines(input);
		counters_1.put(new Ngram(Ngram_start_start),num_of_lines);

		
		// write model
		try (PrintWriter writer = new PrintWriter(output, "UTF-8")) {
			writer.println("\\data\\");
			writer.format("ngram %d=%d\n", n, counters.size());
			writer.println();

			writer.format("\\%d-grams:\n", n);
			
			for (Entry<Ngram, Integer> pair : counters.entrySet()) {
				//get count for first n-1 words
				Ngram prefix=pair.getKey().getPrefix();
				
				
				Integer count_1 = counters_1.get(prefix);
				if (count_1==null){
					System.out.println("help");
				}
				double P = (double) pair.getValue() / count_1;
				//just for fun:
				if (pair.getValue()!=1){
					System.out.println(pair.getKey().toString() + " " + pair.getValue());
				}
				double logP = Math.log(P);// natural logarithm OK?
				writer.println(logP + " " + pair.getKey().toString());
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch blo1ck
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(output);
		System.out.println("DONE");
	}

	private static Integer count_lines(String input) {		
		int count=0;

		try (BufferedReader br = new BufferedReader(new FileReader(input))) {
			

			while (br.readLine() != null){
				count++;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return count;
	}

	private static Map<Ngram, Integer> countNgrams(int n, String input) {
		Map<Ngram, Integer> counters = new HashMap<>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(input))) {
			String line;

			while ((line = br.readLine()) != null) {
				String pattern = "[\\p{Punct}\\s]+";
				String[] line_words = line.split(pattern);
				int len = line_words.length;
				for (int i = 0; i < len+1; i++) {
					List<String> ngram_words = new ArrayList<String>();

					for (int j = i -n +1 ; j <= i; j++) {   //+1
						if (j<0){
							ngram_words.add(Ngram.START_END);
						}else if(j==len){
							ngram_words.add(Ngram.START_END);						
						}else{
							ngram_words.add(line_words[j]);
						}
					}
					
					if (ngram_words.size()!=n){
						System.out.println("huston we have a problem");
					}
					Ngram curr_ngram = new Ngram(ngram_words);
					Integer count;
					if ((count = counters.get(curr_ngram)) == null) {
						counters.put(curr_ngram, 1);
					} else {
						counters.put(curr_ngram, count + 1);
					}
				}
				System.out.print(".");			
			}
			System.out.println();
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

	private static void parseARGS(String[] args) {
		// TODO //set real globals
//		flag_i = "C:\\Users\\OriTerner\\git\\nlp\\ex1\\data\\en.test";
		flag_i = "C:\\Users\\OriTerner\\git\\nlp\\ex1\\data\\en_text.corp";
		flag_o = "C:\\Users\\OriTerner\\git\\nlp\\ex1\\data\\model.lm";
		flag_GAMA = 1;
		flag_n = 4;
		flag_s = "";
	}

}
