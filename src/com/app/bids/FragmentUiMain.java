package com.app.bids;

import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.URLEncoder;
import java.util.Date;
import java.util.List;
import java.util.Vector;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.R.color;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.Html;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import com.app.bids.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class FragmentUiMain extends Fragment {
	// this Fragment will be called from MainActivity
	public FragmentUiMain() {
	}

	static Context context;
	public static View rootView;

	List<Fragment> fragments = new Vector<Fragment>();
	// page adapter between fragment list and view pager
	private PagerAdapter mPagerAdapter;
	// view pager
	private ViewPager mPager;

	// **********************
	View view;
	ListView listView;
	// private ListAdapter adapter;
	private MyListFragmentListener listener;

	public static String type_company = "HAPPY";
	public static JSONObject contentPromotionSelect = null;

	// http://lotto.manaattempted.com/service/promotion?company=dtac&timestamp=1439121293
	public static JSONObject jsoPromotionList = null;
	public static JSONArray contentGetDataLottos = null;
	public static JSONObject jsonGetPromotion = null;
	public static JSONObject jsonGetPromotionTopUp = null;
	public static JSONObject jsonGetPromotionMonthly = null;

	public int selectTitle = 0;

	public interface MyListFragmentListener {
		public void onItemClickedListener(String valueClicked);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();

		// fragment not when container null
		if (container == null) {
			return null;
		}

		rootView = inflater.inflate(R.layout.fragment_home, container, false);

		initPager(); // init pager
		
		return rootView;
	}

	// ============== init pager =========================
	private void initPager() {
		// creating fragments and adding to list
		fragments
				.add(Fragment.instantiate(context, PagerHomeAll.class.getName()));
//		fragments
//				.add(Fragment.instantiate(context, Pager1.class.getName()));

		// creating adapter and linking to view pager
		this.mPagerAdapter = new PagerAdapter(super.getChildFragmentManager(),
				fragments);
		mPager = (ViewPager) rootView.findViewById(R.id.pager_main);
		mPager.setAdapter(this.mPagerAdapter);

		// Slide Pager
		mPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {

				selectTitle = mPager.getCurrentItem();

				// handlerTintMenu.sendEmptyMessage((mPager.getCurrentItem()) +
				// 1);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				mPager.getParent().requestDisallowInterceptTouchEvent(true);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});
	}

	public void registerForListener(MyListFragmentListener mylistFm) {
		this.listener = mylistFm;
	}

	private Fragment mContent;

	public void switchContent(Fragment fragment) {
		mContent = fragment;
		getChildFragmentManager().beginTransaction()
				.replace(R.id.linear_content_frame, fragment).commit();
	}

}