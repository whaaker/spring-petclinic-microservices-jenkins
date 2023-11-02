package org.springframework.samples.petclinic;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestWatcher;

public class ParasoftWatcher implements BeforeEachCallback, TestWatcher  {

	private static final String CTP_BASE_URL = "http://localhost:8080";
	private static final String CTP_USER = "demo";
	private static final String CTP_PASS = "demo-user";
	private static final String CTP_ENV_ID = "32";
	private static String sessionId;

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
		sessionId = RestAssured.with().contentType(ContentType.JSON).post("em/api/v3/environments/" + System.getProperty("CTP_ENV_ID", CTP_ENV_ID) + "/agents/session/start").body().jsonPath().getString("session");
		Runtime.getRuntime().addShutdownHook(new ShutdownHook());
	}

	@Override
	public void beforeEach(ExtensionContext context) throws Exception {
		String testId = getTestId(context);
		RestAssured.with().contentType(ContentType.JSON).body("{\"test\":\"" + testId + "\"}").post("em/api/v3/environments/" + System.getProperty("CTP_ENV_ID", CTP_ENV_ID) + "/agents/test/start");
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
		RestAssured.with().contentType(ContentType.JSON).body(bodyBuilder.toString()).post("em/api/v3/environments/" + System.getProperty("CTP_ENV_ID", CTP_ENV_ID) + "/agents/test/stop");
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
		RestAssured.with().contentType(ContentType.JSON).body(bodyBuilder.toString()).post("em/api/v3/environments/" + System.getProperty("CTP_ENV_ID", CTP_ENV_ID) + "/agents/test/stop");
	}
	
	private static String getTestId(ExtensionContext context) {
		return context.getTestClass().get().getName() + '#' + context.getTestMethod().get().getName();
	}

	static class ShutdownHook extends Thread {
		@Override
		public void run() {
			RestAssured.with().contentType(ContentType.JSON).post("em/api/v3/environments/" + System.getProperty("CTP_ENV_ID", CTP_ENV_ID) + "/agents/session/stop");
			StringBuilder bodyBuilder = new StringBuilder();
			bodyBuilder.append('{');
			bodyBuilder.append("\"sessionTag\":\"selenium\"");
			bodyBuilder.append(',');
			bodyBuilder.append("\"analysisType\":\"FUNCTIONAL_TEST\"");
			bodyBuilder.append('}');
			String response = RestAssured.with().contentType(ContentType.JSON).body(bodyBuilder.toString()).post("em/api/v3/environments/" + System.getProperty("CTP_ENV_ID", CTP_ENV_ID) + "/coverage/" + sessionId).body().asString();
			System.out.println(response);
		}
	}
}