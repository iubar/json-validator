package it.iubar.json.validators;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Logger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class JustifyTest {

	private static final Logger LOGGER = Logger.getLogger(JustifyTest.class.getName());

	private static final String SCHEMA1 = "/hello-schema.json"; // "additionalProperties": true, "required": ...
	private static final String SCHEMA2 = "/hello2-schema.json"; // "additionalProperties": false, "required": ...

	private int parseWithTheJustifyLib(String schemaPath, String strJson) throws FileNotFoundException {
		JustifyStrategy strategy = new JustifyStrategy();
		File schemaFile = new File(JustifyTest.class.getResource(schemaPath).getFile());
		strategy.setSchema(schemaFile);
		return strategy.validate(strJson);
	}

	@Test
	public void testJustifyAdditionalProperties1() {
		String strJson = "{\"hello\" : \"world\", \"missingKeyFromSchema\" : \"should pass because additionalProperties is true\"}";
		int errorCount = Assertions.assertDoesNotThrow(() -> parseWithTheJustifyLib(JustifyTest.SCHEMA1, strJson));
		Assertions.assertEquals(0, errorCount);
	}

	@Test
	public void testJustifyAdditionalProperties2() {
		String strJson = "{\"hello\" : \"world\", \"missingKeyFromSchema\" : \"should fail  because additionalProperties is false\"}";
		int errorCount = Assertions.assertDoesNotThrow(() -> parseWithTheJustifyLib(JustifyTest.SCHEMA2, strJson));
		Assertions.assertEquals(1, errorCount);
	}

	@Test
	public void testJustifyRequired1() {
		String strJson = "{\"missingKeyFromSchema\" : \"should fail because the hello key is required\"}";
		int errorCount = Assertions.assertDoesNotThrow(() -> parseWithTheJustifyLib(JustifyTest.SCHEMA1, strJson));
		Assertions.assertEquals(1, errorCount);
	}

	@Test
	public void testJustifyRequired2() {
		String strJson = "{\"missingKeyFromSchema2\" : \"should fail because the hello key is required and additionalProperties is false\"}";
		int errorCount = Assertions.assertDoesNotThrow(() -> parseWithTheJustifyLib(JustifyTest.SCHEMA2, strJson));
		Assertions.assertEquals(2, errorCount);
	}

	@Test
	public void testJustifyRightSyntax1() {
		String strJson = "{\"hello\" : \"world\"}";
		int errorCount = Assertions.assertDoesNotThrow(() -> parseWithTheJustifyLib(JustifyTest.SCHEMA1, strJson));
		Assertions.assertEquals(0, errorCount);
	}

	@Test
	public void testJustifyRightSyntax2() {
		String strJson = "{\"hello\" : \"world\"}";
		int errorCount = Assertions.assertDoesNotThrow(() -> parseWithTheJustifyLib(JustifyTest.SCHEMA2, strJson));
		Assertions.assertEquals(0, errorCount);
	}

	@Test
	public void testJustifyWrongSyntax() {
		String strJson = "{\"hello\" : \"world\"\"}"; // wrong syntax
		int errorCount = Assertions.assertDoesNotThrow(() -> parseWithTheJustifyLib(JustifyTest.SCHEMA1, strJson));
		Assertions.assertEquals(1, errorCount);
	}

	@Test
	public void testRelaxedSyntax() {
		String strJson = "{\"hello\" : \"world\";}"; // relaxed syntax not permitted
		int errorCount = Assertions.assertDoesNotThrow(() -> parseWithTheJustifyLib(JustifyTest.SCHEMA1, strJson));
		Assertions.assertEquals(1, errorCount);
	}
}
