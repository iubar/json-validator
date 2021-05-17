package it.iubar.json.validators;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;

/**
 * @see https://github.com/networknt/json-schema-validator/blob/master/doc/quickstart.md
 * 
 * @author Borgo
 *
 */
public class NetworkntStrategy extends RootStrategy {

	private static final Logger LOGGER = Logger.getLogger(NetworkntStrategy.class.getName());

	public static JsonNode getJsonNodeFromStringContent(String content) throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode node = mapper.readTree(content);
		return node;
	}

	/**
	 * @see https://github.com/networknt/json-schema-validator/issues/273
	 * @param schemaContent
	 * @return
	 * @throws Exception
	 */
	public static JsonSchema getJsonSchemaFromStringContent(String schemaContent) throws Exception {
		JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
		JsonSchema schema = factory.getSchema(schemaContent);
		return schema;
	}

	@Override
	public int validate(File file) {
		String dataContent = null;
		try {
			dataContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			NetworkntStrategy.LOGGER.severe(e.getMessage());
		}
		return validate(dataContent);
	}

	public int validate(String dataContent) {
		Set<ValidationMessage> errors = null;
		try {
			String schemaContent = FileUtils.readFileToString(this.schema, StandardCharsets.UTF_8);
			JsonSchema schema = NetworkntStrategy.getJsonSchemaFromStringContent(schemaContent);
			JsonNode node = NetworkntStrategy.getJsonNodeFromStringContent(dataContent);
			errors = schema.validate(node);
			if (errors != null) {
				for (ValidationMessage errorMsg : errors) {
					String msg = errorMsg.getMessage();
					NetworkntStrategy.LOGGER.severe(msg);
					Map<String, Object> map = errorMsg.getDetails();
					// ...
				}
			}
			return errors.size();
		} catch (Exception e) {
			NetworkntStrategy.LOGGER.severe("Exception: " + e.getMessage());
			if (e.getCause() != null) {
				NetworkntStrategy.LOGGER.severe("Cause: " + e.getCause());
			}

			return 1;
		}
	}

}
