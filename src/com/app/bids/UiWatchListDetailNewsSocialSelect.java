package com.app.bids;

import org.json.JSONException;

import com.app.bids.R;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UiWatchListDetailNewsSocialSelect extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		
		setContentView(R.layout.ui_watchlist_news_social);

		initViewSelect();

	}

	// ====================== Initial View ========================
	private void initViewSelect() {
		LinearLayout li_back = (LinearLayout) findViewById(R.id.li_back);

		li_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
				overridePendingTransition(R.animator.right_to_center,
						R.animator.center_to_right);
			}
		});

		// view data select
		WebView webNews = (WebView) findViewById(R.id.webNews);

		String url = "" + PagerWatchListDetailNews.linkNewsSelect;

		// webNews.getSettings().setLoadWithOverviewMode(true);
		// webNews.getSettings().setUseWideViewPort(true);
		// webNews.getSettings().setBuiltInZoomControls(true);

		WebSettings webSettings = webNews.getSettings();
		webSettings.setJavaScriptEnabled(true);
		webNews.loadUrl(url);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish();
			overridePendingTransition(R.animator.right_to_center,
					R.animator.center_to_right);
		}
		return super.onKeyDown(keyCode, event);
	}

}
