package com.app.bids;

import java.io.IOException;
import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;
import com.app.bids.R.color;
import com.app.model.login.LoginDialog;
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
public class UiArticleSelectComments extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
		LinearLayout li_back = (LinearLayout) findViewById(R.id.li_back);
		et_comments = (EditText) findViewById(R.id.et_comments);
		bt_send_comments = (Button) findViewById(R.id.bt_send_comments);

		ImageView img_like_count = (ImageView) findViewById(R.id.img_like_count);
		TextView tv_likes_count = (TextView) findViewById(R.id.tv_likes_count);
		try {
			tv_likes_count.setText(""
					+ AttributeBegin.contentGetNewsSelect.get("likes_count")
							.toString() + " คนถูกใจ ");

			if ((AttributeBegin.contentGetNewsSelect.get("this_like")).equals("YES")) {
				img_like_count.setBackgroundResource(R.drawable.icon_checklike);
				tv_likes_count.setTextColor(getResources().getColor(color.c_success));
			}
		} catch (JSONException e1) {
			e1.printStackTrace();
		}

		li_back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});

		bt_send_comments.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				strComments = et_comments.getText().toString().trim();
				if (strComments.equals("")) {
					Toast.makeText(getApplicationContext(), "input comment", 0)
							.show();
				} else if (SplashScreen.userModel.user_id != "") {
					try {
						content = strComments;
						article_id = ""
								+ AttributeBegin.contentGetNewsSelect.get("id");

						// article_id=3&user_id=2&content=test&chart_url=
						CommentLikeArticle.sendArticleComment(article_id,
								SplashScreen.userModel.user_id,
								content); // send Comment
						initGetArticleComment();

						et_comments.setText("");

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(getApplicationContext(), "Login", 0).show();

					LoginDialog.show();

				}
			}
		});
	}

	// ============== get Post Comment =========================
	public static JSONArray contentGetArticleComment = null;

	// public static JSONArray contentGetArticleCommentList = null;

	private void initGetArticleComment() {
		getGetArticleComment resp = new getGetArticleComment();
		resp.execute();
	}

	public class getGetArticleComment extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		private JSONObject jsonGetArticleComment;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progress.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// ======= Ui Home ========
			String url_GetArticleComment;
			try {

				// FragmentChangeActivity.url_bidschart+"/service/getCommentReplyByArticleId?article_id=2388&user_id=104
				url_GetArticleComment = SplashScreen.url_bidschart+"/service/getCommentReplyByArticleId?article_id="
						+ AttributeBegin.contentGetNewsSelect.get("id").toString()
						+ "&user_id="
						+ SplashScreen.userModel.user_id+"&timestamp="+ timestamp;

				Log.v("url_GetArticleComment", "" + url_GetArticleComment);

				jsonGetArticleComment = ReadJson
						.readJsonObjectFromUrl(url_GetArticleComment);

			} catch (IOException e1) {
				connectionError = true;
				jsonGetArticleComment = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonGetArticleComment = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonGetArticleComment = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (jsonGetArticleComment != null) {
					try {

						// {"status":"ok","message":"Get data Success.","dataAll":
						// [{"comments_id":34,"content":"test1","comment_user_id":60,"article_id":418,"agree_count":0,"disagree_count":0,"reply_count":0,"comment_datetime":"2015-04-24 10:10:17","chart_url":"","username":"Sirawich Thangern","replys":[]}]}

						contentGetArticleComment = jsonGetArticleComment
								.getJSONArray("dataAll");

						initListComments(); // Initial List Comments

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.v("jsonGetPostComment", "null");
				}
			} else {
				Log.v("connectionError", "true");
			}

		}
	}

	// ====================== Initial List Comments ========================
	private String replyComment;
	private String replyUserComment;
	private String replyContent;
	private String replyComment_id;
	private String replyInput;
	private boolean ckLikePost = true;
	private boolean ckLikeReply = true;

	ImageView img_agree, img_disagree;
	TextView tv_agree_count, tv_disagree_count, tv_comment_del;
	LinearLayout li_agree, li_disagree;

	private void initListComments() {

		LinearLayout li_comments = (LinearLayout) findViewById(R.id.li_comments);
		li_comments.removeAllViews();

		if (contentGetArticleComment.length() > 0) {
			for (int i = 0; i < contentGetArticleComment.length(); i++) {
				View viewCm = getLayoutInflater().inflate(
						R.layout.row_comments_article, null);

				final LinearLayout li_row_comments = (LinearLayout) viewCm
						.findViewById(R.id.li_row_comments);
				TextView tv_username = (TextView) viewCm
						.findViewById(R.id.tv_username);
				TextView tv_comment_datetime = (TextView) viewCm
						.findViewById(R.id.tv_comment_datetime);
				TextView tv_content = (TextView) viewCm
						.findViewById(R.id.tv_content);

				tv_agree_count = (TextView) viewCm
						.findViewById(R.id.tv_agree_count);
				tv_disagree_count = (TextView) viewCm
						.findViewById(R.id.tv_disagree_count);
				img_agree = (ImageView) viewCm.findViewById(R.id.img_agree);
				img_disagree = (ImageView) viewCm
						.findViewById(R.id.img_disagree);
				tv_comment_del = (TextView) viewCm
						.findViewById(R.id.tv_comment_del);
				li_agree = (LinearLayout) viewCm.findViewById(R.id.li_agree);
				li_disagree = (LinearLayout) viewCm
						.findViewById(R.id.li_disagree);

				try {

					tv_username.setText(""
							+ contentGetArticleComment.getJSONObject(i).get(
									"username"));
					tv_content.setText(""
							+ contentGetArticleComment.getJSONObject(i).get(
									"content"));
					tv_agree_count.setText(""
							+ contentGetArticleComment.getJSONObject(i).get(
									"agree_count"));
					tv_disagree_count.setText(""
							+ contentGetArticleComment.getJSONObject(i).get(
									"disagree_count"));
 
					// agree, uagree comment
					img_agree.setBackgroundResource(R.drawable.icon_agree);
					img_disagree.setBackgroundResource(R.drawable.icon_uagree);
					tv_agree_count.setTextColor(getResources().getColor(R.color.c_content));
					tv_disagree_count.setTextColor(getResources().getColor(R.color.c_content));
					
					// if ((contentGetArticleComment.getJSONObject(i)
					// .get("commentstatus")).equals("agree")) {
					if ((contentGetArticleComment.getJSONObject(i).get(
							"commentstatus").toString()).equals("agree")) {
						img_agree
								.setBackgroundResource(R.drawable.icon_checkagree);
						tv_agree_count.setTextColor(getResources().getColor(R.color.c_success));
					} else if ((contentGetArticleComment.getJSONObject(i).get(
							"commentstatus").toString()).equals("disagree")) {
						img_disagree
								.setBackgroundResource(R.drawable.icon_checkuagree);
						tv_disagree_count.setTextColor(getResources().getColor(R.color.c_danger));
					}

					// date ago
					try {
						String strDateAgo = ""
								+ DateTimeAgo
										.CalAgoTime2(contentGetArticleComment
												.getJSONObject(i)
												.get("comment_datetime")
												.toString());
						tv_comment_datetime.setText(strDateAgo);
					} catch (ParseException e) {
						e.printStackTrace();
					}

					// like post
					final int j = i;
					li_agree.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (SplashScreen.userModel.user_id != "") {
								ckLikePost = true;
								try {
									article_id = ""
											+ AttributeBegin.contentGetNewsSelect
													.get("id");
									comments_id = ""
											+ contentGetArticleComment
													.getJSONObject(j).get(
															"comments_id");

									// ComsendPostLike(); // like
									CommentLikeArticle
											.sendArticleLike(
													comments_id,
													SplashScreen.userModel.user_id,
													ckLikePost);
									initGetArticleComment(); // get Comment

								} catch (JSONException e) {
									e.printStackTrace();
								}
							} else {
								Toast.makeText(getApplicationContext(),
										"Login", 0).show();
								// LoginDialog.show();
							}
						}
					});

					// dislike post
					li_disagree.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (SplashScreen.userModel.user_id != "") {
								ckLikePost = false;
								try {
									article_id = ""
											+ AttributeBegin.contentGetNewsSelect
													.get("id");
									comments_id = ""
											+ contentGetArticleComment
													.getJSONObject(j).get(
															"comments_id");

									// sendPostLike(); // un like
									CommentLikeArticle
											.sendArticleLike(
													comments_id,
													SplashScreen.userModel.user_id,
													ckLikePost);
									initGetArticleComment(); // get Comment

								} catch (JSONException e) {
									e.printStackTrace();
								}
							} else {
								Toast.makeText(getApplicationContext(),
										"Login", 0).show();
								// LoginDialog.show();
							}
						}
					});

					// check show x
					if ((contentGetArticleComment.getJSONObject(i).get("username"))
							.equals(SplashScreen.userModel.userName)) {
						tv_comment_del.setVisibility(View.VISIBLE);
					}

					// delete post
					final String str_comment = contentGetArticleComment
							.getJSONObject(i).get("content").toString();
					
					final AlertDialog.Builder adb = new AlertDialog.Builder(this);
					tv_comment_del.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (SplashScreen.userModel.user_id != "") {
								adb.setTitle("BIDs");
								adb.setMessage("Delete comment '" + str_comment + "'");
								adb.setNegativeButton("Cancle", null);
								adb.setPositiveButton("Sure", new AlertDialog.OnClickListener() {
									public void onClick(DialogInterface dialog, int arg1) {
										try {
											comments_id = ""
													+ contentGetArticleComment
															.getJSONObject(j)
															.get("comments_id");

											// remove comment
											CommentLikeArticle
													.sendArticleRemoveComment(comments_id);

											initGetArticleComment(); // get Comment;

										} catch (JSONException e) {
											e.printStackTrace();
										}
									}
								});
								adb.show();
							} else {
								Toast.makeText(getApplicationContext(),
										"Login", 0).show();
								// LoginDialog.show();
							}
						}
					});

				} catch (JSONException e1) {
					e1.printStackTrace();
				}
				li_comments.addView(viewCm);
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			finish();
		}
		return super.onKeyDown(keyCode, event);
	}

}
