package it.iubar.json.validators;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Logger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class SkemaTest {

	private static final Logger LOGGER = Logger.getLogger(SkemaTest.class.getName());

	private static final String SCHEMA1 = "/hello-schema.json"; // "additionalProperties": true, "required": ...
	private static final String SCHEMA2 = "/hello2-schema.json"; // "additionalProperties": false, "required": ...

	private int parseWithTheSkemaLib(String schemaPath, String strJson)  {
		SkemaStrategy strategy = new SkemaStrategy();
		File schemaFile = new File(SkemaTest.class.getResource(schemaPath).getFile());
		strategy.setSchema(schemaFile);
		try {
			return strategy.validate(strJson);
		} catch (FileNotFoundException e) {
			fail(e.getMessage());
			return 0;
		}
	}

	@Test
	public void testEveritAdditionalProperties1() {
		String strJson = "{\"hello\" : \"world\", \"missingKeyFromSchema\" : \"should pass because additionalProperties is true\"}";
		int errorCount =  parseWithTheSkemaLib(SkemaTest.SCHEMA1, strJson);
		Assertions.assertEquals(0, errorCount);
	}

	@Test
	public void testEveritAdditionalProperties2() {
		String strJson = "{\"hello\" : \"world\", \"missingKeyFromSchema\" : \"should fail because additionalProperties is false\"}";
		int errorCount = parseWithTheSkemaLib(SkemaTest.SCHEMA2, strJson);
		Assertions.assertEquals(1, errorCount);
	}

	@Test
	public void testEveritRequired1() {
		String strJson = "{\"missingKeyFromSchema\" : \"should fail because the hello key is required\"}";
		int errorCount = parseWithTheSkemaLib(SkemaTest.SCHEMA1, strJson);
		Assertions.assertEquals(1, errorCount);
	}

	@Test
	public void testEveritRequired2() {
		String strJson = "{\"missingKeyFromSchema2\" : \"should fail because the hello key is required and additionalProperties is false \"}";
		int errorCount = parseWithTheSkemaLib(SkemaTest.SCHEMA2, strJson);
		Assertions.assertNotEquals(0, errorCount);
	}

	@Test
	public void testEveritRightSyntax2() {
		String strJson = "{\"hello\" : \"world\"}";
		Assertions.assertEquals(0, parseWithTheSkemaLib(SkemaTest.SCHEMA1, strJson));
	}

	@Test
	public void testEveritRightSyntax3() {
		String strJson = "{\"hello\" : \"world\"}";
		Assertions.assertEquals(0, parseWithTheSkemaLib(SkemaTest.SCHEMA2, strJson));
	}

	@Test
	public void testEveritWrongSyntax2() {
		String strJson = "{\"hello\" : \"world\"\"}"; // wrong syntax
		Assertions.assertNotEquals(0, parseWithTheSkemaLib(SkemaTest.SCHEMA1, strJson)); 
	}

	@Test
	public void testRelaxedSyntax2() {
		String strJson = "{\"hello\" : \"world\";}"; // relaxed syntax
		Assertions.assertEquals(1, parseWithTheSkemaLib(SkemaTest.SCHEMA1, strJson));
	}
	
}
