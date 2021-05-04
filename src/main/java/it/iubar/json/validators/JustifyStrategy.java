package it.iubar.json.validators;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.io.IOUtils;
import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonValidationService;
import org.leadpony.justify.api.Problem;
import org.leadpony.justify.api.ProblemHandler;

import jakarta.json.JsonReader;
import jakarta.json.stream.JsonParser;

public class JustifyStrategy extends RootStrategy {

	private static final Logger LOGGER = Logger.getLogger(JustifyStrategy.class.getName());

	@Override
	public int validate(File file) {
		return validate1(file);
	}

	public int validate(String string) {
		return validate1(string);
	}

	/**
	 * Esamino gli errori con JsonReader.
	 */	
	public int validate1(File file) {
		int errorCount = 0;
		JsonValidationService service = JsonValidationService.newInstance();

		// Reads the JSON schema
		JsonSchema schema = service.readSchema(Paths.get(this.schema.toString()));

		// Problem handler which will print problems found.
		// ProblemHandler handler = service.createProblemPrinter(System.out::println);
		MyProblemHandler handler = new MyProblemHandler();

		Path path = Paths.get(file.toString());
		// Reads the JSON instance by JsonReader
		try (JsonReader reader = service.createReader(path, schema, handler)) {
			try {
				reader.readValue();
				// Do something useful here
				// System.out.println("value: " + value); // prints the entire json content
				List<Problem> problems = handler.getProblems();
				if (problems != null) {
					errorCount = problems.size();
				}
			} catch (jakarta.json.stream.JsonParsingException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
				// eg: Invalid token=COMMA at (line no=40, column no=15, offset=911). Expected
				// tokens are: [CURLYOPEN, SQUAREOPEN, STRING, NUMBER, TRUE, FALSE, NULL]
				errorCount = 1;
			}
		}

		return errorCount;
	}

	/**
	 * Esamino gli errori con JsonReader.
	 */
	public int validate1(String string) {
		int errorCount = 0;
		JsonValidationService service = JsonValidationService.newInstance();

		// Reads the JSON schema
		JsonSchema schema = service.readSchema(Paths.get(this.schema.toString()));

		// Problem handler which will print problems found.
		// ProblemHandler handler = service.createProblemPrinter(System.out::println);
		MyProblemHandler handler = new MyProblemHandler();

		InputStream is = IOUtils.toInputStream(string, StandardCharsets.UTF_8);
		// Reads the JSON instance by JsonReader
		try (JsonReader reader = service.createReader(is, schema, handler)) {
			try {
				reader.readValue();
				// Do something useful here
				// System.out.println("value: " + value); // prints the entire json content
				List<Problem> problems = handler.getProblems();
				if (problems != null) {
					errorCount = problems.size();
				}
			} catch (jakarta.json.stream.JsonParsingException e) {
				LOGGER.log(Level.SEVERE, e.getMessage());
				// eg: Invalid token=COMMA at (line no=40, column no=15, offset=911). Expected
				// tokens are: [CURLYOPEN, SQUAREOPEN, STRING, NUMBER, TRUE, FALSE, NULL]
				errorCount = 1;
			}
		}

		return errorCount;
	}

	/**
	 * Esamino gli errori con JsonParser. E' poco utile.
	 */
	public boolean validate2(File file) {
		boolean b = false;
		JsonValidationService service = JsonValidationService.newInstance();

		// Reads the JSON schema
		JsonSchema schema = service.readSchema(Paths.get(this.schema.toString()));

		// Problem handler which will print problems found.
		ProblemHandler handler = service.createProblemPrinter(System.out::println);

		Path path = Paths.get(file.toString());
		// Parses the JSON instance by JsonParser
		try (JsonParser parser = service.createParser(path, schema, handler)) {
			while (parser.hasNext()) {
				JsonParser.Event event = parser.next();
				// Do something useful here
				System.out.println("event: " + event);
			}
			b = true;
		}
		return b;
	}

}
