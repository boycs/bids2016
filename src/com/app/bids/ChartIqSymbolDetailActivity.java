package com.app.bids;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;
import com.app.bids.colorpicker.ColorPickerDialog;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ChartIqSymbolDetailActivity extends Activity {

	private WebView wv_chart;
	String wv_url = SplashScreen.url_bidschart+ "/iq/stx-mobile.html#.SET";
	

//	private ArrayList<LinearLayout> row_color_list_select;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.symbol_detail_activity);

		initReadFileIndicator(); // read file indicator

		initMenu(); // init menu
		addItemsOnSpinnerPeriod(); // init spinner

		initWebView(); // init webview

		initGetSymbol(); // init get symbol
	}

	// ====== init Menu ============
	// bidschart.com/chartIQ/publish/SET:PTT
	// http://bidschart.com/game => studies

	// menu top
	LinearLayout li_top;
	Spinner spn_period;
	EditText et_search;
	ImageView img_compare, img_chart, img_selecttool, img_short, img_studies;

	// menu right
	LinearLayout li_right, li_right_hide_show;
	ImageView img_search_r, img_selecttool_r, img_short_r, img_studies_r,
			img_compare_r, img_chart_r;
	Spinner spn_period_r;

	// ******************* init menu ******************************
	Dialog dialogColorPicker; // color picker

	public void initMenu() {
		wv_chart = (WebView) findViewById(R.id.wv_chart);

//		// menu top
//		li_top = (LinearLayout) findViewById(R.id.li_top);
//		spn_period = (Spinner) findViewById(R.id.spn_period);
//		et_search = (EditText) findViewById(R.id.et_search);
//		img_compare = (ImageView) findViewById(R.id.img_compare);
//		img_chart = (ImageView) findViewById(R.id.img_chart);
//		img_selecttool = (ImageView) findViewById(R.id.img_selecttool);
//		img_short = (ImageView) findViewById(R.id.img_short);
//		img_studies = (ImageView) findViewById(R.id.img_studies);
//
//		et_search.setOnClickListener(onClickListenerMenuTop);
//		img_compare.setOnClickListener(onClickListenerMenuTop);
//		img_chart.setOnClickListener(onClickListenerMenuTop);
//		img_selecttool.setOnClickListener(onClickListenerMenuTop);
//		img_short.setOnClickListener(onClickListenerMenuTop);
//		img_studies.setOnClickListener(onClickListenerMenuTop);
//
//		// menu right
//		spn_period_r = (Spinner) findViewById(R.id.spn_period_r);
//		li_right = (LinearLayout) findViewById(R.id.li_right);
//		li_right_hide_show = (LinearLayout) findViewById(R.id.li_right_hide_show);
//		img_search_r = (ImageView) findViewById(R.id.img_search_r);
//		img_compare_r = (ImageView) findViewById(R.id.img_compare_r);
//		img_chart_r = (ImageView) findViewById(R.id.img_chart_r);
//		img_selecttool_r = (ImageView) findViewById(R.id.img_selecttool_r);
//		img_short_r = (ImageView) findViewById(R.id.img_short_r);
//		img_studies_r = (ImageView) findViewById(R.id.img_studies_r);
//
//		li_right.setVisibility(View.GONE);
//		li_right.setOnTouchListener(mActivitySwipeMotion);
//
//		img_search_r.setOnClickListener(onClickListenerMenuRight);
//		img_compare_r.setOnClickListener(onClickListenerMenuRight);
//		img_chart_r.setOnClickListener(onClickListenerMenuRight);
//		img_selecttool_r.setOnClickListener(onClickListenerMenuRight);
//		img_studies_r.setOnClickListener(onClickListenerMenuRight);
//		img_short_r.setOnClickListener(onClickListenerMenuRight);
	}

	// ******************* menu top ******************************
//	private OnClickListener onClickListenerMenuTop = new OnClickListener() {
//		@Override
//		public void onClick(final View v) {
//
//			switch (v.getId()) {
//			case R.id.img_compare:
//				dialogColorPicker = new ColorPickerDialog(SymbolDetailActivity.this);
//				dialogColorPicker.show();
//				break;
//			case R.id.img_chart:
//				showDialogChart();
//				break;
//			case R.id.et_search:
//				showDialogSearch();
//				break;
//			case R.id.img_selecttool:
//				showDialogSelectTool();
//				break;
//			case R.id.img_short:
//				wv_chart.loadUrl("javascript:(function () { "
//						+ "mobileControl.enableShort();" + "})()");
//				break;
//			case R.id.img_studies:
//				showDialogStudies();
//				break;
//			default:
//				break;
//			}
//		}
//	};
//
//	// ******************* menu right ******************************
//	private OnClickListener onClickListenerMenuRight = new OnClickListener() {
//		@Override
//		public void onClick(final View v) {
//
//			switch (v.getId()) {
//			case R.id.img_compare_r:
//				dialogColorPicker = new ColorPickerDialog(SymbolDetailActivity.this);
//				dialogColorPicker.show();
//				break;
//			case R.id.img_chart_r:
//				showDialogChart();
//				break;
//			case R.id.img_search_r:
//				showDialogSearch();
//				break;
//			case R.id.img_selecttool_r:
//				showDialogSelectTool();
//				break;
//			case R.id.img_short_r:
//				wv_chart.loadUrl("javascript:(function () { "
//						+ "mobileControl.enableShort();" + "})()");
//				break;
//			case R.id.img_studies_r:
//				showDialogStudies();
//				break;
//			default:
//				break;
//			}
//		}
//	};

	// ******************* menu right touch ******************************
