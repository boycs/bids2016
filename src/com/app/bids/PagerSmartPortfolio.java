package com.app.bids;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;
import com.app.bids.PagerWatchListDetailNews.loadAll;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class PagerSmartPortfolio extends Fragment {

	static Context context;
	public static View rootView;

	// activity listener interface
	private OnPageListener pageListener;

	// --- add portfolio
	public static DialogAddPortfolio dialogAddPortfolio;

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
		rootView = inflater.inflate(R.layout.pager_smartportfolio, container,
				false);

		return rootView;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		dialogAddPortfolio = new DialogAddPortfolio(context);

		// if (FragmentChangeActivity.ckLoadSmartPortfolioList) {
		// initPager();
		// } else {
		initLoadData();
		// }

		initView();
	}

	public static void initView() {
		initSearchLayout(); // layout search
	}

	// ***************** search symbol******************
	public static LinearLayout li_search_tabbegin, li_search_select;

	public static LinearLayout li_search;

	public static ViewPager vp_pager;
	public static EditText et_search;
	public static TextView tv_close_search, tv_thai_stock, tv_thai_mutualfund;
	public static ListView listview_search;
	public static ImageView img_portfolio_add;

	// String symbol_search_symbol = "";

	public static String status_tabsearch = "STOCK"; // STOCK, FUND

	public static void initSearchLayout() {

		// text sliding
		initTxtSliding();

		// --- linear show tabsearch begin
		li_search_tabbegin = (LinearLayout) rootView
				.findViewById(R.id.li_search_tabbegin);
		li_search_select = (LinearLayout) rootView
				.findViewById(R.id.li_search_select);
		img_portfolio_add = (ImageView) rootView
				.findViewById(R.id.img_portfolio_add);
		// --- linear tabsearch
		li_search = (LinearLayout) rootView.findViewById(R.id.li_search);
		// --- linear tabsearch begin
		vp_pager = (ViewPager) rootView.findViewById(R.id.vp_pager);
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
		img_portfolio_add.setOnClickListener(new OnClickListener() {
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
						txtLtrade = FunctionSymbol.setFormatNumber(txtLtrade);
					}

					if (txtChange != "") {
						// double db = Double.parseDouble(txtChange);
						// txtChange = String.format(" %,.2f", db);
						txtChange = FunctionSymbol.setFormatNumber(txtChange);
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

	// E/AndroidRuntime(26561): Caused by: java.lang.ClassNotFoundException:
	// Didn't find class "com.app.bids.GoogleAnalyticsApp" on path:
	// DexPathList[[zip file
	// "/data/app/com.app.bids-1/base.apk"],nativeLibraryDirectories=[/vendor/lib64,
	// /system/lib64]]

	// =========== search symbol watchlist ==========
	public static void initSearchSymbol() {
		// sv_search_symbol = (ScrollView)
		// rootView.findViewById(R.id.sv_search_symbol);
		li_search_tabbegin.setVisibility(View.GONE);
		li_search.setVisibility(View.VISIBLE);

		// ----- search stock
		final ListAdapterSearchSymbolStock ListAdapterSearch;
		final ArrayList<CatalogGetSymbol> original_list;
		final ArrayList<CatalogGetSymbol> second_list;

		original_list = new ArrayList<CatalogGetSymbol>();
		original_list.addAll(FragmentChangeActivity.list_getSymbol);
		second_list = new ArrayList<CatalogGetSymbol>();
		second_list.addAll(FragmentChangeActivity.list_getSymbol);

		// ----- search fund
		final ListAdapterSearchNameFundPortList ListAdapterSearchNameFundPortList;
		final ArrayList<CatalogGetNameFund> original_list_fund;
		final ArrayList<CatalogGetNameFund> second_list_fund;

		original_list_fund = new ArrayList<CatalogGetNameFund>();
		original_list_fund.addAll(FragmentChangeActivity.list_getNameFund);
		second_list_fund = new ArrayList<CatalogGetNameFund>();
		second_list_fund.addAll(FragmentChangeActivity.list_getNameFund);

		tv_thai_stock = (TextView) rootView.findViewById(R.id.tv_thai_stock);
		tv_thai_mutualfund = (TextView) rootView
				.findViewById(R.id.tv_thai_mutualfund);
		status_tabsearch = "STOCK"; // STOCK, FUND
		setTitleSearch();

		listview_search = (ListView) rootView
				.findViewById(R.id.list_search_symbol);
		listview_search.setVisibility(View.GONE);

		// ----- set list view ------
		ListAdapterSearch = new ListAdapterSearchSymbolStock(context, 0,
				second_list);
		listview_search.setAdapter(ListAdapterSearch);
		ListAdapterSearchNameFundPortList = new ListAdapterSearchNameFundPortList(
				context, 0, second_list_fund);

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
					vp_pager.setVisibility(View.GONE);
					listview_search.setVisibility(View.VISIBLE);

					if (status_tabsearch == "STOCK") {
						second_list.clear();
						if (FragmentChangeActivity.list_getSymbol != null) {
							for (int i = 0; i < original_list.size(); i++) {
								if (original_list.get(i).symbol
										.toLowerCase()
										.contains(text.toString().toLowerCase())) {
									if ((original_list.get(i).status_segmentId == "COMMON")
											|| (original_list.get(i).status_segmentId == "WARRANT")) {
										second_list.add(original_list.get(i));
									}
								}
							}
							ListAdapterSearch.notifyDataSetChanged();
						}
					} else {
						second_list_fund.clear();
						if (FragmentChangeActivity.list_getNameFund != null) {
							for (int i = 0; i < original_list_fund.size(); i++) {
								if (original_list_fund.get(i).name_initial
										.toLowerCase().contains(
												text.toString().toLowerCase())) {
									if ((original_list.get(i).status_segmentId == "COMMON")
											|| (original_list.get(i).status_segmentId == "WARRANT")) {
										second_list_fund.add(original_list_fund
												.get(i));
									}
								}
							}
							ListAdapterSearchNameFundPortList
									.notifyDataSetChanged();
						}
					}
				} else {
					vp_pager.setVisibility(View.VISIBLE);
					listview_search.setVisibility(View.GONE);
				}
			}
		});

		// -------------------------
		tv_thai_stock.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				status_tabsearch = "STOCK"; // STOCK, FUND
				// ----- set list view ------
				listview_search.setAdapter(ListAdapterSearch);

				String text = et_search.getText().toString();
				setTitleSearch();

				if (text.length() > 0) {
					vp_pager.setVisibility(View.GONE);
					listview_search.setVisibility(View.VISIBLE);

					if (FragmentChangeActivity.list_getSymbol != null) {
						second_list.clear();
						for (int i = 0; i < original_list.size(); i++) {
							if (original_list.get(i).symbol.toLowerCase()
									.contains(text.toString().toLowerCase())) {
								if ((original_list.get(i).status_segmentId == "COMMON")
										|| (original_list.get(i).status_segmentId == "WARRANT")) {
									second_list.add(original_list.get(i));
								}
							}
						}
						ListAdapterSearch.notifyDataSetChanged();
					}
				} else {
					vp_pager.setVisibility(View.VISIBLE);
					listview_search.setVisibility(View.GONE);
				}
			}
		});
		tv_thai_mutualfund.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				status_tabsearch = "FUND"; // STOCK, FUND
				// ----- set list view ------
				listview_search.setAdapter(ListAdapterSearchNameFundPortList);

				String text = et_search.getText().toString();
				setTitleSearch();

				if (text.length() > 0) {
					vp_pager.setVisibility(View.GONE);
					listview_search.setVisibility(View.VISIBLE);
					if (FragmentChangeActivity.list_getNameFund != null) {
						second_list_fund.clear();
						for (int i = 0; i < original_list_fund.size(); i++) {
							if (original_list_fund.get(i).name_initial
									.toLowerCase().contains(
											text.toString().toLowerCase())) {
								if ((original_list.get(i).status_segmentId == "COMMON")
										|| (original_list.get(i).status_segmentId == "WARRANT")) {
									second_list_fund.add(original_list_fund
											.get(i));
								}
							}
						}
						ListAdapterSearchNameFundPortList
								.notifyDataSetChanged();
					}
				} else {
					vp_pager.setVisibility(View.VISIBLE);
					listview_search.setVisibility(View.GONE);
				}
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
		vp_pager.setVisibility(View.VISIBLE);

		li_search_tabbegin.setVisibility(View.VISIBLE);
		li_search.setVisibility(View.GONE);
	}

	// ******* set title search ****
	public static void setTitleSearch() {
		tv_thai_stock.setTextColor(context.getResources().getColor(
				R.color.c_content));
		tv_thai_mutualfund.setTextColor(context.getResources().getColor(
				R.color.c_content));
		tv_thai_stock.setBackgroundColor(Color.TRANSPARENT);
		tv_thai_mutualfund.setBackgroundColor(Color.TRANSPARENT);

		if (status_tabsearch == "STOCK") {
			tv_thai_stock.setTextColor(context.getResources().getColor(
					R.color.bg_default));
			tv_thai_stock
					.setBackgroundResource(R.drawable.border_search_activeleft);
		} else {
			tv_thai_mutualfund.setTextColor(context.getResources().getColor(
					R.color.bg_default));
			tv_thai_mutualfund
					.setBackgroundResource(R.drawable.border_search_activeright);
		}
	}

	// ============== Load initLoadData =========================
	private void initLoadData() {
		loadAll resp = new loadAll();
		resp.execute();
	}

	public class loadAll extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;
		// ======= Ui Newds ========
		private JSONObject jsonGetData;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// http://bidschart.com/service/v2/getPortfolioListByUser?user_id=
			String url_GetData = SplashScreen.url_bidschart
					+ "/service/v2/getPortfolioListByUser?user_id="
					+ SplashScreen.userModel.user_id + "&timestamp="
					+ timestamp;

			Log.v("url_ getPortfolioListByUser", "" + url_GetData);

			try {
				// ======= Ui News ========
				jsonGetData = ReadJson.readJsonObjectFromUrl(url_GetData);
			} catch (IOException e1) {
				connectionError = true;
				jsonGetData = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonGetData = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonGetData = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			FragmentChangeActivity.ckLoadSmartPortfolioList = true;
			if (connectionError == false) {
				if (jsonGetData != null) {
					FragmentChangeActivity.contentGetSmartPortfolioList = jsonGetData;
					initPager();
				} else {
					Log.v("json null", "jsonGetFundamental null");
				}
			} else {
				initPager();
			}
		}
	}

	// ******* pager ****
	List<Fragment> fragmentMain = new Vector<Fragment>();
	static ViewPager mPagerMain;
	private PagerAdapter mPagerAdapterMain;

	private void initPager() {
		// creating fragments and adding to list
		fragmentMain.removeAll(fragmentMain);

		try {
			if ((FragmentChangeActivity.contentGetSmartPortfolioList
					.getString("status")).equals("ok")) {
				JSONArray jsaResult = FragmentChangeActivity.contentGetSmartPortfolioList
						.getJSONArray("result");
				if (jsaResult.length() > 0) {
					fragmentMain.add(Fragment.instantiate(context,
							PagerSmartPortfolioList.class.getName()));
				} else {
					fragmentMain.add(Fragment.instantiate(context,
							PagerSmartPortfolio_1.class.getName()));
					fragmentMain.add(Fragment.instantiate(context,
							PagerSmartPortfolio_2.class.getName()));
					fragmentMain.add(Fragment.instantiate(context,
							PagerSmartPortfolio_3.class.getName()));
				}
			} else {
				fragmentMain.add(Fragment.instantiate(context,
						PagerSmartPortfolio_1.class.getName()));
				fragmentMain.add(Fragment.instantiate(context,
						PagerSmartPortfolio_2.class.getName()));
				fragmentMain.add(Fragment.instantiate(context,
						PagerSmartPortfolio_3.class.getName()));
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		// creating adapter and linking to view pager
		this.mPagerAdapterMain = new PagerAdapter(
				super.getChildFragmentManager(), fragmentMain);
		mPagerMain = (ViewPager) rootView.findViewById(R.id.vp_pager);

		mPagerMain.setAdapter(this.mPagerAdapterMain);

		mPagerMain.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				// LOTBANK = (mPagerMain.getCurrentItem()) + 1;
				// selectTabPager((mPagerMain.getCurrentItem()) + 1);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				mPagerMain.getParent().requestDisallowInterceptTouchEvent(true);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
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

	// the meat of switching the above fragment
	public static void switchFragmentSmartPort(Fragment fragment) {
		if (context == null)
			return;
		if (context instanceof FragmentChangeActivity) {
			FragmentChangeActivity fca = (FragmentChangeActivity) context;
			fca.switchContent(fragment);
		}
	}

}