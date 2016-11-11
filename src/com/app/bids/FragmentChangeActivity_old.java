package com.app.bids;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Stack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;
import com.app.bids.FragmentUiMain.MyListFragmentListener;
import com.app.bids.swiqe.ActivitySwipeMotion;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class FragmentChangeActivity_old extends
		android.support.v4.app.FragmentActivity implements
		MyListFragmentListener {

	public static FragmentManager fm_manage;
	public static MyListFragmentListener mylistFm;

	// **************************
	public static Stack<Fragment> fragmentStack;
	public static FragmentManager fragmentManager;
	public static FragmentUiMain uimainFragment;

	private Fragment mContent;

	private static Activity act;

	// iImageLoader
	// public static ImageLoader imageLoader = ImageLoader.getInstance();
	// public static DisplayImageOptions options;

	// show hide menu
	private Animation animShow, animHide;
	SlidingPanel popup;
	LinearLayout li_menu_hide;

	// banner
	public static String addTestDevice = "B76D5A737EE6DC975354D3FF4C8D7722";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		act = this;

		setContentView(R.layout.fragment_activity);

		// ***************** เซฟรูปลงเครื่อง ******************
		// imageLoader.init(ImageLoaderConfiguration
		// .createDefault(getApplicationContext()));

		// File cacheDir =
		// StorageUtils.getCacheDirectory(getApplicationContext());
		//
		// ImageLoaderConfiguration config = new
		// ImageLoaderConfiguration.Builder(
		// getApplicationContext())
		// .threadPriority(Thread.NORM_PRIORITY - 2)
		// .discCacheFileNameGenerator(new Md5FileNameGenerator())
		// .tasksProcessingOrder(QueueProcessingType.LIFO)
		// .memoryCache(new LruMemoryCache(2 * 1024 * 1024))
		// .memoryCacheSize(2 * 1024 * 1024).memoryCacheSizePercentage(50)
		// .discCache(new UnlimitedDiscCache(cacheDir))
		// .discCacheSize(50 * 1024 * 1024).discCacheFileCount(100)
		// .writeDebugLogs().build();
		//
		// options = new DisplayImageOptions.Builder()
		// .showImageOnLoading(R.drawable.img_default_border)
		// .showImageForEmptyUri(R.drawable.img_default_border)
		// .showImageOnFail(R.drawable.img_default_border).cacheInMemory(true)
		// .cacheOnDisc(true).considerExifParams(true).build();
		//
		// imageLoader.init(config);
		// ***************** end function save image ******************

		if (savedInstanceState == null) {
			fragmentStack = new Stack<Fragment>();

			uimainFragment = new FragmentUiMain();
			uimainFragment.registerForListener(mylistFm);

			fragmentManager = getSupportFragmentManager();
			FragmentTransaction ft = fragmentManager.beginTransaction();
			ft.add(R.id.li_content, uimainFragment);
			fragmentStack.push(uimainFragment);
			ft.commit();
		}

		fm_manage = getSupportFragmentManager();

		initTabMenuBottom(); // tab menu bottom
	}

	// ***************** tab menu bottom ******************
	public void initTabMenuBottom() {
		LinearLayout linear_main_frame = (LinearLayout) findViewById(R.id.linear_main_frame);
		li_menu_hide = (LinearLayout) findViewById(R.id.li_menu_hide);
		popup = (SlidingPanel) findViewById(R.id.popup_window);

		// Hide the popup initially.....
		popup.setVisibility(View.GONE);

		animShow = AnimationUtils.loadAnimation(this, R.animator.menu_show);
		animHide = AnimationUtils.loadAnimation(this, R.animator.menu_hide);

		popup.startAnimation(animHide);
		popup.setVisibility(View.GONE);

		li_menu_hide.setOnClickListener(new OnClickListener() {			
			@Override
			public void onClick(View v) {
				popup.setVisibility(View.VISIBLE);
				li_menu_hide.setVisibility(View.GONE);
				popup.startAnimation(animShow);
			}
		});
		
		linear_main_frame.setOnTouchListener(mActivitySwipeMotion);

	}
	
	// ******************* menu right touch ******************************
		ActivitySwipeMotion mActivitySwipeMotion = new ActivitySwipeMotion(this) {
			public void onSwipeDown() {
				popup.startAnimation(animHide);
				popup.setVisibility(View.GONE);
				li_menu_hide.setVisibility(View.VISIBLE);
			}

			public void onSwipeUp() {
//				li_menu_hide.setVisibility(View.GONE);
//				popup.setVisibility(View.VISIBLE);
//				popup.startAnimation(animShow);
			}
			
		};

	/**
	 * Slider menu item click listener
	 * */

	public void switchContent(Fragment fragment) {
		mContent = fragment;
		getSupportFragmentManager().beginTransaction()
				.replace(R.id.li_content, fragment).commit();
		// getSlidingMenu().showContent();
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		return null;
	}

	// /////////////////////////////////////////////
//	public static String convertInputStreamToString(InputStream inputStream)
//			throws IOException {
//		BufferedReader bufferedReader = new BufferedReader(
//				new InputStreamReader(inputStream));
//		String line = "";
//		String result = "";
//		while ((line = bufferedReader.readLine()) != null)
//			result += line;
//
//		inputStream.close();
//		return result;
//
//	}

	@Override
	public void onItemClickedListener(String valueClicked) {
		// TODO Auto-generated method stub

	}

	private static final int TIME_INTERVAL = 2000;
	private long mBackPressed;

	public void onBackPressed() {

		if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
			finish();
		} else {
			Toast.makeText(getBaseContext(),
					"กดปุ่มกลับอีกครั้งเพื่อปิดโปรแกรม", Toast.LENGTH_SHORT)
					.show();
		}

		mBackPressed = System.currentTimeMillis();

	}

}
