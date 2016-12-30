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

public class ListAdapterSearchSymbolSystemTradeMutualFund extends ArrayAdapter {
	ArrayList<CatalogGetNameFund> arl;

	public ListAdapterSearchSymbolSystemTradeMutualFund(Context context,
			int textViewResourceId, ArrayList<CatalogGetNameFund> arl) {
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
			v = vi.inflate(R.layout.row_mutualfund_search_symbol, null);
		}
		final TextView tv_name_initial = (TextView) v.findViewById(R.id.tv_name_initial);
		final TextView tv_name_t = (TextView) v
				.findViewById(R.id.tv_name_t);
		final TextView tv_asset_initial = (TextView) v
				.findViewById(R.id.tv_asset_initial);
		final LinearLayout li_row = (LinearLayout) v.findViewById(R.id.li_row);

		// text
		tv_name_initial.setText("" + arl.get(position).name_initial);
		tv_name_t.setText("" + arl.get(position).name_t);
		tv_asset_initial.setText(""+ arl.get(position).asset_initial);
		// tag
		li_row.setTag("" + arl.get(position).name_initial);

		final String name_initial = arl.get(position).name_initial;
		li_row.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ----- hide list search --------
				PagerSystemTradeMutualFund.hideListSearch();
				// ----- startActivity detail symbol
				FragmentChangeActivity.strSymbolSelect = name_initial;
				FragmentChangeActivity.startUiDetailMutual();
			}
		});

		return v;
	}

}