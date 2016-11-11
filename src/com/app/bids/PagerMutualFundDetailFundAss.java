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

public class PagerMutualFundDetailFundAss extends Fragment implements
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
				R.layout.pager_mutualfund_detail_fundass, container, false);

		return rootView;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		try {
			if (UiMutualfundDetail.contentGetFundAssData != null) {
				initSetData();
			} else {
				initLoadData();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			// http://bidschart.com/service/v2/getFundAsset?fund=1DIV
			String url_GetData = SplashScreen.url_bidschart
					+ "/service/v2/getFundAsset?fund="
					+ FragmentChangeActivity.strSymbolSelect + "&timestamp="
					+ timestamp;

			Log.v("url_GetMutual FundAss", "" + url_GetData);

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
							UiMutualfundDetail.contentGetFundAssData = jsonGetData
									.getJSONArray("data");
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
	private void initSetData() throws JSONException {
		if (UiMutualfundDetail.contentGetFundAssData != null) {
			mChart = (PieChart) rootView.findViewById(R.id.chart1);
			mChart.setUsePercentValues(true);
			mChart.setDescription("");
			mChart.setExtraOffsets(5, 10, 5, 5);

			mChart.setDragDecelerationFrictionCoef(0.95f);
			// mChart.setCenterText(generateCenterSpannableText());

			mChart.setDrawHoleEnabled(true);
			mChart.setHoleColor(getResources().getColor(R.color.bg_default));

			mChart.setTransparentCircleColor(Color.WHITE);
			mChart.setTransparentCircleAlpha(110);

			mChart.setHoleRadius(58f); // ขนาดวงกลมตรงกลาง
			mChart.setTransparentCircleRadius(60f); // เงารอบวงกลม

			mChart.setDrawCenterText(true);

			mChart.setRotationAngle(0);
			// enable rotation of the chart by touch
			mChart.setRotationEnabled(true);
			mChart.setHighlightEnabled(true);

			// mChart.setUnit(" โ�ฌ");
			// mChart.setDrawUnitsInChart(true);

			// add a selection listener
			mChart.setOnChartValueSelectedListener(this);

			setData(UiMutualfundDetail.contentGetFundAssData.length(), 100);

			mChart.animateY(1000, Easing.EasingOption.EaseInOutQuad);
			// mChart.spin(2000, 0, 360);

			Legend l = mChart.getLegend();
			l.setEnabled(false);
//			l.setPosition(LegendPosition.BELOW_CHART_CENTER);
//			// l.setHorizontalAlignment(LegendHorizontalAlignment.RIGHT);
//			l.setXEntrySpace(3f);
//			l.setTextColor(getResources().getColor(R.color.c_content));
//			l.setYEntrySpace(0f);
//			l.setYOffset(0f);

			Log.v("l.colorsObjc", "" + l.getTextColor());

			// NSArray *arrColor = l.colorsObjc;
			// Log.v("l.colorsObjc", "");

		}
	}

	JSONArray jsaDataList;
	private void setData(int count, float range) throws JSONException {
		// ----- หาผลรวม value
		float fSumV = 0;
		for (int i = 0; i < count; i++) {
			String strValue = UiMutualfundDetail.contentGetFundAssData
					.getJSONObject(i).getString("value");
			fSumV = fSumV + FunctionSymbol.setStringPaseFloat(strValue);
		}

		// ----- คิด value แต่ละตัวเป็น %
		ArrayList<Entry> yVals1 = new ArrayList<Entry>();
		for (int i = 0; i < count; i++) {
			String strValue = UiMutualfundDetail.contentGetFundAssData
					.getJSONObject(i).getString("value");
			float fValue = FunctionSymbol.setStringPaseFloat(strValue);
			float fPercent = (100 * fValue) / fSumV;

			yVals1.add(new Entry(fPercent, i));
		}

		// ----- split detail ตัวย่อ
		jsaDataList = new JSONArray();
		ArrayList<String> xVals = new ArrayList<String>();
		for (int i = 0; i < count; i++) {
			String strDetail = UiMutualfundDetail.contentGetFundAssData.getJSONObject(
					i).getString("detail");
			String sptSym0[] = strDetail.split("\\(");
			String sptSym1 = sptSym0[1].split("\\)")[0];
			xVals.add(sptSym1);
			
			String strValue = UiMutualfundDetail.contentGetFundAssData
					.getJSONObject(i).getString("value");
			JSONObject jso = new JSONObject();
			jso.put("symbol", ""+(i+1)+". "+sptSym1);
			jso.put("detail", strDetail.split("\\.")[1]);
			jso.put("percent", strValue+"%");
			jsaDataList.put(jso);
		}

		//----- test
//		for(int i=0 ;i<yVals1.size() ; i++){
//			Log.v("yVals1", ""+yVals1.get(i));
//			// Entry, xIndex: 0 val (sum): 13.172621
//		}
		
		PieDataSet dataSet = new PieDataSet(yVals1,
				"สินทรัพย์ 10 อันดับแรกที่ลงทุนกองทุน");
		dataSet.setSliceSpace(3f);
		dataSet.setSelectionShift(5f);
		dataSet.setColors(SplashScreen.mpChartArrColor);
		
		// Log.v("colorscolorscolorscolorscolors", ""+colors);
		// dataSet.setSelectionShift(0f);

//		dataSet.setValueLinePart1OffsetPercentage(80.f);
//		dataSet.setValueLinePart1Length(0.3f);
//		dataSet.setValueLinePart2Length(0.4f);
//		dataSet.setValueLineColor(Color.WHITE);
//		dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

		PieData data = new PieData(xVals, dataSet);
		data.setValueFormatter(new PercentFormatter());
		data.setValueTextSize(11f);
		data.setValueTextColor(Color.WHITE);
		mChart.setData(data);

		// undo all highlights
		mChart.highlightValues(null);

		mChart.invalidate();
		
		// ----------- set list data top 100
		setListData();
	}
	
	// ============== set list data ================
		private void setListData() {
			Legend l = mChart.getLegend();
//			Log.v("l.getColors()", ""+l.getColors()[0]); // -10266777

			try {
				if (jsaDataList != null) {
					if (jsaDataList.length() > 0) {
						LinearLayout li_list = (LinearLayout) rootView
								.findViewById(R.id.li_list);
						li_list.removeAllViews();
						for (int i = 0; i < jsaDataList.length(); i++) {
							View viewDetail = ((Activity) context)
									.getLayoutInflater().inflate(
											R.layout.row_mutualfund_fundass,
											null);

							// ------ get data index
							JSONObject jsoIndex = jsaDataList.getJSONObject(i);

							String strSymbol = jsoIndex.getString("symbol");
							String strDetail = jsoIndex.getString("detail");
							String strPercent = jsoIndex.getString("percent");
							
							ImageView img_cat_color = (ImageView)viewDetail.findViewById(R.id.img_cat_color);
							TextView tv_symbol = (TextView) viewDetail
									.findViewById(R.id.tv_symbol);
							TextView tv_detail = (TextView) viewDetail
									.findViewById(R.id.tv_detail);
							TextView tv_percent = (TextView) viewDetail
									.findViewById(R.id.tv_percent);
							tv_symbol.setText(strSymbol);
							tv_detail.setText(strDetail);
							tv_percent.setText(strPercent);
														
//							Log.v("l.getColors()", ""+l.getColors()[i]); // -10266777
							Integer intColor = l.getColors()[i];
							String hexColor = "#" + Integer.toHexString(intColor).substring(2);
							img_cat_color.setBackgroundColor(Color.parseColor(hexColor));
							
							li_list.addView(viewDetail);
						}
					}
				} else {
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

	// ----- ใส่ Text กลาง ข้างในวงกลม
	private SpannableString generateCenterSpannableText() {
		SpannableString s = new SpannableString(
				"MPAndroidChart\ndeveloped by Philipp Jahoda");
		s.setSpan(new RelativeSizeSpan(1.7f), 0, 14, 0);
		s.setSpan(new StyleSpan(Typeface.NORMAL), 14, s.length() - 15, 0);
		s.setSpan(new ForegroundColorSpan(Color.GRAY), 14, s.length() - 15, 0);
		s.setSpan(new RelativeSizeSpan(.8f), 14, s.length() - 15, 0);
		s.setSpan(new StyleSpan(Typeface.ITALIC), s.length() - 14, s.length(),
				0);
		s.setSpan(new ForegroundColorSpan(ColorTemplate.getHoloBlue()),
				s.length() - 14, s.length(), 0);
		return s;
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