package it.iubar.json;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.util.logging.Level;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import it.iubar.json.JsonValidator;
import it.iubar.json.other.EveritStrategy;
import it.iubar.json.other.IValidator;
import it.iubar.json.other.JustifyStrategy;
import it.iubar.json.other.SyntaxException;

public class JsonValidatorTest {

	private static File schemaFile = null;
	
	@BeforeAll 
	private static void init() throws MalformedURLException, IOException {
		JsonValidatorTest.schemaFile = JsonValidatorTest2.fetchSchemaFile();
	}
	
	@Test
	public void testValidator1() throws IOException, SyntaxException {
		File file = new File(JsonValidatorTest.class.getResource("/must-fail.json").getFile());
		IValidator strategy = new EveritStrategy();
		int errors = parseWithValidator(strategy, file);
		assertTrue(errors > 0);
	}
	
	@Test
	@Disabled("La libreria non funziona, questo test fallisce sempre anche se non dovrebbe")
	public void testValidator2() throws IOException, SyntaxException {
		File file = new File(JsonValidatorTest.class.getResource("/must-pass.json").getFile());
		IValidator strategy = new EveritStrategy();
		int errors = parseWithValidator(strategy, file);
		assertTrue(errors == 0);
	}
	
	/**
	 * A  a differenza dell'implementazione con Everit, il metodo si accorge che il json adotta specifiche "rilassate"
	 * 
	 * @throws IOException
	 * @throws SyntaxException
	 */
	@Test
	public void testValidator3() throws IOException, SyntaxException {			
		SyntaxException thrown = Assertions.assertThrows(SyntaxException.class, () -> {
 
				File file = new File(JsonValidatorTest.class.getResource("/must-fail.json").getFile());
				IValidator strategy = new JustifyStrategy();
				int errors = parseWithValidator(strategy, file);
				assertTrue(errors > 0);
				
		 } );
		 
		 

	}
	
	@Test
	public void testValidator4() throws IOException, SyntaxException {
		File file = new File(JsonValidatorTest.class.getResource("/must-pass.json").getFile());
		IValidator strategy = new JustifyStrategy();
		int errors = parseWithValidator(strategy, file);
		assertTrue(errors == 0);
	}	
	
	
	private int parseWithValidator(IValidator strategy, File file) throws FileNotFoundException, SyntaxException {
		JsonValidator client = new JsonValidator(strategy);
		client.setSchema(JsonValidatorTest.schemaFile);
		client.setTargetFolderOrFile(file);		
		return client.run();
	}
	
}
