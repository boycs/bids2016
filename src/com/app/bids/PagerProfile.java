package com.app.bids;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.vending.billing.IInAppBillingService;
import com.app.bids.R;
import com.app.bids.EditProfile.sendEditInfor;
import com.app.bids.EditProfile.sendEditPss;
import com.app.bids.PagerHomeAll.loadData;
import com.app.bids.PagerWatchlistDetail.getFavoriteId;
import com.app.bids.util.IabHelper;
import com.app.bids.util.IabResult;
import com.app.bids.util.Inventory;
import com.app.bids.util.Purchase;
import com.app.bids.util.SkuDetails;
import com.app.model.login.FacebookLoginActivity;
import com.app.model.login.LoginDialog;
import com.google.gson.JsonObject;

import android.R.anim;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

public class PagerProfile extends Fragment {

	static Context context;
	public static View rootView;

	static Dialog dialogLoading;

	// --------- google analytics
	// private Tracker mTracker;
	// String nameTracker = new String("Profile");

	// list contains fragments to instantiate in the viewpager
	List<Fragment> fragmentMain = new Vector<Fragment>();
	private PagerAdapter mPagerAdapter;
	// view pager
	private ViewPager mPager;

	// activity listener interface
	private OnPageListener pageListener;
	public int selectTitle = 0;

	public static JSONObject contentIdeasSelect = null;
	public static String strPostId = "";
	public static String strSymbol = "";

	// ============== billing ===============
	private TextView tv_realtime_1d, tv_realtime_3d, tv_realtime_7d,
			tv_realtime_1m, tv_realtime_6m, tv_realtime_12m;

	// --- edit dialog
	static DialogProfileEdit dialogProfileEdit;

	public interface OnPageListener {
		public void onPage1(String s);
	}

