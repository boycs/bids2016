package com.app.bids;

import java.io.IOException;
import java.text.ParseException;
import java.util.Random;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.app.bids.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;

public class PagerHomeAll extends Fragment {

	static Context context;
	public static View rootView;

	// ================= SET ==================
	// ข้อมูล(เปลียน เป็น .set50, .set100
	// ตามต้องการ)FragmentChangeActivity.url_bidschart+"/service/getSymbolByName?symbol=.set
	// กราฟ(เปลียน เป็น set50, set100
	// ตามต้องการ)FragmentChangeActivity.url_bidschart+"/snapshot/SET-chart
	public static int intSymbolByName = 1;
	public static JSONObject contentGetSymbolByNameSet = null;
	public static JSONObject contentGetSymbolByNameSet50 = null;
	public static JSONObject contentGetSymbolByNameSet100 = null;
	public static JSONObject contentGetSymbolByNameSetHd = null;
	public static JSONObject contentGetSymbolByNameMai = null;

	public static JSONArray josSymbolByName = null;
	// ================= chart ==================
	private String url_SnapShotSet = SplashScreen.url_bidschart+"new/googleChart/.SET";
	private String url_SnapShotSet50 = SplashScreen.url_bidschart+"new/googleChart/.SET50";
	private String url_SnapShotSet100 = SplashScreen.url_bidschart+"new/googleChart/.SET100";
	private String url_SnapShotSetHd = SplashScreen.url_bidschart+"new/googleChart/.SETHD";
	private String url_SnapShotMai = SplashScreen.url_bidschart+"new/googleChart/.MAI";

	// activity listener interface
	private OnPageListener pageListener;

	// public MyPagerAdapter adapterCorousel;

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
		rootView = (LinearLayout) inflater.inflate(R.layout.pager_home,
				container, false);

		// initial Set Data
		getDataAll();

