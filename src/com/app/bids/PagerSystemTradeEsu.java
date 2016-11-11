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

public class PagerSystemTradeEsu extends Fragment {

	static Context context;
	public static View rootView;

	private WebSocketClient mWebSocketClient;

	Dialog dialogLoading;
	
	// --------- google analytics 
//	private Tracker mTracker;
//    String nameTracker = new String("Float Volume Spread");

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
		
		DialogDifinitionSignal = new DialogDifinitionSignal(
				context);

		// inflate view from layout
		rootView = inflater.inflate(R.layout.pager_systemtrade_esu, container,
				false);

		// getFragmentManager().beginTransaction().remove(FragmentPagerSystemTrade.this).commit();

		// --------- google analytics
//		GoogleAnalyticsApp application = (GoogleAnalyticsApp) getActivity().getApplication();
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

		if (FragmentChangeActivity.contentGetSystemTradeEsu != null) {
			initSetData(); // set data
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
		final ListAdapterSearchSymbolSystemTradeEsu ListAdapterSearch;
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

		ListAdapterSearch = new ListAdapterSearchSymbolSystemTradeEsu(context,
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
				tv_search_common.setTextColor(context.getResources().getColor(
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
				tv_search_warrant.setTextColor(context.getResources().getColor(
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
				tv_search_dw.setTextColor(context.getResources().getColor(
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
		private JSONObject jsonGetEsu;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// ======= url ========
			String url_GetEsu = SplashScreen.url_bidschart
					+ "/service/v2/getEsuDetailList";

			try {
				// ======= Ui Home ========
				jsonGetEsu = ReadJson.readJsonObjectFromUrl(url_GetEsu);
			} catch (IOException e1) {
				connectionError = true;
				jsonGetEsu = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonGetEsu = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonGetEsu = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (jsonGetEsu != null) {
					try {
						FragmentChangeActivity.contentGetSystemTradeEsu = jsonGetEsu
								.getJSONArray("data");

						AttributeBegin.statusLoadSystemTradeBreakOut = "1";

						initSetData(); // set data

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
	public static ArrayList<LinearLayout> row_li_watchlist;
	public static ArrayList<TextView> row_tv_orderbook_id;
	public static ArrayList<TextView> row_tv_symbol_name;
	public static ArrayList<TextView> row_tv_last_trade;
	public static ArrayList<TextView> row_tv_change;
	public static ArrayList<TextView> row_tv_percent_change;

	// ============= set data ===========
	TextView tv_count, tv_detail;

	private void initSetData() {
		tv_count = (TextView) rootView.findViewById(R.id.tv_count);
		tv_detail = (TextView) rootView.findViewById(R.id.tv_detail);

		tv_detail.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				final Dialog dialog = new Dialog(context);
				LayoutInflater inflater = LayoutInflater.from(context);
				dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
				View dlView = inflater
						.inflate(R.layout.dialog_detail_esu, null);
				// perform operation on elements in
				// Layout
				dialog.setContentView(dlView);

				((TextView) dlView.findViewById(R.id.tv_link))
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								dialog.dismiss();
								startActivity(new Intent(context,
										UiEsuDetailWebView.class));
							}
						});
				dialog.show();
			}
		});

		try {
			if (FragmentChangeActivity.contentGetSystemTradeEsu != null) {
				tv_count.setText(""
						+ FragmentChangeActivity.contentGetSystemTradeEsu
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

				for (int i = 0; i < FragmentChangeActivity.contentGetSystemTradeEsu
						.length(); i++) {
					View viewSymbol = ((Activity) context).getLayoutInflater()
							.inflate(R.layout.row_systemtrade_symbol, null);
					View viewDetail = ((Activity) context).getLayoutInflater()
							.inflate(R.layout.row_systemtrade_detail_esu, null);

					JSONObject jsoIndex = FragmentChangeActivity.contentGetSystemTradeEsu
							.getJSONObject(i);

					// symbol
					final String symbol_name = jsoIndex
							.getString("symbol_name");
//					String turnover_list_level = jsoIndex
//							.getString("turnover_list_level");
//					String status = jsoIndex.getString("status");
//					String status_xd = jsoIndex.getString("status_xd");

					TextView tv_symbol_name = (TextView) viewSymbol
							.findViewById(R.id.tv_symbol_name);
					tv_symbol_name.setText(symbol_name);
					
//					tv_symbol_name
//					.setText(Html.fromHtml(FunctionSymbol
//							.checkStatusSymbol(symbol_name,
//									turnover_list_level, status,
//									status_xd)));
					
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
					View v_sft = (View) rootView
							.findViewById(R.id.v_sft);
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

					// zof
					String strZofPuas = jsoIndex.getString("zof_puas");
					String strZofPoc = jsoIndex.getString("zof_poc");
					String strZofPuar = jsoIndex.getString("zof_puar");
					String strImb = jsoIndex.getString("IMB");
					String strStatus = jsoIndex.getString("status");
					String strVolumeStatus = jsoIndex
							.getString("volume_status");

					TextView tv_zof_puas = (TextView) viewDetail
							.findViewById(R.id.tv_zof_puas);
					TextView tv_zof_poc = (TextView) viewDetail
							.findViewById(R.id.tv_zof_poc);
					TextView tv_zof_puar = (TextView) viewDetail
							.findViewById(R.id.tv_zof_puar);

					TextView tv_imb = (TextView) viewDetail
							.findViewById(R.id.tv_imb);
					TextView tv_status = (TextView) viewDetail
							.findViewById(R.id.tv_status);
					TextView tv_volume_status = (TextView) viewDetail
							.findViewById(R.id.tv_volume_status);

					tv_zof_puas.setText(strZofPuas);
					tv_zof_poc.setText(strZofPoc);
					tv_zof_puar.setText(strZofPuar);

					tv_imb.setText(FunctionSetBg.setEsuStrImb(strImb));
					tv_imb.setBackgroundResource(FunctionSetBg
							.setEsuBgImb(strImb));
					tv_status.setText(strStatus);
					tv_volume_status.setText(strVolumeStatus);

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
					jsoConnectSocket.put("page", "Esu");
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

		Log.v("changeRow esu", "esu esu");
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