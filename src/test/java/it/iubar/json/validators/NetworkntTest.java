package it.iubar.json.validators;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.logging.Logger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class NetworkntTest {

	private static final Logger LOGGER = Logger.getLogger(NetworkntTest.class.getName());

	private static final String SCHEMA1 = "/hello-schema.json"; // "additionalProperties": true, "required": ...
	private static final String SCHEMA2 = "/hello2-schema.json"; // "additionalProperties": false, "required": ...

	private int parseWithTheNetworkntLib(String schemaPath, String dataContent) throws FileNotFoundException {
		NetworkntStrategy strategy = new NetworkntStrategy();
		File schemaFile = new File(NetworkntTest.class.getResource(schemaPath).getFile());
		strategy.setSchema(schemaFile);
		return strategy.validate(dataContent);
	}

	@Test
	public void testNetworkntAdditionalProperties1() {
		String strJson = "{\"hello\" : \"world\", \"missingKeyFromSchema\" : \"should pass because additionalProperties is true\"}";
		int errorCount = Assertions.assertDoesNotThrow(() -> parseWithTheNetworkntLib(NetworkntTest.SCHEMA1, strJson));
		Assertions.assertEquals(0, errorCount);
	}

	@Test
	public void testNetworkntAdditionalProperties2() {
		String strJson = "{\"hello\" : \"world\", \"missingKeyFromSchema\" : \"should fail because additionalProperties is false\"}";
		int errorCount = Assertions.assertDoesNotThrow(() -> parseWithTheNetworkntLib(NetworkntTest.SCHEMA2, strJson));
		Assertions.assertEquals(1, errorCount);
	}

	@Test
	public void testNetworkntRequired1() {
		String strJson = "{\"missingKeyFromSchema\" : \"should fail because the hello key is required\"}";
		int errorCount = Assertions.assertDoesNotThrow(() -> parseWithTheNetworkntLib(NetworkntTest.SCHEMA1, strJson));
		Assertions.assertEquals(1, errorCount);
	}

	@Test
	public void testNetworkntRequired2() {
		String strJson = "{\"missingKeyFromSchema2\" : \"should fail because the hello key is required and additionalProperties is false\"}";
		int errorCount = Assertions.assertDoesNotThrow(() -> parseWithTheNetworkntLib(NetworkntTest.SCHEMA2, strJson));
		Assertions.assertEquals(2, errorCount);
	}

	@Test
	public void testNetworkntRightSyntax1() {
		String strJson = "{\"hello\" : \"world\"}";
		int errorCount = Assertions.assertDoesNotThrow(() -> parseWithTheNetworkntLib(NetworkntTest.SCHEMA1, strJson));
		Assertions.assertEquals(0, errorCount);
	}

	@Test
	public void testNetworkntRightSyntax2() {
		String strJson = "{\"hello\" : \"world\"}";
		int errorCount = Assertions.assertDoesNotThrow(() -> parseWithTheNetworkntLib(NetworkntTest.SCHEMA2, strJson));
		Assertions.assertEquals(0, errorCount);
	}

	@Test
	public void testNetworkntWrongSyntax() {
		String strJson = "{\"hello\" : \"world\"\"}"; // wrong syntax
		int errorCount = Assertions.assertDoesNotThrow(() -> parseWithTheNetworkntLib(NetworkntTest.SCHEMA1, strJson));
		Assertions.assertEquals(1, errorCount);
	}

	@Test
	public void testRelaxedSyntax() {
		String strJson = "{\"hello\" : \"world\";}"; // relaxed syntax not permitted
		int errorCount = Assertions.assertDoesNotThrow(() -> parseWithTheNetworkntLib(NetworkntTest.SCHEMA1, strJson));
		Assertions.assertEquals(1, errorCount);
	}
}
