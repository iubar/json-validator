package it.iubar.json.other;

import java.io.File;
import java.io.FileNotFoundException;

public interface IValidator {

	public boolean validate(File file) throws FileNotFoundException, SyntaxException;
	
	public File getSchema();

	public void setSchema(File schema);
}
