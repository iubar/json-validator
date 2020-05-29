package it.iubar.json_validator;

import java.io.File;
import java.io.FileNotFoundException;



public class PoTestJsonValidator {
	
	
	
	public static void main(String[] args) throws FileNotFoundException {

		//String[] temp = new String[2];
		//temp[0] = "C:\\Users\\iubar\\Desktop\\Progetti\\po-test-json-validator\\src\\main\\resources\\json\\schema.json";
		//temp[1] = "C:\\Users\\iubar\\Desktop\\Progetti\\po-test-json-validator\\src\\test\\resources\\json";
		
		if (args.length != 2) {
			System.out.println("[ERROR] Attenzione, le due directory non sono state inserite correttamente!");
			showHelp();
			System.exit(1);
		}
		
		String schemaPath = args[0];
		File f1 = new File(schemaPath);
		if(!f1.isFile()) {
			System.out.println("[ERROR] Attenzione, errore nell'inserimento del file schema.json!");
			System.exit(1);
		}
		
		String targetPathOrFile = args[1];
		File f2 = new File(targetPathOrFile);
		if (!f2.exists()) {
			System.out.println("[ERROR] Attenzione, '" + targetPathOrFile + "' non esiste");
			System.exit(1);
		}
		
		Validator client = new Validator();
		client.setSchema(f1);
		client.setTargetFolderOrFile(f2);
		client.run();
	}
	
	public static void showHelp() {
		System.out.println("[HELP] Ecco come fare: java -jar applicativo.jar <schema> <test>");
		System.out.println("[HELP] <schema> = percorso"+File.separator+"del"+File.separator+"file"+File.separator+"schema.json oppure json-hyper-schema");
		System.out.println("[HELP] <test> = percorso"+File.separator+"della"+File.separator+"cartella"+File.separator+"tests oppure schema.json");
	}
	

}
