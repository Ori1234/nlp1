package eval;

import java.util.Map;

import ex1.Utils;

public class Eval {

	public static void main(String[] args) {
		Map<String, String> params = Utils.parseARGS(args); // sets glabals
		
		String input = params.get("-i");
		if (input==null){
            throw new IllegalArgumentException("missing required flag -i");
		}
		String output = params.get("-m");
		if (output==null){
            throw new IllegalArgumentException("missing required flag -m");
		}
		
		
		

	}

}
