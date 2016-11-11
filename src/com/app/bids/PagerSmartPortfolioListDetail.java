package com.app.bids;

import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;
import com.app.bids.PagerWatchList.setRemoveFavorite;
import com.app.bids.PagerWatchListDetailNews.loadAll;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.PercentFormatter;
import com.github.mikephil.charting.utils.ValueFormatter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class PagerSmartPortfolioListDetail extends Fragment implements
		OnSeekBarChangeListener, OnChartValueSelectedListener {

	static Context context;
	public static View rootView;

	static Dialog dialogLoading;

	private PieChart mChartPie;
	private BarChart mChartBar;

	public static JSONObject contentGetDetailList = null;

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
				R.layout.pager_smartportfolio_list_detail, container, false);

		return rootView;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// --------- dialogLoading
		dialogLoading = new Dialog(context);
		dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogLoading.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialogLoading.setContentView(R.layout.progress_bar);
		dialogLoading.setCancelable(false);
		dialogLoading.setCanceledOnTouchOutside(false);
		// dialogLoading.show();

		// if (FragmentChangeActivity.contentGetSmartPortfolioList != null) {
		// initSetData();
		// } else {
		initLoadData();
		// }

		((LinearLayout) rootView.findViewById(R.id.li_back))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						switchFragment(new PagerSmartPortfolio());
					}
				});

		initSearchLayout(); // layout search
	}

	// ============== Load initLoadData =========================
	private void initLoadData() {
		loadAll resp = new loadAll();
		resp.execute();
	}

	private class loadAll extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;
		// ======= Ui Newds ========
		private JSONObject jsonGetData;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialogLoading.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// http://realtime.bidschart.com/service/v2/getEquityList?user_id=1040&timestamp=1468832491
			String url_GetData = "";

			if (FragmentChangeActivity.strSymbolSelect.equals("EQUITY")) {
				url_GetData = SplashScreen.url_bidschart
						+ "/service/v2/getEquityList?user_id="
						+ SplashScreen.userModel.user_id + "&timestamp="
						+ timestamp;
			} else {
				url_GetData = SplashScreen.url_bidschart
						+ "/service/v2/getFundList?user_id="
						+ SplashScreen.userModel.user_id + "&cat="
						+ FragmentChangeActivity.strSymbolSelect
						+ "&timestamp=" + timestamp;
			}

			Log.v("url_ getPortfolioListDetail", "" + url_GetData);

			try {
				// ======= Ui News ========
				jsonGetData = ReadJson.readJsonObjectFromUrl(url_GetData);
			} catch (IOException e1) {
				connectionError = true;
				jsonGetData = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonGetData = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonGetData = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (jsonGetData != null) {
					try {
						if ((jsonGetData.getString("status")).equals("ok")) {
							contentGetDetailList = jsonGetData;
							// Log.v("contentGetEquityList", ""
							// + contentGetEquityList);
							initSetData();
						} else {
							dialogLoading.dismiss();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					dialogLoading.dismiss();
					Log.v("json null", "jsonGetFundamental null");
				}
			} else {
				dialogLoading.dismiss();
				Log.v("json jsonGetFundamental null", "jsonGetFundamental null");
			}
		}
	}

	// ------------- line chart -------------
	public static JSONArray jsaPlPercent;

	// ============== init set data =========================
	JSONArray jsaResult;

	private void initSetData() {

		mChartPie = (PieChart) rootView.findViewById(R.id.chart_pie);
		mChartBar = (BarChart) rootView.findViewById(R.id.chart_bar);
		mChartPie.removeAllViews();
		mChartBar.removeAllViews();

		// ------ set data view ------
		setDataView();

		// chart กลม ใช้ percent, chart แท่ง ใช้ pl_percent
		try {
			jsaResult = contentGetDetailList.getJSONArray("result");

			// ------ pie chart -----
			if (jsaResult != null) {
				mChartPie.setUsePercentValues(true);
				mChartPie.setDescription("");
				mChartPie.setExtraOffsets(5, 10, 5, 5);

				mChartPie.setDragDecelerationFrictionCoef(0.95f);
				// mChartPie.setCenterText("Market Value"); // settext ในวงกลม

				mChartPie.setCenterTextColor(context.getResources().getColor(
						R.color.c_content));

				mChartPie.setDrawHoleEnabled(true);
				mChartPie.setHoleColor(context.getResources().getColor(
						R.color.bg_default));

				mChartPie.setTransparentCircleColor(Color.TRANSPARENT); // สีเงา
																		// ในวงกลม
				mChartPie.setTransparentCircleAlpha(0);

				mChartPie.setHoleRadius(70f); // ขนาดวงกลมตรงกลาง
				mChartPie.setTransparentCircleRadius(77f); // เงารอบวงกลม
				mChartPie.setTransparentCircleColor(Color.TRANSPARENT);

				mChartPie.setDrawCenterText(true);

				mChartPie.setRotationAngle(0);
				// enable rotation of the chart by touch
				mChartPie.setRotationEnabled(true);
				// mChartPie.setHighlightPerTapEnabled(true);

				// mChart.setUnit(" โ�ฌ");
				// mChart.setDrawUnitsInChart(true);

				// add a selection listener
				mChartPie.setOnChartValueSelectedListener(this);

				setDataPieChart(jsaResult.length(), 100);

				mChartPie.animateY(1000, Easing.EasingOption.EaseInOutQuad);
				// mChart.spin(2000, 0, 360);

				Legend l = mChartPie.getLegend();
				l.setPosition(LegendPosition.BELOW_CHART_CENTER);
				// l.setHorizontalAlignment(LegendHorizontalAlignment.LEFT);
				l.setXEntrySpace(3f);
				l.setTextColor(context.getResources().getColor(
						R.color.c_content));
				l.setYEntrySpace(0f);
				l.setYOffset(0f);
				l.setEnabled(false);

			}

			// ------ bar chart ------
			if (jsaResult != null) {
				String strPl_percent = contentGetDetailList.getString(
						"pl_percent").toString();
				float fPl_percent = Float.parseFloat(strPl_percent);

				mChartBar.setOnChartValueSelectedListener(this);

				mChartBar.setDrawBarShadow(false);
				mChartBar.setDrawValueAboveBar(true);

				mChartBar.setDescription("");

				// if more than 60 entries are displayed in the chart, no values
				// will be
				// drawn
				mChartBar.setMaxVisibleValueCount(60);

				// scaling can now only be done on x- and y-axis separately
				mChartBar.setPinchZoom(false);

				mChartBar.setDrawGridBackground(false);
				// mChart.setDrawYLabels(false);


				///////////////////////
				
				///////////////////////////
//		        float MaxValueR, MinValueR;
//				ArrayList<Float> arrMaxMinR = new ArrayList<Float>();
//				for (int i = 0; i < jsaResult.length(); i++) {
//					JSONObject jsaGraph = jsaResult.getJSONObject(i);
//					arrMaxMinR.add(Float.parseFloat(jsaGraph.getString("pl_percent").replaceAll(",", "")));
//				}
//				MaxValueR = FloatMath.ceil(Collections.max(arrMaxMinR));
//				MinValueR = FloatMath.floor(Collections.min(arrMaxMinR));

				XAxis xAxis = mChartBar.getXAxis();
				xAxis.setPosition(XAxisPosition.BOTTOM);
				xAxis.setDrawGridLines(false);
				xAxis.setSpaceBetweenLabels(2);
				xAxis.setTextColor(context.getResources().getColor(
						R.color.c_content));

				// --- ซ้าย
				ValueFormatter custom = new MyValueFormatter();
				YAxis leftAxis = mChartBar.getAxisLeft();
				leftAxis.setLabelCount(7, false);
				leftAxis.setValueFormatter(custom);
				leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART);
				leftAxis.setSpaceTop(15f);
				leftAxis.setTextColor(context.getResources().getColor(
						R.color.c_content));
				leftAxis.setStartAtZero(false);
//				leftAxis.setAxisMaxValue(-10);
//				leftAxis.setAxisMinValue(100); 
//				leftAxis.setAxisMaxValue(MaxValueR);
//				leftAxis.setAxisMinValue(MinValueR); // this replaces setStartAtZero(true)

				// --- ขวา
				YAxis rightAxis = mChartBar.getAxisRight();
				rightAxis.setEnabled(false);
//				rightAxis.setDrawGridLines(false);
//				rightAxis.setTextColor(context.getResources().getColor(
//						R.color.c_content));
//				rightAxis.setStartAtZero(false);
//				rightAxis.setAxisMaxValue(MaxValueR);
//				rightAxis.setAxisMinValue(MinValueR);

				Legend l = mChartBar.getLegend();
				l.setEnabled(false);

				// l.getColors();

				setDataBarChart(jsaResult.length(), (fPl_percent + 1));

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// ============== set data bar chart =========================
	private void setDataBarChart(int count, float range) {
		try {

			ArrayList<String> xVals = new ArrayList<String>();
			for (int i = 0; i < count; i++) {
				xVals.add(jsaResult.getJSONObject(i).getString("symbol"));
			}

			BarDataSet set1;
			if (mChartBar.getData() != null
					&& mChartBar.getData().getDataSetCount() > 0) {
				set1 = (BarDataSet) mChartBar.getData().getDataSetByIndex(0);
				// set1.setYVals(yVals1);
				// mChartBar.getData().setXVals(xVals);
				mChartBar.getData().notifyDataChanged();
				mChartBar.notifyDataSetChanged();
			} else {
				ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();
				set1 = new BarDataSet(yVals, "DataSet");
				for (int i = 0; i < count; i++) {
					String strPl = jsaResult.getJSONObject(i).getString(
							"pl_percent");
					float val = Float.parseFloat(strPl);
					
					yVals.add(new BarEntry(val, i));
					
					set1 = new BarDataSet(yVals, "");
					set1.setBarSpacePercent(35f);
//					if(val < 0){
//						set1.setColor(context.getResources().getColor(R.color.c_danger));
//					}else{
						set1.setColor(context.getResources().getColor(R.color.c_success));
//					}
				}
				
				ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
				dataSets.add(set1);

				BarData data = new BarData(xVals, dataSets);
				data.setValueTextSize(10f);
				data.setValueTextColor(android.graphics.Color.TRANSPARENT);

				mChartBar.setData(data);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// ============== set data pie chart =========================
	private void setDataPieChart(int count, float range) throws JSONException {
		JSONArray jsaResult = contentGetDetailList.getJSONArray("result");

		// ----- หาผลรวม value
		float fSumV = 0;
		for (int i = 0; i < count; i++) {
			String strValue = jsaResult.getJSONObject(i).getString("percent");
			fSumV = fSumV + FunctionSymbol.setStringPaseFloat(strValue);
		}

		// ----- คิด value แต่ละตัวเป็น %
		ArrayList<Entry> yVals1 = new ArrayList<Entry>();
		for (int i = 0; i < count; i++) {
			String strValue = jsaResult.getJSONObject(i).getString("percent");
			float fValue = FunctionSymbol.setStringPaseFloat(strValue);
			float fPercent = (100 * fValue) / fSumV;

			yVals1.add(new Entry(fPercent, i));
		}

		// ----- ชื่อ cat
		ArrayList<String> xVals = new ArrayList<String>();
		for (int i = 0; i < count; i++) {
			xVals.add("");
		}

		PieDataSet dataSet = new PieDataSet(yVals1, "");
		dataSet.setSliceSpace(3f);
		dataSet.setSelectionShift(5f);
		dataSet.setColors(SplashScreen.mpChartArrColor);

		// dataSet.setValueLinePart1OffsetPercentage(80.f);
		// dataSet.setValueLinePart1Length(0.3f);
		// dataSet.setValueLinePart2Length(0.4f);
		// dataSet.setValueLineColor(Color.WHITE);
		// dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

		PieData data = new PieData(xVals, dataSet);
		data.setValueFormatter(new PercentFormatter());
		data.setValueTextSize(11f);
		data.setValueTextColor(Color.WHITE);
		mChartPie.setData(data);

		// undo all highlights
		mChartPie.highlightValues(null);

		mChartPie.invalidate();
	}

	// ============== set data view =========================
	public static ArrayList<View> arr_percent_v;

	private void setDataView() {
		try {
			if ((contentGetDetailList.getString("status")).equals("ok")) {
				TextView tv_total_value = (TextView) rootView
						.findViewById(R.id.tv_total_value);
				TextView tv_market_value0 = (TextView) rootView
						.findViewById(R.id.tv_market_value);
				TextView tv_pl_sum = (TextView) rootView
						.findViewById(R.id.tv_pl_sum);
				TextView tv_pl_percent = (TextView) rootView
						.findViewById(R.id.tv_pl_percent);

				String total_value = contentGetDetailList
						.getString("total_value");
				String market_value = contentGetDetailList
						.getString("market_value");
				String pl_sum = contentGetDetailList.getString("pl_sum");
				String pl_percent = contentGetDetailList
						.getString("pl_percent");

				tv_total_value.setText(total_value + " ฿");
				tv_market_value0.setText(market_value + " ฿");

				tv_pl_sum.setText(FunctionSetBg.setSymbolPlusMinus(pl_sum) + ""
						+ pl_sum + " ฿");
				tv_pl_percent.setText(pl_percent
						+ FunctionSetBg.setSymbolPercent(pl_percent));

				tv_pl_sum.setTextColor(context.getResources().getColor(
						FunctionSetBg.setColorCompareWithZero(pl_sum)));
				tv_pl_percent.setTextColor(context.getResources().getColor(
						FunctionSetBg.setColorCompareWithZero(pl_percent)));

				// ----- list result
				JSONArray jsaResult = contentGetDetailList
						.getJSONArray("result");
				if (jsaResult.length() > 0) {
					LinearLayout list_symbol = (LinearLayout) rootView
							.findViewById(R.id.list_symbol);
					LinearLayout list_detail = (LinearLayout) rootView
							.findViewById(R.id.list_detail);
					list_symbol.removeAllViews();
					list_detail.removeAllViews();
					for (int i = 0; i < jsaResult.length(); i++) {
						View viewSymbol = ((Activity) context)
								.getLayoutInflater().inflate(
										R.layout.row_smartportfolio_symbol,
										null);

						LayoutInflater inflater = (LayoutInflater) context
								.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						View viewDetail = inflater.inflate(
								R.layout.row_smartportfolio_list_detail, null);

						// ------ get data index
						JSONObject jsoIndex = jsaResult.getJSONObject(i);

						final String symbol_name;
						String symbol_fullname_eng;
						String strLast_trade;

						String strPrice = jsoIndex.getString("price");
						String strVolume = jsoIndex.getString("volume");
						String strValue = jsoIndex.getString("value");
						String strDate = jsoIndex.getString("date");
						String strAmount_price = jsoIndex
								.getString("amount_price");
						String strMarket_price = jsoIndex
								.getString("market_price");
						String strPl = jsoIndex.getString("pl");
						String strPl_percent = jsoIndex.getString("pl_percent");

						if (FragmentChangeActivity.strSymbolSelect
								.equals("EQUITY")) {
							symbol_name = jsoIndex.getString("symbol");
							symbol_fullname_eng = jsoIndex
									.getString("symbol_fullname_eng");
							strLast_trade = jsoIndex.getString("last_trade");
						} else {
							symbol_name = jsoIndex.getString("symbol");
							symbol_fullname_eng = jsoIndex.getString("name_e");
							strLast_trade = jsoIndex.getString("invest_value");
						}

						// symbol
						TextView tv_symbol_name = (TextView) viewSymbol
								.findViewById(R.id.tv_symbol_name);
						TextView tv_symbol_fullname_eng = (TextView) viewSymbol
								.findViewById(R.id.tv_symbol_fullname_eng);

						tv_symbol_name.setText(symbol_name);
						tv_symbol_fullname_eng.setText(symbol_fullname_eng);

						((LinearLayout) viewSymbol
								.findViewById(R.id.row_symbol))
								.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										FragmentChangeActivity.strSymbolSelect = symbol_name;
										context.startActivity(new Intent(
												context,
												UiWatchlistDetail.class));
									}
								});

						// detail
						((LinearLayout) viewDetail
								.findViewById(R.id.row_detail))
								.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										FragmentChangeActivity.strSymbolSelect = symbol_name;
										context.startActivity(new Intent(
												context,
												UiWatchlistDetail.class));
									}
								});

						// img chart
						ImageView img_chart = (ImageView) viewDetail
								.findViewById(R.id.img_chart);
						FragmentChangeActivity.imageLoader.displayImage(
								SplashScreen.url_bidschart_chart + symbol_name
										+ ".png", img_chart);

						// ck pe pbv peg

						TextView tv_volume = (TextView) viewDetail
								.findViewById(R.id.tv_volume);
						TextView tv_pl = (TextView) viewDetail
								.findViewById(R.id.tv_pl);
						TextView tv_percent = (TextView) viewDetail
								.findViewById(R.id.tv_percent);
						TextView tv_average_price = (TextView) viewDetail
								.findViewById(R.id.tv_average_price);
						TextView tv_market_price = (TextView) viewDetail
								.findViewById(R.id.tv_market_price);
						TextView tv_amount_price = (TextView) viewDetail
								.findViewById(R.id.tv_amount_price);
						TextView tv_market_value = (TextView) viewDetail
								.findViewById(R.id.tv_market_value);

						tv_volume.setText(strVolume);
						tv_market_price.setText(strLast_trade);
						tv_amount_price.setText(strAmount_price);
						tv_market_value.setText(strMarket_price);
						tv_average_price.setText(strPrice);

						tv_pl.setText(FunctionSetBg.setSymbolPlusMinus(strPl)
								+ "" + strPl);
						tv_percent
								.setText(strPl_percent
										+ FunctionSetBg
												.setSymbolPercent(strPl_percent));

						tv_pl.setTextColor(context.getResources().getColor(
								FunctionSetBg.setColorCompareWithZero(strPl)));
						tv_percent
								.setTextColor(context
										.getResources()
										.getColor(
												FunctionSetBg
														.setColorCompareWithZero(strPl_percent)));

						list_symbol.addView(viewSymbol);
						list_detail.addView(viewDetail);

					}

					// -------- edit list symbol
					((TextView) rootView.findViewById(R.id.tv_edit_list_symbol))
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {

									dialogDelSmartPortDetail();
								}
							});
				}

				dialogLoading.dismiss();
			} else {
				dialogLoading.dismiss();
			}
		} catch (JSONException e) {
			e.printStackTrace();
			dialogLoading.dismiss();
		}
	}

	// ============== dialog delete smart portfolio symbol ===============
	public static JSONObject jsoDelete = null;

	public static void changeRowDeletePort(View v) {
		for (int i = 0; i < row_tv_portdel.size(); i++) {
			row_tv_portdel.get(i).setVisibility(View.GONE);
		}
	}

	public static void hideRowDeleteFav(View v) {
		for (int i = 0; i < row_li_portdel.size(); i++) {
			row_li_portdel.get(i).setVisibility(View.VISIBLE);
		}
	}

	public static ArrayList<LinearLayout> row_li_portdel;
	public static ArrayList<TextView> row_tv_portdel;

	private void dialogDelSmartPortDetail() {
		final Dialog dialog = new Dialog(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		View dlView = inflater.inflate(
				R.layout.dialog_delete_smart_portfolio_list, null);
		// perform operation on elements in Layout
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(dlView);
		((TextView) dlView.findViewById(R.id.dl_tv_close))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

		LinearLayout dl_li_list = (LinearLayout) dlView
				.findViewById(R.id.dl_li_list);
		dl_li_list.removeAllViews();

		row_tv_portdel = new ArrayList<TextView>();
		row_li_portdel = new ArrayList<LinearLayout>();
		row_tv_portdel.clear();
		row_li_portdel.clear();

		// ----- list result
		try {
			JSONArray jsaResult = contentGetDetailList.getJSONArray("result");
			if (jsaResult != null) {
				for (int i = 0; i < jsaResult.length(); i++) {
					View viewRow = ((Activity) context).getLayoutInflater()
							.inflate(R.layout.row_dialog_delfav, null);

					jsoDelete = jsaResult.getJSONObject(i);

					final LinearLayout li_row = (LinearLayout) viewRow
							.findViewById(R.id.li_row);

					ImageView img_delete = (ImageView) viewRow
							.findViewById(R.id.img_delete);
					TextView tv_symbol = (TextView) viewRow
							.findViewById(R.id.tv_symbol);
					final TextView tv_delete = (TextView) viewRow
							.findViewById(R.id.tv_delete);
					tv_delete.setVisibility(View.GONE);

					row_tv_portdel.add(tv_delete);
					row_li_portdel.add(li_row);

					final String symbol_name = jsoDelete.getString("symbol");
					tv_symbol.setText(symbol_name);
					img_delete.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// set row select
							changeRowDeletePort(v);

							tv_delete.setVisibility(View.VISIBLE);

							// Toast.makeText(context, ""+strSymbol, 0).show();
						}
					});

					tv_delete.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							FragmentChangeActivity.strSymbolSelect = symbol_name;
							sendDeletePortList();
							hideRowDeleteFav(v);
							li_row.setVisibility(View.GONE);
							// Toast.makeText(context, "" + strSymbol,
							// 0).show();
						}
					});

					dl_li_list.addView(viewRow);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		dialog.show();

		// set width height dialog
		DisplayMetrics displaymetrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(displaymetrics);

		int ScreenHeight = displaymetrics.heightPixels;
		int ScreenWidth = displaymetrics.widthPixels;

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.width = (int) (ScreenWidth * 1);
		lp.height = (int) (ScreenHeight * 1);
		dialog.getWindow().setAttributes(lp);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
	}

	// ============== send edit port ===============
	public static String resultDeletePortFolio = "";

	private void sendDeletePortList() {
		setDeletePortList resp = new setDeletePortList();
		resp.execute();
	}

	private class setDeletePortList extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		String temp = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progress.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			String url = SplashScreen.url_bidschart
					+ "/service/v2/deletePortfolio";

			String json = "";
			InputStream inputStream = null;

			Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String formattedDate = df.format(c.getTime());

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("portfolio_id",
						jsoDelete.getString("portfolio_id"));

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
					resultDeletePortFolio = AFunctionOther
							.convertInputStreamToString(inputStream);
				else
					resultDeletePortFolio = "Did not work!";

				Log.v("resultDeletePortFolio : ", "" + resultDeletePortFolio);

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
				try {
					JSONObject jsoEditSetAlert = new JSONObject(
							resultDeletePortFolio);

					if (!jsoEditSetAlert.getString("status").equals("ok")) {
						Toast.makeText(context, "Error.", 0).show();
					} else {
						// loadDataDetail();
						Toast.makeText(context, "Success.", 0).show();

						initLoadData(); // get favorite
					}
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			} else {
			}
		}
	}

	// ***************** search symbol******************
	public static LinearLayout li_search_tabbegin, li_search_select;

	public static LinearLayout li_search;

	public static LinearLayout li_view_data;
	public static EditText et_search;
	public static TextView tv_close_search, tv_thai_stock, tv_thai_mutualfund;
	public static ListView listview_search;
	public static ImageView img_portfolio_add;

	// String symbol_search_symbol = "";

	public static String status_tabsearch = "STOCK"; // STOCK, FUND

	public static void initSearchLayout() {

		// text sliding
		initTxtSliding();

		// --- linear show tabsearch begin
		li_search_tabbegin = (LinearLayout) rootView
				.findViewById(R.id.li_search_tabbegin);
		li_search_select = (LinearLayout) rootView
				.findViewById(R.id.li_search_select);
		img_portfolio_add = (ImageView) rootView
				.findViewById(R.id.img_portfolio_add);
		// --- linear tabsearch
		li_search = (LinearLayout) rootView.findViewById(R.id.li_search);
		// --- linear tabsearch begin
		li_view_data = (LinearLayout) rootView.findViewById(R.id.li_view_data);
		et_search = (EditText) rootView.findViewById(R.id.et_search);
		tv_close_search = (TextView) rootView
				.findViewById(R.id.tv_close_search);
		// --- show hide tab search
		li_search_tabbegin.setVisibility(View.VISIBLE);
		li_search.setVisibility(View.GONE);
		li_search_select.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if ((FragmentChangeActivity.contentGetSymbol == null)) {
					Toast.makeText(context, "Symbol Empty.", 0).show();
				} else {
					initSearchSymbol(); // search symbol
				}
			}
		});
		img_portfolio_add.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if ((FragmentChangeActivity.contentGetSymbol == null)) {
					Toast.makeText(context, "Symbol Empty.", 0).show();
				} else {
					initSearchSymbol(); // search symbol
				}
			}
		});
	}

	// ***************** text sliding ******************
	public static void initTxtSliding() {
		String strSliding = "";
		TextView marque = (TextView) rootView
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
						// double db = Double.parseDouble(txtLtrade);
						// DecimalFormat formatter = new
						// DecimalFormat("#,###.00");
						// txtLtrade = formatter.format(db);
						txtLtrade = FunctionSymbol.setFormatNumber(txtLtrade);
					}

					if (txtChange != "") {
						// double db = Double.parseDouble(txtChange);
						// txtChange = String.format(" %,.2f", db);
						txtChange = FunctionSymbol.setFormatNumber(txtChange);
					}

					if (txtSymbol.equals(".SET")) {
						if (txtPchange != "") {
							double dbS = Double.parseDouble(txtSetPChenge
									.replaceAll(",", ""));
							txtPchange = FunctionSetBg
									.setColorTxtSlidingSet(txtSetPChenge
											.replaceAll(",", ""));
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

	// =========== search symbol watchlist ==========
	public static void initSearchSymbol() {
		// sv_search_symbol = (ScrollView)
		// rootView.findViewById(R.id.sv_search_symbol);
		li_search_tabbegin.setVisibility(View.GONE);
		li_search.setVisibility(View.VISIBLE);

		// ----- search stock
		final ListAdapterSearchSymbolStock ListAdapterSearch;
		final ArrayList<CatalogGetSymbol> original_list;
		final ArrayList<CatalogGetSymbol> second_list;

		original_list = new ArrayList<CatalogGetSymbol>();
		original_list.addAll(FragmentChangeActivity.list_getSymbol);
		second_list = new ArrayList<CatalogGetSymbol>();
		second_list.addAll(FragmentChangeActivity.list_getSymbol);

		// ----- search fund
		final ListAdapterSearchNameFundPortDetail ListAdapterSearchNameFund;
		final ArrayList<CatalogGetNameFund> original_list_fund;
		final ArrayList<CatalogGetNameFund> second_list_fund;

		original_list_fund = new ArrayList<CatalogGetNameFund>();
		original_list_fund.addAll(FragmentChangeActivity.list_getNameFund);
		second_list_fund = new ArrayList<CatalogGetNameFund>();
		second_list_fund.addAll(FragmentChangeActivity.list_getNameFund);

		tv_thai_stock = (TextView) rootView.findViewById(R.id.tv_thai_stock);
		tv_thai_mutualfund = (TextView) rootView
				.findViewById(R.id.tv_thai_mutualfund);
		status_tabsearch = "STOCK"; // STOCK, FUND
		setTitleSearch();

		listview_search = (ListView) rootView
				.findViewById(R.id.list_search_symbol);
		listview_search.setVisibility(View.GONE);

		// ----- set list view ------
		ListAdapterSearch = new ListAdapterSearchSymbolStock(context, 0,
				second_list);
		listview_search.setAdapter(ListAdapterSearch);
		ListAdapterSearchNameFund = new ListAdapterSearchNameFundPortDetail(
				context, 0, second_list_fund);

		et_search.addTextChangedListener(new TextWatcher() {
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
				String text = et_search.getText().toString();
				if (text.length() > 0) {
					li_view_data.setVisibility(View.GONE);
					listview_search.setVisibility(View.VISIBLE);

					if (status_tabsearch == "STOCK") {
						second_list.clear();
						if (FragmentChangeActivity.list_getSymbol != null) {
							for (int i = 0; i < original_list.size(); i++) {
								if (original_list.get(i).symbol
										.toLowerCase()
										.contains(text.toString().toLowerCase())) {
									second_list.add(original_list.get(i));
								}
							}
							ListAdapterSearch.notifyDataSetChanged();
						}
					} else {
						second_list_fund.clear();
						if (FragmentChangeActivity.list_getNameFund != null) {
							for (int i = 0; i < original_list_fund.size(); i++) {
								if (original_list_fund.get(i).name_initial
										.toLowerCase().contains(
												text.toString().toLowerCase())) {
									second_list_fund.add(original_list_fund
											.get(i));
								}
							}
							ListAdapterSearchNameFund.notifyDataSetChanged();
						}
					}
				} else {
					li_view_data.setVisibility(View.VISIBLE);
					listview_search.setVisibility(View.GONE);
				}
			}
		});

		// -------------------------
		tv_thai_stock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				status_tabsearch = "STOCK"; // STOCK, FUND
				// ----- set list view ------
				listview_search.setAdapter(ListAdapterSearch);

				String text = et_search.getText().toString();
				setTitleSearch();

				if (text.length() > 0) {
					li_view_data.setVisibility(View.GONE);
					listview_search.setVisibility(View.VISIBLE);

					if (FragmentChangeActivity.list_getSymbol != null) {
						second_list.clear();
						for (int i = 0; i < original_list.size(); i++) {
							if (original_list.get(i).symbol.toLowerCase()
									.contains(text.toString().toLowerCase())) {
								second_list.add(original_list.get(i));
							}
						}
						ListAdapterSearch.notifyDataSetChanged();
					}
				} else {
					li_view_data.setVisibility(View.VISIBLE);
					listview_search.setVisibility(View.GONE);
				}
			}
		});
		tv_thai_mutualfund.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				status_tabsearch = "FUND"; // STOCK, FUND
				// ----- set list view ------
				listview_search.setAdapter(ListAdapterSearchNameFund);

				String text = et_search.getText().toString();
				setTitleSearch();

				if (text.length() > 0) {
					li_view_data.setVisibility(View.GONE);
					listview_search.setVisibility(View.VISIBLE);
					if (FragmentChangeActivity.list_getNameFund != null) {
						second_list_fund.clear();
						for (int i = 0; i < original_list_fund.size(); i++) {
							if (original_list_fund.get(i).name_initial
									.toLowerCase().contains(
											text.toString().toLowerCase())) {
								second_list_fund.add(original_list_fund.get(i));
							}
						}
						ListAdapterSearchNameFund.notifyDataSetChanged();
					}
				} else {
					li_view_data.setVisibility(View.VISIBLE);
					listview_search.setVisibility(View.GONE);
				}
			}
		});

		// -------------------------
		tv_close_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideListSearch();

				// listview_search.setVisibility(View.GONE);

				// setWatchlistSymbol();
			}
		});
	}

	// / ============ close listsearch ======
	public static void hideListSearch() {
		et_search.setText("");
		li_view_data.setVisibility(View.VISIBLE);

		li_search_tabbegin.setVisibility(View.VISIBLE);
		li_search.setVisibility(View.GONE);
	}

	// ******* set title search ****
	public static void setTitleSearch() {
		tv_thai_stock.setTextColor(context.getResources().getColor(
				R.color.c_content));
		tv_thai_mutualfund.setTextColor(context.getResources().getColor(
				R.color.c_content));
		tv_thai_stock.setBackgroundColor(Color.TRANSPARENT);
		tv_thai_mutualfund.setBackgroundColor(Color.TRANSPARENT);

		if (status_tabsearch == "STOCK") {
			tv_thai_stock.setTextColor(context.getResources().getColor(
					R.color.bg_default));
			tv_thai_stock
					.setBackgroundResource(R.drawable.border_search_activeleft);
		} else {
			tv_thai_mutualfund.setTextColor(context.getResources().getColor(
					R.color.bg_default));
			tv_thai_mutualfund
					.setBackgroundResource(R.drawable.border_search_activeright);
		}
	}

	// ------ set background fund_risk
	public static String setBgPercent(String str) {
		double dStr = (Double.parseDouble(str) / 10);
		for (int j = 0; j < dStr; j++) {
			arr_percent_v.get(j).setBackgroundColor(
					context.getResources().getColor(R.color.c_success));
		}
		return str;
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

	@Override
	public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
		if (e == null)
			return;
		Log.i("VAL SELECTED",
				"Value: " + e.getVal() + ", xIndex: " + e.getXIndex()
						+ ", DataSet index: " + dataSetIndex);
	}

	@Override
	public void onNothingSelected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// setData(mSeekBarX.getProgress(), mSeekBarY.getProgress());
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub

	}

}