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
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class UiWatchlistDetail extends FragmentActivity {

	public static Activity act;

	public static WebSocketClient mWebSocketClient;

	// view pager
	public static ViewPager mPagerMainPreMium;
	// list contains fragments to instantiate in the viewpager
	public static List<Fragment> fragmentMain = new Vector<Fragment>();
	private PagerAdapter mPagerAdapterMain;

	public static List<Fragment> fragmentMainPreMium = new Vector<Fragment>();
	public static PagerAdapter mPagerAdapterMainPreMium;

	public static ImageView img_follow;

	public static Dialog dialogLoading;

	public static PagerWatchList pagerWatchList;

	// --------- google analytics
	// private Tracker mTracker;
	// String nameTracker = new String("Detail");

	// ================= data ==================
	public static JSONObject contentGetDetail = null;
	public static String contentGetDetailFundamental = "";
	public static JSONObject contentGetDetailUpdate = null;
	public static LinearLayout li_vertical;
	public static RelativeLayout rl_chartiq;

	// ========== pager detail =======
	public static JSONObject contentGetFundamental = null;
	public static JSONObject contentGetFinancialData = null;
	public static String strIndustry = "";
	public static String strIndustrySector = "ALL";

	public static JSONObject contentGetRetrieveSetAlert = null;
	public static JSONArray contentGetWatchlistNewsBySymbolSet = null;

	// ---------- industry ------
	public static List<String> industryListSector;
	public static JSONArray industryContentGetWatchlistNewsBySymbol = null;
	public static boolean spn_industry_select = false;
	// ---------- news -------
	public static JSONArray newsContentGetWatchlistNewsBySymbol = null;

	// ========== pager webview =======
	public static boolean ckRemoveViewChart = false;
	public static boolean webviewMenuShowHide = false;
	public static boolean ckIndustrySetDatax = false; // ถ้าโหลด pagerIndustry
														// เสร็จถึงจะ back ได้

	// show hide
	public static Animation animShow, animHide;
	public static SlidingPanel popup_window_detail;
	public static LinearLayout li_menu_top, li_data, li_data_symbol, li_detail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.ui_watchlist_detail);

		pagerWatchList = new PagerWatchList();

		// // --------- google analytics
		// Tracker t = ((GoogleAnalyticsApp) getApplicationContext())
		// .getTracker(TrackerName.APP_TRACKER);
		// t.setScreenName(nameTracker);
		// t.send(new HitBuilders.AppViewBuilder().build());

		// --------- google analytics
		// GoogleAnalyticsApp application = (GoogleAnalyticsApp)
		// getApplicationContext();
		// mTracker = application.getDefaultTracker();

		// set ค่า static object เริ่มต้น ให้เป็น null
		setObjectBeginNull();
	}

	// ------- set ค่า static object เริ่มต้น ให้เป็น null
	private void setObjectBeginNull() {
		// ================= data ==================
		contentGetDetail = null;
		contentGetDetailFundamental = "";
		contentGetDetailUpdate = null;
		// ========== pager detail =======
		contentGetFundamental = null;
		contentGetFinancialData = null;
		strIndustry = "";
		strIndustrySector = "ALL";

		contentGetRetrieveSetAlert = null;

		newsContentGetWatchlistNewsBySymbol = null;

		industryListSector = null;
		industryContentGetWatchlistNewsBySymbol = null;
		spn_industry_select = false;
		
		// set view
		initView();
	}

	// @Override
	// protected void onStart() {
	// super.onStart();
	// Log.v("FragmentChangeActivity", "onStart onStart onStart");
	// GoogleAnalytics.getInstance(UiWatchlistDetail.this)
	// .reportActivityStart(this);
	// }
	//
	// @Override
	// protected void onStop() {
	// super.onStop();
	// Log.v("FragmentChangeActivity", "onStop onStop onStop");
	// GoogleAnalytics.getInstance(UiWatchlistDetail.this).reportActivityStop(
	// this);
	// }

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
		((TextView) findViewById(R.id.tv_title_symbol))
				.setText(FragmentChangeActivity.strSymbolSelect + "'s Detail");

		((LinearLayout) findViewById(R.id.li_back))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.wv_chartiq
								.loadUrl("javascript:(function () { "
										+ "mobileControl.closeConnection();"
										+ "})()");
						finish();
					}
				});

		// -------- stop timer ----
		if (FragmentChangeActivity.timerUpdateSymbolStatus) {
			FragmentChangeActivity.timerUpdateSymbolStatus = false;
			FragmentChangeActivity.timerUpdateSymbol.cancel();
		}

		img_follow = (ImageView) findViewById(R.id.img_follow);

		rl_chartiq = (RelativeLayout) findViewById(R.id.rl_chartiq);
		li_vertical = (LinearLayout) findViewById(R.id.li_vertical);

		dialogLoading = new Dialog(UiWatchlistDetail.this);
		dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogLoading.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialogLoading.setContentView(R.layout.progress_bar);
		dialogLoading.setCancelable(false);
		dialogLoading.setCanceledOnTouchOutside(false);

		li_detail = (LinearLayout) findViewById(R.id.li_detail);
		li_menu_top = (LinearLayout) findViewById(R.id.li_menu_top);
		li_data = (LinearLayout) findViewById(R.id.li_data);

		li_menu_top.setVisibility(View.VISIBLE);
		li_data.setVisibility(View.VISIBLE);

		if (FragmentChangeActivity.contentGetTxtSlidingMarquee != null) {
			initTxtSliding();
		} else {
			loadTxtSlidingMarquee(); // text sliding
		}

		loadDataDetail();
		// setFollowSymbol();

	}

	// ***************** text sliding ******************
	private void initTxtSliding() {
		String strSliding = "";

		com.app.custom.CustomTextViewSliding marque = (com.app.custom.CustomTextViewSliding) findViewById(R.id.tv_sliding_marquee);
		ImageView img_status_m = (ImageView) findViewById(R.id.img_status_m);

		try {
			if (FragmentChangeActivity.contentGetTxtSlidingMarquee != null) {

				// status market
				if ((FragmentChangeActivity.jsonTxtSlidingMarquee
						.getString("market")).equals("close")) {
					img_status_m
							.setBackgroundResource(R.drawable.icon_status_m_red);
				} else {
					img_status_m
							.setBackgroundResource(R.drawable.icon_status_m_green);
				}

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
						txtLtrade = FunctionSymbol.setFormatNumber(txtLtrade);
					}

					if (txtChange != "") {
						txtChange = FunctionSymbol.setFormatNumber(txtChange);
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

	// ============== Load Data all =============
	// public static ImageView img_follow;

	public void setFollowSymbol() {
		img_follow.setBackgroundResource(FunctionSymbol
				.setFavoriteNumber(FragmentChangeActivity.strSymbolSelect));

		// follow
		img_follow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// เช็ค follow หรือยัง
				boolean ckFollow = FunctionSymbol
						.checkFollowSymbol(FragmentChangeActivity.strSymbolSelect);

				// นับ follow T เพิ่มได้ ถ้าเกิน 21 เป็น F เพิ่มไม่ได้
				boolean ckFollowCountLimit = FunctionSymbol
						.checkFollowCount(FragmentChangeActivity.strSymbolSelect);

				if (!(SplashScreen.userModel.user_id != "")) {
					Toast.makeText(getApplicationContext(), "Login", 0).show();
					LoginDialog.show();
				} else {
					if (ckFollow) {
						getDataFavoriteId(); // unfollow

						// try {
						// if (FragmentChangeActivity.contentGetSymbolFavorite
						// .length() >= 1) {
						// for (int i = 0; i <
						// FragmentChangeActivity.contentGetSymbolFavorite
						// .length(); i++) {
						// JSONObject jsoIndex =
						// FragmentChangeActivity.contentGetSymbolFavorite
						// .getJSONObject(i);
						//
						// String strFav = jsoIndex
						// .getString("favorite_number");
						//
						// if (FragmentChangeActivity.strFavoriteNumber
						// .equals(strFav)) {
						// if ((jsoIndex.getJSONArray("dataAll")) != null) {
						// FragmentChangeActivity.strGetListSymbol = "";
						// JSONArray jsaFavSymbol = jsoIndex
						// .getJSONArray("dataAll");
						//
						// for (int j = 0; j < jsaFavSymbol
						// .length(); j++) {
						// if (jsaFavSymbol
						// .getJSONObject(j)
						// .getString(
						// "symbol_name")
						// .equals(FragmentChangeActivity.strSymbolSelect)) {
						// strRemoveId = jsaFavSymbol
						// .getJSONObject(j)
						// .getString("id");
						// sendRemoveFavorite();
						// break;
						// }
						// }
						// }
						// break;
						// }
						// }
						// }
						//
						// } catch (JSONException e) {
						// // TODO Auto-generated catch block
						// e.printStackTrace();
						// }
					} else {
						if (ckFollowCountLimit) {
							dialogFavorite();
						} else {
							Toast.makeText(getApplicationContext(),
									"Over limit favorites.", 0).show();
						}
					}
				}
			}
		});
	}

	// ============== Load Data all =============
	public void loadDataDetail() {
		loadData resp = new loadData();
		resp.execute();
	}

	public class loadData extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;
		// ======= json ========
		private JSONObject jsonGetDetail;

		// private JSONObject jsonGetDetailFollow;

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

			// http://www.bidschart.com/service/v2/watchlistSymbol?user_id=104&symbol=AAV
			String url_GetDetail = SplashScreen.url_bidschart
					+ "/service/v2/watchlistSymbol?user_id="
					+ SplashScreen.userModel.user_id + "&symbol="
					+ FragmentChangeActivity.strSymbolSelect + "&timestamp="
					+ timestamp;
			
			Log.v("url_GetDetail",""+url_GetDetail);

			try {
				// ======= Ui Home ========
				jsonGetDetail = ReadJson.readJsonObjectFromUrl(url_GetDetail);
				// jsonGetDetailFollow = ReadJson
				// .readJsonObjectFromUrl(url_GetDetailFollow);
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
							JSONArray jsa = jsonGetDetail
									.getJSONArray("dataAll");
							if (jsa != null) {
								contentGetDetail = jsa.getJSONObject(0);
								contentGetDetailFundamental = contentGetDetail
										.getString("fundamental");
								setDataDetail();
							} else {
								dialogLoading.dismiss();
							}
						}

						// if (jsonGetDetail.getJSONArray("dataAll") != null) {
						// contentGetDetail = jsonGetDetail.getJSONArray(
						// "dataAll").getJSONObject(0);
						//
						// contentGetDetailFundamental = contentGetDetail
						// .getString("fundamental");
						// setDataDetail();
						// }

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.v("json newslist null", "newslist null");
					dialogLoading.dismiss();
				}
			} else {
				dialogLoading.dismiss();
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

	// ======= data show hide
	public void showDataDetail() {
		ckHideShowData = false;
		li_data.setVisibility(View.VISIBLE);

		// int li_height_detail = li_detail.getHeight();
		int li_height_menu_premium = li_menu_premium.getHeight();
		int li_height_menu_top = li_menu_top.getHeight();
		int li_height_data = li_data.getHeight();
		int height_mPagerMainPreMium = FragmentChangeActivity.heightScreen
				- (li_height_menu_premium + li_height_data + li_height_menu_top);

		Log.v("li_height show", li_height_data + "_" + height_mPagerMainPreMium); //

		// LinearLayout.LayoutParams layoutParams = new
		// LinearLayout.LayoutParams(
		// FragmentChangeActivity.widthScreen, height_mPagerMainPreMium);
		// mPagerMainPreMium.setLayoutParams(layoutParams);

		setDataDetailFuncWidth(FragmentChangeActivity.widthScreen,
				height_mPagerMainPreMium);
	}

	public void setDataDetailFuncWidth(int width, int height) {
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				width, height);
		mPagerMainPreMium.setLayoutParams(layoutParams);

		Log.v("mPagerMainPreMium", width + "_" + height + "_"
				+ mPagerMainPreMium.getHeight()); //
	}

	public void hideDataDetail() {
		ckHideShowData = true;
		li_data.setVisibility(View.GONE);

		// int li_height_detail = li_detail.getHeight();
		int li_height_menu_premium = li_menu_premium.getHeight();
		int li_height_menu_top = li_menu_top.getHeight();
		int li_height_data = li_data.getHeight();
		int height_mPagerMainPreMium = FragmentChangeActivity.heightScreen
				- (li_height_menu_premium + li_height_menu_top);

		Log.v("li_height hide", li_height_data + "_" + height_mPagerMainPreMium); //

		// LinearLayout.LayoutParams layoutParams = new
		// LinearLayout.LayoutParams(
		// FragmentChangeActivity.widthScreen, height_mPagerMainPreMium);
		// mPagerMainPreMium.setLayoutParams(layoutParams);

		setDataDetailFuncWidth(FragmentChangeActivity.widthScreen,
				height_mPagerMainPreMium);
	}

	// ====================== setDataDetail ================
	public static LinearLayout li_menu_premium;
	public static TextView tv_chart, tv_news, tv_sector;
	public static TextView tv_premium_chart, tv_premium_industry,
			tv_premium_fundamental, tv_premium_news, tv_premium_hit;
	// ImageView img_follow;
	public static LinearLayout li_showbysell;

	boolean ckHideShowBuySell = false; // hide = false
	boolean ckHideShowData = false;

	private void initTabPagerPreMium() {
		tv_premium_chart = (TextView) findViewById(R.id.tv_premium_chart);
		tv_premium_industry = (TextView) findViewById(R.id.tv_premium_industry);
		tv_premium_fundamental = (TextView) findViewById(R.id.tv_premium_fundamental);
		tv_premium_news = (TextView) findViewById(R.id.tv_premium_news);
		tv_premium_hit = (TextView) findViewById(R.id.tv_premium_hit);

		// try {
		// strIndustry = UiWatchlistDetail.contentGetDetail
		// .getString("industry");
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }

		hideDataDetail();

		tv_premium_chart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectTabPagerPreMium(1);
				mPagerMainPreMium.setCurrentItem(0);
			}
		});
		tv_premium_industry.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectTabPagerPreMium(2);
				mPagerMainPreMium.setCurrentItem(1);
			}
		});
		tv_premium_fundamental.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectTabPagerPreMium(3);
				mPagerMainPreMium.setCurrentItem(2);
			}
		});
		tv_premium_news.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectTabPagerPreMium(4);
				mPagerMainPreMium.setCurrentItem(3);
			}
		});
		tv_premium_hit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectTabPagerPreMium(5);
				mPagerMainPreMium.setCurrentItem(4);
			}
		});
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

	// public static ArrayList<TextView> row_tv_last_trade;
	// public static ArrayList<TextView> row_tv_change;
	// public static ArrayList<TextView> row_tv_percent_change;
	// public static ArrayList<TextView> row_tv_high;
	// public static ArrayList<TextView> row_tv_low;
	// public static ArrayList<TextView> row_tv_volume;
	// public static ArrayList<TextView> row_tv_value;
	// public static ArrayList<TextView> row_tv_buy_volume;
	// public static ArrayList<TextView> row_tv_buy_value;
	// public static ArrayList<TextView> row_tv_sell_volume;
	// public static ArrayList<TextView> row_tv_sell_value;
	// public static ArrayList<TextView> row_tv_avg_buy;
	// public static ArrayList<TextView> row_tv_avg_sell;
	// public static ArrayList<TextView> row_tv_max_buy_price;
	// public static ArrayList<TextView> row_tv_max_buy_price_volume;
	// public static ArrayList<TextView> row_tv_max_sell_price;
	// public static ArrayList<TextView> row_tv_max_sell_price_volume;
	// public static ArrayList<TextView> row_tv_open_price;
	// public static ArrayList<TextView> row_tv_open1_price;
	// public static ArrayList<TextView> row_tv_open1_price_volume;
	// public static ArrayList<TextView> row_tv_open2_price;
	// public static ArrayList<TextView> row_tv_open2_price_volume;
	// public static ArrayList<TextView> row_tv_close_volume;

	// main
	TextView tv_symbol, tv_symbol_status, tv_last_trade, tv_symbol_name_eng,
			tv_change, tv_percenchange;
	// ImageView img_updown;
	// column 1
	TextView tv_volume, tv_value, tv_open, tv_high, tv_low;
	// column 2
	TextView tv_prev_close, tv_ceil, tv_floor, tv_high52W, tv_low52W;
	// column 3
	TextView tv_roe, tv_roa, tv_peg, tv_p_e, tv_p_bv;

	LinearLayout li_showbuysell, li_show_hide, li_buysell;
	ImageView img_show_hide;
	TextView tv_average_buy, tv_max_buy_price_volume, tv_open1_volume,
			tv_buy_volume;

	TextView tv_average_sell, tv_max_sell_price_volume, tv_close_volume,
			tv_sell_volume;

	TextView tv_percentBuy, tv_percentSell;
	View v_percentBuy, v_percentSell;

	private void setDataDetail() {

		// init follow & chart iq
		// img_follow = (ImageView) findViewById(R.id.img_follow);
		setFollowSymbol();
		// initChartIq();

		// -------- tab pager
		li_menu_premium = (LinearLayout) findViewById(R.id.li_menu_premium);
		li_menu_premium.setVisibility(View.GONE);

		// -------- show hide data
		animShow = AnimationUtils.loadAnimation(this,
				R.animator.symbol_detail_down);
		animHide = AnimationUtils.loadAnimation(this,
				R.animator.symbol_detail_up);

		li_data_symbol = (LinearLayout) findViewById(R.id.li_data_symbol);
		popup_window_detail = (SlidingPanel) findViewById(R.id.popup_window_detail);

		li_data_symbol.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ckHideShowData) { // hide data
					showDataDetail();
				} else { // show data
					hideDataDetail();
				}
			}
		});

		try {
			if (contentGetDetail != null) {
				li_menu_premium.setVisibility(View.VISIBLE);

				// main
				tv_symbol = (TextView) findViewById(R.id.tv_symbol);
				tv_symbol_status = (TextView) findViewById(R.id.tv_symbol_status);
				tv_last_trade = (TextView) findViewById(R.id.tv_last_trade);
				tv_symbol_name_eng = (TextView) findViewById(R.id.tv_symbol_name_eng);
				tv_change = (TextView) findViewById(R.id.tv_change);
				tv_percenchange = (TextView) findViewById(R.id.tv_percenchange);
				// img_updown = (ImageView) findViewById(R.id.img_updown);
				// column 1
				tv_volume = (TextView) findViewById(R.id.tv_volume);
				tv_value = (TextView) findViewById(R.id.tv_value);
				tv_open = (TextView) findViewById(R.id.tv_open);
				tv_high = (TextView) findViewById(R.id.tv_high);
				tv_low = (TextView) findViewById(R.id.tv_low);
				// column 2
				tv_prev_close = (TextView) findViewById(R.id.tv_prev_close);
				tv_ceil = (TextView) findViewById(R.id.tv_ceil);
				tv_floor = (TextView) findViewById(R.id.tv_floor);
				tv_high52W = (TextView) findViewById(R.id.tv_high52W);
				tv_low52W = (TextView) findViewById(R.id.tv_low52W);
				// column 3
				tv_roe = (TextView) findViewById(R.id.tv_roe);
				tv_roa = (TextView) findViewById(R.id.tv_roa);
				tv_peg = (TextView) findViewById(R.id.tv_peg);
				tv_p_e = (TextView) findViewById(R.id.tv_p_e);
				tv_p_bv = (TextView) findViewById(R.id.tv_p_bv);

				// show hide
				li_showbysell = (LinearLayout) findViewById(R.id.li_showbuysell);
				li_show_hide = (LinearLayout) findViewById(R.id.li_show_hide);
				li_buysell = (LinearLayout) findViewById(R.id.li_buysell);
				img_show_hide = (ImageView) findViewById(R.id.img_show_hide);
				tv_average_buy = (TextView) findViewById(R.id.tv_average_buy);
				tv_max_buy_price_volume = (TextView) findViewById(R.id.tv_max_buy_price_volume);
				tv_open1_volume = (TextView) findViewById(R.id.tv_open1_volume);
				tv_buy_volume = (TextView) findViewById(R.id.tv_buy_volume);

				tv_average_sell = (TextView) findViewById(R.id.tv_average_sell);
				tv_max_sell_price_volume = (TextView) findViewById(R.id.tv_max_sell_price_volume);
				tv_close_volume = (TextView) findViewById(R.id.tv_close_volume);
				tv_sell_volume = (TextView) findViewById(R.id.tv_sell_volume);

				tv_percentBuy = (TextView) findViewById(R.id.tv_percentBuy);
				tv_percentSell = (TextView) findViewById(R.id.tv_percentSell);
				v_percentBuy = (View) findViewById(R.id.v_percentBuy);
				v_percentSell = (View) findViewById(R.id.v_percentSell);

				// ======= set data ========
				// main
				String strSymbol_name = contentGetDetail
						.getString("symbol_name");
				String strOrderbookId = contentGetDetail
						.getString("orderbook_id");
				String strLast_trade = contentGetDetail.getString("last_trade");
				String strSymbol_fullname_eng = contentGetDetail
						.getString("symbol_fullname_eng");

				String turnover_list_level = contentGetDetail
						.getString("turnover_list_level");
				String status = contentGetDetail.getString("status");
				String status_xd = contentGetDetail.getString("status_xd");
				strGetSymbolOrderBook_Id = strOrderbookId;

				// tv_symbol.setText(Html.fromHtml(FunctionSymbol
				// .checkStatusSymbol(strSymbol_name, turnover_list_level,
				// status, status_xd)));

				tv_symbol.setText(strSymbol_name);
				tv_symbol_status.setText(FunctionSymbol
						.checkStatusSymbolDetail(turnover_list_level, status,
								status_xd));

				tv_last_trade.setText(FunctionSymbol
						.setFormatNumber(strLast_trade));
				tv_symbol_name_eng.setText(strSymbol_fullname_eng);

				// main color
				String strColor = contentGetDetail.getString("change");

				// ck ltrade change
				String strLastTrade = contentGetDetail.getString("last_trade");
				String strChange = contentGetDetail.getString("change");
				String strPercentChange = contentGetDetail
						.getString("percentChange");

				tv_last_trade.setText(strLastTrade);
				// tv_change.setText(strChange);
				if ((strPercentChange == "0") || (strPercentChange == "")
						|| (strPercentChange == "0.00")) {
					tv_percenchange.setText("0.00");
				} else {
					// tv_percenchange.setText(strPercentChange + "%");
					tv_change
							.setText(FunctionSymbol.setFormatNumber(strChange));
					tv_percenchange.setText(" ("
							+ FunctionSymbol.setFormatNumber(strPercentChange)
							+ "%)");
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

				// column 1
				String strVolume = contentGetDetail.getString("volume");
				String strValue = contentGetDetail.getString("value");
				String strOpen = contentGetDetail.getString("open");
				String strHigh = contentGetDetail.getString("high");
				String strLow = contentGetDetail.getString("low");
				String strPrevClose = contentGetDetail.getString("prev_close");

				tv_volume.setText(strVolume);
				tv_value.setText(strValue);
				// -- color write blue
				tv_volume.setTextColor(getResources().getColor(
						FunctionSetBg.setStrColorWriteDetailBlue(strVolume)));
				tv_value.setTextColor(getResources().getColor(
						FunctionSetBg.setStrColorWriteDetailBlue(strValue)));
				
				tv_open.setText(FunctionSymbol.setFormatNumber(strOpen));

				tv_high.setText(FunctionSetBg.setStrDetailList(strHigh));
				tv_low.setText(FunctionSetBg.setStrDetailList(strLow));

				if (strPrevClose != "") {
					if (strHigh != "") {
						if ((Float.parseFloat(strHigh.replaceAll(",", ""))) > Float
								.parseFloat(strPrevClose.replaceAll(",", ""))) {
							tv_high.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[2]));
						} else if ((Float.parseFloat(strHigh
								.replaceAll(",", ""))) < Float
								.parseFloat(strPrevClose.replaceAll(",", ""))) {
							tv_high.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[0]));
						} else {
							tv_high.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[1]));
						}
					}
					if (strLow != "") {
						if ((Float.parseFloat(strLow.replaceAll(",", ""))) > Float
								.parseFloat(strPrevClose.replaceAll(",", ""))) {
							tv_low.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[2]));
						} else if ((Float
								.parseFloat(strLow.replaceAll(",", ""))) < Float
								.parseFloat(strPrevClose.replaceAll(",", ""))) {
							tv_low.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[0]));
						} else {
							tv_low.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[1]));
						}
					}
				}

				// column 2
				String strCeiling = contentGetDetail.getString("ceiling");
				String strFloor = contentGetDetail.getString("floor");
				String strHigh52W = contentGetDetail.getString("high52W");
				String strLow52W = contentGetDetail.getString("low52W");

				String strPbv = contentGetDetail.getString("p_bv");
				String strRoe = contentGetDetail.getString("roe");
				String strRoa = contentGetDetail.getString("roa");
				String strPe = contentGetDetail.getString("p_e");
				String strPeg = contentGetDetail.getString("peg");

				tv_prev_close.setText(FunctionSymbol
						.setFormatNumber(strPrevClose));
				tv_ceil.setText(FunctionSymbol.setFormatNumber(strCeiling));
				tv_floor.setText(FunctionSymbol.setFormatNumber(strFloor));
				tv_high52W.setText(FunctionSymbol.setFormatNumber(strHigh52W));
				tv_low52W.setText(FunctionSymbol.setFormatNumber(strLow52W));

				tv_roe.setText(FunctionSetBg.setStrDetailList(strRoe));
				tv_roa.setText(FunctionSetBg.setStrDetailList(strRoa));

				tv_peg.setText(FunctionSetBg.setStrDetailList(strPeg));
				tv_p_e.setText(FunctionSetBg.setStrDetailList(strPe));
				tv_p_bv.setText(FunctionSetBg.setStrDetailList(strPbv));

				// -- color write blue
				tv_ceil.setTextColor(getResources().getColor(
						FunctionSetBg.setStrColorWriteDetailBlue(strCeiling)));
				tv_roe.setTextColor(getResources().getColor(
						FunctionSetBg.setStrColorWriteDetailBlue(strRoe)));
				tv_roa.setTextColor(getResources().getColor(
						FunctionSetBg.setStrColorWriteDetailBlue(strRoa)));
				
				tv_floor.setTextColor(getResources().getColor(
						FunctionSetBg.setStrColorWriteDetailPink(strFloor)));

				if (SplashScreen.contentSymbol_Set != null) {
					String strPe_set = SplashScreen.contentSymbol_Set
							.getString("p_e");
					String strPbv_set = SplashScreen.contentSymbol_Set
							.getString("p_bv");
					String strPeg_set = SplashScreen.contentSymbol_Set
							.getString("peg");
					String strHigh52W_set = SplashScreen.contentSymbol_Set
							.getString("high52W");
					String strLow52W_set = SplashScreen.contentSymbol_Set
							.getString("low52W");

					tv_p_e.setTextColor(getResources().getColor(
							FunctionSetBg.setStrCheckSet(strPe, strPe_set)));
					tv_p_bv.setTextColor(getResources().getColor(
							FunctionSetBg.setStrCheckSet(strPbv, strPbv_set)));
					tv_peg.setTextColor(getResources().getColor(
							FunctionSetBg.setStrCheckSet(strPeg, strPeg_set)));

					// tv_peg.setTextColor(getResources().getColor(
					// FunctionSetBg.setStrCheckSet(strHigh52W,
					// strHigh52W_set)));
					// tv_peg.setTextColor(getResources().getColor(
					// FunctionSetBg.setStrCheckSet(strLow52W,
					// strLow52W_set)));
				}

				if (strPrevClose != "") {
					if (strHigh52W != "") {
						if ((Float.parseFloat(strHigh52W.replaceAll(",", ""))) > Float
								.parseFloat(strPrevClose.replaceAll(",", ""))) {
							tv_high52W.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[2]));
						} else if ((Float.parseFloat(strHigh52W.replaceAll(",",
								""))) < Float.parseFloat(strPrevClose
								.replaceAll(",", ""))) {
							tv_high52W.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[0]));
						} else {
							tv_high52W.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[1]));
						}
					}
					if (strLow52W != "") {
						if ((Float.parseFloat(strLow52W.replaceAll(",", ""))) > Float
								.parseFloat(strPrevClose.replaceAll(",", ""))) {
							tv_low52W.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[2]));
						} else if ((Float.parseFloat(strLow52W.replaceAll(",",
								""))) < Float.parseFloat(strPrevClose
								.replaceAll(",", ""))) {
							tv_low52W.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[0]));
						} else {
							tv_low52W.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[1]));
						}
					}
					if (strOpen != "") {
						if ((Float.parseFloat(strOpen.replaceAll(",", ""))) > Float
								.parseFloat(strPrevClose.replaceAll(",", ""))) {
							tv_open.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[2]));
						} else if ((Float.parseFloat(strOpen
								.replaceAll(",", ""))) < Float
								.parseFloat(strPrevClose.replaceAll(",", ""))) {
							tv_open.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[0]));
						} else {
							tv_open.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[1]));
						}
					}
				}

				// show hide
				// final boolean ckHideShow = false;
				String strSymbN = contentGetDetail.getString("symbol_name");
				String strSymbDot = strSymbN.substring(1, 2);
				if (strSymbDot.equals(".")) {
					li_showbysell.setVisibility(View.GONE);
				} else {
					li_showbysell.setVisibility(View.VISIBLE);
					li_show_hide.setVisibility(View.GONE);
					li_buysell.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (ckHideShowBuySell) {
								ckHideShowBuySell = false;
								li_show_hide.setVisibility(View.GONE);
								img_show_hide.setVisibility(View.GONE);
								img_show_hide
										.setBackgroundResource(R.drawable.icon_dopdowe_down);
								// showDataDetail();
							} else {
								ckHideShowBuySell = true;
								li_show_hide.setVisibility(View.VISIBLE);
								img_show_hide.setVisibility(View.VISIBLE);
								img_show_hide
										.setBackgroundResource(R.drawable.icon_dopdowe_up);
								// showDataDetail();
							}
						}
					});

					img_show_hide.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (ckHideShowBuySell) {
								ckHideShowBuySell = false;
								li_show_hide.setVisibility(View.GONE);
								img_show_hide.setVisibility(View.GONE);
								img_show_hide
										.setBackgroundResource(R.drawable.icon_dopdowe_down);
								// showDataDetail();
							} else {
								ckHideShowBuySell = true;
								li_show_hide.setVisibility(View.VISIBLE);
								img_show_hide.setVisibility(View.VISIBLE);
								img_show_hide
										.setBackgroundResource(R.drawable.icon_dopdowe_up);
								// showDataDetail();
							}
						}
					});

					String strAverage_buy = contentGetDetail
							.getString("average_buy");
					String strMax_buy_price_volume = contentGetDetail
							.getString("max_buy_price_volume_str");
					String strMax_buy_price = contentGetDetail
							.getString("max_buy_price");
					String strOpen1_volume = contentGetDetail
							.getString("open1_volume");
					String strOpen1 = contentGetDetail.getString("open1");
					String strBuy_volume = contentGetDetail
							.getString("buy_volume_str");

					String strAverage_sell = contentGetDetail
							.getString("average_sell");
					String strMax_sell_price_volume = contentGetDetail
							.getString("max_sell_price_volume_str");
					String strMax_sell_price = contentGetDetail
							.getString("max_sell_price");
					String strClose_volume = contentGetDetail
							.getString("close_volume");
					String strSell_volume = contentGetDetail
							.getString("sell_volume_str");

					String strPercentBuy = contentGetDetail
							.getString("percentBuy");
					String strPercentSell = contentGetDetail
							.getString("percentSell");

					tv_average_buy.setText(FunctionSymbol
							.checkNull(strAverage_buy));
					tv_average_buy
							.setTextColor(getResources()
									.getColor(
											FunctionSetBg
													.setStrColorWriteDetailSuccess(strAverage_buy)));
					tv_max_buy_price_volume.setText(FunctionSymbol
							.setFormatNumberEtc(strMax_buy_price_volume)
							+ ""
							+ FunctionSymbol.setBracket(strMax_buy_price));
					tv_max_buy_price_volume
							.setTextColor(getResources()
									.getColor(
											FunctionSetBg
													.setStrColorWriteDetailSuccess(strMax_buy_price)));
					tv_open1_volume.setText(FunctionSymbol
							.setFormatNumberEtc(strOpen1_volume)
							+ ""
							+ FunctionSymbol.setBracket(strOpen1));
					tv_open1_volume.setTextColor(getResources().getColor(
							FunctionSetBg
									.setStrColorWriteDetailSuccess(strOpen1)));
					tv_buy_volume.setText(FunctionSymbol
							.setFormatNumber0(strBuy_volume));
