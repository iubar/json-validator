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
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;


class PoJsonValidatorTest {

	private static final Logger LOGGER = Logger.getLogger(PoJsonValidatorTest.class.getName());
	
	@BeforeAll 
	private static void init() {
		disableSslVerification();
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
		
	private void run(IValidator strategy) throws MalformedURLException, FileNotFoundException, IOException {
				
		String path1 = Paths.get("").toAbsolutePath().toString();  // Current dir
		String path2 = System.getProperty("user.dir");  // Current dir
		LOGGER.log(Level.SEVERE, path1);
		LOGGER.log(Level.SEVERE, path2);
		assertEquals(path1, path2);		

		String baseAddress = "https://192.168.0.121/svn/java/iubar-paghe-test/trunk/src/test/resources/cedolini/json";
		File schemaFile = new File(path2 + File.separator + "schema.json");
		String address = baseAddress + "/schema/schema.json";
		getContent1(new URL(address), schemaFile); 
		if(!schemaFile.isFile()) {
			fail("The file " + schemaFile + " does not exist or is not readable");
		}
				
		File targetFile = new File(path2 + File.separator + "test002.json");
		String address2 = baseAddress + "/test001.json";
		getContent1(new URL(address2), targetFile); 
		if (!targetFile.exists()) {
			fail("The path " + targetFile + " does not exist or is not readable");
		}
				

		JsonValidator client = new JsonValidator(strategy);
		client.setSchema(schemaFile);
		client.setTargetFolderOrFile(targetFile);
	 
			int errors = client.run();
			if (errors > 0) {
				fail("Validator has found " + errors + " errors !");
			}
 
	}
	
	
//////////////////////////////////////////////////////////
	
	/*
	 * A NIO implementation
	 */
	private void getContent1(URL sourceUrl, File outFile) throws IOException {
		// To read the file from our URL, we'll create a new ReadableByteChannel from the URL stream:			
		URLConnection con = sourceUrl.openConnection();
		final int connectTimeout = 3000; // milliseconds
		final int readTimeout = 5000; // milliseconds
		con.setConnectTimeout(connectTimeout);
		con.setReadTimeout(readTimeout);
		InputStream in = con.getInputStream();
				
		ReadableByteChannel readableByteChannel = Channels.newChannel(in);
		
		// The bytes read from the ReadableByteChannel will be transferred to a FileChannel corresponding to the file that will be downloaded:
			FileOutputStream fileOutputStream = new FileOutputStream(outFile);
			FileChannel fileChannel = fileOutputStream.getChannel();
			// We'll use the transferFrom() method from the ReadableByteChannel class to download the bytes from the given URL to our FileChannel:
			fileOutputStream.getChannel().transferFrom(readableByteChannel, 0, Long.MAX_VALUE);	
			
			fileOutputStream.close();
			fileChannel.close();
	}
	
	
	
	/*
	 * A JErsey
	 */	
	private long getContent2(URL sourceUrl, File outFile) throws IOException{
            long bytesCopied = 0;   	
			Path out = Paths.get(outFile.toString());
			InputStream in = null;
            try {
            	Client client = ClientBuilder.newClient();
            	   client.property(ClientProperties.CONNECT_TIMEOUT, 1000);
            	    client.property(ClientProperties.READ_TIMEOUT,    1000);
                 WebTarget webTarget = client.target(sourceUrl.toString());
                 Invocation.Builder invocationBuilder = webTarget.request(MediaType.TEXT_PLAIN_TYPE);

                 Response response = invocationBuilder.get();

                 if (response.getStatus() != 200) {
                    System.out.println("HTTP status " + response.getStatus());
                    return bytesCopied;
                 }

                in = response.readEntity( InputStream.class );
        
				bytesCopied = Files.copy(in, out, REPLACE_EXISTING );   

   
            }finally {
            	if(in!=null) {        
						in.close(); 
            	}
			}

            return bytesCopied;
     }		
	 
	
	/**
	 * Or with the fluent API if one likes it better
	 * Request.Get("http://host/stuff").execute().saveContent(myFile);
	 * @throws IOException 
	 */
	private void getContent3(URL sourceUrl, File outFile) throws IOException {

		CloseableHttpClient client = HttpClients.createDefault();
		try (CloseableHttpResponse response = client.execute(new HttpGet(sourceUrl.toString()))) {
		    HttpEntity entity = response.getEntity();
		    if (entity != null) {
		        try (FileOutputStream outstream = new FileOutputStream(outFile)) {
		            entity.writeTo(outstream);
		        }
		    }
		}finally {
			if(client!=null) {
				client.close();
			}
		}
		
	}
	
////////////////////////////////////////////
	
//	public Client hostIgnoringClient() {
//	    try
//	    {
//	        SSLContext sslcontext = SSLContext.getInstance( "TLS" );
//	        sslcontext.init( null, null, null );
//	        DefaultClientConfig config = new DefaultClientConfig();
//	        Map<String, Object> properties = config.getProperties();
//	        HTTPSProperties httpsProperties = new HTTPSProperties(
//	                new HostnameVerifier()
//	                {
//	                    @Override
//	                    public boolean verify( String s, SSLSession sslSession )
//	                    {
//	                        return true;
//	                    }
//	                }, sslcontext
//	        );
//	        properties.put( HTTPSProperties.PROPERTY_HTTPS_PROPERTIES, httpsProperties );
//	        config.getClasses().add( JacksonJsonProvider.class );
//	        return Client.create( config );
//	    }
//	    catch ( KeyManagementException | NoSuchAlgorithmException e )
//	    {
//	        throw new RuntimeException( e );
//	    }
//	}
	
 
	private static void disableSslVerification() {
        try {
            // Create a trust manager that does not validate certificate chains
            TrustManager[] trustAllCerts = new TrustManager[] { new X509TrustManager() {
                @Override
                public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                @Override
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    // TODO Auto-generated method stub

                }

                @Override
                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    // TODO Auto-generated method stub

                }
            } };

            // Install the all-trusting trust manager
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());

            // Create all-trusting host name verifier
            HostnameVerifier allHostsValid = new HostnameVerifier() {

                @Override
                public boolean verify(String hostname, SSLSession session) {
                    // TODO Auto-generated method stub
                    return true;
                }
            };

            // Install the all-trusting host verifier
            HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
    }	

}
