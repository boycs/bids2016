package com.app.bids;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;
import com.app.bids.PagerWatchListDetailNews.loadAll;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.gms.internal.m;

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
import android.text.Html;
import android.util.FloatMath;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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

public class PagerMutualFundDetailPerformance extends Fragment implements
		OnSeekBarChangeListener, OnChartValueSelectedListener {

	static Context context;
	public static View rootView;

	private LineChart mChart;

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
				R.layout.pager_mutualfund_detail_performance, container, false);

		return rootView;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		if (UiMutualfundDetail.contentGetPerformGraph != null) {
			initSetData();
		} else {
			initLoadData();
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
			// progress.show();

		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// http://bidschart.com/service/v2/getFundDividendShot?fund=KSDLTF
			String url_GetData = SplashScreen.url_bidschart
					+ "/service/v2/getFundDividendShot?fund="
					+ FragmentChangeActivity.strSymbolSelect + "&timestamp="
					+ timestamp;

			Log.v("url_GetMutual Perform", "" + url_GetData);

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
							
//							UiMutualfundDetail.contentGetPerformData = jsonGetData
//									.getJSONArray("data");
//							UiMutualfundDetail.contentGetPerformYear = jsonGetData
//									.getJSONArray("year");
//							UiMutualfundDetail.contentGetPerformQuarter = jsonGetData
//									.getJSONArray("quarter");
							UiMutualfundDetail.contentGetPerformGraph = jsonGetData
									.getJSONObject("graph");

//							if (UiMutualfundDetail.contentGetPerformGraph != null) {
//								UiMutualfundDetail.contentGetPerformNevGraph = UiMutualfundDetail.contentGetPerformGraph
//										.getJSONArray("nevGraph");
//							}
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
	float MaxValue, MinValue, MaxValueR, MinValueR;
	int lengthMaxMin;
	ArrayList<Float> arrInvestValue;
	ArrayList<Float> arrInvestValueR;
	ArrayList<Float> arrMaxMinR;
	ArrayList<String> arrDataAt;
	ArrayList<Entry> arrMaxMinE;

	public static JSONArray jsaColumn;
	public static JSONArray jsaNevGraph;

	private void initSetData() {
		arrInvestValue = new ArrayList<Float>();
		arrInvestValueR = new ArrayList<Float>();
		arrMaxMinR = new ArrayList<Float>();
		arrDataAt = new ArrayList<String>();
		arrMaxMinE = new ArrayList<Entry>();
		
		try {
			if (UiMutualfundDetail.contentGetPerformGraph != null) {
				
				Log.v("contentGetPerformGraph", "contentGetPerformGraph");

				jsaColumn = UiMutualfundDetail.contentGetPerformGraph
						.getJSONArray("column");
				jsaNevGraph = UiMutualfundDetail.contentGetPerformGraph
						.getJSONArray("nevGraph");
				
				Log.v("jsaNevGraph", ""+jsaNevGraph.length());
				
				for (int i = 0; i < jsaNevGraph.length(); i++) {
					String strDateAt = jsaNevGraph.getJSONArray(i).get(0).toString();
					String strValue = jsaNevGraph.getJSONArray(i).get(1).toString();
					String strValueR = jsaNevGraph.getJSONArray(i).get(2).toString();

					arrDataAt.add(strDateAt);
					arrInvestValue.add(Float.parseFloat(strValue.replaceAll(",", "")));
					arrInvestValueR.add(Float.parseFloat(strValueR.replaceAll(",", "")));
					arrMaxMinR.add(Float.parseFloat(strValue.replaceAll(",", "")));
					arrMaxMinR.add(Float.parseFloat(strValueR.replaceAll(",", "")));					
//					arrMaxMinE.add(new Entry(Float.parseFloat(strValue.replaceAll(",", "")), i));
				}

				MaxValue = FloatMath.ceil(Collections.max(arrInvestValue));
				MinValue = FloatMath.floor(Collections.min(arrInvestValue));
				MaxValueR = FloatMath.ceil(Collections.max(arrMaxMinR));
				MinValueR = FloatMath.floor(Collections.min(arrMaxMinR));
				lengthMaxMin = arrInvestValue.size();
				
				Log.v("arrInvestValue", ""+arrInvestValue);
				Log.v("MaxValue MinValue", ""+MaxValue+"_"+MinValue);

				mChart = (LineChart) rootView.findViewById(R.id.chart1);
				mChart.removeAllViews();
				mChart.removeView(mChart);
				mChart.setOnChartValueSelectedListener(this);

				// no description text
				mChart.setDescription("");
				mChart.setNoDataTextDescription("You need to provide data for the chart.");

				// enable touch gestures
				mChart.setTouchEnabled(true);

				mChart.setDragDecelerationFrictionCoef(0.9f);

				// enable scaling and dragging
				mChart.setDragEnabled(true);
				mChart.setScaleEnabled(true);
				mChart.setDrawGridBackground(false);
				mChart.setHighlightPerDragEnabled(true);

				// if disabled, scaling can be done on x- and y-axis separately
				mChart.setPinchZoom(true);

				// set an alternative background color
				// mChart.setBackgroundColor(Color.LTGRAY);

				// add data
				int iRang = (int)FloatMath.ceil((MaxValue-MinValue)/arrInvestValue.size());
				setData(arrDataAt.size(), iRang);
//				setData(20, 30);

				mChart.animateX(1000);

				// get the legend (only possible after setting data)
				Legend l = mChart.getLegend();

				// modify the legend ...
				// l.setPosition(LegendPosition.LEFT_OF_CHART);
				l.setForm(LegendForm.LINE);
				l.setTextSize(11f);
				l.setTextColor(Color.WHITE);
				l.setPosition(LegendPosition.BELOW_CHART_LEFT);
				// l.setYOffset(11f);

				XAxis xAxis = mChart.getXAxis();
				xAxis.setTextSize(12f);
				xAxis.setTextColor(Color.WHITE);
				xAxis.setDrawGridLines(false);
				xAxis.setDrawAxisLine(false);
				xAxis.setSpaceBetweenLabels(1);

				YAxis leftAxis = mChart.getAxisLeft();
				leftAxis.setTextColor(Color.GREEN);
				leftAxis.setAxisMaxValue(MaxValue+iRang);
				leftAxis.setAxisMinValue(MinValue-iRang);
				leftAxis.setDrawGridLines(true);
//				leftAxis.setGranularityEnabled(true);

				YAxis rightAxis = mChart.getAxisRight();
				rightAxis.setTextColor(ColorTemplate.getHoloBlue());
				rightAxis.setAxisMaxValue(MaxValueR);
				rightAxis.setAxisMinValue(MinValueR);
				rightAxis.setDrawGridLines(false);
//				rightAxis.setDrawZeroLine(false);
//				rightAxis.setGranularityEnabled(false);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void setData(int count, float range) {
		try {

			ArrayList<String> xVals = new ArrayList<String>();
			for (int i = 0; i < count; i++) {
				xVals.add(arrDataAt.get(i));
			}

			ArrayList<Entry> yVals1 = new ArrayList<Entry>();
			for (int i = 0; i < count; i++) {
				float val = arrInvestValue.get(i);
				yVals1.add(new Entry(val, i));
			}

			ArrayList<Entry> yVals2 = new ArrayList<Entry>();
			for (int i = 0; i < count; i++) {
				float val = arrInvestValueR.get(i);
				yVals2.add(new Entry(val, i));
			}

			LineDataSet set1, set2;

			if (mChart.getData() != null
					&& mChart.getData().getDataSetCount() > 0) {
				
				Log.v("mChart.getData()", " !=null ");
				
				set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
				set2 = (LineDataSet) mChart.getData().getDataSetByIndex(1);
//				set1.setYVals(yVals1);
//				set2.setYVals(yVals2);
//				mChart.getData().setXVals(xVals);
				mChart.notifyDataSetChanged();
			} else {
				
				Log.v("mChart.getData()", " ==null ");
				
				// create a dataset and give it a type
				set1 = new LineDataSet(yVals1, ""
						+ jsaColumn.getJSONArray(1).get(1));
				set1.setAxisDependency(AxisDependency.LEFT);
				set1.setColor(Color.GREEN);
				set1.setCircleColor(Color.GREEN);
				set1.setLineWidth(2f); // ขนาดเส้นบนริมกราฟ
				set1.setDrawCircleHole(false); // จุดบนกราฟทึบ ,true จุดเป็นรู
//				set1.setCircleRadius(0f); // ขนาดจุดบนกราฟ
				set1.setFillAlpha(65);
//				Drawable drawable = ContextCompat.getDrawable(context,
//						R.drawable.fade_green); // ไล่สีพื้นที่ในกราฟ
//				set1.setFillDrawable(drawable);
				set1.setDrawCircleHole(false);
				set1.setHighLightColor(Color.GREEN);
				set1.setDrawFilled(true);

				set2 = new LineDataSet(yVals2, ""
						+ jsaColumn.getJSONArray(2).get(1));
//				set2.setAxisDependency(AxisDependency.RIGHT);
				set2.setColor(ColorTemplate.getHoloBlue());
//				set2.setCircleColor(ColorTemplate.getHoloBlue());
//				set2.setLineWidth(2f);
//				set2.setCircleRadius(3f);
//				set2.setFillAlpha(65);
//				set2.setDrawCircleHole(false);
//				set2.setHighLightColor(ColorTemplate.getHoloBlue());

				ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
				dataSets.add(set2);
				dataSets.add(set1); // add the datasets

				// create a data object with the datasets
				LineData data = new LineData(xVals, dataSets);
				data.setValueTextColor(Color.TRANSPARENT);
				data.setValueTextSize(9f);

				// set data
				mChart.setData(data);
			}

		} catch (JSONException e) {
			e.printStackTrace();
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

	@Override
	public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
//		mChart.centerViewToAnimated(e.getXIndex(), e.getVal(), mChart.getData()
//				.getDataSetByIndex(dataSetIndex).getAxisDependency(), 500);
	}

	@Override
	public void onNothingSelected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,
			boolean fromUser) {
		// TODO Auto-generated method stub

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