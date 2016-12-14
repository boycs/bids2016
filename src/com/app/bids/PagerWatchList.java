package com.app.bids;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;

import android.R.integer;
import android.app.ActionBar.LayoutParams;
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
import android.os.SystemClock;
import android.provider.DocumentsContract.Root;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Toast;

public class PagerWatchList extends Fragment {

	static Context context;
	public static View rootView;

	public static WebSocketClient mWebSocketClient;

	public static Dialog dialogLoading;

	// --------- google analytics
	// private Tracker mTracker;
	// String nameTracker = new String("Market Watch");

	public interface OnPageListener {
		public void onPage1(String s);
	}

	static DialogWatchListCategories dialogCategories;
	static DialogWatchListCategoriesIndustry DialogWatchListCategoriesIndustry;
	static DialogWatchListCategoriesIndustrySector DialogWatchListCategoriesIndustrySector;
	static DialogDifinitionSignal DialogDifinitionSignal;

	// onCreateView :
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();

		dialogCategories = new DialogWatchListCategories(context);
		DialogWatchListCategoriesIndustry = new DialogWatchListCategoriesIndustry(
				context);
		DialogWatchListCategoriesIndustrySector = new DialogWatchListCategoriesIndustrySector(
				context);
		DialogDifinitionSignal = new DialogDifinitionSignal(context);

		// fragment not when container null
		if (container == null) {
			return null;
		}

		// inflate view from layout
		rootView = inflater.inflate(R.layout.pager_watchlist, container, false);
		((TextView) rootView.findViewById(R.id.tv_watchlist_categories))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// switchFragment(new PagerWatchListCategories());
						// ((LinearLayout)rootView.findViewById(R.id.li_title)).setVisibility(View.GONE);
						// dialogCagtegory();

						dialogCategories.show();

