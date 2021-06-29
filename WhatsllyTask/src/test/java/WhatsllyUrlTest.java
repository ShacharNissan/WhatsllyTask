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
	private static String VALID_URL = "https://api-us1.whatslly.com/test/ping.json"; // Valid and good Whatslly's URL.
	private static String INVALID_URL = "https://api-us1.whatslly.com/test"; // Invalid Whatslly's URL.
	
	@Test
	void test() throws IOException, JSONException {
		String goodURL = "https://api-us1.whatslly.com/test/ping.json";
		String notFoundURL = "https://api-us1.whatslly.com/te";
		String noAuthURL = "https://api-us1.whatslly.com/";
		JSONObject json = readJsonFromUrl(goodURL);
		System.out.println(json.toString());
		System.out.println(json.get("success"));

		System.out.println(String.format("return code: %d", getResponseCode(goodURL)));
		System.out.println(String.format("return code: %d", getResponseCode(notFoundURL)));
		System.out.println(String.format("return code: %d", getResponseCode(noAuthURL)));
		
		fail("Not yet implemented");
	}

	// Takes all Input received in rd (Reader) and load it into a single String.
	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	// Given a URL, returns output as JsonObject.
	private static JSONObject readJsonFromUrl(String url) throws IOException, JSONException {
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
	private static int getResponseCode(String url) throws IOException {
		URL urlConnection = new URL(url);
		HttpURLConnection connection = (HttpURLConnection)urlConnection.openConnection();
		connection.setRequestMethod("GET");
		connection.connect();

		return connection.getResponseCode();
	}
}
