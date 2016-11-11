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
public class UiProfileIdeasSelectComments extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.ui_ideas_comments);

		initViewSelect();
		initGetPostComment(); // get Comment

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
					+ PagerProfile.contentIdeasSelect.get("likes_count")
							.toString() + " คนถูกใจ ");

			if ((PagerProfile.contentIdeasSelect.get("this_like"))
					.equals("YES")) {
				img_like_count.setBackgroundResource(R.drawable.icon_checklike);
				tv_likes_count.setTextColor(getResources().getColor(
						color.c_success));
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
								+ PagerProfile.contentIdeasSelect
										.get("post_id");

						// article_id=3&user_id=2&content=test&chart_url=
						CommentLikePost.sendPostComment(article_id,
								SplashScreen.userModel.user_id,
								content); // send Comment

						initGetPostComment();

						et_comments.setText("");

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(getApplicationContext(), "Login", 0).show();

					LoginDialog.show();
					// LoginDialog.show();
				}
			}
		});
	}

	// ============== get Post Comment =========================
	// FragmentChangeActivity.url_bidschart+"/service/getPostCommentReplyByIdPost?post_id=99&user_id=1
	public static JSONObject contentGetPostComment = null;
	public static JSONArray contentGetPostCommentList = null;

	private void initGetPostComment() {
		getGetPostComment resp = new getGetPostComment();
		resp.execute();
	}

	public class getGetPostComment extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		private JSONObject jsonGetPostComment;

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

