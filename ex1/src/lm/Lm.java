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
import java.util.Map.Entry;

import utils.Ngram;
import utils.Utils;

public class Lm {

	public static void main(String[] args) {

		Map<String, String> params = Utils.parseARGS(args); // sets glabals

	
		String n_str=params.get("-n");
		if (n_str==null){
            throw new IllegalArgumentException("missing required flag -n");
		}
		int n = Integer.parseInt(n_str);
		String input = params.get("-i");
		if (input==null){
            throw new IllegalArgumentException("missing required flag -i");
		}
		String output = params.get("-o");
		if (output==null){
            throw new IllegalArgumentException("missing required flag -o");
		}
		String smoothing=params.get("-s");
		if (smoothing==null){
            throw new IllegalArgumentException("missing required flag -s");
		}
		double lidstone_LAMBDA;
		if (params.get("-lmbd") == null) {
			lidstone_LAMBDA = 1;
		} else {
			lidstone_LAMBDA = Double.parseDouble(params.get("-lmbd"));
		}
		
		
/*		//COMMENT OUT IF NOT DEBUG
		input = "C:\\Users\\OriTerner\\git\\nlp\\ex1\\data\\en.test";
		input = "C:\\Users\\OriTerner\\git\\nlp\\ex1\\data\\en_text.corp";
		output = "C:\\Users\\OriTerner\\git\\nlp\\ex1\\data\\model.lm";
		n = 3;
		smoothing= "";
	*/
			
		// read file and count ngrams
		Map<Ngram, Integer> counters = countNgrams(n,input);
		Map<Ngram, Integer> counters_1 = countNgrams(n-1,input);		
		
		// write model
		try (PrintWriter writer = new PrintWriter(output, "UTF-8")) {
			writer.println("\\data\\");
			writer.format("ngram %d=%d\n", n, counters.size());
			writer.println();

			write_ngrams(n, counters, writer);
			
			write_ngrams(n-1, counters_1, writer);
			
			writer.println("\\smoothing\\");
			writer.format("%s %f", smoothing,lidstone_LAMBDA);					
			
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

	private static void write_ngrams(int n, Map<Ngram, Integer> counters,
			PrintWriter writer) {
		writer.format("\\%d-grams:\n", n);
		
		for (Entry<Ngram, Integer> pair : counters.entrySet()) {
											
			int count = pair.getValue();								
			writer.println(count + " " + pair.getKey().toString());
		}
		writer.println();
	}
	
	private static Map<Ngram, Integer> countNgrams(int n, String input) {
		Map<Ngram, Integer> counters = new HashMap<>();
		
		try (BufferedReader br = new BufferedReader(new FileReader(input))) {
			String line;

			while ((line = br.readLine()) != null) {
				String pattern = "[\\p{Punct}\\s]+";
				String[] line_words = line.split(pattern);
				int len = line_words.length;
				for (int i = -1; i < len+1; i++) {
					List<String> ngram_words = new ArrayList<String>();
					
					// what happens if the line is too short?
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
}