						if (FragmentChangeActivity.contentGetWatchlistSystemTrade == null) {
							FollowSymbolSystemTrade.initGetWatchlistSystemTrade();
						}
					}
				});

		// --------- google analytics
		// GoogleAnalyticsApp application = (GoogleAnalyticsApp) getActivity()
		// .getApplication();
		// mTracker = application.getDefaultTracker();

		// Tracker t = ((GoogleAnalyticsApp)
		// context).getTracker(TrackerName.APP_TRACKER);
		// t.setScreenName(nameTracker);
		// t.send(new HitBuilders.AppViewBuilder().build());

		dialogLoading = new Dialog(context);
		dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogLoading.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialogLoading.setContentView(R.layout.progress_bar);
		dialogLoading.setCancelable(false);
		dialogLoading.setCanceledOnTouchOutside(false);
		dialogLoading.show();

		if (FragmentChangeActivity.contentGetTxtSlidingMarquee != null) {
			initTxtSliding();
		} else {
			loadTxtSlidingMarquee(); // text sliding
		}

		// ///////////
		// com.app.bidchart.LineChartView lineChart =
		// (com.app.bidchart.LineChartView)
		// rootView.findViewById(R.id.linechart);
		// lineChart.setChartData(getRandomData());

		// initSearchSymbol(); // search symbol
		// initGetData(); // get data

		// // --------- google analytics
		// Tracker t = ((GoogleAnalyticsApp) context)
		// .getTracker(TrackerName.APP_TRACKER);
		// t.setScreenName("Home");
		// t.send(new HitBuilders.AppViewBuilder().build());

		initView();

		return rootView;

	}

	@Override
	public void onStart() {
		super.onStart();
		Log.v("WatchList", "onStart onStart onStart");
		// GoogleAnalytics.getInstance(context).reportActivityStart(getActivity());
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.v("WatchList", "onStop onStop onStop");
		// GoogleAnalytics.getInstance(context).reportActivityStop(getActivity());
	}

	@Override
	public void onResume() {
		super.onResume();
		// Log.v(nameTracker, "onResume onResume onResume");
		//
		// mTracker.setScreenName(nameTracker);
		// mTracker.send(new HitBuilders.ScreenViewBuilder().build());
	}

	public static JSONObject dialogJsoIndustrySetSector = null;
	public static JSONArray dialogContentGetWatchlists = null;

	public void initView() {
		// -------- stop timer ----
		if (FragmentChangeActivity.timerUpdateSymbolStatus) {
			FragmentChangeActivity.timerUpdateSymbolStatus = false;
			FragmentChangeActivity.timerUpdateSymbol.cancel();
		}

		// dialogLoading.show();

		getSymbolSetBegin(); // โหลด .set เริ่มต้นเอาไว้เปรียบเทียบ

		// if (FragmentChangeActivity.contentGetWatchlistSystemTrade == null) {
		// FollowFunction.initGetWatchlistSystemTrade();
		// }

		if ((FragmentChangeActivity.contentGetSymbol == null)
				|| (FragmentChangeActivity.contentGetIndustrySetSector == null)) {
			initGetDataSymbolBegin(); // get symbol,industry begin
		} else {
			if (FragmentChangeActivity.ckLoadWatchlist) {
				initGetData(); // get data
			} else {
				setWatchlistSymbol();
			}
			initSearchLayout(); // layout search
		}
		if (!FragmentChangeActivity.ckLoadFavAll) {
			FragmentChangeActivity.ckLoadFavAll = true;
			getDataFavoriteAll();
		}
	}

	// ***************** text sliding ******************
	public static void initTxtSliding() {
		String strSliding = "";
		TextView marque = (TextView) rootView
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
						// double db = Double.parseDouble(txtLtrade);
						// DecimalFormat formatter = new
						// DecimalFormat("#,###.00");
						// txtLtrade = formatter.format(db);
						txtLtrade = FunctionFormatData.setFormatNumber(txtLtrade);
					}

					if (txtChange != "") {
						// double db = Double.parseDouble(txtChange);
						// txtChange = String.format(" %,.2f", db);
						txtChange = FunctionFormatData.setFormatNumber(txtChange);
					}

					if (txtSymbol.equals(".SET")) {
						if (txtPchange != "") {
							double dbS = Double.parseDouble(txtSetPChenge
									.replaceAll(",", ""));
							txtPchange = FunctionSetBg
									.setColorTxtSlidingSet(txtSetPChenge
											.replaceAll(",", ""));
						}
					} else {
						if (txtPchange != "") {
							double dbS = Double.parseDouble(txtSetPChenge
									.replaceAll(",", ""));
							double dbO = Double.parseDouble(txtPchange
									.replaceAll(",", ""));
							txtPchange = FunctionSetBg.setColorTxtSliding(
									txtSetPChenge, txtPchange);
						}
					}

					// strSliding += "   <font color='#95dd33'>" + txtSymbol
					// + "</font>  " + txtLtrade + "  " + txtChange
					// + "<font color='#eb4848'>" + txtPchange
					// + "</font> ";

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

	public static ScrollView sv_view;
	public static EditText et_search;
	public static TextView tv_close_search, tv_search_common,
			tv_search_warrant, tv_search_dw;
	public static ListView listview_search;

	// String symbol_search_symbol = "";

	public static String status_segmentId = "COMMON"; // COMMON, FOREIGN_STOCK,
														// WARRENT, DW
	public static String status_segmentIdDot = "EQUITY_INDEX";

	public void initSearchLayout() {

		// --- linear show tabsearch begin
		li_search_tabbegin = (LinearLayout) rootView
				.findViewById(R.id.li_search_tabbegin);
		li_search_select = (LinearLayout) rootView
				.findViewById(R.id.li_search_select);
		// --- linear tabsearch
		li_search = (LinearLayout) rootView.findViewById(R.id.li_search);
		// --- linear tabsearch begin
		sv_view = (ScrollView) rootView.findViewById(R.id.sv_view);
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
					if (FragmentChangeActivity.strWatchlistCategory == "portfolio") {
						initSearchSymbolSystemTrade(); // search symbol
					} else {
						initSearchSymbol(); // search symbol
					}
				}
			}
		});
	}

	// =========== search symbol watchlist ==========
	public static void initSearchSymbol() {
		// sv_search_symbol = (ScrollView)
		// rootView.findViewById(R.id.sv_search_symbol);
		li_search_tabbegin.setVisibility(View.GONE);
		li_search.setVisibility(View.VISIBLE);

		// search
		final ListAdapterSearchSymbolWatchList ListAdapterSearch;
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

		ListAdapterSearch = new ListAdapterSearchSymbolWatchList(context, 0,
				second_list);
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
					sv_view.setVisibility(View.GONE);
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
					sv_view.setVisibility(View.VISIBLE);
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

	// =========== search symbol systemtrade ==========
	public void initSearchSymbolSystemTrade() {

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
					sv_view.setVisibility(View.GONE);
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
					sv_view.setVisibility(View.VISIBLE);
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
				setWatchlistSymbol();

				// listview_search.setVisibility(View.GONE);

				// setWatchlistSymbol();
			}
		});
	}

	// / ============ close listsearch ======
	public static void hideListSearch() {
		et_search.setText("");
		sv_view.setVisibility(View.VISIBLE);

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

	// ************* setalert select symbol ***************
	public static Handler handlerSetAlertSymbol = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle b = msg.getData();
			// Toast.makeText(context, "addfav : " + b.getString("msg"),
			// Toast.LENGTH_SHORT).show();
			switchFragmentStatic(new PagerSetAlertSelect());
		}
	};

	// ************* symbol detail ***************
	public static Handler handlerGetSymbolDetail = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			Bundle b = msg.getData();
			FragmentChangeActivity.strSymbolSelect = "" + b.getString("msg");

			// switchFragmentStatic(new PagerWatchlistDetail()); okkk

			// startActivity(new Intent(context,
			// UiWatchlistDetailTest.class));
		}
	};

	// ************* watchlist ***************
	public static Handler handlerWatchList = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			dialogLoading.dismiss();
			switchFragmentStatic(new PagerWatchList());
			// getDataFavorite(); // get favorite
		}
	};

	// ************* login success profile ***************
	public static Handler handlerProfile = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			dialogLoading.dismiss();

			switch (msg.what) {
			case 0:

				break;
			case 1:
				switchFragmentStatic(new PagerProfile());
				break;
			default:
				break;
			}
		}
	};

	// public static float[] getRandomData() {
	// // 6 ตำแหน่ง เลขสุดท้ายเป็น 15 ไม่ได้
	// return new float[] { 8, 2, 15, 13, 13, 17, 15 };
	// // return new float[] { 1, 12, 7, 14, 15, 19};
	// }

	// ====================== Set Data ========================
	public static String strSpnPeriod = "avg";
	public static String strSpnIndustry = "ALL";
	public static String strSpnSector = "ALL";

	public void initGetData() {
		TextView tv_edit = (TextView) rootView.findViewById(R.id.tv_edit);
		tv_edit.setVisibility(View.GONE);
		// sector, topmost, trendsignal, isdustry, fundamental, favorite

		if (FragmentChangeActivity.strWatchlistCategory == "favorite") {
			tv_edit.setVisibility(View.VISIBLE);
			if (FragmentChangeActivity.strFavoriteNumber == "1") {
				FragmentChangeActivity.strGetListSymbol = FragmentChangeActivity.strGetListSymbol_fav1;
			} else if (FragmentChangeActivity.strFavoriteNumber == "2") {
				FragmentChangeActivity.strGetListSymbol = FragmentChangeActivity.strGetListSymbol_fav2;
			} else if (FragmentChangeActivity.strFavoriteNumber == "3") {
				FragmentChangeActivity.strGetListSymbol = FragmentChangeActivity.strGetListSymbol_fav3;
			} else if (FragmentChangeActivity.strFavoriteNumber == "4") {
				FragmentChangeActivity.strGetListSymbol = FragmentChangeActivity.strGetListSymbol_fav4;
			} else if (FragmentChangeActivity.strFavoriteNumber == "5") {
				FragmentChangeActivity.strGetListSymbol = FragmentChangeActivity.strGetListSymbol_fav5;
			}
			if (FragmentChangeActivity.strGetListSymbol == "") {
				Log.v("strGetListSymbol","null null null null");
				getDataFavorite(); // get favorite
			} else {
				Log.v("strGetListSymbol","not null not null");
				getWatchlistSymbol(); // get watchlist symbol
			}
		} else if (FragmentChangeActivity.strWatchlistCategory == "sector") {
			loadIndustrySetSectorSelect(); // load Industry select
		} else if (FragmentChangeActivity.strWatchlistCategory == "topmost") {
			loadTopmostSelect(); // load topmost select
		} else if (FragmentChangeActivity.strWatchlistCategory == "trendsignal") {
			setInitTapSpinner(); // loadTrendSignalSelect(); // load trendsignal
		} else if (FragmentChangeActivity.strWatchlistCategory == "industry") {
			loadIndustrySetSectorSelect();
		} else if (FragmentChangeActivity.strWatchlistCategory == "fundamental") {
			setInitTapSpinner();
		} else if (FragmentChangeActivity.strWatchlistCategory == "portfolio") {
			loadSystemTradePortfolio(); // load portfolio
		} else {
			Log.v("strWatchlistCategory", "null");
		}

		// dialog delete symbol
		tv_edit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// FragmentChangeActivity.contentGetSymbolFavorite
				dialogDelFav();
			}
		});
	}

	// ============== dialog delete fav symbol ===============
	public static void changeRowDeleteFav(View v) {
		for (int i = 0; i < row_tv_favdel.size(); i++) {
			row_tv_favdel.get(i).setVisibility(View.GONE);
		}
	}

	public static void hideRowDeleteFav(View v) {
		for (int i = 0; i < row_li_favdel.size(); i++) {
			row_li_favdel.get(i).setVisibility(View.VISIBLE);
		}
	}

	public static ArrayList<LinearLayout> row_li_favdel;
	public static ArrayList<TextView> row_tv_favdel;

	public void dialogDelFav() {
		final Dialog dialog = new Dialog(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		View dlView = inflater.inflate(R.layout.dialog_delete_favorite, null);
		// perform operation on elements in Layout
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(dlView);
		((TextView) dlView.findViewById(R.id.dl_tv_close))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

		LinearLayout dl_li_list = (LinearLayout) dlView
				.findViewById(R.id.dl_li_list);
		dl_li_list.removeAllViews();

		row_tv_favdel = new ArrayList<TextView>();
		row_li_favdel = new ArrayList<LinearLayout>();
		row_tv_favdel.clear();
		row_li_favdel.clear();

		if (FragmentChangeActivity.contentGetWatchlistSymbol != null) {

			for (int i = 0; i < FragmentChangeActivity.contentGetWatchlistSymbol
					.length(); i++) {
				try {
					View viewRow = ((Activity) context).getLayoutInflater()
							.inflate(R.layout.row_dialog_delfav, null);

					JSONObject jsoIndex = FragmentChangeActivity.contentGetWatchlistSymbol
							.getJSONObject(i);

					final LinearLayout li_row = (LinearLayout) viewRow
							.findViewById(R.id.li_row);

					ImageView img_delete = (ImageView) viewRow
							.findViewById(R.id.img_delete);
					TextView tv_symbol = (TextView) viewRow
							.findViewById(R.id.tv_symbol);
					final TextView tv_delete = (TextView) viewRow
							.findViewById(R.id.tv_delete);
					tv_delete.setVisibility(View.GONE);

					row_tv_favdel.add(tv_delete);
					row_li_favdel.add(li_row);

					tv_symbol.setText(jsoIndex.getString("symbol_name"));
					img_delete.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							// set row select
							changeRowDeleteFav(v);
							tv_delete.setVisibility(View.VISIBLE);

						}
					});

					final String strSymbol = jsoIndex.getString("symbol_name");
					tv_delete.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							FragmentChangeActivity.strSymbolSelect = strSymbol;
							FollowSymbol.sendSymbolRemoveFavorite(); // send remove favorite
							hideRowDeleteFav(v);
							li_row.setVisibility(View.GONE);
						}
					});

					dl_li_list.addView(viewRow);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
		dialog.show();

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
					jsoConnectSocket.put("user_id", 0);
					jsoConnectSocket.put("orderbook_list",
							FragmentChangeActivity.strGetSymbolOrderBook_Id);
					jsoConnectSocket.put("date", "");
					jsoConnectSocket.put("hh", "");
					jsoConnectSocket.put("mm", "");
					jsoConnectSocket.put("requestType", "watchList");
					jsoConnectSocket.put("page", "favorite");
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
							// Log.v("onMessage", message); [1,6,1]

							// ไม่เอา array[0] เป้น 1 ให้เอา array[0] เป็น 3

							// ครั้งแรกส่ง id : 0 ไป
							// ถ้าข้อมูลส่งมา index[0] เป็น 3 แสดงว่า
							// มีข้อมูลอัพเดท
							// เช็คตาม orderbook_id
							//
							// พอส่งซ้ำ เช่น กดเข้าหน้าอีกครั้ง ให้ส่ง id : ไป
							// แล้วเช็ค index[0] เป็น 3 เหมือนเดิม

							// [3,2099,"PTT","264.00","4.00","1.54","264.00","262.00","40.63","10,688.85","92,900","24","214,800","56","263.38","262.98","263.00","14,900","263.00","20,000","263.00","263.00","98,600","0.00","0","0"]
							// [3,2493,"TRUE","7.80","0.15","1.96","7.90","7.60","202.77 M","1,575.04 M",""]

							// connect ครั้งแรก
							// {"id":0,"user_id":18885,"orderbook_list":"1024,1062,1063,1064,1025","date":"","hh":"","mm":"","requestType":"watchList","page":"Watchlist"}
							// requestType :
							// watchList = มี orderbook_list หลายตัว
							// symbolRealtime = มี orderbook_list ตัวเดียว

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

		Log.v("changeRowWatchlist", "changeRowWatchlist changeRowWatchlist");

		// [3,2099,"PTT","264.00","4.00","1.54","264.00","262.00","40.63","10,688.85","92,900","24","214,800","56","263.38","262.98","263.00","14,900","263.00","20,000","263.00","263.00","98,600","0.00","0","0"]

		// [3,2493,"TRUE","7.80","0.15","1.96","7.90","7.60","202.77 M","1,575.04 M",""]

		// id:3 watchList
		// format : JSON Array
		// [
		// 0=id,
		// 1=orderbook_id,
		// 2=symbol_name,
		// 3=last_trade,
		// 4=change,
		// 5=percent_change
		// 6=high
		// 7=low
		// 8=volume,(M)
		// 9=value (M),
		// 10= page
		// ]

		try {
			int indexRow = 0;

			// 0=id,
			// 1=orderbook_id,
			// 2=symbol_name,
			// 3=last_trade,
			// 4=change,
			// 5=percent_change
			// 6=high
			// 7=low
			// 8=volume,(M,K)
			// 9=value (M,K),
			// 10= page

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
					// row_tv_high.get(indexRow).setText(strHigh);
					// row_tv_low.get(indexRow).setText(strLow);
					row_tv_high.get(indexRow).setText(
							FunctionSetBg.setStrDetailList(strHigh));
					row_tv_low.get(indexRow).setText(
							FunctionSetBg.setStrDetailList(strLow));
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

	// ====== init trensignal spinner ============
	// public static String strIndustry = "";
	// public static String strSpnSelectIndustry = "";
	// public static String strSector = "";
	// public static String strSpnSelectSector = "";
	public static List<String> listIndestry;
	public static List<String> listSector;

	// public static int industryIndex = 0;
	public void addItemsOnSpinnerTrendsignalIndustry() {
		try {
			listIndestry = new ArrayList<String>();
			listIndestry.add("ALL");

			for (int i = 0; i < FragmentChangeActivity.contentGetIndustrySetSector
					.length(); i++) {
				JSONObject jsaIndustrySetSectorSelect = FragmentChangeActivity.contentGetIndustrySetSector
						.getJSONObject(i);
				listIndestry.add(jsaIndustrySetSectorSelect
						.getString("industry"));
			}

			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
					context, R.layout.spinner_dropdrow_item, listIndestry);
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			spn_trendsignal_industry.setAdapter(dataAdapter);
			spn_trendsignal_industry
					.setOnItemSelectedListener(new OnItemSelectedListenerTrendsignalIndustry());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void addItemsOnSpinnerTrendsignalSector() {
		try {
			listSector = new ArrayList<String>();
			listSector.add("ALL");
			if (strSpnIndustry.equals("ALL")) {
				for (int i = 0; i < FragmentChangeActivity.contentGetIndustrySetSector
						.length(); i++) {
					JSONObject jsaIndustrySetSectorSelect;
					jsaIndustrySetSectorSelect = FragmentChangeActivity.contentGetIndustrySetSector
							.getJSONObject(i);

					JSONArray jsaSector = jsaIndustrySetSectorSelect
							.getJSONArray("sector");
					for (int k = 0; k < jsaSector.length(); k++) {
						listSector.add("" + jsaSector.get(k));
					}
				}

				ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
						context, R.layout.spinner_dropdrow_item, listSector);
				dataAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

				spn_trendsignal_sector.setAdapter(dataAdapter);
				spn_trendsignal_sector
						.setOnItemSelectedListener(new OnItemSelectedListenerTrendsignalSector());
			} else {
				for (int i = 0; i < FragmentChangeActivity.contentGetIndustrySetSector
						.length(); i++) {
					JSONObject jsaIndustrySetSectorSelect;
					jsaIndustrySetSectorSelect = FragmentChangeActivity.contentGetIndustrySetSector
							.getJSONObject(i);

					if (jsaIndustrySetSectorSelect.getString("industry")
							.equals(strSpnIndustry)) {
						JSONArray jsaSector = jsaIndustrySetSectorSelect
								.getJSONArray("sector");

						for (int k = 0; k < jsaSector.length(); k++) {
							listSector.add(jsaSector.get(k).toString());
						}
						ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
								context, R.layout.spinner_dropdrow_item,
								listSector);
						dataAdapter
								.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

						spn_trendsignal_sector.setAdapter(dataAdapter);
						spn_trendsignal_sector
								.setOnItemSelectedListener(new OnItemSelectedListenerTrendsignalSector());
						break;
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// strSpnPeriod = "avg", strSpnIndustry = "ALL", strSpnSector = "ALL";

	public class OnItemSelectedListenerTrendsignalIndustry implements
			OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			if (position == 0) {
				strSpnIndustry = "ALL";
				strSpnSector = "ALL";
				spn_trendsignal_sector.setVisibility(View.GONE);
				loadTrendSignalSelect(); // load trendsignal
			} else {
				strSpnIndustry = listIndestry.get(position);
				spn_trendsignal_sector.setVisibility(View.VISIBLE);
				addItemsOnSpinnerTrendsignalSector();
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	public class OnItemSelectedListenerTrendsignalSector implements
			OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			if (position == 0) {
				strSpnSector = "ALL";
				loadTrendSignalSelect(); // load trendsignal
			} else {
				strSpnSector = listSector.get(position);
				loadTrendSignalSelect(); // load trendsignal
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	// ====== init fundamental spinner ============
	public void addItemsOnSpinnerFundamentalIndustry() {
		try {
			listIndestry = new ArrayList<String>();
			listIndestry.add("ALL");

			for (int i = 0; i < FragmentChangeActivity.contentGetIndustrySetSector
					.length(); i++) {
				JSONObject jsaIndustrySetSectorSelect = FragmentChangeActivity.contentGetIndustrySetSector
						.getJSONObject(i);

				listIndestry.add(""
						+ jsaIndustrySetSectorSelect.getString("industry"));
			}
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
					context, R.layout.spinner_dropdrow_item, listIndestry);
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

			spn_fundamental_industry.setAdapter(dataAdapter);
			spn_fundamental_industry
					.setOnItemSelectedListener(new OnItemSelectedListenerFundamentalIndustry());
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void addItemsOnSpinnerFundamentalSector() {
		try {
			listSector = new ArrayList<String>();
			listSector.add("ALL");
			if (strSpnIndustry.equals("ALL")) {
				for (int i = 0; i < FragmentChangeActivity.contentGetIndustrySetSector
						.length(); i++) {
					JSONObject jsaIndustrySetSectorSelect;
					jsaIndustrySetSectorSelect = FragmentChangeActivity.contentGetIndustrySetSector
							.getJSONObject(i);

					JSONArray jsaSector = jsaIndustrySetSectorSelect
							.getJSONArray("sector");
					for (int k = 0; k < jsaSector.length(); k++) {
						listSector.add("" + jsaSector.get(k));
					}
				}

				ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
						context, R.layout.spinner_dropdrow_item, listSector);
				dataAdapter
						.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

				spn_fundamental_sector.setAdapter(dataAdapter);
				spn_fundamental_sector
						.setOnItemSelectedListener(new OnItemSelectedListenerFundamentalSector());
			} else {
				for (int i = 0; i < FragmentChangeActivity.contentGetIndustrySetSector
						.length(); i++) {
					JSONObject jsaIndustrySetSectorSelect;
					jsaIndustrySetSectorSelect = FragmentChangeActivity.contentGetIndustrySetSector
							.getJSONObject(i);

					if (jsaIndustrySetSectorSelect.getString("industry")
							.equals(strSpnIndustry)) {
						JSONArray jsaSector = jsaIndustrySetSectorSelect
								.getJSONArray("sector");

						for (int k = 0; k < jsaSector.length(); k++) {
							listSector.add(jsaSector.get(k).toString());
						}
						ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
								context, R.layout.spinner_dropdrow_item,
								listSector);
						dataAdapter
								.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

						spn_fundamental_sector.setAdapter(dataAdapter);
						spn_fundamental_sector
								.setOnItemSelectedListener(new OnItemSelectedListenerFundamentalSector());
						break;
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public class OnItemSelectedListenerFundamentalIndustry implements
			OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			if (position == 0) {
				strSpnIndustry = "ALL";
				strSpnSector = "ALL";
				spn_fundamental_sector.setVisibility(View.GONE);
				loadFundamentalSelect(); // load trendsignal
			} else {
				strSpnIndustry = listIndestry.get(position);
				spn_fundamental_sector.setVisibility(View.VISIBLE);
				addItemsOnSpinnerFundamentalSector();
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
	}

	public class OnItemSelectedListenerFundamentalSector implements
			OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			if (position == 0) {
				strSpnSector = "ALL";
				loadFundamentalSelect(); // load fundamental
			} else {
				strSpnSector = listSector.get(position);
				loadFundamentalSelect(); // load fundamental
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}
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

	// ========== set data list ========
	public static LinearLayout li_title_trendsignal;
	public static Spinner spn_trendsignal_industry, spn_trendsignal_sector;
	public static TextView tv_trendsignal_average, tv_trendsignal_1w,
			tv_trendsignal_1m, tv_trendsignal_3m;
	public static View v_trendsignal_average, v_trendsignal_1w,
			v_trendsignal_1m, v_trendsignal_3m;

	public static LinearLayout li_title_fundamental;
	public static Spinner spn_fundamental_industry, spn_fundamental_sector;

	public static LinearLayout li_list_symbol_detail;
	public static TextView tv_pager_title, tv_pager_title_sub;

	public void setInitTapSpinner() {
		strSpnPeriod = "avg";
		strSpnIndustry = "ALL";
		strSpnSector = "ALL";

		// ----------- trndsignal ----------
		li_title_trendsignal = (LinearLayout) rootView
				.findViewById(R.id.li_title_trendsignal);
		li_title_trendsignal.setVisibility(View.GONE);

		spn_trendsignal_industry = (Spinner) rootView
				.findViewById(R.id.spn_trendsignal_industry);

		spn_trendsignal_sector = (Spinner) rootView
				.findViewById(R.id.spn_trendsignal_sector);

		// ----------- fundamental ----------
		li_title_fundamental = (LinearLayout) rootView
				.findViewById(R.id.li_title_fundamental);
		li_title_fundamental.setVisibility(View.GONE);

		spn_fundamental_industry = (Spinner) rootView
				.findViewById(R.id.spn_fundamental_industry);
		spn_fundamental_sector = (Spinner) rootView
				.findViewById(R.id.spn_fundamental_sector);

		if (FragmentChangeActivity.strWatchlistCategory == "trendsignal") {
			li_title_trendsignal.setVisibility(View.VISIBLE);
			addItemsOnSpinnerTrendsignalIndustry(); // init spinner
			selectTabTrendsignal();
		} else if (FragmentChangeActivity.strWatchlistCategory == "fundamental") {
			li_title_fundamental.setVisibility(View.VISIBLE);
			addItemsOnSpinnerFundamentalIndustry(); // init spinner
		}
	}

	public void selectTabTrendsignal() {
		tv_trendsignal_average = (TextView) rootView
				.findViewById(R.id.tv_trendsignal_average);
		tv_trendsignal_1w = (TextView) rootView
				.findViewById(R.id.tv_trendsignal_1w);
		tv_trendsignal_1m = (TextView) rootView
				.findViewById(R.id.tv_trendsignal_1m);
		tv_trendsignal_3m = (TextView) rootView
				.findViewById(R.id.tv_trendsignal_3m);

		v_trendsignal_average = (View) rootView
				.findViewById(R.id.v_trendsignal_average);
		v_trendsignal_1w = (View) rootView.findViewById(R.id.v_trendsignal_1w);
		v_trendsignal_1m = (View) rootView.findViewById(R.id.v_trendsignal_1m);
		v_trendsignal_3m = (View) rootView.findViewById(R.id.v_trendsignal_3m);

		tv_trendsignal_average.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setTabTrendsignalDefault();
				strSpnPeriod = "avg";

				loadTrendSignalSelect(); // load trendsignal
				v_trendsignal_average.setBackgroundResource(R.color.c_success);
			}
		});
		tv_trendsignal_1w.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setTabTrendsignalDefault();
				strSpnPeriod = "1W";

				loadTrendSignalSelect(); // load trendsignal
				v_trendsignal_1w.setBackgroundResource(R.color.c_success);
			}
		});
		tv_trendsignal_1m.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setTabTrendsignalDefault();
				strSpnPeriod = "1M";

				loadTrendSignalSelect(); // load trendsignal
				v_trendsignal_1m.setBackgroundResource(R.color.c_success);
			}
		});
		tv_trendsignal_3m.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setTabTrendsignalDefault();
				strSpnPeriod = "3M";

				loadTrendSignalSelect(); // load trendsignal
				v_trendsignal_3m.setBackgroundResource(R.color.c_success);
			}
		});
	}

	public static void setTabTrendsignalDefault() {
		tv_trendsignal_average.setTextColor(context.getResources().getColor(
				R.color.c_content));
		tv_trendsignal_1w.setTextColor(context.getResources().getColor(
				R.color.c_content));
		tv_trendsignal_1m.setTextColor(context.getResources().getColor(
				R.color.c_content));
		tv_trendsignal_3m.setTextColor(context.getResources().getColor(
				R.color.c_content));

		v_trendsignal_average.setBackgroundResource(context.getResources()
				.getColor(android.R.color.transparent));
		v_trendsignal_1w.setBackgroundResource(context.getResources().getColor(
				android.R.color.transparent));
		v_trendsignal_1m.setBackgroundResource(context.getResources().getColor(
				android.R.color.transparent));
		v_trendsignal_3m.setBackgroundResource(context.getResources().getColor(
				android.R.color.transparent));
	}

	public void setWatchlistSymbol() {
		FragmentChangeActivity.ckLoadWatchlist = false;

		try {
			// setInitTapSpinner();

			tv_pager_title = (TextView) rootView
					.findViewById(R.id.tv_pager_title);
			tv_pager_title_sub = (TextView) rootView
					.findViewById(R.id.tv_pager_title_sub);

			li_list_symbol_detail = (LinearLayout) rootView
					.findViewById(R.id.li_list_symbol_detail);

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

			final LinearLayout col_title_detail, col_title_detail_fundamental, col_title_detail_portfolio, col_title_detail_trendsignal;
			col_title_detail = (LinearLayout) rootView
					.findViewById(R.id.col_title_detail);
			col_title_detail_fundamental = (LinearLayout) rootView
					.findViewById(R.id.col_title_detail_fundamental);
			col_title_detail_portfolio = (LinearLayout) rootView
					.findViewById(R.id.col_title_detail_portfolio);
			col_title_detail_trendsignal = (LinearLayout) rootView
					.findViewById(R.id.col_title_detail_trendsignal);

			col_title_detail.setVisibility(View.GONE);
			col_title_detail_fundamental.setVisibility(View.GONE);
			col_title_detail_portfolio.setVisibility(View.GONE);
			col_title_detail_trendsignal.setVisibility(View.GONE);

			// ------- fundamental ------
			if (FragmentChangeActivity.strWatchlistCategory == "fundamental") {
				tv_pager_title_sub.setText("Fundamental - "
						+ FragmentChangeActivity.strWatchlistCategorySelect);

				col_title_detail_fundamental.setVisibility(View.VISIBLE);
				if (FragmentChangeActivity.contentGetWatchlistSymbol != null) {
					for (int i = 0; i < FragmentChangeActivity.contentGetWatchlistSymbol
							.length(); i++) {
						View viewSymbol = ((Activity) context)
								.getLayoutInflater().inflate(
										R.layout.row_watchlist_symbol, null);

						LayoutInflater inflater = (LayoutInflater) context
								.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						View viewDetailFun = inflater
								.inflate(
										R.layout.row_watchlist_detail_fundamental,
										null);

						// ------------ check package
						TextView tv_sft_fun = (TextView) rootView
								.findViewById(R.id.tv_sft_fun);
						LinearLayout li_sft = (LinearLayout) viewDetailFun
								.findViewById(R.id.li_sft);
						tv_sft_fun.setVisibility(View.GONE);
						li_sft.setVisibility(View.GONE);
						if (SplashScreen.contentGetUserById != null) {
							if (!(SplashScreen.contentGetUserById
									.getString("package").equals("free"))) {
								tv_sft_fun.setVisibility(View.VISIBLE);
								li_sft.setVisibility(View.VISIBLE);
							}
						}

						// ------------ set data
						JSONObject jsoIndex = FragmentChangeActivity.contentGetWatchlistSymbol
								.getJSONObject(i);

						// symbol
						final String symbol_name = jsoIndex
								.getString("symbol_name");
						String turnover_list_level = jsoIndex
								.getString("turnover_list_level");
						final String status = jsoIndex.getString("status");
						String status_xd = jsoIndex.getString("status_xd");

						TextView tv_symbol_name = (TextView) viewSymbol
								.findViewById(R.id.tv_symbol_name);

						// // status check out
						// String status_checkOut = jsoIndex
						// .getString("status_checkOut");
						// if (!status_checkOut.equals("false")) {
						tv_symbol_name
								.setText(Html.fromHtml(FunctionFormatData
										.checkStatusSymbol(symbol_name,
												turnover_list_level, status,
												status_xd)));

						((TextView) viewSymbol
								.findViewById(R.id.tv_symbol_fullname_eng))
								.setText(jsoIndex
										.getString("symbol_fullname_eng"));

						((LinearLayout) viewSymbol
								.findViewById(R.id.row_symbol))
								.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										FragmentChangeActivity.pagerDetail = "watchlist";
										FragmentChangeActivity.strSymbolSelect = symbol_name;

										if (!status.equals("SP")) {
											context.startActivity(new Intent(
													context,
													UiWatchlistDetail.class));
										}
									}
								});

						// detail
						((LinearLayout) viewDetailFun
								.findViewById(R.id.row_detail))
								.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										FragmentChangeActivity.pagerDetail = "watchlist";
										FragmentChangeActivity.strSymbolSelect = symbol_name;

										if (!status.equals("SP")) {
											context.startActivity(new Intent(
													context,
													UiWatchlistDetail.class));
										}
									}
								});

						// fun
						String strFundam = jsoIndex.getString("fundamental");
						TextView tv_fundamental = (TextView) viewDetailFun
								.findViewById(R.id.tv_fundamental);
						tv_fundamental.setBackgroundColor(FunctionSetBg
								.setColorWatchListSymbolFundamental(strFundam));

						// img chart
						ImageView img_chart = (ImageView) viewDetailFun
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

						TextView tv_last_trade = (TextView) viewDetailFun
								.findViewById(R.id.tv_last_trade);
						TextView tv_change = (TextView) viewDetailFun
								.findViewById(R.id.tv_change);
						TextView tv_percentChange = (TextView) viewDetailFun
								.findViewById(R.id.tv_percentChange);

						tv_last_trade.setText(strLastTrade);
						tv_change.setText(strChange);
						if ((strPercentChange == "0")
								|| (strPercentChange == "")
								|| (strPercentChange == "0.00")) {
							tv_percentChange.setText("0.00");
						} else {
							tv_percentChange.setText(strPercentChange + "%");
						}

						// เซตสี change , lasttrade, percentchange เป็นสีตาม
						// change โดยเอา change เทียบกับ 0
						if (strChange != "") {
							if (!status.equals("SP")) {
								tv_change.setTextColor(context.getResources()
										.getColor(
												FunctionSetBg
														.setColor(strChange)));
								tv_last_trade.setTextColor(context
										.getResources().getColor(
												FunctionSetBg
														.setColor(strChange)));
								tv_percentChange.setTextColor(context
										.getResources().getColor(
												FunctionSetBg
														.setColor(strChange)));
							}
						}

						// color sft
						String strFundamF = jsoIndex.getString("fundamental");
						TextView tv_fundamentalF = (TextView) viewDetailFun
								.findViewById(R.id.tv_fundamental);
						tv_fundamentalF
								.setBackgroundColor(FunctionSetBg
										.setColorWatchListSymbolFundamental(strFundamF));

						// ck pe pbv peg
						String strPe = jsoIndex.getString("p_e");
						String strPbv = jsoIndex.getString("p_bv");
						String strRoe = jsoIndex.getString("roe");
						String strRoa = jsoIndex.getString("roa");
						String strPeg = jsoIndex.getString("peg");
						String strMktcap = jsoIndex
								.getString("market_capitalization");

						TextView tv_p_e = (TextView) viewDetailFun
								.findViewById(R.id.tv_p_e);
						TextView tv_p_bv = (TextView) viewDetailFun
								.findViewById(R.id.tv_p_bv);
						TextView tv_roe = (TextView) viewDetailFun
								.findViewById(R.id.tv_roe);
						TextView tv_roa = (TextView) viewDetailFun
								.findViewById(R.id.tv_roa);
						TextView tv_peg = (TextView) viewDetailFun
								.findViewById(R.id.tv_peg);
						TextView tv_mktcap = (TextView) viewDetailFun
								.findViewById(R.id.tv_mktcap);

						tv_p_e.setText(FunctionSetBg.setStrDetailList(strPe));
						tv_p_bv.setText(FunctionSetBg.setStrDetailList(strPbv));
						tv_roe.setText(FunctionSetBg.setStrDetailList(strRoe));
						tv_roa.setText(FunctionSetBg.setStrDetailList(strRoa));
						tv_peg.setText(FunctionSetBg.setStrDetailList(strPeg));
						tv_mktcap.setText(strMktcap);

						String strPeg_set = SplashScreen.contentSymbol_Set
								.getString("peg");
						tv_peg.setTextColor(context.getResources().getColor(
								FunctionSetBg
										.setStrCheckSet(strPeg, strPeg_set)));

						// -- color write/blue
						if (!status.equals("SP")) {
							tv_roe.setTextColor(context
									.getResources()
									.getColor(
											FunctionSetBg
													.setStrColorWriteDetailBlue(strRoe)));
							tv_roa.setTextColor(context
									.getResources()
									.getColor(
											FunctionSetBg
													.setStrColorWriteDetailBlue(strRoa)));
						} else {
							tv_roe.setTextColor(context.getResources()
									.getColor(R.color.c_content));
							tv_roa.setTextColor(context.getResources()
									.getColor(R.color.c_content));
						}

						if (SplashScreen.contentSymbol_Set != null) {
							String strPe_set = SplashScreen.contentSymbol_Set
									.getString("p_e");
							String strPbv_set = SplashScreen.contentSymbol_Set
									.getString("p_bv");
							// String strPeg_set =
							// SplashScreen.contentSymbol_Set
							// .getString("peg");
							//
							// tv_peg.setTextColor(context.getResources()
							// .getColor(
							// FunctionSetBg.setStrCheckSet(
							// strPeg, strPeg_set)));

							if (!status.equals("SP")) {
								tv_p_e.setTextColor(context.getResources()
										.getColor(
												FunctionSetBg.setStrCheckSet(
														strPe, strPe_set)));

								tv_p_bv.setTextColor(context.getResources()
										.getColor(
												FunctionSetBg.setStrCheckSet(
														strPbv, strPbv_set)));

								// tv_peg.setTextColor(context.getResources()
								// .getColor(
								// FunctionSetBg.setStrCheckSet(
								// strPeg, strPeg_set)));
							} else {
								tv_p_e.setTextColor(context.getResources()
										.getColor(R.color.c_content));

								tv_p_bv.setTextColor(context.getResources()
										.getColor(R.color.c_content));

								// tv_peg.setTextColor(context.getResources()
								// .getColor(R.color.c_content));
							}
						}
						
						// fun graph
						String strFundamentalTrend = jsoIndex
								.getString("fundamental_trend");

						ImageView img_t_trend = (ImageView) viewDetailFun
								.findViewById(R.id.img_t_trend);
						TextView tv_fundamental_trend = (TextView) viewDetailFun
								.findViewById(R.id.tv_fundamental_trend);

						FragmentChangeActivity.imageLoader.displayImage(
								SplashScreen.url_bidschart_chart
										+ "fundamental-"
										+ jsoIndex.getString("symbol_name")
										+ ".png", img_t_trend);

						tv_fundamental_trend.setText(strFundamentalTrend);
						if (!status.equals("SP")) {
							tv_fundamental_trend
									.setTextColor(context
											.getResources()
											.getColor(
													FunctionSetBg
															.setFundamentalTextColor(strFundamentalTrend)));
						} else {
							tv_fundamental_trend
									.setTextColor(context.getResources()
											.getColor(R.color.c_content));
						}

						// fun กราฟแท่ง
						ImageView img_activity, img_profit, img_lev, img_liq;
						img_activity = (ImageView) viewDetailFun
								.findViewById(R.id.img_activity);
						img_profit = (ImageView) viewDetailFun
								.findViewById(R.id.img_profit);
						img_lev = (ImageView) viewDetailFun
								.findViewById(R.id.img_lev);
						img_liq = (ImageView) viewDetailFun
								.findViewById(R.id.img_liq);

						img_activity
								.setBackgroundResource(FunctionSetBg
										.setImgFunGraph(jsoIndex
												.getString("activity")));
						img_profit.setBackgroundResource(FunctionSetBg
								.setImgFunGraph(jsoIndex
										.getString("profitability")));
						img_lev.setBackgroundResource(FunctionSetBg
								.setImgFunGraph(jsoIndex.getString("leverage")));
						img_liq.setBackgroundResource(FunctionSetBg
								.setImgFunGraph(jsoIndex.getString("liquidity")));

						// not set color
						TextView tv_rank = (TextView) viewDetailFun
								.findViewById(R.id.tv_rank);
						TextView tv_cg = (TextView) viewDetailFun
								.findViewById(R.id.tv_cg);
						TextView tv_volume = (TextView) viewDetailFun
								.findViewById(R.id.tv_volume);
						TextView tv_value = (TextView) viewDetailFun
								.findViewById(R.id.tv_value);

						tv_rank.setText(jsoIndex.getString("rangkingsector"));
						tv_cg.setText(jsoIndex.getString("cgscore"));

						String strVolume = jsoIndex.getString("volume");
						String strValue = jsoIndex.getString("value");
						String sptVolume[] = strVolume.split(" ");
						String sptValue[] = strValue.split(" ");

						if (sptVolume.length > 1) {
							tv_volume.setText(sptVolume[0] + "\n"
									+ sptVolume[1]);
						} else {
							tv_volume.setText(strVolume);
						}
						if (sptValue.length > 1) {
							tv_value.setText(sptValue[0] + "\n" + sptValue[1]);
						} else {
							tv_value.setText(strValue);
						}

						// ถ้าเป็น - ให้เป็นสี ขาว
						String strPeColor = tv_p_e.getText().toString();
						String strPbvColor = tv_p_bv.getText().toString();
						String strRankColor = tv_rank.getText().toString();
						String strCgColor = tv_cg.getText().toString();
						String strRoeColor = tv_roe.getText().toString();
						String strRoaColor = tv_roa.getText().toString();
						if(strPeColor.equals("-")){
							tv_p_e.setTextColor(context.getResources()
									.getColor(R.color.c_content));
						}
						if(strPbvColor.equals("-")){
							tv_p_bv.setTextColor(context.getResources()
									.getColor(R.color.c_content));
						}
						if(strRankColor.equals("-")){
							tv_rank.setTextColor(context.getResources()
									.getColor(R.color.c_content));
						}
						if(strCgColor.equals("-")){
							tv_cg.setTextColor(context.getResources()
									.getColor(R.color.c_content));
						}
						if(strRoeColor.equals("-")){
							tv_roe.setTextColor(context.getResources()
									.getColor(R.color.c_content));
						}
						if(strRoaColor.equals("-")){
							tv_roa.setTextColor(context.getResources()
									.getColor(R.color.c_content));
						}
						
						list_symbol.addView(viewSymbol);
						list_detail.addView(viewDetailFun);

					}
				}
				// ------ update symbol -------
				// if (SplashScreen.contentGetUserById != null) {
				// if (!(SplashScreen.contentGetUserById.getString("package")
				// .equals("free"))) {
				//
				// // ---- set status connect
				// ImageView img_connect_c = (ImageView) rootView
				// .findViewById(R.id.img_connect_c);
				// img_connect_c
				// .setBackgroundResource(R.drawable.icon_connect_c_green);
				//
				// // -------- stop timer ----
				// if (FragmentChangeActivity.timerUpdateSymbolStatus) {
				// FragmentChangeActivity.timerUpdateSymbolStatus = false;
				// FragmentChangeActivity.timerUpdateSymbol.cancel();
				// startUpdateSymbol();
				// } else {
				// startUpdateSymbol();
				// }
				// }
				// }

				// ------- portfolio ------
			} else if (FragmentChangeActivity.strWatchlistCategory == "portfolio") {
				tv_pager_title_sub.setText("System Trade Portfolio");

				col_title_detail_portfolio.setVisibility(View.VISIBLE);
				if (FragmentChangeActivity.contentGetWatchlistSystemTrade != null) {
					for (int i = 0; i < FragmentChangeActivity.contentGetWatchlistSystemTrade
							.length(); i++) {
						View viewSymbol = ((Activity) context)
								.getLayoutInflater().inflate(
										R.layout.row_watchlist_symbol, null);

						LayoutInflater inflater = (LayoutInflater) context
								.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						View viewDetailPort = inflater.inflate(
								R.layout.row_watchlist_detail_portfolio, null);

						// ------------ check package
						View v_sft_port = (View) rootView
								.findViewById(R.id.v_sft_port);
						LinearLayout li_sft = (LinearLayout) viewDetailPort
								.findViewById(R.id.li_sft);
						v_sft_port.setVisibility(View.GONE);
						li_sft.setVisibility(View.GONE);
						if (SplashScreen.contentGetUserById != null) {
							if (!(SplashScreen.contentGetUserById
									.getString("package").equals("free"))) {
								v_sft_port.setVisibility(View.VISIBLE);
								li_sft.setVisibility(View.VISIBLE);
							}
						}

						// ------------ set data
						JSONObject jsoIndex = FragmentChangeActivity.contentGetWatchlistSystemTrade
								.getJSONObject(i);

						// symbol
						final String symbol_name = jsoIndex
								.getString("symbol_name");
						String turnover_list_level = jsoIndex
								.getString("turnover_list_level");
						final String status = jsoIndex.getString("status");
						String status_xd = jsoIndex.getString("status_xd");

						TextView tv_symbol_name = (TextView) viewSymbol
								.findViewById(R.id.tv_symbol_name);

						// status check out
						String status_checkOut = jsoIndex
								.getString("status_checkOut");
						if (!status_checkOut.equals("false")) {
							tv_symbol_name.setText(Html.fromHtml(FunctionFormatData
									.checkStatusSymbol(symbol_name,
											turnover_list_level, status,
											status_xd)));

							((TextView) viewSymbol
									.findViewById(R.id.tv_symbol_fullname_eng))
									.setText(jsoIndex
											.getString("symbol_fullname_eng"));

							((LinearLayout) viewSymbol
									.findViewById(R.id.row_symbol))
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											FragmentChangeActivity.pagerDetail = "watchlist";
											FragmentChangeActivity.strSymbolSelect = symbol_name;

											if (!status.equals("SP")) {
												context.startActivity(new Intent(
														context,
														UiWatchlistDetail.class));
											}
										}
									});

							// detail
							((LinearLayout) viewDetailPort
									.findViewById(R.id.row_detail))
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											FragmentChangeActivity.pagerDetail = "watchlist";
											FragmentChangeActivity.strSymbolSelect = symbol_name;

											if (!status.equals("SP")) {
												context.startActivity(new Intent(
														context,
														UiWatchlistDetail.class));
											}
										}
									});

							// img chart
							ImageView img_chart = (ImageView) viewDetailPort
									.findViewById(R.id.img_chart);
							FragmentChangeActivity.imageLoader.displayImage(
									SplashScreen.url_bidschart_chart
											+ jsoIndex.getString("symbol_name")
											+ ".png", img_chart);

							// trend , signal , date
							String strTrade = jsoIndex
									.getString("trade_signal");
							String strSignel = jsoIndex
									.getString("signal_type");
							String strSignelDate = jsoIndex
									.getString("signal_date");

							ImageView img_trade = (ImageView) viewDetailPort
									.findViewById(R.id.img_trade);
							ImageView img_signal = (ImageView) viewDetailPort
									.findViewById(R.id.img_signal);
							TextView tv_signal_date = (TextView) viewDetailPort
									.findViewById(R.id.tv_signal_date);

							img_trade.setBackgroundResource(FunctionSetBg
									.setImgTrendSignal(strTrade));
							img_signal
									.setBackgroundResource(setColorBuySell(strSignel));
							tv_signal_date.setText(DateTimeCreate
									.DateDmyWatchlistPortfolio(strSignelDate)); // 2016-02-05

							// ck ltrade change
							String strLastTrade = jsoIndex
									.getString("last_trade");
							String strChange = jsoIndex.getString("change");
							String strPercentChange = jsoIndex
									.getString("percentChange");

							TextView tv_last_trade = (TextView) viewDetailPort
									.findViewById(R.id.tv_last_trade);
							TextView tv_change = (TextView) viewDetailPort
									.findViewById(R.id.tv_change);
							TextView tv_percentChange = (TextView) viewDetailPort
									.findViewById(R.id.tv_percentChange);

							tv_last_trade.setText(strLastTrade);
							tv_change.setText(strChange);
							if ((strPercentChange == "0")
									|| (strPercentChange == "")
									|| (strPercentChange == "0.00")) {
								tv_percentChange.setText("0.00");
							} else {
								tv_percentChange
										.setText(strPercentChange + "%");
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
									tv_percentChange
											.setTextColor(context
													.getResources()
													.getColor(
															FunctionSetBg
																	.setColor(strChange)));
								}
							}

							// sft
							((LinearLayout) viewDetailPort
									.findViewById(R.id.li_sft))
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											DialogDifinitionSignal.show();
										}
									});

							// color sft
							String strTrends = jsoIndex
									.getString("trendSignal_avg_percentChange");
							String strFundam = jsoIndex
									.getString("fundamental");
							String strColorMacd = jsoIndex
									.getString("color_macd");

							TextView tv_trendSignal_avg_percent = (TextView) viewDetailPort
									.findViewById(R.id.tv_trendSignal_avg_percent);
							TextView tv_fundamental = (TextView) viewDetailPort
									.findViewById(R.id.tv_fundamental);
							TextView tv_color_macd = (TextView) viewDetailPort
									.findViewById(R.id.tv_color_macd);

							tv_trendSignal_avg_percent
									.setBackgroundColor(FunctionSetBg
											.setColorWatchListSymbolTrendSignal(strTrends));
							tv_fundamental
									.setBackgroundColor(FunctionSetBg
											.setColorWatchListSymbolFundamental(strFundam));
							tv_color_macd
									.setBackgroundColor(FunctionSetBg
											.setColorWatchListSymbolColorMacd(strColorMacd));

							// ck hight low
							String strPrevClose = jsoIndex.getString(
									"prev_close").replaceAll(",", "");
							String strHigh = jsoIndex.getString("high")
									.replaceAll(",", "");
							String strLow = jsoIndex.getString("low")
									.replaceAll(",", "");

							TextView tv_high = (TextView) viewDetailPort
									.findViewById(R.id.tv_high);
							TextView tv_low = (TextView) viewDetailPort
									.findViewById(R.id.tv_low);

							tv_high.setText(FunctionSetBg
									.setStrDetailList(strHigh));
							tv_low.setText(FunctionSetBg
									.setStrDetailList(strLow));

							// ----- Trend signal
							if (!status.equals("SP")) {
								if (strPrevClose != "") {
									if (strHigh != "") {
										if ((Float.parseFloat(strHigh
												.replaceAll(",", ""))) > Float
												.parseFloat(strPrevClose
														.replaceAll(",", ""))) {
											tv_high.setTextColor(context
													.getResources()
													.getColor(
															FunctionSetBg.arrColor[2]));
										} else if ((Float.parseFloat(strHigh
												.replaceAll(",", ""))) < Float
												.parseFloat(strPrevClose
														.replaceAll(",", ""))) {
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
										if ((Float.parseFloat(strLow
												.replaceAll(",", ""))) > Float
												.parseFloat(strPrevClose
														.replaceAll(",", ""))) {
											tv_low.setTextColor(context
													.getResources()
													.getColor(
															FunctionSetBg.arrColor[2]));
										} else if ((Float.parseFloat(strLow
												.replaceAll(",", ""))) < Float
												.parseFloat(strPrevClose
														.replaceAll(",", ""))) {
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
							}

							// ck pe pbv peg
							String strPe = jsoIndex.getString("p_e");
							String strPbv = jsoIndex.getString("p_bv");
							String strRoe = jsoIndex.getString("roe");
							String strRoa = jsoIndex.getString("roa");
							String strPeg = jsoIndex.getString("peg");

							TextView tv_p_e = (TextView) viewDetailPort
									.findViewById(R.id.tv_p_e);
							TextView tv_p_bv = (TextView) viewDetailPort
									.findViewById(R.id.tv_p_bv);
							TextView tv_roe = (TextView) viewDetailPort
									.findViewById(R.id.tv_roe);
							TextView tv_roa = (TextView) viewDetailPort
									.findViewById(R.id.tv_roa);
							TextView tv_peg = (TextView) viewDetailPort
									.findViewById(R.id.tv_peg);

							tv_p_e.setText(FunctionSetBg
									.setStrDetailList(strPe));
							tv_p_bv.setText(FunctionSetBg
									.setStrDetailList(strPbv));
							tv_roe.setText(FunctionSetBg
									.setStrDetailList(strRoe));
							tv_roa.setText(FunctionSetBg
									.setStrDetailList(strRoa));
							tv_peg.setText(FunctionSetBg
									.setStrDetailList(strPeg));

							String strPeg_set = SplashScreen.contentSymbol_Set
									.getString("peg");
							tv_peg.setTextColor(context.getResources()
									.getColor(
											FunctionSetBg.setStrCheckSet(
													strPeg, strPeg_set)));

							// -- color write/blue
							if (!status.equals("SP")) {
								tv_roe.setTextColor(context
										.getResources()
										.getColor(
												FunctionSetBg
														.setStrColorWriteDetailBlue(strRoe)));
								tv_roa.setTextColor(context
										.getResources()
										.getColor(
												FunctionSetBg
														.setStrColorWriteDetailBlue(strRoa)));
							} else {
								tv_roe.setTextColor(context.getResources()
										.getColor(R.color.c_content));
								tv_roa.setTextColor(context.getResources()
										.getColor(R.color.c_content));
							}

							if (SplashScreen.contentSymbol_Set != null) {
								String strPe_set = SplashScreen.contentSymbol_Set
										.getString("p_e");
								String strPbv_set = SplashScreen.contentSymbol_Set
										.getString("p_bv");
								// String strPeg_set =
								// SplashScreen.contentSymbol_Set
								// .getString("peg");
								//
								// tv_peg.setTextColor(context.getResources()
								// .getColor(
								// FunctionSetBg.setStrCheckSet(
								// strPeg, strPeg_set)));

								if (!status.equals("SP")) {
									tv_p_e.setTextColor(context.getResources()
											.getColor(
													FunctionSetBg
															.setStrCheckSet(
																	strPe,
																	strPe_set)));

									tv_p_bv.setTextColor(context
											.getResources()
											.getColor(
													FunctionSetBg
															.setStrCheckSet(
																	strPbv,
																	strPbv_set)));

									// tv_peg.setTextColor(context.getResources()
									// .getColor(
									// FunctionSetBg.setStrCheckSet(
									// strPeg, strPeg_set)));
								} else {
									tv_p_e.setTextColor(context.getResources()
											.getColor(R.color.c_content));

									tv_p_bv.setTextColor(context.getResources()
											.getColor(R.color.c_content));

									// tv_peg.setTextColor(context.getResources()
									// .getColor(R.color.c_content));
								}
							}

							// not set color
							TextView tv_volume = (TextView) viewDetailPort
									.findViewById(R.id.tv_volume);
							TextView tv_value = (TextView) viewDetailPort
									.findViewById(R.id.tv_value);
							TextView tv_ceiling = (TextView) viewDetailPort
									.findViewById(R.id.tv_ceiling);
							TextView tv_floor = (TextView) viewDetailPort
									.findViewById(R.id.tv_floor);

							String strVolume = jsoIndex.getString("volume");
							String strValue = jsoIndex.getString("value");
							String sptVolume[] = strVolume.split(" ");
							String sptValue[] = strValue.split(" ");

							if (sptVolume.length > 1) {
								tv_volume.setText(sptVolume[0] + "\n"
										+ sptVolume[1]);
							} else {
								tv_volume.setText(strVolume);
							}
							if (sptValue.length > 1) {
								tv_value.setText(sptValue[0] + "\n"
										+ sptValue[1]);
							} else {
								tv_value.setText(strValue);
							}

							tv_ceiling.setText(jsoIndex.getString("ceiling"));
							tv_floor.setText(jsoIndex.getString("floor"));
							if (status.equals("SP")) {
								tv_ceiling.setTextColor(context.getResources()
										.getColor(R.color.c_content));
								tv_floor.setTextColor(context.getResources()
										.getColor(R.color.c_content));
							}
							
							// ถ้าเป็น - ให้เป็นสี ขาว
							String strHighColor = tv_high.getText().toString();
							String strLowColor = tv_low.getText().toString();
							String strCelingColor = tv_ceiling.getText().toString();
							String strFloorColor = tv_floor.getText().toString();
							String strPeColor = tv_p_e.getText().toString();
							String strPbvColor = tv_p_bv.getText().toString();
							String strRoeColor = tv_roe.getText().toString();
							String strRoaColor = tv_roa.getText().toString();
							if(strHighColor.equals("-")){
								tv_high.setTextColor(context.getResources()
										.getColor(R.color.c_content));
							}
							if(strLowColor.equals("-")){
								tv_low.setTextColor(context.getResources()
										.getColor(R.color.c_content));
							}
							if(strCelingColor.equals("-")){
								tv_ceiling.setTextColor(context.getResources()
										.getColor(R.color.c_content));
							}
							if(strFloorColor.equals("-")){
								tv_floor.setTextColor(context.getResources()
										.getColor(R.color.c_content));
							}
							if(strPeColor.equals("-")){
								tv_p_e.setTextColor(context.getResources()
										.getColor(R.color.c_content));
							}
							if(strPbvColor.equals("-")){
								tv_p_bv.setTextColor(context.getResources()
										.getColor(R.color.c_content));
							}
							if(strRoeColor.equals("-")){
								tv_roe.setTextColor(context.getResources()
										.getColor(R.color.c_content));
							}
							if(strRoaColor.equals("-")){
								tv_roa.setTextColor(context.getResources()
										.getColor(R.color.c_content));
							}
							
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

						} else {
							tv_symbol_name.setText(symbol_name);
							tv_symbol_name.setTextColor(context.getResources()
									.getColor(R.color.c_danger));
						}

						// --- add view tv
						list_symbol.addView(viewSymbol);
						list_detail.addView(viewDetailPort);

					}
				}

				// ------- trendsignal ------
			} else if (FragmentChangeActivity.strWatchlistCategory == "trendsignal") {
				tv_pager_title_sub.setText("Trend Signal - "
						+ FragmentChangeActivity.strWatchlistCategorySelect);

				col_title_detail_trendsignal.setVisibility(View.VISIBLE);
				if (FragmentChangeActivity.contentGetWatchlistSymbol != null) {
					for (int i = 0; i < FragmentChangeActivity.contentGetWatchlistSymbol
							.length(); i++) {
						View viewSymbol = ((Activity) context)
								.getLayoutInflater().inflate(
										R.layout.row_watchlist_symbol, null);

						LayoutInflater inflater = (LayoutInflater) context
								.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						View viewDetailTrend = inflater
								.inflate(
										R.layout.row_watchlist_detail_trendsignal,
										null);

						// ------------ check package
						View v_sft_trend = (View) rootView
								.findViewById(R.id.v_sft_trend);
						LinearLayout li_sft = (LinearLayout) viewDetailTrend
								.findViewById(R.id.li_sft);
						v_sft_trend.setVisibility(View.GONE);
						li_sft.setVisibility(View.GONE);
						if (SplashScreen.contentGetUserById != null) {
							if (!(SplashScreen.contentGetUserById
									.getString("package").equals("free"))) {
								v_sft_trend.setVisibility(View.VISIBLE);
								li_sft.setVisibility(View.VISIBLE);
							}
						}

						// ------------ set data
						JSONObject jsoIndex = FragmentChangeActivity.contentGetWatchlistSymbol
								.getJSONObject(i);

						// symbol
						final String symbol_name = jsoIndex
								.getString("symbol_name");
						String turnover_list_level = jsoIndex
								.getString("turnover_list_level");
						final String status = jsoIndex.getString("status");
						String status_xd = jsoIndex.getString("status_xd");

						TextView tv_symbol_name = (TextView) viewSymbol
								.findViewById(R.id.tv_symbol_name);

						// status check out
						String status_checkOut = jsoIndex
								.getString("status_checkOut");
						if (!status_checkOut.equals("false")) {
							tv_symbol_name.setText(Html.fromHtml(FunctionFormatData
									.checkStatusSymbol(symbol_name,
											turnover_list_level, status,
											status_xd)));

							((TextView) viewSymbol
									.findViewById(R.id.tv_symbol_fullname_eng))
									.setText(jsoIndex
											.getString("symbol_fullname_eng"));

							((LinearLayout) viewSymbol
									.findViewById(R.id.row_symbol))
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											FragmentChangeActivity.pagerDetail = "watchlist";
											FragmentChangeActivity.strSymbolSelect = symbol_name;

											if (!status.equals("SP")) {
												context.startActivity(new Intent(
														context,
														UiWatchlistDetail.class));
											}
										}
									});

							// detail
							((LinearLayout) viewDetailTrend
									.findViewById(R.id.row_detail))
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											FragmentChangeActivity.pagerDetail = "watchlist";
											FragmentChangeActivity.strSymbolSelect = symbol_name;

											if (!status.equals("SP")) {
												context.startActivity(new Intent(
														context,
														UiWatchlistDetail.class));
											}
										}
									});

							// img chart
							ImageView img_chart = (ImageView) viewDetailTrend
									.findViewById(R.id.img_chart);
							FragmentChangeActivity.imageLoader.displayImage(
									SplashScreen.url_bidschart_chart
											+ jsoIndex.getString("symbol_name")
											+ ".png", img_chart);

							// ck ltrade change
							String strLastTrade = jsoIndex
									.getString("last_trade");
							String strChange = jsoIndex.getString("change");
							String strPercentChange = jsoIndex
									.getString("percentChange");

							ImageView img_updown = (ImageView) viewDetailTrend
									.findViewById(R.id.img_updown);
							TextView tv_last_trade = (TextView) viewDetailTrend
									.findViewById(R.id.tv_last_trade);
							TextView tv_change = (TextView) viewDetailTrend
									.findViewById(R.id.tv_change);
							TextView tv_percentChange = (TextView) viewDetailTrend
									.findViewById(R.id.tv_percentChange);

							tv_last_trade.setText(strLastTrade);
							tv_change.setText(strChange);
							if ((strPercentChange == "0")
									|| (strPercentChange == "")
									|| (strPercentChange == "0.00")) {
								tv_percentChange.setText("0.00");
							} else {
								tv_percentChange
										.setText(strPercentChange + "%");
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
									tv_percentChange
											.setTextColor(context
													.getResources()
													.getColor(
															FunctionSetBg
																	.setColor(strChange)));
								}
							}

							// sft
							((LinearLayout) viewDetailTrend
									.findViewById(R.id.li_sft))
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											DialogDifinitionSignal.show();
										}
									});

							// color sft
							String strTrends = jsoIndex
									.getString("trendSignal_avg_percentChange");
							String strFundam = jsoIndex
									.getString("fundamental");
							String strColorm = jsoIndex.getString("color_macd");

							TextView tv_trendSignal_avg_percent = (TextView) viewDetailTrend
									.findViewById(R.id.tv_trendSignal_avg_percent);
							TextView tv_fundamental = (TextView) viewDetailTrend
									.findViewById(R.id.tv_fundamental);
							TextView tv_color_macd = (TextView) viewDetailTrend
									.findViewById(R.id.tv_color_macd);

							tv_trendSignal_avg_percent
									.setBackgroundColor(FunctionSetBg
											.setColorWatchListSymbolTrendSignal(strTrends));
							tv_fundamental
									.setBackgroundColor(FunctionSetBg
											.setColorWatchListSymbolFundamental(strFundam));
							tv_color_macd
									.setBackgroundColor(FunctionSetBg
											.setColorWatchListSymbolColorMacd(strColorm));

							// ck hight low
							String strPrevClose = jsoIndex.getString(
									"prev_close").replaceAll(",", "");
							String strHigh = jsoIndex.getString("high")
									.replaceAll(",", "");
							String strLow = jsoIndex.getString("low")
									.replaceAll(",", "");

							TextView tv_high = (TextView) viewDetailTrend
									.findViewById(R.id.tv_high);
							TextView tv_low = (TextView) viewDetailTrend
									.findViewById(R.id.tv_low);

							ImageView img_trade_signal = (ImageView) viewDetailTrend
									.findViewById(R.id.img_trade_signal);

							tv_high.setText(FunctionSetBg
									.setStrDetailList(strHigh));
							tv_low.setText(FunctionSetBg
									.setStrDetailList(strLow));

							// ----- Trend signal
							String strTrade_signal = jsoIndex
									.getString("trade_signal");
							img_trade_signal
									.setBackgroundResource(FunctionSetBg
											.setImgTrendSignal(strTrade_signal));

							if (!status.equals("SP")) {
								if (strPrevClose != "") {
									if (strHigh != "") {
										if ((Float.parseFloat(strHigh
												.replaceAll(",", ""))) > Float
												.parseFloat(strPrevClose
														.replaceAll(",", ""))) {
											tv_high.setTextColor(context
													.getResources()
													.getColor(
															FunctionSetBg.arrColor[2]));
										} else if ((Float.parseFloat(strHigh
												.replaceAll(",", ""))) < Float
												.parseFloat(strPrevClose
														.replaceAll(",", ""))) {
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
										if ((Float.parseFloat(strLow
												.replaceAll(",", ""))) > Float
												.parseFloat(strPrevClose
														.replaceAll(",", ""))) {
											tv_low.setTextColor(context
													.getResources()
													.getColor(
															FunctionSetBg.arrColor[2]));
										} else if ((Float.parseFloat(strLow
												.replaceAll(",", ""))) < Float
												.parseFloat(strPrevClose
														.replaceAll(",", ""))) {
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
							}

							// ck pe pbv peg
							String strPe = jsoIndex.getString("p_e");
							String strPbv = jsoIndex.getString("p_bv");
							String strRoe = jsoIndex.getString("roe");
							String strRoa = jsoIndex.getString("roa");
							String strPeg = jsoIndex.getString("peg");

							TextView tv_p_e = (TextView) viewDetailTrend
									.findViewById(R.id.tv_p_e);
							TextView tv_p_bv = (TextView) viewDetailTrend
									.findViewById(R.id.tv_p_bv);
							TextView tv_roe = (TextView) viewDetailTrend
									.findViewById(R.id.tv_roe);
							TextView tv_roa = (TextView) viewDetailTrend
									.findViewById(R.id.tv_roa);
							TextView tv_peg = (TextView) viewDetailTrend
									.findViewById(R.id.tv_peg);

							tv_p_e.setText(FunctionSetBg
									.setStrDetailList(strPe));
							tv_p_bv.setText(FunctionSetBg
									.setStrDetailList(strPbv));
							tv_roe.setText(FunctionSetBg
									.setStrDetailList(strRoe));
							tv_roa.setText(FunctionSetBg
									.setStrDetailList(strRoa));
							tv_peg.setText(FunctionSetBg
									.setStrDetailList(strPeg));

							String strPeg_set = SplashScreen.contentSymbol_Set
									.getString("peg");
							tv_peg.setTextColor(context.getResources()
									.getColor(
											FunctionSetBg.setStrCheckSet(
													strPeg, strPeg_set)));

							// -- color write/blue
							if (!status.equals("SP")) {
								tv_roe.setTextColor(context
										.getResources()
										.getColor(
												FunctionSetBg
														.setStrColorWriteDetailBlue(strRoe)));
								tv_roa.setTextColor(context
										.getResources()
										.getColor(
												FunctionSetBg
														.setStrColorWriteDetailBlue(strRoa)));
							} else {
								tv_roe.setTextColor(context.getResources()
										.getColor(R.color.c_content));
								tv_roa.setTextColor(context.getResources()
										.getColor(R.color.c_content));
							}

							if (!status.equals("SP")) {
								if (SplashScreen.contentSymbol_Set != null) {
									String strPe_set = SplashScreen.contentSymbol_Set
											.getString("p_e");
									String strPbv_set = SplashScreen.contentSymbol_Set
											.getString("p_bv");
									// String strPeg_set =
									// SplashScreen.contentSymbol_Set
									// .getString("peg");

									tv_p_e.setTextColor(context.getResources()
											.getColor(
													FunctionSetBg
															.setStrCheckSet(
																	strPe,
																	strPe_set)));

									tv_p_bv.setTextColor(context
											.getResources()
											.getColor(
													FunctionSetBg
															.setStrCheckSet(
																	strPbv,
																	strPbv_set)));

									// tv_peg.setTextColor(context.getResources()
									// .getColor(
									// FunctionSetBg.setStrCheckSet(
									// strPeg, strPeg_set)));
								}
							} else {
								tv_p_e.setTextColor(context.getResources()
										.getColor(R.color.c_content));

								tv_p_bv.setTextColor(context.getResources()
										.getColor(R.color.c_content));

								// tv_peg.setTextColor(context.getResources()
								// .getColor(R.color.c_content));
							}

							// not set color
							TextView tv_volume = (TextView) viewDetailTrend
									.findViewById(R.id.tv_volume);
							TextView tv_value = (TextView) viewDetailTrend
									.findViewById(R.id.tv_value);
							TextView tv_ceiling = (TextView) viewDetailTrend
									.findViewById(R.id.tv_ceiling);
							TextView tv_floor = (TextView) viewDetailTrend
									.findViewById(R.id.tv_floor);

							String strVolume = jsoIndex.getString("volume");
							String strValue = jsoIndex.getString("value");
							String sptVolume[] = strVolume.split(" ");
							String sptValue[] = strValue.split(" ");

							if (sptVolume.length > 1) {
								tv_volume.setText(sptVolume[0] + "\n"
										+ sptVolume[1]);
							} else {
								tv_volume.setText(strVolume);
							}
							if (sptValue.length > 1) {
								tv_value.setText(sptValue[0] + "\n"
										+ sptValue[1]);
							} else {
								tv_value.setText(strValue);
							}

							tv_ceiling.setText(jsoIndex.getString("ceiling"));
							tv_floor.setText(jsoIndex.getString("floor"));
							if (status.equals("SP")) {
								tv_ceiling.setTextColor(context.getResources()
										.getColor(R.color.c_content));
								tv_floor.setTextColor(context.getResources()
										.getColor(R.color.c_content));
							}

							// ถ้าเป็น - ให้เป็นสี ขาว
							String strHighColor = tv_high.getText().toString();
							String strLowColor = tv_low.getText().toString();
							String strCelingColor = tv_ceiling.getText().toString();
							String strFloorColor = tv_floor.getText().toString();
							String strPeColor = tv_p_e.getText().toString();
							String strPbvColor = tv_p_bv.getText().toString();
							String strRoeColor = tv_roe.getText().toString();
							String strRoaColor = tv_roa.getText().toString();
							if(strHighColor.equals("-")){
								tv_high.setTextColor(context.getResources()
										.getColor(R.color.c_content));
							}
							if(strLowColor.equals("-")){
								tv_low.setTextColor(context.getResources()
										.getColor(R.color.c_content));
							}
							if(strCelingColor.equals("-")){
								tv_ceiling.setTextColor(context.getResources()
										.getColor(R.color.c_content));
							}
							if(strFloorColor.equals("-")){
								tv_floor.setTextColor(context.getResources()
										.getColor(R.color.c_content));
							}
							if(strPeColor.equals("-")){
								tv_p_e.setTextColor(context.getResources()
										.getColor(R.color.c_content));
							}
							if(strPbvColor.equals("-")){
								tv_p_bv.setTextColor(context.getResources()
										.getColor(R.color.c_content));
							}
							if(strRoeColor.equals("-")){
								tv_roe.setTextColor(context.getResources()
										.getColor(R.color.c_content));
							}
							if(strRoaColor.equals("-")){
								tv_roa.setTextColor(context.getResources()
										.getColor(R.color.c_content));
							}
														
							// --- add view tv
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

						} else {
							tv_symbol_name.setText(symbol_name);
							tv_symbol_name.setTextColor(context.getResources()
									.getColor(R.color.c_danger));
						}

						list_symbol.addView(viewSymbol);
						list_detail.addView(viewDetailTrend);

					}
				}
				// ------ update symbol -------
				// if (SplashScreen.contentGetUserById != null) {
				// if (!(SplashScreen.contentGetUserById.getString("package")
				// .equals("free"))) {
				//
				// // ---- set status connect
				// ImageView img_connect_c = (ImageView) rootView
				// .findViewById(R.id.img_connect_c);
				// img_connect_c
				// .setBackgroundResource(R.drawable.icon_connect_c_green);
				//
				// // -------- stop timer ----
				// if (FragmentChangeActivity.timerUpdateSymbolStatus) {
				// FragmentChangeActivity.timerUpdateSymbolStatus = false;
				// FragmentChangeActivity.timerUpdateSymbol.cancel();
				// startUpdateSymbol();
				// } else {
				// startUpdateSymbol();
				// }
				// }
				// }

				// ------- favorite ------
			} else {
				FragmentChangeActivity.list_webview
						.removeAll(FragmentChangeActivity.list_webview);

				if (FragmentChangeActivity.strWatchlistCategory == "topmost") {
					tv_pager_title_sub
							.setText("Topmost - "
									+ FragmentChangeActivity.strWatchlistCategorySelect);
				} else if (FragmentChangeActivity.strWatchlistCategory == "industry") {
					tv_pager_title_sub
							.setText(FragmentChangeActivity.strWatchlistCategorySelect
									+ " - "
									+ FragmentChangeActivity.strIndustrySelect);
				} else if (FragmentChangeActivity.strWatchlistCategory == "favorite") {
					tv_pager_title_sub.setText("Favorite "
							+ FragmentChangeActivity.strFavoriteNumber);
				} else {
					tv_pager_title_sub.setText("Favorite "
							+ FragmentChangeActivity.strFavoriteNumber);
				}

				col_title_detail.setVisibility(View.VISIBLE);
				if (FragmentChangeActivity.contentGetWatchlistSymbol != null) {
					for (int i = 0; i < FragmentChangeActivity.contentGetWatchlistSymbol
							.length(); i++) {
						View viewSymbol = ((Activity) context)
								.getLayoutInflater().inflate(
										R.layout.row_watchlist_symbol, null);

						LayoutInflater inflater = (LayoutInflater) context
								.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						View viewDetail = inflater.inflate(
								R.layout.row_watchlist_detail, null);

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

						// ------------ set data
						JSONObject jsoIndex = FragmentChangeActivity.contentGetWatchlistSymbol
								.getJSONObject(i);

						// Log.v("watchlist jsoIndex", ""+jsoIndex);

						// symbol
						final String symbol_name = jsoIndex
								.getString("symbol_name");
						String turnover_list_level = jsoIndex
								.getString("turnover_list_level");
						final String status = jsoIndex.getString("status");
						String status_xd = jsoIndex.getString("status_xd");

						TextView tv_symbol_name = (TextView) viewSymbol
								.findViewById(R.id.tv_symbol_name);

						// status check out
						String status_checkOut = jsoIndex
								.getString("status_checkOut");
						if (!status_checkOut.equals("false")) {
							tv_symbol_name.setText(Html.fromHtml(FunctionFormatData
									.checkStatusSymbol(symbol_name,
											turnover_list_level, status,
											status_xd)));

							((TextView) viewSymbol
									.findViewById(R.id.tv_symbol_fullname_eng))
									.setText(jsoIndex
											.getString("symbol_fullname_eng"));

							((LinearLayout) viewSymbol
									.findViewById(R.id.row_symbol))
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											FragmentChangeActivity.pagerDetail = "watchlist";
											FragmentChangeActivity.strSymbolSelect = symbol_name;

											if (!status.equals("SP")) {
												context.startActivity(new Intent(
														context,
														UiWatchlistDetail.class));
											}
										}
									});

							// detail
							((LinearLayout) viewDetail
									.findViewById(R.id.row_detail))
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											FragmentChangeActivity.pagerDetail = "watchlist";
											FragmentChangeActivity.strSymbolSelect = symbol_name;

											if (!status.equals("SP")) {
												context.startActivity(new Intent(
														context,
														UiWatchlistDetail.class));
											}
										}
									});

							// img chart
							ImageView img_chart = (ImageView) viewDetail
									.findViewById(R.id.img_chart);
							FragmentChangeActivity.imageLoader.displayImage(
									SplashScreen.url_bidschart_chart
											+ jsoIndex.getString("symbol_name")
											+ ".png", img_chart);

							// ck ltrade change
							String strLastTrade = jsoIndex
									.getString("last_trade");
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
							if ((strPercentChange == "0")
									|| (strPercentChange == "")
									|| (strPercentChange == "0.00")) {
								tv_percentChange.setText("0.00");
							} else {
								tv_percentChange
										.setText(strPercentChange + "%");
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
									tv_percentChange
											.setTextColor(context
													.getResources()
													.getColor(
															FunctionSetBg
																	.setColor(strChange)));
								}
							}

							// sft
							((LinearLayout) viewDetail
									.findViewById(R.id.li_sft))
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											DialogDifinitionSignal.show();
										}
									});

							// color sft
							String strTrends = jsoIndex
									.getString("trendSignal_avg_percentChange");
							String strFundam = jsoIndex
									.getString("fundamental");
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
							tv_fundamental
									.setBackgroundColor(FunctionSetBg
											.setColorWatchListSymbolFundamental(strFundam));
							tv_color_macd
									.setBackgroundColor(FunctionSetBg
											.setColorWatchListSymbolColorMacd(strColorm));

							// ck hight low
							String strPrevClose = jsoIndex.getString(
									"prev_close").replaceAll(",", "");
							String strHigh = jsoIndex.getString("high")
									.replaceAll(",", "");
							String strLow = jsoIndex.getString("low")
									.replaceAll(",", "");

							TextView tv_high = (TextView) viewDetail
									.findViewById(R.id.tv_high);
							TextView tv_low = (TextView) viewDetail
									.findViewById(R.id.tv_low);

							ImageView img_trade_signal = (ImageView) viewDetail
									.findViewById(R.id.img_trade_signal);

							tv_high.setText(FunctionSetBg
									.setStrDetailList(strHigh));
							tv_low.setText(FunctionSetBg
									.setStrDetailList(strLow));

							// ----- Trend signal
							String strTrade_signal = jsoIndex
									.getString("trade_signal");
							img_trade_signal
									.setBackgroundResource(FunctionSetBg
											.setImgTrendSignal(strTrade_signal));

							if (!status.equals("SP")) {
								if (strPrevClose != "") {
									if (strHigh != "") {
										if ((Float.parseFloat(strHigh
												.replaceAll(",", ""))) > Float
												.parseFloat(strPrevClose
														.replaceAll(",", ""))) {
											tv_high.setTextColor(context
													.getResources()
													.getColor(
															FunctionSetBg.arrColor[2]));
										} else if ((Float.parseFloat(strHigh
												.replaceAll(",", ""))) < Float
												.parseFloat(strPrevClose
														.replaceAll(",", ""))) {
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
										if ((Float.parseFloat(strLow
												.replaceAll(",", ""))) > Float
												.parseFloat(strPrevClose
														.replaceAll(",", ""))) {
											tv_low.setTextColor(context
													.getResources()
													.getColor(
															FunctionSetBg.arrColor[2]));
										} else if ((Float.parseFloat(strLow
												.replaceAll(",", ""))) < Float
												.parseFloat(strPrevClose
														.replaceAll(",", ""))) {
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
							} else {
								tv_high.setTextColor(context.getResources()
										.getColor(R.color.c_content));
								tv_low.setTextColor(context.getResources()
										.getColor(R.color.c_content));
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

							tv_p_e.setText(FunctionSetBg
									.setStrDetailList(strPe));
							tv_p_bv.setText(FunctionSetBg
									.setStrDetailList(strPbv));
							tv_roe.setText(FunctionSetBg
									.setStrDetailList(strRoe));
							tv_roa.setText(FunctionSetBg
									.setStrDetailList(strRoa));
							tv_peg.setText(FunctionSetBg
									.setStrDetailList(strPeg));

							String strPeg_set = SplashScreen.contentSymbol_Set
									.getString("peg");
							tv_peg.setTextColor(context.getResources()
									.getColor(
											FunctionSetBg.setStrCheckSet(
													strPeg, strPeg_set)));

							// -- color write/blue
							if (!status.equals("SP")) {
								tv_roe.setTextColor(context
										.getResources()
										.getColor(
												FunctionSetBg
														.setStrColorWriteDetailBlue(strRoe)));
								tv_roa.setTextColor(context
										.getResources()
										.getColor(
												FunctionSetBg
														.setStrColorWriteDetailBlue(strRoa)));
							} else {
								tv_roe.setTextColor(context.getResources()
										.getColor(R.color.c_content));
								tv_roa.setTextColor(context.getResources()
										.getColor(R.color.c_content));
							}

							if (!status.equals("SP")) {
								if (SplashScreen.contentSymbol_Set != null) {
									String strPe_set = SplashScreen.contentSymbol_Set
											.getString("p_e");
									String strPbv_set = SplashScreen.contentSymbol_Set
											.getString("p_bv");
									// String strPeg_set =
									// SplashScreen.contentSymbol_Set
									// .getString("peg");

									tv_p_e.setTextColor(context.getResources()
											.getColor(
													FunctionSetBg
															.setStrCheckSet(
																	strPe,
																	strPe_set)));

									tv_p_bv.setTextColor(context
											.getResources()
											.getColor(
													FunctionSetBg
															.setStrCheckSet(
																	strPbv,
																	strPbv_set)));

									// tv_peg.setTextColor(context.getResources()
									// .getColor(
									// FunctionSetBg.setStrCheckSet(
									// strPeg, strPeg_set)));
								}
							} else {
								tv_p_e.setTextColor(context.getResources()
										.getColor(R.color.c_content));

								tv_p_bv.setTextColor(context.getResources()
										.getColor(R.color.c_content));

								// tv_peg.setTextColor(context.getResources()
								// .getColor(R.color.c_content));
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
								tv_volume.setText(sptVolume[0] + "\n"
										+ sptVolume[1]);
							} else {
								tv_volume.setText(strVolume);
							}
							if (sptValue.length > 1) {
								tv_value.setText(sptValue[0] + "\n"
										+ sptValue[1]);
							} else {
								tv_value.setText(strValue);
							}

							tv_ceiling.setText(jsoIndex.getString("ceiling"));
							tv_floor.setText(jsoIndex.getString("floor"));
							if (status.equals("SP")) {
								tv_ceiling.setTextColor(context.getResources()
										.getColor(R.color.c_content));
								tv_floor.setTextColor(context.getResources()
										.getColor(R.color.c_content));
							}
							
							// ถ้าเป็น - ให้เป็นสี ขาว
							String strHighColor = tv_high.getText().toString();
							String strLowColor = tv_low.getText().toString();
							String strCelingColor = tv_ceiling.getText().toString();
							String strFloorColor = tv_floor.getText().toString();
							String strPeColor = tv_p_e.getText().toString();
							String strPbvColor = tv_p_bv.getText().toString();
							String strRoeColor = tv_roe.getText().toString();
							String strRoaColor = tv_roa.getText().toString();
							if(strHighColor.equals("-")){
								tv_high.setTextColor(context.getResources()
										.getColor(R.color.c_content));
							}
							if(strLowColor.equals("-")){
								tv_low.setTextColor(context.getResources()
										.getColor(R.color.c_content));
							}
							if(strCelingColor.equals("-")){
								tv_ceiling.setTextColor(context.getResources()
										.getColor(R.color.c_content));
							}
							if(strFloorColor.equals("-")){
								tv_floor.setTextColor(context.getResources()
										.getColor(R.color.c_content));
							}
							if(strPeColor.equals("-")){
								tv_p_e.setTextColor(context.getResources()
										.getColor(R.color.c_content));
							}
							if(strPbvColor.equals("-")){
								tv_p_bv.setTextColor(context.getResources()
										.getColor(R.color.c_content));
							}
							if(strRoeColor.equals("-")){
								tv_roe.setTextColor(context.getResources()
										.getColor(R.color.c_content));
							}
							if(strRoaColor.equals("-")){
								tv_roa.setTextColor(context.getResources()
										.getColor(R.color.c_content));
							}
							

							// --- add view tv
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
						} else {
							tv_symbol_name.setText(symbol_name);
							tv_symbol_name.setTextColor(context.getResources()
									.getColor(R.color.c_danger));
						}

						list_symbol.addView(viewSymbol);
						list_detail.addView(viewDetail);

						// --------------------------------
						// View viewChart = inflater.inflate(
						// R.layout.add_webview, null);
						// WebView wv_chart =
						// (WebView)viewChart.findViewById(R.id.wv_chart);
						// wv_chart.getSettings().setLoadWithOverviewMode(true);
						// wv_chart.getSettings().setUseWideViewPort(true);
						// wv_chart.getSettings().setBuiltInZoomControls(true);
						// wv_chart.getSettings().setJavaScriptEnabled(true);
						// wv_chart.loadUrl(SplashScreen.url_bidschart
						// + "/iq/stx-mobile-3.html#" + symbol_name);
						// FragmentChangeActivity.arrView_wv.add(wv_chart);
						//
						// FragmentChangeActivity.linear_wv.add(list_detail);

						// LayoutInflater inflater = (LayoutInflater)
						// context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
						// ViewGroup parent =
						// (ViewGroup)findViewById(R.id.where_you_want_to_insert);
						// inflater.inflate(R.layout.the_child_view, parent);

					}
				}

				dialogLoading.dismiss();

				// ------ update symbol -------
				// if (SplashScreen.contentGetUserById != null) {
				// if (!(SplashScreen.contentGetUserById.getString("package")
				// .equals("free"))) {
				//
				// // ---- set status connect
				// ImageView img_connect_c = (ImageView) rootView
				// .findViewById(R.id.img_connect_c);
				// img_connect_c
				// .setBackgroundResource(R.drawable.icon_connect_c_green);
				//
				// // -------- stop timer ----
				// if (FragmentChangeActivity.timerUpdateSymbolStatus) {
				// FragmentChangeActivity.timerUpdateSymbolStatus = false;
				// FragmentChangeActivity.timerUpdateSymbol.cancel();
				// startUpdateSymbol();
				// } else {
				// startUpdateSymbol();
				// }
				// }
				// }
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
		} catch (JSONException e) {
			dialogLoading.dismiss();
			e.printStackTrace();
		}

	}

	// --------------- buy sell
	public static int setColorBuySell(String strColor) {
		int c = R.drawable.icon_empty;
		if (strColor.equals("SELL")) {
			c = R.drawable.icon_system_sell;
		} else if (strColor.equals("BUY")) {
			c = R.drawable.icon_system_buy;
		}
		return c;
	}

	// ============== set =============
	public void getWatchlistSymbol() {
		getSet resp = new getSet();
		resp.execute();
	}

	public class getSet extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;
		// ======= json ========
		private JSONObject jsonGetWatchlistSymbol;

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

			// http://bidschart.com/service/v2/watchlistSymbol?symbol=.set,.set50,.set100,.setHD,.mai

			String url_GetWatchlistSymbol = SplashScreen.url_bidschart
					+ "/service/v2/watchlistSymbolV2?symbol="
					+ FragmentChangeActivity.strGetListSymbol;

