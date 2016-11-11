package com.app.bids;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;
import com.app.bids.PagerHomeAll.loadData;
import com.app.bids.PagerWatchList.loadDataSlidingMarquee;

import android.R.anim;
import android.R.integer;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class PagerSystemTradeMutualFund extends Fragment {

	static Context context;
	public static View rootView;

	private WebSocketClient mWebSocketClient;

	public static Dialog dialogLoading;

	// --------- google analytics
//	private Tracker mTracker;
//	String nameTracker = new String("Mutual Fund");

	// list contains fragments to instantiate in the viewpager
	List<Fragment> fragmentMain = new Vector<Fragment>();
	private PagerAdapter mPagerAdapter;
	// view pager
	private ViewPager mPager;

	// activity listener interface
	public int selectTitle = 0;

	// ================= data ==================
	View view;
	ListView listView;

	static DialogDifinitionSignal DialogDifinitionSignal;

	// private ListAdapter adapter;

	public interface OnPageListener {
		public void onPage1(String s);
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

		DialogDifinitionSignal = new DialogDifinitionSignal(context);

		// inflate view from layout
		rootView = inflater.inflate(R.layout.pager_systemtrade_mutualfund,
				container, false);

		// getFragmentManager().beginTransaction().remove(FragmentPagerSystemTrade.this).commit();

		// --------- google analytics
//		GoogleAnalyticsApp application = (GoogleAnalyticsApp) getActivity()
//				.getApplication();
//		mTracker = application.getDefaultTracker();

		dialogLoading = new Dialog(context);
		dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogLoading.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialogLoading.setContentView(R.layout.progress_bar);
		dialogLoading.setCancelable(false);
		dialogLoading.setCanceledOnTouchOutside(false);
		dialogLoading.show();

		// dialogLoading.show();
		FragmentChangeActivity.id_websocket = 6;

		if (FragmentChangeActivity.contentGetSystemTradeFundDetailList != null) {
			initMenuMutualFund(); // set data
		} else {
			loadDataDetail(); // load data
		}

		// initSearchLayout(); // layout search

		((TextView) rootView.findViewById(R.id.tv_portfolio))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// FragmentChangeActivity.strWatchlistCategory =
						// "portfolio";
						// switchFragment(new PagerWatchList());
					}
				});

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
//		Log.v(nameTracker, "onResume onResume onResume");
//
//		mTracker.setScreenName(nameTracker);
//		mTracker.send(new HitBuilders.ScreenViewBuilder().build());
	}

	// คร่าวๆ watch list
	// get symbolFavorite มาก่อน
	// พอเลือก favorite ก็เอา ไปเทียบ get ดาต้าใน watchlistSymbol

	// watchlistSymbol color
	// [T] : trendSignal_avg_percentChange ส่งค่าไปเป็น avg %change
	// [F]: fundamental ส่งค่าไปเป็นคะแนน เต็ม 5
	// [S]:color_macd ส่งตัวอักษรสีไปเป็น
	// ['GREEN','BLUE','RED','ORANGE','BLACK']

	// ***************** search symbol******************

	public static LinearLayout li_search_tabbegin, li_search_select;

	public static LinearLayout li_search;

	// android.support.v4.view.ViewPager vp_pager;
	public static EditText et_search;
	public static TextView tv_close_search;
	public static ListView listview_search;
	public static LinearLayout li_view;

	private void initSearchLayout() {

		// --- linear show tabsearch begin
		li_search_tabbegin = (LinearLayout) rootView
				.findViewById(R.id.li_search_tabbegin);
		li_search_select = (LinearLayout) rootView
				.findViewById(R.id.li_search_select);
		// --- linear tabsearch
		li_search = (LinearLayout) rootView.findViewById(R.id.li_search);
		// --- linear tabsearch begin
		li_view = (LinearLayout) rootView.findViewById(R.id.li_view);
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
	}

	private void initSearchSymbol() {

		// sv_search_symbol = (ScrollView)
		// rootView.findViewById(R.id.sv_search_symbol);
		li_search_tabbegin.setVisibility(View.GONE);
		li_search.setVisibility(View.VISIBLE);

		// search
		final ListAdapterSearchSymbolSystemTradeCdc ListAdapterSearch;
		final ArrayList<CatalogGetSymbol> original_list;
		final ArrayList<CatalogGetSymbol> second_list;

		original_list = new ArrayList<CatalogGetSymbol>();
		original_list.addAll(FragmentChangeActivity.list_getSymbol);
		second_list = new ArrayList<CatalogGetSymbol>();
		second_list.addAll(FragmentChangeActivity.list_getSymbol);

		listview_search = (ListView) rootView
				.findViewById(R.id.list_search_symbol);
		listview_search.setVisibility(View.GONE);

		ListAdapterSearch = new ListAdapterSearchSymbolSystemTradeCdc(context,
				0, second_list);
		listview_search.setAdapter(ListAdapterSearch);

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
					li_view.setVisibility(View.GONE);
					listview_search.setVisibility(View.VISIBLE);

					second_list.clear();
					for (int i = 0; i < original_list.size(); i++) {
						if (original_list.get(i).symbol.toLowerCase().contains(
								text.toString().toLowerCase())) {
							second_list.add(original_list.get(i));
						}
					}
					ListAdapterSearch.notifyDataSetChanged();
				} else {
					li_view.setVisibility(View.VISIBLE);
					listview_search.setVisibility(View.GONE);
				}
			}
		});

		tv_close_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				et_search.setText("");
				li_view.setVisibility(View.VISIBLE);

				li_search_tabbegin.setVisibility(View.VISIBLE);
				li_search.setVisibility(View.GONE);
				// listview_search.setVisibility(View.GONE);
			}
		});
	}

	// / ============ close listsearch ======
	public static void hideListSearch() {
		et_search.setText("");
		li_view.setVisibility(View.VISIBLE);

		li_search_tabbegin.setVisibility(View.VISIBLE);
		li_search.setVisibility(View.GONE);
	}

	// ============== Load Data all =============
	public static void loadDataDetail() {
		loadData resp = new loadData();
		resp.execute();
	}

	public static class loadData extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;
		// ======= json ========
		private JSONObject jsonGetFundDetailList;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialogLoading.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// ======= url ========
			String url_GetFundDetailList = SplashScreen.url_bidschart
					+ "/service/v2/getFundDetailList?cat=" + strCat + "&time="
					+ strTime + "&offset=1&limit=20";
			// http://bidschart.com/service/v2/getFundDetailList?cat=all&time=1D&offset=1&limit=10

			// Log.v("url_GetFundDetailList", url_GetFundDetailList);

			try {
				// ======= Ui Home ========
				jsonGetFundDetailList = ReadJson
						.readJsonObjectFromUrl(url_GetFundDetailList);
			} catch (IOException e1) {
				connectionError = true;
				jsonGetFundDetailList = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonGetFundDetailList = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonGetFundDetailList = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (jsonGetFundDetailList != null) {
					try {
						FragmentChangeActivity.contentGetSystemTradeFundDetailList = jsonGetFundDetailList
								.getJSONArray("data");

						initMenuMutualFund(); // set data

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					dialogLoading.dismiss();
					Log.v("json FundDetailList null", "FundDetailList null");
				}
			} else {
				dialogLoading.dismiss();
			}
		}
	}

	// =========== init data ================
	public static LinearLayout li_time_1d, li_time_1m, li_time_3m, li_time_6m,
			li_time_1y, li_time_ytd;
	public static TextView tv_time_1d, tv_time_1m, tv_time_3m, tv_time_6m,
			tv_time_1y, tv_time_ytd;
	public static View v_time_1d, v_time_1m, v_time_3m, v_time_6m, v_time_1y,
			v_time_ytd;
	public static TextView tv_cat_all, tv_cat_rmf, tv_cat_ltf, tv_cat_etf,
			tv_cat_prc, tv_cat_mmi, tv_cat_mf, tv_cat_fix, tv_cat_fif,
			tv_cat_ef, tv_cat_cf, tv_cat_other;

	// ---- ArrayList row
	public static ArrayList<TextView> arr_time_tv;
	public static ArrayList<View> arr_time_v;
	public static ArrayList<TextView> arr_cat_tv;
	// ---- ArrayList attr
	public static String arrTime[] = { "1D", "1M", "3M", "6M", "1Y", "YTD" };
	public static String arrCat[] = { "ALL", "RMF", "LTF", "ETF", "PRC", "MMI",
			"MF", "FIX", "FIF", "EF", "CF", "OTHER" };
	// ---- str select
	public static String strTime = arrTime[0];
	public static String strCat = arrCat[0];

	public static void initMenuMutualFund() {
		li_time_1d = (LinearLayout) rootView.findViewById(R.id.li_time_1d);
		li_time_1m = (LinearLayout) rootView.findViewById(R.id.li_time_1m);
		li_time_3m = (LinearLayout) rootView.findViewById(R.id.li_time_3m);
		li_time_6m = (LinearLayout) rootView.findViewById(R.id.li_time_6m);
		li_time_1y = (LinearLayout) rootView.findViewById(R.id.li_time_1y);
		li_time_ytd = (LinearLayout) rootView.findViewById(R.id.li_time_ytd);

		tv_time_1d = (TextView) rootView.findViewById(R.id.tv_time_1d);
		tv_time_1m = (TextView) rootView.findViewById(R.id.tv_time_1m);
		tv_time_3m = (TextView) rootView.findViewById(R.id.tv_time_3m);
		tv_time_6m = (TextView) rootView.findViewById(R.id.tv_time_6m);
		tv_time_1y = (TextView) rootView.findViewById(R.id.tv_time_1y);
		tv_time_ytd = (TextView) rootView.findViewById(R.id.tv_time_ytd);

		v_time_1d = (View) rootView.findViewById(R.id.v_time_1d);
		v_time_1m = (View) rootView.findViewById(R.id.v_time_1m);
		v_time_3m = (View) rootView.findViewById(R.id.v_time_3m);
		v_time_6m = (View) rootView.findViewById(R.id.v_time_6m);
		v_time_1y = (View) rootView.findViewById(R.id.v_time_1y);
		v_time_ytd = (View) rootView.findViewById(R.id.v_time_ytd);

		arr_time_tv = new ArrayList<TextView>();
		arr_time_tv.clear();
		arr_time_tv.add(tv_time_1d);
		arr_time_tv.add(tv_time_1m);
		arr_time_tv.add(tv_time_3m);
		arr_time_tv.add(tv_time_6m);
		arr_time_tv.add(tv_time_1y);
		arr_time_tv.add(tv_time_ytd);

		arr_time_v = new ArrayList<View>();
		arr_time_v.clear();
		arr_time_v.add(v_time_1d);
		arr_time_v.add(v_time_1m);
		arr_time_v.add(v_time_3m);
		arr_time_v.add(v_time_6m);
		arr_time_v.add(v_time_1y);
		arr_time_v.add(v_time_ytd);
		// ------------ menu right
		tv_cat_all = (TextView) rootView.findViewById(R.id.tv_cat_all);
		tv_cat_rmf = (TextView) rootView.findViewById(R.id.tv_cat_rmf);
		tv_cat_ltf = (TextView) rootView.findViewById(R.id.tv_cat_ltf);
		tv_cat_etf = (TextView) rootView.findViewById(R.id.tv_cat_etf);

		tv_cat_prc = (TextView) rootView.findViewById(R.id.tv_cat_prc);
		tv_cat_mmi = (TextView) rootView.findViewById(R.id.tv_cat_mmi);
		tv_cat_mf = (TextView) rootView.findViewById(R.id.tv_cat_mf);
		tv_cat_fix = (TextView) rootView.findViewById(R.id.tv_cat_fix);

		tv_cat_fif = (TextView) rootView.findViewById(R.id.tv_cat_fif);
		tv_cat_ef = (TextView) rootView.findViewById(R.id.tv_cat_ef);
		tv_cat_cf = (TextView) rootView.findViewById(R.id.tv_cat_cf);
		tv_cat_other = (TextView) rootView.findViewById(R.id.tv_cat_other);

		arr_cat_tv = new ArrayList<TextView>();
		arr_cat_tv.clear();
		arr_cat_tv.add(tv_cat_all);
		arr_cat_tv.add(tv_cat_rmf);
		arr_cat_tv.add(tv_cat_ltf);
		arr_cat_tv.add(tv_cat_etf);

		arr_cat_tv.add(tv_cat_prc);
		arr_cat_tv.add(tv_cat_mmi);
		arr_cat_tv.add(tv_cat_mf);
		arr_cat_tv.add(tv_cat_fix);

		arr_cat_tv.add(tv_cat_fif);
		arr_cat_tv.add(tv_cat_ef);
		arr_cat_tv.add(tv_cat_cf);
		arr_cat_tv.add(tv_cat_other);

		// onclick tab menu top right
		li_time_1d.setOnClickListener(onClickListenerTabTime);
		li_time_1m.setOnClickListener(onClickListenerTabTime);
		li_time_3m.setOnClickListener(onClickListenerTabTime);
		li_time_6m.setOnClickListener(onClickListenerTabTime);
		li_time_1y.setOnClickListener(onClickListenerTabTime);
		li_time_ytd.setOnClickListener(onClickListenerTabTime);

		tv_cat_all.setOnClickListener(onClickListenerTabCat);
		tv_cat_rmf.setOnClickListener(onClickListenerTabCat);
		tv_cat_ltf.setOnClickListener(onClickListenerTabCat);
		tv_cat_etf.setOnClickListener(onClickListenerTabCat);
		tv_cat_prc.setOnClickListener(onClickListenerTabCat);
		tv_cat_mmi.setOnClickListener(onClickListenerTabCat);
		tv_cat_mf.setOnClickListener(onClickListenerTabCat);
		tv_cat_fix.setOnClickListener(onClickListenerTabCat);
		tv_cat_fif.setOnClickListener(onClickListenerTabCat);
		tv_cat_ef.setOnClickListener(onClickListenerTabCat);
		tv_cat_cf.setOnClickListener(onClickListenerTabCat);
		tv_cat_other.setOnClickListener(onClickListenerTabCat);

		initSetData(); // set data
	}

	// ============= set data ===========
	public static ArrayList<View> arr_risk_v;

	public static void initSetData() {
		try {
			if (FragmentChangeActivity.contentGetSystemTradeFundDetailList != null) {

				LinearLayout list_symbol = (LinearLayout) rootView
						.findViewById(R.id.list_symbol);
				LinearLayout list_detail = (LinearLayout) rootView
						.findViewById(R.id.list_detail);
				list_symbol.removeAllViews();
				list_detail.removeAllViews();

				for (int i = 0; i < FragmentChangeActivity.contentGetSystemTradeFundDetailList
						.length(); i++) {
					View viewSymbol = ((Activity) context).getLayoutInflater()
							.inflate(
									R.layout.row_systemtrade_symbol_mutualfund,
									null);
					View viewDetail = ((Activity) context).getLayoutInflater()
							.inflate(
									R.layout.row_systemtrade_detail_mutualfund,
									null);

					JSONObject jsoIndex = FragmentChangeActivity.contentGetSystemTradeFundDetailList
							.getJSONObject(i);

					// symbol
					String asset_initial = jsoIndex.getString("asset_initial");
					final String name_initial = jsoIndex
							.getString("name_initial");
					final String name_t = jsoIndex.getString("name_t");

					TextView tv_seq = (TextView) viewSymbol
							.findViewById(R.id.tv_seq);
					TextView tv_name = (TextView) viewSymbol
							.findViewById(R.id.tv_name);
					TextView tv_name_t = (TextView) viewSymbol
							.findViewById(R.id.tv_name_t);
					ImageView img_icon = (ImageView) viewSymbol
							.findViewById(R.id.img_icon);

					int r_count = i + 1;
					tv_seq.setText("" + r_count);
					tv_name.setText(name_initial);
					tv_name_t.setText(name_t);

					// image corner radius android

					FragmentChangeActivity.imageLoader.displayImage(
							SplashScreen.url_bidschart_fund + asset_initial
									+ ".gif", img_icon,
							FragmentChangeActivity.optionsRounded);

					// Bitmap bitImg =
					// BitmapFactory.decodeResource(context.getResources(),
					// R.drawable.img_default_border);
					// img_icon.setImageBitmap(getRoundedCornerImage(bitImg));

					// Log.v("url_bidschart_fund", ""
					// + SplashScreen.url_bidschart_fund + asset_initial
					// + ".gif");

					((LinearLayout) viewSymbol.findViewById(R.id.row_symbol))
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									FragmentChangeActivity.strSymbolSelect = name_initial;
									context.startActivity(new Intent(context,
											UiMutualfundDetail.class));
								}
							});

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
//					String dividend_7yr = jsoIndex.getString("dividend_7yr");
					String date_at = jsoIndex.getString("date_at");
					String fund_risk = jsoIndex.getString("fund_risk");

					((LinearLayout) viewDetail.findViewById(R.id.row_detail))
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									FragmentChangeActivity.strSymbolSelect = name_initial;
									context.startActivity(new Intent(context,
											UiMutualfundDetail.class));
								}
							});

					// img chart
					ImageView img_chart = (ImageView) viewDetail
							.findViewById(R.id.img_chart);
					 FragmentChangeActivity.imageLoader.displayImage(
					 SplashScreen.url_bidschart_chart + name_initial
					 + ".png", img_chart);
					 
					LinearLayout li_risk = (LinearLayout) viewDetail
							.findViewById(R.id.li_risk);
					
					TextView tv_value = (TextView) viewDetail
							.findViewById(R.id.tv_value);
					TextView tv_change = (TextView) viewDetail
							.findViewById(R.id.tv_change);
					TextView tv_policy = (TextView) viewDetail
							.findViewById(R.id.tv_policy);
					TextView tv_1mth = (TextView) viewDetail
							.findViewById(R.id.tv_1mth);
					TextView tv_3mth = (TextView) viewDetail
							.findViewById(R.id.tv_3mth);
					TextView tv_6mth = (TextView) viewDetail
							.findViewById(R.id.tv_6mth);
					TextView tv_1yr = (TextView) viewDetail
							.findViewById(R.id.tv_1yr);
					TextView tv_3yr = (TextView) viewDetail
							.findViewById(R.id.tv_3yr);
					TextView tv_5yr = (TextView) viewDetail
							.findViewById(R.id.tv_5yr);
					TextView tv_date = (TextView) viewDetail
							.findViewById(R.id.tv_date);

					View v_risk_1 = (View) viewDetail
							.findViewById(R.id.v_risk_1);
					View v_risk_2 = (View) viewDetail
							.findViewById(R.id.v_risk_2);
					View v_risk_3 = (View) viewDetail
							.findViewById(R.id.v_risk_3);
					View v_risk_4 = (View) viewDetail
							.findViewById(R.id.v_risk_4);
					View v_risk_5 = (View) viewDetail
							.findViewById(R.id.v_risk_5);
					View v_risk_6 = (View) viewDetail
							.findViewById(R.id.v_risk_6);
					View v_risk_7 = (View) viewDetail
							.findViewById(R.id.v_risk_7);
					View v_risk_8 = (View) viewDetail
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
//						li_risk.setBackgroundResource(R.drawable.bg_tab_risk_active);
						setBgFundRisk(fund_risk); // ------ set background fund_risk
					}else{
						li_risk.setBackgroundResource(R.drawable.bg_tab_risk);
					}
					
					RatingBar rb_rating = (RatingBar) viewDetail
							.findViewById(R.id.rb_rating);

					tv_value.setText(invest_value);
					
					if((!invest_change.equals("")) && (!invest_change.equals("null")) && (!invest_change.equals("-"))){
						tv_change.setText(invest_change + "%");
						tv_change.setTextColor(context.getResources().getColor(
								FunctionSetBg
								.setColorCompareWithZero(invest_change)));
						tv_value.setTextColor(context.getResources().getColor(
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
					tv_date.setText(DateTimeCreate.DateCreateMutualFunTh(date_at));

//					tv_value.setTextColor(context.getResources().getColor(
//							BgColorSymbolDetail
//									.setColorMutualFund(invest_value)));				
					tv_1mth.setTextColor(context.getResources().getColor(
							FunctionSetBg
									.setColorCompareWithZero(dividend_1mth)));
					tv_3mth.setTextColor(context.getResources().getColor(
							FunctionSetBg
									.setColorCompareWithZero(dividend_3mth)));
					tv_6mth.setTextColor(context.getResources().getColor(
							FunctionSetBg
									.setColorCompareWithZero(dividend_6mth)));
					tv_1yr.setTextColor(context.getResources().getColor(
							FunctionSetBg
									.setColorCompareWithZero(dividend_1yr)));
					tv_3yr.setTextColor(context.getResources().getColor(
							FunctionSetBg
									.setColorCompareWithZero(dividend_3yr)));
					tv_5yr.setTextColor(context.getResources().getColor(
							FunctionSetBg
									.setColorCompareWithZero(dividend_5yr)));

					if ((!fund_rating.equals(""))
							&& (!fund_rating.equals("N/A"))) {
						rb_rating.setRating(Float.parseFloat(fund_rating.replaceAll(",", "")));
					}

					list_symbol.addView(viewSymbol);
					list_detail.addView(viewDetail);

				}
			} else {
				dialogLoading.dismiss();
			}
		} catch (JSONException e) {
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
						arr_risk_v.get(i).setBackgroundResource(R.drawable.bg_tab_risk_left);
					} else if (i == (arr_risk_v.size()-1)) {
						arr_risk_v.get(i).setBackgroundResource(R.drawable.bg_tab_risk_right);
					} else {
						arr_risk_v.get(i).setBackgroundColor(
								context.getResources().getColor(
										R.color.bg_default));
					}
				}
			}
		return str;
	}

	public static Bitmap getRoundedCornerImage(Bitmap bitmap) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);

		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = 100;

		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);

		return output;

	}

	// ============= on click tab top ===========
	public static OnClickListener onClickListenerTabTime = new OnClickListener() {
		@Override
		public void onClick(final View v) {
			String tag = v.getTag().toString();
			// ------ check str
			strTime = arrTime[Integer.parseInt(tag)];
			// ------ set color select
			for (int i = 0; i < arrTime.length; i++) {
				arr_time_v.get(i).setBackgroundColor(Color.TRANSPARENT);
				arr_time_tv.get(i).setTextColor(
						context.getResources().getColor(R.color.c_content));
				if (strTime.equals(arrTime[i])) {
					arr_time_v.get(i).setBackgroundColor(
							context.getResources().getColor(R.color.c_success));
					arr_time_tv.get(i).setTextColor(
							context.getResources().getColor(R.color.c_success));
				}
			}
			// ------ load data
			loadDataDetail();
		}
	};

	// ============= on click tab right ===========
	public static OnClickListener onClickListenerTabCat = new OnClickListener() {
		@Override
		public void onClick(final View v) {
			String tag = v.getTag().toString();
			// ------ check str
			strCat = arrCat[Integer.parseInt(tag)];
			// ------ set color select
			for (int i = 0; i < arrCat.length; i++) {
				arr_cat_tv.get(i).setBackgroundColor(Color.TRANSPARENT);
				if (strCat.equals(arrCat[i])) {
					arr_cat_tv.get(i).setBackgroundColor(
							context.getResources().getColor(R.color.c_success));
				}
			}
			// ------ load data
			loadDataDetail();
		}
	};

	// the meat of switching the above fragment
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;
		if (getActivity() instanceof FragmentChangeActivity) {
			FragmentChangeActivity fca = (FragmentChangeActivity) getActivity();
			fca.switchContent(fragment);
		}
	}

	// // ============= set tab top ===========
	// public int selectTabTop(int numtabtop) {
	// v_time_1d.setBackgroundColor(Color.TRANSPARENT);
	// v_time_1m.setBackgroundColor(Color.TRANSPARENT);
	// v_time_3m.setBackgroundColor(Color.TRANSPARENT);
	// v_time_6m.setBackgroundColor(Color.TRANSPARENT);
	// v_time_1y.setBackgroundColor(Color.TRANSPARENT);
	// v_time_ytd.setBackgroundColor(Color.TRANSPARENT);
	//
	// tv_time_1d.setTextColor(getResources().getColor(R.color.c_content));
	// tv_time_1m.setTextColor(getResources().getColor(R.color.c_content));
	// tv_time_3m.setTextColor(getResources().getColor(R.color.c_content));
	// tv_time_6m.setTextColor(getResources().getColor(R.color.c_content));
	// tv_time_1y.setTextColor(getResources().getColor(R.color.c_content));
	// tv_time_ytd.setTextColor(getResources().getColor(R.color.c_content));
	//
	// // tv_premium_chart.setTextColor(getResources().getColor(
	// // R.color.bg_default));
	// //
	// // tv_premium_industry
	// // .setBackgroundResource(R.drawable.border_button_activecenter);
	// return numtabtop;
	// }
	//
	// // ============= set tab right ===========
	// public int selectTabRight(int numtabright) {
	// tv_cat_all.setBackgroundColor(Color.TRANSPARENT);
	// tv_cat_rmf.setBackgroundColor(Color.TRANSPARENT);
	// tv_cat_ltf.setBackgroundColor(Color.TRANSPARENT);
	// tv_cat_etf.setBackgroundColor(Color.TRANSPARENT);
	// tv_cat_prc.setBackgroundColor(Color.TRANSPARENT);
	// tv_cat_mmi.setBackgroundColor(Color.TRANSPARENT);
	// tv_cat_mf.setBackgroundColor(Color.TRANSPARENT);
	// tv_cat_fix.setBackgroundColor(Color.TRANSPARENT);
	// tv_cat_fif.setBackgroundColor(Color.TRANSPARENT);
	// tv_cat_ef.setBackgroundColor(Color.TRANSPARENT);
	// tv_cat_cf.setBackgroundColor(Color.TRANSPARENT);
	// tv_cat_other.setBackgroundColor(Color.TRANSPARENT);
	//
	// // tv_premium_chart.setTextColor(getResources().getColor(
	// // R.color.bg_default));
	// //
	// // tv_premium_industry
	// // .setBackgroundResource(R.drawable.border_button_activecenter);
	// return numtabright;
	// }

}