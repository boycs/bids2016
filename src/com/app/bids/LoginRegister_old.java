package com.app.bids;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Calendar;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.app.bids.R;

@SuppressLint("NewApi")
public class LoginRegister_old extends Activity {

	private String str_name, str_email, str_password, str_password_confirm,
			str_birthday, str_idnumber;

	private int str_birthday_y, str_birthday_m, str_birthday_d;

	EditText et_name, et_email, et_pss, et_pss_con, et_idnumber;
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
		et_idnumber = (EditText) findViewById(R.id.et_idnumber);
		bt_register = (Button) findViewById(R.id.bt_register);

		// add a click listener to the button
		tv_birthday = (TextView) findViewById(R.id.tv_birthday);
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
				str_idnumber = et_idnumber.getText().toString().trim();

				if ((isEmpty(str_name)) || (isEmpty(str_email))
						|| (isEmpty(str_password))
						|| (isEmpty(str_password_confirm))
						|| (isEmpty(str_idnumber))) {
					final AlertDialog.Builder alert = new AlertDialog.Builder(
							LoginRegister_old.this);
					alert.setTitle(" กรุณาใส่ข้อมูลให้ครบ!!");
					alert.setPositiveButton("ตกลง",
							new DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog,
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
								LoginRegister_old.this);
						alert.setTitle(" ยืนยันรหัสผ่านไม่ถูกต้อง!!");
						alert.setPositiveButton("ตกลง",
								new DialogInterface.OnClickListener() {
									@Override
									public void onClick(DialogInterface dialog,
											int which) {
										dialog.cancel();
										// set input empty
										et_pss_con.setText("");
									}
								});
						alert.show();
					}
				}
			}
		});
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
		SplashScreen.myDb.InsertDataMember("" + str_name, "" + str_email, ""
				+ str_password, "y");

		Toast.makeText(getApplicationContext(), "Register Success..",
				Toast.LENGTH_SHORT).show();

		startActivity(new Intent(getApplicationContext(), LoginSetalert.class));
		finish();

	}

	// add register to service
	private ProgressDialog progress;

	private void sendAddRegister() {
		progress = new ProgressDialog(LoginRegister_old.this);
		progress.setTitle("Connecting...");
		progress.setMessage("Please wait.");
		progress.setCancelable(false);
		progress.setIndeterminate(true);
		progress.setCanceledOnTouchOutside(false);

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
			progress.show();
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
							progress.dismiss();
							// prepare the alert box
							final AlertDialog.Builder alertbox = new AlertDialog.Builder(
									LoginRegister_old.this);
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
				progress.dismiss();
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
			progress.dismiss();

		}
	}

	private void sendEditProfileIdCard() {
		sendEdit resp = new sendEdit();
		resp.execute();
	}

	// -------------------- load add birthday
	public class sendEdit extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;
		private JSONObject json;
		private String stateSend = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			String urlO = SplashScreen.url_bidschart+ "/service/v2/editProfileIdCard";
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
							progress.dismiss();
							// prepare the alert box
							final AlertDialog.Builder alertbox = new AlertDialog.Builder(
									LoginRegister_old.this);
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
				progress.dismiss();
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
			progress.dismiss();

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
					LoginSetalert.class));
			finish();
			overridePendingTransition(R.animator.right_to_center,
					R.animator.center_to_right);
		}
		return super.onKeyDown(keyCode, event);
	}

}
