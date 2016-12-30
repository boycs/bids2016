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
import com.app.bids.UiWatchlistDetail.loadData;
import com.google.android.gms.drive.internal.GetDriveIdFromUniqueIdentifierRequest;

public class LoadSymbolDetail extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	// ============== Load Data all =============
	public static void loadDataDetail() {
		loadData resp = new loadData();
		resp.execute();
	}

	public static class loadData extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;
		// ======= json ========
		private JSONObject jsonGetDetail;

		// private JSONObject jsonGetDetailFollow;

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

			// http://www.bidschart.com/service/v2/watchlistSymbol?user_id=104&symbol=AAV
			String url_GetDetail = SplashScreen.url_bidschart
					+ "/service/v2/watchlistSymbol?user_id="
					+ SplashScreen.userModel.user_id + "&symbol="
					+ FragmentChangeActivity.strSymbolSelect + "&timestamp="
					+ timestamp;

			Log.v("url_GetDetail", "" + url_GetDetail);

			try {
				// ======= Ui Home ========
				jsonGetDetail = ReadJson.readJsonObjectFromUrl(url_GetDetail);
			} catch (IOException e1) {
				connectionError = true;
				jsonGetDetail = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonGetDetail = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonGetDetail = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (jsonGetDetail != null) {
					try {
						// get content

						if (jsonGetDetail.getString("status").equals("ok")) {
							JSONArray jsa = jsonGetDetail.getJSONArray("dataAll");
							if (jsa != null) {
								UiWatchlistDetail.contentGetDetail = jsa.getJSONObject(0);
								UiWatchlistDetail.contentGetDetailFundamental = UiWatchlistDetail.contentGetDetail
										.getString("fundamental");
								
								UiWatchlistDetail uw = new UiWatchlistDetail();
								uw.setDataDetailSelectIndustry();
							} else {
								
							}
						}
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
	
}
