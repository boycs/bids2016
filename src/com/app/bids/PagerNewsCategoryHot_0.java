package com.app.bids;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;
import com.app.bids.R;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

public class PagerNewsCategoryHot_0 extends Fragment {

	static Context context;
	public static View rootView;

	// activity listener interface
	private OnPageListener pageListener;

	public MyPagerAdapter adapterCorousel;

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
		rootView = (LinearLayout) inflater.inflate(R.layout.row_news,
				container, false);

		// initial All Main
		initSetData();

		return rootView;

	}

	// ====================== Init set data ========================
	private void initSetData() {
		
//		TextView marque = (TextView) rootView.findViewById(R.id.marque_scrolling_text);
//        marque.setSelected(true);
		
		final LinearLayout li_content = (LinearLayout) rootView
				.findViewById(R.id.li_content);
		final TextView tv_symbol = (TextView) rootView
				.findViewById(R.id.tv_symbol);
		final TextView tv_article_title = (TextView) rootView
				.findViewById(R.id.tv_article_title);

		final TextView tv_created_at = (TextView) rootView
				.findViewById(R.id.tv_created_at);
		final TextView tv_views_count = (TextView) rootView
				.findViewById(R.id.tv_views_count);
		final TextView tv_likes_count = (TextView) rootView
				.findViewById(R.id.tv_likes_count);
		final TextView tv_comments_count = (TextView) rootView
				.findViewById(R.id.tv_comments_count);

		try {
			tv_symbol.setText(""
					+ AttributeBegin.contentGetArticleByCategoryHot.getJSONObject(0)
							.get("symbol"));
			tv_article_title.setText(""
					+ AttributeBegin.contentGetArticleByCategoryHot.getJSONObject(0)
							.get("article_title"));

			// tv_created_at.setText(""
			// + AttributeBegin.contentGetArticleByCategoryHot.getJSONObject(0)
			// .get("created_at"));
			tv_views_count.setText(""
					+ AttributeBegin.contentGetArticleByCategoryHot.getJSONObject(0)
							.get("views_count"));
			tv_likes_count.setText(""
					+ AttributeBegin.contentGetArticleByCategoryHot.getJSONObject(0)
							.get("likes_count"));
			tv_comments_count.setText(""
					+ AttributeBegin.contentGetArticleByCategoryHot.getJSONObject(0)
							.get("comments_count"));

			// 1 เดือน 2015-10-01
			try {
				String strDateAgo = ""
						+ DateTimeAgo
								.CalAgoTime2(AttributeBegin.contentGetArticleByCategoryHot
										.getJSONObject(0).get("created_at")
										.toString());
				String strDateYmd = ""
						+ (AttributeBegin.contentGetArticleByCategoryHot
								.getJSONObject(0).get("created_at").toString())
								.split(" ")[0];
				tv_created_at.setText(strDateAgo + "  " + strDateYmd);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (AttributeBegin.contentGetArticleByCategoryHot.getJSONObject(0)
					.get("content_mode").equals("image")) {
				if ((AttributeBegin.contentGetArticleByCategoryHot.getJSONObject(0)
						.get("content"))
						.equals("http://service.bidschart.com/undefined")) {
				} else {
					FragmentChangeActivity.imageLoader.loadImage(""
							+ AttributeBegin.contentGetArticleByCategoryHot
									.getJSONObject(0).get("content"),
							FragmentChangeActivity.options,
							new SimpleImageLoadingListener() {
								@Override
								public void onLoadingComplete(String imageUri,
										View view, Bitmap loadedImage) {
									BitmapDrawable background = new BitmapDrawable(
											loadedImage);
									li_content
											.setBackgroundDrawable(background);
									super.onLoadingComplete(imageUri, view,
											loadedImage);
								}
							});
				}
			}

			li_content.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						AttributeBegin.contentGetNewsSelect = AttributeBegin.contentGetArticleByCategoryHot
								.getJSONObject(0);
						startActivity(new Intent(context, UiArticleSelect.class));
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			});

		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

}
