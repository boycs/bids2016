package com.app.model.login;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Settings.Secure;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.app.bids.AFunctionOther;
import com.app.bids.FragmentChangeActivity;
import com.app.bids.LoginActivity;
import com.app.bids.LoginRegister;
import com.app.bids.LoginSetalert;
import com.app.bids.PagerWatchList;
import com.app.bids.PagerWatchlistDetail;
import com.app.bids.R;
import com.app.bids.ReadJson;
import com.app.bids.SplashScreen;
import com.app.bids.LoginRegister.sendAdd;
import com.app.model.LoginModel.UserModel;

public class LoginDialog implements OnClickListener {

	private static Dialog dialogLogin;
	private static Dialog dialogRegister;
	private Activity activity;
	private ImageView facebookIcon;
	private ImageView twitterIcon;
	private ImageView plusIcon;
	private Button bt_login_email;
	private TextView tv_register;
	private FragmentChangeActivity mInterface;

	static Dialog dialogLoading;

	public LoginDialog(Activity activity, FragmentChangeActivity mInterface) {
		this.activity = activity;
		this.dialogLogin = new Dialog(activity);
		this.dialogRegister = new Dialog(activity);
		this.mInterface = mInterface;

		// --------- dialogLoading
		dialogLoading = new Dialog(this.activity);
		dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogLoading.setContentView(R.layout.progress_bar);
		dialogLoading.setCancelable(false);
		dialogLoading.setCanceledOnTouchOutside(false);

		dialogLogin();
		dialogRegister();
	}

	private void dialogLogin() {
		dialogLogin.setContentView(R.layout.dialog_login_main);

		facebookIcon = (ImageView) dialogLogin.findViewById(R.id.facebook_icon);
		twitterIcon = (ImageView) dialogLogin.findViewById(R.id.twitter_icon);
		plusIcon = (ImageView) dialogLogin.findViewById(R.id.plus_icon);
		bt_login_email = (Button) dialogLogin.findViewById(R.id.bt_login_email);
		tv_register = (TextView) dialogLogin.findViewById(R.id.tv_register);

		facebookIcon.setOnClickListener(this);
		twitterIcon.setOnClickListener(this);
		plusIcon.setOnClickListener(this);
		bt_login_email.setOnClickListener(this);
		tv_register.setOnClickListener(this);
	}

	public static void show() {
		dialogLogin.show();
	}

	public static void dissmiss() {
		dialogLogin.dismiss();
	}

	String strEmail;
	String strPss;

	@Override
	public void onClick(View v) {
		if (v == facebookIcon) {
			mInterface.onFacebookClick();
		} else if (v == plusIcon) {
			// mInterface.onPlusClick();
		} else if (v == twitterIcon) {
			 mInterface.onTwitterClick();
		} else if (v == tv_register) {
			dialogRegister.show();
		} else if (v == bt_login_email) {
			EditText et_email = (EditText) dialogLogin
					.findViewById(R.id.et_email);
			EditText et_password = (EditText) dialogLogin
					.findViewById(R.id.et_password);
			strEmail = et_email.getText().toString();
			strPss = et_password.getText().toString();
			if ((strEmail.equals("")) || (strPss.equals(""))) {
				Toast.makeText(activity, "Input Data.", Toast.LENGTH_SHORT)
						.show();
			} else {
				sendLoginSetAlert();
			}
		}
	}

	// -------------------- send login
	String resultLogin = "";

	private void sendLoginSetAlert() {
		sendLogin resp = new sendLogin();
		resp.execute();
	}

	public class sendLogin extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;
		private JSONObject json;
		private String stateSend = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			dialogLoading.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			// String url1 =
			// "http://api.xset.info/controllers/servicePhone.php?fpost=login"
			// + "&email=po1@hotmail.com"
			// + "&pass=1234"
			// + "&id_fb=-"
			// + "&platform=mail"
			// + "&name=-"
			// + "&serial_number=xasfdasdfasdfsdf"
			// + "&os=android";

			String url = SplashScreen.url_bidschart
					+ "/service/loginByMail";

			String json = "";
			InputStream inputStream = null;

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("username", strEmail);
				jsonObject.accumulate("password", strPss);

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
					resultLogin = AFunctionOther.convertInputStreamToString(inputStream);
				else
					resultLogin = "Did not work!";

				Log.v("result login mail >> ", "" + resultLogin);
				
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
				Log.v("get login", "" + resultLogin);

//				get login(10338): {"status":"ok","message":"Get data Success.","dataAll":{"user_id":"1040","username":"bids","first_name":"bids","email":"bids@gmail.com","user_type":"pro","followers":"0","following":"0","ideas_views":"0","ideas_like":"0","published_ideas_count":"0","pushished_ideas":"0"}}

