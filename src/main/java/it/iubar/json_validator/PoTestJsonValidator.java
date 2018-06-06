package it.iubar.json_validator;

import java.io.IOException;
import java.io.InputStream;

import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

public class PoTestJsonValidator {
	
	public void main(String[] args) throws IOException {
		try (InputStream inputSchema = getClass().getResourceAsStream("schema.json")) {
			  JSONObject rawSchema = new JSONObject(new JSONTokener(inputSchema));
			  Schema schema = SchemaLoader.load(rawSchema);
			  
			  InputStream inputData = getClass().getResourceAsStream("data01.json");
			  JSONObject data = new JSONObject(new JSONTokener(inputData));
			  schema.validate(data); // throws a ValidationException if this object is invalid
			  System.out.print("RISULTATO: " );
			}
	}
}
