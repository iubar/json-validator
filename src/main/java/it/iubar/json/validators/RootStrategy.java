package it.iubar.json.validators;

import java.io.File;

abstract public class RootStrategy implements IValidator {

	protected File schema = null;

	@Override
	public File getSchema() {
		return this.schema;
	}

	@Override
	public void setSchema(File schema) {
		this.schema = schema;
	}

}
