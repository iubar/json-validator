package it.iubar.json;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import it.iubar.json.validators.EveritStrategy;
import it.iubar.json.validators.IValidator;

/**
 * Per la validazione del json vengono utilizzate 2 librerie in quanto: 
 * - org.json.JSONObject nel parse risolve alcuni errori strutturali nel json 
 * - javax.json.JsonObject (utilizzata nel progetto iubar-paghe-test) se la struttura è errata fallisce il parsing con un'eccezione
 * 
 */
public class JsonValidator {

	private static final Logger LOGGER = Logger.getLogger(JsonValidator.class.getName());

	private static boolean failFast = false;

	private File targetFolderOrFile = null;
	private IValidator validator = null;

	public JsonValidator(IValidator validator) {
		this.validator = validator;
	}

	public Map<String, File> getFilesMap() {
		Map<String, File> filesMap = new HashMap<>();
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

	public int run() throws FileNotFoundException {
		Map<String, File> map = getFilesMap();
		List<Boolean> results = new ArrayList<>();
		for (Map.Entry<String, File> entry : map.entrySet()) {
			String fileName = entry.getKey();
			File file = entry.getValue();
			boolean valid = false;

			if (this.validator instanceof EveritStrategy) {
				JsonValidator.LOGGER.info("Validating " + fileName + " ... 1/2...");
				PreValidator preValidator = new PreValidator();
				boolean valid1 = preValidator.validate(file);
				JsonValidator.LOGGER.info("Validating " + fileName + " ... 2/2...");
				int errorsCount = this.validator.validate(file);
				valid = (valid1 && (errorsCount == 0));
			} else {
				JsonValidator.LOGGER.info("Validating " + fileName);
				int errorsCount = this.validator.validate(file);
				valid = (errorsCount == 0);
			}

			if (valid) {
				JsonValidator.LOGGER.info("File '" + fileName + "' is valid");
			} else {
				JsonValidator.LOGGER.warning("File '" + fileName + "' is NOT valid");
			}

			results.add(valid);

			if (JsonValidator.failFast) { // non proseguo con gli altri file
				if (!valid) {
					break;
				}
			}

		} // end for

		int passed = 0;
		for (Boolean value : results) {
			if (value.booleanValue()) {
				passed++;
			}
		}
		int error = results.size() - passed;

		JsonValidator.LOGGER.info("TOTAL: " + results.size() + "  [PASSED: " + passed + "][ERROR: " + error + "]\n");

		return error;
	}

	public void setSchema(File schemaFile) {
		this.validator.setSchema(schemaFile);
	}

	public void setTargetFolderOrFile(File directory_or_file) {
		this.targetFolderOrFile = directory_or_file;
	}
}
