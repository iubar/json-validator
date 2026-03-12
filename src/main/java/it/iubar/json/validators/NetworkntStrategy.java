package it.iubar.json.validators;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.Error;
import com.networknt.schema.ExecutionConfig;
import com.networknt.schema.ExecutionContext;
import com.networknt.schema.InputFormat;
import com.networknt.schema.Schema;
import com.networknt.schema.SchemaRegistry;
import com.networknt.schema.SpecificationVersion;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;

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

 
	public static Schema getJsonSchemaFromStringContent(String schemaContent) throws Exception {
        SchemaRegistry schemaRegistry = SchemaRegistry.withDefaultDialect(SpecificationVersion.DRAFT_2020_12);
        /*
         * This should be cached for performance.
         * 
         * Loading from a String is not recommended as there is no base IRI to use for
         * resolving relative $ref.
         */
        Schema schema = schemaRegistry.getSchema(schemaContent);
//        String example = "{\"id\": \"2\"}";
//        List<Error> errors = schema.validate(example, InputFormat.JSON,
//                executionContext -> executionContext
//                .executionConfig(executionConfig -> executionConfig.formatAssertionsEnabled(true)));     
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
		List<Error> errors = null;
		try {
			String schemaContent = FileUtils.readFileToString(this.schema, StandardCharsets.UTF_8);
			Schema schema = NetworkntStrategy.getJsonSchemaFromStringContent(schemaContent);
			JsonNode node = NetworkntStrategy.getJsonNodeFromStringContent(dataContent);
			errors = schema.validate(node);
 
			if (errors != null) {
				for (Error error : errors) {
					String msg = error.getMessage();
					NetworkntStrategy.LOGGER.severe(msg);
					// Map<String, Object> map = error.getDetails();
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
