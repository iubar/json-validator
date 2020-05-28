package it.iubar.helloworld;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Test;

import it.iubar.json_validator.Validator;

public class ValidatorTest {

	private static final File schemaFile = new File(ValidatorTest.class.getResource("/schema.json").getFile());
	
	@Test
	public void testValidator1() throws IOException {
		int errors = parseWithValidator(new File(ValidatorTest.class.getResource("/validation-1.json").getFile()));
		assertEquals(0, errors);
	}
	
	@Test
	public void testValidator2() throws IOException {
		int errors = parseWithValidator(new File(ValidatorTest.class.getResource("/validation-2.json").getFile()));
		assertEquals(0, errors);
	}
	
	@Test
	public void testLib1()   {
		String strJson = "{\"hello\" : \"world\";}";
		assertDoesNotThrow(() -> parseWithLib(strJson));
	}
	
	@Test
	public void testLib2()  {
		String strJson = "{\"hello\" : \"world\"\"}";
		assertThrows(Exception.class, () -> parseWithLib(strJson));	 
	}
	
	private void parseWithLib(String strJson) throws FileNotFoundException {			
		JSONObject jsonToValidate = new JSONObject(strJson);
		JSONObject.testValidity(jsonToValidate);
		
		InputStream inputStream = new FileInputStream(ValidatorTest.schemaFile);
		JSONObject rawSchema = new JSONObject(new JSONTokener(inputStream));
		Schema schema = SchemaLoader.load(rawSchema);
		schema.validate(jsonToValidate); 
	}
	
	private int parseWithValidator(File file) {		
		Validator client = new Validator();
		client.setSchema(ValidatorTest.schemaFile);
		client.setTargetFolderOrFile(file);
		
		return client.run();
	}
	
}
