package it.iubar.json.validators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Logger;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import it.iubar.json.JsonValidator;
import it.iubar.json.validators.EveritStrategy;
import it.iubar.json.validators.IValidator;
import it.iubar.json.validators.JustifyStrategy;
import it.iubar.json.validators.NetworkntStrategy;

public class ComparatedValidatorsTest {

	private static final Logger LOGGER = Logger.getLogger(ComparatedValidatorsTest.class.getName());

	private static File schemaFile = null;

	@BeforeAll 
	private static void init() throws MalformedURLException, IOException {
		ComparatedValidatorsTest.schemaFile = new File(ComparatedValidatorsTest.class.getResource("/hello2-schema.json").getFile());
	}

	@Test
	public void testValidator1() throws IOException {
		File file = new File(ComparatedValidatorsTest.class.getResource("/must-fail.json").getFile());
		IValidator strategy = new EveritStrategy();
		int errorCount = parseWithValidator(strategy, file);
		assertEquals(1, errorCount);
	}
	
	@Test
	@Tag("Skip")
	public void testValidator7() throws IOException {
		File file = new File(ComparatedValidatorsTest.class.getResource("/must-fail2.json").getFile());
		IValidator strategy = new EveritStrategy();
		int errorCount = parseWithValidator(strategy, file);
		assertEquals(1, errorCount);
	}
	
	@Test
	@Tag("Skip")
	public void testValidator8() throws IOException {
		File file = new File(ComparatedValidatorsTest.class.getResource("/must-fail3.json").getFile());
		IValidator strategy = new EveritStrategy();
		int errorCount = parseWithValidator(strategy, file);
		assertEquals(1, errorCount);
	}	

	@Test	
	public void testValidator2() throws IOException {
		File file = new File(ComparatedValidatorsTest.class.getResource("/must-pass.json").getFile());
		IValidator strategy = new EveritStrategy();
		int errorCount = parseWithValidator(strategy, file);
		assertEquals(0, errorCount);
	}

	/**
	 * A  a differenza dell'implementazione con Everit, il metodo si accorge che il json adotta specifiche "rilassate"
	 * 
	 * @throws IOException
	 */
	@Test
	public void testValidator3() throws IOException {			
		File file = new File(ComparatedValidatorsTest.class.getResource("/must-fail.json").getFile());
		IValidator strategy = new JustifyStrategy();
		int errorCount = parseWithValidator(strategy, file);
		assertEquals(1, errorCount);
	}

	@Test
	public void testValidator4() throws IOException {
		File file = new File(ComparatedValidatorsTest.class.getResource("/must-pass.json").getFile());
		IValidator strategy = new JustifyStrategy();
		int errorCount = parseWithValidator(strategy, file);
		assertEquals(0, errorCount);
	}	

	@Test
	public void testValidator5() throws IOException {			

		File file = new File(ComparatedValidatorsTest.class.getResource("/must-fail.json").getFile());
		IValidator strategy = new NetworkntStrategy();
		int errorCount = parseWithValidator(strategy, file);
		assertEquals(1, errorCount);
	}

	@Test
	public void testValidator6() throws IOException {
		File file = new File(ComparatedValidatorsTest.class.getResource("/must-pass.json").getFile());
		IValidator strategy = new NetworkntStrategy();
		int errorCount = parseWithValidator(strategy, file);
		assertEquals(0, errorCount);
	}	

	private int parseWithValidator(IValidator strategy, File file) throws FileNotFoundException {
		JsonValidator client = new JsonValidator(strategy);
		client.setSchema(ComparatedValidatorsTest.schemaFile);
		client.setTargetFolderOrFile(file);		
		return client.run();
	}

}