//			http://www.bidschart.com/service/v2/watchlistSymbolV2?symbol=AHC,AOT,PPP,KASET,ABC,KDH,QHHR,SBPF,SGF,TASCO
			Log.v("getWatchlistSymbol", "__" + url_GetWatchlistSymbol);

			try {
				// ======= Ui Home ========
				jsonGetWatchlistSymbol = ReadJson
						.readJsonObjectFromUrl(url_GetWatchlistSymbol);

			} catch (IOException e1) {
				connectionError = true;
				jsonGetWatchlistSymbol = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonGetWatchlistSymbol = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonGetWatchlistSymbol = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);


			dialogLoading.dismiss();
			if (connectionError == false) {
				if (jsonGetWatchlistSymbol != null) {
					try {
						// get content

						FragmentChangeActivity.contentGetWatchlistSymbol = jsonGetWatchlistSymbol
								.getJSONArray("dataAll");

						setWatchlistSymbol(); // Initial set data

						dialogLoading.dismiss();
					} catch (JSONException e) {
						dialogLoading.dismiss();
						e.printStackTrace();
					}
				} else {
					dialogLoading.dismiss();
					Log.v("json newslist null", "newslist null");
				}
			} else {
				dialogLoading.dismiss();
			}
			 dialogLoading.dismiss();
		}
	}

	// ***************** SlidingMarquee ******************
	public static void loadTxtSlidingMarquee() {
		loadDataSlidingMarquee resp = new loadDataSlidingMarquee();
		resp.execute();
	}

	public static class loadDataSlidingMarquee extends
			AsyncTask<Void, Void, Void> {

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

	// ============== set =============
	public static void getSymbolSetBegin() {
		getSetBegin resp = new getSetBegin();
		resp.execute();
	}

	public static class getSetBegin extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;
		// ======= json ========
		private JSONObject jsonGetSymbolBynameSet;

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
			String url_GetSymbolByNameSet = SplashScreen.url_bidschart
					+ "/service/getSymbolByName?symbol=.set";

			try {
				// ======= Ui Home ========
				jsonGetSymbolBynameSet = ReadJson
						.readJsonObjectFromUrl(url_GetSymbolByNameSet);

			} catch (IOException e1) {
				connectionError = true;
				jsonGetSymbolBynameSet = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonGetSymbolBynameSet = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonGetSymbolBynameSet = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {

				if (jsonGetSymbolBynameSet != null) {
					// get content
					try {
						SplashScreen.contentSymbol_Set = jsonGetSymbolBynameSet
								.getJSONObject("dataAll");

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.v("json set default null", "set null");
				}
			} else {
			}
		}
	}

	// ***************** IndustrySetSectorSelect ******************
	public void loadIndustrySetSectorSelect() {
		loadDataSelect resp = new loadDataSelect();
		resp.execute();
	}

	public class loadDataSelect extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		private JSONObject jsonIndustrySetSectorSelect;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialogLoading.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();

			// SplashScreen.url_bidschart+"/service/v2/symbolByIndustrySector?industry=TECH&sector=TECH&page=1&limit=10

			String url_industrySetSector = SplashScreen.url_bidschart
					+ "/service/v2/symbolByIndustrySector?industry="
					+ FragmentChangeActivity.strWatchlistCategorySelect
					+ "&sector=" + FragmentChangeActivity.strIndustrySelect
					+ "&limit=40";

			if (FragmentChangeActivity.strWatchlistCategorySelect == "all") {
				url_industrySetSector = SplashScreen.url_bidschart
						+ "/service/v2/symbolByIndustry?industry="
						+ FragmentChangeActivity.strIndustrySelect
						+ "&limit=40";
			}

			Log.v("url_industrySetSector", "" + url_industrySetSector);

			try {
				jsonIndustrySetSectorSelect = ReadJson
						.readJsonObjectFromUrl(url_industrySetSector);
			} catch (IOException e1) {
				connectionError = true;
				jsonIndustrySetSectorSelect = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonIndustrySetSectorSelect = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonIndustrySetSectorSelect = null;
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (jsonIndustrySetSectorSelect != null) {
					try {
						FragmentChangeActivity.contentGetWatchlistSymbol = jsonIndustrySetSectorSelect
								.getJSONArray("dataAll");

						setWatchlistSymbol(); // set data

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					dialogLoading.dismiss();
					Log.v("jsonGetData", "jsonGetData null");
				}

			} else {
				dialogLoading.dismiss();
				Log.v("connectionError", "connectionError ture");
			}
		}
	}

	// ***************** trendSignal ******************
	public void loadTrendSignalSelect() {
		loadDataTrend resp = new loadDataTrend();
		resp.execute();
	}

	public class loadDataTrend extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		private JSONObject jsonTrendSelect;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialogLoading.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();

			// http://www.bidschart.com/service/v2/getTrendSignal?trend=all&period=avg&limit=40&offset=0&industry=All&sector=All&user_id=53&timestamp=1459757737
			// http://www.bidschart.com/service/v2/getTrendSignal?trend=down&period=avg&limit=40&offset=0&industry=indus&sector=pkg&user_id=53

			// SplashScreen.url_bidschart+"/service/v2/symbolByIndustrySector?industry=TECH&sector=TECH&page=1&limit=10
			// ** top = swing , volume , value , gainer , loser

			String url_trend = SplashScreen.url_bidschart
					+ "/service/v2/getTrendSignal?trend="
					+ FragmentChangeActivity.strWatchlistCategorySelect
					+ "&period=" + strSpnPeriod
					+ "&limit=40&offset=0&industry=" + strSpnIndustry
					+ "&sector=" + strSpnSector + "&user_id="
					+ SplashScreen.userModel.user_id + "&timestamp="
					+ timestamp;

			Log.v("getTrendSignal?trend", strSpnIndustry + "_" + strSpnSector
					+ "_" + strSpnPeriod);
			Log.v("url_trendsignal", "" + url_trend);

			try {
				jsonTrendSelect = ReadJson.readJsonObjectFromUrl(url_trend);
			} catch (IOException e1) {
				connectionError = true;
				jsonTrendSelect = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonTrendSelect = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonTrendSelect = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (jsonTrendSelect != null) {
					try {

						FragmentChangeActivity.contentGetWatchlistSymbol = jsonTrendSelect
								.getJSONArray("dataAll");

						setWatchlistSymbol(); // set data

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					dialogLoading.dismiss();
					Log.v("jsonGetData", "jsonGetData null");
				}

			} else {
				dialogLoading.dismiss();
				Log.v("connectionError", "connectionError ture");
			}
			// dialogLoading.dismiss();
		}
	}

	// ***************** topmost ******************
	public void loadTopmostSelect() {
		loadDataTop resp = new loadDataTop();
		resp.execute();
	}

	public class loadDataTop extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		private JSONObject jsonTopmostSelect;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialogLoading.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();

			// SplashScreen.url_bidschart+"/service/v2/symbolByIndustrySector?industry=TECH&sector=TECH&page=1&limit=10
			// ** top = swing , volume , value , gainer , loser
			String url_topmost = SplashScreen.url_bidschart
					+ "/service/v2/topMost?top="
					+ FragmentChangeActivity.strWatchlistCategorySelect
					+ "&timestamp=" + timestamp;

			try {
				jsonTopmostSelect = ReadJson.readJsonObjectFromUrl(url_topmost);
			} catch (IOException e1) {
				connectionError = true;
				jsonTopmostSelect = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonTopmostSelect = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonTopmostSelect = null;
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (jsonTopmostSelect != null) {
					try {

						FragmentChangeActivity.contentGetWatchlistSymbol = jsonTopmostSelect
								.getJSONArray("dataAll");

						// -------- orderbook for connectsocket
						// FragmentChangeActivity.strGetSymbolOrderBook_Id = "";
						// for (int i = 0; i <
						// FragmentChangeActivity.contentGetWatchlistSymbol
						// .length(); i++) {
						// FragmentChangeActivity.strGetListSymbol +=
						// FragmentChangeActivity.contentGetWatchlistSymbol
						// .getJSONObject(i).getString("orderbook_id");
						// if (i !=
						// (FragmentChangeActivity.contentGetWatchlistSymbol
						// .length() - 1)) {
						// FragmentChangeActivity.strGetSymbolOrderBook_Id +=
						// ",";
						// }
						// }

						setWatchlistSymbol(); // set data

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					dialogLoading.dismiss();
					Log.v("jsonGetData", "jsonGetData null");
				}

			} else {
				dialogLoading.dismiss();
				Log.v("connectionError", "connectionError ture");
			}
			// dialogLoading.dismiss();
		}
	}

	// ***************** fundamental ******************
	public void loadFundamentalSelect() {
		loadDataFun resp = new loadDataFun();
		resp.execute();
	}

	public class loadDataFun extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		private JSONObject jsonTopmostSelect;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialogLoading.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();

			// SplashScreen.url_bidschart+"/service/v2/symbolByIndustrySector?industry=TECH&sector=TECH&page=1&limit=10
			// ** top = swing , volume , value , gainer , loser

			// /service/v2/getFundamentalSignal?fundamental_type=growth&limit=10
			// fundamental =all,turnaround,growth,dividend,good

			// String url_topmost = SplashScreen.url_bidschart
			// + "/service/v2/getFundamentalSignal?fundamental_type="
			// + FragmentChangeActivity.strWatchlistCategorySelect
			// + "&timestamp=" + timestamp;

			// http://www.bidschart.com/service/v2/getTrendSignal?trend=all&period=avg&limit=40&offset=0&industry=All&sector=All&user_id=53&timestamp=1459757737
			// http://bidschart.com/service/v2/getTrendSignal?trend=down&period=avg&limit=40&offset=0&industry=indus&sector=pkg&user_id=53

			// SplashScreen.url_bidschart+"/service/v2/symbolByIndustrySector?industry=TECH&sector=TECH&page=1&limit=10
			// ** top = swing , volume , value , gainer , loser
			String url_fund = SplashScreen.url_bidschart
					+ "/service/v2/getFundamentalSignal?fundamental_type="
					+ FragmentChangeActivity.strWatchlistCategorySelect
					+ "&limit=40&offset=0&industry=" + strSpnIndustry
					+ "&sector=" + strSpnSector + "&user_id="
					+ SplashScreen.userModel.user_id + "&timestamp="
					+ timestamp;

			Log.v("url_fundamental", "" + url_fund);

			try {
				jsonTopmostSelect = ReadJson.readJsonObjectFromUrl(url_fund);
			} catch (IOException e1) {
				connectionError = true;
				jsonTopmostSelect = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonTopmostSelect = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonTopmostSelect = null;
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (jsonTopmostSelect != null) {
					try {

						FragmentChangeActivity.contentGetWatchlistSymbol = jsonTopmostSelect
								.getJSONArray("dataAll");

						// // -------- orderbook for connectsocket
						// FragmentChangeActivity.strGetSymbolOrderBook_Id = "";
						// for (int i = 0; i <
						// FragmentChangeActivity.contentGetWatchlistSymbol
						// .length(); i++) {
						// FragmentChangeActivity.strGetListSymbol +=
						// FragmentChangeActivity.contentGetWatchlistSymbol
						// .getJSONObject(i).getString("orderbook_id");
						// if (i !=
						// (FragmentChangeActivity.contentGetWatchlistSymbol
						// .length() - 1)) {
						// FragmentChangeActivity.strGetSymbolOrderBook_Id +=
						// ",";
						// }
						// }

						setWatchlistSymbol(); // set data

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					dialogLoading.dismiss();
					Log.v("jsonGetData", "jsonGetData null");
				}

			} else {
				dialogLoading.dismiss();
				Log.v("connectionError", "connectionError ture");
			}
			// dialogLoading.dismiss();
		}
	}

	// ***************** systemtrade portfolio ******************
	public void loadSystemTradePortfolio() {
		loadDataPort resp = new loadDataPort();
		resp.execute();
	}

	public class loadDataPort extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		private JSONObject jsonPortfolio;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();

			// http://bidschart.com/service/v2/watchlistSystemTrade?user_id=53
			String url_port = SplashScreen.url_bidschart
					+ "/service/v2/watchlistSystemTrade?user_id="
					+ SplashScreen.userModel.user_id;

			try {
				jsonPortfolio = ReadJson.readJsonObjectFromUrl(url_port);
			} catch (IOException e1) {
				connectionError = true;
				jsonPortfolio = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonPortfolio = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonPortfolio = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (jsonPortfolio != null) {
					try {

						FragmentChangeActivity.contentGetWatchlistSymbol = jsonPortfolio
								.getJSONArray("dataAll");

						// Log.v("data portfolio",
						// ""+FragmentChangeActivity.contentGetWatchlistSymbol);

						setWatchlistSymbol(); // set data

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					dialogLoading.dismiss();
					Log.v("jsonGetData", "jsonGetData null");
				}

			} else {
				dialogLoading.dismiss();
				Log.v("connectionError", "connectionError ture");
			}
			// dialogLoading.dismiss();
		}
	}

	// ============== get favorite all =============
	public void getDataFavoriteAll() {
		getFavoriteAll resp = new getFavoriteAll();
		resp.execute();
	}

	public class getFavoriteAll extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;
		// ======= json ========
		private JSONObject jsonFav_1;
		private JSONObject jsonFav_2;
		private JSONObject jsonFav_3;
		private JSONObject jsonFav_4;
		private JSONObject jsonFav_5;

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

			// http://www.bidschart.com/service/getFavoriteByUserIdFavoriteNumber?user_id=1587&favorite_number=1

			String url_fav_1 = SplashScreen.url_bidschart
					+ "/service/getFavoriteByUserIdFavoriteNumber?user_id="
					+ SplashScreen.userModel.user_id + "&favorite_number=1"+ "&limit=20&ofset=1&timestamp="+ timestamp;
			String url_fav_2 = SplashScreen.url_bidschart
					+ "/service/getFavoriteByUserIdFavoriteNumber?user_id="
					+ SplashScreen.userModel.user_id + "&favorite_number=2"+ "&limit=20&ofset=1&timestamp="+ timestamp;
			String url_fav_3 = SplashScreen.url_bidschart
					+ "/service/getFavoriteByUserIdFavoriteNumber?user_id="
					+ SplashScreen.userModel.user_id + "&favorite_number=3"+ "&limit=20&ofset=1&timestamp="+ timestamp;
			String url_fav_4 = SplashScreen.url_bidschart
					+ "/service/getFavoriteByUserIdFavoriteNumber?user_id="
					+ SplashScreen.userModel.user_id + "&favorite_number=4"+ "&limit=20&ofset=1&timestamp="+ timestamp;
			String url_fav_5 = SplashScreen.url_bidschart
					+ "/service/getFavoriteByUserIdFavoriteNumber?user_id="
					+ SplashScreen.userModel.user_id + "&favorite_number=5"+ "&limit=20&ofset=1&timestamp="+ timestamp;

