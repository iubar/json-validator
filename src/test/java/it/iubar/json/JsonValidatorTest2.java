package it.iubar.json;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import it.iubar.json.other.EveritStrategy;
import it.iubar.json.other.IValidator;
import it.iubar.json.other.JustifyStrategy;
import it.iubar.json.other.SyntaxException;


class JsonValidatorTest2 {

	private static final Logger LOGGER = Logger.getLogger(JsonValidatorTest2.class.getName());
	
	private static File schemaFile = null;
	
	@BeforeAll 
	private static void init() throws MalformedURLException, IOException {
		JsonValidatorTest2.schemaFile = JsonValidatorTest2.fetchSchemaFile();
	}
	
	@Test
	@DisplayName("JustifyStrategy")
	void runTest1() {
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
	@DisplayName("EveritStrategy")
	@Disabled("La libreria non funziona, questo test fallisce sempre anche se non dovrebbe")	
	void runTest2() {
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
		
	private void run(IValidator strategy) throws MalformedURLException, FileNotFoundException, IOException, SyntaxException {
			
		File targetFile = new File(getOutPath() + File.separator + "test001.json");
		String address2 = GetContent.BASE_ADDRESS + "/test001.json";
		GetContent.getContent(new URL(address2), targetFile); 
		if (!targetFile.exists()) {
			fail("The path " + targetFile + " does not exist or is not readable");
		}
				

		JsonValidator client = new JsonValidator(strategy);
		client.setSchema(JsonValidatorTest2.schemaFile );
		client.setTargetFolderOrFile(targetFile);
	 
			int errors = client.run();
			if (errors > 0) {
				fail("Validator has found " + errors + " errors !");
			}
 
	}
	
	
public static File fetchSchemaFile() throws MalformedURLException, IOException {
	File schemaFile = new File(getOutPath() + File.separator + "schema.json");
	String address = GetContent.BASE_ADDRESS + "/schema/schema.json";
	GetContent.getContent(new URL(address), schemaFile); 
	if(!schemaFile.isFile()) {
		fail("The file " + schemaFile + " does not exist or is not readable");
	}
	return schemaFile;
	}

public static String getOutPath() {
	String path1 = Paths.get("").toAbsolutePath().toString();  // Current dir
	String path2 = System.getProperty("user.dir");  // Current dir
	LOGGER.log(Level.INFO, path1);
	LOGGER.log(Level.INFO, path2);
	assertEquals(path1, path2, "I percorsi dovrebbero essere identici");
	String targetPath = path2 + File.separator + "target";
	File targetDir = new File(targetPath);
	if(targetDir.exists()) {
		return targetPath;
	}
	return path2;
}



}
