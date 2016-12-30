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
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
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
import android.view.Window;
import android.view.WindowManager;
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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class PagerSystemTradeDusit extends Fragment {

	static Context context;
	public static View rootView;

	private WebSocketClient mWebSocketClient;

	Dialog dialogLoading;

	// --------- google analytics
	// private Tracker mTracker;
	// String nameTracker = new String("Float Volume Spread");

	// list contains fragments to instantiate in the viewpager
	List<Fragment> fragmentMain = new Vector<Fragment>();
	private PagerAdapter mPagerAdapter;
	// view pager
	private ViewPager mPager;

	// activity listener interface
	public int selectTitle = 0;

	public static String url_GetDusitRatio;

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
		rootView = inflater.inflate(R.layout.pager_systemtrade_dusit,
				container, false);

		// getFragmentManager().beginTransaction().remove(FragmentPagerSystemTrade.this).commit();

		// --------- google analytics
		// GoogleAnalyticsApp application = (GoogleAnalyticsApp)
		// getActivity().getApplication();
		// mTracker = application.getDefaultTracker();

		dialogLoading = new Dialog(context);
		dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogLoading.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialogLoading.setContentView(R.layout.progress_bar);
		dialogLoading.setCancelable(false);
		dialogLoading.setCanceledOnTouchOutside(false);

		dialogLoading.show();

		FragmentChangeActivity.id_websocket = 6;

		if (FragmentChangeActivity.contentGetTxtSlidingMarquee != null) {
			initTxtSliding();
		} else {
			loadTxtSlidingMarquee(); // text sliding
		}

		// if(!(FragmentChangeActivity.pageSystemTrade)){
		// FragmentChangeActivity.pageSystemTrade = true;

		url_GetDusitRatio = SplashScreen.url_bidschart
				+ "/service/v2/getDusitRatioConditionV2?user_id="
				+ SplashScreen.userModel.user_id
				+ "&set_get=get&gpm=>20&ebit=>10&ebitda=>10&npm=>10&revenue=>10&eps=>10&current=<2&quick=>1&de=<1&roe=>15";

		if (FragmentChangeActivity.contentGetSystemTradeDusitRatio != null) {
			initListData(); // set data
		} else {
			loadDataDetail(); // load data
		}

		initSearchLayout(); // layout search

		// ((TextView) rootView.findViewById(R.id.tv_portfolio))
		// .setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// FragmentChangeActivity.strWatchlistCategory = "portfolio";
		// switchFragment(new PagerWatchList());
		// }
		// });

		return rootView;

	}

	@Override
	public void onResume() {
		super.onResume();
		// Log.v(nameTracker, "onResume onResume onResume");
		//
		// mTracker.setScreenName(nameTracker);
		// mTracker.send(new HitBuilders.ScreenViewBuilder().build());
	}

	// ***************** text sliding ******************
	private void initTxtSliding() {
		String strSliding = "";
		com.app.custom.CustomTextViewSliding marque = (com.app.custom.CustomTextViewSliding) rootView
				.findViewById(R.id.tv_sliding_marquee);

		ImageView img_status_m = (ImageView) rootView
				.findViewById(R.id.img_status_m);

		try {
			if (FragmentChangeActivity.contentGetTxtSlidingMarquee != null) {
				// status market
				img_status_m.setBackgroundResource(FunctionSetBg
						.setMarketStatus(FragmentChangeActivity.jsonTxtSlidingMarquee
								.getString("market")));

				for (int i = 0; i < FragmentChangeActivity.contentGetTxtSlidingMarquee
						.length(); i++) {
					try {
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
							double db = Double.parseDouble(txtLtrade);
							DecimalFormat formatter = new DecimalFormat(
									"#,###.00");
							txtLtrade = formatter.format(db);
						}

						if (txtChange != "") {
							double db = Double.parseDouble(txtChange);
							txtChange = String.format(" %,.2f", db);
						}

						if (txtSymbol.equals(".SET")) {
							if (txtPchange != "") {
								double dbS = Double.parseDouble(txtSetPChenge);
								txtPchange = FunctionSetBg
										.setColorTxtSlidingSet(txtSetPChenge);
							}
						} else {
							if (txtPchange != "") {
								double dbS = Double.parseDouble(txtSetPChenge);
								double dbO = Double.parseDouble(txtPchange);
								txtPchange = FunctionSetBg.setColorTxtSliding(
										txtSetPChenge, txtPchange);
							}
						}

						strSliding += "   " + txtSymbol + "   " + txtLtrade
								+ "  " + txtChange + "" + txtPchange;

					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		marque.setText(Html.fromHtml(strSliding), TextView.BufferType.SPANNABLE);
		marque.setSelected(true);
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

	public static LinearLayout li_search_tabbegin, li_search_select, li_view;

	public static LinearLayout li_search;

	// public static ScrollView sv_view;
	public static EditText et_search;
	public static TextView tv_close_search, tv_search_common,
			tv_search_warrant, tv_search_dw;
	public static ListView listview_search;

	// String symbol_search_symbol = "";

	public static String status_segmentId = "COMMON"; // COMMON, FOREIGN_STOCK,
														// WARRENT, DW
	public static String status_segmentIdDot = "EQUITY_INDEX";

	public static void initSearchLayout() {

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

	public static void initSearchSymbol() {

		// sv_search_symbol = (ScrollView)
		// rootView.findViewById(R.id.sv_search_symbol);
		li_search_tabbegin.setVisibility(View.GONE);
		li_search.setVisibility(View.VISIBLE);

		// search
		final ListAdapterSearchSymbolSystemTradeDusit ListAdapterSearch;
		final ArrayList<CatalogGetSymbol> original_list;
		final ArrayList<CatalogGetSymbol> second_list;

		original_list = new ArrayList<CatalogGetSymbol>();
		original_list.addAll(FragmentChangeActivity.list_getSymbol);
		second_list = new ArrayList<CatalogGetSymbol>();
		second_list.addAll(FragmentChangeActivity.list_getSymbol);

		tv_search_common = (TextView) rootView
				.findViewById(R.id.tv_search_common);
		tv_search_warrant = (TextView) rootView
				.findViewById(R.id.tv_search_warrant);
		tv_search_dw = (TextView) rootView.findViewById(R.id.tv_search_dw);

		listview_search = (ListView) rootView
				.findViewById(R.id.list_search_symbol);
		listview_search.setVisibility(View.GONE);

		ListAdapterSearch = new ListAdapterSearchSymbolSystemTradeDusit(context,
				0, second_list);
		listview_search.setAdapter(ListAdapterSearch);
		
//		FragmentChangeActivity.strSymbolSelect = symbol_name;
//		FragmentChangeActivity.contentGetSystemTradeDusitRatioSelect = jsoIndex;
//		context.startActivity(new Intent(context,
//				UiDusitValuation.class));

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
							if (((original_list.get(i).status_segmentId)
									.equals(status_segmentId))
									|| ((original_list.get(i).status_segmentId)
											.equals(status_segmentIdDot))) {
								second_list.add(original_list.get(i));
							}
						}
					}
					ListAdapterSearch.notifyDataSetChanged();
				} else {
					li_view.setVisibility(View.VISIBLE);
					listview_search.setVisibility(View.GONE);
				}
			}
		});

		// -------------------------
		tv_search_common.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				status_segmentId = "COMMON"; // COMMON, FOREIGN_STOCK, WARRENT,
												// DW
				setTitleSearch();
				tv_search_common.setTextColor(context.getResources().getColor(
						R.color.bg_default));
				tv_search_common
						.setBackgroundResource(R.drawable.border_search_activeleft);

				second_list.clear();
				for (int i = 0; i < original_list.size(); i++) {
					if (original_list.get(i).symbol.toLowerCase().contains(
							et_search.getText().toString().toLowerCase())) {
						if (((original_list.get(i).status_segmentId)
								.equals(status_segmentId))
								|| ((original_list.get(i).status_segmentId)
										.equals(status_segmentIdDot))) {
							second_list.add(original_list.get(i));
						}
					}
				}
				ListAdapterSearch.notifyDataSetChanged();
			}
		});
		tv_search_warrant.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				status_segmentId = "WARRENT"; // COMMON, FOREIGN_STOCK, WARRENT,
												// DW
				setTitleSearch();
				tv_search_warrant.setTextColor(context.getResources().getColor(
						R.color.bg_default));
				tv_search_warrant
						.setBackgroundResource(R.drawable.border_search_activecenter);

				second_list.clear();
				for (int i = 0; i < original_list.size(); i++) {
					if (original_list.get(i).symbol.toLowerCase().contains(
							et_search.getText().toString().toLowerCase())) {
						if (((original_list.get(i).status_segmentId)
								.equals(status_segmentId))
								|| ((original_list.get(i).status_segmentId)
										.equals(status_segmentIdDot))) {
							second_list.add(original_list.get(i));
						}
					}
				}
				ListAdapterSearch.notifyDataSetChanged();
			}
		});
		tv_search_dw.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				status_segmentId = "DW"; // COMMON, FOREIGN_STOCK, WARRENT, DW
				setTitleSearch();
				tv_search_dw.setTextColor(context.getResources().getColor(
						R.color.bg_default));
				tv_search_dw
						.setBackgroundResource(R.drawable.border_search_activeright);

				second_list.clear();
				for (int i = 0; i < original_list.size(); i++) {
					if (original_list.get(i).symbol.toLowerCase().contains(
							et_search.getText().toString().toLowerCase())) {
						if (((original_list.get(i).status_segmentId)
								.equals(status_segmentId))
								|| ((original_list.get(i).status_segmentId)
										.equals(status_segmentIdDot))) {
							second_list.add(original_list.get(i));
						}
					}
				}
				ListAdapterSearch.notifyDataSetChanged();
			}
		});

		// -------------------------
		tv_close_search.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				hideListSearch();

				// listview_search.setVisibility(View.GONE);

				// setWatchlistSymbol();
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

	// ******* set title search ****
	public static void setTitleSearch() {
		tv_search_common.setTextColor(context.getResources().getColor(
				R.color.c_content));
		tv_search_warrant.setTextColor(context.getResources().getColor(
				R.color.c_content));
		tv_search_dw.setTextColor(context.getResources().getColor(
				R.color.c_content));
		tv_search_common.setBackgroundColor(Color.TRANSPARENT);
		tv_search_warrant.setBackgroundColor(Color.TRANSPARENT);
		tv_search_dw.setBackgroundColor(Color.TRANSPARENT);
	}

	// ============== Load Data all =============
	private void loadDataDetail() {
		loadData resp = new loadData();
		resp.execute();
	}

	public class loadData extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;
		// ======= json ========
		private JSONObject jsonGetDusitRatio;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// ======= url ========
			try {
				// ======= Ui Home ========
				jsonGetDusitRatio = ReadJson
						.readJsonObjectFromUrl(url_GetDusitRatio);
				
				Log.v("url_GetDusitRatio",""+url_GetDusitRatio);
				
			} catch (IOException e1) {
				connectionError = true;
				jsonGetDusitRatio = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonGetDusitRatio = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonGetDusitRatio = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (jsonGetDusitRatio != null) {
					try {
						Log.v("jsonGetDusitRatio",""+jsonGetDusitRatio);
						
						FragmentChangeActivity.contentGetSystemTradeDusitRatio = jsonGetDusitRatio
								.getJSONArray("data");
						FragmentChangeActivity.contentGetSystemTradeDusitRatioDusit = jsonGetDusitRatio
								.getJSONObject("dusit");
						if(FragmentChangeActivity.contentGetSystemTradeDusitRatioDefault == null){
							FragmentChangeActivity.contentGetSystemTradeDusitRatioDefault = jsonGetDusitRatio
									.getJSONObject("default");
							initEditCondition(); // init condition
						}else{
							FragmentChangeActivity.contentGetSystemTradeDusitRatioDefault = jsonGetDusitRatio
									.getJSONObject("default");
						}
						initListData(); // set data
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.v("json newslist null", "newslist null");
				}
			} else {
			}
			dialogLoading.dismiss();
		}
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

			// FragmentChangeActivity.url_bidschart+"/service/v2/symbolByIndustrySector?industry=TECH&sector=TECH&page=1&limit=10
			// ** top = swing , volume , value , gainer , loser
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

	// =========== edit condition================
	public static boolean editConditionShow = false;
	public static TextView tv_edit_sl;
	public static ImageView img_edit_sl;
	public static ScrollView scv_edit_condition;
	public static TextView tv_gpm_sign, tv_ebit_sign, tv_npm_sign,
			tv_revenue_sign, tv_ebitda_sign, tv_eps_sign, tv_current_sign,
			tv_quick_sign, tv_de_sign, tv_roe_sign;
	public static TextView tv_gpm_minus, tv_ebit_minus, tv_npm_minus,
			tv_revenue_minus, tv_ebitda_minus, tv_eps_minus, tv_current_minus,
			tv_quick_minus, tv_de_minus, tv_roe_minus;
	public static TextView tv_gpm_plus, tv_ebit_plus, tv_npm_plus,
			tv_revenue_plus, tv_ebitda_plus, tv_eps_plus, tv_current_plus,
			tv_quick_plus, tv_de_plus, tv_roe_plus;
	public static EditText et_gpm, et_ebit, et_npm, et_revenue, et_ebitda,
			et_eps, et_current, et_quick, et_de, et_roe;
	public static TextView tv_default, tv_apply;

	private void initEditCondition() {
		tv_edit_sl = (TextView) rootView.findViewById(R.id.tv_edit_sl);
		img_edit_sl = (ImageView) rootView.findViewById(R.id.img_edit_sl);

		scv_edit_condition = (ScrollView) rootView
				.findViewById(R.id.scv_edit_condition);
		editConditionShow = false;
		scv_edit_condition.setVisibility(View.GONE);

		tv_edit_sl.setVisibility(View.GONE);
		img_edit_sl.setVisibility(View.VISIBLE);
		tv_edit_sl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (editConditionShow) {
					editConditionShow = false;
					scv_edit_condition.setVisibility(View.GONE);

					tv_edit_sl.setVisibility(View.GONE);
					img_edit_sl.setVisibility(View.VISIBLE);
				} else {
					editConditionShow = true;
					scv_edit_condition.setVisibility(View.VISIBLE);

					tv_edit_sl.setVisibility(View.VISIBLE);
					img_edit_sl.setVisibility(View.GONE);
				}
			}
		});
		img_edit_sl.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (editConditionShow) {
					editConditionShow = false;
					scv_edit_condition.setVisibility(View.GONE);

					tv_edit_sl.setVisibility(View.GONE);
					img_edit_sl.setVisibility(View.VISIBLE);
				} else {
					editConditionShow = true;
					scv_edit_condition.setVisibility(View.VISIBLE);

					tv_edit_sl.setVisibility(View.VISIBLE);
					img_edit_sl.setVisibility(View.GONE);
				}
			}
		});

		tv_gpm_sign = (TextView) rootView.findViewById(R.id.tv_gpm_sign);
		tv_ebit_sign = (TextView) rootView.findViewById(R.id.tv_ebit_sign);
		tv_npm_sign = (TextView) rootView.findViewById(R.id.tv_npm_sign);
		tv_revenue_sign = (TextView) rootView
				.findViewById(R.id.tv_revenue_sign);
		tv_ebitda_sign = (TextView) rootView.findViewById(R.id.tv_ebitda_sign);
		tv_eps_sign = (TextView) rootView.findViewById(R.id.tv_eps_sign);
		tv_current_sign = (TextView) rootView
				.findViewById(R.id.tv_current_sign);
		tv_quick_sign = (TextView) rootView.findViewById(R.id.tv_quick_sign);
		tv_de_sign = (TextView) rootView.findViewById(R.id.tv_de_sign);
		tv_roe_sign = (TextView) rootView.findViewById(R.id.tv_roe_sign);

		tv_gpm_minus = (TextView) rootView.findViewById(R.id.tv_gpm_minus);
		tv_ebit_minus = (TextView) rootView.findViewById(R.id.tv_ebit_minus);
		tv_npm_minus = (TextView) rootView.findViewById(R.id.tv_npm_minus);
		tv_revenue_minus = (TextView) rootView
				.findViewById(R.id.tv_revenue_minus);
		tv_ebitda_minus = (TextView) rootView
				.findViewById(R.id.tv_ebitda_minus);
		tv_eps_minus = (TextView) rootView.findViewById(R.id.tv_eps_minus);
		tv_current_minus = (TextView) rootView
				.findViewById(R.id.tv_current_minus);
		tv_quick_minus = (TextView) rootView.findViewById(R.id.tv_quick_minus);
		tv_de_minus = (TextView) rootView.findViewById(R.id.tv_de_minus);
		tv_roe_minus = (TextView) rootView.findViewById(R.id.tv_roe_minus);
		tv_gpm_minus.setOnClickListener(onClickListenerPlusMinus);
		tv_ebit_minus.setOnClickListener(onClickListenerPlusMinus);
		tv_npm_minus.setOnClickListener(onClickListenerPlusMinus);
		tv_revenue_minus.setOnClickListener(onClickListenerPlusMinus);
		tv_ebitda_minus.setOnClickListener(onClickListenerPlusMinus);
		tv_eps_minus.setOnClickListener(onClickListenerPlusMinus);
		tv_current_minus.setOnClickListener(onClickListenerPlusMinus);
		tv_quick_minus.setOnClickListener(onClickListenerPlusMinus);
		tv_de_minus.setOnClickListener(onClickListenerPlusMinus);
		tv_roe_minus.setOnClickListener(onClickListenerPlusMinus);

		tv_gpm_plus = (TextView) rootView.findViewById(R.id.tv_gpm_plus);
		tv_ebit_plus = (TextView) rootView.findViewById(R.id.tv_ebit_plus);
		tv_npm_plus = (TextView) rootView.findViewById(R.id.tv_npm_plus);
		tv_revenue_plus = (TextView) rootView
				.findViewById(R.id.tv_revenue_plus);
		tv_ebitda_plus = (TextView) rootView.findViewById(R.id.tv_ebitda_plus);
		tv_eps_plus = (TextView) rootView.findViewById(R.id.tv_eps_plus);
		tv_current_plus = (TextView) rootView
				.findViewById(R.id.tv_current_plus);
		tv_quick_plus = (TextView) rootView.findViewById(R.id.tv_quick_plus);
		tv_de_plus = (TextView) rootView.findViewById(R.id.tv_de_plus);
		tv_roe_plus = (TextView) rootView.findViewById(R.id.tv_roe_plus);
		tv_gpm_plus.setOnClickListener(onClickListenerPlusMinus);
		tv_ebit_plus.setOnClickListener(onClickListenerPlusMinus);
		tv_npm_plus.setOnClickListener(onClickListenerPlusMinus);
		tv_revenue_plus.setOnClickListener(onClickListenerPlusMinus);
		tv_ebitda_plus.setOnClickListener(onClickListenerPlusMinus);
		tv_eps_plus.setOnClickListener(onClickListenerPlusMinus);
		tv_current_plus.setOnClickListener(onClickListenerPlusMinus);
		tv_quick_plus.setOnClickListener(onClickListenerPlusMinus);
		tv_de_plus.setOnClickListener(onClickListenerPlusMinus);
		tv_roe_plus.setOnClickListener(onClickListenerPlusMinus);

		et_gpm = (EditText) rootView.findViewById(R.id.et_gpm);
		et_ebit = (EditText) rootView.findViewById(R.id.et_ebit);
		et_npm = (EditText) rootView.findViewById(R.id.et_npm);
		et_revenue = (EditText) rootView.findViewById(R.id.et_revenue);
		et_ebitda = (EditText) rootView.findViewById(R.id.et_ebitda);
		et_eps = (EditText) rootView.findViewById(R.id.et_eps);
		et_current = (EditText) rootView.findViewById(R.id.et_current);
		et_quick = (EditText) rootView.findViewById(R.id.et_quick);
		et_de = (EditText) rootView.findViewById(R.id.et_de);
		et_roe = (EditText) rootView.findViewById(R.id.et_roe);
		editEditText(); // EditCondition EditText

		tv_default = (TextView) rootView.findViewById(R.id.tv_default);
		tv_apply = (TextView) rootView.findViewById(R.id.tv_apply);
		tv_default.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentChangeActivity.contentGetSystemTradeDusitRatioDusit = FragmentChangeActivity.contentGetSystemTradeDusitRatioDefault;
				setDataDefault();
			}
		});
		tv_apply.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				editConditionShow = false;
				scv_edit_condition.setVisibility(View.GONE);
				tv_edit_sl.setVisibility(View.GONE);
				img_edit_sl.setVisibility(View.VISIBLE);
				
				String strGpm = tv_gpm_sign.getText() + ""
						+ et_gpm.getText().toString();
				String strEbit = tv_ebit_sign.getText() + ""
						+ et_ebit.getText().toString();
				String strNpm = tv_npm_sign.getText() + ""
						+ et_npm.getText().toString();
				String strRevenue = tv_revenue_sign.getText() + ""
						+ et_revenue.getText().toString();
				String strEbitda = tv_ebitda_sign.getText() + ""
						+ et_ebitda.getText().toString();
				String strEps = tv_eps_sign.getText() + ""
						+ et_eps.getText().toString();
				String strCurrent = tv_current_sign.getText() + ""
						+ et_current.getText().toString();
				String strQuick = tv_quick_sign.getText() + ""
						+ et_quick.getText().toString();
				String strDe = tv_de_sign.getText() + ""
						+ et_de.getText().toString();
				String strRoe = tv_roe_sign.getText() + ""
						+ et_roe.getText().toString();

				url_GetDusitRatio = SplashScreen.url_bidschart
						+ "/service/v2/getDusitRatioConditionV2?user_id="
						+ SplashScreen.userModel.user_id + "&set_get=get"
						+ "&gpm=" + strGpm + "&ebit=" + strEbit + "&ebitda="
						+ strEbitda + "&npm=" + strNpm + "&revenue="
						+ strRevenue + "&eps=" + strEps + "&current="
						+ strCurrent + "&quick=" + strQuick + "&de=" + strDe
						+ "&roe=" + strRoe;
				
				Log.v("url_GetDusitRatio apply",""+url_GetDusitRatio);
				
				loadDataDetail(); // load data
			}
		});
		setDataDefault(); // set data default
	}

	// ============= set data default ===========
	private void setDataDefault() {
		try{
			if (FragmentChangeActivity.contentGetSystemTradeDusitRatioDefault != null) {

				Log.v("contentGetSystemTradeDusitRatioDefault",
						""
								+ FragmentChangeActivity.contentGetSystemTradeDusitRatioDefault);

				// {"gpm":">20","ebit":">10","ebitda":">10","npm":">10","revenue":">10","eps":">10","current":"<2","quick":"<1","de":"<1","roe":">15"}

				// org.json.JSONException: Value >20 at gpm of type
				// java.lang.String cannot be converted to JSONObject

				String gpm = FragmentChangeActivity.contentGetSystemTradeDusitRatioDefault
						.getString("gpm");
				String ebit = FragmentChangeActivity.contentGetSystemTradeDusitRatioDefault
						.getString("ebit");
				String ebitda = FragmentChangeActivity.contentGetSystemTradeDusitRatioDefault
						.getString("ebitda");
				String npm = FragmentChangeActivity.contentGetSystemTradeDusitRatioDefault
						.getString("npm");
				String revenue = FragmentChangeActivity.contentGetSystemTradeDusitRatioDefault
						.getString("revenue");
				String eps = FragmentChangeActivity.contentGetSystemTradeDusitRatioDefault
						.getString("eps");
				String current = FragmentChangeActivity.contentGetSystemTradeDusitRatioDefault
						.getString("current");
				String quick = FragmentChangeActivity.contentGetSystemTradeDusitRatioDefault
						.getString("quick");
				String de = FragmentChangeActivity.contentGetSystemTradeDusitRatioDefault
						.getString("de");
				String roe = FragmentChangeActivity.contentGetSystemTradeDusitRatioDefault
						.getString("roe");

				tv_gpm_sign.setText("" + gpm.charAt(0));
				tv_ebit_sign.setText("" + ebit.charAt(0));
				tv_npm_sign.setText("" + npm.charAt(0));
				tv_revenue_sign.setText("" + revenue.charAt(0));
				tv_ebitda_sign.setText("" + ebitda.charAt(0));
				tv_eps_sign.setText("" + eps.charAt(0));
				tv_current_sign.setText("" + current.charAt(0));
				tv_quick_sign.setText("" + quick.charAt(0));
				tv_de_sign.setText("" + de.charAt(0));
				tv_roe_sign.setText("" + roe.charAt(0));
				tv_gpm_sign.setOnClickListener(onClickListenerEditSign);
				tv_ebit_sign.setOnClickListener(onClickListenerEditSign);
				tv_npm_sign.setOnClickListener(onClickListenerEditSign);
				tv_revenue_sign.setOnClickListener(onClickListenerEditSign);
				tv_ebitda_sign.setOnClickListener(onClickListenerEditSign);
				tv_eps_sign.setOnClickListener(onClickListenerEditSign);
				tv_current_sign.setOnClickListener(onClickListenerEditSign);
				tv_quick_sign.setOnClickListener(onClickListenerEditSign);
				tv_de_sign.setOnClickListener(onClickListenerEditSign);
				tv_roe_sign.setOnClickListener(onClickListenerEditSign);

				et_gpm.setText(gpm.substring(1, gpm.length()));
				et_ebit.setText(ebit.substring(1, ebit.length()));
				et_npm.setText(npm.substring(1, npm.length()));
				et_revenue.setText(revenue.substring(1, revenue.length()));
				et_ebitda.setText(ebitda.substring(1, ebitda.length()));
				et_eps.setText(eps.substring(1, eps.length()));
				et_current.setText(current.substring(1, current.length()));
				et_quick.setText(quick.substring(1, quick.length()));
				et_de.setText(de.substring(1, de.length()));
				et_roe.setText(roe.substring(1, roe.length()));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// ============= EditCondition Plus Minus ===========
	public static OnClickListener onClickListenerPlusMinus = new OnClickListener() {
		@Override
		public void onClick(final View v) {

			// Toast.makeText(context, "" + v.getId(), 0).show();
			String text;
			double dText;
			switch (v.getId()) {
			case R.id.tv_gpm_minus:
				text = et_gpm.getText().toString();
				if( (text.equals("N/A")) || (text.equals(""))){
					text = "0";
				}
				dText = Double.parseDouble(text);
				et_gpm.setText("" + (dText - 1));
				break;
			case R.id.tv_ebit_minus:
				text = et_ebit.getText().toString();
				if( (text.equals("N/A")) || (text.equals(""))){
					text = "0";
				}
				dText = Double.parseDouble(text);
				et_ebit.setText("" + (dText - 1));
				break;
			case R.id.tv_npm_minus:
				text = et_npm.getText().toString();
				if( (text.equals("N/A")) || (text.equals(""))){
					text = "0";
				}
				dText = Double.parseDouble(text);
				et_npm.setText("" + (dText - 1));
				break;
			case R.id.tv_revenue_minus:
				text = et_revenue.getText().toString();
				if( (text.equals("N/A")) || (text.equals(""))){
					text = "0";
				}
				dText = Double.parseDouble(text);
				et_revenue.setText("" + (dText - 1));
				break;
			case R.id.tv_ebitda_minus:
				text = et_ebitda.getText().toString();
				if( (text.equals("N/A")) || (text.equals(""))){
					text = "0";
				}
				dText = Double.parseDouble(text);
				et_ebitda.setText("" + (dText - 1));
				break;
			case R.id.tv_eps_minus:
				text = et_eps.getText().toString();
				if( (text.equals("N/A")) || (text.equals(""))){
					text = "0";
				}
				dText = Double.parseDouble(text);
				et_eps.setText("" + (dText - 1));
				break;
			case R.id.tv_current_minus:
				text = et_current.getText().toString();
				if( (text.equals("N/A")) || (text.equals(""))){
					text = "0";
				}
				dText = Double.parseDouble(text);
				et_current.setText(FunctionFormatData.setFormat2Digit(""+(dText - 0.10)));
				break;
			case R.id.tv_quick_minus:
				text = et_quick.getText().toString();
				if( (text.equals("N/A")) || (text.equals(""))){
					text = "0";
				}
				dText = Double.parseDouble(text);
				et_quick.setText(FunctionFormatData.setFormat2Digit(""+(dText - 0.10)));
				break;
			case R.id.tv_de_minus:
				text = et_de.getText().toString();
				if( (text.equals("N/A")) || (text.equals(""))){
					text = "0";
				}
				dText = Double.parseDouble(text);
				et_de.setText(FunctionFormatData.setFormat2Digit(""+(dText - 0.10)));
				break;
			case R.id.tv_roe_minus:
				text = et_roe.getText().toString();
				if( (text.equals("N/A")) || (text.equals(""))){
					text = "0";
				}
				dText = Double.parseDouble(text);
				et_roe.setText("" + (dText - 1));
				break;
			case R.id.tv_gpm_plus:
				text = et_gpm.getText().toString();
				if( (text.equals("N/A")) || (text.equals(""))){
					text = "0";
				}
				dText = Double.parseDouble(text);
				et_gpm.setText("" + (dText + 1));
				break;
			case R.id.tv_ebit_plus:
				text = et_ebit.getText().toString();
				if( (text.equals("N/A")) || (text.equals(""))){
					text = "0";
				}
				dText = Double.parseDouble(text);
				et_ebit.setText("" + (dText + 1));
				break;
			case R.id.tv_npm_plus:
				text = et_npm.getText().toString();
				if( (text.equals("N/A")) || (text.equals(""))){
					text = "0";
				}
				dText = Double.parseDouble(text);
				et_npm.setText("" + (dText + 1));
				break;
			case R.id.tv_revenue_plus:
				text = et_revenue.getText().toString();
				if( (text.equals("N/A")) || (text.equals(""))){
					text = "0";
				}
				dText = Double.parseDouble(text);
				et_revenue.setText("" + (dText + 1));
				break;
			case R.id.tv_ebitda_plus:
				text = et_ebitda.getText().toString();
				if( (text.equals("N/A")) || (text.equals(""))){
					text = "0";
				}
				dText = Double.parseDouble(text);
				et_ebitda.setText("" + (dText + 1));
				break;
			case R.id.tv_eps_plus:
				text = et_eps.getText().toString();
				if( (text.equals("N/A")) || (text.equals(""))){
					text = "0";
				}
				dText = Double.parseDouble(text);
				et_eps.setText("" + (dText + 1));
				break;
			case R.id.tv_current_plus:
				text = et_current.getText().toString();
				if( (text.equals("N/A")) || (text.equals(""))){
					text = "0";
				}
				dText = Double.parseDouble(text);
				et_current.setText(FunctionFormatData.setFormat2Digit(""+(dText + 0.10)));
				break;
			case R.id.tv_quick_plus:
				text = et_quick.getText().toString();
				if( (text.equals("N/A")) || (text.equals(""))){
					text = "0";
				}
				dText = Double.parseDouble(text);
				et_quick.setText(FunctionFormatData.setFormat2Digit(""+(dText + 0.10)));
				break;
			case R.id.tv_de_plus:
				text = et_de.getText().toString();
				if( (text.equals("N/A")) || (text.equals(""))){
					text = "0";
				}
				dText = Double.parseDouble(text);
				et_de.setText(FunctionFormatData.setFormat2Digit(""+(dText + 0.10)));
				break;
			case R.id.tv_roe_plus:
				text = et_roe.getText().toString();
				if( (text.equals("N/A")) || (text.equals(""))){
					text = "0";
				}
				dText = Double.parseDouble(text);
				et_roe.setText("" + (dText + 1));
				break;
			default:
				break;
			}
		}
	};

	// ============= EditCondition EditText ===========
	private void editEditText() {
		et_gpm.addTextChangedListener(new TextWatcher() {
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
				String text = et_gpm.getText().toString();

			}
		});
		et_ebit.addTextChangedListener(new TextWatcher() {
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
				String text = et_ebit.getText().toString();

			}
		});
		et_npm.addTextChangedListener(new TextWatcher() {
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
				String text = et_npm.getText().toString();

			}
		});
		et_revenue.addTextChangedListener(new TextWatcher() {
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
				String text = et_revenue.getText().toString();

			}
		});
		et_ebitda.addTextChangedListener(new TextWatcher() {
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
				String text = et_ebitda.getText().toString();

			}
		});
		et_eps.addTextChangedListener(new TextWatcher() {
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
				String text = et_eps.getText().toString();

			}
		});
		et_current.addTextChangedListener(new TextWatcher() {
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
				String text = et_current.getText().toString();

			}
		});
		et_quick.addTextChangedListener(new TextWatcher() {
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
				String text = et_quick.getText().toString();

			}
		});
		et_de.addTextChangedListener(new TextWatcher() {
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
				String text = et_de.getText().toString();

			}
		});
		et_roe.addTextChangedListener(new TextWatcher() {
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
				String text = et_roe.getText().toString();

			}
		});
	}

	// -------- on click edit sign -----------
	public static Dialog alertDialogSetAlert;
	public static OnClickListener onClickListenerEditSign = new OnClickListener() {
		@Override
		public void onClick(final View v) {

			final View vId = v;
			
			alertDialogSetAlert = new Dialog(context);
			LayoutInflater inflater = LayoutInflater.from(context);
			View dlView = inflater
					.inflate(R.layout.dialog_condition_sign, null);
			// perform operation on elements in Layout
			alertDialogSetAlert.requestWindowFeature(Window.FEATURE_NO_TITLE);
			alertDialogSetAlert.setContentView(dlView);

			TextView tv_left = (TextView) dlView.findViewById(R.id.tv_left);
			TextView tv_tantamount = (TextView) dlView
					.findViewById(R.id.tv_tantamount);
			TextView tv_right = (TextView) dlView.findViewById(R.id.tv_right);
			TextView tv_cancle = (TextView) dlView.findViewById(R.id.tv_cancle);

			tv_left.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					alertDialogSetAlert.dismiss();
					switch (vId.getId()) {
					case R.id.tv_gpm_sign:
						tv_gpm_sign.setText("<");
						break;
					case R.id.tv_ebit_sign:
						tv_ebit_sign.setText("<");
						break;
					case R.id.tv_npm_sign:
						tv_npm_sign.setText("<");
						break;
					case R.id.tv_revenue_sign:
						tv_revenue_sign.setText("<");
						break;
					case R.id.tv_ebitda_sign:
						tv_ebitda_sign.setText("<");
						break;
					case R.id.tv_eps_sign:
						tv_eps_sign.setText("<");
						break;
					case R.id.tv_current_sign:
						tv_current_sign.setText("<");
						break;
					case R.id.tv_quick_sign:
						tv_quick_sign.setText("<");
						break;
					case R.id.tv_de_sign:
						tv_de_sign.setText("<");
						break;
					case R.id.tv_roe_sign:
						tv_roe_sign.setText("<");
						break;
					default:
						break;
					}
				}
			});

			tv_tantamount.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					alertDialogSetAlert.dismiss();
					switch (vId.getId()) {
					case R.id.tv_gpm_sign:
						tv_gpm_sign.setText("=");
						break;
					case R.id.tv_ebit_sign:
						tv_ebit_sign.setText("=");
						break;
					case R.id.tv_npm_sign:
						tv_npm_sign.setText("=");
						break;
					case R.id.tv_revenue_sign:
						tv_revenue_sign.setText("=");
						break;
					case R.id.tv_ebitda_sign:
						tv_ebitda_sign.setText("=");
						break;
					case R.id.tv_eps_sign:
						tv_eps_sign.setText("=");
						break;
					case R.id.tv_current_sign:
						tv_current_sign.setText("=");
						break;
					case R.id.tv_quick_sign:
						tv_quick_sign.setText("=");
						break;
					case R.id.tv_de_sign:
						tv_de_sign.setText("=");
						break;
					case R.id.tv_roe_sign:
						tv_roe_sign.setText("=");
						break;
					default:
						break;
					}
				}
			});

			tv_right.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					alertDialogSetAlert.dismiss();
					switch (vId.getId()) {
					case R.id.tv_gpm_sign:
						tv_gpm_sign.setText(">");
						break;
					case R.id.tv_ebit_sign:
						tv_ebit_sign.setText(">");
						break;
					case R.id.tv_npm_sign:
						tv_npm_sign.setText(">");
						break;
					case R.id.tv_revenue_sign:
						tv_revenue_sign.setText(">");
						break;
					case R.id.tv_ebitda_sign:
						tv_ebitda_sign.setText(">");
						break;
					case R.id.tv_eps_sign:
						tv_eps_sign.setText(">");
						break;
					case R.id.tv_current_sign:
						tv_current_sign.setText(">");
						break;
					case R.id.tv_quick_sign:
						tv_quick_sign.setText(">");
						break;
					case R.id.tv_de_sign:
						tv_de_sign.setText(">");
						break;
					case R.id.tv_roe_sign:
						tv_roe_sign.setText(">");
						break;
					default:
						break;
					}
				}
			});

			tv_cancle.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					alertDialogSetAlert.dismiss();
				}
			});

			alertDialogSetAlert.show();

			// set width height dialog
			DisplayMetrics displaymetrics = new DisplayMetrics();
			WindowManager windowManager = (WindowManager) context
					.getSystemService(Context.WINDOW_SERVICE);
			windowManager.getDefaultDisplay().getMetrics(displaymetrics);

			int ScreenHeight = displaymetrics.heightPixels;
			int ScreenWidth = displaymetrics.widthPixels;

			WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
			lp.gravity = Gravity.RIGHT;
			// lp.width = (int) (ScreenWidth * 0.87);
			// lp.height = (int) (ScreenHeight * 0.67);
			// alertDialogSetAlert.getWindow().setAttributes(lp);

			alertDialogSetAlert.getWindow().setBackgroundDrawable(
					new ColorDrawable(android.graphics.Color.TRANSPARENT));
		}
	};

	// =========== set data ================
	public static ArrayList<LinearLayout> row_li_watchlist;
	public static ArrayList<TextView> row_tv_orderbook_id;
	public static ArrayList<TextView> row_tv_symbol_name;
	public static ArrayList<TextView> row_tv_last_trade;
	public static ArrayList<TextView> row_tv_change;
	public static ArrayList<TextView> row_tv_percent_change;

	// ============= set data ===========
	TextView tv_count, tv_detail;

	private void initListData() {		
		tv_count = (TextView) rootView.findViewById(R.id.tv_count);
		tv_detail = (TextView) rootView.findViewById(R.id.tv_detail);

		tv_detail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Dialog dialog = new Dialog(context);
				LayoutInflater inflater = LayoutInflater.from(context);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				View dlView = inflater.inflate(R.layout.dialog_detail_dusit,
						null);
				// perform operation on elements in
				// Layout
				dialog.setContentView(dlView);

				((TextView) dlView.findViewById(R.id.tv_link))
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
								startActivity(new Intent(context,
										UiDusitDetailWebView.class));
							}
						});
				dialog.show();
			}
		});

		try {
			if (FragmentChangeActivity.contentGetSystemTradeDusitRatio != null) {
				tv_count.setText(""
						+ FragmentChangeActivity.contentGetSystemTradeDusitRatio
								.length());

				LinearLayout list_symbol = (LinearLayout) rootView
						.findViewById(R.id.list_symbol);
				LinearLayout list_detail = (LinearLayout) rootView
						.findViewById(R.id.list_detail);
				list_symbol.removeAllViews();
				list_detail.removeAllViews();
				
				// ---- custom row
				row_li_watchlist = new ArrayList<LinearLayout>();
				row_tv_orderbook_id = new ArrayList<TextView>();
				row_tv_symbol_name = new ArrayList<TextView>();
				row_tv_last_trade = new ArrayList<TextView>();
				row_tv_change = new ArrayList<TextView>();
				row_tv_percent_change = new ArrayList<TextView>();

				row_li_watchlist.clear();
				row_tv_orderbook_id.clear();
				row_tv_symbol_name.clear();
				row_tv_last_trade.clear();
				row_tv_change.clear();
				row_tv_percent_change.clear();

				for (int i = 0; i < FragmentChangeActivity.contentGetSystemTradeDusitRatio
						.length(); i++) {
					View viewSymbol = ((Activity) context).getLayoutInflater()
							.inflate(R.layout.row_systemtrade_symbol, null);
					View viewDetail = ((Activity) context).getLayoutInflater()
							.inflate(R.layout.row_systemtrade_detail_dusit,
									null);

					final JSONObject jsoIndex = FragmentChangeActivity.contentGetSystemTradeDusitRatio
							.getJSONObject(i);

					// symbol
					final String symbol_name = jsoIndex.getString("symbol");
					// String turnover_list_level = jsoIndex
					// .getString("turnover_list_level");
					// String status = jsoIndex.getString("status");
					// String status_xd = jsoIndex.getString("status_xd");

					TextView tv_symbol_name = (TextView) viewSymbol
							.findViewById(R.id.tv_symbol_name);
					tv_symbol_name.setText(symbol_name);

					// tv_symbol_name
					// .setText(Html.fromHtml(FunctionSymbol
					// .checkStatusSymbol(symbol_name,
					// turnover_list_level, status,
					// status_xd)));

					((TextView) viewSymbol
							.findViewById(R.id.tv_symbol_fullname_eng))
							.setText(jsoIndex.getString("symbol_fullname_eng"));

					((LinearLayout) viewSymbol.findViewById(R.id.row_symbol))
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									FragmentChangeActivity.strSymbolSelect = symbol_name;
									FragmentChangeActivity.contentGetSystemTradeDusitRatioSelect = jsoIndex;
									context.startActivity(new Intent(context,
											UiDusitValuation.class));
								}
							});

					// ------------ check package
					View v_sft = (View) rootView.findViewById(R.id.v_sft);
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

					// detail
					((LinearLayout) viewDetail.findViewById(R.id.row_detail))
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									FragmentChangeActivity.strSymbolSelect = symbol_name;
									FragmentChangeActivity.contentGetSystemTradeDusitRatioSelect = jsoIndex;
									context.startActivity(new Intent(context,
											UiDusitValuation.class));
								}
							});

					// img chart
					ImageView img_chart = (ImageView) viewDetail
							.findViewById(R.id.img_chart);
					FragmentChangeActivity.imageLoader.displayImage(
							SplashScreen.url_bidschart_chart + symbol_name
									+ ".png", img_chart);

					// ck ltrade change
					String strLastTrade = jsoIndex.getString("last_trade");
					String strChange = jsoIndex.getString("change");
					String strPercentChange = jsoIndex
							.getString("percentChange");

					TextView tv_last_trade = (TextView) viewDetail
							.findViewById(R.id.tv_last_trade);
					TextView tv_change = (TextView) viewDetail
							.findViewById(R.id.tv_change);
					TextView tv_percentChange = (TextView) viewDetail
							.findViewById(R.id.tv_percentChange);

					tv_last_trade.setText(strLastTrade);
					tv_change.setText(strChange);
					if ((strPercentChange == "0") || (strPercentChange == "")) {
						tv_percentChange.setText("0.00");
					} else {
						tv_percentChange.setText(strPercentChange + "%");
					}

					// เซตสี change , lasttrade, percentchange เป็นสีตาม
					// change โดยเอา change เทียบกับ 0
					if (strChange != "") {
						tv_change.setTextColor(context.getResources().getColor(
								FunctionSetBg.setColor(strChange)));
						tv_last_trade.setTextColor(context.getResources()
								.getColor(FunctionSetBg.setColor(strChange)));
						tv_percentChange.setTextColor(context.getResources()
								.getColor(FunctionSetBg.setColor(strChange)));
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
					String strEps = jsoIndex.getString("eps");
					String strBv = jsoIndex.getString("bv");
					String strSales_growth = jsoIndex.getString("sales_growth");
					String strAvg_roe = jsoIndex.getString("avg_roe");
					String strAvg_gpm = jsoIndex.getString("avg_gpm");
					String strAvg_ebit_percent = jsoIndex
							.getString("avg_ebit_percent");
					String strAvg_npm = jsoIndex.getString("avg_npm");
					String strAvg_revenue_growth = jsoIndex
							.getString("avg_revenue_growth");
					String strAvg_ebitda_growth = jsoIndex
							.getString("avg_ebitda_growth");
					String strAvg_eps_growth = jsoIndex
							.getString("avg_eps_growth");
					String strCurrent_ratio = jsoIndex
							.getString("current_ratio");
					String strQuick_ratio = jsoIndex.getString("quick_ratio");
					String strDe = jsoIndex.getString("de");
					String strTrendSignal_avg_percentChange = jsoIndex
							.getString("trendSignal_avg_percentChange");
					String strColor_macd = jsoIndex.getString("color_macd");
					String strFundamental = jsoIndex.getString("fundamental");

					TextView tv_trendSignal_avg_percent = (TextView) viewDetail
							.findViewById(R.id.tv_trendSignal_avg_percent);
					TextView tv_fundamental = (TextView) viewDetail
							.findViewById(R.id.tv_fundamental);
					TextView tv_color_macd = (TextView) viewDetail
							.findViewById(R.id.tv_color_macd);

					tv_trendSignal_avg_percent
							.setBackgroundColor(FunctionSetBg
									.setColorWatchListSymbolTrendSignal(strTrendSignal_avg_percentChange));
					tv_fundamental
							.setBackgroundColor(FunctionSetBg
									.setColorWatchListSymbolFundamental(strFundamental));
					tv_color_macd.setBackgroundColor(FunctionSetBg
							.setColorWatchListSymbolColorMacd(strColor_macd));

					// --------------------
					TextView tv_gpm = (TextView) viewDetail
							.findViewById(R.id.tv_gpm);
					TextView tv_ebit_magit = (TextView) viewDetail
							.findViewById(R.id.tv_ebit_magit);
					TextView tv_npm = (TextView) viewDetail
							.findViewById(R.id.tv_npm);

					TextView tv_roe = (TextView) viewDetail
							.findViewById(R.id.tv_roe);
					TextView tv_eps = (TextView) viewDetail
							.findViewById(R.id.tv_eps);
					TextView tv_bv = (TextView) viewDetail
							.findViewById(R.id.tv_bv);

					TextView tv_sale_growth = (TextView) viewDetail
							.findViewById(R.id.tv_sale_growth);
					TextView tv_revenue_growth = (TextView) viewDetail
							.findViewById(R.id.tv_revenue_growth);
					TextView tv_ebitda_growth = (TextView) viewDetail
							.findViewById(R.id.tv_ebitda_growth);
					TextView tv_eps_growth = (TextView) viewDetail
							.findViewById(R.id.tv_eps_growth);

					TextView tv_current_ratio = (TextView) viewDetail
							.findViewById(R.id.tv_current_ratio);
					TextView tv_quick_ratio = (TextView) viewDetail
							.findViewById(R.id.tv_quick_ratio);
					TextView tv_de = (TextView) viewDetail
							.findViewById(R.id.tv_de);

					tv_gpm.setText(strAvg_gpm);
					tv_ebit_magit.setText(strAvg_ebit_percent);
					tv_npm.setText(strAvg_npm);
					tv_roe.setText(strAvg_roe);
					tv_eps.setText(strAvg_eps_growth);
					tv_bv.setText(strBv);
					tv_sale_growth.setText(strSales_growth);
					tv_revenue_growth.setText(strAvg_revenue_growth);
					tv_ebitda_growth.setText(strAvg_ebitda_growth);
					tv_eps_growth.setText(strAvg_eps_growth);
					tv_current_ratio.setText(strCurrent_ratio);
					tv_quick_ratio.setText(strQuick_ratio);
					tv_de.setText(strDe);
					
					// --- add row update
					// row_tv_orderbook_id.add();
					row_tv_symbol_name.add(tv_symbol_name);
					row_tv_last_trade.add(tv_last_trade);
					row_tv_change.add(tv_change);
					row_tv_percent_change.add(tv_percentChange);

					// --- add view tv
					list_symbol.addView(viewSymbol);
					list_detail.addView(viewDetail);

				}

				dialogLoading.dismiss();

				// ------ connect socket -----------------
				// -------- orderbook for connectsocket
				FragmentChangeActivity.strGetSymbolOrderBook_Id = "";
				for (int i = 0; i < FragmentChangeActivity.contentGetWatchlistSymbol
						.length(); i++) {
					FragmentChangeActivity.strGetListSymbol += FragmentChangeActivity.contentGetWatchlistSymbol
							.getJSONObject(i).getString("orderbook_id");

					String strOrerBookId = FragmentChangeActivity.contentGetWatchlistSymbol
							.getJSONObject(i).getString("orderbook_id");
					if (i == 0) {
						FragmentChangeActivity.strGetSymbolOrderBook_Id = strOrerBookId;
					} else {
						FragmentChangeActivity.strGetSymbolOrderBook_Id += ","
								+ strOrerBookId;
					}
				}

				if (FragmentChangeActivity.strGetSymbolOrderBook_Id != "") {
					if (!(SplashScreen.contentGetUserById.getString("package")
							.equals("free"))) {
						connectWebSocket();
					}
				}
				
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		dialogLoading.dismiss();
	}

	// ============= connect socket ===============
	public static JSONObject jsoConnectSocket;
	public static JSONArray jsaMassageSocket;

	public void connectWebSocket() {
		URI uri;
		try {
			uri = new URI(SplashScreen.url_websocket);
		} catch (URISyntaxException e) {
			e.printStackTrace();
			return;
		}

		mWebSocketClient = new WebSocketClient(uri) {
			@Override
			public void onOpen(ServerHandshake serverHandshake) {

				jsoConnectSocket = new JSONObject();
				try {
					jsoConnectSocket.put("id",
							FragmentChangeActivity.id_websocket);
					jsoConnectSocket.put("user_id", 6);
					jsoConnectSocket.put("orderbook_list",
							FragmentChangeActivity.strGetSymbolOrderBook_Id);
					jsoConnectSocket.put("date", "");
					jsoConnectSocket.put("hh", "");
					jsoConnectSocket.put("mm", "");
					jsoConnectSocket.put("requestType", "watchList");
					jsoConnectSocket.put("page", "Dusit");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Log.v("jsonObj send connect", jsoConnectSocket.toString());

				mWebSocketClient.send(jsoConnectSocket.toString());
			}

			@Override
			public void onMessage(String s) {
				final String message = s;
				if (getActivity() != null) {
					getActivity().runOnUiThread(new Runnable() {
						@Override
						public void run() {
							try {
								jsaMassageSocket = new JSONArray(message);
								Log.v("jsaMassageSocket", "" + jsaMassageSocket);

								if (jsaMassageSocket.get(0).toString()
										.equals("3")) {
									changeRowUpdate();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					});
				}
			}

			@Override
			public void onClose(int i, String s, boolean b) {
				Log.v("Websocket", "Closed " + s);
			}

			@Override
			public void onError(Exception e) {
				Log.v("Websocket", "Error " + e.getMessage());
			}
		};

		mWebSocketClient.connect();

		// if (mWebSocketClient.connect()) {
		ImageView img_connect_c = (ImageView) rootView
				.findViewById(R.id.img_connect_c);
		img_connect_c.setBackgroundResource(R.drawable.icon_connect_c_green);
		// }

	}

	// ============== Set update row ===============
	public void changeRowUpdate() {

		Log.v("changeRow dusit", "dusit dusit");
		try {
			int indexRow = 0;

			String strOderbookId = "" + jsaMassageSocket.get(1);
			String strSplitOrderbookId[] = FragmentChangeActivity.strGetSymbolOrderBook_Id
					.split(",");

			final String strLastTrade = "" + jsaMassageSocket.get(3);
			final String strChange = "" + jsaMassageSocket.get(4);
			String strPercentChange = "" + jsaMassageSocket.get(5);
			String strHigh = "" + jsaMassageSocket.get(6);
			String strLow = "" + jsaMassageSocket.get(7);
			String strVolume = "" + jsaMassageSocket.get(8);
			String strValue = "" + jsaMassageSocket.get(9);

			for (int i = 0; i < strSplitOrderbookId.length; i++) {
				if (strSplitOrderbookId[i].equals(strOderbookId)) {
					indexRow = i;
					row_tv_last_trade.get(indexRow).setText(strLastTrade);
					row_tv_change.get(indexRow).setText(strChange);
					if ((strPercentChange == "0") || strPercentChange == "") {
						row_tv_percent_change.get(indexRow).setText("0.00");
					} else {
						row_tv_percent_change.get(indexRow).setText(
								strPercentChange + "%");
					}

					final int idx = indexRow;
					if (strChange != "") {
						new CountDownTimer(600, 1) {
							public void onTick(long millisUntilFinished) {
								row_tv_last_trade.get(idx).setTextColor(
										context.getResources().getColor(
												FunctionSetBg.arrColor[3]));
								row_tv_change.get(idx).setTextColor(
										context.getResources().getColor(
												FunctionSetBg.arrColor[3]));
								row_tv_percent_change.get(idx).setTextColor(
										context.getResources().getColor(
												FunctionSetBg.arrColor[3]));
							}

							public void onFinish() {
								row_tv_last_trade.get(idx).setTextColor(
										context.getResources().getColor(
												FunctionSetBg
														.setColor(strChange)));
								row_tv_change.get(idx).setTextColor(
										context.getResources().getColor(
												FunctionSetBg
														.setColor(strChange)));
								row_tv_percent_change.get(idx).setTextColor(
										context.getResources().getColor(
												FunctionSetBg
														.setColor(strChange)));
							}
						}.start();

					}
					
					break;
				}
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

}