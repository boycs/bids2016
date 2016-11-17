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

public class FunctionSymbol extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	// ใส่หมายเลข ว่าอยู่ favorite ไหน
	public static int setFavoriteNumber(String strSymbol) {
		int c = R.drawable.icon_favorite_default;

		// FragmentChangeActivity.strFavoriteNumber
		// FragmentChangeActivity.strSymbolSelect

		try {
			for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite
					.length(); i++) {

				// jsoIndex มี 5 index คือ favorite ทั้ง 5
				JSONObject jsoIndex = FragmentChangeActivity.contentGetSymbolFavorite
						.optJSONObject(i);

				String strFavNumber = jsoIndex.getString("favorite_number");
				if ((jsoIndex.getJSONArray("dataAll")) != null) {
					JSONArray jsaFavSymbol = jsoIndex.getJSONArray("dataAll");

					for (int j = 0; j < jsaFavSymbol.length(); j++) {
						String strSymbolIndexJ = jsaFavSymbol.getJSONObject(j)
								.getString("symbol_name");
						if (strSymbol.equals(strSymbolIndexJ)) {
							if (strFavNumber.equals("1")) {
								c = R.drawable.icon_favorite_1;
							} else if (strFavNumber.equals("2")) {
								c = R.drawable.icon_favorite_2;
							} else if (strFavNumber.equals("3")) {
								c = R.drawable.icon_favorite_3;
							} else if (strFavNumber.equals("4")) {
								c = R.drawable.icon_favorite_4;
							} else if (strFavNumber.equals("5")) {
								c = R.drawable.icon_favorite_5;
							}
							// FragmentChangeActivity.strFollowRemoveId =
							FragmentChangeActivity.strFavoriteNumber = strFavNumber;
							break;
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return c;
	}

	// ใส่หมายเลข follow เกิน 21 หรือยัง
	public static boolean checkFollowCount(String strFav) {
		boolean c = true;
		try {
			for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite
					.length(); i++) {

				// jsoIndex มี 5 index คือ favorite ทั้ง 5
				JSONObject jsoIndex = FragmentChangeActivity.contentGetSymbolFavorite
						.optJSONObject(i);

				String strFavNumber = jsoIndex.getString("favorite_number");
				if (strFav.equals(strFavNumber)) {
					if ((jsoIndex.getJSONArray("dataAll").length()) < 21) {
						c = true;
					} else {
						c = false;
					}
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return c;
	}

	// เช็คว่า follow หรือยัง
	public static boolean checkFollowSymbol(String strSymbol) {
		boolean c = false;
		try {
			for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite
					.length(); i++) {

				// jsoIndex มี 5 index คือ favorite ทั้ง 5
				JSONObject jsoIndex = FragmentChangeActivity.contentGetSymbolFavorite
						.optJSONObject(i);

				String strFavNumber = jsoIndex.getString("favorite_number");
				if ((jsoIndex.getJSONArray("dataAll")) != null) {
					JSONArray jsaFavSymbol = jsoIndex.getJSONArray("dataAll");

					for (int j = 0; j < jsaFavSymbol.length(); j++) {
						String strSymbolIndexJ = jsaFavSymbol.getJSONObject(j)
								.getString("symbol_name");
						if (strSymbol.equals(strSymbolIndexJ)) {
							c = true;
							FragmentChangeActivity.strFavoriteNumber = strFavNumber;
							return true;
						}
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return c;
	}

	// เช็ค follow ใน search
	public static int checkFollowSearchSymbol(String strSymbol) {
		int c = R.drawable.icon_plus_blue;

		// FragmentChangeActivity.strFavoriteNumber
		// FragmentChangeActivity.strSymbolSelect

		try {
			for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite
					.length(); i++) {

				// jsoIndex มี 5 index คือ favorite ทั้ง 5
				JSONObject jsoIndex = FragmentChangeActivity.contentGetSymbolFavorite
						.optJSONObject(i);

				String strFavNumber = jsoIndex.getString("favorite_number");
				if ((jsoIndex.getJSONArray("dataAll")) != null) {
					JSONArray jsaFavSymbol = jsoIndex.getJSONArray("dataAll");

					for (int j = 0; j < jsaFavSymbol.length(); j++) {
						String strSymbolIndexJ = jsaFavSymbol.getJSONObject(j)
								.getString("symbol_name");
						if (strSymbol.equals(strSymbolIndexJ)) {
							c = R.drawable.icon_check_green;
							// FragmentChangeActivity.strFavoriteNumber =
							// strFavNumber;
							break;
						}
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

	// check str null
	public static String checkNull(String str_ck) {
		if (str_ck.equals("")) {
			str_ck = "N/a";
		} else if (str_ck.equals(null)) {
			str_ck = "N/a";
		} else if (str_ck.equals("null")) {
			str_ck = "N/a";
		} else {
			str_ck = FunctionSymbol.setFormatNumber(str_ck);
		}

		return str_ck;
	}

	// formatter = new DecimalFormat("0.00");
	// number = formatter.format(-0.123);

	// เช็คว่า format DecimalFormat & .2 digit
	// https://examples.javacodegeeks.com/core-java/text/format-number-with-custom-numberformat/
	public static String setFormatNumber(String strNumber) {
		String c = "";
		if ((!strNumber.equals("")) && (!strNumber.equals("0"))
				&& (!strNumber.equals("0.00")) && (!strNumber.equals("-"))) {
			float value = Float.parseFloat(strNumber.replaceAll(",", ""));
			// DecimalFormat myFormatter = new DecimalFormat("#,###.00");
			// c = myFormatter.format(value);

			// Log.v("setFormatNumber", "_"+value);

			if ((value < 1) && (value > -1) && (value > -1000)) {
				DecimalFormat myFormatter2 = new DecimalFormat("0.00");
				c = myFormatter2.format(value);
			} else {
				DecimalFormat myFormatter = new DecimalFormat("#,###.00");
				c = myFormatter.format(value);
			}
		} else {
			c = "0.00";
		}
		return c;
	}

	// return - ไม่ใส่จุด
	public static String setFormatNumberEtc(String strNumber) {
		String c = "";
		if ((!strNumber.equals("")) && (!strNumber.equals("0"))
				&& (!strNumber.equals("0.00")) && (!strNumber.equals("-"))) {
			c = strNumber;
		} else {
			c = "-";
		}
		return c;
	}

	// ค่าเป็น 0 ให้ใส่ - ไม่ใส่จุด
	public static String setFormatNumberBar(String strNumber) {
		String c = "";
		if ((strNumber.equals("")) || (strNumber.equals("0"))
				|| (strNumber.equals("0.00")) || (strNumber.equals("-"))) {
			c = "-";
		} else {
			c = strNumber;
		}
		return c;
	}

	// return 0
	public static String setFormatNumber0(String strNumber) {
		String c = "0";
		if ((!strNumber.equals("")) && (!strNumber.equals("0"))
				&& (!strNumber.equals("0.00")) && (!strNumber.equals("-"))) {
			c = strNumber;
		} else {
			c = "0";
		}
		return c;
	}

	// แปลงสตริงเป็น float
	public static float setStringPaseFloat(String strNumber) {
		float fNum = 0;
		if (!strNumber.equals("")) {
			fNum = Float.parseFloat(strNumber.replaceAll(",", ""));
		}
		return fNum;
	}

	// เช็คว่า ว่ามีข้อมูลใน (วงเล็บ) ไหม
	public static String setBracket(String strNumber) {
		String c = "";
		if ((!strNumber.equals("")) && (!strNumber.equals("0"))
				&& (!strNumber.equals("0.00")) && (!strNumber.equals("-"))) {
			c = "(" + setFormatNumber(strNumber) + ")";
		} else {
		}
		return c;
	}

	// เช็คว่า format Decimal
	public static String setFormatDecimal(String strNumber) {
		String c = "";
		float value = Float.parseFloat(strNumber.replaceAll(",", ""));
		DecimalFormat myFormatter = new DecimalFormat("#,###");
		c = myFormatter.format(value);
		return c;
	}

	// เช็คว่า format Decimal ใส่ , และ .
	public static String setFormatDecimal2(String strNumber) {
		String c = "";
		float value = Float.parseFloat(strNumber.replaceAll(",", ""));
		DecimalFormat myFormatter = new DecimalFormat("#,###,###.000");
		c = myFormatter.format(value);
		return c;
	}

	// เช็คว่า format Decimal ใส่ , ทศนิยม 2 ตำแหน่ง
	public static String setFormatAnd2Digit(String strNumber) {
		String c = "0.00";
		if (strNumber.equals("N/A")) {
			c = "N/A";
		} else if (!(strNumber.equals("")) && !(strNumber.equals("0"))) {
			float value = Float.parseFloat(strNumber.replaceAll(",", ""));
			if (value > 0.00) {
				DecimalFormat myFormatter = new DecimalFormat("#,###,###.00");
				c = myFormatter.format(value);
				if (value < 1) {
					c = "0" + c;
				}
			}
		}

		return c;
	}

	// return เป็น ทศนิยม 2 ตำแหน่ง
	public static String setFormat2Digit(String strNumber) {
		String c = "";
		float value = Float.parseFloat(strNumber.replaceAll(",", ""));
		c = String.format("%.02f", value);
		return c;
	}

	// เช็คว่า follow หรือยัง
	public static String checkFollowSymbolWatchlistSystemTradeWatctId(
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

	// เช็คว่า status symbol => turnover_list_level, status, status_xd
	// symbol_name (staus)
	// @"T%@",symbol[@"turnover_list_level”], เป็น 1,2,3 ใส่
	// T ข้างหน้า
	// symbol[@"status”], ถ้า status เป็น SP สี text
	// เป็นสีขาวหมด กดดูรายละเอียดไม่ได้
	// symbol[@"status_xd"] ถ้าเป็น XD ใส่ (XD) ต่อท้
	// เช่น (T1,SP,XD)
	public static String checkStatusSymbol(String symbol_name,
			String turnover_list_level, String status, String status_xd) {
		String strStatus = "";
		if ((!turnover_list_level.equals("0"))
				&& (!turnover_list_level.equals("null"))) {
			strStatus = "(T" + turnover_list_level;
			if ((!status.equals("")) && (!status.equals("null"))) {
				strStatus = "," + status;
				if ((!status_xd.equals("")) && (!status_xd.equals("null"))) {
					strStatus = "," + status_xd;
				}
			} else if ((!status_xd.equals("")) && (!status_xd.equals("null"))) {
				strStatus = "," + status_xd;
			}
			strStatus += ")";
		} else if ((!status.equals("")) && (!status.equals("null"))) {
			strStatus = "(" + status;
			if ((!status_xd.equals("")) && (!status_xd.equals("null"))) {
				strStatus = "," + status_xd;
			}
			strStatus += ")";
		} else if ((!status_xd.equals("")) && (!status_xd.equals("null"))) {
			strStatus = "(" + status_xd + ")";
		} else {

		}

		String cStatus = symbol_name + "<font color='#478ecb'>" + strStatus
				+ "</font>";

		return cStatus;
	}

	public static String checkStatusSymbolDetail(String turnover_list_level,
			String status, String status_xd) {
		String strStatus = "";
		if ((!turnover_list_level.equals("0"))
				&& (!turnover_list_level.equals("null"))) {
			strStatus = "(T" + turnover_list_level;
			if ((!status.equals("")) && (!status.equals("null"))) {
				strStatus = "," + status;
				if ((!status_xd.equals("")) && (!status_xd.equals("null"))) {
					strStatus = "," + status_xd;
				}
			} else if ((!status_xd.equals("")) && (!status_xd.equals("null"))) {
				strStatus = "," + status_xd;
			}
			strStatus += ")";
		} else if ((!status.equals("")) && (!status.equals("null"))) {
			strStatus = "(" + status;
			if ((!status_xd.equals("")) && (!status_xd.equals("null"))) {
				strStatus = "," + status_xd;
			}
			strStatus += ")";
		} else if ((!status_xd.equals("")) && (!status_xd.equals("null"))) {
			strStatus = "(" + status_xd + ")";
		} else {

		}

		String cStatus = strStatus;

		return cStatus;
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

				Log.v("result Edit Watchlist WatchlistSystemTrade : ", ""
						+ result);

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

	// ได้ข้อมูล favorite ทั้งหมดมา แล้วปั่นเอา symbol เพื่อไป getSymbol
	// public static void getFavorite() {
	// try {
	// for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite
	// .length(); i++) {
	// JSONObject jsoIndex = FragmentChangeActivity.contentGetSymbolFavorite
	// .optJSONObject(i);
	//
	// // get favorite number
	// String strFav = jsoIndex.getString("favorite_number");
	//
	// // ถ้าตรงกับ favorite ที่เลือกก็ get เอา symbol
	// if (FragmentChangeActivity.strFavoriteNumber.equals(strFav)) {
	// if ((jsoIndex.getJSONArray("dataAll")) != null) {
	// FragmentChangeActivity.strGetListSymbol = "";
	// JSONArray jsaFavSymbol = jsoIndex
	// .getJSONArray("dataAll");
	//
	// for (int j = 0; j < jsaFavSymbol.length(); j++) {
	// FragmentChangeActivity.strGetListSymbol += jsaFavSymbol
	// .getJSONObject(j).getString("symbol_name");
	// if (j != (jsaFavSymbol.length() - 1)) {
	// // เก็บ symbol ต่อกันเป็นสตริง ,
	// // เพื่อเอาไป get ข้อมูล fav นั้นในฟังก์ชัน
	// // getWatchlistSymbol
	// FragmentChangeActivity.strGetListSymbol += ",";
	// }
	// }
	// }
	// break;
	// }
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }

	// ============== follow ===============
	public static void sendAddFavorite() {
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
						Log.v("contentGetWatchlistSystemTrade length",
								""
										+ FragmentChangeActivity.contentGetWatchlistSystemTrade
												.length());
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} else {
				Log.v("connectionError", "Error");
			}
		}
	}

	// ============== get favorite id เพื่อจะเอา id fav มา unfollow
	// =============
	public static JSONArray contentGetFavoriteId = null;

	public static void getDataFavoriteId() {
		getFavoriteId resp = new getFavoriteId();
		resp.execute();
	}

	public static class getFavoriteId extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;
		// ======= json ========
		private JSONObject jsonFavId;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progress.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// ======= url ========
			String url_fav = SplashScreen.url_bidschart
					+ "/service/getFavoriteByUserIdFavoriteNumber?user_id="
					+ SplashScreen.userModel.user_id + "&favorite_number="
					+ FragmentChangeActivity.strFavoriteNumber + "&timestamp="
					+ timestamp;
			Log.v("getFavoriteId", "" + url_fav);
			try {
				jsonFavId = ReadJson.readJsonObjectFromUrl(url_fav);
			} catch (IOException e1) {
				connectionError = true;
				jsonFavId = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonFavId = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonFavId = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (jsonFavId != null) {
					try {
						contentGetFavoriteId = jsonFavId
								.getJSONArray("dataAll");

						for (int i = 0; i < contentGetFavoriteId.length(); i++) {
							JSONObject jso = contentGetFavoriteId
									.getJSONObject(i);
							if (jso.getString("symbol_name").equals(
									FragmentChangeActivity.strSymbolSelect)) {
								strRemoveId = jso.getString("id");
								// strRemoveId = jso.getString("orderbook_id");
								sendRemoveFavorite();
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

			} else {
			}
		}
	}

	// ============== send removefavorite ===============
	public static String strRemoveId = "";

	public static void sendRemoveFavorite() {

		setRemoveFavorite resp = new setRemoveFavorite();
		resp.execute();
	}

	public static class setRemoveFavorite extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		String temp = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progress.show();

		}

		@Override
		protected Void doInBackground(Void... params) {

			// getFavoriteByUserIdFavoriteNumber // หา get favorite id

			String url = SplashScreen.url_bidschart + "/service/removeFavorite";

			String json = "";
			InputStream inputStream = null;
			String result = "";

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();

				jsonObject.accumulate("favorite_id", strRemoveId);

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

				Log.v("result Edit Watchlist follow : ", "" + result);

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
			getDataFavorite(); // get favorite;
		}
	}

	// ============== get favorite =============
	public static void getDataFavorite() {
		getFavorite resp = new getFavorite();
		resp.execute();
	}

	public static class getFavorite extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;
		// ======= json ========
		private JSONObject jsonFav;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progress.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// ======= url ========
			// http://www.bidschart.com/service/v2/symbolFavorite?user_id=104
			String url_fav = SplashScreen.url_bidschart
					+ "/service/v2/symbolFavorite?user_id="
					+ SplashScreen.userModel.user_id + "&timestamp="
					+ timestamp;

			Log.v("url_fav", "" + url_fav);

			try {
				jsonFav = ReadJson.readJsonObjectFromUrl(url_fav);
			} catch (IOException e1) {
				connectionError = true;
				jsonFav = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonFav = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonFav = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (jsonFav != null) {
					try {
						FragmentChangeActivity.contentGetSymbolFavorite = jsonFav
								.getJSONArray("dataAll");

						if (FragmentChangeActivity.contentGetSymbolFavorite
								.length() >= 1) {
							for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite
									.length(); i++) {
								JSONObject jsoIndex = FragmentChangeActivity.contentGetSymbolFavorite
										.optJSONObject(i);

								String strFav = jsoIndex
										.getString("favorite_number");

								if (FragmentChangeActivity.strFavoriteNumber
										.equals(strFav)) {
									if ((jsoIndex.getJSONArray("dataAll")) != null) {
										FragmentChangeActivity.strGetListSymbol = "";
										JSONArray jsaFavSymbol = jsoIndex
												.getJSONArray("dataAll");

										for (int j = 0; j < jsaFavSymbol
												.length(); j++) {
											FragmentChangeActivity.strGetListSymbol += jsaFavSymbol
													.getJSONObject(j)
													.getString("symbol_name");
											if (j != (jsaFavSymbol.length() - 1)) {
												FragmentChangeActivity.strGetListSymbol += ",";
											}
										}
										getWatchlistSymbol(); // get watchlist
																// symbol
									}
									break;
								}
							}
						} else {
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} else {
			}
		}
	}

	// ============== set =============
	public static void getWatchlistSymbol() {
		getSet resp = new getSet();
		resp.execute();
	}

	public static class getSet extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;
		// ======= json ========
		private JSONObject jsonGetWatchlistSymbol;
		private JSONObject jsonGetWatchlistSymbolBegin;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progress.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// ======= url ========

			// String url_GetWatchlistSymbolBegin =
			// SplashScreen.url_bidschart
			// +
			// "/service/v2/watchlistSymbol?symbol=.set,.set50,.set100,.setHD,.mai";

			String url_GetWatchlistSymbol = SplashScreen.url_bidschart
					+ "/service/v2/watchlistSymbol?symbol="
					+ FragmentChangeActivity.strGetListSymbol;

			try {
				// ======= Ui Home ========
				jsonGetWatchlistSymbol = ReadJson
						.readJsonObjectFromUrl(url_GetWatchlistSymbol);

			} catch (IOException e1) {
				connectionError = true;
				jsonGetWatchlistSymbol = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonGetWatchlistSymbol = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonGetWatchlistSymbol = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {

				if (jsonGetWatchlistSymbol != null) {
					try {
						// get content
						FragmentChangeActivity.contentGetWatchlistSymbol = jsonGetWatchlistSymbol
								.getJSONArray("dataAll");

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.v("json newslist null", "newslist null");
				}
			} else {
			}
		}
	}

	public static void testttt() {
		// // ------ high low
		// tv_high.setText(BgColorSymbolDetail.setStrDetailList(strHigh));
		// tv_low.setText(BgColorSymbolDetail.setStrDetailList(strLow));
		//
		// if (strPrevClose != "") {
		// if (strHigh != "") {
		// if ((Float.parseFloat(strHigh.replaceAll(",", ""))) > Float
		// .parseFloat(strPrevClose.replaceAll(",", ""))) {
		// tv_high.setTextColor(getResources().getColor(
		// BgColorSymbolDetail.arrColor[2]));
		// } else if ((Float.parseFloat(strHigh.replaceAll(",", ""))) < Float
		// .parseFloat(strPrevClose.replaceAll(",", ""))) {
		// tv_high.setTextColor(getResources().getColor(
		// BgColorSymbolDetail.arrColor[0]));
		// } else {
		// tv_high.setTextColor(getResources().getColor(
		// BgColorSymbolDetail.arrColor[1]));
		// }
		// }
		// if (strLow != "") {
		// if ((Float.parseFloat(strLow.replaceAll(",", ""))) > Float
		// .parseFloat(strPrevClose.replaceAll(",", ""))) {
		// tv_low.setTextColor(getResources().getColor(
		// BgColorSymbolDetail.arrColor[2]));
		// } else if ((Float.parseFloat(strLow.replaceAll(",", ""))) < Float
		// .parseFloat(strPrevClose.replaceAll(",", ""))) {
		// tv_low.setTextColor(getResources().getColor(
		// BgColorSymbolDetail.arrColor[0]));
		// } else {
		// tv_low.setTextColor(getResources().getColor(
		// BgColorSymbolDetail.arrColor[1]));
		// }
		// }
		// }
		// // ------ open
		// if (strOpen != "") {
		// if ((Float.parseFloat(strOpen.replaceAll(",", ""))) > Float
		// .parseFloat(strPrevClose.replaceAll(",", ""))) {
		// tv_open.setTextColor(getResources().getColor(
		// BgColorSymbolDetail.arrColor[2]));
		// } else if ((Float.parseFloat(strOpen
		// .replaceAll(",", ""))) < Float
		// .parseFloat(strPrevClose.replaceAll(",", ""))) {
		// tv_open.setTextColor(getResources().getColor(
		// BgColorSymbolDetail.arrColor[0]));
		// } else {
		// tv_open.setTextColor(getResources().getColor(
		// BgColorSymbolDetail.arrColor[1]));
		// }
		// }
		// // ------ last trade, percenchage
		// if (strChange != "") {
		// tv_last_trade.setTextColor(getResources().getColor(
		// BgColorSymbolDetail.setColor(strChange)));
		// tv_percenchange.setTextColor(getResources().getColor(
		// BgColorSymbolDetail.setColor(strChange)));
		// }

	}

}
