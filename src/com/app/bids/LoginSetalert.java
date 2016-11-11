package com.app.bids;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;
import com.app.bids.R;
import com.facebook.Session.NewPermissionsRequest;
import com.facebook.*;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;
import com.facebook.widget.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.apache.http.HttpEntity;
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

public class LoginSetalert extends Activity {

	private ProgressDialog progress;

	// layout
//	private CheckBox checkbox_remember;
	private EditText et_email, et_password;
	private String str_email, str_password;
	// login
	public static String lg_id_member = "";
	public static String lg_name = "";
	public static String lg_id_fb = "";
	public static String lg_email = "";
	public static String lg_platform = "";
	public static String lg_device_id = "";

	// ui view
	public static ArrayList<String> arr_getsymbol = new ArrayList<String>();
	public static ArrayList<CatalogGetSymbol> list_getSymbol = new ArrayList<CatalogGetSymbol>();
//	public static int grid_symbol_position = 0;
//	public static String symbol_search_id = "";
//	public static String grid_id_add_symbol = "";
//	public static String grid_name_add_symbol = "";
//	public static String grid_id_orderbook = "";
//	public static int fav_select = 1;
//	public static int menu_select = 1;
	// public static String[] favoriteNamexx = { "", "favorite 1", "favorite 2",
	// "favorite 3", "favorite 4", "favorite 5" };

	private FragmentChangeActivity mInterface;
	
	private static final String PERMISSION = "publish_actions";
	private static final Location SEATTLE_LOCATION = new Location("") {
		{
			setLatitude(47.6097);
			setLongitude(-122.3331);
		}
	};

	private final String PENDING_ACTION_BUNDLE_KEY = "com.app.setalert.LoginActivity:PendingAction";

	private LoginButton loginButtonFb;
	
	// private ProfilePictureView profilePictureView;
	// private TextView greeting;
	private PendingAction pendingAction = PendingAction.NONE;
	private GraphUser user;
	private GraphPlace place;
	private List<GraphUser> tags;
	private boolean canPresentShareDialog;
	private boolean canPresentShareDialogWithPhotos;

	private enum PendingAction {
		NONE, POST_PHOTO, POST_STATUS_UPDATE
	}

	private UiLifecycleHelper uiHelper;

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	private FacebookDialog.Callback dialogCallback = new FacebookDialog.Callback() {
		@Override
		public void onError(FacebookDialog.PendingCall pendingCall,
				Exception error, Bundle data) {
			Log.d("HelloFacebook", String.format("Error: %s", error.toString()));
		}

		@Override
		public void onComplete(FacebookDialog.PendingCall pendingCall,
				Bundle data) {
			Log.d("HelloFacebook", "Success!");
		}
	};

	// function logout Fb
	public static void callFacebookLogout(Context context) {
		Session session = Session.getActiveSession();
		if (session != null) {
			if (!session.isClosed()) {
				session.closeAndClearTokenInformation();
			}
		} else {
			session = new Session(context);
			Session.setActiveSession(session);
			session.closeAndClearTokenInformation();
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {

		// start service
		// startService(new Intent(this, MyService.class));

		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		if (savedInstanceState != null) {
			String name = savedInstanceState
					.getString(PENDING_ACTION_BUNDLE_KEY);
			pendingAction = PendingAction.valueOf(name);
		}

		setContentView(R.layout.dialog_login_main);

		// GET DEVICE ID => 53e004ada30ad7d2
		lg_device_id = Secure
				.getString(getContentResolver(), Secure.ANDROID_ID);
		Log.v("DEVICE ID ->>", "" + lg_device_id);

//		checkbox_remember = (CheckBox) findViewById(R.id.checkbox_remember);
		et_email = (EditText) findViewById(R.id.et_email);
		et_password = (EditText) findViewById(R.id.et_pss);

		// check databases member
		initLoginInput(); // init login input

		// check databases favorite
		initFavorite(); // init favorite

//		loginButtonFb = (LoginButton) findViewById(R.id.loginButtonFb);
//		loginButtonFb
//				.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
//					@Override
//					public void onUserInfoFetched(GraphUser user) {
//						LoginActivity.this.user = user;
//						updateUI();
//						// It's possible that we were waiting for this.user to
//						// be populated in order to post a
//						// status update.
//						handlePendingAction();
//					}
//				});
		
		((ImageView) findViewById(R.id.facebook_icon)).setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mInterface.onFacebookClick();
			}
		});

