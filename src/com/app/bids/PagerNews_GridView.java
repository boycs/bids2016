package com.app.bids;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;
import com.app.bids.PagerHomeAll.loadData;
import com.app.bids.PagerSystemTrade.loadDataSlidingMarquee;
import com.google.gson.JsonObject;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.R.anim;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

public class PagerNews_GridView extends Fragment {

	static Context context;
	public static View rootView;
	Dialog dialogLoading;

	// list contains fragments to instantiate in the viewpager
	List<Fragment> fragmentMain = new Vector<Fragment>();
	private PagerAdapter mPagerAdapter;
	// view pager
	private ViewPager mPager;

	private OnPageListener pageListener;
	public int selectTitle = 0;

	// carousel
	// public final static int PAGES = 5;
	public final static int LOOPS = 1000;
	// public final static int FIRST_PAGE = PAGES * LOOPS / 2;
	public final static float BIG_SCALE = 1f; // old 1.0f;
	public final static float SMALL_SCALE = 1f; // old 0.8f;
	public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;
	public static View view;

	public static MyPagerAdapter adapterCorousel;
	public static ViewPager pagerCorousel;

	public static int PAGES = 0;
	public static int FIRST_PAGE = 0;

	// ================= data ==================
	// FragmentChangeActivity.url_bidschart+"/service/getNews?offset=1&limit=10
	public static JSONArray contentGetNews = null;
	// FragmentChangeActivity.url_bidschart+"/service/getArticleByCategory?offset=1&limit=3&category=2
	public static JSONArray contentGetArticleByCategoryHot = null;

	public static JSONObject contentGetNewsSelect = null;

	public static ArrayList<CatalogNews> list_getNews = new ArrayList<CatalogNews>();

	public interface OnPageListener {
		public void onPage1(String s);
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
		rootView = inflater.inflate(R.layout.pager_news, container, false);

		// TextView marque = (TextView)
		// rootView.findViewById(R.id.marque_scrolling_text);
		// marque.setSelected(true);
		// TextView marque1 = (TextView)
		// rootView.findViewById(R.id.sliding_text_marquee);
		// marque1.setSelected(true);

		dialogLoading = new Dialog(context);
		dialogLoading.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialogLoading.setContentView(R.layout.progress_bar);
		dialogLoading.setCancelable(false);
		dialogLoading.setCanceledOnTouchOutside(false);

		dialogLoading.show();

		if (FragmentChangeActivity.contentGetTxtSlidingMarquee != null) {
			initTxtSliding();
		} else {
			loadTxtSlidingMarquee(); // text sliding
		}

		initHeatMap(); // img heatmap
		loadData(); // load data

		// if (!(FragmentChangeActivity.pageNews)) {
		// FragmentChangeActivity.pageNews = true;
		// } else {
		// initSetData();
		// }

		return rootView;

	}

