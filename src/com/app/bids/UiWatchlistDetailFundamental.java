package com.app.bids;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.security.auth.PrivateCredentialPermission;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;
import com.app.bids.FragmentChangeActivity.myWebClient;
import com.app.bids.PagerWatchListDetailNews.loadAll;
import com.app.bids.colorpicker.ColorPickerAdapter;
import com.app.bids.colorpicker.ColorPickerDialog;
import com.app.model.login.LoginDialog;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData.Item;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class UiWatchlistDetailFundamental extends FragmentActivity {

	public static Activity act;

	public static WebSocketClient mWebSocketClient;

	public static Dialog dialogLoading;

	// --------- google analytics
	// private Tracker mTracker;
	// String nameTracker = new String("Detail");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.ui_watchlist_detail_fundamental);

		// // --------- google analytics
		// Tracker t = ((GoogleAnalyticsApp) getApplicationContext())
		// .getTracker(TrackerName.APP_TRACKER);
		// t.setScreenName(nameTracker);
		// t.send(new HitBuilders.AppViewBuilder().build());

		// --------- google analytics
		// GoogleAnalyticsApp application = (GoogleAnalyticsApp)
		// getApplicationContext();
		// mTracker = application.getDefaultTracker();

		// set view
		initView();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		// Log.v(nameTracker, "onResume onResume onResume");
		//
		// mTracker.setScreenName(nameTracker);
		// mTracker.send(new HitBuilders.ScreenViewBuilder().build());
	}

	// ============== init view ===============
	private void initView() {

		((LinearLayout) findViewById(R.id.li_back))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});
		
		((TextView)findViewById(R.id.tv_back)).setText(FragmentChangeActivity.strSymbolSelect+"' Detail");

		// -------- stop timer ----
		if (FragmentChangeActivity.timerUpdateSymbolStatus) {
			FragmentChangeActivity.timerUpdateSymbolStatus = false;
			FragmentChangeActivity.timerUpdateSymbol.cancel();
		}

		dialogLoading = new Dialog(UiWatchlistDetailFundamental.this);
		dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogLoading.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialogLoading.setContentView(R.layout.progress_bar);
		dialogLoading.setCancelable(false);
		dialogLoading.setCanceledOnTouchOutside(false);

		if (FragmentChangeActivity.contentGetTxtSlidingMarquee != null) {
			initTxtSliding();
		} else {
			loadTxtSlidingMarquee(); // text sliding
		}

		setDataDetail(); // setdata detail
		if ((UiWatchlistDetail.contentGetFundamental != null)
				&& (UiWatchlistDetail.contentGetFundamental != null)) {
			setDataFinancial();
			setDataFundamental();
		} else {
			loadDataDetailFund();
		}
	}

	// ***************** text sliding ******************
	private void initTxtSliding() {
		String strSliding = "";

		com.app.custom.CustomTextViewSliding marque = (com.app.custom.CustomTextViewSliding) findViewById(R.id.tv_sliding_marquee);
		ImageView img_status_m = (ImageView) findViewById(R.id.img_status_m);

		try {
			if (FragmentChangeActivity.contentGetTxtSlidingMarquee != null) {

				// status market
				img_status_m.setBackgroundResource(FunctionSetBg
						.setMarketStatus(FragmentChangeActivity.jsonTxtSlidingMarquee
								.getString("market")));

				for (int i = 0; i < FragmentChangeActivity.contentGetTxtSlidingMarquee
						.length(); i++) {

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
						double db = Double.parseDouble(txtLtrade.replaceAll(
								",", ""));
						txtLtrade = FunctionFormatData
								.setFormatNumber(txtLtrade);
					}

					if (txtChange != "") {
						txtChange = FunctionFormatData
								.setFormatNumber(txtChange);
					}

					if (txtSymbol.equals(".SET")) {
						if (txtPchange != "") {
							double dbS = Double.parseDouble(txtSetPChenge
									.replaceAll(",", ""));
							txtPchange = FunctionSetBg
									.setColorTxtSlidingSet(txtSetPChenge);
						}
					} else {
						if (txtPchange != "") {
							double dbS = Double.parseDouble(txtSetPChenge
									.replaceAll(",", ""));
							double dbO = Double.parseDouble(txtPchange
									.replaceAll(",", ""));
							txtPchange = FunctionSetBg.setColorTxtSliding(
									txtSetPChenge, txtPchange);
						}
					}

					strSliding += "   " + txtSymbol + "   " + txtLtrade + "  "
							+ txtChange + "" + txtPchange;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		marque.setText(Html.fromHtml(strSliding), TextView.BufferType.SPANNABLE);
		marque.setSelected(true);
	}

	// ***************** SlidingMarquee ******************
	private void loadTxtSlidingMarquee() {
		loadDataSlidingMarquee resp = new loadDataSlidingMarquee();
		resp.execute();
	}

	public class loadDataSlidingMarquee extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();

			// SplashScreen.url_bidschart+"/service/v2/symbolByIndustrySector?industry=TECH&sector=TECH&page=1&limit=10
			// ** top = swing , volume , value , gainer , loser

			// http://www.bidschart.com/service/v2/getMaiSet
			String url_TxtSlidingMarquee = SplashScreen.url_bidschart
					+ "/service/v2/getMaiSet";

			try {
				FragmentChangeActivity.jsonTxtSlidingMarquee = ReadJson
						.readJsonObjectFromUrl(url_TxtSlidingMarquee);
			} catch (IOException e1) {
				connectionError = true;
				FragmentChangeActivity.jsonTxtSlidingMarquee = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				FragmentChangeActivity.jsonTxtSlidingMarquee = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				FragmentChangeActivity.jsonTxtSlidingMarquee = null;
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (FragmentChangeActivity.jsonTxtSlidingMarquee != null) {
					try {

						FragmentChangeActivity.contentGetTxtSlidingMarquee = FragmentChangeActivity.jsonTxtSlidingMarquee
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

	// ============== Load Data fund =============
	public void loadDataDetailFund() {
		loadDataFund resp = new loadDataFund();
		resp.execute();
	}

	public class loadDataFund extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;
		// ======= json ========
		private JSONObject jsonGetFundamental;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progress.show();

			dialogLoading.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// ======= url ========
			String url_GetFundamental = SplashScreen.url_bidschart
					+ "/service/v2/getFundamentalBySymbol?symbol="
					+ FragmentChangeActivity.strSymbolSelect + "&timestamp="
					+ timestamp;

			// http://bidschart.com/service/v2/getFinancialData?symbol=ppp
			String url_GetFinancialData = SplashScreen.url_bidschart
					+ "/service/v2/getFinancialData?symbol="
					+ FragmentChangeActivity.strSymbolSelect + "&timestamp="
					+ timestamp;

			try {
				jsonGetFundamental = ReadJson
						.readJsonObjectFromUrl(url_GetFundamental);
				UiWatchlistDetail.contentGetFinancialData = ReadJson
						.readJsonObjectFromUrl(url_GetFinancialData);
			} catch (IOException e1) {
				connectionError = true;
				jsonGetFundamental = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonGetFundamental = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonGetFundamental = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				try {
					// financial data
					if (UiWatchlistDetail.contentGetFinancialData != null) {
						setDataFinancial();
					} else {
						Log.v("json null", "jsonGetFinancialData null");
					}

					// fundamental
					if (jsonGetFundamental != null) {
						if ((jsonGetFundamental.getString("status"))
								.equals("ok")) {
							UiWatchlistDetail.contentGetFundamental = jsonGetFundamental
									.getJSONObject("dataAll");
						} else {
							UiWatchlistDetail.contentGetFundamental = null;
						}

						setDataFundamental();
					} else {
						Log.v("json null", "jsonGetFundamental null");
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Log.v("json jsonGetFundamental null", "jsonGetFundamental null");
			}
		}

	}

	// li_height show(22625): 118_502_626_2292_1672
	// li_height show(26401): 118_502_658_2292_1014_1046

	// li_height show(26401): 118_502_893_2292_779_983
	// li_height hide(26401): 118_502_689_2292_1672_779
	// li_height show(26401): 118_502_689_2292_983_1672
	// li_height show(26401): 118_502_689_2292_983_983
	// li_height show(26401): 118_502_893_2292_779_983
	// li_height show(26401): 118_502_689_2292_983_779 // 983
	// li_height hide(26401): 118_502_893_2292_1672_983 // 779

	// set realtime
	public static String strGetSymbolOrderBook_Id = "";
	public static TextView change_tv_last_trade;
	public static TextView change_tv_change;
	public static TextView change_tv_percent_change;
	public static TextView change_tv_high;
	public static TextView change_tv_low;
	public static TextView change_tv_volume;
	public static TextView change_tv_value;
	public static TextView change_tv_buy_volume;
	public static TextView change_tv_buy_value;
	public static TextView change_tv_sell_volume;
	public static TextView change_tv_sell_value;
	public static TextView change_tv_avg_buy;
	public static TextView change_tv_avg_sell;
	public static TextView change_tv_max_buy_price;
	public static TextView change_tv_max_buy_price_volume;
	public static TextView change_tv_max_sell_price;
	public static TextView change_tv_max_sell_price_volume;
	public static TextView change_tv_open_price;
	public static TextView change_tv_open1_price;
	public static TextView change_tv_open1_price_volume;
	public static TextView change_tv_open2_price;
	public static TextView change_tv_open2_price_volume;
	public static TextView change_tv_close_volume;

	// main
	TextView tv_symbol, tv_symbol_status, tv_last_trade, tv_symbol_name_eng,
			tv_change, tv_percenchange;

	private void setDataDetail() {

		try {
			if (UiWatchlistDetail.contentGetDetail != null) {
				// main
				tv_symbol = (TextView) findViewById(R.id.tv_symbol);
				tv_symbol_status = (TextView) findViewById(R.id.tv_symbol_status);
				tv_last_trade = (TextView) findViewById(R.id.tv_last_trade);
				tv_symbol_name_eng = (TextView) findViewById(R.id.tv_symbol_name_eng);
				tv_change = (TextView) findViewById(R.id.tv_change);
				tv_percenchange = (TextView) findViewById(R.id.tv_percenchange);
				// ======= set data ========
				((TextView) findViewById(R.id.tv_title_symbol))
						.setText(FragmentChangeActivity.strSymbolSelect
								+ "'s Fundamental");
				// main
				String strSymbol_name = UiWatchlistDetail.contentGetDetail
						.getString("symbol_name");
				String strOrderbookId = UiWatchlistDetail.contentGetDetail
						.getString("orderbook_id");
				String strLast_trade = UiWatchlistDetail.contentGetDetail
						.getString("last_trade");
				String strSymbol_fullname_eng = UiWatchlistDetail.contentGetDetail
						.getString("symbol_fullname_eng");

				String turnover_list_level = UiWatchlistDetail.contentGetDetail
						.getString("turnover_list_level");
				String status = UiWatchlistDetail.contentGetDetail
						.getString("status");
				String status_xd = UiWatchlistDetail.contentGetDetail
						.getString("status_xd");
				strGetSymbolOrderBook_Id = strOrderbookId;

				// tv_symbol.setText(Html.fromHtml(FunctionSymbol
				// .checkStatusSymbol(strSymbol_name, turnover_list_level,
				// status, status_xd)));

				tv_symbol.setText(strSymbol_name);
				tv_symbol_status.setText(FunctionFormatData
						.checkStatusSymbolDetail(turnover_list_level, status,
								status_xd));

				tv_last_trade.setText(FunctionFormatData
						.setFormatNumber(strLast_trade));
				tv_symbol_name_eng.setText(strSymbol_fullname_eng);

				// main color
				String strColor = UiWatchlistDetail.contentGetDetail
						.getString("change");

				// ck ltrade change
				String strLastTrade = UiWatchlistDetail.contentGetDetail
						.getString("last_trade");
				String strChange = UiWatchlistDetail.contentGetDetail
						.getString("change");
				String strPercentChange = UiWatchlistDetail.contentGetDetail
						.getString("percentChange");

				tv_last_trade.setText(strLastTrade);
				// tv_change.setText(strChange);
				if ((strPercentChange == "0") || (strPercentChange == "")
						|| (strPercentChange == "0.00")) {
					tv_percenchange.setText("0.00");
				} else {
					// tv_percenchange.setText(strPercentChange + "%");
					tv_change.setText(FunctionFormatData
							.setFormatNumber(strChange));
					tv_percenchange.setText(" ("
							+ FunctionFormatData
									.setFormatNumber(strPercentChange) + "%)");
				}

				// เซตสี change , lasttrade, percentchange เป็นสีตาม
				// change โดยเอา change เทียบกับ 0
				if (strChange != "") {
					tv_last_trade.setTextColor(getResources().getColor(
							FunctionSetBg.setColor(strChange)));
					tv_change.setTextColor(getResources().getColor(
							FunctionSetBg.setColor(strChange)));
					tv_percenchange.setTextColor(getResources().getColor(
							FunctionSetBg.setColor(strChange)));
				}

				// ------ connect socket -----------------
				if (FragmentChangeActivity.strGetSymbolOrderBook_Id != "") {
					if (!(SplashScreen.contentGetUserById.getString("package")
							.equals("free"))) {
						connectWebSocket();
					}
				}
			} else {
				Toast.makeText(getApplicationContext(), "No data.", 0).show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ============= connect socket ===============
	public static JSONObject jsoConnectSocket;
	public static JSONArray jsaMassageSocket;

	public void connectWebSocket() {
		URI uri;
		try {
			uri = new URI(SplashScreen.url_websocket);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return;
		}

		mWebSocketClient = new WebSocketClient(uri) {
			@Override
			public void onOpen(ServerHandshake serverHandshake) {

				// V/Websocket(27790): Error failed to connect to
				// 2008.bidschart.com/61.91.124.163 (port 4504) after 90000ms:
				// isConnected failed: ECONNREFUSED (Connection refused)

				// {"id":0,"user_id":0,"orderbook_list":"1024,1062,1063,1064,1025","date":"","hh":"","mm":"","requestType":"watchList"};
				// {"id":0,"user_id":0,"orderbook_list":"2493,1213,10542,1881,7170,4262,1207,2310","date":"","hh":"","mm":"","requestType":"watchList"}

				// jsonObj send connect(14720):
				// {"id":0,"user_id":0,"orderbook_list":"2493,1213,10542,1881,7170,4262,1207,2310","date":"","hh":"","mm":"","requestType":"watchList"}
				// Websocket(14720): Error Attempt to invoke virtual method
				// 'void
				// android.support.v4.app.FragmentActivity.runOnUiThread(java.lang.Runnable)'
				// on a null object reference

				jsoConnectSocket = new JSONObject();
				try {
					jsoConnectSocket.put("id",
							FragmentChangeActivity.id_websocket);
					jsoConnectSocket.put("user_id", 6);
					jsoConnectSocket.put("orderbook_list",
							strGetSymbolOrderBook_Id);
					jsoConnectSocket.put("date", "");
					jsoConnectSocket.put("hh", "");
					jsoConnectSocket.put("mm", "");
					jsoConnectSocket.put("requestType", "symbolRealtime");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Log.v("jsonObj send connect symbol",
						jsoConnectSocket.toString());

				// 11-04 10:12:02.208: V/jsonObj send connect(25506):
				// {"id":6,"user_id":0,"orderbook_list":",PTT,.SET,PPP,AAV,A,AKR,DTAC,CRYSTAL,ASK,CBG,KTB,KBS,KBANK,CCN,UKEM,RS,JAS,MEGA,BBL,SIMAT209910245530426217751653183162171342114901793282218171054217162152175069791213188120991024553042621775165318316217134211490179328221817105421716215217506979121318812099,PTT,.SET,PPP,AAV,A,AKR,DTAC,CRYSTAL,ASK,CBG,KTB,KBS,KBANK,CCN,UKEM,RS,JAS,MEGA,BBL,SIMAT2099102455304262177516531831621713421149017932822181710542171621521750697912131881209910245530426217751653183162171342114901793282218171054217162152175069791213188120991024,PTT,.SET,PPP,AAV,A,AKR,DTAC,CRYSTAL,ASK,CBG,KTB,KBS,KBANK,CCN,UKEM,RS,JAS,MEGA,BBL,SIMAT20991024553042621775165318316217134211490179328221817105421716215217506979121318812099102455304262177516531831621713421149017932822181710542171621521750697912131881209910245530,PTT,.SET,PPP,AAV,A,AKR,DTAC,CRYSTAL,ASK,CBG,KTB,KBS,KBANK,CCN,UKEM,RS,JAS,MEGA,BBL,SIMAT209910245530426217751653183162171342114901793282218171054217162152175069791213188120991024553042621775165318316217134211490179328221817105421716215217506979121318812099102455304262,PTT,.SET,PPP,AAV,A,AKR,DTAC,CRYSTAL,ASK,CBG,KTB,KBS,KBANK,CCN,UKEM,RS,JAS,MEGA,BBL,SIMAT2099102455304262177516531831621713421149017932822181710542171621521750697912131881209910245530426217751653183162171342114901793282218171054217162152175069791213188120991024553042621775,PTT,.SET,PPP,AAV,A,AKR,DTAC,CRYSTAL,ASK,CBG,KTB,KBS,KBANK,CCN,UKEM,RS,JAS,MEGA,BBL,SIMAT20991024553042621775165318316217134211490179328221817105421716215217506979121318812099102455304262177516531831621713421149017932822181710542171621521750697912131881209910245530426217751653,PTT,.SET,PPP,AAV,A,AKR,DTAC,CRYSTAL,ASK,CBG,KTB,KBS,KBANK,CCN,UKEM,RS,JAS,MEGA,BBL,SIMAT209910245530426217751653183162171342114901793282218171054217162152175069791213188120991024553042621775165318316217134211490179328221817105421716215217506979121318812099102455304262177516531831,PTT,.SET,PPP,AAV,A,AKR,DTAC,CRYSTAL,ASK,CBG,KTB,KBS,KBANK,CCN,UKEM,RS,JAS,MEGA,BBL,SIMAT2099102455304262177516531831621713421149017932822181710542171621521750697912131881209910245530426217751653183162171342114901793282218171054217162152175069791213188120991024553042621775165318316217,PTT,.SET,PPP,AAV,A,AKR,DTAC,CRYSTAL,ASK,CBG,KTB,KBS,KBANK,CCN,UKEM,RS,JAS,MEGA,BBL,SIMAT20991024553042621775165318316217134211490179328221817105421716215217506979121318812099102455304262177516531831621713421149017932822181710542171621521750697912131881209910245530426217751653183162171342,PTT,.SET,PPP,AAV,A,AKR,DTAC,CRYSTAL,ASK,CBG,KTB,KBS,KBANK,CCN,UKEM,RS,JAS,MEGA,BBL,SIMAT2099102455304262177516531831621713421149017932822181710542171621521750697912131881209910245530426217751653183162171342114901793282218171054217162152175069791213188120991024553042621775165318316217134211490,PTT,.SET,PPP,AAV,A,AKR,DTAC,CRYSTAL,ASK,CBG,KTB,KBS,KBANK,CCN,UKEM,RS,JAS,MEGA,BBL,SIMAT20991024553042621775165318316217134211490179328221817105421716215217506979121318812099102455304262177516531831621713421149017932822181710542171621521750697912131881209910245530426217751653183162171342114901793,PTT,.SET,PPP,AAV,A,AKR,DTAC,CRYSTAL,ASK,CBG,KTB,KBS,KBANK,CCN,UKEM,RS,JAS,MEGA,BBL,SIMAT209910245530426217751653183162171342114901793282218171054217162152175069791213188120991024553042621775165318316217134211490179328221817105421716215217506979121318812099102455304262177516531831621713421149017932822,PTT,.SET,PPP,AAV,A,AKR,DTAC,CRYSTAL,ASK,CBG,KTB,KBS,KBANK,CCN,UKEM,RS,JAS,MEGA,BBL,SIMAT2099102455304262177516531831621713421149017932822181710542171621521750697912131881209910245530426217751653183162171342114901793282218171054217162152175069791213188120991024553042621775165318316217134211490179328221817,PTT,.SET,PPP,AAV,A,AKR,DTAC,CRYSTAL,ASK,CBG,KTB,KBS,KBANK,CCN,UKEM,RS,JAS,MEGA,BBL,SIMAT209910245530426217751653183162171342114901793282218171054217162152175069791213188120991024553042621775165318316217134211490179328221817105421716215217506979121318812099102455304262177516531831621713421149017932822181710542,PTT,.SET,PPP,AAV,A,AKR,DTAC,CRYSTAL,ASK,CBG,KT

				mWebSocketClient.send(jsoConnectSocket.toString());
			}

			@Override
			public void onMessage(String s) {
				final String message = s;
				if (getApplicationContext() != null) {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							// Log.v("onMessage", message); [1,6,1]

							// ไม่เอา array[0] เป้น 1 ให้เอา array[0] เป็น 3

							// ครั้งแรกส่ง id : 0 ไป
							// ถ้าข้อมูลส่งมา index[0] เป็น 3 แสดงว่า
							// มีข้อมูลอัพเดท
							// เช็คตาม orderbook_id
							//
							// พอส่งซ้ำ เช่น กดเข้าหน้าอีกครั้ง ให้ส่ง id : ไป
							// แล้วเช็ค index[0] เป็น 3 เหมือนเดิม

							// [3,2099,"PTT","264.00","4.00","1.54","264.00","262.00","40.63","10,688.85","92,900","24","214,800","56","263.38","262.98","263.00","14,900","263.00","20,000","263.00","263.00","98,600","0.00","0","0"]
							// [3,2493,"TRUE","7.80","0.15","1.96","7.90","7.60","202.77 M","1,575.04 M",""]

							// connect ครั้งแรก
							// {"id":0,"user_id":18885,"orderbook_list":"1024,1062,1063,1064,1025","date":"","hh":"","mm":"","requestType":"watchList","page":"Watchlist"}
							// requestType :
							// watchList = มี orderbook_list หลายตัว
							// symbolRealtime = มี orderbook_list ตัวเดียว

							try {
								jsaMassageSocket = new JSONArray(message);
								Log.v("jsaMassageSocket Symbol Detail", ""
										+ jsaMassageSocket);

								if (jsaMassageSocket.get(0).toString()
										.equals("3")) {
									changeSymbolRealTime();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					});
				}
			}

			@Override
			public void onClose(int i, String s, boolean b) {
				Log.v("Websocket", "Closed " + s);
			}

			@Override
			public void onError(Exception e) {
				Log.v("Websocket", "Error " + e.getMessage());
			}
		};

		mWebSocketClient.connect();

	}

	// ============== Set watchlist symbol ===============
	public void changeSymbolRealTime() {
		Log.v("changeSymbolRealTime",
				"changeSymbolRealTime changeSymbolRealTime");

		// [3,2099,"PTT","264.00","4.00","1.54","264.00","262.00","40.63","10,688.85","92,900","24","214,800","56","263.38","262.98","263.00","14,900","263.00","20,000","263.00","263.00","98,600","0.00","0","0"]

		try {
			// 0=id,
			// 1=orderbook_id,
			// 2=symbol_name,
			// 3=last_trade,
			// 4=change,
			// 5=percent_change,
			// 6=high,
			// 7=low,
			// 8=volume (M,K),
			// 9=value (M,K),
			// 10=buy_volume,
			// 11=buy_value,
			// 12=sell_volume,
			// 13=sell_value,
			// 14=avg_buy,
			// 15=avg_sell,
			// 16=max_buy_price
			// 17=max_buy_price_volume,
			// 18=max_sell_price
			// 19=max_sell_price_volume,
			// 20=open_price
			// 21=open1_price,
			// 22=open1_price_volume,
			// 23=open2_price,
			// 24=open2_price_volume,
			// 25=close_volume,
			// 26=side [S,B,””]
			// 27=orderQty,
			// 28=timeOfEvent,
			// 29=page

			String strOderbookId = "" + jsaMassageSocket.get(1);
			String strSplitOrderbookId[] = FragmentChangeActivity.strGetSymbolOrderBook_Id
					.split(",");

			String strLastTrade = "" + jsaMassageSocket.get(3);
			final String strChange = "" + jsaMassageSocket.get(4);
			String strPercentChange = "" + jsaMassageSocket.get(5);
			String strHigh = "" + jsaMassageSocket.get(6);
			String strLow = "" + jsaMassageSocket.get(7);
			String strVolume = "" + jsaMassageSocket.get(8);
			String strValue = "" + jsaMassageSocket.get(9);

			tv_last_trade.setText(strLastTrade);
			tv_change.setText(strChange);
			tv_percenchange.setText("(" + strPercentChange + "%)");

			if (strChange != "") {
				new CountDownTimer(600, 1) {
					public void onTick(long millisUntilFinished) {
						tv_last_trade.setTextColor(getResources().getColor(
								FunctionSetBg.arrColor[3]));
						tv_change.setTextColor(getResources().getColor(
								FunctionSetBg.arrColor[3]));
						tv_percenchange.setTextColor(getResources().getColor(
								FunctionSetBg.arrColor[3]));
					}

					public void onFinish() {
						tv_last_trade.setTextColor(getResources().getColor(
								FunctionSetBg.setColor(strChange)));
						tv_change.setTextColor(getResources().getColor(
								FunctionSetBg.setColor(strChange)));
						tv_percenchange.setTextColor(getResources().getColor(
								FunctionSetBg.setColor(strChange)));
					}
				}.start();
			}

			// if (strPrevClose != "") {
			// if (strHigh != "") {
			// if ((Float.parseFloat(strHigh.replaceAll(",", ""))) > Float
			// .parseFloat(strPrevClose.replaceAll(",", ""))) {
			// tv_high.setTextColor(getResources().getColor(
			// FunctionSetBg.arrColor[2]));
			// } else if ((Float.parseFloat(strHigh
			// .replaceAll(",", ""))) < Float
			// .parseFloat(strPrevClose.replaceAll(",", ""))) {
			// tv_high.setTextColor(getResources().getColor(
			// FunctionSetBg.arrColor[0]));
			// } else {
			// tv_high.setTextColor(getResources().getColor(
			// FunctionSetBg.arrColor[1]));
			// }
			// }
			// if (strLow != "") {
			// if ((Float.parseFloat(strLow.replaceAll(",", ""))) > Float
			// .parseFloat(strPrevClose.replaceAll(",", ""))) {
			// tv_low.setTextColor(getResources().getColor(
			// FunctionSetBg.arrColor[2]));
			// } else if ((Float
			// .parseFloat(strLow.replaceAll(",", ""))) < Float
			// .parseFloat(strPrevClose.replaceAll(",", ""))) {
			// tv_low.setTextColor(getResources().getColor(
			// FunctionSetBg.arrColor[0]));
			// } else {
			// tv_low.setTextColor(getResources().getColor(
			// FunctionSetBg.arrColor[1]));
			// }
			// }
			// }

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// ------------- init data Financial
	JSONArray graph_financial;
	JSONArray data_financial;

	private void setDataFinancial() {
		dialogLoading.dismiss();
		try {
			if (UiWatchlistDetail.contentGetFinancialData != null) {
				graph_financial = UiWatchlistDetail.contentGetFinancialData
						.getJSONArray("graph_financial");
				data_financial = UiWatchlistDetail.contentGetFinancialData
						.getJSONArray("data_financial");

				LinearLayout list_title = (LinearLayout) findViewById(R.id.list_title);
				LinearLayout list_data = (LinearLayout) findViewById(R.id.list_data);
				list_title.removeAllViews();
				list_data.removeAllViews();

				for (int i = 0; i < data_financial.length(); i++) {
					JSONArray jsaGraph = graph_financial.getJSONArray(i);
					JSONArray jsaData = data_financial.getJSONArray(i);

					// index 0 title
					if (i == 0) {
						for (int j = 0; j < jsaGraph.length(); j++) {
							View viewTitle = getLayoutInflater().inflate(
									R.layout.row_financial_data, null);
							TextView tv_row = (TextView) viewTitle
									.findViewById(R.id.tv_row);
							tv_row.setText(jsaGraph.get(j).toString());
							if (j == 0) {
								tv_row.setTextColor(getResources().getColor(
										FunctionSetBg.arrColor[4]));
							} else {
								tv_row.setTextColor(getResources().getColor(
										FunctionSetBg.arrColor[5]));
							}

							list_title.addView(viewTitle);
						}
						// set data เริ่มจาก index 1
					} else {
						JSONArray jsa_index_prev = graph_financial
								.getJSONArray(i - 1); // index 1 ตั้งไว้เทียบสี

						LayoutInflater inflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
						View viewData = inflater.inflate(R.layout.row_linear,
								null);

						LinearLayout li_row = (LinearLayout) viewData
								.findViewById(R.id.li_row);
						for (int j = 0; j < jsaData.length(); j++) {
							LayoutInflater inflater2 = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
							View viewDataRow = inflater2.inflate(
									R.layout.row_financial_data, null);

							TextView tv_row = (TextView) viewDataRow
									.findViewById(R.id.tv_row);
							tv_row.setText(jsaData.get(j).toString());
							if (j == 0) { // title
								tv_row.setTextColor(getResources().getColor(
										FunctionSetBg.arrColor[4]));
							} else { // data
								if (i == 1) {
									tv_row.setTextColor(getResources()
											.getColor(FunctionSetBg.arrColor[3]));
								} else {
									if (i > 1) {
										String str1 = jsaGraph.get(j)
												.toString(); // ตัวตั้ง
										String str2 = jsa_index_prev.get(j)
												.toString();
										if (j == 2) {
											tv_row.setTextColor(getResources()
													.getColor(
															FunctionSetBg
																	.setColorCompare2Attribute(
																			str2,
																			str1)));
										} else {
											tv_row.setTextColor(getResources()
													.getColor(
															FunctionSetBg
																	.setColorCompare2Attribute(
																			str1,
																			str2)));
										}
									}
								}
							}
							li_row.addView(viewDataRow);
						}
						list_data.addView(li_row);
					}
				}

				// ------ Stacked Bar Chart
				initSetDataBarChart();

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// ------------- init data
	TextView tv_turnaroundt, tv_growth, tv_dividend, tv_fundamentals,
			tv_strength, tv_stf_fun, tv_quarter_year;
	TextView tv_activity_1, tv_activity_2, tv_activity_3, tv_activity_4,
			tv_activity_5;
	TextView tv_profitability_1, tv_profitability_2, tv_profitability_3,
			tv_profitability_4, tv_profitability_5;
	TextView tv_leverage_1, tv_leverage_2, tv_leverage_3, tv_leverage_4,
			tv_leverage_5;
	TextView tv_liquidity_1, tv_liquidity_2, tv_liquidity_3, tv_liquidity_4,
			tv_liquidity_5;
	TextView tv_fundamental_trend, tv_rankingsector, tv_cgscore, tv_score;
	ImageView img_chart;
	ProgressWheel progress_score;

	private void setDataFundamental() {
		dialogLoading.dismiss();
		try {
			if (UiWatchlistDetail.contentGetFundamental != null) {
				// -------------
				tv_quarter_year = (TextView) findViewById(R.id.tv_quarter_year);
				tv_stf_fun = (TextView) findViewById(R.id.tv_stf_fun);
				tv_turnaroundt = (TextView) findViewById(R.id.tv_turnaroundt);
				tv_growth = (TextView) findViewById(R.id.tv_growth);
				tv_dividend = (TextView) findViewById(R.id.tv_dividend);
				tv_fundamentals = (TextView) findViewById(R.id.tv_fundamentals);
				tv_strength = (TextView) findViewById(R.id.tv_strength);
				// -------------
				tv_activity_1 = (TextView) findViewById(R.id.tv_activity_1);
				tv_activity_2 = (TextView) findViewById(R.id.tv_activity_2);
				tv_activity_3 = (TextView) findViewById(R.id.tv_activity_3);
				tv_activity_4 = (TextView) findViewById(R.id.tv_activity_4);
				tv_activity_5 = (TextView) findViewById(R.id.tv_activity_5);

				tv_profitability_1 = (TextView) findViewById(R.id.tv_profitability_1);
				tv_profitability_2 = (TextView) findViewById(R.id.tv_profitability_2);
				tv_profitability_3 = (TextView) findViewById(R.id.tv_profitability_3);
				tv_profitability_4 = (TextView) findViewById(R.id.tv_profitability_4);
				tv_profitability_5 = (TextView) findViewById(R.id.tv_profitability_5);

				tv_leverage_1 = (TextView) findViewById(R.id.tv_leverage_1);
				tv_leverage_2 = (TextView) findViewById(R.id.tv_leverage_2);
				tv_leverage_3 = (TextView) findViewById(R.id.tv_leverage_3);
				tv_leverage_4 = (TextView) findViewById(R.id.tv_leverage_4);
				tv_leverage_5 = (TextView) findViewById(R.id.tv_leverage_5);

				tv_liquidity_1 = (TextView) findViewById(R.id.tv_liquidity_1);
				tv_liquidity_2 = (TextView) findViewById(R.id.tv_liquidity_2);
				tv_liquidity_3 = (TextView) findViewById(R.id.tv_liquidity_3);
				tv_liquidity_4 = (TextView) findViewById(R.id.tv_liquidity_4);
				tv_liquidity_5 = (TextView) findViewById(R.id.tv_liquidity_5);
				// -------------
				tv_fundamental_trend = (TextView) findViewById(R.id.tv_fundamental_trend);
				tv_rankingsector = (TextView) findViewById(R.id.tv_rankingsector);
				tv_cgscore = (TextView) findViewById(R.id.tv_cgscore);
				img_chart = (ImageView) findViewById(R.id.img_chart);
				// -------------
				progress_score = (ProgressWheel) findViewById(R.id.progress_score);
				tv_score = (TextView) findViewById(R.id.tv_score);

				String strScore = UiWatchlistDetail.contentGetFundamental
						.getString("score");

				double dScore = Double.parseDouble(strScore);
				int percentCal20 = (int) ((100 * dScore) / 20);
				int percentCal360 = (int) ((360 * percentCal20) / 100);
				progress_score.incrementProgress(percentCal360); // เต็มวง 360
				tv_score.setText("" + dScore);

				// ----- tab title
				String strColorGrowth = UiWatchlistDetail.contentGetFundamental
						.getString("growth");
				String strColorFundamentals = UiWatchlistDetail.contentGetFundamental
						.getString("fundamentals");
				String strColorDividend = UiWatchlistDetail.contentGetFundamental
						.getString("dividend");
				String strColorTurnaround = UiWatchlistDetail.contentGetFundamental
						.getString("turnaround");
				String strColorStrength = UiWatchlistDetail.contentGetFundamental
						.getString("strength");

				if (strColorGrowth.equals("true")) {
					tv_growth.setTextColor(getResources().getColor(
							R.color.c_content));
					tv_growth.setBackgroundResource(R.color.c_success);
				}
				if (strColorFundamentals.equals("true")) {
					tv_fundamentals.setTextColor(getResources().getColor(
							R.color.c_content));
					tv_fundamentals.setBackgroundResource(R.color.c_success);
				}
				if (strColorDividend.equals("true")) {
					tv_dividend.setTextColor(getResources().getColor(
							R.color.c_content));
					tv_dividend.setBackgroundResource(R.color.c_success);
				}
				if (strColorTurnaround.equals("true")) {
					tv_turnaroundt.setTextColor(getResources().getColor(
							R.color.c_content));
					tv_turnaroundt.setBackgroundResource(R.color.c_success);
				}
				if (strColorStrength.equals("true")) {
					tv_strength.setTextColor(getResources().getColor(
							R.color.c_content));
					tv_strength.setBackgroundResource(R.color.c_success);
				}

				tv_quarter_year.setText("Data: "
						+ UiWatchlistDetail.contentGetFundamental
								.getString("quarter")
						+ "/"
						+ UiWatchlistDetail.contentGetFundamental
								.getString("year"));

				tv_stf_fun
						.setBackgroundColor(FunctionSetBg
								.setColorWatchListSymbolFundamental(UiWatchlistDetail.contentGetDetailFundamental));

				// if (UiWatchlistDetail.contentGetDetail != null) {
				// String strFundam = UiWatchlistDetail.contentGetDetail
				// .getString("fundamental");
				// tv_stf_fun.setBackgroundColor(BgColorSymbolDetail
				// .setColorWatchListSymbolFundamental(UiWatchlistDetail.contentGetDetailFundamental));
				// }

				// ----- box
				String strColorActivity = UiWatchlistDetail.contentGetFundamental
						.getString("activity");
				String strColorProfitability = UiWatchlistDetail.contentGetFundamental
						.getString("profitability");
				String strColorLeverage = UiWatchlistDetail.contentGetFundamental
						.getString("leverage");
				String strColorLiquidity = UiWatchlistDetail.contentGetFundamental
						.getString("liquidity");

				if (strColorActivity != "") {
					double dColor = Double.parseDouble(strColorActivity
							.replaceAll(",", ""));
					if (dColor < 1) {

					} else if (dColor < 2) {
						tv_activity_1.setBackgroundResource(R.color.c_success);
					} else if (dColor < 3) {
						tv_activity_1.setBackgroundResource(R.color.c_success);
						tv_activity_2.setBackgroundResource(R.color.c_success);
					} else if (dColor < 4) {
						tv_activity_1.setBackgroundResource(R.color.c_success);
						tv_activity_2.setBackgroundResource(R.color.c_success);
						tv_activity_3.setBackgroundResource(R.color.c_success);
					} else if (dColor < 5) {
						tv_activity_1.setBackgroundResource(R.color.c_success);
						tv_activity_2.setBackgroundResource(R.color.c_success);
						tv_activity_3.setBackgroundResource(R.color.c_success);
						tv_activity_4.setBackgroundResource(R.color.c_success);
					} else if (dColor < 6) {
						tv_activity_1.setBackgroundResource(R.color.c_success);
						tv_activity_2.setBackgroundResource(R.color.c_success);
						tv_activity_3.setBackgroundResource(R.color.c_success);
						tv_activity_4.setBackgroundResource(R.color.c_success);
						tv_activity_5.setBackgroundResource(R.color.c_success);
					} else {
					}
				}

				if (strColorProfitability != "") {
					double dColor = Double.parseDouble(strColorProfitability
							.replaceAll(",", ""));
					if (dColor < 1) {

					} else if (dColor < 2) {
						tv_profitability_1
								.setBackgroundResource(R.color.c_success);
					} else if (dColor < 3) {
						tv_profitability_1
								.setBackgroundResource(R.color.c_success);
						tv_profitability_2
								.setBackgroundResource(R.color.c_success);
					} else if (dColor < 4) {
						tv_profitability_1
								.setBackgroundResource(R.color.c_success);
						tv_profitability_2
								.setBackgroundResource(R.color.c_success);
						tv_profitability_3
								.setBackgroundResource(R.color.c_success);
					} else if (dColor < 5) {
						tv_profitability_1
								.setBackgroundResource(R.color.c_success);
						tv_profitability_2
								.setBackgroundResource(R.color.c_success);
						tv_profitability_3
								.setBackgroundResource(R.color.c_success);
						tv_profitability_4
								.setBackgroundResource(R.color.c_success);
					} else if (dColor < 6) {
						tv_profitability_1
								.setBackgroundResource(R.color.c_success);
						tv_profitability_2
								.setBackgroundResource(R.color.c_success);
						tv_profitability_3
								.setBackgroundResource(R.color.c_success);
						tv_profitability_4
								.setBackgroundResource(R.color.c_success);
						tv_profitability_5
								.setBackgroundResource(R.color.c_success);
					} else {
					}
				}

				if (strColorLeverage != "") {
					double dColor = Double.parseDouble(strColorLeverage
							.replaceAll(",", ""));
					if (dColor < 1) {

					} else if (dColor < 2) {
						tv_leverage_1.setBackgroundResource(R.color.c_success);
					} else if (dColor < 3) {
						tv_leverage_1.setBackgroundResource(R.color.c_success);
						tv_leverage_2.setBackgroundResource(R.color.c_success);
					} else if (dColor < 4) {
						tv_leverage_1.setBackgroundResource(R.color.c_success);
						tv_leverage_2.setBackgroundResource(R.color.c_success);
						tv_leverage_3.setBackgroundResource(R.color.c_success);
					} else if (dColor < 5) {
						tv_leverage_1.setBackgroundResource(R.color.c_success);
						tv_leverage_2.setBackgroundResource(R.color.c_success);
						tv_leverage_3.setBackgroundResource(R.color.c_success);
						tv_leverage_4.setBackgroundResource(R.color.c_success);
					} else if (dColor < 6) {
						tv_leverage_1.setBackgroundResource(R.color.c_success);
						tv_leverage_2.setBackgroundResource(R.color.c_success);
						tv_leverage_3.setBackgroundResource(R.color.c_success);
						tv_leverage_4.setBackgroundResource(R.color.c_success);
						tv_leverage_5.setBackgroundResource(R.color.c_success);
					} else {
					}
				}

				if (strColorLiquidity != "") {
					double dColor = Double.parseDouble(strColorLiquidity
							.replaceAll(",", ""));
					if (dColor < 1) {

					} else if (dColor < 2) {
						tv_liquidity_1.setBackgroundResource(R.color.c_success);
					} else if (dColor < 3) {
						tv_liquidity_1.setBackgroundResource(R.color.c_success);
						tv_liquidity_2.setBackgroundResource(R.color.c_success);
					} else if (dColor < 4) {
						tv_liquidity_1.setBackgroundResource(R.color.c_success);
						tv_liquidity_2.setBackgroundResource(R.color.c_success);
						tv_liquidity_3.setBackgroundResource(R.color.c_success);
					} else if (dColor < 5) {
						tv_liquidity_1.setBackgroundResource(R.color.c_success);
						tv_liquidity_2.setBackgroundResource(R.color.c_success);
						tv_liquidity_3.setBackgroundResource(R.color.c_success);
						tv_liquidity_4.setBackgroundResource(R.color.c_success);
					} else if (dColor < 6) {
						tv_liquidity_1.setBackgroundResource(R.color.c_success);
						tv_liquidity_2.setBackgroundResource(R.color.c_success);
						tv_liquidity_3.setBackgroundResource(R.color.c_success);
						tv_liquidity_4.setBackgroundResource(R.color.c_success);
						tv_liquidity_5.setBackgroundResource(R.color.c_success);
					} else {
					}
				}

				// ----- bottom
				String strFundamentalTrend = UiWatchlistDetail.contentGetFundamental
						.getString("fundamental_trend");
				tv_fundamental_trend.setText(strFundamentalTrend);
				if (strFundamentalTrend.equals("up")) {
					tv_fundamental_trend.setTextColor(getResources().getColor(
							R.color.c_success));
				} else if (strFundamentalTrend.equals("down")) {
					tv_fundamental_trend.setTextColor(getResources().getColor(
							R.color.c_danger));
				} else {
					tv_fundamental_trend.setTextColor(getResources().getColor(
							R.color.c_warning));
				}
				tv_rankingsector
						.setText(UiWatchlistDetail.contentGetFundamental
								.getString("rangkingsector"));
				tv_cgscore.setText(UiWatchlistDetail.contentGetFundamental
						.getString("cgscore"));

				FragmentChangeActivity.imageLoader.displayImage(
						SplashScreen.url_bidschart_chart
								+ "fundamental-"
								+ UiWatchlistDetail.contentGetFundamental
										.getString("symbol") + ".png",
						img_chart);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// ------------- init DataBarChart
	// private BarChart mChartBar;
	private CombinedChart mChartCombine;

	private void initSetDataBarChart() throws JSONException {
		// mChartBar = (BarChart) findViewById(R.id.chart_bar);
		mChartCombine = (CombinedChart) findViewById(R.id.chart_combine);

		// mChartBar.removeAllViews();
		mChartCombine.removeAllViews();

		// -----------------------------
		mChartCombine.setDescription("");
		// mChartCombine.setBackgroundColor(Color.WHITE);
		mChartCombine.setDrawGridBackground(false);
		mChartCombine.setDrawBarShadow(false);

		// draw bars behind lines
		mChartCombine.setDrawOrder(new DrawOrder[] { DrawOrder.BAR,
				DrawOrder.BUBBLE, DrawOrder.CANDLE, DrawOrder.LINE,
				DrawOrder.SCATTER });

		float MaxValueR, MinValueR;
		ArrayList<Float> arrMaxMinR = new ArrayList<Float>();
		ArrayList<String> xVals = new ArrayList<String>();
		for (int i = 1; i < graph_financial.length(); i++) {
			JSONArray jsaGraph = graph_financial.getJSONArray(i);
			xVals.add(jsaGraph.get(0).toString());
			arrMaxMinR.add(Float.parseFloat(jsaGraph.get(6).toString()
					.replaceAll(",", "")));
		}
		MaxValueR = FloatMath.ceil(Collections.max(arrMaxMinR));
		MinValueR = FloatMath.floor(Collections.min(arrMaxMinR));

		// --- ขวา
		YAxis rightAxis = mChartCombine.getAxisRight();
		rightAxis.setDrawGridLines(false);
		rightAxis.setTextColor(getResources().getColor(R.color.c_content));
		rightAxis.setStartAtZero(false);
		rightAxis.setAxisMaxValue(MaxValueR);
		rightAxis.setAxisMinValue(MinValueR);

		// --- ซ้าย
		YAxis leftAxis = mChartCombine.getAxisLeft();
		leftAxis.setDrawGridLines(false);
		leftAxis.setTextColor(getResources().getColor(R.color.c_content));
		leftAxis.setStartAtZero(false);

		// --- บน, ล่าง
		XAxis xAxis = mChartCombine.getXAxis();
		xAxis.setPosition(XAxisPosition.BOTTOM);
		xAxis.setTextColor(getResources().getColor(R.color.c_content));

		CombinedData dataCombine = new CombinedData(xVals);
		dataCombine.setData(generateLineData());
		dataCombine.setData(generateBarData());
		// data.setData(generateBubbleData());
		// data.setData(generateScatterData());
		// data.setData(generateCandleData());

		mChartCombine.setData(dataCombine);
		mChartCombine.invalidate();

	}

	private LineData generateLineData() throws JSONException {

		LineData d = new LineData();

		ArrayList<Entry> entries = new ArrayList<Entry>();
		for (int i = 1; i < graph_financial.length(); i++) {
			JSONArray jsaGraph = graph_financial.getJSONArray(i);
			float fVals = Float.parseFloat(jsaGraph.get(6).toString());
			entries.add(new Entry(fVals, (i - 1)));

		}

		LineDataSet set = new LineDataSet(entries, "Line DataSet");
		set.setColor(getResources().getColor(R.color.c_warning));
		set.setLineWidth(2f);
		set.setCircleColor(getResources().getColor(R.color.c_warning));
		set.setCircleSize(2.5f);
		set.setFillColor(getResources().getColor(R.color.c_warning));
		set.setDrawCircleHole(false);
		set.setDrawValues(true);
		set.setAxisDependency(YAxis.AxisDependency.RIGHT);
		set.setValueTextColor(Color.WHITE);

		d.setValueTextColor(android.graphics.Color.TRANSPARENT);
		d.addDataSet(set);

		return d;
	}

	private BarData generateBarData() {
		BarData d = new BarData();
		try {
			ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
			BarDataSet set = new BarDataSet(entries, "");
			ArrayList<Integer> mpChartArrColor = new ArrayList<Integer>();
			mpChartArrColor.add(Color.rgb(99, 87, 103));
			mpChartArrColor.add(Color.rgb(29, 186, 157));
			mpChartArrColor.add(Color.rgb(80, 175, 205));
			mpChartArrColor.add(Color.rgb(43, 129, 202));
			for (int i = 1; i < graph_financial.length(); i++) {
				JSONArray jsaGraph = graph_financial.getJSONArray(i);
				float val1 = Float.parseFloat(jsaGraph.get(1).toString());
				float val2 = Float.parseFloat(jsaGraph.get(2).toString());
				float val3 = Float.parseFloat(jsaGraph.get(3).toString());
				float val4 = Float.parseFloat(jsaGraph.get(4).toString());
				float[] val = { val1, val2, val3, val4 };

				entries.add(new BarEntry(val, (i - 1)));

				set = new BarDataSet(entries, "");
				set.setColors(mpChartArrColor);
				set.setValueTextColor(Color.TRANSPARENT);

				// d.addDataSet(set);
				// set.setAxisDependency(YAxis.AxisDependency.LEFT);
			}
			d.setValueTextColor(android.graphics.Color.TRANSPARENT);
			d.addDataSet(set);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return d;
	}

//	protected void switchFragment(PagerWatchList fragment) {
//		if (getApplicationContext() == null)
//			return;
//		if (getApplicationContext() instanceof FragmentChangeActivity) {
//			FragmentChangeActivity fca = (FragmentChangeActivity) getApplicationContext();
//			fca.switchContent(fragment);
//		}
//	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		finish();
		return super.onKeyDown(keyCode, event);
	}

}
