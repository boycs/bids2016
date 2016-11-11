package com.app.bids;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReadJson {

	private static String readAll(Reader rd) throws IOException {
		StringBuilder sb = new StringBuilder();
		int cp;
		while ((cp = rd.read()) != -1) {
			sb.append((char) cp);
		}
		return sb.toString();
	}

	public static JSONObject SendAndReadBetResult(String parameter)
			throws IOException, JSONException {
		JSONObject json = null;
		HttpGet getRequest = new HttpGet(parameter);
		try {
			final HttpParams httpParams = new BasicHttpParams();
			HttpConnectionParams.setConnectionTimeout(httpParams, 20000);
			DefaultHttpClient httpClient = new DefaultHttpClient(httpParams);
			HttpResponse getResponse = httpClient.execute(getRequest);
			final int statusCode = getResponse.getStatusLine().getStatusCode();

			if (statusCode != HttpStatus.SC_OK) {
				return null;
			}

			HttpEntity getResponseEntity = getResponse.getEntity();
			InputStream httpResponseStream = getResponseEntity.getContent();
			Reader inputStreamReader = new InputStreamReader(httpResponseStream);

			BufferedReader br = new BufferedReader(inputStreamReader);

			String line = "";

			while ((line = br.readLine()) != null) {
				json = new JSONObject(line);
			}

			httpResponseStream.close();
			inputStreamReader.close();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		} catch (RuntimeException e) {
			e.printStackTrace();
			return null;
		}

		return json;
	}

	public static JSONObject readJsonObjectFromUrl(String url)
			throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is,
					"UTF-8"));
			String jsonText = readAll(rd);
			JSONObject json = new JSONObject(jsonText);
			return json;
		} catch (Exception ex) {
			is.close();
			return null;
		} finally {
			is.close();
		}
	}

	public static JSONArray readJsonArrayFromUrl(String url)
			throws IOException, JSONException {
		InputStream is = new URL(url).openStream();
		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(is,
					"UTF-8"));
			String jsonText = readAll(rd);
			JSONArray json = new JSONArray(jsonText);
			return json;
		} finally {
			is.close();
		}
	}
}