	// ***************** text sliding ******************
	private void initTxtSliding() {
		String strSliding = "";
		TextView marque = (TextView) rootView
				.findViewById(R.id.tv_sliding_marquee);

		if (FragmentChangeActivity.contentGetTxtSlidingMarquee != null) {
			for (int i = 0; i < FragmentChangeActivity.contentGetTxtSlidingMarquee
					.length(); i++) {
				try {
					String txtSymbol = FragmentChangeActivity.contentGetTxtSlidingMarquee
							.getJSONObject(i).getString("symbol_name");
					String txtLtrade = FragmentChangeActivity.contentGetTxtSlidingMarquee
							.getJSONObject(i).getString("last_trade");
					String txtChange = FragmentChangeActivity.contentGetTxtSlidingMarquee
							.getJSONObject(i).getString("change");
					String txtPchange = FragmentChangeActivity.contentGetTxtSlidingMarquee
							.getJSONObject(i).getString("percentChange");

					if (txtLtrade != "") {
						double db = Double.parseDouble(txtLtrade);
						DecimalFormat formatter = new DecimalFormat("#,###.00");
						txtLtrade = formatter.format(db);
					}

					if (txtChange != "") {
						double db = Double.parseDouble(txtChange);
						txtChange = String.format(" %,.2f", db);
					}

					if (txtPchange != "") {
						double db = Double.parseDouble(txtPchange);
						txtPchange = "(" + String.format(" %,.2f", db) + "%)";
					}

					// strSliding += "  " + txtSymbol + "  " + txtLtrade + "  "
					// + txtChange + "" + txtPchange;

					strSliding += "   <font color='#95dd33'>" + txtSymbol
							+ "</font>  " + txtLtrade + "  " + txtChange
							+ "<font color='#eb4848'>" + txtPchange
							+ "</font> ";

					// symbol_name: ".SET",
					// last_trade: "1297.11",
					// change: "5.34",
					// percentChange: "0.4133"
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		}

		marque.setText(Html.fromHtml(strSliding), TextView.BufferType.SPANNABLE);
		marque.setSelected(true);
	}

	// ***************** SlidingMarquee ******************
	private void loadTxtSlidingMarquee() {
		loadDataSlidingMarquee resp = new loadDataSlidingMarquee();
		resp.execute();
	}

	public class loadDataSlidingMarquee extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		private JSONObject jsonTxtSlidingMarquee;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();

			// FragmentChangeActivity.url_bidschart+"/service/v2/symbolByIndustrySector?industry=TECH&sector=TECH&page=1&limit=10
			// ** top = swing , volume , value , gainer , loser
			String url_TxtSlidingMarquee = SplashScreen.url_bidschart+"/service/v2/getMaiSet";

			try {
				jsonTxtSlidingMarquee = ReadJson
						.readJsonObjectFromUrl(url_TxtSlidingMarquee);
			} catch (IOException e1) {
				connectionError = true;
				jsonTxtSlidingMarquee = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonTxtSlidingMarquee = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonTxtSlidingMarquee = null;
				e.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (jsonTxtSlidingMarquee != null) {
					try {

						FragmentChangeActivity.contentGetTxtSlidingMarquee = jsonTxtSlidingMarquee
								.getJSONArray("dataAll");

						initTxtSliding();

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.v("jsonGetData", "jsonGetData null");
				}

			} else {
				Log.v("connectionError", "connectionError ture");
			}
		}
	}

	// ============== Load Data all =============
	private void loadData() {

		loadData resp = new loadData();
		resp.execute();
	}

	public class loadData extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;
		// ======= json ========
		private JSONObject jsonGetNews;
		private JSONObject jsonGetArticleByCategoryHot;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// ======= url ========
			String url_GetNews = SplashScreen.url_bidschart+"/service/getNews?offset=1&limit=21";
			String url_GetArticleByCategoryHot = SplashScreen.url_bidschart+"/service/getArticleByCategory?offset=1&limit=3&category=2";

			try {
				// ======= Ui Home ========
				jsonGetNews = ReadJson.readJsonObjectFromUrl(url_GetNews);
				jsonGetArticleByCategoryHot = ReadJson
						.readJsonObjectFromUrl(url_GetArticleByCategoryHot);
			} catch (IOException e1) {
				connectionError = true;
				jsonGetNews = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonGetNews = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonGetNews = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (jsonGetNews != null) {
					try {
						// carousel home
						contentGetArticleByCategoryHot = jsonGetArticleByCategoryHot
								.getJSONArray("dataAll");
						PAGES = contentGetArticleByCategoryHot.length();
						FIRST_PAGE = PAGES * 1000 / 2;

						// get news
						contentGetNews = jsonGetNews.getJSONArray("dataAll");

						// arr symbol init
						// if (contentGetNews != null) {
						// list_getNews.clear();
						// for (int i = 0; i < contentGetNews.length(); i++) {
						// JSONObject jso = contentGetNews
						// .getJSONObject(i);
						// // arr_getsymbol.add(jso
						// // .getString("symbol"));
						//
						// CatalogNews cg = new CatalogNews();
						// cg.id = jso.getString("id");
						// cg.content = jso.getString("content");
						// cg.views_count = jso.getString("views_count");
						// cg.likes_count = jso.getString("likes_count");
						// cg.comments_count = jso
						// .getString("comments_count");
						// cg.reply_total_count = jso
						// .getString("reply_total_count");
						// cg.article_type = jso.getString("article_type");
						// cg.symbol = jso.getString("symbol");
						// cg.article_title = jso
						// .getString("article_title");
						// cg.article_description = jso
						// .getString("article_description");
						// cg.article_keyword = jso
						// .getString("article_keyword");
						// cg.visiabled = jso.getString("visiabled");
						// cg.img_link = jso.getString("img_link");
						// cg.img_icon = jso.getString("img_icon");
						// cg.pin = jso.getString("pin");
						// cg.content_mode = jso.getString("content_mode");
						// cg.deleted_at = jso.getString("deleted_at");
						// cg.img_file_name = jso
						// .getString("img_file_name");
						// cg.img_file_size = jso
						// .getString("img_file_size");
						// cg.img_content_type = jso
						// .getString("img_content_type");
						// cg.img_updated_at = jso
						// .getString("img_updated_at");
						// cg.created_at = jso.getString("created_at");
						// cg.updated_at = jso.getString("updated_at");
						// cg.category = jso.getString("category");
						// cg.name = jso.getString("name");
						// cg.this_like = jso.getString("this_like");
						//
						// list_getNews.add(cg);
						//
						// }

						initSetData();

						// }
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.v("json newslist null", "newslist null");
				}
			} else {
			}
			dialogLoading.dismiss();
		}
	}

