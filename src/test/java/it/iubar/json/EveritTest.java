package it.iubar.json;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class EveritTest {
	

	@Test
	public void testEveritLib1()   {
		String strJson = "{\"hello\" : \"world\";}";
		assertDoesNotThrow(() -> parseStringWithTheEveritLib(strJson));
	}
	
	@Test
	public void testEveritLib2()  {
		String strJson = "{\"hello\" : \"world\"\"}";
		assertThrows(Exception.class, () -> parseStringWithTheEveritLib(strJson));	 
	}
	
	private void parseStringWithTheEveritLib(String strJson) throws FileNotFoundException {			
		JSONObject jsonToValidate = new JSONObject(strJson);
		JSONObject.testValidity(jsonToValidate);				
		File schemaFile = new File(JsonValidatorTest.class.getResource("/hello-schema.json").getFile());
		InputStream inputStream = new FileInputStream(schemaFile);
		JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
		Schema schema = SchemaLoader.load(rawSchema);
		schema.validate(jsonToValidate); 
	}
	
}
