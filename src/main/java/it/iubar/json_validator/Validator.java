package it.iubar.json_validator;

 
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.File;


// throws IOException
public class Validator {
	
	public void run()  {
		
		List<String> names = new ArrayList<String>();
		List<Boolean> result = new ArrayList<Boolean>();;
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
			result.add(validate(names.get(i)));
		
		int passed = 0;
	    for (Boolean value : result) {
	        if (value.booleanValue())
	            passed ++;
	    }
	    int error = result.size()-passed;
	    
	    System.out.print("\nTOTAL [PASSED: " + passed +"][ERROR: " + error + "]\n");
	    
	    Names();
	    
	}
	
	public boolean validate(String data_name)  {
		
		boolean passed = false;
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		InputStream inputSchema = loader.getResourceAsStream("schema.json");
		JSONObject rawSchema = new JSONObject(new JSONTokener(inputSchema));
		//System.out.println(rawSchema);
		Schema schema = SchemaLoader.load(rawSchema);
		InputStream inputData = loader.getResourceAsStream(data_name);
		JSONObject data = new JSONObject(new JSONTokener(inputData));
		//System.out.println(data);
		System.out.println("\nValidation of: " + data_name);
		try {
			  schema.validate(data);
				System.out.println("JSON PASSED");
				passed = true;

			} catch (ValidationException e) {
			  // prints #/rectangle/a: -5.0 is not higher or equal to 0
				System.out.print("JSON ERROR: ");
				passed = false;
				System.out.print(e.getMessage()+"\n");
				  e.getCausingExceptions().stream()
				      .map(ValidationException::getMessage)
				      .forEach(System.out::println);
			}
		return passed;
	}
	
	
	
	public void Names(){
	        List<String> results = new ArrayList<String>();
	                  
	        File[] files = new File("src/resources/").listFiles(); 

	        for (File file : files) {
	            if (file.isFile()) {
	                if (!file.getName().equals("schema.json")) {
	                    results.add(file.getName());
	                }                
	            }
	            
	        }
	        for (int i=0; i<results.size(); i++) {
	            System.out.println(results.get(i));
	        }
	    }

	

}
