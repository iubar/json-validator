package it.iubar.json.validators;

import it.iubar.json.JsonValidator;
import it.iubar.json.utils.GetContent;
import it.iubar.json.utils.TrustAllHostNameVerifier;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

public class RealDataValidatorTest {

	private static final Logger LOGGER = Logger.getLogger(RealDataValidatorTest.class.getName());

	public static final String BASE_ADDRESS =
		"https://" + GetContent.HOST_NAME + "/svn/java/iubar-paghe-test/trunk/src/test/resources/cedolini/json";

	private static File schemaFile = null;

	private static File fetchSchemaFile()  {
		File schemaFile = new File(RealDataValidatorTest.getOutPath() + File.separator + "schema.json");
		String address = RealDataValidatorTest.BASE_ADDRESS + "/schema/schema.json";
		URL url;
		try {
			url = new URL(address);
			LOGGER.log(Level.INFO, "URL : " + url);
			GetContent.getContent1(url, schemaFile);
		} catch (MalformedURLException e) {
 			//e.printStackTrace(); 			
 			String error =  e.getMessage();
 			LOGGER.log(Level.SEVERE, error);
 			fail(error, e);
		} catch (IOException e) {
			//e.printStackTrace();
 			String error =  e.getMessage();			
			LOGGER.log(Level.SEVERE, error);
			fail(error, e);
		}
		if (!schemaFile.isFile()) {
			String error = "The file " + schemaFile + " does not exist or is not readable";
			LOGGER.log(Level.SEVERE, error);
			fail(error);
		}
		return schemaFile;
	}

	public static String getOutPath() {
		String path1 = Paths.get("").toAbsolutePath().toString(); // Current dir
		String path2 = System.getProperty("user.dir"); // Current dir
		RealDataValidatorTest.LOGGER.log(Level.INFO, "out path:" + path1);
		RealDataValidatorTest.LOGGER.log(Level.INFO, "out path:" + path2);
		Assertions.assertEquals(path1, path2, "Paths should be the same");
		String targetPath = path2 + File.separator + "target";
		File targetDir = new File(targetPath);
		if (targetDir.exists()) {
			return targetPath;
		}
		return path2;
	}

	@BeforeAll
	public static void init() throws MalformedURLException, IOException {
		RealDataValidatorTest.schemaFile = RealDataValidatorTest.fetchSchemaFile();
	}

	private File getJsonDataFile() throws IOException {
		File targetFile = new File(RealDataValidatorTest.getOutPath() + File.separator + "test001.json");
		String address2 = RealDataValidatorTest.BASE_ADDRESS + "/test001.json";
		GetContent.getContent1(new URL(address2), targetFile);
		if (!targetFile.exists()) {
			Assertions.fail("The path " + targetFile + " does not exist or is not readable");
		} else {
			String dataContent = FileUtils.readFileToString(targetFile, StandardCharsets.UTF_8);
			RealDataValidatorTest.LOGGER.log(Level.FINE, dataContent);
		}
		return targetFile;
	}

	private void run(IValidator strategy) throws MalformedURLException, FileNotFoundException, IOException {
		File targetFile = getJsonDataFile();
		JsonValidator client = new JsonValidator(strategy);
		client.setSchema(RealDataValidatorTest.schemaFile);
		client.setTargetFolderOrFile(targetFile);
		int errors = client.run();
		if (errors > 0) {
			Assertions.fail("Validator has found " + errors + " errors !");
		}
	}

	@Test
	@DisplayName("EveritStrategy")
	@Tag("LocalTestOnly")
	void runTest1() {
		Assertions.assertDoesNotThrow(() -> {
			IValidator strategy = new EveritStrategy();
			try {
				run(strategy);
			} catch (Exception e) {
				e.printStackTrace();
				RealDataValidatorTest.LOGGER.log(Level.SEVERE, e.getMessage());
				throw e;
			}
		});
	}

	@Test
	@DisplayName("JustifyStrategy")
	@Tag("LocalTestOnly")
	@Disabled("Libreria obsoleta: org.opentest4j.AssertionFailedError: Unexpected exception thrown: org.leadpony.justify.api.JsonValidatingException: [2,56][/$schema] Unsuppoted metaschema: https://json-schema.org/draft-07/schema")
	void runTest2() {
		Assertions.assertDoesNotThrow(() -> {
			IValidator strategy = new JustifyStrategy();
			try {
				run(strategy);
			} catch (Exception e) {
				e.printStackTrace();
				RealDataValidatorTest.LOGGER.log(Level.SEVERE, e.getMessage());
				throw e;
			}
		});
	}

	@Test
	@DisplayName("NetworkntStrategy")
	@Tag("LocalTestOnly")
	void runTest3() {
		Assertions.assertDoesNotThrow(() -> {
			IValidator strategy = new NetworkntStrategy();
			try {
				run(strategy);
			} catch (Exception e) {
				e.printStackTrace();
				RealDataValidatorTest.LOGGER.log(Level.SEVERE, e.getMessage());
				throw e;
			}
		});
	}
	
	@Test
	@DisplayName("SkemaStrategy")
	@Tag("LocalTestOnly")
	void runTest4() {
		Assertions.assertDoesNotThrow(() -> {
			IValidator strategy = new SkemaStrategy();
			try {
				run(strategy);
			} catch (Exception e) {
				e.printStackTrace();
				RealDataValidatorTest.LOGGER.log(Level.SEVERE, e.getMessage());
				throw e;
			}
		});
	}
	
}
