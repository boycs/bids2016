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

			String url = SplashScreen.url_bidschart+"/service/addFavorite";

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
					resultFollow = AFunctionOther.convertInputStreamToString(inputStream);
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
