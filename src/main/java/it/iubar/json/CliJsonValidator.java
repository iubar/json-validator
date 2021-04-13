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

	private static void die(String errorMsg) {
		CliJsonValidator.LOGGER.log(Level.SEVERE, errorMsg);
		System.err.println(errorMsg);
		System.exit(1);
	}

	private static void handleWrongUsage(String msg, boolean b) {
		CliJsonValidator.printAppNameAndVersion();
		System.out.println(msg);
		if (b) {
			System.out.println("");
			CliJsonValidator.printHelp();
		}
		System.exit(1);
	}

	public static void main(String[] args) {

		// create the command line parser
		CommandLineParser parser = new DefaultParser();

		// create the Options
		CliJsonValidator.options = new Options();

		try {
			// parse the command line arguments
			CommandLine line = parser.parse(CliJsonValidator.options, args);
			List<String> argList = line.getArgList();
			CliJsonValidator cliValidator = new CliJsonValidator();
			int errors = cliValidator.run(argList);
			if (errors > 0) {
				String errorMsg = "Found " + errors + " !";
				CliJsonValidator.die(errorMsg);
			}
		} catch (ParseException exp) {
			String errorMsg = "The parsing has failed. Reason: " + exp.getMessage();
			CliJsonValidator.die(errorMsg);
		} catch (FileNotFoundException e) {
			String errorMsg = "File not found: " + e.getMessage();
			CliJsonValidator.die(errorMsg);
		}

	}

	/**
	 * Il metodo non restituisce le informazioni corrette quando il codice è eseguito all'intero dell'IDE
	 * Diversamente quando l'applicazione è pacchettizata in un jar, il risultato è quello atteso
	 */	
	private static void printAppNameAndVersion() {
		// String className = PoJsonValidator.class.getName();
		Package mainPackage = CliJsonValidator.class.getPackage(); // returns "package it.iubar.json"
		String version = mainPackage.getImplementationVersion(); // returns "0.0.2"
		// String groupId = mainPackage.getName(); // returns "it.iubar.json"
		String appName = mainPackage.getImplementationTitle(); // returns "Po Json Validator"
		System.out.println("");
		System.out.println(appName + " " + version);
		System.out.println("(json validator tool)");
		System.out.println("");
	}

	private static void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
		formatter.setWidth(200);
		String jarName = new java.io.File(CliJsonValidator.class.getProtectionDomain().getCodeSource().getLocation().getPath()).getName();
		String cmdLineSyntax = "java -jar " + jarName + " <schema.json> <data.json>|<folder path>";
		formatter.printHelp(cmdLineSyntax, CliJsonValidator.options, true);
		if (false) {
			StringWriter out = new StringWriter();
			PrintWriter pw = new PrintWriter(out);
			formatter.printUsage(pw, 80, cmdLineSyntax);
			pw.flush();
			System.out.println(out.toString());
		}
	}

	private int run(List<String> argList) throws FileNotFoundException {
		if (argList.size() != 2) {
			CliJsonValidator.handleWrongUsage("[ERROR] Attention wrong number of arguments", true);
		}
		String schemaPath = argList.get(0);
		File f1 = new File(schemaPath);
		if (!f1.isFile()) {
			CliJsonValidator.handleWrongUsage("[ERROR] The file " + f1 + " does not exist or is not readable", false);
		}

		String targetPathOrFile = argList.get(1);
		File f2 = new File(targetPathOrFile);
		if (!f2.exists()) {
			CliJsonValidator.handleWrongUsage("[ERROR] The path " + f2 + " does not exist or is not readable", false);
		}
		 
		IValidator strategy = factoryStrategy();

		JsonValidator client = new JsonValidator(strategy);
		client.setSchema(f1);
		client.setTargetFolderOrFile(f2);
		return client.run();
	}

	private IValidator factoryStrategy() {
		// STRATEGIES:
		// IValidator strategy = new EveritStrategy(); 		// https://github.com/everit-org/json-schema
		// IValidator strategy = new NetworkntStrategy(); 	// https://github.com/networknt/json-schema-validator
		// IValidator strategy = new JustifyStrategy();		// https://github.com/leadpony/justify
		IValidator strategy = new JustifyStrategy();
		return strategy;
	}

}
