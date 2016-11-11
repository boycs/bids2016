package com.app.bids;

import java.io.IOException;
import java.text.ParseException;
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
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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

public class PagerWatchListCategoriesIndustrySector extends Fragment {

	static Context context;
	public static View rootView;

	// activity listener interface
	private OnPageListener pageListener;

	// FragmentChangeActivity.url_bidschart+"/watchlist?platform=mobile&user=10
	public static JSONArray contentGetWatchlists = null;

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
				R.layout.pager_watchlist_categories_select, container, false);

		return rootView;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		((LinearLayout) rootView.findViewById(R.id.li_back))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						switchFragment(new PagerWatchListCategoriesIndustry());
					}
				});
		((TextView) rootView.findViewById(R.id.tv_close))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						switchFragment(new PagerWatchList());
					}
				});

		initSetData();
	}

	private void initSetData() {
		try {
			LinearLayout li_list = (LinearLayout) rootView
					.findViewById(R.id.li_list);
			li_list.removeAllViews();

			final String strIndustry = PagerWatchListCategoriesIndustry.jsoIndustrySetSector.getString("industry");
			((TextView) rootView.findViewById(R.id.tv_title)).setText(strIndustry);
			JSONArray jsaSector = PagerWatchListCategoriesIndustry.jsoIndustrySetSector
					.getJSONArray("sector");

			for (int i = 0; i <= jsaSector.length(); i++) {
				View viewData = ((Activity) context).getLayoutInflater()
						.inflate(R.layout.row_watchlist_categories, null);

				TextView tv_name = (TextView) viewData
						.findViewById(R.id.tv_name);
				
				final int indexJ = i;
				if (i == 0) {
					tv_name.setText("All");
					tv_name.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							FragmentChangeActivity.strWatchlistCategory = "industry";
							FragmentChangeActivity.strWatchlistCategorySelect = strIndustry;
							FragmentChangeActivity.strIndustrySelect = strIndustry;
							switchFragment(new PagerWatchList());
						}
					});
				} else {
					final String strSelect = jsaSector.get(indexJ-1).toString();
					tv_name.setText(strSelect);
					tv_name.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							FragmentChangeActivity.strWatchlistCategory = "industry";
							FragmentChangeActivity.strWatchlistCategorySelect = strIndustry;
							FragmentChangeActivity.strIndustrySelect = strSelect;
							switchFragment(new PagerWatchList());
						}
					});
				}
				li_list.addView(viewData);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
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