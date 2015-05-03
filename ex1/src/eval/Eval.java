package eval;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Map;

import utils.Model;
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
		String text = null;
		model.calculateProplexity(text);
	}

	
	
	//TODO implement
	private static Model loadModel(String model_file) {		
		//TODO read and parse model file
		try (BufferedReader br = new BufferedReader(new FileReader(model_file))) {
			String line;
			while ((line = br.readLine()) != null) {
				// TODO Auto-generated method stub
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}