package com.app.bids;

import java.util.ArrayList;

import com.app.bids.R;
import com.app.bids.GridviewAdapterUiFav.ViewHolder;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ListAdapterSearchSymbolSystemTrade extends ArrayAdapter {
	ArrayList<CatalogGetSymbol> arl;

	public ListAdapterSearchSymbolSystemTrade(Context context,
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
				Message msg = new Message();
				Bundle b = new Bundle();
				b.putString("msg", "" + img_add_symbol.getTag().toString());
				msg.setData(b);

				FragmentChangeActivity.strSymbolSelect = symbolSelect;
				PagerWatchListDetailFollow.sendAddFavorite();
				// PagerWatchList.handlerGetSymbolAddFav.sendMessage(msg);
			}
		});

		img_add_symbol.setBackgroundResource(FunctionSymbol
				.checkFollowSearchSymbol(symbolSelect));

		li_row.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Message msg = new Message();
				Bundle b = new Bundle();
				b.putString("msg", "" + img_add_symbol.getTag().toString());
				msg.setData(b);

				FragmentChangeActivity.strSymbolSelect = symbolSelect;
				PagerWatchList.handlerSetAlertSymbol.sendMessage(msg);

			}
		});

		return v;

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