/**
 * @author Shachar Nissan
 * This Task was made as part of Whatslly's recruit test.
 * 29/06/2021
 */
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
	private static final String START_URL = "https://api-";
	private static final String VALID_END_URL = ".whatslly.com/test/ping.json"; // Valid and good Whatslly's URL.
	private static final String INVALID_END_URL = ".whatslly.com/test"; // Invalid Whatslly's URL.
	private static final String[] REGIONS = { "us1", "br1", "eu1", "st1" };

	private static final String JSON_SUCCESS_KEY = "success";
	private static final boolean JSON_SUCCESS_VALUE_OK = true;

	private static final int OK_RESPONSE_CODE = 200;
	private static final int NOT_FOUND_RESPONSE_CODE = 404;
	
	private static final int NUMBER_OF_TESTS = 150;

	/*
	 * Test each of available regions in REGIONS array for valid response.
	 * URL inputs: https://api-us1.whatslly.com/test/ping.json,
	 * https://api-br1.whatslly.com/test/ping.json,
	 * https://api-eu1.whatslly.com/test/ping.json,
	 * https://api-st1.whatslly.com/test/ping.json 
	 * Expected results : Response code 200.
	 */
	@Test
	void testUrlAllRegionsOk() {
		String url = "";
		int code;
		for (int i = 0; i < REGIONS.length; i++) {
			url = START_URL + REGIONS[i] + VALID_END_URL;

			try {
				code = getResponseCode(url);
				assertEquals(OK_RESPONSE_CODE, code);
			} catch (Exception e) {
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
		} catch (Exception e) {
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
		JSONObject json;
		try {
			json = readJsonFromUrl(url);
			assertEquals(JSON_SUCCESS_VALUE_OK, json.get(JSON_SUCCESS_KEY));
		} catch (Exception e) {
			fail("Test testSuccessStatusValid - failed to validate.");
		}
	}

	/*
	 * Preform a load test to the server with NUMBER_OF_TESTS (150) api requests.
	 * input: URL-https://api-us1.whatslly.com/test/ping.json,  NUMBER_OF_TESTS=150
	 * Expected result : true (all tests - OK)
	 */
	@Test
	void testLoad() {
		String url = START_URL + REGIONS[0] + VALID_END_URL;
		int code = 0;
		boolean flag = true;
		for (int i = 0; i < NUMBER_OF_TESTS; i++) {
			try {
				code = getResponseCode(url);
				if (code != OK_RESPONSE_CODE)
					flag = false;
			} catch (Exception e) {
				fail("Test testHostStatusValid - failed to validate.");
			}
		}
		assertEquals(true, flag);
	}

	/*
	 *  Takes all Input received in rd (Reader) and load it into a single String.
	 *  input: Reader rd - reader with data in a buffer.
	 *  output: String - data after append.
	 */
	private static String readAll(Reader rd) throws IOException {
		int cp;
		StringBuilder sb = new StringBuilder();
		
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	/*
	 *  Given a URL, returns output as JsonObject.
	 *  input: String url - the String to parse as URL.
	 *  output: JSONObject - the data received from url in Json format.
	 */
	private static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
		BufferedReader br;
		InputStream is = new URL(url).openStream(); // open connection to URL and return data as InputStream.
		
		try {
			br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
			return new JSONObject(readAll(br)); //convert data from buffer to Json object.
		} finally {
			is.close(); // close connection to URL
		}
	}

	/*
	 * Given a URL, return status / response code.
	 * input: String url - the String to parse as URL.
	 * output: int code - get the status code from an HTTP response massage.
	 */
	private static int getResponseCode(String url) throws IOException {
		int code;
		URL urlConnection = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) urlConnection.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();
		code = connection.getResponseCode();
		connection.disconnect();
		
		return code;
	}
}
