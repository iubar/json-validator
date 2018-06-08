package it.iubar.json_validator;

import java.io.File;

public class PoTestJsonValidator {
	
	public static void main(String[] args) {
		if (args.length != 2) {
			// visualizzare l'help
			System.exit(1);
		}
		
		String schemaPath = args[0];
		File f1 = new File(schemaPath);
		if(!f1.isFile()) {
			/// visualizzare errore
			System.exit(1);
		}
		
		String targetPath = args[1];
		File f2 = new File(targetPath);
		if(!f2.isDirectory()) {
			/// visualizzare errore
			System.exit(1);
		}
		
		Validator client = new Validator();
		//client.setSchema(...);
		//client.setTargetFolder(...);
		client.run();
	}
}
