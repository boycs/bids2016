package com.app.bids;

import java.util.ArrayList;

import com.app.bids.R;
import com.app.bids.GridviewAdapterUiFav.ViewHolder;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ListAdapterSearchSymbolSetAlert extends ArrayAdapter {
	ArrayList<CatalogGetSymbol> arl;

	public ListAdapterSearchSymbolSetAlert(Context context,
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
			v = vi.inflate(R.layout.row_setalert_search_symbol, null);

		}
		final TextView tv_symbol = (TextView) v.findViewById(R.id.tv_symbol);
		final TextView tv_market_id = (TextView) v
				.findViewById(R.id.tv_market_id);
		final TextView tv_symbol_fullname_eng = (TextView) v
				.findViewById(R.id.tv_symbol_fullname_eng);
		final ImageView img_add_symbol = (ImageView) v
				.findViewById(R.id.img_add_symbol);
		final LinearLayout li_row = (LinearLayout) v.findViewById(R.id.li_row);

		// text
		tv_symbol.setText("" + arl.get(position).symbol);
		tv_market_id.setText("" + arl.get(position).market_id);
		tv_symbol_fullname_eng.setText(""
				+ arl.get(position).symbol_fullname_eng);
		// tag
		tv_symbol.setTag("" + arl.get(position).symbol);
		tv_market_id.setTag("" + arl.get(position).market_id);
		tv_symbol_fullname_eng.setTag(""
				+ arl.get(position).symbol_fullname_eng);
		img_add_symbol.setTag("" + arl.get(position).symbol);
		li_row.setTag("" + arl.get(position).symbol);

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
//				PagerWatchList.handlerGetSymbolAddFav.sendMessage(msg);
			}
		});

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

	// the meat of switching the above fragment
//	private static void switchFragment(Fragment fragment) {
//		if (context == null)
//			return;
//		if (context instanceof FragmentChangeActivity) {
//			FragmentChangeActivity fca = (FragmentChangeActivity) context;
//			fca.switchContent(fragment);
//		}
//	}

}