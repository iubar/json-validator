package it.iubar.json.other;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonValidationService;
import org.leadpony.justify.api.ProblemHandler;

import jakarta.json.JsonReader;
import jakarta.json.JsonValue;
import jakarta.json.stream.JsonParser;

public class JustifyStrategy extends RootStrategy  {

	@Override
	public boolean validate(File file) {
		return validate1(file);
	}
	
	public boolean validate1(File file) {
		JsonValidationService service = JsonValidationService.newInstance();

		// Reads the JSON schema
		JsonSchema schema = service.readSchema(Paths.get(this.schema.toString()));

		// Problem handler which will print problems found.
		ProblemHandler handler = service.createProblemPrinter(System.out::println);

		Path path = Paths.get(file.toString());
		// Reads the JSON instance by JsonReader
		try (JsonReader reader = service.createReader(path, schema, handler)) {
		    JsonValue value = reader.readValue();
		    // Do something useful here
		    System.out.println("value: " + value);
		}
		return false;
	}
	
	public boolean validate2(File file) {
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
		}
		return false;
	}
 

}
