package it.iubar.json.other;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import org.apache.commons.io.FileUtils;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.ValidationMessage;
import com.networknt.schema.SpecVersion;
 

/**
 * @see https://github.com/networknt/json-schema-validator/blob/master/doc/quickstart.md
 * 
 * @author Borgo
 *
 */
public class NetworkntStrategy extends RootStrategy {

	private static final Logger LOGGER = Logger.getLogger(NetworkntStrategy.class.getName());
	
	@Override
	public boolean validate(File file) {

		Set<ValidationMessage> errors = null;	
		try {
			String schemaContent = FileUtils.readFileToString(this.schema, StandardCharsets.UTF_8);
			String dataContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
			JsonSchema schema = getJsonSchemaFromStringContent(schemaContent);
			JsonNode node = getJsonNodeFromStringContent(dataContent);
			errors = schema.validate(node);	
			if(errors!=null) {
				for (ValidationMessage errorMsg : errors) {
					String msg = errorMsg.getMessage();
					Map<String, Object> details = errorMsg.getDetails();
					LOGGER.severe("Error: " + msg);
				}
			}
		} catch (Exception e) {
			LOGGER.severe("Exception: " + e.getMessage());
			if(e.getCause()!=null) {
				LOGGER.severe("Cause: " + e.getCause());
			}
			return false;
		}		
		return (errors.size() == 0);
	}
	
	/**
	 * @see https://github.com/networknt/json-schema-validator/issues/273
	 * @param schemaContent
	 * @return
	 * @throws Exception
	 */
    protected JsonSchema getJsonSchemaFromStringContent(String schemaContent) throws Exception {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7);
        JsonSchema schema = factory.getSchema(schemaContent);
        return schema;
    }
    
    protected JsonNode getJsonNodeFromStringContent(String content) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode node = mapper.readTree(content);
        return node;
    }
    

}
