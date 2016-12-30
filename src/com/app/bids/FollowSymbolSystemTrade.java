package com.app.bids;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.app.bids.R;

public class FollowSymbolSystemTrade extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}
	

	// get watch_id เพื่อส่ง id ไป remove (ในกรณีที่ follow = true)
	public static String getWatchIdWatchlistSystemTrade(
			String strSymbol) {
		String c = "";
		try {
			if (FragmentChangeActivity.contentGetWatchlistSystemTrade != null) {
				for (int i = 0; i < FragmentChangeActivity.contentGetWatchlistSystemTrade
						.length(); i++) {
					JSONObject jsoIndex = FragmentChangeActivity.contentGetWatchlistSystemTrade
							.optJSONObject(i);
					final String symbolIndexI = jsoIndex
							.getString("symbol_name");
					final String symbolWatchId = jsoIndex.getString("watch_id");
					if (strSymbol.equals(symbolIndexI)) {
						c = symbolWatchId;
						strWatc_id = c;
						sendRemoveWatchlistSystemTrade();
						break;
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return c;
	}

	// =============== WatchlistSystemTrade ============
	// เช็คว่า follow หรือยัง
	public static boolean checkFollowSymbolWatchlistSystemTrade(String strSymbol) {
		boolean c = false;
		try {
			if (FragmentChangeActivity.contentGetWatchlistSystemTrade != null) {
				for (int i = 0; i < FragmentChangeActivity.contentGetWatchlistSystemTrade
						.length(); i++) {
					JSONObject jsoIndex = FragmentChangeActivity.contentGetWatchlistSystemTrade
							.optJSONObject(i);
					final String symbolIndexI = jsoIndex
							.getString("symbol_name");
					if (strSymbol.equals(symbolIndexI)) {
						c = true;
						break;
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return c;
	}

	// เช็ค follow ใน search
	public static int checkFollowSearchSymbolWatchlistSystemTrade(
			String strSymbol) {
		int c = R.drawable.icon_plus_blue;
		try {
			if (FragmentChangeActivity.contentGetWatchlistSystemTrade != null) {
				for (int i = 0; i < FragmentChangeActivity.contentGetWatchlistSystemTrade
						.length(); i++) {
					JSONObject jsoIndex = FragmentChangeActivity.contentGetWatchlistSystemTrade
							.optJSONObject(i);
					final String symbolIndexI = jsoIndex
							.getString("symbol_name");
					if (strSymbol.equals(symbolIndexI)) {
						c = R.drawable.icon_check_green;
						break;
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return c;
	}

	// ============== send removefavorite ===============
	public static String strWatc_id;

	public static void sendRemoveWatchlistSystemTrade() {
		setRemoveWatchlistSystemTrade resp = new setRemoveWatchlistSystemTrade();
		resp.execute();
	}

	public static class setRemoveWatchlistSystemTrade extends
			AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		String temp = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progress.show();

		}

		@Override
		protected Void doInBackground(Void... params) {

			String url = SplashScreen.url_bidschart
					+ "/watchlist/destroyForMobile/" + strWatc_id;

			String json = "";
			InputStream inputStream = null;
			String result = "";

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();

				jsonObject.accumulate("platform", "mobile");

				// 4. convert JSONObject to JSON to String
				json = jsonObject.toString();

				// 5. set json to StringEntity
				StringEntity se = new StringEntity(json, "UTF-8");

				// 6. set httpPost Entity
				httppost.setEntity(se);

				// 7. Set some headers to inform server about the type of the
				// content
				httppost.setHeader("Accept", "application/json");
				httppost.setHeader("Content-type", "application/json");

				// 8. Execute POST request to the given URL
				HttpResponse httpResponse = httpclient.execute(httppost);

				// 9. receive response as inputStream
				inputStream = httpResponse.getEntity().getContent();

				// 10. convert inputstream to string
				if (inputStream != null)
					result = AFunctionOther
							.convertInputStreamToString(inputStream);
				else
					result = "Did not work!";

//				Log.v("result Edit Watchlist WatchlistSystemTrade : ", ""
//						+ result);

			} catch (IOException e) {
				connectionError = true;
				e.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			initGetWatchlistSystemTrade(); // get systemtrade;
		}
	}

	// ============== follow WatchlistSystemTrade ===============
	public static void sendAddWatchlistSystemTrade() {
		setFavorite resp = new setFavorite();
		resp.execute();
	}

	public static class setFavorite extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		String temp = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progress.show();

		}

		@Override
		protected Void doInBackground(Void... params) {

			String url = SplashScreen.url_bidschart
					+ "/service/v2/addWatchlistSystemTrade";

			String json = "";
			InputStream inputStream = null;
			String result = "";

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("symbol_name",
						FragmentChangeActivity.strSymbolSelect);
				jsonObject
						.accumulate("user_id", SplashScreen.userModel.user_id);
				jsonObject.accumulate("type", 0);

				// 4. convert JSONObject to JSON to String
				json = jsonObject.toString();

				// 5. set json to StringEntity
				StringEntity se = new StringEntity(json, "UTF-8");

				// 6. set httpPost Entity
				httppost.setEntity(se);

				// 7. Set some headers to inform server about the type of the
				// content
				httppost.setHeader("Accept", "application/json");
				httppost.setHeader("Content-type", "application/json");

				// 8. Execute POST request to the given URL
				HttpResponse httpResponse = httpclient.execute(httppost);

				// 9. receive response as inputStream
				inputStream = httpResponse.getEntity().getContent();

				// 10. convert inputstream to string
				if (inputStream != null)
					result = AFunctionOther
							.convertInputStreamToString(inputStream);
				else
					result = "Did not work!";

			} catch (IOException e) {
				connectionError = true;
				e.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			initGetWatchlistSystemTrade();
		}
	}

	// ============== get WatchlistSystemTrade =========================
	public static void initGetWatchlistSystemTrade() {
		getWatchlistSystemTrade resp = new getWatchlistSystemTrade();
		resp.execute();
	}

	public static class getWatchlistSystemTrade extends
			AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		private JSONObject jsonWatchlistSystemTrade;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();

			String url_WatchlistSystemTrade = SplashScreen.url_bidschart
					+ "/service/v2/watchlistSystemTrade?user_id="
					+ SplashScreen.userModel.user_id;

			try {
				jsonWatchlistSystemTrade = ReadJson
						.readJsonObjectFromUrl(url_WatchlistSystemTrade);
			} catch (IOException e1) {
				connectionError = true;
				jsonWatchlistSystemTrade = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonWatchlistSystemTrade = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonWatchlistSystemTrade = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (jsonWatchlistSystemTrade != null) {
					try {
						FragmentChangeActivity.contentGetWatchlistSystemTrade = jsonWatchlistSystemTrade
								.getJSONArray("dataAll");
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} else {
				Log.v("connectionError", "Error");
			}
		}
	}

}
