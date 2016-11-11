package com.app.bids;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;
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
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

public class PagerWatchListDetailChart_old extends Fragment {

	static Context context;
	public static View rootView;

	// activity listener interface
	private OnPageListener pageListener;

	// FragmentChangeActivity.url_bidschart+"/watchlist?platform=mobile&user=10
	public static JSONArray contentGetWatchlists = null;

//	public static WebView wv_chart;
//	public static WebView wv_test;
	// String wv_url = SplashScreen.url_bidschart+"/iq/stx-mobile-2.html#.SET";
	// String wv_url = SplashScreen.url_bidschart+"/iq/stx-mobile-3.html#.SET";

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
				R.layout.pager_watchlist_detail_chart_old, container, false);
		
		return rootView;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		initReadFileIndicator(); // read file indicator
		initWebView(); // init webview
		
//		initTestWebView();
	}
	
	// ====== init webview ============
	public static LinearLayout li_view_page,li_test_wv, li_view_chart;

	public void initWebView() {

//		li_view.setVisibility(View.GONE);
//		wv_chart = (WebView) rootView.findViewById(R.id.wv_chart);		
//		wv_chart.getSettings().setLoadWithOverviewMode(true);
//		wv_chart.getSettings().setUseWideViewPort(true);
//		wv_chart.getSettings().setBuiltInZoomControls(true);
//		wv_chart.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
//		wv_chart.setWebViewClient(new myWebClient());
//		wv_chart.getSettings().setJavaScriptEnabled(true);
//		wv_chart.loadUrl(SplashScreen.url_bidschart
//		 + "/iq/stx-mobile-3.html#"
//		 + FragmentChangeActivity.strSymbolSelect);
		
		li_view_chart = (LinearLayout) rootView.findViewById(R.id.li_view_chart);
		li_view_page = (LinearLayout) rootView.findViewById(R.id.li_view_page);
		
		li_view_chart.addView(FragmentChangeActivity.wv_chartiq);
		FragmentChangeActivity.wv_chartiq.loadUrl("javascript:(function () { "
				+ "mobileControl.changeSymbol('"
				+ FragmentChangeActivity.strSymbolSelect + "');" + "})()");
		
//		setHeightChart();
		
		initMenuIqChart();
	}
	
	public static void setHeightChart(){
		WindowManager.LayoutParams params = new WindowManager.LayoutParams();
		params.width = FragmentChangeActivity.widthScreen;
		params.height = FragmentChangeActivity.heightScreen;
		li_view_chart.setLayoutParams(params);
		
//		Toast.makeText(context, "W : "+FragmentChangeActivity.widthScreen+ " H : "+FragmentChangeActivity.heightScreen, 0).show();  // 2392
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

	// ====== init menu ============
	LinearLayout li_top, li_right, li_right_hide_show;
	LinearLayout li_1d, li_1w, li_1m, li_5min, li_30min;
	TextView tv_1d, tv_1w, tv_1m, tv_5min, tv_30min;
	ImageView img_compare, img_indecator, img_countidear;

	public void initMenuIqChart() {
		// menu top
		li_top = (LinearLayout) rootView.findViewById(R.id.li_top);
		li_1d = (LinearLayout) rootView.findViewById(R.id.li_1d);
		li_1w = (LinearLayout) rootView.findViewById(R.id.li_1w);
		li_1m = (LinearLayout) rootView.findViewById(R.id.li_1m);
		li_5min = (LinearLayout) rootView.findViewById(R.id.li_5min);
		li_30min = (LinearLayout) rootView.findViewById(R.id.li_30min);

		tv_1d = (TextView) rootView.findViewById(R.id.tv_1d);
		tv_1w = (TextView) rootView.findViewById(R.id.tv_1w);
		tv_1m = (TextView) rootView.findViewById(R.id.tv_1m);
		tv_5min = (TextView) rootView.findViewById(R.id.tv_5min);
		tv_30min = (TextView) rootView.findViewById(R.id.tv_30min);

		li_1d.setOnClickListener(onClickListenerMenuTop);
		li_1w.setOnClickListener(onClickListenerMenuTop);
		li_1m.setOnClickListener(onClickListenerMenuTop);
		li_5min.setOnClickListener(onClickListenerMenuTop);
		li_30min.setOnClickListener(onClickListenerMenuTop);

		// menu right
		li_right = (LinearLayout) rootView.findViewById(R.id.li_right);
		li_right_hide_show = (LinearLayout) rootView
				.findViewById(R.id.li_right_hide_show);
		img_compare = (ImageView) rootView.findViewById(R.id.img_compare);
		img_indecator = (ImageView) rootView.findViewById(R.id.img_indecator);
		img_countidear = (ImageView) rootView.findViewById(R.id.img_countidear);

		img_compare.setOnClickListener(onClickListenerMenuRight);
		img_indecator.setOnClickListener(onClickListenerMenuRight);
		img_countidear.setOnClickListener(onClickListenerMenuRight);

	}

	// ******************* menu top ******************************
	private OnClickListener onClickListenerMenuTop = new OnClickListener() {
		@Override
		public void onClick(final View v) {

			li_1d.setBackgroundResource(Color.TRANSPARENT);
			li_1w.setBackgroundResource(Color.TRANSPARENT);
			li_1m.setBackgroundResource(Color.TRANSPARENT);
			li_5min.setBackgroundResource(Color.TRANSPARENT);
			li_30min.setBackgroundResource(Color.TRANSPARENT);

			tv_1d.setTextColor(getResources().getColor(R.color.c_success));
			tv_1w.setTextColor(getResources().getColor(R.color.c_success));
			tv_1m.setTextColor(getResources().getColor(R.color.c_success));
			tv_5min.setTextColor(getResources().getColor(R.color.c_success));
			tv_30min.setTextColor(getResources().getColor(R.color.c_success));

			switch (v.getId()) {
			case R.id.li_1d:
				tv_1d.setTextColor(getResources().getColor(R.color.bg_default));
				li_1d.setBackgroundColor(getResources().getColor(
						R.color.c_success));
				FragmentChangeActivity.wv_chartiq.loadUrl("javascript:(function () { "
						+ "mobileControl.changePeriod('day');" + "})()");
				break;
			case R.id.li_1w:
				tv_1w.setTextColor(getResources().getColor(R.color.bg_default));
				li_1w.setBackgroundColor(getResources().getColor(
						R.color.c_success));
				FragmentChangeActivity.wv_chartiq.loadUrl("javascript:(function () { "
						+ "mobileControl.changePeriod('week');" + "})()");
				break;
			case R.id.li_1m:
				tv_1m.setTextColor(getResources().getColor(R.color.bg_default));
				li_1m.setBackgroundColor(getResources().getColor(
						R.color.c_success));
				FragmentChangeActivity.wv_chartiq.loadUrl("javascript:(function () { "
						+ "mobileControl.changePeriod('month');" + "})()");
				break;
			case R.id.li_5min:
				tv_5min.setTextColor(getResources()
						.getColor(R.color.bg_default));
				li_5min.setBackgroundColor(getResources().getColor(
						R.color.c_success));
				FragmentChangeActivity.wv_chartiq.loadUrl("javascript:(function () { "
						+ "mobileControl.changePeriod(5);" + "})()");
				break;
			case R.id.li_30min:
				tv_30min.setTextColor(getResources().getColor(
						R.color.bg_default));
				li_30min.setBackgroundColor(getResources().getColor(
						R.color.c_success));
				FragmentChangeActivity.wv_chartiq.loadUrl("javascript:(function () { "
						+ "mobileControl.changePeriod(30);" + "})()");
				break;
			default:
				break;
			}
		}
	};

	// ******************* menu right ******************************
	private OnClickListener onClickListenerMenuRight = new OnClickListener() {
		@Override
		public void onClick(final View v) {

			switch (v.getId()) {
			case R.id.img_compare: // color
				dialogColorPicker = new ColorPickerDialog(context);
				dialogColorPicker.show();
				break;
			case R.id.img_indecator: // indecator
				showDialogStudies();
				break;
			case R.id.img_countidear: // style

				// showDialogChart();
				break;
			default:
				break;
			}
		}
	};

	// ******************* chart dialog ******************************
	private AlertDialog alertDialogSelectTool;

	public void showDialogSelectTool() {
		LayoutInflater li = LayoutInflater.from(context);
		View viewDl = li.inflate(R.layout.dialog_selecttool, null);

		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder.setView(viewDl);
		// left
		((TextView) viewDl.findViewById(R.id.tv_None))
				.setOnClickListener(onClickListenerSelectTool);
		((TextView) viewDl.findViewById(R.id.tv_Crosshairs))
				.setOnClickListener(onClickListenerSelectTool);
		((TextView) viewDl.findViewById(R.id.tv_segment))
				.setOnClickListener(onClickListenerSelectTool);
		((TextView) viewDl.findViewById(R.id.tv_continuous))
				.setOnClickListener(onClickListenerSelectTool);
		((TextView) viewDl.findViewById(R.id.tv_ray))
				.setOnClickListener(onClickListenerSelectTool);
		((TextView) viewDl.findViewById(R.id.tv_line))
				.setOnClickListener(onClickListenerSelectTool);
		((TextView) viewDl.findViewById(R.id.tv_horizontal))
				.setOnClickListener(onClickListenerSelectTool);
		((TextView) viewDl.findViewById(R.id.tv_vertical))
				.setOnClickListener(onClickListenerSelectTool);
		((TextView) viewDl.findViewById(R.id.tv_rectangle))
				.setOnClickListener(onClickListenerSelectTool);
		((TextView) viewDl.findViewById(R.id.tv_ellipse))
				.setOnClickListener(onClickListenerSelectTool);
		((TextView) viewDl.findViewById(R.id.tv_shape))
				.setOnClickListener(onClickListenerSelectTool);
		// right
		((TextView) viewDl.findViewById(R.id.tv_measure))
				.setOnClickListener(onClickListenerSelectTool);
		((TextView) viewDl.findViewById(R.id.tv_channel))
				.setOnClickListener(onClickListenerSelectTool);
		((TextView) viewDl.findViewById(R.id.tv_freeform))
				.setOnClickListener(onClickListenerSelectTool);
		((TextView) viewDl.findViewById(R.id.tv_fibonacci))
				.setOnClickListener(onClickListenerSelectTool);
		((TextView) viewDl.findViewById(R.id.tv_gartley))
				.setOnClickListener(onClickListenerSelectTool);
		((TextView) viewDl.findViewById(R.id.tv_pitchfork))
				.setOnClickListener(onClickListenerSelectTool);
		((TextView) viewDl.findViewById(R.id.tv_annotation))
				.setOnClickListener(onClickListenerSelectTool);
		((TextView) viewDl.findViewById(R.id.tv_callout))
				.setOnClickListener(onClickListenerSelectTool);

		// create alert dialog
		alertDialogSelectTool = alertDialogBuilder.create();
		// show it
		alertDialogSelectTool.show();
	}

	// ******************* on click chart ******************************
	private OnClickListener onClickListenerSelectTool = new OnClickListener() {
		@Override
		public void onClick(final View v) {

			String tag = v.getTag().toString();
			// Toast.makeText(getApplicationContext(), "" + tag,
			// Toast.LENGTH_SHORT).show();

			// wv_chart.loadUrl("javascript:(function () { "
			// + "mobileControl.changeChartType('" + tag + "');" + "})()");
			alertDialogSelectTool.dismiss();

		}
	};

	// ******************* read file indicator ******************************
	public static ArrayList<ChartIqGetIndicatorValueCatalog> list_getStudiesValue = new ArrayList<ChartIqGetIndicatorValueCatalog>();
	JSONArray jsonIndicator = null;

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
					list_getStudiesValue.add(ct);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
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

	// ******************* studies dialog ******************************
	private AlertDialog alertDialogStudies;

	public void showDialogStudies() {
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
								FragmentChangeActivity.wv_chartiq.loadUrl("javascript:(function () { "
										+ "mobileControl.addStudy('"
										+ tv_row_value.getTag() + "');"
										+ "})()");
								alertDialogStudies.dismiss();
							}
						});
						li_list_value.addView(viewValue);
					}
				}

				tv_row.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// changeRowcolor(v);
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
												alertDialogStudies.dismiss();
											}
										});

								li_list_value.addView(view2);
							}
						} catch (JSONException e2) {
							e2.printStackTrace();
						}
					}
				});

				li_list_title.addView(view2);
			}

			// ********* search studiea value *********
			final ArrayList<ChartIqGetIndicatorValueCatalog> original_list;
			final ArrayList<ChartIqGetIndicatorValueCatalog> second_list;

			original_list = new ArrayList<ChartIqGetIndicatorValueCatalog>();
			original_list.addAll(list_getStudiesValue);
			second_list = new ArrayList<ChartIqGetIndicatorValueCatalog>();
			second_list.addAll(list_getStudiesValue);

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
											FragmentChangeActivity.wv_chartiq.loadUrl("javascript:(function () { "
													+ "mobileControl.addStudy('"
													+ tv_row_value.getTag()
													+ "');" + "})()");
											alertDialogStudies.dismiss();
										}
									});
							li_list_value.addView(viewValue);
						}
					} else {
					}
				}
			});

			// create alert dialog
			alertDialogStudies = alertDialogBuilder.create();

			// show it
			alertDialogStudies.show();

			// set width height dialog
			// DisplayMetrics displaymetrics = new DisplayMetrics();
			// getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			// int ScreenHeight = displaymetrics.heightPixels;
			// int ScreenWidth = displaymetrics.widthPixels;
			//
			// WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
			// lp.width = (int) (ScreenWidth * 0.9);
			// lp.height = (int) (ScreenHeight * 0.75);
			//
			// alertDialogStudies.getWindow().setAttributes(lp);

		} catch (JSONException e2) {
			e2.printStackTrace();
		}
	}

	// config rotation
	// @Override
	// public void onConfigurationChanged(Configuration newConfig) {
	// super.onConfigurationChanged(newConfig);
	// // Toast.makeText(getApplicationContext(), "" + newConfig.orientation,
	// // Toast.LENGTH_SHORT).show();
	//
	// // Checks the orientation of the screen
	// if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) { //
	// แนวนอน
	// li_top.setVisibility(View.VISIBLE);
	// li_right.setVisibility(View.VISIBLE);
	// li_right_hide_show.setVisibility(View.VISIBLE);
	// } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
	// // แนวตั้ง
	// li_top.setVisibility(View.GONE);
	// li_right.setVisibility(View.GONE);
	// }
	// }

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