package it.iubar.json.validators;

import java.io.File;
import java.io.FileNotFoundException;

public interface IValidator {

	File getSchema();

	void setSchema(File schema);

	int validate(File file) throws FileNotFoundException;
}
