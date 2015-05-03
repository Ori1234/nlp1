package ex1;

import java.util.HashMap;
import java.util.Map;

public class Utils {
	
	
	
	public static Map<String, String> parseARGS(String[] args) {
		// a structure to hold the command line params
		Map<String, String> params = new HashMap<String, String>();
		// go over params
		for (int i = 0; i < args.length; i++) {
			switch (args[i].charAt(0)) {
	        case '-':
	            if (args[i].length() < 2) {
	                throw new IllegalArgumentException("Not a valid argument: "+args[i]);
	            } else {
	                if (args.length-1 == i)
	                    throw new IllegalArgumentException("Expected arg after: "+args[i]);
	                // -opt
	                params.put(args[i], args[i+1]);
	                i++;
	            }
	            break;
			}
		}		
		return params;
	}

}
