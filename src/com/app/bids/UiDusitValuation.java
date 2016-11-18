package com.app.bids;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Inet4Address;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Vector;

import javax.security.auth.PrivateCredentialPermission;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;
import com.app.bids.FragmentChangeActivity.myWebClient;
import com.app.bids.PagerWatchListDetailNews.loadAll;
import com.app.bids.colorpicker.ColorPickerAdapter;
import com.app.bids.colorpicker.ColorPickerDialog;
import com.app.model.login.LoginDialog;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.R.integer;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData.Item;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class UiDusitValuation extends FragmentActivity {

	public static Activity act;

	public static Dialog dialogLoading;

	// ------ url
	public static String url_ValuationPE_PBV;
	public static String url_ValuationCondition;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.ui_dusit_valuation);

		// set view
		initView();
	}

	// ============== init view ===============
	public static JSONObject jsonGetValuationPE_PBV = null;
	public static JSONObject jsonGetValuationCondition = null;

	public static JSONObject jsonGetValuationPE_PBV_default = null;
	public static JSONObject jsonGetValuationCondition_default = null;

	private void initView() {
		jsonGetValuationPE_PBV_default = null;
		jsonGetValuationCondition_default = null;

		dialogLoading = new Dialog(UiDusitValuation.this);
		dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogLoading.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialogLoading.setContentView(R.layout.progress_bar);
		dialogLoading.setCancelable(false);
		dialogLoading.setCanceledOnTouchOutside(false);

		url_ValuationPE_PBV = SplashScreen.url_bidschart
				+ "/service/v2/getValuationPE_PBV?user_id="
				+ SplashScreen.userModel.user_id + "&symbol="
				+ FragmentChangeActivity.strSymbolSelect;
		url_ValuationCondition = SplashScreen.url_bidschart
				+ "/service/v2/getValuationCondition?symbol="
				+ FragmentChangeActivity.strSymbolSelect + "&user_id="
				+ SplashScreen.userModel.user_id;

		loadDataDetail();
		// setFollowSymbol();

		((LinearLayout) findViewById(R.id.li_back))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});

	}

	// ============== Load Data all =============
	public void loadDataDetail() {
		loadData resp = new loadData();
		resp.execute();
	}

	public class loadData extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;

		// ======= json ========
		// private JSONObject jsonGetDetailFollow;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progress.show();

			dialogLoading.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// ======= url ========

			// String url_ValuationPE_PBV = SplashScreen.url_bidschart
			// + "/service/v2/getValuationPE_PBV?user_id="
			// + SplashScreen.userModel.user_id + "&symbol="
			// + FragmentChangeActivity.strSymbolSelect + "&timestamp="
			// + timestamp;
			// String url_ValuationCondition = SplashScreen.url_bidschart
			// + "/service/v2/getValuationCondition?symbol="
			// + FragmentChangeActivity.strSymbolSelect + "&user_id="
			// + SplashScreen.userModel.user_id + "&timestamp="
			// + timestamp;

			// http://bidschart.com/service/v2/getValuationPE_PBV?user_id=1584&symbol=TNDT
			// http://bidschart.com/service/v2/getValuationCondition?symbol=TNDT&user_id=1584

			try {
				// ======= Ui Home ========
				jsonGetValuationPE_PBV = ReadJson
						.readJsonObjectFromUrl(url_ValuationPE_PBV);
				jsonGetValuationCondition = ReadJson
						.readJsonObjectFromUrl(url_ValuationCondition);
			} catch (IOException e1) {
				connectionError = true;
				jsonGetValuationPE_PBV = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonGetValuationPE_PBV = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonGetValuationPE_PBV = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (connectionError == false) {
				if ((jsonGetValuationPE_PBV != null)
						&& (jsonGetValuationCondition != null)) {
					setDataDetail();
					dialogLoading.dismiss();
				} else {
					Toast.makeText(getApplicationContext(), "Data Null", 0)
							.show();
					dialogLoading.dismiss();
				}
			} else {
				dialogLoading.dismiss();
			}
		}
	}


	// ============== Load Data pe bv setting =============
	public void loadDataPeBvSetting() {
		loadDataPeBvSetting resp = new loadDataPeBvSetting();
		resp.execute();
	}

	public class loadDataPeBvSetting extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progress.show();

			dialogLoading.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// ======= url ========
			try {
				// ======= Ui Home ========
				jsonGetValuationPE_PBV = ReadJson
						.readJsonObjectFromUrl(url_ValuationPE_PBV);
			} catch (IOException e1) {
				connectionError = true;
				jsonGetValuationPE_PBV = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonGetValuationPE_PBV = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonGetValuationPE_PBV = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (connectionError == false) {
				if (jsonGetValuationPE_PBV != null) {
					setDataPeBvSetting();
					dialogLoading.dismiss();
				} else {
					Toast.makeText(getApplicationContext(), "Data Null", 0)
							.show();
					dialogLoading.dismiss();
				}
			} else {
				dialogLoading.dismiss();
			}
		}
	}
	
	// ======= set data
	public static LinearLayout li_p_ebv_edit, li_ddm_edit;
	public static ImageView img_setting_p_ebv, img_setting_ddm;
	public static boolean ckEditShow_p_ebv = false;
	public static boolean ckEditShow_ddm = false;

	public void setDataDetail() {
		if (jsonGetValuationPE_PBV != null) {
			if (jsonGetValuationPE_PBV_default == null) {
				jsonGetValuationPE_PBV_default = jsonGetValuationPE_PBV;
			}
			setDataPE_PBV(); // set data pe pbv
		}
		if (jsonGetValuationCondition != null) {
			if (jsonGetValuationCondition_default == null) {
				jsonGetValuationCondition_default = jsonGetValuationCondition;
			}
			setDataCondition(); // set data condition
		}

		li_p_ebv_edit = (LinearLayout) findViewById(R.id.li_p_ebv_edit);
		li_ddm_edit = (LinearLayout) findViewById(R.id.li_ddm_edit);
		img_setting_p_ebv = (ImageView) findViewById(R.id.img_setting_p_ebv);
		img_setting_ddm = (ImageView) findViewById(R.id.img_setting_ddm);

		ckEditShow_p_ebv = false;
		ckEditShow_ddm = false;
		li_p_ebv_edit.setVisibility(View.GONE);
		li_ddm_edit.setVisibility(View.GONE);
		img_setting_p_ebv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ckEditShow_p_ebv) {
					ckEditShow_p_ebv = false;
					li_p_ebv_edit.setVisibility(View.GONE);
				} else {
					ckEditShow_p_ebv = true;
					li_p_ebv_edit.setVisibility(View.VISIBLE);
					ckEditShow_ddm = false;
					li_ddm_edit.setVisibility(View.GONE);
				}
			}
		});
		img_setting_ddm.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ckEditShow_ddm) {
					ckEditShow_ddm = false;
					li_ddm_edit.setVisibility(View.GONE);
				} else {
					ckEditShow_ddm = true;
					li_ddm_edit.setVisibility(View.VISIBLE);
					ckEditShow_p_ebv = false;
					li_p_ebv_edit.setVisibility(View.GONE);
				}
			}
		});
		try {
			// --------- data symbol --------
			if (FragmentChangeActivity.contentGetSystemTradeDusitRatioSelect != null) {
				TextView tv_symbol = (TextView) findViewById(R.id.tv_symbol);
				TextView tv_symbol_status = (TextView) findViewById(R.id.tv_symbol_status);
				TextView tv_last_trade = (TextView) findViewById(R.id.tv_last_trade);
				TextView tv_symbol_name_eng = (TextView) findViewById(R.id.tv_symbol_name_eng);
				TextView tv_percenchange = (TextView) findViewById(R.id.tv_percenchange);
				LinearLayout li_data_symbol = (LinearLayout) findViewById(R.id.li_data_symbol);
				li_data_symbol.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						startActivity(new Intent(getApplicationContext(),
								UiWatchlistDetail.class));
					}
				});

				String strSymbol_name = FragmentChangeActivity.contentGetSystemTradeDusitRatioSelect
						.getString("symbol");
				String strLast_trade = FragmentChangeActivity.contentGetSystemTradeDusitRatioSelect
						.getString("last_trade");
				String strSymbol_fullname_eng = FragmentChangeActivity.contentGetSystemTradeDusitRatioSelect
						.getString("symbol_fullname_eng");

				tv_symbol.setText(strSymbol_name);
				tv_last_trade.setText(FunctionSymbol
						.setFormatNumber(strLast_trade));
				tv_symbol_name_eng.setText(strSymbol_fullname_eng);

				// ck ltrade change
				String strLastTrade = FragmentChangeActivity.contentGetSystemTradeDusitRatioSelect
						.getString("last_trade");
				String strChange = FragmentChangeActivity.contentGetSystemTradeDusitRatioSelect
						.getString("change");
				String strPercentChange = FragmentChangeActivity.contentGetSystemTradeDusitRatioSelect
						.getString("percentChange");

				tv_last_trade.setText(strLastTrade);
				if ((strPercentChange == "0") || (strPercentChange == "")
						|| (strPercentChange == "0.00")) {
					tv_percenchange.setText("0.00");
				} else {
					tv_percenchange.setText(strPercentChange + "%");
					tv_percenchange.setText(FunctionSymbol
							.setFormatNumber(strChange)
							+ " ("
							+ FunctionSymbol.setFormatNumber(strPercentChange)
							+ "%)");
				}

				// เซตสี change , lasttrade, percentchange เป็นสีตาม
				// change โดยเอา change เทียบกับ 0
				if (strChange != "") {
					tv_last_trade.setTextColor(getResources().getColor(
							FunctionSetBg.setColor(strChange)));
					tv_percenchange.setTextColor(getResources().getColor(
							FunctionSetBg.setColor(strChange)));
				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ======= set data PE_PBV
	public static TextView tv_p_ebv_eps_minus, tv_p_ebv_pe_minus,
			tv_p_ebv_bvps_minus, tv_p_ebv_pbv_minus, tv_p_ebv_mos_minus;
	public static EditText et_p_ebv_eps, et_p_ebv_pe, et_p_ebv_bvps,
			et_p_ebv_pbv, et_p_ebv_mos;
	public static TextView tv_p_ebv_eps_plus, tv_p_ebv_pe_plus,
			tv_p_ebv_bvps_plus, tv_p_ebv_pbv_plus, tv_p_ebv_mos_plus;
	public static TextView tv_p_ebv_default, tv_p_ebv_apply;

	public void setDataPE_PBV() {
		setDataPeBvSetting(); // set data pebv setting
		
		try {
			// --------- data PE_PBV
			JSONObject jsoPE_PBV_data = null;
			JSONObject jsoPE_PBV_set = null;
			JSONObject jsoPE_PBV_default = null;
			jsoPE_PBV_data = jsonGetValuationPE_PBV.getJSONObject("data");
			jsoPE_PBV_set = jsonGetValuationPE_PBV.getJSONObject("set");
			jsoPE_PBV_default = jsonGetValuationPE_PBV.getJSONObject("default");

			if (jsoPE_PBV_data != null) {				
				// ------- list pe pbv
				JSONArray jsaPE_PBV = jsoPE_PBV_data.getJSONArray("pe_pbv");
				if (jsaPE_PBV != null) {
					LinearLayout list_pebv_data = (LinearLayout) findViewById(R.id.list_pebv_data);
					list_pebv_data.removeAllViews();

					for (int i = 0; i < jsaPE_PBV.length(); i++) {
						JSONObject jsoIndex = jsaPE_PBV.getJSONObject(i);
						View viewData = getLayoutInflater().inflate(
								R.layout.row_dusit_valuation_pebv_data, null);
						TextView tv_year_ = (TextView) viewData
								.findViewById(R.id.tv_year_);
						TextView tv_eps_ = (TextView) viewData
								.findViewById(R.id.tv_eps_);
						TextView tv_bvps_ = (TextView) viewData
								.findViewById(R.id.tv_bvps_);
						TextView tv_pe_ = (TextView) viewData
								.findViewById(R.id.tv_pe_);
						TextView tv_pbv_ = (TextView) viewData
								.findViewById(R.id.tv_pbv_);

						tv_year_.setText(jsoIndex.getString("year"));
						tv_eps_.setText(FunctionSymbol
								.setFormatAnd2Digit(jsoIndex.getString("eps")));
						tv_bvps_.setText(FunctionSymbol
								.setFormatAnd2Digit(jsoIndex.getString("bv")));
						tv_pe_.setText(FunctionSymbol
								.setFormatAnd2Digit(jsoIndex.getString("p_e")));
						tv_pbv_.setText(FunctionSymbol
								.setFormatAnd2Digit(jsoIndex.getString("p_bv")));

						list_pebv_data.addView(viewData);
					}
				}

			}

			// ------------- edit pe pbv
			if (jsoPE_PBV_set != null) {
				tv_p_ebv_eps_minus = (TextView) findViewById(R.id.tv_p_ebv_eps_minus);
				tv_p_ebv_pe_minus = (TextView) findViewById(R.id.tv_p_ebv_pe_minus);
				tv_p_ebv_bvps_minus = (TextView) findViewById(R.id.tv_p_ebv_bvps_minus);
				tv_p_ebv_pbv_minus = (TextView) findViewById(R.id.tv_p_ebv_pbv_minus);
				tv_p_ebv_mos_minus = (TextView) findViewById(R.id.tv_p_ebv_mos_minus);

				et_p_ebv_eps = (EditText) findViewById(R.id.et_p_ebv_eps);
				et_p_ebv_pe = (EditText) findViewById(R.id.et_p_ebv_pe);
				et_p_ebv_bvps = (EditText) findViewById(R.id.et_p_ebv_bvps);
				et_p_ebv_pbv = (EditText) findViewById(R.id.et_p_ebv_pbv);
				et_p_ebv_mos = (EditText) findViewById(R.id.et_p_ebv_mos);
				editEditTextPeBv(); // Edit EditText

				tv_p_ebv_eps_plus = (TextView) findViewById(R.id.tv_p_ebv_eps_plus);
				tv_p_ebv_pe_plus = (TextView) findViewById(R.id.tv_p_ebv_pe_plus);
				tv_p_ebv_bvps_plus = (TextView) findViewById(R.id.tv_p_ebv_bvps_plus);
				tv_p_ebv_pbv_plus = (TextView) findViewById(R.id.tv_p_ebv_pbv_plus);
				tv_p_ebv_mos_plus = (TextView) findViewById(R.id.tv_p_ebv_mos_plus);

				tv_p_ebv_default = (TextView) findViewById(R.id.tv_p_ebv_default);
				tv_p_ebv_apply = (TextView) findViewById(R.id.tv_p_ebv_apply);

				tv_p_ebv_eps_minus
						.setOnClickListener(onClickListenerPlusMinusPeBv);
				tv_p_ebv_pe_minus
						.setOnClickListener(onClickListenerPlusMinusPeBv);
				tv_p_ebv_bvps_minus
						.setOnClickListener(onClickListenerPlusMinusPeBv);
				tv_p_ebv_pbv_minus
						.setOnClickListener(onClickListenerPlusMinusPeBv);
				tv_p_ebv_mos_minus
						.setOnClickListener(onClickListenerPlusMinusPeBv);

				tv_p_ebv_eps_plus
						.setOnClickListener(onClickListenerPlusMinusPeBv);
				tv_p_ebv_pe_plus
						.setOnClickListener(onClickListenerPlusMinusPeBv);
				tv_p_ebv_bvps_plus
						.setOnClickListener(onClickListenerPlusMinusPeBv);
				tv_p_ebv_pbv_plus
						.setOnClickListener(onClickListenerPlusMinusPeBv);
				tv_p_ebv_mos_plus
						.setOnClickListener(onClickListenerPlusMinusPeBv);

				et_p_ebv_eps.setText(FunctionSymbol
						.setFormatAnd2Digit(jsoPE_PBV_set
								.getString("eps_next_year")));
				et_p_ebv_pe.setText(FunctionSymbol
						.setFormatAnd2Digit(jsoPE_PBV_set.getString("pe")));
				et_p_ebv_bvps.setText(FunctionSymbol
						.setFormatAnd2Digit(jsoPE_PBV_set
								.getString("pbv_next_year")));
				et_p_ebv_pbv.setText(FunctionSymbol
						.setFormatAnd2Digit(jsoPE_PBV_set.getString("pbv")));
				et_p_ebv_mos.setText(FunctionSymbol
						.setFormatAnd2Digit(jsoPE_PBV_set.getString("mos")));

				tv_p_ebv_default.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (jsonGetValuationPE_PBV_default != null) {
							jsonGetValuationPE_PBV = jsonGetValuationPE_PBV_default;
							setDataPE_PBV();
						}
					}
				});
				tv_p_ebv_apply.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						// http://realtime.bidschart.com/service/v2/setValuationPE_PBV?
						// symbol=INTUCH&user_id=1040&pbv=9.77&eps_next_year=4.97&mos=20.00
						// &pbv_next_year=6.77&pe=13.66&timestamp=1475482820

						java.util.Date date = new java.util.Date();
						long timestamp = date.getTime();
						url_ValuationPE_PBV = SplashScreen.url_bidschart
								+ "/service/v2/setValuationPE_PBV?symbol="
								+ FragmentChangeActivity.strSymbolSelect
								+ "&user_id=" + SplashScreen.userModel.user_id
								+ "&pbv=" + et_p_ebv_pbv.getText().toString()
								+ "&eps_next_year="
								+ et_p_ebv_eps.getText().toString() + "&mos="
								+ et_p_ebv_mos.getText().toString()
								+ "&pbv_next_year="
								+ et_p_ebv_bvps.getText().toString() + "&pe="
								+ et_p_ebv_pe.getText().toString()
								+ "&timestamp=" + timestamp;
						Log.v("url_ValuationPE_PBV", ""+url_ValuationPE_PBV);
						
						loadDataPeBvSetting();

					}
				});
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	// set data be bv setting
	public void setDataPeBvSetting() {
		try {
			// --------- data PE_PBV
			JSONObject jsoPE_PBV_data = null;
			jsoPE_PBV_data = jsonGetValuationPE_PBV.getJSONObject("data");
			Log.v("setDataPeBvSetting",""+jsoPE_PBV_data);

			// {"valuation_current_pe":23.836,"valuation_pe":16.92356,
			//"valuation_current_pbv":34.2886,"valuation_pbv":24.344906,
			//"pe_pbv":[{"year":"2011","eps":"4.32","bv":"66.62","p_e":"11.96","p_bv":"1.32"},
			//{"year":"2012","eps":"12.51","bv":"79.83","p_e":"10.85","p_bv":"1.37"},
			//{"year":"2013","eps":"28.90","bv":"88.04","p_e":"5.70","p_bv":"1.59"},
			//{"year":"2014","eps":"1.19","bv":"9.53","p_e":"16.28","p_bv":"1.53"},
			//{"year":"2015","eps":"1.36","bv":"9.59","p_e":"14.20","p_bv":"1.48"}]}

			
			if (jsoPE_PBV_data != null) {
				TextView tv_current_pe = (TextView) findViewById(R.id.tv_current_pe);			
				TextView tv_pe = (TextView) findViewById(R.id.tv_pe);				
				TextView tv_current_pbv = (TextView) findViewById(R.id.tv_current_pbv);				
				TextView tv_pbv = (TextView) findViewById(R.id.tv_pbv);				
				tv_current_pe.setText(FunctionSymbol
						.setFormatAnd2Digit(jsoPE_PBV_data
								.getString("valuation_current_pe")));
				tv_pe.setText(FunctionSymbol.setFormatAnd2Digit(jsoPE_PBV_data
						.getString("valuation_pe")));
				tv_current_pbv.setText(FunctionSymbol
						.setFormatAnd2Digit(jsoPE_PBV_data
								.getString("valuation_current_pbv")));
				tv_pbv.setText(FunctionSymbol.setFormatAnd2Digit(jsoPE_PBV_data
						.getString("valuation_pbv")));
				
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ============= Edit Pe Bv EditText ===========
	private void editEditTextPeBv() {
		et_p_ebv_eps.addTextChangedListener(new TextWatcher() {
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
				String text = et_p_ebv_eps.getText().toString();

			}
		});
		et_p_ebv_pe.addTextChangedListener(new TextWatcher() {
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
				String text = et_p_ebv_pe.getText().toString();

			}
		});
		et_p_ebv_bvps.addTextChangedListener(new TextWatcher() {
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
				String text = et_p_ebv_bvps.getText().toString();

			}
		});
		et_p_ebv_pbv.addTextChangedListener(new TextWatcher() {
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
				String text = et_p_ebv_pbv.getText().toString();

			}
		});
		et_p_ebv_mos.addTextChangedListener(new TextWatcher() {
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
				String text = et_p_ebv_mos.getText().toString();

			}
		});
	}

	// ============= Edit PeBv Plus Minus ===========
	public static OnClickListener onClickListenerPlusMinusPeBv = new OnClickListener() {
		@Override
		public void onClick(final View v) {
			// Toast.makeText(context, "" + v.getId(), 0).show();
			String text;
			double dText;
			switch (v.getId()) {
			case R.id.tv_p_ebv_eps_minus:
				text = et_p_ebv_eps.getText().toString();
				if( (text=="N/A") && (text=="")){
					text = "0";
				}
				dText = Double.parseDouble(text);
				et_p_ebv_eps.setText(FunctionSymbol.setFormat2Digit("" + (dText - 1)));
				break;
			case R.id.tv_p_ebv_pe_minus:
				text = et_p_ebv_pe.getText().toString();
				if( (text=="N/A") && (text=="")){
					text = "0";
				}
				dText = Double.parseDouble(text);
				et_p_ebv_pe.setText(FunctionSymbol.setFormat2Digit("" + (dText - 1)));
				break;
			case R.id.tv_p_ebv_bvps_minus:
				text = et_p_ebv_bvps.getText().toString();
				if( (text=="N/A") && (text=="")){
					text = "0";
				}
				dText = Double.parseDouble(text);
				et_p_ebv_bvps.setText(FunctionSymbol.setFormat2Digit("" + (dText - 1)));
				break;
			case R.id.tv_p_ebv_pbv_minus:
				text = et_p_ebv_pbv.getText().toString();
				if( (text=="N/A") && (text=="")){
					text = "0";
				}
				dText = Double.parseDouble(text);
				et_p_ebv_pbv.setText(FunctionSymbol.setFormat2Digit("" + (dText - 1)));
				break;
			case R.id.tv_p_ebv_mos_minus:
				text = et_p_ebv_mos.getText().toString();
				if( (text=="N/A") && (text=="")){
					text = "0";
				}
				dText = Double.parseDouble(text);
				et_p_ebv_mos.setText(FunctionSymbol.setFormat2Digit("" + (dText - 1)));
				break;
			case R.id.tv_p_ebv_eps_plus:
				text = et_p_ebv_eps.getText().toString();
				if( (text=="N/A") && (text=="")){
					text = "0";
				}
				dText = Double.parseDouble(text);
				et_p_ebv_eps.setText(FunctionSymbol.setFormat2Digit("" + (dText + 1)));
				break;
			case R.id.tv_p_ebv_pe_plus:
				text = et_p_ebv_pe.getText().toString();
				if( (text=="N/A") && (text=="")){
					text = "0";
				}
				dText = Double.parseDouble(text);
				et_p_ebv_pe.setText(FunctionSymbol.setFormat2Digit("" + (dText + 1)));
				break;
			case R.id.tv_p_ebv_bvps_plus:
				text = et_p_ebv_bvps.getText().toString();
				if( (text=="N/A") && (text=="")){
					text = "0";
				}
				dText = Double.parseDouble(text);
				et_p_ebv_bvps.setText(FunctionSymbol.setFormat2Digit("" + (dText + 1)));
				break;
			case R.id.tv_p_ebv_pbv_plus:
				text = et_p_ebv_pbv.getText().toString();
				if( (text=="N/A") && (text=="")){
					text = "0";
				}
				dText = Double.parseDouble(text);
				et_p_ebv_pbv.setText(FunctionSymbol.setFormat2Digit("" + (dText + 1)));
				break;
			case R.id.tv_p_ebv_mos_plus:
				text = et_p_ebv_mos.getText().toString();
				if( (text=="N/A") && (text=="")){
					text = "0";
				}
				dText = Double.parseDouble(text);
				et_p_ebv_mos.setText(FunctionSymbol.setFormat2Digit("" + (dText + 1)));
				break;
			default:
				break;
			}
		}
	};

	// ======= set data Condition
	public static TextView tv_ddm_dpr_minus, tv_ddm_bvps_minus,
			tv_ddm_roe_minus, tv_ddm_pe_minus, tv_ddm_krf_minus,
			tv_ddm_krm_minus, tv_ddm_b_minus, tv_ddm_mos_minus;
	public static EditText et_ddm_dpr_eps, et_ddm_bvps_eps, et_ddm_roe_eps,
			et_ddm_pe_eps, et_ddm_krf_eps, et_ddm_krm_eps, et_ddm_b_eps,
			et_ddm_mos_eps;
	public static TextView tv_ddm_dpr_plus, tv_ddm_bvps_plus, tv_ddm_roe_plus,
			tv_ddm_pe_plus, tv_ddm_krf_plus, tv_ddm_krm_plus, tv_ddm_b_plus,
			tv_ddm_mos_plus;
	public static TextView tv_ddm_default, tv_ddm_apply, tv_ddm_ke_eps;

	public void setDataCondition() {
		try {
			// --------- data condition
			JSONObject jsoCondition_data = null;
			JSONObject jsoCondition_set = null;
			JSONObject jsoCondition_default = null;
			jsoCondition_data = jsonGetValuationCondition.getJSONObject("data");
			jsoCondition_set = jsonGetValuationCondition.getJSONObject("set");
			jsoCondition_default = jsonGetValuationCondition
					.getJSONObject("default");

			setDataConditionDdm(jsoCondition_data); // set data Condition
													// DDM
			if (jsoCondition_data != null) {
				TextView tv_valuation = (TextView) findViewById(R.id.tv_valuation);
				TextView tv_valuation_mos = (TextView) findViewById(R.id.tv_valuation_mos);
				tv_valuation.setText(FunctionSymbol
						.setFormatAnd2Digit(jsoCondition_data
								.getString("valuation")));
				tv_valuation_mos.setText(FunctionSymbol
						.setFormatAnd2Digit(jsoCondition_data
								.getString("valuation_mos")));

				TextView tv_v5 = (TextView) findViewById(R.id.tv_v5);
				TextView tv_valuation_current = (TextView) findViewById(R.id.tv_valuation_current);
				tv_v5.setText(FunctionSymbol
						.setFormatAnd2Digit(jsoCondition_data.getString("v5")));
				tv_valuation_current.setText(FunctionSymbol
						.setFormatAnd2Digit(jsoCondition_data
								.getString("valuation_current")));
			}

			// ------------- edit condition
			if (jsoCondition_set != null) {
				tv_ddm_dpr_minus = (TextView) findViewById(R.id.tv_ddm_dpr_minus);
				tv_ddm_bvps_minus = (TextView) findViewById(R.id.tv_ddm_bvps_minus);
				tv_ddm_roe_minus = (TextView) findViewById(R.id.tv_ddm_roe_minus);
				tv_ddm_pe_minus = (TextView) findViewById(R.id.tv_ddm_pe_minus);
				tv_ddm_krf_minus = (TextView) findViewById(R.id.tv_ddm_krf_minus);
				tv_ddm_krm_minus = (TextView) findViewById(R.id.tv_ddm_krm_minus);
				tv_ddm_b_minus = (TextView) findViewById(R.id.tv_ddm_b_minus);
				tv_ddm_mos_minus = (TextView) findViewById(R.id.tv_ddm_mos_minus);

				et_ddm_dpr_eps = (EditText) findViewById(R.id.et_ddm_dpr_eps);
				et_ddm_bvps_eps = (EditText) findViewById(R.id.et_ddm_bvps_eps);
				et_ddm_roe_eps = (EditText) findViewById(R.id.et_ddm_roe_eps);
				et_ddm_pe_eps = (EditText) findViewById(R.id.et_ddm_pe_eps);
				et_ddm_krf_eps = (EditText) findViewById(R.id.et_ddm_krf_eps);
				et_ddm_krm_eps = (EditText) findViewById(R.id.et_ddm_krm_eps);
				et_ddm_b_eps = (EditText) findViewById(R.id.et_ddm_b_eps);
				et_ddm_mos_eps = (EditText) findViewById(R.id.et_ddm_mos_eps);
				tv_ddm_ke_eps = (TextView) findViewById(R.id.tv_ddm_ke_eps);
				editEditTextDdm(); // Edit EditText

				tv_ddm_dpr_plus = (TextView) findViewById(R.id.tv_ddm_dpr_plus);
				tv_ddm_bvps_plus = (TextView) findViewById(R.id.tv_ddm_bvps_plus);
				tv_ddm_roe_plus = (TextView) findViewById(R.id.tv_ddm_roe_plus);
				tv_ddm_pe_plus = (TextView) findViewById(R.id.tv_ddm_pe_plus);
				tv_ddm_krf_plus = (TextView) findViewById(R.id.tv_ddm_krf_plus);
				tv_ddm_krm_plus = (TextView) findViewById(R.id.tv_ddm_krm_plus);
				tv_ddm_b_plus = (TextView) findViewById(R.id.tv_ddm_b_plus);
				tv_ddm_mos_plus = (TextView) findViewById(R.id.tv_ddm_mos_plus);

				tv_ddm_default = (TextView) findViewById(R.id.tv_ddm_default);
				tv_ddm_apply = (TextView) findViewById(R.id.tv_ddm_apply);
				
				tv_ddm_dpr_minus.setOnClickListener(onClickListenerPlusMinusDdm);
				tv_ddm_bvps_minus.setOnClickListener(onClickListenerPlusMinusDdm);
				tv_ddm_roe_minus.setOnClickListener(onClickListenerPlusMinusDdm);
				tv_ddm_pe_minus.setOnClickListener(onClickListenerPlusMinusDdm);
				tv_ddm_krf_minus.setOnClickListener(onClickListenerPlusMinusDdm);
				tv_ddm_krm_minus.setOnClickListener(onClickListenerPlusMinusDdm);
				tv_ddm_b_minus.setOnClickListener(onClickListenerPlusMinusDdm);
				tv_ddm_mos_minus.setOnClickListener(onClickListenerPlusMinusDdm);

				tv_ddm_dpr_plus.setOnClickListener(onClickListenerPlusMinusDdm);
				tv_ddm_bvps_plus.setOnClickListener(onClickListenerPlusMinusDdm);
				tv_ddm_roe_plus.setOnClickListener(onClickListenerPlusMinusDdm);
				tv_ddm_pe_plus.setOnClickListener(onClickListenerPlusMinusDdm);
				tv_ddm_krf_plus.setOnClickListener(onClickListenerPlusMinusDdm);
				tv_ddm_krm_plus.setOnClickListener(onClickListenerPlusMinusDdm);
				tv_ddm_b_plus.setOnClickListener(onClickListenerPlusMinusDdm);
				tv_ddm_mos_plus.setOnClickListener(onClickListenerPlusMinusDdm);

				tv_ddm_default.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (jsonGetValuationCondition_default != null) {
							jsonGetValuationCondition = jsonGetValuationCondition_default;
							setDataCondition();
						}
					}
				});
				tv_ddm_apply.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						// http://realtime.bidschart.com/service/v2/setValuationCondition?symbol=INTUCH
						// &user_id=1040&mos=20.00&bv=6.66&krm=12.00&ke=11.67&krf=4.00&pe=13.66&roe=65.40
						// &payout_ratio=46.91&beta=0.96&timestamp=1475482850

						java.util.Date date = new java.util.Date();
						long timestamp = date.getTime();
						url_ValuationCondition = SplashScreen.url_bidschart
								+ "/service/v2/setValuationCondition?symbol="
								+ FragmentChangeActivity.strSymbolSelect
								+ "&user_id=" + SplashScreen.userModel.user_id

								+ "&mos=" + et_ddm_mos_eps.getText().toString()
								+ "&bv=" + et_ddm_bvps_eps.getText().toString()
								+ "&krm=" + et_ddm_krm_eps.getText().toString()
								+ "&ke=" + tv_ddm_ke_eps.getText().toString()
								+ "&krf=" + et_ddm_krf_eps.getText().toString()
								+ "&pe=" + et_ddm_pe_eps.getText().toString()
								+ "&roe=" + et_ddm_roe_eps.getText().toString()
								+ "&payout_ratio="
								+ et_ddm_dpr_eps.getText().toString()
								+ "&beta=" + et_ddm_b_eps.getText().toString()
								+ "&timestamp=" + timestamp;

						loadDataDetail(); // load data
					}
				});

				et_ddm_dpr_eps.setText(FunctionSymbol
						.setFormatAnd2Digit(jsoCondition_set
								.getString("payout_ratio")));
				et_ddm_bvps_eps.setText(FunctionSymbol
						.setFormatAnd2Digit(jsoCondition_set.getString("bv")));
				et_ddm_roe_eps.setText(FunctionSymbol
						.setFormatAnd2Digit(jsoCondition_set.getString("roe")));
				et_ddm_pe_eps.setText(FunctionSymbol
						.setFormatAnd2Digit(jsoCondition_set.getString("pe")));
				et_ddm_krf_eps.setText(FunctionSymbol
						.setFormatAnd2Digit(jsoCondition_set.getString("krf")));
				et_ddm_krm_eps.setText(FunctionSymbol
						.setFormatAnd2Digit(jsoCondition_set.getString("krm")));
				et_ddm_b_eps
						.setText(FunctionSymbol
								.setFormatAnd2Digit(jsoCondition_set
										.getString("beta")));
				et_ddm_mos_eps.setText(FunctionSymbol
						.setFormatAnd2Digit(jsoCondition_set.getString("mos")));
				tv_ddm_ke_eps.setText(FunctionSymbol
						.setFormatAnd2Digit(jsoCondition_data.getString("ke")));
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// ============= Edit DDM EditText ===========
	private void editEditTextDdm() {
		et_ddm_dpr_eps.addTextChangedListener(new TextWatcher() {
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
				String text = et_ddm_dpr_eps.getText().toString();
			}
		});
		et_ddm_bvps_eps.addTextChangedListener(new TextWatcher() {
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
				String text = et_ddm_bvps_eps.getText().toString();
			}
		});
		et_ddm_roe_eps.addTextChangedListener(new TextWatcher() {
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
				String text = et_ddm_roe_eps.getText().toString();
			}
		});
		et_ddm_pe_eps.addTextChangedListener(new TextWatcher() {
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
				String text = et_ddm_pe_eps.getText().toString();
			}
		});
		et_ddm_krf_eps.addTextChangedListener(new TextWatcher() {
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
				String text = et_ddm_krf_eps.getText().toString();
				calDdmKe(); // cal DDM
			}
		});
		et_ddm_krm_eps.addTextChangedListener(new TextWatcher() {
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
				String text = et_ddm_krm_eps.getText().toString();
				calDdmKe(); // cal DDM
			}
		});
		et_ddm_b_eps.addTextChangedListener(new TextWatcher() {
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
				String text = et_ddm_b_eps.getText().toString();
				calDdmKe(); // cal DDM
			}
		});
		et_ddm_mos_eps.addTextChangedListener(new TextWatcher() {
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
				String text = et_ddm_mos_eps.getText().toString();
			}
		});
	}

	// ============= Edit ddm Plus Minus ===========
	public static OnClickListener onClickListenerPlusMinusDdm = new OnClickListener() {
		@Override
		public void onClick(final View v) {

			// Toast.makeText(context, "" + v.getId(), 0).show();
			String text;
			double dText;
			switch (v.getId()) {
			case R.id.tv_ddm_dpr_minus:
				text = et_ddm_dpr_eps.getText().toString();
				dText = Double.parseDouble(text);
				// if (dText > 0) {
				et_ddm_dpr_eps.setText(FunctionSymbol.setFormat2Digit("" + (dText - 1)));
				// }
				break;
			case R.id.tv_ddm_bvps_minus:
				text = et_ddm_bvps_eps.getText().toString();
				dText = Double.parseDouble(text);
				// if (dText > 0) {
				et_ddm_bvps_eps.setText(FunctionSymbol.setFormat2Digit("" + (dText - 1)));
				// }
				break;
			case R.id.tv_ddm_roe_minus:
				text = et_ddm_roe_eps.getText().toString();
				dText = Double.parseDouble(text);
				// if (dText > 0) {
				et_ddm_roe_eps.setText(FunctionSymbol.setFormat2Digit("" + (dText - 1)));
				// }
				break;
			case R.id.tv_ddm_pe_minus:
				text = et_ddm_pe_eps.getText().toString();
				dText = Double.parseDouble(text);
				// if (dText > 0) {
				et_ddm_pe_eps.setText(FunctionSymbol.setFormat2Digit("" + (dText - 1)));
				// }
				break;
			case R.id.tv_ddm_krf_minus:
				text = et_ddm_krf_eps.getText().toString();
				dText = Double.parseDouble(text);
				// if (dText > 0) {
				et_ddm_krf_eps.setText(FunctionSymbol.setFormat2Digit("" + (dText - 1)));
				// }
				break;
			case R.id.tv_ddm_krm_minus:
				text = et_ddm_krm_eps.getText().toString();
				dText = Double.parseDouble(text);
				// if (dText > 0) {
				et_ddm_krm_eps.setText(FunctionSymbol.setFormat2Digit("" + (dText - 1)));
				// }
				break;
			case R.id.tv_ddm_b_minus:
				text = et_ddm_b_eps.getText().toString();
				dText = Double.parseDouble(text);
				// if (dText > 0) {
				et_ddm_b_eps.setText(FunctionSymbol.setFormat2Digit("" + (dText - 1)));
				// }
				break;
			case R.id.tv_ddm_mos_minus:
				text = et_ddm_mos_eps.getText().toString();
				dText = Double.parseDouble(text);
				// if (dText > 0) {
				et_ddm_mos_eps.setText(FunctionSymbol.setFormat2Digit("" + (dText - 1)));
				// }
				break;
			case R.id.tv_ddm_dpr_plus:
				text = et_ddm_dpr_eps.getText().toString();
				dText = Double.parseDouble(text);
				et_ddm_dpr_eps.setText(FunctionSymbol.setFormat2Digit("" + (dText + 1)));
				break;
			case R.id.tv_ddm_bvps_plus:
				text = et_ddm_bvps_eps.getText().toString();
				dText = Double.parseDouble(text);
				et_ddm_bvps_eps.setText(FunctionSymbol.setFormat2Digit("" + (dText + 1)));
				break;
			case R.id.tv_ddm_roe_plus:
				text = et_ddm_roe_eps.getText().toString();
				dText = Double.parseDouble(text);
				et_ddm_roe_eps.setText(FunctionSymbol.setFormat2Digit("" + (dText + 1)));
				break;
			case R.id.tv_ddm_pe_plus:
				text = et_ddm_pe_eps.getText().toString();
				dText = Double.parseDouble(text);
				et_ddm_pe_eps.setText(FunctionSymbol.setFormat2Digit("" + (dText + 1)));
				break;
			case R.id.tv_ddm_krf_plus:
				text = et_ddm_krf_eps.getText().toString();
				dText = Double.parseDouble(text);
				et_ddm_krf_eps.setText(FunctionSymbol.setFormat2Digit("" + (dText + 1)));
				break;
			case R.id.tv_ddm_krm_plus:
				text = et_ddm_krm_eps.getText().toString();
				dText = Double.parseDouble(text);
				et_ddm_krm_eps.setText(FunctionSymbol.setFormat2Digit("" + (dText + 1)));
				break;
			case R.id.tv_ddm_b_plus:
				text = et_ddm_b_eps.getText().toString();
				dText = Double.parseDouble(text);
				et_ddm_b_eps.setText(FunctionSymbol.setFormat2Digit("" + (dText + 1)));
				break;
			case R.id.tv_ddm_mos_plus:
				text = et_ddm_mos_eps.getText().toString();
				dText = Double.parseDouble(text);
				et_ddm_mos_eps.setText(FunctionSymbol.setFormat2Digit("" + (dText + 1)));
				break;
			default:
				break;
			}
		}
	};

	// ======= cal DDM
	public static void calDdmKe() {
		String sDpr = et_ddm_dpr_eps.getText().toString();
		String sEps = et_ddm_bvps_eps.getText().toString();
		String sRoe = et_ddm_roe_eps.getText().toString();
		String sPe = et_ddm_pe_eps.getText().toString();
		String sKrf = et_ddm_krf_eps.getText().toString();
		String sKrm = et_ddm_krm_eps.getText().toString();
		String sB = et_ddm_b_eps.getText().toString();
		String sMos = et_ddm_mos_eps.getText().toString();

		if (!(sKrf.equals("")) && !(sKrm.equals("")) && !(sB.equals(""))) {
			double dKrf = Double.parseDouble(sKrf);
			double dKrm = Double.parseDouble(sKrm);
			double dB = Double.parseDouble(sB);

			// ke = rf+(rm-rf)*beta;
			double ke = dKrf + (dKrm - dKrf) * dB;
			tv_ddm_ke_eps.setText(FunctionSymbol.setFormatAnd2Digit("" + ke));
		}
	};

	// ======= set data Condition DDM
	public void setDataConditionDdm(JSONObject jsoCondition_data) {
		try {
			if (jsoCondition_data != null) {
				JSONArray jsoCondition_ddm = jsoCondition_data
						.getJSONArray("ddm");

				// --------- data condition --------
				if (jsoCondition_ddm != null) {
					LinearLayout list_ddm_title = (LinearLayout) findViewById(R.id.list_ddm_title);
					LinearLayout list_ddm_data = (LinearLayout) findViewById(R.id.list_ddm_data);
					list_ddm_title.removeAllViews();
					list_ddm_data.removeAllViews();

					for (int i = 0; i < jsoCondition_ddm.length(); i++) {
						JSONArray jsaData = jsoCondition_ddm.getJSONArray(i);

						// index 0 title
						if (i == 0) {
							for (int j = 0; j < jsaData.length(); j++) {
								View viewTitle = getLayoutInflater().inflate(
										R.layout.row_dusit_valuation_ddm_title,
										null);
								TextView tv_row = (TextView) viewTitle
										.findViewById(R.id.tv_row);

								tv_row.setText(jsaData.get(j).toString());
								if (j == 0) {
									tv_row.setTextColor(getResources()
											.getColor(FunctionSetBg.arrColor[4]));
								} else {
									tv_row.setTextColor(getResources()
											.getColor(FunctionSetBg.arrColor[5]));
								}
								list_ddm_title.addView(viewTitle);
							}
							// set data เริ่มจาก index 1
						} else {
							LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
							View viewData = inflater.inflate(
									R.layout.row_linear, null);

							LinearLayout li_row = (LinearLayout) viewData
									.findViewById(R.id.li_row);
							for (int j = 0; j < jsaData.length(); j++) {
								LayoutInflater inflater2 = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
								View viewDataRow = inflater2.inflate(
										R.layout.row_dusit_valuation_ddm_data,
										null);

								TextView tv_row = (TextView) viewDataRow
										.findViewById(R.id.tv_row);

								if (j == 0) {
									Calendar calendar = Calendar.getInstance();
									int year = calendar.get(Calendar.YEAR);
									int yearP = Integer.parseInt(jsaData.get(j)
											.toString());
									tv_row.setText("" + (year + yearP));
									tv_row.setTextColor(getResources()
											.getColor(FunctionSetBg.arrColor[4]));
								} else {
									tv_row.setText(FunctionSymbol
											.setFormatAnd2Digit(jsaData.get(j)
													.toString()));
								}

								li_row.addView(viewDataRow);
							}
							list_ddm_data.addView(li_row);
						}
					}

				}
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
