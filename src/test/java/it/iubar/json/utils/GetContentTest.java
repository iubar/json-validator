package it.iubar.json.utils;

import it.iubar.json.validators.RealDataValidatorTest;

import static org.junit.jupiter.api.Assertions.fail;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("LocalTestOnly")
class GetContentTest {

	private static final Logger LOGGER = Logger.getLogger(GetContentTest.class.getName());

	// private static final String BASE_URL =  RealDataValidatorTest.BASE_ADDRESS ;
	private static final String BASE_URL =  "https://" + GetContent.HOST_NAME;

	private static URL url = null;

	private static File file = null;

	@BeforeAll
	static void setup() throws Exception {
		GetContentTest.file = new File(RealDataValidatorTest.getOutPath() + File.separator + "test001.json");
		try {
			GetContentTest.url = new URL(RealDataValidatorTest.BASE_ADDRESS + "/test001.json");
		} catch (MalformedURLException e) {
			Assertions.fail(e.getMessage());
		}
		disableSSLVerification();
	}


	/**
	 * Errore PKIX — Certificato SSL non riconosciuto
	 * Causa
	 * La JVM non riesce a validare il certificato SSL del server perché:
	 * 
	 * - il server usa un certificato self-signed
	 * - il certificato è firmato da una CA interna/privata non presente nel truststore della JVM
	 * - l'ambiente di test usa HTTP interno con certificati non standard
	 * 	
	 */
	@BeforeEach
	void verificaHostRaggiungibile() {    	
		assertHostRaggiungibile(BASE_URL);
	}

	static void assertHostRaggiungibile(String url) {
		try {
			HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setConnectTimeout(3000);   // 3 secondi max
			conn.setReadTimeout(3000);
			conn.setRequestMethod("HEAD");  // solo header, nessun body
			int code = conn.getResponseCode();
			LOGGER.log(Level.INFO, "Host raggiungibile — HTTP status: " + code);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE,"Host non raggiungibile: " + e.getMessage());
			fail("Host non raggiungibile [" + url + "]: " + e.getMessage(), e);
		}
	}

	static void disableSSLVerification() throws Exception {
		TrustManager[] trustAll = new TrustManager[]{
				new X509TrustManager() {
					public X509Certificate[] getAcceptedIssuers() { return null; }
					public void checkClientTrusted(X509Certificate[] c, String a) {}
					public void checkServerTrusted(X509Certificate[] c, String a) {}
				}
		};

		SSLContext sc = SSLContext.getInstance("TLS");
		sc.init(null, trustAll, new java.security.SecureRandom());
		HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		HttpsURLConnection.setDefaultHostnameVerifier((hostname, session) -> true);
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
		LOGGER.log(Level.INFO, "URL : " + GetContentTest.url);
		try {			
			long bytes = GetContent.getContent2(GetContentTest.url, GetContentTest.file);
			GetContentTest.LOGGER.log(Level.INFO, "bytes saved: " + bytes);
		} catch (KeyManagementException | NoSuchAlgorithmException | IOException e) {
			String error =  e.getMessage();	
			LOGGER.log(Level.SEVERE, error);
			fail(e.getMessage(), e);
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
