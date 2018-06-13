package it.iubar.json_validator;

import java.io.File;



public class PoTestJsonValidator {
	
	
	
	public static void main(String[] args) {

		String[] temp = new String[2];
		temp[0] = "C:\\Users\\iubar\\Desktop\\Progetti\\po-test-json-validator\\src\\main\\resources\\json\\schema.json";
		temp[1] = "C:\\Users\\iubar\\Desktop\\Progetti\\po-test-json-validator\\src\\test\\resources\\json";
		
		if (temp.length != 2) {
			System.out.println("[ERROR] Attenzione, le due directory non sono state inserite correttamente!");
			showHelp();
			System.exit(1);
		}
		
		String schemaPath = temp[0];
		File f1 = new File(schemaPath);
		if(!f1.isFile()) {
			System.out.println("[ERROR] Attenzione, errore nell'inserimento del file schema.json!");
			System.exit(1);
		}
		
		String targetPath = temp[1];
		File f2 = new File(targetPath);
		if(!f2.isDirectory()) {
			System.out.println("[ERROR] Attenzione, errore nell'inserimento del percorso della cartella test!");
			System.exit(1);
		}
		
		Validator client = new Validator();
		client.setSchema(f1);
		client.setTargetFolder(f2);
		client.run();
	}
	
	public static void showHelp() {
		System.out.println("[HELP] Ecco come fare: java -jar applicativo.jar <schema> <test>");
		System.out.println("[HELP] <schema> = percorso/del/file/schema.json");
		System.out.println("[HELP] <test> = percorso/della/cartella/tests");
	}
	

}
