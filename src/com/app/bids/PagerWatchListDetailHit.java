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
import com.app.bids.PagerSetAlert.loadDataRetrieveSetAlert;
import com.app.bids.PagerSetAlert.setRemoveSetAlert;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
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
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class PagerWatchListDetailHit extends Fragment {

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
				R.layout.pager_watchlist_detail_hit, container, false);

		if (UiWatchlistDetail.contentGetRetrieveSetAlert != null) {
			setDataSetAlert();
		} else {
			loadDataRetrieveSetAlertAll();
		}
		return rootView;

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	// ============== Load Data RetrieveSetAlert all =============
	public void loadDataRetrieveSetAlertAll() {
		loadDataRetrieveSetAlert resp = new loadDataRetrieveSetAlert();
		resp.execute();
	}

	public class loadDataRetrieveSetAlert extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;
		// ======= json ========
		private JSONObject jsonGetDetail;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// ======= url ========

			// http://bidschart.com/bidsMaster/retrieveSetAlertAllList?user_id=53
			String url_GetDetail = SplashScreen.url_bidschart
					+ "/bidsMaster/retrieveSetAlertAllList?user_id="
					+ SplashScreen.userModel.user_id + "&timestamp="
					+ timestamp;

			// http://bidschart.com/bidsMaster/retrieveSetAlertList?user_id=104&orderbook_id=2099
