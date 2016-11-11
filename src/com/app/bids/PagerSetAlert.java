package com.app.bids;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import twitter4j.SymbolEntity;

import com.app.bids.R;
import com.app.bids.PagerHomeAll.loadData;
import com.app.bids.PagerSetAlertSelect.loadDataRetrieveSetAler;
import com.app.bids.PagerSetAlertSelect.setAddSetAlert;
import com.app.bids.PagerSystemTrade.loadDataSlidingMarquee;
import com.google.gson.JsonObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class PagerSetAlert extends Fragment {

	static Context context;
	public static View rootView;

	Dialog dialogLoading;

	// --------- google analytics
//	private Tracker mTracker;
//	String nameTracker = new String("Hit List");
	
	public static boolean setALertNewLoad = true;

	public interface OnPageListener {
		public void onPage1(String s);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		// fragment not when container null
		if (container == null) {
			return null;
		}

		// inflate view from layot
		rootView = inflater.inflate(R.layout.pager_setalert, container, false);

		// --------- google analytics
//		GoogleAnalyticsApp application = (GoogleAnalyticsApp) getActivity().getApplication();
//		mTracker = application.getDefaultTracker();
		
		// --------- dialogLoading
		dialogLoading = new Dialog(context);
		dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogLoading.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialogLoading.setContentView(R.layout.progress_bar);
		dialogLoading.setCancelable(false);
		dialogLoading.setCanceledOnTouchOutside(false);
		// dialogLoading.show();

		if (FragmentChangeActivity.contentGetTxtSlidingMarquee != null) {
			initTxtSliding();
		} else {
			loadTxtSlidingMarquee(); // text sliding
		}

		initSearchLayout(); // layout search

		if(setALertNewLoad){
			Log.v("setALertNewLoad", "IIIIIIIIIIIII");
			loadDataRetrieveSetAlertAll();
		}else{
			Log.v("setALertNewLoad", "EEEEEEEEEEEEEEEE");
			setDataSetAlert();
		}

		return rootView;

	}

	@Override
	public void onResume() {
		super.onResume();
//		Log.v(nameTracker, "onResume onResume onResume");
//		
//		mTracker.setScreenName(nameTracker);
//		mTracker.send(new HitBuilders.ScreenViewBuilder().build());
	}
	
	// ***************** search symbol******************

	LinearLayout li_search_tabbegin, li_search_select;
	ImageView img_search;
	LinearLayout li_search;

	ScrollView sv_view;
	EditText et_search;
	TextView tv_close_search, tv_search_common, tv_search_warrant,
			tv_search_dw;
	ListView listview_search;

	// String symbol_search_symbol = "";

	String status_segmentId = "COMMON"; // COMMON, FOREIGN_STOCK, WARRENT, DW
	public static String status_segmentIdDot = "EQUITY_INDEX";

	private void initSearchLayout() {

		// --- linear show tabsearch begin
		li_search_tabbegin = (LinearLayout) rootView
				.findViewById(R.id.li_search_tabbegin);
		li_search_select = (LinearLayout) rootView
				.findViewById(R.id.li_search_select);
		img_search = (ImageView) rootView.findViewById(R.id.img_search);
		// --- linear tabsearch
		li_search = (LinearLayout) rootView.findViewById(R.id.li_search);

		// --- show hide tab search
		li_search_tabbegin.setVisibility(View.VISIBLE);
		li_search.setVisibility(View.GONE);
		li_search_select.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if ((FragmentChangeActivity.contentGetSymbol == null)) {
					Toast.makeText(context, "Symbol Empty.", 0).show();
				} else {
					initSearchSymbol(); // search symbol
				}
			}
		});

		img_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if ((FragmentChangeActivity.contentGetSymbol == null)) {
					Toast.makeText(context, "Symbol Empty.", 0).show();
				} else {
					initSearchSymbol(); // search symbol
				}
			}
		});

	}

	private void initSearchSymbol() {

		// sv_search_symbol = (ScrollView)
		// rootView.findViewById(R.id.sv_search_symbol);
		li_search_tabbegin.setVisibility(View.GONE);
		li_search.setVisibility(View.VISIBLE);

		sv_view = (ScrollView) rootView.findViewById(R.id.sv_view);
		et_search = (EditText) rootView.findViewById(R.id.et_search);
		tv_close_search = (TextView) rootView
				.findViewById(R.id.tv_close_search);

		// search
		final ListAdapterSearchSymbolSetAlert ListAdapterSearch;
		final ArrayList<CatalogGetSymbol> original_list;
		final ArrayList<CatalogGetSymbol> second_list;

		original_list = new ArrayList<CatalogGetSymbol>();
		original_list.addAll(FragmentChangeActivity.list_getSymbol);
		second_list = new ArrayList<CatalogGetSymbol>();
		second_list.addAll(FragmentChangeActivity.list_getSymbol);

		tv_search_common = (TextView) rootView
				.findViewById(R.id.tv_search_common);
		tv_search_warrant = (TextView) rootView
				.findViewById(R.id.tv_search_warrant);
		tv_search_dw = (TextView) rootView.findViewById(R.id.tv_search_dw);

		listview_search = (ListView) rootView
				.findViewById(R.id.list_search_symbol);
		listview_search.setVisibility(View.GONE);

		ListAdapterSearch = new ListAdapterSearchSymbolSetAlert(context, 0,
				second_list);
		listview_search.setAdapter(ListAdapterSearch);

		et_search.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String text = et_search.getText().toString();
				if (text.length() > 0) {
					sv_view.setVisibility(View.GONE);
					listview_search.setVisibility(View.VISIBLE);

					second_list.clear();
					for (int i = 0; i < original_list.size(); i++) {
						if (original_list.get(i).symbol.toLowerCase().contains(
								text.toString().toLowerCase())) {
							if (((original_list.get(i).status_segmentId)
									.equals(status_segmentId)) || ((original_list.get(i).status_segmentId)
											.equals(status_segmentIdDot))) {
								second_list.add(original_list.get(i));
							}
						}
					}
					ListAdapterSearch.notifyDataSetChanged();
				} else {
					sv_view.setVisibility(View.VISIBLE);
					listview_search.setVisibility(View.GONE);
				}
			}
		});

		// -------------------------
		tv_search_common.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				status_segmentId = "COMMON"; // COMMON, FOREIGN_STOCK, WARRENT,
												// DW
				setTitleSearch();
				tv_search_common.setTextColor(getResources().getColor(
						R.color.bg_default));
				tv_search_common
						.setBackgroundResource(R.drawable.border_search_activeleft);

				second_list.clear();
				for (int i = 0; i < original_list.size(); i++) {
					if (original_list.get(i).symbol.toLowerCase().contains(
							et_search.getText().toString().toLowerCase())) {
						if (((original_list.get(i).status_segmentId)
								.equals(status_segmentId)) || ((original_list.get(i).status_segmentId)
										.equals(status_segmentIdDot))) {
							second_list.add(original_list.get(i));
						}
					}
				}
				ListAdapterSearch.notifyDataSetChanged();
			}
		});
		tv_search_warrant.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				status_segmentId = "WARRENT"; // COMMON, FOREIGN_STOCK, WARRENT,
												// DW
				setTitleSearch();
				tv_search_warrant.setTextColor(getResources().getColor(
						R.color.bg_default));
				tv_search_warrant
						.setBackgroundResource(R.drawable.border_search_activecenter);

				second_list.clear();
				for (int i = 0; i < original_list.size(); i++) {
					if (original_list.get(i).symbol.toLowerCase().contains(
							et_search.getText().toString().toLowerCase())) {
						if (((original_list.get(i).status_segmentId)
								.equals(status_segmentId)) || ((original_list.get(i).status_segmentId)
										.equals(status_segmentIdDot))) {
							second_list.add(original_list.get(i));
						}
					}
				}
				ListAdapterSearch.notifyDataSetChanged();
			}
		});
		tv_search_dw.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				status_segmentId = "DW"; // COMMON, FOREIGN_STOCK, WARRENT, DW
				setTitleSearch();
				tv_search_dw.setTextColor(getResources().getColor(
						R.color.bg_default));
				tv_search_dw
						.setBackgroundResource(R.drawable.border_search_activeright);

				second_list.clear();
				for (int i = 0; i < original_list.size(); i++) {
					if (original_list.get(i).symbol.toLowerCase().contains(
							et_search.getText().toString().toLowerCase())) {
						if (((original_list.get(i).status_segmentId)
								.equals(status_segmentId)) || ((original_list.get(i).status_segmentId)
										.equals(status_segmentIdDot))) {
							second_list.add(original_list.get(i));
						}
					}
				}
				ListAdapterSearch.notifyDataSetChanged();
			}
		});
		// -------------------------
		tv_close_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				et_search.setText("");
				sv_view.setVisibility(View.VISIBLE);

				li_search_tabbegin.setVisibility(View.VISIBLE);
				li_search.setVisibility(View.GONE);
				// listview_search.setVisibility(View.GONE);
			}
		});
	}

	// ******* set title search ****
	private void setTitleSearch() {
		tv_search_common.setTextColor(getResources()
				.getColor(R.color.c_content));
		tv_search_warrant.setTextColor(getResources().getColor(
				R.color.c_content));
		tv_search_dw.setTextColor(getResources().getColor(R.color.c_content));
		tv_search_common.setBackgroundColor(Color.TRANSPARENT);
		tv_search_warrant.setBackgroundColor(Color.TRANSPARENT);
		tv_search_dw.setBackgroundColor(Color.TRANSPARENT);
	}

	// ***************** SlidingMarquee ******************
	private void loadTxtSlidingMarquee() {
		loadDataSlidingMarquee resp = new loadDataSlidingMarquee();
		resp.execute();
	}

	public class loadDataSlidingMarquee extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		private JSONObject jsonTxtSlidingMarquee;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();

			// FragmentChangeActivity.url_bidschart+"/service/v2/symbolByIndustrySector?industry=TECH&sector=TECH&page=1&limit=10
			// ** top = swing , volume , value , gainer , loser
			String url_TxtSlidingMarquee = SplashScreen.url_bidschart
					+ "/service/v2/getMaiSet";

			try {
				jsonTxtSlidingMarquee = ReadJson
						.readJsonObjectFromUrl(url_TxtSlidingMarquee);
			} catch (IOException e1) {
				connectionError = true;
				jsonTxtSlidingMarquee = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonTxtSlidingMarquee = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonTxtSlidingMarquee = null;
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (jsonTxtSlidingMarquee != null) {
					try {

						FragmentChangeActivity.contentGetTxtSlidingMarquee = jsonTxtSlidingMarquee
								.getJSONArray("dataAll");

						initTxtSliding();

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.v("jsonGetData", "jsonGetData null");
				}

			} else {
				Log.v("connectionError", "connectionError ture");
			}
		}
	}

	// ***************** text sliding ******************
	private void initTxtSliding() {
		String strSliding = "";
		TextView marque = (TextView) rootView
				.findViewById(R.id.tv_sliding_marquee);

		if (FragmentChangeActivity.contentGetTxtSlidingMarquee != null) {

			for (int i = 0; i < FragmentChangeActivity.contentGetTxtSlidingMarquee
					.length(); i++) {
				try {
					String txtSymbol = FragmentChangeActivity.contentGetTxtSlidingMarquee
							.getJSONObject(i).getString("symbol_name");
					String txtLtrade = FragmentChangeActivity.contentGetTxtSlidingMarquee
							.getJSONObject(i).getString("last_trade");
					String txtChange = FragmentChangeActivity.contentGetTxtSlidingMarquee
							.getJSONObject(i).getString("change");
					String txtPchange = FragmentChangeActivity.contentGetTxtSlidingMarquee
							.getJSONObject(i).getString("percentChange");

					String txtSetPChenge = FragmentChangeActivity.contentGetTxtSlidingMarquee
							.getJSONObject(0).getString("percentChange");

					if (txtLtrade != "") {
						double db = Double.parseDouble(txtLtrade);
						DecimalFormat formatter = new DecimalFormat("#,###.00");
						txtLtrade = formatter.format(db);
					}

					if (txtChange != "") {
						double db = Double.parseDouble(txtChange);
						txtChange = String.format(" %,.2f", db);
					}

					if (txtSymbol.equals(".SET")) {
						if (txtPchange != "") {
							double dbS = Double.parseDouble(txtSetPChenge);
							txtPchange = FunctionSetBg
									.setColorTxtSlidingSet(txtSetPChenge);
						}
					} else {
						if (txtPchange != "") {
							double dbS = Double.parseDouble(txtSetPChenge);
							double dbO = Double.parseDouble(txtPchange);
							txtPchange = FunctionSetBg
									.setColorTxtSliding(txtSetPChenge,
											txtPchange);
						}
					}

					// strSliding += "   <font color='#95dd33'>" + txtSymbol
					// + "</font>  " + txtLtrade + "  " + txtChange
					// + "<font color='#eb4848'>" + txtPchange
					// + "</font> ";

					strSliding += "   " + txtSymbol + "   " + txtLtrade + "  "
							+ txtChange + "" + txtPchange;

				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}

		marque.setText(Html.fromHtml(strSliding), TextView.BufferType.SPANNABLE);
		marque.setSelected(true);
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
					FragmentChangeActivity.contentGetRetrieveSetAlert = jsonGetDetail;

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
	String strId, strOrderbookId, strType;
	Dialog alertDialogSetAlert;

	public static JSONObject jsoSetAlertSelectChange = null;
	private void setDataSetAlert() {
		// dialogLoading.dismiss();
		setALertNewLoad = false;

		LinearLayout li_list = (LinearLayout) rootView
				.findViewById(R.id.li_list);
		LinearLayout li_list_hit = (LinearLayout) rootView
				.findViewById(R.id.li_list_hit);

		li_list.setVisibility(View.GONE);
		li_list_hit.setVisibility(View.GONE);

		try {
			JSONArray jsaList1 = FragmentChangeActivity.contentGetRetrieveSetAlert
					.getJSONArray("list1");
			JSONArray jsaList2 = FragmentChangeActivity.contentGetRetrieveSetAlert
					.getJSONArray("list2");

			LinearLayout li_list1 = (LinearLayout) rootView
					.findViewById(R.id.li_list1);
			LinearLayout li_list2 = (LinearLayout) rootView
					.findViewById(R.id.li_list2);
			li_list1.removeAllViews();
			li_list2.removeAllViews();

			// ---- list 1 -----
			if (jsaList1.length() > 0) {
				li_list.setVisibility(View.VISIBLE);

				for (int i = 0; i < jsaList1.length(); i++) {
					final JSONObject jsoIndex = jsaList1.getJSONObject(i);

					View viewSetAlert = ((Activity) context)
							.getLayoutInflater().inflate(
									R.layout.row_setalert_list1, null);

					TextView tv_symbol, tv_price1, tv_date, tv_time, tv_running;
					ImageView img_type, img_manage, img_stoploss;

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
					img_stoploss = (ImageView) viewSetAlert
							.findViewById(R.id.img_stoploss);

					// ----------
					final String strId_index = jsoIndex.getString("id");
					final String strOrderbookId_index = jsoIndex
							.getString("orderbook_id");
					final String strSymbol = jsoIndex.getString("symbol_name");
					String strIs_stoploss = jsoIndex.getString("is_stoploss");
					
					if(strIs_stoploss.equals("0")){
						img_stoploss.setBackgroundResource(R.drawable.img_bidshit_takeprofit);
					}else{
						img_stoploss.setBackgroundResource(R.drawable.img_bidshit_stoploss);
					}
					
					tv_symbol.setText(strSymbol);
					tv_symbol.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							FragmentChangeActivity.strSymbolSelect = strSymbol;

							startActivity(new Intent(context,
									UiWatchlistDetail.class));
						}
					});
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

					// final String strSymbolName_index = jsoIndex
					// .getString("symbol_name");
					final String strType_index = jsoIndex.getString("type");

					final LinearLayout row_symbol = (LinearLayout) viewSetAlert
							.findViewById(R.id.row_symbol);
					img_manage.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							alertDialogSetAlert = new Dialog(context);
							LayoutInflater inflater = LayoutInflater
									.from(context);
							View dlView = inflater.inflate(
									R.layout.dialog_setalert_row, null);
							// perform operation on elements in Layout
							alertDialogSetAlert
									.requestWindowFeature(Window.FEATURE_NO_TITLE);
							alertDialogSetAlert.setContentView(dlView);

							LinearLayout li_change = (LinearLayout) dlView
									.findViewById(R.id.li_change);
							LinearLayout li_delete = (LinearLayout) dlView
									.findViewById(R.id.li_delete);

							li_change.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									alertDialogSetAlert.dismiss();
									FragmentChangeActivity.strSymbolSelect = strSymbol;
									jsoSetAlertSelectChange = jsoIndex;									
									switchFragment(new PagerSetAlertSelectChange());
//									switchFragment(new PagerSetAlertSelect());
								}
							});

							li_delete.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									alertDialogSetAlert.dismiss();
									strId = strId_index;
									strOrderbookId = strOrderbookId_index;
									// strSymbolName = strSymbolName_index;
									strType = strType_index;
									FragmentChangeActivity.strSymbolSelect = strSymbol;
									sendRemoveSetAlert();
								}
							});

							alertDialogSetAlert.show();

							// set width height dialog
							DisplayMetrics displaymetrics = new DisplayMetrics();
							WindowManager windowManager = (WindowManager) context
									.getSystemService(Context.WINDOW_SERVICE);
							windowManager.getDefaultDisplay().getMetrics(
									displaymetrics);

							int ScreenHeight = displaymetrics.heightPixels;
							int ScreenWidth = displaymetrics.widthPixels;

							WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
							lp.gravity = Gravity.RIGHT;
							// lp.width = (int) (ScreenWidth * 0.87);
							// lp.height = (int) (ScreenHeight * 0.67);
							// alertDialogSetAlert.getWindow().setAttributes(lp);

							alertDialogSetAlert
									.getWindow()
									.setBackgroundDrawable(
											new ColorDrawable(
													android.graphics.Color.TRANSPARENT));

						}
					});

					li_list1.addView(viewSetAlert);
				}
			}

			// ---- list 2 -----
			if (jsaList2.length() > 0) {
				li_list_hit.setVisibility(View.VISIBLE);

				for (int i = 0; i < jsaList2.length(); i++) {
					final JSONObject jsoIndex = jsaList2.getJSONObject(i);

					View viewSetAlert = ((Activity) context)
							.getLayoutInflater().inflate(
									R.layout.row_setalert_list2, null);

					TextView tv_symbol, tv_price1, tv_date, tv_time, tv_time_ago;
					ImageView img_type, img_manage, img_stoploss;

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
					img_stoploss = (ImageView) viewSetAlert
							.findViewById(R.id.img_stoploss);

					// ----------
					final String strId_index = jsoIndex.getString("id");
					final String strOrderbookId_index = jsoIndex
							.getString("orderbook_id");
					final String strSymbol = jsoIndex.getString("symbol_name");
					String strIs_stoploss = jsoIndex.getString("is_stoploss");
					
					if(strIs_stoploss.equals("0")){
						img_stoploss.setBackgroundResource(R.drawable.img_bidshit_takeprofit);
					}else{
						img_stoploss.setBackgroundResource(R.drawable.img_bidshit_stoploss);
					}
					
					tv_symbol.setText(strSymbol);
					tv_symbol.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							FragmentChangeActivity.strSymbolSelect = strSymbol;

							startActivity(new Intent(context,
									UiWatchlistDetail.class));
						}
					});

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

					// final String strSymbolName_index = jsoIndex
					// .getString("symbol_name");
					final String strType_index = jsoIndex.getString("type");
					img_manage.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							alertDialogSetAlert = new Dialog(context);
							LayoutInflater inflater = LayoutInflater
									.from(context);
							View dlView = inflater.inflate(
									R.layout.dialog_setalert_row, null);
							// perform operation on elements in Layout
							alertDialogSetAlert
									.requestWindowFeature(Window.FEATURE_NO_TITLE);
							alertDialogSetAlert.setContentView(dlView);

							LinearLayout li_change = (LinearLayout) dlView
									.findViewById(R.id.li_change);
							LinearLayout li_delete = (LinearLayout) dlView
									.findViewById(R.id.li_delete);

							li_change.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									alertDialogSetAlert.dismiss();
									FragmentChangeActivity.strSymbolSelect = strSymbol;
									jsoSetAlertSelectChange = jsoIndex;					
									switchFragment(new PagerSetAlertSelectChange());
