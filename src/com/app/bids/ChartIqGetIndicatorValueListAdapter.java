package com.app.bids;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import com.app.bids.R;

public class ChartIqGetIndicatorValueListAdapter extends ArrayAdapter {
	ArrayList<ChartIqGetIndicatorValueCatalog> arl;

	public ChartIqGetIndicatorValueListAdapter(Context context, int textViewResourceId,
			ArrayList<ChartIqGetIndicatorValueCatalog> arl) {
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
			v = vi.inflate(R.layout.row_dialog_studies, null);

		}
		final TextView tv_row = (TextView) v.findViewById(R.id.tv_row);

		// text
		tv_row.setText("" + arl.get(position).title);
		tv_row.setTag("" + arl.get(position).key);

		return v;

	}
}