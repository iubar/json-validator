package it.iubar.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.json.stream.JsonParsingException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Per la validazione del json vengono utilizzate 2 librerie in quanto:
 * - org.json.JSONObject nel parse risolve alcuni errori strutturali nel json
 * - javax.json.JsonObject (utilizzata nel progetto iubar-paghe-test) se la struttura è errata fallisce il parsing con un'eccezione
 * 
 */
public class Validator {

	private static final Logger LOGGER = Logger.getLogger(Validator.class.getName());

	private JSONObject schema = new JSONObject();
	private File targetFolderOrFile = null;

	public void setSchema(File schema) {
		try {
			FileInputStream objFileInputStream = new FileInputStream(schema);
			this.schema = new JSONObject(new JSONTokener(objFileInputStream));
		} catch (FileNotFoundException ex) {
			LOGGER.severe(ex.getMessage());
		}
	}

	public void setTargetFolderOrFile(File directory_or_file) {
		this.targetFolderOrFile = directory_or_file;
	}

	public int run() throws FileNotFoundException  {
		Map<String, File> map = getFilesMap();
		List<Boolean> result = new ArrayList<Boolean>();
		for (Map.Entry<String, File> entry : map.entrySet()){
			String fileName = entry.getKey();
			File file = entry.getValue();
			
			LOGGER.info("########## Validation of: " + fileName + " ##########");
			
			boolean valid1 = validateUseJavaxLib(file);
			boolean valid2 = validateUseEveritLib(file);
			
			if (valid1) {
				LOGGER.info("File parsing validation is valid");
			} else {
				LOGGER.info("File parsing validation is NOT valid");
			}
			
			if (valid2) {
				LOGGER.info("File with schema validation is valid");
			} else {
				LOGGER.info("File with schema validation is NOT valid");
			}
				
			boolean valid = (valid1 && valid2);
			if (valid) {
				LOGGER.info("File '" + fileName + "' is valid");	
			} else {
				LOGGER.warning("File '" + fileName + "' is NOT valid");
			}
			
			result.add(valid1 && valid2);
		}
		int passed = 0;
		for (Boolean value : result) {
			if (value.booleanValue())
				passed ++;
		}
		int error = result.size()-passed;

		LOGGER.info("TOTAL: " + result.size() + "  [PASSED: " + passed +"][ERROR: " + error + "]\n");

		return error;
	}
	
	public boolean validateUseJavaxLib(File file) throws FileNotFoundException  {
		boolean valid = false;
		try {
			String content = FileUtils.readFileToString(file, "utf-8");
	    	InputStream is = IOUtils.toInputStream(content);    	
			JsonReader reader = Json.createReader(is);
	    	JsonObject root = reader.readObject();
	    	if (root != null) {
	    		valid = true;
	    	}
		} catch (JsonParsingException ex) {
			LOGGER.severe(ex.getMessage());
		} catch (IOException e) {
			LOGGER.severe(e.getMessage());
		}
		
		return valid;
	}

	public boolean validateUseEveritLib(File file) throws FileNotFoundException  {
		boolean valid = false;
		
		JSONObject jsonObj = new JSONObject(new FileInputStream(file));

		SchemaLoader loader = SchemaLoader.builder().schemaJson(this.schema).draftV7Support().build();
		Schema schemaJSON = loader.load().build();

		try {
			org.everit.json.schema.Validator validator = org.everit.json.schema.Validator.builder().failEarly().build();
			validator.performValidation(schemaJSON, jsonObj);
			valid = true;
		} catch (ValidationException e) {
			LOGGER.severe(e.getMessage());
			
			e.getCausingExceptions().stream().map(ValidationException::getMessage).forEach(System.out::println);

			List<ValidationException> list = e.getCausingExceptions();
			for (ValidationException validationException : list) {
				String violation = validationException.getPointerToViolation();
				String[] parts = violation.split("/");
				String name = parts[parts.length-1];
				int count = validationException.getViolationCount();
				LOGGER.info("Object name: " + name);
				LOGGER.info("Violation: " + count);
				List<String> messages = validationException.getAllMessages();
				for (String message : messages) {
					LOGGER.info("	- " + message);
				}
			}	  
		}
		
		return valid;
	}
	
	public Map<String, File> getFilesMap (){
		Map<String, File> filesMap = new HashMap<String, File>();
		if (this.targetFolderOrFile.isDirectory()) {
			File[] files = this.targetFolderOrFile.listFiles();

			for (File file : files) {
				if (file.isFile()) {                
					filesMap.put(file.getName(), file);
				}  
			}
		} else {
			filesMap.put(this.targetFolderOrFile.getName(), this.targetFolderOrFile);
		}        

		return filesMap;
	}
}
