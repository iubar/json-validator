package it.iubar.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;
import java.util.logging.Logger;

//import javax.json.Json;
//import javax.json.JsonObject;
//import javax.json.JsonReader;
//import javax.json.stream.JsonParsingException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.stream.JsonParsingException;

/**
 * 
 * It uses the Javax lib
 * 
 * @author Borgo
 *
 */
public class PreValidator {

	private static final Logger LOGGER = Logger.getLogger(PreValidator.class.getName());
	
	public boolean validate(File file) throws FileNotFoundException  {
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
			LOGGER.log(Level.SEVERE, ex.getMessage());
		} catch (IOException ex) {
			LOGGER.log(Level.SEVERE, ex.getMessage());
		}
		
		return valid;
	}
	
}
