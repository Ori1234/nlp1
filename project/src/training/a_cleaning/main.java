package training.a_cleaning;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;

public class main {

	public static void main(String[] args) throws FileNotFoundException,
			IOException {
		
		//TODO change to parameters to the program
		int limit = 60;

		String path = "data\\europarl.en.devel";
		String output = "data\\europarl.en.devel_output";

		BufferedReader br = new BufferedReader(new FileReader(path));
		PrintWriter writer = new PrintWriter(output, "UTF-8");

		
		String line;
		while ((line = br.readLine()) != null) {
			String[] tokens = line.split("[\\p{Punct}\\s]");
			if (tokens.length > limit)
				continue;
			writer.println(line);
		}
		br.close();
		writer.close();
		
		System.out.println(output);
	}
}
