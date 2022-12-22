package it.iubar.json.validators;

import it.iubar.json.JsonValidator;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class PagheOpenJsonTest {

	private static final Logger LOGGER = Logger.getLogger(PagheOpenJsonTest.class.getName());

	private static final String EXPLAINATION = "Json is wrong and it's right that can't be validated against the schema";

	private static String schemaFilename = "/po/po-right-schema.json";

	private static File schemaFile = null;

	@BeforeAll
	public static void init() throws MalformedURLException, IOException {
		PagheOpenJsonTest.schemaFile = new File(PagheOpenJsonTest.class.getResource(schemaFilename).getFile());
	}

	private int parseWithValidator(IValidator strategy, File file) throws FileNotFoundException {
		JsonValidator client = new JsonValidator(strategy);
		client.setSchema(PagheOpenJsonTest.schemaFile);
		client.setTargetFolderOrFile(file);
		return client.run();
	}

	@Test
	public void testPoSchema() throws IOException {
		File file = new File(PagheOpenJsonTest.class.getResource(schemaFilename).getFile());
		IValidator strategy = new EveritStrategy();
		JsonValidator client = new JsonValidator(strategy);
		File schemaFile = new File(PagheOpenJsonTest.class.getResource("/po/draft7-schema.json").getFile());
		client.setSchema(schemaFile);
		client.setTargetFolderOrFile(file);
		int errorCount = client.run();
		Assertions.assertEquals(0, errorCount);
	}

	@Test
	public void testPagheOpen1() throws IOException {
		File file = new File(PagheOpenJsonTest.class.getResource("/po/po-must-fail1.json").getFile());
		IValidator strategy = new EveritStrategy();
		int errorCount = parseWithValidator(strategy, file);
		Assertions.assertNotEquals(0, errorCount, EXPLAINATION);
	}

	@Test
	public void testPagheOpen2() throws IOException {
		File file = new File(PagheOpenJsonTest.class.getResource("/po/po-must-fail1.json").getFile());
		IValidator strategy = new JustifyStrategy();
		int errorCount = parseWithValidator(strategy, file);
		Assertions.assertNotEquals(0, errorCount, EXPLAINATION);
	}

	@Test
	public void testPagheOpen3() throws IOException {
		File file = new File(PagheOpenJsonTest.class.getResource("/po/po-must-fail1.json").getFile());
		IValidator strategy = new NetworkntStrategy();
		int errorCount = parseWithValidator(strategy, file);
		Assertions.assertNotEquals(0, errorCount, EXPLAINATION);
	}
}
