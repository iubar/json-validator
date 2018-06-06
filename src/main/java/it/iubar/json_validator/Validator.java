package it.iubar.json_validator;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.ValidationException;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

// throws IOException
public class Validator {
	
	public void run() throws IOException {
		validate();
	}
	public void validate() throws IOException {
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		try (InputStream inputSchema = loader.getResourceAsStream("schema.json")){
		JSONObject rawSchema = new JSONObject(new JSONTokener(inputSchema));
		System.out.print(rawSchema);
		Schema schema = SchemaLoader.load(rawSchema);
			  
		InputStream inputData = loader.getResourceAsStream("data01.json");
		JSONObject data = new JSONObject(new JSONTokener(inputData));
		schema.validate(data); // throws a ValidationException if this object is invalid		
		} /*catch (ValidationException e) {
			  System.out.println(e.getMessage());
			}*/
	}


}
