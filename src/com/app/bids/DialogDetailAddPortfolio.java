package com.app.bids;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.bids.R;
import com.app.bids.PagerSetAlertSelect.setAddSetAlert;

public class DialogDetailAddPortfolio {

	private static Dialog dialog;
	private static Context context;
	private static Dialog dialogLoading;

	public DialogDetailAddPortfolio(Context context2) {
		this.context = context2;
		this.dialog = new Dialog(context2);

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
		dialog.setContentView(R.layout.dialog_add_portfolio);

		// ----------
		dialogLoading = new Dialog(context);
		dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogLoading.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialogLoading.setContentView(R.layout.progress_bar);
		dialogLoading.setCancelable(false);
		dialogLoading.setCanceledOnTouchOutside(false);

		((TextView) dialog.findViewById(R.id.tv_close))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dissmiss();
					}
				});

	}

	public static void show() {
		dialog.show();

		initSetData();

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

	public static void dissmiss() {
		dialog.dismiss();
	}

	// ----- init set data
	public static double d_invest, d_volume, d_value, d_invest_begin;
	public static TextView tv_save;
	// public static TextView tv_name_initial, tv_invest_value, tv_name_t,
	// tv_invest_change;
	public static TextView tv_invest_value_plus, tv_invest_value_minus,
			tv_volume_plus, tv_volume_minus, tv_value_cal;
	public static EditText et_invest_value, et_volume;

	public static void initSetData() {

		setRowDetail(); // ----------------

		d_invest = 0;
		d_volume = 0;
		d_value = 0;

		tv_save = (TextView) dialog.findViewById(R.id.tv_save);
		tv_invest_value_plus = (TextView) dialog
				.findViewById(R.id.tv_invest_value_plus);
		tv_invest_value_minus = (TextView) dialog
				.findViewById(R.id.tv_invest_value_minus);
		tv_volume_plus = (TextView) dialog.findViewById(R.id.tv_volume_plus);
		tv_volume_minus = (TextView) dialog.findViewById(R.id.tv_volume_minus);
		tv_value_cal = (TextView) dialog.findViewById(R.id.tv_value_cal);

		et_invest_value = (EditText) dialog.findViewById(R.id.et_invest_value);
		et_volume = (EditText) dialog.findViewById(R.id.et_volume);

		try {
			if (UiWatchlistDetail.contentGetDetail != null) {
				// main
				String name_initial, invest_value, name_e, invest_change;
				name_initial = UiWatchlistDetail.contentGetDetail
						.getString("symbol_name");
				invest_value = UiWatchlistDetail.contentGetDetail
						.getString("last_trade");
				name_e = UiWatchlistDetail.contentGetDetail
						.getString("symbol_fullname_eng");
				invest_change = UiWatchlistDetail.contentGetDetail
						.getString("percentChange");

				if (!invest_value.equals("") && !invest_value.equals("null")
						&& !invest_value.equals(null)) {
					d_invest_begin = Double.parseDouble(invest_value);
				} else {
					d_invest_begin = 0.00;
				}

				// ---------------------------------------
				et_invest_value.setText("" + d_invest_begin);
				et_volume.setText("0.0");

				tv_save.setEnabled(false);
				tv_save.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (((et_invest_value.getText().toString().trim()) != "")
								&& ((et_volume.getText().toString().trim()) != "")) {
							sendAddPortFolio();
						}
					}
				});

				d_invest = Double.parseDouble("" + d_invest_begin);
				// ----- invest
				tv_invest_value_plus.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Log.v("tv_invest_value_plus", "000000000000");
						String text = et_invest_value.getText().toString();
						d_invest = Double.parseDouble(text);
						d_invest = d_invest + calGapUp(d_invest);
						setDataText();
					}
				});
				tv_invest_value_minus.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String text = et_invest_value.getText().toString();
						d_invest = Double.parseDouble(text);
						if (d_invest > 0) {
							d_invest = d_invest - calGapDown(d_invest);
							setDataText();
						}
					}
				});

				// ----- volume
				tv_volume_minus.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						Log.v("tv_volume_minus", "000000000000");
						String text = et_volume.getText().toString();
						d_volume = Double.parseDouble(text);
						if (d_volume >= 100) {
							d_volume = d_volume - 100;
							setDataText();
						}
					}
				});
				tv_volume_plus.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String text = et_volume.getText().toString();
						d_volume = Double.parseDouble(text);
						d_volume = d_volume + 100;
						setDataText();
					}
				});

				// -------- key change
				et_invest_value.addTextChangedListener(new TextWatcher() {
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
						Log.v("tv_invest_value_plus", "11111111111");
						String text = et_invest_value.getText().toString();
						if (text.equals("0")) {
							d_invest = 0;
						} else {
							d_invest = Double.parseDouble(text);
						}
						// d_invest = d_invest + calGapUp(d_invest);
						setDataCalValue();
					}
				});

				et_volume.addTextChangedListener(new TextWatcher() {
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
						Log.v("tv_volume", "11111111111");
						String text = et_volume.getText().toString();
						if (text.equals("0")) {
							d_volume = 0;
						} else {
							d_volume = Double.parseDouble(text);
						}
						setDataCalValue();
					}
				});

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// ----- set data text
	// tv_last_trade.setText(String.format("%,.2f", dPrice1));
	public static void setDataText() {
		et_invest_value.setText(""
				+ FunctionFormatData.setFormatAnd2Digit("" + d_invest));
		et_volume.setText("" + d_volume);
	}

	// ----- set calculus value
	public static void setDataCalValue() {
		if (d_invest_begin == d_invest) {
			tv_save.setTextColor(context.getResources().getColor(
					R.color.c_title));
			// tv_save.setEnabled(false);
		} else if (d_invest_begin > d_invest) {
			tv_save.setTextColor(context.getResources().getColor(
					R.color.c_content));
			// tv_save.setEnabled(true);
		} else if (d_invest_begin < d_invest) {
			tv_save.setTextColor(context.getResources().getColor(
					R.color.c_content));
			// tv_save.setEnabled(true);
		}

		tv_save.setEnabled(true);
		d_value = d_invest * d_volume;
		tv_value_cal.setText(""
				+ FunctionFormatData.setFormatAnd2Digit("" + d_value));

		// et_volume.setText("" + d_volume);
		// et_invest_value.setText(""
		// + FunctionSymbol.setFormatAnd2Digit("" + d_invest));
		et_invest_value.setTextColor(context.getResources().getColor(
				FunctionSetBg.setColorCompare2Attribute("" + d_invest, ""
						+ d_invest_begin)));

		if (d_volume > 0) {
			tv_save.setEnabled(true);
			tv_save.setTextColor(context.getResources().getColor(
					R.color.c_content));
		} else {
			tv_save.setEnabled(false);
			tv_save.setTextColor(context.getResources().getColor(
					R.color.c_title));
		}

	}

	// ============== set row detail ===============
	public static ArrayList<View> arr_risk_v;

	public static void setRowDetail() {
		try {
			if (UiWatchlistDetail.contentGetDetail != null) {
				LinearLayout col_title_detail, col_title_detail_fund;
				col_title_detail = (LinearLayout) dialog
						.findViewById(R.id.col_title_detail);
				col_title_detail_fund = (LinearLayout) dialog
						.findViewById(R.id.col_title_detail_fund);
				col_title_detail.setVisibility(View.GONE);
				col_title_detail_fund.setVisibility(View.GONE);

				// ----------------------
				View viewSymbol = ((Activity) context).getLayoutInflater()
						.inflate(R.layout.row_watchlist_symbol, null);

				LayoutInflater inflater = (LayoutInflater) context
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				View viewDetail = inflater.inflate(
						R.layout.row_watchlist_detail, null);

				View viewDetailFund = inflater.inflate(
						R.layout.row_systemtrade_detail_mutualfund, null);

				LinearLayout list_symbol = (LinearLayout) dialog
						.findViewById(R.id.list_symbol);
				LinearLayout list_detail = (LinearLayout) dialog
						.findViewById(R.id.list_detail);
				list_symbol.removeAllViews();
				list_detail.removeAllViews();

				JSONObject jsoIndex = UiWatchlistDetail.contentGetDetail;
				if (FragmentChangeActivity.selectSearchPortStock) {
					col_title_detail.setVisibility(View.VISIBLE);

					// Log.v("data stock", "" + jsoIndex);

					// ------------ check package
					View v_sft = (View) dialog.findViewById(R.id.v_sft);
					LinearLayout li_sft = (LinearLayout) viewDetail
							.findViewById(R.id.li_sft);
					v_sft.setVisibility(View.GONE);
					li_sft.setVisibility(View.GONE);
					if (SplashScreen.contentGetUserById != null) {
						if (!(SplashScreen.contentGetUserById
								.getString("package").equals("free"))) {
							v_sft.setVisibility(View.VISIBLE);
							li_sft.setVisibility(View.VISIBLE);
						}
					}

					// ------------ set data
					// symbol
					final String symbol_name = jsoIndex
							.getString("symbol_name");
					String turnover_list_level = jsoIndex
							.getString("turnover_list_level");
					final String status = jsoIndex.getString("status");
					String status_xd = jsoIndex.getString("status_xd");

					TextView tv_symbol_name = (TextView) viewSymbol
							.findViewById(R.id.tv_symbol_name);
					tv_symbol_name.setText(Html.fromHtml(FunctionFormatData
							.checkStatusSymbol(symbol_name,
									turnover_list_level, status, status_xd)));

					((TextView) viewSymbol
							.findViewById(R.id.tv_symbol_fullname_eng))
							.setText(jsoIndex.getString("symbol_fullname_eng"));

					// detail

					// img chart
					ImageView img_chart = (ImageView) viewDetail
							.findViewById(R.id.img_chart);
					FragmentChangeActivity.imageLoader.displayImage(
							SplashScreen.url_bidschart_chart
									+ jsoIndex.getString("symbol_name")
									+ ".png", img_chart);

					// ck ltrade change
					String strLastTrade = jsoIndex.getString("last_trade");
					String strChange = jsoIndex.getString("change");
					String strPercentChange = jsoIndex
							.getString("percentChange");

					ImageView img_updown = (ImageView) viewDetail
							.findViewById(R.id.img_updown);
					TextView tv_last_trade = (TextView) viewDetail
							.findViewById(R.id.tv_last_trade);
					TextView tv_change = (TextView) viewDetail
							.findViewById(R.id.tv_change);
					TextView tv_percentChange = (TextView) viewDetail
							.findViewById(R.id.tv_percentChange);

					tv_last_trade.setText(strLastTrade);
					tv_change.setText(strChange);
					if ((strPercentChange == "0") || (strPercentChange == "")
							|| (strPercentChange == "0.00")) {
						tv_percentChange.setText("0.00");
					} else {
						tv_percentChange.setText(strPercentChange + "%");
					}

					// เซตสี change , lasttrade, percentchange เป็นสีตาม
					// change โดยเอา change เทียบกับ 0
					if (strChange != "") {
						if (!status.equals("SP")) {
							tv_change
									.setTextColor(context
											.getResources()
											.getColor(
													FunctionSetBg
															.setColor(strChange)));
							tv_last_trade
									.setTextColor(context
											.getResources()
											.getColor(
													FunctionSetBg
															.setColor(strChange)));
							tv_percentChange.setTextColor(context
									.getResources().getColor(
											FunctionSetBg.setColor(strChange)));
						}
					}

					// sft
					((LinearLayout) viewDetail.findViewById(R.id.li_sft))
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									DialogDifinitionSignal.show();
								}
							});

					// color sft
					String strTrends = jsoIndex
							.getString("trendSignal_avg_percentChange");
					String strFundam = jsoIndex.getString("fundamental");
					String strColorm = jsoIndex.getString("color_macd");

					TextView tv_trendSignal_avg_percent = (TextView) viewDetail
							.findViewById(R.id.tv_trendSignal_avg_percent);
					TextView tv_fundamental = (TextView) viewDetail
							.findViewById(R.id.tv_fundamental);
					TextView tv_color_macd = (TextView) viewDetail
							.findViewById(R.id.tv_color_macd);

					tv_trendSignal_avg_percent.setBackgroundColor(FunctionSetBg
							.setColorWatchListSymbolTrendSignal(strTrends));
					tv_fundamental.setBackgroundColor(FunctionSetBg
							.setColorWatchListSymbolFundamental(strFundam));
					tv_color_macd.setBackgroundColor(FunctionSetBg
							.setColorWatchListSymbolColorMacd(strColorm));

					// ck hight low
					String strPrevClose = jsoIndex.getString("prev_close")
							.replaceAll(",", "");
					String strHigh = jsoIndex.getString("high").replaceAll(",",
							"");
					String strLow = jsoIndex.getString("low").replaceAll(",",
							"");

					TextView tv_high = (TextView) viewDetail
							.findViewById(R.id.tv_high);
					TextView tv_low = (TextView) viewDetail
							.findViewById(R.id.tv_low);

					ImageView img_trade_signal = (ImageView) viewDetail
							.findViewById(R.id.img_trade_signal);

					tv_high.setText(FunctionSetBg.setStrDetailList(strHigh));
					tv_low.setText(FunctionSetBg.setStrDetailList(strLow));

					// ----- Trend signal
					String strTrade_signal = jsoIndex.getString("trade_signal");
					img_trade_signal.setBackgroundResource(FunctionSetBg
							.setImgTrendSignal(strTrade_signal));

					if (!status.equals("SP")) {
						if (strPrevClose != "") {
							if (strHigh != "") {
								if ((Float.parseFloat(strHigh.replaceAll(",",
										""))) > Float.parseFloat(strPrevClose
										.replaceAll(",", ""))) {
									tv_high.setTextColor(context
											.getResources()
											.getColor(FunctionSetBg.arrColor[2]));
								} else if ((Float.parseFloat(strHigh
										.replaceAll(",", ""))) < Float
										.parseFloat(strPrevClose.replaceAll(
												",", ""))) {
									tv_high.setTextColor(context
											.getResources()
											.getColor(FunctionSetBg.arrColor[0]));
								} else {
									tv_high.setTextColor(context
											.getResources()
											.getColor(FunctionSetBg.arrColor[1]));
								}
							}
							if (strLow != "") {
								if ((Float.parseFloat(strLow
										.replaceAll(",", ""))) > Float
										.parseFloat(strPrevClose.replaceAll(
												",", ""))) {
									tv_low.setTextColor(context
											.getResources()
											.getColor(FunctionSetBg.arrColor[2]));
								} else if ((Float.parseFloat(strLow.replaceAll(
										",", ""))) < Float
										.parseFloat(strPrevClose.replaceAll(
												",", ""))) {
									tv_low.setTextColor(context
											.getResources()
											.getColor(FunctionSetBg.arrColor[0]));
								} else {
									tv_low.setTextColor(context
											.getResources()
											.getColor(FunctionSetBg.arrColor[1]));
								}
							}
						}
					} else {
						tv_high.setTextColor(context.getResources().getColor(
								R.color.c_content));
						tv_low.setTextColor(context.getResources().getColor(
								R.color.c_content));
					}

					// ck pe pbv peg
					String strPe = jsoIndex.getString("p_e");
					String strPbv = jsoIndex.getString("p_bv");
					String strRoe = jsoIndex.getString("roe");
					String strRoa = jsoIndex.getString("roa");
					String strPeg = jsoIndex.getString("peg");

					TextView tv_p_e = (TextView) viewDetail
							.findViewById(R.id.tv_p_e);
					TextView tv_p_bv = (TextView) viewDetail
							.findViewById(R.id.tv_p_bv);
					TextView tv_roe = (TextView) viewDetail
							.findViewById(R.id.tv_roe);
					TextView tv_roa = (TextView) viewDetail
							.findViewById(R.id.tv_roa);
					TextView tv_peg = (TextView) viewDetail
							.findViewById(R.id.tv_peg);

					tv_p_e.setText(FunctionSetBg.setStrDetailList(strPe));
					tv_p_bv.setText(FunctionSetBg.setStrDetailList(strPbv));
					tv_roe.setText(FunctionSetBg.setStrDetailList(strRoe));
					tv_roa.setText(FunctionSetBg.setStrDetailList(strRoa));
					tv_peg.setText(FunctionSetBg.setStrDetailList(strPeg));

					// -- color write/blue
					if (!status.equals("SP")) {
						tv_roe.setTextColor(context.getResources().getColor(
								FunctionSetBg
										.setStrColorWriteDetailBlue(strRoe)));
						tv_roa.setTextColor(context.getResources().getColor(
								FunctionSetBg
										.setStrColorWriteDetailBlue(strRoa)));
					} else {
						tv_roe.setTextColor(context.getResources().getColor(
								R.color.c_content));
						tv_roa.setTextColor(context.getResources().getColor(
								R.color.c_content));
					}

					if (!status.equals("SP")) {
						if (SplashScreen.contentSymbol_Set != null) {
							String strPe_set = SplashScreen.contentSymbol_Set
									.getString("p_e");
							String strPbv_set = SplashScreen.contentSymbol_Set
									.getString("p_bv");
							String strPeg_set = SplashScreen.contentSymbol_Set
									.getString("peg");

							tv_p_e.setTextColor(context.getResources()
									.getColor(
											FunctionSetBg.setStrCheckSet(strPe,
													strPe_set)));

							tv_p_bv.setTextColor(context.getResources()
									.getColor(
											FunctionSetBg.setStrCheckSet(
													strPbv, strPbv_set)));

							tv_peg.setTextColor(context.getResources()
									.getColor(
											FunctionSetBg.setStrCheckSet(
													strPeg, strPeg_set)));
						}
					} else {
						tv_p_e.setTextColor(context.getResources().getColor(
								R.color.c_content));

						tv_p_bv.setTextColor(context.getResources().getColor(
								R.color.c_content));

						tv_peg.setTextColor(context.getResources().getColor(
								R.color.c_content));
					}

					// not set color
					TextView tv_volume = (TextView) viewDetail
							.findViewById(R.id.tv_volume);
					TextView tv_value = (TextView) viewDetail
							.findViewById(R.id.tv_value);
					TextView tv_ceiling = (TextView) viewDetail
							.findViewById(R.id.tv_ceiling);
					TextView tv_floor = (TextView) viewDetail
							.findViewById(R.id.tv_floor);
					String strVolume = jsoIndex.getString("volume");
					String strValue = jsoIndex.getString("value");
					String sptVolume[] = strVolume.split(" ");
					String sptValue[] = strValue.split(" ");

					if (sptVolume.length > 1) {
						tv_volume.setText(sptVolume[0] + "\n" + sptVolume[1]);
					} else {
						tv_volume.setText(strVolume);
					}
					if (sptValue.length > 1) {
						tv_value.setText(sptValue[0] + "\n" + sptValue[1]);
					} else {
						tv_value.setText(strValue);
					}

					tv_ceiling.setText(jsoIndex.getString("ceiling"));
					tv_floor.setText(jsoIndex.getString("floor"));
					if (status.equals("SP")) {
						tv_ceiling.setTextColor(context.getResources()
								.getColor(R.color.c_content));
						tv_floor.setTextColor(context.getResources().getColor(
								R.color.c_content));
					}

					list_symbol.addView(viewSymbol);
					list_detail.addView(viewDetail);

				} else {
					col_title_detail_fund.setVisibility(View.VISIBLE);

					Log.v("data fund", "" + jsoIndex);

					// symbol
					String asset_initial = jsoIndex.getString("asset_initial");
					final String name_initial = jsoIndex
							.getString("name_initial");
					final String name_t = jsoIndex.getString("name_t");

					TextView tv_name = (TextView) viewSymbol
							.findViewById(R.id.tv_symbol_name);
					TextView tv_name_t = (TextView) viewSymbol
							.findViewById(R.id.tv_symbol_fullname_eng);

					tv_name.setText(name_initial);
					tv_name_t.setText(name_t);

					// detail
					String dividend_policy = jsoIndex
							.getString("dividend_policy");
					String type_initial = jsoIndex.getString("type_initial");
					String invest_value = jsoIndex.getString("invest_value");
					String invest_change = jsoIndex.getString("invest_change");
					String fund_rating = jsoIndex.getString("fund_rating");
					String dividend_1mth = jsoIndex.getString("dividend_1mth");
					String dividend_3mth = jsoIndex.getString("dividend_3mth");
					String dividend_6mth = jsoIndex.getString("dividend_6mth");
					String dividend_1yr = jsoIndex.getString("dividend_1yr");
					String dividend_3yr = jsoIndex.getString("dividend_3yr");
					String dividend_5yr = jsoIndex.getString("dividend_5yr");
					String date_at = jsoIndex.getString("date_at");
					String fund_risk = jsoIndex.getString("fund_risk");

					// img chart
					ImageView img_chart = (ImageView) viewDetailFund
							.findViewById(R.id.img_chart);
					FragmentChangeActivity.imageLoader.displayImage(
							SplashScreen.url_bidschart_chart + name_initial
									+ ".png", img_chart);

					LinearLayout li_risk = (LinearLayout) viewDetailFund
							.findViewById(R.id.li_risk);

					TextView tv_value = (TextView) viewDetailFund
							.findViewById(R.id.tv_value);
					TextView tv_change = (TextView) viewDetailFund
							.findViewById(R.id.tv_change);
					TextView tv_policy = (TextView) viewDetailFund
							.findViewById(R.id.tv_policy);
					TextView tv_1mth = (TextView) viewDetailFund
							.findViewById(R.id.tv_1mth);
					TextView tv_3mth = (TextView) viewDetailFund
							.findViewById(R.id.tv_3mth);
					TextView tv_6mth = (TextView) viewDetailFund
							.findViewById(R.id.tv_6mth);
					TextView tv_1yr = (TextView) viewDetailFund
							.findViewById(R.id.tv_1yr);
					TextView tv_3yr = (TextView) viewDetailFund
							.findViewById(R.id.tv_3yr);
					TextView tv_5yr = (TextView) viewDetailFund
							.findViewById(R.id.tv_5yr);
					TextView tv_date = (TextView) viewDetailFund
							.findViewById(R.id.tv_date);

					View v_risk_1 = (View) viewDetailFund
							.findViewById(R.id.v_risk_1);
					View v_risk_2 = (View) viewDetailFund
							.findViewById(R.id.v_risk_2);
					View v_risk_3 = (View) viewDetailFund
							.findViewById(R.id.v_risk_3);
					View v_risk_4 = (View) viewDetailFund
							.findViewById(R.id.v_risk_4);
					View v_risk_5 = (View) viewDetailFund
							.findViewById(R.id.v_risk_5);
					View v_risk_6 = (View) viewDetailFund
							.findViewById(R.id.v_risk_6);
					View v_risk_7 = (View) viewDetailFund
							.findViewById(R.id.v_risk_7);
					View v_risk_8 = (View) viewDetailFund
							.findViewById(R.id.v_risk_8);

					arr_risk_v = new ArrayList<View>();
					arr_risk_v.clear();
					arr_risk_v.add(v_risk_1);
					arr_risk_v.add(v_risk_2);
					arr_risk_v.add(v_risk_3);
					arr_risk_v.add(v_risk_4);
					arr_risk_v.add(v_risk_5);
					arr_risk_v.add(v_risk_6);
					arr_risk_v.add(v_risk_7);
					arr_risk_v.add(v_risk_8);

					if ((!fund_risk.equals("")) && (!fund_risk.equals("-"))) {
						// li_risk.setBackgroundResource(R.drawable.bg_tab_risk_active);
						setBgFundRisk(fund_risk); // ------ set background
													// fund_risk
					} else {
						li_risk.setBackgroundResource(R.drawable.bg_tab_risk);
					}

					RatingBar rb_rating = (RatingBar) viewDetailFund
							.findViewById(R.id.rb_rating);

					tv_value.setText(invest_value);

					if ((!invest_change.equals(""))
							&& (!invest_change.equals("null"))
							&& (!invest_change.equals("-"))) {
						tv_change.setText(invest_change + "%");
						tv_change
								.setTextColor(context
										.getResources()
										.getColor(
												FunctionSetBg
														.setColorCompareWithZero(invest_change)));
						tv_value.setTextColor(context
								.getResources()
								.getColor(
										FunctionSetBg
												.setColorCompareWithZero(invest_change)));
					}

					tv_policy.setText(dividend_policy);
					tv_1mth.setText(dividend_1mth);
					tv_3mth.setText(dividend_3mth);
					tv_6mth.setText(dividend_6mth);
					tv_1yr.setText(dividend_1yr);
					tv_3yr.setText(dividend_3yr);
					tv_5yr.setText(dividend_5yr);
					tv_date.setText(DateTimeCreate
							.DateCreateMutualFunTh(date_at));

					tv_1mth.setTextColor(context.getResources().getColor(
							FunctionSetBg
									.setColorCompareWithZero(dividend_1mth)));
					tv_3mth.setTextColor(context.getResources().getColor(
							FunctionSetBg
									.setColorCompareWithZero(dividend_3mth)));
					tv_6mth.setTextColor(context.getResources().getColor(
							FunctionSetBg
									.setColorCompareWithZero(dividend_6mth)));
					tv_1yr.setTextColor(context
							.getResources()
							.getColor(
									FunctionSetBg
											.setColorCompareWithZero(dividend_1yr)));
					tv_3yr.setTextColor(context
							.getResources()
							.getColor(
									FunctionSetBg
											.setColorCompareWithZero(dividend_3yr)));
					tv_5yr.setTextColor(context
							.getResources()
							.getColor(
									FunctionSetBg
											.setColorCompareWithZero(dividend_5yr)));

					if ((!fund_rating.equals("") && (!fund_rating
							.equals("null"))) && (!fund_rating.equals("N/A"))) {
						rb_rating.setRating(Float.parseFloat(fund_rating
								.replaceAll(",", "")));
					}

					list_symbol.addView(viewSymbol);
					list_detail.addView(viewDetailFund);

				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// ------ set background fund_risk
	public static String setBgFundRisk(String str) {
		int intFrisk = Integer.parseInt(str);
		for (int i = 0; i < arr_risk_v.size(); i++) {
			arr_risk_v.get(i).setBackgroundColor(Color.TRANSPARENT);
			if (intFrisk <= i) {
				if (i == 0) {
					arr_risk_v.get(i).setBackgroundResource(
							R.drawable.bg_tab_risk_left);
				} else if (i == (arr_risk_v.size() - 1)) {
					arr_risk_v.get(i).setBackgroundResource(
							R.drawable.bg_tab_risk_right);
				} else {
					arr_risk_v.get(i)
							.setBackgroundColor(
									context.getResources().getColor(
											R.color.bg_default));
				}
			}
		}
		return str;
	}

	// ============== add portfolio ===============
	public static String resultAddPortFolio = "";

	public static void sendAddPortFolio() {

		setPortFolio resp = new setPortFolio();
		resp.execute();
	}

	public static class setPortFolio extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		String temp = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialogLoading.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			String url = SplashScreen.url_bidschart
					+ "/service/v2/addPortfolio";

			String json = "";
			InputStream inputStream = null;

			Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			String formattedDate = df.format(c.getTime());

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				String symbol_type = "";
				if (FragmentChangeActivity.selectSearchPortStock) {
					symbol_type = "equity";
				} else {
					symbol_type = "fund";
				}

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();

				jsonObject.accumulate("user_id", SplashScreen.userModel.user_id);
				jsonObject.accumulate("symbol", FragmentChangeActivity.strSymbolSelect);
				jsonObject.accumulate("symbol_type", symbol_type);
				jsonObject.accumulate("price", d_invest);
				jsonObject.accumulate("volume", d_volume);
				jsonObject.accumulate("value", d_value);
				jsonObject.accumulate("date", formattedDate);

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
					resultAddPortFolio = AFunctionOther
							.convertInputStreamToString(inputStream);
				else
					resultAddPortFolio = "Did not work!";

				Log.v("result resultAddPortFolio : ", "" + resultAddPortFolio);

				// {"status":"error","message":"Cannot access property on non-object","result":false}

				// {"status":"ok","message":"Get data Success.","result":{"user_id":"1587","symbol":"PTTEP","symbol_type":"equity","price":90.75,"volume":100,"value":9075,"date":"2016-12-06","portfolio_id":3872}}

				
				// resultAddPortFolio :(28911):
				// {"status":"ok","message":"Get data Success.","result":{"user_id":"104","symbol":"KFLRMF","symbol_type":"equity","price":70.7406,"volume":600,"value":42444.36,"date":"2016-06-28","portfolio_id":245}}
				// {"status":"ok","message":"Get data Success.","result":{"user_id":"104","symbol":"TMB50","symbol_type":"equity","price":86.517,"volume":500,"value":43258.5,"date":"2016-07-15","portfolio_id":572}}

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
					JSONObject jsoAddSetAlert = new JSONObject(
							resultAddPortFolio);

					if (!jsoAddSetAlert.getString("status").equals("ok")) {
						Toast.makeText(context, "Error.", 0).show();

						dialogLoading.dismiss();
						dissmiss();
					} else {
						// loadDataDetail();
						Toast.makeText(context, "Success.", 0).show();

						FragmentChangeActivity.ckLoadSmartPortfolioList = false;
						dialogLoading.dismiss();
						dissmiss();

//						switchFragmentPort(new PagerSmartPortfolio());
					}
				} catch (JSONException e1) {
					e1.printStackTrace();
					dialogLoading.dismiss();
				}
			} else {
				dialogLoading.dismiss();
			}
		}
	}

	// ============ calculas price ใช้ do while }
	// เอา return มาบวก price แล้วมาใส่เอาเรย์ข้างหน้า ไม่เกินค่า celling
	public static double calGapUp(double price) {
		double gap;
		if (price < 2) {
			gap = 0.01;
		} else if (price >= 2 && price < 5) {
			gap = 0.02;
		} else if (price >= 5 && price < 10) {
			gap = 0.05;
		} else if (price >= 10 && price < 25) {
			gap = 0.10;
		} else if (price >= 25 && price < 100) {
			gap = 0.25;
		} else if (price >= 100 && price < 200) {
			gap = 0.50;
		} else if (price >= 200 && price < 400) {
			gap = 1.00;
		} else {
			gap = 2.00;
		}
		return gap;
	}

	// เอา price มาลบ return แล้วมาใส่เอาเรย์ข้างหน้า ไม่น้อยกว่าค่า foor
	public static double calGapDown(double price) {
		double gap;
		if (price < 2.02) {
			gap = 0.01;
		} else if (price >= 2 && price < 5.05) {
			gap = 0.02;
		} else if (price >= 5 && price < 10.10) {
			gap = 0.05;
		} else if (price >= 10 && price < 25.25) {
			gap = 0.10;
		} else if (price >= 25 && price < 100.50) {
			gap = 0.25;
		} else if (price >= 100 && price < 201) {
			gap = 0.50;
		} else if (price >= 200 && price < 402) {
			gap = 1.00;
		} else {
			gap = 2.00;
		}
		return gap;
	}

	protected static void switchFragment(PagerWatchList fragment) {
		if (context == null)
			return;
		if (context instanceof FragmentChangeActivity) {
			FragmentChangeActivity fca = (FragmentChangeActivity) context;
			fca.switchContent(fragment);
		}
	}

	protected static void switchFragmentPort(PagerSmartPortfolio fragment) {
		if (context == null)
			return;
		if (context instanceof FragmentChangeActivity) {
			FragmentChangeActivity fca = (FragmentChangeActivity) context;
			fca.switchContent(fragment);
		}
	}

}