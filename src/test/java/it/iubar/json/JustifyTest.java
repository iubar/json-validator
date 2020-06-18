package it.iubar.json;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.input.CharSequenceReader;
import org.junit.jupiter.api.Test;
import org.leadpony.justify.api.JsonSchema;
import org.leadpony.justify.api.JsonValidationService;

import it.iubar.json.validators.MyProblemHandler;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

public class JustifyTest {
	
	private static final Logger LOGGER = Logger.getLogger(JustifyTest.class.getName());
	
	private static String schema1 = "/hello-schema.json"; // "additionalProperties": true, "required": ... 
	private static String schema2 = "/hello2-schema.json"; // "additionalProperties": false, "required": ... 
 
 
	
	@Test
	public void testRelaxedSyntax()   {
		String strJson = "{\"hello\" : \"world\";}";  // relaxed syntax not permitted
		int errorCount = assertDoesNotThrow(() -> parseWithTheJustifyLib(schema1 , strJson));
		assertEquals(1, errorCount);
	}
	
	@Test
	public void testJustifyWrongSyntax()  {
		String strJson = "{\"hello\" : \"world\"\"}";  // wrong syntax
		int errorCount = assertDoesNotThrow(() -> parseWithTheJustifyLib(schema1, strJson));
		assertEquals(1, errorCount);
	}
	
	@Test
	public void testJustifyRightSyntax1()   {
		String strJson = "{\"hello\" : \"world\"}";
		int errorCount = assertDoesNotThrow(() -> parseWithTheJustifyLib(schema1, strJson));
		assertEquals(0, errorCount);
	}
			
	@Test
	public void testJustifyRequired1()   {
		String strJson = "{\"missingKeyFromSchema\" : \"should fail because the hello key is required\"}";
		int errorCount = assertDoesNotThrow(() -> parseWithTheJustifyLib(schema1, strJson));
		assertEquals(1, errorCount);
	}
	
	@Test
	public void testJustifyAdditionalProperties1()   {
		String strJson = "{\"hello\" : \"world\", \"missingKeyFromSchema\" : \"should pass because additionalProperties is true\"}";
		int errorCount = assertDoesNotThrow(() -> parseWithTheJustifyLib(schema1, strJson));
		assertEquals(0, errorCount);
	}	
	
	@Test
	public void testJustifyRequired2()   {
		String strJson = "{\"missingKeyFromSchema2\" : \"should fail because the hello key is required and additionalProperties is false\"}";
		int errorCount = assertDoesNotThrow(() -> parseWithTheJustifyLib(schema2, strJson));
		assertEquals(2, errorCount);
	}
	
	@Test
	public void testJustifyAdditionalProperties2()   {
		String strJson = "{\"hello\" : \"world\", \"missingKeyFromSchema\" : \"should fail  because additionalProperties is false\"}";
	 	int errorCount = assertDoesNotThrow(() -> parseWithTheJustifyLib(schema2, strJson));
	 	assertEquals(1, errorCount);
	}	
	
	@Test
	public void testJustifyRightSyntax2()   {
		String strJson = "{\"hello\" : \"world\"}";
		int errorCount = assertDoesNotThrow(() -> parseWithTheJustifyLib(schema2, strJson));
		assertEquals(0, errorCount);
	}	
 
	private int parseWithTheJustifyLib(String schemaPath, String strJson) throws FileNotFoundException {
		int errorCount = 0;
		JsonValidationService service = JsonValidationService.newInstance();
 
		File schemaFile = new File(JustifyTest.class.getResource(schemaPath).getFile());
		InputStream inputStream = new FileInputStream(schemaFile);
		
		// Reads the JSON schema
		JsonSchema schema = service.readSchema(inputStream);
		 
		Reader strReader = new CharSequenceReader(strJson);
		
		// Problem handler which will print problems found.
		// ProblemHandler handler2 = service.createProblemPrinter(System.out::println);
		MyProblemHandler handler = new MyProblemHandler();
		// Reads the JSON instance by JsonReader
		try (JsonReader reader = service.createReader(strReader, schema, handler)) {
			try {
				JsonValue value = reader.readValue();
			    // Do something useful here
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
}
