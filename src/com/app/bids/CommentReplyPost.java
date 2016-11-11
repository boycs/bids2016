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

public class CommentReplyPost extends Activity {

	public static String post_id, user_id, chart_url = "", replyContent, replyComment_id;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	public static void sendPostReply(String postid, String userid, String replyContents, String replyCommentid) {
		post_id = postid;
		user_id = userid;
		replyContent = replyContents;
		replyComment_id = replyCommentid;

		sendPostReply();

	}

	// ============== send Post Reply =========================
	public static void sendPostReply() {
		setReply resp = new setReply();
		resp.execute();
	}

	public static class setReply extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		String temp = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progress.show();

		}

		@Override
		protected Void doInBackground(Void... params) {

			String url = SplashScreen.url_bidschart+ "/service/addReplyPost";

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
				jsonObject.accumulate("content", replyContent);
				jsonObject.accumulate("chart_url", "");
				jsonObject.accumulate("comment_id", replyComment_id);

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
				Log.v("result reply", "" + result);
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

}
