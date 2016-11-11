package com.app.bids;

import java.io.IOException;
import java.text.ParseException;

import org.json.JSONArray;
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
import android.graphics.drawable.Drawable;
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
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class PagerWatchListDetailNews extends Fragment {

	static Context context;
	public static View rootView;

	// activity listener interface
	private OnPageListener pageListener;

	public static String linkNewsSelect = "";
//	public static JSONObject contentSocialSelect;

	public static String strArticleIdSelect;

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
				R.layout.pager_watchlist_detail_news, container, false);

		return rootView;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		if (UiWatchlistDetail.newsContentGetWatchlistNewsBySymbol != null) {
			initTabNews();
		} else {
			initLoadData();
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();

	}

	// ============== Load initLoadData =========================
	private void initLoadData() {
		loadAll resp = new loadAll();
		resp.execute();
	}

	public class loadAll extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;
		// ======= Ui Newds ========
		private JSONObject jsonGetWatchlistNewsBySymbol;
		private JSONObject jsonGetWatchlistNewsBySymbolSet;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progress.show();

		}

		@Override
		protected Void doInBackground(Void... params) {
			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// ======= Ui News ========
			String url_GetWatchlistNewsBySymbol = SplashScreen.url_bidschart
					+ "/service/v2/watchlistNewsBySymbol?symbol="
					+ FragmentChangeActivity.strSymbolSelect;

			// http://chart.bidschart.com/api/feed?provider=set&symbol=PTT
			String url_GetWatchlistNewsBySymbolSet = "http://chart.bidschart.com/api/feed?provider=set&symbol="
					+ FragmentChangeActivity.strSymbolSelect;
			try {
				// ======= Ui News ========
				jsonGetWatchlistNewsBySymbol = ReadJson
						.readJsonObjectFromUrl(url_GetWatchlistNewsBySymbol);

				jsonGetWatchlistNewsBySymbolSet = ReadJson
						.readJsonObjectFromUrl(url_GetWatchlistNewsBySymbolSet);
			} catch (IOException e1) {
				connectionError = true;
				jsonGetWatchlistNewsBySymbol = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonGetWatchlistNewsBySymbol = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonGetWatchlistNewsBySymbol = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (jsonGetWatchlistNewsBySymbol != null) {
					try {
						UiWatchlistDetail.newsContentGetWatchlistNewsBySymbol = jsonGetWatchlistNewsBySymbol
								.getJSONArray("data");

						UiWatchlistDetail.contentGetWatchlistNewsBySymbolSet = jsonGetWatchlistNewsBySymbolSet
								.getJSONArray("data");

						initTabNews();
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

	// ====================== set tab new ================
	public static TextView tv_tab_set, tv_tab_social, tv_tab_bids;
	public static int itemTabSelect = 1;

	private void initTabNews() {
		tv_tab_set = (TextView) rootView.findViewById(R.id.tv_tab_set);
		tv_tab_social = (TextView) rootView.findViewById(R.id.tv_tab_social);
		tv_tab_bids = (TextView) rootView.findViewById(R.id.tv_tab_bids);

		itemTabSelect = 1;
		initSetDataNews(); // set data

		tv_tab_set.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				itemTabSelect = 1;
				selectTabNews(1);
				initSetDataNews(); // set data
			}
		});
		tv_tab_social.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				itemTabSelect = 2;
				selectTabNews(2);
				initSetDataNews(); // set data
			}
		});
		tv_tab_bids.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				itemTabSelect = 3;
				selectTabNews(3);
				initSetDataNews(); // set data
			}
		});
	}

	// ============= set tab ===========
	public int selectTabNews(int numtab) {
		tv_tab_set.setBackgroundColor(Color.TRANSPARENT);
		tv_tab_social.setBackgroundColor(Color.TRANSPARENT);
		tv_tab_bids.setBackgroundColor(Color.TRANSPARENT);

		tv_tab_set.setTextColor(getResources().getColor(R.color.c_content));
		tv_tab_social.setTextColor(getResources().getColor(R.color.c_content));
		tv_tab_bids.setTextColor(getResources().getColor(R.color.c_content));

		if (numtab == 1) {
			tv_tab_set
					.setBackgroundResource(R.drawable.border_button_activeleft);
			tv_tab_set
					.setTextColor(getResources().getColor(R.color.bg_default));
		} else if (numtab == 2) {
			tv_tab_social
					.setBackgroundResource(R.drawable.border_button_activecenter);
			tv_tab_social.setTextColor(getResources().getColor(
					R.color.bg_default));
		} else if (numtab == 3) {
			tv_tab_bids
					.setBackgroundResource(R.drawable.border_button_activecenter);
			tv_tab_bids.setTextColor(getResources()
					.getColor(R.color.bg_default));
		}
		return numtab;
	}

	// ============== set news ===============
	private void initSetDataNews() {

		LinearLayout li_list = (LinearLayout) rootView
				.findViewById(R.id.li_list);
		li_list.removeAllViews();
		try {
			if (itemTabSelect != 1) {
				if (UiWatchlistDetail.newsContentGetWatchlistNewsBySymbol.length() > 0) {
					for (int i = 0; i < UiWatchlistDetail.newsContentGetWatchlistNewsBySymbol
							.length(); i++) {

						JSONObject jsoIndex = UiWatchlistDetail.newsContentGetWatchlistNewsBySymbol
								.getJSONObject(i);

						View view2 = ((Activity) context).getLayoutInflater()
								.inflate(R.layout.row_watchlist_news_bidschart,
										null);

						if (itemTabSelect == 1) {

						} else if (itemTabSelect == 2) {
							if (jsoIndex.getString("provider").equals(
									"facebook")) {
								view2 = ((Activity) context)
										.getLayoutInflater()
										.inflate(
												R.layout.row_watchlist_news_facebook,
												null);
							} else if (jsoIndex.getString("provider").equals(
									"pantip")) {
								view2 = ((Activity) context)
										.getLayoutInflater()
										.inflate(
												R.layout.row_watchlist_news_pantip,
												null);
							}

							final LinearLayout li_row = (LinearLayout) view2
									.findViewById(R.id.li_row);

							ImageView iv_content = (ImageView) view2
									.findViewById(R.id.iv_content);
							TextView tv_article_title = (TextView) view2
									.findViewById(R.id.tv_article_title);
							TextView tv_comments_count = (TextView) view2
									.findViewById(R.id.tv_comments_count);
							TextView tv_created_at = (TextView) view2
									.findViewById(R.id.tv_created_at);

							tv_article_title.setText(jsoIndex
									.getString("title"));
							tv_comments_count.setText(jsoIndex
									.getString("comment_count"));
							tv_created_at.setText(DateTimeCreate
									.DateDmyThaiCreate(jsoIndex
											.getString("created_at")));

							final int j = i;
							li_row.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									try {
										strArticleIdSelect = UiWatchlistDetail.newsContentGetWatchlistNewsBySymbol
												.getJSONObject(j).getString(
														"id");

//										contentSocialSelect = UiWatchlistDetail.contentGetWatchlistNewsBySymbol
//												.getJSONObject(j);
										linkNewsSelect = UiWatchlistDetail.newsContentGetWatchlistNewsBySymbol
												.getJSONObject(j).getString("link");
									} catch (JSONException e) {
										e.printStackTrace();
									}
									startActivity(new Intent(
											context,
											UiWatchListDetailNewsSocialSelect.class));
								}
							});

							if (jsoIndex.getString("provider").equals(
									"facebook")) {
								String strFacebook_id = jsoIndex.getJSONObject(
										"rss").getString("facebook_id");
								Log.v("strFacebook_id", "" + strFacebook_id);

								if ((jsoIndex.get("content")).equals("")) {
									Log.v("img content", "undefined undefined");
								} else {
									FragmentChangeActivity.imageLoader
											.displayImage(
													"https://graph.facebook.com/"
															+ strFacebook_id
															+ "/picture?type=normal",
													iv_content);
								}

							} else {
								iv_content
										.setBackgroundResource(R.drawable.img_pantip);
							}
							li_list.addView(view2);
						} else if (itemTabSelect == 3) {
							if (jsoIndex.getString("provider").equals("web")) {
								view2 = ((Activity) context)
										.getLayoutInflater()
										.inflate(
												R.layout.row_watchlist_news_bidschart,
												null);

								final LinearLayout li_row = (LinearLayout) view2
										.findViewById(R.id.li_row);

								ImageView iv_content = (ImageView) view2
										.findViewById(R.id.iv_content);

								TextView tv_article_title = (TextView) view2
										.findViewById(R.id.tv_article_title);
								TextView tv_views_count = (TextView) view2
										.findViewById(R.id.tv_views_count);
								TextView tv_likes_count = (TextView) view2
										.findViewById(R.id.tv_likes_count);
								TextView tv_comments_count = (TextView) view2
										.findViewById(R.id.tv_comments_count);
								TextView tv_created_at = (TextView) view2
										.findViewById(R.id.tv_created_at);

								if ((jsoIndex.get("content")).equals("")) {
									Log.v("img content", "undefined undefined");
								} else {
									FragmentChangeActivity.imageLoader
											.displayImage(
													"http://service.bidschart.com/"
															+ jsoIndex
																	.getString("content"),
													iv_content,
													FragmentChangeActivity.options);
								}

								tv_article_title.setText(jsoIndex
										.getString("article_title"));
								tv_views_count.setText(jsoIndex
										.getString("views_count"));
								tv_likes_count.setText(jsoIndex
										.getString("likes_count"));
								tv_comments_count.setText(jsoIndex
										.getString("comments_count"));
								tv_created_at.setText(DateTimeCreate
										.DateDmyThaiCreate(jsoIndex.getString(
												"created_at").toString()));

								final int j = i;
								li_row.setOnClickListener(new OnClickListener() {
									@Override
									public void onClick(View v) {
										try {
											strArticleIdSelect = UiWatchlistDetail.newsContentGetWatchlistNewsBySymbol
													.getJSONObject(j).get("id")
													.toString();
											// FragmentChangeActivity.contentGetArticleSelect
											// =
											// contentGetWatchlistNewsBySymbol.getJSONObject(j);
										} catch (JSONException e) {
											e.printStackTrace();
										}

										startActivity(new Intent(
												context,
												UiWatchListDetailNewsSelect.class));
									}
								});
								li_list.addView(view2);
							}
						}

					}
				}
			} else {
				if (UiWatchlistDetail.contentGetWatchlistNewsBySymbolSet.length() > 0) {
					for (int i = 0; i < UiWatchlistDetail.contentGetWatchlistNewsBySymbolSet
							.length(); i++) {

						JSONObject jsoIndex = UiWatchlistDetail.contentGetWatchlistNewsBySymbolSet
								.getJSONObject(i);

						View view2 = ((Activity) context).getLayoutInflater()
								.inflate(R.layout.row_watchlist_news_set,
										null);

//						if (jsoIndex.getString("provider").equals("facebook")) {
//							view2 = ((Activity) context)
//									.getLayoutInflater()
//									.inflate(
//											R.layout.row_watchlist_news_facebook,
//											null);
//						} else if (jsoIndex.getString("provider").equals(
//								"pantip")) {
//							view2 = ((Activity) context).getLayoutInflater()
//									.inflate(
//											R.layout.row_watchlist_news_pantip,
//											null);
//						}
						
						final LinearLayout li_row = (LinearLayout) view2
								.findViewById(R.id.li_row);

						ImageView iv_content = (ImageView) view2
								.findViewById(R.id.iv_content);
						TextView tv_article_title = (TextView) view2
								.findViewById(R.id.tv_article_title);
						TextView tv_created_at = (TextView) view2
								.findViewById(R.id.tv_created_at);

						tv_article_title.setText(jsoIndex.getString("title"));
						tv_created_at.setText(DateTimeCreate
								.DateDmyWatchlistPortfolio(jsoIndex
										.getString("local_date")));
						
						String url_link = "";
						if (jsoIndex.getString("owner").equals(
								"www.settrade.com")) {
							url_link = jsoIndex.getString("link");
							iv_content
							.setBackgroundResource(R.drawable.img_settrade);
						} else {
							url_link = "www.set.or.th"+jsoIndex.getString("link");
							iv_content
									.setBackgroundResource(R.drawable.img_set);
						}

						final String url_link2 = url_link;
						li_row.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								linkNewsSelect = url_link2;
								startActivity(new Intent(context,
										UiWatchListDetailNewsSocialSelect.class));
							}
						});
						li_list.addView(view2);
					}
				}
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
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