	// ====================== Init main ========================
	private void initSetData() {
		// initial Corousel
		// initCorousel();

		initPagerHot(); // pager Hot

		// list news
		// initGridNews();
		initListNews(); // list news

	}

	// ====================== Initial Corousel ========================
	private void initCorousel() {
		ViewPager pagerCorousel = (ViewPager) rootView
				.findViewById(R.id.vp_ArticleByCategoryHot);

		adapterCorousel = new MyPagerAdapter(context,
				this.getFragmentManager(), pagerCorousel.getId());
		pagerCorousel.setAdapter(adapterCorousel);
		pagerCorousel.setOnPageChangeListener(adapterCorousel);
		pagerCorousel.setCurrentItem(FIRST_PAGE);
		pagerCorousel.setOffscreenPageLimit(3);
		pagerCorousel.setPageMargin(190);
		// pagerCorousel.setPageMargin(-198);

	}

	// ====================== Initial grid news ========================
	private void initGridNews() {
		GridView gv_news = (GridView) rootView.findViewById(R.id.gv_news);
		GridviewAdapterNews mAdapter = new GridviewAdapterNews(
				this.getActivity(), list_getNews, false);
		gv_news.setAdapter(mAdapter);

		// select newws
		gv_news.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> arg0, View arg1,
					int position, long arg3) {
				try {
					PagerNews_GridView.contentGetNewsSelect = contentGetNews
							.getJSONObject(position);
					startActivity(new Intent(context, UiArticleSelect.class));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		});
	}

	// ====================== Initial List News ========================
	private void initListNews() {
		LinearLayout li_list = (LinearLayout) rootView
				.findViewById(R.id.li_list);
		li_list.removeAllViews();

		if (contentGetNews.length() > 3) {
			int a = 0, b = 0, c = 0, x = 0;
			for (int i = 0; i < ((contentGetNews.length()) / 3); i++) {
				if (i > 0) {
					x = c + 1;
				}
				a = x;
				b = x + 1;
				c = x + 2;

				View view2 = ((Activity) context).getLayoutInflater().inflate(
						R.layout.row_news_3coll, null);

				try {
					// ------ collumn 1 -----------
					final LinearLayout li_coll_1 = (LinearLayout) view2
							.findViewById(R.id.li_coll_1);
					TextView tv_symbol_coll_1 = (TextView) view2
							.findViewById(R.id.tv_symbol_coll_1);
					TextView tv_article_title_coll_1 = (TextView) view2
							.findViewById(R.id.tv_article_title_coll_1);
					TextView tv_created_at_coll_1 = (TextView) view2
							.findViewById(R.id.tv_created_at_coll_1);
					final LinearLayout li_content_coll_1 = (LinearLayout) view2
							.findViewById(R.id.li_content_coll_1);

					// contentGetNews.getJSONObject(i).get("symbol").toString();
					tv_symbol_coll_1.setText(contentGetNews.getJSONObject(a)
							.get("symbol").toString());
					tv_article_title_coll_1.setText(contentGetNews
							.getJSONObject(a).get("article_title").toString());

					// 1 เดือน 2015-10-01

					String strDateAgo_coll_1;
					// try {
					strDateAgo_coll_1 = ""
							+ DateTimeCreate
									.DateDmyThaiCreateShot(contentGetNews
											.getJSONObject(a).get("created_at")
											.toString());
					tv_created_at_coll_1.setText(strDateAgo_coll_1);
					// strDateAgo_coll_1 = ""
					// + DateTimeAgo.CalAgoTime2(contentGetNews
					// .getJSONObject(a).get("created_at").toString());
					// String strDateYmd_coll_1 = ""
					// + (contentGetNews.getJSONObject(a).get("created_at")
					// .toString()).split(" ")[0];
					// tv_created_at_coll_1.setText(strDateAgo_coll_1 + "  "
					// + strDateYmd_coll_1);
					// } catch (ParseException e) {
					// e.printStackTrace();
					// }

					if ((contentGetNews.getJSONObject(a).get("content")
							.toString())
							.equals("http://service.bidschart.com/undefined")) {
					} else {
						FragmentChangeActivity.imageLoader.loadImage(""
								+ contentGetNews.getJSONObject(a)
										.get("content").toString(),
								FragmentChangeActivity.options,
								new SimpleImageLoadingListener() {
									@Override
									public void onLoadingComplete(
											String imageUri, View view,
											Bitmap loadedImage) {
										BitmapDrawable background = new BitmapDrawable(
												loadedImage);
										li_content_coll_1
												.setBackgroundDrawable(background);
										super.onLoadingComplete(imageUri, view,
												loadedImage);
									}
								});
					}

					final int j1 = a;
					li_coll_1.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Toast.makeText(context, "" + j1, 0).show();
							// try {
							// PagerNews.contentGetNewsSelect = contentGetNews
							// .getJSONObject(j);
							// startActivity(new Intent(context,
							// UiArticleSelect.class));
							// } catch (JSONException e) {
							// e.printStackTrace();
							// }
						}
					});

					// ------ collumn 2 -----------
					final LinearLayout li_coll_2 = (LinearLayout) view2
							.findViewById(R.id.li_coll_2);
					TextView tv_symbol_coll_2 = (TextView) view2
							.findViewById(R.id.tv_symbol_coll_2);
					TextView tv_article_title_coll_2 = (TextView) view2
							.findViewById(R.id.tv_article_title_coll_2);
					TextView tv_created_at_coll_2 = (TextView) view2
							.findViewById(R.id.tv_created_at_coll_2);
					final LinearLayout li_content_coll_2 = (LinearLayout) view2
							.findViewById(R.id.li_content_coll_2);

					// contentGetNews.getJSONObject(i).get("symbol").toString();
					tv_symbol_coll_2.setText(contentGetNews.getJSONObject(b)
							.get("symbol").toString());
					tv_article_title_coll_2.setText(contentGetNews
							.getJSONObject(b).get("article_title").toString());

					// 1 เดือน 2015-10-01

					String strDateAgo_coll_2;
					// try {
					strDateAgo_coll_2 = ""
							+ DateTimeCreate
									.DateDmyThaiCreateShot(contentGetNews
											.getJSONObject(b).get("created_at")
											.toString());
					tv_created_at_coll_2.setText(strDateAgo_coll_2);

					// strDateAgo_coll_2 = ""
					// + DateTimeAgo.CalAgoTime2(contentGetNews
					// .getJSONObject(b).get("created_at").toString());
					// String strDateYmd_coll_2 = ""
					// + (contentGetNews.getJSONObject(b).get("created_at")
					// .toString()).split(" ")[0];
					// tv_created_at_coll_2.setText(strDateAgo_coll_2 + "  "
					// + strDateYmd_coll_2);
					// } catch (ParseException e) {
					// e.printStackTrace();
					// }

					if ((contentGetNews.getJSONObject(b).get("content")
							.toString())
							.equals("http://service.bidschart.com/undefined")) {
					} else {
						FragmentChangeActivity.imageLoader.loadImage(""
								+ contentGetNews.getJSONObject(b)
										.get("content").toString(),
								FragmentChangeActivity.options,
								new SimpleImageLoadingListener() {
									@Override
									public void onLoadingComplete(
											String imageUri, View view,
											Bitmap loadedImage) {
										BitmapDrawable background = new BitmapDrawable(
												loadedImage);
										li_content_coll_2
												.setBackgroundDrawable(background);
										super.onLoadingComplete(imageUri, view,
												loadedImage);
									}
								});
					}

					final int j2 = b;
					li_coll_2.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Toast.makeText(context, "" + j2, 0).show();
							// try {
							// PagerNews.contentGetNewsSelect = contentGetNews
							// .getJSONObject(j+1);
							// startActivity(new Intent(context,
							// UiArticleSelect.class));
							// } catch (JSONException e) {
							// e.printStackTrace();
							// }
						}
					});

					// ------ collumn 3 -----------
					final LinearLayout li_coll_3 = (LinearLayout) view2
							.findViewById(R.id.li_coll_3);
					TextView tv_symbol_coll_3 = (TextView) view2
							.findViewById(R.id.tv_symbol_coll_3);
					TextView tv_article_title_coll_3 = (TextView) view2
							.findViewById(R.id.tv_article_title_coll_3);
					TextView tv_created_at_coll_3 = (TextView) view2
							.findViewById(R.id.tv_created_at_coll_3);
					final LinearLayout li_content_coll_3 = (LinearLayout) view2
							.findViewById(R.id.li_content_coll_3);

					// contentGetNews.getJSONObject(i).get("symbol").toString();
					tv_symbol_coll_3.setText(contentGetNews.getJSONObject(c)
							.get("symbol").toString());
					tv_article_title_coll_3.setText(contentGetNews
							.getJSONObject(c).get("article_title").toString());

					// 1 เดือน 2015-10-01

					String strDateAgo_coll_3;
					// try {
					strDateAgo_coll_3 = ""
							+ DateTimeCreate
									.DateDmyThaiCreateShot(contentGetNews
											.getJSONObject(c).get("created_at")
											.toString());
					tv_created_at_coll_3.setText(strDateAgo_coll_3);

					// strDateAgo_coll_3 = ""
					// + DateTimeAgo.CalAgoTime2(contentGetNews
					// .getJSONObject(c).get("created_at").toString());
					// String strDateYmd_coll_3 = ""
					// + (contentGetNews.getJSONObject(c).get("created_at")
					// .toString()).split(" ")[0];
					// tv_created_at_coll_3.setText(strDateAgo_coll_3 + "  "
					// + strDateYmd_coll_3);
					// } catch (ParseException e) {
					// e.printStackTrace();
					// }

					if ((contentGetNews.getJSONObject(c).get("content")
							.toString())
							.equals("http://service.bidschart.com/undefined")) {
					} else {
						FragmentChangeActivity.imageLoader.loadImage(""
								+ contentGetNews.getJSONObject(c)
										.get("content").toString(),
								FragmentChangeActivity.options,
								new SimpleImageLoadingListener() {
									@Override
									public void onLoadingComplete(
											String imageUri, View view,
											Bitmap loadedImage) {
										BitmapDrawable background = new BitmapDrawable(
												loadedImage);
										li_content_coll_3
												.setBackgroundDrawable(background);
										super.onLoadingComplete(imageUri, view,
												loadedImage);
									}
								});
					}

					final int j3 = c;
					li_coll_3.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							Toast.makeText(context, "" + j3, 0).show();
							// try {
							// PagerNews.contentGetNewsSelect = contentGetNews
							// .getJSONObject(j+2);
							// startActivity(new Intent(context,
							// UiArticleSelect.class));
							// } catch (JSONException e) {
							// e.printStackTrace();
							// }
						}
					});

					li_list.addView(view2);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}

	}

	// convertView = inflator.inflate(R.layout.row_news_grid, null);
	// tv_symbol = (TextView) convertView
	// .findViewById(R.id.tv_symbol);
	// tv_article_title = (TextView) convertView
	// .findViewById(R.id.tv_article_title);
	// tv_created_at = (TextView) convertView
	// .findViewById(R.id.tv_created_at);
	// tv_views_count = (TextView) convertView
	// .findViewById(R.id.tv_views_count);
	// tv_likes_count = (TextView) convertView
	// .findViewById(R.id.tv_likes_count);
	// tv_comments_count = (TextView) convertView
	// .findViewById(R.id.tv_comments_count);
	// li_content = (LinearLayout) convertView
	// .findViewById(R.id.li_content);
	//
	// tv_symbol.setText(cgn.symbol.toString());
	// tv_article_title.setText(cgn.article_title.toString());
	// // tv_created_at.setText(cgn.created_at.toString());
	// tv_views_count.setText(cgn.views_count.toString());
	// tv_likes_count.setText(cgn.likes_count.toString());
	// tv_comments_count.setText(cgn.comments_count.toString());
	//
	// // 1 เดือน 2015-10-01
	// try {
	// String strDateAgo = ""
	// + DateTimeAgo.CalAgoTime2(cgn.created_at.toString());
	// String strDateYmd = "" + (cgn.created_at.toString()).split(" ")[0];
	// tv_created_at.setText(strDateAgo + "  " + strDateYmd);
	// } catch (ParseException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	// if ((cgn.content.toString())
	// .equals("http://service.bidschart.com/undefined")) {
	// } else {
	// FragmentChangeActivity.imageLoader.loadImage(""
	// + cgn.content.toString(),
	// FragmentChangeActivity.options,
	// new SimpleImageLoadingListener() {
	// @Override
	// public void onLoadingComplete(String imageUri,
	// View view, Bitmap loadedImage) {
	// BitmapDrawable background = new BitmapDrawable(
	// loadedImage);
	// li_content.setBackgroundDrawable(background);
	// super.onLoadingComplete(imageUri, view,
	// loadedImage);
	// }
	// });
	// }

	// ============= heat map ===========
	private void initHeatMap() {
		ImageView img_heatmap = (ImageView) rootView
				.findViewById(R.id.img_heatmap);
		FragmentChangeActivity.imageLoader.displayImage(
				SplashScreen.url_bidschart+"/images/heatmap-phone.png",
				img_heatmap);

		img_heatmap.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(context, UiHotHeatmap.class));
			}
		});

	}

	// ============= Pager ===========
	private void initPagerHot() {
		// creating fragments and adding to list
		fragmentMain.removeAll(fragmentMain);
		if (contentGetArticleByCategoryHot.length() > 0) {

			fragmentMain.add(Fragment.instantiate(context,
					PagerNewsCategoryHot_0.class.getName()));
			if (contentGetArticleByCategoryHot.length() == 2) {
				fragmentMain.add(Fragment.instantiate(context,
						PagerNewsCategoryHot_1.class.getName()));
			} else {
				fragmentMain.add(Fragment.instantiate(context,
						PagerNewsCategoryHot_1.class.getName()));
				fragmentMain.add(Fragment.instantiate(context,
						PagerNewsCategoryHot_2.class.getName()));
			}

			// creating adapter and linking to view pager
			this.mPagerAdapter = new PagerAdapter(
					super.getChildFragmentManager(), fragmentMain);
			mPager = (ViewPager) rootView
					.findViewById(R.id.vp_ArticleByCategoryHot);

			mPager.setAdapter(this.mPagerAdapter);

			// mPager.setOnClickListener(new OnClickListener() {
			// @Override
			// public void onClick(View v) {
			// // Toast.makeText(getApplicationContext(), "sssss",
			// // Toast.LENGTH_SHORT).show();
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
				}

				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
					mPager.getParent().requestDisallowInterceptTouchEvent(true);
				}

				@Override
				public void onPageScrollStateChanged(int arg0) {
				}
			});
		}
	}

	// the meat of switching the above fragment
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;
		if (getActivity() instanceof FragmentChangeActivity) {
			FragmentChangeActivity fca = (FragmentChangeActivity) getActivity();
			fca.switchContent(fragment);
		}
	}

}