package com.app.bids;

import java.io.IOException;
import java.text.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
import com.app.bids.R;

public class DialogWatchListCategories {

	private static Dialog dialog;
	private static Context context;

	// FragmentChangeActivity.url_bidschart+"/watchlist?platform=mobile&user=10
//	public static JSONArray contentGetWatchlists = null;

	public DialogWatchListCategories(Context context2) {
		this.context = context2;
		this.dialog = new Dialog(context2);

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
		dialog.setContentView(R.layout.dialog_watchlist_categories_horizontal);

	}

	public static void show() {
		dialog.show();
		
		initMenu();

		// set width height dialog
		DisplayMetrics displaymetrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(displaymetrics);

		int ScreenHeight = displaymetrics.heightPixels;
		int ScreenWidth = displaymetrics.widthPixels;

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.width = (int) (ScreenWidth * 1);
		lp.height = (int) (ScreenHeight * 1);
		dialog.getWindow().setAttributes(lp);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
	}

	public static void dissmiss() {
		dialog.dismiss();
	}

//	static DialogWatchListCategoriesIndustry dialogIndustry;

	public static void initMenu() {
//		dialogIndustry = new DialogWatchListCategoriesIndustry(context);

		initFavorite();
		initSystemTradePortfolio();
		initTopMost();
		initIndustry();
		initTrendSignal();
		initFundamental();
	}

	// ======== Industry ========

	public static void initIndustry() {
		((LinearLayout) dialog.findViewById(R.id.li_idustry))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						// switchFragment(new
						// PagerWatchListCategoriesIndustry());
						
//						DialogWatchListCategoriesIndustry dialogIndustry = new DialogWatchListCategoriesIndustry(context);
//						dialogIndustry.show();
						
						DialogWatchListCategoriesIndustry.show();
					}
				});
	}

	// ======== favorite ========
	public static void initFavorite() {
//		PagerWatchList.getDataFavoriteIdRemove();
		((TextView) dialog.findViewById(R.id.tv_close))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						dialog.dismiss();
					}
				});

		((LinearLayout) dialog.findViewById(R.id.li_favorite1))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.ckLoadWatchlist = true;
						
						FragmentChangeActivity.strWatchlistCategory = "favorite";
						FragmentChangeActivity.strFavoriteNumber = "1";
//						FragmentChangeActivity.strFavoriteTitle = "Favorite 1";
						dialog.dismiss();
						switchFragment(new PagerWatchList());
					}
				});
		((LinearLayout) dialog.findViewById(R.id.li_favorite2))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.ckLoadWatchlist = true;
						
						FragmentChangeActivity.strWatchlistCategory = "favorite";
						FragmentChangeActivity.strFavoriteNumber = "2";
//						FragmentChangeActivity.strFavoriteTitle = "Favorite 2";
						dialog.dismiss();
						switchFragment(new PagerWatchList());
					}
				});
		((LinearLayout) dialog.findViewById(R.id.li_favorite3))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.ckLoadWatchlist = true;
						
						FragmentChangeActivity.strWatchlistCategory = "favorite";
						FragmentChangeActivity.strFavoriteNumber = "3";
//						FragmentChangeActivity.strFavoriteTitle = "Favorite 3";
						dialog.dismiss();
						switchFragment(new PagerWatchList());
					}
				});
		((LinearLayout) dialog.findViewById(R.id.li_favorite4))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.ckLoadWatchlist = true;
						
						FragmentChangeActivity.strWatchlistCategory = "favorite";
						FragmentChangeActivity.strFavoriteNumber = "4";
//						FragmentChangeActivity.strFavoriteTitle = "Favorite 4";
						dialog.dismiss();
						switchFragment(new PagerWatchList());
					}
				});
		((LinearLayout) dialog.findViewById(R.id.li_favorite5))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.ckLoadWatchlist = true;
						
						FragmentChangeActivity.strWatchlistCategory = "favorite";
						FragmentChangeActivity.strFavoriteNumber = "5";
