package it.iubar.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import it.iubar.json.validators.IValidator;
import it.iubar.json.validators.JustifyStrategy;

public class CliJsonValidator {
 
	private static final Logger LOGGER = Logger.getLogger(CliJsonValidator.class.getName());
	
	private static Options options = null;
	
	public static void main(String[] args) {
		
		// create the command line parser
		CommandLineParser parser = new DefaultParser();

		// create the Options
		CliJsonValidator.options = new Options();
		
	    try {
	        // parse the command line arguments
	        CommandLine line = parser.parse( options, args );
	        List<String> argList = line.getArgList();
	        CliJsonValidator cliValidator = new CliJsonValidator();
	        int errors = cliValidator.run(argList);
			if (errors > 0) {
				String errorMsg = "Found " + errors + " !"; 	
		    	die(errorMsg);
			}
	    }  catch( ParseException exp ) {    	
	    	String errorMsg = "The parsing has failed. Reason: " + exp.getMessage() ;
	    	die(errorMsg);
		} catch (FileNotFoundException e) {
			String errorMsg = "File not found: " + e.getMessage();
			die(errorMsg);
		}

	}
		
	private static void die(String errorMsg) {
    	LOGGER.log(Level.SEVERE, errorMsg);
        System.err.println(errorMsg);
        System.exit(1);
	}
	
	private static void handleWrongUsage(String msg, boolean b) {
		printAppNameAndVersion();
		System.out.println(msg);
		if(b) {
			System.out.println("");
			CliJsonValidator.printHelp();	
		}
		System.exit(1);
	}
	
	private static void printAppNameAndVersion() {
		// String className = PoJsonValidator.class.getName();
		Package mainPackage = CliJsonValidator.class.getPackage(); // returns "package it.iubar.json"
		String version = mainPackage.getImplementationVersion(); // returns "0.0.2"
		// String groupId = mainPackage.getName(); // returns "it.iubar.json"	 
		String appName = mainPackage.getImplementationTitle(); // returns "Po Json Validator"		  		
		System.out.println("");
		System.out.println(appName + " " + version);
		System.out.println("(an xpath content extractor)");
		System.out.println("");
	}
	
	private static void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.setWidth(200);
    	String jarName = new java.io.File(CliJsonValidator.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
    	String cmdLineSyntax = "java -jar " + jarName + " <schema.json> <data.json>|<folder path>";
	    formatter.printHelp(cmdLineSyntax, CliJsonValidator.options, true);	        
		 if(false) { 
			 StringWriter out = new StringWriter();
			 PrintWriter pw = new PrintWriter(out);	     
		     formatter.printUsage(pw, 80, cmdLineSyntax);   	     
		     pw.flush();
		     System.out.println(out.toString());
		 }     
	}
	
	private int run(List<String> argList) throws FileNotFoundException {  
		if (argList.size() != 2) {
			handleWrongUsage("[ERROR] Attention wrong number of arguments", true);
		}		
		String schemaPath = argList.get(0);
		File f1 = new File(schemaPath);
		if(!f1.isFile()) {
			handleWrongUsage("[ERROR] The file " + f1 + " does not exist or is not readable", false);
		}
		
		String targetPathOrFile = argList.get(1);
		File f2 = new File(targetPathOrFile);
		if (!f2.exists()) {
			handleWrongUsage("[ERROR] The path " + f2 + " does not exist or is not readable", false);
		}
		
		// STRATEGY 
		
		// IValidator strategy = new EveritStrategy();
		// IValidator strategy = new NetworkntStrategy();
		IValidator strategy = new JustifyStrategy(); 
		
		JsonValidator client = new JsonValidator(strategy);
		client.setSchema(f1);
		client.setTargetFolderOrFile(f2); 
		return client.run();
	}	

}
