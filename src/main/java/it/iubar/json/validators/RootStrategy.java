package it.iubar.json.validators;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.json.JSONObject;
import org.json.JSONTokener;

abstract public class RootStrategy implements IValidator {

	protected File schema = null;

	@Override
	public File getSchema() {
		return schema;
	}

	@Override
	public void setSchema(File schema) {
		this.schema = schema;
	}
	

	
}
