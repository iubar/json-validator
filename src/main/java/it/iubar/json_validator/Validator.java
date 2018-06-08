package it.iubar.json_validator;

import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

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
	private File targetFolder = null;
	
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
		
	public void setTargetFolder(File directory) {
		this.targetFolder = directory;
	}
	
	public void run()  {
		
		List<JSONObject> names = JsonFiles();
		List<Boolean> result = new ArrayList<Boolean>();
				
		for (int i = 0; i<18 ; i++)
			result.add(validate(names.get(i)));
			
		int passed = 0;
	    for (Boolean value : result) {
	        if (value.booleanValue())
	            passed ++;
	    }
	    int error = result.size()-passed;

	    LOGGER.info("\nTOTAL: " + result.size() + "  [PASSED: " + passed +"][ERROR: " + error + "]\n");

	}
	
	public boolean validate(JSONObject file)  {
		
		boolean passed = false;
		
		Schema schemaJSON = SchemaLoader.load(this.schema); 
		
		System.out.println("\nValidation of: " + ""); 
		
		try {
			    schemaJSON.validate(file);
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
	
	
	
	public List<JSONObject> JsonFiles(){
		
        List<JSONObject> results = new ArrayList<JSONObject>();
        /*
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        
        String str = loader.getResource("schema.json").getFile();
        try {
			str = URLDecoder.decode(str, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
        File prova = new File (str);
        
        String final_path = prova.getParent();
        
      // LOGGER.info(final_path);
        	        
        File f = new File(final_path).getAbsoluteFile();
        boolean b = f.isDirectory();
        
        File[] files = f.listFiles();
        
        for (File file : files) {
            if (file.isFile()) {
                if (!file.getName().equals("schema.json"))
                    results.add(file.getName());                
            }  
        }*/
        return results;
    }
}
