package com.app.bids;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import java.util.Timer;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;
import com.app.bids.PagerProfile.loadData;
import com.app.bids.UiProfileBilling.getFavoriteId;
import com.app.bids.swiqe.ActivitySwipeMotion;
import com.app.bids.util.IabHelper;
import com.app.bids.util.IabResult;
import com.app.bids.util.Inventory;
import com.app.bids.util.Purchase;
import com.app.bids.util.SkuDetails;
import com.app.model.LoginModel.LoginInterface;
import com.app.model.LoginModel.UserModel;
import com.app.model.login.FacebookLoginActivity;
import com.app.model.login.LoginDialog;
import com.app.model.login.LoginDialog2;
import com.app.model.login.TwitterLoginActivity;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import android.R.bool;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.hardware.camera2.params.BlackLevelPattern;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.utils.StorageUtils;

@SuppressLint("NewApi")
public class FragmentChangeActivity extends FragmentActivity implements
		LoginInterface {

	private Fragment mContent;
	public static View view;
	private static Activity act;

	public static int id_websocket = 0;

	static LoginDialog loginDialog;

	// ========== notification =========
	public static final String EXTRA_MESSAGE = "message";
	// public static final String PROPERTY_REG_ID =
	// "AIzaSyAiXU0AvAIfz57etjZBHnHSG-p3ESSKOQA";
	public static final String PROPERTY_REG_ID = "";
	private static final String PROPERTY_APP_VERSION = "1";
	private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

	String SENDER_ID = "611883145612297"; // bids
	static final String TAG = "GCM Demo";

	GoogleCloudMessaging gcm;
	AtomicInteger msgId = new AtomicInteger();
	// Context context;

	String regid;

	// list contains fragments to instantiate in the viewpager
	List<Fragment> fragmentMain = new Vector<Fragment>();

	// ImageLoader
	public static ImageLoader imageLoader = ImageLoader.getInstance();

	public static DisplayImageOptions options;
	public static DisplayImageOptions optionsRounded;

	// json article
	public static JSONObject contentGetArticleSelect = null;

	// get symbol
	public static JSONArray contentGetSymbol = null;
	public static JSONArray contentUpdateSymbol = null;
	public static ArrayList<String> arr_getsymbol = new ArrayList<String>();
	public static ArrayList<CatalogGetSymbol> list_getSymbol = new ArrayList<CatalogGetSymbol>();
	public static ArrayList<CatalogGetSymbol> list_webview = new ArrayList<CatalogGetSymbol>();

	// get name fund
	public static JSONArray contentGetNameFund = null;
	public static ArrayList<CatalogGetNameFund> list_getNameFund = new ArrayList<CatalogGetNameFund>();

	// pager detail select
	public static String pagerDetail = "watchlist";
	public static String strSymbolSelect = "";
	public static String strFollowRemoveId = "";

	// profile
	public static boolean ckShowSuggestion = false;

	// witchlist
	public static JSONArray contentGetIndustrySetSector = null;
	public static JSONArray contentGetWatchlistSymbol = null;
	public static JSONArray contentGetWatchlistSystemTrade = null;
	public static Timer timerUpdateSymbol;
	public static int timerUpdateSymbolLength = 40000;
	public static int timerDelayChangeColor = 1000;
	public static boolean timerUpdateSymbolStatus = false;
	public static boolean ckLoadFavAll = false;
	public static boolean ckLoadWatchlist = false; // false ไม่ต้องโหลดใหม่ ,
													// true
													// ให้โหลดใหม่(เลือกCategories)

	// public static JSONArray contentGetWatchlistSymbolBegin = null;
	public static JSONArray contentGetSymbolFavorite = null;
	public static JSONArray contentGetSymbolFavorite_1 = null;
	public static JSONArray contentGetSymbolFavorite_2 = null;
	public static JSONArray contentGetSymbolFavorite_3 = null;
	public static JSONArray contentGetSymbolFavorite_4 = null;
	public static JSONArray contentGetSymbolFavorite_5 = null;
	public static String strGetListSymbol_fav1 = "";
	public static String strGetListSymbol_fav2 = "";
	public static String strGetListSymbol_fav3 = "";
	public static String strGetListSymbol_fav4 = "";
	public static String strGetListSymbol_fav5 = "";
	public static String strGetListSymbol = ""; // .set,.set50,.set100,.setHD,.mai
	public static String strGetSymbolOrderBook_Id_default = "1024,1062,1063,1064,1025"; // order
																						// book
																						// id
	public static String strGetSymbolOrderBook_Id = ""; // order book id
	public static JSONArray contentGetFavorite = null;

	public static String strWatchlistCategory = "favorite"; // sector, topmost,
															// trendsignal,
															// isdustry,
															// fundamental,
															// favorite,
															// portfolio

	// system trade
	public static JSONObject contentGetSystemTradeMacd = null;
	public static JSONObject contentGetSystemTradeEma = null;
	public static JSONArray contentGetSystemTradeCdc = null;
	public static JSONArray contentGetSystemTradeBreakOut = null;
	public static JSONArray contentGetSystemTradeBreakOutAth = null;
	public static JSONArray contentGetSystemTradeEsu = null;
	public static JSONArray contentGetSystemTradeFundDetailList = null;
	public static JSONArray contentGetSystemTradeDusitRatio = null;
	public static JSONObject contentGetSystemTradeDusitRatioDusit = null;
	public static JSONObject contentGetSystemTradeDusitRatioDefault = null;
	public static JSONObject contentGetSystemTradeDusitRatioSelect = null;

	public static JSONObject contentGetRetrieveSetAlert = null;

	// smart portfolio
	public static JSONObject contentGetSmartPortfolioList = null;
	public static JSONObject contentAddPortfolioAdd = null;

	// public static CatalogGetNameFund catAddPortfolioNameFund;
	// public static CatalogGetSymbol catAddPortfolioSymbol;

	public static boolean selectSearchPortStock = true; // T=stock, F=fund
	public static String strSearchPortName = "";
	public static ArrayList<CatalogGetSymbol> contentAddPortfolioAddz = null;
	public static boolean ckLoadSmartPortfolioList = false; // ถ้า add port
															// ให้โหลดใหม่
	public static CatalogAddPorfotlio catalogAddPorfotlio = new CatalogAddPorfotlio();

	public static JSONArray contentGetTxtSlidingMarquee = null;
	public static JSONObject jsonTxtSlidingMarquee;

	public static String strWatchlistCategorySelect = "";
	public static String strIndustrySelect = "";

	// favorite select
	// public static String strFavoriteTitle = "Favorite 1";
	public static String strFavoriteNumber = "1";

	private PagerAdapter mPagerAdapterMain;

	// Hot --> hot, game, heatmap
	public static int hotPageNum = 0;
	// public static int hotPageArr[] = { 0, 1, 2 }; // hot, game, heatmap
	public static String hotPageUrl[] = { "/mainMini", "/game?user_id=104",
			"/m/heat-map" }; // hot, game, heatmap

	// loading
	private LoadingDialog loadingDialog;

	// status load default
	public static boolean stsWatchList = false;
	public static boolean stsSystemTrade = false;
	public static boolean stsPortfolio = false;

	// public static boolean stsNews = false;
	public static boolean stsProfile = false;

	// ========== WatchlistDetail chartiq webview
	public static WebView wv_chartiq;
	public static int heightScreen;
	public static int widthScreen;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		act = this;
		act.requestWindowFeature(Window.FEATURE_NO_TITLE);

		registerInBackground();

		// http://www.bidschart.com/iq/stx-mobile-3.html#.SET
		wv_chartiq = new WebView(act);
		wv_chartiq.getSettings().setLoadWithOverviewMode(true);
		wv_chartiq.getSettings().setUseWideViewPort(true);
		wv_chartiq.getSettings().setBuiltInZoomControls(true);
		wv_chartiq.getSettings().setDomStorageEnabled(true); // android webview
																// local storage
		
		wv_chartiq.getSettings().setJavaScriptEnabled(true);
		wv_chartiq.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT));
		wv_chartiq.setWebViewClient(new myWebClient());
		wv_chartiq.loadUrl(SplashScreen.wv_chartiq_url + ".SET");

		loadingDialog = new LoadingDialog(act, this); // dialog loadding
		// loadingDialog.show();

		// set the Above View
		if (savedInstanceState != null)
			mContent = getSupportFragmentManager().getFragment(
					savedInstanceState, "mContent");
		if (mContent == null)
			mContent = new PagerDefault();

		// set the Above View
		LayoutInflater inflater = (LayoutInflater) getApplicationContext()
				.getSystemService(
						getApplicationContext().LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.fragment_activity, null);

		setContentView(view);

		// --------- google analytics
		// Tracker t = ((GoogleAnalyticsApp) getApplicationContext())
		// .getTracker(TrackerName.APP_TRACKER);
		// t.setScreenName("Home");
		// t.send(new HitBuilders.AppViewBuilder().build());

		// ======================
		// GET DEVICE ID => 53e004ada30ad7d2
		loginDialog = new LoginDialog(act, this);

		DisplayMetrics displaymetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
		heightScreen = displaymetrics.heightPixels;
		widthScreen = displaymetrics.widthPixels; 

		contentGetSymbol = null;
		if (SplashScreen.userModel.user_id != "") {
			selectMenu();
			initMenuBottomPopup();
		} else {
			onFacebookClick();
			selectMenu();
			initMenuBottomPopup();
		}

		// initGetDataBegin(); // get data begin

		// ***************** เซฟรูปลงเครื่อง ******************
		// imageLoader.init(ImageLoaderConfiguration
		// .createDefault(getApplicationContext()));

		File cacheDir = StorageUtils.getCacheDirectory(getApplicationContext());
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				getApplicationContext())
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				.memoryCache(new LruMemoryCache(2 * 1024 * 1024))
				.memoryCacheSize(2 * 1024 * 1024).memoryCacheSizePercentage(50)
				.discCache(new UnlimitedDiscCache(cacheDir))
				.discCacheSize(50 * 1024 * 1024).discCacheFileCount(100)
				.writeDebugLogs().build();

		imageLoader.init(config);

		options = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.icon_empty)
				.showImageForEmptyUri(R.drawable.icon_empty)
				.showImageOnFail(R.drawable.icon_empty).cacheInMemory(true)
				.cacheOnDisc(true).considerExifParams(true).build();
		// ------- Rounded -----------
		optionsRounded = new DisplayImageOptions.Builder()
				.showImageOnLoading(R.drawable.icon_empty)
				.showImageForEmptyUri(R.drawable.icon_empty)
				.showImageOnFail(R.drawable.icon_empty).cacheInMemory(true)
				.cacheOnDisc(true).considerExifParams(true)
				.displayer(new RoundedBitmapDisplayer(120)) // rounded corner
				.build();

		// ***************** end function save image ******************

		getSupportFragmentManager().beginTransaction()
				.replace(R.id.li_content_frame, mContent).commit();
	}

	// @Override
	// protected void onStart() {
	// super.onStart();
	// Log.v("FragmentChangeActivity", "onStart onStart onStart");
	// GoogleAnalytics.getInstance(FragmentChangeActivity.this).reportActivityStart(this);
	// }
	// @Override
	// protected void onStop() {
	// super.onStop();
	// Log.v("FragmentChangeActivity", "onStop onStop onStop");
	// GoogleAnalytics.getInstance(FragmentChangeActivity.this).reportActivityStop(this);
	// }

	// ====== webview client ============
	public class myWebClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			super.onPageFinished(view, url);
			Log.v("myWebClient", "onPageFinished");
		}
	}

	// ***************** select menu ******************
	LinearLayout menu_bottom_home_t, menu_bottom_smartport_t,
			menu_bottom_systemtrade_t, menu_bottom_news_t,
			menu_bottom_profile_t;
	ImageView img_bottom_home_t, img_bottom_smartport_t,
			img_bottom_systemtrade_t, img_bottom_news_t, img_bottom_profile_t;
	TextView tv_bottom_home_t, tv_bottom_smartport_t, tv_bottom_systemtrade_t,
			tv_bottom_news_t, tv_bottom_profile_t;

	public void selectMenu() {

		// loginDialog = new LoginDialog(act, this);

		// initBilling(); // initBilling

		handlerSetProfile.sendEmptyMessage(1);
		switchContent(new PagerWatchList());

		// ========= menu bottom
		// row 0
		menu_bottom_home_t = (LinearLayout) findViewById(R.id.menu_bottom_home_t);
		menu_bottom_smartport_t = (LinearLayout) findViewById(R.id.menu_bottom_smartport_t);
		menu_bottom_systemtrade_t = (LinearLayout) findViewById(R.id.menu_bottom_systemtrade_t);
		menu_bottom_news_t = (LinearLayout) findViewById(R.id.menu_bottom_news_t);
		menu_bottom_profile_t = (LinearLayout) findViewById(R.id.menu_bottom_profile_t);
		// ---- img
		img_bottom_home_t = (ImageView) findViewById(R.id.img_bottom_home_t);
		img_bottom_smartport_t = (ImageView) findViewById(R.id.img_bottom_smartport_t);
		img_bottom_systemtrade_t = (ImageView) findViewById(R.id.img_bottom_systemtrade_t);
		img_bottom_news_t = (ImageView) findViewById(R.id.img_bottom_news_t);
		img_bottom_profile_t = (ImageView) findViewById(R.id.img_bottom_profile_t);
		// ---- textview
		tv_bottom_home_t = (TextView) findViewById(R.id.tv_bottom_home_t);
		tv_bottom_smartport_t = (TextView) findViewById(R.id.tv_bottom_smartport_t);
		tv_bottom_systemtrade_t = (TextView) findViewById(R.id.tv_bottom_systemtrade_t);
		tv_bottom_news_t = (TextView) findViewById(R.id.tv_bottom_news_t);
		tv_bottom_profile_t = (TextView) findViewById(R.id.tv_bottom_profile_t);

		menu_bottom_home_t.setOnClickListener(onClickMenuBottom);
		menu_bottom_smartport_t.setOnClickListener(onClickMenuBottom);
		menu_bottom_systemtrade_t.setOnClickListener(onClickMenuBottom);
		menu_bottom_news_t.setOnClickListener(onClickMenuBottom);
		menu_bottom_profile_t.setOnClickListener(onClickMenuBottom);
	}

	// ***************** onClick menu bottom ******************

	boolean ckShowSystemtrade = false;
	boolean ckShowHot = false;

	public static FragmentManager fragmentManager;
	private OnClickListener onClickMenuBottom = new OnClickListener() {
		@Override
		public void onClick(final View v) {
			// String tag = v.getTag().toString();
			setMenuBg(); // menu bg default

			switch (v.getId()) {
			// ********** menu 0
			case R.id.menu_bottom_home_t:
				ckShowSystemtrade = false;
				ckShowHot = false;
				hideMenuPopUp();

				img_bottom_home_t
						.setBackgroundResource(R.drawable.menu_bottom_home_active);
				tv_bottom_home_t.setTextColor(getResources().getColor(
						R.color.c_success));

				FragmentChangeActivity.id_websocket = 6;
				pagerDetail = "watchlist";

				switchContent(new PagerWatchList());

				// startActivity(new Intent(act,UiWebviewTest.class));

				break;
			case R.id.menu_bottom_smartport_t:
				ckShowSystemtrade = false;
				ckShowHot = false;
				hideMenuPopUp();

				img_bottom_smartport_t
						.setBackgroundResource(R.drawable.menu_bottom_smartport_active);
				tv_bottom_smartport_t.setTextColor(getResources().getColor(
						R.color.c_success));

				switchContent(new PagerSmartPortfolio());
				break;
			case R.id.menu_bottom_systemtrade_t:
				if (ckShowSystemtrade) {
					ckShowSystemtrade = false;
					hideMenuSysTemtrade();

					img_bottom_systemtrade_cdc
							.setBackgroundResource(R.drawable.menu_bottom_systemtrade_cdc_active);
					tv_bottom_systemtrade_cdc.setTextColor(getResources()
							.getColor(R.color.c_success));
					switchContent(new PagerSystemTradeCdc());
				} else {
					ckShowSystemtrade = true;
					showMenuSysTemtrade();
				}

				img_bottom_systemtrade_t
						.setBackgroundResource(R.drawable.menu_bottom_systemtrade_active);
				tv_bottom_systemtrade_t.setTextColor(getResources().getColor(
						R.color.c_success));

				pagerDetail = "systemtrade";
				// switchContent(new PagerSystemTrade());
				break;
			case R.id.menu_bottom_news_t:
				if (ckShowHot) {
					ckShowHot = false;
					hideMenuHot();

					img_bottom_hot_hot
							.setBackgroundResource(R.drawable.menu_bottom_heat_active);
					tv_bottom_hot_hot.setTextColor(getResources().getColor(
							R.color.c_success));
					switchContent(new PagerHot_WebView());
				} else {
					ckShowHot = true;
					showMenuHot();
				}

				img_bottom_news_t
						.setBackgroundResource(R.drawable.menu_bottom_heat_active);
				tv_bottom_news_t.setTextColor(getResources().getColor(
						R.color.c_success));

				// switchContent(new PagerHot_News_WebView());
				break;
			case R.id.menu_bottom_profile_t:
				ckShowSystemtrade = false;
				ckShowHot = false;
				hideMenuPopUp();

				img_bottom_profile_t
						.setBackgroundResource(R.drawable.menu_bottom_profile_active);
				tv_bottom_profile_t.setTextColor(getResources().getColor(
						R.color.c_success));

				if (SplashScreen.userModel.user_id != "") {
					switchContent(new PagerProfile());
				} else {
					// onFacebookClick();
					loginDialog.show();
				}
				break;
			default:
				break;
			}
		}
	};

	// ***************** menu background ******************
	public void setMenuBg() {
		img_bottom_home_t.setBackgroundResource(R.drawable.menu_bottom_home);
		img_bottom_smartport_t
				.setBackgroundResource(R.drawable.menu_bottom_smartport);
		img_bottom_systemtrade_t
				.setBackgroundResource(R.drawable.menu_bottom_systemtrade);
		img_bottom_news_t.setBackgroundResource(R.drawable.menu_bottom_heat);
		img_bottom_profile_t
				.setBackgroundResource(R.drawable.menu_bottom_profile);

		tv_bottom_home_t.setTextColor(getResources()
				.getColor(R.color.c_content));
		tv_bottom_smartport_t.setTextColor(getResources().getColor(
				R.color.c_content));
		tv_bottom_systemtrade_t.setTextColor(getResources().getColor(
				R.color.c_content));
		tv_bottom_news_t.setTextColor(getResources()
				.getColor(R.color.c_content));
		tv_bottom_profile_t.setTextColor(getResources().getColor(
				R.color.c_content));
	}

	// ************ menu bottom popup *******
	LinearLayout menu_bottom_systemtrade_mutualfund,
			menu_bottom_systemtrade_cdc, menu_bottom_systemtrade_breakout,
			menu_bottom_systemtrade_esu, menu_bottom_systemtrade_dusit,
			menu_bottom_systemtrade_setalert;
	LinearLayout menu_bottom_hot_hot, menu_bottom_hot_game,
			menu_bottom_hot_heapmap;

	ImageView img_bottom_systemtrade_mutualfund, img_bottom_systemtrade_cdc,
			img_bottom_systemtrade_breakout, img_bottom_systemtrade_esu,
			img_bottom_systemtrade_dusit, img_bottom_systemtrade_setalert;
	ImageView img_bottom_hot_hot, img_bottom_hot_game, img_bottom_hot_heapmap;

	TextView tv_bottom_systemtrade_mutualfund, tv_bottom_systemtrade_cdc,
			tv_bottom_systemtrade_breakout, tv_bottom_systemtrade_esu,
			tv_bottom_systemtrade_dusit, tv_bottom_systemtrade_setalert;
	TextView tv_bottom_hot_hot, tv_bottom_hot_game, tv_bottom_hot_heatmap;

	private Animation animShow, animHide;
	SlidingPanel popup_window_systemtrade, popup_window_hot;
	LinearLayout li_menu_hide;

	// http://bidschart.com/game?user_id=104

	// ***************** tab menu bottom ******************
	public void initMenuBottomPopup() {
		li_menu_hide = (LinearLayout) findViewById(R.id.li_menu_hide);

		popup_window_systemtrade = (SlidingPanel) findViewById(R.id.popup_window_systemtrade);
		popup_window_hot = (SlidingPanel) findViewById(R.id.popup_window_hot);

		// Hide the popup initially.....
		popup_window_systemtrade.setVisibility(View.GONE);
		popup_window_hot.setVisibility(View.GONE);

		animShow = AnimationUtils.loadAnimation(this, R.animator.menu_show);
		animHide = AnimationUtils.loadAnimation(this, R.animator.menu_hide);

		// ======= show hide menu bottom
		// ---- linear
		menu_bottom_systemtrade_mutualfund = (LinearLayout) findViewById(R.id.menu_bottom_systemtrade_mutualfund);
		menu_bottom_systemtrade_cdc = (LinearLayout) findViewById(R.id.menu_bottom_systemtrade_cdc);
		menu_bottom_systemtrade_breakout = (LinearLayout) findViewById(R.id.menu_bottom_systemtrade_breakout);
		menu_bottom_systemtrade_esu = (LinearLayout) findViewById(R.id.menu_bottom_systemtrade_esu);
		menu_bottom_systemtrade_dusit = (LinearLayout) findViewById(R.id.menu_bottom_systemtrade_dusit);
		menu_bottom_systemtrade_setalert = (LinearLayout) findViewById(R.id.menu_bottom_systemtrade_setalert);

		menu_bottom_hot_hot = (LinearLayout) findViewById(R.id.menu_bottom_hot_hot);
		menu_bottom_hot_game = (LinearLayout) findViewById(R.id.menu_bottom_hot_game);
		menu_bottom_hot_heapmap = (LinearLayout) findViewById(R.id.menu_bottom_hot_heapmap);
		// ---- img
		img_bottom_systemtrade_mutualfund = (ImageView) findViewById(R.id.img_bottom_systemtrade_mutualfund);
		img_bottom_systemtrade_cdc = (ImageView) findViewById(R.id.img_bottom_systemtrade_cdc);
		img_bottom_systemtrade_breakout = (ImageView) findViewById(R.id.img_bottom_systemtrade_breakout);
		img_bottom_systemtrade_esu = (ImageView) findViewById(R.id.img_bottom_systemtrade_esu);
		img_bottom_systemtrade_dusit = (ImageView) findViewById(R.id.img_bottom_systemtrade_dusit);
		img_bottom_systemtrade_setalert = (ImageView) findViewById(R.id.img_bottom_systemtrade_setalert);

		img_bottom_hot_hot = (ImageView) findViewById(R.id.img_bottom_hot_hot);
		img_bottom_hot_game = (ImageView) findViewById(R.id.img_bottom_hot_game);
		img_bottom_hot_heapmap = (ImageView) findViewById(R.id.img_bottom_hot_heapmap);
		// ---- textview
		tv_bottom_systemtrade_mutualfund = (TextView) findViewById(R.id.tv_bottom_systemtrade_mutualfund);
		tv_bottom_systemtrade_cdc = (TextView) findViewById(R.id.tv_bottom_systemtrade_cdc);
		tv_bottom_systemtrade_breakout = (TextView) findViewById(R.id.tv_bottom_systemtrade_breakout);
		tv_bottom_systemtrade_esu = (TextView) findViewById(R.id.tv_bottom_systemtrade_esu);
		tv_bottom_systemtrade_dusit = (TextView) findViewById(R.id.tv_bottom_systemtrade_dusit);
		tv_bottom_systemtrade_setalert = (TextView) findViewById(R.id.tv_bottom_systemtrade_setalert);

		tv_bottom_hot_hot = (TextView) findViewById(R.id.tv_bottom_hot_hot);
		tv_bottom_hot_game = (TextView) findViewById(R.id.tv_bottom_hot_game);
		tv_bottom_hot_heatmap = (TextView) findViewById(R.id.tv_bottom_hot_heatmap);

		menu_bottom_systemtrade_mutualfund
				.setOnClickListener(onClickMenuBottomPopUp);
		menu_bottom_systemtrade_cdc.setOnClickListener(onClickMenuBottomPopUp);
		menu_bottom_systemtrade_breakout
				.setOnClickListener(onClickMenuBottomPopUp);
		menu_bottom_systemtrade_esu.setOnClickListener(onClickMenuBottomPopUp);
		menu_bottom_systemtrade_dusit
				.setOnClickListener(onClickMenuBottomPopUp);
		menu_bottom_systemtrade_setalert
				.setOnClickListener(onClickMenuBottomPopUp);

		menu_bottom_hot_hot.setOnClickListener(onClickMenuBottomPopUp);
		menu_bottom_hot_game.setOnClickListener(onClickMenuBottomPopUp);
		menu_bottom_hot_heapmap.setOnClickListener(onClickMenuBottomPopUp);

	}

	// ***************** onClick menu bottom pupup ******************
	private OnClickListener onClickMenuBottomPopUp = new OnClickListener() {
		@Override
		public void onClick(final View v) {
			// String tag = v.getTag().toString();
			setMenuBgPopUp(); // menu bg default

			switch (v.getId()) {
			case R.id.menu_bottom_systemtrade_mutualfund:
				ckShowSystemtrade = false;
				hideMenuSysTemtrade();

				img_bottom_systemtrade_mutualfund
						.setBackgroundResource(R.drawable.menu_bottom_systemtrade_mutualfund_active);
				tv_bottom_systemtrade_mutualfund.setTextColor(getResources()
						.getColor(R.color.c_success));

				switchContent(new PagerSystemTradeMutualFund());
				break;
			case R.id.menu_bottom_systemtrade_cdc:
				ckShowSystemtrade = false;
				hideMenuSysTemtrade();

				img_bottom_systemtrade_cdc
						.setBackgroundResource(R.drawable.menu_bottom_systemtrade_cdc_active);
				tv_bottom_systemtrade_cdc.setTextColor(getResources().getColor(
						R.color.c_success));

				switchContent(new PagerSystemTradeCdc());
				break;
			case R.id.menu_bottom_systemtrade_breakout:
				ckShowSystemtrade = false;
				hideMenuSysTemtrade();

				img_bottom_systemtrade_breakout
						.setBackgroundResource(R.drawable.menu_bottom_systemtrade_breakout_active);
				tv_bottom_systemtrade_breakout.setTextColor(getResources()
						.getColor(R.color.c_success));

				switchContent(new PagerSystemTradeBreakOut());
				break;
			case R.id.menu_bottom_systemtrade_esu:
				ckShowSystemtrade = false;
				hideMenuSysTemtrade();

				img_bottom_systemtrade_esu
						.setBackgroundResource(R.drawable.menu_bottom_systemtrade_esu_active);
				tv_bottom_systemtrade_esu.setTextColor(getResources().getColor(
						R.color.c_success));

				switchContent(new PagerSystemTradeEsu());
				break;
			case R.id.menu_bottom_systemtrade_dusit:
				ckShowSystemtrade = false;
				hideMenuSysTemtrade();

				img_bottom_systemtrade_dusit
						.setBackgroundResource(R.drawable.menu_bottom_systemtrade_dusit_active);
				tv_bottom_systemtrade_dusit.setTextColor(getResources()
						.getColor(R.color.c_success));

				switchContent(new PagerSystemTradeDusit());
				break;

			case R.id.menu_bottom_systemtrade_setalert:
				ckShowSystemtrade = false;
				hideMenuSysTemtrade();

				img_bottom_systemtrade_setalert
						.setBackgroundResource(R.drawable.menu_bottom_setalert_active);
				tv_bottom_systemtrade_setalert.setTextColor(getResources()
						.getColor(R.color.c_success));

				switchContent(new PagerSetAlert());
				break;
			case R.id.menu_bottom_hot_hot:
				ckShowHot = false;
				hotPageNum = 0;
				hideMenuHot();

				img_bottom_hot_hot
						.setBackgroundResource(R.drawable.menu_bottom_heat_active);
				tv_bottom_hot_hot.setTextColor(getResources().getColor(
						R.color.c_success));

				switchContent(new PagerHot_WebView());
				// switchContent(new PagerHot_News_WebView());
				break;
			case R.id.menu_bottom_hot_game:
				ckShowHot = false;
				hotPageNum = 1;
				hideMenuHot();

				img_bottom_hot_game
						.setBackgroundResource(R.drawable.menu_bottom_hot_gamebid_active);
				tv_bottom_hot_game.setTextColor(getResources().getColor(
						R.color.c_success));

				switchContent(new PagerHot_WebView());
				// switchContent(new PagerHot_Game_WebView());
				break;
			case R.id.menu_bottom_hot_heapmap:
				ckShowHot = false;
				hotPageNum = 2;
				hideMenuHot();

				img_bottom_hot_heapmap
						.setBackgroundResource(R.drawable.menu_bottom_hot_heapmap_active);
				tv_bottom_hot_heatmap.setTextColor(getResources().getColor(
						R.color.c_success));

				switchContent(new PagerHot_WebView());
				// switchContent(new PagerHot_Heapmap_WebView());
				break;
			default:
				break;
			}
		}
	};

	// ***************** menu background popup ******************
	public void setMenuBgPopUp() {
		img_bottom_systemtrade_mutualfund
				.setBackgroundResource(R.drawable.menu_bottom_systemtrade_mutualfund);
		img_bottom_systemtrade_cdc
				.setBackgroundResource(R.drawable.menu_bottom_systemtrade_cdc);
		img_bottom_systemtrade_breakout
				.setBackgroundResource(R.drawable.menu_bottom_systemtrade_breakout);
		img_bottom_systemtrade_esu
				.setBackgroundResource(R.drawable.menu_bottom_systemtrade_esu);
		img_bottom_systemtrade_dusit
				.setBackgroundResource(R.drawable.menu_bottom_systemtrade_dusit);
		img_bottom_systemtrade_setalert
				.setBackgroundResource(R.drawable.menu_bottom_setalert);

		img_bottom_hot_hot.setBackgroundResource(R.drawable.menu_bottom_heat);
		img_bottom_hot_game
				.setBackgroundResource(R.drawable.menu_bottom_hot_gamebid);
		img_bottom_hot_heapmap
				.setBackgroundResource(R.drawable.menu_bottom_hot_heapmap);

		tv_bottom_systemtrade_mutualfund.setTextColor(getResources().getColor(
				R.color.c_content));
		tv_bottom_systemtrade_cdc.setTextColor(getResources().getColor(
				R.color.c_content));
		tv_bottom_systemtrade_breakout.setTextColor(getResources().getColor(
				R.color.c_content));
		tv_bottom_systemtrade_esu.setTextColor(getResources().getColor(
				R.color.c_content));
		tv_bottom_systemtrade_dusit.setTextColor(getResources().getColor(
				R.color.c_content));
		tv_bottom_systemtrade_setalert.setTextColor(getResources().getColor(
				R.color.c_content));

		tv_bottom_hot_hot.setTextColor(getResources().getColor(
				R.color.c_content));
		tv_bottom_hot_game.setTextColor(getResources().getColor(
				R.color.c_content));
		tv_bottom_hot_heatmap.setTextColor(getResources().getColor(
				R.color.c_content));
	}

	// ======= menu systemtrade show hide
	public void showMenuSysTemtrade() {
		popup_window_systemtrade.startAnimation(animShow);
		popup_window_systemtrade.setVisibility(View.VISIBLE);

		popup_window_hot.setVisibility(View.GONE);
		// li_menu_hide.setVisibility(View.GONE);
	}

	public void hideMenuSysTemtrade() {
		popup_window_systemtrade.startAnimation(animHide);
		popup_window_systemtrade.setVisibility(View.GONE);
		// li_menu_hide.setVisibility(View.VISIBLE);
	}

	// ======= menu systemtrade show hide
	public void showMenuHot() {
		popup_window_hot.startAnimation(animShow);
		popup_window_hot.setVisibility(View.VISIBLE);

		popup_window_systemtrade.setVisibility(View.GONE);
		// li_menu_hide.setVisibility(View.GONE);
	}

	public void hideMenuHot() {
		popup_window_hot.startAnimation(animHide);
		popup_window_hot.setVisibility(View.GONE);
		// li_menu_hide.setVisibility(View.VISIBLE);
	}

	// ======= menu systemtrade shide
	public void hideMenuPopUp() {
		popup_window_systemtrade.setVisibility(View.GONE);
		popup_window_hot.setVisibility(View.GONE);
	}

	// {Session state:CLOSED, token:{AccessToken token:ACCESS_TOKEN_REMOVED
	// permissions:[]}, appId:802896589752581}

	// ******************* set login ***************
	public static Handler handlerSetProfile = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				// tv_bottom_home_t.setText("Home");
				// tv_bottom_home.setText("Home");
				break;
			case 1:
				// tv_bottom_home_t.setText("WatchList");
				// tv_bottom_home.setText("WatchList");

				loginDialog.dissmiss();
				break;
			default:
				break;
			}
		}
	};

	// ******************* set call watchlist ***************
	public static Handler handlerWatchlist = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:

				break;
			case 1:

				break;
			default:
				break;
			}
		}
	};

	// set data user login
	public static final int loginStateF = 0;
	public static final int loginStateT = 1;
	public static Handler handlerSetUser = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			Log.v("handlerSetUser", "" + msg);

			switch (msg.what) {
			case loginStateT: // login suceess
				// imageLoader.displayImage(userModel.profileImagePath,
				// img_profile);
				// tv_username.setText("" + userModel.userName);
				// LoginDialog2.dissmiss();
				// mDrawerLayout.closeDrawer(mDrawerSlideMenu);
				break;
			case loginStateF:
				// imageLoader.displayImage(userModel.profileImagePath,
				// img_profile);
				// LoginDialog2.dissmiss();
				// tv_username.setText("");
				// mDrawerLayout.closeDrawer(mDrawerSlideMenu);
				break;
			default:
				break;
			}
		}
	};

	// ************* logout ***************
	public static Handler handlerLogout = new Handler() {
		@Override
		public void handleMessage(Message msg) {

		}
	};

	@Override
	public void onDone(UserModel model) {
	}

	@Override
	public void onFacebookClick() {
		// Intent it = new Intent(act, FacebookSplashScreen2.class);
		Intent it = new Intent(act, FacebookLoginActivity.class);
		startActivityForResult(it, SplashScreen.LOGIN_FACEBOOK_REQUEST);
		loginDialog.dissmiss();
	}

	@Override
	public void onPlusClick() {
		// Intent it = new Intent(act, PlusSplashScreen.class);
		// startActivityForResult(it, LOGIN_FACEBOOK_REQUEST);
	}

	@Override
	public void onTwitterClick() {
		Intent it = new Intent(act, TwitterLoginActivity.class);
		startActivityForResult(it, SplashScreen.LOGIN_FACEBOOK_REQUEST);
	}

	/**
	 * Slider menu item click listener
	 * */

	public void switchContent(Fragment fragment) {
		mContent = fragment;
		// getSupportFragmentManager().beginTransaction()
		// .replace(R.id.li_content_frame, fragment).commit();

		// getSupportFragmentManager().beginTransaction().remove(new
		// PagerDefault()).commit();
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.li_content_frame, fragment).commit();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return null;
	}

	// =========== Billing ============
	// http://www.artit-k.com/in-app-billing-for-android-part-3/
	// https://play.google.com/apps/publish/?dev_acc=17541615962600910624#ProfilePlace
	// https://play.google.com/apps/publish/?dev_acc=17541615962600910624#InAppPlace:p=com.app.bids

	public static JSONObject contentGetPostSelect = null;
	public static JSONArray contentGetSymbolSelect = null;
	public static Dialog dialogBillingLoading;
	private static String tag;
	private static IabHelper mHelper;

	public static String base64PublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAmj0U7822axgGTRf1MOWf6YAo9/ADndVnR3J1nUuK5+73WMDpPNVRpS31nv5HqtC+pG0fXea/yivXTRRTbjXx0CjoQFnL1wYjJ4ZMMySWUkz1iH7y+3krf5KbJJ7CwV+lST3fkJbFwv67Ha2HJkyHRHKRlNnB1tZ61GZOb1NMubZNuogwMXatUrsiyvUNGUQQBXlyPVqL0fAweecrGxx+i1OfWFsqbAM75w6hjuJ/d7axGJwdpMzi45GP5NxQ3mFhoVRRf7LLPt+gGJQorAA2ye+NvTIVnefMp2nzgivhWjFj6PITQF2rZkbQUpxqWOPm9RqadGP0n/7hX/jvnlyZtQIDAQAB";

	public static boolean isSetup;
	// ProductID // bids_purchase_0.99, วิเคราะห์หุ้น BIDs , สนับสนุนทีมพัฒนา
	// $0.99
	public static String productID = "bids_purchase_0.99";
	// public static final String productID = "android.test.purchased"; // Test
	// Product

	// Purchase
	private static Purchase purchaseOwned;

	public static void initBilling() {
		mHelper = new IabHelper(act, base64PublicKey);
		tag = "in_app_billing_test";
		mHelper.enableDebugLogging(true, tag);

		// --------- dialogBillingLoading
		dialogBillingLoading = new Dialog(act);
		dialogBillingLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogBillingLoading.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialogBillingLoading.setContentView(R.layout.progress_bar);
		dialogBillingLoading.setCancelable(false);
		dialogBillingLoading.setCanceledOnTouchOutside(false);

		try {
			mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
				@Override
				public void onIabSetupFinished(IabResult result) {
					boolean blnSuccess = result.isSuccess();
					boolean blnFail = result.isFailure();

					isSetup = blnSuccess;

					Log.v(tag, "mHelper.startSetup() ...");
					Log.v(tag,
							"	- blnSuccess return "
									+ String.valueOf(blnSuccess));
					Log.v(tag, "	- blnFail return " + String.valueOf(blnFail));
					Log.v(tag, "initBilling isSetup " + isSetup);

					sendQuery();
				}
			});
		} catch (Exception e) {
			e.printStackTrace();

			isSetup = false;
			// Toast.makeText(act, "mHelper.startSetup() - fail!",
			// Toast.LENGTH_SHORT).show();
			Log.v(tag, "mHelper.startSetup() - fail!");
		}
	}

	public static void sendQuery() {

		Log.v("sendQuery isSetup", "" + isSetup);

		if (!isSetup)
			return;

		mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
			@Override
			public void onQueryInventoryFinished(IabResult result, Inventory inv) {
				boolean blnSuccess = result.isSuccess();
				boolean blnFail = result.isFailure();

				if (!blnSuccess)
					return;

				if (!inv.hasPurchase(productID))
					return;

				purchaseOwned = inv.getPurchase(productID);

				if (!inv.hasDetails(productID))
					return;

				SkuDetails skuDetails = inv.getSkuDetails(productID);
			}
		});
	}

	public static boolean ckSuccessBilling;

	public static void sendPurchase() {
		ckSuccessBilling = false;
		if (!isSetup)
			return;

		mHelper.launchPurchaseFlow(act, productID, 1001,
				new IabHelper.OnIabPurchaseFinishedListener() {
					@Override
					public void onIabPurchaseFinished(IabResult result,
							Purchase info) {
						boolean blnSuccess = result.isSuccess();
						boolean blnFail = result.isFailure();

						ckSuccessBilling = blnSuccess;

						Log.v("sendPurchase blnSuccess", "" + blnSuccess);

						if (!blnSuccess)
							return;

						purchaseOwned = info;

					}
				}, "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
	}

	public static void sendConsume() {
		if (!isSetup)
			return;
		if (purchaseOwned == null)
			return;

		mHelper.consumeAsync(purchaseOwned,
				new IabHelper.OnConsumeFinishedListener() {
					@Override
					public void onConsumeFinished(Purchase purchase,
							IabResult result) {
						boolean blnSuccess = result.isSuccess();
						boolean blnFail = result.isFailure();

						if (!blnSuccess)
							return;

						purchaseOwned = null;

					}
				});
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (isSetup) {
			boolean blnResult = mHelper.handleActivityResult(requestCode,
					resultCode, data);

			// Toast.makeText(
			// act,
			// "onActivityResult() - mHelper.handleActivityResult() = "
			// + blnResult, Toast.LENGTH_SHORT).show();
			Log.d(tag, "onActivityResult() - mHelper.handleActivityResult() = "
					+ blnResult);

			Log.v("onActivityResult() blnResult", "" + blnResult);

			// if (blnResult)
			// sendBilling();

			if (ckSuccessBilling) {
				sendBilling();
			}

			if (requestCode == 1001) {
				int responseCode = data.getIntExtra("RESPONSE_CODE", 0);
				String purchaseData = data
						.getStringExtra("INAPP_PURCHASE_DATA");
				String dataSignature = data
						.getStringExtra("INAPP_DATA_SIGNATURE");

				if (resultCode == RESULT_OK) {
					try {
						JSONObject jo = new JSONObject(purchaseData);
						String sku = jo.getString("productId");
						Log.v("You have bought the ", "" + sku);
					} catch (JSONException e) {
						Log.v("You have bought the ",
								"Failed to parse purchase data.");
						e.printStackTrace();
					}
				}
			}

			return;
		}

		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	protected void onDestroy() {
		if (isSetup)
			mHelper.dispose();
		mHelper = null;

		super.onDestroy();
	}

	// ============== send billing =============

	public static JSONArray contentSetPayPromotion = null;

	private void sendBilling() {
		setPayPromotion resp = new setPayPromotion();
		resp.execute();
	}

	public class setPayPromotion extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;
		// ======= json ========
		private JSONObject jsonBilling;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialogBillingLoading.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// ======= url ========

			// http://bidschart.com/service/v2/setPayPromotion?user_id=10&packet_id=free&device_id=d983a4d5757d8aca&platform=android

			// http://bidschart.com/service/v2/setPayPromotion?user_id=38&packet_id=free&device_id=web&platform=web
			// method::post
			// packet_id :: premium7d , premium1m , premium6m , premium12m ,
			// free
			// platform :: web , android , ios

			// -----------------------------------------
			String url = SplashScreen.url_bidschart
					+ "/service/v2/setPayPromotion";

			String json = "";
			InputStream inputStream = null;
			String result = "";

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();

				jsonObject.accumulate("user_id",
						SplashScreen.contentGetUserById.getString("user_id"));
				jsonObject.accumulate("packet_id", PagerProfile.strPacketId);
				jsonObject.accumulate("device_id", SplashScreen.deviceId);
				jsonObject.accumulate("platform", "android");

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

				Log.v("send billing result", "" + result);

				// {"status":"ok","message":"Get data Success.","dataAll":{"date_start":"2016-05-18 16:52:20","date_end":"2016-05-18 16:52:20","date_count":0,"packet_id":"free","packet_name":"free","packet_price":0}}

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

			if (connectionError == false) {
				// finish();
			} else {
				Toast.makeText(getApplicationContext(), "connecttion Error!!",
						0).show();
			}
			dialogBillingLoading.dismiss();
		}
	}

	// ============== Load Data prifile =============
	private void initLoadProfile() {
		loadData resp = new loadData();
		resp.execute();
	}

	public class loadData extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;

		private JSONObject jsonGetUserById;
		private JSONObject jsonGetFollowingByUserId;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();

			// http://www.bidschart.com/service/getUserById?user_id=104
			String url_GetUserById = SplashScreen.url_bidschart
					+ "/service/getUserById?user_id="
					+ SplashScreen.userModel.user_id;

			try {
				jsonGetUserById = ReadJson
						.readJsonObjectFromUrl(url_GetUserById);
			} catch (IOException e1) {
				connectionError = true;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (jsonGetUserById != null) {
					try {
						SplashScreen.contentGetUserById = jsonGetUserById
								.getJSONObject("dataAll");

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.v("json jsonGetMyIdeas null", "jsonGetMyIdeas null");
				}
			} else {
			}
		}
	}

	// ************* start ui detail ***************
	public static void startUiDetail() {
		act.startActivity(new Intent(act, UiWatchlistDetail.class));
	}
	
	public static void startUiDetailMutual() {
		act.startActivity(new Intent(act, UiMutualfundDetail.class));
	}

	@Override
	protected void onResume() {
		super.onResume();
		// Check device for Play Services APK.
		// checkPlayServices();
	}

	// ============== Notification =========================
	private boolean checkPlayServices() {
		int resultCode = GooglePlayServicesUtil
				.isGooglePlayServicesAvailable(this);
		if (resultCode != ConnectionResult.SUCCESS) {
			if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
				GooglePlayServicesUtil.getErrorDialog(resultCode, this,
						PLAY_SERVICES_RESOLUTION_REQUEST).show();
			} else {
				Log.i(TAG, "This device is not supported.");
				finish();
			}
			return false;
		}
		return true;
	}

	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getGcmPreferences(context);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(PROPERTY_REG_ID, regId);
		editor.putInt(PROPERTY_APP_VERSION, appVersion);
		editor.commit();
	}

	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getGcmPreferences(context);
		String registrationId = prefs.getString(PROPERTY_REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		// Check if app was updated; if so, it must clear the registration ID
		// since the existing regID is not guaranteed to work with the new
		// app version.
		int registeredVersion = prefs.getInt(PROPERTY_APP_VERSION,
				Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	public static String result = "";

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(act);
					}
					regid = gcm.register(SENDER_ID);
					msg = "Device registered, registration ID=" + regid;

					// Log.v("regid msg", "" + msg);

					// google develop key api ดูใน google console

					String url = SplashScreen.url_bidschart
							+ "/bidsMaster/updateDeviceId";

					InputStream inputStream = null;

					HttpClient httpclient = new DefaultHttpClient();
					HttpPost httppost = new HttpPost(url);

					// 3. build jsonObject
					List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
							2);

					nameValuePairs.add(new BasicNameValuePair("user_id ",
							SplashScreen.userModel.user_id));
					nameValuePairs
							.add(new BasicNameValuePair("platform ", "1"));
					nameValuePairs.add(new BasicNameValuePair("device_id",
							regid));

					httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
							"UTF-8"));

					// 8. Execute POST request to the given URL
					HttpResponse httpResponse = httpclient.execute(httppost);

					// 9. receive response as inputStream
					inputStream = httpResponse.getEntity().getContent();

					// 10. convert inputstream to string
					if (inputStream != null)
						result = AFunctionOther
								.convertInputStreamToString(inputStream);
					else
						result = "fail";

					// Persist the regID - no need to register again.
					storeRegistrationId(act, regid);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				// Log.v("regid", "" + msg);
				// Device registered, registration
				// ID=APA91bGCTIwCM6OhhBR4k_W72MIkolXXmYpVmZWdJrGGPrdptLd8H3UAsFRjJjBJRDrfg6_RSw-Eyvvz1g7CPmnXap__ZTLT2HD-Dz68pPYFh62KIGfQ0fAKfAA2fIdrk2rtHjvOjyfW
			}
		}.execute(null, null, null);
	}

	/**
	 * @return Application's version code from the {@code PackageManager}.
	 */
	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			// should never happen
			throw new RuntimeException("Could not get package name: " + e);
		}
	}

	private SharedPreferences getGcmPreferences(Context context) {
		return getSharedPreferences(
				FragmentChangeActivity.class.getSimpleName(),
				Context.MODE_PRIVATE);
	}

	private void sendRegistrationIdToBackend() {
		// Your implementation here.
	}

	private static final int TIME_INTERVAL = 2000;
	private long mBackPressed;

	// [FragmentWatchList{3cf1ff43}, FragmentNews{48fdd14 #1 id=0x7f050105},
	// FragmentNews{22024237 #4 id=0x7f050105}, FragmentNews{2d6ccbbe #5
	// id=0x7f050105}]

	public void onBackPressed() {
		// Log.v("Stack00 size", "" + fragmentStack.size());
		// Log.v("Stack00", "" + fragmentStack);
		//
		// if (fragmentStack.size() > 1) {
		// FragmentTransaction ft = fragmentManager.beginTransaction();
		// fragmentStack.lastElement().onPause();
		// ft.remove(fragmentStack.pop());
		// fragmentStack.lastElement().onResume();
		// ft.show(fragmentStack.lastElement());
		// ft.commit();
		// fragmentStack.clear();
		// } else {

		if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
			finish();
		} else {
			Toast.makeText(getBaseContext(),
					"กดปุ่มกลับอีกครั้งเพื่อปิดโปรแกรม", Toast.LENGTH_SHORT)
					.show();
		}

		mBackPressed = System.currentTimeMillis();

		// }
		// Log.v("Stack11 size", "" + fragmentStack.size());
		// Log.v("Stack11", "" + fragmentStack);

	}

}
