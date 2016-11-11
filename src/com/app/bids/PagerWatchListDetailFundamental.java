package com.app.bids;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.charts.CombinedChart.DrawOrder;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.Legend.LegendPosition;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.BubbleData;
import com.github.mikephil.charting.data.BubbleDataSet;
import com.github.mikephil.charting.data.BubbleEntry;
import com.github.mikephil.charting.data.CandleData;
import com.github.mikephil.charting.data.CandleDataSet;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.DataSet;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.data.filter.Approximator;
import com.github.mikephil.charting.data.filter.Approximator.ApproximatorType;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.FloatMath;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class PagerWatchListDetailFundamental extends Fragment implements
		OnChartValueSelectedListener {

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
				R.layout.pager_watchlist_detail_fundamental, container, false);

		return rootView;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// try {
		// TextView tv_check_package =
		// (TextView)rootView.findViewById(R.id.tv_check_package);
		// LinearLayout li_check_package =
		// (LinearLayout)rootView.findViewById(R.id.li_check_package);
		// tv_check_package.setVisibility(View.GONE);
		// li_check_package.setVisibility(View.GONE);

		// if (SplashScreen.contentGetUserById != null) {
		// if (!(SplashScreen.contentGetUserById.getString("package")
		// .equals("free"))) {
		// li_check_package.setVisibility(View.VISIBLE);
		// initial Data
		if (UiWatchlistDetail.contentGetFundamental != null) {
			initSetDataFinancial();
			initSetData();
		} else {
			initLoadData();
		}
		// } else {
		// tv_check_package.setVisibility(View.VISIBLE);
		// }
		// } else {
		// tv_check_package.setVisibility(View.VISIBLE);
		// }
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	// ============== Load initLoadData =========================
	private void initLoadData() {
		loadAll resp = new loadAll();
		resp.execute();
	}

	public class loadAll extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;
		// ======= Ui Newds ========
		private JSONObject jsonGetFundamental;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progress.show();

		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// ======= Ui News ========
			// http://www.bidschart.com/service/v2/getFundamentalBySymbol?symbol=scp
			String url_GetFundamental = SplashScreen.url_bidschart
					+ "/service/v2/getFundamentalBySymbol?symbol="
					+ FragmentChangeActivity.strSymbolSelect + "&timestamp="
					+ timestamp;

			// http://bidschart.com/service/v2/getFinancialData?symbol=ppp
			String url_GetFinancialData = SplashScreen.url_bidschart
					+ "/service/v2/getFinancialData?symbol="
					+ FragmentChangeActivity.strSymbolSelect + "&timestamp="
					+ timestamp;

			// Log.v("url_GetFundamental", "" + url_GetFundamental);

			try {
				// ======= Ui News ========
				jsonGetFundamental = ReadJson
						.readJsonObjectFromUrl(url_GetFundamental);
				UiWatchlistDetail.contentGetFinancialData = ReadJson
						.readJsonObjectFromUrl(url_GetFinancialData);
			} catch (IOException e1) {
				connectionError = true;
				jsonGetFundamental = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonGetFundamental = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonGetFundamental = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				try {
					// financial data
					if (UiWatchlistDetail.contentGetFinancialData != null) {
						initSetDataFinancial();
					} else {
						Log.v("json null", "jsonGetFinancialData null");
					}

					// fundamental
					if (jsonGetFundamental != null) {
						if ((jsonGetFundamental.getString("status"))
								.equals("ok")) {
							UiWatchlistDetail.contentGetFundamental = jsonGetFundamental
									.getJSONObject("dataAll");
						} else {
							UiWatchlistDetail.contentGetFundamental = null;
						}

						initSetData();
					} else {
						Log.v("json null", "jsonGetFundamental null");
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Log.v("json jsonGetFundamental null", "jsonGetFundamental null");
			}
		}
	}

	// ------------- init data Financial
	JSONArray graph_financial;
	JSONArray data_financial;

	private void initSetDataFinancial() {

		// Toast.makeText(context,
		// "initSetDataFinancial"+FragmentChangeActivity.strSymbolSelect,
		// 0).show();

		try {
			if (UiWatchlistDetail.contentGetFinancialData != null) {
				graph_financial = UiWatchlistDetail.contentGetFinancialData
						.getJSONArray("graph_financial");
				data_financial = UiWatchlistDetail.contentGetFinancialData
						.getJSONArray("data_financial");

				LinearLayout list_title = (LinearLayout) rootView
						.findViewById(R.id.list_title);
				LinearLayout list_data = (LinearLayout) rootView
						.findViewById(R.id.list_data);
				list_title.removeAllViews();
				list_data.removeAllViews();

				for (int i = 0; i < data_financial.length(); i++) {
					JSONArray jsaGraph = graph_financial.getJSONArray(i);
					JSONArray jsaData = data_financial.getJSONArray(i);

					// index 0 title
					if (i == 0) {
						for (int j = 0; j < jsaGraph.length(); j++) {
							View viewTitle = ((Activity) context)
									.getLayoutInflater().inflate(
											R.layout.row_financial_data, null);
							TextView tv_row = (TextView) viewTitle
									.findViewById(R.id.tv_row);
							tv_row.setText(jsaGraph.get(j).toString());
							if (j == 0) {
								tv_row.setTextColor(context.getResources()
										.getColor(FunctionSetBg.arrColor[4]));
							} else {
								tv_row.setTextColor(context.getResources()
										.getColor(FunctionSetBg.arrColor[5]));
							}

							list_title.addView(viewTitle);
						}
						// set data เริ่มจาก index 1
					} else {
						JSONArray jsa_index_prev = graph_financial
								.getJSONArray(i - 1); // index 1 ตั้งไว้เทียบสี

						LayoutInflater inflater = (LayoutInflater) context
								.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						View viewData = inflater.inflate(R.layout.row_linear,
								null);

						LinearLayout li_row = (LinearLayout) viewData
								.findViewById(R.id.li_row);
						for (int j = 0; j < jsaData.length(); j++) {
							LayoutInflater inflater2 = (LayoutInflater) context
									.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
							View viewDataRow = inflater2.inflate(
									R.layout.row_financial_data, null);

							TextView tv_row = (TextView) viewDataRow
									.findViewById(R.id.tv_row);
							tv_row.setText(jsaData.get(j).toString());
							if (j == 0) { // title
								tv_row.setTextColor(context.getResources()
										.getColor(FunctionSetBg.arrColor[4]));
							} else { // data
								if (i == 1) {
									tv_row.setTextColor(context
											.getResources()
											.getColor(FunctionSetBg.arrColor[3]));
								} else {
									if (i > 1) {
										String str1 = jsaGraph.get(j)
												.toString(); // ตัวตั้ง
										String str2 = jsa_index_prev.get(j)
												.toString();
										if (j == 2) {
											tv_row.setTextColor(context
													.getResources()
													.getColor(
															FunctionSetBg
																	.setColorCompare2Attribute(
																			str2,
																			str1)));
										} else {
											tv_row.setTextColor(context
													.getResources()
													.getColor(
															FunctionSetBg
																	.setColorCompare2Attribute(
																			str1,
																			str2)));
										}
									}
								}
							}
							li_row.addView(viewDataRow);
						}
						list_data.addView(li_row);
					}
				}

				// ------ Stacked Bar Chart
				initSetDataBarChart();

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// ------------- init data
	TextView tv_turnaroundt, tv_growth, tv_dividend, tv_fundamentals,
			tv_strength, tv_stf_fun, tv_quarter_year;
	TextView tv_activity_1, tv_activity_2, tv_activity_3, tv_activity_4,
			tv_activity_5;
	TextView tv_profitability_1, tv_profitability_2, tv_profitability_3,
			tv_profitability_4, tv_profitability_5;
	TextView tv_leverage_1, tv_leverage_2, tv_leverage_3, tv_leverage_4,
			tv_leverage_5;
	TextView tv_liquidity_1, tv_liquidity_2, tv_liquidity_3, tv_liquidity_4,
			tv_liquidity_5;
	TextView tv_fundamental_trend, tv_rankingsector, tv_cgscore, tv_score;
	ImageView img_chart;
	ProgressWheel progress_score;

	private void initSetData() {
		try {
			if (UiWatchlistDetail.contentGetFundamental != null) {
				// -------------
				tv_quarter_year = (TextView) rootView
						.findViewById(R.id.tv_quarter_year);
				tv_stf_fun = (TextView) rootView.findViewById(R.id.tv_stf_fun);
				tv_turnaroundt = (TextView) rootView
						.findViewById(R.id.tv_turnaroundt);
				tv_growth = (TextView) rootView.findViewById(R.id.tv_growth);
				tv_dividend = (TextView) rootView
						.findViewById(R.id.tv_dividend);
				tv_fundamentals = (TextView) rootView
						.findViewById(R.id.tv_fundamentals);
				tv_strength = (TextView) rootView
						.findViewById(R.id.tv_strength);
				// -------------
				tv_activity_1 = (TextView) rootView
						.findViewById(R.id.tv_activity_1);
				tv_activity_2 = (TextView) rootView
						.findViewById(R.id.tv_activity_2);
				tv_activity_3 = (TextView) rootView
						.findViewById(R.id.tv_activity_3);
				tv_activity_4 = (TextView) rootView
						.findViewById(R.id.tv_activity_4);
				tv_activity_5 = (TextView) rootView
						.findViewById(R.id.tv_activity_5);

				tv_profitability_1 = (TextView) rootView
						.findViewById(R.id.tv_profitability_1);
				tv_profitability_2 = (TextView) rootView
						.findViewById(R.id.tv_profitability_2);
				tv_profitability_3 = (TextView) rootView
						.findViewById(R.id.tv_profitability_3);
				tv_profitability_4 = (TextView) rootView
						.findViewById(R.id.tv_profitability_4);
				tv_profitability_5 = (TextView) rootView
						.findViewById(R.id.tv_profitability_5);

				tv_leverage_1 = (TextView) rootView
						.findViewById(R.id.tv_leverage_1);
				tv_leverage_2 = (TextView) rootView
						.findViewById(R.id.tv_leverage_2);
				tv_leverage_3 = (TextView) rootView
						.findViewById(R.id.tv_leverage_3);
				tv_leverage_4 = (TextView) rootView
						.findViewById(R.id.tv_leverage_4);
				tv_leverage_5 = (TextView) rootView
						.findViewById(R.id.tv_leverage_5);

				tv_liquidity_1 = (TextView) rootView
						.findViewById(R.id.tv_liquidity_1);
				tv_liquidity_2 = (TextView) rootView
						.findViewById(R.id.tv_liquidity_2);
				tv_liquidity_3 = (TextView) rootView
						.findViewById(R.id.tv_liquidity_3);
				tv_liquidity_4 = (TextView) rootView
						.findViewById(R.id.tv_liquidity_4);
				tv_liquidity_5 = (TextView) rootView
						.findViewById(R.id.tv_liquidity_5);
				// -------------
				tv_fundamental_trend = (TextView) rootView
						.findViewById(R.id.tv_fundamental_trend);
				tv_rankingsector = (TextView) rootView
						.findViewById(R.id.tv_rankingsector);
				tv_cgscore = (TextView) rootView.findViewById(R.id.tv_cgscore);
				img_chart = (ImageView) rootView.findViewById(R.id.img_chart);
				// -------------
				progress_score = (ProgressWheel) rootView
						.findViewById(R.id.progress_score);
				tv_score = (TextView) rootView.findViewById(R.id.tv_score);

				String strScore = UiWatchlistDetail.contentGetFundamental
						.getString("score");

				double dScore = Double.parseDouble(strScore);
				int percentCal20 = (int) ((100 * dScore) / 20);
				int percentCal360 = (int) ((360 * percentCal20) / 100);
				progress_score.incrementProgress(percentCal360); // เต็มวง 360
				tv_score.setText("" + dScore);

				// ----- tab title
				String strColorGrowth = UiWatchlistDetail.contentGetFundamental
						.getString("growth");
				String strColorFundamentals = UiWatchlistDetail.contentGetFundamental
						.getString("fundamentals");
				String strColorDividend = UiWatchlistDetail.contentGetFundamental
						.getString("dividend");
				String strColorTurnaround = UiWatchlistDetail.contentGetFundamental
						.getString("turnaround");
				String strColorStrength = UiWatchlistDetail.contentGetFundamental
						.getString("strength");

				if (strColorGrowth.equals("true")) {
					tv_growth.setTextColor(getResources().getColor(
							R.color.c_content));
					tv_growth.setBackgroundResource(R.color.c_success);
				}
				if (strColorFundamentals.equals("true")) {
					tv_fundamentals.setTextColor(getResources().getColor(
							R.color.c_content));
					tv_fundamentals.setBackgroundResource(R.color.c_success);
				}
				if (strColorDividend.equals("true")) {
					tv_dividend.setTextColor(getResources().getColor(
							R.color.c_content));
					tv_dividend.setBackgroundResource(R.color.c_success);
				}
				if (strColorTurnaround.equals("true")) {
					tv_turnaroundt.setTextColor(getResources().getColor(
							R.color.c_content));
					tv_turnaroundt.setBackgroundResource(R.color.c_success);
				}
				if (strColorStrength.equals("true")) {
					tv_strength.setTextColor(getResources().getColor(
							R.color.c_content));
					tv_strength.setBackgroundResource(R.color.c_success);
				}

				tv_quarter_year.setText("Data: "
						+ UiWatchlistDetail.contentGetFundamental
								.getString("quarter")
						+ "/"
						+ UiWatchlistDetail.contentGetFundamental
								.getString("year"));

				tv_stf_fun
						.setBackgroundColor(FunctionSetBg
								.setColorWatchListSymbolFundamental(UiWatchlistDetail.contentGetDetailFundamental));

				// if (UiWatchlistDetail.contentGetDetail != null) {
				// String strFundam = UiWatchlistDetail.contentGetDetail
				// .getString("fundamental");
				// tv_stf_fun.setBackgroundColor(BgColorSymbolDetail
				// .setColorWatchListSymbolFundamental(UiWatchlistDetail.contentGetDetailFundamental));
				// }

				// ----- box
				String strColorActivity = UiWatchlistDetail.contentGetFundamental
						.getString("activity");
				String strColorProfitability = UiWatchlistDetail.contentGetFundamental
						.getString("profitability");
				String strColorLeverage = UiWatchlistDetail.contentGetFundamental
						.getString("leverage");
				String strColorLiquidity = UiWatchlistDetail.contentGetFundamental
						.getString("liquidity");

				if (strColorActivity != "") {
					double dColor = Double.parseDouble(strColorActivity
							.replaceAll(",", ""));
					if (dColor < 1) {

					} else if (dColor < 2) {
						tv_activity_1.setBackgroundResource(R.color.c_success);
					} else if (dColor < 3) {
						tv_activity_1.setBackgroundResource(R.color.c_success);
						tv_activity_2.setBackgroundResource(R.color.c_success);
					} else if (dColor < 4) {
						tv_activity_1.setBackgroundResource(R.color.c_success);
						tv_activity_2.setBackgroundResource(R.color.c_success);
						tv_activity_3.setBackgroundResource(R.color.c_success);
					} else if (dColor < 5) {
						tv_activity_1.setBackgroundResource(R.color.c_success);
						tv_activity_2.setBackgroundResource(R.color.c_success);
						tv_activity_3.setBackgroundResource(R.color.c_success);
						tv_activity_4.setBackgroundResource(R.color.c_success);
					} else if (dColor < 6) {
						tv_activity_1.setBackgroundResource(R.color.c_success);
						tv_activity_2.setBackgroundResource(R.color.c_success);
						tv_activity_3.setBackgroundResource(R.color.c_success);
						tv_activity_4.setBackgroundResource(R.color.c_success);
						tv_activity_5.setBackgroundResource(R.color.c_success);
					} else {
					}
				}

				if (strColorProfitability != "") {
					double dColor = Double.parseDouble(strColorProfitability
							.replaceAll(",", ""));
					if (dColor < 1) {

					} else if (dColor < 2) {
						tv_profitability_1
								.setBackgroundResource(R.color.c_success);
					} else if (dColor < 3) {
						tv_profitability_1
								.setBackgroundResource(R.color.c_success);
						tv_profitability_2
								.setBackgroundResource(R.color.c_success);
					} else if (dColor < 4) {
						tv_profitability_1
								.setBackgroundResource(R.color.c_success);
						tv_profitability_2
								.setBackgroundResource(R.color.c_success);
						tv_profitability_3
								.setBackgroundResource(R.color.c_success);
					} else if (dColor < 5) {
						tv_profitability_1
								.setBackgroundResource(R.color.c_success);
						tv_profitability_2
								.setBackgroundResource(R.color.c_success);
						tv_profitability_3
								.setBackgroundResource(R.color.c_success);
						tv_profitability_4
								.setBackgroundResource(R.color.c_success);
					} else if (dColor < 6) {
						tv_profitability_1
								.setBackgroundResource(R.color.c_success);
						tv_profitability_2
								.setBackgroundResource(R.color.c_success);
						tv_profitability_3
								.setBackgroundResource(R.color.c_success);
						tv_profitability_4
								.setBackgroundResource(R.color.c_success);
						tv_profitability_5
								.setBackgroundResource(R.color.c_success);
					} else {
					}
				}

				if (strColorLeverage != "") {
					double dColor = Double.parseDouble(strColorLeverage
							.replaceAll(",", ""));
					if (dColor < 1) {

					} else if (dColor < 2) {
						tv_leverage_1.setBackgroundResource(R.color.c_success);
					} else if (dColor < 3) {
						tv_leverage_1.setBackgroundResource(R.color.c_success);
						tv_leverage_2.setBackgroundResource(R.color.c_success);
					} else if (dColor < 4) {
						tv_leverage_1.setBackgroundResource(R.color.c_success);
						tv_leverage_2.setBackgroundResource(R.color.c_success);
						tv_leverage_3.setBackgroundResource(R.color.c_success);
					} else if (dColor < 5) {
						tv_leverage_1.setBackgroundResource(R.color.c_success);
						tv_leverage_2.setBackgroundResource(R.color.c_success);
						tv_leverage_3.setBackgroundResource(R.color.c_success);
						tv_leverage_4.setBackgroundResource(R.color.c_success);
					} else if (dColor < 6) {
						tv_leverage_1.setBackgroundResource(R.color.c_success);
						tv_leverage_2.setBackgroundResource(R.color.c_success);
						tv_leverage_3.setBackgroundResource(R.color.c_success);
						tv_leverage_4.setBackgroundResource(R.color.c_success);
						tv_leverage_5.setBackgroundResource(R.color.c_success);
					} else {
					}
				}

				if (strColorLiquidity != "") {
					double dColor = Double.parseDouble(strColorLiquidity
							.replaceAll(",", ""));
					if (dColor < 1) {

					} else if (dColor < 2) {
						tv_liquidity_1.setBackgroundResource(R.color.c_success);
					} else if (dColor < 3) {
						tv_liquidity_1.setBackgroundResource(R.color.c_success);
						tv_liquidity_2.setBackgroundResource(R.color.c_success);
					} else if (dColor < 4) {
						tv_liquidity_1.setBackgroundResource(R.color.c_success);
						tv_liquidity_2.setBackgroundResource(R.color.c_success);
						tv_liquidity_3.setBackgroundResource(R.color.c_success);
					} else if (dColor < 5) {
						tv_liquidity_1.setBackgroundResource(R.color.c_success);
						tv_liquidity_2.setBackgroundResource(R.color.c_success);
						tv_liquidity_3.setBackgroundResource(R.color.c_success);
						tv_liquidity_4.setBackgroundResource(R.color.c_success);
					} else if (dColor < 6) {
						tv_liquidity_1.setBackgroundResource(R.color.c_success);
						tv_liquidity_2.setBackgroundResource(R.color.c_success);
						tv_liquidity_3.setBackgroundResource(R.color.c_success);
						tv_liquidity_4.setBackgroundResource(R.color.c_success);
						tv_liquidity_5.setBackgroundResource(R.color.c_success);
					} else {
					}
				}

				// ----- bottom
				String strFundamentalTrend = UiWatchlistDetail.contentGetFundamental
						.getString("fundamental_trend");
				tv_fundamental_trend.setText(strFundamentalTrend);
				if (strFundamentalTrend.equals("up")) {
					tv_fundamental_trend.setTextColor(context.getResources()
							.getColor(R.color.c_success));
				} else if (strFundamentalTrend.equals("down")) {
					tv_fundamental_trend.setTextColor(context.getResources()
							.getColor(R.color.c_danger));
				} else {
					tv_fundamental_trend.setTextColor(context.getResources()
							.getColor(R.color.c_warning));
				}
				tv_rankingsector
						.setText(UiWatchlistDetail.contentGetFundamental
								.getString("rangkingsector"));
				tv_cgscore.setText(UiWatchlistDetail.contentGetFundamental
						.getString("cgscore"));

				FragmentChangeActivity.imageLoader.displayImage(
						SplashScreen.url_bidschart_chart
								+ "fundamental-"
								+ UiWatchlistDetail.contentGetFundamental
										.getString("symbol") + ".png",
						img_chart);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// ------------- init DataBarChart
	// private BarChart mChartBar;
	private CombinedChart mChartCombine;
	private int itemcount = 0;

	private void initSetDataBarChart() throws JSONException {
		// mChartBar = (BarChart) rootView.findViewById(R.id.chart_bar);
		mChartCombine = (CombinedChart) rootView
				.findViewById(R.id.chart_combine);

		// mChartBar.removeAllViews();
		mChartCombine.removeAllViews();

		// mChartBar.setOnChartValueSelectedListener(this);
		//
		// mChartBar.setDescription("");
		//
		// // if more than 60 entries are displayed in the chart, no values will
		// be
		// // drawn
		// mChartBar.setMaxVisibleValueCount(60);
		//
		// // scaling can now only be done on x- and y-axis separately
		// mChartBar.setPinchZoom(false);
		//
		// mChartBar.setDrawGridBackground(false);
		// mChartBar.setDrawBarShadow(false);
		//
		// mChartBar.setDrawValueAboveBar(false);
		//
		// // change the position of the y-labels
		// YAxis yLabels = mChartBar.getAxisLeft();
		// yLabels.setValueFormatter(new MyValueFormatter());
		// yLabels.setTextColor(context.getResources().getColor(R.color.c_content));
		//
		// mChartBar.getAxisRight().setEnabled(false);
		//
		// XAxis xLabels = mChartBar.getXAxis();
		// xLabels.setPosition(XAxisPosition.BOTTOM);
		// xLabels.setTextColor(context.getResources().getColor(R.color.c_content));
		//
		// Legend l = mChartBar.getLegend();
		// l.setPosition(LegendPosition.BELOW_CHART_RIGHT);
		// l.setFormSize(8f);
		// l.setFormToTextSpace(4f);
		// l.setXEntrySpace(6f);
		//
		// // -------------------------------
		// ArrayList<String> xVals = new ArrayList<String>();
		// ArrayList<String> xVals_title = new ArrayList<String>();
		// for (int i = 1; i < graph_financial.length(); i++) {
		// JSONArray jsaGraph = graph_financial.getJSONArray(i);
		// xVals.add(jsaGraph.get(0).toString());
		// if (i == 0) {
		// for (int j = 0; j < jsaGraph.length(); j++) {
		// xVals_title.add(jsaGraph.get(j).toString());
		// }
		// }
		// }
		//
		// ArrayList<BarEntry> yVals1 = new ArrayList<BarEntry>();
		// for (int i = 1; i < graph_financial.length(); i++) {
		// JSONArray jsaGraph = graph_financial.getJSONArray(i);
		// float val1 = Float.parseFloat(jsaGraph.get(1).toString());
		// float val2 = Float.parseFloat(jsaGraph.get(2).toString());
		// float val3 = Float.parseFloat(jsaGraph.get(3).toString());
		// float val4 = Float.parseFloat(jsaGraph.get(4).toString());
		// float val5 = Float.parseFloat(jsaGraph.get(5).toString());
		// float val6 = Float.parseFloat(jsaGraph.get(6).toString());
		// float[] val = { val1, val2, val3, val4 };
		//
		// yVals1.add(new BarEntry(val, (i - 1)));
		// // yVals1.add(new BarEntry(new float[] { val1, val2, val3, val4},
		// // (i-1)));
		// }
		//
		// BarDataSet set1 = new BarDataSet(yVals1, "");
		// set1.setColors(SplashScreen.mpChartArrColor);
		// set1.setStackLabels(new String[] { "Births", "Divorces", "Marriages"
		// });
		//
		// ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
		// dataSets.add(set1);
		//
		// BarData data = new BarData(xVals, dataSets);
		// data.setValueFormatter(new MyValueFormatter());
		// // data.setValueTextColor(context.getResources().getColor(
		// // R.color.c_content));
		//
		// mChartBar.setData(data);
		// mChartBar.invalidate();

		// mChartCombine.setOnChartValueSelectedListener(this);
		// mChartCombine.setDescription("");
		// // if more than 60 entries are displayed in the chart, no values will
		// be
		// // drawn
		// mChartCombine.setMaxVisibleValueCount(60);
		// // scaling can now only be done on x- and y-axis separately
		// mChartCombine.setPinchZoom(false);
		// mChartCombine.setDrawGridBackground(false);
		// mChartCombine.setDrawBarShadow(false);
		// mChartCombine.setDrawValueAboveBar(false);

		// -----------------------------
		mChartCombine.setDescription("");
		// mChartCombine.setBackgroundColor(Color.WHITE);
		mChartCombine.setDrawGridBackground(false);
		mChartCombine.setDrawBarShadow(false);

		// draw bars behind lines
		mChartCombine.setDrawOrder(new DrawOrder[] { DrawOrder.BAR,
				DrawOrder.BUBBLE, DrawOrder.CANDLE, DrawOrder.LINE,
				DrawOrder.SCATTER });

		float MaxValueR, MinValueR;
		ArrayList<Float> arrMaxMinR = new ArrayList<Float>();
		ArrayList<String> xVals = new ArrayList<String>();
		for (int i = 1; i < graph_financial.length(); i++) {
			JSONArray jsaGraph = graph_financial.getJSONArray(i);
			xVals.add(jsaGraph.get(0).toString());
			arrMaxMinR.add(Float.parseFloat(jsaGraph.get(6).toString()
					.replaceAll(",", "")));
		}
		MaxValueR = FloatMath.ceil(Collections.max(arrMaxMinR));
		MinValueR = FloatMath.floor(Collections.min(arrMaxMinR));

		// --- ขวา
		YAxis rightAxis = mChartCombine.getAxisRight();
		rightAxis.setDrawGridLines(false);
		rightAxis.setTextColor(context.getResources().getColor(
				R.color.c_content));
		rightAxis.setStartAtZero(false);
		rightAxis.setAxisMaxValue(MaxValueR);
		rightAxis.setAxisMinValue(MinValueR);

		// --- ซ้าย
		YAxis leftAxis = mChartCombine.getAxisLeft();
		leftAxis.setDrawGridLines(false);
		leftAxis.setTextColor(context.getResources()
				.getColor(R.color.c_content));
		leftAxis.setStartAtZero(false);

		// --- บน, ล่าง
		XAxis xAxis = mChartCombine.getXAxis();
		xAxis.setPosition(XAxisPosition.BOTTOM);
		xAxis.setTextColor(context.getResources().getColor(R.color.c_content));

		CombinedData dataCombine = new CombinedData(xVals);
		dataCombine.setData(generateLineData());
		dataCombine.setData(generateBarData());
		// data.setData(generateBubbleData());
		// data.setData(generateScatterData());
		// data.setData(generateCandleData());

		mChartCombine.setData(dataCombine);
		mChartCombine.invalidate();

	}

	private LineData generateLineData() throws JSONException {

		LineData d = new LineData();

		ArrayList<Entry> entries = new ArrayList<Entry>();
		itemcount = graph_financial.length() - 1;
		for (int i = 1; i < graph_financial.length(); i++) {
			JSONArray jsaGraph = graph_financial.getJSONArray(i);
			float fVals = Float.parseFloat(jsaGraph.get(6).toString());
			entries.add(new Entry(fVals, (i - 1)));
		}

		LineDataSet set = new LineDataSet(entries, "Line DataSet");
		set.setColor(context.getResources().getColor(R.color.c_warning));
		set.setLineWidth(2f);
		set.setCircleColor(context.getResources().getColor(R.color.c_warning));
		set.setCircleSize(2.5f);
		set.setFillColor(context.getResources().getColor(R.color.c_warning));
		set.setDrawCubic(true);
		set.setDrawValues(true);
		set.setValueTextSize(10f);
		set.setValueTextColor(context.getResources()
				.getColor(R.color.c_content));

		set.setAxisDependency(YAxis.AxisDependency.RIGHT);

		d.setValueTextColor(android.graphics.Color.TRANSPARENT);
		d.addDataSet(set);

		return d;
	}

	private BarData generateBarData() {
		BarData d = new BarData();
		try {
			ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
			BarDataSet set = new BarDataSet(entries, "");
			for (int i = 1; i < graph_financial.length(); i++) {
				JSONArray jsaGraph = graph_financial.getJSONArray(i);
				float val1 = Float.parseFloat(jsaGraph.get(1).toString());
				float val2 = Float.parseFloat(jsaGraph.get(2).toString());
				float val3 = Float.parseFloat(jsaGraph.get(3).toString());
				float val4 = Float.parseFloat(jsaGraph.get(4).toString());
				float[] val = { val1, val2, val3, val4 };

				entries.add(new BarEntry(val, (i - 1)));

				set = new BarDataSet(entries, "");
				set.setColors(SplashScreen.mpChartArrColor);
				set.setValueTextColor(context.getResources().getColor(
						R.color.c_content));

				// d.addDataSet(set);
				// set.setAxisDependency(YAxis.AxisDependency.LEFT);
			}
			d.setValueTextColor(android.graphics.Color.TRANSPARENT);
			d.addDataSet(set);
		} catch (JSONException e) {
			e.printStackTrace();
		}

		return d;
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
		// TODO Auto-generated method stub

	}

	@Override
	public void onNothingSelected() {
		// TODO Auto-generated method stub

	}

}