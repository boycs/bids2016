package com.app.bids;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class FollowSymbol extends Activity {

	public static String user_id, favorite_number, favorite_symbol;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	// ใส่หมายเลข follow เกิน 21 หรือยัง ถ้าน้อยกว่า 21 return true(เพิ่มได้)
	public static boolean checkFollowCount(String strFav) {
		boolean c = true;
		if (FragmentChangeActivity.strFavoriteNumber == "1") {
			FragmentChangeActivity.contentGetSymbolFavorite = FragmentChangeActivity.contentGetSymbolFavorite_1;
		} else if (FragmentChangeActivity.strFavoriteNumber == "2") {
			FragmentChangeActivity.contentGetSymbolFavorite = FragmentChangeActivity.contentGetSymbolFavorite_2;
		} else if (FragmentChangeActivity.strFavoriteNumber == "3") {
			FragmentChangeActivity.contentGetSymbolFavorite = FragmentChangeActivity.contentGetSymbolFavorite_3;
		} else if (FragmentChangeActivity.strFavoriteNumber == "4") {
			FragmentChangeActivity.contentGetSymbolFavorite = FragmentChangeActivity.contentGetSymbolFavorite_4;
		} else if (FragmentChangeActivity.strFavoriteNumber == "5") {
			FragmentChangeActivity.contentGetSymbolFavorite = FragmentChangeActivity.contentGetSymbolFavorite_5;
		}

		if ((FragmentChangeActivity.contentGetSymbolFavorite.length()) < 21) {
			return true;
		} else {
			return false;
		}
	}

	// เช็คว่า follow หรือยัง
	public static boolean checkFollowSymbol(String strSymbol) {
		boolean c = false;
		try {
			for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite_1
					.length(); i++) {
				String strSymbolIndex = FragmentChangeActivity.contentGetSymbolFavorite_1
						.getJSONObject(i).getString("symbol_name");
				if (strSymbol.equals(strSymbolIndex)) {
					return true;
				}
			}
			for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite_2
					.length(); i++) {
				String strSymbolIndex = FragmentChangeActivity.contentGetSymbolFavorite_2
						.getJSONObject(i).getString("symbol_name");
				if (strSymbol.equals(strSymbolIndex)) {
					return true;
				}
			}
			for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite_3
					.length(); i++) {
				String strSymbolIndex = FragmentChangeActivity.contentGetSymbolFavorite_3
						.getJSONObject(i).getString("symbol_name");
				if (strSymbol.equals(strSymbolIndex)) {
					return true;
				}
			}
			for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite_4
					.length(); i++) {
				String strSymbolIndex = FragmentChangeActivity.contentGetSymbolFavorite_4
						.getJSONObject(i).getString("symbol_name");
				if (strSymbol.equals(strSymbolIndex)) {
					return true;
				}
			}
			for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite_5
					.length(); i++) {
				String strSymbolIndex = FragmentChangeActivity.contentGetSymbolFavorite_5
						.getJSONObject(i).getString("symbol_name");
				if (strSymbol.equals(strSymbolIndex)) {
					return true;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return c;
	}

	// เช็คว่าอยู่ favorit ไหน
	public static String checkFavoriteNumber(String strSymbol) {
		String favId = "";
		try {
			for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite_1
					.length(); i++) {
				String strSymbolIndex = FragmentChangeActivity.contentGetSymbolFavorite_1
						.getJSONObject(i).getString("symbol_name");
				if (strSymbol.equals(strSymbolIndex)) {
					favId = FragmentChangeActivity.contentGetSymbolFavorite_1
							.getJSONObject(i).getString("id");
					return favId;
				}
			}
			for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite_2
					.length(); i++) {
				String strSymbolIndex = FragmentChangeActivity.contentGetSymbolFavorite_2
						.getJSONObject(i).getString("symbol_name");
				if (strSymbol.equals(strSymbolIndex)) {
					favId = FragmentChangeActivity.contentGetSymbolFavorite_2
							.getJSONObject(i).getString("id");
					return favId;
				}
			}
			for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite_3
					.length(); i++) {
				String strSymbolIndex = FragmentChangeActivity.contentGetSymbolFavorite_3
						.getJSONObject(i).getString("symbol_name");
				if (strSymbol.equals(strSymbolIndex)) {
					favId = FragmentChangeActivity.contentGetSymbolFavorite_3
							.getJSONObject(i).getString("id");
					return favId;
				}
			}
			for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite_4
					.length(); i++) {
				String strSymbolIndex = FragmentChangeActivity.contentGetSymbolFavorite_4
						.getJSONObject(i).getString("symbol_name");
				if (strSymbol.equals(strSymbolIndex)) {
					favId = FragmentChangeActivity.contentGetSymbolFavorite_4
							.getJSONObject(i).getString("id");
					return favId;
				}
			}
			for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite_5
					.length(); i++) {
				String strSymbolIndex = FragmentChangeActivity.contentGetSymbolFavorite_5
						.getJSONObject(i).getString("symbol_name");
				if (strSymbol.equals(strSymbolIndex)) {
					favId = FragmentChangeActivity.contentGetSymbolFavorite_5
							.getJSONObject(i).getString("id");
					return favId;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return favId;
	}

	// เช็ค follow ใน search
	public static int setColorFollowSearchSymbol(String strSymbol) {
		int c = R.drawable.icon_plus_blue;
		try {
			for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite_1
					.length(); i++) {
				String strSymbolIndex = FragmentChangeActivity.contentGetSymbolFavorite_1
						.getJSONObject(i).getString("symbol_name");
				if (strSymbol.equals(strSymbolIndex)) {
					c = R.drawable.icon_check_green;
					return c;
				}
			}
			for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite_2
					.length(); i++) {
				String strSymbolIndex = FragmentChangeActivity.contentGetSymbolFavorite_2
						.getJSONObject(i).getString("symbol_name");
				if (strSymbol.equals(strSymbolIndex)) {
					c = R.drawable.icon_check_green;
					return c;
				}
			}
			for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite_3
					.length(); i++) {
				String strSymbolIndex = FragmentChangeActivity.contentGetSymbolFavorite_3
						.getJSONObject(i).getString("symbol_name");
				if (strSymbol.equals(strSymbolIndex)) {
					c = R.drawable.icon_check_green;
					return c;
				}
			}
			for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite_4
					.length(); i++) {
				String strSymbolIndex = FragmentChangeActivity.contentGetSymbolFavorite_4
						.getJSONObject(i).getString("symbol_name");
				if (strSymbol.equals(strSymbolIndex)) {
					c = R.drawable.icon_check_green;
					return c;
				}
			}
			for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite_5
					.length(); i++) {
				String strSymbolIndex = FragmentChangeActivity.contentGetSymbolFavorite_5
						.getJSONObject(i).getString("symbol_name");
				if (strSymbol.equals(strSymbolIndex)) {
					c = R.drawable.icon_check_green;
					return c;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return c;
	}

	// ============== send symbol add favorite ===============
	public static void sendSymbolAddFavorite() {
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
			PagerWatchList pg = new PagerWatchList();
			pg.getDataFavorite();
			// updateDataFavorite();
		}
	}

	// ============== send symbol remove favorite ===============
	public static void sendSymbolRemoveFavorite() {
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
								"favorite_id",checkFavoriteNumber(FragmentChangeActivity.strSymbolSelect));

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
			PagerWatchList pg = new PagerWatchList();
			pg.getDataFavorite(); // get favorite
			// switchFragment(new PagerWatchlistDetail());
		}
	}

	// ============== send follow ================
	public static String resultFollow = "";

	public static void sendRemoveFavorite(String userid, String favoriteNumber,
			String favoriteSymbol) {

		user_id = userid;
		favorite_number = favoriteNumber;
		favorite_symbol = favoriteSymbol;

		setFollow resp = new setFollow();
		resp.execute();
	}

	public static class setFollow extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		String temp = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progress.show();

		}

		@Override
		protected Void doInBackground(Void... params) {

			String url = SplashScreen.url_bidschart + "/service/addFavorite";

			String json = "";
			InputStream inputStream = null;

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("favorite_number", favorite_number);
				jsonObject.accumulate("favorite_symbol", favorite_symbol);
				jsonObject.accumulate("user_id", user_id);

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
					resultFollow = AFunctionOther
							.convertInputStreamToString(inputStream);
				else
					resultFollow = "Did not work!";
				Log.v("result like article", "" + resultFollow);
				// {"status":"ok","message":"Get data Success.","dataAll":{"reply_id":66,"comment_id":95,"post_id":99,"reply_user_id":59,"agree_count":0,"disagree_count":0,"reply_datetime":"2015-04-21 14:37:27","content":"???????","chart_url":"","username":"??????? ???????"}}

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

			// try {
			// JSONObject jsonObj = new JSONObject(resultLikePost);
			// UiArticleSelect ua = new UiArticleSelect();
			// ua.setLikePost(jsonObj);
			// } catch (JSONException e) {
			// e.printStackTrace();
			// }

		}
	}

}
