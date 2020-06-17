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

//import javax.json.Json;
//import javax.json.JsonObject;
//import javax.json.JsonReader;
//import javax.json.stream.JsonParsingException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import it.iubar.json.other.EveritStrategy;
import it.iubar.json.other.IValidator;
import it.iubar.json.other.JustifyStrategy;
import it.iubar.json.other.SyntaxException;

/**
 * Per la validazione del json vengono utilizzate 2 librerie in quanto:
 * - org.json.JSONObject nel parse risolve alcuni errori strutturali nel json
 * - javax.json.JsonObject (utilizzata nel progetto iubar-paghe-test) se la struttura è errata fallisce il parsing con un'eccezione
 * 
 */
public class JsonValidator {

	private static final Logger LOGGER = Logger.getLogger(JsonValidator.class.getName());
 
	private File targetFolderOrFile = null;
	private IValidator validator = null;

	public JsonValidator(IValidator validator) {
		this.validator = validator;
	}

	public void setTargetFolderOrFile(File directory_or_file) {
		this.targetFolderOrFile = directory_or_file;
	}

	public int run() throws FileNotFoundException, SyntaxException  {
		Map<String, File> map = getFilesMap();
		List<Boolean> result = new ArrayList<Boolean>();
		for (Map.Entry<String, File> entry : map.entrySet()){
			String fileName = entry.getKey();
			File file = entry.getValue();
			boolean valid = false;
			if(this.validator instanceof EveritStrategy) {
				LOGGER.info("Validating " + fileName + " ... 1/2...");
				PreValidator preValidator = new PreValidator();
				boolean valid1 = preValidator.validate(file);
				LOGGER.info("Validating " + fileName + " ... 2/2...");
				boolean valid2 = this.validator.validate(file);			
				valid = (valid1 && valid2);
			}else if(this.validator instanceof JustifyStrategy) {
				LOGGER.info("Validating " + fileName);
				valid = this.validator.validate(file);	
			}else {
				throw new RuntimeException("Situazione imprevista");
			}					
			if (valid) {
				LOGGER.info("File '" + fileName + "' is valid");	
			} else {
				LOGGER.warning("File '" + fileName + "' is NOT valid");
			}
			
			result.add(valid);
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



	public void setSchema(File schemaFile) {
		this.validator.setSchema(schemaFile);
	}
}
