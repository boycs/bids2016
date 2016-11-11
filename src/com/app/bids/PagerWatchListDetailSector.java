package com.app.bids;

import java.io.IOException;
import java.text.ParseException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;
import com.app.bids.PagerWatchListDetailNews.loadAll;

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
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;

public class PagerWatchListDetailSector extends Fragment {

	static Context context;
	public static View rootView;

	// activity listener interface
	private OnPageListener pageListener;

	public static JSONArray contentGetWatchlistNewsBySymbol = null;

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
				R.layout.pager_watchlist_detail_sector, container, false);

		return rootView;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		 // initial Data
		 initLoadData();
	}

	// ============== Load initLoadData =========================
	private void initLoadData() {
		loadAll resp = new loadAll();
		resp.execute();
	}

	public class loadAll extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;
		// ======= Ui Newds ========
		private JSONObject jsonGetWatchlistNewsBySymbol;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			// progress.show();

		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// ======= Ui News ========
			String url_GetWatchlistNewsBySymbol = SplashScreen.url_bidschart
					+ "/service/v2/symbolBySectorSymbol?symbol="
					+ FragmentChangeActivity.strSymbolSelect
					+ "&page=1&limit10";

			try {
				// ======= Ui News ========
				jsonGetWatchlistNewsBySymbol = ReadJson
						.readJsonObjectFromUrl(url_GetWatchlistNewsBySymbol);
			} catch (IOException e1) {
				connectionError = true;
				jsonGetWatchlistNewsBySymbol = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonGetWatchlistNewsBySymbol = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonGetWatchlistNewsBySymbol = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (jsonGetWatchlistNewsBySymbol != null) {
					try {

						contentGetWatchlistNewsBySymbol = jsonGetWatchlistNewsBySymbol
								.getJSONArray("dataAll");
						// Toast.makeText(context,
						// "content : "+contentGetWatchlistNewsBySymbol.length(),
						// 0).show();
						initSetData();

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					Log.v("json null", "jsonGetArticleTitleByType null");
				}

			} else {
				Log.v("json newslist null", "jsonGetArticleTitleByType null");
			}
		}
	}

	private void initSetData() {
		try {
			LinearLayout list_symbol = (LinearLayout) rootView
					.findViewById(R.id.list_symbol);
			LinearLayout list_detail = (LinearLayout) rootView
					.findViewById(R.id.list_detail);
			list_symbol.removeAllViews();
			list_detail.removeAllViews();

			for (int i = 0; i < contentGetWatchlistNewsBySymbol.length(); i++) {
				View viewSymbol = ((Activity) context).getLayoutInflater()
						.inflate(R.layout.row_watchlist_symbol, null);
				View viewDetail = ((Activity) context).getLayoutInflater()
						.inflate(R.layout.row_sector_detail, null);

				JSONObject jsoIndex = contentGetWatchlistNewsBySymbol
						.optJSONObject(i);

				// symbol
				((TextView) viewSymbol.findViewById(R.id.tv_symbol_name))
						.setText(jsoIndex.getString("symbol_name").toString());
				((TextView) viewSymbol
						.findViewById(R.id.tv_symbol_fullname_eng))
						.setText(jsoIndex.getString("symbol_fullname_eng")
								.toString());

				final String symbol_name = jsoIndex.getString("symbol_name")
						.toString();
				((LinearLayout) viewSymbol.findViewById(R.id.row_symbol))
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								FragmentChangeActivity.strSymbolSelect = symbol_name;
								switchFragment(new PagerWatchlistDetail());

								// PagerWatchListDetail pwd = new
								// PagerWatchListDetail();
								// pwd.loadDataDetail();
							}
						});

				// detail
				((LinearLayout) viewDetail.findViewById(R.id.row_detail))
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								FragmentChangeActivity.strSymbolSelect = symbol_name;
								switchFragment(new PagerWatchlistDetail());
							}
						});
				// ck ltrade change
				String strLastTrade = jsoIndex.getString("last_trade")
						.toString().toString().replaceAll(",", "");
				String strChange = jsoIndex.getString("change").toString()
						.toString().replaceAll(",", "");
				String strPercentChange = jsoIndex.getString("percentChange")
						.toString().toString().replaceAll(",", "");

				ImageView img_updown = (ImageView) viewDetail
						.findViewById(R.id.img_updown);
				TextView tv_last_trade = (TextView) viewDetail
						.findViewById(R.id.tv_last_trade);
				TextView tv_change = (TextView) viewDetail
						.findViewById(R.id.tv_change);
				TextView tv_percentChange = (TextView) viewDetail
						.findViewById(R.id.tv_percentChange);

				tv_last_trade.setText(FunctionSetBg
						.setStrDetailList(strLastTrade));
				tv_change.setText(FunctionSetBg
						.setStrDetailList(strChange));
				if ((strPercentChange == "0") || strPercentChange == "") {
					tv_percentChange.setText("0.00");
				} else {
					tv_percentChange.setText(FunctionSetBg
							.setStrDetailList(strPercentChange) + "%");
				}

				if (strLastTrade != "") {
					img_updown.setBackgroundResource(FunctionSetBg
							.setImgUpDown(strLastTrade));
					tv_last_trade.setTextColor(getResources().getColor(
							FunctionSetBg.setColor(strLastTrade)));
				}
				if (strChange != "") {
					tv_change.setTextColor(getResources().getColor(
							FunctionSetBg.setColor(strChange)));
					tv_percentChange.setTextColor(getResources().getColor(
							FunctionSetBg.setColor(strChange)));
				}

				// ck hight low
				String strPrevClose = jsoIndex.getString("prev_close")
						.toString().replaceAll(",", "");
				String strHigh = jsoIndex.getString("high").toString()
						.replaceAll(",", "");
				String strLow = jsoIndex.getString("low").toString()
						.replaceAll(",", "");

				TextView tv_high = (TextView) viewDetail
						.findViewById(R.id.tv_high);
				TextView tv_low = (TextView) viewDetail
						.findViewById(R.id.tv_low);

				tv_high.setText(FunctionSetBg.setStrDetailList(strHigh));
				tv_low.setText(FunctionSetBg.setStrDetailList(strLow));

				if (strPrevClose != "") {
					if (strHigh != "") {
						if ((Float.parseFloat(strHigh)) > Float
								.parseFloat(strPrevClose)) {
							tv_high.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[2]));
						} else if ((Float.parseFloat(strHigh)) < Float
								.parseFloat(strPrevClose)) {
							tv_high.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[0]));
						} else {
							tv_high.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[1]));
						}
					}
					if (strLow != "") {
						if ((Float.parseFloat(strLow)) > Float
								.parseFloat(strPrevClose)) {
							tv_low.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[2]));
						} else if ((Float.parseFloat(strLow)) < Float
								.parseFloat(strPrevClose)) {
							tv_low.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[0]));
						} else {
							tv_low.setTextColor(getResources().getColor(
									FunctionSetBg.arrColor[1]));
						}
					}
				}

				// ck pe pbv peg
				String strPe = jsoIndex.getString("p_e").toString()
						.replaceAll(",", "");
				String strPbv = jsoIndex.getString("p_bv").toString()
						.replaceAll(",", "");
				String strRoe = jsoIndex.getString("roe").toString()
						.replaceAll(",", "");
				String strRoa = jsoIndex.getString("roa").toString()
						.replaceAll(",", "");
				String strPeg = jsoIndex.getString("peg").toString()
						.replaceAll(",", "");

				TextView tv_p_e = (TextView) viewDetail
						.findViewById(R.id.tv_p_e);
				TextView tv_p_bv = (TextView) viewDetail
						.findViewById(R.id.tv_p_bv);
				TextView tv_roe = (TextView) viewDetail
						.findViewById(R.id.tv_roe);
				TextView tv_roa = (TextView) viewDetail
						.findViewById(R.id.tv_roa);
				TextView tv_peg = (TextView) viewDetail
						.findViewById(R.id.tv_peg);

				tv_p_e.setText(FunctionSetBg.setStrDetailList(strPe));
				tv_p_bv.setText(FunctionSetBg.setStrDetailList(strPbv));
				tv_roe.setText(FunctionSetBg.setStrDetailList(strRoe));
				tv_roa.setText(FunctionSetBg.setStrDetailList(strRoa));
				tv_peg.setText(FunctionSetBg.setStrDetailList(strPeg));

				if (SplashScreen.contentSymbol_Set != null) {
					String strPe_set = SplashScreen.contentSymbol_Set
							.getString("p_e").toString().replaceAll(",", "");
					String strPbv_set = SplashScreen.contentSymbol_Set
							.getString("p_bv").toString().replaceAll(",", "");
					String strPeg_set = SplashScreen.contentSymbol_Set
							.getString("peg").toString().replaceAll(",", "");

					if (!(strPe.equals("")) && (strPe != null)
							&& !(strPe_set.equals(""))
							&& (strPe_set.equals(null))) {
						tv_p_e.setTextColor(getResources().getColor(
								FunctionSetBg.setStrCheckSet(strPe,
										strPe_set)));
					}
					if (!(strPbv.equals("")) && (strPbv != null)
							&& !(strPbv_set.equals(""))
							&& (strPbv_set.equals(null))) {
						tv_p_bv.setTextColor(getResources().getColor(
								FunctionSetBg.setStrCheckSet(strPbv,
										strPbv_set)));
					}
					if (!(strPeg.equals("")) && (strPeg != null)
							&& !(strPeg_set.equals(""))
							&& (strPeg_set.equals(null))) {
						tv_peg.setTextColor(getResources().getColor(
								FunctionSetBg.setStrCheckSet(strPeg,
										strPeg_set)));
					}
				}

				// not set color
				((TextView) viewDetail.findViewById(R.id.tv_volume))
						.setText(jsoIndex.getString("volume").toString());
				((TextView) viewDetail.findViewById(R.id.tv_value))
						.setText(jsoIndex.getString("value").toString());
				((TextView) viewDetail.findViewById(R.id.tv_ceiling))
						.setText(jsoIndex.getString("ceiling").toString());
				((TextView) viewDetail.findViewById(R.id.tv_floor))
						.setText(jsoIndex.getString("floor").toString());

				list_symbol.addView(viewSymbol);
				list_detail.addView(viewDetail);
			}
		} catch (JSONException e) {
			e.printStackTrace();
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