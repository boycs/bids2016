package com.app.bids;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;
import com.app.bids.GridviewAdapterUiFav.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ListAdapterSearchSymbolSystemTradeCdc extends ArrayAdapter {
	ArrayList<CatalogGetSymbol> arl;

	public ListAdapterSearchSymbolSystemTradeCdc(Context context,
			int textViewResourceId, ArrayList<CatalogGetSymbol> arl) {
		super(context, textViewResourceId);
		this.arl = arl;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arl.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;

		if (v == null) {

			LayoutInflater vi;
			vi = LayoutInflater.from(getContext());
			v = vi.inflate(R.layout.row_systemtrade_search_symbol, null);

		}

		TextView tv_symbol = (TextView) v.findViewById(R.id.tv_symbol);
		TextView tv_market_id = (TextView) v.findViewById(R.id.tv_market_id);
		TextView tv_symbol_fullname_eng = (TextView) v
				.findViewById(R.id.tv_symbol_fullname_eng);

		ImageView img_trade_signal = (ImageView) v
				.findViewById(R.id.img_trade_signal);
		TextView tv_trade_date = (TextView) v.findViewById(R.id.tv_trade_date);

		ImageView img_macd = (ImageView) v.findViewById(R.id.img_macd);
		ImageView img_bb = (ImageView) v.findViewById(R.id.img_bb);
		ImageView img_sto = (ImageView) v.findViewById(R.id.img_sto);
		ImageView img_rsi = (ImageView) v.findViewById(R.id.img_rsi);
		ImageView img_ema = (ImageView) v.findViewById(R.id.img_ema);

		final ImageView img_add_symbol = (ImageView) v
				.findViewById(R.id.img_add_symbol);
		final LinearLayout li_row = (LinearLayout) v.findViewById(R.id.li_row);

		// text
		tv_symbol.setText(arl.get(position).symbol);
		tv_market_id.setText(arl.get(position).market_id);
		tv_symbol_fullname_eng.setText(arl.get(position).symbol_fullname_eng);

		img_trade_signal.setBackgroundResource(setColorBuySell(arl
				.get(position).trade_signal));
		tv_trade_date.setText(DateTimeCreate.DateDmySystemTradeSearch(arl
				.get(position).trade_date)); // 2016-02-05

		// tag
		tv_symbol.setTag(arl.get(position).symbol);
		tv_market_id.setTag(arl.get(position).market_id);
		tv_symbol_fullname_eng.setTag(arl.get(position).symbol_fullname_eng);
		img_add_symbol.setTag(arl.get(position).symbol);
		li_row.setTag(arl.get(position).symbol);

		// color
		img_macd.setBackgroundResource(setColorMacd(arl.get(position).color_macd));
		img_bb.setBackgroundResource(setColorBb(arl.get(position).color_bb));
		img_sto.setBackgroundResource(setColorSto(arl.get(position).color_sto));
		img_rsi.setBackgroundResource(setColorRsi(arl.get(position).color_rsi));
		img_ema.setBackgroundResource(setColorEma(arl.get(position).color_ema));

		// img_add_symbol.setTag("Img");
		// li_row.setTag("Row");

		final String symbolSelect = arl.get(position).symbol;
		img_add_symbol.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				FragmentChangeActivity.strSymbolSelect = symbolSelect;
				boolean ckFollow = FollowSymbolSystemTrade
						.checkFollowSymbolWatchlistSystemTrade(FragmentChangeActivity.strSymbolSelect);

				// /service/v2/addWatchlistSystemTrade
				// symbol_name=%@&user=%@&type=0
				// แบบ post
				// /watchlist/destroyForMobile/%@,symbolFollow[@"watch_id"]
				// platform=mobile

				if (ckFollow) { // unfollow
					img_add_symbol
							.setBackgroundResource(R.drawable.icon_plus_blue);
					// FollowFunction.sendRemoveWatchlistSystemTrade();
					FollowSymbolSystemTrade.getWatchIdWatchlistSystemTrade(FragmentChangeActivity.strSymbolSelect);
				} else {
					if (FollowSymbol
							.checkFollowCount(FragmentChangeActivity.strSymbolSelect)) {
						img_add_symbol
								.setBackgroundResource(R.drawable.icon_check_green);
						sendAddWatchlistSystemTrade();
					} else {
					}
				}
			}
		});

		img_add_symbol.setBackgroundResource(FollowSymbolSystemTrade
				.checkFollowSearchSymbolWatchlistSystemTrade(symbolSelect));

		li_row.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ----- hide list search --------
				PagerSystemTradeCdc.hideListSearch();
				// ----- startActivity detail symbol
				FragmentChangeActivity.strSymbolSelect = symbolSelect;
				FragmentChangeActivity.startUiDetail();

			}
		});

		return v;

	}

	// ============== follow ===============
	public static void sendAddWatchlistSystemTrade() {
		setWatchlistSystemTrade resp = new setWatchlistSystemTrade();
		resp.execute();
	}

	public static class setWatchlistSystemTrade extends
			AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		String temp = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progress.show();

		}

		@Override
		protected Void doInBackground(Void... params) {

			String url = SplashScreen.url_bidschart
					+ "/service/v2/addWatchlistSystemTrade";

			String json = "";
			InputStream inputStream = null;
			String result = "";

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("symbol_name",
						FragmentChangeActivity.strSymbolSelect);
				jsonObject.accumulate("user", SplashScreen.userModel.user_id);
				jsonObject.accumulate("type", 0);

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

				Log.v("result addWatchlistSystemTrade", "" + result);

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
			FollowSymbolSystemTrade.initGetWatchlistSystemTrade();
		}
	}

	// ================ color ============
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

	// --------------- macd
	public static int setColorMacd(String strColor) {
		int c = R.drawable.icon_empty;

		if (strColor.equals("BLUE")) {
			c = R.drawable.icon_system_macd_b;
		} else if (strColor.equals("GREEN")) {
			c = R.drawable.icon_system_macd_g;
		} else if (strColor.equals("ORANGE")) {
			c = R.drawable.icon_system_macd_o;
		} else if (strColor.equals("RED")) {
			c = R.drawable.icon_system_macd_r;
		} else if (strColor.equals("BLACK")) {
			c = R.drawable.icon_empty;
		}
		return c;
	}

	// --------------- bb
	public static int setColorBb(String strColor) {
		int c = R.drawable.icon_empty;

		if (strColor.equals("BLUE")) {
			c = R.drawable.icon_system_bb_b;
		} else if (strColor.equals("GREEN")) {
			c = R.drawable.icon_system_bb_g;
		} else if (strColor.equals("ORANGE")) {
			c = R.drawable.icon_system_bb_o;
		} else if (strColor.equals("RED")) {
			c = R.drawable.icon_system_bb_r;
		} else if (strColor.equals("BLACK")) {
			c = R.drawable.icon_empty;
		}
		return c;
	}

	// --------------- sto
	public static int setColorSto(String strColor) {
		int c = R.drawable.icon_empty;

		if (strColor.equals("BLUE")) {
			c = R.drawable.icon_system_sto_b;
		} else if (strColor.equals("GREEN")) {
			c = R.drawable.icon_system_sto_g;
		} else if (strColor.equals("ORANGE")) {
			c = R.drawable.icon_system_sto_o;
		} else if (strColor.equals("RED")) {
			c = R.drawable.icon_system_sto_r;
		} else if (strColor.equals("BLACK")) {
			c = R.drawable.icon_empty;
		}
		return c;
	}

	// --------------- rsi
	public static int setColorRsi(String strColor) {
		int c = R.drawable.icon_empty;

		if (strColor.equals("BLUE")) {
			c = R.drawable.icon_system_rsi_b;
		} else if (strColor.equals("GREEN")) {
			c = R.drawable.icon_system_rsi_g;
		} else if (strColor.equals("ORANGE")) {
			c = R.drawable.icon_system_rsi_o;
		} else if (strColor.equals("RED")) {
			c = R.drawable.icon_system_rsi_r;
		} else if (strColor.equals("BLACK")) {
			c = R.drawable.icon_empty;
		}
		return c;
	}

	// --------------- ema
	public static int setColorEma(String strColor) {
		int c = R.drawable.icon_empty;

		if (strColor.equals("BLUE")) {
			c = R.drawable.icon_system_ema_b;
		} else if (strColor.equals("GREEN")) {
			c = R.drawable.icon_system_ema_g;
		} else if (strColor.equals("ORANGE")) {
			c = R.drawable.icon_system_ema_o;
		} else if (strColor.equals("RED")) {
			c = R.drawable.icon_system_ema_r;
		} else if (strColor.equals("BLACK")) {
			c = R.drawable.icon_empty;
		}
		return c;
	}

}