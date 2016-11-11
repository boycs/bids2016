package com.app.bids;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
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
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

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

public class UiWatchlistDetail_old extends FragmentActivity implements
		OnTouchListener, Handler.Callback {

	private static Activity act;

	// view pager
	static ViewPager mPagerMainPreMium;
	// list contains fragments to instantiate in the viewpager
	List<Fragment> fragmentMain = new Vector<Fragment>();
	private PagerAdapter mPagerAdapterMain;

	List<Fragment> fragmentMainPreMium = new Vector<Fragment>();
	private PagerAdapter mPagerAdapterMainPreMium;

	// show hide
	LinearLayout li_menu_top, li_data;

	ImageView img_follow;

	static Dialog dialogLoading;

	// ================= data ==================
	public static JSONObject contentGetDetail = null;
	public static String contentGetDetailFundamental = "";
	public static JSONObject contentGetDetailUpdate = null;
	public static LinearLayout li_vertical;
	public static RelativeLayout rl_chartiq;

	// ========== pager detail =======
	public static JSONObject contentGetFundamental = null;
	public static String strIndustry = "";
	public static String strIndustrySector = "ALL";

	public static JSONObject contentGetRetrieveSetAlert = null;
	public static JSONArray contentGetWatchlistNewsBySymbol = null;

	public static boolean ckRemoveViewChart = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_watchlist_detail_old);

		((TextView) findViewById(R.id.tv_title_symbol))
				.setText(FragmentChangeActivity.strSymbolSelect + "'s Detail");

		((LinearLayout) findViewById(R.id.li_back))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						wv_chart_iq.loadUrl("javascript:(function () { "
								+ "mobileControl.closeConnection();" + "})()");
						finish();
					}
				});

		// -------- stop timer ----
		if (FragmentChangeActivity.timerUpdateSymbolStatus) {
			FragmentChangeActivity.timerUpdateSymbolStatus = false;
			FragmentChangeActivity.timerUpdateSymbol.cancel();
		}

		img_follow = (ImageView) findViewById(R.id.img_follow);

		rl_chartiq = (RelativeLayout) findViewById(R.id.rl_chartiq);
		li_vertical = (LinearLayout) findViewById(R.id.li_vertical);

		dialogLoading = new Dialog(UiWatchlistDetail_old.this);
		dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogLoading.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialogLoading.setContentView(R.layout.progress_bar);
		dialogLoading.setCancelable(false);
		dialogLoading.setCanceledOnTouchOutside(false);

		li_menu_top = (LinearLayout) findViewById(R.id.li_menu_top);
		li_data = (LinearLayout) findViewById(R.id.li_data);
		li_menu_top.setVisibility(View.VISIBLE);
		li_data.setVisibility(View.VISIBLE);

		if (FragmentChangeActivity.contentGetTxtSlidingMarquee != null) {
			initTxtSliding();
		} else {
			loadTxtSlidingMarquee(); // text sliding
		}

		loadDataDetail();
		// setFollowSymbol();

	}

	// ***************** text sliding ******************
	private void initTxtSliding() {
		String strSliding = "";

		com.app.custom.CustomTextViewSliding marque = (com.app.custom.CustomTextViewSliding) findViewById(R.id.tv_sliding_marquee);
		ImageView img_status_m = (ImageView) findViewById(R.id.img_status_m);

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
						double db = Double.parseDouble(txtLtrade.replaceAll(
								",", ""));
						txtLtrade = FunctionSymbol.setFormatNumber(txtLtrade);
					}

					if (txtChange != "") {
						txtChange = FunctionSymbol.setFormatNumber(txtChange);
					}

					if (txtSymbol.equals(".SET")) {
						if (txtPchange != "") {
							double dbS = Double.parseDouble(txtSetPChenge
									.replaceAll(",", ""));
							txtPchange = FunctionSetBg
									.setColorTxtSlidingSet(txtSetPChenge);
						}
					} else {
						if (txtPchange != "") {
							double dbS = Double.parseDouble(txtSetPChenge
									.replaceAll(",", ""));
							double dbO = Double.parseDouble(txtPchange
									.replaceAll(",", ""));
							txtPchange = FunctionSetBg
									.setColorTxtSliding(txtSetPChenge,
											txtPchange);
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

	// ***************** SlidingMarquee ******************
	private void loadTxtSlidingMarquee() {
		loadDataSlidingMarquee resp = new loadDataSlidingMarquee();
		resp.execute();
	}

	public class loadDataSlidingMarquee extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();

			// SplashScreen.url_bidschart+"/service/v2/symbolByIndustrySector?industry=TECH&sector=TECH&page=1&limit=10
			// ** top = swing , volume , value , gainer , loser

			// http://www.bidschart.com/service/v2/getMaiSet
			String url_TxtSlidingMarquee = SplashScreen.url_bidschart
					+ "/service/v2/getMaiSet";

			try {
				FragmentChangeActivity.jsonTxtSlidingMarquee = ReadJson
						.readJsonObjectFromUrl(url_TxtSlidingMarquee);
			} catch (IOException e1) {
				connectionError = true;
				FragmentChangeActivity.jsonTxtSlidingMarquee = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				FragmentChangeActivity.jsonTxtSlidingMarquee = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				FragmentChangeActivity.jsonTxtSlidingMarquee = null;
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (FragmentChangeActivity.jsonTxtSlidingMarquee != null) {
					try {

						FragmentChangeActivity.contentGetTxtSlidingMarquee = FragmentChangeActivity.jsonTxtSlidingMarquee
								.getJSONArray("dataAll");

						initTxtSliding();

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.v("jsonGetData", "jsonGetData null");
				}

			} else {
				Log.v("connectionError", "connectionError ture");
			}
		}
	}

	// ============== Load Data all =============
	// public static ImageView img_follow;

	public void setFollowSymbol() {
		img_follow.setBackgroundResource(FunctionSymbol
				.setFavoriteNumber(FragmentChangeActivity.strSymbolSelect));

		// follow
		img_follow.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// เช็ค follow หรือยัง
				boolean ckFollow = FunctionSymbol
						.checkFollowSymbol(FragmentChangeActivity.strSymbolSelect);

				// นับ follow T เพิ่มได้ ถ้าเกิน 21 เป็น F เพิ่มไม่ได้
				boolean ckFollowCountLimit = FunctionSymbol
						.checkFollowCount(FragmentChangeActivity.strSymbolSelect);

				if (!(SplashScreen.userModel.user_id != "")) {
					Toast.makeText(getApplicationContext(), "Login", 0).show();
					LoginDialog.show();
				} else {
					if (ckFollow) {
						getDataFavoriteId(); // unfollow
					} else {
						if (ckFollowCountLimit) {
							dialogFavorite();
						} else {
							Toast.makeText(getApplicationContext(),
									"over limit favorites.", 0).show();
						}
					}
				}
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
		private JSONObject jsonGetDetail;

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

			// http://www.bidschart.com/service/v2/watchlistSymbol?user_id=104&symbol=AAV
			String url_GetDetail = SplashScreen.url_bidschart
					+ "/service/v2/watchlistSymbol?user_id="
					+ SplashScreen.userModel.user_id + "&symbol="
					+ FragmentChangeActivity.strSymbolSelect + "&timestamp="
					+ timestamp;

			try {
				// ======= Ui Home ========
				jsonGetDetail = ReadJson.readJsonObjectFromUrl(url_GetDetail);
				// jsonGetDetailFollow = ReadJson
				// .readJsonObjectFromUrl(url_GetDetailFollow);
			} catch (IOException e1) {
				connectionError = true;
				jsonGetDetail = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonGetDetail = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonGetDetail = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (jsonGetDetail != null) {
					try {
						// get content

						// contentGetDetailFollow = jsonGetDetailFollow
						// .getJSONArray("dataAll");

						if (jsonGetDetail.getJSONArray("dataAll") != null) {
							contentGetDetail = jsonGetDetail.getJSONArray(
									"dataAll").getJSONObject(0);

							contentGetDetailFundamental = contentGetDetail
									.getString("fundamental");
							setDataDetail();
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.v("json newslist null", "newslist null");
					dialogLoading.dismiss();
				}
			} else {
				dialogLoading.dismiss();
			}
		}

	}

	// ======= data show hide
	public void showDataDetail() {
		// popup_window_detail.startAnimation(animShow);
		popup_window_detail.setVisibility(View.VISIBLE);
		// popup_window_systemtrade.setVisibility(View.VISIBLE);
	}

	public void hideDataDetail() {
		// popup_window_detail.startAnimation(animHide);
		popup_window_detail.setVisibility(View.GONE);
		// popup_window_systemtrade.setVisibility(View.GONE);
	}

	// ====================== setDataDetail ================
	public static LinearLayout li_menu, li_menu_premium;
	public static TextView tv_chart, tv_news, tv_sector;
	public static TextView tv_premium_chart, tv_premium_industry,
			tv_premium_fundamental, tv_premium_news, tv_premium_hit;
	// ImageView img_follow;
	public static LinearLayout li_showbysell;

	boolean ckHideShow = false;
	boolean ckHideShowData = false;

	private void initTabPagerPreMium() {
		tv_premium_chart = (TextView) findViewById(R.id.tv_premium_chart);
		tv_premium_industry = (TextView) findViewById(R.id.tv_premium_industry);
		tv_premium_fundamental = (TextView) findViewById(R.id.tv_premium_fundamental);
		tv_premium_news = (TextView) findViewById(R.id.tv_premium_news);
		tv_premium_hit = (TextView) findViewById(R.id.tv_premium_hit);

		// try {
		// strIndustry = UiWatchlistDetail.contentGetDetail
		// .getString("industry");
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }

		tv_premium_chart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectTabPagerPreMium(1);
				mPagerMainPreMium.setCurrentItem(0);
				// Toast.makeText(getApplicationContext(),
				// ""+mPagerMainPreMium.getCurrentItem(), 0).show();
				// mPagerMainPreMium.setCurrentItem(mPagerMainPreMium.getCurrentItem()
				// + 1);
			}
		});
		tv_premium_industry.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectTabPagerPreMium(2);
				mPagerMainPreMium.setCurrentItem(1);
				// Toast.makeText(getApplicationContext(),
				// ""+mPagerMainPreMium.getCurrentItem(), 0).show();
			}
		});
		tv_premium_fundamental.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectTabPagerPreMium(3);
				mPagerMainPreMium.setCurrentItem(2);
				// Toast.makeText(getApplicationContext(),
				// ""+mPagerMainPreMium.getCurrentItem(), 0).show();
			}
		});
		tv_premium_news.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectTabPagerPreMium(4);
				mPagerMainPreMium.setCurrentItem(3);
				// Toast.makeText(getApplicationContext(),
				// ""+mPagerMainPreMium.getCurrentItem(), 0).show();
			}
		});
		tv_premium_hit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectTabPagerPreMium(5);
				mPagerMainPreMium.setCurrentItem(4);
				// Toast.makeText(getApplicationContext(),
				// ""+mPagerMainPreMium.getCurrentItem(), 0).show();
			}
		});
	}

	// main
	TextView tv_symbol, tv_symbol_status, tv_last_trade, tv_symbol_name_eng,
			tv_percenchange;
	// ImageView img_updown;
	// column 1
	TextView tv_volume, tv_value, tv_open, tv_high, tv_low;
	// column 2
	TextView tv_prev_close, tv_ceil, tv_floor, tv_high52W, tv_low52W;
	// column 3
	TextView tv_roe, tv_roa, tv_peg, tv_p_e, tv_p_bv;

	// show hide
	private Animation animShow, animHide;
	SlidingPanel popup_window_detail;
	LinearLayout li_data_symbol, li_data_all;
	LinearLayout li_showbuysell, li_show_hide, li_buysell;
	ImageView img_show_hide;
	TextView tv_average_buy, tv_max_buy_price_volume, tv_open1_volume,
			tv_buy_volume;

	TextView tv_average_sell, tv_max_sell_price_volume, tv_close_volume,
			tv_sell_volume;

	TextView tv_percentBuy, tv_percentSell;
	View v_percentBuy, v_percentSell;

	private void setDataDetail() {
		// init follow & chart iq
		// img_follow = (ImageView) findViewById(R.id.img_follow);
		setFollowSymbol();
		// initChartIq();

		// -------- tab pager
		li_menu = (LinearLayout) findViewById(R.id.li_menu);
		li_menu_premium = (LinearLayout) findViewById(R.id.li_menu_premium);
		li_menu.setVisibility(View.VISIBLE);
		li_menu_premium.setVisibility(View.GONE);

		// -------- show hide data
		animShow = AnimationUtils.loadAnimation(this,
				R.animator.symbol_detail_down);
		animHide = AnimationUtils.loadAnimation(this,
				R.animator.symbol_detail_up);

		li_data_symbol = (LinearLayout) findViewById(R.id.li_data_symbol);
		li_data_all = (LinearLayout) findViewById(R.id.li_data_all);
		popup_window_detail = (SlidingPanel) findViewById(R.id.popup_window_detail);

		li_data_symbol.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (ckHideShowData == false) { // hide data
					ckHideShowData = true;
					// li_data_all.setVisibility(View.GONE);
					hideDataDetail();

					// --------- set params pager ------
					ScrollView sv_data = (ScrollView) findViewById(R.id.sv_data);
					int sv_width = sv_data.getWidth();
					int sv_height = sv_data.getHeight();

					li_menu_premium = (LinearLayout) findViewById(R.id.li_menu_premium);
					int li_height_menu_premium = li_menu_premium.getHeight();
					
					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
							sv_width, (sv_height - li_height_menu_premium));
					mPagerMainPreMium.setLayoutParams(layoutParams);
				} else { // show data
					ckHideShowData = false;
					// li_data_all.setVisibility(View.VISIBLE);
					showDataDetail();

					// --------- set params pager ------
					ScrollView sv_data = (ScrollView) findViewById(R.id.sv_data);
					int sv_width = sv_data.getWidth();
					int sv_height = sv_data.getHeight();

					li_menu_premium = (LinearLayout) findViewById(R.id.li_menu_premium);
					int li_height_menu_premium = li_menu_premium.getHeight();

					LinearLayout li_data_all = (LinearLayout) findViewById(R.id.li_data_all);
					int li_height_showdataall = li_data_all.getHeight();

					LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
							sv_width,
							(sv_height - (li_height_menu_premium + li_height_showdataall)));
					mPagerMainPreMium.setLayoutParams(layoutParams);
				}
			}
		});

		try {
			// if (SplashScreen.contentGetUserById != null) {
			// if (!(SplashScreen.contentGetUserById.getString("package")
			// .equals("free"))) {
			// li_menu.setVisibility(View.GONE);
			// li_menu_premium.setVisibility(View.VISIBLE);
			// initPagerPreMium();
			// } else {
			// initPager();
			// }
			// } else {
			// initPager();
			// }

			if (contentGetDetail != null) {
				li_menu.setVisibility(View.GONE);
				li_menu_premium.setVisibility(View.VISIBLE);
				initPagerPreMium();

				// main
				tv_symbol = (TextView) findViewById(R.id.tv_symbol);
				tv_symbol_status = (TextView) findViewById(R.id.tv_symbol_status);
				tv_last_trade = (TextView) findViewById(R.id.tv_last_trade);
				tv_symbol_name_eng = (TextView) findViewById(R.id.tv_symbol_name_eng);
				tv_percenchange = (TextView) findViewById(R.id.tv_percenchange);
				// img_updown = (ImageView) findViewById(R.id.img_updown);
				// column 1
				tv_volume = (TextView) findViewById(R.id.tv_volume);
				tv_value = (TextView) findViewById(R.id.tv_value);
				tv_open = (TextView) findViewById(R.id.tv_open);
				tv_high = (TextView) findViewById(R.id.tv_high);
				tv_low = (TextView) findViewById(R.id.tv_low);
				// column 2
				tv_prev_close = (TextView) findViewById(R.id.tv_prev_close);
				tv_ceil = (TextView) findViewById(R.id.tv_ceil);
				tv_floor = (TextView) findViewById(R.id.tv_floor);
				tv_high52W = (TextView) findViewById(R.id.tv_high52W);
				tv_low52W = (TextView) findViewById(R.id.tv_low52W);
				// column 3
				tv_roe = (TextView) findViewById(R.id.tv_roe);
				tv_roa = (TextView) findViewById(R.id.tv_roa);
				tv_peg = (TextView) findViewById(R.id.tv_peg);
				tv_p_e = (TextView) findViewById(R.id.tv_p_e);
				tv_p_bv = (TextView) findViewById(R.id.tv_p_bv);

				// show hide
				li_showbysell = (LinearLayout) findViewById(R.id.li_showbuysell);
				li_show_hide = (LinearLayout) findViewById(R.id.li_show_hide);
				li_buysell = (LinearLayout) findViewById(R.id.li_buysell);
				img_show_hide = (ImageView) findViewById(R.id.img_show_hide);
				tv_average_buy = (TextView) findViewById(R.id.tv_average_buy);
				tv_max_buy_price_volume = (TextView) findViewById(R.id.tv_max_buy_price_volume);
				tv_open1_volume = (TextView) findViewById(R.id.tv_open1_volume);
				tv_buy_volume = (TextView) findViewById(R.id.tv_buy_volume);

				tv_average_sell = (TextView) findViewById(R.id.tv_average_sell);
				tv_max_sell_price_volume = (TextView) findViewById(R.id.tv_max_sell_price_volume);
				tv_close_volume = (TextView) findViewById(R.id.tv_close_volume);
				tv_sell_volume = (TextView) findViewById(R.id.tv_sell_volume);

				tv_percentBuy = (TextView) findViewById(R.id.tv_percentBuy);
				tv_percentSell = (TextView) findViewById(R.id.tv_percentSell);
				v_percentBuy = (View) findViewById(R.id.v_percentBuy);
				v_percentSell = (View) findViewById(R.id.v_percentSell);

				// ======= set data ========
				// main
				String strSymbol_name = contentGetDetail
						.getString("symbol_name");
				String strLast_trade = contentGetDetail.getString("last_trade");
				String strSymbol_fullname_eng = contentGetDetail
						.getString("symbol_fullname_eng");

				String turnover_list_level = contentGetDetail
						.getString("turnover_list_level");
				String status = contentGetDetail.getString("status");
				String status_xd = contentGetDetail.getString("status_xd");

				// tv_symbol.setText(Html.fromHtml(FunctionSymbol
				// .checkStatusSymbol(strSymbol_name, turnover_list_level,
				// status, status_xd)));

				tv_symbol.setText(strSymbol_name);
				tv_symbol_status.setText(FunctionSymbol
						.checkStatusSymbolDetail(turnover_list_level, status,
								status_xd));

				tv_last_trade.setText(FunctionSymbol
						.setFormatNumber(strLast_trade));
				tv_symbol_name_eng.setText(strSymbol_fullname_eng);

				// main color
				String strColor = contentGetDetail.getString("change");

				// ck ltrade change
				String strLastTrade = contentGetDetail.getString("last_trade");
				String strChange = contentGetDetail.getString("change");
				String strPercentChange = contentGetDetail
						.getString("percentChange");

				tv_last_trade.setText(strLastTrade);
				// tv_change.setText(strChange);
				if ((strPercentChange == "0") || (strPercentChange == "")
						|| (strPercentChange == "0.00")) {
					tv_percenchange.setText("0.00");
				} else {
					tv_percenchange.setText(strPercentChange + "%");
					tv_percenchange.setText(FunctionSymbol
							.setFormatNumber(strChange)
							+ "("
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

				// column 1
				String strVolume = contentGetDetail.getString("volume");
				String strValue = contentGetDetail.getString("value");
				String strOpen = contentGetDetail.getString("open");
				String strHigh = contentGetDetail.getString("high");
				String strLow = contentGetDetail.getString("low");
				String strPrevClose = contentGetDetail.getString("prev_close");

				tv_volume.setText(strVolume);
				tv_value.setText(strValue);
				tv_open.setText(FunctionSymbol.setFormatNumber(strOpen));

				tv_high.setText(FunctionSetBg.setStrDetailList(strHigh));
				tv_low.setText(FunctionSetBg.setStrDetailList(strLow));

				if (strPrevClose != "") {
					if (strHigh != "") {
						if ((Float.parseFloat(strHigh.replaceAll(",", ""))) > Float
								.parseFloat(strPrevClose.replaceAll(",", ""))) {
							tv_high.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[2]));
						} else if ((Float.parseFloat(strHigh
								.replaceAll(",", ""))) < Float
								.parseFloat(strPrevClose.replaceAll(",", ""))) {
							tv_high.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[0]));
						} else {
							tv_high.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[1]));
						}
					}
					if (strLow != "") {
						if ((Float.parseFloat(strLow.replaceAll(",", ""))) > Float
								.parseFloat(strPrevClose.replaceAll(",", ""))) {
							tv_low.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[2]));
						} else if ((Float
								.parseFloat(strLow.replaceAll(",", ""))) < Float
								.parseFloat(strPrevClose.replaceAll(",", ""))) {
							tv_low.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[0]));
						} else {
							tv_low.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[1]));
						}
					}
				}

				// column 2
				String strCeiling = contentGetDetail.getString("ceiling");
				String strFloor = contentGetDetail.getString("floor");
				String strHigh52W = contentGetDetail.getString("high52W");
				String strLow52W = contentGetDetail.getString("low52W");

				String strPbv = contentGetDetail.getString("p_bv");
				String strRoe = contentGetDetail.getString("roe");
				String strRoa = contentGetDetail.getString("roa");
				String strPe = contentGetDetail.getString("p_e");
				String strPeg = contentGetDetail.getString("peg");

				tv_prev_close.setText(FunctionSymbol
						.setFormatNumber(strPrevClose));
				tv_ceil.setText(FunctionSymbol.setFormatNumber(strCeiling));
				tv_floor.setText(FunctionSymbol.setFormatNumber(strFloor));
				tv_high52W.setText(FunctionSymbol.setFormatNumber(strHigh52W));
				tv_low52W.setText(FunctionSymbol.setFormatNumber(strLow52W));

				tv_roe.setText(FunctionSetBg.setStrDetailList(strRoe));
				tv_roa.setText(FunctionSetBg.setStrDetailList(strRoa));

				tv_peg.setText(FunctionSetBg.setStrDetailList(strPeg));
				tv_p_e.setText(FunctionSetBg.setStrDetailList(strPe));
				tv_p_bv.setText(FunctionSetBg.setStrDetailList(strPbv));

				// -- color write blue
				tv_ceil.setTextColor(getResources()
						.getColor(
								FunctionSetBg
										.setStrColorWriteDetailBlue(strHigh)));
				tv_roe.setTextColor(getResources()
						.getColor(
								FunctionSetBg
										.setStrColorWriteDetailBlue(strHigh)));
				tv_roa.setTextColor(getResources().getColor(
						FunctionSetBg.setStrColorWriteDetailBlue(strLow)));

				if (SplashScreen.contentSymbol_Set != null) {
					String strPe_set = SplashScreen.contentSymbol_Set
							.getString("p_e");
					String strPbv_set = SplashScreen.contentSymbol_Set
							.getString("p_bv");
					String strPeg_set = SplashScreen.contentSymbol_Set
							.getString("peg");
					String strHigh52W_set = SplashScreen.contentSymbol_Set
							.getString("high52W");
					String strLow52W_set = SplashScreen.contentSymbol_Set
							.getString("low52W");

					tv_p_e.setTextColor(getResources().getColor(
							FunctionSetBg
									.setStrCheckSet(strPe, strPe_set)));
					tv_p_bv.setTextColor(getResources().getColor(
							FunctionSetBg.setStrCheckSet(strPbv,
									strPbv_set)));
					tv_peg.setTextColor(getResources().getColor(
							FunctionSetBg.setStrCheckSet(strPeg,
									strPeg_set)));
					tv_peg.setTextColor(getResources().getColor(
							FunctionSetBg.setStrCheckSet(strHigh52W,
									strHigh52W_set)));
					tv_peg.setTextColor(getResources().getColor(
							FunctionSetBg.setStrCheckSet(strLow52W,
									strLow52W_set)));
				}

				if (strPrevClose != "") {
					if (strHigh52W != "") {
						if ((Float.parseFloat(strHigh52W.replaceAll(",", ""))) > Float
								.parseFloat(strPrevClose.replaceAll(",", ""))) {
							tv_high52W.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[2]));
						} else if ((Float.parseFloat(strHigh52W.replaceAll(",",
								""))) < Float.parseFloat(strPrevClose
								.replaceAll(",", ""))) {
							tv_high52W.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[0]));
						} else {
							tv_high52W.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[1]));
						}
					}
					if (strLow52W != "") {
						if ((Float.parseFloat(strLow52W.replaceAll(",", ""))) > Float
								.parseFloat(strPrevClose.replaceAll(",", ""))) {
							tv_low52W.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[2]));
						} else if ((Float.parseFloat(strLow52W.replaceAll(",",
								""))) < Float.parseFloat(strPrevClose
								.replaceAll(",", ""))) {
							tv_low52W.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[0]));
						} else {
							tv_low52W.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[1]));
						}
					}
					if (strOpen != "") {
						if ((Float.parseFloat(strOpen.replaceAll(",", ""))) > Float
								.parseFloat(strPrevClose.replaceAll(",", ""))) {
							tv_open.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[2]));
						} else if ((Float.parseFloat(strOpen
								.replaceAll(",", ""))) < Float
								.parseFloat(strPrevClose.replaceAll(",", ""))) {
							tv_open.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[0]));
						} else {
							tv_open.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[1]));
						}
					}
				}

				// show hide
				// final boolean ckHideShow = false;
				String strSymbN = contentGetDetail.getString("symbol_name");
				String strSymbDot = strSymbN.substring(1, 2);
				if (strSymbDot.equals(".")) {
					li_showbysell.setVisibility(View.GONE);
				} else {
					li_showbysell.setVisibility(View.VISIBLE);
					li_show_hide.setVisibility(View.GONE);
					li_buysell.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (ckHideShow == false) {
								ckHideShow = true;
								li_show_hide.setVisibility(View.VISIBLE);
								img_show_hide.setVisibility(View.VISIBLE);
								img_show_hide
										.setBackgroundResource(R.drawable.icon_dopdowe_up);
							} else {
								ckHideShow = false;
								li_show_hide.setVisibility(View.GONE);
								img_show_hide.setVisibility(View.GONE);
								img_show_hide
										.setBackgroundResource(R.drawable.icon_dopdowe_down);
							}
						}
					});

					img_show_hide.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (ckHideShow == false) {
								ckHideShow = true;
								li_show_hide.setVisibility(View.VISIBLE);
								img_show_hide.setVisibility(View.VISIBLE);
								img_show_hide
										.setBackgroundResource(R.drawable.icon_dopdowe_up);
							} else {
								ckHideShow = false;
								li_show_hide.setVisibility(View.GONE);
								img_show_hide.setVisibility(View.GONE);
								img_show_hide
										.setBackgroundResource(R.drawable.icon_dopdowe_down);
							}
						}
					});

					String strAverage_buy = contentGetDetail
							.getString("average_buy");
					String strMax_buy_price_volume = contentGetDetail
							.getString("max_buy_price_volume");
					String strMax_buy_price = contentGetDetail
							.getString("max_buy_price");
					String strOpen1_volume = contentGetDetail
							.getString("open1_volume");
					String strOpen1 = contentGetDetail.getString("open1");
					String strBuy_volume = contentGetDetail
							.getString("buy_volume");

					String strAverage_sell = contentGetDetail
							.getString("average_sell");
					String strMax_sell_price_volume = contentGetDetail
							.getString("max_sell_price_volume");
					String strMax_sell_price = contentGetDetail
							.getString("max_sell_price");
					String strClose_volume = contentGetDetail
							.getString("close_volume");
					String strSell_volume = contentGetDetail
							.getString("sell_volume");

					String strPercentBuy = contentGetDetail
							.getString("percentBuy");
					String strPercentSell = contentGetDetail
							.getString("percentSell");

					tv_average_buy.setText(FunctionSymbol
							.checkNull(strAverage_buy));
					tv_average_buy
							.setTextColor(getResources()
									.getColor(
											FunctionSetBg
													.setStrColorWriteDetailSuccess(strAverage_buy)));
					tv_max_buy_price_volume.setText(FunctionSymbol
							.setFormatNumber(strMax_buy_price_volume)
							+ ""
							+ FunctionSymbol.setBracket(strMax_buy_price));
					tv_max_buy_price_volume
							.setTextColor(getResources()
									.getColor(
											FunctionSetBg
													.setStrColorWriteDetailSuccess(strMax_buy_price)));
					tv_open1_volume.setText(FunctionSymbol
							.setFormatNumber(strOpen1_volume)
							+ ""
							+ FunctionSymbol.setBracket(strOpen1));
					tv_open1_volume.setTextColor(getResources().getColor(
							FunctionSetBg
									.setStrColorWriteDetailSuccess(strOpen1)));
					tv_buy_volume.setText(FunctionSymbol
							.setFormatNumber(strBuy_volume));
					tv_buy_volume
							.setTextColor(getResources()
									.getColor(
											FunctionSetBg
													.setStrColorWriteDetailBlue(strBuy_volume)));

					tv_average_sell.setText(FunctionSymbol
							.checkNull(strAverage_sell));
					tv_average_sell
							.setTextColor(getResources()
									.getColor(
											FunctionSetBg
													.setStrColorWriteDetailDanger(strAverage_sell)));
					tv_max_sell_price_volume.setText(FunctionSymbol
							.setFormatNumber(strMax_sell_price_volume)
							+ ""
							+ FunctionSymbol.setBracket(strMax_sell_price));
					tv_max_sell_price_volume
							.setTextColor(getResources()
									.getColor(
											FunctionSetBg
													.setStrColorWriteDetailDanger(strMax_sell_price_volume)));
					tv_close_volume.setText(FunctionSetBg
							.setStrDetailList(strClose_volume));

					tv_sell_volume.setText(FunctionSymbol
							.setFormatNumber(strSell_volume));
					tv_sell_volume
							.setTextColor(getResources()
									.getColor(
											FunctionSetBg
													.setStrColorWriteDetailBlue(strSell_volume)));

					// percentBuy , sell
					tv_percentBuy.setText("BUY ("
							+ FunctionSymbol.setFormatNumber(strPercentBuy)
							+ "%)");
					tv_percentSell.setText("("
							+ FunctionSymbol.setFormatNumber(strPercentSell)
							+ "%) SELL");

					float vBuy = Float.parseFloat(strPercentBuy.replaceAll(",",
							""));
					float vSell = Float.parseFloat(strPercentSell.replaceAll(
							",", ""));
					LinearLayout.LayoutParams loparams = (LinearLayout.LayoutParams) v_percentBuy
							.getLayoutParams();
					loparams.weight = vBuy;
					v_percentBuy.setLayoutParams(loparams);

					LinearLayout.LayoutParams loparams2 = (LinearLayout.LayoutParams) v_percentSell
							.getLayoutParams();
					loparams2.weight = vSell;
					v_percentSell.setLayoutParams(loparams2);

					// ------ update symbol -------
					if (SplashScreen.contentGetUserById != null) {
						if (!(SplashScreen.contentGetUserById
								.getString("package").equals("free"))) {

							// ---- set status connect
							ImageView img_connect_c = (ImageView) findViewById(R.id.img_connect_c);
							img_connect_c
									.setBackgroundResource(R.drawable.icon_connect_c_green);

							startUpdateSymbol();
						}
					}
				}
			} else {
				Toast.makeText(getApplicationContext(), "No data.", 0).show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dialogLoading.dismiss();
	}

	// ============== send getupdate symbol ===============
	// Timer timerUpdateSymbol;

	public void startUpdateSymbol() {
		FragmentChangeActivity.timerUpdateSymbol = new Timer();
		FragmentChangeActivity.timerUpdateSymbol.schedule(new TimerTask() {
			@Override
			public void run() {
				Log.v("startUpdateSymbol", "run detail");

				FragmentChangeActivity.timerUpdateSymbolStatus = true;
				getUpdateSymbol();
			}
		}, 0, FragmentChangeActivity.timerUpdateSymbolLength);
	}

	String resultUpdateSymbol = "";

	public void getUpdateSymbol() {
		setUpdateSymbol resp = new setUpdateSymbol();
		resp.execute();
	}

	public class setUpdateSymbol extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;
		// ======= json ========
		private JSONObject jsonGetUpdateSymbol;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progress.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// ======= url ========
			String url_GetWatchlistSymbol = SplashScreen.url_bidschart
					+ "/service/v2/getUpdateBySymbol?symbol="
					+ FragmentChangeActivity.strSymbolSelect;

			// Log.v("url_GetWatchlistSymbol update", url_GetWatchlistSymbol);

			try {
				// ======= Ui Home ========
				jsonGetUpdateSymbol = ReadJson
						.readJsonObjectFromUrl(url_GetWatchlistSymbol);

			} catch (IOException e1) {
				connectionError = true;
				jsonGetUpdateSymbol = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonGetUpdateSymbol = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonGetUpdateSymbol = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {

				if (jsonGetUpdateSymbol != null) {
					try {
						// get content
						contentGetDetailUpdate = jsonGetUpdateSymbol
								.getJSONObject("dataAll");
						updateRowWatchlist();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
				}
			} else {
			}
		}
	}

	// ============== update watchlist symbol ===============
	public void updateRowWatchlist() {
		try {
			if (contentGetDetailUpdate != null) {

				// ======= set data ========
				// main
				String strSymbol_name = contentGetDetail
						.getString("symbol_name");
				String strLast_trade = contentGetDetail.getString("last_trade");

				String strLast_tradeUpdate = contentGetDetailUpdate
						.getString("last_trade");

				// String turnover_list_level = contentGetDetail
				// .getString("turnover_list_level");
				// String status = contentGetDetail.getString("status");
				// String status_xd = contentGetDetail.getString("status_xd");
				// tv_symbol.setText(Html.fromHtml(FunctionSymbol
				// .checkStatusSymbol(strSymbol_name, turnover_list_level,
				// status, status_xd)));

				// ---- เปียบเทียบ ถ้าไม่เท่ากันค่อย set ค่า
				if (!strLast_trade.equals(strLast_tradeUpdate)) {
					Log.v("strLast_trade", strSymbol_name + "_" + strLast_trade
							+ "_" + strLast_tradeUpdate);
					tv_last_trade.setText(FunctionSymbol
							.setFormatNumber(strLast_tradeUpdate));
					if (strLast_tradeUpdate != "") {
						tv_last_trade.setTextColor(getResources().getColor(
								FunctionSetBg
										.setColor(strLast_tradeUpdate)));
					}
				}

				// main color
				String strColor = contentGetDetail.getString("change");
				String strColorUpdate = contentGetDetailUpdate
						.getString("change");

				// ck ltrade change
				String strChange = contentGetDetail.getString("change");
				String strPercentChange = contentGetDetail
						.getString("percentChange");

				String strChangeUpdate = contentGetDetailUpdate
						.getString("change");
				String strPercentChangeUpdate = contentGetDetailUpdate
						.getString("percentChange");

				// tv_change.setText(strChange);

				if ((!strPercentChange.equals(strPercentChangeUpdate))
						|| (!strChange.equals(strChangeUpdate))) {
					Log.v("strPercentChange", strSymbol_name + "_"
							+ strPercentChange + "_" + strPercentChangeUpdate);
					if ((strPercentChangeUpdate == "0")
							|| (strPercentChangeUpdate == "")
							|| (strPercentChangeUpdate == "0.00")) {
						tv_percenchange.setText("0.00");
					} else {
						tv_percenchange
								.setText(FunctionSymbol
										.setFormatNumber(strChangeUpdate)
										+ "("
										+ FunctionSymbol
												.setFormatNumber(strPercentChangeUpdate)
										+ "%)");
					}
					if (strChangeUpdate != "") {
						tv_percenchange.setTextColor(getResources().getColor(
								FunctionSetBg.setColor(strChangeUpdate)));
						// img_updown.setBackgroundResource(BgColorSymbolDetail
						// .setImgUpDown(strChangeUpdate));
					}
				}

				// column 1
				String strVolume = contentGetDetail.getString("volume");
				String strValue = contentGetDetail.getString("value");
				String strOpen = contentGetDetail.getString("open");
				String strHigh = contentGetDetail.getString("high");
				String strLow = contentGetDetail.getString("low");

				String strVolumeUpdate = contentGetDetailUpdate
						.getString("volume");
				String strValueUpdate = contentGetDetailUpdate
						.getString("value");
				String strOpenUpdate = contentGetDetailUpdate.getString("open");
				String strHighUpdate = contentGetDetailUpdate.getString("high");
				String strLowUpdate = contentGetDetailUpdate.getString("low");

				if (!strVolume.equals(strVolumeUpdate)) {
					Log.v("strVolume", strSymbol_name + "_" + strVolume + "_"
							+ strVolumeUpdate);
					tv_volume.setText(strVolumeUpdate);
				}
				if (!strValue.equals(strValueUpdate)) {
					Log.v("strValue", strSymbol_name + "_" + strValue + "_"
							+ strValueUpdate);
					tv_value.setText(strValueUpdate);
				}
				if (!strOpen.equals(strOpenUpdate)) {
					Log.v("strOpen", strSymbol_name + "_" + strOpen + "_"
							+ strOpenUpdate);
					tv_open.setText(FunctionSymbol
							.setFormatNumber(strOpenUpdate));
				}
				if (!strHigh.equals(strHighUpdate)) {
					Log.v("strHigh", strSymbol_name + "_" + strHigh + "_"
							+ strHighUpdate);
					tv_high.setText(FunctionSymbol
							.setFormatNumber(strHighUpdate));
				}
				if (!strLow.equals(strLowUpdate)) {
					Log.v("strLow", strSymbol_name + "_" + strLow + "_"
							+ strLowUpdate);
					tv_low.setText(FunctionSymbol.setFormatNumber(strLowUpdate));
				}

				// column 2
				String strHigh52W = contentGetDetail.getString("high52W");
				String strLow52W = contentGetDetail.getString("low52W");

				String strHigh52WUpdate = contentGetDetailUpdate
						.getString("high52W");
				String strLow52WUpdate = contentGetDetailUpdate
						.getString("low52W");

				tv_high52W.setText(FunctionSymbol
						.setFormatNumber(strHigh52WUpdate));
				tv_low52W.setText(FunctionSymbol
						.setFormatNumber(strLow52WUpdate));

				if (!strHigh52W.equals(strHigh52WUpdate)) {
					Log.v("strHigh52W", strSymbol_name + "_" + strHigh52W + "_"
							+ strHigh52WUpdate);
					tv_high52W.setText(FunctionSymbol
							.setFormatNumber(strHigh52WUpdate));
				}
				if (!strLow52W.equals(strLow52WUpdate)) {
					Log.v("strLow52W", strSymbol_name + "_" + strLow52W + "_"
							+ strLow52WUpdate);
					tv_low52W.setText(FunctionSymbol
							.setFormatNumber(strLow52WUpdate));
				}

				// show hide
				String strSymbN = contentGetDetail.getString("symbol_name");
				String strSymbDot = strSymbN.substring(1, 2);
				if (strSymbDot.equals(".")) {

				} else {
					// ---------------------
					String strAverage_buy = contentGetDetail
							.getString("average_buy");
					String strMax_buy_price_volume = contentGetDetail
							.getString("max_buy_price_volume");
					String strMax_buy_price = contentGetDetail
							.getString("max_buy_price");
					String strOpen1_volume = contentGetDetail
							.getString("open1_volume");
					String strOpen1 = contentGetDetail.getString("open1");
					String strBuy_volume = contentGetDetail
							.getString("buy_volume");

					String strAverage_buyUpdate = contentGetDetailUpdate
							.getString("average_buy");
					String strMax_buy_price_volumeUpdate = contentGetDetailUpdate
							.getString("max_buy_price_volume");
					String strOpen1_volumeUpdate = contentGetDetailUpdate
							.getString("open1_volume");
					String strBuy_volumeUpdate = contentGetDetailUpdate
							.getString("buy_volume");

					if (!strAverage_buy.equals(strAverage_buyUpdate)) {
						Log.v("strAverage_buy", strSymbol_name + "_"
								+ strAverage_buy + "_" + strAverage_buyUpdate);
						tv_average_buy.setText(FunctionSymbol
								.checkNull(strAverage_buyUpdate));
					}
					if (!strMax_buy_price_volume
							.equals(strMax_buy_price_volumeUpdate)) {
						Log.v("strMax_buy_price_volume", strSymbol_name + "_"
								+ strMax_buy_price_volume + "_"
								+ strMax_buy_price_volumeUpdate);
						tv_max_buy_price_volume.setText(FunctionSymbol
								.setFormatNumber(strMax_buy_price_volumeUpdate)
								+ ""
								+ FunctionSymbol.setBracket(strMax_buy_price));
					}
					if (!strOpen1_volume.equals(strOpen1_volumeUpdate)) {
						Log.v("strOpen1_volume", strSymbol_name + "_"
								+ strOpen1_volume + "_" + strOpen1_volumeUpdate);
						tv_open1_volume.setText(FunctionSymbol
								.setFormatNumber(strOpen1_volumeUpdate)
								+ ""
								+ FunctionSymbol.setBracket(strOpen1));
					}
					if (!strBuy_volume.equals(strBuy_volumeUpdate)) {
						Log.v("strBuy_volume", strSymbol_name + "_"
								+ strBuy_volume + "_" + strBuy_volumeUpdate);
						tv_buy_volume.setText(FunctionSymbol
								.setFormatNumber(strBuy_volumeUpdate));
					}

					// ---------------------
					String strAverage_sell = contentGetDetail
							.getString("average_sell");
					String strMax_sell_price_volume = contentGetDetail
							.getString("max_sell_price_volume");
					String strClose_volume = contentGetDetail
							.getString("close_volume");
					String strSell_volume = contentGetDetail
							.getString("sell_volume");

					String strAverage_sellUpdate = contentGetDetailUpdate
							.getString("average_sell");
					String strMax_sell_price_volumeUpdate = contentGetDetailUpdate
							.getString("max_sell_price_volume");
					String strMax_sell_price = contentGetDetail
							.getString("max_sell_price");
					String strClose_volumeUpdate = contentGetDetailUpdate
							.getString("close_volume");
					String strSell_volumeUpdate = contentGetDetailUpdate
							.getString("sell_volume");

					if (!strAverage_sell.equals(strAverage_sellUpdate)) {
						Log.v("strAverage_sell", strSymbol_name + "_"
								+ strAverage_sell + "_" + strAverage_sellUpdate);
						tv_average_sell.setText(FunctionSymbol
								.checkNull(strAverage_sellUpdate));
					}
					if (!strMax_sell_price_volume
							.equals(strMax_sell_price_volumeUpdate)) {
						Log.v("strMax_sell_price_volume", strSymbol_name + "_"
								+ strMax_sell_price_volume + "_"
								+ strMax_sell_price_volumeUpdate);
						tv_max_sell_price_volume
								.setText(FunctionSymbol
										.setFormatNumber(strMax_sell_price_volumeUpdate)
										+ ""
										+ FunctionSymbol
												.setBracket(strMax_sell_price));
					}
					if (!strClose_volume.equals(strClose_volumeUpdate)) {
						Log.v("strClose_volume", strSymbol_name + "_"
								+ strClose_volume + "_" + strClose_volumeUpdate);
						tv_close_volume.setText(FunctionSetBg
								.setStrDetailList(strClose_volumeUpdate));
					}
					if (!strSell_volume.equals(strSell_volumeUpdate)) {
						Log.v("strSell_volume", strSymbol_name + "_"
								+ strSell_volume + "_" + strSell_volumeUpdate);
						tv_sell_volume.setText(FunctionSymbol
								.setFormatNumber(strSell_volumeUpdate));
					}

					// ---------------------
					String strPercentBuy = contentGetDetail
							.getString("percentBuy");
					String strPercentSell = contentGetDetail
							.getString("percentSell");

					String strPercentBuyUpdate = contentGetDetailUpdate
							.getString("percentBuy");
					String strPercentSellUpdate = contentGetDetailUpdate
							.getString("percentSell");

					// percentBuy , sell
					if (!strPercentBuy.equals(strPercentBuyUpdate)) {
						tv_percentBuy.setText("BUY ("
								+ FunctionSymbol
										.setFormatNumber(strPercentBuyUpdate)
								+ "%)");
					}
					if (!strPercentSell.equals(strPercentSellUpdate)) {
						tv_percentSell.setText("("
								+ FunctionSymbol
										.setFormatNumber(strPercentSellUpdate)
								+ "%) SELL");
					}

					float vBuy = Float.parseFloat(strPercentBuyUpdate
							.replaceAll(",", ""));
					float vSell = Float.parseFloat(strPercentSellUpdate
							.replaceAll(",", ""));
					Log.v("buy sell", "" + vBuy + "_" + vSell);
					LinearLayout.LayoutParams loparams = (LinearLayout.LayoutParams) v_percentBuy
							.getLayoutParams();
					loparams.weight = vBuy;
					v_percentBuy.setLayoutParams(loparams);

					LinearLayout.LayoutParams loparams2 = (LinearLayout.LayoutParams) v_percentSell
							.getLayoutParams();
					loparams2.weight = vSell;
					v_percentSell.setLayoutParams(loparams2);

					contentGetDetail = contentGetDetailUpdate;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// ============= dialog favorite =========
	@SuppressLint("NewApi")
	public static AlertDialog alertDialogFollow;

	private void dialogFavorite() {
		LayoutInflater layoutInflater = LayoutInflater.from(this);
		View dlView = layoutInflater.inflate(R.layout.dialog_favorite, null);
		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);
		alertDialogBuilder.setView(dlView);

		LinearLayout li_favorite1 = (LinearLayout) dlView
				.findViewById(R.id.li_favorite1);
		LinearLayout li_favorite2 = (LinearLayout) dlView
				.findViewById(R.id.li_favorite2);
		LinearLayout li_favorite3 = (LinearLayout) dlView
				.findViewById(R.id.li_favorite3);
		LinearLayout li_favorite4 = (LinearLayout) dlView
				.findViewById(R.id.li_favorite4);
		LinearLayout li_favorite5 = (LinearLayout) dlView
				.findViewById(R.id.li_favorite5);

		li_favorite1.setOnClickListener(onClickListenerFavorite);
		li_favorite2.setOnClickListener(onClickListenerFavorite);
		li_favorite3.setOnClickListener(onClickListenerFavorite);
		li_favorite4.setOnClickListener(onClickListenerFavorite);
		li_favorite5.setOnClickListener(onClickListenerFavorite);

		// alertDialogBuilder.setNegativeButton("ยกเลิก",
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int id) {
		// dialog.cancel();
		// }
		// });

		// create an alert dialog
		alertDialogFollow = alertDialogBuilder.create();
		alertDialogFollow.show();

		// android.util.AndroidRuntimeException: requestFeature() must be called
		// before adding content
		// alertDialogFollow.requestWindowFeature(Window.FEATURE_NO_TITLE);

		alertDialogFollow.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

		WindowManager.LayoutParams wmlp = alertDialogFollow.getWindow()
				.getAttributes();

		wmlp.gravity = Gravity.TOP | Gravity.RIGHT;
		// wmlp.x = 100; // x position
		wmlp.y = 100; // y position
	}

	// ********** select favorite **********
	private OnClickListener onClickListenerFavorite = new OnClickListener() {
		@Override
		public void onClick(final View v) {
			switch (v.getId()) {
			case R.id.li_favorite1:
				FragmentChangeActivity.strFavoriteNumber = "1";
				checkAddFavorite();
				alertDialogFollow.dismiss();
				break;
			case R.id.li_favorite2:
				FragmentChangeActivity.strFavoriteNumber = "2";
				checkAddFavorite();
				alertDialogFollow.dismiss();
				break;
			case R.id.li_favorite3:
				FragmentChangeActivity.strFavoriteNumber = "3";
				checkAddFavorite();
				alertDialogFollow.dismiss();
				break;
			case R.id.li_favorite4:
				FragmentChangeActivity.strFavoriteNumber = "4";
				checkAddFavorite();
				alertDialogFollow.dismiss();
				break;
			case R.id.li_favorite5:
				FragmentChangeActivity.strFavoriteNumber = "5";
				checkAddFavorite();
				alertDialogFollow.dismiss();
				break;
			default:
				break;
			}
		}
	};

	// ============== send addfavorite ===============
	public void checkAddFavorite() {
		if (FunctionSymbol
				.checkFollowCount(FragmentChangeActivity.strFavoriteNumber)) {
			sendAddFavorite();
		} else {
			Toast.makeText(
					getApplicationContext(),
					"Over Limit Favorite "
							+ FragmentChangeActivity.strFavoriteNumber, 0)
					.show();
		}
	}

	public void sendAddFavorite() {
		setFavorite resp = new setFavorite();
		resp.execute();
	}

	public class setFavorite extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		String temp = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialogLoading.show();

		}

		@Override
		protected Void doInBackground(Void... params) {

			String url = SplashScreen.url_bidschart + "/service/addFavorite";

			String json = "";
			InputStream inputStream = null;
			String result = "";

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("favorite_number",
						FragmentChangeActivity.strFavoriteNumber);
				jsonObject.accumulate("favorite_symbol",
						FragmentChangeActivity.strSymbolSelect);
				jsonObject
						.accumulate("user_id", SplashScreen.userModel.user_id);

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
					result = AFunctionOther.convertInputStreamToString(inputStream);
				else
					result = "Did not work!";
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

			updateDataFavorite();

			// switchFragment(new PagerWatchlistDetail());
		}
	}

	// ============= Pager PreMium ===========
	private void initPagerPreMium() {
		try {
			strIndustry = UiWatchlistDetail_old.contentGetDetail
					.getString("industry");
		} catch (JSONException e) {
			e.printStackTrace();
		}

		initTabPagerPreMium(); // init tab pager

		// creating fragments and adding to list
		fragmentMainPreMium.removeAll(fragmentMainPreMium);
		fragmentMainPreMium.add(Fragment.instantiate(getApplicationContext(),
				PagerWatchListDetailChart_old.class.getName()));
		fragmentMainPreMium.add(Fragment.instantiate(getApplicationContext(),
				PagerWatchListDetailIndustry.class.getName()));
		fragmentMainPreMium.add(Fragment.instantiate(getApplicationContext(),
				PagerWatchListDetailFundamental.class.getName()));
		fragmentMainPreMium.add(Fragment.instantiate(getApplicationContext(),
				PagerWatchListDetailNews.class.getName()));
		fragmentMainPreMium.add(Fragment.instantiate(getApplicationContext(),
				PagerWatchListDetailHit.class.getName()));

		// creating adapter and linking to view pager
		this.mPagerAdapterMainPreMium = new PagerAdapter(
				super.getSupportFragmentManager(), fragmentMainPreMium);
		mPagerMainPreMium = (ViewPager) findViewById(R.id.vp_pager);

		mPagerMainPreMium.setAdapter(this.mPagerAdapterMainPreMium);

		mPagerMainPreMium.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				// LOTBANK = (mPagerMain.getCurrentItem()) + 1;
				// selectTabPagerPreMium((mPagerMainPreMium.getCurrentItem()) +
				// 1);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				mPagerMainPreMium.getParent()
						.requestDisallowInterceptTouchEvent(true);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		// --------- set params pager ------
		ScrollView sv_data = (ScrollView) findViewById(R.id.sv_data);
		int sv_width = sv_data.getWidth();
		int sv_height = sv_data.getHeight();

		li_menu_premium = (LinearLayout) findViewById(R.id.li_menu_premium);
		int li_height_menu_premium = li_menu_premium.getHeight();

		LinearLayout li_data_all = (LinearLayout) findViewById(R.id.li_data_all);
		int li_height_showdataall = li_data_all.getHeight();

		// li_data_all
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				sv_width,
				(sv_height - (li_height_menu_premium + li_height_showdataall)));
		mPagerMainPreMium.setLayoutParams(layoutParams);

	}

	// ============= set tab ===========
	public int selectTabPagerPreMium(int numtab) {
		tv_premium_chart.setBackgroundColor(Color.TRANSPARENT);
		tv_premium_industry.setBackgroundColor(Color.TRANSPARENT);
		tv_premium_fundamental.setBackgroundColor(Color.TRANSPARENT);
		tv_premium_news.setBackgroundColor(Color.TRANSPARENT);
		tv_premium_hit.setBackgroundColor(Color.TRANSPARENT);

		tv_premium_chart.setTextColor(getResources()
				.getColor(R.color.c_content));
		tv_premium_industry.setTextColor(getResources().getColor(
				R.color.c_content));
		tv_premium_fundamental.setTextColor(getResources().getColor(
				R.color.c_content));
		tv_premium_news
				.setTextColor(getResources().getColor(R.color.c_content));
		tv_premium_hit.setTextColor(getResources().getColor(R.color.c_content));

		if (numtab == 1) {
			tv_premium_chart
					.setBackgroundResource(R.drawable.border_button_activeleft);
			tv_premium_chart.setTextColor(getResources().getColor(
					R.color.bg_default));
		} else if (numtab == 2) {
			tv_premium_industry
					.setBackgroundResource(R.drawable.border_button_activecenter);
			tv_premium_industry.setTextColor(getResources().getColor(
					R.color.bg_default));
		} else if (numtab == 3) {
			tv_premium_fundamental
					.setBackgroundResource(R.drawable.border_button_activecenter);
			tv_premium_fundamental.setTextColor(getResources().getColor(
					R.color.bg_default));
		} else if (numtab == 4) {
			tv_premium_news
					.setBackgroundResource(R.drawable.border_button_activecenter);
			tv_premium_news.setTextColor(getResources().getColor(
					R.color.bg_default));
		} else if (numtab == 5) {
			tv_premium_hit
					.setBackgroundResource(R.drawable.border_button_activeright);
			tv_premium_hit.setTextColor(getResources().getColor(
					R.color.bg_default));
		}
		return numtab;
	}

	// ============= set tab ===========
	public int selectTabPager(int numtab) {
		tv_chart.setBackgroundColor(Color.TRANSPARENT);
		tv_news.setBackgroundColor(Color.TRANSPARENT);
		tv_sector.setBackgroundColor(Color.TRANSPARENT);

		tv_chart.setTextColor(getResources().getColor(R.color.c_content));
		tv_news.setTextColor(getResources().getColor(R.color.c_content));
		tv_sector.setTextColor(getResources().getColor(R.color.c_content));

		if (numtab == 1) {
			tv_chart.setBackgroundResource(R.drawable.border_button_activeleft);
			tv_chart.setTextColor(getResources().getColor(R.color.bg_default));
		} else if (numtab == 2) {
			tv_sector
					.setBackgroundResource(R.drawable.border_button_activecenter);
			tv_sector.setTextColor(getResources().getColor(R.color.bg_default));
		} else if (numtab == 3) {
			tv_news.setBackgroundResource(R.drawable.border_button_activeright);
			tv_news.setTextColor(getResources().getColor(R.color.bg_default));
		}

		return numtab;
	}

	// ============== send removefavorite ===============
	public static String strRemoveId = "";

	public void sendRemoveFavorite() {

		setRemoveFavorite resp = new setRemoveFavorite();
		resp.execute();
	}

	public class setRemoveFavorite extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		String temp = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progress.show();

		}

		@Override
		protected Void doInBackground(Void... params) {

			// getFavoriteByUserIdFavoriteNumber // หา get favorite id

			String url = SplashScreen.url_bidschart + "/service/removeFavorite";

			String json = "";
			InputStream inputStream = null;
			String result = "";

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();

				jsonObject.accumulate("favorite_id", strRemoveId);

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
					result = AFunctionOther.convertInputStreamToString(inputStream);
				else
					result = "Did not work!";

				// Log.v("result remove fav : ", ""+result);

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

			updateDataFavorite();
			// switchFragment(new PagerWatchlistDetail());
		}
	}

	// ============== get favorite id =============
	public static JSONArray contentGetFavoriteId = null;

	private void getDataFavoriteId() {
		getFavoriteId resp = new getFavoriteId();
		resp.execute();
	}

	public class getFavoriteId extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;
		// ======= json ========
		private JSONObject jsonFavId;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// ======= url ========
			String url_fav;
			url_fav = SplashScreen.url_bidschart
					+ "/service/getFavoriteByUserIdFavoriteNumber?user_id="
					+ SplashScreen.userModel.user_id + "&favorite_number="
					+ FragmentChangeActivity.strFavoriteNumber + "&timestamp="
					+ timestamp;

			try {
				jsonFavId = ReadJson.readJsonObjectFromUrl(url_fav);
			} catch (IOException e1) {
				connectionError = true;
				jsonFavId = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonFavId = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonFavId = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (jsonFavId != null) {
					try {
						contentGetFavoriteId = jsonFavId
								.getJSONArray("dataAll");

						for (int i = 0; i < contentGetFavoriteId.length(); i++) {
							JSONObject jso = contentGetFavoriteId
									.getJSONObject(i);
							if (jso.getString("symbol_name").equals(
									FragmentChangeActivity.strSymbolSelect)) {
								strRemoveId = jso.getString("id");
								// strRemoveId = jso.getString("orderbook_id");
								sendRemoveFavorite();
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}

			} else {
			}
		}
	}

	// ============== update favorite =============
	public void updateDataFavorite() {
		getFavorite resp = new getFavorite();
		resp.execute();
	}

	public class getFavorite extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;
		// ======= json ========
		private JSONObject jsonFav;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progress.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// ======= url ========
			// http://www.bidschart.com/service/v2/symbolFavorite?user_id=104
			String url_fav = SplashScreen.url_bidschart
					+ "/service/v2/symbolFavorite?user_id="
					+ SplashScreen.userModel.user_id + "&timestamp="
					+ timestamp;

			Log.v("url_fav update", "" + url_fav);

			try {
				jsonFav = ReadJson.readJsonObjectFromUrl(url_fav);
			} catch (IOException e1) {
				connectionError = true;
				jsonFav = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonFav = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonFav = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (jsonFav != null) {
					try {
						FragmentChangeActivity.contentGetSymbolFavorite = jsonFav
								.getJSONArray("dataAll");

						setFollowSymbol();

					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			} else {
			}
			dialogLoading.dismiss();
		}
	}

	// ======= WebView =========
	// config rotation
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Checks the orientation of the screen
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// แนวนอน
			Log.v("onConfigurationChan >> ", "แนวนอน");
			rl_chartiq.setVisibility(View.VISIBLE);
			li_vertical.setVisibility(View.GONE);
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			// แนวตั้ง
			Log.v("onConfigurationChan >> ", "แนวตั้ง");
			rl_chartiq.setVisibility(View.GONE);
			li_vertical.setVisibility(View.VISIBLE);
		}

	}

	// ====== init ChartIq ============
	private WebView wv_chart_iq;
	String wv_url = SplashScreen.url_bidschart + "/iq/stx-mobile-3.html";

	private Double chartIqScreenHeight = 0.95;
	private Double chartIqScreenWidth = 0.7;

	public void initChartIq() {
		wv_url = SplashScreen.url_bidschart + "/iq/stx-mobile-3.html#"
				+ FragmentChangeActivity.strSymbolSelect;

		initReadFileIndicator();
		initMenu(); // init menu
		initWebView();
	}

	// ====== init Menu ============
	TextView tv_ciq_symbol, tv_ciq_symbol_name_eng, tv_ciq_last_trade,
			tv_ciq_percenchange;

	TextView tv_ciq_1d, tv_ciq_1w, tv_ciq_1min, tv_ciq_5min, tv_ciq_15min,
			tv_ciq_30min, tv_ciq_60min, tv_ciq_120min;

	LinearLayout li_ciq_1view, li_ciq_2view, li_ciq_3view;

	TextView tv_ciq_style, tv_ciq_indicator, tv_ciq_compare, tv_ciq_draw;

	LinearLayout li_ciq_showview;

	public void initMenu() {
		tv_ciq_symbol = (TextView) findViewById(R.id.tv_ciq_symbol);
		tv_ciq_symbol_name_eng = (TextView) findViewById(R.id.tv_ciq_symbol_name_eng);
		tv_ciq_last_trade = (TextView) findViewById(R.id.tv_ciq_last_trade);
		tv_ciq_percenchange = (TextView) findViewById(R.id.tv_ciq_percenchange);

		li_ciq_showview = (LinearLayout) findViewById(R.id.li_ciq_showview);

		try {
			if (SplashScreen.contentGetUserById != null) {
				if (!(SplashScreen.contentGetUserById.getString("package")
						.equals("free"))) {
					// li_ciq_showview.setVisibility(View.VISIBLE);
				} else {
					// li_ciq_showview.setVisibility(View.GONE);
				}
			} else {
				// li_ciq_showview.setVisibility(View.GONE);
			}

			tv_ciq_symbol.setText(contentGetDetail.getString("symbol_name"));
			tv_ciq_symbol_name_eng.setText(contentGetDetail
					.getString("symbol_fullname_eng"));
			tv_ciq_last_trade.setText(contentGetDetail.getString("last_trade"));

			String strColor = contentGetDetail.getString("change");

			// ck ltrade change
			String strLastTrade = contentGetDetail.getString("last_trade");
			String strChange = contentGetDetail.getString("change");
			String strPercentChange = contentGetDetail
					.getString("percentChange");

			// tv_change.setText(strChange);
			if ((strPercentChange == "0") || (strPercentChange == "")
					|| (strPercentChange == "0.00")) {
				tv_ciq_percenchange.setText("0.00");
			} else {
				// tv_ciq_percenchange.setText(strChange + "("
				// + strPercentChange + "%)");
				tv_ciq_percenchange.setText(FunctionSymbol
						.setFormatNumber(strChange)
						+ "("
						+ FunctionSymbol.setFormatNumber(strPercentChange)
						+ "%)");
			}

			if (strLastTrade != "") {
				tv_ciq_last_trade.setTextColor(getResources().getColor(
						FunctionSetBg.setColor(strLastTrade)));
			}
			if (strChange != "") {
				tv_ciq_percenchange.setTextColor(getResources().getColor(
						FunctionSetBg.setColor(strChange)));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// ----------------------
		tv_ciq_1d = (TextView) findViewById(R.id.tv_ciq_1d);
		tv_ciq_1w = (TextView) findViewById(R.id.tv_ciq_1w);
		tv_ciq_1min = (TextView) findViewById(R.id.tv_ciq_1min);
		tv_ciq_5min = (TextView) findViewById(R.id.tv_ciq_5min);
		tv_ciq_15min = (TextView) findViewById(R.id.tv_ciq_15min);
		tv_ciq_30min = (TextView) findViewById(R.id.tv_ciq_30min);
		tv_ciq_60min = (TextView) findViewById(R.id.tv_ciq_60min);
		tv_ciq_120min = (TextView) findViewById(R.id.tv_ciq_120min);
		tv_ciq_1d.setOnClickListener(onClickListenerChartPeriod);
		tv_ciq_1w.setOnClickListener(onClickListenerChartPeriod);
		tv_ciq_1min.setOnClickListener(onClickListenerChartPeriod);
		tv_ciq_5min.setOnClickListener(onClickListenerChartPeriod);
		tv_ciq_15min.setOnClickListener(onClickListenerChartPeriod);
		tv_ciq_30min.setOnClickListener(onClickListenerChartPeriod);
		tv_ciq_60min.setOnClickListener(onClickListenerChartPeriod);
		tv_ciq_120min.setOnClickListener(onClickListenerChartPeriod);

		// ----------------------
		li_ciq_1view = (LinearLayout) findViewById(R.id.li_ciq_1view);
		li_ciq_2view = (LinearLayout) findViewById(R.id.li_ciq_2view);
		li_ciq_3view = (LinearLayout) findViewById(R.id.li_ciq_3view);
		li_ciq_1view.setOnClickListener(onClickListenerChartView);
		li_ciq_2view.setOnClickListener(onClickListenerChartView);
		li_ciq_3view.setOnClickListener(onClickListenerChartView);

		// ----------------------
		tv_ciq_style = (TextView) findViewById(R.id.tv_ciq_style);
		tv_ciq_indicator = (TextView) findViewById(R.id.tv_ciq_indicator);
		tv_ciq_compare = (TextView) findViewById(R.id.tv_ciq_compare);
		tv_ciq_draw = (TextView) findViewById(R.id.tv_ciq_draw);
		tv_ciq_style.setOnClickListener(onClickListenerChartMenu);
		tv_ciq_indicator.setOnClickListener(onClickListenerChartMenu);
		tv_ciq_compare.setOnClickListener(onClickListenerChartMenu);
		tv_ciq_draw.setOnClickListener(onClickListenerChartMenu);

	}

	// ========== period ==============
	String arrChartPeroid[] = { "day", "week", "1", "3", "5", "15", "30", "60",
			"120" };
	private OnClickListener onClickListenerChartPeriod = new OnClickListener() {
		@Override
		public void onClick(final View v) {

			tv_ciq_1d.setBackgroundColor(Color.TRANSPARENT);
			tv_ciq_1w.setBackgroundColor(Color.TRANSPARENT);
			tv_ciq_1min.setBackgroundColor(Color.TRANSPARENT);
			tv_ciq_5min.setBackgroundColor(Color.TRANSPARENT);
			tv_ciq_15min.setBackgroundColor(Color.TRANSPARENT);
			tv_ciq_30min.setBackgroundColor(Color.TRANSPARENT);
			tv_ciq_60min.setBackgroundColor(Color.TRANSPARENT);
			tv_ciq_120min.setBackgroundColor(Color.TRANSPARENT);

			tv_ciq_1d.setTextColor(getResources().getColor(
					R.color.chartiq_default));
			tv_ciq_1w.setTextColor(getResources().getColor(
					R.color.chartiq_default));
			tv_ciq_1min.setTextColor(getResources().getColor(
					R.color.chartiq_default));
			tv_ciq_5min.setTextColor(getResources().getColor(
					R.color.chartiq_default));
			tv_ciq_15min.setTextColor(getResources().getColor(
					R.color.chartiq_default));
			tv_ciq_30min.setTextColor(getResources().getColor(
					R.color.chartiq_default));
			tv_ciq_60min.setTextColor(getResources().getColor(
					R.color.chartiq_default));
			tv_ciq_120min.setTextColor(getResources().getColor(
					R.color.chartiq_default));

			switch (v.getId()) {
			case R.id.tv_ciq_1d:
				tv_ciq_1d.setBackgroundResource(R.drawable.chartiq_activeleft);
				tv_ciq_1d.setTextColor(getResources().getColor(
						R.color.bg_default));
				wv_chart_iq.loadUrl("javascript:(function () { "
						+ "mobileControl.changePeriod('" + arrChartPeroid[0]
						+ "');" + "})()");
				break;
			case R.id.tv_ciq_1w:
				tv_ciq_1w
						.setBackgroundResource(R.drawable.chartiq_activecenter);
				tv_ciq_1w.setTextColor(getResources().getColor(
						R.color.bg_default));
				wv_chart_iq.loadUrl("javascript:(function () { "
						+ "mobileControl.changePeriod('" + arrChartPeroid[1]
						+ "');" + "})()");
				break;
			case R.id.tv_ciq_1min:
				tv_ciq_1min
						.setBackgroundResource(R.drawable.chartiq_activecenter);
				tv_ciq_1min.setTextColor(getResources().getColor(
						R.color.bg_default));
				wv_chart_iq.loadUrl("javascript:(function () { "
						+ "mobileControl.changePeriod(" + arrChartPeroid[2]
						+ ");" + "})()");
				break;
			case R.id.tv_ciq_5min:
				tv_ciq_5min
						.setBackgroundResource(R.drawable.chartiq_activecenter);
				tv_ciq_5min.setTextColor(getResources().getColor(
						R.color.bg_default));
				wv_chart_iq.loadUrl("javascript:(function () { "
						+ "mobileControl.changePeriod(" + arrChartPeroid[4]
						+ ");" + "})()");
				break;
			case R.id.tv_ciq_15min:
				tv_ciq_15min
						.setBackgroundResource(R.drawable.chartiq_activecenter);
				tv_ciq_15min.setTextColor(getResources().getColor(
						R.color.bg_default));
				wv_chart_iq.loadUrl("javascript:(function () { "
						+ "mobileControl.changePeriod(" + arrChartPeroid[5]
						+ ");" + "})()");
				break;
			case R.id.tv_ciq_30min:
				tv_ciq_30min
						.setBackgroundResource(R.drawable.chartiq_activecenter);
				tv_ciq_30min.setTextColor(getResources().getColor(
						R.color.bg_default));
				wv_chart_iq.loadUrl("javascript:(function () { "
						+ "mobileControl.changePeriod(" + arrChartPeroid[6]
						+ ");" + "})()");
				break;
			case R.id.tv_ciq_60min:
				tv_ciq_60min
						.setBackgroundResource(R.drawable.chartiq_activecenter);
				tv_ciq_60min.setTextColor(getResources().getColor(
						R.color.bg_default));
				wv_chart_iq.loadUrl("javascript:(function () { "
						+ "mobileControl.changePeriod(" + arrChartPeroid[7]
						+ ");" + "})()");
				break;
			case R.id.tv_ciq_120min:
				tv_ciq_120min
						.setBackgroundResource(R.drawable.chartiq_activeright);
				tv_ciq_120min.setTextColor(getResources().getColor(
						R.color.bg_default));
				wv_chart_iq.loadUrl("javascript:(function () { "
						+ "mobileControl.changePeriod(" + arrChartPeroid[8]
						+ ");" + "})()");
				break;
			default:
				break;
			}
		}
	};

	// ========== chart view ==============
	String arrChartView[] = { "1", "2", "3" };
	private OnClickListener onClickListenerChartView = new OnClickListener() {
		@Override
		public void onClick(final View v) {

			li_ciq_1view.setBackgroundColor(Color.TRANSPARENT);
			li_ciq_2view.setBackgroundColor(Color.TRANSPARENT);
			li_ciq_3view.setBackgroundColor(Color.TRANSPARENT);

			switch (v.getId()) {
			case R.id.li_ciq_1view:
				li_ciq_1view
						.setBackgroundResource(R.drawable.chartiq_activeleft);
				wv_chart_iq.loadUrl("javascript:(function () { "
						+ "mobileControl.changeLayout(" + arrChartView[0]
						+ ");" + "})()");
				break;
			case R.id.li_ciq_2view:
				li_ciq_2view
						.setBackgroundResource(R.drawable.chartiq_activecenter);
				wv_chart_iq.loadUrl("javascript:(function () { "
						+ "mobileControl.changeLayout(" + arrChartView[1]
						+ ");" + "})()");
				break;
			case R.id.li_ciq_3view:
				li_ciq_3view
						.setBackgroundResource(R.drawable.chartiq_activeright);
				wv_chart_iq.loadUrl("javascript:(function () { "
						+ "mobileControl.changeLayout(" + arrChartView[2]
						+ ");" + "})()");
				break;
			default:
				break;
			}
		}
	};

	// ========== chart menu ==============
	private OnClickListener onClickListenerChartMenu = new OnClickListener() {
		@Override
		public void onClick(final View v) {

			tv_ciq_style.setBackgroundColor(Color.TRANSPARENT);
			tv_ciq_indicator.setBackgroundColor(Color.TRANSPARENT);
			tv_ciq_compare.setBackgroundColor(Color.TRANSPARENT);
			tv_ciq_draw.setBackgroundColor(Color.TRANSPARENT);

			tv_ciq_style.setTextColor(getResources().getColor(
					R.color.chartiq_default));
			tv_ciq_indicator.setTextColor(getResources().getColor(
					R.color.chartiq_default));
			tv_ciq_compare.setTextColor(getResources().getColor(
					R.color.chartiq_default));
			tv_ciq_draw.setTextColor(getResources().getColor(
					R.color.chartiq_default));

			switch (v.getId()) {
			case R.id.tv_ciq_style:
				tv_ciq_style
						.setBackgroundResource(R.drawable.chartiq_activeleft);
				tv_ciq_style.setTextColor(getResources().getColor(
						R.color.bg_default));
				showDialogChartIqStyle();
				break;
			case R.id.tv_ciq_indicator:
				tv_ciq_indicator
						.setBackgroundResource(R.drawable.chartiq_activecenter);
				tv_ciq_indicator.setTextColor(getResources().getColor(
						R.color.bg_default));
				showDialogChartIqIndicator();
				break;
			case R.id.tv_ciq_compare:
				tv_ciq_compare
						.setBackgroundResource(R.drawable.chartiq_activecenter);
				tv_ciq_compare.setTextColor(getResources().getColor(
						R.color.bg_default));
				showDialogChartIqCompare();
				break;
			case R.id.tv_ciq_draw:
				tv_ciq_draw
						.setBackgroundResource(R.drawable.chartiq_activeright);
				tv_ciq_draw.setTextColor(getResources().getColor(
						R.color.bg_default));
				showDialogChartIqDraw();
				break;
			default:
				break;
			}
		}
	};

	// ========= dialog compare ========
	Dialog dialogChartIqCompare; // color picke
	Dialog dialogChartIqCompareSearch; // search

	// private EditText dl_compare_et_search;
	private TextView dl_tv_compare_search_select, dl_tv_compare_done;
	private GridView gridViewColors;

	public void showDialogChartIqCompare() {

		LayoutInflater li = LayoutInflater.from(this);
		View viewDl = li.inflate(R.layout.dialog_chartiq_compare, null);

		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);
		alertDialogBuilder.setView(viewDl);

		gridViewColors = (GridView) viewDl.findViewById(R.id.gridViewColors);

		final TextView dl_tv_color = (TextView) viewDl
				.findViewById(R.id.dl_tv_color);

		dl_tv_compare_search_select = (TextView) viewDl
				.findViewById(R.id.dl_tv_compare_search);
		dl_tv_compare_done = (TextView) viewDl
				.findViewById(R.id.dl_tv_compare_done);
		dl_tv_compare_search_select.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialogChartIqCompareSearch(); // search symbol
			}
		});
		dl_tv_compare_done.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String strSymbolSearch = dl_tv_compare_search_select.getText()
						.toString().trim();
				String strColor = dl_tv_color.getTag().toString().trim();

				if ((!strSymbolSearch.equals("")) && (!strColor.equals(""))) {
					dialogChartIqCompare.dismiss();
					wv_chart_iq.loadUrl("javascript:(function () { "
							+ "mobileControl.addCompare('" + strSymbolSearch
							+ "','rgb(" + strColor + ")');" + "})()");
					dialogChartIqCompare.dismiss();
				} else {
				}
			}
		});

		gridViewColors.setAdapter(new ColorPickerAdapter(
				getApplicationContext()));

		// close the dialog on item click
		gridViewColors.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final int i = position;
				Integer intColor = ColorPickerAdapter.colorList.get(i);
				String hexColor = "#"
						+ Integer.toHexString(intColor).substring(2);

				int red = Color.red(intColor);
				int green = Color.green(intColor);
				int blue = Color.blue(intColor);

				String rgbColor = red + "," + green + "," + blue;

				dl_tv_color.setBackgroundColor(Color.parseColor(hexColor));
				dl_tv_color.setTag(rgbColor);

				// mobileControl.addCompare('ptt','rgb(%.0f,%.0f,%.0f)')

				// wv_chart_iq.loadUrl("javascript:(function () { "
				// + "mobileControl.addCompare('"
				// + FragmentChangeActivity.strSymbolSelect + "','rgb("
				// + rgbColor + ")');" + "})()");
				// dialogChartIqCompare.dismiss();

			}
		});

		// create alert dialog
		dialogChartIqCompare = alertDialogBuilder.create();
		// show it
		dialogChartIqCompare.show();

		// set width height dialog
		DisplayMetrics displaymetrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(displaymetrics);

		int ScreenHeight = displaymetrics.heightPixels;
		int ScreenWidth = displaymetrics.widthPixels;

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.gravity = Gravity.TOP;
		lp.width = (int) (ScreenWidth * chartIqScreenWidth);
		lp.height = (int) (ScreenHeight * chartIqScreenHeight);
		dialogChartIqCompare.getWindow().setAttributes(lp);
		dialogChartIqCompare.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
	}

	// ------ ChartIq Compare Search
	private ListView dl_list_compare_search;
	private EditText dl_et_compare_search;
	private TextView dl_tv_compare_search_common, dl_tv_compare_search_warrant,
			dl_tv_compare_search_dw, dl_tv_compare_search_close;

	public static String status_segmentId = "COMMON"; // COMMON, FOREIGN_STOCK,
														// WARRENT, DW
	public static String status_segmentIdDot = "EQUITY_INDEX";

	public void showDialogChartIqCompareSearch() {

		LayoutInflater li = LayoutInflater.from(this);
		View viewDl = li.inflate(R.layout.dialog_chartiq_compare_search, null);

		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);
		alertDialogBuilder.setView(viewDl);

		dl_list_compare_search = (ListView) viewDl
				.findViewById(R.id.dl_list_compare_search);
		dl_et_compare_search = (EditText) viewDl
				.findViewById(R.id.dl_et_compare_search);
		dl_tv_compare_search_common = (TextView) viewDl
				.findViewById(R.id.dl_tv_compare_search_common);
		dl_tv_compare_search_warrant = (TextView) viewDl
				.findViewById(R.id.dl_tv_compare_search_warrant);
		dl_tv_compare_search_dw = (TextView) viewDl
				.findViewById(R.id.dl_tv_compare_search_dw);
		dl_tv_compare_search_close = (TextView) viewDl
				.findViewById(R.id.dl_tv_compare_search_close);

		final ChartIqCompateListAdapter ListAdapterSearch;
		final ArrayList<CatalogGetSymbol> original_list;
		final ArrayList<CatalogGetSymbol> second_list;

		original_list = new ArrayList<CatalogGetSymbol>();
		original_list.addAll(FragmentChangeActivity.list_getSymbol);
		second_list = new ArrayList<CatalogGetSymbol>();
		second_list.addAll(FragmentChangeActivity.list_getSymbol);

		ListAdapterSearch = new ChartIqCompateListAdapter(
				getApplicationContext(), 0, second_list);
		dl_list_compare_search.setAdapter(ListAdapterSearch);

		dl_et_compare_search.addTextChangedListener(new TextWatcher() {
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
				String text = dl_et_compare_search.getText().toString();

				second_list.clear();
				for (int i = 0; i < original_list.size(); i++) {
					if (original_list.get(i).symbol.toLowerCase().contains(
							text.toString().toLowerCase())) {
						if (((original_list.get(i).status_segmentId)
								.equals(status_segmentId)) || ((original_list.get(i).status_segmentId)
										.equals(status_segmentIdDot))) {
							second_list.add(original_list.get(i));
						}
					}
				}
				ListAdapterSearch.notifyDataSetChanged();
			}
		});

		// -------------------------
		dl_tv_compare_search_common.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				status_segmentId = "COMMON"; // COMMON, FOREIGN_STOCK, WARRENT,
												// DW
				setTitleSearch();
				dl_tv_compare_search_common.setTextColor(getResources()
						.getColor(R.color.bg_default));
				dl_tv_compare_search_common
						.setBackgroundResource(R.drawable.border_search_activeleft);

				second_list.clear();
				for (int i = 0; i < original_list.size(); i++) {
					if (original_list.get(i).symbol.toLowerCase().contains(
							dl_et_compare_search.getText().toString()
									.toLowerCase())) {
						if (((original_list.get(i).status_segmentId)
								.equals(status_segmentId)) || ((original_list.get(i).status_segmentId)
										.equals(status_segmentIdDot))) {
							second_list.add(original_list.get(i));
						}
					}
				}
				ListAdapterSearch.notifyDataSetChanged();
			}
		});
		dl_tv_compare_search_warrant.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				status_segmentId = "WARRENT"; // COMMON, FOREIGN_STOCK, WARRENT,
												// DW
				setTitleSearch();
				dl_tv_compare_search_warrant.setTextColor(getResources()
						.getColor(R.color.bg_default));
				dl_tv_compare_search_warrant
						.setBackgroundResource(R.drawable.border_search_activecenter);

				second_list.clear();
				for (int i = 0; i < original_list.size(); i++) {
					if (original_list.get(i).symbol.toLowerCase().contains(
							dl_et_compare_search.getText().toString()
									.toLowerCase())) {
						if (((original_list.get(i).status_segmentId)
								.equals(status_segmentId)) || ((original_list.get(i).status_segmentId)
										.equals(status_segmentIdDot))) {
							second_list.add(original_list.get(i));
						}
					}
				}
				ListAdapterSearch.notifyDataSetChanged();
			}
		});
		dl_tv_compare_search_dw.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				status_segmentId = "DW"; // COMMON, FOREIGN_STOCK, WARRENT, DW
				setTitleSearch();
				dl_tv_compare_search_dw.setTextColor(getResources().getColor(
						R.color.bg_default));
				dl_tv_compare_search_dw
						.setBackgroundResource(R.drawable.border_search_activeright);

				second_list.clear();
				for (int i = 0; i < original_list.size(); i++) {
					if (original_list.get(i).symbol.toLowerCase().contains(
							dl_et_compare_search.getText().toString()
									.toLowerCase())) {
						if (((original_list.get(i).status_segmentId)
								.equals(status_segmentId)) || ((original_list.get(i).status_segmentId)
										.equals(status_segmentIdDot))) {
							second_list.add(original_list.get(i));
						}
					}
				}
				ListAdapterSearch.notifyDataSetChanged();
			}
		});

		// -------------------------
		dl_tv_compare_search_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogChartIqCompareSearch.dismiss();
			}
		});

		// create alert dialog
		dialogChartIqCompareSearch = alertDialogBuilder.create();
		// show it
		dialogChartIqCompareSearch.show();

		// set width height dialog
		DisplayMetrics displaymetrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(displaymetrics);

		int ScreenHeight = displaymetrics.heightPixels;
		int ScreenWidth = displaymetrics.widthPixels;

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.gravity = Gravity.TOP;
		lp.width = (int) (ScreenWidth * 1);
		lp.height = (int) (ScreenHeight * 1);
		dialogChartIqCompareSearch.getWindow().setAttributes(lp);
		dialogChartIqCompareSearch.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
	}

	// ---- chartiq compare adapter
	public class ChartIqCompateListAdapter extends ArrayAdapter {
		ArrayList<CatalogGetSymbol> arl;

		public ChartIqCompateListAdapter(Context context,
				int textViewResourceId, ArrayList<CatalogGetSymbol> arl) {
			super(context, textViewResourceId);
			this.arl = arl;
		}

		@Override
		public int getCount() {
			return arl.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View v = convertView;
			if (v == null) {
				LayoutInflater vi;
				vi = LayoutInflater.from(getContext());
				v = vi.inflate(R.layout.row_chartiq_search_compare_symbol, null);
			}
			final TextView tv_symbol = (TextView) v
					.findViewById(R.id.tv_symbol);
			final TextView tv_market_id = (TextView) v
					.findViewById(R.id.tv_market_id);
			final TextView tv_symbol_fullname_eng = (TextView) v
					.findViewById(R.id.tv_symbol_fullname_eng);
			final LinearLayout li_row = (LinearLayout) v
					.findViewById(R.id.li_row);

			// text
			tv_symbol.setText("" + arl.get(position).symbol);
			tv_market_id.setText("" + arl.get(position).market_id);
			tv_symbol_fullname_eng.setText(""
					+ arl.get(position).symbol_fullname_eng);
			// tag
			tv_symbol.setTag("" + arl.get(position).symbol);
			tv_market_id.setTag("" + arl.get(position).market_id);
			tv_symbol_fullname_eng.setTag(""
					+ arl.get(position).symbol_fullname_eng);
			li_row.setTag("" + arl.get(position).symbol);

			final String symbolSelect = arl.get(position).symbol;

			li_row.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialogChartIqCompareSearch.dismiss();
					dl_tv_compare_search_select.setText(symbolSelect);
				}
			});

			return v;
		}
	}

	// ------- set title compare search ------
	private void setTitleSearch() {
		dl_tv_compare_search_common.setTextColor(getResources().getColor(
				R.color.c_content));
		dl_tv_compare_search_warrant.setTextColor(getResources().getColor(
				R.color.c_content));
		dl_tv_compare_search_dw.setTextColor(getResources().getColor(
				R.color.c_content));
		dl_tv_compare_search_common.setBackgroundColor(Color.TRANSPARENT);
		dl_tv_compare_search_warrant.setBackgroundColor(Color.TRANSPARENT);
		dl_tv_compare_search_dw.setBackgroundColor(Color.TRANSPARENT);
	}

	// ========= dialog style ========
	private AlertDialog alertDialogChartIqStyle;

	public void showDialogChartIqStyle() {
		LayoutInflater li = LayoutInflater.from(this);
		View viewDl = li.inflate(R.layout.dialog_chartiq_style, null);

		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);
		alertDialogBuilder.setView(viewDl);

		final TextView tv_chartstyle, tv_charttype, tv_chartscale;
		final LinearLayout li_chartstyle, li_charttype, li_chartscale;
		tv_chartstyle = (TextView) viewDl.findViewById(R.id.tv_chartstyle);
		tv_charttype = (TextView) viewDl.findViewById(R.id.tv_charttype);
		tv_chartscale = (TextView) viewDl.findViewById(R.id.tv_chartscale);

		li_chartstyle = (LinearLayout) viewDl.findViewById(R.id.li_chartstyle);
		li_charttype = (LinearLayout) viewDl.findViewById(R.id.li_charttype);
		li_chartscale = (LinearLayout) viewDl.findViewById(R.id.li_chartscale);

		tv_chartstyle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tv_chartstyle.setTextColor(getResources().getColor(
						R.color.c_content));
				tv_charttype.setTextColor(getResources().getColor(
						R.color.c_title));
				tv_chartscale.setTextColor(getResources().getColor(
						R.color.c_title));

				li_chartstyle.setVisibility(View.VISIBLE);
				li_charttype.setVisibility(View.GONE);
				li_chartscale.setVisibility(View.GONE);
			}
		});
		tv_charttype.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tv_chartstyle.setTextColor(getResources().getColor(
						R.color.c_title));
				tv_charttype.setTextColor(getResources().getColor(
						R.color.c_content));
				tv_chartscale.setTextColor(getResources().getColor(
						R.color.c_title));

				li_chartstyle.setVisibility(View.GONE);
				li_charttype.setVisibility(View.VISIBLE);
				li_chartscale.setVisibility(View.GONE);
			}
		});
		tv_chartscale.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tv_chartstyle.setTextColor(getResources().getColor(
						R.color.c_title));
				tv_charttype.setTextColor(getResources().getColor(
						R.color.c_title));
				tv_chartscale.setTextColor(getResources().getColor(
						R.color.c_content));

				li_chartstyle.setVisibility(View.GONE);
				li_charttype.setVisibility(View.GONE);
				li_chartscale.setVisibility(View.VISIBLE);
			}
		});

		// style
		((LinearLayout) viewDl.findViewById(R.id.li_candle))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) viewDl.findViewById(R.id.li_bar))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) viewDl.findViewById(R.id.li_coloredbar))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) viewDl.findViewById(R.id.li_line))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) viewDl.findViewById(R.id.li_hollowcandle))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) viewDl.findViewById(R.id.li_mountain))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) viewDl.findViewById(R.id.li_coloredline))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) viewDl.findViewById(R.id.li_baseelinedelta))
				.setOnClickListener(onClickListenerChartIqStyle);
		// type
		((LinearLayout) viewDl.findViewById(R.id.li_heikinashi))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) viewDl.findViewById(R.id.li_kagi))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) viewDl.findViewById(R.id.li_linebreak))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) viewDl.findViewById(R.id.li_pointfigure))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) viewDl.findViewById(R.id.li_rangebars))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) viewDl.findViewById(R.id.li_renko))
				.setOnClickListener(onClickListenerChartIqStyle);
		// scale
		// TextView tv_logscale = (TextView)
		// viewDl.findViewById(R.id.tv_logscale);

		// create alert dialog
		alertDialogChartIqStyle = alertDialogBuilder.create();
		// show it
		alertDialogChartIqStyle.show();

		// set width height dialog
		DisplayMetrics displaymetrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(displaymetrics);

		int ScreenHeight = displaymetrics.heightPixels;
		int ScreenWidth = displaymetrics.widthPixels;

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.gravity = Gravity.TOP;
		lp.width = (int) (ScreenWidth * chartIqScreenWidth);
		lp.height = (int) (ScreenHeight * chartIqScreenHeight);
		alertDialogChartIqStyle.getWindow().setAttributes(lp);
		alertDialogChartIqStyle.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
	}

	// -------- on click style -----------
	private OnClickListener onClickListenerChartIqStyle = new OnClickListener() {
		@Override
		public void onClick(final View v) {

			String tag = v.getTag().toString();

			wv_chart_iq.loadUrl("javascript:(function () { "
					+ "mobileControl.changeChartType('" + tag + "');" + "})()");
			alertDialogChartIqStyle.dismiss();

		}
	};

	// ========= read file indicator ========
	public static ArrayList<ChartIqGetIndicatorValueCatalog> list_getIndicatorValue = new ArrayList<ChartIqGetIndicatorValueCatalog>();
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
					list_getIndicatorValue.add(ct);
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

	// ========= dialog indicator ========
	private AlertDialog alertDialogChartIqIndicator;

	private ArrayList<TextView> row_indicator_title;

	public void showDialogChartIqIndicator() {
		try {
			// row_color_list_select.clear();
			LayoutInflater li = LayoutInflater.from(this);
			View viewDl = li.inflate(R.layout.dialog_chartiq_indicator_list,
					null);

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

			row_indicator_title = new ArrayList<TextView>();
			row_indicator_title.clear();

			for (int i = 0; i < jsonIndicator.length(); i++) {
				View view2 = getLayoutInflater().inflate(
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
								wv_chart_iq
										.loadUrl("javascript:(function () { "
												+ "mobileControl.addStudy('"
												+ tv_row_value.getTag() + "');"
												+ "})()");
								alertDialogChartIqIndicator.dismiss();
							}
						});
						li_list_value.addView(viewValue);
					}
				}

				final int num_row = i;
				tv_row.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						changeRowIndicatorTitle(v, num_row);
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
												wv_chart_iq.loadUrl("javascript:(function () { "
														+ "mobileControl.addStudy('"
														+ tv_row_value.getTag()
														+ "');" + "})()");
												alertDialogChartIqIndicator
														.dismiss();
											}
										});
								li_list_value.addView(view2);
							}
						} catch (JSONException e2) {
							e2.printStackTrace();
						}
					}
				});

				row_indicator_title.add(tv_row);

				li_list_title.addView(view2);
			}

			// ********* search studiea value *********
			final ArrayList<ChartIqGetIndicatorValueCatalog> original_list;
			final ArrayList<ChartIqGetIndicatorValueCatalog> second_list;

			original_list = new ArrayList<ChartIqGetIndicatorValueCatalog>();
			original_list.addAll(list_getIndicatorValue);
			second_list = new ArrayList<ChartIqGetIndicatorValueCatalog>();
			second_list.addAll(list_getIndicatorValue);

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
											wv_chart_iq
													.loadUrl("javascript:(function () { "
															+ "mobileControl.addStudy('"
															+ tv_row_value
																	.getTag()
															+ "');" + "})()");
											alertDialogChartIqIndicator
													.dismiss();
										}
									});
							li_list_value.addView(viewValue);
						}
					} else {
					}
				}
			});

			// create alert dialog
			alertDialogChartIqIndicator = alertDialogBuilder.create();

			// show it
			alertDialogChartIqIndicator.show();

			// set width height dialog
			DisplayMetrics displaymetrics = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
			int ScreenHeight = displaymetrics.heightPixels;
			int ScreenWidth = displaymetrics.widthPixels;

			WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
			lp.width = (int) (ScreenWidth * chartIqScreenWidth);
			lp.height = (int) (ScreenHeight * chartIqScreenHeight);

			alertDialogChartIqIndicator.getWindow().setAttributes(lp);

		} catch (JSONException e2) {
			e2.printStackTrace();
		}
	}

	public void changeRowIndicatorTitle(View v, int num_row) {
		for (int i = 0; i < row_indicator_title.size(); i++) {
			row_indicator_title.get(i).setTextColor(
					getResources().getColor(R.color.c_title));
		}
		row_indicator_title.get(num_row).setTextColor(
				getResources().getColor(R.color.c_content));
	}

	// ========= dialog draw ========
	private AlertDialog alertDialogChartIqDraw;

	public void showDialogChartIqDraw() {
		LayoutInflater li = LayoutInflater.from(this);
		View viewDl = li.inflate(R.layout.dialog_chartiq_draw, null);

		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				this);
		alertDialogBuilder.setView(viewDl);

		// -------- coll 1 -------
		((TextView) viewDl.findViewById(R.id.tv_segment))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) viewDl.findViewById(R.id.tv_line))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) viewDl.findViewById(R.id.tv_measure))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) viewDl.findViewById(R.id.tv_fibonacci))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) viewDl.findViewById(R.id.tv_rectangle))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) viewDl.findViewById(R.id.tv_annotation))
				.setOnClickListener(onClickListenerChartIqDraw);

		// -------- coll 2 -------
		((TextView) viewDl.findViewById(R.id.tv_continuous))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) viewDl.findViewById(R.id.tv_horizontal))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) viewDl.findViewById(R.id.tv_channel))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) viewDl.findViewById(R.id.tv_gartley))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) viewDl.findViewById(R.id.tv_ellipse))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) viewDl.findViewById(R.id.tv_callout))
				.setOnClickListener(onClickListenerChartIqDraw);

		// -------- coll 3 -------
		((TextView) viewDl.findViewById(R.id.tv_ray))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) viewDl.findViewById(R.id.tv_vertical))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) viewDl.findViewById(R.id.tv_freeform))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) viewDl.findViewById(R.id.tv_pitchfork))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) viewDl.findViewById(R.id.tv_shape))
				.setOnClickListener(onClickListenerChartIqDraw);

		((TextView) viewDl.findViewById(R.id.tv_clear))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						wv_chart_iq.loadUrl("javascript:(function () { "
								+ "mobileControl.clearDrawing();" + "})()");
						alertDialogChartIqDraw.dismiss();
					}
				});

		// create alert dialog
		alertDialogChartIqDraw = alertDialogBuilder.create();
		// show it
		alertDialogChartIqDraw.show();

		// set width height dialog
		DisplayMetrics displaymetrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(displaymetrics);

		int ScreenHeight = displaymetrics.heightPixels;
		int ScreenWidth = displaymetrics.widthPixels;

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.gravity = Gravity.TOP;
		lp.width = (int) (ScreenWidth * chartIqScreenWidth);
		lp.height = (int) (ScreenHeight * chartIqScreenHeight);
		alertDialogChartIqDraw.getWindow().setAttributes(lp);
		alertDialogChartIqDraw.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
	}

	// -------- on click Draw -----------
	private OnClickListener onClickListenerChartIqDraw = new OnClickListener() {
		@Override
		public void onClick(final View v) {

			String tag = v.getTag().toString();
			// Toast.makeText(getApplicationContext(), "" + tag,
			// Toast.LENGTH_SHORT).show();

			wv_chart_iq.loadUrl("javascript:(function () { "
					+ "mobileControl.setDrawingType('" + tag + "');" + "})()");
			alertDialogChartIqDraw.dismiss();

		}
	};

	// ========= init webview ========
	LinearLayout li_ciq_tool;

	boolean ckShowHideCiqTool;

	// -------------
	private static final int CLICK_ON_WEBVIEW = 1;
	private static final int CLICK_ON_URL = 2;

	private final Handler handler = new Handler();

	public void initWebView() {
		wv_chart_iq = (WebView) findViewById(R.id.wv_chart_iq);
		li_ciq_tool = (LinearLayout) findViewById(R.id.li_ciq_tool);
		ckShowHideCiqTool = false;
		// li_ciq_tool.setVisibility(View.GONE);

		// mobileControl.enableRealtime() // เชื่อมต่อ
		// mobileControl.disableRealtime() // ปิดการเชื่อมต่อ

		// closeConnection() // ปิด connect ทุกครั้งที่ออกจากหน้าchart

		// wv_chart_iq.setOnTouchListener(this);
		wv_chart_iq.getSettings().setLoadWithOverviewMode(true);
		wv_chart_iq.getSettings().setUseWideViewPort(true);
		wv_chart_iq.getSettings().setBuiltInZoomControls(true);
		wv_chart_iq.getSettings().setJavaScriptEnabled(true);
		wv_chart_iq.loadUrl(wv_url);
		wv_chart_iq.setWebViewClient(new myWebClient());

		try {
			if (SplashScreen.contentGetUserById != null) {
				if (!(SplashScreen.contentGetUserById.getString("package")
						.equals("free"))) {
					wv_chart_iq.loadUrl("javascript:(function () { "
							+ "mobileControl.enableRealtime();" + "})()");
				} else {
					wv_chart_iq.loadUrl("javascript:(function () { "
							+ "mobileControl.disableRealtime();" + "})()");
				}
			} else {
				wv_chart_iq.loadUrl("javascript:(function () { "
						+ "mobileControl.disableRealtime();" + "})()");
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// Toast.makeText(getApplicationContext(), "onTouch",
		// Toast.LENGTH_SHORT).show();
		// if (v.getId() == R.id.wv_chart_iq
		// && event.getAction() == MotionEvent.ACTION_DOWN) {
		// Toast.makeText(getApplicationContext(), "onTouch",
		// Toast.LENGTH_SHORT).show();
		// handler.sendEmptyMessageDelayed(CLICK_ON_WEBVIEW, 500);
		// }
		// return false;

		// // ---- ontouch show hide topbar
		// wv_chart_iq.setOnTouchListener(new View.OnTouchListener() {
		//
		// public final static int FINGER_RELEASED = 0;
		// public final static int FINGER_TOUCHED = 1;
		// public final static int FINGER_DRAGGING = 2;
		// public final static int FINGER_UNDEFINED = 3;
		//
		// private int fingerState = FINGER_RELEASED;
		//
		// @Override
		// public boolean onTouch(View v, MotionEvent event) {
		// switch (event.getAction()) {
		// case MotionEvent.ACTION_DOWN:
		// if (fingerState == FINGER_RELEASED)
		// fingerState = FINGER_TOUCHED;
		// else
		// fingerState = FINGER_UNDEFINED;
		// break;
		//
		// case MotionEvent.ACTION_UP:
		// if (fingerState != FINGER_DRAGGING) {
		// fingerState = FINGER_RELEASED;
		//
		// Toast.makeText(getApplicationContext(),
		// "cccccc clicked", Toast.LENGTH_SHORT).show();
		//
		// if (ckShowHideCiqTool) {
		// ckShowHideCiqTool = false;
		// li_ciq_tool.setVisibility(View.GONE);
		// wv_chart_iq.loadUrl("javascript:(function () { "
		// + "mobileControl.hideTopbar();" + "})()");
		// } else {
		// ckShowHideCiqTool = true;
		// li_ciq_tool.setVisibility(View.VISIBLE);
		// wv_chart_iq.loadUrl("javascript:(function () { "
		// + "mobileControl.showTopbar();" + "})()");
		// }
		//
		// } else if (fingerState == FINGER_DRAGGING)
		// fingerState = FINGER_RELEASED;
		// else
		// fingerState = FINGER_UNDEFINED;
		// break;
		//
		// case MotionEvent.ACTION_MOVE:
		// if (fingerState == FINGER_TOUCHED
		// || fingerState == FINGER_DRAGGING)
		// fingerState = FINGER_DRAGGING;
		// else
		// fingerState = FINGER_UNDEFINED;
		// break;
		//
		// default:
		// fingerState = FINGER_UNDEFINED;
		// }
		// return false;
		// }
		// });
	}

	// ====== webview client ============
	public class myWebClient extends WebViewClient {

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, String url) {
			// TODO Auto-generated method stub
			super.onPageFinished(view, url);

			wv_chart_iq.loadUrl("javascript:(function () { "
					+ "mobileControl.showTopbar();" + "})()");

			// --------- update topbar
			try {
				// ------ high low
				String strOpen1 = FunctionSymbol
						.setFormatNumber(contentGetDetail.getString("open1"));
				String strOpen2 = FunctionSymbol
						.setFormatNumber(contentGetDetail.getString("open2"));
				String strHigh = FunctionSymbol
						.setFormatNumber(contentGetDetail.getString("high"));
				String strLow = FunctionSymbol.setFormatNumber(contentGetDetail
						.getString("low"));
				String strClose = FunctionSymbol
						.setFormatNumber(contentGetDetail.getString("close"));
				String strPrevClose = FunctionSymbol
						.setFormatNumber(contentGetDetail
								.getString("prev_close"));
				String strChange = FunctionSymbol
						.setFormatNumber(contentGetDetail.getString("change"));
				String strPercentChange = FunctionSymbol
						.setFormatNumber(contentGetDetail
								.getString("percentChange"));

				String strOpen1Color = "#"
						+ Integer.toHexString(
								getResources().getColor(R.color.c_content))
								.substring(2);
				String strOpen2Color = "#"
						+ Integer.toHexString(
								getResources().getColor(R.color.c_content))
								.substring(2);
				String strHighColor = "#"
						+ Integer.toHexString(
								getResources().getColor(R.color.c_content))
								.substring(2);
				String strLowColor = "#"
						+ Integer.toHexString(
								getResources().getColor(R.color.c_content))
								.substring(2);
				String strCloseColor = "#"
						+ Integer.toHexString(
								getResources().getColor(R.color.c_danger))
								.substring(2);
				String strPrevCloseColor = "#"
						+ Integer.toHexString(
								getResources().getColor(R.color.c_warning))
								.substring(2);
				String strChangeColor = "#"
						+ Integer.toHexString(
								getResources().getColor(R.color.c_content))
								.substring(2);
				String strPercentChangeColor = "#"
						+ Integer.toHexString(
								getResources().getColor(R.color.c_content))
								.substring(2);

				String strSymbolFullnameEng = contentGetDetail
						.getString("symbol_fullname_eng");
				wv_chart_iq.loadUrl("javascript:(function () { "
						+ "mobileControl.updateDesc(" + strSymbolFullnameEng
						+ ");" + "})()");

				if (strPrevClose != "") {
					if (strHigh != "") {
						if ((Float.parseFloat(strHigh.replaceAll(",", ""))) > Float
								.parseFloat(strPrevClose.replaceAll(",", ""))) {
							strHighColor = "#"
									+ Integer
											.toHexString(
													getResources()
															.getColor(
																	FunctionSetBg.arrColor[2]))
											.substring(2);
						} else if ((Float.parseFloat(strHigh
								.replaceAll(",", ""))) < Float
								.parseFloat(strPrevClose.replaceAll(",", ""))) {
							strHighColor = "#"
									+ Integer
											.toHexString(
													getResources()
															.getColor(
																	FunctionSetBg.arrColor[0]))
											.substring(2);
						} else {
							strHighColor = "#"
									+ Integer
											.toHexString(
													getResources()
															.getColor(
																	FunctionSetBg.arrColor[1]))
											.substring(2);
						}
					}
					if (strLow != "") {
						if ((Float.parseFloat(strLow.replaceAll(",", ""))) > Float
								.parseFloat(strPrevClose.replaceAll(",", ""))) {
							strLowColor = "#"
									+ Integer
											.toHexString(
													getResources()
															.getColor(
																	FunctionSetBg.arrColor[2]))
											.substring(2);
						} else if ((Float
								.parseFloat(strLow.replaceAll(",", ""))) < Float
								.parseFloat(strPrevClose.replaceAll(",", ""))) {
							strLowColor = "#"
									+ Integer
											.toHexString(
													getResources()
															.getColor(
																	FunctionSetBg.arrColor[0]))
											.substring(2);
						} else {
							strLowColor = "#"
									+ Integer
											.toHexString(
													getResources()
															.getColor(
																	FunctionSetBg.arrColor[1]))
											.substring(2);
						}
					}
				}
				// ------ open 1
				if (strOpen1 != "") {
					if ((Float.parseFloat(strOpen1.replaceAll(",", ""))) > Float
							.parseFloat(strPrevClose.replaceAll(",", ""))) {
						strOpen1Color = "#"
								+ Integer
										.toHexString(
												getResources()
														.getColor(
																FunctionSetBg.arrColor[2]))
										.substring(2);
					} else if ((Float.parseFloat(strOpen1.replaceAll(",", ""))) < Float
							.parseFloat(strPrevClose.replaceAll(",", ""))) {
						strOpen1Color = "#"
								+ Integer
										.toHexString(
												getResources()
														.getColor(
																FunctionSetBg.arrColor[0]))
										.substring(2);
					} else {
						strOpen1Color = "#"
								+ Integer
										.toHexString(
												getResources()
														.getColor(
																FunctionSetBg.arrColor[1]))
										.substring(2);
					}
				}
				// ------ open 2
				if (strOpen2 != "") {
					if ((Float.parseFloat(strOpen2.replaceAll(",", ""))) > Float
							.parseFloat(strPrevClose.replaceAll(",", ""))) {
						strOpen2Color = "#"
								+ Integer
										.toHexString(
												getResources()
														.getColor(
																FunctionSetBg.arrColor[2]))
										.substring(2);
					} else if ((Float.parseFloat(strOpen2.replaceAll(",", ""))) < Float
							.parseFloat(strPrevClose.replaceAll(",", ""))) {
						strOpen2Color = "#"
								+ Integer
										.toHexString(
												getResources()
														.getColor(
																FunctionSetBg.arrColor[0]))
										.substring(2);
					} else {
						strOpen2Color = "#"
								+ Integer
										.toHexString(
												getResources()
														.getColor(
																FunctionSetBg.arrColor[1]))
										.substring(2);
					}
				}
				// ------ last trade, percenchage
				if (strChange != "") {
					strChangeColor = "#"
							+ Integer.toHexString(
									getResources().getColor(
											FunctionSetBg
													.setColor(strChange)))
									.substring(2);
					strPercentChangeColor = "#"
							+ Integer.toHexString(
									getResources().getColor(
											FunctionSetBg
													.setColor(strChange)))
									.substring(2);
				}

				// Log.v("valueeee",
				// strOpen1+"_"+strOpen2+"_"+strHigh+"_"+"_"+strLow+"_"+strClose+"_"+strPrevClose+"_"+strChange+"_"+strPercentChange);

				wv_chart_iq.loadUrl("javascript:(function () { "
						+ "mobileControl.updateTopbar({"
						+ "\"open1\": {\"value\" : "
						+ '\"'
						+ strOpen1
						+ '\"'
						+ ", \"color\": "
						+ '\"'
						+ strOpen1Color
						+ '\"'
						+ "},"
						+ "\"open2\": {\"value\": "
						+ '\"'
						+ strOpen2
						+ '\"'
						+ ", \"color\": "
						+ '\"'
						+ strOpen2Color
						+ '\"'
						+ "},"
						+ "\"high\": {\"value\": "
						+ '\"'
						+ strHigh
						+ '\"'
						+ ", \"color\": "
						+ '\"'
						+ strHighColor
						+ '\"'
						+ "},"
						+ "\"low\": {\"value\": "
						+ '\"'
						+ strLow
						+ '\"'
						+ ", \"color\": "
						+ '\"'
						+ strLowColor
						+ '\"'
						+ "},"
						+ "\"close\": {\"value\": "
						+ '\"'
						+ strClose
						+ '\"'
						+ ", \"color\": "
						+ '\"'
						+ strCloseColor
						+ '\"'
						+ "},"
						+ "\"prevclose\": {\"value\": "
						+ '\"'
						+ strPrevClose
						+ '\"'
						+ ", \"color\":  "
						+ '\"'
						+ strPrevCloseColor
						+ '\"'
						+ "},"
						+ "\"change\": {\"value\": "
						+ '\"'
						+ strChange
						+ '\"'
						+ ", \"color\": "
						+ '\"'
						+ strChangeColor
						+ '\"'
						+ "},"
						+ "\"percentChange\": {\"value\": "
						+ '\"'
						+ strPercentChange
						+ '\"'
						+ ", \"color\": "
						+ '\"'
						+ strPercentChangeColor + '\"' + "} });" + "})()");

			} catch (JSONException e) {
				e.printStackTrace();
			}

		}
	}

	protected void switchFragment(PagerWatchList fragment) {
		if (getApplicationContext() == null)
			return;
		if (getApplicationContext() instanceof FragmentChangeActivity) {
			FragmentChangeActivity fca = (FragmentChangeActivity) getApplicationContext();
			fca.switchContent(fragment);
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			wv_chart_iq.loadUrl("javascript:(function () { "
					+ "mobileControl.closeConnection();" + "})()");
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (v.getId() == R.id.wv_chart_iq
				&& event.getAction() == MotionEvent.ACTION_DOWN) {
			handler.sendEmptyMessageDelayed(CLICK_ON_WEBVIEW, 500);
		}
		return false;
	}

	@Override
	public boolean handleMessage(Message msg) {
		if (msg.what == CLICK_ON_URL) {
			handler.removeMessages(CLICK_ON_WEBVIEW);
			return true;
		}
		if (msg.what == CLICK_ON_WEBVIEW) {
			Toast.makeText(this, "WebView clicked", Toast.LENGTH_SHORT).show();
			return true;
		}
		return false;
	}

}
