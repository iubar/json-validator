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
				
		for (int i = 0; i<names.size() ; i++) {
			result.add(validate(names.get(i)));
		}
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
	
	
	
	public List<JSONObject> JsonFiles (){
        List<String> results = new ArrayList<String>();
        String directory = "" + targetFolder;
      // LOGGER.info(final_path);
                    
       // File f = new File(directory).getAbsoluteFile();
      //  System.out.println(f);
        boolean b = this.targetFolder.isDirectory();
        
        File[] files = this.targetFolder.listFiles();
        
        for (File file : files) {
            if (file.isFile()) {                
                results.add(file.getName()); 
            }  
        }
               
        List<JSONObject> JSONlist = new ArrayList<JSONObject>();
        
        
        
        for (int i=0; i<results.size(); i++) {
            FileInputStream objFileInputStream=null;
            try {
                objFileInputStream = new FileInputStream(directory + "\\" + results.get(i));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            JSONlist.add(new JSONObject(new JSONTokener(objFileInputStream)));
        }
        
        return JSONlist;
    }
}