	// onAttach : set activity listener
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		// if implemented by activity, set listener
		if (activity instanceof OnPageListener) {
			pageListener = (OnPageListener) activity;
		}
		// else create local listener (code never executed in this example)
		else
			pageListener = new OnPageListener() {
				@Override
				public void onPage1(String s) {
					Log.d("PAG1", "Button event from page 1 : " + s);
				}
			};
	}

	// onCreateView :
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		context = getActivity();
		// fragment not when container null
		if (container == null) {
			return null;
		}

		// inflate view from layot
		rootView = (LinearLayout) inflater.inflate(R.layout.pager_profile,
				container, false);

		dialogProfileEdit = new DialogProfileEdit(context);

		// --------- google analytics
		// GoogleAnalyticsApp application = (GoogleAnalyticsApp)
		// getActivity().getApplication();
		// mTracker = application.getDefaultTracker();

		// --------- dialogLoading
		dialogLoading = new Dialog(context);
		dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogLoading.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialogLoading.setContentView(R.layout.progress_bar);
		dialogLoading.setCancelable(false);
		dialogLoading.setCanceledOnTouchOutside(false);

		// dialogLoading.show();

		// initial load all
		if (SplashScreen.contentGetUserById != null) {
			initProfile(); // init profile
		} else {
			initLoadAllData();
		}

		// ------------- Billing ---------
		FragmentChangeActivity.initBilling();

		((Button) rootView.findViewById(R.id.bt_billing))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

						// FragmentChangeActivity.initBilling();
						// FragmentChangeActivity.sendPurchase();

						// startActivity(new Intent(context,
						// UiProfileBilling.class));
					}
				});

		return rootView;

	}

	@Override
	public void onResume() {
		super.onResume();
		// Log.v(nameTracker, "onResume onResume onResume");
		//
		// mTracker.setScreenName(nameTracker);
		// mTracker.send(new HitBuilders.ScreenViewBuilder().build());
	}

	// ============== billing ===============
	public static String strPacketId = "free";
	public static TextView tv_profile_realtime, tv_start, tv_expire, tv_date,
			tv_remining, tv_remining_percent;
	public static ImageView img_profile_realtime;
	public static ProgressWheel progress_remining;
	
	public static LinearLayout li_pro;

	private void initBilling() {
		// ---------- premium pro
		tv_profile_realtime = (TextView) rootView
				.findViewById(R.id.tv_profile_realtime);
		img_profile_realtime = (ImageView) rootView
				.findViewById(R.id.img_profile_realtime);
		tv_date = (TextView) rootView.findViewById(R.id.tv_date);
		tv_start = (TextView) rootView.findViewById(R.id.tv_start);
		tv_expire = (TextView) rootView.findViewById(R.id.tv_expire);
		tv_remining = (TextView) rootView.findViewById(R.id.tv_remining);
		tv_remining_percent = (TextView) rootView
				.findViewById(R.id.tv_remining_percent);
		progress_remining = (ProgressWheel) rootView
				.findViewById(R.id.progress_remining);
		li_pro = (LinearLayout)rootView.findViewById(R.id.li_pro);

		try {
			if (!(SplashScreen.contentGetUserById.getString("package")
					.equals("free"))) {
				// img_profile_realtime
				// .setBackgroundResource(R.drawable.icon_profile_realtime_active);
				img_profile_realtime.setVisibility(View.GONE);
				li_pro.setVisibility(View.VISIBLE);
				tv_profile_realtime.setText("Real Time");
			}else{
				img_profile_realtime.setVisibility(View.VISIBLE);
				li_pro.setVisibility(View.GONE);
				tv_profile_realtime.setText("Delay 15 minute");
			}

			// --------------
			String dateStart = SplashScreen.contentGetUserById
					.getString("start");
			String dateStop = SplashScreen.contentGetUserById
					.getString("expire");
			// Current Date
			Calendar c = Calendar.getInstance();
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

			tv_date.setText(DateTimeCreate.DateDmyThaiCreate(df.format(c
					.getTime())));
			tv_start.setText(DateTimeCreate.DateDmyThaiCreate(dateStart));
			tv_expire.setText(DateTimeCreate.DateDmyThaiCreate(dateStop));

			try {
				String strCalTimeRemining = DateTimeAgo
						.CalDifferentTimeProfileRemining(dateStart, dateStop);

				if ((strCalTimeRemining.equals("0"))
						|| (strCalTimeRemining.equals("0_Second"))) {
					tv_remining_percent
							.setText(strCalTimeRemining.split("_")[0]);
				} else {
					tv_remining_percent
							.setText(strCalTimeRemining.split("_")[0]);
					tv_remining.setText(strCalTimeRemining.split("_")[1]
							+ " Remining");
				}

				String strCalTimeReminingPercent = DateTimeAgo
						.CalDifferentTimeProfileReminingPercent(dateStart,
								dateStop);

				int intReminingPercent = Integer
						.parseInt(strCalTimeReminingPercent);

				Log.v("intReminingPercent", "" + intReminingPercent);

				progress_remining.incrementProgress(intReminingPercent); // ยังไม่คิดเป็น
																			// %
																			// เต็มวงใช้
																			// 360
			} catch (ParseException e) {
				e.printStackTrace();
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

		// ---------- layout
		tv_realtime_1d = (TextView) rootView.findViewById(R.id.tv_realtime_1d);
		tv_realtime_3d = (TextView) rootView.findViewById(R.id.tv_realtime_3d);
		tv_realtime_7d = (TextView) rootView.findViewById(R.id.tv_realtime_7d);
		tv_realtime_1m = (TextView) rootView.findViewById(R.id.tv_realtime_1m);
		tv_realtime_6m = (TextView) rootView.findViewById(R.id.tv_realtime_6m);
		tv_realtime_12m = (TextView) rootView
				.findViewById(R.id.tv_realtime_12m);

		// packet_id :: premium7d , premium1m , premium6m , premium12m , free

		// billing result(12973):
		// {"status":"ok","message":"Get data Success.","dataAll":{"date_start":"2016-08-26 14:30:09","date_end":"2016-08-27 14:30:09","date_count":1,"packet_id":"premium1d","packet_name":"Premium 1 days","packet_price":0.99}}

		// ---- 1d
		tv_realtime_1d.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				strPacketId = "premium1d";
				FragmentChangeActivity.productID = "bids_purchase_0.99";
				FragmentChangeActivity.sendPurchase();
			}
		});
		// ---- 3d
		tv_realtime_3d.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				strPacketId = "premium3d";
				FragmentChangeActivity.productID = "bids_purchase_1.99";
				FragmentChangeActivity.sendPurchase();
			}
		});
		// ---- 7d
		tv_realtime_7d.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				strPacketId = "premium7d";
				FragmentChangeActivity.productID = "bids_purchase_2.99";
				FragmentChangeActivity.sendPurchase();
			}
		});

		// ---- 1m
		tv_realtime_1m.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				strPacketId = "premium1m";
				FragmentChangeActivity.productID = "bids_purchase_9.99";
				FragmentChangeActivity.sendPurchase();
			}
		});

		// ---- 6m
		tv_realtime_6m.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				strPacketId = "premium6m";
				FragmentChangeActivity.productID = "bids_purchase_49.99";
				FragmentChangeActivity.sendPurchase();
			}
		});

		// ---- 12m
		tv_realtime_12m.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				strPacketId = "premium12m";
				FragmentChangeActivity.productID = "bids_purchase_99.99";
				FragmentChangeActivity.sendPurchase();
			}
		});

	}

	// //////////////////////////////////////////

	// public class MyActivity extends Activity{
	//
	// public IabHelper mHelper
	//
	// public purchaseLauncher(){
	//
	// mHelper.launchPurchaseFlow(this, SKU_GAS, 10001,
	// mPurchaseFinishedListener, "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
	//
	// }
	//
	// /*The finished, query and consume listeners should also be implemented in
	// here*/
	// }
	//
	// public class FragmentActivity extends Fragment{
	//
	// MyActivity myAct = (MyActivity) getActivity();
	//
	// myAct.purchaseLauncher();
	//
	// }
	//
	// protected void onActivityResult(int requestCode, int resultCode,Intent
	// data)
	// {
	// super.onActivityResult(requestCode, resultCode, data);
	//
	// FragmentManager fragmentManager = getSupportFragmentManager();
	// Fragment fragment = fragmentManager.findFragmentByTag("YourTag");
	// if (fragment != null)
	// {
	// ((MyFragmentWithIabHelper)fragment).onActivityResult(requestCode,
	// resultCode,data);
	// }
	// }

	// ///////////////////////////////////////////

	// @Override
	// public void onActivityResult(int requestCode, int resultCode, Intent
	// data) {
	// Toast.makeText(
	// context,
	// "onActivityResult isSetup : "+isSetup, Toast.LENGTH_SHORT).show();
	//
	// if (isSetup) {
	// boolean blnResult = mHelper.handleActivityResult(requestCode,
	// resultCode, data);
	//
	// Toast.makeText(
	// context,
	// "onActivityResult() - mHelper.handleActivityResult() = "
	// + blnResult, Toast.LENGTH_SHORT).show();
	// Log.d(tag, "onActivityResult() - mHelper.handleActivityResult() = "
	// + blnResult);
	//
	// if (blnResult)
	// return;
	// }
	//
	// super.onActivityResult(requestCode, resultCode, data);
	// }

	// @Override
	// public void onDestroy() {
	// if (isSetup)
	// mHelper.dispose();
	// mHelper = null;
	//
	// super.onDestroy();
	// }

	// @Override
	// public void onActivityCreated(Bundle savedInstanceState) {
	// super.onActivityCreated(savedInstanceState);
	//
	// progress = new ProgressDialog(context);
	// // progress.setTitle("กำลังโหลด...");
	// // progress.setMessage("Loading...");
	// progress.setProgressStyle(R.layout.progress_bar);
	// progress.setCancelable(false);
	// progress.setIndeterminate(true);
	// progress.setCanceledOnTouchOutside(false);
	//
	// // initial load all
	// initLoadAllData();
	//
	// }

	// ============== init profile ===============
	TextView tv_logout, tv_firstname, tv_website, tv_facebook, tv_twitter,
			tv_googleplus, tv_followers, tv_ideas, tv_views, tv_likes,
			tv_status;
	TextView tv_edite, tv_edit_profile, tv_username, tv_email, tv_plan, tv_ex,
			tv_brithdate, tv_id;
	LinearLayout li_portpolio, li_systemtrade, li_status;
	ImageView img_setting;

	String strStatus;

	private void initProfile() {
		tv_logout = (TextView) rootView.findViewById(R.id.tv_logout);
		tv_edite = (TextView) rootView.findViewById(R.id.tv_edit);

		tv_firstname = (TextView) rootView.findViewById(R.id.tv_firstname);
		tv_status = (TextView) rootView.findViewById(R.id.tv_status);
		li_status = (LinearLayout) rootView.findViewById(R.id.li_status);

		tv_website = (TextView) rootView.findViewById(R.id.tv_website);
		tv_facebook = (TextView) rootView.findViewById(R.id.tv_facebook);
		tv_twitter = (TextView) rootView.findViewById(R.id.tv_twitter);
		tv_googleplus = (TextView) rootView.findViewById(R.id.tv_googleplus);
		if (SplashScreen.userModel.type == 1) {
			tv_facebook.setTextColor(getResources().getColor(R.color.c_blue));
		} else if (SplashScreen.userModel.type == 2) {
			tv_twitter.setTextColor(getResources().getColor(R.color.c_blue));
		} else if (SplashScreen.userModel.type == 3) {
			tv_googleplus.setTextColor(getResources().getColor(R.color.c_blue));
		} else {
			tv_website.setTextColor(getResources().getColor(R.color.c_blue));
		}

		tv_followers = (TextView) rootView.findViewById(R.id.tv_followers);
		tv_ideas = (TextView) rootView.findViewById(R.id.tv_ideas);
		tv_views = (TextView) rootView.findViewById(R.id.tv_views);
		tv_likes = (TextView) rootView.findViewById(R.id.tv_likes);

		img_setting = (ImageView) rootView.findViewById(R.id.img_setting);

		tv_edit_profile = (TextView) rootView
				.findViewById(R.id.tv_edit_profile);

		tv_username = (TextView) rootView.findViewById(R.id.tv_username);
		tv_email = (TextView) rootView.findViewById(R.id.tv_email);
		tv_brithdate = (TextView) rootView.findViewById(R.id.tv_brithdate);
		tv_id = (TextView) rootView.findViewById(R.id.tv_id);
		tv_plan = (TextView) rootView.findViewById(R.id.tv_plan);
		tv_ex = (TextView) rootView.findViewById(R.id.tv_ex);

		li_portpolio = (LinearLayout) rootView.findViewById(R.id.li_portpolio);
		li_systemtrade = (LinearLayout) rootView
				.findViewById(R.id.li_systemtrade);

		if (SplashScreen.contentGetUserById != null) {
			try {
				if (!(SplashScreen.contentGetUserById.getString("package")
						.equals("free"))) {
					SplashScreen.url_bidschart = "http://realtime.bidschart.com";
				}

				strStatus = SplashScreen.contentGetUserById.getString("status");
				tv_status.setText(strStatus);
				tv_status.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogUpdateStatus();
					}
				});

				li_status.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogUpdateStatus();
					}
				});

				tv_firstname.setText(SplashScreen.contentGetUserById
						.getString("first_name")
						+ " "
						+ SplashScreen.contentGetUserById
								.getString("last_name"));

				tv_followers.setText(SplashScreen.contentGetUserById
						.getString("followers"));
				tv_ideas.setText(SplashScreen.contentGetUserById
						.getString("pushished_ideas"));
				tv_views.setText(SplashScreen.contentGetUserById
						.getString("ideas_views"));
				tv_likes.setText(SplashScreen.contentGetUserById
						.getString("ideas_like"));

				// ------- information -----
				tv_username.setText(SplashScreen.contentGetUserById
						.getString("username"));
				tv_email.setText(SplashScreen.contentGetUserById
						.getString("email"));
				tv_brithdate.setText(SplashScreen.contentGetUserById
						.getString("birthday"));
				tv_id.setText(SplashScreen.contentGetUserById
						.getString("packet_id"));
				// tv_plan.setText(contentGetUserById.getString("user_id"));
				tv_ex.setText(SplashScreen.contentGetUserById
						.getString("expire"));

			} catch (JSONException e) {
				e.printStackTrace();
			}
		}

		// -------------------------
		tv_edite.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogProfileEdit.show();
				// dialogEditProfile();
			}
		});

		// -------------------------
		li_portpolio.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// switchFragment(new PagerWatchList());
			}
		});

		li_systemtrade.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// switchFragment(new PagerSystemTrade());
			}
		});

		img_setting.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// LayoutInflater li = LayoutInflater.from(context);
				// View promptsView = li.inflate(R.layout.dialog_insert, null);
				//
				// AlertDialog.Builder alertDialogBuilder = new
				// AlertDialog.Builder(
				// LanguageList.context);
				// alertDialogBuilder.setView(promptsView);
				// final EditText userAddThai = (EditText) promptsView
				// .findViewById(R.id.addTextThaiDialogUserInput);
				// final EditText userAddOther = (EditText) promptsView
				// .findViewById(R.id.addTextOtherDialogUserInput);
				// final TextView setCountry = (TextView) promptsView
				// .findViewById(R.id.setNameCountryDialogUserInput);
				// setCountry.setText("" + LanguageCountry.name_countryThai);
				//
				// .setNegativeButton("ยกเลิก",
				// new DialogInterface.OnClickListener() {
				// public void onClick(DialogInterface dialog,
				// int id) {
				// dialog.cancel();
				// }
				// });
				// // create alert dialog
				// AlertDialog alertDialog = alertDialogBuilder.create();
				// // show it
				// alertDialog.show();

				// get prompts.xml view
				LayoutInflater layoutInflater = LayoutInflater.from(context);
				View dlView = layoutInflater.inflate(
						R.layout.dialog_profile_menusetting, null);
				AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
						context);
				alertDialogBuilder.setView(dlView);

				LinearLayout li_edit = (LinearLayout) dlView
						.findViewById(R.id.li_edit);
				LinearLayout li_setting = (LinearLayout) dlView
						.findViewById(R.id.li_setting);
				LinearLayout li_singout = (LinearLayout) dlView
						.findViewById(R.id.li_singout);

				li_edit.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {

					}
				});

				li_setting.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogSetting(); // profile setting
					}
				});

				li_singout.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// FacebookLoginActivity.callFacebookLogout(context);
					}
				});

				// alertDialogBuilder.setNegativeButton("ยกเลิก",
				// new DialogInterface.OnClickListener() {
				// public void onClick(DialogInterface dialog,
				// int id) {
				// dialog.cancel();
				// }
				// });

				// create an alert dialog
				AlertDialog alert = alertDialogBuilder.create();
				alert.show();
			}
		});

		// ------ Logout -------
		tv_logout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {				
				// --- cancle update
				if (FragmentChangeActivity.timerUpdateSymbolStatus) {
					FragmentChangeActivity.timerUpdateSymbolStatus = false;
					FragmentChangeActivity.timerUpdateSymbol.cancel();
				}
				// ---- check logout
				if (SplashScreen.userModel.type == 1) { // facebook
					SplashScreen.url_bidschart = "http://www.bidschart.com";
					FacebookLoginActivity.callFacebookLogout(context);
					SplashScreen.myDb.UpdateDataStatusLogin("2", "N");
					updateDataLogin();

				} else if (SplashScreen.userModel.type == 2) { // twitter
					SplashScreen.myDb.UpdateDataStatusLogin("4", "N");
					updateDataLogin();

				} else if (SplashScreen.userModel.type == 3) { // plus
					SplashScreen.myDb.UpdateDataStatusLogin("3", "N");
					updateDataLogin();

				} else { // website
					SplashScreen.userModel.status = "";
					SplashScreen.userModel.user_id = "";
					SplashScreen.url_bidschart = "http://www.bidschart.com";

					SplashScreen.myDb.UpdateDataStatusLogin("1", "N");
					updateDataLogin();
				}
			}
		});

		dialogLoading.dismiss();

		((ImageView) rootView.findViewById(R.id.img_suggestion))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogSuggestion();
					}
				});
		// if (!FragmentChangeActivity.ckShowSuggestion) {
		// FragmentChangeActivity.ckShowSuggestion = true;
		// dialogSuggestion();
		// }

		// ----------- ส่วนของการจ่ายเงิน
		initBilling();

	}

	// ====== encode password type md5 to string ======
	public static String strPssToMd5(String s) {
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
			digest.update(s.getBytes(Charset.forName("US-ASCII")), 0,
					s.length());
			byte[] magnitude = digest.digest();
			BigInteger bi = new BigInteger(1, magnitude);
			String hash = String.format("%0" + (magnitude.length << 1) + "x",
					bi);
			return hash;
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}

	FragmentChangeActivity frg;
	private void updateDataLogin() {
		frg = new FragmentChangeActivity();
		SplashScreen.myDb = new LinnaeusDatabase(context);
		SplashScreen.myDb.getWritableDatabase(); // First method
		SplashScreen.arrLogin = SplashScreen.myDb.SelectDataLogin();
		
		FragmentChangeActivity.ckLoadWatchlist = true;
		FragmentChangeActivity.strWatchlistCategory = "favorite";
		FragmentChangeActivity.strFavoriteNumber = "1";

		FragmentChangeActivity.ckLoadFavAll = false;
		SplashScreen.contentGetUserById = null;
		SplashScreen.userModel = null;
		FragmentChangeActivity.contentGetWatchlistSymbol = null;
		FragmentChangeActivity.contentGetSymbol = null;
		FragmentChangeActivity.strGetListSymbol = "";

		Log.v("LLLLLLLout","__"+FragmentChangeActivity.strGetListSymbol);
		
		startActivity(new Intent(context, LoginActivity.class));
		((Activity) context).finish();
		
//		frg.finish();
	}

	// ============== tab pager ===============
	LinearLayout li_followingideas, li_myideas, li_follower, li_following;
	TextView tv_followingideas, tv_myideas, tv_follower, tv_following;
	View v_followingideas, v_myideas, v_follower, v_following;

	private void initTabPager() {
		li_followingideas = (LinearLayout) rootView
				.findViewById(R.id.li_followingideas);
		li_myideas = (LinearLayout) rootView.findViewById(R.id.li_myideas);
		li_follower = (LinearLayout) rootView.findViewById(R.id.li_follower);
		li_following = (LinearLayout) rootView.findViewById(R.id.li_following);

		tv_followingideas = (TextView) rootView
				.findViewById(R.id.tv_followingideas);
		tv_myideas = (TextView) rootView.findViewById(R.id.tv_myideas);
		tv_follower = (TextView) rootView.findViewById(R.id.tv_follower);
		tv_following = (TextView) rootView.findViewById(R.id.tv_following);

		v_followingideas = (View) rootView.findViewById(R.id.v_followingideas);
		v_myideas = (View) rootView.findViewById(R.id.v_myideas);
		v_follower = (View) rootView.findViewById(R.id.v_follower);
		v_following = (View) rootView.findViewById(R.id.v_following);

		// onclick tab
		li_followingideas.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectTitle = 0;
				setColorTabPager(selectTitle);
				mPager.setCurrentItem(selectTitle); // last
			}
		});
		li_myideas.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectTitle = 1;
				setColorTabPager(selectTitle);
				mPager.setCurrentItem(selectTitle); // about
			}
		});
		li_follower.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectTitle = 2;
				setColorTabPager(selectTitle);
				mPager.setCurrentItem(selectTitle); // about
			}
		});
		li_following.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				selectTitle = 3;
				setColorTabPager(selectTitle);
				mPager.setCurrentItem(selectTitle); // about
			}
		});
	}

	// ============== color fab pagfer ================
	private int setColorTabPager(int cTitle) {
		tv_followingideas
				.setTextColor(getResources().getColor(R.color.c_title));
		tv_myideas.setTextColor(getResources().getColor(R.color.c_title));
		tv_follower.setTextColor(getResources().getColor(R.color.c_title));
		tv_following.setTextColor(getResources().getColor(R.color.c_title));

		v_followingideas.setBackgroundColor(getResources().getColor(
				R.color.c_title));
		v_myideas.setBackgroundColor(getResources().getColor(R.color.c_title));
		v_follower.setBackgroundColor(getResources().getColor(R.color.c_title));
		v_following
				.setBackgroundColor(getResources().getColor(R.color.c_title));

		if (cTitle == 0) {
			tv_followingideas.setTextColor(getResources().getColor(
					R.color.c_content));
			v_followingideas.setBackgroundColor(getResources().getColor(
					R.color.c_content));
		} else if (cTitle == 1) {
			tv_myideas.setTextColor(getResources().getColor(R.color.c_content));
			v_myideas.setBackgroundColor(getResources().getColor(
					R.color.c_content));
		} else if (cTitle == 2) {
			tv_follower
					.setTextColor(getResources().getColor(R.color.c_content));
			v_follower.setBackgroundColor(getResources().getColor(
					R.color.c_content));
		} else if (cTitle == 3) {
			tv_following.setTextColor(getResources()
					.getColor(R.color.c_content));
			v_following.setBackgroundColor(getResources().getColor(
					R.color.c_content));
		}
		return cTitle;
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

			dialogLoading.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();

			// http://www.bidschart.com/service/getUserById?user_id=104
			String url_GetUserById = SplashScreen.url_bidschart
					+ "/service/getUserById?user_id="
					+ SplashScreen.userModel.user_id;

			Log.v("url_GetUserById", "" + url_GetUserById);

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

						initProfile(); // init profile
						// initPager(); // init pager
						// initTabPager(); // tab pager

						dialogLoading.show();
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.v("json jsonGetMyIdeas null", "jsonGetMyIdeas null");
				}
			} else {
			}
			dialogLoading.dismiss();
		}
	}

	// ============= Profile Setting =========
	@SuppressLint("NewApi")
	private void dialogSetting() {

		LayoutInflater layoutInflater = LayoutInflater.from(context);
		View dlView = layoutInflater.inflate(R.layout.dialog_profile_setting,
				null);
		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder.setView(dlView);

		TextView dl_tv_close = (TextView) dlView.findViewById(R.id.dl_tv_close);

		// dl_tv_close.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// alertDialogBuilder.setOnDismissListener(null);
		// }
		// });

		alertDialogBuilder.setNegativeButton("ยกเลิก",
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});

		// create an alert dialog
		AlertDialog alert = alertDialogBuilder.create();
		alert.show();
	}

	// ============= Pager ===========
	private void initPager() {
		// creating fragments and adding to list
		fragmentMain.removeAll(fragmentMain);
		fragmentMain.add(Fragment.instantiate(context,
				PagerProfileIdeas.class.getName()));
		fragmentMain.add(Fragment.instantiate(context,
				PagerProfileMyIdeas.class.getName()));
		fragmentMain.add(Fragment.instantiate(context,
				PagerProfileFollower.class.getName()));
		fragmentMain.add(Fragment.instantiate(context,
				PagerProfileFollowing.class.getName()));

		// creating adapter and linking to view pager
		this.mPagerAdapter = new PagerAdapter(super.getChildFragmentManager(),
				fragmentMain);
		mPager = (ViewPager) rootView.findViewById(R.id.vp_pager);

		mPager.setAdapter(this.mPagerAdapter);

		// mPager.setOnClickListener(new OnClickListener() {
		// @Override
		// public void onClick(View v) {
		// }
		// });
		//
		// mPager.setOnTouchListener(new View.OnTouchListener() {
		// @Override
		// public boolean onTouch(View v, MotionEvent arg1) {
		// v.getParent().requestDisallowInterceptTouchEvent(true);
		// return false;
		// }
		// });

		mPager.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				selectTitle = mPager.getCurrentItem();
				setColorTabPager(selectTitle);
				// tab pager
				initTabPager();
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				mPager.getParent().requestDisallowInterceptTouchEvent(true);
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

		// tab pager
		initTabPager();
	}

	// ============= dialog Suggestion ===========
	private static Dialog dialogSuggestion;

	private static String str_suggestion, str_problem;

	// ============= Profile Setting =========
	@SuppressLint("NewApi")
	private void dialogSuggestion() {

		dialogSuggestion = new Dialog(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		View dlView = inflater
				.inflate(R.layout.dialog_profile_suggestion, null);
		// perform operation on elements in Layout
		dialogSuggestion.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogSuggestion.setContentView(dlView);

		final EditText dl_et_suggestion, dl_et_problem;
		final TextView dl_tv_submit, dl_tv_rate;

		dl_tv_submit = (TextView) dlView.findViewById(R.id.dl_tv_submit);
		dl_tv_rate = (TextView) dlView.findViewById(R.id.dl_tv_rate);

		dl_et_suggestion = (EditText) dlView
				.findViewById(R.id.dl_et_suggestion);
		dl_et_problem = (EditText) dlView.findViewById(R.id.dl_et_problem);

		dl_tv_submit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				str_suggestion = dl_et_suggestion.getText().toString();
				str_problem = dl_et_problem.getText().toString();

				if ((str_suggestion.trim().equals(""))
						&& (str_problem.trim().equals(""))) {
					Toast.makeText(context, "No data!!!", 0).show();
				} else {
					sendSuggestion resp = new sendSuggestion();
					resp.execute();
				}

			}
		});

		dl_tv_rate.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogSuggestion.dismiss();
			}
		});

		dialogSuggestion.show();

		// set width height dialog
		DisplayMetrics displaymetrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(displaymetrics);

		int ScreenHeight = displaymetrics.heightPixels;
		int ScreenWidth = displaymetrics.widthPixels;

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.width = (int) (ScreenWidth * 0.87);
		lp.height = (int) (ScreenHeight * 0.67);

		dialogSuggestion.getWindow().setAttributes(lp);

		dialogSuggestion.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

	}

	public static class sendSuggestion extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		String temp = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialogLoading.show();
		}

		@Override
		protected Void doInBackground(Void... params) {
			String url = SplashScreen.url_bidschart + "/service/v2/addSuggest";

			String json = "";
			InputStream inputStream = null;
			String result = "";

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("suggest", str_suggestion);
				jsonObject.accumulate("problem", str_problem);

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
					result = AFunctionOther
							.convertInputStreamToString(inputStream);
				else
					result = "Did not work!";

				Log.v("result Suggestion", result);

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
			dialogLoading.dismiss();
			dialogSuggestion.dismiss();
		}
	}

	// ============= edit profile ===========
	// send edit infomation
	private static Dialog dialogEditProfile;
	TextView dl_tv_savechanges;

	// ============= Profile Setting =========
	@SuppressLint("NewApi")
	private void dialogEditProfile() {

		dialogEditProfile = new Dialog(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		View dlView = inflater.inflate(R.layout.dialog_profile_edit, null);
		// perform operation on elements in Layout
		dialogEditProfile.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogEditProfile.setContentView(dlView);

		try {
			final String str_user_id = SplashScreen.contentGetUserById
					.getString("user_id");
			final String str_user_pss = SplashScreen.contentGetUserById
					.getString("password");

			final EditText dl_et_username, dl_et_email, dl_et_birthday, dl_et_firstname, dl_et_lastname, dl_et_country, dl_et_website_url, dl_et_facebook, dl_et_google, dl_et_twitter, dl_et_about;
			final TextView dl_tv_change_pass, dl_tv_savechanges;

			dl_tv_change_pass = (TextView) dlView
					.findViewById(R.id.dl_tv_change_pass);
			dl_et_username = (EditText) dlView
					.findViewById(R.id.dl_et_username);
			dl_et_email = (EditText) dlView.findViewById(R.id.dl_et_email);
			// dl_et_idnumber = (EditText) dlView
			// .findViewById(R.id.dl_et_idnumber);
			dl_et_birthday = (EditText) dlView
					.findViewById(R.id.dl_et_birthday);
			dl_et_firstname = (EditText) dlView
					.findViewById(R.id.dl_et_firstname);
			dl_et_lastname = (EditText) dlView
					.findViewById(R.id.dl_et_lastname);
			dl_et_country = (EditText) dlView.findViewById(R.id.dl_et_country);
			dl_et_website_url = (EditText) dlView
					.findViewById(R.id.dl_et_website_url);
			dl_et_facebook = (EditText) dlView
					.findViewById(R.id.dl_et_facebook);
			dl_et_google = (EditText) dlView.findViewById(R.id.dl_et_google);
			dl_et_twitter = (EditText) dlView.findViewById(R.id.dl_et_twitter);
			dl_et_about = (EditText) dlView.findViewById(R.id.dl_et_about);
			dl_tv_savechanges = (TextView) dlView
					.findViewById(R.id.dl_tv_savechanges);

			// -------- set data -----
			dl_et_username.setText(SplashScreen.contentGetUserById
					.getString("username"));
			dl_et_email.setText(SplashScreen.contentGetUserById
					.getString("email"));
			// dl_et_idnumber.setText(SplashScreen.contentGetUserById
			// .getString("key_id"));
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
					String str_username, str_email, str_birthday, str_firstname, str_lastname, str_country, website_url, str_twitter, str_facebook, str_google, str_about;

					str_username = dl_et_username.getText().toString();
					str_email = dl_et_email.getText().toString();
					// str_idnumber = dl_et_idnumber.getText().toString();
					str_birthday = dl_et_birthday.getText().toString();
					str_firstname = dl_et_firstname.getText().toString();
					str_lastname = dl_et_lastname.getText().toString();
					str_country = dl_et_country.getText().toString();
					website_url = dl_et_website_url.getText().toString();
					str_twitter = dl_et_twitter.getText().toString();
					str_facebook = dl_et_facebook.getText().toString();
					str_google = dl_et_google.getText().toString();
					str_about = dl_et_about.getText().toString();

					dialogLoading.show();
					EditProfile.editInformation(str_user_id, str_username,
							str_email, str_birthday, str_firstname,
							str_lastname, str_country, website_url,
							str_twitter, str_facebook, str_google, str_about);
					dialogEditProfile.dismiss();
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
		((TextView) dlView.findViewById(R.id.dl_tv_close))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialogEditProfile.dismiss();
					}
				});

		dialogEditProfile.show();

		// set width height dialog
		DisplayMetrics displaymetrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(displaymetrics);

		int ScreenHeight = displaymetrics.heightPixels;
		int ScreenWidth = displaymetrics.widthPixels;

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.width = (int) (ScreenWidth * 0.95);
		lp.height = (int) (ScreenHeight * 0.8);

		dialogEditProfile.getWindow().setAttributes(lp);
		dialogEditProfile.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));

	}

	// ------- dialog status ----------
	public static String strInputUpdateStatus;

	private void dialogUpdateStatus() {

		final Dialog dialog = new Dialog(context);
		LayoutInflater inflater = LayoutInflater.from(context);
		View dlView = inflater.inflate(R.layout.dialog_profile_edit_status,
				null);
		// perform operation on elements in Layout
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(dlView);

		final EditText dl_et_input = (EditText) dlView
				.findViewById(R.id.dl_et_input);
		dl_et_input.setText(strStatus);

		// ----- send ---------
		((TextView) dlView.findViewById(R.id.dl_tv_send))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						strInputUpdateStatus = dl_et_input.getText().toString()
								.trim();
						if (!(strInputUpdateStatus.equals(""))) {
							sendUpdateStatus();
							dialog.dismiss();
						} else {
							Toast.makeText(context, "Input Status", 0).show();
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

	// ============== edit status ================
	private void sendUpdateStatus() {
		sendEditStatus resp = new sendEditStatus();
		resp.execute();
	}

	public class sendEditStatus extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;

		String temp = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected Void doInBackground(Void... params) {

			String url = SplashScreen.url_bidschart+"/service/updateStatusUser";

			String json = "";
			InputStream inputStream = null;
			String result = "";

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();
				jsonObject
						.accumulate("user_id", SplashScreen.userModel.user_id);
				jsonObject.accumulate("status", strInputUpdateStatus);

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
					result = AFunctionOther
							.convertInputStreamToString(inputStream);
				else
					result = "Did not work!";

				Log.v("result change status : ", "" + result);

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
			initLoadAllData();
		}
	}

	// ------- dialog chang password ----------
	private void dialogChangePassword(String str_user_id, String str_user_pss) {
		final String strId, strPss;
		strId = str_user_id;
		strPss = str_user_pss;

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
						String strInput = dl_et_input.getText().toString()
								.trim();

						if (strInput.equals(strPss)) {
							dialogChangePasswordConfirm(strId, strInput);
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

		// LayoutInflater inflater = LayoutInflater.from(context);
		// View dlPss = inflater.inflate(
		// R.layout.dialog_profile_edit_password, null);
		// AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
		// context);
		// // alertDialogBuilder.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// alertDialogBuilder.setView(dlPss);
		//
		// final EditText dl_et_input = (EditText) dlPss
		// .findViewById(R.id.dl_et_input);
		//
		// // set dialog message
		// alertDialogBuilder
		// .setCancelable(false)
		// .setPositiveButton("Next",
		// new DialogInterface.OnClickListener() {
		// public void onClick(
		// DialogInterface dialog, int id) {
		//
		// String strInput = dl_et_input.getText().toString();
		// dialogChangePasswordConfirm(strId, strInput);
		//
		// }
		// })
		// .setNegativeButton("Cancel",
		// new DialogInterface.OnClickListener() {
		// public void onClick(
		// DialogInterface dialog, int id) {
		// dialog.cancel();
		// }
		// });
		// // create alert dialog
		// AlertDialog alertDialog = alertDialogBuilder.create();
		// // show it
		// alertDialog.show();
	}

	private void dialogChangePasswordConfirm(String str_user_id,
			String str_user_pss) {

		final String strId, strPssOld;
		strId = str_user_id;
		strPssOld = str_user_pss;

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

						// if ((strInputNew.equals(""))
						// || (strInputConf.equals(""))) {
						// Toast.makeText(context, "Input Password", 0).show();
						// } else
						if ((strInputNew.equals(strInputConf))) {
							EditProfile.editPassword(strId, strPssOld,
									strInputNew);
							dialog.dismiss();
						} else {
							Toast.makeText(
									context,
									"Input Password : " + strInputNew + "_"
											+ strInputNew, 0).show();
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

		// String strId, strPss;
		// strId = str_user_id;
		// strPss = str_user_pss;
		// LayoutInflater inflater = LayoutInflater.from(context);
		// View dlPss = inflater.inflate(
		// R.layout.dialog_profile_edit_password_confirm, null);
		// AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
		// context);
		// // alertDialogBuilder.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// alertDialogBuilder.setView(dlPss);
		//
		// final EditText dl_et_new_pss = (EditText) dlPss
		// .findViewById(R.id.dl_et_new_pss);
		// final EditText dl_et_confirm_pss = (EditText) dlPss
		// .findViewById(R.id.dl_et_confirm_pss);
		//
		// // set dialog message
		// alertDialogBuilder
		// .setCancelable(false)
		// .setPositiveButton("Next",
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int id) {
		//
		// String strPssNew = dl_et_new_pss.getText()
		// .toString();
		// String strPssCon = dl_et_confirm_pss.getText()
		// .toString();
		//
		// }
		// })
		// .setNegativeButton("Cancel",
		// new DialogInterface.OnClickListener() {
		// public void onClick(DialogInterface dialog, int id) {
		// dialog.cancel();
		// }
		// });
		// // create alert dialog
		// AlertDialog alertDialog = alertDialogBuilder.create();
		// // show it
		// alertDialog.show();
	}

	// ************* add search to fav ***************
	public static Handler handlerProfile = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switchFragmentProfile(new PagerProfile());
		}
	};

	// the meat of switching the above fragment
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;
		if (getActivity() instanceof FragmentChangeActivity) {
			FragmentChangeActivity fca = (FragmentChangeActivity) getActivity();
			fca.switchContent(fragment);
		}
	}

	// the meat of switching the above fragment
	public static void switchFragmentProfile(Fragment fragment) {
		if (context == null)
			return;
		if (context instanceof FragmentChangeActivity) {
			FragmentChangeActivity fca = (FragmentChangeActivity) context;
			fca.switchContent(fragment);
		}
	}

}