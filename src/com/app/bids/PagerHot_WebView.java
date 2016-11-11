package com.app.bids;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;
import com.app.bids.PagerHomeAll.loadData;
import com.app.bids.PagerSystemTrade.loadDataSlidingMarquee;

import android.R.anim;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

public class PagerHot_WebView extends Fragment {

	static Context context;
	public static View rootView;
	Dialog dialogLoading;

	// --------- google analytics 
//	private Tracker mTracker;
//    String nameTracker = new String("Hot");
    
	// list contains fragments to instantiate in the viewpager
	List<Fragment> fragmentMain = new Vector<Fragment>();
	private PagerAdapter mPagerAdapter;
	// view pager
	private ViewPager mPager;

	private OnPageListener pageListener;
	public int selectTitle = 0;

	// carousel
	public static MyPagerAdapter adapterCorousel;
	public static ViewPager pagerCorousel;

	public static View view;

	public interface OnPageListener {
		public void onPage1(String s);
	}

	// ===== callback =====
	private ValueCallback<Uri> mUploadMessage;
	private final static int FILECHOOSER_RESULTCODE = 1;

	// onCreateView
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		// fragment not when container null
		if (container == null) {
			return null;
		}

		// inflate view from layot
		rootView = inflater.inflate(R.layout.pager_hot_webview,
				container, false);

		// --------- google analytics
//		GoogleAnalyticsApp application = (GoogleAnalyticsApp) getActivity().getApplication();
//		mTracker = application.getDefaultTracker();

		dialogLoading = new Dialog(context);
		dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogLoading.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialogLoading.setContentView(R.layout.progress_bar);
		dialogLoading.setCancelable(false);
		// dialogLoading.setCanceledOnTouchOutside(false);

		if (FragmentChangeActivity.contentGetTxtSlidingMarquee != null) {
			initTxtSliding();
		} else {
			loadTxtSlidingMarquee(); // text sliding
		}

		initWebView(); // http://bidschart.com/mainMini

		return rootView;

	}

	@Override
	public void onResume() {
		super.onResume();
//		Log.v(nameTracker, "onResume onResume onResume");
		
//		mTracker.setScreenName(nameTracker);
//		mTracker.send(new HitBuilders.ScreenViewBuilder().build());
	}
	
	// ====================== Initial View ========================
	WebView webView;
	TextView tv_title;
	ImageView img_back, img_reload;
	String webViewUrl;
//	String webViewUrl = SplashScreen.url_bidschart + "/m/heat-map";

	private void initWebView() {
		// dialogLoading.show();

		webViewUrl = SplashScreen.url_bidschart + FragmentChangeActivity.hotPageUrl[FragmentChangeActivity.hotPageNum];
		Log.v("webView Url", ""+webViewUrl);

		// -------------- title
		tv_title = (TextView) rootView.findViewById(R.id.tv_title);
		img_back = (ImageView) rootView.findViewById(R.id.img_back);
		img_reload = (ImageView) rootView.findViewById(R.id.img_reload);

		if (FragmentChangeActivity.hotPageNum == 0) {
			tv_title.setText("Hot");
//			nameTracker = new String("Hot");
		} else if (FragmentChangeActivity.hotPageNum == 1) {
			tv_title.setText("Game");
//			nameTracker = new String("Game");
		} else if (FragmentChangeActivity.hotPageNum == 2) {
			tv_title.setText("Heat Map");
//			nameTracker = new String("Heat Map");
		}
		
		img_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (webView.canGoBack()) {
					webView.goBack();
				}
			}
		});

		img_reload.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				initWebView();
			}
		});

		// -------------- vebview
		java.util.Date date = new java.util.Date();
		long timestamp = date.getTime();
		
		webView = (WebView) rootView.findViewById(R.id.wv_webview);

		webView.getSettings().setLoadWithOverviewMode(true);
		webView.getSettings().setUseWideViewPort(true);
		webView.getSettings().setBuiltInZoomControls(true);
		webView.setInitialScale(100); // zoom is %
		WebSettings webSettings = webView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webView.loadUrl(webViewUrl);
		webView.setWebViewClient(new myWebClient());
	}

	public class myWebClient extends WebViewClient {
		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {

			view.loadUrl(url);
			return true;
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);

			if (FragmentChangeActivity.hotPageNum == 0) {
				tv_title.setText(view.getTitle());
			} else if (FragmentChangeActivity.hotPageNum == 1) {
				tv_title.setText("Game");
			} else if (FragmentChangeActivity.hotPageNum == 2) {
				tv_title.setText("Heat Map");
			}
			// dialogLoading.dismiss();
		}
	}

	// ***************** text sliding ******************
	public static void initTxtSliding() {
		String strSliding = "";
		com.app.custom.CustomTextViewSliding marque = (com.app.custom.CustomTextViewSliding) rootView
				.findViewById(R.id.tv_sliding_marquee);

		ImageView img_status_m = (ImageView) rootView
				.findViewById(R.id.img_status_m);

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

				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		marque.setText(Html.fromHtml(strSliding), TextView.BufferType.SPANNABLE);
		marque.setSelected(true);
	}

	// ***************** SlidingMarquee ******************
	public static void loadTxtSlidingMarquee() {
		loadDataSlidingMarquee resp = new loadDataSlidingMarquee();
		resp.execute();
	}

	public static class loadDataSlidingMarquee extends
			AsyncTask<Void, Void, Void> {

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

	// flipscreen not loading again
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
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