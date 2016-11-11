package com.app.bids;

import java.io.IOException;
import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

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
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class UiWatchListDetailNewsSelect extends Activity {

	public static JSONObject contentGetArticleById = null;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
//		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		
		setContentView(R.layout.ui_article_select);

		// initial Data
		initLoadData();
	}

	// ============== Load initLoadData =========================
	private void initLoadData() {
		loadAll resp = new loadAll();
		resp.execute();
	}

	public class loadAll extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;
		// ======= Ui Newds ========
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
			String url_GetArticleById = SplashScreen.url_bidschart
					+ "/service/getArticleById?article_id="
					+ PagerWatchListDetailNews.strArticleIdSelect + "&user_id="
					+ SplashScreen.userModel.user_id;

			try {
				// ======= Ui News ========
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

						contentGetArticleById = jsonGetArticleById
								.getJSONObject("dataAll");
						// Toast.makeText(context,
						// "content : "+contentGetWatchlistNewsBySymbol.length(),
						// 0).show();

						initView();

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

	// ====================== Initial View ========================
	private String article_id;

	private void initView() {
		try {
			TextView tv_close = (TextView) findViewById(R.id.tv_close);
			// like comments
			LinearLayout li_bottom_like = (LinearLayout) findViewById(R.id.li_bottom_like);
			LinearLayout li_bottom_comment = (LinearLayout) findViewById(R.id.li_bottom_comment);
			
			ImageView img_bottom_like = (ImageView) findViewById(R.id.img_bottom_like);
			TextView tv_bottom_like = (TextView) findViewById(R.id.tv_bottom_like);
			if(!(contentGetArticleById.getString("this_like").equals("NO"))){
				img_bottom_like.setBackgroundResource(R.drawable.icon_checklike);
				tv_bottom_like.setTextColor(getResources().getColor(R.color.c_success));
			}else{
				img_bottom_like.setBackgroundResource(R.drawable.icon_like);
				tv_bottom_like.setTextColor(getResources().getColor(R.color.c_title));
			}

			// like
			li_bottom_like.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (SplashScreen.userModel.user_id != "") {
						try {
							article_id = ""
									+ contentGetArticleById.getString("id");
							PagerWatchListDetailNews.strArticleIdSelect = contentGetArticleById
									.getString("id");

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
					}
				}
			});

			// comment
			li_bottom_comment.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					startActivity(new Intent(getApplicationContext(),
							UiWatchlistDetailNewsSelectComments.class));
				}
			});

			tv_close.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					finish();
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

			tv_symbol.setText(contentGetArticleById.get("symbol").toString());
			tv_article_title.setText(contentGetArticleById.get("article_title")
					.toString());
			tv_views_count.setText(contentGetArticleById.get("views_count")
					.toString());
			tv_likes_count.setText(contentGetArticleById.get("likes_count")
					.toString());
			tv_comments_count.setText(contentGetArticleById.get(
					"comments_count").toString());

			try {
				String strDateAgo = ""
						+ DateTimeAgo.CalAgoTime2(contentGetArticleById
								.getString("created_at"));
				String strDateYmd = ""
						+ (DateTimeCreate
								.DateDmyThaiCreate(contentGetArticleById
										.getString("created_at")));
				tv_created_at.setText(strDateAgo + "  " + strDateYmd);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			String data = "<html><head>"
					+ "<style type=\"text/css\">body{color: #585f65;}"
					+ "</style></head>"
					+ "<body>"
					+ contentGetArticleById.get("article_description")
							.toString() + "</body></html>";

			String mimeType = "text/html";
			String encoding = "utf-8";

			wv_article_description.setBackgroundColor(Color.TRANSPARENT);
			// webNews.getSettings().setJavaScriptEnabled(true);
			wv_article_description.loadDataWithBaseURL(null, data, mimeType,
					encoding, null);

			if ((contentGetArticleById.get("content"))
					.equals("http://service.bidschart.com/undefined")) {
			} else {
				FragmentChangeActivity.imageLoader.loadImage(""
						+ contentGetArticleById.get("content"),
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
				url_GetArticleById = SplashScreen.url_bidschart
						+ "/service/getArticleById?article_id="
						+ PagerWatchListDetailNews.strArticleIdSelect
						+ "&user_id=" + SplashScreen.userModel.user_id;

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

						contentGetArticleById = jsonGetArticleById
								.getJSONObject("dataAll");

						initView();

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
		}
		return super.onKeyDown(keyCode, event);
	}

}
