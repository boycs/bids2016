package com.app.bids;

import java.io.IOException;
import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;
import com.app.bids.PagerWatchlistDetail.loadData;
import com.app.bids.UiWatchListDetailNewsSelect.loadAll;
import com.app.model.login.FacebookLoginActivity;
import com.app.model.login.LoginDialog;
import com.app.model.login.LoginDialog2;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class UiProfileIdeasSelect extends Activity {

	public static JSONObject contentGetPostSelect = null;
	public static JSONArray contentGetSymbolSelect = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ui_ideas_select);

		// close
		((TextView) findViewById(R.id.tv_close)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		loadDataDetail();
		// loadDataPostId();

	}

	// ====================== Initial View ========================
	private boolean ckLikePost = true;
	boolean ckHideShow = false;
	private String article_id, comments_id;
	public static TextView tv_bottom_like;
	public static ImageView img_bottom_like, img_like;

	private void initView() {
		try {
			LinearLayout li_bottom_comment = (LinearLayout) findViewById(R.id.li_bottom_comment);
			LinearLayout li_bottom_like = (LinearLayout) findViewById(R.id.li_bottom_like);
			img_bottom_like = (ImageView) findViewById(R.id.img_bottom_like);
			tv_bottom_like = (TextView) findViewById(R.id.tv_bottom_like);

			// show hide
			final LinearLayout li_show_hide = (LinearLayout) findViewById(R.id.li_show_hide);
			final ImageView img_show_hide = (ImageView) findViewById(R.id.img_show_hide);

			li_show_hide.setVisibility(View.GONE);
			img_show_hide.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (ckHideShow == false) {
						ckHideShow = true;
						li_show_hide.setVisibility(View.VISIBLE);
						img_show_hide
								.setBackgroundResource(R.drawable.icon_dopdowe_up);
					} else {
						ckHideShow = false;
						li_show_hide.setVisibility(View.GONE);
						img_show_hide
								.setBackgroundResource(R.drawable.icon_dopdowe_down);
					}
				}
			});

			// data
			final LinearLayout li_content = (LinearLayout) findViewById(R.id.li_content);
			TextView tv_username = (TextView) findViewById(R.id.tv_username);
			TextView tv_created_at = (TextView) findViewById(R.id.tv_created_at); // 5เดิอนที่แล้ว2015-07-15

			TextView tv_article_title = (TextView) findViewById(R.id.tv_article_title);

			TextView tv_symbol_ = (TextView) findViewById(R.id.tv_symbol_);
			TextView tv_likes_count = (TextView) findViewById(R.id.tv_likes_count);
			TextView tv_comments_count = (TextView) findViewById(R.id.tv_comments_count);
			TextView tv_views_count = (TextView) findViewById(R.id.tv_views_count);
			WebView wv_des = (WebView) findViewById(R.id.wv_des);
			ImageView img_like = (ImageView) findViewById(R.id.img_like);

			tv_symbol_.setText(PagerProfile.contentIdeasSelect.get("symbol")
					.toString());
			// tv_article_title.setText(PagerProfile.contentIdeasSelect.get(
			// "article_title").toString());
			tv_views_count.setText(PagerProfile.contentIdeasSelect.get(
					"views_count").toString()
					+ " views");
			tv_likes_count.setText(PagerProfile.contentIdeasSelect.get(
					"likes_count").toString()
					+ " likes");
			tv_comments_count.setText(PagerProfile.contentIdeasSelect.get(
					"comments_count").toString()
					+ " comments");

			// Toast.makeText(getApplicationContext(),
			// ""+contentGetPostSelect.get("this_like"), 0).show();
			if ((PagerProfile.contentIdeasSelect.get("this_like").toString())
					.equals("YES")) {
				img_like.setBackgroundResource(R.drawable.icon_checklike);
				tv_likes_count.setTextColor(getResources().getColor(
						R.color.c_success));
				tv_bottom_like.setTextColor(getResources().getColor(
						R.color.c_success));
				img_bottom_like
						.setBackgroundResource(R.drawable.icon_checklike);
				li_bottom_like
						.setBackgroundResource(R.drawable.border_like_success);
			}

			// 1 เดือน 2015-10-01
			try {
				String strDateAgo = ""
						+ DateTimeAgo
								.CalAgoTime2(PagerProfile.contentIdeasSelect.getString("post_create_datetime"));
				String strDateYmd = ""
						+ (DateTimeCreate.DateDmyThaiCreate(PagerProfile.contentIdeasSelect.getString("post_create_datetime")));
				tv_created_at.setText(strDateAgo + "  " + strDateYmd);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if ((PagerProfile.contentIdeasSelect.get("image")).equals("")) {
			} else {
				FragmentChangeActivity.imageLoader.loadImage(
						SplashScreen.url_bidschart+"/snapshot/"
								+ PagerProfile.contentIdeasSelect.get("image"),
						FragmentChangeActivity.options,
						new SimpleImageLoadingListener() {
							@Override
							public void onLoadingComplete(String imageUri,
									View view, Bitmap loadedImage) {
								BitmapDrawable background = new BitmapDrawable(
										loadedImage);
								li_content.setBackgroundDrawable(background);
								super.onLoadingComplete(imageUri, view,
										loadedImage);
							}
						});
			}

			// like
			li_bottom_like.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (SplashScreen.userModel.user_id != "") {
						try {
							article_id = ""
									+ PagerProfile.contentIdeasSelect.get("post_id");

							// like
							CommentLikePost.sendLikePost(article_id,
									SplashScreen.userModel.user_id);

							loadDataPostId(); // load background

						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else {
						Toast.makeText(getApplicationContext(), "Login", 0)
						.show();
						LoginDialog.show();
					}
				}
			});

			// comment
			li_bottom_comment.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(getApplicationContext(),
							UiProfileIdeasSelectComments.class));
				}
			});
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

	}

	// ====================== load post id ========================
	private void loadDataPostId() {
		loadPostId resp = new loadPostId();
		resp.execute();
	}

	public class loadPostId extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;
		private JSONObject jsonGetPostById;

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
			String url_GetPostById;
			try {

				// FragmentChangeActivity.url_bidschart+"/service/getPostCommentReplyByIdPost?post_id=50&user_id=1

				url_GetPostById = SplashScreen.url_bidschart+"/service/getPostById?post_id="
						+ PagerProfile.strPostId
						+ "&user_id="
						+ SplashScreen.userModel.user_id
						+ "&timestamp=" + timestamp;

				jsonGetPostById = ReadJson
						.readJsonObjectFromUrl(url_GetPostById);

			} catch (IOException e1) {
				connectionError = true;
				jsonGetPostById = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonGetPostById = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonGetPostById = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (jsonGetPostById != null) {
					try {

						contentGetPostSelect = jsonGetPostById
								.getJSONObject("dataAll");

						initView();
//						loadDataDetail();

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.v("json null", "jsonGetPostTitleByType null");
				}

			} else {
				Log.v("json newslist null", "jsonGetPostTitleByType null");
			}
		}
	}

	// ============== Load detail =============
	public void loadDataDetail() {
		loadDetail resp = new loadDetail();
		resp.execute();
	}

	public class loadDetail extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;
		// ======= json ========
		private JSONObject jsonGetDetail;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progress.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// ======= url ========
			String url_GetDetail = SplashScreen.url_bidschart+"/service/v2/watchlistSymbol?user_id="
					+ SplashScreen.userModel.user_id
					+ "&symbol="
					+ PagerProfile.strSymbol
					+ "&timestamp="
					+ timestamp;

			try {
				// ======= Ui Home ========
				jsonGetDetail = ReadJson.readJsonObjectFromUrl(url_GetDetail);
			} catch (IOException e1) {
				connectionError = true;
				jsonGetDetail = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonGetDetail = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonGetDetail = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (jsonGetDetail != null) {
					try {
						// get content
						contentGetSymbolSelect = jsonGetDetail
								.getJSONArray("dataAll");

						setDataDetail();

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.v("json newslist null", "newslist null");
				}
			} else {
			}
		}
	}

	// ====================== setDataDetail ================
	private void setDataDetail() {
		try {
			if (contentGetSymbolSelect.length() > 0) {
				final JSONObject objDetail = contentGetSymbolSelect
						.getJSONObject(0);
				// main
				TextView tv_watch = (TextView) findViewById(R.id.tv_watch);
				TextView tv_symbol = (TextView) findViewById(R.id.tv_symbol);
				TextView tv_last_trade = (TextView) findViewById(R.id.tv_last_trade);
				TextView tv_symbol_name_eng = (TextView) findViewById(R.id.tv_symbol_name_eng);
				TextView tv_percenchange = (TextView) findViewById(R.id.tv_percenchange);
				ImageView img_updown = (ImageView) findViewById(R.id.img_updown);

				// main
				tv_symbol
						.setText(objDetail.getString("symbol_name").toString());
				tv_last_trade.setText(objDetail.getString("last_trade")
						.toString());
				tv_symbol_name_eng.setText(objDetail.getString(
						"symbol_fullname_eng").toString());
				tv_percenchange.setText("(" + objDetail.getString("change")
						+ ")" + objDetail.getString("percentChange") + "%");
				// main color
				String strColor = objDetail.getString("change").toString();
				tv_last_trade.setTextColor(getResources().getColor(
						FunctionSetBg.setColor(strColor)));
				tv_percenchange.setTextColor(getResources().getColor(
						FunctionSetBg.setColor(strColor)));
				img_updown.setImageResource(FunctionSetBg.setImgUpDown(strColor));

				// ---today
				TextView tv_high = (TextView) findViewById(R.id.tv_high);
				TextView tv_low = (TextView) findViewById(R.id.tv_low);
				TextView tv_volume = (TextView) findViewById(R.id.tv_volume);
				TextView tv_open = (TextView) findViewById(R.id.tv_open);
				TextView tv_prev_close = (TextView) findViewById(R.id.tv_prev_close);

				tv_high.setText(objDetail.getString("high").toString());
				tv_low.setText(objDetail.getString("low").toString());
				tv_volume.setText(objDetail.getString("volume").toString()
						.toString());
				tv_open.setText(objDetail.getString("open").toString());
				tv_prev_close.setText(objDetail.getString("prev_close")
						.toString());

				// ---summary
				TextView tv_52whigh = (TextView) findViewById(R.id.tv_52whigh);
				TextView tv_52wlow = (TextView) findViewById(R.id.tv_52wlow);
				TextView tv_1yrtn = (TextView) findViewById(R.id.tv_1yrtn);

				tv_52whigh.setText(objDetail.getString("high52W").toString());
				tv_52wlow.setText(objDetail.getString("low52W").toString());
				// tv_1yrtn.setText(objDetail.getString("").toString().toString());

				// ---fandamental
				TextView tv_mktcap = (TextView) findViewById(R.id.tv_mktcap);
				TextView tv_p_bv = (TextView) findViewById(R.id.tv_p_bv);
				TextView tv_eps = (TextView) findViewById(R.id.tv_eps);
				TextView tv_p_e = (TextView) findViewById(R.id.tv_p_e);
				TextView tv_dps = (TextView) findViewById(R.id.tv_dps);
				TextView tv_roa = (TextView) findViewById(R.id.tv_roa);
				TextView tv_roe = (TextView) findViewById(R.id.tv_roe);
				TextView tv_yield = (TextView) findViewById(R.id.tv_yield);
				TextView tv_float = (TextView) findViewById(R.id.tv_float);

				// tv_mktcap.setText(objDetail.getString("").toString().toString());
				tv_p_bv.setText(objDetail.getString("p_bv").toString().toString());
				// tv_eps.setText(objDetail.getString("").toString().toString());
				tv_p_e.setText(objDetail.getString("p_e").toString().toString());
				// tv_dps.setText(objDetail.getString("").toString().toString());
				tv_roa.setText(objDetail.getString("roa").toString().toString());
				tv_roe.setText(objDetail.getString("roe").toString().toString());
				// tv_yield.setText(objDetail.getString("").toString().toString());
				// tv_float.setText(objDetail.getString("").toString().toString());

			}

		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish();
			// overridePendingTransition(R.animator.right_to_center,
			// R.animator.center_to_right);
		}
		return super.onKeyDown(keyCode, event);
	}

}
