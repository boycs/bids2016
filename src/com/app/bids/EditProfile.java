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

import com.app.bids.DialogProfileEdit.loadData;
import com.app.bids.LoginRegister.sendEdit;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class EditProfile extends Activity {

	static Context context;
	static Dialog dialog;

	public static String user_id = "", username = "", email = "",
			birthday = "", firstname = "", lastname = "", country = "",
			website = "", twitter = "", facebook = "", google = "", about = "",
			password_old, password_new, status_update;

	public static String comment_your_idea_onsite = "",
			comment_your_idea_email = "", agree_your_idea_onsite = "",
			agree_your_idea_email = "", followed_you_onsite = "",
			followed_you_email = "", reply_your_comment_onsite = "",
			reply_your_comment_email = "",
			you_follow_published_new_idea_onsite = "",
			you_follow_published_new_idea_email = "",
			you_follow_published_new_idea_popup = "",
			published_idea_on_symbol_you_follow_onsite = "",
			published_idea_on_symbol_you_follow_email = "",
			published_idea_on_symbol_you_follow_popup = "",
			commented_on_idea_you_follow_onsite = "",
			commented_on_idea_you_follow_email = "",
			you_follow_post_new_comment_onsite = "",
			you_follow_post_new_comment_email = "",
			you_follow_change_status_onsite = "",
			you_follow_change_status_email = "", notification_sound = "";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	// ============== edit informaiont =========================
	public static void editInformation(String str_user_id, String str_username,
			String str_email, String str_birthday, String str_firstname,
			String str_lastname, String str_country, String website_url,
			String str_twitter, String str_facebook, String str_google,
			String str_about) {

		user_id = str_user_id;
		username = str_username;
		email = str_email;
		// idnumber = str_idnumber;
		birthday = str_birthday;
		firstname = str_firstname;
		lastname = str_lastname;
		country = str_country;
		website = website_url;
		twitter = str_twitter;
		facebook = str_facebook;
		google = str_google;
		about = str_about;

		sendEditProfileIdCard();
		sendEditInformation();

	}

	public static void sendEditInformation() {
		sendEditInfor resp = new sendEditInfor();
		resp.execute();
	}

	public static class sendEditInfor extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		String temp = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progress.show();

		}

		@Override
		protected Void doInBackground(Void... params) {

			String url = SplashScreen.url_bidschart
					+ "/service/editUserInformation";

			String json = "";
			InputStream inputStream = null;
			String result = "";

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				// user_id=2&username=edit+name&email=game%40gmail.com&first_name=game&last_name=jung
				// &website=gimo.com&twitter=gimo.twitter&country=ubon&google=gimo.google&about_me=gimoJiJi&facebook=gimo.facebook

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("user_id", user_id);
				jsonObject.accumulate("username", username);
				jsonObject.accumulate("email", email);
				jsonObject.accumulate("first_name", firstname);
				jsonObject.accumulate("last_name", lastname);
				jsonObject.accumulate("website", website);
				jsonObject.accumulate("twitter", twitter);
				jsonObject.accumulate("country", country);
				jsonObject.accumulate("google", google);
				jsonObject.accumulate("about_me", about);
				jsonObject.accumulate("facebook", facebook);

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
				// Log.v("result edit information", "" + result);

				// JSONObject obj = new JSONObject(result);
				// FragmentChangeActivity.loginProfileData = obj
				// .getJSONObject("dataAll");

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
			PagerProfile.handlerProfile.sendEmptyMessage(0);

			// {"status":"ok","message":"Get data Success.","dataAll":{"user_id":"104","username":"Sirawich Thangern","password":null,"has_picture":"N","first_name":"narongritnl","last_name":"","email":"narongritnl@hotmail.com","website":"","twitter_handle":null,"bitcoin_address":null,"google_url":"","twitter_url":"","facebook_url":"","country":"","status":"wichhhh","saved_chart":"0","about_me":"","followers":"1","following":"4","pushished_ideas":"0","published_ideas_count":"0","ideas_views":"7","ideas_like":"1","repulation":"0","join_datetime":"2015-05-26 14:40:06","last_connect_datetime":"2015-05-26 14:40:06","online":"Y","notification_count":"0","favorite_chart_count":"0","favorite_symbol_count":"0","comment_your_idea_onsite":"Y","comment_your_idea_email":"Y","agree_your_idea_onsite":"Y","agree_your_idea_email":"Y","followed_you_onsite":"Y","followed_you_email":"Y","reply_your_comment_onsite":"Y","reply_your_comment_email":"Y","you_follow_published_new_idea_onsite":"Y","you_follow_published_new_idea_email":"Y","you_follow_published_new_idea_popup":"Y","published_idea_on_symbol_you_follow_onsite":"Y","published_idea_on_symbol_you_follow_email":"Y","published_idea_on_symbol_you_follow_popup":"Y","commented_on_idea_you_follow_onsite":"Y","commented_on_idea_you_follow_email":"Y","you_follow_post_new_comment_onsite":"Y","you_follow_post_new_comment_email":"N","you_follow_change_status_onsite":"N","you_follow_change_status_email":"Y","notification_sound":"Y","facebook_id":null,"facebook_token":"CAALaOu4GCQUBAP7KxWz4jwl0ehTZCgYCIkkSLQgJJZC7x8padbAhVIgK9I1oGcQQRcIS6hiFIz3YkxCp14aiJYKoKdQbpDfcKwp2XRGyeSlAXPyFqHYQZCW238qGYZCRZBz8ZBZAJ2SpbB4o5519ninalFHzMbLXvHPJ2YJlcAttZBgCIdJUPh5FCnWvpLPKBUAk3HuICLA2mO6ft5IbRUxoqu79DexHmYQxZB6SLWUyzZBgZDZD","twitter_token":null,"session_token":"eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiIxMDQiLCJpYXQiOjE0NTE5Njg2ODIsImV4cCI6MTQ1MzE3ODI4Mn0.YqZ5xYCzd8pSeOkJ7S3_uIH-tsG2gipmetpLHDohPwg","user_type":"free","package":"free","start":"-0001-11-30 00:00:00","expire":"-0001-11-30 00:00:00","platform":null,"device_id":null,"packet_id":null,"packet_name":null,"packet_price":null,"key_id":null,"birthday":null}}

		}
	}

	// ============ Edit ProfileIdCard =========
	public static String resultEditProfileIdCard = "";
	public static JSONObject jsoEditProfileIdCardDataAll = null;

	public static void sendEditProfileIdCard() {
		sendEdit resp = new sendEdit();
		resp.execute();
	}

	public static class sendEdit extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;
		private JSONObject json;
		private String stateSend = "";

		String temp = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			String url = SplashScreen.url_bidschart
					+ "/service/v2/editProfileIdCard";

			String json = "";
			InputStream inputStream = null;

			try {

				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();

				jsonObject.accumulate("user_id", user_id);
				// jsonObject.accumulate("key_id", idnumber);
				jsonObject.accumulate("birthday", birthday);

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
					resultEditProfileIdCard = AFunctionOther.convertInputStreamToString(inputStream);
				else
					resultEditProfileIdCard = "Did not work!";

				Log.v("result EditProfileIdCard : ", ""
						+ resultEditProfileIdCard);
				// {"status":"ok","message":"Get data Success.","dataAll":{"user_id":"1586","username":"k",
				// "first_name":"k","email":"k@gmail.com","user_type":"pro","followers":"0","following":"0",
				// "ideas_views":"0","ideas_like":"0","published_ideas_count":"0","pushished_ideas":"0","packet_price":0,
				// "packet_name":"pro beta 14 day","packet_id":"proBeta","package":"proBeta","start":"2016-03-10 15:31:36",
				// "expire":"2016-03-24 15:31:36"}}

			} catch (IOException e) {
				connectionError = true;
				e.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				e.printStackTrace();
			} catch (JSONException e) {
				connectionError = true;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			Log.v("connection update Error", "" + connectionError);

			if (connectionError == false) {
				try {
					JSONObject jsoEditProfileIdCard = new JSONObject(
							resultEditProfileIdCard);
					if (jsoEditProfileIdCard.getString("status").equals("ok")) {
						jsoEditProfileIdCardDataAll = jsoEditProfileIdCard
								.getJSONObject("dataAll");

						Log.v("jsoEditProfileIdCardDataAll : ", ""
								+ jsoEditProfileIdCardDataAll);

					}
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	// ============== edit Password =========================
	public static void editPassword(String str_user_id,
			String str_password_old, String str_password_new) {

		user_id = str_user_id;
		password_old = str_password_old;
		password_new = str_password_new;

		sendEditPassword();

	}

	public static void sendEditPassword() {
		sendEditPss resp = new sendEditPss();
		resp.execute();
	}

	public static class sendEditPss extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		String temp = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progress.show();

		}

		@Override
		protected Void doInBackground(Void... params) {

			String url = SplashScreen.url_bidschart + "/service/editPassword";

			String json = "";
			InputStream inputStream = null;
			String result = "";

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				// user_id=2&password_old=12345&password_new=123456

				Log.v("pw s : ", user_id + "_" + password_old + "_"
						+ password_new);

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("user_id", user_id);
				jsonObject.accumulate("password_old", PagerProfile.strPssToMd5(password_old));
				jsonObject.accumulate("password_new", password_new);

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

				Log.v("result change password : ", "" + result);

				// JSONObject obj = new JSONObject(result);
				// FragmentChangeActivity.loginProfileData = obj
				// .getJSONObject("dataAll");

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
			initLoadAllDataProfile();
		}
	}

	// ============== Load Data all =============
	public static void initLoadAllDataProfile() {
		loadData resp = new loadData();
		resp.execute();
	}

	public static class loadData extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;

		private JSONObject jsonGetUserById;
		private JSONObject jsonGetFollowingByUserId;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();

			// http://www.bidschart.com/service/getUserById?user_id=104
			String url_GetUserById = SplashScreen.url_bidschart
					+ "/service/getUserById?user_id="
					+ SplashScreen.userModel.user_id;

			try {
				jsonGetUserById = ReadJson
						.readJsonObjectFromUrl(url_GetUserById);
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
				if (jsonGetUserById != null) {
					try {
						SplashScreen.contentGetUserById = jsonGetUserById
								.getJSONObject("dataAll");

						switchFragment(new PagerProfile());
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					switchFragment(new PagerProfile());
				}
			} else {
				switchFragment(new PagerProfile());
			}
		}
	}

	public static void editNotification(String str_user_id,
			String str_comment_your_idea_onsite,
			String str_comment_your_idea_email,
			String str_agree_your_idea_onsite,
			String str_agree_your_idea_email, String str_followed_you_onsite,
			String str_followed_you_email,
			String str_reply_your_comment_onsite,
			String str_reply_your_comment_email,
			String str_you_follow_published_new_idea_onsite,
			String str_you_follow_published_new_idea_email,
			String str_you_follow_published_new_idea_popup,
			String str_published_idea_on_symbol_you_follow_onsite,
			String str_published_idea_on_symbol_you_follow_email,
			String str_published_idea_on_symbol_you_follow_popup,
			String str_commented_on_idea_you_follow_onsite,
			String str_commented_on_idea_you_follow_email,
			String str_you_follow_post_new_comment_onsite,
			String str_you_follow_post_new_comment_email,
			String str_you_follow_change_status_onsite,
			String str_you_follow_change_status_email,
			String str_notification_sound) {

		user_id = str_user_id;
		comment_your_idea_onsite = str_comment_your_idea_onsite;
		comment_your_idea_email = str_comment_your_idea_email;
		agree_your_idea_onsite = str_agree_your_idea_onsite;
		agree_your_idea_email = str_agree_your_idea_email;
		followed_you_onsite = str_followed_you_onsite;
		followed_you_email = str_followed_you_email;
		reply_your_comment_onsite = str_reply_your_comment_onsite;
		reply_your_comment_email = str_reply_your_comment_email;
		you_follow_published_new_idea_onsite = str_you_follow_published_new_idea_onsite;
		you_follow_published_new_idea_email = str_you_follow_published_new_idea_email;
		you_follow_published_new_idea_popup = str_you_follow_published_new_idea_popup;
		published_idea_on_symbol_you_follow_onsite = str_published_idea_on_symbol_you_follow_onsite;
		published_idea_on_symbol_you_follow_email = str_published_idea_on_symbol_you_follow_email;
		published_idea_on_symbol_you_follow_popup = str_published_idea_on_symbol_you_follow_popup;
		commented_on_idea_you_follow_onsite = str_commented_on_idea_you_follow_onsite;
		commented_on_idea_you_follow_email = str_commented_on_idea_you_follow_email;
		you_follow_post_new_comment_onsite = str_you_follow_post_new_comment_onsite;
		you_follow_post_new_comment_email = str_you_follow_post_new_comment_email;
		you_follow_change_status_onsite = str_you_follow_change_status_onsite;
		you_follow_change_status_email = str_you_follow_change_status_email;
		notification_sound = str_notification_sound;

		sendEditNotification();

	}

	// ============== send Post Reply =========================
	public static void sendEditNotification() {
		sendEditNotic resp = new sendEditNotic();
		resp.execute();
	}

	public static class sendEditNotic extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		String temp = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progress.show();

		}

		@Override
		protected Void doInBackground(Void... params) {

			String url = SplashScreen.url_bidschart
					+ "/service/editUserNotification";

			String json = "";
			InputStream inputStream = null;
			String result = "";

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				// user_id=2&username=edit+name&email=game%40gmail.com&first_name=game&last_name=jung
				// &website=gimo.com&twitter=gimo.twitter&country=ubon&google=gimo.google&about_me=gimoJiJi&facebook=gimo.facebook

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("user_id", user_id);
				jsonObject.accumulate("username", username);
				jsonObject.accumulate("email", email);
				jsonObject.accumulate("first_name", firstname);
				jsonObject.accumulate("last_name", lastname);
				jsonObject.accumulate("website", website);
				jsonObject.accumulate("twitter", twitter);
				jsonObject.accumulate("country", country);
				jsonObject.accumulate("google", google);
				jsonObject.accumulate("about_me", about);
				jsonObject.accumulate("facebook", facebook);

				jsonObject.accumulate("user_id", user_id);
				jsonObject.accumulate("comment_your_idea_onsite",
						comment_your_idea_onsite);
				jsonObject.accumulate("comment_your_idea_email",
						comment_your_idea_email);
				jsonObject.accumulate("agree_your_idea_onsite",
						agree_your_idea_onsite);
				jsonObject.accumulate("agree_your_idea_email",
						agree_your_idea_email);
				jsonObject.accumulate("followed_you_onsite",
						followed_you_onsite);
				jsonObject.accumulate("followed_you_email", followed_you_email);
				jsonObject.accumulate("reply_your_comment_onsite",
						reply_your_comment_onsite);
				jsonObject.accumulate("reply_your_comment_email",
						reply_your_comment_email);
				jsonObject.accumulate("you_follow_published_new_idea_onsite",
						you_follow_published_new_idea_onsite);
				jsonObject.accumulate("you_follow_published_new_idea_email",
						you_follow_published_new_idea_email);
				jsonObject.accumulate("you_follow_published_new_idea_popup",
						you_follow_published_new_idea_popup);
				jsonObject.accumulate(
						"published_idea_on_symbol_you_follow_onsite",
						published_idea_on_symbol_you_follow_onsite);
				jsonObject.accumulate(
						"published_idea_on_symbol_you_follow_email",
						published_idea_on_symbol_you_follow_email);
				jsonObject.accumulate(
						"published_idea_on_symbol_you_follow_popup",
						published_idea_on_symbol_you_follow_popup);
				jsonObject.accumulate("commented_on_idea_you_follow_onsite",
						commented_on_idea_you_follow_onsite);
				jsonObject.accumulate("commented_on_idea_you_follow_email",
						commented_on_idea_you_follow_email);
				jsonObject.accumulate("you_follow_post_new_comment_onsite",
						you_follow_post_new_comment_onsite);
				jsonObject.accumulate("you_follow_post_new_comment_email",
						you_follow_post_new_comment_email);
				jsonObject.accumulate("you_follow_change_status_onsite",
						you_follow_change_status_onsite);
				jsonObject.accumulate("you_follow_change_status_email",
						you_follow_change_status_email);
				jsonObject.accumulate("notification_sound", notification_sound);

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
				// Log.v("result edit noti", "" + result);

				// JSONObject obj = new JSONObject(result);
				// FragmentChangeActivity.loginProfileData = obj
				// .getJSONObject("dataAll");

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

	protected static void switchFragment(PagerProfile fragment) {
		if (context == null)
			return;
		if (context instanceof FragmentChangeActivity) {
			FragmentChangeActivity fca = (FragmentChangeActivity) context;
			fca.switchContent(fragment);
		}
	}

}
