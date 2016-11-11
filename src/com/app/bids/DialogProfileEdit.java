package com.app.bids;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;
import com.app.bids.EditProfile.sendEdit;
import com.app.bids.EditProfile.sendEditInfor;
import com.app.bids.PagerProfile.loadData;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract.Profile;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class DialogProfileEdit {

	private static Dialog dialog;
	private static Context context;

	// FragmentChangeActivity.url_bidschart+"/watchlist?platform=mobile&user=10
	// public static JSONArray contentGetWatchlists = null;

	static Dialog dialogLoading;

	public DialogProfileEdit(Context context2) {
		this.context = context2;
		this.dialog = new Dialog(context2);

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
		dialog.setContentView(R.layout.dialog_profile_edit);

		// --------- dialogLoading
		dialogLoading = new Dialog(context);
		dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogLoading.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialogLoading.setContentView(R.layout.progress_bar);
		dialogLoading.setCancelable(false);
		dialogLoading.setCanceledOnTouchOutside(false);

	}

	public static void show() {
		dialog.show();

		initViewDialog();

		// set width height dialog
		DisplayMetrics displaymetrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(displaymetrics);

		int ScreenHeight = displaymetrics.heightPixels;
		int ScreenWidth = displaymetrics.widthPixels;

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.gravity = Gravity.TOP;
		lp.width = (int) (ScreenWidth * 1);
		lp.height = (int) (ScreenHeight * 0.88);
		dialog.getWindow().setAttributes(lp);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
	}

	public static void dissmiss() {
		dialog.dismiss();
	}

	// ---- view dialog
	public static String str_username, str_email, str_idnumber, str_birthday,
			str_firstname, str_lastname, str_country, website_url, str_twitter,
			str_facebook, str_google, str_about;

	public static void initViewDialog() {
		try {
			final String str_user_id = SplashScreen.contentGetUserById
					.getString("user_id");
			final String str_user_pss = SplashScreen.contentGetUserById
					.getString("password");

			final EditText dl_et_username, dl_et_email, dl_et_idnumber, dl_et_birthday, dl_et_firstname, dl_et_lastname, dl_et_country, dl_et_website_url, dl_et_facebook, dl_et_google, dl_et_twitter, dl_et_about;
			final TextView dl_tv_change_pass, dl_tv_savechanges;

			dl_tv_change_pass = (TextView) dialog
					.findViewById(R.id.dl_tv_change_pass);
			dl_et_username = (EditText) dialog
					.findViewById(R.id.dl_et_username);
			dl_et_email = (EditText) dialog.findViewById(R.id.dl_et_email);
			dl_et_idnumber = (EditText) dialog
					.findViewById(R.id.dl_et_idnumber);
			dl_et_birthday = (EditText) dialog
					.findViewById(R.id.dl_et_birthday);
			dl_et_firstname = (EditText) dialog
					.findViewById(R.id.dl_et_firstname);
			dl_et_lastname = (EditText) dialog
					.findViewById(R.id.dl_et_lastname);
			dl_et_country = (EditText) dialog.findViewById(R.id.dl_et_country);
			dl_et_website_url = (EditText) dialog
					.findViewById(R.id.dl_et_website_url);
			dl_et_facebook = (EditText) dialog
					.findViewById(R.id.dl_et_facebook);
			dl_et_google = (EditText) dialog.findViewById(R.id.dl_et_google);
			dl_et_twitter = (EditText) dialog.findViewById(R.id.dl_et_twitter);
			dl_et_about = (EditText) dialog.findViewById(R.id.dl_et_about);
			dl_tv_savechanges = (TextView) dialog
					.findViewById(R.id.dl_tv_savechanges);

			// -------- set data -----
			dl_et_username.setText(SplashScreen.contentGetUserById
					.getString("username"));
			dl_et_email.setText(SplashScreen.contentGetUserById
					.getString("email"));
			dl_et_idnumber.setText(SplashScreen.contentGetUserById
					.getString("key_id"));
			dl_et_birthday.setText(SplashScreen.contentGetUserById
					.getString("birthday"));
			dl_et_firstname.setText(SplashScreen.contentGetUserById
					.getString("first_name"));
			dl_et_lastname.setText(SplashScreen.contentGetUserById
					.getString("last_name"));
			dl_et_country.setText(SplashScreen.contentGetUserById
					.getString("country"));
			dl_et_website_url.setText(SplashScreen.contentGetUserById
					.getString("website"));
			dl_et_facebook.setText(SplashScreen.contentGetUserById
					.getString("facebook_url"));
			dl_et_google.setText(SplashScreen.contentGetUserById
					.getString("google_url"));
			dl_et_twitter.setText(SplashScreen.contentGetUserById
					.getString("twitter_url"));
			dl_et_about.setText(SplashScreen.contentGetUserById
					.getString("about_me"));

			dl_tv_savechanges.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialogLoading.show();
					
					str_username = dl_et_username.getText().toString();
					str_email = dl_et_email.getText().toString();
					str_idnumber = dl_et_idnumber.getText().toString();
					str_birthday = dl_et_birthday.getText().toString();
					str_firstname = dl_et_firstname.getText().toString();
					str_lastname = dl_et_lastname.getText().toString();
					str_country = dl_et_country.getText().toString();
					website_url = dl_et_website_url.getText().toString();
					str_twitter = dl_et_twitter.getText().toString();
					str_facebook = dl_et_facebook.getText().toString();
					str_google = dl_et_google.getText().toString();
					str_about = dl_et_about.getText().toString();

					editInformation(str_user_id, str_username, str_email,
							str_idnumber, str_birthday, str_firstname,
							str_lastname, str_country, website_url,
							str_twitter, str_facebook, str_google, str_about);
				}
			});

			dl_tv_change_pass.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialogChangePassword(str_user_id, str_user_pss);
				}
			});
		} catch (JSONException e) {
			e.printStackTrace();
		}

		// close dialog
		((TextView) dialog.findViewById(R.id.dl_tv_close))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
	}

	// ============== edit informaiont =========================
	public static String user_id = "", username = "", email = "",
			idnumber = "", birthday = "", firstname = "", lastname = "",
			country = "", website = "", twitter = "", facebook = "",
			google = "", about = "", password_old, password_new, status_update;

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

	public static void editInformation(String str_user_id, String str_username,
			String str_email, String str_idnumber, String str_birthday,
			String str_firstname, String str_lastname, String str_country,
			String website_url, String str_twitter, String str_facebook,
			String str_google, String str_about) {

		user_id = str_user_id;
		username = str_username;
		email = str_email;
		idnumber = str_idnumber;
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

						dialogLoading.dismiss();
						dialog.dismiss();
						switchFragment(new PagerProfile());
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					dialogLoading.dismiss();
					dialog.dismiss();
					switchFragment(new PagerProfile());
					Log.v("json jsonGetMyIdeas null", "jsonGetMyIdeas null");
				}
			} else {
				dialog.dismiss();
				switchFragment(new PagerProfile());
			}
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
				jsonObject.accumulate("key_id", idnumber);
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

	// ------- dialog chang password ----------
	public static void dialogChangePassword(String str_user_id,
			String str_user_pss) {
		final String strId, strPssOld;
		strId = str_user_id;
		strPssOld = str_user_pss;

		final Dialog dialog = new Dialog(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		View dlView = inflater.inflate(R.layout.dialog_profile_edit_password,
				null);
		// perform operation on elements in Layout
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(dlView);

		final EditText dl_et_input = (EditText) dlView
				.findViewById(R.id.dl_et_input);

		// ----- send ---------
		((TextView) dlView.findViewById(R.id.dl_tv_send))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String strPssOld = dl_et_input.getText().toString()
								.trim();
						
						String strInputToMd5 = PagerProfile.strPssToMd5(strPssOld);
						
						Log.v("change pass", strPssOld+"___"+strInputToMd5);
						
						if (strInputToMd5.equals(strPssOld)) {
							dialogChangePasswordConfirm(strId, strPssOld);
							dialog.dismiss();
						} else {
							Toast.makeText(context, "Fail.", 0).show();
						}

						// if(!(strInput.equals(""))){
						// dialogChangePasswordConfirm(strId, strInput);
						// }else{
						// Toast.makeText(context, "Input Password", 0).show();
						// }
					}
				});

		((TextView) dlView.findViewById(R.id.dl_tv_cancle))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});
		 dialog.show();
	}

	public static void dialogChangePasswordConfirm(String str_user_id, String str_user_pssold) {

		final String strId, strPssOld;
		strId = str_user_id;
		strPssOld = str_user_pssold;

		final Dialog dialog = new Dialog(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		View dlView = inflater.inflate(
				R.layout.dialog_profile_edit_password_confirm, null);
		// perform operation on elements in Layout
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(dlView);

		final EditText dl_et_new_pss = (EditText) dlView
				.findViewById(R.id.dl_et_new_pss);
		final EditText dl_et_confirm_pss = (EditText) dlView
				.findViewById(R.id.dl_et_confirm_pss);

		// ----- send ---------
		((TextView) dlView.findViewById(R.id.dl_tv_send))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						String strInputNew = dl_et_new_pss.getText().toString()
								.trim();
						String strInputConf = dl_et_confirm_pss.getText()
								.toString().trim();

						if ((strInputNew.equals(""))
								|| (strInputConf.equals(""))) {
							Toast.makeText(context, "Input Password", 0).show();
						} else if ((strInputNew.equals(strInputConf))) {
							EditProfile.editPassword(strId, strPssOld,
									strInputNew);
							// dialog.dismiss();
						} else {
							// Toast.makeText(
							// context,
							// "Input Password : " + strInputNew + "_"
							// + strInputNew, 0).show();
						}
					}
				});

		((TextView) dlView.findViewById(R.id.dl_tv_cancle))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

		 dialog.show();
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