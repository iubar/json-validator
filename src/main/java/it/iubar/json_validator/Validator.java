package it.iubar.json_validator;

 
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

// throws IOException
public class Validator {
	
	public void run()  {
		
		List<String> names = new ArrayList<String>();;
		for (int i = 1; i<17 ; i++)
		{
			if (i<10)
				names.add("test0" + i + ".json");
			else 
				names.add("test" + i + ".json");
		}
		names.add("test17A.json");
		names.add("test17B.json");
		
		for (int i = 0; i<18 ; i++)
			validate(names.get(i));
	}
	public void validate(String data_name)  {
		
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream inputSchema = loader.getResourceAsStream("schema.json");
		JSONObject rawSchema = new JSONObject(new JSONTokener(inputSchema));
		//System.out.println(rawSchema);
		Schema schema = SchemaLoader.load(rawSchema);
	
		InputStream inputData = loader.getResourceAsStream(data_name);
		JSONObject data = new JSONObject(new JSONTokener(inputData));
		//System.out.println(data);
		System.out.println("\nValidazione di: " + data_name);
		try {
			  schema.validate(data);
				System.out.println("JSON CORRETTO");

			} catch (ValidationException e) {
			  // prints #/rectangle/a: -5.0 is not higher or equal to 0
				System.out.print("JSON ERRATO: ");
				System.out.print(e.getMessage()+"\n");
				  e.getCausingExceptions().stream()
				      .map(ValidationException::getMessage)
				      .forEach(System.out::println);
			}
	}

}
