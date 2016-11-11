package com.app.bids;

import java.io.IOException;
import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;
import com.app.bids.PagerWatchListDetailNews.loadAll;

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

public class PagerProfileFollower extends Fragment {

	static Context context;
	public static View rootView;

	// activity listener interface
	private OnPageListener pageListener;

	public static JSONArray contentGetFollowerByUserId = null;

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
				R.layout.pager_profile_lists, container, false);

		// initial Data
		initLoadData();

		return rootView;

	}

	// ============== Load initLoadData =========================
	private void initLoadData() {
		loadAll resp = new loadAll();
		resp.execute();
	}

	public class loadAll extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		private JSONObject jsonGetFollowerByUserId;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progress.show();

		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();

			String url_GetFollowerByUserId = SplashScreen.url_bidschart+"/service/getFollowerByUserId?user_id="
					+ SplashScreen.userModel.user_id;
			try {
				jsonGetFollowerByUserId = ReadJson
						.readJsonObjectFromUrl(url_GetFollowerByUserId);
			} catch (IOException e1) {
				connectionError = true;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (connectionError == false) {
				if (jsonGetFollowerByUserId != null) {
					try {
						contentGetFollowerByUserId = null;
						contentGetFollowerByUserId = jsonGetFollowerByUserId
								.getJSONArray("dataAll");

						initSetData();

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.v("json null", "jsonGetArticleTitleByType null");
				}

			} else {
				Log.v("json newslist null", "jsonGetArticleTitleByType null");
			}
		}
	}

	// ========= set data =========
	private void initSetData() {
		LinearLayout li_list = (LinearLayout) rootView
				.findViewById(R.id.li_list);
		li_list.removeAllViews();

		if (contentGetFollowerByUserId.length() > 0) {
			for (int i = 0; i < contentGetFollowerByUserId.length(); i++) {
				View view2 = ((Activity) context).getLayoutInflater().inflate(
						R.layout.row_profile_follow, null);

				final LinearLayout li_row = (LinearLayout) view2
						.findViewById(R.id.li_row);

				LinearLayout li_content = (LinearLayout) view2
						.findViewById(R.id.li_content);
				TextView tv_username = (TextView) view2
						.findViewById(R.id.tv_username);
				TextView tv_follow_count = (TextView) view2
						.findViewById(R.id.tv_follow_count);
				TextView tv_ideas_count = (TextView) view2
						.findViewById(R.id.tv_ideas_count);
				final ImageView img_follow = (ImageView) view2
						.findViewById(R.id.img_follow);

				try {
					tv_username.setText(contentGetFollowerByUserId
							.getJSONObject(i).get("username").toString());
					tv_follow_count.setText(contentGetFollowerByUserId
							.getJSONObject(i).get("followers").toString()
							+ " Followers");
					tv_ideas_count.setText(contentGetFollowerByUserId
							.getJSONObject(i).get("ideas_views").toString()
							+ " Ideas");
					if ((contentGetFollowerByUserId.getJSONObject(i).get(
							"follow").toString()).equals("true")) {
						img_follow
								.setBackgroundResource(R.drawable.icon_uncheck);
					} else {
						img_follow.setBackgroundResource(R.drawable.icon_check);
					}

					final int j = i;
					img_follow.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							try {
								// follow
								FollowProfile
										.sendFollow(
												SplashScreen.userModel.user_id,
												contentGetFollowerByUserId
														.getJSONObject(j)
														.get("follower_id")
														.toString());
								// initial Data
								initLoadData();
							} catch (JSONException e) {
								e.printStackTrace();
							}
						}
					});

				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				li_list.addView(view2);
			}
		}
	}

	// ============== send follow =============
	private void sendFollow() {

	}

}