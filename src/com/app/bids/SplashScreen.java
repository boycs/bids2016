package com.app.bids;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;
import com.app.bids.AnimatedGifImageView.TYPE;
import com.app.bids.LoginActivity.loadData;
import com.app.bids.LoginActivity.sendLogin;
import com.app.model.LoginModel.UserModel;
import com.app.model.login.FacebookLoginActivity;
import com.facebook.Session;
import com.facebook.model.GraphUser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SplashScreen extends Activity {

	private Handler myHandler;
	Runnable runnable;
	long delay_time;
	long time = 3000L;

	public static JSONObject contentSymbol_Set = null;

	boolean switchMe = false;

	// ----- db login
	public static String DatabaseName = "bidschart_db";
	SQLiteDatabase db;
	public static LinnaeusDatabase myDb;
	public static LinnaeusDatabase myDbFav;
	public static String[][] arrLogin;
	public static String[][] arrMember;
	public static String[][] arrFav;

	public static String lg_device_id = "";
	public static String lg_id_member = "";
	public static String lg_name = "";
	public static String lg_id_fb = "";
	public static String lg_email = "";
	public static String lg_platform = "";

	public static JSONObject contentGetUserById = null;

	public static UserModel userModel = new UserModel();
	Session session;
	public static final int LOGIN_FACEBOOK_REQUEST = 212;
	public static final int TWITTER_LOGIN_REQUEST_CODE = 1;
	public static GraphUser graphUser;
	public static String deviceId = "";

	public static String url_bidschart = "http://www.bidschart.com";
	public static String wv_chartiq_url = "http://bidschart.com/rework/android/";
	public static String url_bidschart_chart = "http://bidschart.com/images/sparkline/";
	public static String url_bidschart_fund = "http://fund.bidschart.com/imagesV2/blg/";
	public static String url_websocket = "ws1://bidschart.com:4504";
	public static String url_websocket2 = "ws1://bidschart.com:8080";

	// ----------- font --------
	public static Typeface fontSliding;
	public static Typeface fontDefault;

	// ----------- MPChart ----
	public static ArrayList<Integer> mpChartArrColor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		// this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		requestWindowFeature(Window.FEATURE_NO_TITLE);

		setContentView(R.layout.splash_screen);

		// set sliding
		fontSliding = Typeface.createFromAsset(getApplicationContext()
				.getAssets(), "BPdotsUnicasePlus.otf");

		// set font
		fontDefault = Typeface.createFromAsset(getApplicationContext()
				.getAssets(), "SF-UI-Display-Regular.otf");

		// --------- mpchart color ------
		mpChartArrColor = new ArrayList<Integer>();
		mpChartArrColor.add(Color.rgb(99, 87, 103));
		mpChartArrColor.add(Color.rgb(29, 186, 157));
		mpChartArrColor.add(Color.rgb(80, 175, 205));
		mpChartArrColor.add(Color.rgb(43, 129, 202));
		mpChartArrColor.add(Color.rgb(105, 100, 173));
		mpChartArrColor.add(Color.rgb(70, 87, 117));
		mpChartArrColor.add(Color.rgb(169, 65, 100));
		mpChartArrColor.add(Color.rgb(235, 57, 117));
		mpChartArrColor.add(Color.rgb(238, 152, 49));
		mpChartArrColor.add(Color.rgb(238, 200, 49));

		// ID : email = 1, facebook = 2, googleplus = 3, twitter = 4
		myDb = new LinnaeusDatabase(this);
		myDb.getWritableDatabase(); // First method
		arrLogin = SplashScreen.myDb.SelectDataLogin();

		myHandler = new Handler();
		initLogin();

		// myHandler.postDelayed(new Runnable() {
		// public void run() {
		// if (SplashScreen.arrLogin != null) {
		// if ((SplashScreen.arrLogin[0][2]).equals("Y")) { // email
		// sendLoginMail();
		// } else if ((SplashScreen.arrLogin[1][2]).equals("Y")) { // facebook
		// Intent it = new Intent(getApplicationContext(),
		// FacebookLoginActivity.class);
		// startActivityForResult(it,
		// SplashScreen.TWITTER_LOGIN_REQUEST_CODE);
		// finish();
		// // } else if ((SplashScreen.arrLogin[2][2]).equals("Y"))
		// // { // googleplus
		// // } else if ((SplashScreen.arrLogin[3][2]).equals("Y"))
		// // { // twitter
		// } else {
		// startActivity(new Intent(getApplicationContext(),
		// LoginActivity.class));
		// finish();
		// }
		// }
		// }
		// }, 2000);
	}

	// check connect network
	public void initLogin() {
		if (isNetworkAvailable()) {
			runnable = new Runnable() {
				public void run() {
					if (SplashScreen.arrLogin != null) {
						if ((SplashScreen.arrLogin[0][2]).equals("Y")) { // email
							sendLoginMail();
						} else if ((SplashScreen.arrLogin[1][2]).equals("Y")) { // facebook
							Intent it = new Intent(getApplicationContext(),
									FacebookLoginActivity.class);
							startActivityForResult(it,
									SplashScreen.TWITTER_LOGIN_REQUEST_CODE);
							finish();
						} else {
							startActivity(new Intent(getApplicationContext(),
									LoginActivity.class));
							finish();
						}
					}
				}
			};
		} else {
			final AlertDialog.Builder alertbox = new AlertDialog.Builder(
					SplashScreen.this);
			alertbox.setTitle("  Bids");
			alertbox.setMessage(" internet is not available.");
			alertbox.setPositiveButton("OK",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							initLogin();
						}
					});
			alertbox.setNegativeButton("Cancel",
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							finish();
						}
					});
			alertbox.show();
		}
	}

	// check connect network
	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager
				.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	// http://www.akexorcist.com/2013/05/android-code-splash-screen.html
	public void onResume() {
		super.onResume();
		delay_time = time;
		myHandler.postDelayed(runnable, delay_time);
		time = System.currentTimeMillis();
	}

	public void onPause() {
		super.onPause();
		myHandler.removeCallbacks(runnable);
		time = delay_time - (System.currentTimeMillis() - time);
	}

	// ============ send login =========
	static Dialog dialogLoading;

	String resultLoginMail = "";
	JSONObject jsoLoginMailDataAll = null;

	private void sendLoginMail() {
		// dialogLoading = new Dialog(SplashScreen.this);
		// dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// dialogLoading.getWindow().setBackgroundDrawable(
		// new ColorDrawable(android.graphics.Color.TRANSPARENT));
		// dialogLoading.setContentView(R.layout.progress_bar);
		// dialogLoading.setCancelable(false);
		// dialogLoading.setCanceledOnTouchOutside(false);

		sendLogin resp = new sendLogin();
		resp.execute();
	}

	public class sendLogin extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;
		private JSONObject json;
		private String stateSend = "";

		String temp = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// dialogLoading.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			String url = "http://www.bidschart.com/service/loginByMail";

			String json = "";
			InputStream inputStream = null;

			try {
				// String s_name = URLEncoder.encode(str_name, "UTF-8");
				// String s_email = URLEncoder.encode(str_email, "UTF-8");
				// String s_pass = URLEncoder.encode(str_password, "UTF-8");

				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();

				jsonObject.accumulate("username", SplashScreen.arrLogin[0][3]);
				jsonObject.accumulate("password", SplashScreen.arrLogin[0][4]);

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
					resultLoginMail = AFunctionOther
							.convertInputStreamToString(inputStream);
				else
					resultLoginMail = "Did not work!";

				Log.v("result login : ", "" + resultLoginMail);
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
					JSONObject jsoLogin = new JSONObject(resultLoginMail);
					if (jsoLogin.getString("status").equals("ok")) {
						jsoLoginMailDataAll = jsoLogin.getJSONObject("dataAll");

						// --- intent fragmentchageactivity
						SplashScreen.userModel.user_id = jsoLoginMailDataAll
								.getString("user_id");
						SplashScreen.userModel.type = SplashScreen.userModel.NONE_TYPE;

						initLoadProfile();

					} else {
						startActivity(new Intent(getApplicationContext(),
								LoginActivity.class));
						finish();

					}
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			} else {
				// dialogLoading.dismiss();
				startActivity(new Intent(getApplicationContext(),
						LoginActivity.class));
				finish();

			}
			// dialogLoading.dismiss();
		}
	}

	// ============== Load Data all =============
	private void initLoadProfile() {
		loadData resp = new loadData();
		resp.execute();
	}

	public class loadData extends AsyncTask<Void, Void, Void> {
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

						startActivity(new Intent(getApplicationContext(),
								FragmentChangeActivity.class));
						finish();
						Toast.makeText(getApplicationContext(),
								"Login Success", Toast.LENGTH_SHORT).show();

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.v("json jsonGetMyIdeas null", "jsonGetMyIdeas null");
				}
			} else {
			}
		}
	}

}
