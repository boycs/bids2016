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
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader.TileMode;
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
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class UiMutualfundDetail extends FragmentActivity {

	private static Activity act;

	// view pager
	static ViewPager mPagerMain;
	// list contains fragments to instantiate in the viewpager
	List<Fragment> fragmentMain = new Vector<Fragment>();
	private PagerAdapter mPagerAdapterMain;

	public static ImageView img_follow;

	static Dialog dialogLoading;

	// --------- google analytics
//	private Tracker mTracker;
//	String nameTracker = new String("Mutual Fund Detail");

	// ================= data ==================
	public static JSONArray contentGetDetailData;
	public static JSONArray contentGetDetailGraph;

	public static String contentGetDetailFundamental = "";
	public static JSONObject contentGetDetailUpdate = null;
	public static LinearLayout li_vertical;
	public static RelativeLayout rl_chartiq;

	// ========== pager detail =======
	public static JSONArray contentGetChartData;
	public static JSONArray contentGetChartGraph;
	public static JSONArray contentGetFundAssData;
	public static JSONArray contentGetDividendData;
	public static JSONArray contentGetDividendGraph;

	public static JSONArray contentGetPerformData;
	public static JSONArray contentGetPerformYear;
	public static JSONArray contentGetPerformQuarter;
	public static JSONObject contentGetPerformGraph;
	public static JSONArray contentGetPerformNevGraph;

	// --- add mutualfund
	static DialogAddPortfolio dialogAddPortfolio;

	// show hide
	SlidingPanel popup_window_detail;
	LinearLayout li_menu_top, li_data, li_data_symbol, li_detail;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		
		setContentView(R.layout.ui_mutualfund_detail);

		// // --------- google analytics
		// Tracker t = ((GoogleAnalyticsApp) getApplicationContext())
		// .getTracker(TrackerName.APP_TRACKER);
		// t.setScreenName(nameTracker);
		// t.send(new HitBuilders.AppViewBuilder().build());

		dialogAddPortfolio = new DialogAddPortfolio(this);

		// --------- google analytics
//		GoogleAnalyticsApp application = (GoogleAnalyticsApp) getApplicationContext();
//		mTracker = application.getDefaultTracker();

		// set ค่า static object เริ่มต้น ให้เป็น null
		setObjectBeginNull(); 
		// set view
		initView();
	}
	
	//------- set ค่า static object เริ่มต้น ให้เป็น null
	private void setObjectBeginNull(){
		// ================= data ==================
		contentGetDetailData = null;
		contentGetDetailGraph = null; 

		contentGetDetailFundamental = "";
		contentGetDetailUpdate = null;
		// ========== pager detail =======
		contentGetChartData = null;
		contentGetChartGraph = null;
		contentGetFundAssData = null;
		contentGetDividendData = null;
		contentGetDividendGraph = null;

		contentGetPerformData = null;
		contentGetPerformYear = null;
		contentGetPerformQuarter = null;
		contentGetPerformGraph = null;
		contentGetPerformNevGraph = null;
	}	

	// @Override
	// protected void onStart() {
	// super.onStart();
	// Log.v("FragmentChangeActivity", "onStart onStart onStart");
	// GoogleAnalytics.getInstance(UiWatchlistDetail.this)
	// .reportActivityStart(this);
	// }
	//
	// @Override
	// protected void onStop() {
	// super.onStop();
	// Log.v("FragmentChangeActivity", "onStop onStop onStop");
	// GoogleAnalytics.getInstance(UiWatchlistDetail.this).reportActivityStop(
	// this);
	// }

	@Override
	public void onResume() {
		super.onResume();
//		Log.v(nameTracker, "onResume onResume onResume");
//
//		mTracker.setScreenName(nameTracker);
//		mTracker.send(new HitBuilders.ScreenViewBuilder().build());
	}

	// ============== init view ===============
	private void initView() {
		((TextView) findViewById(R.id.tv_title_symbol))
				.setText(FragmentChangeActivity.strSymbolSelect + "'s Detail");

		((LinearLayout) findViewById(R.id.li_back))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.wv_chartiq
								.loadUrl("javascript:(function () { "
										+ "mobileControl.closeConnection();"
										+ "})()");
						finish();
					}
				});

		dialogLoading = new Dialog(UiMutualfundDetail.this);
		dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogLoading.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialogLoading.setContentView(R.layout.progress_bar);
		dialogLoading.setCancelable(false);
		dialogLoading.setCanceledOnTouchOutside(false);

		li_data = (LinearLayout) findViewById(R.id.li_data);
		li_menu_top = (LinearLayout) findViewById(R.id.li_menu_top);
		loadDataDetail();

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

			// http://www.bidschart.com/service/v2/getFundDetail?fund=
			String url_GetDetail = SplashScreen.url_bidschart
					+ "/service/v2/getFundDetail?fund="
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

						if (jsonGetDetail.getString("status").equals("ok")) {
							contentGetDetailData = jsonGetDetail
									.getJSONArray("data");
							contentGetDetailGraph = jsonGetDetail
									.getJSONArray("graph");

							setDataDetail();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.v("json mutualfund detail null",
							"mutualfund detail null");
					dialogLoading.dismiss();
				}
			} else {
				dialogLoading.dismiss();
			}
		}

	}

	// ======= data show hide
	public void showDataDetail() {
		ckHideShowData = false;
		li_data.setVisibility(View.VISIBLE);

		// int li_height_detail = li_detail.getHeight();
		int li_height_menu_top = li_menu_top.getHeight();
		int li_height_data = li_data.getHeight();
		int height_mPagerMainPreMium = FragmentChangeActivity.heightScreen
				- (li_height_data + li_height_menu_top);

		Log.v("li_height show", li_height_data + "_" + height_mPagerMainPreMium); //

		setDataDetailFuncWidth(FragmentChangeActivity.widthScreen,
				height_mPagerMainPreMium);
	}

	public void setDataDetailFuncWidth(int width, int height) {
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				width, height);
		mPagerMain.setLayoutParams(layoutParams);

		Log.v("mPagerMainPreMium",
				width + "_" + height + "_" + mPagerMain.getHeight()); //
	}

	public void hideDataDetail() {
		ckHideShowData = true;
		li_data.setVisibility(View.GONE);

		// int li_height_detail = li_detail.getHeight();
		int li_height_menu_top = li_menu_top.getHeight();
		int li_height_data = li_data.getHeight();
		int height_mPagerMainPreMium = FragmentChangeActivity.heightScreen
				- (li_height_menu_top);

		Log.v("li_height hide", li_height_data + "_" + height_mPagerMainPreMium); //

		// LinearLayout.LayoutParams layoutParams = new
		// LinearLayout.LayoutParams(
		// FragmentChangeActivity.widthScreen, height_mPagerMainPreMium);
		// mPagerMainPreMium.setLayoutParams(layoutParams);

		setDataDetailFuncWidth(FragmentChangeActivity.widthScreen,
				height_mPagerMainPreMium);
	}

	// ====================== setDataDetail ================
	public static LinearLayout li_menu;
	public static TextView tv_tab_chart, tv_tab_fundass, tv_tab_dividend,
			tv_tab_perform;
	// ImageView img_follow;
	public static LinearLayout li_showbysell;

	boolean ckHideShowBuySell = false; // hide = false
	boolean ckHideShowData = false;

	private void initTabPager() {
		tv_tab_chart = (TextView) findViewById(R.id.tv_tab_chart);
		tv_tab_fundass = (TextView) findViewById(R.id.tv_tab_fundass);
		tv_tab_dividend = (TextView) findViewById(R.id.tv_tab_dividend);
		tv_tab_perform = (TextView) findViewById(R.id.tv_tab_perform);

		// hideDataDetail();

		tv_tab_chart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectTabPager(1);
				mPagerMain.setCurrentItem(0);
			}
		});
		tv_tab_fundass.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectTabPager(2);
				mPagerMain.setCurrentItem(1);
			}
		});
		tv_tab_dividend.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectTabPager(3);
				mPagerMain.setCurrentItem(2);
			}
		});
		tv_tab_perform.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectTabPager(4);
				mPagerMain.setCurrentItem(3);
			}
		});
	}

	// main
	TextView tv_name_initial, tv_invest_value1, tv_name_t, tv_invest_change,
			tv_asset_name;
	ImageView img_icon;
	RatingBar rb_rating;
	// column 1
	TextView tv_asset_value, tv_invest_value, tv_sell_price, tv_buyback_price;
	// column 2
	TextView tv_fund_category, tv_dividend_policy, tv_register_date,
			tv_date_at;
	// risk
	public static ArrayList<View> arr_risk_v;
	LinearLayout li_risk;
	View v_risk_1, v_risk_2, v_risk_3, v_risk_4, v_risk_5, v_risk_6, v_risk_7,
			v_risk_8;

	private void setDataDetail() {

		try {
			if (contentGetDetailData != null) {
				JSONObject jsoData = contentGetDetailData.getJSONObject(0);
				// main
				tv_name_initial = (TextView) findViewById(R.id.tv_name_initial);
				tv_invest_value1 = (TextView) findViewById(R.id.tv_invest_value1);
				tv_name_t = (TextView) findViewById(R.id.tv_name_t);
				tv_invest_change = (TextView) findViewById(R.id.tv_invest_change);

				img_icon = (ImageView) findViewById(R.id.img_icon);
				tv_asset_name = (TextView) findViewById(R.id.tv_asset_name);
				rb_rating = (RatingBar) findViewById(R.id.rb_rating);
				// column 1
				tv_asset_value = (TextView) findViewById(R.id.tv_asset_value);
				tv_invest_value = (TextView) findViewById(R.id.tv_invest_value);
				tv_sell_price = (TextView) findViewById(R.id.tv_sell_price);
				tv_buyback_price = (TextView) findViewById(R.id.tv_buyback_price);
				// column 2
				tv_fund_category = (TextView) findViewById(R.id.tv_fund_category);
				tv_dividend_policy = (TextView) findViewById(R.id.tv_dividend_policy);
				tv_register_date = (TextView) findViewById(R.id.tv_register_date);
				tv_date_at = (TextView) findViewById(R.id.tv_date_at);
				// risk
				li_risk = (LinearLayout) findViewById(R.id.li_risk);
				v_risk_1 = (View) findViewById(R.id.v_risk_1);
				v_risk_2 = (View) findViewById(R.id.v_risk_2);
				v_risk_3 = (View) findViewById(R.id.v_risk_3);
				v_risk_4 = (View) findViewById(R.id.v_risk_4);
				v_risk_5 = (View) findViewById(R.id.v_risk_5);
				v_risk_6 = (View) findViewById(R.id.v_risk_6);
				v_risk_7 = (View) findViewById(R.id.v_risk_7);
				v_risk_8 = (View) findViewById(R.id.v_risk_8);

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

				// ======= set data ========
				// main
				final String name_initial = jsoData.getString("name_initial");
				final String invest_value = jsoData.getString("invest_value");
				final String name_t = jsoData.getString("name_t");
				final String name_e = jsoData.getString("name_e");
				final String invest_change = jsoData.getString("invest_change");

				String asset_name = jsoData.getString("asset_name");
				String asset_initial = jsoData.getString("asset_initial");
				String fund_rating = jsoData.getString("fund_rating");
				// column 1
				String asset_value = jsoData.getString("asset_value");
				String sell_price = jsoData.getString("sell_price");
				String buyback_price = jsoData.getString("buyback_price");
				// column 2
				String fund_category = jsoData.getString("fund_category");
				String dividend_policy = jsoData.getString("dividend_policy");
				String register_date = jsoData.getString("register_date");
				String date_at = jsoData.getString("date_at");
				// risk
				String fund_risk = jsoData.getString("fund_risk");

				// main
				tv_name_initial.setText(name_initial);
				tv_invest_value1.setText(invest_value);
				tv_name_t.setText(name_t);

				if ((!invest_change.equals(""))
						&& (!invest_change.equals("null"))
						&& (!invest_change.equals("-"))) {
					tv_invest_change.setText(FunctionSetBg
							.setSymbolPlusMinus(invest_change)+invest_change
							+ FunctionSetBg.setSymbolPercent(invest_change));
					tv_invest_change.setTextColor(getResources().getColor(
							FunctionSetBg
									.setColorCompareWithZero(invest_change)));
					tv_invest_value1.setTextColor(getResources().getColor(
							FunctionSetBg
									.setColorCompareWithZero(invest_change)));
				}
				// tv_invest_value1.setTextColor(getResources().getColor(
				// BgColorSymbolDetail.setColorMutualFund(invest_value)));
				// tv_invest_change.setTextColor(getResources().getColor(
				// BgColorSymbolDetail.setColorMutualFund(invest_change)));

				tv_asset_name.setText(asset_name);
				if ((!fund_rating.equals("")) && (!fund_rating.equals("N/A"))) {
					rb_rating.setRating(Float.parseFloat(fund_rating
							.replaceAll(",", "")));
				}

				FragmentChangeActivity.imageLoader.displayImage(
						SplashScreen.url_bidschart_fund + asset_initial
								+ ".gif", img_icon,
						FragmentChangeActivity.optionsRounded);

				// column 1
				tv_asset_value.setText(asset_value);
				tv_invest_value.setText(invest_value);
				tv_sell_price.setText(sell_price);
				tv_buyback_price.setText(buyback_price);

				// invest_value เทียบกับ 0
				// buyback_price,sell_price เทียบกับ invest_value
				tv_invest_value.setTextColor(getResources().getColor(
						FunctionSetBg.setColorCompareWithZero(invest_value)));
				tv_sell_price.setTextColor(getResources().getColor(
						FunctionSetBg.setColorCompare2Attribute(sell_price,
								invest_value)));
				tv_buyback_price.setTextColor(getResources().getColor(
						FunctionSetBg.setColorCompare2Attribute(buyback_price,
								invest_value)));
				// column 2
				tv_fund_category.setText(fund_category);
				tv_dividend_policy.setText(dividend_policy);
				tv_register_date.setText(DateTimeCreate
						.DateCreateMutualFunTh(register_date));
				tv_date_at.setText(DateTimeCreate
						.DateCreateMutualFunTh(date_at));
				// risk
				if ((!fund_risk.equals("")) && (!fund_risk.equals("-"))) {
					// li_risk.setBackgroundResource(R.drawable.bg_tab_risk_big_active);
					setBgFundRisk(fund_risk); // ------ set background fund_risk
				} else {
					li_risk.setBackgroundResource(R.drawable.bg_tab_risk_big);
				}


				// add portfolio
				((ImageView) findViewById(R.id.img_portfolio_add))
						.setOnClickListener(new OnClickListener() {
					@Override
						public void onClick(View v) {
							try {
								FragmentChangeActivity.contentAddPortfolioAdd = new JSONObject();
								FragmentChangeActivity.contentAddPortfolioAdd.put("symbol", name_initial);
								FragmentChangeActivity.contentAddPortfolioAdd.put("fullname_eng", name_e);
								FragmentChangeActivity.contentAddPortfolioAdd.put("fullname_thai", name_t);
								FragmentChangeActivity.contentAddPortfolioAdd.put("price", invest_value);
								FragmentChangeActivity.contentAddPortfolioAdd.put("volume", invest_change);
								dialogAddPortfolio.show();
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					});
				
				// -------- init pager
				initPager();

			} else {
				Toast.makeText(getApplicationContext(), "No data.", 0).show();
				dialogLoading.dismiss();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dialogLoading.dismiss();
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
					arr_risk_v.get(i).setBackgroundResource(R.color.bg_default);
				}
			}
		}
		return str;
	}

	// ============= Pager ===========
	private void initPager() {
		// creating fragments and adding to list
		fragmentMain.removeAll(fragmentMain);
		fragmentMain.add(Fragment.instantiate(getApplicationContext(),
				PagerMutualFundDetailChart.class.getName()));
		fragmentMain.add(Fragment.instantiate(getApplicationContext(),
				PagerMutualFundDetailFundAss.class.getName()));
		fragmentMain.add(Fragment.instantiate(getApplicationContext(),
				PagerMutualFundDetailDividend.class.getName()));
		fragmentMain.add(Fragment.instantiate(getApplicationContext(),
				PagerMutualFundDetailPerformance.class.getName()));

		// creating adapter and linking to view pager
		this.mPagerAdapterMain = new PagerAdapter(
				super.getSupportFragmentManager(), fragmentMain);
		mPagerMain = (ViewPager) findViewById(R.id.vp_pager);

		mPagerMain.setAdapter(this.mPagerAdapterMain);

		mPagerMain.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				selectTabPager((mPagerMain.getCurrentItem()) + 1);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				mPagerMain.getParent().requestDisallowInterceptTouchEvent(true);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		initTabPager(); // init tab pager

	}

	// ============= set tab ===========
	public int selectTabPager(int numtab) {
		tv_tab_chart.setBackgroundColor(Color.TRANSPARENT);
		tv_tab_fundass.setBackgroundColor(Color.TRANSPARENT);
		tv_tab_dividend.setBackgroundColor(Color.TRANSPARENT);
		tv_tab_perform.setBackgroundColor(Color.TRANSPARENT);

		tv_tab_chart.setTextColor(getResources().getColor(R.color.c_title));
		tv_tab_fundass.setTextColor(getResources().getColor(R.color.c_title));
		tv_tab_dividend.setTextColor(getResources().getColor(R.color.c_title));
		tv_tab_perform.setTextColor(getResources().getColor(R.color.c_title));

		if (numtab == 1) {
			tv_tab_chart
					.setBackgroundResource(R.drawable.border_button_activeleft);
			tv_tab_chart.setTextColor(getResources().getColor(
					R.color.bg_default));

			// showDataDetail();
		} else if (numtab == 2) {
			tv_tab_fundass
					.setBackgroundResource(R.drawable.border_button_activecenter);
			tv_tab_fundass.setTextColor(getResources().getColor(
					R.color.bg_default));

			// hideDataDetail();
		} else if (numtab == 3) {
			tv_tab_dividend
					.setBackgroundResource(R.drawable.border_button_activecenter);
			tv_tab_dividend.setTextColor(getResources().getColor(
					R.color.bg_default));

			// showDataDetail();
		} else if (numtab == 4) {
			tv_tab_perform
					.setBackgroundResource(R.drawable.border_button_activeright);
			tv_tab_perform.setTextColor(getResources().getColor(
					R.color.bg_default));

			// hideDataDetail();
		}
		return numtab;
	}

	// config rotation
	public static boolean orientation_landscape = false; // เช็ค แนวนอน

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			// แนวนอน
			Log.v("onConfigurationChan >> ", "แนวนอน");
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			// แนวตั้ง
			Log.v("onConfigurationChan >> ", "แนวตั้ง");
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
			FragmentChangeActivity.wv_chartiq
					.loadUrl("javascript:(function () { "
							+ "mobileControl.closeConnection();" + "})()");
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
