package com.app.bids;

import java.io.IOException;
import java.text.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
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

public class DialogWatchListCategoriesIndustry {

	private static Dialog dialog;
	private static Context context;

	// FragmentChangeActivity.url_bidschart+"/watchlist?platform=mobile&user=10
	// public static JSONArray contentGetWatchlists = null;

	public DialogWatchListCategoriesIndustry(Context context2) {
		this.context = context2;
		this.dialog = new Dialog(context2);

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
		dialog.setContentView(R.layout.dialog_watchlist_categories_select);

	}

	public static void show() {
		dialog.show();

		initSetData();
		
		// set width height dialog
		DisplayMetrics displaymetrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(displaymetrics);

		int ScreenHeight = displaymetrics.heightPixels;
		int ScreenWidth = displaymetrics.widthPixels;

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.width = (int) (ScreenWidth * 1);
		lp.height = (int) (ScreenHeight * 1);
		dialog.getWindow().setAttributes(lp);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
	}

	public static void dissmiss() {
		dialog.dismiss();
	}

	// static DialogWatchListCategoriesIndustrySector dialogIndustrySector;
	// public static JSONObject jsoIndustrySetSector = null;

	public static void initSetData() {

		((LinearLayout) dialog.findViewById(R.id.li_back))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
		((TextView) dialog.findViewById(R.id.tv_close))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						
						dialog.dismiss();
						DialogWatchListCategories.dissmiss();
					}
				});
		((TextView) dialog.findViewById(R.id.tv_title)).setText("Industry");

		try {
			LinearLayout li_list = (LinearLayout) dialog
					.findViewById(R.id.li_list);
			li_list.removeAllViews();

			for (int i = 0; i < FragmentChangeActivity.contentGetIndustrySetSector
					.length(); i++) {
				View viewData = ((Activity) context).getLayoutInflater()
						.inflate(R.layout.row_watchlist_categories, null);

				final int j = i;
				JSONObject jsaIndustrySetSectorSelect = FragmentChangeActivity.contentGetIndustrySetSector
						.getJSONObject(i);

				LinearLayout row_list = (LinearLayout) viewData
						.findViewById(R.id.row_list);
				TextView tv_name = (TextView) viewData
						.findViewById(R.id.tv_name);
				tv_name.setText(jsaIndustrySetSectorSelect
						.getString("industry"));
				row_list.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						try {
							PagerWatchList.dialogJsoIndustrySetSector = FragmentChangeActivity.contentGetIndustrySetSector
									.getJSONObject(j);

//							DialogWatchListCategoriesIndustrySector dialogIndustrySector = new DialogWatchListCategoriesIndustrySector(
//									context);
//							dialogIndustrySector.show();
							
							DialogWatchListCategoriesIndustrySector.show();
							
							// Log.v("PagerWatchList.dialogJsoIndustrySetSector",
							// ""+PagerWatchList.dialogJsoIndustrySetSector);

						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				});

				li_list.addView(viewData);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected static void switchFragment(PagerWatchList fragment) {
		if (context == null)
			return;
		if (context instanceof FragmentChangeActivity) {
			FragmentChangeActivity fca = (FragmentChangeActivity) context;
			fca.switchContent(fragment);
		}
	}

	// the meat of switching the above fragment
	// public static void switchFragment(Fragment fragment) {
	// if (context == null)
	// return;
	// if (context instanceof FragmentChangeActivity) {
	// FragmentChangeActivity fca = (FragmentChangeActivity) getActivity();
	// fca.switchContent(fragment);
	// }
	// }

}