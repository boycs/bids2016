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

public class PagerWatchListDetailChart extends Fragment {

	static Context context;
	public static View rootView;

	// activity listener interface
	private OnPageListener pageListener;

	// FragmentChangeActivity.url_bidschart+"/watchlist?platform=mobile&user=10
	public static JSONArray contentGetWatchlists = null;

	// ---- show hide menu
	public static LinearLayout li_ciq_period, li_ciq_tabbottom;

	// public static WebView wv_chart;
	// public static WebView wv_test;
	// String wv_url = SplashScreen.url_bidschart+"/iq/stx-mobile-2.html#.SET";
	// String wv_url = SplashScreen.url_bidschart+"/iq/stx-mobile-3.html#.SET";
	
	// ------- dialog menu setting
	static DialogDetailChartiqSytle dialogChartiqSytle;
	static DialogDetailChartiqIndicator dialogChartiqIndicator;
	static DialogDetailChartiqDraw dialogChartiqDraw;
	static DialogDetailChartiqCompare dialogChartiqCompare;

	// munu chart
	Dialog dialogColorPicker; // color picker

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
				R.layout.pager_watchlist_detail_chart, container, false);

		dialogChartiqSytle = new DialogDetailChartiqSytle(context);
		dialogChartiqIndicator = new DialogDetailChartiqIndicator(context);
		dialogChartiqDraw = new DialogDetailChartiqDraw(context);
		dialogChartiqCompare = new DialogDetailChartiqCompare(context);
		
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

	private Double chartIqScreenHeight = 0.95; // 0.95
	private Double chartIqScreenWidth = 0.7;

	public void initChartIq() {
//		wv_url = SplashScreen.url_bidschart + "/iq/stx-mobile-3.html#"
//				+ FragmentChangeActivity.strSymbolSelect;

		initReadFileIndicator();
		initMenu(); // init menu
		initWebView();
	}

	// ========= read file indicator ========
	public static ArrayList<ChartIqGetIndicatorValueCatalog> list_getIndicatorValue = new ArrayList<ChartIqGetIndicatorValueCatalog>();
	public static JSONArray jsonIndicator = null;

	public void initReadFileIndicator() {
		try {
			String jsonArray = AssetJSONFile("indicator.json", context);

			jsonIndicator = new JSONArray(jsonArray);

			for (int i = 0; i < jsonIndicator.length(); i++) {
				JSONArray jsaIndexValue = jsonIndicator.getJSONObject(i)
						.getJSONArray("value");
				for (int j = 0; j < jsaIndexValue.length(); j++) {
					JSONObject jso = jsaIndexValue.getJSONObject(j);
					ChartIqGetIndicatorValueCatalog ct = new ChartIqGetIndicatorValueCatalog();
					ct.title = jso.getString("title");
					ct.key = jso.getString("key");
					list_getIndicatorValue.add(ct);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// ====== init Menu ============
	ImageView img_tool_showhide, img_tool_refresh_chart;
	TextView tv_ciq_symbol, tv_ciq_symbol_name_eng, tv_ciq_last_trade,
			tv_ciq_percenchange;

	TextView tv_ciq_1d, tv_ciq_1w, tv_ciq_1min, tv_ciq_5min, tv_ciq_15min,
			tv_ciq_30min, tv_ciq_60min, tv_ciq_120min;

	LinearLayout li_ciq_1view, li_ciq_2view, li_ciq_3view;

	TextView tv_ciq_style, tv_ciq_indicator, tv_ciq_compare, tv_ciq_draw;

	LinearLayout li_ciq_showview;


	public static int chartiq_icon_showhide_margin_top = 30; // แนวตั้ง
	public static int chartiq_icon_showhide_margin_top2 = 45; // แนวนอน
	
	public static void menuShow() {
		((LinearLayout) rootView.findViewById(R.id.li_ciq_period))
				.setVisibility(View.VISIBLE);
		((LinearLayout) rootView.findViewById(R.id.li_ciq_tabbottom))
				.setVisibility(View.VISIBLE);
		((ImageView) rootView.findViewById(R.id.img_tool_refresh_chart))
				.setVisibility(View.VISIBLE);
		
		if (UiWatchlistDetail.orientation_landscape) {
			((LinearLayout) rootView.findViewById(R.id.li_ciq_tool))
					.setPadding(0, chartiq_icon_showhide_margin_top2, 0,0);

			((ImageView) rootView.findViewById(R.id.img_tool_refresh_chart))
					.setVisibility(View.GONE);
		} else {
			((LinearLayout) rootView.findViewById(R.id.li_ciq_tool))
			.setPadding(0, chartiq_icon_showhide_margin_top, 0,0);
					
			((ImageView) rootView.findViewById(R.id.img_tool_refresh_chart))
					.setVisibility(View.VISIBLE);
		}
		// topbarShow();
	}

	public static void menuHide() {
		((LinearLayout) rootView.findViewById(R.id.li_ciq_period))
				.setVisibility(View.GONE);
		((LinearLayout) rootView.findViewById(R.id.li_ciq_tabbottom))
				.setVisibility(View.GONE);
		((ImageView) rootView.findViewById(R.id.img_tool_refresh_chart))
				.setVisibility(View.GONE);
		
		if (UiWatchlistDetail.orientation_landscape) {
			((LinearLayout) rootView.findViewById(R.id.li_ciq_tool))
					.setPadding(0, chartiq_icon_showhide_margin_top2, 0,0);
		} else {
			((LinearLayout) rootView.findViewById(R.id.li_ciq_tool))
					.setPadding(0, chartiq_icon_showhide_margin_top, 0,0);
		}
		// topbarHide();
	}

	public static void topbarHide() {
		FragmentChangeActivity.wv_chartiq.loadUrl("javascript:(function () { "
				+ "mobileControl.hideTopbar();" + "})()");
	}

	public static void topbarShow() {
		FragmentChangeActivity.wv_chartiq.loadUrl("javascript:(function () { "
				+ "mobileControl.showTopbar();" + "})()");
	}

	public void initMenu() {
		img_tool_showhide = (ImageView) rootView
				.findViewById(R.id.img_tool_showhide);
		img_tool_refresh_chart = (ImageView) rootView
				.findViewById(R.id.img_tool_refresh_chart);

//		tv_ciq_symbol = (TextView) rootView.findViewById(R.id.tv_ciq_symbol);
//		tv_ciq_symbol_name_eng = (TextView) rootView
//				.findViewById(R.id.tv_ciq_symbol_name_eng);
//		tv_ciq_last_trade = (TextView) rootView
//				.findViewById(R.id.tv_ciq_last_trade);
//		tv_ciq_percenchange = (TextView) rootView
//				.findViewById(R.id.tv_ciq_percenchange);
//
//		li_ciq_showview = (LinearLayout) rootView
//				.findViewById(R.id.li_ciq_showview);
//
//		try {
//			if (SplashScreen.contentGetUserById != null) {
//				if (!(SplashScreen.contentGetUserById.getString("package")
//						.equals("free"))) {
//					// li_ciq_showview.setVisibility(View.VISIBLE);
//				} else {
//					// li_ciq_showview.setVisibility(View.GONE);
//				}
//			} else {
//				// li_ciq_showview.setVisibility(View.GONE);
//			}
//
//			tv_ciq_symbol.setText(UiWatchlistDetail.contentGetDetail
//					.getString("symbol_name"));
//			tv_ciq_symbol_name_eng.setText(UiWatchlistDetail.contentGetDetail
//					.getString("symbol_fullname_eng"));
//			tv_ciq_last_trade.setText(UiWatchlistDetail.contentGetDetail
//					.getString("last_trade"));
//
//			String strColor = UiWatchlistDetail.contentGetDetail
//					.getString("change");
//
//			// ck ltrade change
//			String strLastTrade = UiWatchlistDetail.contentGetDetail
//					.getString("last_trade");
//			String strChange = UiWatchlistDetail.contentGetDetail
//					.getString("change");
//			String strPercentChange = UiWatchlistDetail.contentGetDetail
//					.getString("percentChange");
//
//			// tv_change.setText(strChange);
//			if ((strPercentChange == "0") || (strPercentChange == "")
//					|| (strPercentChange == "0.00")) {
//				tv_ciq_percenchange.setText("0.00");
//			} else {
//				// tv_ciq_percenchange.setText(strChange + "("
//				// + strPercentChange + "%)");
//				tv_ciq_percenchange.setText(FunctionSymbol
//						.setFormatNumber(strChange)
//						+ "("
//						+ FunctionSymbol.setFormatNumber(strPercentChange)
//						+ "%)");
//			}
//
//			if (strLastTrade != "") {
//				tv_ciq_last_trade.setTextColor(getResources().getColor(
//						BgColorSymbolDetail.setColor(strLastTrade)));
//			}
//			if (strChange != "") {
//				tv_ciq_percenchange.setTextColor(getResources().getColor(
//						BgColorSymbolDetail.setColor(strChange)));
//			}
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}

		webViewTopbar(); // update topbar

		// ----------------------
		tv_ciq_1d = (TextView) rootView.findViewById(R.id.tv_ciq_1d);
		tv_ciq_1w = (TextView) rootView.findViewById(R.id.tv_ciq_1w);
		tv_ciq_1min = (TextView) rootView.findViewById(R.id.tv_ciq_1min);
		tv_ciq_5min = (TextView) rootView.findViewById(R.id.tv_ciq_5min);
		tv_ciq_15min = (TextView) rootView.findViewById(R.id.tv_ciq_15min);
		tv_ciq_30min = (TextView) rootView.findViewById(R.id.tv_ciq_30min);
		tv_ciq_60min = (TextView) rootView.findViewById(R.id.tv_ciq_60min);
		tv_ciq_120min = (TextView) rootView.findViewById(R.id.tv_ciq_120min);
		tv_ciq_1d.setOnClickListener(onClickListenerChartPeriod);
		tv_ciq_1w.setOnClickListener(onClickListenerChartPeriod);
		tv_ciq_1min.setOnClickListener(onClickListenerChartPeriod);
		tv_ciq_5min.setOnClickListener(onClickListenerChartPeriod);
		tv_ciq_15min.setOnClickListener(onClickListenerChartPeriod);
		tv_ciq_30min.setOnClickListener(onClickListenerChartPeriod);
		tv_ciq_60min.setOnClickListener(onClickListenerChartPeriod);
		tv_ciq_120min.setOnClickListener(onClickListenerChartPeriod);

		// ----------------------
		li_ciq_1view = (LinearLayout) rootView.findViewById(R.id.li_ciq_1view);
		li_ciq_2view = (LinearLayout) rootView.findViewById(R.id.li_ciq_2view);
		li_ciq_3view = (LinearLayout) rootView.findViewById(R.id.li_ciq_3view);
		li_ciq_1view.setOnClickListener(onClickListenerChartView);
		li_ciq_2view.setOnClickListener(onClickListenerChartView);
		li_ciq_3view.setOnClickListener(onClickListenerChartView);

		// ----------------------
		tv_ciq_style = (TextView) rootView.findViewById(R.id.tv_ciq_style);
		tv_ciq_indicator = (TextView) rootView
				.findViewById(R.id.tv_ciq_indicator);
		tv_ciq_compare = (TextView) rootView.findViewById(R.id.tv_ciq_compare);
		tv_ciq_draw = (TextView) rootView.findViewById(R.id.tv_ciq_draw);
		tv_ciq_style.setOnClickListener(onClickListenerChartMenu);
		tv_ciq_indicator.setOnClickListener(onClickListenerChartMenu);
		tv_ciq_compare.setOnClickListener(onClickListenerChartMenu);
		tv_ciq_draw.setOnClickListener(onClickListenerChartMenu);

		img_tool_showhide.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (UiWatchlistDetail.webviewMenuShowHide) {
					UiWatchlistDetail.webviewMenuShowHide = false;
					menuHide();
				} else {
					UiWatchlistDetail.webviewMenuShowHide = true;
					menuShow();
				}
			}
		});

		img_tool_refresh_chart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				li_view_chart.removeView(FragmentChangeActivity.wv_chartiq);

				FragmentChangeActivity.wv_chartiq = new WebView(context);
				FragmentChangeActivity.wv_chartiq.getSettings()
						.setLoadWithOverviewMode(true);
				FragmentChangeActivity.wv_chartiq.getSettings()
						.setUseWideViewPort(true);
				FragmentChangeActivity.wv_chartiq.getSettings()
						.setBuiltInZoomControls(true);
				FragmentChangeActivity.wv_chartiq.getSettings()
						.setJavaScriptEnabled(true);
				FragmentChangeActivity.wv_chartiq
						.setLayoutParams(new ViewGroup.LayoutParams(
								ViewGroup.LayoutParams.MATCH_PARENT,
								ViewGroup.LayoutParams.MATCH_PARENT));
				FragmentChangeActivity.wv_chartiq
						.setWebViewClient(new myWebClient());
				FragmentChangeActivity.wv_chartiq
						.loadUrl("http://www.bidschart.com/iq/stx-mobile-3.html#"
								+ FragmentChangeActivity.strSymbolSelect);

				li_view_chart.addView(FragmentChangeActivity.wv_chartiq);
			}
		});

	}

	// ======= update topbar
	public void webViewTopbar() {
		// FragmentChangeActivity.wv_chartiq.loadUrl("javascript:(function () { "
		// + "mobileControl.showTopbar();" + "})()");

		// --------- update topbar
		try {
			// ------ high low
			String strOpen1 = FunctionFormatData
					.setFormatNumber(UiWatchlistDetail.contentGetDetail
							.getString("open1"));
			String strOpen2 = FunctionFormatData
					.setFormatNumber(UiWatchlistDetail.contentGetDetail
							.getString("open2"));
			String strHigh = FunctionFormatData
					.setFormatNumber(UiWatchlistDetail.contentGetDetail
							.getString("high"));
			String strLow = FunctionFormatData
					.setFormatNumber(UiWatchlistDetail.contentGetDetail
							.getString("low"));
			String strClose = FunctionFormatData
					.setFormatNumber(UiWatchlistDetail.contentGetDetail
							.getString("close"));
			String strPrevClose = FunctionFormatData
					.setFormatNumber(UiWatchlistDetail.contentGetDetail
							.getString("prev_close"));
			String strChange = FunctionFormatData
					.setFormatNumber(UiWatchlistDetail.contentGetDetail
							.getString("change"));
			String strPercentChange = FunctionFormatData
					.setFormatNumber(UiWatchlistDetail.contentGetDetail
							.getString("percentChange"));

			String strOpen1Color = "#"
					+ Integer.toHexString(
							getResources().getColor(R.color.c_content))
							.substring(2);
			String strOpen2Color = "#"
					+ Integer.toHexString(
							getResources().getColor(R.color.c_content))
							.substring(2);
			String strHighColor = "#"
					+ Integer.toHexString(
							getResources().getColor(R.color.c_content))
							.substring(2);
			String strLowColor = "#"
					+ Integer.toHexString(
							getResources().getColor(R.color.c_content))
							.substring(2);
			String strCloseColor = "#"
					+ Integer.toHexString(
							getResources().getColor(R.color.c_danger))
							.substring(2);
			String strPrevCloseColor = "#"
					+ Integer.toHexString(
							getResources().getColor(R.color.c_warning))
							.substring(2);
			String strChangeColor = "#"
					+ Integer.toHexString(
							getResources().getColor(R.color.c_content))
							.substring(2);
			String strPercentChangeColor = "#"
					+ Integer.toHexString(
							getResources().getColor(R.color.c_content))
							.substring(2);

			String strSymbolFullnameEng = UiWatchlistDetail.contentGetDetail
					.getString("symbol_fullname_eng");
			FragmentChangeActivity.wv_chartiq
					.loadUrl("javascript:(function () { "
							+ "mobileControl.updateDesc("
							+ strSymbolFullnameEng + ");" + "})()");

			if (strPrevClose != "") {
				if (strHigh != "") {
					if ((Float.parseFloat(strHigh.replaceAll(",", ""))) > Float
							.parseFloat(strPrevClose.replaceAll(",", ""))) {
						strHighColor = "#"
								+ Integer
										.toHexString(
												getResources()
														.getColor(
																FunctionSetBg.arrColor[2]))
										.substring(2);
					} else if ((Float.parseFloat(strHigh.replaceAll(",", ""))) < Float
							.parseFloat(strPrevClose.replaceAll(",", ""))) {
						strHighColor = "#"
								+ Integer
										.toHexString(
												getResources()
														.getColor(
																FunctionSetBg.arrColor[0]))
										.substring(2);
					} else {
						strHighColor = "#"
								+ Integer
										.toHexString(
												getResources()
														.getColor(
																FunctionSetBg.arrColor[1]))
										.substring(2);
					}
				}
				if (strLow != "") {
					if ((Float.parseFloat(strLow.replaceAll(",", ""))) > Float
							.parseFloat(strPrevClose.replaceAll(",", ""))) {
						strLowColor = "#"
								+ Integer
										.toHexString(
												getResources()
														.getColor(
																FunctionSetBg.arrColor[2]))
										.substring(2);
					} else if ((Float.parseFloat(strLow.replaceAll(",", ""))) < Float
							.parseFloat(strPrevClose.replaceAll(",", ""))) {
						strLowColor = "#"
								+ Integer
										.toHexString(
												getResources()
														.getColor(
																FunctionSetBg.arrColor[0]))
										.substring(2);
					} else {
						strLowColor = "#"
								+ Integer
										.toHexString(
												getResources()
														.getColor(
																FunctionSetBg.arrColor[1]))
										.substring(2);
					}
				}
			}
			// ------ open 1
			if (strOpen1 != "") {
				if ((Float.parseFloat(strOpen1.replaceAll(",", ""))) > Float
						.parseFloat(strPrevClose.replaceAll(",", ""))) {
					strOpen1Color = "#"
							+ Integer.toHexString(
									getResources().getColor(
											FunctionSetBg.arrColor[2]))
									.substring(2);
				} else if ((Float.parseFloat(strOpen1.replaceAll(",", ""))) < Float
						.parseFloat(strPrevClose.replaceAll(",", ""))) {
					strOpen1Color = "#"
							+ Integer.toHexString(
									getResources().getColor(
											FunctionSetBg.arrColor[0]))
									.substring(2);
				} else {
					strOpen1Color = "#"
							+ Integer.toHexString(
									getResources().getColor(
											FunctionSetBg.arrColor[1]))
									.substring(2);
				}
			}
			// ------ open 2
			if (strOpen2 != "") {
				if ((Float.parseFloat(strOpen2.replaceAll(",", ""))) > Float
						.parseFloat(strPrevClose.replaceAll(",", ""))) {
					strOpen2Color = "#"
							+ Integer.toHexString(
									getResources().getColor(
											FunctionSetBg.arrColor[2]))
									.substring(2);
				} else if ((Float.parseFloat(strOpen2.replaceAll(",", ""))) < Float
						.parseFloat(strPrevClose.replaceAll(",", ""))) {
					strOpen2Color = "#"
							+ Integer.toHexString(
									getResources().getColor(
											FunctionSetBg.arrColor[0]))
									.substring(2);
				} else {
					strOpen2Color = "#"
							+ Integer.toHexString(
									getResources().getColor(
											FunctionSetBg.arrColor[1]))
									.substring(2);
				}
			}
			// ------ last trade, percenchage
			if (strChange != "") {
				strChangeColor = "#"
						+ Integer.toHexString(
								getResources()
										.getColor(
												FunctionSetBg
														.setColor(strChange)))
								.substring(2);
				strPercentChangeColor = "#"
						+ Integer.toHexString(
								getResources()
										.getColor(
												FunctionSetBg
														.setColor(strChange)))
								.substring(2);
			}

			// Log.v("valueeee",
			// strOpen1+"_"+strOpen2+"_"+strHigh+"_"+"_"+strLow+"_"+strClose+"_"+strPrevClose+"_"+strChange+"_"+strPercentChange);

			FragmentChangeActivity.wv_chartiq
					.loadUrl("javascript:(function () { "
							+ "mobileControl.updateTopbar({"
							+ "\"open1\": {\"value\" : "
							+ '\"'
							+ strOpen1
							+ '\"'
							+ ", \"color\": "
							+ '\"'
							+ strOpen1Color
							+ '\"'
							+ "},"
							+ "\"open2\": {\"value\": "
							+ '\"'
							+ strOpen2
							+ '\"'
							+ ", \"color\": "
							+ '\"'
							+ strOpen2Color
							+ '\"'
							+ "},"
							+ "\"high\": {\"value\": "
							+ '\"'
							+ strHigh
							+ '\"'
							+ ", \"color\": "
							+ '\"'
							+ strHighColor
							+ '\"'
							+ "},"
							+ "\"low\": {\"value\": "
							+ '\"'
							+ strLow
							+ '\"'
							+ ", \"color\": "
							+ '\"'
							+ strLowColor
							+ '\"'
							+ "},"
							+ "\"close\": {\"value\": "
							+ '\"'
							+ strClose
							+ '\"'
							+ ", \"color\": "
							+ '\"'
							+ strCloseColor
							+ '\"'
							+ "},"
							+ "\"prevclose\": {\"value\": "
							+ '\"'
							+ strPrevClose
							+ '\"'
							+ ", \"color\":  "
							+ '\"'
							+ strPrevCloseColor
							+ '\"'
							+ "},"
							+ "\"change\": {\"value\": "
							+ '\"'
							+ strChange
							+ '\"'
							+ ", \"color\": "
							+ '\"'
							+ strChangeColor
							+ '\"'
							+ "},"
							+ "\"percentChange\": {\"value\": "
							+ '\"'
							+ strPercentChange
							+ '\"'
							+ ", \"color\": "
							+ '\"'
							+ strPercentChangeColor
							+ '\"'
							+ "} });"
							+ "})()");

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// ========== period ==============
	String arrChartPeroid[] = { "day", "week", "1", "3", "5", "15", "30", "60",
			"120" };
	private OnClickListener onClickListenerChartPeriod = new OnClickListener() {
		@Override
		public void onClick(final View v) {

			tv_ciq_1d.setBackgroundColor(Color.TRANSPARENT);
			tv_ciq_1w.setBackgroundColor(Color.TRANSPARENT);
			tv_ciq_1min.setBackgroundColor(Color.TRANSPARENT);
			tv_ciq_5min.setBackgroundColor(Color.TRANSPARENT);
			tv_ciq_15min.setBackgroundColor(Color.TRANSPARENT);
			tv_ciq_30min.setBackgroundColor(Color.TRANSPARENT);
			tv_ciq_60min.setBackgroundColor(Color.TRANSPARENT);
			tv_ciq_120min.setBackgroundColor(Color.TRANSPARENT);

			tv_ciq_1d.setTextColor(getResources().getColor(
					R.color.chartiq_default));
			tv_ciq_1w.setTextColor(getResources().getColor(
					R.color.chartiq_default));
			tv_ciq_1min.setTextColor(getResources().getColor(
					R.color.chartiq_default));
			tv_ciq_5min.setTextColor(getResources().getColor(
					R.color.chartiq_default));
			tv_ciq_15min.setTextColor(getResources().getColor(
					R.color.chartiq_default));
			tv_ciq_30min.setTextColor(getResources().getColor(
					R.color.chartiq_default));
			tv_ciq_60min.setTextColor(getResources().getColor(
					R.color.chartiq_default));
			tv_ciq_120min.setTextColor(getResources().getColor(
					R.color.chartiq_default));

			switch (v.getId()) {
			case R.id.tv_ciq_1d:
				tv_ciq_1d.setBackgroundResource(R.drawable.chartiq_activeleft);
				tv_ciq_1d.setTextColor(getResources().getColor(
						R.color.bg_default));
				FragmentChangeActivity.wv_chartiq
						.loadUrl("javascript:(function () { "
								+ "mobileControl.changePeriod('"
								+ arrChartPeroid[0] + "');" + "})()");
				break;
			case R.id.tv_ciq_1w:
				tv_ciq_1w
						.setBackgroundResource(R.drawable.chartiq_activecenter);
				tv_ciq_1w.setTextColor(getResources().getColor(
						R.color.bg_default));
				FragmentChangeActivity.wv_chartiq
						.loadUrl("javascript:(function () { "
								+ "mobileControl.changePeriod('"
								+ arrChartPeroid[1] + "');" + "})()");
				break;
			case R.id.tv_ciq_1min:
				tv_ciq_1min
						.setBackgroundResource(R.drawable.chartiq_activecenter);
				tv_ciq_1min.setTextColor(getResources().getColor(
						R.color.bg_default));
				FragmentChangeActivity.wv_chartiq
						.loadUrl("javascript:(function () { "
								+ "mobileControl.changePeriod("
								+ arrChartPeroid[2] + ");" + "})()");
				break;
			case R.id.tv_ciq_5min:
				tv_ciq_5min
						.setBackgroundResource(R.drawable.chartiq_activecenter);
				tv_ciq_5min.setTextColor(getResources().getColor(
						R.color.bg_default));
				FragmentChangeActivity.wv_chartiq
						.loadUrl("javascript:(function () { "
								+ "mobileControl.changePeriod("
								+ arrChartPeroid[4] + ");" + "})()");
				break;
			case R.id.tv_ciq_15min:
				tv_ciq_15min
						.setBackgroundResource(R.drawable.chartiq_activecenter);
				tv_ciq_15min.setTextColor(getResources().getColor(
						R.color.bg_default));
				FragmentChangeActivity.wv_chartiq
						.loadUrl("javascript:(function () { "
								+ "mobileControl.changePeriod("
								+ arrChartPeroid[5] + ");" + "})()");
				break;
			case R.id.tv_ciq_30min:
				tv_ciq_30min
						.setBackgroundResource(R.drawable.chartiq_activecenter);
				tv_ciq_30min.setTextColor(getResources().getColor(
						R.color.bg_default));
				FragmentChangeActivity.wv_chartiq
						.loadUrl("javascript:(function () { "
								+ "mobileControl.changePeriod("
								+ arrChartPeroid[6] + ");" + "})()");
				break;
			case R.id.tv_ciq_60min:
				tv_ciq_60min
						.setBackgroundResource(R.drawable.chartiq_activecenter);
				tv_ciq_60min.setTextColor(getResources().getColor(
						R.color.bg_default));
				FragmentChangeActivity.wv_chartiq
						.loadUrl("javascript:(function () { "
								+ "mobileControl.changePeriod("
								+ arrChartPeroid[7] + ");" + "})()");
				break;
			case R.id.tv_ciq_120min:
				tv_ciq_120min
						.setBackgroundResource(R.drawable.chartiq_activeright);
				tv_ciq_120min.setTextColor(getResources().getColor(
						R.color.bg_default));
				FragmentChangeActivity.wv_chartiq
						.loadUrl("javascript:(function () { "
								+ "mobileControl.changePeriod("
								+ arrChartPeroid[8] + ");" + "})()");
				break;
			default:
				break;
			}
		}
	};

	// ========== chart view ==============
	String arrChartView[] = { "1", "2", "3" };
	private OnClickListener onClickListenerChartView = new OnClickListener() {
		@Override
		public void onClick(final View v) {

			li_ciq_1view.setBackgroundColor(Color.TRANSPARENT);
			li_ciq_2view.setBackgroundColor(Color.TRANSPARENT);
			li_ciq_3view.setBackgroundColor(Color.TRANSPARENT);

			switch (v.getId()) {
			case R.id.li_ciq_1view:
				li_ciq_1view
						.setBackgroundResource(R.drawable.chartiq_activeleft);
				FragmentChangeActivity.wv_chartiq
						.loadUrl("javascript:(function () { "
								+ "mobileControl.changeLayout("
								+ arrChartView[0] + ");" + "})()");
				break;
			case R.id.li_ciq_2view:
				li_ciq_2view
						.setBackgroundResource(R.drawable.chartiq_activecenter);
				FragmentChangeActivity.wv_chartiq
						.loadUrl("javascript:(function () { "
								+ "mobileControl.changeLayout("
								+ arrChartView[1] + ");" + "})()");
				break;
			case R.id.li_ciq_3view:
				li_ciq_3view
						.setBackgroundResource(R.drawable.chartiq_activeright);
				FragmentChangeActivity.wv_chartiq
						.loadUrl("javascript:(function () { "
								+ "mobileControl.changeLayout("
								+ arrChartView[2] + ");" + "})()");
				break;
			default:
				break;
			}
		}
	};

	// ========== chart menu ==============
	private OnClickListener onClickListenerChartMenu = new OnClickListener() {
		@Override
		public void onClick(final View v) {

			tv_ciq_style.setBackgroundColor(Color.TRANSPARENT);
			tv_ciq_indicator.setBackgroundColor(Color.TRANSPARENT);
			tv_ciq_compare.setBackgroundColor(Color.TRANSPARENT);
			tv_ciq_draw.setBackgroundColor(Color.TRANSPARENT);

			tv_ciq_style.setTextColor(getResources().getColor(
					R.color.chartiq_default));
			tv_ciq_indicator.setTextColor(getResources().getColor(
					R.color.chartiq_default));
			tv_ciq_compare.setTextColor(getResources().getColor(
					R.color.chartiq_default));
			tv_ciq_draw.setTextColor(getResources().getColor(
					R.color.chartiq_default));

			switch (v.getId()) {
			case R.id.tv_ciq_style:
				tv_ciq_style
						.setBackgroundResource(R.drawable.chartiq_activeleft);
				tv_ciq_style.setTextColor(getResources().getColor(
						R.color.bg_default));
				
				dialogChartiqSytle.show();
//				showDialogChartIqStyle();
				break;
			case R.id.tv_ciq_indicator:
				tv_ciq_indicator
						.setBackgroundResource(R.drawable.chartiq_activecenter);
				tv_ciq_indicator.setTextColor(getResources().getColor(
						R.color.bg_default));
				
				dialogChartiqIndicator.show();
//				showDialogChartIqIndicator(); 
				break;
			case R.id.tv_ciq_compare:
				tv_ciq_compare
						.setBackgroundResource(R.drawable.chartiq_activecenter);
				tv_ciq_compare.setTextColor(getResources().getColor(
						R.color.bg_default));
				
				dialogChartiqCompare.show();
//				showDialogChartIqCompare();  
				break;
			case R.id.tv_ciq_draw:
				tv_ciq_draw
						.setBackgroundResource(R.drawable.chartiq_activeright);
				tv_ciq_draw.setTextColor(getResources().getColor(
						R.color.bg_default));
				
				dialogChartiqDraw.show();
//				showDialogChartIqDraw(); 
				break;
			default:
				break;
			}
		}
	};

	// ========= dialog compare ========
	Dialog dialogChartIqCompare; // color picke
	Dialog dialogChartIqCompareSearch; // search

	// private EditText dl_compare_et_search;
	private TextView dl_tv_compare_search_select, dl_tv_compare_done;
	private GridView gridViewColors;

	public void showDialogChartIqCompare() {

		LayoutInflater li = LayoutInflater.from(context);
		View viewDl = li.inflate(R.layout.dialog_chartiq_compare, null);

		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder.setView(viewDl);

		gridViewColors = (GridView) viewDl.findViewById(R.id.gridViewColors);

		final TextView dl_tv_color = (TextView) viewDl
				.findViewById(R.id.dl_tv_color);

		dl_tv_compare_search_select = (TextView) viewDl
				.findViewById(R.id.dl_tv_compare_search);
		dl_tv_compare_done = (TextView) viewDl
				.findViewById(R.id.dl_tv_compare_done);
		dl_tv_compare_search_select.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialogChartIqCompareSearch(); // search symbol
			}
		});
		dl_tv_compare_done.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String strSymbolSearch = dl_tv_compare_search_select.getText()
						.toString().trim();
				String strColor = dl_tv_color.getTag().toString().trim();

				if ((!strSymbolSearch.equals("")) && (!strColor.equals(""))) {
					dialogChartIqCompare.dismiss();
					FragmentChangeActivity.wv_chartiq
							.loadUrl("javascript:(function () { "
									+ "mobileControl.addCompare('"
									+ strSymbolSearch + "','rgb(" + strColor
									+ ")');" + "})()");
				} else {
				}
			}
		});

		gridViewColors.setAdapter(new ColorPickerAdapter(context));

		// close the dialog on item click
		gridViewColors.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final int i = position;
				Integer intColor = ColorPickerAdapter.colorList.get(i);
				String hexColor = "#"
						+ Integer.toHexString(intColor).substring(2);

				int red = Color.red(intColor);
				int green = Color.green(intColor);
				int blue = Color.blue(intColor);

				String rgbColor = red + "," + green + "," + blue;

				dl_tv_color.setBackgroundColor(Color.parseColor(hexColor));
				dl_tv_color.setTag(rgbColor);

				// mobileControl.addCompare('ptt','rgb(%.0f,%.0f,%.0f)')

				// FragmentChangeActivity.wv_chartiq.loadUrl("javascript:(function () { "
				// + "mobileControl.addCompare('"
				// + FragmentChangeActivity.strSymbolSelect + "','rgb("
				// + rgbColor + ")');" + "})()");
				// dialogChartIqCompare.dismiss();

			}
		});

		// create alert dialog
		dialogChartIqCompare = alertDialogBuilder.create();
		// show it
		dialogChartIqCompare.show();

		// set width height dialog
		DisplayMetrics displaymetrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(displaymetrics);

		int ScreenHeight = displaymetrics.heightPixels;
		int ScreenWidth = displaymetrics.widthPixels;

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.gravity = Gravity.TOP;
		lp.width = (int) (ScreenWidth * chartIqScreenWidth);
		lp.height = (int) (ScreenHeight * chartIqScreenHeight);
		dialogChartIqCompare.getWindow().setAttributes(lp);
		dialogChartIqCompare.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
	}

	// ------ ChartIq Compare Search
	private ListView dl_list_compare_search;
	private EditText dl_et_compare_search;
	private TextView dl_tv_compare_search_common, dl_tv_compare_search_warrant,
			dl_tv_compare_search_dw, dl_tv_compare_search_close;

	public static String status_segmentId = "COMMON"; // COMMON, FOREIGN_STOCK,
														// WARRENT, DW
	public static String status_segmentIdDot = "EQUITY_INDEX";

	public void showDialogChartIqCompareSearch() {

		LayoutInflater li = LayoutInflater.from(context);
		View viewDl = li.inflate(R.layout.dialog_chartiq_compare_search, null);

		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder.setView(viewDl);

		dl_list_compare_search = (ListView) viewDl
				.findViewById(R.id.dl_list_compare_search);
		dl_et_compare_search = (EditText) viewDl
				.findViewById(R.id.dl_et_compare_search);
		dl_tv_compare_search_common = (TextView) viewDl
				.findViewById(R.id.dl_tv_compare_search_common);
		dl_tv_compare_search_warrant = (TextView) viewDl
				.findViewById(R.id.dl_tv_compare_search_warrant);
		dl_tv_compare_search_dw = (TextView) viewDl
				.findViewById(R.id.dl_tv_compare_search_dw);
		dl_tv_compare_search_close = (TextView) viewDl
				.findViewById(R.id.dl_tv_compare_search_close);

		final ChartIqCompateListAdapter ListAdapterSearch;
		final ArrayList<CatalogGetSymbol> original_list;
		final ArrayList<CatalogGetSymbol> second_list;

		original_list = new ArrayList<CatalogGetSymbol>();
		original_list.addAll(FragmentChangeActivity.list_getSymbol);
		second_list = new ArrayList<CatalogGetSymbol>();
		second_list.addAll(FragmentChangeActivity.list_getSymbol);

		ListAdapterSearch = new ChartIqCompateListAdapter(context, 0,
				second_list);
		dl_list_compare_search.setAdapter(ListAdapterSearch);

		dl_et_compare_search.addTextChangedListener(new TextWatcher() {
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
				String text = dl_et_compare_search.getText().toString();

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
			}
		});

		// -------------------------
		dl_tv_compare_search_common.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				status_segmentId = "COMMON"; // COMMON, FOREIGN_STOCK, WARRENT,
												// DW
				setTitleSearch();
				dl_tv_compare_search_common.setTextColor(getResources()
						.getColor(R.color.bg_default));
				dl_tv_compare_search_common
						.setBackgroundResource(R.drawable.border_search_activeleft);

				second_list.clear();
				for (int i = 0; i < original_list.size(); i++) {
					if (original_list.get(i).symbol.toLowerCase().contains(
							dl_et_compare_search.getText().toString()
									.toLowerCase())) {
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
		dl_tv_compare_search_warrant.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				status_segmentId = "WARRENT"; // COMMON, FOREIGN_STOCK, WARRENT,
												// DW
				setTitleSearch();
				dl_tv_compare_search_warrant.setTextColor(getResources()
						.getColor(R.color.bg_default));
				dl_tv_compare_search_warrant
						.setBackgroundResource(R.drawable.border_search_activecenter);

				second_list.clear();
				for (int i = 0; i < original_list.size(); i++) {
					if (original_list.get(i).symbol.toLowerCase().contains(
							dl_et_compare_search.getText().toString()
									.toLowerCase())) {
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
		dl_tv_compare_search_dw.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				status_segmentId = "DW"; // COMMON, FOREIGN_STOCK, WARRENT, DW
				setTitleSearch();
				dl_tv_compare_search_dw.setTextColor(getResources().getColor(
						R.color.bg_default));
				dl_tv_compare_search_dw
						.setBackgroundResource(R.drawable.border_search_activeright);

				second_list.clear();
				for (int i = 0; i < original_list.size(); i++) {
					if (original_list.get(i).symbol.toLowerCase().contains(
							dl_et_compare_search.getText().toString()
									.toLowerCase())) {
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
		dl_tv_compare_search_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogChartIqCompareSearch.dismiss();
			}
		});

		// create alert dialog
		dialogChartIqCompareSearch = alertDialogBuilder.create();
		// show it
		dialogChartIqCompareSearch.show();

		// set width height dialog
		DisplayMetrics displaymetrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(displaymetrics);

		int ScreenHeight = displaymetrics.heightPixels;
		int ScreenWidth = displaymetrics.widthPixels;

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.gravity = Gravity.TOP;
		lp.width = (int) (ScreenWidth * 1);
		lp.height = (int) (ScreenHeight * 1);
		dialogChartIqCompareSearch.getWindow().setAttributes(lp);
		dialogChartIqCompareSearch.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
	}

	// ---- chartiq compare adapter
	public class ChartIqCompateListAdapter extends ArrayAdapter {
		ArrayList<CatalogGetSymbol> arl;

		public ChartIqCompateListAdapter(Context context,
				int textViewResourceId, ArrayList<CatalogGetSymbol> arl) {
			super(context, textViewResourceId);
			this.arl = arl;
		}

		@Override
		public int getCount() {
			return arl.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View v = convertView;
			if (v == null) {
				LayoutInflater vi;
				vi = LayoutInflater.from(getContext());
				v = vi.inflate(R.layout.row_chartiq_search_compare_symbol, null);
			}
			final TextView tv_symbol = (TextView) v
					.findViewById(R.id.tv_symbol);
			final TextView tv_market_id = (TextView) v
					.findViewById(R.id.tv_market_id);
			final TextView tv_symbol_fullname_eng = (TextView) v
					.findViewById(R.id.tv_symbol_fullname_eng);
			final LinearLayout li_row = (LinearLayout) v
					.findViewById(R.id.li_row);

			// text
			tv_symbol.setText("" + arl.get(position).symbol);
			tv_market_id.setText("" + arl.get(position).market_id);
			tv_symbol_fullname_eng.setText(""
					+ arl.get(position).symbol_fullname_eng);
			// tag
			tv_symbol.setTag("" + arl.get(position).symbol);
			tv_market_id.setTag("" + arl.get(position).market_id);
			tv_symbol_fullname_eng.setTag(""
					+ arl.get(position).symbol_fullname_eng);
			li_row.setTag("" + arl.get(position).symbol);

			final String symbolSelect = arl.get(position).symbol;

			li_row.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialogChartIqCompareSearch.dismiss();
					dl_tv_compare_search_select.setText(symbolSelect);
				}
			});

			return v;
		}
	}

	// ------- set title compare search ------
	private void setTitleSearch() {
		dl_tv_compare_search_common.setTextColor(getResources().getColor(
				R.color.c_content));
		dl_tv_compare_search_warrant.setTextColor(getResources().getColor(
				R.color.c_content));
		dl_tv_compare_search_dw.setTextColor(getResources().getColor(
				R.color.c_content));
		dl_tv_compare_search_common.setBackgroundColor(Color.TRANSPARENT);
		dl_tv_compare_search_warrant.setBackgroundColor(Color.TRANSPARENT);
		dl_tv_compare_search_dw.setBackgroundColor(Color.TRANSPARENT);
	}

	// ========= dialog style ========
	private AlertDialog alertDialogChartIqStyle;

	public void showDialogChartIqStyle() {
		LayoutInflater li = LayoutInflater.from(context);
		View viewDl = li.inflate(R.layout.dialog_chartiq_style, null);

		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder.setView(viewDl);

		final TextView tv_chartstyle, tv_charttype, tv_chartscale;
		final LinearLayout li_chartstyle, li_charttype, li_chartscale;
		tv_chartstyle = (TextView) viewDl.findViewById(R.id.tv_chartstyle);
		tv_charttype = (TextView) viewDl.findViewById(R.id.tv_charttype);
		tv_chartscale = (TextView) viewDl.findViewById(R.id.tv_chartscale);

		li_chartstyle = (LinearLayout) viewDl.findViewById(R.id.li_chartstyle);
		li_charttype = (LinearLayout) viewDl.findViewById(R.id.li_charttype);
		li_chartscale = (LinearLayout) viewDl.findViewById(R.id.li_chartscale);

		tv_chartstyle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tv_chartstyle.setTextColor(getResources().getColor(
						R.color.c_content));
				tv_charttype.setTextColor(getResources().getColor(
						R.color.c_title));
				tv_chartscale.setTextColor(getResources().getColor(
						R.color.c_title));

				li_chartstyle.setVisibility(View.VISIBLE);
				li_charttype.setVisibility(View.GONE);
				li_chartscale.setVisibility(View.GONE);
			}
		});
		tv_charttype.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tv_chartstyle.setTextColor(getResources().getColor(
						R.color.c_title));
				tv_charttype.setTextColor(getResources().getColor(
						R.color.c_content));
				tv_chartscale.setTextColor(getResources().getColor(
						R.color.c_title));

				li_chartstyle.setVisibility(View.GONE);
				li_charttype.setVisibility(View.VISIBLE);
				li_chartscale.setVisibility(View.GONE);
			}
		});
		tv_chartscale.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tv_chartstyle.setTextColor(getResources().getColor(
						R.color.c_title));
				tv_charttype.setTextColor(getResources().getColor(
						R.color.c_title));
				tv_chartscale.setTextColor(getResources().getColor(
						R.color.c_content));

				li_chartstyle.setVisibility(View.GONE);
				li_charttype.setVisibility(View.GONE);
				li_chartscale.setVisibility(View.VISIBLE);
			}
		});

		// style
		((LinearLayout) viewDl.findViewById(R.id.li_candle))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) viewDl.findViewById(R.id.li_bar))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) viewDl.findViewById(R.id.li_coloredbar))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) viewDl.findViewById(R.id.li_line))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) viewDl.findViewById(R.id.li_hollowcandle))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) viewDl.findViewById(R.id.li_mountain))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) viewDl.findViewById(R.id.li_coloredline))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) viewDl.findViewById(R.id.li_baseelinedelta))
				.setOnClickListener(onClickListenerChartIqStyle);
		// type
		((LinearLayout) viewDl.findViewById(R.id.li_heikinashi))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) viewDl.findViewById(R.id.li_kagi))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) viewDl.findViewById(R.id.li_linebreak))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) viewDl.findViewById(R.id.li_pointfigure))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) viewDl.findViewById(R.id.li_rangebars))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) viewDl.findViewById(R.id.li_renko))
				.setOnClickListener(onClickListenerChartIqStyle);
		// scale
		// TextView tv_logscale = (TextView)
		// viewDl.findViewById(R.id.tv_logscale);

		// create alert dialog
		alertDialogChartIqStyle = alertDialogBuilder.create();
		// show it
		alertDialogChartIqStyle.show();

		// set width height dialog
		DisplayMetrics displaymetrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(displaymetrics);

		int ScreenHeight = (int) (displaymetrics.heightPixels * chartIqScreenHeight);
		int ScreenWidth = (int) (displaymetrics.widthPixels * chartIqScreenWidth);

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		// lp.gravity = Gravity.TOP;
		lp.width = ScreenWidth;
		lp.height = ScreenHeight;
		
		alertDialogChartIqStyle.getWindow().setAttributes(lp);
		alertDialogChartIqStyle.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		
	}

	// -------- on click style -----------
	private OnClickListener onClickListenerChartIqStyle = new OnClickListener() {
		@Override
		public void onClick(final View v) {

			String tag = v.getTag().toString();

			FragmentChangeActivity.wv_chartiq
					.loadUrl("javascript:(function () { "
							+ "mobileControl.changeChartType('" + tag + "');"
							+ "})()");
			alertDialogChartIqStyle.dismiss();

		}
	};

	public static String AssetJSONFile(String filename, Context context)
			throws IOException {
		AssetManager manager = context.getAssets();
		InputStream file = manager.open(filename);
		byte[] formArray = new byte[file.available()];
		file.read(formArray);
		file.close();

		return new String(formArray);
	}

	// ========= dialog indicator ========
	private AlertDialog alertDialogChartIqIndicator;

	private ArrayList<TextView> row_indicator_title;

	public void showDialogChartIqIndicator() {
		try {
			// row_color_list_select.clear();
			LayoutInflater li = LayoutInflater.from(context);
			View viewDl = li.inflate(R.layout.dialog_chartiq_indicator_list,
					null);

			final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					context);
			alertDialogBuilder.setView(viewDl);

			final EditText et_search = (EditText) viewDl
					.findViewById(R.id.et_search);

			LinearLayout li_list_title = (LinearLayout) viewDl
					.findViewById(R.id.li_list_title);
			final LinearLayout li_list_value = (LinearLayout) viewDl
					.findViewById(R.id.li_list_value);
			li_list_title.removeAllViews();

			row_indicator_title = new ArrayList<TextView>();
			row_indicator_title.clear();

			for (int i = 0; i < jsonIndicator.length(); i++) {
				View view2 = ((Activity) context).getLayoutInflater().inflate(
						R.layout.row_dialog_studies, null);

				TextView tv_row = (TextView) view2.findViewById(R.id.tv_row);
				// row_color_list_select.add(tv_row);

				JSONObject jsoIndex = jsonIndicator.getJSONObject(i);
				final JSONArray jsaValue = jsoIndex.getJSONArray("value");

				tv_row.setText("" + jsoIndex.getString("title") + " ("
						+ jsaValue.length() + ")");
				tv_row.setTag("" + jsoIndex.getString("key"));

				// list value begin
				if (i == 0) {
					li_list_value.removeAllViews();
					for (int j = 0; j < jsaValue.length(); j++) {

						View viewValue = ((Activity) context)
								.getLayoutInflater().inflate(
										R.layout.row_dialog_studies, null);

						final TextView tv_row_value = (TextView) viewValue
								.findViewById(R.id.tv_row);

						tv_row_value.setText(""
								+ jsaValue.getJSONObject(j).getString("title"));
						tv_row_value.setTag(""
								+ jsaValue.getJSONObject(j).getString("key"));
						tv_row_value.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								FragmentChangeActivity.wv_chartiq
										.loadUrl("javascript:(function () { "
												+ "mobileControl.addStudy('"
												+ tv_row_value.getTag() + "');"
												+ "})()");
								alertDialogChartIqIndicator.dismiss();
							}
						});
						li_list_value.addView(viewValue);
					}
				}

				final int num_row = i;
				tv_row.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						changeRowIndicatorTitle(v, num_row);
						li_list_value.removeAllViews();
						try {
							for (int j = 0; j < jsaValue.length(); j++) {
								View view2 = ((Activity) context)
										.getLayoutInflater().inflate(
												R.layout.row_dialog_studies,
												null);

								final TextView tv_row_value = (TextView) view2
										.findViewById(R.id.tv_row);

								tv_row_value.setText(""
										+ jsaValue.getJSONObject(j).getString(
												"title"));
								tv_row_value.setTag(""
										+ jsaValue.getJSONObject(j).getString(
												"key"));
								tv_row_value
										.setOnClickListener(new OnClickListener() {
											@Override
											public void onClick(View v) {
												FragmentChangeActivity.wv_chartiq.loadUrl("javascript:(function () { "
														+ "mobileControl.addStudy('"
														+ tv_row_value.getTag()
														+ "');" + "})()");
												alertDialogChartIqIndicator
														.dismiss();
											}
										});
								li_list_value.addView(view2);
							}
						} catch (JSONException e2) {
							e2.printStackTrace();
						}
					}
				});

				row_indicator_title.add(tv_row);

				li_list_title.addView(view2);
			}

			// ********* search studiea value *********
			final ArrayList<ChartIqGetIndicatorValueCatalog> original_list;
			final ArrayList<ChartIqGetIndicatorValueCatalog> second_list;

			original_list = new ArrayList<ChartIqGetIndicatorValueCatalog>();
			original_list.addAll(list_getIndicatorValue);
			second_list = new ArrayList<ChartIqGetIndicatorValueCatalog>();
			second_list.addAll(list_getIndicatorValue);

			et_search.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					String text = et_search.getText().toString();
					if (text.length() > 0) {
						second_list.clear();
						for (int i = 0; i < original_list.size(); i++) {
							if (original_list.get(i).title.toLowerCase()
									.contains(text.toString().toLowerCase())) {
								second_list.add(original_list.get(i));
							}
						}
						li_list_value.removeAllViews();
						for (int j = 0; j < second_list.size(); j++) {
							View viewValue = ((Activity) context)
									.getLayoutInflater().inflate(
											R.layout.row_dialog_studies, null);

							final TextView tv_row_value = (TextView) viewValue
									.findViewById(R.id.tv_row);

							tv_row_value.setText(""
									+ second_list.get(j).getTitle());
							tv_row_value.setTag(""
									+ second_list.get(j).getKey());
							tv_row_value
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											FragmentChangeActivity.wv_chartiq
													.loadUrl("javascript:(function () { "
															+ "mobileControl.addStudy('"
															+ tv_row_value
																	.getTag()
															+ "');" + "})()");
											alertDialogChartIqIndicator
													.dismiss();
										}
									});
							li_list_value.addView(viewValue);
						}
					} else {
					}
				}
			});

			// create alert dialog
			alertDialogChartIqIndicator = alertDialogBuilder.create();

			// show it
			alertDialogChartIqIndicator.show();

			// set width height dialog
			DisplayMetrics displaymetrics = new DisplayMetrics();
			((Activity) context).getWindowManager().getDefaultDisplay()
					.getMetrics(displaymetrics);
			int ScreenHeight = displaymetrics.heightPixels;
			int ScreenWidth = displaymetrics.widthPixels;

			WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
			lp.width = (int) (ScreenWidth * chartIqScreenWidth);
			lp.height = (int) (ScreenHeight * chartIqScreenHeight);

			alertDialogChartIqIndicator.getWindow().setAttributes(lp);
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
	}

	public void changeRowIndicatorTitle(View v, int num_row) {
		for (int i = 0; i < row_indicator_title.size(); i++) {
			row_indicator_title.get(i).setTextColor(
					getResources().getColor(R.color.c_title));
		}
		row_indicator_title.get(num_row).setTextColor(
				getResources().getColor(R.color.c_content));
	}

	// ========= dialog draw ========
	private AlertDialog alertDialogChartIqDraw;

	public void showDialogChartIqDraw() {
		LayoutInflater li = LayoutInflater.from(context);
		View viewDl = li.inflate(R.layout.dialog_chartiq_draw, null);

		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder.setView(viewDl);

		// -------- coll 1 -------
		((TextView) viewDl.findViewById(R.id.tv_segment))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) viewDl.findViewById(R.id.tv_line))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) viewDl.findViewById(R.id.tv_measure))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) viewDl.findViewById(R.id.tv_fibonacci))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) viewDl.findViewById(R.id.tv_rectangle))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) viewDl.findViewById(R.id.tv_annotation))
				.setOnClickListener(onClickListenerChartIqDraw);

		// -------- coll 2 -------
		((TextView) viewDl.findViewById(R.id.tv_continuous))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) viewDl.findViewById(R.id.tv_horizontal))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) viewDl.findViewById(R.id.tv_channel))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) viewDl.findViewById(R.id.tv_gartley))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) viewDl.findViewById(R.id.tv_ellipse))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) viewDl.findViewById(R.id.tv_callout))
				.setOnClickListener(onClickListenerChartIqDraw);

		// -------- coll 3 -------
		((TextView) viewDl.findViewById(R.id.tv_ray))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) viewDl.findViewById(R.id.tv_vertical))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) viewDl.findViewById(R.id.tv_freeform))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) viewDl.findViewById(R.id.tv_pitchfork))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) viewDl.findViewById(R.id.tv_shape))
				.setOnClickListener(onClickListenerChartIqDraw);

		((TextView) viewDl.findViewById(R.id.tv_clear))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.wv_chartiq
								.loadUrl("javascript:(function () { "
										+ "mobileControl.clearDrawing();"
										+ "})()");
						alertDialogChartIqDraw.dismiss();
					}
				});

		// create alert dialog
		alertDialogChartIqDraw = alertDialogBuilder.create();
		// show it
		alertDialogChartIqDraw.show();

		// set width height dialog
		DisplayMetrics displaymetrics = new DisplayMetrics();
		((Activity) context).getWindowManager().getDefaultDisplay()
				.getMetrics(displaymetrics);

		int ScreenHeight = displaymetrics.heightPixels;
		int ScreenWidth = displaymetrics.widthPixels;

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		// lp.gravity = Gravity.TOP;
		lp.width = (int) (ScreenWidth * chartIqScreenWidth);
		lp.height = (int) (ScreenHeight * chartIqScreenHeight);
		alertDialogChartIqDraw.getWindow().setAttributes(lp);
		alertDialogChartIqDraw.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
	}

	// -------- on click Draw -----------
	private OnClickListener onClickListenerChartIqDraw = new OnClickListener() {
		@Override
		public void onClick(final View v) {

			String tag = v.getTag().toString();
			// Toast.makeText(context, "" + tag,
			// Toast.LENGTH_SHORT).show();

			FragmentChangeActivity.wv_chartiq
					.loadUrl("javascript:(function () { "
							+ "mobileControl.setDrawingType('" + tag + "');"
							+ "})()");
			alertDialogChartIqDraw.dismiss();

		}
	};

	// ====== init webview ============
	public static LinearLayout li_test_wv, li_view_chart;

	LinearLayout li_ciq_tool;
	boolean ckShowHideCiqTool;

	public void initWebView() {
		li_view_chart = (LinearLayout) rootView
				.findViewById(R.id.li_view_chart);

		// LinearLayout.LayoutParams layoutParams = new
		// LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
		// 800);
		// li_view_chart.setLayoutParams(layoutParams); // line no 50

		li_view_chart.addView(FragmentChangeActivity.wv_chartiq);
		FragmentChangeActivity.wv_chartiq.loadUrl("javascript:(function () { "
				+ "mobileControl.changeSymbol('"
				+ FragmentChangeActivity.strSymbolSelect + "');" + "})()");

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