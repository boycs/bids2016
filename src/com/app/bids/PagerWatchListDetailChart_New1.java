package com.app.bids;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.app.bids.R;
import com.app.bids.FragmentChangeActivity.myWebClient;
import com.app.bids.colorpicker.ColorPickerAdapter;
import com.app.bids.colorpicker.ColorPickerDialog;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

@SuppressLint("NewApi")
public class PagerWatchListDetailChart_New1 extends Fragment {

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
				R.layout.pager_watchlist_detail_chart_new1, container, false);

		return rootView;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		initChartIq();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.v("check chart", "onDestroy");
		li_view_chart.removeView(FragmentChangeActivity.wv_chartiq);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.v("check chart", "onDestroyView");
		li_view_chart.removeView(FragmentChangeActivity.wv_chartiq);
	}

	// ====== init ChartIq ============
//	String wv_url = SplashScreen.url_bidschart + "/iq/stx-mobile-3.html";
	// http://bidschart.com/rework/ios/ptt
//	String wv_url = SplashScreen.url_bidschart + "/rework/ios/.set";
	
	public void initChartIq() {
//		wv_url = SplashScreen.url_bidschart + "/iq/stx-mobile-3.html#"
//				+ FragmentChangeActivity.strSymbolSelect;
		SplashScreen.wv_chartiq_url = SplashScreen.wv_chartiq_url + FragmentChangeActivity.strSymbolSelect;

		initWebView();
		
		Log.v("wv_url", ""+SplashScreen.wv_chartiq_url);
	}
	
	// ====== init Menu ============
	LinearLayout li_ciq_showview;

	// ------ show hide เมนู แสดง symbol
	public static void menuShowHide() {
		if (UiWatchlistDetail.orientation_landscape) {
			FragmentChangeActivity.wv_chartiq.loadUrl("javascript:(function () { "
					+ "mobileControl.showTitle();"
					+ "})()");
		} else {
			topbarHide();
			FragmentChangeActivity.wv_chartiq.loadUrl("javascript:(function () { "
					+ "mobileControl.hideTitle();"
					+ "})()");
		}
	}
	
	public static void topbarHide() {
		FragmentChangeActivity.wv_chartiq.loadUrl("javascript:(function () { "
				+ "mobileControl.hideNav();"
				+ "})()");
	}

	public static void topbarShow() {
		FragmentChangeActivity.wv_chartiq.loadUrl("javascript:(function () { "
				+ "mobileControl.showNav();"
				+ "})()");
	}

	// ====== init webview ============
	public static LinearLayout li_test_wv, li_view_chart;

	LinearLayout li_ciq_tool;
	boolean ckShowHideCiqTool;

	public void initWebView() {
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
		    WebView.setWebContentsDebuggingEnabled(true);
		}
		
		li_view_chart = (LinearLayout) rootView
				.findViewById(R.id.li_view_chart);

		li_view_chart.addView(FragmentChangeActivity.wv_chartiq);
		
		FragmentChangeActivity.wv_chartiq.loadUrl("javascript:(function () { "
				+ "mobileControl.changeSymbol('"
				+ FragmentChangeActivity.strSymbolSelect + "');" + "})()");
		
		//----------------
		topbarHide();
		menuShowHide();

//		try {
//			if (SplashScreen.contentGetUserById != null) {
//				if (!(SplashScreen.contentGetUserById.getString("package")
//						.equals("free"))) {
//					FragmentChangeActivity.wv_chartiq
//							.loadUrl("javascript:(function () { "
//									+ "mobileControl.enableRealtime();"
//									+ "})()");
//				} else {
//					FragmentChangeActivity.wv_chartiq
//							.loadUrl("javascript:(function () { "
//									+ "mobileControl.disableRealtime();"
//									+ "})()");
//				}
//			} else {
//				FragmentChangeActivity.wv_chartiq
//						.loadUrl("javascript:(function () { "
//								+ "mobileControl.disableRealtime();" + "})()");
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}

	}

	// ====== webview client ============
	public class myWebClient extends WebViewClient {

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);

		}
	}

	public static String AssetJSONFile(String filename, Context context)
			throws IOException {
		AssetManager manager = context.getAssets();
		InputStream file = manager.open(filename);
		byte[] formArray = new byte[file.available()];
		file.read(formArray);
		file.close();

		return new String(formArray);
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