//			Log.v("getFavoriteByUserIdFavoriteNumber 1", "" + url_fav_1);
//			Log.v("getFavoriteByUserIdFavoriteNumber 2", "" + url_fav_2);
//			Log.v("getFavoriteByUserIdFavoriteNumber 3", "" + url_fav_3);
//			Log.v("getFavoriteByUserIdFavoriteNumber 4", "" + url_fav_4);
//			Log.v("getFavoriteByUserIdFavoriteNumber 5", "" + url_fav_5);

			try {
				jsonFav_1 = ReadJson.readJsonObjectFromUrl(url_fav_1);
				jsonFav_2 = ReadJson.readJsonObjectFromUrl(url_fav_2);
				jsonFav_3 = ReadJson.readJsonObjectFromUrl(url_fav_3);
				jsonFav_4 = ReadJson.readJsonObjectFromUrl(url_fav_4);
				jsonFav_5 = ReadJson.readJsonObjectFromUrl(url_fav_5);
			} catch (IOException e1) {
				connectionError = true;
				jsonFav_1 = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonFav_1 = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonFav_1 = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				try {
					FragmentChangeActivity.contentGetSymbolFavorite_1 = jsonFav_1
							.getJSONArray("dataAll");
					FragmentChangeActivity.contentGetSymbolFavorite_2 = jsonFav_2
							.getJSONArray("dataAll");
					FragmentChangeActivity.contentGetSymbolFavorite_3 = jsonFav_3
							.getJSONArray("dataAll");
					FragmentChangeActivity.contentGetSymbolFavorite_4 = jsonFav_4
							.getJSONArray("dataAll");
					FragmentChangeActivity.contentGetSymbolFavorite_5 = jsonFav_5
							.getJSONArray("dataAll");
					
					Log.v("getFavoriteByUserIdFavoriteNumber 1 lenght", "__" + FragmentChangeActivity.contentGetSymbolFavorite_1.length());
					Log.v("getFavoriteByUserIdFavoriteNumber 2 lenght", "__" + FragmentChangeActivity.contentGetSymbolFavorite_2.length());
					Log.v("getFavoriteByUserIdFavoriteNumber 3 lenght", "__" + FragmentChangeActivity.contentGetSymbolFavorite_3.length());
					Log.v("getFavoriteByUserIdFavoriteNumber 4 lenght", "__" + FragmentChangeActivity.contentGetSymbolFavorite_4.length());
					Log.v("getFavoriteByUserIdFavoriteNumber 5 lenght", "__" + FragmentChangeActivity.contentGetSymbolFavorite_5.length());

					// ---- fav 1 -------
					if (FragmentChangeActivity.contentGetSymbolFavorite_1 != null) {
						FragmentChangeActivity.strGetListSymbol_fav1 = "";
						for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite_1
								.length(); i++) {
							JSONObject jsoIndex = FragmentChangeActivity.contentGetSymbolFavorite_1
									.getJSONObject(i);

							FragmentChangeActivity.strGetListSymbol_fav1 += jsoIndex
									.getString("symbol_name");
							if (i != (FragmentChangeActivity.contentGetSymbolFavorite_1
									.length() - 1)) {
								FragmentChangeActivity.strGetListSymbol_fav1 += ",";
							}
						}
					} else {
						dialogLoading.dismiss();
					}
					// ---- fav 2 -------
					if (FragmentChangeActivity.contentGetSymbolFavorite_2 != null) {
						FragmentChangeActivity.strGetListSymbol_fav2 = "";
						for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite_2
								.length(); i++) {
							JSONObject jsoIndex = FragmentChangeActivity.contentGetSymbolFavorite_2
									.getJSONObject(i);

							FragmentChangeActivity.strGetListSymbol_fav2 += jsoIndex
									.getString("symbol_name");
							if (i != (FragmentChangeActivity.contentGetSymbolFavorite_2
									.length() - 1)) {
								FragmentChangeActivity.strGetListSymbol_fav2 += ",";
							}
						}
					} else {
						dialogLoading.dismiss();
					}
					// ---- fav 3 -------
					if (FragmentChangeActivity.contentGetSymbolFavorite_3 != null) {
						FragmentChangeActivity.strGetListSymbol_fav3 = "";
						for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite_3
								.length(); i++) {
							JSONObject jsoIndex = FragmentChangeActivity.contentGetSymbolFavorite_3
									.getJSONObject(i);

							FragmentChangeActivity.strGetListSymbol_fav3 += jsoIndex
									.getString("symbol_name");
							if (i != (FragmentChangeActivity.contentGetSymbolFavorite_3
									.length() - 1)) {
								FragmentChangeActivity.strGetListSymbol_fav3 += ",";
							}
						}
					} else {
						dialogLoading.dismiss();
					}
					// ---- fav 4 -------
					if (FragmentChangeActivity.contentGetSymbolFavorite_4 != null) {
						FragmentChangeActivity.strGetListSymbol_fav4 = "";
						for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite_4
								.length(); i++) {
							JSONObject jsoIndex = FragmentChangeActivity.contentGetSymbolFavorite_4
									.getJSONObject(i);

							FragmentChangeActivity.strGetListSymbol_fav4 += jsoIndex
									.getString("symbol_name");
							if (i != (FragmentChangeActivity.contentGetSymbolFavorite_4
									.length() - 1)) {
								FragmentChangeActivity.strGetListSymbol_fav4 += ",";
							}
						}
					} else {
						dialogLoading.dismiss();
					}
					// ---- fav 5 -------
					if (FragmentChangeActivity.contentGetSymbolFavorite_5 != null) {
						FragmentChangeActivity.strGetListSymbol_fav5 = "";
						for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite_5
								.length(); i++) {
							JSONObject jsoIndex = FragmentChangeActivity.contentGetSymbolFavorite_5
									.getJSONObject(i);

							FragmentChangeActivity.strGetListSymbol_fav5 += jsoIndex
									.getString("symbol_name");
							if (i != (FragmentChangeActivity.contentGetSymbolFavorite_5
									.length() - 1)) {
								FragmentChangeActivity.strGetListSymbol_fav5 += ",";
							}
						}
					} else {
						dialogLoading.dismiss();
					}
					
