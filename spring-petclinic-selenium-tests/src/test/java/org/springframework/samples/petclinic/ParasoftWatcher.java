package org.springframework.samples.petclinic;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ParasoftWatcher implements BeforeEachCallback, TestWatcher  {

	private static final String CTP_BASE_URL = "http://54.187.104.213:8080";
	private static final String CTP_USER = "demo";
	private static final String CTP_PASS = "demo-user";
	private static final String CTP_ENV_ID = "32";
	private static String sessionId;
	private static final Logger log = LoggerFactory.getLogger(ParasoftWatcher.class);
	
	static {
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
		
	    Response response = RestAssured.with().contentType(ContentType.JSON).post("em/api/v3/environments/" + System.getProperty("CTP_ENV_ID", CTP_ENV_ID) + "/agents/session/start");
	    ResponseBody responseBody = response.body(); 
	    log.info("Starting session with CTP"); // logging
	    log.info(responseBody.prettyPrint()); // logging
	    sessionId = responseBody.jsonPath().getString("session");
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());
		log.info("sessionId: " + sessionId);
	}

	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		String testId = getTestId(context);
		
		Response response = RestAssured.with().contentType(ContentType.JSON).body("{\"test\":\"" + testId + "\"}").post("em/api/v3/environments/" + System.getProperty("CTP_ENV_ID", CTP_ENV_ID) + "/agents/test/start");
		log.info("Starting test"); // logging
	    log.info(response.body().prettyPrint()); // logging
	}

	@Override
	public void testSuccessful(ExtensionContext context) {
		String testId = getTestId(context);
		StringBuilder bodyBuilder = new StringBuilder();
		bodyBuilder.append('{');
		bodyBuilder.append("\"test\":\"" + testId + "\"");
		bodyBuilder.append(',');
		bodyBuilder.append("\"result\":\"PASS\"");
		bodyBuilder.append('}');
		
		Response response = RestAssured.with().contentType(ContentType.JSON).body(bodyBuilder.toString()).post("em/api/v3/environments/" + System.getProperty("CTP_ENV_ID", CTP_ENV_ID) + "/agents/test/stop");
	    log.info("Stopping test - passed"); // logging
	    log.info(response.body().prettyPrint()); // logging
	}

	@Override
	public void testFailed(ExtensionContext context, Throwable cause) {
		String testId = getTestId(context);
		StringBuilder bodyBuilder = new StringBuilder();
		bodyBuilder.append('{');
		bodyBuilder.append("\"test\":\"" + testId + "\"");
		bodyBuilder.append(',');
		bodyBuilder.append("\"result\":\"FAIL\"");
		bodyBuilder.append(',');
		bodyBuilder.append("\"message\":\"" + cause.getMessage() + "\"");
		bodyBuilder.append('}');
		
		Response response = RestAssured.with().contentType(ContentType.JSON).body(bodyBuilder.toString()).post("em/api/v3/environments/" + System.getProperty("CTP_ENV_ID", CTP_ENV_ID) + "/agents/test/stop");
	    log.info("Stopping test - failed"); // logging
	    log.info(response.body().prettyPrint()); // logging
	}
	
	private static String getTestId(ExtensionContext context) {
		return context.getTestClass().get().getName() + '#' + context.getTestMethod().get().getName();
	}

	static class ShutdownHook extends Thread {
		@Override
		public void run() {
			Response response1 = RestAssured.with().contentType(ContentType.JSON).post("em/api/v3/environments/" + System.getProperty("CTP_ENV_ID", CTP_ENV_ID) + "/agents/session/stop");
		    log.info("Stopping session"); // logging
		    log.info(response1.body().prettyPrint()); // logging
			
			StringBuilder bodyBuilder = new StringBuilder();
			bodyBuilder.append('{');
			bodyBuilder.append("\"sessionTag\":\"PetClinicJenkins-Jtest\"");
			bodyBuilder.append(',');
			bodyBuilder.append("\"analysisType\":\"FUNCTIONAL_TEST\"");
			bodyBuilder.append('}');
			
			Response response2 = RestAssured.with().contentType(ContentType.JSON).body(bodyBuilder.toString()).post("em/api/v3/environments/" + System.getProperty("CTP_ENV_ID", CTP_ENV_ID) + "/coverage/" + sessionId);
			log.info("Publishing to DTP - POST v3/environments/{envId}/coverage/"+sessionId); // logging
		    log.info(response2.body().prettyPrint()); // logging
		}
	}
}