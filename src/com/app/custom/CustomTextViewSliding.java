package com.app.custom;

import com.app.bids.FragmentChangeActivity;
import com.app.bids.SplashScreen;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

public class CustomTextViewSliding extends TextView {

	public CustomTextViewSliding(Context context, AttributeSet attr) {
		super(context, attr);
		setTypeface(SplashScreen.fontSliding);

		// android:fontFamily="sans-serif-condensed"

		// setBackgroundColor(Color.GRAY);
		// setTextColor(Color.BLUE);
		// setTextSize(17);

	}

	// @Override
	// public void setTextColor(int color) {
	// // TODO Auto-generated method stub
	// super.setTextColor(color);
	// }
	//
	// @Override
	// public void setTextSize(float size) {
	// // TODO Auto-generated method stub
	// super.setTextSize(size);
	// }
	//
	// @Override
	// public void setBackgroundColor(int color) {
	// super.setBackgroundColor(color);
	// }

}
