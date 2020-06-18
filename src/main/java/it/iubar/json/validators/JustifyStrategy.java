package it.iubar.json.validators;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonValidationService;
import org.leadpony.justify.api.ProblemHandler;
import org.leadpony.justify.api.SpecVersion;

import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.json.spi.JsonProvider;
import jakarta.json.stream.JsonParser;

public class JustifyStrategy extends RootStrategy  {

	private static final Logger LOGGER = Logger.getLogger(JustifyStrategy.class.getName());
	
	@Override
	public int validate(File file) {
		return validate1(file);
	}
	
	public int validate1(File file)  {
		int errorCount = 0;
		JsonValidationService service = JsonValidationService.newInstance();

		// Reads the JSON schema
		JsonSchema schema = service.readSchema(Paths.get(this.schema.toString()));

		// Problem handler which will print problems found.
		//ProblemHandler handler = service.createProblemPrinter(System.out::println);
		MyProblemHandler handler = new MyProblemHandler();

		Path path = Paths.get(file.toString());
		// Reads the JSON instance by JsonReader
		try (JsonReader reader = service.createReader(path, schema, handler)) {
			try {
				JsonValue value = reader.readValue();
			    // Do something useful here
			    // System.out.println("value: " + value); // prints the entire json content
				List<String> problems = handler.getProblems2();
				if(problems!=null) {
					errorCount = problems.size();
				}				
			}catch (jakarta.json.stream.JsonParsingException e) {
				LOGGER.severe(e.getMessage());
				 // eg: Invalid token=COMMA at (line no=40, column no=15, offset=911). Expected tokens are: [CURLYOPEN, SQUAREOPEN, STRING, NUMBER, TRUE, FALSE, NULL]
				errorCount = 1;
			}
		}
	
		return errorCount;
	}
	
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
