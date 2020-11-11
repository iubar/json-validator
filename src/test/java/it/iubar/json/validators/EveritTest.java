package it.iubar.json.validators;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Logger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class EveritTest {

	private static final Logger LOGGER = Logger.getLogger(EveritTest.class.getName());

	private static final String SCHEMA1 = "/hello-schema.json"; // "additionalProperties": true, "required": ...
	private static final String SCHEMA2 = "/hello2-schema.json"; // "additionalProperties": false, "required": ...

	private int parseWithTheEveritLib(String schemaPath, String strJson) throws FileNotFoundException {
		EveritStrategy strategy = new EveritStrategy();
		File schemaFile = new File(EveritTest.class.getResource(schemaPath).getFile());
		strategy.setSchema(schemaFile);
		return strategy.validate(strJson);

	}

	@Test
	public void testEveritAdditionalProperties1() {
		String strJson = "{\"hello\" : \"world\", \"missingKeyFromSchema\" : \"should pass because additionalProperties is true\"}";
		int errorCount = Assertions.assertDoesNotThrow(() -> parseWithTheEveritLib(EveritTest.SCHEMA1, strJson));
		Assertions.assertEquals(0, errorCount);
	}

	@Test
	public void testEveritAdditionalProperties2() {
		String strJson = "{\"hello\" : \"world\", \"missingKeyFromSchema\" : \"should fail because additionalProperties is false\"}";
		int errorCount = Assertions.assertDoesNotThrow(() -> parseWithTheEveritLib(EveritTest.SCHEMA2, strJson));
		Assertions.assertEquals(1, errorCount);
	}

	@Test
	public void testEveritRequired1() {
		String strJson = "{\"missingKeyFromSchema\" : \"should fail because the hello key is required\"}";
		int errorCount = Assertions.assertDoesNotThrow(() -> parseWithTheEveritLib(EveritTest.SCHEMA1, strJson));
		Assertions.assertEquals(1, errorCount);
	}

	@Test
	public void testEveritRequired2() {
		String strJson = "{\"missingKeyFromSchema2\" : \"should fail because the hello key is required and additionalProperties is false \"}";
		int errorCount = Assertions.assertDoesNotThrow(() -> parseWithTheEveritLib(EveritTest.SCHEMA2, strJson));
		Assertions.assertEquals(2, errorCount);
	}

	@Test
	public void testEveritRightSyntax2() {
		String strJson = "{\"hello\" : \"world\"}";
		Assertions.assertDoesNotThrow(() -> parseWithTheEveritLib(EveritTest.SCHEMA1, strJson));
	}

	@Test
	public void testEveritRightSyntax3() {
		String strJson = "{\"hello\" : \"world\"}";
		Assertions.assertDoesNotThrow(() -> parseWithTheEveritLib(EveritTest.SCHEMA2, strJson));
	}

	@Test
	public void testEveritWrongSyntax2() {
		String strJson = "{\"hello\" : \"world\"\"}"; // wrong syntax
		int errorCount = Assertions.assertDoesNotThrow(() -> parseWithTheEveritLib(EveritTest.SCHEMA1, strJson));
		Assertions.assertEquals(1, errorCount);
	}

	@Test
	public void testRelaxedSyntax2() {
		String strJson = "{\"hello\" : \"world\";}"; // relaxed syntax
		Assertions.assertDoesNotThrow(() -> parseWithTheEveritLib(EveritTest.SCHEMA1, strJson));
	}
}
