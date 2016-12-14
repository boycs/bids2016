package com.app.bids;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;
import com.app.bids.PagerHomeAll.loadData;
import com.app.bids.PagerWatchList.getFavorite;
import com.app.bids.PagerWatchListDetailFollow.setFavorite;
import com.app.model.login.LoginDialog;

import android.R.anim;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

public class PagerWatchlistDetail extends Fragment {

	static Context context;
	public static View rootView;

	private WebSocketClient mWebSocketClient;

	// view pager
	static ViewPager mPagerMain;
	static ViewPager mPagerMainPreMium;
	// list contains fragments to instantiate in the viewpager
	List<Fragment> fragmentMain = new Vector<Fragment>();
	private PagerAdapter mPagerAdapterMain;

	List<Fragment> fragmentMainPreMium = new Vector<Fragment>();
	private PagerAdapter mPagerAdapterMainPreMium;

	// activity listener interface
	private OnPageListener pageListener;

	// show hide
	LinearLayout li_menu_top, li_data;

	static Dialog dialogLoading;

	// ================= data ==================
	public static JSONObject contentGetDetail = null;

	// public static JSONArray contentGetDetailFollow = null;

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
		rootView = (LinearLayout) inflater.inflate(
				R.layout.pager_watchlist_detail, container, false);

		return rootView;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// FragmentChangeActivity.pagerDetail = "watchlistDetail";

		((TextView) rootView.findViewById(R.id.tv_title_symbol))
				.setText(FragmentChangeActivity.strSymbolSelect + "'s Detail");

		((LinearLayout) rootView.findViewById(R.id.li_back))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (FragmentChangeActivity.pagerDetail == "watchlist") {
							switchFragment(new PagerWatchList());
						} else {
							switchFragment(new PagerSystemTrade());
						}
					}
				});

		dialogLoading = new Dialog(context);
		dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogLoading.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialogLoading.setContentView(R.layout.progress_bar);
		dialogLoading.setCancelable(false);
		dialogLoading.setCanceledOnTouchOutside(false);

		dialogLoading.show();

		FragmentChangeActivity.id_websocket = 6;

		li_menu_top = (LinearLayout) rootView.findViewById(R.id.li_menu_top);
		li_data = (LinearLayout) rootView.findViewById(R.id.li_data);
		li_menu_top.setVisibility(View.VISIBLE);
		li_data.setVisibility(View.VISIBLE);

		// if(contentGetDetail == null){
		loadDataDetail();
		// }else{
		// setDataDetail();
		// }

		// initPager();
		// initPagerPreMium();

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

			// Log.v("url_GetDetail",""+url_GetDetail);

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
						contentGetDetail = jsonGetDetail
								.getJSONArray("dataAll").getJSONObject(0);
						// contentGetDetailFollow = jsonGetDetailFollow
						// .getJSONArray("dataAll");

						setDataDetail();

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

	// ============= connect socket ===============
//	JSONObject jsoConnectSocket;
//	JSONArray jsaMassageSocket;
//
//	private void connectWebSocket() {
//		URI uri;
//		try {
//			uri = new URI(SplashScreen.url_websocket);
//		} catch (URISyntaxException e) {
//			e.printStackTrace();
//			return;
//		}
//
//		mWebSocketClient = new WebSocketClient(uri) {
//			@Override
//			public void onOpen(ServerHandshake serverHandshake) {
//
//				jsoConnectSocket = new JSONObject();
//				try {
//					jsoConnectSocket.put("id",
//							FragmentChangeActivity.id_websocket);
//					jsoConnectSocket.put("user_id", 0);
//					jsoConnectSocket.put("orderbook_list", contentGetDetail.getString("orderbook_id"));
//					jsoConnectSocket.put("date", "");
//					jsoConnectSocket.put("hh", "");
//					jsoConnectSocket.put("mm", "");
//					jsoConnectSocket.put("requestType", "watchList");
//					jsoConnectSocket.put("page", "favorite");
//				} catch (JSONException e) {
//					e.printStackTrace();
//				}
//				Log.v("jsonObj send connect detail", jsoConnectSocket.toString());
//				mWebSocketClient.send(jsoConnectSocket.toString());
//			}
//
//			@Override
//			public void onMessage(String s) {
//				final String message = s;
//				if (getActivity() != null) {
//					getActivity().runOnUiThread(new Runnable() {
//						@Override
//						public void run() {
//							try {
//								jsaMassageSocket = new JSONArray(message);
//								Log.v("jsaMsgSocket symboldetail", "" + jsaMassageSocket);
//
//								if (jsaMassageSocket.get(0).toString()
//										.equals("3")) {
//									changeRowWatchlist();
//								}
//							} catch (JSONException e) {
//								e.printStackTrace();
//							}
//						}
//					});
//				}
//			}
//
//			@Override
//			public void onClose(int i, String s, boolean b) {
//				Log.v("Websocket", "Closed " + s);
//			}
//
//			@Override
//			public void onError(Exception e) {
//				Log.v("Websocket", "Error " + e.getMessage());
//			}
//		};
//
//		mWebSocketClient.connect();
//
//		// if (mWebSocketClient.connect()) {
//		// ImageView img_connect_c = (ImageView) rootView
//		// .findViewById(R.id.img_connect_c);
//		// img_connect_c.setBackgroundResource(R.drawable.icon_connect_c_green);
//		// }
//
//	}

	// ============== Set watchlist symbol ===============
