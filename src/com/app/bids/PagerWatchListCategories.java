package com.app.bids;

import java.io.IOException;
import java.text.ParseException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class PagerWatchListCategories extends Fragment {

	static Context context;
	public static View rootView;

	// activity listener interface
	private OnPageListener pageListener;

	// FragmentChangeActivity.url_bidschart+"/watchlist?platform=mobile&user=10
	public static JSONArray contentGetWatchlists = null;

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

		// inflate view from layout
		rootView = (LinearLayout) inflater.inflate(
				R.layout.pager_watchlist_categories_horizontal, container,
				false);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		((TextView) rootView.findViewById(R.id.tv_close))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						switchFragment(new PagerWatchList());
					}
				});

		initMenu();
	}

	private void initMenu() {
		initFavorite();
		initSystemTradePortfolio();
		initTopMost();
		initIndustry();
		initTrendSignal();
		initFundamental();
	}

	// ======== favorite ========
	private void initFavorite() {

		((LinearLayout) rootView.findViewById(R.id.li_favorite1))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.strWatchlistCategory = "favorite";
						FragmentChangeActivity.strFavoriteNumber = "1";
						// FragmentChangeActivity.strFavoriteTitle =
						// "Favorite 1";
						switchFragment(new PagerWatchList());
					}
				});
		((LinearLayout) rootView.findViewById(R.id.li_favorite2))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.strWatchlistCategory = "favorite";
						FragmentChangeActivity.strFavoriteNumber = "2";
						// FragmentChangeActivity.strFavoriteTitle =
						// "Favorite 2";
						switchFragment(new PagerWatchList());
					}
				});
		((LinearLayout) rootView.findViewById(R.id.li_favorite3))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.strWatchlistCategory = "favorite";
						FragmentChangeActivity.strFavoriteNumber = "3";
						// FragmentChangeActivity.strFavoriteTitle =
						// "Favorite 3";
						switchFragment(new PagerWatchList());
					}
				});
		((LinearLayout) rootView.findViewById(R.id.li_favorite4))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.strWatchlistCategory = "favorite";
						FragmentChangeActivity.strFavoriteNumber = "4";
						// FragmentChangeActivity.strFavoriteTitle =
						// "Favorite 4";
						switchFragment(new PagerWatchList());
					}
				});
		((LinearLayout) rootView.findViewById(R.id.li_favorite5))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.strWatchlistCategory = "favorite";
						FragmentChangeActivity.strFavoriteNumber = "5";
						// FragmentChangeActivity.strFavoriteTitle =
						// "Favorite 5";
						switchFragment(new PagerWatchList());
					}
				});
	}

	// ======== TopMost ========
	private void initTopMost() {
		// ((TextView) rootView.findViewById(R.id.tv_swing))
		((LinearLayout) rootView.findViewById(R.id.li_swing))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.strWatchlistCategory = "topmost";
						FragmentChangeActivity.strWatchlistCategorySelect = "swing";
						switchFragment(new PagerWatchList());
					}
				});
		// ((TextView) rootView.findViewById(R.id.tv_volume))
		((LinearLayout) rootView.findViewById(R.id.li_volume))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.strWatchlistCategory = "topmost";
						FragmentChangeActivity.strWatchlistCategorySelect = "volume";
						switchFragment(new PagerWatchList());
					}
				});
		// ((TextView) rootView.findViewById(R.id.tv_value))
		((LinearLayout) rootView.findViewById(R.id.li_value))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.strWatchlistCategory = "topmost";
						FragmentChangeActivity.strWatchlistCategorySelect = "value";
						switchFragment(new PagerWatchList());
					}
				});
		// ((TextView) rootView.findViewById(R.id.tv_gainer))
		((LinearLayout) rootView.findViewById(R.id.li_gainer))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.strWatchlistCategory = "topmost";
						FragmentChangeActivity.strWatchlistCategorySelect = "gainer";
						switchFragment(new PagerWatchList());
					}
				});
		// ((TextView) rootView.findViewById(R.id.tv_loser))
		((LinearLayout) rootView.findViewById(R.id.li_loser))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.strWatchlistCategory = "topmost";
						FragmentChangeActivity.strWatchlistCategorySelect = "loser";
						switchFragment(new PagerWatchList());
					}
				});
	}

	// ======== SystemTradePortfolio ========
	private void initSystemTradePortfolio() {
		((LinearLayout) rootView.findViewById(R.id.li_systemtrade_portfolio))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.strWatchlistCategory = "portfolio";
						switchFragment(new PagerWatchList());
						// if (SplashScreen.contentGetUserById != null) {
						// try {
						// if (!(SplashScreen.contentGetUserById
						// .getString("package").equals("proBeta"))) {
						//
						// FragmentChangeActivity.strWatchlistCategory =
						// "portfolio";
						// switchFragment(new PagerWatchList());
						// }
						// } catch (JSONException e) {
						// e.printStackTrace();
						// }
						// }
					}
				});

	}

	// ======== Industry ========
	private void initIndustry() {
		((LinearLayout) rootView.findViewById(R.id.li_idustry))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						switchFragment(new PagerWatchListCategoriesIndustry());
					}
				});
	}

	// ======== Fundamental ========
	public static String arrFundamental[] = { "all", "turnaround", "growth",
			"dividend", "good" };

	private void initFundamental() {
		// ((TextView) rootView.findViewById(R.id.tv_activity))
		((LinearLayout) rootView.findViewById(R.id.li_activity))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.strWatchlistCategory = "fundamental";
						FragmentChangeActivity.strWatchlistCategorySelect = arrFundamental[0];
						switchFragment(new PagerWatchList());
					}
				});

		// ((TextView) rootView.findViewById(R.id.tv_profitability))
		((LinearLayout) rootView.findViewById(R.id.li_profitability))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.strWatchlistCategory = "fundamental";
						FragmentChangeActivity.strWatchlistCategorySelect = arrFundamental[1];
						switchFragment(new PagerWatchList());
					}
				});
		// ((TextView) rootView.findViewById(R.id.tv_leverage))
		((LinearLayout) rootView.findViewById(R.id.li_leverage))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.strWatchlistCategory = "fundamental";
						FragmentChangeActivity.strWatchlistCategorySelect = arrFundamental[2];
						switchFragment(new PagerWatchList());
					}
				});
		// ((TextView) rootView.findViewById(R.id.tv_liquidity))
		((LinearLayout) rootView.findViewById(R.id.li_liquidity))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.strWatchlistCategory = "fundamental";
						FragmentChangeActivity.strWatchlistCategorySelect = arrFundamental[3];
						switchFragment(new PagerWatchList());
					}
				});
		((LinearLayout) rootView.findViewById(R.id.li_good))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.strWatchlistCategory = "fundamental";
						FragmentChangeActivity.strWatchlistCategorySelect = arrFundamental[4];
						switchFragment(new PagerWatchList());
					}
				});
	}

	// ======== TrendSignal ========
	public static String arrTrendSignal[] = { "all", "up", "down", "sizeway" };

	private void initTrendSignal() {
		((LinearLayout) rootView.findViewById(R.id.li_trendsignal_all))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.strWatchlistCategory = "trendsignal";
						FragmentChangeActivity.strWatchlistCategorySelect = arrTrendSignal[0];
						switchFragment(new PagerWatchList());
					}
				});

		((LinearLayout) rootView.findViewById(R.id.li_trendsignal_up))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.strWatchlistCategory = "trendsignal";
						FragmentChangeActivity.strWatchlistCategorySelect = arrTrendSignal[1];
						switchFragment(new PagerWatchList());
					}
				});
		((LinearLayout) rootView.findViewById(R.id.li_trendsignal_down))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.strWatchlistCategory = "trendsignal";
						FragmentChangeActivity.strWatchlistCategorySelect = arrTrendSignal[2];
						switchFragment(new PagerWatchList());
					}
				});
		((LinearLayout) rootView.findViewById(R.id.li_trendsignal_sideway))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.strWatchlistCategory = "trendsignal";
						FragmentChangeActivity.strWatchlistCategorySelect = arrTrendSignal[3];
						switchFragment(new PagerWatchList());
					}
				});
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