		((Button) findViewById(R.id.bt_login_email))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						// startActivity(new Intent(getApplicationContext(),
						// FragmentActivity2.class));
						// finish();

						str_email = et_email.getText().toString().trim();
						str_password = et_password.getText().toString().trim();

						if ((!str_email.equals(""))
								&& (!str_password.equals(""))) {
							if (SplashScreen.arrMember != null) {
								int countDB = SplashScreen.arrMember.length;
								if ((SplashScreen.arrMember[countDB - 1][2].equals(""
										+ str_email))
										&& (SplashScreen.arrMember[countDB - 1][3].equals(""
												+ str_password))) {
//									if (checkbox_remember.isChecked()) {
//										if ((arrMember[countDB - 1][4])
//												.equals("y")) {
//											sendLoginSetAlert();
//										} else {
//											myDb.UpdateDataMember(""
//													+ arrMember.length, "y");
//											sendLoginSetAlert();
//										}
//									} else {
//										if ((arrMember[countDB - 1][4])
//												.equals("n")) {
//											sendLoginSetAlert();
//										} else {
//											myDb.UpdateDataMember(""
//													+ arrMember.length, "n");
//											sendLoginSetAlert();
//										}
//									}				
									

									// login
									sendLoginSetAlert();
								} else {
									// login fail
									// alertLoginFail();
								}
							} else {
								// login
								sendLoginSetAlert();
							}
						} else {
							final AlertDialog.Builder alertbox = new AlertDialog.Builder(
									LoginSetalert.this);
							alertbox.setTitle("SET Alert");
							alertbox.setMessage(" Field is not blank.");
							alertbox.setPositiveButton("OK",
									new DialogInterface.OnClickListener() {
										@Override
										public void onClick(
												DialogInterface dialog,
												int which) {
											dialog.cancel();
										}
									});
							alertbox.show();
						}

					}
				});

		((TextView) findViewById(R.id.tv_register))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						startActivity(new Intent(getApplicationContext(),
								LoginRegister.class));
						finish();
						overridePendingTransition(R.animator.right_to_center,
								R.animator.center_to_right);
					}
				});

		((TextView) findViewById(R.id.tv_forget))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
