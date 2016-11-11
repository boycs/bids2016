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
public class UiHotHeatmap extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ui_heatmap_select);

		initView();

	}

	// ====================== Initial View ========================

	private void initView() {
		WebView wv_heatmap = (WebView) findViewById(R.id.wv_heatmap);

		wv_heatmap.getSettings().setLoadWithOverviewMode(true);
		wv_heatmap.getSettings().setUseWideViewPort(true);
		wv_heatmap.getSettings().setBuiltInZoomControls(true);
//		wv_heatmap.setInitialScale(70); // zoom is %
		WebSettings webSettings = wv_heatmap.getSettings();
		webSettings.setJavaScriptEnabled(true);
		wv_heatmap.loadUrl(SplashScreen.url_bidschart+"/images/heatmap-phone.png");
		
		// String data = "<html><head>"
		// + "<style type=\"text/css\">body{color: #585f65;}"
		// + "</style></head>"
		// + "<body>"
		// + PagerNews.contentGetNewsSelect.get("article_description")
		// .toString() + "</body></html>";
		//
		// String mimeType = "text/html";
		// String encoding = "utf-8";
		//
		// webNews.getSettings().setJavaScriptEnabled(true);
		// wv_heatmap.loadDataWithBaseURL(null, data, mimeType,
		// encoding, null);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish();
			// overridePendingTransition(R.animator.right_to_center,
			// R.animator.center_to_right);
		}
		return super.onKeyDown(keyCode, event);
	}

}
