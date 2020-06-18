package it.iubar.json.validators;

import java.io.File;
import java.io.FileNotFoundException;

public interface IValidator {

	public int validate(File file) throws FileNotFoundException;
	
	public File getSchema();

	public void setSchema(File schema);
}
