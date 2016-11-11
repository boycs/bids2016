package com.app.bids;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.CommentLikeArticle.setDelete;
import com.app.bids.CommentLikeArticle.setLikeArticle;

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
import android.widget.LinearLayout;
import android.widget.TextView;

public class CommentLikePost extends Activity {

	public static String post_id, user_id, content, chart_url = "",
			comments_id, reply_id;
	public static boolean ckLikePost, ckLikeReply;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	// ============== send Like post =========================
	public static String resultLikeArticle = "";

	public static void sendLikePost(String articleid, String userid) {

		post_id = articleid;
		user_id = userid;

		setLikePost resp = new setLikePost();
		resp.execute();
	}

	public static class setLikePost extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		String temp = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progress.show();

		}

		@Override
		protected Void doInBackground(Void... params) {

			String url = SplashScreen.url_bidschart+ "/service/likePost";

			String json = "";
			InputStream inputStream = null;

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("post_id", post_id);
				jsonObject.accumulate("user_id", user_id);

				// 4. convert JSONObject to JSON to String
				json = jsonObject.toString();

				// 5. set json to StringEntity
				StringEntity se = new StringEntity(json, "UTF-8");

				// 6. set httpPost Entity
				httppost.setEntity(se);

				// 7. Set some headers to inform server about the type of the
				// content
				httppost.setHeader("Accept", "application/json");
				httppost.setHeader("Content-type", "application/json");

				// 8. Execute POST request to the given URL
				HttpResponse httpResponse = httpclient.execute(httppost);

				// 9. receive response as inputStream
				inputStream = httpResponse.getEntity().getContent();

				// 10. convert inputstream to string
				if (inputStream != null)
					resultLikeArticle = AFunctionOther.convertInputStreamToString(inputStream);
				else
					resultLikeArticle = "Did not work!";
				Log.v("result like article", "" + resultLikeArticle);
				// {"status":"ok","message":"Get data Success.","dataAll":{"reply_id":66,"comment_id":95,"post_id":99,"reply_user_id":59,"agree_count":0,"disagree_count":0,"reply_datetime":"2015-04-21 14:37:27","content":"???????","chart_url":"","username":"??????? ???????"}}

			} catch (IOException e) {
				connectionError = true;
				e.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			// try {
			// JSONObject jsonObj = new JSONObject(resultLikePost);
			// UiArticleSelect ua = new UiArticleSelect();
			// ua.setLikePost(jsonObj);
			// } catch (JSONException e) {
			// e.printStackTrace();
			// }

		}
	}

	// ============== send Post Comment =========================
	public static void sendPostComment(String postid, String userid,
			String contents) {

		post_id = postid;
		user_id = userid;
		content = contents;

		setComment resp = new setComment();
		resp.execute();
	}

	public static class setComment extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		String temp = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progress.show();

		}

		@Override
		protected Void doInBackground(Void... params) {

			String url = SplashScreen.url_bidschart+ "/service/addCommentPost";

			String json = "";
			InputStream inputStream = null;
			String result = "";

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("post_id", post_id);
				jsonObject.accumulate("user_id", user_id);
				jsonObject.accumulate("content", content);
				jsonObject.accumulate("chart_url", "");

				// 4. convert JSONObject to JSON to String
				json = jsonObject.toString();

				// 5. set json to StringEntity
				StringEntity se = new StringEntity(json, "UTF-8");

				// 6. set httpPost Entity
				httppost.setEntity(se);

				// 7. Set some headers to inform server about the type of the
				// content
				httppost.setHeader("Accept", "application/json");
				httppost.setHeader("Content-type", "application/json");

				// 8. Execute POST request to the given URL
				HttpResponse httpResponse = httpclient.execute(httppost);

				// 9. receive response as inputStream
				inputStream = httpResponse.getEntity().getContent();

				// 10. convert inputstream to string
				if (inputStream != null)
					result = AFunctionOther.convertInputStreamToString(inputStream);
				else
					result = "Did not work!";

				Log.v("result", "" + result);
				// {"status":"error","message":"SQLSTATE[22003]: Numeric value out of range: 1264 Out of range value for column 'comment_user_id' at row 1 (SQL: insert into `comments` (`content`, `post_id`, `chart_url`, `comment_user_id`, `comment_datetime`) values (test3, 99, , 1626284554267851, 2015-04-20 14:28:47))","dataAll":[]}

			} catch (IOException e) {
				connectionError = true;
				e.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

		}
	}

	// ============== send Like Post =========================
	public static void sendPostLike(String commentsid, String userid, Boolean ck) {

		comments_id = commentsid;
		user_id = userid;
		ckLikePost = ck;

		setAgree resp = new setAgree();
		resp.execute();
	}

	public static class setAgree extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		String temp = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progress.show();

		}

		@Override
		protected Void doInBackground(Void... params) {

			String url = "";
			if (ckLikePost) {
				url = SplashScreen.url_bidschart+ "/service/agreeCommentPost";
			} else {
				url = SplashScreen.url_bidschart+ "/service/disagreeCommentPost";
			}

			String json = "";
			InputStream inputStream = null;
			String result = "";

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("comment_id", comments_id);
				jsonObject.accumulate("user_id", user_id);

				// 4. convert JSONObject to JSON to String
				json = jsonObject.toString();

				// 5. set json to StringEntity
				StringEntity se = new StringEntity(json, "UTF-8");

				// 6. set httpPost Entity
				httppost.setEntity(se);

				// 7. Set some headers to inform server about the type of the
				// content
				httppost.setHeader("Accept", "application/json");
				httppost.setHeader("Content-type", "application/json");

				// 8. Execute POST request to the given URL
				HttpResponse httpResponse = httpclient.execute(httppost);

				// 9. receive response as inputStream
				inputStream = httpResponse.getEntity().getContent();

				// 10. convert inputstream to string
				if (inputStream != null)
					result = AFunctionOther.convertInputStreamToString(inputStream);
				else
					result = "Did not work!";
				Log.v("result like post", "" + result);
				// {"status":"ok","message":"Get data Success.","dataAll":{"reply_id":66,"comment_id":95,"post_id":99,"reply_user_id":59,"agree_count":0,"disagree_count":0,"reply_datetime":"2015-04-21 14:37:27","content":"???????","chart_url":"","username":"??????? ???????"}}

			} catch (IOException e) {
				connectionError = true;
				e.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			// UiPostSelectComments up = new UiPostSelectComments();
			// up.initGetPostComment();

		}
	}

	// ============== send Like Reply =========================
	public static void sendReplyLike(String replyid, String userid, Boolean ck) {

		reply_id = replyid;
		user_id = userid;
		ckLikeReply = ck;

		setAgreeReply resp = new setAgreeReply();
		resp.execute();
	}

	public static class setAgreeReply extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		String temp = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progress.show();

		}

		@Override
		protected Void doInBackground(Void... params) {

			String url = "";
			if (ckLikeReply) {
				url = SplashScreen.url_bidschart+ "/service/agreeReplyPost";
			} else {
				url = SplashScreen.url_bidschart+ "/service/disagreeReplyPost";
			}

			String json = "";
			InputStream inputStream = null;
			String result = "";

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("reply_id", reply_id);
				jsonObject.accumulate("user_id", user_id);

				// 4. convert JSONObject to JSON to String
				json = jsonObject.toString();

				// 5. set json to StringEntity
				StringEntity se = new StringEntity(json, "UTF-8");

				// 6. set httpPost Entity
				httppost.setEntity(se);

				// 7. Set some headers to inform server about the type of the
				// content
				httppost.setHeader("Accept", "application/json");
				httppost.setHeader("Content-type", "application/json");

				// 8. Execute POST request to the given URL
				HttpResponse httpResponse = httpclient.execute(httppost);

				// 9. receive response as inputStream
				inputStream = httpResponse.getEntity().getContent();

				// 10. convert inputstream to string
				if (inputStream != null)
					result = AFunctionOther.convertInputStreamToString(inputStream);
				else
					result = "Did not work!";
				Log.v("result like reply", "" + result);
				// {"status":"ok","message":"Get data Success.","dataAll":{"reply_id":66,"comment_id":95,"post_id":99,"reply_user_id":59,"agree_count":0,"disagree_count":0,"reply_datetime":"2015-04-21 14:37:27","content":"???????","chart_url":"","username":"??????? ???????"}}

			} catch (IOException e) {
				connectionError = true;
				e.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

		}
	}

	// ============== send remove comment =========================
	public static void sendPostRemoveComment(String commentsid) {

		comments_id = commentsid;

		setDeleteComment resp = new setDeleteComment();
		resp.execute();
	}

	public static class setDeleteComment extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		String temp = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progress.show();

		}

		@Override
		protected Void doInBackground(Void... params) {

			String url = "http://service.bidschart.com/comment/removeComment";

			String json = "";
			InputStream inputStream = null;
			String result = "";

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("comments_id", comments_id);

				// 4. convert JSONObject to JSON to String
				json = jsonObject.toString();

				// 5. set json to StringEntity
				StringEntity se = new StringEntity(json, "UTF-8");

				// 6. set httpPost Entity
				httppost.setEntity(se);

				// 7. Set some headers to inform server about the type of the
				// content
				httppost.setHeader("Accept", "application/json");
				httppost.setHeader("Content-type", "application/json");

				// 8. Execute POST request to the given URL
				HttpResponse httpResponse = httpclient.execute(httppost);

				// 9. receive response as inputStream
				inputStream = httpResponse.getEntity().getContent();

				// 10. convert inputstream to string
				if (inputStream != null)
					result = AFunctionOther.convertInputStreamToString(inputStream);
				else
					result = "Did not work!";
				Log.v("result remove comment", "" + result);
				// {"status":"ok","message":"Get data Success.","dataAll":{"reply_id":66,"comment_id":95,"post_id":99,"reply_user_id":59,"agree_count":0,"disagree_count":0,"reply_datetime":"2015-04-21 14:37:27","content":"???????","chart_url":"","username":"??????? ???????"}}

			} catch (IOException e) {
				connectionError = true;
				e.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			// UiPostSelectComments up = new UiPostSelectComments();
			// up.initGetPostComment();

		}
	}

	// ============== send remove reply =========================
	public static void sendPostRemoveReply(String commentsid) {

		reply_id = commentsid;

		setDeleteReply resp = new setDeleteReply();
		resp.execute();
	}

	public static class setDeleteReply extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		String temp = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progress.show();

		}

		@Override
		protected Void doInBackground(Void... params) {

			String url = "http://service.bidschart.com/reply/removeReply";

			String json = "";
			InputStream inputStream = null;
			String result = "";

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("reply_id", reply_id);

				// 4. convert JSONObject to JSON to String
				json = jsonObject.toString();

				// 5. set json to StringEntity
				StringEntity se = new StringEntity(json, "UTF-8");

				// 6. set httpPost Entity
				httppost.setEntity(se);

				// 7. Set some headers to inform server about the type of the
				// content
				httppost.setHeader("Accept", "application/json");
				httppost.setHeader("Content-type", "application/json");

				// 8. Execute POST request to the given URL
				HttpResponse httpResponse = httpclient.execute(httppost);

				// 9. receive response as inputStream
				inputStream = httpResponse.getEntity().getContent();

				// 10. convert inputstream to string
				if (inputStream != null)
					result = AFunctionOther.convertInputStreamToString(inputStream);
				else
					result = "Did not work!";
				Log.v("result remove comment", "" + result);
				// {"status":"ok","message":"Get data Success.","dataAll":{"reply_id":66,"comment_id":95,"post_id":99,"reply_user_id":59,"agree_count":0,"disagree_count":0,"reply_datetime":"2015-04-21 14:37:27","content":"???????","chart_url":"","username":"??????? ???????"}}

			} catch (IOException e) {
				connectionError = true;
				e.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				e.printStackTrace();
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			// UiPostSelectComments up = new UiPostSelectComments();
			// up.initGetPostComment();

		}
	}

}
