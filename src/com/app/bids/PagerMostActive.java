package com.app.bids;

import java.text.ParseException;
import java.util.List;
import java.util.Vector;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
import com.app.bids.R;

public class PagerMostActive extends Fragment {

	static Context context;
	public static View rootView;

	// view pager
	static ViewPager mPagerMain;
	// list contains fragments to instantiate in the viewpager
	List<Fragment> fragmentMain = new Vector<Fragment>();
	private PagerAdapter mPagerAdapterMain;
	
	// activity listener interface
	private OnPageListener pageListener;

	public interface OnPageListener {
		public void onPage1(String s);
	}

	// onAttach : set activity listener
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// if implemented by activity, set listener
		if (activity instanceof OnPageListener) {
			pageListener = (OnPageListener) activity;
		}
		// else create local listener (code never executed in this example)
		else
			pageListener = new OnPageListener() {
				@Override
				public void onPage1(String s) {
					Log.d("PAG1", "Button event from page 1 : " + s);
				}
			};
	}

	// onCreateView :
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		// fragment not when container null
		if (container == null) {
			return null;
		}

		// inflate view from layout
		rootView = (LinearLayout) inflater.inflate(
				R.layout.pager_mostactivevolume, container, false);

		return rootView;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// FragmentChangeActivity.SET_MENU_SELECT = "home";
		//
		// spn_select_date = 0;
		//
		// if (SplashScreen2.award1st_last == "") {
		// // Toast.makeText(getActivity(), "if if",
		// // Toast.LENGTH_SHORT).show();
		// load resp = new load();
		// resp.execute();
		// } else {
		// // initial show award in linear
		// initLinearAward();
		// }

		setPagerMainAward();
		
	}

	// ====================== Pager Main ========================
	private void setPagerMainAward() {
		// creating fragments and adding to list
		fragmentMain.removeAll(fragmentMain);
		fragmentMain.add(Fragment.instantiate(context, Pager1.class.getName()));
		fragmentMain.add(Fragment.instantiate(context,Pager2.class.getName()));
		fragmentMain.add(Fragment.instantiate(context,PagerDefault.class.getName()));

		// creating adapter and linking to view pager
		this.mPagerAdapterMain = new PagerAdapter(
				super.getFragmentManager(), fragmentMain);
		mPagerMain = (ViewPager) rootView.findViewById(R.id.vp_pager);

		mPagerMain.setAdapter(this.mPagerAdapterMain);

		mPagerMain.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Toast.makeText(getApplicationContext(), "sssss",
				// Toast.LENGTH_SHORT).show();
			}
		});

		mPagerMain.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent arg1) {
				v.getParent().requestDisallowInterceptTouchEvent(true);
				return false;
			}
		});
		mPagerMain.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				// LOTBANK = (mPagerMain.getCurrentItem()) + 1;
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				mPagerMain.getParent().requestDisallowInterceptTouchEvent(true);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

	}

	// the meat of switching the above fragment
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;
		if (getActivity() instanceof FragmentChangeActivity) {
			FragmentChangeActivity fca = (FragmentChangeActivity) getActivity();
			fca.switchContent(fragment);
		}
	}

}