package it.iubar.json;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class EveritTest {
	
	private static final Logger LOGGER = Logger.getLogger(EveritTest.class.getName());
	
	private static String schema1 = "/hello-schema.json"; // "additionalProperties": true, "required": ... 
	private static String schema2 = "/hello2-schema.json"; // "additionalProperties": false, "required": ... 

	@Test
	public void testRelaxedSyntax()   {
		String strJson = "{\"hello\" : \"world\";}";  // relaxed syntax
		assertDoesNotThrow(() -> parseWithJSONObject(schema1 , strJson));
	}
	
	@Test
	public void testWrongSyntax()  {
		String strJson = "{\"hello\" : \"world\"\"}"; // wrong syntax
		JSONException ex = Assertions.assertThrows(JSONException.class, () -> parseWithJSONObject(schema1, strJson));
		LOGGER.severe(ex.getMessage());
	}
	
	@Test
	public void testRightSyntax()   {
		String strJson = "{\"hello\" : \"world\"}";
		assertDoesNotThrow(() -> parseWithJSONObject(schema1, strJson));
	}
	
	@Test
	public void testRelaxedSyntax2()   {
		String strJson = "{\"hello\" : \"world\";}";  // relaxed syntax
		assertDoesNotThrow(() -> parseWithTheEveritLib(schema1 , strJson));
	}
	
	@Test
	public void testEveritWrongSyntax2()  {
		String strJson = "{\"hello\" : \"world\"\"}";  // wrong syntax
		JSONException ex = Assertions.assertThrows(JSONException.class, () -> parseWithTheEveritLib(schema1, strJson));
		LOGGER.severe(ex.getMessage());
	}
	
	@Test
	public void testEveritRightSyntax2()   {
		String strJson = "{\"hello\" : \"world\"}";
		assertDoesNotThrow(() -> parseWithTheEveritLib(schema1, strJson));
	}
			
	@Test
	@Disabled("La libreria Everit fallisce nel valutare la direttiva \"required\"")
	public void testEveritRequired1()   {
		String strJson = "{\"missingKeyFromSchema\" : \"should fail because the hello key is required\"}";
		int errorCount = assertDoesNotThrow(() -> parseWithTheEveritLib(schema1, strJson));
		assertEquals(1, errorCount);
	}
	
	@Test
	public void testEveritAdditionalProperties1()   {
		String strJson = "{\"hello\" : \"world\", \"missingKeyFromSchema\" : \"should pass because additionalProperties is true\"}";
		int errorCount = assertDoesNotThrow(() -> parseWithTheEveritLib(schema1, strJson));
		assertEquals(0, errorCount);		
	}	
	
	@Test
	@Disabled("La libreria Everit fallisce nel valutare la direttiva \"additionalProperties\"")		
	public void testEveritRequired2()   {
		String strJson = "{\"missingKeyFromSchema2\" : \"should fail because the hello key is required additionalProperties is false \"}";
		int errorCount = assertDoesNotThrow(() -> parseWithTheEveritLib(schema2, strJson));
		assertEquals(1, errorCount);
	}
	
	@Test
	@Disabled("La libreria Everit fallisce nel valutare la direttiva \"additionalProperties\"")	
	public void testEveritAdditionalProperties2()   {
		String strJson = "{\"hello\" : \"world\", \"missingKeyFromSchema\" : \"should fail because additionalProperties is false\"}";
		int errorCount = assertDoesNotThrow(() -> parseWithTheEveritLib(schema2, strJson));
		assertEquals(1, errorCount);
	}	
	
	@Test
	public void testEveritRightSyntax3()   {
		String strJson = "{\"hello\" : \"world\"}";
		assertDoesNotThrow(() -> parseWithTheEveritLib(schema2, strJson));
	}	

	private Schema getSchema(String schemaPath) throws FileNotFoundException {
		File schemaFile = new File(ComparatedValidatorsTest.class.getResource(schemaPath).getFile());
		InputStream inputStream = new FileInputStream(schemaFile);
		JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
		Schema schema = SchemaLoader.load(rawSchema);
		return schema;
	}
	
	private Schema getSchema2(String schemaPath) throws FileNotFoundException {
 		InputStream inputStream = new FileInputStream(schemaPath);
		JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));		
		SchemaLoader loader = SchemaLoader.builder().schemaJson(rawSchema).draftV7Support().build(); // Draft V7
		Schema schema = loader.load().build();
		return schema;
	}

	
	private void parseWithJSONObject(String schemaPath, String strJson) throws FileNotFoundException {			
		JSONObject jsonToValidate = new JSONObject(strJson);
		JSONObject.testValidity(jsonToValidate);				
		Schema schema = getSchema(schemaPath);  
		schema.validate(jsonToValidate); 
	}
	
	private int parseWithTheEveritLib(String schemaPath, String strJson) throws FileNotFoundException {
		JSONObject jsonToValidate = new JSONObject(strJson);
		Schema schema = getSchema(schemaPath); 

		try {
			org.everit.json.schema.Validator validator = org.everit.json.schema.Validator.builder().failEarly().build();
			validator.performValidation(schema, jsonToValidate);
 
		} catch (ValidationException e) {
			LOGGER.severe(e.getMessage());
			
			e.getCausingExceptions().stream().map(ValidationException::getMessage).forEach(System.out::println);

			List<ValidationException> list = e.getCausingExceptions();
			for (ValidationException validationException : list) {
				String violation = validationException.getPointerToViolation();
				String[] parts = violation.split("/");
				String name = parts[parts.length-1];
				int count = validationException.getViolationCount();
				LOGGER.severe("Object name: " + name);
				LOGGER.severe("Violation: " + count);
				List<String> messages = validationException.getAllMessages();
				for (String message : messages) {
					LOGGER.severe("	- " + message);
				}
			}	
			return list.size();
		}
		return 0;	
	}
}
