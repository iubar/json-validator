package it.iubar.json.validators;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.JsonNode;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.ValidationMessage;

import it.iubar.json.validators.NetworkntStrategy;

public class NetworkntTest {
	
	private static final Logger LOGGER = Logger.getLogger(NetworkntTest.class.getName());
	
	private static String schema1 = "/hello-schema.json"; // "additionalProperties": true, "required": ... 
	private static String schema2 = "/hello2-schema.json"; // "additionalProperties": false, "required": ... 
 
 
	
	@Test
	public void testRelaxedSyntax()   {
		String strJson = "{\"hello\" : \"world\";}";  // relaxed syntax not permitted
		int errorCount = assertDoesNotThrow(() -> parseWithTheNetworkntLib(schema1 , strJson));
		assertEquals(1, errorCount);
	}
	
	@Test
	public void testNetworkntWrongSyntax()  {
		String strJson = "{\"hello\" : \"world\"\"}";  // wrong syntax
		int errorCount = assertDoesNotThrow(() -> parseWithTheNetworkntLib(schema1, strJson));
		assertEquals(1, errorCount);
	}
	
	@Test
	public void testNetworkntRightSyntax1()   {
		String strJson = "{\"hello\" : \"world\"}";
		int errorCount = assertDoesNotThrow(() -> parseWithTheNetworkntLib(schema1, strJson));
		assertEquals(0, errorCount);
	}
			
	@Test
	public void testNetworkntRequired1()   {
		String strJson = "{\"missingKeyFromSchema\" : \"should fail because the hello key is required\"}";
		int errorCount = assertDoesNotThrow(() -> parseWithTheNetworkntLib(schema1, strJson));
		assertEquals(1, errorCount);
	}
	
	@Test
	public void testNetworkntAdditionalProperties1()   {
		String strJson = "{\"hello\" : \"world\", \"missingKeyFromSchema\" : \"should pass because additionalProperties is true\"}";
		int errorCount = assertDoesNotThrow(() -> parseWithTheNetworkntLib(schema1, strJson));
		assertEquals(0, errorCount);
	}	
	
	@Test
	public void testNetworkntRequired2()   {
		String strJson = "{\"missingKeyFromSchema2\" : \"should fail because the hello key is required and additionalProperties is false\"}";
		int errorCount = assertDoesNotThrow(() -> parseWithTheNetworkntLib(schema2, strJson));
		assertEquals(2, errorCount);
	}
	
	@Test
	public void testNetworkntAdditionalProperties2()   {
		String strJson = "{\"hello\" : \"world\", \"missingKeyFromSchema\" : \"should fail  because additionalProperties is false\"}";
	 	int errorCount = assertDoesNotThrow(() -> parseWithTheNetworkntLib(schema2, strJson));
	 	assertEquals(1, errorCount);
	}	
	
	@Test
	public void testNetworkntRightSyntax2()   {
		String strJson = "{\"hello\" : \"world\"}";
		int errorCount = assertDoesNotThrow(() -> parseWithTheNetworkntLib(schema2, strJson));
		assertEquals(0, errorCount);
	}	
 
	private int parseWithTheNetworkntLib(String schemaPath, String dataContent) throws FileNotFoundException {
		Set<ValidationMessage> errors = null;	
		try {
			 
			File schemaFile = new File(NetworkntTest.class.getResource(schemaPath).getFile());
			String schemaContent = FileUtils.readFileToString(schemaFile);
			
			// InputStream inputStream = new FileInputStream(schemaFile);
			// String schemaContent = IOUtils.toString(inputStream, StandardCharsets.UTF_8.name());

			JsonSchema schema = NetworkntStrategy.getJsonSchemaFromStringContent(schemaContent);
			JsonNode node = NetworkntStrategy.getJsonNodeFromStringContent(dataContent);
			errors = schema.validate(node);	
			if(errors!=null) {
				for (ValidationMessage errorMsg : errors) {
					String msg = errorMsg.getMessage();
					Map<String, Object> details = errorMsg.getDetails();
					LOGGER.severe("Error: " + msg);
				}
			}
			return errors.size();
		} catch (Exception e) {
			LOGGER.severe("Exception: " + e.getMessage());
			if(e.getCause()!=null) {
				LOGGER.severe("Cause: " + e.getCause());
			}
	 
			return 1;
		}
	}
}
