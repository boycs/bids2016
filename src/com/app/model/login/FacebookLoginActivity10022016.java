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

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.bids.AFunctionOther;
import com.app.bids.FragmentChangeActivity;
import com.app.bids.LoginActivity;
import com.app.bids.PagerWatchList;
import com.app.bids.R;
import com.app.bids.SplashScreen;
import com.app.bids.UiArticleSelectComments;
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

public class FacebookLoginActivity10022016 extends Activity {

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
		if (session != null) {
			Log.v("logout 1 _ ", "11111 " + !session.isClosed());
			if (!session.isClosed()) {
				Log.v("logout 2", "22222");
				session.closeAndClearTokenInformation();
			}
		} else {
			Log.v("logout 2", "22222");
			session = new Session(context);
			Session.setActiveSession(session);
			session.closeAndClearTokenInformation();
		}

		session = null;
		SplashScreen.userModel = null;
		// LoginActivity.handlerSetProfile.sendEmptyMessage(0);

		PagerWatchList.handlerWatchList.sendEmptyMessage(1);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
						handlePendingAction();
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
			new AlertDialog.Builder(FacebookLoginActivity10022016.this)
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

		if (enableButtons && SplashScreen.graphUser != null) {
			profilePictureView
					.setProfileId(SplashScreen.graphUser.getId());
			greeting.setText(getString(R.string.hello_user,
					SplashScreen.graphUser.getFirstName()));

			JSONObject obj = new JSONObject();

			try {
				obj.put("user_id", SplashScreen.graphUser.getId());
				obj.put("firstname", SplashScreen.graphUser.getFirstName());
				obj.put("lastname", SplashScreen.graphUser.getLastName());
				obj.put("email", "");
				obj.put("username", SplashScreen.graphUser.getName());
				obj.put("profilepath", "http://graph.facebook.com/"
						+ SplashScreen.graphUser.getId()
						+ "/picture?type=normal");

				Log.v("session getAccessToken", "" + session.getAccessToken());
				Log.v("user getName",
						"" + SplashScreen.graphUser.getName());
				// CAALaOu4GCQUBAB17sbGIRWpnJzWxiUZAcwSzBqxnr3LlwB1mBgAsTDDmDZCHuKt65PuQHZARMlMWMYQEphDmixVfmYHTAFITNKMAZC0ldO4rNuIowuNYa2xp7swwF30saPLmZBvTYNNi9NjGCNDKsds97s6zqVxHAfCEGFyMctyi0ZAxeJpQWdQQoa7Wfv3UZAKffZCLH7TFJZAFXzMSRHwjIEK4h5dT8SBT0SjCRZBHaUXAZDZD

				// UserModel userModel = new UserModel();
				// FragmentActivity.userModel.user_id = user.getId();
				SplashScreen.userModel.firstName = SplashScreen.graphUser
						.getFirstName();
				SplashScreen.userModel.lastName = SplashScreen.graphUser
						.getLastName();
				SplashScreen.userModel.userName = SplashScreen.graphUser
						.getName();
				SplashScreen.userModel.token = session
						.getAccessToken();
				SplashScreen.userModel.email = SplashScreen.graphUser
						.getProperty("email").toString();
				SplashScreen.userModel.profileImagePath = "http://graph.facebook.com/"
						+ SplashScreen.graphUser.getId()
						+ "/picture?type=normal";

				sendLoginByFacebook(); // Login by facebook

			} catch (Exception e) {
				e.printStackTrace();
			}

			Intent intent = getIntent();
			intent.putExtra("userjson", obj.toString());
			intent.putExtra("type", UserModel.FACEBOOK_TYPE);
			setResult(RESULT_OK, intent);
			finish();

		} else {
			// profilePictureView.setProfileId(null);
			// greeting.setText(null);
			FragmentChangeActivity.handlerSetUser
					.sendEmptyMessage(FragmentChangeActivity.loginStateF);
		}

		Log.v("user >>> ", "" + SplashScreen.graphUser);
		// GraphObject{graphObjectClass=GraphUser,
		// state={"id":"1626284554267851","first_name":"มองใหม่","timezone":7,"email":"b-boy_sci_ubu@hotmail.com",
		// "verified":true,"name":"มองใหม่ มุมใหม่","locale":"th_TH",
		// "link":"https:\/\/www.facebook.com\/app_scoped_user_id\/1626284554267851\/","last_name":"มุมใหม่",
		// "gender":"male","updated_time":"2015-03-23T02:19:52+0000"}}

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
			// progress.show();

		}

		@Override
		protected Void doInBackground(Void... params) {

			String url = SplashScreen.url_bidschart+"/service/loginByFacebook";

			String json = "";
			InputStream inputStream = null;

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("username",
						SplashScreen.userModel.userName);
				jsonObject.accumulate("facebook_token",
						SplashScreen.userModel.token);
				jsonObject.accumulate("email",
						SplashScreen.userModel.email);

				Log.v("userModel.userName >> ", ""
						+ SplashScreen.userModel.userName);

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

				Log.v("result login fb >> ", "" + resultLogin);

//				{"status":"ok","message":"Get data Success.","dataAll":{"user_id":"104","username":"Sirawich Thangern",
//					"first_name":"narongrit","email":"narongritnl@hotmail.com","user_type":"free","followers":"1",
//					"following":"4","ideas_views":"7","ideas_like":"1","published_ideas_count":"0","pushished_ideas":"0"}}

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

					SplashScreen.userModel.user_id = ""
							+ obj.getJSONObject("dataAll").getInt("user_id");

					SplashScreen.contentGetUserById = obj
							.getJSONObject("dataAll");

				} catch (JSONException e) {
					e.printStackTrace();
				}

				// {"status":"ok","message":"Get data Success.","dataAll":{"user_id":"104","username":"Sirawich Thangern",
				// "first_name":"narongritnl","email":"narongritnl@hotmail.com","user_type":"free","followers":"0",
				// "following":"0","ideas_views":"0","ideas_like":"0","published_ideas_count":"0","pushished_ideas":"0"}}

				// LoginActivity.handlerSetUser
				// .sendEmptyMessage(LoginActivity.loginStateT);

				FragmentChangeActivity.handlerSetProfile.sendEmptyMessage(1); // loign
																				// success
				finish();

				// PagerWatchList.handlerWatchList.sendEmptyMessage(1);

				// startActivity(new Intent(getApplicationContext(),
				// LoginActivity.class));
			} else {
				// PagerWatchList.handlerWatchList.sendEmptyMessage(1);

				// startActivity(new Intent(getApplicationContext(),
				// LoginActivity.class));
			}

			// startActivity(new Intent(getApplicationContext(),
			// LoginActivity.class));

		}
	}

}