//						FragmentChangeActivity.strFavoriteTitle = "Favorite 5";
						dialog.dismiss();
						switchFragment(new PagerWatchList());
					}
				});
	}

	// ======== TopMost ========
	public static void initTopMost() {
		// ((TextView) dialog.findViewById(R.id.tv_swing))
		((LinearLayout) dialog.findViewById(R.id.li_swing))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.ckLoadWatchlist = true;
						
						FragmentChangeActivity.strWatchlistCategory = "topmost";
						FragmentChangeActivity.strWatchlistCategorySelect = "swing";
						dialog.dismiss();
						switchFragment(new PagerWatchList());
					}
				});
		// ((TextView) dialog.findViewById(R.id.tv_volume))
		((LinearLayout) dialog.findViewById(R.id.li_volume))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.ckLoadWatchlist = true;
						
						FragmentChangeActivity.strWatchlistCategory = "topmost";
						FragmentChangeActivity.strWatchlistCategorySelect = "volume";
						dialog.dismiss();
						switchFragment(new PagerWatchList());
					}
				});
		// ((TextView) dialog.findViewById(R.id.tv_value))
		((LinearLayout) dialog.findViewById(R.id.li_value))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.ckLoadWatchlist = true;
						
						FragmentChangeActivity.strWatchlistCategory = "topmost";
						FragmentChangeActivity.strWatchlistCategorySelect = "value";
						dialog.dismiss();
						switchFragment(new PagerWatchList());
					}
				});
		// ((TextView) dialog.findViewById(R.id.tv_gainer))
		((LinearLayout) dialog.findViewById(R.id.li_gainer))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.ckLoadWatchlist = true;
						
						FragmentChangeActivity.strWatchlistCategory = "topmost";
						FragmentChangeActivity.strWatchlistCategorySelect = "gainer";
						dialog.dismiss();
						switchFragment(new PagerWatchList());
					}
				});
		// ((TextView) dialog.findViewById(R.id.tv_loser))
		((LinearLayout) dialog.findViewById(R.id.li_loser))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.ckLoadWatchlist = true;
						
						FragmentChangeActivity.strWatchlistCategory = "topmost";
						FragmentChangeActivity.strWatchlistCategorySelect = "loser";
						dialog.dismiss();
						switchFragment(new PagerWatchList());
					}
				});
	}

	// ======== SystemTradePortfolio ========
	public static void initSystemTradePortfolio() {
		((LinearLayout) dialog.findViewById(R.id.li_systemtrade_portfolio))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.ckLoadWatchlist = true;
						
						FragmentChangeActivity.strWatchlistCategory = "portfolio";
						dialog.dismiss();
						switchFragment(new PagerWatchList());
						
//						if (SplashScreen.contentGetUserById != null) {
//							try {
//								if (!(SplashScreen.contentGetUserById
//										.getString("package").equals("proBeta"))) {
//									FragmentChangeActivity.ckLoadWatchlist = true;
//									
//									FragmentChangeActivity.strWatchlistCategory = "portfolio";
//									dialog.dismiss();
//									switchFragment(new PagerWatchList());
//								}
//							} catch (JSONException e) {
//								e.printStackTrace();
//							}
//						}
					}
				});

	}

	// ======== Fundamental ========
	public static String arrFundamental[] = { "all", "turnaround", "growth",
			"dividend", "good", "strength" };

	public static void initFundamental() {
		// ((TextView) dialog.findViewById(R.id.tv_activity))
		((LinearLayout) dialog.findViewById(R.id.li_activity))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.ckLoadWatchlist = true;
						
						FragmentChangeActivity.strWatchlistCategory = "fundamental";
						FragmentChangeActivity.strWatchlistCategorySelect = arrFundamental[0];
						dialog.dismiss();
						switchFragment(new PagerWatchList());
					}
				});

		// ((TextView) dialog.findViewById(R.id.tv_profitability))
		((LinearLayout) dialog.findViewById(R.id.li_profitability))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.ckLoadWatchlist = true;
						
						FragmentChangeActivity.strWatchlistCategory = "fundamental";
						FragmentChangeActivity.strWatchlistCategorySelect = arrFundamental[1];
						dialog.dismiss();
						switchFragment(new PagerWatchList());
					}
				});
		// ((TextView) dialog.findViewById(R.id.tv_leverage))
		((LinearLayout) dialog.findViewById(R.id.li_leverage))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.ckLoadWatchlist = true;
						
						FragmentChangeActivity.strWatchlistCategory = "fundamental";
						FragmentChangeActivity.strWatchlistCategorySelect = arrFundamental[2];
						dialog.dismiss();
						switchFragment(new PagerWatchList());
					}
				});
		// ((TextView) dialog.findViewById(R.id.tv_liquidity))
		((LinearLayout) dialog.findViewById(R.id.li_liquidity))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.ckLoadWatchlist = true;
						
						FragmentChangeActivity.strWatchlistCategory = "fundamental";
						FragmentChangeActivity.strWatchlistCategorySelect = arrFundamental[3];
						dialog.dismiss();
						switchFragment(new PagerWatchList());
					}
				});
		((LinearLayout) dialog.findViewById(R.id.li_good))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.ckLoadWatchlist = true;
						
						FragmentChangeActivity.strWatchlistCategory = "fundamental";
						FragmentChangeActivity.strWatchlistCategorySelect = arrFundamental[4];
						dialog.dismiss();
						switchFragment(new PagerWatchList());
					}
				});
		((LinearLayout) dialog.findViewById(R.id.li_strength))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.ckLoadWatchlist = true;
						
						FragmentChangeActivity.strWatchlistCategory = "fundamental";
						FragmentChangeActivity.strWatchlistCategorySelect = arrFundamental[5];
						dialog.dismiss();
						switchFragment(new PagerWatchList());
					}
				});
	}

	// ======== TrendSignal ========
	public static String arrTrendSignal[] = { "all", "up", "down", "sideway" };

	public static void initTrendSignal() {
		((LinearLayout) dialog.findViewById(R.id.li_trendsignal_all))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.ckLoadWatchlist = true;
						
						FragmentChangeActivity.strWatchlistCategory = "trendsignal";
						FragmentChangeActivity.strWatchlistCategorySelect = arrTrendSignal[0];
						dialog.dismiss();
						switchFragment(new PagerWatchList());
					}
				});

		((LinearLayout) dialog.findViewById(R.id.li_trendsignal_up))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.ckLoadWatchlist = true;
						
						FragmentChangeActivity.strWatchlistCategory = "trendsignal";
						FragmentChangeActivity.strWatchlistCategorySelect = arrTrendSignal[1];
						dialog.dismiss();
						switchFragment(new PagerWatchList());
					}
				});
		((LinearLayout) dialog.findViewById(R.id.li_trendsignal_down))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.ckLoadWatchlist = true;
						
						FragmentChangeActivity.strWatchlistCategory = "trendsignal";
						FragmentChangeActivity.strWatchlistCategorySelect = arrTrendSignal[2];
						dialog.dismiss();
						switchFragment(new PagerWatchList());
					}
				});
		((LinearLayout) dialog.findViewById(R.id.li_trendsignal_sideway))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.ckLoadWatchlist = true;
						
						FragmentChangeActivity.strWatchlistCategory = "trendsignal";
						FragmentChangeActivity.strWatchlistCategorySelect = arrTrendSignal[3];
						dialog.dismiss();
						switchFragment(new PagerWatchList());
					}
				});
	}

	protected static void switchFragment(PagerWatchList fragment) {
		if (context == null)
			return;
		if (context instanceof FragmentChangeActivity) {
			FragmentChangeActivity fca = (FragmentChangeActivity) context;
			fca.switchContent(fragment);
		}
	}

	// the meat of switching the above fragment
	// public static void switchFragment(Fragment fragment) {
	// if (context == null)
	// return;
	// if (context instanceof FragmentChangeActivity) {
	// FragmentChangeActivity fca = (FragmentChangeActivity) getActivity();
	// fca.switchContent(fragment);
	// }
	// }

}