//					tv_buy_volume
//							.setTextColor(getResources()
//									.getColor(
//											FunctionSetBg
//													.setStrColorWriteDetailBlue(strBuy_volume)));

					tv_average_sell.setText(FunctionSymbol
							.checkNull(strAverage_sell));
					tv_average_sell
							.setTextColor(getResources()
									.getColor(
											FunctionSetBg
													.setStrColorWriteDetailDanger(strAverage_sell)));
					tv_max_sell_price_volume.setText(FunctionSymbol
							.setFormatNumberEtc(strMax_sell_price_volume)
							+ ""
							+ FunctionSymbol.setBracket(strMax_sell_price));
					tv_max_sell_price_volume
							.setTextColor(getResources()
									.getColor(
											FunctionSetBg
													.setStrColorWriteDetailDanger(strMax_sell_price_volume)));
					tv_close_volume.setText(FunctionSetBg
							.setStrDetailList(strClose_volume));
					tv_close_volume
							.setTextColor(getResources()
									.getColor(
											FunctionSetBg
													.setStrColorWriteDetailDanger(strClose_volume)));

					tv_sell_volume.setText(FunctionSymbol
							.setFormatNumberEtc(strSell_volume));
					tv_sell_volume
							.setTextColor(getResources()
									.getColor(
											FunctionSetBg
													.setStrColorWriteDetailBlue(strSell_volume)));

					// percentBuy , sell
					tv_percentBuy.setText("BUY ("
							+ FunctionSymbol.setFormatNumber(strPercentBuy)
							+ "%)");
					tv_percentSell.setText("("
							+ FunctionSymbol.setFormatNumber(strPercentSell)
							+ "%) SELL");

					float vBuy = Float.parseFloat(strPercentBuy.replaceAll(",",
							""));
					float vSell = Float.parseFloat(strPercentSell.replaceAll(
							",", ""));
					LinearLayout.LayoutParams loparams = (LinearLayout.LayoutParams) v_percentBuy
							.getLayoutParams();
					loparams.weight = vBuy;
					v_percentBuy.setLayoutParams(loparams);

					LinearLayout.LayoutParams loparams2 = (LinearLayout.LayoutParams) v_percentSell
							.getLayoutParams();
					loparams2.weight = vSell;
					v_percentSell.setLayoutParams(loparams2);

					// ------ update symbol -------
					// if (SplashScreen.contentGetUserById != null) {
					// if (!(SplashScreen.contentGetUserById
					// .getString("package").equals("free"))) {
					//
					// // ---- set status connect
					// ImageView img_connect_c = (ImageView)
					// findViewById(R.id.img_connect_c);
					// img_connect_c
					// .setBackgroundResource(R.drawable.icon_connect_c_green);
					//
					// startUpdateSymbol();
					// }
					// }

					// --- add view tv
					// row_tv_last_trade.add(tv_last_trade);
					// row_tv_change.add(tv_change);
					// row_tv_percent_change.add(tv_percenchange);
					// row_tv_high.add(tv_high);
					// row_tv_low.add(tv_low);
					// row_tv_volume.add(tv_volume);
					// row_tv_value.add(tv_value);
					// row_tv_buy_volume.add(tv_buy_volume);
					// row_tv_buy_value.add(tv_buy_);
					// row_tv_sell_volume.add(tv_sell_volume);
					// row_tv_sell_value.add(tv_);
					// row_tv_avg_buy.add(tv_average_buy);
					// row_tv_avg_sell.add(tv_average_sell);
					// row_tv_max_buy_price.add(tv_);
					// row_tv_max_buy_price_volume.add(tv_max_buy_price_volume);
					// row_tv_max_sell_price.add(tv_);
					// row_tv_max_sell_price_volume.add(tv_max_sell_price_volume);
					// row_tv_open_price.add(tv_open);
					// row_tv_open1_price.add(tv_);
					// row_tv_open1_price_volume.add(tv_open1_volume);
					// row_tv_open2_price.add(tv_);
					// row_tv_open2_price_volume.add(tv_);
					// row_tv_close_volume.add(tv_close_volume);
				}

				// ------ connect socket -----------------
				if (FragmentChangeActivity.strGetSymbolOrderBook_Id != "") {
					if (!(SplashScreen.contentGetUserById.getString("package")
							.equals("free"))) {
						connectWebSocket();
					}
				}

				// -------- heightScreen ui detail
				FragmentChangeActivity.heightScreen = li_detail.getHeight();
				// -------- init pager
				initPagerPreMium();

			} else {
				Toast.makeText(getApplicationContext(), "No data.", 0).show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// dialogLoading.dismiss();
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
			tv_high.setText(strHigh);
			tv_low.setText(strLow);
			tv_volume.setText(strVolume);
			tv_value.setText(strValue);

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

	// ============= dialog favorite =========
	@SuppressLint("NewApi")
	public static AlertDialog alertDialogFollow;

	private void dialogFavorite() {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View dlView = layoutInflater.inflate(R.layout.dialog_favorite, null);
		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);
		alertDialogBuilder.setView(dlView);

		LinearLayout li_favorite1 = (LinearLayout) dlView
				.findViewById(R.id.li_favorite1);
		LinearLayout li_favorite2 = (LinearLayout) dlView
				.findViewById(R.id.li_favorite2);
		LinearLayout li_favorite3 = (LinearLayout) dlView
				.findViewById(R.id.li_favorite3);
		LinearLayout li_favorite4 = (LinearLayout) dlView
				.findViewById(R.id.li_favorite4);
		LinearLayout li_favorite5 = (LinearLayout) dlView
				.findViewById(R.id.li_favorite5);

		li_favorite1.setOnClickListener(onClickListenerFavorite);
		li_favorite2.setOnClickListener(onClickListenerFavorite);
		li_favorite3.setOnClickListener(onClickListenerFavorite);
		li_favorite4.setOnClickListener(onClickListenerFavorite);
		li_favorite5.setOnClickListener(onClickListenerFavorite);

		// alertDialogBuilder.setNegativeButton("ยกเลิก",
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int id) {
		// dialog.cancel();
		// }
		// });

		// create an alert dialog
		alertDialogFollow = alertDialogBuilder.create();
		alertDialogFollow.show();

		// android.util.AndroidRuntimeException: requestFeature() must be called
		// before adding content
		// alertDialogFollow.requestWindowFeature(Window.FEATURE_NO_TITLE);

		alertDialogFollow.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		WindowManager.LayoutParams wmlp = alertDialogFollow.getWindow()
				.getAttributes();

		wmlp.gravity = Gravity.TOP | Gravity.RIGHT;
		// wmlp.x = 100; // x position
		wmlp.y = 100; // y position
	}

	// ********** select favorite **********
	private OnClickListener onClickListenerFavorite = new OnClickListener() {
		@Override
		public void onClick(final View v) {
			switch (v.getId()) {
			case R.id.li_favorite1:
				FragmentChangeActivity.strFavoriteNumber = "1";
				checkAddFavorite();
				alertDialogFollow.dismiss();
				break;
			case R.id.li_favorite2:
				FragmentChangeActivity.strFavoriteNumber = "2";
				checkAddFavorite();
				alertDialogFollow.dismiss();
				break;
			case R.id.li_favorite3:
				FragmentChangeActivity.strFavoriteNumber = "3";
				checkAddFavorite();
				alertDialogFollow.dismiss();
				break;
			case R.id.li_favorite4:
				FragmentChangeActivity.strFavoriteNumber = "4";
				checkAddFavorite();
				alertDialogFollow.dismiss();
				break;
			case R.id.li_favorite5:
				FragmentChangeActivity.strFavoriteNumber = "5";
				checkAddFavorite();
				alertDialogFollow.dismiss();
				break;
			default:
				break;
			}
		}
	};

	// ============== send addfavorite ===============
	public void checkAddFavorite() {
		if (FunctionSymbol
				.checkFollowCount(FragmentChangeActivity.strFavoriteNumber)) {
			sendAddFavorite();
		} else {
			Toast.makeText(
					getApplicationContext(),
					"Over Limit Favorite "
							+ FragmentChangeActivity.strFavoriteNumber, 0)
					.show();
		}
	}

	public void sendAddFavorite() {
		setFavorite resp = new setFavorite();
		resp.execute();
	}

	public class setFavorite extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		String temp = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialogLoading.show();

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
			pagerWatchList.initGetData();

			// updateDataFavorite();

			// switchFragment(new PagerWatchlistDetail());
		}
	}

	// ============= Pager PreMium ===========
	private void initPagerPreMium() {
		try {
			strIndustry = UiWatchlistDetail.contentGetDetail
					.getString("industry");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// creating fragments and adding to list
		fragmentMainPreMium.removeAll(fragmentMainPreMium);
		// fragmentMainPreMium.add(Fragment.instantiate(getApplicationContext(),
		// PagerWatchListDetailChart.class.getName()));
		fragmentMainPreMium.add(Fragment.instantiate(getApplicationContext(),
				PagerWatchListDetailChart_New1.class.getName()));
		fragmentMainPreMium.add(Fragment.instantiate(getApplicationContext(),
				PagerWatchListDetailIndustry.class.getName()));
		fragmentMainPreMium.add(Fragment.instantiate(getApplicationContext(),
				PagerWatchListDetailFundamental.class.getName()));
		fragmentMainPreMium.add(Fragment.instantiate(getApplicationContext(),
				PagerWatchListDetailNews.class.getName()));
		fragmentMainPreMium.add(Fragment.instantiate(getApplicationContext(),
				PagerWatchListDetailHit.class.getName()));

		// creating adapter and linking to view pager
		this.mPagerAdapterMainPreMium = new PagerAdapter(
				super.getSupportFragmentManager(), fragmentMainPreMium);
		mPagerMainPreMium = (ViewPager) findViewById(R.id.vp_pager);

		mPagerMainPreMium.setAdapter(this.mPagerAdapterMainPreMium);

		mPagerMainPreMium.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				selectTabPagerPreMium((mPagerMainPreMium.getCurrentItem()) + 1);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				mPagerMainPreMium.getParent()
						.requestDisallowInterceptTouchEvent(true);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		initTabPagerPreMium(); // init tab pager

	}

	// ============= set tab ===========
	public int selectTabPagerPreMium(int numtab) {
		tv_premium_chart.setBackgroundColor(Color.TRANSPARENT);
		tv_premium_industry.setBackgroundColor(Color.TRANSPARENT);
		tv_premium_fundamental.setBackgroundColor(Color.TRANSPARENT);
		tv_premium_news.setBackgroundColor(Color.TRANSPARENT);
		tv_premium_hit.setBackgroundColor(Color.TRANSPARENT);

		tv_premium_chart.setTextColor(getResources()
				.getColor(R.color.c_content));
		tv_premium_industry.setTextColor(getResources().getColor(
				R.color.c_content));
		tv_premium_fundamental.setTextColor(getResources().getColor(
				R.color.c_content));
		tv_premium_news
				.setTextColor(getResources().getColor(R.color.c_content));
		tv_premium_hit.setTextColor(getResources().getColor(R.color.c_content));

		if (numtab == 1) {
			tv_premium_chart
					.setBackgroundResource(R.drawable.border_button_activeleft);
			tv_premium_chart.setTextColor(getResources().getColor(
					R.color.bg_default));

			hideDataDetail();
		} else if (numtab == 2) {
			tv_premium_industry
					.setBackgroundResource(R.drawable.border_button_activecenter);
			tv_premium_industry.setTextColor(getResources().getColor(
					R.color.bg_default));

			hideDataDetail();
		} else if (numtab == 3) {
			tv_premium_fundamental
					.setBackgroundResource(R.drawable.border_button_activecenter);
			tv_premium_fundamental.setTextColor(getResources().getColor(
					R.color.bg_default));

			hideDataDetail();
		} else if (numtab == 4) {
			tv_premium_news
					.setBackgroundResource(R.drawable.border_button_activecenter);
			tv_premium_news.setTextColor(getResources().getColor(
					R.color.bg_default));

			hideDataDetail();
		} else if (numtab == 5) {
			tv_premium_hit
					.setBackgroundResource(R.drawable.border_button_activeright);
			tv_premium_hit.setTextColor(getResources().getColor(
					R.color.bg_default));

			showDataDetail();
		}
		return numtab;
	}

	// ============= set tab ===========
	public int selectTabPager(int numtab) {
		tv_chart.setBackgroundColor(Color.TRANSPARENT);
		tv_news.setBackgroundColor(Color.TRANSPARENT);
		tv_sector.setBackgroundColor(Color.TRANSPARENT);

		tv_chart.setTextColor(getResources().getColor(R.color.c_content));
		tv_news.setTextColor(getResources().getColor(R.color.c_content));
		tv_sector.setTextColor(getResources().getColor(R.color.c_content));

		if (numtab == 1) {
			tv_chart.setBackgroundResource(R.drawable.border_button_activeleft);
			tv_chart.setTextColor(getResources().getColor(R.color.bg_default));
		} else if (numtab == 2) {
			tv_sector
					.setBackgroundResource(R.drawable.border_button_activecenter);
			tv_sector.setTextColor(getResources().getColor(R.color.bg_default));
		} else if (numtab == 3) {
			tv_news.setBackgroundResource(R.drawable.border_button_activeright);
			tv_news.setTextColor(getResources().getColor(R.color.bg_default));
		}

		return numtab;
	}

	// ============== send removefavorite ===============
	public static String strRemoveId = "";

	public void sendRemoveFavorite() {

		setRemoveFavorite resp = new setRemoveFavorite();
		resp.execute();
	}

	public class setRemoveFavorite extends AsyncTask<Void, Void, Void> {

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

			pagerWatchList.initGetData();

			// updateDataFavorite();
			// switchFragment(new PagerWatchlistDetail());
		}
	}

	// ============== get favorite id =============
	public static JSONArray contentGetFavoriteId = null;

	private void getDataFavoriteId() {
		getFavoriteId resp = new getFavoriteId();
		resp.execute();
	}

	public class getFavoriteId extends AsyncTask<Void, Void, Void> {
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

						// {"status":"ok","message":"Success.","dataAll":[{"id":"162","favorite_number":"1","favorite_sort":"100","favorite_symbol":"SIMAT","favorite_type":"WATCHLIST","user_id":"104","created_at":"2016-01-08 01:53:46","updated_at":"2016-01-08 01:53:46","symbol_name":"SIMAT","symbol_pk":"SIMAT","market_id":"MAI","symbol_fullname_eng":"SIMAT TECHNOLOGIES PUBLIC","symbol_fullname_thai":"บริษัท ไซแมท เทคโนโลยี จำกัด (มหาชน)","last_trade":"3.34","average_price":"3.35","average_buy":"3.34637","average_sell":"3.35527","prev_close":"3.34","open":"3.34","open1":"3.34","open2":"3.34","close":"3.34","adj_close":"3.34","high":"3.4","high52W":"4.94","low":"3.3","low52W":"2.5","volume":"2.70 M","value":"9,054,824.00","change":"0.00","percentChange":"0.00","ceiling":"4.34","floor":"2.34","yield":"0","eps":"-0.08","p_bv":"2.12","d_e":"1.02","p_e":"0","roa":"-2.09","roe":"-11.04","npm":"-6.1","peg":null,"dps":"0","ffloat":"54.44","paid_up":"395.74","ebitda":"14.99","cg_score":"0","percentChange1W":"6.37","percentChange1M":"33.6","percentChange3M":"15.17","status":null,"industry":"TECH-m","SET50":"N","SET100":"N","SETHD":"N","sector":"TECH-ms","security_type":"S","benefit":null,"listed_share":"395742431","bv_nv":"1.58","qp_e":"6","financial_statement_date":"2016-06-30","ending_date":"2015-12-31","pod":"12","par_value":"1","market_capitalization":"1321779719.54","tr_bv":"0.23","npg_flag":"0","acc_ds":"0","acc_py":null,"dpr":"0","earning_date":"2016-06-30","allow_short_sell":"0","allow_nvdr":"1","allow_short_sell_on_nvdr":"0","allow_ttf":"3","stabilization_flag":"0","notification_type":null,"non_compliance":"0","parent_symbol":"SIMAT_SYMB","orderbook_id":"1881","subscriptionGroupId":"261","beneficial_signs":"","updateDatetime":"2016-11-14 09:59:49","split_flag":"0","1y_rtn":null,"magic1":null,"magic2":null,"mg":null,"marketListId":"STOCK","segmentId":"COMMON_STOCK","prev_volume":"903940","max_volume200day":"17320745","max_price200day":"3.98","max_volume_high_price200day":"2.67","project_open1":"0","project_open2":"3.74","project_close":"0","project_open1_volume":"0","project_open2_volume":"8000","project_close_volume":"0","project_open1_value":"0","project_open2_value":"29920","project_close_value":"0","project_open1_percent_change":"0","project_open2_percent_change":"0","project_close_percent_change":"0","max_buy_price":"3.34","max_sell_price":"3.34","max_buy_price_volume":"499900","max_sell_price_volume":"79200","open1_volume":"25000","open2_volume":"10000","close_volume":"0","buy_volume":"650400","buy_value":"2176476","sell_volume":"546700","sell_value":"1834323.9","last_update_date":"2016-11-14","turnover_list_level":"0","ipo_price":"3.8","market_status":"","first_trading_date":"2011-01-01"},{"id":"517","favorite_number":"1","favorite_sort":"100","favorite_symbol":"CBG","favorite_type":"WATCHLIST","user_id":"104","created_at":"2016-03-23 12:15:45","updated_at":"2016-03-23 12:15:45","symbol_name":"CBG","symbol_pk":"CBG","market_id":"SET","symbol_fullname_eng":"CARABAO GROUP PUBLIC COMPANY","symbol_fullname_thai":"บริษัท คาราบาวกรุ๊ป จำกัด (มหาชน)","last_trade":"70.50","average_price":"70.84","average_buy":"70.1334","average_sell":"69.9639","prev_close":"70.25","open":"69.75","open1":"69.75","open2":"70.75","close":"70.5","adj_close":"70.75","high":"72.5","high52W":"73","low":"69.25","low52W":"32.25","volume":"4.11 M","value":"291,199,488.00","change":"0.25","percentChange":"0.36","ceiling":"91.25","floor":"49.25","yield":"1.27","eps":"0.76876","p_bv":"10.71","d_e":"0.18","p_e":"51.79","roa":"21.08","roe":"21.43","npm":"17.14","peg":"-5.02","dps":"0.4","ffloat":"24.68","paid_up":"1000","ebitda":"1020.56","cg_score":"3","percentChange1W":"1.81","percentChange1M":"16.12","percentChange3M":"17.57","status":null,"industry":"AGRO","SET50":"N","SET100":"N","SETHD":"N","sector":"FOOD","security_type":"S","benefit":null,"listed_share":"1000000000","bv_nv":"6.56","qp_e":"6","financial_statement_date":"

						for (int i = 0; i < contentGetFavoriteId.length(); i++) {
							JSONObject jso = contentGetFavoriteId
									.getJSONObject(i);
							Log.v("jso.getString(symbol_name)",
									""
											+ jso.getString("symbol_name")
											+ "__"
											+ FragmentChangeActivity.strSymbolSelect);
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

	// ============== update favorite =============
	public void updateDataFavorite() {
		getFavorite resp = new getFavorite();
		resp.execute();
	}

	public class getFavorite extends AsyncTask<Void, Void, Void> {
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

			String url_fav = SplashScreen.url_bidschart
					+ "/service/v2/symbolFavorite?user_id="
					+ SplashScreen.userModel.user_id + "&timestamp="
					+ timestamp;

			// Log.v("url_fav update", "" + url_fav);

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

						setFollowSymbol();
						dialogLoading.dismiss();
						FragmentChangeActivity.ckLoadWatchlist = true;

						// pagerWatchList.initGetData();

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} else {
				dialogLoading.dismiss();
			}
			// dialogLoading.dismiss();
		}
	}

	// config rotation
	public static boolean orientation_landscape = false; // เช็ค แนวนอน

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// แนวนอน
			Log.v("onConfigurationChan >> ", "แนวนอน");

			orientation_landscape = true;

			mPagerMainPreMium.setCurrentItem(0);

			li_vertical.setVisibility(View.GONE);
			li_menu_premium.setVisibility(View.GONE);

			PagerWatchListDetailChart_New1.topbarShow();
			PagerWatchListDetailChart_New1.menuShowHide();

			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			mPagerMainPreMium.setLayoutParams(layoutParams);
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			// แนวตั้ง
			Log.v("onConfigurationChan >> ", "แนวตั้ง");

			orientation_landscape = false;

			mPagerMainPreMium.setCurrentItem(0);

			li_vertical.setVisibility(View.VISIBLE);
			li_menu_premium.setVisibility(View.VISIBLE);

			PagerWatchListDetailChart_New1.menuShowHide();
			PagerWatchListDetailChart_New1.topbarHide();

			ckHideShowData = true;
			li_data.setVisibility(View.GONE);

			hideDataDetail();
		}
	}

	protected void switchFragment(PagerWatchList fragment) {
		if (getApplicationContext() == null)
			return;
		if (getApplicationContext() instanceof FragmentChangeActivity) {
			FragmentChangeActivity fca = (FragmentChangeActivity) getApplicationContext();
			fca.switchContent(fragment);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			FragmentChangeActivity.wv_chartiq
					.loadUrl("javascript:(function () { "
							+ "mobileControl.closeConnection();" + "})()");
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
