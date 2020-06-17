package it.iubar.json;

import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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

import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

public class GetContent {
	
	static {
		GetContent.disableSslVerification();
	}

	private static final String HOST_NAME = "192.168.0.121";
	
	public static String BASE_ADDRESS = "https://" + HOST_NAME + "/svn/java/iubar-paghe-test/trunk/src/test/resources/cedolini/json";
	
	private static final Logger LOGGER = Logger.getLogger(GetContent.class.getName());

	/*
	 * A NIO implementation
	 */
	public static void getContent1(URL sourceUrl, File outFile) throws IOException {
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
	private static long getContent2(URL sourceUrl, File outFile) throws IOException{
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
	private static void getContent3(URL sourceUrl, File outFile) throws IOException {

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
 
	static void disableSslVerification() {
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
				     if(hostname.equals(HOST_NAME)) {
				         return true; 
				     }
				     return false;
				}
			};

			// Install the all-trusting host verifier
			HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
		} catch (NoSuchAlgorithmException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		} catch (KeyManagementException e) {
			LOGGER.log(Level.SEVERE, e.getMessage());
		}
	}	
}