//	public void changeRowWatchlist() {
//		try {
//
//			String strLastTrade = "" + jsaMassageSocket.get(3);
//			String strChange = "" + jsaMassageSocket.get(4);
//			String strPercentChange = "" + jsaMassageSocket.get(5);
//			String strHigh = "" + jsaMassageSocket.get(6);
//			String strLow = "" + jsaMassageSocket.get(7);
//			String strVolume = "" + jsaMassageSocket.get(8);
//			String strValue = "" + jsaMassageSocket.get(9);
//
//			String strBuyVolume = "" + jsaMassageSocket.get(10);
//			String strBuyValue = "" + jsaMassageSocket.get(11);
//			String strSellVolume = "" + jsaMassageSocket.get(12);
//			String strSellValue = "" + jsaMassageSocket.get(13);
//
//			String strAvgBuy = "" + jsaMassageSocket.get(14);
//			String strAvgSell = "" + jsaMassageSocket.get(15);
//
//			String strMaxBuyPrice = "" + jsaMassageSocket.get(16);
//			String strMaxBuyPriceVolume = "" + jsaMassageSocket.get(17);
//			String strMaxSellPrice = "" + jsaMassageSocket.get(18);
//			String strMaxSellPriceVolume = "" + jsaMassageSocket.get(19);
//
//			String strOpenPrice = "" + jsaMassageSocket.get(20);
//			String strOpen1Price = "" + jsaMassageSocket.get(21);
//			String strOpen1PriceVolume = "" + jsaMassageSocket.get(22);
//			String strOpen2Price = "" + jsaMassageSocket.get(23);
//			String strOpen2PriceVolume = "" + jsaMassageSocket.get(24);
//
//			String strCloseVolume = "" + jsaMassageSocket.get(25);
//			String strSide = "" + jsaMassageSocket.get(26);
//			String strOrderQty = "" + jsaMassageSocket.get(27);
//			String strTimeOfEvent = "" + jsaMassageSocket.get(28);
//
//			// -------- set data socket
//			// main
//			TextView tv_last_trade = (TextView) rootView
//					.findViewById(R.id.tv_last_trade);
//			TextView tv_percenchange = (TextView) rootView
//					.findViewById(R.id.tv_percenchange);
//			ImageView img_updown = (ImageView) rootView
//					.findViewById(R.id.img_updown);
//			// column 1
//			TextView tv_volume = (TextView) rootView
//					.findViewById(R.id.tv_volume);
//			TextView tv_value = (TextView) rootView.findViewById(R.id.tv_value);
//			TextView tv_open = (TextView) rootView.findViewById(R.id.tv_open);
//			TextView tv_high = (TextView) rootView.findViewById(R.id.tv_high);
//			TextView tv_low = (TextView) rootView.findViewById(R.id.tv_low);
//
//			// show hide
//			TextView tv_average_buy = (TextView) rootView
//					.findViewById(R.id.tv_average_buy);
//			TextView tv_max_buy_price_volume = (TextView) rootView
//					.findViewById(R.id.tv_max_buy_price_volume);
//			TextView tv_open1_volume = (TextView) rootView
//					.findViewById(R.id.tv_open1_volume);
//			TextView tv_buy_volume = (TextView) rootView
//					.findViewById(R.id.tv_buy_volume);
//
//			TextView tv_average_sell = (TextView) rootView
//					.findViewById(R.id.tv_average_sell);
//			TextView tv_max_sell_price_volume = (TextView) rootView
//					.findViewById(R.id.tv_max_sell_price_volume);
//			TextView tv_close_volume = (TextView) rootView
//					.findViewById(R.id.tv_close_volume);
//			TextView tv_sell_volume = (TextView) rootView
//					.findViewById(R.id.tv_sell_volume);
//
//			// ======= set data ========
//			// main
//			tv_last_trade.setText(strLastTrade);
//
//			// main color
//			String strColor = strChange;
//
//			// tv_change.setText(strChange);
//			if ((strPercentChange == "0") || strPercentChange == "") {
//				tv_percenchange.setText("0.00");
//			} else {
//				tv_percenchange.setText("(" + strChange + ")"
//						+ strPercentChange + "%");
//			}
//
//			if (strLastTrade != "") {
//				tv_last_trade.setTextColor(getResources().getColor(
//						BgColorSymbolDetail.setColor(strLastTrade)));
//			}
//			if (strChange != "") {
//				tv_percenchange.setTextColor(getResources().getColor(
//						BgColorSymbolDetail.setColor(strChange)));
//				img_updown.setBackgroundResource(BgColorSymbolDetail
//						.setImgUpDown(strChange));
//			}
//
//			// column 1
//			tv_volume.setText(strVolume);
//			tv_value.setText(strValue);
//			tv_open.setText(strOpen1Price);
//			tv_high.setText(strHigh);
//			tv_low.setText(strLow);
//
//			// ck hight low
//			String strPrevClose = contentGetDetail.getString("prev_close")
//					.toString().replaceAll(",", "");
//			strHigh = strHigh.replaceAll(",", "");
//			strLow = strLow.replaceAll(",", "");
//
//			if (strPrevClose != "") {
//				if (strHigh != "") {
//					if ((Float.parseFloat(strHigh)) > Float
//							.parseFloat(strPrevClose)) {
//						tv_high.setTextColor(getResources().getColor(
//								BgColorSymbolDetail.arrColor[2]));
//					} else if ((Float.parseFloat(strHigh)) < Float
//							.parseFloat(strPrevClose)) {
//						tv_high.setTextColor(getResources().getColor(
//								BgColorSymbolDetail.arrColor[0]));
//					} else {
//						tv_high.setTextColor(getResources().getColor(
//								BgColorSymbolDetail.arrColor[1]));
//					}
//				}
//				if (strLow != "") {
//					if ((Float.parseFloat(strLow)) > Float
//							.parseFloat(strPrevClose)) {
//						tv_low.setTextColor(getResources().getColor(
//								BgColorSymbolDetail.arrColor[2]));
//					} else if ((Float.parseFloat(strLow)) < Float
//							.parseFloat(strPrevClose)) {
//						tv_low.setTextColor(getResources().getColor(
//								BgColorSymbolDetail.arrColor[0]));
//					} else {
//						tv_low.setTextColor(getResources().getColor(
//								BgColorSymbolDetail.arrColor[1]));
//					}
//				}
//			}
//
//			tv_average_buy.setText(strAvgBuy);
//			tv_max_buy_price_volume.setText(strMaxBuyPriceVolume);
//			tv_open1_volume.setText(strOpen1PriceVolume);
//			tv_buy_volume.setText(strBuyVolume);
//
//			tv_average_sell.setText(CheckStringIsNull.checkNull(strAvgSell));
//			tv_max_sell_price_volume.setText(strMaxSellPriceVolume);
//			tv_close_volume.setText(strCloseVolume);
//			tv_sell_volume.setText(strSellVolume);
//
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//	}

	// ====================== setDataDetail ================
	LinearLayout li_menu, li_menu_premium;
	TextView tv_chart, tv_news, tv_sector;
	TextView tv_premium_chart, tv_premium_industry, tv_premium_fundamental,
			tv_premium_news, tv_premium_hit;
	// ImageView img_follow;
	LinearLayout li_showbysell;

	boolean ckHideShow = false;

	private void initTabPagerPreMium() {
		tv_premium_chart = (TextView) rootView
				.findViewById(R.id.tv_premium_chart);
		tv_premium_industry = (TextView) rootView
				.findViewById(R.id.tv_premium_industry);
		tv_premium_fundamental = (TextView) rootView
				.findViewById(R.id.tv_premium_fundamental);
		tv_premium_news = (TextView) rootView
				.findViewById(R.id.tv_premium_news);
		tv_premium_hit = (TextView) rootView.findViewById(R.id.tv_premium_hit);

		tv_premium_chart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectTabPagerPreMium(1);
				mPagerMainPreMium.setCurrentItem(0);
			}
		});
		tv_premium_industry.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectTabPagerPreMium(2);
				mPagerMainPreMium.setCurrentItem(1);
			}
		});
		tv_premium_fundamental.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectTabPagerPreMium(3);
				mPagerMainPreMium.setCurrentItem(2);
			}
		});
		tv_premium_news.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectTabPagerPreMium(4);
				mPagerMainPreMium.setCurrentItem(3);
			}
		});
		tv_premium_hit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectTabPagerPreMium(5);
				mPagerMainPreMium.setCurrentItem(4);
			}
		});
	}

	private void initTabPager() {
		tv_chart = (TextView) rootView.findViewById(R.id.tv_chart);
		tv_sector = (TextView) rootView.findViewById(R.id.tv_sector);
		tv_news = (TextView) rootView.findViewById(R.id.tv_news);

		tv_chart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectTabPager(1);
				mPagerMain.setCurrentItem(0);
			}
		});
		tv_sector.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectTabPager(2);
				mPagerMain.setCurrentItem(1);
			}
		});
		tv_news.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectTabPager(3);
				mPagerMain.setCurrentItem(2);
			}
		});
	}

	private void setDataDetail() throws JSONException {
		// -------- tab pager
		li_menu = (LinearLayout) rootView.findViewById(R.id.li_menu);
		li_menu_premium = (LinearLayout) rootView
				.findViewById(R.id.li_menu_premium);
		li_menu.setVisibility(View.VISIBLE);
		li_menu_premium.setVisibility(View.GONE);

		if (SplashScreen.contentGetUserById != null) {
			if (!(SplashScreen.contentGetUserById.getString("package")
					.equals("free"))) {
				li_menu.setVisibility(View.GONE);
				li_menu_premium.setVisibility(View.VISIBLE);
				initPagerPreMium();
			} else {
				initPager();
			}
		} else {
			initPager();
		}
		// ----------------------

		ImageView img_follow = (ImageView) rootView
				.findViewById(R.id.img_follow);
		try {
			if (contentGetDetail != null) {
				// main
				TextView tv_symbol = (TextView) rootView
						.findViewById(R.id.tv_symbol);
				TextView tv_last_trade = (TextView) rootView
						.findViewById(R.id.tv_last_trade);
				TextView tv_symbol_name_eng = (TextView) rootView
						.findViewById(R.id.tv_symbol_name_eng);
				TextView tv_percenchange = (TextView) rootView
						.findViewById(R.id.tv_percenchange);
				ImageView img_updown = (ImageView) rootView
						.findViewById(R.id.img_updown);
				// column 1
				TextView tv_volume = (TextView) rootView
						.findViewById(R.id.tv_volume);
				TextView tv_value = (TextView) rootView
						.findViewById(R.id.tv_value);
				TextView tv_open = (TextView) rootView
						.findViewById(R.id.tv_open);
				TextView tv_high = (TextView) rootView
						.findViewById(R.id.tv_high);
				TextView tv_low = (TextView) rootView.findViewById(R.id.tv_low);
				// column 2
				TextView tv_prev_close = (TextView) rootView
						.findViewById(R.id.tv_prev_close);
				TextView tv_ceil = (TextView) rootView
						.findViewById(R.id.tv_ceil);
				TextView tv_floor = (TextView) rootView
						.findViewById(R.id.tv_floor);
				TextView tv_high52W = (TextView) rootView
						.findViewById(R.id.tv_high52W);
				TextView tv_low52W = (TextView) rootView
						.findViewById(R.id.tv_low52W);
				// column 3
				TextView tv_roe = (TextView) rootView.findViewById(R.id.tv_roe);
				TextView tv_roa = (TextView) rootView.findViewById(R.id.tv_roa);
				TextView tv_peg = (TextView) rootView.findViewById(R.id.tv_peg);
				TextView tv_p_e = (TextView) rootView.findViewById(R.id.tv_p_e);
				TextView tv_p_bv = (TextView) rootView
						.findViewById(R.id.tv_p_bv);

				// show hide
				LinearLayout li_showbysell = (LinearLayout) rootView
						.findViewById(R.id.li_showbuysell);
				final LinearLayout li_show_hide = (LinearLayout) rootView
						.findViewById(R.id.li_show_hide);
				final LinearLayout li_buysell = (LinearLayout) rootView
						.findViewById(R.id.li_buysell);
				final ImageView img_show_hide = (ImageView) rootView
						.findViewById(R.id.img_show_hide);
				TextView tv_average_buy = (TextView) rootView
						.findViewById(R.id.tv_average_buy);
				TextView tv_max_buy_price_volume = (TextView) rootView
						.findViewById(R.id.tv_max_buy_price_volume);
				TextView tv_open1_volume = (TextView) rootView
						.findViewById(R.id.tv_open1_volume);
				TextView tv_buy_volume = (TextView) rootView
						.findViewById(R.id.tv_buy_volume);

				TextView tv_average_sell = (TextView) rootView
						.findViewById(R.id.tv_average_sell);
				TextView tv_max_sell_price_volume = (TextView) rootView
						.findViewById(R.id.tv_max_sell_price_volume);
				TextView tv_close_volume = (TextView) rootView
						.findViewById(R.id.tv_close_volume);
				TextView tv_sell_volume = (TextView) rootView
						.findViewById(R.id.tv_sell_volume);

				TextView tv_percentBuy = (TextView) rootView
						.findViewById(R.id.tv_percentBuy);
				TextView tv_percentSell = (TextView) rootView
						.findViewById(R.id.tv_percentSell);
				View v_percentBuy = (View) rootView
						.findViewById(R.id.v_percentBuy);
				View v_percentSell = (View) rootView
						.findViewById(R.id.v_percentSell);

				// ======= set data ========
				// follow
				img_follow.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!(SplashScreen.userModel.user_id != "")) {
							Toast.makeText(context, "Login", 0).show();
							LoginDialog.show();
						} else {
							try {
								if ((contentGetDetail
										.getString("favorite_type").toString())
										.equals("")) {
									// switchFragment(new
									// PagerWatchListDetailFollow());
									dialogFavorite();
								} else {
									// follow แล้ว กดอีกครับต้อง unfollow
									// เลย ไม่ใช่เด้งไปหน้าเลือก favorite

									// PagerWatchListDetailFollow.sendFavorite();

									getDataFavoriteId(); // unfollow

									// loadDataDetail();

									// switchFragment(new
									// PagerWatchListDetailAddFavorite());
									// sendRemoveFavorite(); //unfollow

									// Follow
									// FollowSymbol.sendRemoveFavorite(FragmentChangeActivity.userModel.user_id,
									// FragmentChangeActivity.strSymbolSelect);
									// loadDataDetail();
								}
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					}
				});

				if (((contentGetDetail.getString("favorite_type").toString())
						.equals(""))
						|| (contentGetDetail.getString("favorite_type")
								.toString()).equals("null")) {
					img_follow
							.setBackgroundResource(R.drawable.icon_favorite_default);
				} else {
					// BgColorSymbolDetail.setFollowNumber
				}

				// main
				tv_symbol.setText(contentGetDetail.getString("symbol_name")
						.toString());
				tv_last_trade.setText(contentGetDetail.getString("last_trade")
						.toString());
				tv_symbol_name_eng.setText(contentGetDetail.getString(
						"symbol_fullname_eng").toString());

				// main color
				String strColor = contentGetDetail.getString("change")
						.toString();

				// ck ltrade change
				String strLastTrade = contentGetDetail.getString("last_trade")
						.toString();
				String strChange = contentGetDetail.getString("change")
						.toString();
				String strPercentChange = contentGetDetail.getString(
						"percentChange").toString();

				// tv_change.setText(strChange);
				if ((strPercentChange == "0") || strPercentChange == "") {
					tv_percenchange.setText("0.00");
				} else {
					tv_percenchange.setText("("
							+ contentGetDetail.getString("change") + ")"
							+ strPercentChange + "%");
				}

				if (strLastTrade != "") {
					tv_last_trade.setTextColor(getResources().getColor(
							FunctionSetBg.setColor(strLastTrade)));
				}
				if (strChange != "") {
					// tv_change.setTextColor(getResources()
					// .getColor(
					// FragmentChangeActivity
					// .setColor(strChange)));
					tv_percenchange.setTextColor(getResources().getColor(
							FunctionSetBg.setColor(strChange)));
					img_updown.setBackgroundResource(FunctionSetBg
							.setImgUpDown(strChange));
				}

				// column 1
				tv_volume.setText(contentGetDetail.getString("volume").toString().toString());
				tv_value.setText(contentGetDetail.getString("value").toString());
				tv_open.setText(contentGetDetail.getString("open").toString());
				tv_high.setText(contentGetDetail.getString("high").toString());
				tv_low.setText(contentGetDetail.getString("low").toString());

				// ck hight low
				String strPrevClose = contentGetDetail.getString("prev_close")
						.toString().replaceAll(",", "");
				String strHigh = contentGetDetail.getString("high").toString()
						.replaceAll(",", "");
				String strLow = contentGetDetail.getString("low").toString()
						.replaceAll(",", "");

				if (strPrevClose != "") {
					if (strHigh != "") {
						if ((Float.parseFloat(strHigh)) > Float
								.parseFloat(strPrevClose)) {
							tv_high.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[2]));
						} else if ((Float.parseFloat(strHigh)) < Float
								.parseFloat(strPrevClose)) {
							tv_high.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[0]));
						} else {
							tv_high.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[1]));
						}
					}
					if (strLow != "") {
						if ((Float.parseFloat(strLow)) > Float
								.parseFloat(strPrevClose)) {
							tv_low.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[2]));
						} else if ((Float.parseFloat(strLow)) < Float
								.parseFloat(strPrevClose)) {
							tv_low.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[0]));
						} else {
							tv_low.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[1]));
						}
					}
				}
				// column 2
				tv_prev_close.setText(contentGetDetail.getString("prev_close")
						.toString());
				tv_ceil.setText(contentGetDetail.getString("ceiling")
						.toString());
				tv_floor.setText(contentGetDetail.getString("floor").toString());
				tv_high52W.setText(contentGetDetail.getString("high52W")
						.toString());
				tv_low52W.setText(contentGetDetail.getString("low52W")
						.toString());
				// column 3tString("p_e").toString().replaceAll(",", "");
				String strPbv = contentGetDetail.getString("p_bv").toString()
						.replaceAll(",", "");
				String strRoe = contentGetDetail.getString("roe").toString()
						.replaceAll(",", "");
				String strRoa = contentGetDetail.getString("roa").toString()
						.replaceAll(",", "");
				String strPe = contentGetDetail.getString("p_e").toString()
						.replaceAll(",", "");
				String strPeg = contentGetDetail.getString("peg").toString()
						.replaceAll(",", "");

				tv_roe.setText(FunctionSetBg.setStrDetailList(strRoe));
				tv_roa.setText(FunctionSetBg.setStrDetailList(strRoa));
				tv_peg.setText(FunctionSetBg.setStrDetailList(strPeg));
				tv_p_e.setText(FunctionSetBg.setStrDetailList(strPe));
				tv_p_bv.setText(FunctionSetBg.setStrDetailList(strPbv));

				if (SplashScreen.contentSymbol_Set != null) {
					String strPe_set = SplashScreen.contentSymbol_Set
							.getString("p_e").toString().replaceAll(",", "");
					String strPbv_set = SplashScreen.contentSymbol_Set
							.getString("p_bv").toString().replaceAll(",", "");
					String strPeg_set = SplashScreen.contentSymbol_Set
							.getString("peg").toString().replaceAll(",", "");

					if (!(strPe.equals("")) && (strPe != null)
							&& !(strPe_set.equals(""))
							&& (strPe_set.equals(null))) {
						tv_p_e.setTextColor(getResources().getColor(
								FunctionSetBg.setStrCheckSet(strPe,
										strPe_set)));
					}
					if (!(strPbv.equals("")) && (strPbv != null)
							&& !(strPbv_set.equals(""))
							&& (strPbv_set.equals(null))) {
						tv_p_bv.setTextColor(getResources().getColor(
								FunctionSetBg.setStrCheckSet(strPbv,
										strPbv_set)));
					}
					if (!(strPeg.equals("")) && (strPeg != null)
							&& !(strPeg_set.equals(""))
							&& (strPeg_set.equals(null))) {
						tv_peg.setTextColor(getResources().getColor(
								FunctionSetBg.setStrCheckSet(strPeg,
										strPeg_set)));
					}
				}

				// show hide
				// final boolean ckHideShow = false;
				String strSymbN = contentGetDetail.getString("symbol_name");
				if ((strSymbN.equals(".SET")) || (strSymbN.equals(".SETHD"))
						|| (strSymbN.equals(".SET100"))
						|| (strSymbN.equals(".MAI"))
						|| (strSymbN.equals(".SET50"))) {
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
								img_show_hide
										.setBackgroundResource(R.drawable.icon_dopdowe_up);
							} else {
								ckHideShow = false;
								li_show_hide.setVisibility(View.GONE);
								img_show_hide
										.setBackgroundResource(R.drawable.icon_dopdowe_down);
							}
						}
					});

					tv_average_buy.setText(FunctionFormatData
							.checkNull(contentGetDetail
									.getString("average_buy")));
					tv_max_buy_price_volume.setText(contentGetDetail.getString(
							"max_buy_price_volume").toString());
					tv_open1_volume.setText(contentGetDetail.getString(
							"open1_volume").toString());
					tv_buy_volume.setText(contentGetDetail.getString(
							"buy_volume").toString());

					tv_average_sell.setText(FunctionFormatData
							.checkNull(contentGetDetail
									.getString("average_sell")));
					tv_max_sell_price_volume.setText(contentGetDetail
							.getString("max_sell_price_volume").toString());
					tv_close_volume.setText(contentGetDetail.getString(
							"close_volume").toString());
					tv_sell_volume.setText(contentGetDetail.getString(
							"sell_volume").toString());

					tv_percentBuy.setText("BUY ("
							+ contentGetDetail.getString("percentBuy") + "%)");
					tv_percentSell.setText("("
							+ contentGetDetail.getString("percentSell")
							+ "%) SELL");

					// percentBuy , sell
					float vBuy = Float.parseFloat(contentGetDetail
							.getString("percentBuy"));
					float vSell = Float.parseFloat(contentGetDetail
							.getString("percentSell"));
					Log.v("buy sell", "" + vBuy + "_" + vSell);
					LinearLayout.LayoutParams loparams = (LinearLayout.LayoutParams) v_percentBuy
							.getLayoutParams();
					loparams.weight = vBuy;
					v_percentBuy.setLayoutParams(loparams);

					LinearLayout.LayoutParams loparams2 = (LinearLayout.LayoutParams) v_percentSell
							.getLayoutParams();
					loparams2.weight = vSell;
					v_percentSell.setLayoutParams(loparams2);
				}
				
				// ------ connect socket -----------------
				if ( contentGetDetail.getString("orderbook_id") != "") {
					if (SplashScreen.contentGetUserById != null) {
						if (!(SplashScreen.contentGetUserById.getString("package")
								.equals("free"))) {
//							connectWebSocket();
						}
					}
				}

			} else {
				Toast.makeText(context, "No data.", 0).show();
			}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dialogLoading.dismiss();
	}

	// ============= dialog favorite =========
	@SuppressLint("NewApi")
	public static AlertDialog alertDialogFav;

	private void dialogFavorite() {

		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View dlView = layoutInflater.inflate(R.layout.dialog_favorite, null);
		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
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
		alertDialogFav = alertDialogBuilder.create();

		alertDialogFav.requestWindowFeature(Window.FEATURE_NO_TITLE);
		alertDialogFav.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		WindowManager.LayoutParams wmlp = alertDialogFav.getWindow()
				.getAttributes();

		wmlp.gravity = Gravity.TOP | Gravity.RIGHT;
		// wmlp.x = 100; // x position
		wmlp.y = 150; // y position

		alertDialogFav.show();
	}

	// ********** select favorite **********
	private OnClickListener onClickListenerFavorite = new OnClickListener() {
		@Override
		public void onClick(final View v) {
			alertDialogFav.dismiss();
			switch (v.getId()) {
			case R.id.li_favorite1:
				FragmentChangeActivity.strFavoriteNumber = "1";
				sendFavorite();
				// switchFragment(new PagerWatchlistDetail());
				break;
			case R.id.li_favorite2:
				FragmentChangeActivity.strFavoriteNumber = "2";
				sendFavorite();
				// switchFragment(new PagerWatchlistDetail());
				break;
			case R.id.li_favorite3:
				FragmentChangeActivity.strFavoriteNumber = "3";
				sendFavorite();
				// switchFragment(new PagerWatchlistDetail());
				break;
			case R.id.li_favorite4:
				FragmentChangeActivity.strFavoriteNumber = "4";
				sendFavorite();
				break;
			case R.id.li_favorite5:
				FragmentChangeActivity.strFavoriteNumber = "5";
				sendFavorite();
				// switchFragment(new PagerWatchlistDetail());
				break;
			default:
				break;
			}
		}
	};

	// ============== send addfavorite ===============
	public static void sendFavorite() {
		setFavorite resp = new setFavorite();
		resp.execute();
	}

	public static class setFavorite extends AsyncTask<Void, Void, Void> {

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
					+ "/service/addFavorite";

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

			dialogLoading.dismiss();
			switchFragment(new PagerWatchlistDetail());
		}
	}

	// ============= Pager ===========
	private void initPager() {
		initTabPager(); // init tab pager

		// creating fragments and adding to list
		fragmentMain.removeAll(fragmentMain);
		fragmentMain.add(Fragment.instantiate(context,
				PagerWatchListDetailChart_old.class.getName()));
		fragmentMain.add(Fragment.instantiate(context,
				PagerWatchListDetailSector.class.getName()));
		fragmentMain.add(Fragment.instantiate(context,
				PagerWatchListDetailNews.class.getName()));

		// creating adapter and linking to view pager
		this.mPagerAdapterMain = new PagerAdapter(
				super.getChildFragmentManager(), fragmentMain);
		mPagerMain = (ViewPager) rootView.findViewById(R.id.vp_pager);

		mPagerMain.setAdapter(this.mPagerAdapterMain);

		mPagerMain.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				// LOTBANK = (mPagerMain.getCurrentItem()) + 1;
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

	}

	// ============= Pager PreMium ===========
	private void initPagerPreMium() {
		initTabPagerPreMium(); // init tab pager

		// creating fragments and adding to list
		fragmentMainPreMium.removeAll(fragmentMainPreMium);
		fragmentMainPreMium.add(Fragment.instantiate(context,
				PagerWatchListDetailChart_old.class.getName()));
		fragmentMainPreMium.add(Fragment.instantiate(context,
				PagerWatchListDetailIndustry.class.getName()));
		fragmentMainPreMium.add(Fragment.instantiate(context,
				PagerWatchListDetailFundamental.class.getName()));
		fragmentMainPreMium.add(Fragment.instantiate(context,
				PagerWatchListDetailNews.class.getName()));
		fragmentMainPreMium.add(Fragment.instantiate(context,
				PagerWatchListDetailHit.class.getName()));

		// creating adapter and linking to view pager
		this.mPagerAdapterMainPreMium = new PagerAdapter(
				super.getChildFragmentManager(), fragmentMainPreMium);
		mPagerMainPreMium = (ViewPager) rootView.findViewById(R.id.vp_pager);

		mPagerMainPreMium.setAdapter(this.mPagerAdapterMainPreMium);

		mPagerMainPreMium.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				// LOTBANK = (mPagerMain.getCurrentItem()) + 1;
				selectTabPagerPreMium((mPagerMainPreMium.getCurrentItem()) + 1);
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

	}

	// config rotation
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Toast.makeText(context, "onConfigurationChan", Toast.LENGTH_SHORT)
				.show();

		// Checks the orientation of the screen
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			Toast.makeText(context, "แนวนอน", Toast.LENGTH_SHORT).show();
			// แนวนอน
			// li_menu_top.setVisibility(View.GONE);
			// li_data.setVisibility(View.GONE);
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			Toast.makeText(context, "แนวตั้ง", Toast.LENGTH_SHORT).show();
			// แนวตั้ง
			// li_menu_top.setVisibility(View.VISIBLE);
			// li_data.setVisibility(View.VISIBLE);
		}
	}

	// ============= set tab ===========
	public int selectTabPagerPreMium(int numtab) {
		tv_premium_chart.setBackgroundColor(Color.TRANSPARENT);
		tv_premium_industry.setBackgroundColor(Color.TRANSPARENT);
		tv_premium_fundamental.setBackgroundColor(Color.TRANSPARENT);
		tv_premium_news.setBackgroundColor(Color.TRANSPARENT);
		tv_premium_hit.setBackgroundColor(Color.TRANSPARENT);

		if (numtab == 1) {
			tv_premium_chart
					.setBackgroundResource(R.drawable.border_button_activeleft);
		} else if (numtab == 2) {
			tv_premium_industry
					.setBackgroundResource(R.drawable.border_button_activecenter);
		} else if (numtab == 3) {
			tv_premium_fundamental
					.setBackgroundResource(R.drawable.border_button_activecenter);
		} else if (numtab == 4) {
			tv_premium_news
					.setBackgroundResource(R.drawable.border_button_activecenter);
		} else if (numtab == 5) {
			tv_premium_hit
					.setBackgroundResource(R.drawable.border_button_activeright);
		}
		return numtab;
	}

	// ============= set tab ===========
	public int selectTabPager(int numtab) {
		tv_chart.setBackgroundColor(Color.TRANSPARENT);
		tv_news.setBackgroundColor(Color.TRANSPARENT);
		tv_sector.setBackgroundColor(Color.TRANSPARENT);

		if (numtab == 1) {
			tv_chart.setBackgroundResource(R.drawable.border_button_activeleft);
		} else if (numtab == 2) {
			tv_sector
					.setBackgroundResource(R.drawable.border_button_activecenter);
		} else if (numtab == 3) {
			tv_news.setBackgroundResource(R.drawable.border_button_activeright);
		}

		return numtab;
	}

	// ============== send removefavorite ===============
	public static String strRemoveId = "";

	public static void sendRemoveFavorite() {

		setRemoveFavorite resp = new setRemoveFavorite();
		resp.execute();
	}

	public static class setRemoveFavorite extends AsyncTask<Void, Void, Void> {

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

			String url = SplashScreen.url_bidschart
					+ "/service/removeFavorite";

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

			switchFragment(new PagerWatchlistDetail());
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
			dialogLoading.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// ======= url ========
			String url_fav;
			url_fav = SplashScreen.url_bidschart
					+ "/service/getFavoriteByUserIdFavoriteNumber?user_id=104&favorite_number="
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

	// the meat of switching the above fragment
	private static void switchFragment(Fragment fragment) {
		if (context == null)
			return;
		if (context instanceof FragmentChangeActivity) {
			FragmentChangeActivity fca = (FragmentChangeActivity) context;
			fca.switchContent(fragment);
		}
	}

}