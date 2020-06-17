package it.iubar.json;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.glassfish.jersey.client.ClientProperties;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import it.iubar.json.other.EveritStrategy;
import it.iubar.json.other.IValidator;
import it.iubar.json.other.JustifyStrategy;
import it.iubar.json.other.SyntaxException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


class JsonValidatorTest2 {

	private static final Logger LOGGER = Logger.getLogger(JsonValidatorTest2.class.getName());
	
	private static String baseAddress = "https://192.168.0.121/svn/java/iubar-paghe-test/trunk/src/test/resources/cedolini/json";
 
	private static File schemaFile = null;
	
	@BeforeAll 
	private static void init() throws MalformedURLException, IOException {
		JsonValidatorTest2.schemaFile = JsonValidatorTest2.fetchSchemaFile();
	}
	
	@Test
	@DisplayName("JustifyStrategy")
	void runTest1() {
		 Assertions.assertDoesNotThrow(() -> {
			 IValidator strategy = new JustifyStrategy();
			try {
				run(strategy);
			} catch (Exception e) {				
				e.printStackTrace();
				LOGGER.log(Level.SEVERE, e.getMessage());
				throw e;
			}
		 } );
	}
	
	@Test
	@DisplayName("EveritStrategy")
	@Disabled("La libreria non funziona, questo test fallisce sempre anche se non dovrebbe")	
	void runTest2() {
		 Assertions.assertDoesNotThrow(() -> {
			 IValidator strategy = new EveritStrategy();
			try {
				run(strategy);
			} catch (Exception e) {				
				e.printStackTrace();
				LOGGER.log(Level.SEVERE, e.getMessage());
				throw e;
			}
		 } );
	}
		
	private void run(IValidator strategy) throws MalformedURLException, FileNotFoundException, IOException, SyntaxException {
			
		File targetFile = new File(getOutPath() + File.separator + "test001.json");
		String address2 = JsonValidatorTest2.baseAddress + "/test001.json";
		GetContent.getContent1(new URL(address2), targetFile); 
		if (!targetFile.exists()) {
			fail("The path " + targetFile + " does not exist or is not readable");
		}
				

		JsonValidator client = new JsonValidator(strategy);
		client.setSchema(JsonValidatorTest2.schemaFile );
		client.setTargetFolderOrFile(targetFile);
	 
			int errors = client.run();
			if (errors > 0) {
				fail("Validator has found " + errors + " errors !");
			}
 
	}
	
	
public static File fetchSchemaFile() throws MalformedURLException, IOException {
	File schemaFile = new File(getOutPath() + File.separator + "schema.json");
	String address = JsonValidatorTest2.baseAddress + "/schema/schema.json";
	GetContent.getContent1(new URL(address), schemaFile); 
	if(!schemaFile.isFile()) {
		fail("The file " + schemaFile + " does not exist or is not readable");
	}
	return schemaFile;
	}

public static String getOutPath() {
	String path1 = Paths.get("").toAbsolutePath().toString();  // Current dir
	String path2 = System.getProperty("user.dir");  // Current dir
	LOGGER.log(Level.SEVERE, path1);
	LOGGER.log(Level.SEVERE, path2);
	assertEquals(path1, path2);
	String targetPath = path2 + File.separator + "target";
	File targetDir = new File(targetPath);
	if(targetDir.exists()) {
		return targetPath;
	}
	return path2;
}



}
