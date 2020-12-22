package it.iubar.json.validators;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;
import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

public class EveritStrategy extends RootStrategy {

	private static final Logger LOGGER = Logger.getLogger(EveritStrategy.class.getName());

	public JSONObject getSchemaASJsonObjct() throws FileNotFoundException {
		JSONObject _schema = null;
		InputStream objFileInputStream = new FileInputStream(this.schema);
		_schema = new JSONObject(new JSONTokener(objFileInputStream));
		return _schema;
	}

	@Override
	public int validate(File file) throws FileNotFoundException {

		// InputStream is = new FileInputStream(file);
		// JSONObject jsonObj = new JSONObject(new JSONTokener(is));

		String dataContent = null;
		try {
			dataContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject(dataContent);
		} catch (Exception e) {
			return 1;
		}
		return validate2(jsonObj);
	}

	public int validate(String strJson) throws FileNotFoundException {
		JSONObject jsonObj = null;
		try {
			jsonObj = new JSONObject(strJson);
		} catch (Exception e) {
			return 1;
		}
		return validate2(jsonObj);
	}

	private int validate2(JSONObject jsonObj) throws FileNotFoundException {

		SchemaLoader loader = SchemaLoader.builder().schemaJson(getSchemaASJsonObjct()).draftV7Support().build(); // Draft
																													// V7
		Schema schemaJSON = loader.load().build();

		try {
			org.everit.json.schema.Validator validator = org.everit.json.schema.Validator.builder()
					// .failEarly()
					.build();
			validator.performValidation(schemaJSON, jsonObj);
		} catch (ValidationException e) {
			EveritStrategy.LOGGER.severe(e.getMessage());

			e.getCausingExceptions().stream().map(ValidationException::getMessage).forEach(System.out::println);

			e.toJSON();

			List<ValidationException> list = e.getCausingExceptions();
			for (ValidationException validationException : list) {
				String violation = validationException.getPointerToViolation();
				String[] parts = violation.split("/");
				String name = parts[parts.length - 1];
				int count = validationException.getViolationCount();
				EveritStrategy.LOGGER.severe("Object name: " + name);
				EveritStrategy.LOGGER.severe("Violation: " + count);
				List<String> messages = validationException.getAllMessages();
				for (String message : messages) {
					EveritStrategy.LOGGER.severe(message);
				}
			}
			if (list.size() > 0) {
				return list.size(); // the size of the causing exceptions
			} else {
				return 1;
			}
		}
		return 0;
	}

}
