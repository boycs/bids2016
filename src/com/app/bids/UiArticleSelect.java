package com.app.bids;

import java.io.IOException;
import java.text.ParseException;

import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;
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
public class UiArticleSelect extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ui_article_select);

		((TextView) findViewById(R.id.tv_close))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						finish();
					}
				});

		loadBackGround();

	}

	// ====================== Initial View ========================
	private boolean ckLikePost = true;
	private String article_id, comments_id;
	public static TextView tv_likes_count, tv_bottom_like;
	public static ImageView img_bottom_like, img_likes_count;
	public static LinearLayout li_bottom_like;

	private void initView() {
		try {
			LinearLayout li_bottom_comment = (LinearLayout) findViewById(R.id.li_bottom_comment);
			li_bottom_like = (LinearLayout) findViewById(R.id.li_bottom_like);
			img_likes_count = (ImageView) findViewById(R.id.img_likes_count);
			img_bottom_like = (ImageView) findViewById(R.id.img_bottom_like);
			tv_likes_count = (TextView) findViewById(R.id.tv_likes_count);
			tv_bottom_like = (TextView) findViewById(R.id.tv_bottom_like);

			// like
			li_bottom_like.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (SplashScreen.userModel.user_id != "") {
						try {
							article_id = ""
									+ AttributeBegin.contentGetNewsSelect.get("id");

							// like
							CommentLikeArticle.sendLikeArticle(article_id,
									SplashScreen.userModel.user_id);

							loadBackGround(); // load background

						} catch (JSONException e) {
							e.printStackTrace();
						}
					} else {
						Toast.makeText(getApplicationContext(), "Login", 0)
						.show();
						LoginDialog.show();
//						LoginDialog2.show();
					}
				}
			});
			// comment
			li_bottom_comment.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(getApplicationContext(),
							UiArticleSelectComments.class));
				}
			});

			final LinearLayout li_content = (LinearLayout) findViewById(R.id.li_content);
			TextView tv_symbol = (TextView) findViewById(R.id.tv_symbol);
			TextView tv_article_title = (TextView) findViewById(R.id.tv_article_title);

			TextView tv_created_at = (TextView) findViewById(R.id.tv_created_at);
			TextView tv_views_count = (TextView) findViewById(R.id.tv_views_count);
			TextView tv_likes_count = (TextView) findViewById(R.id.tv_likes_count);
			TextView tv_comments_count = (TextView) findViewById(R.id.tv_comments_count);
			WebView wv_article_description = (WebView) findViewById(R.id.wv_article_description);

			tv_symbol.setText(AttributeBegin.contentGetNewsSelect.get("symbol")
					.toString());
			tv_article_title.setText(AttributeBegin.contentGetNewsSelect.get(
					"article_title").toString());
			tv_views_count.setText(AttributeBegin.contentGetNewsSelect.get(
					"views_count").toString());
			tv_likes_count.setText(AttributeBegin.contentGetNewsSelect.get(
					"likes_count").toString());
			tv_comments_count.setText(AttributeBegin.contentGetNewsSelect.get(
					"comments_count").toString());

			// Toast.makeText(getApplicationContext(),
			// ""+AttributeBegin.contentGetNewsSelect.get("this_like"), 0).show();
			if ((AttributeBegin.contentGetNewsSelect.get("this_like").toString())
					.equals("YES")) {
				img_likes_count
						.setBackgroundResource(R.drawable.icon_checklike);
				tv_likes_count.setTextColor(getResources().getColor(
						R.color.c_success));
				tv_bottom_like.setTextColor(getResources().getColor(
						R.color.c_success));
				img_bottom_like
						.setBackgroundResource(R.drawable.icon_checklike);
				li_bottom_like
						.setBackgroundResource(R.drawable.border_like_success);
			}

			try {
				String strDateAgo = ""
						+ DateTimeAgo
								.CalAgoTime2(AttributeBegin.contentGetNewsSelect.getString("created_at"));
				String strDateYmd = ""
						+ (DateTimeCreate.DateDmyThaiCreate(AttributeBegin.contentGetNewsSelect.getString("created_at")));
				tv_created_at.setText(strDateAgo + "  " + strDateYmd);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String data = "<html><head>"
					+ "<style type=\"text/css\">body{color: #585f65;}"
					+ "</style></head>"
					+ "<body>"
					+ AttributeBegin.contentGetNewsSelect.get("article_description")
							.toString() + "</body></html>";

			String mimeType = "text/html";
			String encoding = "utf-8";

			wv_article_description.setBackgroundColor(Color.TRANSPARENT);
			// webNews.getSettings().setJavaScriptEnabled(true);
			wv_article_description.loadDataWithBaseURL(null, data, mimeType,
					encoding, null);

			if ((AttributeBegin.contentGetNewsSelect.get("content"))
					.equals("http://service.bidschart.com/undefined")) {
			} else {
				FragmentChangeActivity.imageLoader.loadImage(""
						+ AttributeBegin.contentGetNewsSelect.get("content"),
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
		} catch (JSONException e1) {
			e1.printStackTrace();
		}
	}

	// ====================== load background ========================
	// public static void setLikePost(JSONObject result) {
	// try {
	// if ((result.get("status")).equals("ok")) {
	// JSONObject objDataAll = result.getJSONObject("dataAll");
	// // if ((objDataAll.get("status")).equals("like")) {
	// if ((objDataAll.get("status").toString()) == "like") {
	// tv_likes_count.setText(objDataAll.get("likes_count").toString());
	//
	// tv_likes_count.setTextColor(color.c_success);
	// tv_bottom_like.setTextColor(color.c_success);
	// img_likes_count.setBackgroundResource(R.drawable.icon_checklike);
	// img_bottom_like.setBackgroundResource(R.drawable.icon_checklike);
	// li_bottom_like.setBackgroundResource(R.drawable.border_like_success);
	// } else {
	// tv_likes_count.setText(objDataAll.get("likes_count").toString());
	//
	// tv_likes_count.setTextColor(color.c_title);
	// tv_bottom_like.setTextColor(color.c_title);
	// img_likes_count.setBackgroundResource(R.drawable.icon_like);
	// img_bottom_like.setBackgroundResource(R.drawable.icon_like);
	// li_bottom_like.setBackgroundResource(R.drawable.border_button);
	// }
	// }
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// }

	// ====================== load background ========================
	private void loadBackGround() {
		loadAllBg resp = new loadAllBg();
		resp.execute();
	}

	public class loadAllBg extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;
		private JSONObject jsonGetArticleById;

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
			String url_GetArticleById;
			try {
				url_GetArticleById = SplashScreen.url_bidschart+"/service/getArticleById?article_id="
						+ AttributeBegin.contentGetNewsSelect.get("id").toString()
						+ "&user_id="
						+ SplashScreen.userModel.user_id;

				jsonGetArticleById = ReadJson
						.readJsonObjectFromUrl(url_GetArticleById);
			} catch (IOException e1) {
				connectionError = true;
				jsonGetArticleById = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonGetArticleById = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonGetArticleById = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (jsonGetArticleById != null) {
					try {

						AttributeBegin.contentGetNewsSelect = jsonGetArticleById
								.getJSONObject("dataAll");
						
						initView();

//						TextView tv_views_count = (TextView) findViewById(R.id.tv_views_count);
//						TextView tv_likes_count = (TextView) findViewById(R.id.tv_likes_count);
//						TextView tv_comments_count = (TextView) findViewById(R.id.tv_comments_count);
//
//						tv_views_count.setText(AttributeBegin.contentGetNewsSelect
//								.get("views_count").toString());
//						tv_likes_count.setText(AttributeBegin.contentGetNewsSelect
//								.get("likes_count").toString());
//						tv_comments_count
//								.setText(AttributeBegin.contentGetNewsSelect.get(
//										"comments_count").toString());
//
//						if ((AttributeBegin.contentGetNewsSelect.get("this_like")
//								.toString()).equals("YES")) {
//							tv_likes_count
//									.setText(AttributeBegin.contentGetNewsSelect
//											.get("likes_count").toString());
//							tv_likes_count.setTextColor(getResources()
//									.getColor(color.c_success));
//							tv_bottom_like.setTextColor(getResources()
//									.getColor(color.c_success));
//							img_bottom_like
//									.setBackgroundResource(R.drawable.icon_checklike);
//							img_likes_count
//									.setBackgroundResource(R.drawable.icon_checklike);
//							li_bottom_like
//									.setBackgroundResource(R.drawable.border_like_success);
//						} else {
//							tv_likes_count
//									.setText(AttributeBegin.contentGetNewsSelect
//											.get("likes_count").toString());
//							tv_likes_count.setTextColor(getResources()
//									.getColor(color.c_title));
//							tv_bottom_like.setTextColor(getResources()
//									.getColor(color.c_title));
//							img_bottom_like
//									.setBackgroundResource(R.drawable.icon_like);
//							img_likes_count
//									.setBackgroundResource(R.drawable.icon_like);
//							li_bottom_like
//									.setBackgroundResource(R.drawable.border_button);
//						}

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
