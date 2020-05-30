package it.iubar.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class PoJsonValidator {
 
	private static Options options = null;
	
	public static void main(String[] args) throws FileNotFoundException {
		
		// create the command line parser
		CommandLineParser parser = new DefaultParser();

		// create the Options
		PoJsonValidator.options = new Options();
		
	    try {
	        // parse the command line arguments
	        CommandLine line = parser.parse( options, args );
	        List<String> argList = line.getArgList();
	        PoJsonValidator validator = new PoJsonValidator();
	        validator.run(argList);
	    }
	    catch( ParseException exp ) {
	        // oops, something went wrong
	        System.err.println( "The paarsing has failed. Reason: " + exp.getMessage() );
	    }

	}
	
	
	private void run(List<String> argList) {  
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
		
		Validator client = new Validator();
		client.setSchema(f1);
		client.setTargetFolderOrFile(f2);
		try {
			client.run();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	private static void handleWrongUsage(String msg, boolean b) {
		printAppNameAndVersion();
		System.out.println(msg);
		if(b) {
			System.out.println("");
			PoJsonValidator.printHelp();	
		}
		System.exit(1);
	}
	
	private static void printAppNameAndVersion() {
		// String className = PoJsonValidator.class.getName();
		Package mainPackage = PoJsonValidator.class.getPackage(); // returns "package it.iubar.json"
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
    	String jarName = new java.io.File(PoJsonValidator.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
    	String cmdLineSyntax = "java -jar " + jarName + " <schema.json> <data.json>|<folder path>";
	    formatter.printHelp(cmdLineSyntax, PoJsonValidator.options, true);	        
		 if(false) { 
			 StringWriter out = new StringWriter();
			 PrintWriter pw = new PrintWriter(out);	     
		     formatter.printUsage(pw, 80, cmdLineSyntax);   	     
		     pw.flush();
		     System.out.println(out.toString());
		 }     
	}

}
