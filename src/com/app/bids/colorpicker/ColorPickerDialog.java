package com.app.bids.colorpicker;

import com.app.bids.R;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

public class ColorPickerDialog extends Dialog {

	public ColorPickerDialog(Context context) {
		super(context);
		this.setTitle("ColorPickerDialog");
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.dialog_chartiq_compare);

		GridView gridViewColors = (GridView) findViewById(R.id.gridViewColors);
		gridViewColors.setAdapter(new ColorPickerAdapter(getContext()));

		// close the dialog on item click
		gridViewColors.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final int i = position;
//				Integer intColor = ColorPickerAdapter.colorList.get(i);
//				String hexColor = "#"+ Integer.toHexString(intColor).substring(2);
//				Toast.makeText(getContext(), "" + hexColor, 0).show();
				ColorPickerDialog.this.dismiss();
			}
		});
	}
}