//									switchFragment(new PagerSetAlertSelect());
								}
							});

							li_delete.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									alertDialogSetAlert.dismiss();
									strId = strId_index;
									strOrderbookId = strOrderbookId_index;
									// strSymbolName = strSymbolName_index;
									strType = strType_index;
									FragmentChangeActivity.strSymbolSelect = strSymbol;
									sendRemoveSetAlert();
								}
							});

							alertDialogSetAlert.show();

							// set width height dialog
							DisplayMetrics displaymetrics = new DisplayMetrics();
							WindowManager windowManager = (WindowManager) context
									.getSystemService(Context.WINDOW_SERVICE);
							windowManager.getDefaultDisplay().getMetrics(
									displaymetrics);

							int ScreenHeight = displaymetrics.heightPixels;
							int ScreenWidth = displaymetrics.widthPixels;

							WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
							lp.gravity = Gravity.RIGHT;
							// lp.width = (int) (ScreenWidth * 0.87);
							// lp.height = (int) (ScreenHeight * 0.67);
							// alertDialogSetAlert.getWindow().setAttributes(lp);

							alertDialogSetAlert
									.getWindow()
									.setBackgroundDrawable(
											new ColorDrawable(
													android.graphics.Color.TRANSPARENT));
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
			dialogLoading.show();

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
					result = AFunctionOther.convertInputStreamToString(inputStream);
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
			dialogLoading.dismiss();
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