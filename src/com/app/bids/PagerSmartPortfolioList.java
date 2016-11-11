package com.app.bids;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;
import com.app.bids.PagerWatchListDetailNews.loadAll;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.PercentFormatter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class PagerSmartPortfolioList extends Fragment implements
		OnSeekBarChangeListener, OnChartValueSelectedListener {

	static Context context;
	public static View rootView;

	private PieChart mChart;

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
				R.layout.pager_smartportfolio_list, container, false);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (FragmentChangeActivity.contentGetSmartPortfolioList != null) {
			initSetData();
		} else {
			// initLoadData();
		}
	}

	// ============== Load initLoadData =========================
	private void initLoadData() {
		loadAll resp = new loadAll();
		resp.execute();
	}

	public class loadAll extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;
		// ======= Ui Newds ========
		private JSONObject jsonGetData;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// http://bidschart.com/service/v2/getPortfolioListByUser?user_id=
			String url_GetData = SplashScreen.url_bidschart
					+ "/service/v2/getPortfolioListByUser?user_id="
					+ SplashScreen.userModel.user_id + "&timestamp="
					+ timestamp;

			Log.v("url_ getPortfolioListByUser", "" + url_GetData);

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
							FragmentChangeActivity.contentGetSmartPortfolioList = jsonGetData;
							initSetData();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.v("json null", "jsonGetFundamental null");
				}
			} else {
				Log.v("json jsonGetFundamental null", "jsonGetFundamental null");
			}
		}
	}

	// ============== init set data =========================
	private void initSetData() {
		try {
			if ((FragmentChangeActivity.contentGetSmartPortfolioList
					.getString("status")).equals("ok")) {
				mChart = (PieChart) rootView.findViewById(R.id.chart1);
				mChart.setUsePercentValues(true);
				mChart.setDescription("");
				mChart.setExtraOffsets(5, 10, 5, 5);

				mChart.setDragDecelerationFrictionCoef(0.95f);
				mChart.setCenterText(FragmentChangeActivity.contentGetSmartPortfolioList
						.getString("total_value") + " ฿\nInvestments"); // settext
																		// ในวงกลม
				mChart.setCenterTextColor(getResources().getColor(
						R.color.c_content));

				mChart.setDrawHoleEnabled(true);
				mChart.setHoleColor(getResources().getColor(R.color.bg_default));

				mChart.setTransparentCircleColor(Color.TRANSPARENT); // สีเงา ในวงกลม
				mChart.setTransparentCircleAlpha(0);

				mChart.setHoleRadius(70f); // ขนาดวงกลมตรงกลาง
				mChart.setTransparentCircleRadius(77f); // เงารอบวงกลม
				mChart.setTransparentCircleColor(Color.TRANSPARENT);

				mChart.setDrawCenterText(true);

				mChart.setRotationAngle(0);
				// enable rotation of the chart by touch
				mChart.setRotationEnabled(true);
//				mChart.setHighlightPerTapEnabled(true);

				// mChart.setUnit(" โ�ฌ");
				// mChart.setDrawUnitsInChart(true);

				// add a selection listener
				mChart.setOnChartValueSelectedListener(this);

				JSONArray jsaResult = FragmentChangeActivity.contentGetSmartPortfolioList
						.getJSONArray("result");
				setData(jsaResult.length(), 100);

				mChart.animateY(1000, Easing.EasingOption.EaseInOutQuad);
				// mChart.spin(2000, 0, 360);

				Legend l = mChart.getLegend();
				l.setPosition(LegendPosition.BELOW_CHART_CENTER);
//				l.setHorizontalAlignment(LegendHorizontalAlignment.LEFT);
				l.setXEntrySpace(3f);
				l.setTextColor(getResources().getColor(R.color.c_content));
				l.setYEntrySpace(0f);
				l.setYOffset(0f);
				l.setEnabled(false);

				// Log.v("l.colorsObjc", "");
				// NSArray *arrColor = l.colorsObjc;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void setData(int count, float range) throws JSONException {
		JSONArray jsaResult = FragmentChangeActivity.contentGetSmartPortfolioList
				.getJSONArray("result");

		// ----- หาผลรวม value
		float fSumV = 0;
		for (int i = 0; i < count; i++) {
			String strValue = jsaResult.getJSONObject(i).getString("value");
			fSumV = fSumV + FunctionSymbol.setStringPaseFloat(strValue);
		}

		// ----- คิด value แต่ละตัวเป็น %
		ArrayList<Entry> yVals1 = new ArrayList<Entry>();
		for (int i = 0; i < count; i++) {
			String strValue = jsaResult.getJSONObject(i).getString("value");
			float fValue = FunctionSymbol.setStringPaseFloat(strValue);
			float fPercent = (100 * fValue) / fSumV;

			yVals1.add(new Entry(fPercent, i));
		}

		// ----- ชื่อ cat
		ArrayList<String> xVals = new ArrayList<String>();
		for (int i = 0; i < count; i++) {
			String strDetail = jsaResult.getJSONObject(i).getString("cat");
			xVals.add(strDetail);
		}

		// add a lot of colors

        ArrayList<Integer> colors = new ArrayList<Integer>();
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.COLORFUL_COLORS)
            colors.add(c);

        for (int c : ColorTemplate.LIBERTY_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        
		// PieDataSet dataSet = new PieDataSet(yVals1,
		// "สินทรัพย์ 10 อันดับแรกที่ลงทุนกองทุน");
		PieDataSet dataSet = new PieDataSet(yVals1, "");
		dataSet.setSliceSpace(3f);
		dataSet.setSelectionShift(5f);
		dataSet.setColors(colors);
		
//		dataSet.setColors(SplashScreen.mpChartArrColor);
		
//		juyful, colorful, liberty


//		dataSet.setValueLinePart1OffsetPercentage(80.f);
//		dataSet.setValueLinePart1Length(0.3f);
//		dataSet.setValueLinePart2Length(0.4f);
//		dataSet.setValueLineColor(Color.WHITE);
//		dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

		PieData data = new PieData(xVals, dataSet);
		data.setValueFormatter(new PercentFormatter());
		data.setValueTextSize(13f);
		data.setValueTextColor(Color.WHITE);
		mChart.setData(data);

		// undo all highlights
		mChart.highlightValues(null);

		mChart.invalidate();

		// ------ set data view ------
		setDataView();
	}

	// ============== set data view =========================
	public static ArrayList<View> arr_percent_v;

	private void setDataView() {
		Legend l = mChart.getLegend();
		try {
			if (FragmentChangeActivity.contentGetSmartPortfolioList != null) {
				TextView tv_total_value = (TextView) rootView
						.findViewById(R.id.tv_total_value);
				TextView tv_market_value = (TextView) rootView
						.findViewById(R.id.tv_market_value);
				TextView tv_pl_sum = (TextView) rootView
						.findViewById(R.id.tv_pl_sum);
				TextView tv_pl_percent = (TextView) rootView
						.findViewById(R.id.tv_pl_percent);

				String total_value = FragmentChangeActivity.contentGetSmartPortfolioList
						.getString("total_value");
				String market_value = FragmentChangeActivity.contentGetSmartPortfolioList
						.getString("market_value");
				String pl_sum = FragmentChangeActivity.contentGetSmartPortfolioList
						.getString("pl_sum");
				String pl_percent = FragmentChangeActivity.contentGetSmartPortfolioList
						.getString("pl_percent");

				tv_total_value.setText(total_value + " ฿");
				tv_market_value.setText(market_value + " ฿");

				tv_pl_sum.setText(FunctionSetBg.setSymbolPlusMinus(pl_sum) + ""
						+ pl_sum + " ฿");
				tv_pl_percent.setText(pl_percent
						+ FunctionSetBg.setSymbolPercent(pl_percent));

				tv_pl_sum.setTextColor(context.getResources().getColor(
						FunctionSetBg.setColorCompareWithZero(pl_sum)));
				tv_pl_percent.setTextColor(context.getResources().getColor(
						FunctionSetBg.setColorCompareWithZero(pl_percent)));

				// ----- list result
				JSONArray jsaResult = FragmentChangeActivity.contentGetSmartPortfolioList
						.getJSONArray("result");
				if (jsaResult.length() > 0) {
					LinearLayout li_list = (LinearLayout) rootView
							.findViewById(R.id.li_list);
					li_list.removeAllViews();
					for (int i = 0; i < jsaResult.length(); i++) {
						View viewRow = ((Activity) context)
								.getLayoutInflater()
								.inflate(R.layout.row_smartportfolio_list, null);

						LinearLayout li_row = (LinearLayout) viewRow
								.findViewById(R.id.li_row);
						ImageView img_cat_color = (ImageView) viewRow
								.findViewById(R.id.img_cat_color);
						TextView tv_cat = (TextView) viewRow
								.findViewById(R.id.tv_cat);
						TextView tv_value = (TextView) viewRow
								.findViewById(R.id.tv_value);
						TextView tv_percent = (TextView) viewRow
								.findViewById(R.id.tv_percent);

						final String cat = jsaResult.getJSONObject(i)
								.getString("cat");
						String value = jsaResult.getJSONObject(i).getString(
								"value");
						String percent = jsaResult.getJSONObject(i).getString(
								"percent");

						tv_cat.setText(cat);
						tv_value.setText(value + " ฿");
						tv_percent.setText(percent
								+ FunctionSetBg.setSymbolPercent(percent));
						Integer intColor = l.getColors()[i];
						String hexColor = "#" + Integer.toHexString(intColor).substring(2);
						img_cat_color.setBackgroundColor(Color.parseColor(hexColor));

						// ---view percent
						View v_percent_1 = (View) viewRow
								.findViewById(R.id.v_percent_1);
						View v_percent_2 = (View) viewRow
								.findViewById(R.id.v_percent_2);
						View v_percent_3 = (View) viewRow
								.findViewById(R.id.v_percent_3);
						View v_percent_4 = (View) viewRow
								.findViewById(R.id.v_percent_4);
						View v_percent_5 = (View) viewRow
								.findViewById(R.id.v_percent_5);
						View v_percent_6 = (View) viewRow
								.findViewById(R.id.v_percent_6);
						View v_percent_7 = (View) viewRow
								.findViewById(R.id.v_percent_7);
						View v_percent_8 = (View) viewRow
								.findViewById(R.id.v_percent_8);
						View v_percent_9 = (View) viewRow
								.findViewById(R.id.v_percent_9);
						View v_percent_10 = (View) viewRow
								.findViewById(R.id.v_percent_10);
						arr_percent_v = new ArrayList<View>();
						arr_percent_v.clear();
						arr_percent_v.add(v_percent_1);
						arr_percent_v.add(v_percent_2);
						arr_percent_v.add(v_percent_3);
						arr_percent_v.add(v_percent_4);
						arr_percent_v.add(v_percent_5);
						arr_percent_v.add(v_percent_6);
						arr_percent_v.add(v_percent_7);
						arr_percent_v.add(v_percent_8);
						arr_percent_v.add(v_percent_9);
						arr_percent_v.add(v_percent_10);
						if ((!percent.equals("")) && (!percent.equals("-"))
								&& (!percent.equals("null"))
								&& (!percent.equals("N/A"))) {
							setBgPercent(percent.replaceAll(",", ""));
						}

						li_row.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								FragmentChangeActivity.strSymbolSelect = cat;
								switchFragment(new PagerSmartPortfolioListDetail());
							}
						});

						li_list.addView(viewRow);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
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