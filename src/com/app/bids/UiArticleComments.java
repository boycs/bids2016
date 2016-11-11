package com.app.bids;

import java.io.IOException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.app.bids.R;
import com.app.model.login.LoginDialog2;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class UiArticleComments extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getActionBar().hide();

		setContentView(R.layout.ui_article_comments);

		initViewSelect();
		initGetArticleComment(); // get Article Comment

	}

	// ====================== Initial View ========================
	private EditText et_comments;
	private Button bt_send_comments;
	private String strComments;
	private String urlPostComments;
	private String article_id, comments_id, reply_id, content, chart_url = "";

	private void initViewSelect() {
//		LinearLayout li_back = (LinearLayout) findViewById(R.id.li_back);
//		et_comments = (EditText) findViewById(R.id.et_comments);
//		bt_send_comments = (Button) findViewById(R.id.bt_send_comments);
//
//		TextView tv_likes_count = (TextView) findViewById(R.id.tv_likes_count);
//		TextView tv_comments_count = (TextView) findViewById(R.id.tv_comments_count);
//		try {
//			tv_likes_count.setText(""
//					+ SplashScreen.contentGetArticleSelect.get(
//							"likes_count").toString() + "  ถูกใจ ");
//			tv_comments_count.setText(""
//					+ SplashScreen.contentGetArticleSelect.get(
//							"comments_count").toString() + "  ความคิดเห็น");
//		} catch (JSONException e1) {
//			e1.printStackTrace();
//		}
//
//		li_back.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				finish();
//				overridePendingTransition(R.animator.right_to_center,
//						R.animator.center_to_right);
//			}
//		});
//
//		bt_send_comments.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				strComments = et_comments.getText().toString().trim();
//				if (strComments.equals("")) {
//					Toast.makeText(getApplicationContext(), "input data", 0)
//							.show();
//				} else if (FragmentChangeActivity.userModel.user_id == "") {
//					// Toast.makeText(getApplicationContext(), "Login", 0)
//					// .show();
//
//					LoginDialog.show();
//
//				} else {
//					try {
//						content = strComments;
//						article_id = ""
//								+ SplashScreen.contentGetArticleSelect
//										.get("id");
//
//						// article_id=3&user_id=2&content=test&chart_url=
//						CommentLikeArticle.sendArticleComment(article_id,
//								FragmentChangeActivity.userModel.user_id, content); // send
//																				// Post
//																				// Comment
//						initGetArticleComment();
//
//						et_comments.setText("");
//
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		});
	}

	// ============== get Post Comment =========================
	public static JSONArray contentGetArticleComment = null;

	private void initGetArticleComment() {
//		getGetArticleComment resp = new getGetArticleComment();
//		resp.execute();
//	}
//
//	public class getGetArticleComment extends AsyncTask<Void, Void, Void> {
//
//		boolean connectionError = false;
//
//		private JSONObject jsonGetArticleComment;
//
//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//			// progress.show();
//
//			// hide show loading
//			// ((TextView) findViewById(R.id.tv_loading))
//			// .setVisibility(View.VISIBLE);
//			//
//			// ((LinearLayout) findViewById(R.id.hide_layout))
//			// .setVisibility(View.GONE);
//
//		}
//
//		@Override
//		protected Void doInBackground(Void... params) {
//
//			java.util.Date date = new java.util.Date();
//			long timestamp = date.getTime();
//			// ======= Ui Home ========
//			String url_GetArticleComment;
//			try {
//
//				// FragmentChangeActivity.url_bidschart+"/service/getCommentReplyByArticleId?article_id=3&user_id=2
//				url_GetArticleComment = FragmentChangeActivity.url_bidschart+"/service/getCommentReplyByArticleId?article_id="
//						+ SplashScreen.contentGetArticleSelect
//								.get("id").toString()
//						+ "&user_id="
//						+ FragmentChangeActivity.userModel.user_id;
//
//				Log.v("url_GetArticleComment", "" + url_GetArticleComment);
//
//				jsonGetArticleComment = ReadJson
//						.readJsonObjectFromUrl(url_GetArticleComment);
//				
//			} catch (IOException e1) {
//				connectionError = true;
//				jsonGetArticleComment = null;
//				e1.printStackTrace();
//			} catch (JSONException e1) {
//				connectionError = true;
//				jsonGetArticleComment = null;
//				e1.printStackTrace();
//			} catch (RuntimeException e) {
//				connectionError = true;
//				jsonGetArticleComment = null;
//				e.printStackTrace();
//			}
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void result) {
//			super.onPostExecute(result);
//
//			if (connectionError == false) {
//				if (jsonGetArticleComment != null) {
//					try {
//						
//
////						{"status":"ok","message":"Get data Success.","dataAll":
////							[{"comments_id":34,"content":"test1","comment_user_id":60,"article_id":418,"agree_count":0,"disagree_count":0,"reply_count":0,"comment_datetime":"2015-04-24 10:10:17","chart_url":"","username":"Sirawich Thangern","replys":[]}]}
//
//
//						contentGetArticleComment = jsonGetArticleComment
//								.getJSONArray("dataAll");
////						contentGetArticleCommentList = contentGetArticleComment
////								.getJSONArray("replys");
//
//						// Log.v("contentGetPostComment", ""
//						// + contentGetPostComment);
//						// Log.v("contentGetPostCommentList", ""
//						// + contentGetPostCommentList);
//
//						initListComments(); // Initial List Comments
//
//						// hide show loading
//						// ((TextView) findViewById(R.id.tv_loading))
//						// .setVisibility(View.GONE);
//						//
//						// ((LinearLayout) findViewById(R.id.hide_layout))
//						// .setVisibility(View.VISIBLE);
//
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//				} else {
//					Log.v("jsonGetPostComment", "null");
//				}
//			} else {
//				Log.v("connectionError", "true");
//			}
//
//			// hide show loading
//			// ((TextView)
//			// findViewById(R.id.tv_loading)).setVisibility(View.GONE);
//			//
//			// ((LinearLayout) findViewById(R.id.hide_layout))
//			// .setVisibility(View.VISIBLE);
//		}
	}

	// ====================== Initial List Comments ========================
	private String replyComment;
	private String replyUserComment;
	private String replyContent;
	private String replyComment_id;
	private String replyInput;
	private boolean ckLikePost = true;
	private boolean ckLikeReply = true;

	private void initListComments() {

//		LinearLayout li_comments = (LinearLayout) findViewById(R.id.li_comments);
//		li_comments.removeAllViews();
//
//		if (contentGetArticleComment.length() > 0) {
//			for (int i = 0; i < contentGetArticleComment.length(); i++) {
//				View viewCm = getLayoutInflater().inflate(
//						R.layout.row_post_comments, null);
//
//				final LinearLayout li_row_comments = (LinearLayout) viewCm
//						.findViewById(R.id.li_row_comments);
//				TextView tv_username = (com.app.custom.CustomTextView) viewCm
//						.findViewById(R.id.tv_username);
//				TextView tv_content = (com.app.custom.CustomTextView) viewCm
//						.findViewById(R.id.tv_content);
//				TextView tv_agree_count = (com.app.custom.CustomTextView) viewCm
//						.findViewById(R.id.tv_agree_count);
//				TextView tv_disagree_count = (com.app.custom.CustomTextView) viewCm
//						.findViewById(R.id.tv_disagree_count);
//				ImageView img_agree = (ImageView) viewCm
//						.findViewById(R.id.img_agree);
//				ImageView img_disagree = (ImageView) viewCm
//						.findViewById(R.id.img_disagree);
//
//				try {
//
//					Log.v("contentGetArticleComment",""+contentGetArticleComment);
//					
//					tv_username.setText(""
//							+ contentGetArticleComment.getJSONObject(i)
//									.get("username"));
//					tv_content.setText(""
//							+ contentGetArticleComment.getJSONObject(i)
//									.get("content"));
//					tv_agree_count.setText(""
//							+ contentGetArticleComment.getJSONObject(i)
//									.get("agree_count"));
//					tv_disagree_count.setText(""
//							+ contentGetArticleComment.getJSONObject(i)
//									.get("disagree_count"));
//
//					final JSONArray contentGetPostReply = (JSONArray) contentGetArticleComment
//							.getJSONObject(i).get("replys");
//					
//
//					final int j = i;
//
//					// like post
//					img_agree.setOnClickListener(new OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							if (FragmentChangeActivity.userModel.user_id == "") {
//								Toast.makeText(getApplicationContext(),
//										"Login", 0).show();
//								LoginDialog.show();
//							} else {
//								ckLikePost = true;
//								try {
//									article_id = ""
//											+ SplashScreen.contentGetArticleSelect
//													.get("id");
//									comments_id = ""
//											+ contentGetArticleComment
//													.getJSONObject(j).get(
//															"comments_id");
//
//									// ComsendPostLike(); // like
//									CommentLikeArticle.sendArticleLike(
//											comments_id,
//											FragmentChangeActivity.userModel.user_id,
//											ckLikePost);
//									initGetArticleComment(); // get
//																// Post
//																// Comment
//
//								} catch (JSONException e) {
//									e.printStackTrace();
//								}
//							}
//						}
//					});
//
//					// dislike post
//					img_disagree.setOnClickListener(new OnClickListener() {
//						@Override
//						public void onClick(View v) {
//							if (FragmentChangeActivity.userModel.user_id == "") {
//								Toast.makeText(getApplicationContext(),
//										"Login", 0).show();
//								LoginDialog.show();
//							} else {
//								ckLikePost = false;
//								try {
//									article_id = ""
//											+ SplashScreen.contentGetArticleSelect
//													.get("id");
//									comments_id = ""
//											+ contentGetArticleComment
//													.getJSONObject(j).get(
//															"comments_id");
//
//									// sendPostLike(); // un like
//									CommentLikeArticle.sendArticleLike(
//											comments_id,
//											FragmentChangeActivity.userModel.user_id,
//											ckLikePost);
//									initGetArticleComment(); // get
//									// Post
//									// Comment
//
//								} catch (JSONException e) {
//									e.printStackTrace();
//								}
//							}
//						}
//					});
//
//				} catch (JSONException e1) {
//					e1.printStackTrace();
//				}
//				li_comments.addView(viewCm);
//			}
//		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
