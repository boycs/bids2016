package com.app.bids;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;
import com.app.bids.LoginRegister.sendAdd;
import com.app.bids.LoginSetalert.sendLogin;
import com.app.bids.PagerProfile.loadData;
import com.app.model.LoginModel.UserModel;
import com.app.model.login.FacebookLoginActivity;
import com.app.model.login.TwitterLoginActivity;
import com.facebook.Session;
import com.facebook.model.GraphUser;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings.Secure;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class LoginActivity extends Activity implements OnClickListener {

	private ImageView facebookIcon;
	private ImageView twitterIcon;
	private ImageView plusIcon;

	private TextView tv_forget;
	private TextView tv_register;

	private Button bt_login_email;
	private EditText et_email, et_password;
	private CheckBox cb_remember;

	private String str_email, str_password;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.login_main);
		
		SplashScreen.userModel = new UserModel();

		facebookIcon = (ImageView) findViewById(R.id.facebook_icon);
		twitterIcon = (ImageView) findViewById(R.id.twitter_icon);
		plusIcon = (ImageView) findViewById(R.id.plus_icon);
		facebookIcon.setOnClickListener(this);
		twitterIcon.setOnClickListener(this);
		plusIcon.setOnClickListener(this);

		tv_forget = (TextView) findViewById(R.id.tv_forget);
		tv_register = (TextView) findViewById(R.id.tv_register);
		bt_login_email = (Button) findViewById(R.id.bt_login_email);
		cb_remember = (CheckBox) findViewById(R.id.cb_remember);

		et_email = (EditText) findViewById(R.id.et_email);
		et_password = (EditText) findViewById(R.id.et_password);

		// -- size icon left et
		Drawable drawableUser = getResources().getDrawable(
				R.drawable.icon_username);
		drawableUser.setBounds(0, 0,
				(int) (drawableUser.getIntrinsicWidth() * 0.5),
				(int) (drawableUser.getIntrinsicHeight() * 0.5));
		ScaleDrawable sdu = new ScaleDrawable(drawableUser, 0, 10f, 10f);
		et_email.setCompoundDrawables(sdu.getDrawable(), null, null, null);

		Drawable drawablePss = getResources().getDrawable(
				R.drawable.icon_password);
		drawablePss.setBounds(0, 0,
				(int) (drawablePss.getIntrinsicWidth() * 0.5),
				(int) (drawablePss.getIntrinsicHeight() * 0.5));
		ScaleDrawable sde = new ScaleDrawable(drawablePss, 0, 10f, 10f);
		et_password.setCompoundDrawables(sde.getDrawable(), null, null, null);

		tv_register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(getApplicationContext(),
						LoginRegister.class));
				finish();

			}
		});

		// initLoginInput(); // init login input *** error
		bt_login_email.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				str_email = et_email.getText().toString().trim();
				str_password = et_password.getText().toString().trim();

				if ((!str_email.equals("")) && (!str_password.equals(""))) {
					sendLoginMail();
				} else {
					final AlertDialog.Builder alertbox = new AlertDialog.Builder(
							LoginActivity.this);
					alertbox.setTitle("  Bids");
					alertbox.setMessage(" Field is not blank.");
					alertbox.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							});
					alertbox.show();
				}

			}
		});

		// GET DEVICE ID => 53e004ada30ad7d2
		SplashScreen.lg_device_id = Secure.getString(getContentResolver(),
				Secure.ANDROID_ID);
		Log.v("DEVICE ID ->>", "" + SplashScreen.lg_device_id);

		// -----------check login fb-----
		// cb_remember.setChecked(true);

		if (SplashScreen.arrLogin != null) {
			if (!(SplashScreen.arrLogin[0][3].toString().trim().equals(""))) {
				et_email.setText(SplashScreen.arrLogin[0][3].toString().trim());
			}
			if ((SplashScreen.arrLogin[0][2]).equals("Y")) { // email
				cb_remember.setChecked(true);
				str_email = SplashScreen.arrLogin[0][3].toString().trim();
				str_password = SplashScreen.arrLogin[0][4].toString().trim();
				sendLoginMail();
			} else if ((SplashScreen.arrLogin[1][2]).equals("Y")) { // facebook
				Intent it = new Intent(getApplicationContext(),
						FacebookLoginActivity.class);
				startActivityForResult(it,
						SplashScreen.TWITTER_LOGIN_REQUEST_CODE);
				finish();
				overridePendingTransition(R.animator.right_to_center,
						R.animator.center_to_right);
			} else if ((SplashScreen.arrLogin[2][2]).equals("Y")) { // googleplus

			} else if ((SplashScreen.arrLogin[3][2]).equals("Y")) { // twitter

			} else {
				// startActivity(new Intent(getApplicationContext(),
				// LoginActivity.class));
				// finish();
			}
		}

	}

	private static final String TWITTER_KEY = "qweYTYiqer5tTeqiq1";
	private static final String TWITTER_SECRET = "wuquUUwy1626661719qw8wwWQHEJQ";

	@Override
	public void onClick(View v) {
		if (v == facebookIcon) {
			// startActivity(new Intent(getApplicationContext(),
			// FragmentChangeActivity.class));
			// finish();

			Intent it = new Intent(getApplicationContext(),
					FacebookLoginActivity.class);
			startActivityForResult(it, SplashScreen.TWITTER_LOGIN_REQUEST_CODE);
			finish();
			overridePendingTransition(R.animator.right_to_center,
					R.animator.center_to_right);
		} else if (v == plusIcon) {
			// Intent it = new Intent(act, PlusLoginActivity.class);
			// startActivityForResult(it, LOGIN_FACEBOOK_REQUEST);
		} else if (v == twitterIcon) {
			Intent it = new Intent(getApplicationContext(),
					TwitterLoginActivity.class);
			startActivityForResult(it, SplashScreen.LOGIN_FACEBOOK_REQUEST);
			finish();
			overridePendingTransition(R.animator.right_to_center,
					R.animator.center_to_right);
		}
	}

	// ============ send login =========
	static Dialog dialogLoading;

	String resultLoginMail = "";
	JSONObject jsoLoginMailDataAll = null;

	private void sendLoginMail() {
		dialogLoading = new Dialog(LoginActivity.this);
		dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogLoading.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialogLoading.setContentView(R.layout.progress_bar);
		dialogLoading.setCancelable(false);
		dialogLoading.setCanceledOnTouchOutside(false);

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
			dialogLoading.show();
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

				Log.v("url", "" + url);
				Log.v("str_name", "" + str_email);
				Log.v("str_password", "" + str_password);

				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();

				jsonObject.accumulate("username", str_email);
				jsonObject.accumulate("password", str_password);

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
					resultLoginMail = AFunctionOther.convertInputStreamToString(inputStream);
				else
					resultLoginMail = "Did not work!";

				Log.v("result login : ", "" + resultLoginMail);
				// {"status":"ok","message":"Get data Success.","dataAll":{"user_id":"1587","username":"k2",
				// "first_name":"k2","email":"k2@gmail.com","user_type":"pro","followers":"0","following":"0",
				// "ideas_views":"0","ideas_like":"0","published_ideas_count":"0","pushished_ideas":"0"}}

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
					JSONObject jsoLogin = new JSONObject(resultLoginMail);
					if (jsoLogin.getString("status").equals("ok")) {
						jsoLoginMailDataAll = jsoLogin.getJSONObject("dataAll");

						// --- intent fragmentchageactivity
						SplashScreen.userModel.user_id = jsoLoginMailDataAll
								.getString("user_id");
						SplashScreen.userModel.type = SplashScreen.userModel.NONE_TYPE;

						if (cb_remember.isChecked()) {
							SplashScreen.myDb.UpdateDataEmailLogin("1", "Y",
									str_email, str_password);
						} else {
							SplashScreen.myDb.UpdateDataEmailLogin("1", "N",
									str_email, str_password);
						}
						initLoadProfile();

					} else {
						dialogLoading.dismiss();
						final AlertDialog.Builder alertbox = new AlertDialog.Builder(
								LoginActivity.this);
						alertbox.setTitle("  Bids");
						alertbox.setMessage("login Fail!!");
						alertbox.setNeutralButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface arg0,
											int arg1) {
										et_email.setText("");
										et_password.setText("");
									}
								});
						alertbox.show();
					}
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			} else {
				dialogLoading.dismiss();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getApplicationContext());
				builder.setMessage("เชื่อมต่อล้มเหลว..")
						.setCancelable(false)
						.setPositiveButton("Login",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {

										sendLoginMail();

									}
								})
						.setNegativeButton("Cancel",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {
										dialog.cancel();
									}
								});
				builder.show();
			}
			dialogLoading.dismiss();
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
						Log.v("SplashScreen.contentGetUserById",""+SplashScreen.contentGetUserById);

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

	// init login input
	private void initLoginInput() {
		SplashScreen.myDb = new LinnaeusDatabase(this);
		SplashScreen.myDb.getWritableDatabase(); // First method
		SplashScreen.arrLogin = SplashScreen.myDb.SelectDataLogin();
		// get data schedule
		if (SplashScreen.arrLogin != null) {
			int countDB = SplashScreen.arrLogin.length;
			if ((SplashScreen.arrLogin[countDB - 1][4]).equals("Y")) {
				et_email.setText("" + SplashScreen.arrLogin[countDB - 1][2]);
				et_password.setText("" + SplashScreen.arrLogin[countDB - 1][3]);
				// cb_remember.setChecked(true);

				str_email = et_email.getText().toString().trim();
				str_password = et_password.getText().toString().trim();
				// sendLoginSetAlert(); // login auto
			} else {
				et_email.setText("" + SplashScreen.arrLogin[countDB - 1][2]);
				// et_password.setText("" + arrMember[countDB - 1][3]);
			}
		} else {
			// ไม่มีข้อมูลผู้ใช้
		}
	}

	// login fail
	private void alertLoginFail() {
		final AlertDialog.Builder alert = new AlertDialog.Builder(
				LoginActivity.this);
		alert.setTitle("  Bids");
		alert.setMessage("login Fail!!");
		alert.setNeutralButton("OK", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		alert.show();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish();
			overridePendingTransition(R.animator.right_to_center,
					R.animator.center_to_right);
		}
		return super.onKeyDown(keyCode, event);
	}

}