//			String url_GetDetail = SplashScreen.url_bidschart
//					+ "/bidsMaster/retrieveSetAlertList?user_id="
//					+ SplashScreen.userModel.user_id + "&orderbook_id="
//					+ strOrderBookId + "&timestamp=" + timestamp;

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
					UiWatchlistDetail.contentGetRetrieveSetAlert = jsonGetDetail;

					// Log.v("contentGetRetrieveSetAlert", ""
					// + contentGetRetrieveSetAlert);

					setDataSetAlert();

				} else {
					Log.v("json newslist null", "newslist null");
				}
			} else {
			}
			// dialogLoading.dismiss();
		}
	}

	// ============== add setalert ===============
	String strId, strOrderbookId, strSymbolName, strType;
	AlertDialog alertDialogSetAlert;

	private void setDataSetAlert() {
		// dialogLoading.dismiss();

		LinearLayout li_list = (LinearLayout) rootView
				.findViewById(R.id.li_list);
		LinearLayout li_list_hit = (LinearLayout) rootView
				.findViewById(R.id.li_list_hit);
		TextView tv_add_alert = (TextView) rootView
				.findViewById(R.id.tv_add_alert);

		li_list.setVisibility(View.GONE);
		li_list_hit.setVisibility(View.GONE);

		tv_add_alert.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// switchFragment(new PagerSetAlertSelect());
			}
		});

		try {
			JSONArray jsaList1 = UiWatchlistDetail.contentGetRetrieveSetAlert
					.getJSONArray("list1");
			JSONArray jsaList2 = UiWatchlistDetail.contentGetRetrieveSetAlert
					.getJSONArray("list2");

			LinearLayout li_list1 = (LinearLayout) rootView
					.findViewById(R.id.li_list1);
			LinearLayout li_list2 = (LinearLayout) rootView
					.findViewById(R.id.li_list2);
			li_list1.removeAllViews();
			li_list2.removeAllViews();

			// ---- list 1 -----
			if (jsaList1 != null) {
				li_list.setVisibility(View.VISIBLE);

				for (int i = 0; i < jsaList1.length(); i++) {
					JSONObject jsoIndex = jsaList1.getJSONObject(i);

					View viewSetAlert = ((Activity) context)
							.getLayoutInflater().inflate(
									R.layout.row_setalert_list1, null);

					TextView tv_symbol, tv_price1, tv_date, tv_time, tv_running;
					ImageView img_type, img_manage;

					tv_symbol = (TextView) viewSetAlert
							.findViewById(R.id.tv_symbol);
					tv_price1 = (TextView) viewSetAlert
							.findViewById(R.id.tv_price1);
					tv_date = (TextView) viewSetAlert
							.findViewById(R.id.tv_date);
					tv_time = (TextView) viewSetAlert
							.findViewById(R.id.tv_time);
					tv_running = (TextView) viewSetAlert
							.findViewById(R.id.tv_running);
					img_type = (ImageView) viewSetAlert
							.findViewById(R.id.img_type);
					img_manage = (ImageView) viewSetAlert
							.findViewById(R.id.img_manage);
					img_manage.setVisibility(View.GONE);

					tv_symbol.setText(jsoIndex.getString("symbol_name"));
					tv_price1.setText(jsoIndex.getString("price1"));

					tv_date.setText(DateTimeCreate.DateDmyThaiCreate(jsoIndex
							.getString("create_datetime")));
					tv_time.setText(DateTimeCreate.TimeCreate(jsoIndex
							.getString("create_datetime")));

					if (jsoIndex.getString("type").equals("0")) {
						tv_price1.setTextColor(getResources().getColor(
								R.color.c_success));
						img_type.setBackgroundResource(R.drawable.icon_alert_up);
					} else if (jsoIndex.getString("type").equals("2")) {
						tv_price1.setTextColor(getResources().getColor(
								R.color.c_danger));
						img_type.setBackgroundResource(R.drawable.icon_alert_down);
					} else {
						tv_price1.setTextColor(getResources().getColor(
								R.color.c_content));
						img_type.setBackgroundResource(R.drawable.icon_alert_default);
					}

					// ----------
					final String strId_index = jsoIndex.getString("id");
					final String strOrderbookId_index = jsoIndex
							.getString("orderbook_id");
					final String strSymbolName_index = jsoIndex
							.getString("symbol_name");
					final String strType_index = jsoIndex.getString("type");
					img_manage.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							LayoutInflater layoutInflater = LayoutInflater
									.from(context);
							View dlView = layoutInflater.inflate(
									R.layout.dialog_setalert_row, null);
							final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
									context);
							alertDialogBuilder.setView(dlView);

							LinearLayout li_change = (LinearLayout) dlView
									.findViewById(R.id.li_change);
							LinearLayout li_delete = (LinearLayout) dlView
									.findViewById(R.id.li_delete);

							li_change.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									alertDialogSetAlert.dismiss();
									FragmentChangeActivity.strSymbolSelect = strSymbolName_index;
									// switchFragment(new
									// PagerSetAlertSelect());
								}
							});

							li_delete.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									alertDialogSetAlert.dismiss();
									strId = strId_index;
									strOrderbookId = strOrderbookId_index;
									strSymbolName = strSymbolName_index;
									strType = strType_index;
									FragmentChangeActivity.strSymbolSelect = strSymbolName;
									sendRemoveSetAlert();
								}
							});

							// create an alert dialog
							alertDialogSetAlert = alertDialogBuilder.create();

							alertDialogSetAlert
									.requestWindowFeature(Window.FEATURE_NO_TITLE);
							alertDialogSetAlert
									.getWindow()
									.setBackgroundDrawable(
											new ColorDrawable(
													android.graphics.Color.TRANSPARENT));
							WindowManager.LayoutParams wmlp = alertDialogSetAlert
									.getWindow().getAttributes();

							// wmlp.gravity = Gravity.TOP | Gravity.RIGHT;
							// wmlp.x = 100; // x position
							// wmlp.y = 150; // y position

							alertDialogSetAlert.show();
						}
					});

					li_list1.addView(viewSetAlert);
				}
			}

			// ---- list 2 -----
			if (jsaList2.length() > 0) {
				li_list_hit.setVisibility(View.VISIBLE);

				for (int i = 0; i < jsaList2.length(); i++) {
					JSONObject jsoIndex = jsaList2.getJSONObject(i);

					View viewSetAlert = ((Activity) context)
							.getLayoutInflater().inflate(
									R.layout.row_setalert_list2, null);

					TextView tv_symbol, tv_price1, tv_date, tv_time, tv_time_ago;
					ImageView img_type, img_manage;

					tv_symbol = (TextView) viewSetAlert
							.findViewById(R.id.tv_symbol);
					tv_price1 = (TextView) viewSetAlert
							.findViewById(R.id.tv_price1);
					tv_date = (TextView) viewSetAlert
							.findViewById(R.id.tv_date);
					tv_time = (TextView) viewSetAlert
							.findViewById(R.id.tv_time);
					tv_time_ago = (TextView) viewSetAlert
							.findViewById(R.id.tv_time_ago);
					img_type = (ImageView) viewSetAlert
							.findViewById(R.id.img_type);
					img_manage = (ImageView) viewSetAlert
							.findViewById(R.id.img_manage);
					img_manage.setVisibility(View.GONE);

					tv_symbol.setText(jsoIndex.getString("symbol_name"));
					tv_price1.setText(jsoIndex.getString("price1"));

					tv_date.setText(DateTimeCreate.DateDmyThaiCreate(jsoIndex
							.getString("create_datetime")));
					tv_time.setText(DateTimeCreate.TimeCreate(jsoIndex
							.getString("create_datetime")));

					try {
						tv_time_ago.setText(DateTimeAgo
								.CalDifferentTimeAgoSetAlert(jsoIndex
										.getString("hit_datetime")));
					} catch (ParseException e) {
						e.printStackTrace();
					}

					if (jsoIndex.getString("type").equals("0")) {
						tv_price1.setTextColor(getResources().getColor(
								R.color.c_success));
						img_type.setBackgroundResource(R.drawable.icon_alert_up);
					} else if (jsoIndex.getString("type").equals("2")) {
						tv_price1.setTextColor(getResources().getColor(
								R.color.c_danger));
						img_type.setBackgroundResource(R.drawable.icon_alert_down);
					} else {
						tv_price1.setTextColor(getResources().getColor(
								R.color.c_content));
						img_type.setBackgroundResource(R.drawable.icon_alert_default);
					}

					// ----------
					final String strId_index = jsoIndex.getString("id");
					final String strOrderbookId_index = jsoIndex
							.getString("orderbook_id");
					final String strSymbolName_index = jsoIndex
							.getString("symbol_name");
					final String strType_index = jsoIndex.getString("type");
					img_manage.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							LayoutInflater layoutInflater = LayoutInflater
									.from(context);
							View dlView = layoutInflater.inflate(
									R.layout.dialog_setalert_row, null);
							final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
									context);
							alertDialogBuilder.setView(dlView);

							LinearLayout li_change = (LinearLayout) dlView
									.findViewById(R.id.li_change);
							LinearLayout li_delete = (LinearLayout) dlView
									.findViewById(R.id.li_delete);

							li_change.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									alertDialogSetAlert.dismiss();
									FragmentChangeActivity.strSymbolSelect = strSymbolName_index;
									// switchFragment(new
									// PagerSetAlertSelect());
								}
							});

							li_delete.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									alertDialogSetAlert.dismiss();
									strId = strId_index;
									strOrderbookId = strOrderbookId_index;
									strSymbolName = strSymbolName_index;
									strType = strType_index;
									FragmentChangeActivity.strSymbolSelect = strSymbolName;
									sendRemoveSetAlert();
								}
							});

							// create an alert dialog
							alertDialogSetAlert = alertDialogBuilder.create();

							alertDialogSetAlert
									.requestWindowFeature(Window.FEATURE_NO_TITLE);
							alertDialogSetAlert
									.getWindow()
									.setBackgroundDrawable(
											new ColorDrawable(
													android.graphics.Color.TRANSPARENT));
							WindowManager.LayoutParams wmlp = alertDialogSetAlert
									.getWindow().getAttributes();

							// wmlp.gravity = Gravity.TOP | Gravity.RIGHT;
							// wmlp.x = 100; // x position
							// wmlp.y = 150; // y position

							alertDialogSetAlert.show();
						}
					});

					li_list2.addView(viewSetAlert);
				}
			} else {

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	// ============== remove setalert ===============
	public void sendRemoveSetAlert() {

		setRemoveSetAlert resp = new setRemoveSetAlert();
		resp.execute();
	}

	public class setRemoveSetAlert extends AsyncTask<Void, Void, Void> {

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
					+ "/bidsMaster/removeSetAlertById";

			String json = "";
			InputStream inputStream = null;
			String result = "";

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("id", strId);

				// jsonObject.accumulate("user_id",
				// FragmentChangeActivity.userModel.user_id);
				// jsonObject.accumulate("orderbook_id", strOrderbookId);
				// jsonObject.accumulate("symbol_name", strSymbolName);
				// jsonObject.accumulate("type", strType);

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

				// Log.v("XXXXX", "" +
				// FragmentChangeActivity.userModel.user_id+"_"+strOrderbookId+"_"+strSymbolName+"_"+strType);
				// Log.v("result addSetAlert : ", "" + result);

				// {"result":1,"data":{"id":"117","user_id":"104","type":"0","orderbook_id":"2099","hit_status":"0","symbol_name":"PTT","price1":"272","price2":"0","create_datetime":"2016-02-18 15:00:58","hit_datetime":"","first_hit_datetime":null,"update_datetime":"2016-02-18 15:00:58"}}
				// {"result":1,"data":{"id":"118","user_id":"104","type":"2","orderbook_id":"2099","hit_status":"0","symbol_name":"PTT","price1":"197","price2":"0","create_datetime":"2016-02-18 15:02:27","hit_datetime":"","first_hit_datetime":null,"update_datetime":"2016-02-18 15:02:27"}}

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
			loadDataRetrieveSetAlertAll();
		}
	}

	// the meat of switching the above fragment
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;
		if (getActivity() instanceof FragmentChangeActivity) {
			FragmentChangeActivity fca = (FragmentChangeActivity) getActivity();
			fca.switchContent(fragment);
		}
	}

}