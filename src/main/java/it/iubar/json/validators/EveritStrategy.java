package it.iubar.json.validators;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;
import java.util.logging.Logger;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

public class EveritStrategy extends RootStrategy {

	private static final Logger LOGGER = Logger.getLogger(EveritStrategy.class.getName());
	
	@Override
	public int validate(File file) throws FileNotFoundException {
 
		
		JSONObject jsonObj = new JSONObject(new FileInputStream(file));

		SchemaLoader loader = SchemaLoader.builder().schemaJson(getSchemaASJsonObjct()).draftV7Support().build(); // Draft V7
		Schema schemaJSON = loader.load().build();

		try {
			org.everit.json.schema.Validator validator = org.everit.json.schema.Validator.builder().failEarly().build();
			validator.performValidation(schemaJSON, jsonObj);
 
		} catch (ValidationException e) {
			LOGGER.severe(e.getMessage());
			
			e.getCausingExceptions().stream().map(ValidationException::getMessage).forEach(System.out::println);

			List<ValidationException> list = e.getCausingExceptions();
			for (ValidationException validationException : list) {
				String violation = validationException.getPointerToViolation();
				String[] parts = violation.split("/");
				String name = parts[parts.length-1];
				int count = validationException.getViolationCount();
				LOGGER.severe("Object name: " + name);
				LOGGER.severe("Violation: " + count);
				List<String> messages = validationException.getAllMessages();
				for (String message : messages) {
					LOGGER.severe(message);
				}
			}
			return list.size();
		}
		
		return 0;
	}
	
	public JSONObject getSchemaASJsonObjct() {
		
	JSONObject _schema =  null;
	try {
		InputStream objFileInputStream = new FileInputStream(this.schema);
		_schema = new JSONObject(new JSONTokener(objFileInputStream));
	} catch (FileNotFoundException ex) {
		LOGGER.severe(ex.getMessage());
	}	
	return _schema;
	}

}
