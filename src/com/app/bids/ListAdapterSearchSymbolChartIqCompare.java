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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
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

public class ListAdapterSearchSymbolChartIqCompare extends ArrayAdapter {
	ArrayList<CatalogGetSymbol> arl;

	public ListAdapterSearchSymbolChartIqCompare(Context context,
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
			v = vi.inflate(R.layout.row_chartiq_search_compare_symbol, null);
		}
		final TextView tv_symbol = (TextView) v.findViewById(R.id.tv_symbol);
		final TextView tv_market_id = (TextView) v
				.findViewById(R.id.tv_market_id);
		final TextView tv_symbol_fullname_eng = (TextView) v
				.findViewById(R.id.tv_symbol_fullname_eng);
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
		li_row.setTag("" + arl.get(position).symbol);

		final String symbolSelect = arl.get(position).symbol;

		li_row.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

			}
		});

		return v;
	}

}