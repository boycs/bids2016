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
import com.app.bids.PagerWatchlistDetail.loadData;
import com.app.bids.UiWatchListDetailNewsSelect.loadAll;
import com.app.bids.util.IabHelper;
import com.app.bids.util.IabResult;
import com.app.bids.util.Inventory;
import com.app.bids.util.Purchase;
import com.app.bids.util.SkuDetails;
import com.app.model.login.FacebookLoginActivity;
import com.app.model.login.LoginDialog2;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class UiProfileBilling extends Activity {

	public static JSONObject contentGetPostSelect = null;
	public static JSONArray contentGetSymbolSelect = null;

	Dialog dialogLoading;

	private static Context context;
	private static String tag;
	private static IabHelper mHelper;

	private static final String base64PublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAlHVZM+ExaSPWh3QYkIpkE/aFkTKC9mAUvYFL/iHOOY0lraEQ3f7VwH03jUqz/TotEsZEnd12ZLnQ+tGqWuckISKnhiUNqlmy1erGnhPaIasEAMg5hhBr6TZ0Blrcl+s081iQNQwCAw/Tq7vTMC12cQQ+TbhKIxBXiAl0rFn7XpBtLyAZSxD01DkC2SqW2njnLwzF0PbYIXdJsvZ4mfXNL4lqxNfMpLXEXPxWrCJrVCvhvyO9VYw64wSF2bbfwVMl64tN/cv2bR1Vn7z8yAKcKXUtgFbGQcKDg4zf/bPL0jC/6vDyQlwyJeIm/69g8x4s/v3odc8Q1gTtewYChX8jJQIDAQAB";

	private static boolean isSetup;
	// ProductID
	private static final String productID = "android.test.purchased"; // Test
																		// Product

	// Purchase
	private static Purchase purchaseOwned;

	// View
	private Button btnQuery, btnPurchase, btnConsume;

	boolean ckSuccessBilling;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ui_profile_billing);

		// --------- dialogLoading
		dialogLoading = new Dialog(this);
		dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogLoading.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialogLoading.setContentView(R.layout.progress_bar);
		dialogLoading.setCancelable(false);
		dialogLoading.setCanceledOnTouchOutside(false);

		// dialogLoading.show();
		// Context
		context = getApplicationContext();

		// log tag
		tag = "in_app_billing_test";

		// Helper
		mHelper = new IabHelper(context, base64PublicKey);
		// mHelper.enableDebugLogging(true);
		mHelper.enableDebugLogging(true, tag);
		
		// Assign View
		btnQuery = (Button) findViewById(R.id.btnQuery);
		btnPurchase = (Button) findViewById(R.id.btnPurchase_1);
		btnConsume = (Button) findViewById(R.id.btnConsume);

		try {
			mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
				@Override
				public void onIabSetupFinished(IabResult result) {
					boolean blnSuccess = result.isSuccess();
					boolean blnFail = result.isFailure();

					isSetup = blnSuccess;

					Toast.makeText(
							context,
							"mHelper.startSetup() - blnSuccess return "
									+ String.valueOf(blnSuccess),
							Toast.LENGTH_SHORT).show();
					Toast.makeText(
							context,
							"mHelper.startSetup() - blnFail return "
									+ String.valueOf(blnFail),
							Toast.LENGTH_SHORT).show();
					
					Log.d(tag, "mHelper.startSetup() ...");
					Log.d(tag,
							"	- blnSuccess return "
									+ String.valueOf(blnSuccess));
					Log.d(tag, "	- blnFail return " + String.valueOf(blnFail));
				}
			});
		} catch (Exception e) {
			e.printStackTrace();

			isSetup = false;
			Toast.makeText(context, "mHelper.startSetup() - fail!",
					Toast.LENGTH_SHORT).show();
			Log.w(tag, "mHelper.startSetup() - fail!");
		}

		// ========== btn Query ==========
		btnQuery.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isSetup)
					return;

				mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
					@Override
					public void onQueryInventoryFinished(IabResult result,
							Inventory inv) {
						boolean blnSuccess = result.isSuccess();
						boolean blnFail = result.isFailure();

						Toast.makeText(
								context,
								"mHelper.queryInventoryAsync() - blnSuccess return "
										+ String.valueOf(blnSuccess),
								Toast.LENGTH_SHORT).show();
						Toast.makeText(
								context,
								"mHelper.queryInventoryAsync() - blnFail return "
										+ String.valueOf(blnFail),
								Toast.LENGTH_SHORT).show();
						Log.d(tag, "mHelper.queryInventoryAsync() ...");
						Log.d(tag,
								"	- blnSuccess return "
										+ String.valueOf(blnSuccess));
						Log.d(tag,
								"	- blnFail return " + String.valueOf(blnFail));

						if (!blnSuccess)
							return;

						Log.d(tag,
								"	- inv.hasPurchase()   = "
										+ inv.hasPurchase(productID));
						Log.d(tag,
								"	- inv.getPurchase()   = "
										+ inv.getPurchase(productID));
						Log.d(tag,
								"	- inv.hasDetails()    = "
										+ inv.hasDetails(productID));
						Log.d(tag,
								"	- inv.getSkuDetails() = "
										+ inv.getSkuDetails(productID));

						if (!inv.hasPurchase(productID))
							return;

						purchaseOwned = inv.getPurchase(productID);

						Log.d(tag, "	- inv.getPurchase() ...");
						Log.d(tag, "		.getDeveloperPayload() = "
								+ purchaseOwned.getDeveloperPayload());
						Log.d(tag, "		.getItemType()         = "
								+ purchaseOwned.getItemType());
						Log.d(tag, "		.getOrderId()          = "
								+ purchaseOwned.getOrderId());
						Log.d(tag, "		.getOriginalJson()     = "
								+ purchaseOwned.getOriginalJson());
						Log.d(tag, "		.getPackageName()      = "
								+ purchaseOwned.getPackageName());
						Log.d(tag,
								"		.getPurchaseState()    = "
										+ String.valueOf(purchaseOwned
												.getPurchaseState()));
						Log.d(tag,
								"		.getPurchaseTime()     = "
										+ String.valueOf(purchaseOwned
												.getPurchaseTime()));
						Log.d(tag, "		.getSignature()        = "
								+ purchaseOwned.getSignature());
						Log.d(tag, "		.getSku()              = "
								+ purchaseOwned.getSku());
						Log.d(tag, "		.getToken()            = "
								+ purchaseOwned.getToken());

						if (!inv.hasDetails(productID))
							return;

						SkuDetails skuDetails = inv.getSkuDetails(productID);
						Log.d(tag, "	- inv.getSkuDetails() ...");
						Log.d(tag,
								"		.getDescription() = "
										+ skuDetails.getDescription());
						Log.d(tag,
								"		.getPrice()       = "
										+ skuDetails.getPrice());
						Log.d(tag,
								"		.getSku()         = " + skuDetails.getSku());
						Log.d(tag,
								"		.getTitle()       = "
										+ skuDetails.getTitle());
						Log.d(tag,
								"		.getType()        = " + skuDetails.getType());
					}
				});
			}
		});

		// ========== btn Purchase ==========
		btnPurchase.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isSetup)
					return;

				mHelper.launchPurchaseFlow(UiProfileBilling.this, productID,
						1001, new IabHelper.OnIabPurchaseFinishedListener() {
							@Override
							public void onIabPurchaseFinished(IabResult result,
									Purchase info) {
								boolean blnSuccess = result.isSuccess();
								boolean blnFail = result.isFailure();

								Toast.makeText(
										context,
										"mHelper.launchPurchaseFlow() - blnSuccess return "
												+ String.valueOf(blnSuccess),
										Toast.LENGTH_SHORT).show();
								Toast.makeText(
										context,
										"mHelper.launchPurchaseFlow() - blnFail return "
												+ String.valueOf(blnFail),
										Toast.LENGTH_SHORT).show();
								Log.d(tag, "mHelper.launchPurchaseFlow() ...");

								// - blnSuccess return true สำเร็จ false
								// ไม่สำเร็จ
								// - blnFail return false สำเร็จ true ไม่สำเร็จ

								ckSuccessBilling = blnSuccess;
								Log.d(tag,
										"	- blnSuccess return "
												+ String.valueOf(blnSuccess));
								Log.d(tag,
										"	- blnFail return "
												+ String.valueOf(blnFail));

								if (!blnSuccess)
									return;

								purchaseOwned = info;

								Log.d(tag, "	- info ...");
								Log.d(tag,
										"		.getDeveloperPayload() = "
												+ info.getDeveloperPayload());
								Log.d(tag,
										"		.getItemType()         = "
												+ info.getItemType());
								Log.d(tag,
										"		.getOrderId()          = "
												+ info.getOrderId());
								Log.d(tag,
										"		.getOriginalJson()     = "
												+ info.getOriginalJson());
								Log.d(tag,
										"		.getPackageName()      = "
												+ info.getPackageName());
								Log.d(tag,
										"		.getPurchaseState()    = "
												+ String.valueOf(info
														.getPurchaseState()));
								Log.d(tag,
										"		.getPurchaseTime()     = "
												+ String.valueOf(info
														.getPurchaseTime()));
								Log.d(tag,
										"		.getSignature()        = "
												+ info.getSignature());
								Log.d(tag,
										"		.getSku()              = "
												+ info.getSku());
								Log.d(tag,
										"		.getToken()            = "
												+ info.getToken());
							}
						}, "bGoa+V7g/yqDXvKRqq+JTFn4uQZbPiQJo4pf9RzJ");
			}
		});

		// ========== btn Consume ==========
		btnConsume.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!isSetup)
					return;
				if (purchaseOwned == null)
					return; 

				mHelper.consumeAsync(purchaseOwned,
						new IabHelper.OnConsumeFinishedListener() {
							@Override
							public void onConsumeFinished(Purchase purchase,
									IabResult result) {
								boolean blnSuccess = result.isSuccess();
								boolean blnFail = result.isFailure();

								Toast.makeText(
										context,
										"mHelper.consumeAsync() - blnSuccess return "
												+ String.valueOf(blnSuccess),
										Toast.LENGTH_SHORT).show();
								Toast.makeText(
										context,
										"mHelper.consumeAsync() - blnFail return "
												+ String.valueOf(blnFail),
										Toast.LENGTH_SHORT).show();
								Log.d(tag, "mHelper.consumeAsync() ...");
								Log.d(tag,
										"	- blnSuccess return "
												+ String.valueOf(blnSuccess));
								Log.d(tag,
										"	- blnFail return "
												+ String.valueOf(blnFail));

								if (!blnSuccess)
									return;

								purchaseOwned = null;

								Log.d(tag, "	- purchase ...");
								Log.d(tag, "		.getDeveloperPayload() = "
										+ purchase.getDeveloperPayload());
								Log.d(tag, "		.getItemType()         = "
										+ purchase.getItemType());
								Log.d(tag, "		.getOrderId()          = "
										+ purchase.getOrderId());
								Log.d(tag, "		.getOriginalJson()     = "
										+ purchase.getOriginalJson());
								Log.d(tag, "		.getPackageName()      = "
										+ purchase.getPackageName());
								Log.d(tag,
										"		.getPurchaseState()    = "
												+ String.valueOf(purchase
														.getPurchaseState()));
								Log.d(tag,
										"		.getPurchaseTime()     = "
												+ String.valueOf(purchase
														.getPurchaseTime()));
								Log.d(tag, "		.getSignature()        = "
										+ purchase.getSignature());
								Log.d(tag, "		.getSku()              = "
										+ purchase.getSku());
								Log.d(tag, "		.getToken()            = "
										+ purchase.getToken());
							}
						});
			}
		});

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (isSetup) {
			boolean blnResult = mHelper.handleActivityResult(requestCode,
					resultCode, data);

			Toast.makeText(
					context,
					"onActivityResult() - mHelper.handleActivityResult() = "
							+ blnResult, Toast.LENGTH_SHORT).show();
			Log.d(tag, "onActivityResult() - mHelper.handleActivityResult() = "
					+ blnResult);

			 if (blnResult)
				 sendBilling();
//				 if(ckSuccessBilling){
//					 sendBilling();
//				 }
			return;
		}
		super.onActivityResult(requestCode, resultCode, data);

	}

	@Override
	protected void onDestroy() {
		if (isSetup)
			mHelper.dispose();
		mHelper = null;

		super.onDestroy();
	}

	// ============== send billing =============

	public static JSONArray contentGetFavoriteId = null;

	private void sendBilling() {
		getFavoriteId resp = new getFavoriteId();
		resp.execute();
	}

	public class getFavoriteId extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;
		// ======= json ========
		private JSONObject jsonBilling;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialogLoading.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// ======= url ========

			// http://bidschart.com/service/v2/setPayPromotion?user_id=10&packet_id=free&device_id=d983a4d5757d8aca&platform=android

			// http://bidschart.com/service/v2/setPayPromotion?user_id=38&packet_id=free&device_id=web&platform=web
			// method::post
			// packet_id :: premium7d , premium1m , premium6m , premium12m ,
			// free
			// platform :: web , android , ios

			// -----------------------------------------
			String url = SplashScreen.url_bidschart
					+ "/service/v2/setPayPromotion";

			String json = "";
			InputStream inputStream = null;
			String result = "";

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();

				jsonObject.accumulate("user_id",
						SplashScreen.contentGetUserById
								.getString("user_id"));
				jsonObject.accumulate("packet_id", PagerProfile.strPacketId);
				jsonObject.accumulate("device_id",
						SplashScreen.deviceId);
				jsonObject.accumulate("platform", "android");

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
				finish();
			} else {
				Toast.makeText(getApplicationContext(), "connecttion Error!!",
						0).show();
			}
			dialogLoading.dismiss();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish();
			// overridePendingTransition(R.animator.right_to_center,
			// R.animator.center_to_right);
		}
		return super.onKeyDown(keyCode, event);
	}

}