//					Log.v("strGetListSymbol fav 1", "__" + FragmentChangeActivity.strGetListSymbol_fav1);
//					Log.v("strGetListSymbol fav 2", "__" + FragmentChangeActivity.strGetListSymbol_fav2);
//					Log.v("strGetListSymbol fav 3", "__" + FragmentChangeActivity.strGetListSymbol_fav3);
//					Log.v("strGetListSymbol fav 4", "__" + FragmentChangeActivity.strGetListSymbol_fav4);
//					Log.v("strGetListSymbol fav 5", "__" + FragmentChangeActivity.strGetListSymbol_fav5);
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				dialogLoading.dismiss();
			}
		}
	}

	// ============== get favorite =============
	public void getDataFavorite() {
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
			dialogLoading.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// ======= url ========

			// http://www.bidschart.com/service/getFavoriteByUserIdFavoriteNumber?user_id=1587&favorite_number=1

			String url_fav = SplashScreen.url_bidschart
					+ "/service/getFavoriteByUserIdFavoriteNumber?user_id="
					+ SplashScreen.userModel.user_id + "&favorite_number="
					+ FragmentChangeActivity.strFavoriteNumber+ "&limit=20&ofset=1&timestamp="+ timestamp;

			Log.v("getFavoriteByUserIdFavoriteNumber", "__" + url_fav);

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
						if (FragmentChangeActivity.contentGetSymbolFavorite != null) {
							FragmentChangeActivity.strGetListSymbol = "";
							for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite
									.length(); i++) {
								JSONObject jsoIndex = FragmentChangeActivity.contentGetSymbolFavorite
										.getJSONObject(i);

								FragmentChangeActivity.strGetListSymbol += jsoIndex
										.getString("symbol_name");
								if (i != (FragmentChangeActivity.contentGetSymbolFavorite
										.length() - 1)) {
									FragmentChangeActivity.strGetListSymbol += ",";
								}
							}
							if (FragmentChangeActivity.strFavoriteNumber == "1") {
								FragmentChangeActivity.strGetListSymbol_fav1 = FragmentChangeActivity.strGetListSymbol;
								FragmentChangeActivity.contentGetSymbolFavorite_1 = FragmentChangeActivity.contentGetSymbolFavorite;
							} else if (FragmentChangeActivity.strFavoriteNumber == "2") {
								FragmentChangeActivity.strGetListSymbol_fav2 = FragmentChangeActivity.strGetListSymbol;
								FragmentChangeActivity.contentGetSymbolFavorite_2 = FragmentChangeActivity.contentGetSymbolFavorite;
							} else if (FragmentChangeActivity.strFavoriteNumber == "3") {
								FragmentChangeActivity.strGetListSymbol_fav3 = FragmentChangeActivity.strGetListSymbol;
								FragmentChangeActivity.contentGetSymbolFavorite_3 = FragmentChangeActivity.contentGetSymbolFavorite;
							} else if (FragmentChangeActivity.strFavoriteNumber == "4") {
								FragmentChangeActivity.strGetListSymbol_fav4 = FragmentChangeActivity.strGetListSymbol;
								FragmentChangeActivity.contentGetSymbolFavorite_4 = FragmentChangeActivity.contentGetSymbolFavorite;
							} else if (FragmentChangeActivity.strFavoriteNumber == "5") {
								FragmentChangeActivity.strGetListSymbol_fav5 = FragmentChangeActivity.strGetListSymbol;
								FragmentChangeActivity.contentGetSymbolFavorite_5 = FragmentChangeActivity.contentGetSymbolFavorite;
							}
							getWatchlistSymbol(); // get watchlist symbol
						} else {
							dialogLoading.dismiss();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					dialogLoading.dismiss();
				}
			} else {
				dialogLoading.dismiss();
			}
		}
	}

	// ============== get symbol =========================
	public void initGetDataSymbolBegin() {
		getSymbolBegin resp = new getSymbolBegin();
		resp.execute();
	}

	public class getSymbolBegin extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		private JSONObject jsonGetSymbol;
		private JSONObject jsonIndustrySetSector;
		private JSONObject jsonGetNameFund; // กองทุน

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();

			String url_GetSymbol = SplashScreen.url_bidschart
					+ "/service/getSymbol";
			String url_industrySetSector = SplashScreen.url_bidschart
					+ "/service/v2/industrySetSector";
			String url_GetNameFund = SplashScreen.url_bidschart
					+ "/service/v2/getNameFund"; // กองทุน

			// http://www.bidschart.com/service/getSymbol
			// http://www.bidschart.com/service/v2/industrySetSector
			// http://www.bidschart.com/service/v2/getNameFund

			try {
				// ======= Ui Home ========
				jsonGetSymbol = ReadJson.readJsonObjectFromUrl(url_GetSymbol);
				jsonIndustrySetSector = ReadJson
						.readJsonObjectFromUrl(url_industrySetSector);
				jsonGetNameFund = ReadJson
						.readJsonObjectFromUrl(url_GetNameFund);

			} catch (IOException e1) {
				connectionError = true;
				jsonGetSymbol = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonGetSymbol = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonGetSymbol = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {

				try {
					// --------------- get IndustrySetSector ----------
					if (jsonIndustrySetSector != null) {
						FragmentChangeActivity.contentGetIndustrySetSector = jsonIndustrySetSector
								.getJSONArray("dataAll");
					}
					// --------------- get symbol ----------
					if (jsonGetSymbol != null) {
						FragmentChangeActivity.contentGetSymbol = jsonGetSymbol
								.getJSONArray("dataAll");
						// arr symbol init
						if (FragmentChangeActivity.contentGetSymbol != null) {
							try {
								WebView wv;
								for (int i = 0; i < FragmentChangeActivity.contentGetSymbol
										.length(); i++) {
									JSONObject jso = FragmentChangeActivity.contentGetSymbol
											.getJSONObject(i);
									// arr_getsymbol.add(jso
									// .getString("symbol"));

									CatalogGetSymbol cg = new CatalogGetSymbol();
									cg.symbol = jso.getString("symbol");
									cg.market_id = jso.getString("market_id");
									cg.symbol_fullname_eng = jso
											.getString("symbol_fullname_eng");
									cg.symbol_fullname_thai = jso
											.getString("symbol_fullname_thai");
									cg.last_trade = jso.getString("last_trade");
									cg.volume = jso.getString("volume");
									cg.change = jso.getString("change");
									cg.percentChange = jso
											.getString("percentChange");
									cg.status_segmentId = jso
											.getString("status_segmentId");

									cg.orderbook_id = jso
											.getString("orderbook_id");
									cg.color_macd = jso.getString("color_macd");
									cg.color_bb = jso.getString("color_bb");
									cg.color_sto = jso.getString("color_sto");
									cg.color_rsi = jso.getString("color_rsi");
									cg.color_ema = jso.getString("color_ema");

									cg.trade_signal = jso
											.getString("trade_signal");
									cg.trade_date = jso.getString("trade_date");

									FragmentChangeActivity.list_getSymbol
											.add(cg);
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

							initSearchLayout(); // layout search
//							initGetData(); // get data

						} else {
							Log.v("json null", "symbol null");
						}
					} else {
						Log.v("symbol null", "symbol null");
					}
					// --------------- get NameFund ----------
					// Log.v("jsonGetNameFund", "" + jsonGetNameFund);
					if (jsonGetNameFund != null) {
						FragmentChangeActivity.contentGetNameFund = jsonGetNameFund
								.getJSONArray("data");
						// arr symbol init
						if (FragmentChangeActivity.contentGetNameFund != null) {
							try {
								for (int i = 0; i < FragmentChangeActivity.contentGetNameFund
										.length(); i++) {
									JSONObject jso = FragmentChangeActivity.contentGetNameFund
											.getJSONObject(i);

									CatalogGetNameFund cg = new CatalogGetNameFund();
									cg.name_initial = jso
											.getString("name_initial");
									cg.name_t = jso.getString("name_t");
									cg.name_e = jso.getString("name_e");
									cg.asset_initial = jso
											.getString("asset_initial");
									cg.set_initial = jso
											.getString("set_initial");
									cg.type_initial = jso
											.getString("type_initial");
									cg.invest_value = jso
											.getString("invest_value");
									cg.invest_change = jso
											.getString("invest_change");
									FragmentChangeActivity.list_getNameFund
											.add(cg);
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}

//							initSearchLayout(); // layout search
//							initGetData(); // get data

						} else {
							Log.v("json null", "symbol null");
						}
					} else {
						Log.v("symbol null", "symbol null");
					}
					
					initGetData(); // get data
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
				Toast.makeText(context, "การเชื่อมต่อล้มเหลว", 0).show();
				Log.v("connectionError", "Error");
			}
		}
	}

	// the meat of switching the above fragment
	public static void switchFragment(Fragment fragment) {
		if (context == null)
			return;
		if (context instanceof FragmentChangeActivity) {
			FragmentChangeActivity fca = (FragmentChangeActivity) context;
			fca.switchContent(fragment);
		}
	}

	// the meat of switching the above fragment
	public static void switchFragmentStatic(Fragment fragment) {
		if (context == null)
			return;
		if (context instanceof FragmentChangeActivity) {
			FragmentChangeActivity fca = (FragmentChangeActivity) context;
			fca.switchContent(fragment);
		}
	}

}
