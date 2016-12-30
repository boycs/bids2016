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

public class UiWatchlistDetailNews extends FragmentActivity {

	public static Activity act;

	public static WebSocketClient mWebSocketClient;

	public static Dialog dialogLoading;
	
	public static String linkNewsSelect = "";
	public static String strArticleIdSelect;

	// --------- google analytics
	// private Tracker mTracker;
	// String nameTracker = new String("Detail");

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.ui_watchlist_detail_news);

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

		dialogLoading = new Dialog(UiWatchlistDetailNews.this);
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
		if (UiWatchlistDetail.newsContentGetWatchlistNewsBySymbol != null) {
			initTabNews();
		} else {
			loadDataDetailNews();
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
								+ "'s News");
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

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// ============== load data news =========================
		private void loadDataDetailNews() {
			loadDataNews resp = new loadDataNews();
			resp.execute();
		}

		public class loadDataNews extends AsyncTask<Void, Void, Void> {

			boolean connectionError = false;
			// ======= Ui Newds ========
			private JSONObject jsonGetWatchlistNewsBySymbol;
			private JSONObject jsonGetWatchlistNewsBySymbolSet;

			@Override
			protected void onPreExecute() {
				super.onPreExecute();
				// progress.show();

			}

			@Override
			protected Void doInBackground(Void... params) {
				java.util.Date date = new java.util.Date();
				long timestamp = date.getTime();
				// ======= Ui News ========
				String url_GetWatchlistNewsBySymbol = SplashScreen.url_bidschart
						+ "/service/v2/watchlistNewsBySymbol?symbol="
						+ FragmentChangeActivity.strSymbolSelect;

				// http://chart.bidschart.com/api/feed?provider=set&symbol=PTT
				String url_GetWatchlistNewsBySymbolSet = "http://chart.bidschart.com/api/feed?provider=set&symbol="
						+ FragmentChangeActivity.strSymbolSelect;
				try {
					// ======= Ui News ========
					jsonGetWatchlistNewsBySymbol = ReadJson
							.readJsonObjectFromUrl(url_GetWatchlistNewsBySymbol);

					jsonGetWatchlistNewsBySymbolSet = ReadJson
							.readJsonObjectFromUrl(url_GetWatchlistNewsBySymbolSet);
				} catch (IOException e1) {
					connectionError = true;
					jsonGetWatchlistNewsBySymbol = null;
					e1.printStackTrace();
				} catch (JSONException e1) {
					connectionError = true;
					jsonGetWatchlistNewsBySymbol = null;
					e1.printStackTrace();
				} catch (RuntimeException e) {
					connectionError = true;
					jsonGetWatchlistNewsBySymbol = null;
					e.printStackTrace();
				}
				return null;
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);

				if (connectionError == false) {
					if (jsonGetWatchlistNewsBySymbol != null) {
						try {
							UiWatchlistDetail.newsContentGetWatchlistNewsBySymbol = jsonGetWatchlistNewsBySymbol
									.getJSONArray("data");

							UiWatchlistDetail.contentGetWatchlistNewsBySymbolSet = jsonGetWatchlistNewsBySymbolSet
									.getJSONArray("data");

							initTabNews();
						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else {
						Log.v("json null", "jsonGetArticleTitleByType null");
					}

				} else {
					Log.v("json newslist null", "jsonGetArticleTitleByType null");
				}
			}
		}

		// ====================== set tab new ================
		public static TextView tv_tab_set, tv_tab_social, tv_tab_bids;
		public static int itemTabSelect = 1;

		private void initTabNews() {
			tv_tab_set = (TextView) findViewById(R.id.tv_tab_set);
			tv_tab_social = (TextView) findViewById(R.id.tv_tab_social);
			tv_tab_bids = (TextView) findViewById(R.id.tv_tab_bids);

			itemTabSelect = 1;
			initSetDataNews(); // set data

			tv_tab_set.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					itemTabSelect = 1;
					selectTabNews(1);
					initSetDataNews(); // set data
				}
			});
			tv_tab_social.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					itemTabSelect = 2;
					selectTabNews(2);
					initSetDataNews(); // set data
				}
			});
			tv_tab_bids.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					itemTabSelect = 3;
					selectTabNews(3);
					initSetDataNews(); // set data
				}
			});
		}

		// ============= set tab ===========
		public int selectTabNews(int numtab) {
			tv_tab_set.setBackgroundColor(Color.TRANSPARENT);
			tv_tab_social.setBackgroundColor(Color.TRANSPARENT);
			tv_tab_bids.setBackgroundColor(Color.TRANSPARENT);

			tv_tab_set.setTextColor(getResources().getColor(R.color.c_content));
			tv_tab_social.setTextColor(getResources().getColor(R.color.c_content));
			tv_tab_bids.setTextColor(getResources().getColor(R.color.c_content));

			if (numtab == 1) {
				tv_tab_set
						.setBackgroundResource(R.drawable.border_button_activeleft);
				tv_tab_set
						.setTextColor(getResources().getColor(R.color.bg_default));
			} else if (numtab == 2) {
				tv_tab_social
						.setBackgroundResource(R.drawable.border_button_activecenter);
				tv_tab_social.setTextColor(getResources().getColor(
						R.color.bg_default));
			} else if (numtab == 3) {
				tv_tab_bids
						.setBackgroundResource(R.drawable.border_button_activecenter);
				tv_tab_bids.setTextColor(getResources()
						.getColor(R.color.bg_default));
			}
			return numtab;
		}

		// ============== set news ===============
		private void initSetDataNews() {

			LinearLayout li_list = (LinearLayout) findViewById(R.id.li_list);
			li_list.removeAllViews();
			try {
				if (itemTabSelect != 1) {
					if (UiWatchlistDetail.newsContentGetWatchlistNewsBySymbol.length() > 0) {
						for (int i = 0; i < UiWatchlistDetail.newsContentGetWatchlistNewsBySymbol
								.length(); i++) {

							JSONObject jsoIndex = UiWatchlistDetail.newsContentGetWatchlistNewsBySymbol
									.getJSONObject(i);
							final String strProvider = jsoIndex.getString("provider");

							View view2 = getLayoutInflater()
									.inflate(R.layout.row_watchlist_news_bidschart,
											null);

							if (itemTabSelect == 1) {

							} else if (itemTabSelect == 2) {
								if (strProvider.equals("facebook")) {
									view2 = getLayoutInflater()
											.inflate(
													R.layout.row_watchlist_news_facebook,
													null);
								} else if (strProvider.equals("pantip")) {
									view2 = getLayoutInflater()
											.inflate(
													R.layout.row_watchlist_news_pantip,
													null);
								}

								final LinearLayout li_row = (LinearLayout) view2
										.findViewById(R.id.li_row);

								ImageView iv_content = (ImageView) view2
										.findViewById(R.id.iv_content);
								TextView tv_article_title = (TextView) view2
										.findViewById(R.id.tv_article_title);
								TextView tv_comments_count = (TextView) view2
										.findViewById(R.id.tv_comments_count);
								TextView tv_created_at = (TextView) view2
										.findViewById(R.id.tv_created_at);
								
								final int j = i;
								if (strProvider.equals("facebook")) {
									tv_article_title.setText(jsoIndex
											.getString("title"));
									tv_comments_count.setText(jsoIndex
											.getString("comment_count"));
									tv_created_at.setText(DateTimeCreate
											.DateDmyThaiCreate(jsoIndex
													.getString("created_at")));
								} else if (strProvider.equals("pantip")) {
									tv_article_title.setText(jsoIndex
											.getString("article_title"));
									tv_comments_count.setText(jsoIndex
											.getString("comments_count"));
									tv_created_at.setText(DateTimeCreate
											.DateDmyThaiCreate(jsoIndex
													.getString("created_at")));
								}

								

								li_row.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										try {
											strArticleIdSelect = UiWatchlistDetail.newsContentGetWatchlistNewsBySymbol
													.getJSONObject(j).getString(
															"id");

//											contentSocialSelect = UiWatchlistDetail.contentGetWatchlistNewsBySymbol
//													.getJSONObject(j);
											linkNewsSelect = UiWatchlistDetail.newsContentGetWatchlistNewsBySymbol
													.getJSONObject(j).getString("link");
										} catch (JSONException e) {
											e.printStackTrace();
										}
										startActivity(new Intent(getApplicationContext(),
												UiWatchListDetailNewsSocialSelect.class));
									}
								});

								if (jsoIndex.getString("provider").equals(
										"facebook")) {
									String strFacebook_id = jsoIndex.getJSONObject(
											"rss").getString("facebook_id");
									Log.v("strFacebook_id", "" + strFacebook_id);

									if ((jsoIndex.get("content")).equals("")) {
										Log.v("img content", "undefined undefined");
									} else {
										FragmentChangeActivity.imageLoader
												.displayImage(
														"https://graph.facebook.com/"
																+ strFacebook_id
																+ "/picture?type=normal",
														iv_content);
									}

								} else {
									iv_content
											.setBackgroundResource(R.drawable.img_pantip);
								}
								li_list.addView(view2);
							} else if (itemTabSelect == 3) {
								if (jsoIndex.getString("provider").equals("web")) {
									view2 = getLayoutInflater()
											.inflate(
													R.layout.row_watchlist_news_bidschart,
													null);

									final LinearLayout li_row = (LinearLayout) view2
											.findViewById(R.id.li_row);

									ImageView iv_content = (ImageView) view2
											.findViewById(R.id.iv_content);

									TextView tv_article_title = (TextView) view2
											.findViewById(R.id.tv_article_title);
									TextView tv_views_count = (TextView) view2
											.findViewById(R.id.tv_views_count);
									TextView tv_likes_count = (TextView) view2
											.findViewById(R.id.tv_likes_count);
									TextView tv_comments_count = (TextView) view2
											.findViewById(R.id.tv_comments_count);
									TextView tv_created_at = (TextView) view2
											.findViewById(R.id.tv_created_at);

									if ((jsoIndex.get("content")).equals("")) {
										Log.v("img content", "undefined undefined");
									} else {
										FragmentChangeActivity.imageLoader
												.displayImage(
														"http://service.bidschart.com/"
																+ jsoIndex
																		.getString("content"),
														iv_content,
														FragmentChangeActivity.options);
									}

									tv_article_title.setText(jsoIndex
											.getString("article_title"));
									tv_views_count.setText(jsoIndex
											.getString("views_count"));
									tv_likes_count.setText(jsoIndex
											.getString("likes_count"));
									tv_comments_count.setText(jsoIndex
											.getString("comments_count"));
									tv_created_at.setText(DateTimeCreate
											.DateDmyThaiCreate(jsoIndex.getString(
													"created_at").toString()));

									final int j = i;
									li_row.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											try {
												strArticleIdSelect = UiWatchlistDetail.newsContentGetWatchlistNewsBySymbol
														.getJSONObject(j).get("id")
														.toString();
												// FragmentChangeActivity.contentGetArticleSelect
												// =
												// contentGetWatchlistNewsBySymbol.getJSONObject(j);
											} catch (JSONException e) {
												e.printStackTrace();
											}

											startActivity(new Intent(
													getApplicationContext(),
													UiWatchListDetailNewsSelect.class));
										}
									});
									li_list.addView(view2);
								}
							}

						}
					}
				} else {
					if (UiWatchlistDetail.contentGetWatchlistNewsBySymbolSet.length() > 0) {
						for (int i = 0; i < UiWatchlistDetail.contentGetWatchlistNewsBySymbolSet
								.length(); i++) {

							JSONObject jsoIndex = UiWatchlistDetail.contentGetWatchlistNewsBySymbolSet
									.getJSONObject(i);

							View view2 = getLayoutInflater()
									.inflate(R.layout.row_watchlist_news_set,
											null);

//							if (jsoIndex.getString("provider").equals("facebook")) {
//								view2 = ((Activity) context)
//										.getLayoutInflater()
//										.inflate(
//												R.layout.row_watchlist_news_facebook,
//												null);
//							} else if (jsoIndex.getString("provider").equals(
//									"pantip")) {
//								view2 = ((Activity) context).getLayoutInflater()
//										.inflate(
//												R.layout.row_watchlist_news_pantip,
//												null);
//							}
							
							final LinearLayout li_row = (LinearLayout) view2
									.findViewById(R.id.li_row);

							ImageView iv_content = (ImageView) view2
									.findViewById(R.id.iv_content);
							TextView tv_article_title = (TextView) view2
									.findViewById(R.id.tv_article_title);
							TextView tv_created_at = (TextView) view2
									.findViewById(R.id.tv_created_at);

							tv_article_title.setText(jsoIndex.getString("title"));
							tv_created_at.setText(DateTimeCreate
									.DateDmyWatchlistPortfolio(jsoIndex
											.getString("local_date")));
							
							String url_link = "";
							if (jsoIndex.getString("owner").equals(
									"www.settrade.com")) {
								url_link = jsoIndex.getString("link");
								iv_content
								.setBackgroundResource(R.drawable.img_settrade);
							} else {
								url_link = "www.set.or.th"+jsoIndex.getString("link");
								iv_content
										.setBackgroundResource(R.drawable.img_set);
							}

							final String url_link2 = url_link;
							li_row.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									linkNewsSelect = url_link2;
									startActivity(new Intent(getApplicationContext(),
											UiWatchListDetailNewsSocialSelect.class));
								}
							});
							li_list.addView(view2);
						}
					}
				}
			} catch (JSONException e1) {
				e1.printStackTrace();
			}
		}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		finish();
		return super.onKeyDown(keyCode, event);
	}

}
