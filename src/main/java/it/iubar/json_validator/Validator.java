package it.iubar.json_validator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


import org.everit.json.schema.Schema;
import org.everit.json.schema.ValidationException;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.logging.Logger;

// throws IOException
public class Validator {
	
	private static final Logger LOGGER = Logger.getLogger(Validator.class.getName());
	
	private JSONObject schema = new JSONObject();
	private File targetFolderOrFile = null;
	
	/**
	 * 
	 * @param schema = path del file schema
	 */
	public void setSchema(File schema) {
		try {
		FileInputStream objFileInputStream = null;
		objFileInputStream = new  FileInputStream(schema);
		this.schema = new JSONObject(new JSONTokener(objFileInputStream));
		}catch (FileNotFoundException ex) {
        }
	}
		
	public void setTargetFolderOrFile(File directory_or_file) {
		this.targetFolderOrFile = directory_or_file;
	}
	
	public void run()  {
		
		Map<String, JSONObject> map = getJsonFilesMap();
		List<Boolean> result = new ArrayList<Boolean>();
		for (Map.Entry<String, JSONObject> entry : map.entrySet()){

			result.add(validate(entry));
		}
		int passed = 0;
	    for (Boolean value : result) {
	        if (value.booleanValue())
	            passed ++;
	    }
	    int error = result.size()-passed;

	    LOGGER.info("\nTOTAL: " + result.size() + "  [PASSED: " + passed +"][ERROR: " + error + "]\n");
	}
	
	public boolean validate(Entry<String, JSONObject> entry)  {
		boolean passed = false;
		
		
		String key = entry.getKey(); 
		JSONObject value = entry.getValue();
		
		Schema schemaJSON = SchemaLoader.load(this.schema); 
		System.out.println("\n########## Validation of: " + key + " ##########");
		
		try {
			    schemaJSON.validate(value);
				System.out.println("JSON PASSED");
				passed = true;

			} catch (ValidationException e) {
			  // prints #/rectangle/a: -5.0 is not higher or equal to 0
				System.out.println("\nJSON ERRORS: ");
				passed = false;
				
				System.out.print(e.getMessage()+"\n");
				  e.getCausingExceptions().stream()
				      .map(ValidationException::getMessage)
				      .forEach(System.out::println);
				  
				  List<ValidationException> list = e.getCausingExceptions();
				  System.out.println();
				  for (ValidationException validationException : list) {
					  String violation = validationException.getPointerToViolation();
					  String[] parts = violation.split("/");
					  String name = parts[parts.length-1];
					  int count = validationException.getViolationCount();
					  System.out.println("******************************************************");
					  System.out.println("Object name: " + name);
					  System.out.println("Violation: " + count);
					  List<String> messages = validationException.getAllMessages();
					  for (String message : messages) {
						  System.out.println("	- " + message);
					  }
					  System.out.println();
				}	  
			}
		 System.out.println("################################################\n");
		return passed;
	}
		
	public Map<String, JSONObject> getJsonFilesMap (){
		Map<String, JSONObject> jsonMap = new HashMap<String, JSONObject>();
		try {	        	        
	        if (targetFolderOrFile.isDirectory()) {
	        	String directory = "" + targetFolderOrFile;
	        	File[] files = this.targetFolderOrFile.listFiles();
	        	
	        	List<String> results = new ArrayList<String>();
	            for (File file : files) {
	                if (file.isFile()) {                
	                    results.add(file.getName()); 
	                }  
	            }
	            
	            for (int i=0; i<results.size(); i++) {	                
                	String filename = directory + File.separator + results.get(i);
                	FileInputStream objFileInputStream = new FileInputStream(filename);
                    jsonMap.put(results.get(i), new JSONObject(new JSONTokener(objFileInputStream)));	           
	            }
	        } else {
	        	FileInputStream objFileInputStream = new FileInputStream(this.targetFolderOrFile);
	            jsonMap.put(this.targetFolderOrFile.getName(), new JSONObject(new JSONTokener(objFileInputStream)));
	        }        
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
     
        return jsonMap;
    }
}
