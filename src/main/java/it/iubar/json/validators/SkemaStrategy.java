package it.iubar.json.validators;

import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
 
import org.json.JSONObject;
import org.json.JSONTokener;

import com.github.erosb.jsonsKema.JsonParser;
import com.github.erosb.jsonsKema.JsonValue;
import com.github.erosb.jsonsKema.Schema;
import com.github.erosb.jsonsKema.SchemaLoader;
import com.github.erosb.jsonsKema.ValidationFailure;

 
/**
 * @see https://github.com/everit-org/json-schema 
 */
public class SkemaStrategy extends RootStrategy {

	private static final Logger LOGGER = Logger.getLogger(SkemaStrategy.class.getName());

	@Override
	public int validate(File file) throws FileNotFoundException {
		String dataContent = null;
		try {
			dataContent = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
		} catch (IOException e1) {
			e1.printStackTrace();
		}

 
		return validate2(getValidatorFromFile(), dataContent);
	}

	public int validate(String strJson) throws FileNotFoundException {
		return validate2(getValidatorFromFile(), strJson);
	}
	
	

	private int validate2(com.github.erosb.jsonsKema.Validator validator, String jsonAsString) {
 
		JsonValue instance = null;;
		// parse the input instance to validate against the schema
		try {
			JsonParser parser =  new JsonParser(jsonAsString);
			instance = parser.parse();	
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
			return 1;
		}
		

		// run the validation
		ValidationFailure failure = validator.validate(instance);

		// print the validation failures (if any)
		System.out.println("FAILURE : " + failure);	
		if(failure==null) {
			return 0;
		}else {		
		return 1;
		}
	}
	
	
	private com.github.erosb.jsonsKema.Validator getValidatorFromString(String schemaAsString){
		// parse the schema JSON as string
		JsonValue  schemaJson = new JsonParser(schemaAsString).parse();
		// map the raw json to a reusable Schema instance
		Schema schema = new SchemaLoader(schemaJson).load();

		// create a validator instance for each validation (one-time use object) 
		com.github.erosb.jsonsKema.Validator validator = com.github.erosb.jsonsKema.Validator.forSchema(schema);		
		return validator;
	}
	
	private com.github.erosb.jsonsKema.Validator getValidatorFromUrl(){
		// HTTP protocol is also supported
		Schema schema = SchemaLoader.forURL("classpath:///path/to/your/schema.json").load();
		// Create a validator instance for each validation (one-time use object) 
		com.github.erosb.jsonsKema.Validator validator = com.github.erosb.jsonsKema.Validator.forSchema(schema);
		return validator;
	}
	
	public com.github.erosb.jsonsKema.Validator getValidatorFromFile() throws FileNotFoundException { 
		String dataContent = null;
		try {
			dataContent = FileUtils.readFileToString(this.schema, StandardCharsets.UTF_8);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		return getValidatorFromString(dataContent);
	}
	
 
}