		return rootView;

	}

	// ====================== Init page =============
	private void initSetData() {
		// list set data
		initListSet();

		// initial ProgressBar
		initProgressBar();
	}

	// ====================== list set =============
	private void initListSet() {

		try {
			LinearLayout li_list_set = (LinearLayout) rootView
					.findViewById(R.id.li_list_set);
			li_list_set.removeAllViews();
			// set
			if (contentGetSymbolByNameSet != null) {
				View view2 = ((Activity) context).getLayoutInflater().inflate(
						R.layout.index_home_set, null);

				WebView wv_chart = (WebView) view2.findViewById(R.id.wv_chart);
				TextView tv_last_trade = (TextView) view2
						.findViewById(R.id.tv_last_trade);
				TextView tv_change = (TextView) view2
						.findViewById(R.id.tv_change);
				TextView tv_percentChange = (TextView) view2
						.findViewById(R.id.tv_percentChange);

				TextView tv_symbol_name = (TextView) view2
						.findViewById(R.id.tv_symbol_name);
				TextView tv_value = (TextView) view2
						.findViewById(R.id.tv_value);

				TextView tv_open2 = (TextView) view2
						.findViewById(R.id.tv_open2);
				TextView tv_high = (TextView) view2.findViewById(R.id.tv_high);
				TextView tv_low = (TextView) view2.findViewById(R.id.tv_low);

				TextView tv_prev_close = (TextView) view2
						.findViewById(R.id.tv_prev_close);
				TextView tv_high52W = (TextView) view2
						.findViewById(R.id.tv_high52W);
				TextView tv_low52W = (TextView) view2
						.findViewById(R.id.tv_low52W);

				tv_last_trade.setText(""
						+ contentGetSymbolByNameSet.get("last_trade"));
				tv_change.setText("" + contentGetSymbolByNameSet.get("change"));
				tv_percentChange.setText(""
						+ contentGetSymbolByNameSet.get("percentChange") + "%");

				tv_symbol_name.setText(""
						+ contentGetSymbolByNameSet.get("symbol_name"));
				tv_value.setText("" + contentGetSymbolByNameSet.get("value"));

				tv_open2.setText("" + contentGetSymbolByNameSet.get("open2"));
				tv_high.setText("" + contentGetSymbolByNameSet.get("high"));
				tv_low.setText("" + contentGetSymbolByNameSet.get("low"));

				tv_prev_close.setText(""
						+ contentGetSymbolByNameSet.get("prev_close"));
				tv_high52W.setText(""
						+ contentGetSymbolByNameSet.get("high52W"));
				tv_low52W.setText("" + contentGetSymbolByNameSet.get("low52W"));
				
				String urlChart = "" + url_SnapShotSet;
				wv_chart.getSettings().setJavaScriptEnabled(true);
				wv_chart.getSettings().setUseWideViewPort(true);
				wv_chart.getSettings().setDomStorageEnabled(true);
				wv_chart.setInitialScale(100);
				wv_chart.loadUrl(urlChart);
				
				li_list_set.addView(view2);
			}
			// set50
			if (contentGetSymbolByNameSet50 != null) {
				View view2 = ((Activity) context).getLayoutInflater().inflate(
						R.layout.index_home_set, null);

				WebView wv_chart = (WebView) view2.findViewById(R.id.wv_chart);
				TextView tv_last_trade = (TextView) view2
						.findViewById(R.id.tv_last_trade);
				TextView tv_change = (TextView) view2
						.findViewById(R.id.tv_change);
				TextView tv_percentChange = (TextView) view2
						.findViewById(R.id.tv_percentChange);

				TextView tv_symbol_name = (TextView) view2
						.findViewById(R.id.tv_symbol_name);
				TextView tv_value = (TextView) view2
						.findViewById(R.id.tv_value);

				TextView tv_open2 = (TextView) view2
						.findViewById(R.id.tv_open2);
				TextView tv_high = (TextView) view2.findViewById(R.id.tv_high);
				TextView tv_low = (TextView) view2.findViewById(R.id.tv_low);

				TextView tv_prev_close = (TextView) view2
						.findViewById(R.id.tv_prev_close);
				TextView tv_high52W = (TextView) view2
						.findViewById(R.id.tv_high52W);
				TextView tv_low52W = (TextView) view2
						.findViewById(R.id.tv_low52W);

				tv_last_trade.setText(""
						+ contentGetSymbolByNameSet50.get("last_trade"));
				tv_change.setText(""
						+ contentGetSymbolByNameSet50.get("change"));
				tv_percentChange.setText(""
						+ contentGetSymbolByNameSet50.get("percentChange")
						+ "%");

				tv_symbol_name.setText(""
						+ contentGetSymbolByNameSet50.get("symbol_name"));
				tv_value.setText("" + contentGetSymbolByNameSet50.get("value"));

				tv_open2.setText("" + contentGetSymbolByNameSet50.get("open2"));
				tv_high.setText("" + contentGetSymbolByNameSet50.get("high"));
				tv_low.setText("" + contentGetSymbolByNameSet50.get("low"));

				tv_prev_close.setText(""
						+ contentGetSymbolByNameSet50.get("prev_close"));
				tv_high52W.setText(""
						+ contentGetSymbolByNameSet50.get("high52W"));
				tv_low52W.setText(""
						+ contentGetSymbolByNameSet50.get("low52W"));
				
				String urlChart = "" + url_SnapShotSet50;
				wv_chart.getSettings().setJavaScriptEnabled(true);
				wv_chart.getSettings().setUseWideViewPort(true);
				wv_chart.getSettings().setDomStorageEnabled(true);
				wv_chart.setInitialScale(100);
				wv_chart.loadUrl(urlChart);
				
				li_list_set.addView(view2);
			}
			// set100
			if (contentGetSymbolByNameSet100 != null) {
				View view2 = ((Activity) context).getLayoutInflater().inflate(
						R.layout.index_home_set, null);

				WebView wv_chart = (WebView) view2.findViewById(R.id.wv_chart);
				TextView tv_last_trade = (TextView) view2
						.findViewById(R.id.tv_last_trade);
				TextView tv_change = (TextView) view2
						.findViewById(R.id.tv_change);
				TextView tv_percentChange = (TextView) view2
						.findViewById(R.id.tv_percentChange);

				TextView tv_symbol_name = (TextView) view2
						.findViewById(R.id.tv_symbol_name);
				TextView tv_value = (TextView) view2
						.findViewById(R.id.tv_value);

				TextView tv_open2 = (TextView) view2
						.findViewById(R.id.tv_open2);
				TextView tv_high = (TextView) view2.findViewById(R.id.tv_high);
				TextView tv_low = (TextView) view2.findViewById(R.id.tv_low);

				TextView tv_prev_close = (TextView) view2
						.findViewById(R.id.tv_prev_close);
				TextView tv_high52W = (TextView) view2
						.findViewById(R.id.tv_high52W);
				TextView tv_low52W = (TextView) view2
						.findViewById(R.id.tv_low52W);

				tv_last_trade.setText(""
						+ contentGetSymbolByNameSet100.get("last_trade"));
				tv_change.setText(""
						+ contentGetSymbolByNameSet100.get("change"));
				tv_percentChange.setText(""
						+ contentGetSymbolByNameSet100.get("percentChange")
						+ "%");

				tv_symbol_name.setText(""
						+ contentGetSymbolByNameSet100.get("symbol_name"));
				tv_value.setText("" + contentGetSymbolByNameSet100.get("value"));

				tv_open2.setText("" + contentGetSymbolByNameSet100.get("open2"));
				tv_high.setText("" + contentGetSymbolByNameSet100.get("high"));
				tv_low.setText("" + contentGetSymbolByNameSet100.get("low"));

				tv_prev_close.setText(""
						+ contentGetSymbolByNameSet100.get("prev_close"));
				tv_high52W.setText(""
						+ contentGetSymbolByNameSet100.get("high52W"));
				tv_low52W.setText(""
						+ contentGetSymbolByNameSet100.get("low52W"));
				
				String urlChart = "" + url_SnapShotSet100;
				wv_chart.getSettings().setJavaScriptEnabled(true);
				wv_chart.getSettings().setUseWideViewPort(true);
				wv_chart.getSettings().setDomStorageEnabled(true);
				wv_chart.setInitialScale(100);
				wv_chart.loadUrl(urlChart);
				
				li_list_set.addView(view2);
			}
			// sethd
			if (contentGetSymbolByNameSetHd != null) {
				View view2 = ((Activity) context).getLayoutInflater().inflate(
						R.layout.index_home_set, null);

				WebView wv_chart = (WebView) view2.findViewById(R.id.wv_chart);
				TextView tv_last_trade = (TextView) view2
						.findViewById(R.id.tv_last_trade);
				TextView tv_change = (TextView) view2
						.findViewById(R.id.tv_change);
				TextView tv_percentChange = (TextView) view2
						.findViewById(R.id.tv_percentChange);

				TextView tv_symbol_name = (TextView) view2
						.findViewById(R.id.tv_symbol_name);
				TextView tv_value = (TextView) view2
						.findViewById(R.id.tv_value);

				TextView tv_open2 = (TextView) view2
						.findViewById(R.id.tv_open2);
				TextView tv_high = (TextView) view2.findViewById(R.id.tv_high);
				TextView tv_low = (TextView) view2.findViewById(R.id.tv_low);

				TextView tv_prev_close = (TextView) view2
						.findViewById(R.id.tv_prev_close);
				TextView tv_high52W = (TextView) view2
						.findViewById(R.id.tv_high52W);
				TextView tv_low52W = (TextView) view2
						.findViewById(R.id.tv_low52W);

				tv_last_trade.setText(""
						+ contentGetSymbolByNameSetHd.get("last_trade"));
				tv_change.setText(""
						+ contentGetSymbolByNameSetHd.get("change"));
				tv_percentChange.setText(""
						+ contentGetSymbolByNameSetHd.get("percentChange")
						+ "%");

				tv_symbol_name.setText(""
						+ contentGetSymbolByNameSetHd.get("symbol_name"));
				tv_value.setText("" + contentGetSymbolByNameSetHd.get("value"));

				tv_open2.setText("" + contentGetSymbolByNameSetHd.get("open2"));
				tv_high.setText("" + contentGetSymbolByNameSetHd.get("high"));
				tv_low.setText("" + contentGetSymbolByNameSetHd.get("low"));

				tv_prev_close.setText(""
						+ contentGetSymbolByNameSetHd.get("prev_close"));
				tv_high52W.setText(""
						+ contentGetSymbolByNameSetHd.get("high52W"));
				tv_low52W.setText(""
						+ contentGetSymbolByNameSetHd.get("low52W"));
				
				String urlChart = "" + url_SnapShotSetHd;
				wv_chart.getSettings().setJavaScriptEnabled(true);
				wv_chart.getSettings().setUseWideViewPort(true);
				wv_chart.getSettings().setDomStorageEnabled(true);
				wv_chart.setInitialScale(100);
				wv_chart.loadUrl(urlChart);
				
				li_list_set.addView(view2);
			}
			// mai
			if (contentGetSymbolByNameMai != null) {
				View view2 = ((Activity) context).getLayoutInflater().inflate(
						R.layout.index_home_set, null);

				WebView wv_chart = (WebView) view2.findViewById(R.id.wv_chart);
				TextView tv_last_trade = (TextView) view2
						.findViewById(R.id.tv_last_trade);
				TextView tv_change = (TextView) view2
						.findViewById(R.id.tv_change);
				TextView tv_percentChange = (TextView) view2
						.findViewById(R.id.tv_percentChange);

				TextView tv_symbol_name = (TextView) view2
						.findViewById(R.id.tv_symbol_name);
				TextView tv_value = (TextView) view2
						.findViewById(R.id.tv_value);

				TextView tv_open2 = (TextView) view2
						.findViewById(R.id.tv_open2);
				TextView tv_high = (TextView) view2.findViewById(R.id.tv_high);
				TextView tv_low = (TextView) view2.findViewById(R.id.tv_low);

				TextView tv_prev_close = (TextView) view2
						.findViewById(R.id.tv_prev_close);
				TextView tv_high52W = (TextView) view2
						.findViewById(R.id.tv_high52W);
				TextView tv_low52W = (TextView) view2
						.findViewById(R.id.tv_low52W);

				tv_last_trade.setText(""
						+ contentGetSymbolByNameMai.get("last_trade"));
				tv_change.setText("" + contentGetSymbolByNameMai.get("change"));
				tv_percentChange.setText(""
						+ contentGetSymbolByNameMai.get("percentChange") + "%");

				tv_symbol_name.setText(""
						+ contentGetSymbolByNameMai.get("symbol_name"));
				tv_value.setText("" + contentGetSymbolByNameMai.get("value"));

				tv_open2.setText("" + contentGetSymbolByNameMai.get("open2"));
				tv_high.setText("" + contentGetSymbolByNameMai.get("high"));
				tv_low.setText("" + contentGetSymbolByNameMai.get("low"));

				tv_prev_close.setText(""
						+ contentGetSymbolByNameMai.get("prev_close"));
				tv_high52W.setText(""
						+ contentGetSymbolByNameMai.get("high52W"));
				tv_low52W.setText("" + contentGetSymbolByNameMai.get("low52W"));
				
				String urlChart = "" + url_SnapShotMai;
				wv_chart.getSettings().setJavaScriptEnabled(true);
				wv_chart.getSettings().setUseWideViewPort(true);
				wv_chart.getSettings().setDomStorageEnabled(true);
				wv_chart.setInitialScale(100);
				wv_chart.loadUrl(urlChart);
				
				li_list_set.addView(view2);
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ====================== init ProgressBar =============
	private ProgressWheel progressBar1, progressBar2;
	private ProgressWheel wheelTxt;
	private boolean wasSpinning = false;
	private SeekBar seekBarProgressAmount;
	private Button btnSpin, btnIncrement;

	private void initProgressBar() {
		progressBar1 = (ProgressWheel) rootView.findViewById(R.id.progressBar1);
		progressBar2 = (ProgressWheel) rootView.findViewById(R.id.progressBar2);
		progressBar1.incrementProgress(150);
		progressBar2.incrementProgress(200);
		// wheelTxt.setText("150%");

		// seekBarProgressAmount = (SeekBar)
		// rootView.findViewById(R.id.progressAmount);
		// btnSpin = (Button) rootView.findViewById(R.id.btn_spin);
		// btnIncrement = (Button) rootView.findViewById(R.id.btn_increment);
		//
		// seekBarProgressAmount.setOnSeekBarChangeListener(new
		// ProgressUpdater(pwOne));
		// btnSpin.setOnClickListener(new SpinListener(pwOne,
		// btnSpin,btnIncrement, seekBarProgressAmount));
		// // pwOne.incrementProgress(250);
		// btnIncrement.setOnClickListener(new OnClickListener() {
		// public void onClick(View v) {
		// pwOne.incrementProgress(36);
		// }
		// });
	}

	private static final class SpinListener implements OnClickListener {

		private boolean isRunning = false;
		private final ProgressWheel wheel;
		private final Button spinButton, incButton;
		private final SeekBar seekBar;
		private int cachedProgress = 0;

		SpinListener(ProgressWheel wheel, Button spinButton, Button incButton,
				SeekBar seekBar) {
			this.wheel = wheel;
			this.spinButton = spinButton;
			this.incButton = incButton;
			this.seekBar = seekBar;
		}

		@Override
		public void onClick(View button) {
			isRunning = !isRunning;
			wheel.setText("250");
			if (isRunning) {
				cachedProgress = wheel.getProgress();
				wheel.resetCount();
				wheel.setText("Spinning...");
				wheel.startSpinning();
				spinButton.setText("Stop spinning");
			} else {
				spinButton.setText("Start spinning");
				wheel.setText("");
				wheel.stopSpinning();
				wheel.setProgress(cachedProgress);
			}
			seekBar.setEnabled(!isRunning);
			incButton.setEnabled(!isRunning);
		}
	}

	public static int pxFromDp(final Context context, final float dp) {
		return (int) (dp * context.getResources().getDisplayMetrics().density);
	}

	private static class ProgressUpdater implements
			SeekBar.OnSeekBarChangeListener {

		private final ProgressWheel wheel;

		ProgressUpdater(ProgressWheel wheel) {
			this.wheel = wheel;
		}

		@Override
		public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
			double progress = 360.0 * (seekBar.getProgress() / 100.0);
			wheel.setProgress((int) progress);
		}

		@Override
		public void onStartTrackingTouch(SeekBar seekBar) {
		}

		@Override
		public void onStopTrackingTouch(SeekBar seekBar) {
		}
	}

	// ============== Load Data all =============
	private void getDataAll() {
		loadData resp = new loadData();
		resp.execute();
	}

	public class loadData extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;
		// ======= json ========
		private JSONObject jsonGetSymbolBynameSet;
		private JSONObject jsonGetSymbolBynameSet50;
		private JSONObject jsonGetSymbolBynameSet100;
		private JSONObject jsonGetSymbolBynameSetHd;
		private JSONObject jsonGetSymbolBynameMai;

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
			String url_GetSymbolByNameSet = SplashScreen.url_bidschart+"/service/getSymbolByName?symbol=.set";
			String url_GetSymbolByNameSet50 = SplashScreen.url_bidschart+"/service/getSymbolByName?symbol=.set50";
			String url_GetSymbolByNameSet100 = SplashScreen.url_bidschart+"/service/getSymbolByName?symbol=.set100";
			String url_GetSymbolByNameSetHd = SplashScreen.url_bidschart+"/service/getSymbolByName?symbol=.setHD";
			String url_GetSymbolByNameMai = SplashScreen.url_bidschart+"/service/getSymbolByName?symbol=.mai";

			try {
				// ======= Ui Home ========
				jsonGetSymbolBynameSet = ReadJson
						.readJsonObjectFromUrl(url_GetSymbolByNameSet);
				jsonGetSymbolBynameSet50 = ReadJson
						.readJsonObjectFromUrl(url_GetSymbolByNameSet50);
				jsonGetSymbolBynameSet100 = ReadJson
						.readJsonObjectFromUrl(url_GetSymbolByNameSet100);
				jsonGetSymbolBynameSetHd = ReadJson
						.readJsonObjectFromUrl(url_GetSymbolByNameSetHd);
				jsonGetSymbolBynameMai = ReadJson
						.readJsonObjectFromUrl(url_GetSymbolByNameMai);
			} catch (IOException e1) {
				connectionError = true;
				jsonGetSymbolBynameSet = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonGetSymbolBynameSet = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonGetSymbolBynameSet = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (jsonGetSymbolBynameSet != null) {
					try {
						// get content
						contentGetSymbolByNameSet = jsonGetSymbolBynameSet
								.getJSONObject("dataAll");
						contentGetSymbolByNameSet50 = jsonGetSymbolBynameSet50
								.getJSONObject("dataAll");
						contentGetSymbolByNameSet100 = jsonGetSymbolBynameSet100
								.getJSONObject("dataAll");
						contentGetSymbolByNameSetHd = jsonGetSymbolBynameSetHd
								.getJSONObject("dataAll");
						contentGetSymbolByNameMai = jsonGetSymbolBynameMai
								.getJSONObject("dataAll");

						// josSymbolByName.put(contentGetSymbolByNameSet);
						// josSymbolByName.put(contentGetSymbolByNameSet50);
						// josSymbolByName.put(contentGetSymbolByNameSet100);
						// josSymbolByName.put(contentGetSymbolByNameSetHd);
						// josSymbolByName.put(contentGetSymbolByNameMai);

						initSetData(); // Initial set data

						// Log.v("contentGetSymbolByName ", ""
						// + josSymbolByName);

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.v("json newslist null", "newslist null");
				}
			} else {
			}
		}
	}

}
