package com.app.bids;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import com.app.bids.R;

public class MyPagerAdapter extends FragmentPagerAdapter implements
		ViewPager.OnPageChangeListener {

	private MyLinearLayout cur = null;
	private MyLinearLayout next = null;
	private Context context;
	private FragmentManager fm;
	private float scale;
	private int caroselId;

	// public final static int PAGES = 5;

	public MyPagerAdapter(Context context, FragmentManager fm, int caroselId) {
		super(fm);
		this.fm = fm;
		this.caroselId = caroselId;
		this.context = context;
		// Log.v("MyPagerAdapter","MyPagerAdapter in");
	}

	@Override
	public Fragment getItem(int position) {
		// Log.v("getItem","getItem in");
		// make the first pager bigger than others
		if (position == AttributeBegin.FIRST_PAGE)
			scale = AttributeBegin.BIG_SCALE;
		else
			scale = AttributeBegin.SMALL_SCALE;

		position = position % AttributeBegin.PAGES;
		return MyFragment.newInstance(context, position, scale);
	}

	@Override
	public int getCount() {
		// Log.v("getCount","getCount in");
		return AttributeBegin.PAGES * AttributeBegin.LOOPS;
	}

	@Override
	public void onPageScrolled(int position, float positionOffset,
			int positionOffsetPixels) {
		// Log.v("onPageScrolled","onPageScrolled in");
		if (positionOffset >= 0f && positionOffset <= 1f) {
			cur = getRootView(position);
			next = getRootView(position + 1);

			cur.setScaleBoth(AttributeBegin.BIG_SCALE
					- AttributeBegin.DIFF_SCALE * positionOffset);
			next.setScaleBoth(AttributeBegin.SMALL_SCALE
					+ AttributeBegin.DIFF_SCALE * positionOffset);

//			Log.v("scale", "" + scale);

		}

		// Log.v("value", " C:" + cur + " N:" + next + " B:"
		// + AttributeBegin.BIG_SCALE + " D:"
		// + AttributeBegin.DIFF_SCALE + " S:"
		// + AttributeBegin.SMALL_SCALE + "");
	}

	@Override
	public void onPageSelected(int position) {
		// Log.v("onPageSelected","onPageSelected in");
	}

	@Override
	public void onPageScrollStateChanged(int state) {
		// Log.v("onPageScrollStateChanged","onPageScrollStateChanged in");
	}

	private MyLinearLayout getRootView(int position) {
		// Log.v("getRootView","getRootView in");
		return (MyLinearLayout) fm
				.findFragmentByTag(this.getFragmentTag(position)).getView()
				.findViewById(R.id.root);
	}

	private String getFragmentTag(int position) {
		// Log.v("getFragmentTag","getFragmentTag in");
		return "android:switcher:" + caroselId + ":" + position;
	}

}
