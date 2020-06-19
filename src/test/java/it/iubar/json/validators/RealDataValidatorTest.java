package it.iubar.json.validators;

import static org.junit.jupiter.api.Assertions.assertEquals;
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

import it.iubar.json.JsonValidator;
import it.iubar.json.validators.EveritStrategy;
import it.iubar.json.validators.IValidator;
import it.iubar.json.validators.JustifyStrategy;
import it.iubar.json.validators.NetworkntStrategy;


class RealDataValidatorTest {

	private static final Logger LOGGER = Logger.getLogger(RealDataValidatorTest.class.getName());

	private static File schemaFile = null;

	@BeforeAll 
	private static void init() throws MalformedURLException, IOException {
		RealDataValidatorTest.schemaFile = RealDataValidatorTest.fetchSchemaFile();
	}

	@Test
	@DisplayName("EveritStrategy")
	@Tag("LocalTestOnly")
	@Tag("Skip")
	void runTest1() {
		Assertions.assertDoesNotThrow(() -> {
			IValidator strategy = new EveritStrategy();
			try {
				run(strategy);
			} catch (Exception e) {				
				e.printStackTrace();
				LOGGER.log(Level.SEVERE, e.getMessage());
				throw e;
			}
		} );
	}

	@Test
	@DisplayName("JustifyStrategy")
	@Tag("LocalTestOnly")
	void runTest2() {
		Assertions.assertDoesNotThrow(() -> {
			IValidator strategy = new JustifyStrategy();
			try {
				run(strategy);
			} catch (Exception e) {				
				e.printStackTrace();
				LOGGER.log(Level.SEVERE, e.getMessage());
				throw e;
			}
		} );
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
				LOGGER.log(Level.SEVERE, e.getMessage());
				throw e;
			}
		} );
	}

	private void run(IValidator strategy) throws MalformedURLException, FileNotFoundException, IOException {
		File targetFile = getJsonDataFile();
		JsonValidator client = new JsonValidator(strategy);
		client.setSchema(RealDataValidatorTest.schemaFile );
		client.setTargetFolderOrFile(targetFile);	 
		int errors = client.run();
		if (errors > 0) {
			fail("Validator has found " + errors + " errors !");
		}
	}

	public static String getOutPath() {
		String path1 = Paths.get("").toAbsolutePath().toString();  // Current dir
		String path2 = System.getProperty("user.dir");  // Current dir
		LOGGER.log(Level.INFO, path1);
		LOGGER.log(Level.INFO, path2);
		assertEquals(path1, path2, "Paths should be the same");
		String targetPath = path2 + File.separator + "target";
		File targetDir = new File(targetPath);
		if(targetDir.exists()) {
			return targetPath;
		}
		return path2;
	}
	
	private File getJsonDataFile() throws IOException {
		File targetFile = new File(getOutPath() + File.separator + "test001.json");
		String address2 = GetContent.BASE_ADDRESS + "/test001.json";
		GetContent.download(new URL(address2), targetFile); 
		if (!targetFile.exists()) {
			fail("The path " + targetFile + " does not exist or is not readable");
		}else {
			String dataContent = FileUtils.readFileToString(targetFile, StandardCharsets.UTF_8);
			LOGGER.log(Level.FINE, dataContent);
		}
		return targetFile;
	}

	private static File fetchSchemaFile() throws MalformedURLException, IOException {
		File schemaFile = new File(getOutPath() + File.separator + "schema.json");
		String address = GetContent.BASE_ADDRESS + "/schema/schema.json";
		GetContent.download(new URL(address), schemaFile); 
		if(!schemaFile.isFile()) {
			fail("The file " + schemaFile + " does not exist or is not readable");
		}
		return schemaFile;
	}	

}
