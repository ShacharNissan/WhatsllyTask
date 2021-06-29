import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

class WhatsllyUrlTest {
	private String START_URL = "https://api-";
	private String VALID_END_URL = ".whatslly.com/test/ping.json"; // Valid and good Whatslly's URL.
	private String INVALID_END_URL = ".whatslly.com/test"; // Invalid Whatslly's URL.
	private String[] REGIONS = {"us1", "br1", "eu1", "st1"};
	
	private String JSON_SUCCESS_KEY = "success";
	private String JSON_HOST_KEY = "host";
	private String JSON_HOST_VALUE_OK = ".whatslly.com";
	private boolean JSON_SUCCESS_VALUE_OK = true;
	
	private int OK_RESPONSE_CODE = 200;
	private int NOT_FOUND_RESPONSE_CODE = 404;
	
	/*
	 * Test each of available regions in REGIONS array for valid response.
	 * URL inputs: 	https://api-us1.whatslly.com/test/ping.json,
	 *  			https://api-br1.whatslly.com/test/ping.json,
	 *  			https://api-eu1.whatslly.com/test/ping.json,
	 *  			https://api-st1.whatslly.com/test/ping.json
	 * Expected results : Response code 200.
	 */
	@Test
	void testUrlAllRegionsOk() {
		String url = "";
		for (int i = 0; i < REGIONS.length ; i++) {
			url = START_URL + REGIONS[i] + VALID_END_URL;
			
			try {
				int code = getResponseCode(url);
				assertEquals(OK_RESPONSE_CODE, code);
			}catch (Exception e) {
				fail("Test testUrlAllRegionsOk - failed to load URL = " + url);
			}
		}
	}
	
	/*
	 * Test one of available regions in REGIONS array for invalid response.
	 * URL input: https://api-us1.whatslly.com/test
	 * Expected result : Response code 404.
	 */
	@Test
	void testUrlInvalid() {
		String url = START_URL + REGIONS[0] + INVALID_END_URL;
		int code = 0;
		try {
			code = getResponseCode(url);
			assertEquals(NOT_FOUND_RESPONSE_CODE, code);
		}catch (Exception e) {
			fail("Test testUrlInvalid - unexpected error occurred.");
		}
	}
	
	/*
	 * Gets json from URL and validate JSON_SUCCESS_KEY ("success") key.
	 * URL input: https://api-us1.whatslly.com/test/ping.json
	 * Expected result : JSON_SUCCESS_VALUE_OK (true).
	 */
	@Test
	void testSuccessStatusValid() {
		String url = START_URL + REGIONS[0] + VALID_END_URL;
		try {
			JSONObject json = readJsonFromUrl(url);
			assertEquals(JSON_SUCCESS_VALUE_OK, json.get(JSON_SUCCESS_KEY));
		}catch (Exception e) {
			fail("Test testSuccessStatusValid - failed to validate.");
		}
	}
	
	/*
	 * Gets json from URL and validate JSON_HOST_KEY ("host") key.
	 * URL input: https://api-us1.whatslly.com/test/ping.json
	 * Expected result : START_URL + REGIONS[0] + JSON_HOST_VALUE_OK (https://api-us1.whatslly.com).
	 */
	@Test
	void testHostStatusValid() {
		String url = START_URL + REGIONS[0] + VALID_END_URL;
		String testUrl = START_URL + REGIONS[0] + JSON_HOST_VALUE_OK;
		try {
			JSONObject json = readJsonFromUrl(url);
			assertEquals(testUrl, json.get(JSON_HOST_KEY));
		}catch (Exception e) {
			fail("Test testHostStatusValid - failed to validate.");
		}
	}

	// Takes all Input received in rd (Reader) and load it into a single String.
	private String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	// Given a URL, returns output as JsonObject.
	private JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} finally {
			is.close();
		}
	}
	
	// Given a URL, return status / response code.
	private int getResponseCode(String url) throws IOException {
		URL urlConnection = new URL(url);
		HttpURLConnection connection = (HttpURLConnection)urlConnection.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();

		return connection.getResponseCode();
	}
}