//				FragmentChangeActivity.url_bidschart+"/service/getPostCommentReplyByIdPost?post_id=185&user_id=104
				
				url_GetArticleComment = SplashScreen.url_bidschart+"/service/getPostCommentReplyByIdPost?post_id="
						+ PagerProfile.contentIdeasSelect.get("post_id")
								.toString()
						+ "&user_id="
						+ SplashScreen.userModel.user_id+"&timestamp="+ timestamp;

				Log.v("url_GetArticleComment", "" + url_GetArticleComment);

				jsonGetPostComment = ReadJson
						.readJsonObjectFromUrl(url_GetArticleComment);

			} catch (IOException e1) {
				connectionError = true;
				jsonGetPostComment = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonGetPostComment = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonGetPostComment = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (jsonGetPostComment != null) {
					try {

						// {"status":"ok","message":"Get data Success.","dataAll":
						// [{"comments_id":34,"content":"test1","comment_user_id":60,"article_id":418,"agree_count":0,"disagree_count":0,"reply_count":0,"comment_datetime":"2015-04-24 10:10:17","chart_url":"","username":"Sirawich Thangern","replys":[]}]}

						contentGetPostComment = jsonGetPostComment
								.getJSONObject("dataAll");
						contentGetPostCommentList = contentGetPostComment
								.getJSONArray("comment");

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

		if (contentGetPostCommentList.length() > 0) {
			for (int i = 0; i < contentGetPostCommentList.length(); i++) {
				View viewCm = getLayoutInflater().inflate(
						R.layout.row_comments_post, null);

				final LinearLayout li_row_comments = (LinearLayout) viewCm
						.findViewById(R.id.li_row_comments);
				TextView tv_username = (TextView) viewCm
						.findViewById(R.id.tv_username);
				TextView tv_comment_datetime = (TextView) viewCm
						.findViewById(R.id.tv_comment_datetime);
				TextView tv_content = (TextView) viewCm
						.findViewById(R.id.tv_content);
				TextView tv_reply = (TextView) viewCm
						.findViewById(R.id.tv_reply);

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

				LinearLayout li_reply_comments = (LinearLayout) viewCm
						.findViewById(R.id.li_reply_comments);

				try {

					tv_username.setText(""
							+ contentGetPostCommentList.getJSONObject(i).get(
									"username"));
					tv_content.setText(""
							+ contentGetPostCommentList.getJSONObject(i).get(
									"content"));
					tv_agree_count.setText(""
							+ contentGetPostCommentList.getJSONObject(i).get(
									"agree_count"));
					tv_disagree_count.setText(""
							+ contentGetPostCommentList.getJSONObject(i).get(
									"disagree_count"));

					// agree, uagree comment
					img_agree.setBackgroundResource(R.drawable.icon_agree);
					img_disagree.setBackgroundResource(R.drawable.icon_uagree);
					tv_agree_count.setTextColor(getResources().getColor(
							R.color.c_content));
					tv_disagree_count.setTextColor(getResources().getColor(
							R.color.c_content));

					// if ((contentGetPostComment.getJSONObject(i)
					// .get("commentstatus")).equals("agree")) {
					if ((contentGetPostCommentList.getJSONObject(i).get(
							"commentstatus").toString()).equals("agree")) {
						img_agree
								.setBackgroundResource(R.drawable.icon_checkagree);
						tv_agree_count.setTextColor(getResources().getColor(
								R.color.c_success));
					} else if ((contentGetPostCommentList.getJSONObject(i).get(
							"commentstatus").toString()).equals("disagree")) {
						img_disagree
								.setBackgroundResource(R.drawable.icon_checkuagree);
						tv_disagree_count.setTextColor(getResources().getColor(
								R.color.c_danger));
					}

					// date ago
					try {
						String strDateAgo = ""
								+ DateTimeAgo
										.CalAgoTime2(contentGetPostCommentList
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
									comments_id = ""
											+ contentGetPostCommentList
													.getJSONObject(j).get(
															"comments_id");

									// ComsendPostLike(); // like
									CommentLikePost
											.sendPostLike(
													comments_id,
													SplashScreen.userModel.user_id,
													ckLikePost);
									initGetPostComment(); // get Comment

								} catch (JSONException e) {
									e.printStackTrace();
								}
							} else {
								Toast.makeText(getApplicationContext(),
										"Login", 0).show();
								LoginDialog.show();
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
									comments_id = ""
											+ contentGetPostCommentList
													.getJSONObject(j).get(
															"comments_id");

									// sendPostLike(); // un like
									CommentLikePost
											.sendPostLike(
													comments_id,
													SplashScreen.userModel.user_id,
													ckLikePost);
									initGetPostComment(); // get Comment

								} catch (JSONException e) {
									e.printStackTrace();
								}
							} else {
								Toast.makeText(getApplicationContext(),
										"Login", 0).show();
								
								LoginDialog.show();
								// LoginDialog.show();
							}
						}
					});

					// check show x
					if ((contentGetPostCommentList.getJSONObject(i)
							.get("username"))
							.equals(SplashScreen.userModel.userName)) {
						tv_comment_del.setVisibility(View.VISIBLE);
					}

					// delete post
					final String str_comment = contentGetPostCommentList
							.getJSONObject(i).get("content").toString();

					final AlertDialog.Builder adb = new AlertDialog.Builder(
							this);
					tv_comment_del.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if (SplashScreen.userModel.user_id == "") {
								Toast.makeText(getApplicationContext(),
										"Login", 0).show();
								// LoginDialog.show();
							} else {
								adb.setTitle("BIDs");
								adb.setMessage("Delete comment '" + str_comment
										+ "'");
								adb.setNegativeButton("Cancle", null);
								adb.setPositiveButton("Sure",
										new AlertDialog.OnClickListener() {
											public void onClick(
													DialogInterface dialog,
													int arg1) {
												try {
													comments_id = ""
															+ contentGetPostCommentList
																	.getJSONObject(
																			j)
																	.get("comments_id");

													// remove comment
													CommentLikePost
															.sendPostRemoveComment(comments_id);

													initGetPostComment(); // get
																			// Comment;

												} catch (JSONException e) {
													e.printStackTrace();
												}
											}
										});
								adb.show();
							}
						}
					});

					// ************* Reply *************
					final JSONArray contentGetPostReply = (JSONArray) contentGetPostCommentList
							.getJSONObject(i).get("reply");
					if (contentGetPostReply.length() > 0) {
						for (int k = 0; k < contentGetPostReply.length(); k++) {
							View viewReply = getLayoutInflater().inflate(
									R.layout.row_commenstreply_post, null);
							TextView tv_username_reply = (TextView) viewReply
									.findViewById(R.id.tv_username_reply);
							TextView tv_content_reply = (TextView) viewReply
									.findViewById(R.id.tv_content_reply);
							TextView tv_agree_count_reply = (TextView) viewReply
									.findViewById(R.id.tv_agree_count_reply);
							TextView tv_disagree_count_reply = (TextView) viewReply
									.findViewById(R.id.tv_disagree_count_reply);
							ImageView img_agree_reply = (ImageView) viewReply
									.findViewById(R.id.img_agree_reply);
							ImageView img_disagree_reply = (ImageView) viewReply
									.findViewById(R.id.img_disagree_reply);

							TextView tv_comment_del_reply = (TextView) viewReply
									.findViewById(R.id.tv_comment_del_reply);

							// {"content":"ads","comment_id":86,"username":"Crop",
							// "reply_datetime":"2015-04-17 15:46:48","reply_user_id":14,
							// "reply_id":44,"chart_url":"","disagree_count":0,"post_id":99,"agree_count":0}

							tv_username_reply.setText(""
									+ contentGetPostReply.getJSONObject(k).get(
											"username"));
							tv_content_reply.setText(""
									+ contentGetPostReply.getJSONObject(k).get(
											"content"));
							tv_agree_count_reply.setText(""
									+ contentGetPostReply.getJSONObject(k).get(
											"agree_count"));
							tv_disagree_count_reply.setText(""
									+ contentGetPostReply.getJSONObject(k).get(
											"disagree_count"));

							if ((contentGetPostReply.getJSONObject(k).get(
									"replystatus").toString()).equals("agree")) {
								img_agree_reply
										.setBackgroundResource(R.drawable.icon_checkagree);
								tv_agree_count_reply
										.setTextColor(getResources().getColor(
												R.color.c_success));
							} else if ((contentGetPostReply.getJSONObject(k)
									.get("replystatus").toString())
									.equals("disagree")) {
								img_disagree_reply
										.setBackgroundResource(R.drawable.icon_checkuagree);
								tv_disagree_count_reply
										.setTextColor(getResources().getColor(
												R.color.c_danger));
							}

							// like reply
							final int m = k;
							img_agree_reply
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											if (SplashScreen.userModel.user_id != "") {
												ckLikeReply = true;
												try {
													reply_id = ""
															+ contentGetPostReply
																	.getJSONObject(
																			m)
																	.get("reply_id");

													// sendReplyLike(); // like
													CommentLikePost
															.sendReplyLike(
																	reply_id,
																	SplashScreen.userModel.user_id,
																	ckLikeReply);
													initGetPostComment(); // get
																			// Post
																			// Comment

												} catch (JSONException e) {
													e.printStackTrace();
												}
											} else {
												Toast.makeText(
														getApplicationContext(),
														"Login", 0).show();
												LoginDialog.show();
											}
										}
									});

							// dislike reply
							img_disagree_reply
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											if (SplashScreen.userModel.user_id != "") {
												ckLikeReply = false;
												try {
													reply_id = ""
															+ contentGetPostReply
																	.getJSONObject(
																			m)
																	.get("reply_id");

													// sendReplyLike(); // un
													// like
													CommentLikePost
															.sendReplyLike(
																	reply_id,
																	SplashScreen.userModel.user_id,
																	ckLikeReply);
													initGetPostComment(); // get
																			// Post
																			// Comment

												} catch (JSONException e) {
													e.printStackTrace();
												}
											} else {
												Toast.makeText(
														getApplicationContext(),
														"Login", 0).show();
												LoginDialog.show();
											}
										}
									});

							// check show x
							if ((contentGetPostReply.getJSONObject(k)
									.get("username"))
									.equals(SplashScreen.userModel.userName)) {
								tv_comment_del_reply
										.setVisibility(View.VISIBLE);
							}

							// delete reply
							final String str_reply = contentGetPostReply
									.getJSONObject(k).get("content").toString();

							final AlertDialog.Builder adb_reply = new AlertDialog.Builder(
									this);
							tv_comment_del_reply
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											if ( SplashScreen.userModel.user_id == "" ) {
												Toast.makeText(
														getApplicationContext(),
														"Login", 0).show();
												
												LoginDialog.show();
												// LoginDialog.show();
											} else {
												adb_reply.setTitle("BIDs");
												adb_reply
														.setMessage("Delete reply '"
																+ str_reply
																+ "'");
												adb_reply.setNegativeButton(
														"Cancle", null);
												adb_reply
														.setPositiveButton(
																"Sure",
																new AlertDialog.OnClickListener() {
																	public void onClick(
																			DialogInterface dialog,
																			int arg1) {
																		try {
																			String reply_id = ""
																					+ contentGetPostReply
																							.getJSONObject(
																									m)
																							.get("reply_id");

																			// remove reply
																			CommentLikePost
																					.sendPostRemoveComment(reply_id);

																			initGetPostComment(); // get
																									// Comment;

																		} catch (JSONException e) {
																			e.printStackTrace();
																		}
																	}
																});
												adb_reply.show();
											}
										}
									});

							li_reply_comments.addView(viewReply);
						}
					}

					tv_reply.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							if ( SplashScreen.userModel.user_id == "" ) {
								Toast.makeText(getApplicationContext(),
										"Login", 0).show();
								LoginDialog.show();
								// LoginDialog.show();
							} else {
								try {
									replyUserComment = ""
											+ contentGetPostCommentList
													.getJSONObject(j).get(
															"username");
									replyComment = ""
											+ contentGetPostCommentList
													.getJSONObject(j).get(
															"content");
									replyComment_id = ""
											+ contentGetPostCommentList
													.getJSONObject(j).get(
															"comments_id");
								} catch (JSONException e1) {
									e1.printStackTrace();
								}

								// get prompts.xml view
								LayoutInflater layoutInflater = LayoutInflater
										.from(UiProfileIdeasSelectComments.this);
								View dlView = layoutInflater.inflate(
										R.layout.dialog_reply, null);
								AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
										UiProfileIdeasSelectComments.this);
								alertDialogBuilder.setView(dlView);

								final TextView dl_tv_comments = (TextView) dlView
										.findViewById(R.id.dl_tv_comments);
								final EditText dl_et_input = (EditText) dlView
										.findViewById(R.id.dl_et_input);

								dl_tv_comments.setText("ตอบกลับ "
										+ replyUserComment + " '"
										+ replyComment + "'");
								// setup a dialog window
								alertDialogBuilder
										.setCancelable(false)
										.setPositiveButton(
												"Reply",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int id) {
														replyInput = dl_et_input
																.getText()
																.toString()
																.trim();

														if (replyInput
																.equals("")) {
															Toast.makeText(
																	getApplicationContext(),
																	"input data",
																	0).show();
														} else if (SplashScreen.userModel.user_id == "") {
															LoginDialog.show();
														} else {
															try {
																replyContent = replyInput;
																String post_id = ""
																		+ contentGetPostCommentList
																				.getJSONObject(
																						j)
																				.get("post_id");

																// sendPostReply();
																// //send Post
																// reply

																CommentReplyPost
																		.sendPostReply(
																				post_id,
																				SplashScreen.userModel.user_id,
																				replyContent,
																				replyComment_id);
																initGetPostComment(); // send
																						// Post
																						// reply

																dialog.dismiss();

															} catch (JSONException e) {
																e.printStackTrace();
															}
														}
													}
												})
										.setNegativeButton(
												"Cancel",
												new DialogInterface.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int id) {
														dialog.cancel();
													}
												});

								// create an alert dialog
								AlertDialog alert = alertDialogBuilder.create();
								alert.show();
							}
						}
					});

					// **************************************

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
