/**
 * Copyright 2010-present Facebook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.app.model.login;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.bids.AFunctionOther;
import com.app.bids.FragmentChangeActivity;
import com.app.bids.LoadingDialog;
import com.app.bids.LoginActivity;
import com.app.bids.PagerWatchList;
import com.app.bids.R;
import com.app.bids.ReadJson;
import com.app.bids.SplashScreen;
import com.app.bids.UiArticleSelectComments;
import com.app.bids.PagerProfile.loadData;
import com.app.model.LoginModel.UserModel;
import com.facebook.*;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphPlace;
import com.facebook.model.GraphUser;
import com.facebook.widget.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

public class FacebookLoginActivity extends Activity {

	private static final String PERMISSION = "publish_actions";

	private final String PENDING_ACTION_BUNDLE_KEY = "com.new_set_layout.LoginFacebookActivity";

	private LoginButton loginButton;
	private ProfilePictureView profilePictureView;
	private TextView greeting;
	private PendingAction pendingAction = PendingAction.NONE;
	// public static GraphUser user;
	private GraphPlace place;
	private List<GraphUser> tags;
	private boolean canPresentShareDialog;

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
		// if (session != null) {
		Log.v("logout 1 _ ", "11111 " + !session.isClosed());
		if (!session.isClosed()) {
			Log.v("logout 2", "22222");
			session.closeAndClearTokenInformation();
		}
		// } else {
		// Log.v("logout 2", "22222");
		// session = new Session(context);
		// Session.setActiveSession(session);
		// session.closeAndClearTokenInformation();
		// }

		Log.v("logout session", "" + session);
		// {Session state:CLOSED, token:{AccessToken token:ACCESS_TOKEN_REMOVED
		// permissions:[]}, appId:802896589752581}

		// session = null;
		SplashScreen.userModel.status = "";
		SplashScreen.userModel.user_id = "";
		SplashScreen.url_bidschart = "http://www.bidschart.com";
		// LoginActivity.handlerSetProfile.sendEmptyMessage(0);

		// PagerWatchList.handlerWatchList.sendEmptyMessage(1);

	}

	// static Dialog dialogLoading;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		// Generate keyHask
		PackageInfo info;
		try {
			info = getPackageManager().getPackageInfo(getPackageName(),
					PackageManager.GET_SIGNATURES);
			for (Signature signature : info.signatures) {
				MessageDigest md;
				md = MessageDigest.getInstance("SHA");
				md.update(signature.toByteArray());
				// String something = new String(Base64.encode(md.digest(), 0));
			}
		} catch (NameNotFoundException e1) {
			Log.e("name not found", e1.toString());
		} catch (NoSuchAlgorithmException e) {
			Log.e("no such an algorithm", e.toString());
		} catch (Exception e) {
			Log.e("exception", e.toString());
		}

		if (savedInstanceState != null) {
			String name = savedInstanceState
					.getString(PENDING_ACTION_BUNDLE_KEY);
			pendingAction = PendingAction.valueOf(name);
		}

		setContentView(R.layout.login_main_facebook);

		// dialogLoading = new Dialog(this);
		// dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// dialogLoading.getWindow().setBackgroundDrawable(
		// new ColorDrawable(android.graphics.Color.TRANSPARENT));
		// dialogLoading.setContentView(R.layout.progress_bar);
		// dialogLoading.setCancelable(false);
		// dialogLoading.setCanceledOnTouchOutside(false);

		loginButton = (LoginButton) findViewById(R.id.login_button);
		loginButton.setApplicationId(getResources().getString(R.string.app_id));
		loginButton.setReadPermissions("email");
		loginButton
				.setUserInfoChangedCallback(new LoginButton.UserInfoChangedCallback() {
					@Override
					public void onUserInfoFetched(GraphUser user) {

						Log.v("graph user null", "" + (user == null));

						SplashScreen.graphUser = user;
						updateUI();
						// handlePendingAction();
					}
				});

		profilePictureView = (ProfilePictureView) findViewById(R.id.profilePicture);
		greeting = (TextView) findViewById(R.id.greeting);

		// Can we present the share dialog for regular links?
		canPresentShareDialog = FacebookDialog.canPresentShareDialog(this,
				FacebookDialog.ShareDialogFeature.SHARE_DIALOG);
	}

	@Override
	protected void onResume() {
		super.onResume();
		uiHelper.onResume();

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
			new AlertDialog.Builder(FacebookLoginActivity.this)
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
		boolean enableButtons = (session.isOpened());

		// SplashScreen.graphUser(15251): GraphObject{graphObjectClass=GraphUser, 
		// state={"id":"1755704791337345","first_name":"Pongpang","gender":"male","last_name":"Iillpang",
		// "link":"https:\/\/www.facebook.com\/app_scoped_user_id\/1755704791337345\/","locale":"th_TH",
		// "name":"Pongpang Iillpang","timezone":7,"updated_time":"2016-07-14T04:01:27+0000","verified":true}}
		
		// SplashScreen.graphUser(28887): GraphObject{graphObjectClass=GraphUser,
		// state={"id":"241760999521890","email":"wichcoin@gmail.com","first_name":"วิชญ์","gender":"male","last_name":"แบงค์เก่า เหรียญเก่า","link":"https:\/\/www.facebook.com\/app_scoped_user_id\/241760999521890\/","locale":"th_TH","name":"วิชญ์ แบงค์เก่า เหรียญเก่า","timezone":7,"updated_time":"2016-07-06T15:57:03+0000","verified":true}}

		Log.v("SplashScreen.graphUser", ""+SplashScreen.graphUser);
		
		// GraphObject{graphObjectClass=GraphUser, state={"id":"1044594912326428","email":"kinji_amano@hotmail.com","first_name":"Sirasit","gender":"male","last_name":"Limjittakorn","link":"https:\/\/www.facebook.com\/app_scoped_user_id\/1044594912326428\/","locale":"en_US","name":"Sirasit Limjittakorn","timezone":7,"updated_time":"2016-09-04T14:32:24+0000","verified":true}}

		if (enableButtons && SplashScreen.graphUser != null) {
			loginButton.setVisibility(View.GONE);
			// ((TextView)findViewById(R.id.tv_launch)).setVisibility(View.VISIBLE);

			// dialogLoading.show();

			profilePictureView.setProfileId(SplashScreen.graphUser.getId());
			greeting.setText(getString(R.string.hello_user,
					SplashScreen.graphUser.getFirstName()));

			// GraphObject{graphObjectClass=GraphUser, 
//			state={"id":"1044594912326428","email":"kinji_amano@hotmail.com",
//			"first_name":"Sirasit","gender":"male","last_name":"Limjittakorn",
//			"link":"https:\/\/www.facebook.com\/app_scoped_user_id\/1044594912326428\/",
//			"locale":"en_US","name":"Sirasit Limjittakorn","timezone":7,"updated_time":"2016-09-04T14:32:24+0000","verified":true}}

			try {
				SplashScreen.userModel.firstName = SplashScreen.graphUser
						.getProperty("first_name").toString();
				SplashScreen.userModel.lastName = SplashScreen.graphUser
						.getProperty("last_name").toString();
				SplashScreen.userModel.id = SplashScreen.graphUser
						.getProperty("id").toString();
				SplashScreen.userModel.userName = SplashScreen.graphUser
						.getProperty("name").toString();
				SplashScreen.userModel.token = session.getAccessToken();
//				SplashScreen.userModel.email = SplashScreen.graphUser
//						.getProperty("email").toString();
				SplashScreen.userModel.email = "";
				SplashScreen.userModel.profileImagePath = "http://graph.facebook.com/"
						+ SplashScreen.graphUser.getId()
						+ "/picture?type=normal";
				
				Log.v("SplashScreen.userModel.firstName", ""+SplashScreen.userModel.firstName);
				Log.v("SplashScreen.userModel.lastName", ""+SplashScreen.userModel.lastName);
				Log.v("SplashScreen.userModel.id", ""+SplashScreen.userModel.id);
				Log.v("SplashScreen.userModel.userName", ""+SplashScreen.userModel.userName);
				Log.v("SplashScreen.userModel.email", ""+SplashScreen.userModel.email);

				sendLoginByFacebook(); // Login by facebook

			} catch (Exception e) {
				e.printStackTrace();
			}

			Intent intent = getIntent();
//			intent.putExtra("userjson", obj.toString());
			intent.putExtra("type", UserModel.FACEBOOK_TYPE);
			setResult(RESULT_OK, intent);
			// finish();

		} else {
			loginButton.setVisibility(View.VISIBLE);

			FragmentChangeActivity.handlerSetUser
					.sendEmptyMessage(FragmentChangeActivity.loginStateF);
		}
	}

	@SuppressWarnings("incomplete-switch")
	private void handlePendingAction() {
		PendingAction previouslyPendingAction = pendingAction;
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

	// ============== loginByFacebook =========================
	String resultLogin = "";

	private void sendLoginByFacebook() {
		loginByFacebook resp = new loginByFacebook();
		resp.execute();
	}

	public class loginByFacebook extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		String temp = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			String url = SplashScreen.url_bidschart
					+ "/service/loginByFacebook";

			String json = "";
			InputStream inputStream = null;

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

//				Facebook id : 812456322136230
//				username : Sirawich Thangern
//				firstName : Sirawich
//				lastName : Thangern
//				email : narongritnl@hotmail.com
//				token : EAAIsgSWJzAkBAHnNv7DeSFZAIdNn1vKOZBBLkUtELS2lyiZCfHgMUvTZClVdL0wGf9DVczuNrJdH3ZCo87UM5T2ZCz5vDwPvIDKNhWVJBntwwPXDgHt20D4p62O5ZC6EG1rIczx9bG5c3ShAnZBz9yV4Y1NhWYLyulK07rMiU7LNEh7mT4AyJahVQOYFumTw3l6HJMDTk2Cpi5QZB8iZAsH1Hb

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();

				jsonObject.accumulate("facebook_id", SplashScreen.userModel.id);
				jsonObject.accumulate("username",SplashScreen.userModel.userName);
				jsonObject.accumulate("first_name",SplashScreen.userModel.firstName);
				jsonObject.accumulate("last_name",SplashScreen.userModel.lastName);
				jsonObject.accumulate("facebook_token",SplashScreen.userModel.token);
				jsonObject.accumulate("email", SplashScreen.userModel.email);
				
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

				Log.v("loginByFacebook resultLogin", "" + resultLogin);
				
//				 {"status":"ok","message":"Get data Success.","email":"1","dataAll":{"user_id":"104","username":"Sirawich Thangern",
//				 "first_name":"","email":"narongritnl@hotmail.com","user_type":"free","followers":"1","following":"4","ideas_views":"7",
//				 "ideas_like":"1","published_ideas_count":"0","pushished_ideas":"0"}}

//				 {"status":"ok","message":"not email","email":"0","dataAll":{"username":"Pongpang Iillpang",
//				 "email":"","first_name":"Pongpang","last_name":"Iillpang",
//				 "facebook_token":"","facebook_id":"1755704791337345"}}

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

			if (connectionError == false) {
				JSONObject obj;
				try {
					obj = new JSONObject(resultLogin);

					Log.v("loginByFacebook onPostExecute obj", "" + obj);

					if (obj.getString("status").equals("ok")) {
//						if (obj.getString("message")
//								.equals("Get data Success.")) {
							SplashScreen.userModel.user_id = ""
									+ obj.getJSONObject("dataAll").getInt(
											"user_id");

							SplashScreen.userModel.status = ""
									+ obj.getString("status");

							SplashScreen.contentGetUserById = obj
									.getJSONObject("dataAll");

							SplashScreen.userModel.type = SplashScreen.userModel.FACEBOOK_TYPE;

							initLoadAllData();
//						} else {
//							Toast.makeText(getApplicationContext(),
//									"Get data Fail.", 0).show();
							
//							callFacebookLogout(getApplicationContext());
//							SplashScreen.myDb.UpdateDataStatusLogin("2", "N");
							
//							startActivity(new Intent(getApplicationContext(),
//									LoginActivity.class));
//							finish();
//						}
					} else {
						Toast.makeText(getApplicationContext(), "Login Fail.",
								0).show();
						
//						callFacebookLogout(getApplicationContext());
//						SplashScreen.myDb.UpdateDataStatusLogin("2", "N");
						
						startActivity(new Intent(getApplicationContext(),
								LoginActivity.class));
						finish();
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			} else {
			}
		}
	}

	// ============== Load Data all =============
	private void initLoadAllData() {
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

						SplashScreen.myDb.UpdateDataStatusLogin("2", "Y");
						if (SplashScreen.contentGetUserById != null) {
							if (!(SplashScreen.contentGetUserById
									.getString("package").equals("free"))) {
//								SplashScreen.url_bidschart = "http://realtime.bidschart.com";
							}
						}

						startActivity(new Intent(getApplicationContext(),
								FragmentChangeActivity.class));
						finish();
						overridePendingTransition(R.animator.right_to_center,
								R.animator.center_to_right);

					} catch (JSONException e) {
						e.printStackTrace();
						finish();
					}
				} else {
					Log.v("json jsonGetMyIdeas null", "jsonGetMyIdeas null");
					finish();
				}
			} else {
				finish();
			}
		}
	}

}