//	ActivitySwipeMotion mActivitySwipeMotion = new ActivitySwipeMotion(this) {
//		public void onSwipeLeft() {
//		    final Animation tabBlockHolderAnimation = AnimationUtils.loadAnimation(SymbolDetailActivity.this, R.anim.right_to_center);
//
//		    tabBlockHolderAnimation.setFillAfter(true);
//		    li_right_hide_show.startAnimation(tabBlockHolderAnimation);
//		}
//
//		public void onSwipeRight() {
//		    final Animation tabBlockHolderAnimation = AnimationUtils.loadAnimation(SymbolDetailActivity.this, R.anim.center_to_right);
//
//		    tabBlockHolderAnimation.setFillAfter(true);
//		    li_right_hide_show.startAnimation(tabBlockHolderAnimation);
//		}
//
//		public void onSwipeDown() {
//		}
//
//		public void onSwipeUp() {
//		}
//		
//	};

	// ���¡ js � webview

	// function test
	// mobileControl.enableShort()
	// mobileControl.disableWidget()

	// mobileControl.changeSymbol('DCORP')
	// http://www.bidschart.com/iq/stx-mobile.html#TRUE

	// �͹��� �� day week month 5 30
	// mobileControl.changePeriod(5)
	// mobileControl.changePeriod('month')

	// ====== init webview ============
	public void initWebView() {

		wv_chart.getSettings().setLoadWithOverviewMode(true);
		wv_chart.getSettings().setUseWideViewPort(true);
		wv_chart.getSettings().setBuiltInZoomControls(true);
		wv_chart.getSettings().setJavaScriptEnabled(true);
		wv_chart.loadUrl(wv_url);
		wv_chart.setWebViewClient(new myWebClient());

	}

	// ====== init spinner ============
	public void addItemsOnSpinnerPeriod() {

		List<String> list = new ArrayList<String>();
		list.add("1 D");
		list.add("1 W");
		list.add("1 M");
		list.add("5 min");
		list.add("30 min");

		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, list);
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		spn_period.setAdapter(dataAdapter);
		spn_period
				.setOnItemSelectedListener(new OnItemSelectedListenerPeriod());
		// menu reight
		spn_period_r.setAdapter(dataAdapter);
		spn_period_r
				.setOnItemSelectedListener(new OnItemSelectedListenerPeriod());
	}

	public class OnItemSelectedListenerPeriod implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// Toast.makeText(getApplicationContext(), ""+position,
			// Toast.LENGTH_SHORT).show();
			if (position == 0) {
				wv_chart.loadUrl("javascript:(function () { "
						+ "mobileControl.changePeriod('day');" + "})()");
			} else if (position == 1) {
				wv_chart.loadUrl("javascript:(function () { "
						+ "mobileControl.changePeriod('week');" + "})()");
			} else if (position == 2) {
				wv_chart.loadUrl("javascript:(function () { "
						+ "mobileControl.changePeriod('month');" + "})()");
			} else if (position == 3) {
				wv_chart.loadUrl("javascript:(function () { "
						+ "mobileControl.changePeriod(5);" + "})()");
			} else if (position == 4) {
				wv_chart.loadUrl("javascript:(function () { "
						+ "mobileControl.changePeriod(30);" + "})()");
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}

	}

	// ====== webview client ============
	public class myWebClient extends WebViewClient {

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			// TODO Auto-generated method stub
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub

			view.loadUrl(url);
			return true;

		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);

			// progress.dismiss();
		}
	}

	// ******************* Get Symbol ******************************
	public static JSONArray contentGetSymbol = null;
	public static ArrayList<String> arr_getsymbol = new ArrayList<String>();
	public static ArrayList<CatalogGetSymbol> list_getSymbol = new ArrayList<CatalogGetSymbol>();

	private void initGetSymbol() {
		loadSymbol resp = new loadSymbol();
		resp.execute();
	}

	public class loadSymbol extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;

		private JSONObject jsonGetSymbol;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {

			String url_GetSymbol = SplashScreen.url_bidschart+ "/service/getSymbol";

			try {

				jsonGetSymbol = ReadJson.readJsonObjectFromUrl(url_GetSymbol);

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
				if (jsonGetSymbol != null) {
					try {

						// get content home
						contentGetSymbol = jsonGetSymbol
								.getJSONArray("dataAll");

						Log.v("contentGetSymbol length : ", ""
								+ contentGetSymbol.length());

						// // arr symbol init
						if (contentGetSymbol != null) {
							try {
								for (int i = 0; i < contentGetSymbol.length(); i++) {
									JSONObject jso = contentGetSymbol
											.getJSONObject(i);
									// arr_getsymbol.add(jso
									// .getString("symbol"));

									CatalogGetSymbol cg = new CatalogGetSymbol();
									cg.symbol = jso.getString("symbol");
									cg.market_id = jso.getString("market_id");
									cg.symbol_fullname_eng = jso
											.getString("symbol_fullname_eng");
									cg.symbol_fullname_thai = jso
											.getString("symbol_fullname_thai");
									cg.last_trade = jso.getString("last_trade");
									cg.volume = jso.getString("volume");
									cg.change = jso.getString("change");
									cg.percentChange = jso
											.getString("percentChange");

									list_getSymbol.add(cg);
								}

								Log.v("list_getSymbol size", ""
										+ list_getSymbol.size());

								// JSONArray
							} catch (JSONException e) {
								e.printStackTrace();
							}
						} else {
							Log.v("json null", "symbol null");
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.v("json newslist null", "symbol null2");
				}
			} else {
			}

		}
	}

	// ******************* chart dialog ******************************
	private AlertDialog alertDialogSelectTool;

	public void showDialogSelectTool() {
		LayoutInflater li = LayoutInflater.from(this);
		View viewDl = li.inflate(R.layout.dialog_selecttool, null);

		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);
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

	// ******************* chart dialog ******************************
	private AlertDialog alertDialogChart;

	public void showDialogChart() {
//		LayoutInflater li = LayoutInflater.from(this);
//		View viewDl = li.inflate(R.layout.dialog_chart, null);
//
//		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//				this);
//		alertDialogBuilder.setView(viewDl);
//		// style
//		((TextView) viewDl.findViewById(R.id.tv_candle))
//				.setOnClickListener(onClickListenerChart);
//		((TextView) viewDl.findViewById(R.id.tv_bar))
//				.setOnClickListener(onClickListenerChart);
//		((TextView) viewDl.findViewById(R.id.tv_coloredbar))
//				.setOnClickListener(onClickListenerChart);
//		((TextView) viewDl.findViewById(R.id.tv_line))
//				.setOnClickListener(onClickListenerChart);
//		((TextView) viewDl.findViewById(R.id.tv_hollowcandle))
//				.setOnClickListener(onClickListenerChart);
//		((TextView) viewDl.findViewById(R.id.tv_mountain))
//				.setOnClickListener(onClickListenerChart);
//		((TextView) viewDl.findViewById(R.id.tv_coloredline))
//				.setOnClickListener(onClickListenerChart);
//		((TextView) viewDl.findViewById(R.id.tv_baseelinedelta))
//				.setOnClickListener(onClickListenerChart);
//		// type
//		((TextView) viewDl.findViewById(R.id.tv_heikinashi))
//				.setOnClickListener(onClickListenerChart);
//		((TextView) viewDl.findViewById(R.id.tv_kagi))
//				.setOnClickListener(onClickListenerChart);
//		((TextView) viewDl.findViewById(R.id.tv_linebreak))
//				.setOnClickListener(onClickListenerChart);
//		((TextView) viewDl.findViewById(R.id.tv_pointfigure))
//				.setOnClickListener(onClickListenerChart);
//		((TextView) viewDl.findViewById(R.id.tv_rangebars))
//				.setOnClickListener(onClickListenerChart);
//		((TextView) viewDl.findViewById(R.id.tv_renko))
//				.setOnClickListener(onClickListenerChart);
//		// create alert dialog
//		alertDialogChart = alertDialogBuilder.create();
//		// show it
//		alertDialogChart.show();
	}

	// ******************* on click chart ******************************
	private OnClickListener onClickListenerChart = new OnClickListener() {
		@Override
		public void onClick(final View v) {

			String tag = v.getTag().toString();
			// Toast.makeText(getApplicationContext(), "" + tag,
			// Toast.LENGTH_SHORT).show();
			wv_chart.loadUrl("javascript:(function () { "
					+ "mobileControl.changeChartType('" + tag + "');" + "})()");
			alertDialogChart.dismiss();

		}
	};

	// ******************* read file indicator ******************************
	public static ArrayList<ChartIqGetIndicatorValueCatalog> list_getStudiesValue = new ArrayList<ChartIqGetIndicatorValueCatalog>();
	JSONArray jsonIndicator = null;

	public void initReadFileIndicator() {
		try {
			String jsonArray = AssetJSONFile("indicator.json",
					getApplicationContext());

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
//			row_color_list_select.clear();
			LayoutInflater li = LayoutInflater.from(this);
			View viewDl = li.inflate(R.layout.dialog_chartiq_indicator_list, null);

			final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
					this);
			alertDialogBuilder.setView(viewDl);

			final EditText et_search = (EditText) viewDl
					.findViewById(R.id.et_search);

			LinearLayout li_list_title = (LinearLayout) viewDl
					.findViewById(R.id.li_list_title);
			final LinearLayout li_list_value = (LinearLayout) viewDl
					.findViewById(R.id.li_list_value);
			li_list_title.removeAllViews();

			for (int i = 0; i < jsonIndicator.length(); i++) {
				View view2 = getLayoutInflater().inflate(
						R.layout.row_dialog_studies, null);

				TextView tv_row = (TextView) view2.findViewById(R.id.tv_row);
//				row_color_list_select.add(tv_row);
				
				JSONObject jsoIndex = jsonIndicator.getJSONObject(i);
				final JSONArray jsaValue = jsoIndex.getJSONArray("value");

				tv_row.setText("" + jsoIndex.getString("title") + " ("
						+ jsaValue.length() + ")");
				tv_row.setTag("" + jsoIndex.getString("key"));

				// list value begin
				if (i == 0) {
					li_list_value.removeAllViews();
					for (int j = 0; j < jsaValue.length(); j++) {

						View viewValue = getLayoutInflater().inflate(
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
								wv_chart.loadUrl("javascript:(function () { "
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
//						changeRowcolor(v);
						li_list_value.removeAllViews();
						try {
							for (int j = 0; j < jsaValue.length(); j++) {
								View view2 = getLayoutInflater().inflate(
										R.layout.row_dialog_studies, null);

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
												wv_chart.loadUrl("javascript:(function () { "
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
							View viewValue = getLayoutInflater().inflate(
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
											wv_chart.loadUrl("javascript:(function () { "
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
			DisplayMetrics displaymetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			int ScreenHeight = displaymetrics.heightPixels;
			int ScreenWidth = displaymetrics.widthPixels;

			WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
			lp.width = (int) (ScreenWidth * 0.9);
			lp.height = (int) (ScreenHeight * 0.75);
			
			alertDialogStudies.getWindow().setAttributes(lp);

		} catch (JSONException e2) {
			e2.printStackTrace();
		}
	}

//	public void changeRowcolor(View v) {
//		for (int i = 0; i < row_color_list_select.size(); i++) {
//			if (i % 2 == 0) {
//				row_list.get(i).setBackgroundColor(
//						Color.parseColor("#88b7b7b7"));
//			} else {
//			row_color_list_select.get(i).setBackgroundColor(Color.TRANSPARENT);
//			}
//		}
//		v.setBackgroundColor(getResources().getColor(R.color.bg_dialog_value));
//	}
	
	// config rotation
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Toast.makeText(getApplicationContext(), "" + newConfig.orientation,
		// Toast.LENGTH_SHORT).show();

		// Checks the orientation of the screen
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) { // �ǹ͹
			li_top.setVisibility(View.GONE);
			li_right.setVisibility(View.VISIBLE);
			li_right_hide_show.setVisibility(View.VISIBLE);
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) { // �ǵ��
			li_top.setVisibility(View.VISIBLE);
			li_right.setVisibility(View.GONE);
		}
	}

	private static final int TIME_INTERVAL = 2000;
	private long mBackPressed;

	public void onBackPressed() {

		if (wv_chart.canGoBack()) {

			wv_chart.goBack();

		} else {
			if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
				nextActivity();
				// super.onBackPressed();
				// return;
			} else {
				// initWebView(); // init webview
				Toast.makeText(getBaseContext(),
						"กดกลับอีกครั้งเพื่อปิดโปรแกรม", Toast.LENGTH_SHORT)
						.show();
			}
			mBackPressed = System.currentTimeMillis();
		}

	}

	public void nextActivity() {
		finish();
		// if (iAd.isLoaded()) {
		// iAd.show();
		// } else {
		// Intent intent = new Intent(Intent.ACTION_MAIN);
		// intent.addCategory(Intent.CATEGORY_HOME);
		// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// startActivity(intent);
		// }
	}

}
