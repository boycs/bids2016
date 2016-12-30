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
import com.app.bids.FollowSymbol.sendSymbolAddFavorite;
import com.app.bids.FollowSymbol.sendSymbolRemoveFavorite;
import com.app.bids.FollowSymbol.setFollow;
import com.app.bids.PagerWatchList.getFavorite;
import com.google.android.gms.drive.internal.GetDriveIdFromUniqueIdentifierRequest;

public class FollowSymbolDetail extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	// เช็คว่าอยู่ favorit ไหน 
	public static int checkNumberFollowSymbolDetail(String strSymbol) {
		String favId = "";
		int c = R.drawable.icon_favorite_default;
		try {
			for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite_1
					.length(); i++) {
				String strSymbolIndex = FragmentChangeActivity.contentGetSymbolFavorite_1
						.getJSONObject(i).getString("symbol_name");
				if (strSymbol.equals(strSymbolIndex)) {
					c = R.drawable.icon_favorite_1;
					FragmentChangeActivity.strFavoriteNumber = "1";
					return c;
				}
			}
			for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite_2
					.length(); i++) {
				String strSymbolIndex = FragmentChangeActivity.contentGetSymbolFavorite_2
						.getJSONObject(i).getString("symbol_name");
				if (strSymbol.equals(strSymbolIndex)) {
					c = R.drawable.icon_favorite_2;
					FragmentChangeActivity.strFavoriteNumber = "2";
					return c;
				}
			}
			for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite_3
					.length(); i++) {
				String strSymbolIndex = FragmentChangeActivity.contentGetSymbolFavorite_3
						.getJSONObject(i).getString("symbol_name");
				if (strSymbol.equals(strSymbolIndex)) {
					c = R.drawable.icon_favorite_3;
					FragmentChangeActivity.strFavoriteNumber = "3";
					return c;
				}
			}
			for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite_4
					.length(); i++) {
				String strSymbolIndex = FragmentChangeActivity.contentGetSymbolFavorite_4
						.getJSONObject(i).getString("symbol_name");
				if (strSymbol.equals(strSymbolIndex)) {
					c = R.drawable.icon_favorite_4;
					FragmentChangeActivity.strFavoriteNumber = "4";
					return c;
				}
			}
			for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite_5
					.length(); i++) {
				String strSymbolIndex = FragmentChangeActivity.contentGetSymbolFavorite_5
						.getJSONObject(i).getString("symbol_name");
				if (strSymbol.equals(strSymbolIndex)) {
					c = R.drawable.icon_favorite_5;
					FragmentChangeActivity.strFavoriteNumber = "5";
					return c;
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


	// ============== send symbol add favorite detail ===============
	public static void sendSymbolAddFavoriteDetail() {
		sendSymbolAddFavorite resp = new sendSymbolAddFavorite();
		resp.execute();
	}

	public static class sendSymbolAddFavorite extends
			AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		String temp = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			String url = SplashScreen.url_bidschart + "/service/addFavorite";

			String json = "";
			InputStream inputStream = null;
			String result = "";

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("favorite_number",
						FragmentChangeActivity.strFavoriteNumber);
				jsonObject.accumulate("favorite_symbol",
						FragmentChangeActivity.strSymbolSelect);
				jsonObject
						.accumulate("user_id", SplashScreen.userModel.user_id);

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

				// Log.v("sendAddFavorite", ""+result);

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
			getDataFavorite();
		}
	}

	
	// ============== send symbol remove favorite detail ===============
	public static void sendSymbolRemoveFavoriteDetail() {
		sendSymbolRemoveFavorite resp = new sendSymbolRemoveFavorite();
		resp.execute();
	}

	public static class sendSymbolRemoveFavorite extends
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

				jsonObject
						.accumulate(
								"favorite_id",FollowSymbol.checkFavoriteNumber(FragmentChangeActivity.strSymbolSelect));

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

				Log.v("sendRemoveFavorite Watchlist : ", "" + result);

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
			getDataFavorite();
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
		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// ======= url ========

			// http://www.bidschart.com/service/getFavoriteByUserIdFavoriteNumber?user_id=1587&favorite_number=1

			String url_fav = SplashScreen.url_bidschart
					+ "/service/getFavoriteByUserIdFavoriteNumber?user_id="
					+ SplashScreen.userModel.user_id + "&favorite_number="
					+ FragmentChangeActivity.strFavoriteNumber+ "&limit=20&ofset=1&timestamp="+ timestamp;

			Log.v("getFavoriteByUserIdFavoriteNumber", "__" + url_fav);

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
						if (FragmentChangeActivity.contentGetSymbolFavorite != null) {
							FragmentChangeActivity.strGetListSymbol = "";
							for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite
									.length(); i++) {
								JSONObject jsoIndex = FragmentChangeActivity.contentGetSymbolFavorite
										.getJSONObject(i);

								FragmentChangeActivity.strGetListSymbol += jsoIndex
										.getString("symbol_name");
								if (i != (FragmentChangeActivity.contentGetSymbolFavorite
										.length() - 1)) {
									FragmentChangeActivity.strGetListSymbol += ",";
								}
							}
							if (FragmentChangeActivity.strFavoriteNumber == "1") {
								FragmentChangeActivity.strGetListSymbol_fav1 = FragmentChangeActivity.strGetListSymbol;
								FragmentChangeActivity.contentGetSymbolFavorite_1 = FragmentChangeActivity.contentGetSymbolFavorite;
							} else if (FragmentChangeActivity.strFavoriteNumber == "2") {
								FragmentChangeActivity.strGetListSymbol_fav2 = FragmentChangeActivity.strGetListSymbol;
								FragmentChangeActivity.contentGetSymbolFavorite_2 = FragmentChangeActivity.contentGetSymbolFavorite;
							} else if (FragmentChangeActivity.strFavoriteNumber == "3") {
								FragmentChangeActivity.strGetListSymbol_fav3 = FragmentChangeActivity.strGetListSymbol;
								FragmentChangeActivity.contentGetSymbolFavorite_3 = FragmentChangeActivity.contentGetSymbolFavorite;
							} else if (FragmentChangeActivity.strFavoriteNumber == "4") {
								FragmentChangeActivity.strGetListSymbol_fav4 = FragmentChangeActivity.strGetListSymbol;
								FragmentChangeActivity.contentGetSymbolFavorite_4 = FragmentChangeActivity.contentGetSymbolFavorite;
							} else if (FragmentChangeActivity.strFavoriteNumber == "5") {
								FragmentChangeActivity.strGetListSymbol_fav5 = FragmentChangeActivity.strGetListSymbol;
								FragmentChangeActivity.contentGetSymbolFavorite_5 = FragmentChangeActivity.contentGetSymbolFavorite;
							}

							PagerWatchList pg = new PagerWatchList();
							pg.getWatchlistSymbol(); // get favorite
							
							UiWatchlistDetail uwd = new UiWatchlistDetail();
							uwd.setFollowSymbol(); // set follow
						} else {
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
				}
			} else {
			}
		}
	}
	
}
