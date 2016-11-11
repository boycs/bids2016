package com.app.bids;

import java.io.IOException;
import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;
import com.app.bids.PagerWatchlistDetail.loadData;
import com.app.bids.UiWatchListDetailNewsSelect.loadAll;
import com.app.model.login.FacebookLoginActivity;
import com.app.model.login.LoginDialog2;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class UiCdcDetailWebView extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ui_detail_webview);

		initView();

	}

	// ====================== Initial View ========================

	private void initView() {
		((ImageView) findViewById(R.id.img_back))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});

		WebView wv_webview = (WebView) findViewById(R.id.wv_webview);

		wv_webview.getSettings().setLoadWithOverviewMode(true);
		wv_webview.getSettings().setUseWideViewPort(true);
		wv_webview.getSettings().setBuiltInZoomControls(true);
		// wv_heatmap.setInitialScale(70); // zoom is %
		WebSettings webSettings = wv_webview.getSettings();
		webSettings.setJavaScriptEnabled(true);
		wv_webview.loadUrl("http://www.chaloke.com/donations");

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
