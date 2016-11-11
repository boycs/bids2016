package com.app.bids.colorpicker;

import java.util.ArrayList;
import java.util.List;

import com.app.bids.R;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

public class ColorPickerAdapter extends BaseAdapter {

	private Context context;
	public static List<Integer> colorList = new ArrayList<Integer>();
	int colorGridColumnWidth;

	public ColorPickerAdapter(Context context) {

		this.context = context;

		// defines the width of each color square
		colorGridColumnWidth = context.getResources().getInteger(R.integer.colorGridColumnWidth);

		int colorCount = 96;
		int step = 256 / (colorCount / 6);
		int red = 0, green = 0, blue = 0;

		colorList.clear();
		// FF 00 00 --> FF FF 00
		for (red = 255, green = 0, blue = 0; green <= 255; green += step)
			colorList.add(Color.rgb(red, green, blue));

		// FF FF 00 --> 00 FF 00
		for (red = 255, green = 255, blue = 0; red >= 0; red -= step)
			colorList.add(Color.rgb(red, green, blue));

		// 00 FF 00 --> 00 FF FF
		for (red = 0, green = 255, blue = 0; blue <= 255; blue += step)
			colorList.add(Color.rgb(red, green, blue));

		// 00 FF FF -- > 00 00 FF
		for (red = 0, green = 255, blue = 255; green >= 0; green -= step)
			colorList.add(Color.rgb(red, green, blue));

		// 00 00 FF --> FF 00 FF
		for (red = 0, green = 0, blue = 255; red <= 255; red += step)
			colorList.add(Color.rgb(red, green, blue));

		// FF 00 FF -- > FF 00 00
		for (red = 255, green = 0, blue = 255; blue >= 0; blue -= 256 / step)
			colorList.add(Color.rgb(red, green, blue));

		// add gray colors
		for (int i = 255; i >= 0; i -= 11) {
			colorList.add(Color.rgb(i, i, i));
		}
		
//		Toast.makeText(context, "colorList : "+colorList.size(), 0).show();
		
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ImageView imageView;

		// can we reuse a view?
		if (convertView == null) {
			imageView = new ImageView(context);
			// set the width of each color square
			imageView.setLayoutParams(new GridView.LayoutParams(colorGridColumnWidth, colorGridColumnWidth));

		} else {
			imageView = (ImageView) convertView;
		}

		imageView.setBackgroundColor(colorList.get(position));
		imageView.setId(position);

//		final int i = position;
//		imageView.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Integer intColor = colorList.get(i);
//				String hexColor = "#" + Integer.toHexString(intColor).substring(2);
//				Toast.makeText(context, ""+hexColor, 0).show();
//			}
//		});

		return imageView;
	}

	public int getCount() {
		return colorList.size();
	}

	public Object getItem(int position) {
		return null;
	}

	public long getItemId(int position) {
		return 0;
	}
}