				JSONObject obj;
				try {
					obj = new JSONObject(resultLogin);

//					stateSend = obj.getString("status");

					if (obj.getString("status").equals("ok")) {

						dialogLoading.dismiss();
						dialogLogin.dismiss();
												
						SplashScreen.contentGetUserById = obj
								.getJSONObject("dataAll");
						
						SplashScreen.userModel.user_id = 	SplashScreen.contentGetUserById.getString("user_id");		
						SplashScreen.userModel.type = 0;
						
						PagerWatchList.handlerProfile.sendEmptyMessage(1);

//						 loadInitDataLogin(); // load init login success

					} else {
						dialogLoading.dismiss();
						dialogLogin.dismiss();
						Toast.makeText(activity, "Login Fail..",
								Toast.LENGTH_SHORT).show();
					}

				} catch (JSONException e) {
					e.printStackTrace();
					dialogLoading.dismiss();
					dialogLogin.dismiss();
				}
			} else {
				dialogLoading.dismiss();
				dialogLogin.dismiss();
			}
		}
	}

	// -------------dialog register
	private String str_name, str_email, str_password, str_password_confirm;

	private void dialogRegister() {
		dialogRegister.setContentView(R.layout.dialog_login_register);

		final EditText et_name = (EditText) dialogRegister
				.findViewById(R.id.et_name);
		final EditText et_email = (EditText) dialogRegister
				.findViewById(R.id.et_email);
		final EditText et_pss = (EditText) dialogRegister
				.findViewById(R.id.et_pss);
		final EditText et_pss_con = (EditText) dialogRegister
				.findViewById(R.id.et_pss_confirm);

		((Button) dialogRegister.findViewById(R.id.bt_register))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						str_name = et_name.getText().toString().trim();
						str_email = et_email.getText().toString().trim();
						str_password = et_pss.getText().toString().trim();
						str_password_confirm = et_pss_con.getText().toString()
								.trim();

						if ((isEmpty(str_name)) || (isEmpty(str_email))
								|| (isEmpty(str_password))
								|| (isEmpty(str_password_confirm))) {
							final AlertDialog.Builder alert = new AlertDialog.Builder(
									activity);
							alert.setTitle(" กรุณาใส่ข้อมูลให้ครบ!!");
							alert.setPositiveButton("ตกลง",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.cancel();
										}
									});
							alert.show();
						} else {
							if ((str_password.equals(str_password_confirm))) {
								sendAddRegister(); // add register to service
							} else {
								final AlertDialog.Builder alert = new AlertDialog.Builder(
										activity);
								alert.setTitle(" ยืนยันรหัสผ่านไม่ถูกต้อง!!");
								alert.setPositiveButton("ตกลง",
										new DialogInterface.OnClickListener() {
											@Override
											public void onClick(
													DialogInterface dialog,
													int which) {
												dialog.cancel();
												// set input empty
												et_pss.setText("");
												et_pss_con.setText("");
											}
										});
								alert.show();
							}
						}
					}
				});

	}

	// add data to db
	private void sendInsertDb() {
		SplashScreen.myDb.InsertDataMember("" + str_name, "" + str_email, ""
				+ str_password, "y");

		Toast.makeText(activity, "Register Success..",
				Toast.LENGTH_SHORT).show();

		dialogRegister.dismiss();
		PagerWatchList.handlerProfile.sendEmptyMessage(1);

	}

	// add register to service
//	private ProgressDialog progress;

	private void sendAddRegister() {
//		progress = new ProgressDialog(activity);
//		progress.setTitle("Connecting...");
//		progress.setMessage("Please wait.");
//		progress.setCancelable(false);
//		progress.setIndeterminate(true);
//		progress.setCanceledOnTouchOutside(false);

		sendAdd resp = new sendAdd();
		resp.execute();
	}

	// -------------------- load add register
	public class sendAdd extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;
		private JSONObject json;
		private String stateSend = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
//			progress.show();
			
			dialogLoading.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			String urlO = "http://api.xset.info/controllers/servicePhone.php?fpost=register";
			String url1;
			String url;

			// api.xset.info/controllers/servicePhone.php?fpost=register&name=boy&email=boy@hot&pass=boyp&id_fb=-&platform=mail

			try {
				String s_name = URLEncoder.encode(str_name, "UTF-8");
				String s_email = URLEncoder.encode(str_email, "UTF-8");
				String s_pass = URLEncoder.encode(str_password, "UTF-8");

				// String uri =
				// "http://api.xset.info/controllers/servicePhone.php?fpost=register"
				// + "&name=co"
				// + "&email=co@co.com"
				// + "&pass=co"
				// + "&id_fb=-"
				// + "&platform=mail"
				// + "&serial_number=53e004ada30ad7d2"
				// + "&os=android";

				url = "http://api.xset.info/controllers/servicePhone.php?fpost=register"
						+ "&name="
						+ s_name
						+ "&email="
						+ s_email
						+ "&pass="
						+ s_pass
						+ "&id_fb=-"
						+ "&platform=mail"
						+ "&serial_number="
						+ LoginSetalert.lg_device_id
						+ "&os=android";
				Log.v("addRegister", "" + url);

				json = ReadJson.readJsonObjectFromUrl(url);

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
				if (json != null) {
					try {
						stateSend = json.getString("status");
						Log.v("stateSendAdd", "" + json);
						Log.v("stateSendAdd", "" + stateSend);
						if (stateSend.equals("false")) {
//							progress.dismiss();
							
							dialogLoading.dismiss();
							
							// prepare the alert box
							final AlertDialog.Builder alertbox = new AlertDialog.Builder(activity);
							alertbox.setTitle("  สมัครสมาชิกไม่สำเร็จ");
							alertbox.setNeutralButton("ตกลง",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface arg0, int arg1) {
											// add data to db
											// sendInsertDb();
											// startActivity(new
											// Intent(getApplicationContext(),
											// LoginRegister.class));
											// finish();
										}
									});
							alertbox.show();

						} else {
							sendInsertDb();
							// Toast.makeText(getApplicationContext(),
							// "Success",
							// Toast.LENGTH_SHORT).show();
							// startActivity(new Intent(getApplicationContext(),
							// LoginActivity.class));
							// finish();
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.v("json null", "null");
				}

			} else {
//				progress.dismiss();
				
				dialogLoading.dismiss();
				AlertDialog.Builder builder = new AlertDialog.Builder(activity);
				builder.setMessage("Connect Fail...")
						.setCancelable(false)
						.setPositiveButton("Send",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {

										sendAddRegister();

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
//			progress.dismiss();
			
			dialogLoading.dismiss();

		}
	}

	// ck empty input
	private boolean isEmpty(String str) {
		if (str.length() > 0) {
			return false;
		} else {
			return true;
		}
	}
}
