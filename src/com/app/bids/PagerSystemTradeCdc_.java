package com.app.bids;

import java.io.IOException;
import java.text.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ToggleButton;
import com.app.bids.R;

public class PagerSystemTradeCdc_ extends Fragment {

	static Context context;
	public static View rootView;

	// activity listener interface
	private OnPageListener pageListener;

	public static JSONArray jsaBuySell = null; // data buy sell
	public static int statBuySell = 0; // 0 => buy, 1=> sell

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
				R.layout.pager_systemtrade_buysell, container, false);

		initMenu();

		return rootView;

	}

	// ============= init menu ===========

	TextView tv_signal, tv_buysel, tv_count;
	ToggleButton toggle_buysell;

	private void initMenu() {
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

	// ============= set data ===========
	private void initSetData() {
		// Log.v("initSetData", ""+PagerSystemTrade.contentGetMacd);
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

				for (int i = 0; i < jsaBuySell.length(); i++) {
					View viewSymbol = ((Activity) context).getLayoutInflater()
							.inflate(R.layout.row_systemtrade_symbol, null);
					View viewDetail = ((Activity) context).getLayoutInflater()
							.inflate(R.layout.row_systemtrade_detail, null);

					JSONObject jsoIndex = jsaBuySell.getJSONObject(i);

					// symbol
					((TextView) viewSymbol.findViewById(R.id.tv_symbol_name))
							.setText(jsoIndex.getString("symbol").toString());
					((TextView) viewSymbol
							.findViewById(R.id.tv_symbol_fullname_eng))
							.setText(jsoIndex.getString("symbol_fullname_eng")
									.toString());

					if ((jsoIndex.getString("follow").toString())
							.equals("false")) {
						// img_follow.setBackgroundResource(R.drawable.syst_addfollowed);
						// img_follow.setBackgroundResource(R.drawable.syst_addfollowed);
					} else {
						// img_follow.setBackgroundResource(R.drawable.syst_selectfollow);
						// img_follow.setBackgroundResource(R.drawable.syst_selectfollow);
					}

					final String symbol_name = jsoIndex.getString("symbol")
							.toString();
					((LinearLayout) viewSymbol.findViewById(R.id.row_symbol))
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									FragmentChangeActivity.strSymbolSelect = symbol_name;
									switchFragment(new PagerWatchlistDetail());
								}
							});

					// detail
					((LinearLayout) viewDetail.findViewById(R.id.row_detail))
							.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									FragmentChangeActivity.strSymbolSelect = symbol_name;
									switchFragment(new PagerWatchlistDetail());
								}
							});
					// ck > 0
					// ----- Trend signal
					String strTrade_signal = jsoIndex.getString("trade_signal")
							.toString();
					((ImageView) viewDetail.findViewById(R.id.img_trade_signal))
							.setBackgroundResource(FunctionSetBg.setImgTrendSignal(strTrade_signal));
					
					String strLastTrade = jsoIndex.getString("last_trade")
							.toString();
					String strChange = jsoIndex.getString("change").toString();
					String strPercentChange = jsoIndex.getString(
							"percentChange").toString();

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
					if (strPercentChange == "0") {
						tv_percentChange.setText("0.00");
					} else {
						tv_percentChange.setText(strPercentChange + "%");
					}

					if (strLastTrade != "") {
						img_updown.setBackgroundResource(FunctionSetBg
								.setImgUpDown(strLastTrade));
						tv_last_trade.setTextColor(getResources().getColor(
								FunctionSetBg.setColor(strLastTrade)));
					}
					if (strChange != "") {
						tv_change.setTextColor(getResources().getColor(
								FunctionSetBg.setColor(strChange)));
						tv_percentChange.setTextColor(getResources().getColor(
								FunctionSetBg.setColor(strChange)));
					}

					// ck hight low
					String strColorPclose = jsoIndex.getString("prev_close")
							.toString().replaceAll(",", "");
					String strHigh = jsoIndex.getString("high").toString()
							.replaceAll(",", "");
					String strLow = jsoIndex.getString("low").toString()
							.replaceAll(",", "");

					TextView tv_high = (TextView) viewDetail
							.findViewById(R.id.tv_high);
					TextView tv_low = (TextView) viewDetail
							.findViewById(R.id.tv_low);

					tv_high.setText(FunctionSetBg
							.setStrDetailList(strHigh));
					tv_low.setText(FunctionSetBg
							.setStrDetailList(strLow));

					if (strColorPclose != "") {
						if (strHigh != "") {
							if ((Float.parseFloat(strHigh)) > Float
									.parseFloat(strColorPclose)) {
								tv_high.setTextColor(getResources().getColor(
										FunctionSetBg.arrColor[2]));
							} else if ((Float.parseFloat(strHigh)) < Float
									.parseFloat(strColorPclose)) {
								tv_high.setTextColor(getResources().getColor(
										FunctionSetBg.arrColor[0]));
							} else {
								tv_high.setTextColor(getResources().getColor(
										FunctionSetBg.arrColor[1]));
							}
						}
						if (strLow != "") {
							if ((Float.parseFloat(strLow)) > Float
									.parseFloat(strColorPclose)) {
								tv_low.setTextColor(getResources().getColor(
										FunctionSetBg.arrColor[2]));
							} else if ((Float.parseFloat(strLow)) < Float
									.parseFloat(strColorPclose)) {
								tv_low.setTextColor(getResources().getColor(
										FunctionSetBg.arrColor[0]));
							} else {
								tv_low.setTextColor(getResources().getColor(
										FunctionSetBg.arrColor[1]));
							}
						}
					}

					// ////
					// TextView tv_last_trade = (TextView) viewDetail
					// .findViewById(R.id.tv_last_trade);
					// TextView tv_percentChange = (TextView) viewDetail
					// .findViewById(R.id.tv_percentChange);
					// ImageView img_updown = (ImageView) viewDetail
					// .findViewById(R.id.img_updown);
					//
					// tv_last_trade.setText(jsoIndex.getString("last_trade")
					// .toString());
					// tv_percentChange.setText(jsoIndex
					// .getString("percentChange").toString() + "%");
					//
					// String strColor =
					// jsoIndex.getString("change").toString();
					// tv_last_trade.setTextColor(getResources().getColor(
					// FragmentChangeActivity.setColor(strColor)));
					// tv_percentChange.setTextColor(getResources().getColor(
					// FragmentChangeActivity.setColor(strColor)));
					// img_updown.setImageResource(FragmentChangeActivity.setImgUpDown(strColor));
					// // img_updown.setBackgroundResource(PagerWatchList
					// // .setImgUpDown(strColor));
					//
					// ((TextView) viewDetail.findViewById(R.id.tv_change))
					// .setText(jsoIndex.getString("change").toString());
					// ((TextView) viewDetail.findViewById(R.id.tv_high))
					// .setText(jsoIndex.getString("high").toString());
					// ((TextView) viewDetail.findViewById(R.id.tv_low))
					// .setText(jsoIndex.getString("low").toString());

					((TextView) viewDetail.findViewById(R.id.tv_volume))
							.setText(jsoIndex.getString("volume").toString());
					((TextView) viewDetail.findViewById(R.id.tv_value))
							.setText(jsoIndex.getString("value").toString());
					((TextView) viewDetail.findViewById(R.id.tv_ceiling))
							.setText(jsoIndex.getString("ceiling").toString());
					((TextView) viewDetail.findViewById(R.id.tv_floor))
							.setText(jsoIndex.getString("floor").toString());

					list_symbol.addView(viewSymbol);
					list_detail.addView(viewDetail);
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected void switchFragment(PagerWatchlistDetail pagerSymbolDetail) {
		if (getActivity() == null)
			return;
		if (getActivity() instanceof FragmentChangeActivity) {
			FragmentChangeActivity fca = (FragmentChangeActivity) getActivity();
			fca.switchContent(pagerSymbolDetail);
		}
	}

}