//						startActivity(new Intent(getApplicationContext(),
//								LoginRegister.class));
//						finish();
//						overridePendingTransition(R.animator.right_to_center,
//								R.animator.center_to_right);
					}
				});

		// profilePictureView = (ProfilePictureView)
		// findViewById(R.id.profilePicture);
		// greeting = (TextView) findViewById(R.id.greeting);

		// Can we present the share dialog for regular links?
		canPresentShareDialog = FacebookDialog.canPresentShareDialog(this,
				FacebookDialog.ShareDialogFeature.SHARE_DIALOG);
		// Can we present the share dialog for photos?
		canPresentShareDialogWithPhotos = FacebookDialog.canPresentShareDialog(
				this, FacebookDialog.ShareDialogFeature.PHOTOS);
	}

	// init login input
	private void initLoginInput() {
		SplashScreen.myDb = new LinnaeusDatabase(this);
		SplashScreen.myDb.getWritableDatabase(); // First method
		SplashScreen.arrMember = SplashScreen.myDb.SelectDataMember(); // add data member in array

		// get data schedule
		if (SplashScreen.arrMember != null) {
			int countDB = SplashScreen.arrMember.length;
			if ((SplashScreen.arrMember[countDB - 1][4]).equals("y")) {
				et_email.setText("" + SplashScreen.arrMember[countDB - 1][2]);
				et_password.setText("" + SplashScreen.arrMember[countDB - 1][3]);
//				checkbox_remember.setChecked(true);

				str_email = et_email.getText().toString().trim();
				str_password = et_password.getText().toString().trim();
				sendLoginSetAlert(); // login auto
			} else {
				et_email.setText("" + SplashScreen.arrMember[countDB - 1][2]);
				// et_password.setText("" + arrMember[countDB - 1][3]);
			}
		} else {
			// ไม่มีข้อมูลผู้ใช้
		}
	}

	// login fail
	private void alertLoginFail() {
		final AlertDialog.Builder alert = new AlertDialog.Builder(
				LoginSetalert.this);
		alert.setTitle(" ลงชื่อเข้าใช้ไม่ถูกต้อง!!");
		alert.setPositiveButton("ตกลง", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		alert.show();
	}

	// init favorite
	private void initFavorite() {
		SplashScreen.myDbFav = new LinnaeusDatabase(this);
		SplashScreen.myDbFav.getWritableDatabase(); // First method
		SplashScreen.arrFav = SplashScreen.myDbFav.SelectDataFav(); // get data in array

		// get data schedule
		if (SplashScreen.arrFav != null) {

		} else {
			for (int i = 1; i < 6; i++) {
				SplashScreen.myDbFav.InsertDataFav("favorite " + i);
			}
			SplashScreen.myDbFav = new LinnaeusDatabase(this);
			SplashScreen.myDbFav.getWritableDatabase(); // First method
			SplashScreen.arrFav = SplashScreen.myDbFav.SelectDataFav(); // get data in array
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		uiHelper.onResume();

		// Call the 'activateApp' method to log an app event for use in
		// analytics and advertising reporting. Do so in
		// the onResume methods of the primary Activities that an app may be
		// launched into.
		AppEventsLogger.activateApp(this);

		updateUI();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		uiHelper.onSaveInstanceState(outState);

		outState.putString(PENDING_ACTION_BUNDLE_KEY, pendingAction.name());
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data, dialogCallback);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();

		// Call the 'deactivateApp' method to log an app event for use in
		// analytics and advertising
		// reporting. Do so in the onPause methods of the primary Activities
		// that an app may be launched into.
		AppEventsLogger.deactivateApp(this);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (pendingAction != PendingAction.NONE
				&& (exception instanceof FacebookOperationCanceledException || exception instanceof FacebookAuthorizationException)) {
			new AlertDialog.Builder(LoginSetalert.this)
					.setTitle(R.string.cancelled)
					.setMessage(R.string.permission_not_granted)
					.setPositiveButton(R.string.ok, null).show();
			pendingAction = PendingAction.NONE;
		} else if (state == SessionState.OPENED_TOKEN_UPDATED) {
			handlePendingAction();
		}
		updateUI();
	}

	private void updateUI() {
		Session session = Session.getActiveSession();
		boolean enableButtons = (session != null && session.isOpened());

		// postStatusUpdateButton.setEnabled(enableButtons ||
		// canPresentShareDialog);
		// postPhotoButton.setEnabled(enableButtons ||
		// canPresentShareDialogWithPhotos);
		// pickFriendsButton.setEnabled(enableButtons);
		// pickPlaceButton.setEnabled(enableButtons);

		if (enableButtons && user != null) {
			startActivity(new Intent(getApplicationContext(),
					FragmentChangeActivity.class));
			finish();

			// =>> user.get
			// profilePictureView.setProfileId(user.getId());
			// greeting.setText(getString(R.string.hello_user,
			// user.getFirstName()));

		} else {
			// Toast.makeText(getApplicationContext(), "Login Fail!!!",
			// Toast.LENGTH_SHORT).show();

			// profilePictureView.setProfileId(null);
			// greeting.setText(null);
		}
	}

	@SuppressWarnings("incomplete-switch")
	private void handlePendingAction() {
		PendingAction previouslyPendingAction = pendingAction;
		// These actions may re-set pendingAction if they are still pending, but
		// we assume they
		// will succeed.
		pendingAction = PendingAction.NONE;

		switch (previouslyPendingAction) {
		case POST_PHOTO:
			// postPhoto();
			break;
		case POST_STATUS_UPDATE:
			// postStatusUpdate();
			break;
		}
	}

	private interface GraphObjectWithId extends GraphObject {
		String getId();
	}

	private void showPublishResult(String message, GraphObject result,
			FacebookRequestError error) {
		String title = null;
		String alertMessage = null;
//		if (error == null) {
//			title = getString(R.string.success);
//			String id = result.cast(GraphObjectWithId.class).getId();
//			alertMessage = getString(R.string.successfully_posted_post,
//					message, id);
//		} else {
//			title = getString(R.string.error);
//			alertMessage = error.getErrorMessage();
//		}

		new AlertDialog.Builder(this).setTitle(title).setMessage(alertMessage)
				.setPositiveButton(R.string.ok, null).show();
	}

	private void onClickPostStatusUpdate() {
		performPublish(PendingAction.POST_STATUS_UPDATE, canPresentShareDialog);
	}

	private FacebookDialog.ShareDialogBuilder createShareDialogBuilderForLink() {
		return new FacebookDialog.ShareDialogBuilder(this)
				.setName("Hello Facebook")
				.setDescription(
						"The 'Hello Facebook' sample application showcases simple Facebook integration")
				.setLink("http://developers.facebook.com/android");
	}

	private void postStatusUpdate() {
		if (canPresentShareDialog) {
			FacebookDialog shareDialog = createShareDialogBuilderForLink()
					.build();
			uiHelper.trackPendingDialogCall(shareDialog.present());
		} else if (user != null && hasPublishPermission()) {
//			final String message = getString(R.string.status_update,
//					user.getFirstName(), (new Date().toString()));
//			Request request = Request.newStatusUpdateRequest(
//					Session.getActiveSession(), message, place, tags,
//					new Request.Callback() {
//						@Override
//						public void onCompleted(Response response) {
//							showPublishResult(message,
//									response.getGraphObject(),
//									response.getError());
//						}
//					});
//			request.executeAsync();
		} else {
			pendingAction = PendingAction.POST_STATUS_UPDATE;
		}
	}

	private boolean hasPublishPermission() {
		Session session = Session.getActiveSession();
		return session != null
				&& session.getPermissions().contains("publish_actions");
	}

	private void performPublish(PendingAction action, boolean allowNoSession) {
		Session session = Session.getActiveSession();
		if (session != null) {
			pendingAction = action;
			if (hasPublishPermission()) {
				// We can do the action right away.
				handlePendingAction();
				return;
			} else if (session.isOpened()) {
				// We need to get new permissions, then complete the action when
				// we get called back.
				session.requestNewPublishPermissions(new Session.NewPermissionsRequest(
						this, PERMISSION));
				return;
			}
		}

		if (allowNoSession) {
			pendingAction = action;
			handlePendingAction();
		}
	}

	// send login set alert
	private void sendLoginSetAlert() {
		progress = new ProgressDialog(LoginSetalert.this);
		progress.setTitle("Connecting...");
		progress.setMessage("Please wait.");
		progress.setCancelable(false);
		progress.setIndeterminate(true);
		progress.setCanceledOnTouchOutside(false);

		sendLogin resp = new sendLogin();
		resp.execute();
	}

	// -------------------- send login
	public class sendLogin extends AsyncTask<Void, Void, Void> {
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

			// String url1 =
			// "http://api.xset.info/controllers/servicePhone.php?fpost=login"
			// + "&email=po1@hotmail.com"
			// + "&pass=1234"
			// + "&id_fb=-"
			// + "&platform=mail"
			// + "&name=-"
			// + "&serial_number=xasfdasdfasdfsdf"
			// + "&os=android";

			String url = "http://api.xset.info/controllers/servicePhone.php?fpost=login"
					+ "&email="
					+ str_email
					+ "&pass="
					+ str_password
					+ "&id_fb=-"
					+ "&platform=mail"
					+ "&name=-"
					+ "&serial_number=" + lg_device_id + "&os=android";

			Log.v("login", "" + url);

			try {

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
				Log.v("get login", "" + json);
				if (json != null) {
					try {
						stateSend = json.getString("status");
						Log.v("stateSendAdd", "" + json);

						if (stateSend.equals("true")) {
							Toast.makeText(getApplicationContext(),
									"Login Success", Toast.LENGTH_SHORT).show();

							// {"data":{"id_fb":"-","id_member":"42","platform":"mail","email":"boy","name":"boy"},
							// "status":"true","description":"Login Success"}

							JSONObject jso = json.getJSONObject("data");
							lg_id_member = jso.getString("id_member");
							lg_name = jso.getString("name");
							lg_id_fb = jso.getString("id_fb");
							lg_email = jso.getString("email");
							lg_platform = jso.getString("platform");

//							if (arrMember != null) {
//								if (checkbox_remember.isChecked()) {
//									myDb.UpdateDataMember("1", "y");
//								} else {
//									myDb.UpdateDataMember("1", "n");
//								}
//							} else {
//								if (checkbox_remember.isChecked()) {
//									LoginActivity.myDb.InsertDataMember(""
//											+ lg_name, "" + lg_email, ""
//											+ str_password, "y");
//								} else {
//									LoginActivity.myDb.InsertDataMember(""
//											+ lg_name, "" + lg_email, ""
//											+ str_password, "n");
//								}
//							}

//							loadInitDataLogin(); // load init after login
													// success
							
							startActivity(new Intent(getApplicationContext(),
									FragmentChangeActivity.class));
							finish();

						} else {
							progress.dismiss();
							// prepare the alert box
							final AlertDialog.Builder alertbox = new AlertDialog.Builder(
									LoginSetalert.this);
							alertbox.setTitle("  ลงชื่อเข้าใช้ไม่สำเร็จ");
							alertbox.setNeutralButton("ตกลง",
									new DialogInterface.OnClickListener() {
										public void onClick(
												DialogInterface arg0, int arg1) {

										}
									});
							alertbox.show();
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.v("json null", "null");
				}
			} else {
				Log.v("Load Symbol", "Load Symbol Fail..");
				// Toast.makeText(getApplicationContext(), "Load Symbol Fail..",
				// Toast.LENGTH_SHORT).show();
			}
			progress.dismiss();

		}
	}

	// load init login success
	private void loadInitDataLogin() {
		progress = new ProgressDialog(LoginSetalert.this);
		progress.setTitle("Connecting...");
		progress.setMessage("Please wait.");
		progress.setCancelable(false);
		progress.setIndeterminate(true);
		progress.setCanceledOnTouchOutside(false);

		loadData resp = new loadData();
		resp.execute();
	}

	// -------------------- load login
	public class loadData extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;
		private JSONArray json;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			progress.show();

		}

		@Override
		protected Void doInBackground(Void... params) {
			// String url =
			// "http://api.xset.info/controllers/servicePhone.php?fpost=getSymbol";
			String url = "http://api.xset.info/controllers/servicePhone.php?fpost=getSymbol&id_market=SET";

			Log.v("url getSymbol", "" + url);

			try {

				json = ReadJson.readJsonArrayFromUrl(url);

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

			Log.v("connection getSymbol Error", "" + connectionError);

			if (connectionError == false) {
				if (json != null) {
					try {
						for (int i = 0; i < json.length(); i++) {
							JSONObject jso = json.getJSONObject(i);
							arr_getsymbol.add(jso.getString("symbol_name"));

							CatalogGetSymbol cg = new CatalogGetSymbol();
							cg.symbol = jso.getString("symbol");
							cg.market_id = jso.getString("market_id");
							cg.symbol_fullname_eng = jso.getString("symbol_fullname_eng");
							cg.symbol_fullname_thai = jso.getString("symbol_fullname_thai");
							cg.last_trade = jso.getString("last_trade");
							cg.volume = jso.getString("volume");
							cg.change = jso.getString("change");
							cg.percentChange = jso.getString("percentChange");

							list_getSymbol.add(cg);
						}

						// Log.v("list_getSymbol size", "" +
						// list_getSymbol.size());
						Log.v("arr_getsymbol size", "" + arr_getsymbol.size());
						// Log.v("list_getSymbol", "" + list_getSymbol);

						progress.dismiss();
						startActivity(new Intent(getApplicationContext(),
								FragmentChangeActivity.class));
						finish();

						// JSONArray
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.v("json null", "symbol null");
					progress.dismiss();
					startActivity(new Intent(getApplicationContext(),
							FragmentChangeActivity.class));
					finish();
				}
			} else {
				progress.dismiss();
				AlertDialog.Builder builder = new AlertDialog.Builder(
						getApplicationContext());
				builder.setMessage("Connect Fail...")
						.setCancelable(false)
						.setPositiveButton("Load",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int id) {

										sendLoginSetAlert();

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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			AlertDialog.Builder builder = new AlertDialog.Builder(
					LoginSetalert.this);
			builder.setIcon(R.drawable.ic_launcher);
			builder.setTitle("Exit SetAlert");
			builder.setMessage(" Are you sure you want to Exit?")
					.setCancelable(false)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									finish();
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int id) {
									dialog.cancel();
								}
							});
			builder.show();
		}
		return super.onKeyDown(keyCode, event);
	}

}
