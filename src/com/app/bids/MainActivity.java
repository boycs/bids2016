package com.app.bids;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import com.app.bids.R;

public class MainActivity extends Activity {

	private ViewGroup hiddenPanel;
	private boolean isPanelShown;
	
	LinearLayout main_screen;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.test_menu);

		main_screen = (LinearLayout)findViewById(R.id.main_screen);
		
		hiddenPanel = (ViewGroup) findViewById(R.id.hidden_panel);
		hiddenPanel.setVisibility(View.INVISIBLE);
		isPanelShown = false;
		
		
//		main_screen.onTouchEvent()
	}
	

	// @Override
	// public boolean onCreateOptionsMenu(Menu menu) {
	// // Inflate the menu; this adds items to the action bar if it is present.
	// getMenuInflater().inflate(R.menu.main, menu);
	// return true;
	// }

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.v("touchhhhhhhhh", "" + event.getAction());
		return super.onTouchEvent(event);
	}

	public void slideUpDown(final View view) {
		if (!isPanelShown) {
			Log.v("if", "if");
			// Show the panel
			Animation bottomUp = AnimationUtils.loadAnimation(this,
					R.animator.bottom_up);

			hiddenPanel.startAnimation(bottomUp);
			hiddenPanel.setVisibility(View.VISIBLE);
			isPanelShown = true;
		} else {
			Log.v("else", "else");
			// Hide the Panel
			Animation bottomDown = AnimationUtils.loadAnimation(this,
					R.animator.bottom_down);

			hiddenPanel.startAnimation(bottomDown);
			hiddenPanel.setVisibility(View.INVISIBLE);
			isPanelShown = false;
		}
	}

}
