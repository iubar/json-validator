package it.iubar.json;

import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import jakarta.json.stream.JsonParsingException;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

/**
 *
 * It uses the Javax lib
 *
 */
public class PreValidator {

	private static final Logger LOGGER = Logger.getLogger(PreValidator.class.getName());

	public boolean validate(File file) throws FileNotFoundException {
		boolean valid = false;
		try {
			Charset charset = java.nio.charset.StandardCharsets.UTF_8;
			String content = FileUtils.readFileToString(file, charset);
			InputStream is = IOUtils.toInputStream(content);
			JsonReader reader = Json.createReader(is);
			JsonObject root = reader.readObject();
			if (root != null) {
				valid = true;
			}
		} catch (JsonParsingException ex) {
			PreValidator.LOGGER.log(Level.SEVERE, ex.getMessage());
		} catch (IOException ex) {
			PreValidator.LOGGER.log(Level.SEVERE, ex.getMessage());
		}

		return valid;
	}
}
