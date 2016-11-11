package com.app.bids;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;

public class PagerWatchListDetailFollow extends Fragment {

	static Context context;
	public static View rootView;

	// activity listener interface
	private OnPageListener pageListener;

	public interface OnPageListener {
		public void onPage1(String s);
	}

	// onAttach : set activity listener
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// if implemented by activity, set listener
		if (activity instanceof OnPageListener) {
			pageListener = (OnPageListener) activity;
		}
		// else create local listener (code never executed in this example)
		else
			pageListener = new OnPageListener() {
				@Override
				public void onPage1(String s) {
					Log.d("PAG1", "Button event from page 1 : " + s);
				}
			};
	}

	// onCreateView :
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		// fragment not when container null
		if (container == null) {
			return null;
		}

		// inflate view from layout
		rootView = (LinearLayout) inflater.inflate(
				R.layout.pager_watchlist_detail_follow, container, false);

		return rootView;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initView();
	}

	// init view
	LinearLayout li_back, li_favorite1, li_favorite2, li_favorite3,
			li_favorite4, li_favorite5;

	private void initView() {
		li_back = (LinearLayout) rootView.findViewById(R.id.li_back);
		li_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				switchFragment(new PagerWatchlistDetail());
			}
		});

		li_favorite1 = (LinearLayout) rootView.findViewById(R.id.li_favorite1);
		li_favorite2 = (LinearLayout) rootView.findViewById(R.id.li_favorite2);
		li_favorite3 = (LinearLayout) rootView.findViewById(R.id.li_favorite3);
		li_favorite4 = (LinearLayout) rootView.findViewById(R.id.li_favorite4);
		li_favorite5 = (LinearLayout) rootView.findViewById(R.id.li_favorite5);

		li_favorite1.setOnClickListener(onClickListenerFavorite);
		li_favorite2.setOnClickListener(onClickListenerFavorite);
		li_favorite3.setOnClickListener(onClickListenerFavorite);
		li_favorite4.setOnClickListener(onClickListenerFavorite);
		li_favorite5.setOnClickListener(onClickListenerFavorite);
	}

	// ********** select favorite **********
	private OnClickListener onClickListenerFavorite = new OnClickListener() {
		@Override
		public void onClick(final View v) {

			switch (v.getId()) {
			case R.id.li_favorite1:
				FragmentChangeActivity.strFavoriteNumber = "1";
				sendAddFavorite();
				// switchFragment(new PagerWatchlistDetail());
				break;
			case R.id.li_favorite2:
				FragmentChangeActivity.strFavoriteNumber = "2";
				sendAddFavorite();
				// switchFragment(new PagerWatchlistDetail());
				break;
			case R.id.li_favorite3:
				FragmentChangeActivity.strFavoriteNumber = "3";
				sendAddFavorite();
				// switchFragment(new PagerWatchlistDetail());
				break;
			case R.id.li_favorite4:
				FragmentChangeActivity.strFavoriteNumber = "4";
				sendAddFavorite();
				break;
			case R.id.li_favorite5:
				FragmentChangeActivity.strFavoriteNumber = "5";
				sendAddFavorite();
				// switchFragment(new PagerWatchlistDetail());
				break;
			default:
				break;
			}
		}
	};

	// ============== send add favorite ===============
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
					result = AFunctionOther.convertInputStreamToString(inputStream);
				else
					result = "Did not work!";

				// Log.v("result", "" + result);
				// JSONObject jsonObj = new JSONObject(result);
				// PagerWatchlistDetail.contentGetDetail =
				// jsonObj.getJSONObject("dataAll");

				// {"status":"ok","message":"Success.","dataAll":{"id":"97","favorite_number":"4","favorite_symbol":"PTT",
				// "favorite_type":"WATCHLIST","user_id":"104","created_at":"2015-12-21 11:17:07","updated_at":"2015-12-21 11:17:07",
				// "symbol_name":"PTT","symbol_pk":"PTT","market_id":"SET","symbol_fullname_eng":"PTT PUBLIC CO.,LTD.",
				// "symbol_fullname_thai":null,"last_trade":"225.00","average_price":"230.18","average_buy":null,"average_sell":null,
				// "prev_close":"242","open":"238","open1":"238","open2":"3","close":"225","adj_close":null,"high":"238",
				// "high52W":"378","low":"225","low52W":"220","volume":"6.93 M","value":"1,594,824,400.00","change":"-17.00",
				// "percentChange":"-7.02","ceiling":"292","floor":"157.5","eps":"6.94","yield":"4.89","p_bv":"0.92","p_e":"0",
				// "percentChange1W":"2.27","percentChange1M":"-14.45","percentChange3M":"-11.07","status":null,"industry":"RESOURC",
				// "SET50":"Y","SET100":"Y","SETHD":"Y","sector":"ENERG","security_type":"S","benefit":null,
				// "listed_share":"2856299625","bv_nv":"244.82","qp_e":"9","financial_statement_date":"2015-09-30","dps":"6",
				// "ending_date":"2015-06-30","pod":"6","par_value":"10","market_capitalization":"642667000000","tr_bv":"0.24",
				// "npg_flag":"0","acc_ds":"11","acc_py":"2","dpr":"0","earning_date":"2015-09-30","allow_short_sell":"1",
				// "allow_nvdr":"1","allow_short_sell_on_nvdr":"0","allow_ttf":"3","stabilization_flag":"0","notification_type":null,
				// "non_compliance":"0","parent_symbol":"PTT_SYMB","orderbook_id":"2099","subscriptionGroupId":"258","beneficial_signs":null,
				// "updateDatetime":"2015-12-21 08:06:07","split_flag":"0","cg_score":null,"1y_rtn":null,"roe":"7.23","magic1":null,
				// "magic2":null,"mg":null,"d_e":null,"npm":"4.3","roa":"5.97","ffloat":null,"peg":null,"marketListId":"STOCK",
				// "segmentId":"COMMON_STOCK","prev_volume":"6099300","max_volume200day":"33274087","max_price200day":"369",
				// "max_volume_high_price200day":"38.25","project_open1":"211","project_open2":"208","project_close":"234",
				// "project_open1_volume":"745200","project_open2_volume":"189000","project_close_volume":"527000",
				// "project_open1_value":"157237200","project_open2_value":"39312000","project_close_value":"123318000",
				// "project_open1_percent_change":"-12.8099","project_open2_percent_change":"-9.56522",
				// "project_close_percent_change":"-3.30578","max_buy_price":"230","max_sell_price":"234","max_buy_price_volume":"46100",
				// "max_sell_price_volume":"50000","open1_volume":"0","open2_volume":"0","close_volume":"0","buy_volume":"912700",
				// "buy_value":"213391200","sell_volume":"2341900","sell_value":"545958590","last_update_date":"2015-12-18"}}

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
			switchFragment(new PagerWatchList());
			// switchFragment(new PagerWatchlistDetail());
		}
	}

	// ============== get favorite id =============
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
		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// ======= url ========
			String url_fav;
			url_fav = SplashScreen.url_bidschart
					+ "/service/getFavoriteByUserIdFavoriteNumber?user_id="
					+ SplashScreen.userModel.user_id + "&favorite_number="
					+ FragmentChangeActivity.strFavoriteNumber + "&timestamp="
					+ timestamp;

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
					result = AFunctionOther.convertInputStreamToString(inputStream);
				else
					result = "Did not work!";

				// Log.v("result remove fav : ", ""+result);

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

			updateDataFavorite();

			// switchFragment(new PagerWatchlistDetail());
		}
	}

	// ============== update favorite =============
	public static void updateDataFavorite() {
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
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} else {
			}
		}
	}

	// the meat of switching the above fragment
	private static void switchFragment(Fragment fragment) {
		if (context == null)
			return;
		if (context instanceof FragmentChangeActivity) {
			FragmentChangeActivity fca = (FragmentChangeActivity) context;
			fca.switchContent(fragment);
		}
	}

}