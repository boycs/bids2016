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

public class PagerHot_Heatmap_WebView extends Fragment {

	static Context context;
	public static View rootView;
	Dialog dialogLoading;

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
		rootView = inflater.inflate(R.layout.pager_hot_heatmap_webview, container,
				false);

		dialogLoading = new Dialog(context);
		dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogLoading.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialogLoading.setContentView(R.layout.progress_bar);
		dialogLoading.setCancelable(false);
//		dialogLoading.setCanceledOnTouchOutside(false);

		initWebView(); // http://bidschart.com/mainMini

		return rootView;

	}

	// ====================== Initial View ========================
	WebView webView;
	ImageView img_back, img_reload;
	String webViewUrl = SplashScreen.url_bidschart+"/images/heatmap-phone.png";

	private void initWebView() {
//		dialogLoading.show();

		// -------------- title
		img_back = (ImageView) rootView.findViewById(R.id.img_back);
		img_reload = (ImageView) rootView.findViewById(R.id.img_reload);

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

//			dialogLoading.dismiss();
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