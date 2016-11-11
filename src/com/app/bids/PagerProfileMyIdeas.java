package com.app.bids;

import java.io.IOException;
import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;
import com.app.bids.PagerWatchListDetailNews.loadAll;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

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

public class PagerProfileMyIdeas extends Fragment {

	static Context context;
	public static View rootView;

	// activity listener interface
	private OnPageListener pageListener;

	public static JSONArray contentGetMyIdeas = null;

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

		private JSONObject jsonGetMyIdeas;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progress.show();

		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();

			String url_GetMyIdeas = SplashScreen.url_bidschart+"/service/getMyIdeas?user_id="
					+ SplashScreen.userModel.user_id;
			
//			String url_GetMyIdeas = FragmentChangeActivity.url_bidschart+"/service/getMyIdeas?user_id=53";

			try {
				jsonGetMyIdeas = ReadJson.readJsonObjectFromUrl(url_GetMyIdeas);
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
				if (jsonGetMyIdeas != null) {
					try {

						contentGetMyIdeas = jsonGetMyIdeas
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

		if (contentGetMyIdeas.length() > 0) {
			for (int i = 0; i < contentGetMyIdeas.length(); i++) {
				View view2 = ((Activity) context).getLayoutInflater().inflate(
						R.layout.row_profile_ideas, null);

				final LinearLayout li_row = (LinearLayout) view2
						.findViewById(R.id.li_row);

				final LinearLayout li_content = (LinearLayout) view2
						.findViewById(R.id.li_content);
				TextView tv_username = (TextView) view2
						.findViewById(R.id.tv_username);
				TextView tv_created_at = (TextView) view2
						.findViewById(R.id.tv_created_at); // 5เดิอนที่แล้ว2015-07-15

				TextView tv_article_title = (TextView) view2
						.findViewById(R.id.tv_article_title);

				TextView tv_symbol = (TextView) view2
						.findViewById(R.id.tv_symbol);
				TextView tv_likes_count = (TextView) view2
						.findViewById(R.id.tv_likes_count);
				TextView tv_comments_count = (TextView) view2
						.findViewById(R.id.tv_comments_count);
				TextView tv_views_count = (TextView) view2
						.findViewById(R.id.tv_views_count);

				try {
					tv_username.setText(contentGetMyIdeas.getJSONObject(i)
							.get("username").toString());
					tv_symbol.setText(contentGetMyIdeas.getJSONObject(i)
							.get("symbol").toString());
					// tv_article_title.setText(contentGetMyIdeas
					// .getJSONObject(i).get("article_title").toString());
					tv_likes_count.setText(contentGetMyIdeas.getJSONObject(i)
							.get("likes_count").toString()
							+ " likes");
					tv_comments_count.setText(contentGetMyIdeas
							.getJSONObject(i).get("comments_count").toString()
							+ " comments");
					tv_views_count.setText(contentGetMyIdeas.getJSONObject(i)
							.get("views_count").toString()
							+ " views");
					// 1 เดือน 2015-10-01
					try {
						String strDateAgo = ""
								+ DateTimeAgo
										.CalAgoTime2(contentGetMyIdeas
												.getJSONObject(i)
												.get("post_create_datetime")
												.toString());
						String strDateYmd = ""
								+ (contentGetMyIdeas.getJSONObject(i).get(
										"post_create_datetime").toString())
										.split(" ")[0];
						tv_created_at.setText(strDateAgo + "  " + strDateYmd);
					} catch (ParseException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					if ((contentGetMyIdeas.getJSONObject(i).get("image"))
							.equals("")) {
					} else {
						FragmentChangeActivity.imageLoader.loadImage(
								SplashScreen.url_bidschart+"/snapshot/"
										+ contentGetMyIdeas.getJSONObject(i).get(
												"image"),
								FragmentChangeActivity.options,
								new SimpleImageLoadingListener() {
									@Override
									public void onLoadingComplete(
											String imageUri, View view,
											Bitmap loadedImage) {
										BitmapDrawable background = new BitmapDrawable(
												loadedImage);
										li_content
												.setBackgroundDrawable(background);
										super.onLoadingComplete(imageUri, view,
												loadedImage);
									}
								});
					}

					final int j = i;
					final String strPostId = contentGetMyIdeas.getJSONObject(i).get("views_count").toString();
					final String strPostSymbol = contentGetMyIdeas.getJSONObject(i).get("symbol").toString();
					li_row.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							try {
								PagerProfile.contentIdeasSelect = contentGetMyIdeas.getJSONObject(j);
								
								PagerProfile.strPostId = strPostId;
								PagerProfile.strSymbol = strPostSymbol;
								startActivity(new Intent(context,
										UiProfileIdeasSelect.class));
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
}