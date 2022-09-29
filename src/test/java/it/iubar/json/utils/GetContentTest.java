package it.iubar.json.utils;

import it.iubar.json.validators.RealDataValidatorTest;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("LocalTestOnly")
class GetContentTest {

	private static final Logger LOGGER = Logger.getLogger(GetContentTest.class.getName());

	private static URL url = null;

	private static File file = null;

	@BeforeAll
	public static void init() {
		GetContentTest.file = new File(RealDataValidatorTest.getOutPath() + File.separator + "test001.json");
		try {
			GetContentTest.url = new URL(RealDataValidatorTest.BASE_ADDRESS + "/test001.json");
		} catch (MalformedURLException e) {
			Assertions.fail(e.getMessage());
		}
	}

	@Test
	void test1() {
		try {
			GetContent.getContent1(GetContentTest.url, GetContentTest.file);
		} catch (IOException e) {
			Assertions.fail(e.getMessage());
		}
	}

	@Test
	void test2() {
		try {
			long bytes = GetContent.getContent2(GetContentTest.url, GetContentTest.file);
			GetContentTest.LOGGER.log(Level.INFO, "bytes saved: " + bytes);
		} catch (KeyManagementException | NoSuchAlgorithmException | IOException e) {
			Assertions.fail(e.getMessage());
		}
	}

	@Test
	@Disabled("Se uso Apache HttpClient, per il problema del self-signed cert non ho ancora trovato una soluzione")
	void test3() {
		try {
			GetContent.getContent3(GetContentTest.url, GetContentTest.file);
		} catch (IOException e) {
			Assertions.fail(e.getMessage());
		}
	}
}
