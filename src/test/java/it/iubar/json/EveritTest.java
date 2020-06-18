package it.iubar.json;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.logging.Logger;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Test;

public class EveritTest {
	
	private static final Logger LOGGER = Logger.getLogger(EveritTest.class.getName());
	private static String schema1 = "/hello-schema.json";
	private static String schema2 = "/hello2-schema.json";

	@Test
	public void testEveritRelaxedSyntax()   {
		String strJson = "{\"hello\" : \"world\";}";
		assertDoesNotThrow(() -> parseStringWithTheEveritLib(schema1 , strJson));
	}
	
	@Test
	public void testEveritWrongSyntax()  {
		String strJson = "{\"hello\" : \"world\"\"}";
		Exception ex = assertThrows(Exception.class, () -> parseStringWithTheEveritLib(schema1, strJson));
		LOGGER.severe(ex.getMessage());
	}
	
	@Test
	public void testEveritRightSyntax()   {
		String strJson = "{\"hello\" : \"world\"}";
		assertDoesNotThrow(() -> parseStringWithTheEveritLib(schema1, strJson));
	}
	
	@Test
	public void testEveritRequired1()   {
		String strJson = "{\"missingKeyFromSchema\" : \"should pass\"}";
		Exception ex = assertThrows(Exception.class, () -> parseStringWithTheEveritLib(schema1, strJson));
		LOGGER.severe(ex.getMessage());
	}
	
	@Test
	public void testEveritAdditionalProperties1()   {
		String strJson = "{\"hello\" : \"world\", \"missingKeyFromSchema\" : \"should pass\"}";
		assertDoesNotThrow(() -> parseStringWithTheEveritLib(schema1, strJson));
	}	
	
	@Test
	public void testEveritRequired2()   {
		String strJson = "{\"missingKeyFromSchema2\" : \"should fail\"}";
		Exception ex = assertThrows(Exception.class, () -> parseStringWithTheEveritLib(schema2, strJson));
		LOGGER.severe(ex.getMessage());
	}
	
	@Test
	public void testEveritAdditionalProperties2()   {
		String strJson = "{\"hello\" : \"world\", \"missingKeyFromSchema\" : \"should pass\"}";
		Exception ex = assertThrows(Exception.class, () -> parseStringWithTheEveritLib(schema2, strJson));
		LOGGER.severe(ex.getMessage());
	}	
	
	@Test
	public void testEveritRightSyntax2()   {
		String strJson = "{\"hello\" : \"world\"}";
		assertDoesNotThrow(() -> parseStringWithTheEveritLib(schema2, strJson));
	}	
	
	private void parseStringWithTheEveritLib(String schemaPath, String strJson) throws FileNotFoundException {			
		JSONObject jsonToValidate = new JSONObject(strJson);
		JSONObject.testValidity(jsonToValidate);				
		File schemaFile = new File(JsonValidatorTest.class.getResource(schemaPath).getFile());
		InputStream inputStream = new FileInputStream(schemaFile);
		JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
		Schema schema = SchemaLoader.load(rawSchema);
		schema.validate(jsonToValidate); 
	}
	
}
