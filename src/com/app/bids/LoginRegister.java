package com.app.bids;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;
import com.app.model.LoginModel.UserModel;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ScaleDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

@SuppressLint("NewApi")
public class LoginRegister extends Activity {

	private String str_name, str_email, str_password, str_password_confirm,
			str_birthday, str_user_id;

	EditText et_name, et_email, et_pss, et_pss_con;
	CalendarView cv_birthday;
	Button bt_register;
	TextView tv_birthday;

	private int mYear;
	private int mMonth;
	private int mDay;
	static final int DATE_DIALOG_ID = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_register);

		et_name = (EditText) findViewById(R.id.et_name);
		et_email = (EditText) findViewById(R.id.et_email);
		et_pss = (EditText) findViewById(R.id.et_pss);
		et_pss_con = (EditText) findViewById(R.id.et_pss_confirm);
		// et_idnumber = (EditText) findViewById(R.id.et_idnumber);
		tv_birthday = (TextView) findViewById(R.id.tv_birthday);
		bt_register = (Button) findViewById(R.id.bt_register);

		// -- size icon left et
		Drawable drawableUser = getResources().getDrawable(
				R.drawable.icon_username);
		drawableUser.setBounds(0, 0,
				(int) (drawableUser.getIntrinsicWidth() * 0.5),
				(int) (drawableUser.getIntrinsicHeight() * 0.5));
		ScaleDrawable sdu = new ScaleDrawable(drawableUser, 0, 10f, 10f);
		et_name.setCompoundDrawables(sdu.getDrawable(), null, null, null);

		Drawable drawableEm = getResources().getDrawable(
				R.drawable.icon_email);
		drawableEm.setBounds(0, 0,
				(int) (drawableEm.getIntrinsicWidth() * 0.5),
				(int) (drawableEm.getIntrinsicHeight() * 0.5));
		ScaleDrawable sdem = new ScaleDrawable(drawableEm, 0, 10f, 10f);
		et_email.setCompoundDrawables(sdem.getDrawable(), null, null, null);

		Drawable drawablePss = getResources().getDrawable(
				R.drawable.icon_password);
		drawablePss.setBounds(0, 0,
				(int) (drawablePss.getIntrinsicWidth() * 0.5),
				(int) (drawablePss.getIntrinsicHeight() * 0.5));
		ScaleDrawable sdp = new ScaleDrawable(drawablePss, 0, 10f, 10f);
		et_pss.setCompoundDrawables(sdp.getDrawable(), null, null, null);

		Drawable drawablePssc = getResources().getDrawable(
				R.drawable.icon_password);
		drawablePssc.setBounds(0, 0,
				(int) (drawablePssc.getIntrinsicWidth() * 0.5),
				(int) (drawablePssc.getIntrinsicHeight() * 0.5));
		ScaleDrawable sdps = new ScaleDrawable(drawablePssc, 0, 10f, 10f);
		et_pss_con.setCompoundDrawables(sdps.getDrawable(), null, null, null);

		Drawable drawableBd = getResources().getDrawable(
				R.drawable.icon_birthday);
		drawableBd.setBounds(0, 0,
				(int) (drawableBd.getIntrinsicWidth() * 0.5),
				(int) (drawableBd.getIntrinsicHeight() * 0.5));
		ScaleDrawable sdbd = new ScaleDrawable(drawableBd, 0, 10f, 10f);
		tv_birthday.setCompoundDrawables(sdbd.getDrawable(), null, null, null);

		// add a click listener to the button
		tv_birthday.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				showDialog(DATE_DIALOG_ID);
			}
		});

		// get the current date
		final Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mDay = c.get(Calendar.DAY_OF_MONTH);

		// display the current date (this method is below)
		updateDisplay();

		// cv_birthday = (CalendarView) findViewById(R.id.cv_birthday);
		// cv_birthday.setOnDateChangeListener(new OnDateChangeListener() {
		// @Override
		// public void onSelectedDayChange(CalendarView view, int year,
		// int month, int dayOfMonth) {
		// Toast.makeText(
		// getBaseContext(),
		// "Selected Date is\n\n" + dayOfMonth + " / " + month
		// + " / " + year, Toast.LENGTH_LONG).show();
		// }
		// });

		bt_register.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				str_name = et_name.getText().toString().trim();
				str_email = et_email.getText().toString().trim();
				str_password = et_pss.getText().toString().trim();
				str_password_confirm = et_pss_con.getText().toString().trim();
				// str_idnumber = et_idnumber.getText().toString().trim();

				if ((isEmpty(str_name)) || (isEmpty(str_email))
						|| (isEmpty(str_password))
						|| (isEmpty(str_password_confirm))) {

					final AlertDialog.Builder alert = new AlertDialog.Builder(
							LoginRegister.this);
					alert.setTitle(" Bids");
					alert.setMessage("Field is not empty");
					alert.setPositiveButton("OK",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									dialog.cancel();
								}
							});
					alert.show();
				} else {
					if (isEmailValid(str_email)) {
						if ((str_password.equals(str_password_confirm))) {
							sendAddRegister(); // add register to service
						} else {
							final AlertDialog.Builder alert = new AlertDialog.Builder(
									LoginRegister.this);
							alert.setTitle(" Bids");
							alert.setMessage("Confirm Password is invalid");
							alert.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.cancel();
											// set input empty
											et_pss_con.setText("");
										}
									});
							alert.show();
						}
					} else {
						final AlertDialog.Builder alert = new AlertDialog.Builder(
								LoginRegister.this);
						alert.setTitle(" Bids");
						alert.setMessage("E-mail is invalid");
						alert.setPositiveButton("OK",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.cancel();
										// set input empty
										et_email.setText("");
									}
								});
						alert.show();
					}
				}
			}
		});
	}

	boolean isEmailValid(CharSequence email) {
		return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
	}

	@Override
	protected Dialog onCreateDialog(int id) {
		switch (id) {
		case DATE_DIALOG_ID:
			return new DatePickerDialog(this, mDateSetListener, mYear, mMonth,
					mDay);
		}
		return null;
	}

	// updates the date we display in the TextView
	private void updateDisplay() {
		tv_birthday.setText(new StringBuilder()
				// Month is 0 based so add 1
				.append(mMonth + 1).append("-").append(mDay).append("-")
				.append(mYear).append(" "));

		tv_birthday.setTag(new StringBuilder()
				// Month is 0 based so add 1
				.append(mYear).append("-").append(mMonth + 1).append("-")
				.append(mDay).append(" "));
	}

	// the callback received when the user "sets" the date in the dialog
	private DatePickerDialog.OnDateSetListener mDateSetListener = new DatePickerDialog.OnDateSetListener() {
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			mYear = year;
			mMonth = monthOfYear;
			mDay = dayOfMonth;
			updateDisplay();
		}
	};

	// add data to db
	private void sendInsertDb() {
		SplashScreen.myDb.InsertDataMemberLogin(str_name, str_email,
				str_password, "Y");

		Toast.makeText(getApplicationContext(), "Register Success!!",
				Toast.LENGTH_SHORT).show();

	}

	// ============ add Register =========
	static Dialog dialogLoading;

	String resultRegister = "";
	JSONObject jsoRegisterDataAll = null;

	private void sendAddRegister() {
		dialogLoading = new Dialog(LoginRegister.this);
		dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogLoading.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialogLoading.setContentView(R.layout.progress_bar);
		dialogLoading.setCancelable(false);
		dialogLoading.setCanceledOnTouchOutside(false);

		sendAdd resp = new sendAdd();
		resp.execute();
	}

	public class sendAdd extends AsyncTask<Void, Void, Void> {
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

			String url = SplashScreen.url_bidschart + "/service/registerByMail";

			String json = "";
			InputStream inputStream = null;

			try {

				// String s_name = URLEncoder.encode(str_name, "UTF-8");
				// String s_email = URLEncoder.encode(str_email, "UTF-8");
				// String s_pass = URLEncoder.encode(str_password, "UTF-8");

				Log.v("url", "" + url);
				Log.v("str_name", "" + str_name);
				Log.v("str_password", "" + str_password);
				Log.v("str_email", "" + str_email);

				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();

				jsonObject.accumulate("username", str_name);
				jsonObject.accumulate("password", str_password);
				jsonObject.accumulate("email", str_email);

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
					resultRegister = AFunctionOther.convertInputStreamToString(inputStream);
				else
					resultRegister = "Did not work!";

				Log.v("result register : ", "" + resultRegister);
				// {"status":"ok","message":"Get data Success.","dataAll":{"user_id":"1586","username":"k",
				// "first_name":"k","email":"k@gmail.com","user_type":"pro","followers":"0","following":"0",
				// "ideas_views":"0","ideas_like":"0","published_ideas_count":"0","pushished_ideas":"0","packet_price":0,
				// "packet_name":"pro beta 14 day","packet_id":"proBeta","package":"proBeta","start":"2016-03-10 15:31:36",
				// "expire":"2016-03-24 15:31:36"}}

				// {"status":"error","message":"The username has already been taken.  The email must be a valid email address."}

				// {"status":"error","message":"  The email has already been taken."}

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
					JSONObject jsoRegister = new JSONObject(resultRegister);
					String msgError = jsoRegister.getString("message");
					if (jsoRegister.getString("status").equals("ok")) {
						jsoRegisterDataAll = jsoRegister
								.getJSONObject("dataAll");

						Log.v("jsoRegisterDataAll : ", "" + jsoRegisterDataAll);

						str_user_id = jsoRegisterDataAll.getString("user_id");
						sendEditProfileIdCard(); // add birthday, idnumber
						sendInsertDb();

						// --- intent fragmentchageactivity
						SplashScreen.userModel.user_id = jsoRegisterDataAll
								.getString("user_id");
						SplashScreen.userModel.status = jsoRegister
								.getString("status");
						SplashScreen.contentGetUserById = jsoRegisterDataAll;
						SplashScreen.userModel.type = SplashScreen.userModel.NONE_TYPE;

						startActivity(new Intent(getApplicationContext(),
								FragmentChangeActivity.class));

					} else {
						dialogLoading.dismiss();
						// prepare the alert box
						final AlertDialog.Builder alertbox = new AlertDialog.Builder(
								LoginRegister.this);
						alertbox.setTitle("  Bids");
						alertbox.setMessage(msgError);
						alertbox.setNeutralButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface arg0,
											int arg1) {
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
			dialogLoading.dismiss();
		}
	}

	// ============ Edit ProfileIdCard =========
	String resultEditProfileIdCard = "";
	JSONObject jsoEditProfileIdCardDataAll = null;

	private void sendEditProfileIdCard() {
		sendEdit resp = new sendEdit();
		resp.execute();
	}

	public class sendEdit extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;
		private JSONObject json;
		private String stateSend = "";

		String temp = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialogLoading.dismiss();
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

				jsonObject.accumulate("user_id", str_user_id);
				// jsonObject.accumulate("key_id", str_idnumber);
				jsonObject.accumulate("birthday", tv_birthday.getTag()
						.toString().trim());

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

	// ck empty input
	private boolean isEmpty(String str) {
		if (str.length() > 0) {
			return false;
		} else {
			return true;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			startActivity(new Intent(getApplicationContext(),
					LoginActivity.class));
			finish();
			overridePendingTransition(R.animator.right_to_center,
					R.animator.center_to_right);
		}
		return super.onKeyDown(keyCode, event);
	}

}
