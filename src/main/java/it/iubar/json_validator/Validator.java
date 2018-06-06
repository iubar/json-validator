package it.iubar.json_validator;

 
import java.io.InputStream;
 

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

// throws IOException
public class Validator {
	
	public void run()  {
		validate("data01.json");
	}
	public void validate(String data_name)  {
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream inputSchema = loader.getResourceAsStream("schema.json");
		JSONObject rawSchema = new JSONObject(new JSONTokener(inputSchema));
		//System.out.println(rawSchema);
		Schema schema = SchemaLoader.load(rawSchema);
	
		InputStream inputData = loader.getResourceAsStream(data_name);
		JSONObject data = new JSONObject(new JSONTokener(inputData));
		System.out.println(data);
		try {
			  schema.validate(data);
			} catch (ValidationException e) {
			  // prints #/rectangle/a: -5.0 is not higher or equal to 0
				System.out.println(e.getMessage());
				  e.getCausingExceptions().stream()
				      .map(ValidationException::getMessage)
				      .forEach(System.out::println);
			}
	}

}
