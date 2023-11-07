package org.springframework.samples.petclinic;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParasoftWatcher implements BeforeEachCallback, TestWatcher  {

	private static final String CTP_BASE_URL = "http://54.190.117.79:8080";
	private static final String CTP_USER = "demo";
	private static final String CTP_PASS = "demo-user";
	private static final int CTP_ENV_ID = 32;
	private static final String TEST_SESSION_TAG = "PetClinicJenkins-Jtest";
	private static String sessionId;
	private static String baselineId;

	private static final Logger log = LoggerFactory.getLogger(ParasoftWatcher.class);

	static {
		log.info("Starting session with CTP");
		
		URL url = null;
		try {
			url = new URL(System.getProperty("CTP_BASE_URL", CTP_BASE_URL));
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		// CTP connection
		RestAssured.baseURI = url.getProtocol()+"://"+url.getHost();
	    RestAssured.port = url.getPort();
	    RestAssured.authentication = RestAssured.basic(System.getProperty("CTP_USER", CTP_USER), System.getProperty("CTP_PASS", CTP_PASS));
		log.info("baseURI = " + RestAssured.baseURI);
		log.info("port = " + RestAssured.port);
	    log.info("Calling... POST /em/api/v3/environments/" + CTP_ENV_ID + "/agents/session/start");
	    sessionId = RestAssured.with().contentType(ContentType.JSON).post("em/api/v3/environments/" + CTP_ENV_ID + "/agents/session/start").body().jsonPath().getString("session");
	    log.info("sessionId from session start: " + sessionId);
	    
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());
	}

	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		String testId = getTestId(context);
		log.info("Starting test: " + testId);
		log.info("Calling... POST /em/api/v3/environments/" + CTP_ENV_ID + "/agents/test/start");
		Response response = RestAssured.with().contentType(ContentType.JSON).body("{\"test\":\"" + testId + "\"}").post("em/api/v3/environments/" + CTP_ENV_ID + "/agents/test/start");
		log.info("Response Status Code: " + response.getStatusCode());
        log.info("Response Payload: " + response.getBody().asString());
	}

	@Override
	public void testSuccessful(ExtensionContext context) {
		String testId = getTestId(context);
		log.info("Stopping test (success): " + testId);
		StringBuilder bodyBuilder = new StringBuilder();
		bodyBuilder.append('{');
		bodyBuilder.append("\"test\":\"" + testId + "\"");
		bodyBuilder.append(',');
		bodyBuilder.append("\"result\":\"PASS\"");
		bodyBuilder.append('}');
		log.info("Calling... POST /em/api/v3/environments/" + CTP_ENV_ID + "/agents/test/stop");
		Response response = RestAssured.with().contentType(ContentType.JSON).body(bodyBuilder.toString()).post("em/api/v3/environments/" + CTP_ENV_ID + "/agents/test/stop");
		log.info("Response Status Code: " + response.getStatusCode());
        log.info("Response Payload: " + response.getBody().asString());
    }
	

	@Override
	public void testFailed(ExtensionContext context, Throwable cause) {
		String testId = getTestId(context);
		log.info("Stopping test (fail): " + testId);
		StringBuilder bodyBuilder = new StringBuilder();
		bodyBuilder.append('{');
		bodyBuilder.append("\"test\":\"" + testId + "\"");
		bodyBuilder.append(',');
		bodyBuilder.append("\"result\":\"FAIL\"");
		bodyBuilder.append(',');
		bodyBuilder.append("\"message\":\"" + cause.getMessage() + "\"");
		bodyBuilder.append('}');
		log.info("Calling... POST /em/api/v3/environments/" + CTP_ENV_ID + "/agents/test/stop");
		Response response = RestAssured.with().contentType(ContentType.JSON).body(bodyBuilder.toString()).post("em/api/v3/environments/" + CTP_ENV_ID + "/agents/test/stop");
		log.info("Response Status Code: " + response.getStatusCode());
        log.info("Response Payload: " + response.getBody().asString());
	}

	private static String getTestId(ExtensionContext context) {
		return context.getTestClass().get().getName() + '#' + context.getTestMethod().get().getName();
	}

	static class ShutdownHook extends Thread {
		@Override
		public void run() {
			log.info("ShutdownHook is called");
			baselineId = System.getProperty("baselineId", "latestBaseline");
			RestAssured.with().contentType(ContentType.JSON).post("em/api/v3/environments/" + CTP_ENV_ID + "/agents/session/stop");
			StringBuilder bodyBuilder = new StringBuilder();
			bodyBuilder.append('{');
			bodyBuilder.append("\"sessionTag\":\"" + TEST_SESSION_TAG + "\"");
			bodyBuilder.append(',');
			bodyBuilder.append("\"analysisType\":\"FUNCTIONAL_TEST\"");
			bodyBuilder.append('}');
			log.info("Publish coverage and test results to DTP");
			log.info("Calling... POST /em/api/v3/environments/" + CTP_ENV_ID + "/coverage/" + sessionId);
			Response response1 = RestAssured.with().contentType(ContentType.JSON).body(bodyBuilder.toString()).post("em/api/v3/environments/" + CTP_ENV_ID + "/coverage/" + sessionId) ;
			log.info("Response Status Code: " + response1.getStatusCode());
			log.info("Response Payload: " + response1.getBody().asString());
			
			log.info("Setting this run with baselineId");
			log.info("Calling... POST /em/api/v3/environments" + CTP_ENV_ID + "/coverage/baselines/" + baselineId);
			Response response2 = RestAssured.with().contentType(ContentType.JSON).body("string").post("em/api/v3/environments/" + CTP_ENV_ID + "/coverage/baselines/" + baselineId); 
			log.info("Response Status Code: " + response2.getStatusCode());
			log.info("Response Payload: " + response2.body().asString());
		}
	}
}