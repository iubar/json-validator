package it.iubar.json.validators;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class EveritDependenciesTest {
	
	private static final Logger LOGGER = Logger.getLogger(EveritDependenciesTest.class.getName());
	
	private static String schema1 = "/hello-schema.json"; // "additionalProperties": true, "required": ... 

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
	public void test1()   {
		JSONObject jo = new JSONObject("{\"int\": 0}");
		Object obj = jo.get("int"); // returns an Integer
		LOGGER.info("obj class: " + obj.getClass().getName());
		double d = jo.getDouble("int"); // still returns the value/object as in the old version (Double)
		LOGGER.info("obj: " + obj);
		LOGGER.info("d: " + d);		
		assertEquals("java.lang.Integer", obj.getClass().getName());
	}
	
	@Test
	public void test2()   {
		JSONObject jo = new JSONObject("{\"float\": 0.333}");
		Object obj = jo.get("float"); // returns a Double, in a future release, the dependency will return a BigDecimal (https://github.com/stleary/JSON-java/pull/453)
		double d = jo.getDouble("float"); // still returns the value/object as in the old version (Double)
		LOGGER.info("obj: " + obj);
		LOGGER.info("d: " + d);		
		assertEquals("java.lang.Double", obj.getClass().getName()); // in a future release the dependency will returns a BigDecimal
		int scale = 6; // it's a constant (a fixed value)
		BigDecimal bd1 = new BigDecimal(d).setScale(scale, RoundingMode.HALF_EVEN);
		LOGGER.info("bd1: " + bd1);			
		BigDecimal bd2 = BigDecimal.valueOf(d);
		LOGGER.info("bd2 (better): " + bd2);
		assertEquals(Double.toString(d), bd2.toString());		
	}
	
	@Test
	public void test3()   {
		JSONObject jo = new JSONObject("{\"float\": 0.333}");
		Object obj = jo.getBigDecimal("float"); // it's a java.math.BigDecimal
		Object opt = jo.optBigDecimal("float", null); // it's a java.math.BigDecimal
		LOGGER.info("obj class: " + obj.getClass().getName());
		LOGGER.info("opt class: " + opt.getClass().getName());
		LOGGER.info("obj: " + obj);
		LOGGER.info("opt: " + opt);		
		assertEquals(obj, opt);
	}	
	
	private void parseWithJSONObject(String schemaPath, String strJson) throws FileNotFoundException {			
		JSONObject jsonToValidate = new JSONObject(strJson);
		JSONObject.testValidity(jsonToValidate);				
		Schema schema = getSchema(schemaPath);  
		schema.validate(jsonToValidate); 
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
	 
}
