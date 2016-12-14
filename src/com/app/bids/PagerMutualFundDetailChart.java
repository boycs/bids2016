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
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.LimitLine.LimitLabelPosition;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.ChartTouchListener;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Utils;

import android.R.color;
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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
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

public class PagerMutualFundDetailChart extends Fragment implements
		OnSeekBarChangeListener, OnChartGestureListener,
		OnChartValueSelectedListener {

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
				R.layout.pager_mutualfund_detail_chart, container, false);

		return rootView;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		mChart = (LineChart) rootView.findViewById(R.id.chart1);
		if (UiMutualfundDetail.contentGetChartGraph != null) {
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
			// http://bidschart.com/service/v2/getFundDetail?fund=1DIV
			String url_GetData = SplashScreen.url_bidschart
					+ "/service/v2/getFundDetail?fund="
					+ FragmentChangeActivity.strSymbolSelect + "&timestamp="
					+ timestamp;

			Log.v("url_GetMutual Chart", "" + url_GetData);

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
							UiMutualfundDetail.contentGetChartData = jsonGetData
									.getJSONArray("data");
							UiMutualfundDetail.contentGetChartGraph = jsonGetData
									.getJSONArray("graph");
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
	float MaxValue, MinValue;
	int lengthMaxMin;
	ArrayList<Float> arrInvestValue;
	ArrayList<String> arrDataAt;
	ArrayList<Entry> arrMaxMinE;

	private void initSetData() {
		arrInvestValue = new ArrayList<Float>();
		arrDataAt = new ArrayList<String>();
		arrMaxMinE = new ArrayList<Entry>();
		mChart.removeView(mChart);
		mChart.removeAllViews();
		
		try {
			if (UiMutualfundDetail.contentGetChartGraph != null) {
				for (int i = 0; i < UiMutualfundDetail.contentGetChartGraph
						.length(); i++) {
					String strValue = UiMutualfundDetail.contentGetChartGraph
							.getJSONObject(i).getString("invest_value");
					String strDateAt = UiMutualfundDetail.contentGetChartGraph
							.getJSONObject(i).getString("date_at");

					arrDataAt.add(strDateAt);
					if(!strValue.equals("-")){
						arrInvestValue.add(Float.parseFloat(strValue.replaceAll(",", "")));
						arrMaxMinE.add(new Entry(Float.parseFloat(strValue.replaceAll(",", "")), i));
					}
				}

				MaxValue = Collections.max(arrInvestValue);
				MinValue = Collections.min(arrInvestValue);
				lengthMaxMin = arrInvestValue.size();
				
//				Log.v("initSetData chart", ""+MaxValue+"_"+MinValue);

//				mChart.removeAllViews();
				mChart.setOnChartGestureListener(this);
				mChart.setOnChartValueSelectedListener(this);
				mChart.setDrawGridBackground(false);

				// no description text
				mChart.setDescription("");
				mChart.setNoDataTextDescription("You need to provide data /for the chart.");

				// enable touch gestures
				mChart.setTouchEnabled(true);

				// enable scaling and dragging
				mChart.setDragEnabled(true);
				mChart.setScaleEnabled(true);
				// mChart.setScaleXEnabled(true);
				// mChart.setScaleYEnabled(true);

				// if disabled, scaling can be done on x- and y-axis separately
				mChart.setPinchZoom(true);

				// x-axis limit line
				LimitLine llXAxis = new LimitLine(10f, "Index 10");
				llXAxis.setLineWidth(2f);
				llXAxis.enableDashedLine(10f, 10f, 0f);
				llXAxis.setLabelPosition(LimitLabelPosition.POS_RIGHT);
				llXAxis.setTextSize(10f);
				llXAxis.setTextColor(getResources().getColor(R.color.c_content));

				XAxis xAxis = mChart.getXAxis();
				xAxis.setTextColor(Color.WHITE);

				YAxis leftAxis = mChart.getAxisLeft();
				leftAxis.removeAllLimitLines(); // reset all limit lines to
												// avoid overlapping lines

				leftAxis.setAxisMaxValue((float) (Math.ceil(MaxValue)));
				leftAxis.setAxisMinValue((float) (Math.floor(MinValue)));
				leftAxis.setYOffset(20f);
				leftAxis.enableGridDashedLine(10f, 10f, 0f);
				leftAxis.setTextColor(getResources()
						.getColor(R.color.c_content));
				leftAxis.setDrawAxisLine(false);

				// limit lines are drawn behind data (and not on top)
				leftAxis.setDrawLimitLinesBehindData(true);

				mChart.getAxisRight().setEnabled(false);

				// add data
				setData(arrDataAt.size(), (int) MaxValue);

				mChart.animateX(1000, Easing.EasingOption.EaseInOutQuart);
				// mChart.invalidate();

				// get the legend (only possible after setting data)
				Legend l = mChart.getLegend();

				// modify the legend ...
				// l.setPosition(LegendPosition.LEFT_OF_CHART);
				l.setForm(LegendForm.LINE);
				l.setTextColor(Color.WHITE);

				// // dont forget to refresh the drawing
				// mChart.invalidate();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void setData(int count, float range) {
		ArrayList<String> xVals = new ArrayList<String>();
		
		for (int i = 0; i < count; i++) {
			xVals.add(arrDataAt.get(i));
		}

		ArrayList<Entry> yVals = new ArrayList<Entry>();
		for (int i = 0; i < count; i++) {

			float val = arrInvestValue.get(i);
			yVals.add(new Entry(val, i));
		}

		LineDataSet set1;

//		mChart.removeAllViews();
		if (mChart.getData() != null && mChart.getData().getDataSetCount() > 0) {
			set1 = (LineDataSet) mChart.getData().getDataSetByIndex(0);
//			set1.setYVals(arrMaxMinE);
//			mChart.getData().setXVals(xVals);
			// mChart.getData().setValueTextColor(getResources().getColor(R.color.c_content));
			mChart.notifyDataSetChanged();
		} else {
			// create a dataset and give it a type
			set1 = new LineDataSet(yVals,
					FragmentChangeActivity.strSymbolSelect);

			// set the line to be drawn like this "- - - - - -"
			set1.enableDashedLine(0f, 0f, 0f); // เส้นบนขอบกราฟไม่ให้เป็นเส้นประ
            set1.enableDashedLine(10f, 5f, 0f);
			set1.setColor(Color.GREEN); // เส้นบนขอบกราฟ
			set1.setCircleColor(Color.GREEN); // จุดบนกราฟ
			set1.setLineWidth(2f); // ขนาดเส้นบนริมกราฟ
			set1.setCircleSize(0f); // ขนาดจุดบนกราฟ
			set1.setDrawCircleHole(false); // จุดบนกราฟทึบ ,true จุดเป็นรู
			set1.setValueTextSize(9f); // ขนาดฟร้อนบนจุดกราฟ
			set1.setValueTextColor(getResources().getColor(R.color.c_content)); // สี
																				// text
			set1.setDrawFilled(true); // ใส่สีใต้กราฟ

//			if (Utils.getSDKInt() >= 18) {
//				// fill drawable only supported on api level 18 and above
//				Drawable drawable = ContextCompat.getDrawable(context,
//						R.drawable.fade_green); // ไล่สีพื้นที่ในกราฟ
//				set1.setFillDrawable(drawable);
//			} else {
//				set1.setFillColor(Color.YELLOW);
//			}
			
//			set1.setFillColor(R.drawable.fade_green);
			set1.setColor(R.drawable.fade_green); ////////////////

			ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
			dataSets.add(set1); // add the datasets

			// create a data object with the datasets
			LineData data = new LineData(xVals, dataSets);
			data.setValueTextColor(Color.TRANSPARENT);

			// set data
			mChart.setData(data);
		}
	}

	@Override
	public void onNothingSelected() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onChartLongPressed(MotionEvent me) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onChartDoubleTapped(MotionEvent me) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onChartSingleTapped(MotionEvent me) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onChartFling(MotionEvent me1, MotionEvent me2, float velocityX,
			float velocityY) {
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
	public void onValueSelected(Entry e, int dataSetIndex,Highlight h) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onChartScale(MotionEvent me, float scaleX, float scaleY) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onChartTranslate(MotionEvent me, float dX, float dY) {
		// TODO Auto-generated method stub
		
	}

}