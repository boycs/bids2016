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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class PagerSystemTradeCdc extends Fragment {

	static Context context;
	public static View rootView;

	private WebSocketClient mWebSocketClient;

	Dialog dialogLoading;

	// --------- google analytics
//	private Tracker mTracker;
//	String nameTracker = new String("Action Zone");

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
		rootView = inflater.inflate(R.layout.pager_systemtrade_cdc, container,
				false);

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

		FragmentChangeActivity.id_websocket = 6;

		if (FragmentChangeActivity.contentGetTxtSlidingMarquee != null) {
			initTxtSliding();
		} else {
			loadTxtSlidingMarquee(); // text sliding
		}

		// if(!(FragmentChangeActivity.pageSystemTrade)){
		// FragmentChangeActivity.pageSystemTrade = true;

		if (FragmentChangeActivity.contentGetWatchlistSystemTrade == null) {
			FollowSymbolSystemTrade.initGetWatchlistSystemTrade();
		}

		if (FragmentChangeActivity.contentGetSystemTradeMacd != null) {
			initMenuCDC(); // set data
		} else {
			loadDataDetail(); // load data
		}

		initSearchLayout(); // layout search

		((TextView) rootView.findViewById(R.id.tv_portfolio))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.strWatchlistCategory = "portfolio";
						switchFragment(new PagerWatchList());
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
								txtPchange = FunctionSetBg
										.setColorTxtSliding(txtSetPChenge,
												txtPchange);
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

	public static LinearLayout li_search_tabbegin, li_search_select;

	public static LinearLayout li_search;

	// android.support.v4.view.ViewPager vp_pager;
	public static EditText et_search;
	public static TextView tv_close_search, tv_search_common,
			tv_search_warrant, tv_search_dw;
	public static ListView listview_search;
	public static LinearLayout li_view;

	// String symbol_search_symbol = "";

	String status_segmentId = "COMMON"; // COMMON, FOREIGN_STOCK, WARRENT, DW
	public static String status_segmentIdDot = "EQUITY_INDEX";

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

		tv_search_common = (TextView) rootView
				.findViewById(R.id.tv_search_common);
		tv_search_warrant = (TextView) rootView
				.findViewById(R.id.tv_search_warrant);
		tv_search_dw = (TextView) rootView.findViewById(R.id.tv_search_dw);

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
							if (((original_list.get(i).status_segmentId)
									.equals(status_segmentId)) || ((original_list.get(i).status_segmentId)
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
				tv_search_common.setTextColor(getResources().getColor(
						R.color.bg_default));
				tv_search_common
						.setBackgroundResource(R.drawable.border_search_activeleft);

				second_list.clear();
				for (int i = 0; i < original_list.size(); i++) {
					if (original_list.get(i).symbol.toLowerCase().contains(
							et_search.getText().toString().toLowerCase())) {
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
		tv_search_warrant.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				status_segmentId = "WARRENT"; // COMMON, FOREIGN_STOCK, WARRENT,
												// DW
				setTitleSearch();
				tv_search_warrant.setTextColor(getResources().getColor(
						R.color.bg_default));
				tv_search_warrant
						.setBackgroundResource(R.drawable.border_search_activecenter);

				second_list.clear();
				for (int i = 0; i < original_list.size(); i++) {
					if (original_list.get(i).symbol.toLowerCase().contains(
							et_search.getText().toString().toLowerCase())) {
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
		tv_search_dw.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				status_segmentId = "DW"; // COMMON, FOREIGN_STOCK, WARRENT, DW
				setTitleSearch();
				tv_search_dw.setTextColor(getResources().getColor(
						R.color.bg_default));
				tv_search_dw
						.setBackgroundResource(R.drawable.border_search_activeright);

				second_list.clear();
				for (int i = 0; i < original_list.size(); i++) {
					if (original_list.get(i).symbol.toLowerCase().contains(
							et_search.getText().toString().toLowerCase())) {
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

	// ******* set title search ****
	private void setTitleSearch() {
		tv_search_common.setTextColor(getResources()
				.getColor(R.color.c_content));
		tv_search_warrant.setTextColor(getResources().getColor(
				R.color.c_content));
		tv_search_dw.setTextColor(getResources().getColor(R.color.c_content));
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
		private JSONObject jsonGetMacd;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// ======= url ========
			String url_GetMacd = SplashScreen.url_bidschart
					+ "/service/trade/macd?set=ALL&id_user="
					+ SplashScreen.userModel.user_id;
			// FragmentChangeActivity.url_bidschart+"/service/trade/ema?set=ALL&id_user=0
			// FragmentChangeActivity.url_bidschart+"/service/trade/macd?set=ALL&id_user=0

			try {
				// ======= Ui Home ========
				jsonGetMacd = ReadJson.readJsonObjectFromUrl(url_GetMacd);
			} catch (IOException e1) {
				connectionError = true;
				jsonGetMacd = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonGetMacd = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonGetMacd = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (jsonGetMacd != null) {
					try {
						FragmentChangeActivity.contentGetSystemTradeMacd = jsonGetMacd
								.getJSONObject("dataAll");

						AttributeBegin.statusLoadSystemTradeCdc = "1";

						initMenuCDC(); // set data

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

	// =========== set data ================
	public static JSONArray jsaBuySell = null; // data buy sell
	public static int statBuySell = 0; // 0 => buy, 1=> sell

	TextView tv_signal, tv_buysel, tv_count;
	ToggleButton toggle_buysell;

	private void initMenuCDC() {
		tv_signal = (TextView) rootView.findViewById(R.id.tv_signal);
		tv_buysel = (TextView) rootView.findViewById(R.id.tv_buysel);
		tv_count = (TextView) rootView.findViewById(R.id.tv_count);
		toggle_buysell = (ToggleButton) rootView
				.findViewById(R.id.toggle_buysell);
		toggle_buysell
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {
						if (isChecked) {
							// tvStateofToggleButton.setText("ON");
							statBuySell = 0;
							tv_buysel.setText("Buy Signal");
							initSetData();
						} else {
							// tvStateofToggleButton.setText("OFF");
							statBuySell = 1;
							tv_buysel.setText("Sell Signal");
							initSetData();
						}
					}
				});

		initSetData();
	}

	public static ArrayList<LinearLayout> row_li_watchlist;
	public static ArrayList<TextView> row_tv_orderbook_id;
	public static ArrayList<TextView> row_tv_symbol_name;
	public static ArrayList<TextView> row_tv_last_trade;
	public static ArrayList<TextView> row_tv_change;
	public static ArrayList<TextView> row_tv_percent_change;
	public static ArrayList<TextView> row_tv_high;
	public static ArrayList<TextView> row_tv_low;
	public static ArrayList<TextView> row_tv_volume;
	public static ArrayList<TextView> row_tv_value;
	public static ArrayList<ImageView> row_img_updown;

	// ============= set data ===========
	LinearLayout li_detail;

	private void initSetData() {

		li_detail = (LinearLayout) rootView.findViewById(R.id.li_detail);

		li_detail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Dialog dialog = new Dialog(context);
				LayoutInflater inflater = LayoutInflater.from(context);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				View dlView = inflater
						.inflate(R.layout.dialog_detail_cdc, null);
				// perform operation on elements in
				// Layout
				dialog.setContentView(dlView);

				((TextView) dlView.findViewById(R.id.tv_link))
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
								startActivity(new Intent(context,
										UiCdcDetailWebView.class));
							}
						});

				dialog.show();
			}
		});

		try {

			if (FragmentChangeActivity.contentGetSystemTradeMacd != null) {
				if (statBuySell == 0) {
					jsaBuySell = FragmentChangeActivity.contentGetSystemTradeMacd
							.getJSONArray("buy");
					tv_count.setBackgroundResource(R.drawable.icon_count_buy);
				} else {
					jsaBuySell = FragmentChangeActivity.contentGetSystemTradeMacd
							.getJSONArray("sell");
					tv_count.setBackgroundResource(R.drawable.icon_count_sell);
				}

				// Log.v("jsaBuySell", ""+jsaBuySell);
				tv_count.setText("" + jsaBuySell.length());

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
				row_tv_high = new ArrayList<TextView>();
				row_tv_low = new ArrayList<TextView>();
				row_tv_volume = new ArrayList<TextView>();
				row_tv_value = new ArrayList<TextView>();
				row_img_updown = new ArrayList<ImageView>();

				row_li_watchlist.clear();
				row_tv_orderbook_id.clear();
				row_tv_symbol_name.clear();
				row_tv_last_trade.clear();
				row_tv_change.clear();
				row_tv_percent_change.clear();
				row_tv_high.clear();
				row_tv_low.clear();
				row_tv_volume.clear();
				row_tv_value.clear();
				row_img_updown.clear();

				for (int i = 0; i < jsaBuySell.length(); i++) {
					View viewSymbol = ((Activity) context).getLayoutInflater()
							.inflate(R.layout.row_systemtrade_symbol, null);
					View viewDetail = ((Activity) context).getLayoutInflater()
							.inflate(R.layout.row_systemtrade_detail_cdc, null);

					JSONObject jsoIndex = jsaBuySell.getJSONObject(i);

					// symbol
					final String symbol_name = jsoIndex.getString("symbol");
					String turnover_list_level = jsoIndex
							.getString("turnover_list_level");
					String status = jsoIndex.getString("status");
					String status_xd = jsoIndex.getString("status_xd");

					TextView tv_symbol_name = (TextView) viewSymbol
							.findViewById(R.id.tv_symbol_name);

					tv_symbol_name.setText(Html.fromHtml(FunctionFormatData
							.checkStatusSymbol(symbol_name,
									turnover_list_level, status, status_xd)));

					((TextView) viewSymbol
							.findViewById(R.id.tv_symbol_fullname_eng))
							.setText(jsoIndex.getString("symbol_fullname_eng"));

					((LinearLayout) viewSymbol.findViewById(R.id.row_symbol))
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									FragmentChangeActivity.strSymbolSelect = symbol_name;
									context.startActivity(new Intent(context,
											UiWatchlistDetail.class));
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
									context.startActivity(new Intent(context,
											UiWatchlistDetail.class));
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
						tv_last_trade
								.setTextColor(context.getResources()
										.getColor(
												FunctionSetBg
														.setColor(strChange)));
						tv_percentChange
								.setTextColor(context.getResources()
										.getColor(
												FunctionSetBg
														.setColor(strChange)));
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

					tv_trendSignal_avg_percent
							.setBackgroundColor(FunctionSetBg
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

					tv_high.setText(FunctionSetBg
							.setStrDetailList(strHigh));
					tv_low.setText(FunctionSetBg.setStrDetailList(strLow));

					// ----- Trend signal
					String strTrade_signal = jsoIndex.getString("trade_signal");
					img_trade_signal.setBackgroundResource(FunctionSetBg
							.setImgTrendSignal(strTrade_signal));

					if (strPrevClose != "") {
						if (strHigh != "") {
							if ((Float.parseFloat(strHigh)) > Float
									.parseFloat(strPrevClose)) {
								tv_high.setTextColor(context
										.getResources()
										.getColor(
												FunctionSetBg.arrColor[2]));
							} else if ((Float.parseFloat(strHigh)) < Float
									.parseFloat(strPrevClose)) {
								tv_high.setTextColor(context
										.getResources()
										.getColor(
												FunctionSetBg.arrColor[0]));
							} else {
								tv_high.setTextColor(context
										.getResources()
										.getColor(
												FunctionSetBg.arrColor[1]));
							}
						}
						if (strLow != "") {
							if ((Float.parseFloat(strLow)) > Float
									.parseFloat(strPrevClose)) {
								tv_low.setTextColor(context
										.getResources()
										.getColor(
												FunctionSetBg.arrColor[2]));
							} else if ((Float.parseFloat(strLow)) < Float
									.parseFloat(strPrevClose)) {
								tv_low.setTextColor(context
										.getResources()
										.getColor(
												FunctionSetBg.arrColor[0]));
							} else {
								tv_low.setTextColor(context
										.getResources()
										.getColor(
												FunctionSetBg.arrColor[1]));
							}
						}
					}

					// ck pe pbv peg
					String strPe = jsoIndex.getString("p_e")
							.replaceAll(",", "");
					String strPbv = jsoIndex.getString("p_bv").replaceAll(",",
							"");
					String strRoe = jsoIndex.getString("roe").replaceAll(",",
							"");
					String strRoa = jsoIndex.getString("roa").replaceAll(",",
							"");
					String strPeg = jsoIndex.getString("peg").replaceAll(",",
							"");

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
					tv_p_bv.setText(FunctionSetBg
							.setStrDetailList(strPbv));
					tv_roe.setText(FunctionSetBg.setStrDetailList(strRoe));
					tv_roe.setTextColor(context.getResources().getColor(
							FunctionSetBg.setStrDetailListColor(strRoe)));
					tv_roa.setText(FunctionSetBg.setStrDetailList(strRoa));
					tv_roa.setTextColor(context.getResources().getColor(
							FunctionSetBg.setStrDetailListColor(strRoa)));
					tv_peg.setText(FunctionSetBg.setStrDetailList(strPeg));

					if (SplashScreen.contentSymbol_Set != null) {
						String strPe_set = SplashScreen.contentSymbol_Set
								.getString("p_e").replaceAll(",", "");
						String strPbv_set = SplashScreen.contentSymbol_Set
								.getString("p_bv").replaceAll(",", "");
						String strPeg_set = SplashScreen.contentSymbol_Set
								.getString("peg").replaceAll(",", "");

						if (!(strPe.equals("")) && (strPe != null)
								&& !(strPe.equals("null"))
								&& !(strPe_set.equals(""))
								&& !(strPe_set.equals(null))
								&& !(strPe_set.equals("null"))) {
							tv_p_e.setTextColor(context.getResources()
									.getColor(
											FunctionSetBg.setStrCheckSet(
													strPe, strPe_set)));
						}
						if (!(strPbv.equals("")) && (strPbv != null)
								&& !(strPbv.equals("null"))
								&& !(strPbv_set.equals(""))
								&& !(strPbv_set.equals(null))
								&& !(strPbv_set.equals("null"))) {
							tv_p_bv.setTextColor(context.getResources()
									.getColor(
											FunctionSetBg.setStrCheckSet(
													strPbv, strPbv_set)));
						}
						if (!(strPeg.equals("")) && (strPeg != null)
								&& !(strPeg.equals("null"))
								&& !(strPeg_set.equals(""))
								&& !(strPeg_set.equals(null))
								&& !(strPeg_set.equals("null"))) {
							tv_peg.setTextColor(context.getResources()
									.getColor(
											FunctionSetBg.setStrCheckSet(
													strPeg, strPeg_set)));
						}
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
					
					Log.v("cdccccccccc", ""+sptVolume.length+"__"+sptValue.length);
					if (sptVolume.length > 1) {
						tv_volume.setText(sptVolume[0] + "\n" + sptVolume[1]);
					}else{
						tv_volume.setText(strVolume);
					}
					if (sptValue.length > 1) {
						tv_value.setText(sptValue[0] + "\n" + sptValue[1]);
					}else{
						tv_value.setText(strValue);
					}

					tv_ceiling.setText(jsoIndex.getString("ceiling"));
					tv_floor.setText(jsoIndex.getString("floor"));

					// --- add row update
					// row_tv_orderbook_id.add();
					row_tv_symbol_name.add(tv_symbol_name);
					row_tv_last_trade.add(tv_last_trade);
					row_tv_change.add(tv_change);
					row_tv_percent_change.add(tv_percentChange);
					row_tv_high.add(tv_high);
					row_tv_low.add(tv_low);
					row_tv_volume.add(tv_volume);
					row_tv_value.add(tv_value);
					row_img_updown.add(img_updown);

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

				// V/Websocket(27790): Error failed to connect to
				// 2008.bidschart.com/61.91.124.163 (port 4504) after 90000ms:
				// isConnected failed: ECONNREFUSED (Connection refused)

				// {"id":0,"user_id":0,"orderbook_list":"1024,1062,1063,1064,1025","date":"","hh":"","mm":"","requestType":"watchList"};
				// {"id":0,"user_id":0,"orderbook_list":"2493,1213,10542,1881,7170,4262,1207,2310","date":"","hh":"","mm":"","requestType":"watchList"}

				// jsonObj send connect(14720):
				// {"id":0,"user_id":0,"orderbook_list":"2493,1213,10542,1881,7170,4262,1207,2310","date":"","hh":"","mm":"","requestType":"watchList"}
				// Websocket(14720): Error Attempt to invoke virtual method
				// 'void
				// android.support.v4.app.FragmentActivity.runOnUiThread(java.lang.Runnable)'
				// on a null object reference

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
					jsoConnectSocket.put("page", "cdc");
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Log.v("jsonObj send connect", jsoConnectSocket.toString());

				// 11-04 10:12:02.208: V/jsonObj send connect(25506):
				// {"id":6,"user_id":0,"orderbook_list":",PTT,.SET,PPP,AAV,A,AKR,DTAC,CRYSTAL,ASK,CBG,KTB,KBS,KBANK,CCN,UKEM,RS,JAS,MEGA,BBL,SIMAT209910245530426217751653183162171342114901793282218171054217162152175069791213188120991024553042621775165318316217134211490179328221817105421716215217506979121318812099,PTT,.SET,PPP,AAV,A,AKR,DTAC,CRYSTAL,ASK,CBG,KTB,KBS,KBANK,CCN,UKEM,RS,JAS,MEGA,BBL,SIMAT2099102455304262177516531831621713421149017932822181710542171621521750697912131881209910245530426217751653183162171342114901793282218171054217162152175069791213188120991024,PTT,.SET,PPP,AAV,A,AKR,DTAC,CRYSTAL,ASK,CBG,KTB,KBS,KBANK,CCN,UKEM,RS,JAS,MEGA,BBL,SIMAT20991024553042621775165318316217134211490179328221817105421716215217506979121318812099102455304262177516531831621713421149017932822181710542171621521750697912131881209910245530,PTT,.SET,PPP,AAV,A,AKR,DTAC,CRYSTAL,ASK,CBG,KTB,KBS,KBANK,CCN,UKEM,RS,JAS,MEGA,BBL,SIMAT209910245530426217751653183162171342114901793282218171054217162152175069791213188120991024553042621775165318316217134211490179328221817105421716215217506979121318812099102455304262,PTT,.SET,PPP,AAV,A,AKR,DTAC,CRYSTAL,ASK,CBG,KTB,KBS,KBANK,CCN,UKEM,RS,JAS,MEGA,BBL,SIMAT2099102455304262177516531831621713421149017932822181710542171621521750697912131881209910245530426217751653183162171342114901793282218171054217162152175069791213188120991024553042621775,PTT,.SET,PPP,AAV,A,AKR,DTAC,CRYSTAL,ASK,CBG,KTB,KBS,KBANK,CCN,UKEM,RS,JAS,MEGA,BBL,SIMAT20991024553042621775165318316217134211490179328221817105421716215217506979121318812099102455304262177516531831621713421149017932822181710542171621521750697912131881209910245530426217751653,PTT,.SET,PPP,AAV,A,AKR,DTAC,CRYSTAL,ASK,CBG,KTB,KBS,KBANK,CCN,UKEM,RS,JAS,MEGA,BBL,SIMAT209910245530426217751653183162171342114901793282218171054217162152175069791213188120991024553042621775165318316217134211490179328221817105421716215217506979121318812099102455304262177516531831,PTT,.SET,PPP,AAV,A,AKR,DTAC,CRYSTAL,ASK,CBG,KTB,KBS,KBANK,CCN,UKEM,RS,JAS,MEGA,BBL,SIMAT2099102455304262177516531831621713421149017932822181710542171621521750697912131881209910245530426217751653183162171342114901793282218171054217162152175069791213188120991024553042621775165318316217,PTT,.SET,PPP,AAV,A,AKR,DTAC,CRYSTAL,ASK,CBG,KTB,KBS,KBANK,CCN,UKEM,RS,JAS,MEGA,BBL,SIMAT20991024553042621775165318316217134211490179328221817105421716215217506979121318812099102455304262177516531831621713421149017932822181710542171621521750697912131881209910245530426217751653183162171342,PTT,.SET,PPP,AAV,A,AKR,DTAC,CRYSTAL,ASK,CBG,KTB,KBS,KBANK,CCN,UKEM,RS,JAS,MEGA,BBL,SIMAT2099102455304262177516531831621713421149017932822181710542171621521750697912131881209910245530426217751653183162171342114901793282218171054217162152175069791213188120991024553042621775165318316217134211490,PTT,.SET,PPP,AAV,A,AKR,DTAC,CRYSTAL,ASK,CBG,KTB,KBS,KBANK,CCN,UKEM,RS,JAS,MEGA,BBL,SIMAT20991024553042621775165318316217134211490179328221817105421716215217506979121318812099102455304262177516531831621713421149017932822181710542171621521750697912131881209910245530426217751653183162171342114901793,PTT,.SET,PPP,AAV,A,AKR,DTAC,CRYSTAL,ASK,CBG,KTB,KBS,KBANK,CCN,UKEM,RS,JAS,MEGA,BBL,SIMAT209910245530426217751653183162171342114901793282218171054217162152175069791213188120991024553042621775165318316217134211490179328221817105421716215217506979121318812099102455304262177516531831621713421149017932822,PTT,.SET,PPP,AAV,A,AKR,DTAC,CRYSTAL,ASK,CBG,KTB,KBS,KBANK,CCN,UKEM,RS,JAS,MEGA,BBL,SIMAT2099102455304262177516531831621713421149017932822181710542171621521750697912131881209910245530426217751653183162171342114901793282218171054217162152175069791213188120991024553042621775165318316217134211490179328221817,PTT,.SET,PPP,AAV,A,AKR,DTAC,CRYSTAL,ASK,CBG,KTB,KBS,KBANK,CCN,UKEM,RS,JAS,MEGA,BBL,SIMAT209910245530426217751653183162171342114901793282218171054217162152175069791213188120991024553042621775165318316217134211490179328221817105421716215217506979121318812099102455304262177516531831621713421149017932822181710542,PTT,.SET,PPP,AAV,A,AKR,DTAC,CRYSTAL,ASK,CBG,KT

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

	// ============== Set watchlist symbol ===============
	public void changeRowUpdate() {

		Log.v("changeRow cdc", "cdc cdc");
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
					row_tv_high.get(indexRow).setText(FunctionSetBg.setStrDetailList(strHigh));
					row_tv_low.get(indexRow).setText(FunctionSetBg.setStrDetailList(strLow));
					row_tv_volume.get(indexRow).setText(strVolume);
					row_tv_value.get(indexRow).setText(strValue);
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
	
	// ============= Pager ===========
	private void initPager() {
		// creating fragments and adding to list
		fragmentMain.removeAll(fragmentMain);
		// fragmentMain.add(Fragment.instantiate(context,
		// PagerSystemTradeBidSystem.class.getName()));
		fragmentMain.add(Fragment.instantiate(context,
				PagerSystemTradeCdc_.class.getName()));

		// creating adapter and linking to view pager
		this.mPagerAdapter = new PagerAdapter(super.getChildFragmentManager(),
				fragmentMain);
		mPager = (ViewPager) rootView.findViewById(R.id.vp_pager);

		mPager.setAdapter(this.mPagerAdapter);

		mPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				selectTitle = mPager.getCurrentItem();
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				mPager.getParent().requestDisallowInterceptTouchEvent(true);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		// initial tabtitle
		// initTabTitle();
		dialogLoading.dismiss();
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