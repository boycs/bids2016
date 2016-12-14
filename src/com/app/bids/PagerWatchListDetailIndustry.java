package com.app.bids;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;
import com.app.bids.PagerWatchListDetailNews.loadAll;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

public class PagerWatchListDetailIndustry extends Fragment{

	static Context context;
	public static View rootView;

	// activity listener interface
	private OnPageListener pageListener;

	Spinner spn_industry;

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

		Log.v("check indust", "onAttach");

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
				R.layout.pager_watchlist_detail_industry, container, false);

		return rootView;

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		((TextView) rootView.findViewById(R.id.tv_industry))
				.setText(UiWatchlistDetail.strIndustry);

		spn_industry = (Spinner) rootView.findViewById(R.id.spn_industry);

		if (UiWatchlistDetail.industryContentGetWatchlistNewsBySymbol != null) {
			initSetData();
			ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
					context, R.layout.spinner_dropdrow_item, UiWatchlistDetail.industryListSector);
			dataAdapter
					.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			spn_industry.setAdapter(dataAdapter);
		} else {
			addItemsOnSpinnerPeriod(); // init spinner
		}
		
//		addItemsOnSpinnerPeriod(); // init spinner
//		UiWatchlistDetail.dialogLoading.dismiss();
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (myAsyncTask != null)
			myAsyncTask.cancel(true);
		myAsyncTask = null;

	}

	// ====== init spinner ============
	public void addItemsOnSpinnerPeriod() {

		try {
			if (FragmentChangeActivity.contentGetIndustrySetSector != null) {
				for (int i = 0; i < FragmentChangeActivity.contentGetIndustrySetSector
						.length(); i++) {
					JSONObject jsaIndustrySetSectorSelect;
					jsaIndustrySetSectorSelect = FragmentChangeActivity.contentGetIndustrySetSector
							.getJSONObject(i);

					if (jsaIndustrySetSectorSelect.getString("industry")
							.equals(UiWatchlistDetail.strIndustry)) {
						UiWatchlistDetail.industryListSector = new ArrayList<String>();
						UiWatchlistDetail.industryListSector.add("ALL");

						JSONArray jsaSector = jsaIndustrySetSectorSelect
								.getJSONArray("sector");

						for (int k = 0; k < jsaSector.length(); k++) {
							UiWatchlistDetail.industryListSector.add("" + jsaSector.get(k));
						}
						ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(
								context, R.layout.spinner_dropdrow_item,
								UiWatchlistDetail.industryListSector);
						dataAdapter
								.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
						spn_industry.setAdapter(dataAdapter);

						spn_industry.setOnItemSelectedListener(new OnItemSelectedListenerPeriod());
						
						dataAdapter.notifyDataSetChanged();
						
//						Toast.makeText(context, "addItemsOnSpinnerPeriod", 0).show();
						
//						if(!UiWatchlistDetail.spn_industry_select){
//							UiWatchlistDetail.spn_industry_select = true;
//							spn_industry
//									.setOnItemSelectedListener(new OnItemSelectedListenerPeriod());
//						}else{
//							initSetData();
//						}
						break;
					}
				}
			} else {
				UiWatchlistDetail.dialogLoading.dismiss();
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	public class OnItemSelectedListenerPeriod implements OnItemSelectedListener {
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			
//			Toast.makeText(context, "OnItemSelectedListenerPeriod " + position, 0).show();

//			if(!UiWatchlistDetail.spn_industry_select){
//				UiWatchlistDetail.spn_industry_select = true;
//				spn_industry
//						.setOnItemSelectedListener(new OnItemSelectedListenerPeriod());
//			}else{
//				initSetData();
//				if (position == 0) {
//					UiWatchlistDetail.strIndustrySector = "ALL";
//					initLoadData();
//				} else {
//					UiWatchlistDetail.strIndustrySector = UiWatchlistDetail.industryListSector.get(position);
//					initLoadData();
//				}
//			}
			
			if (position == 0) {
				UiWatchlistDetail.strIndustrySector = "ALL";
				initLoadData();
			} else {
				UiWatchlistDetail.strIndustrySector = UiWatchlistDetail.industryListSector.get(position);
				initLoadData();
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
		}

	}

	// ============== Load initLoadData =========================
	private MyAsyncTask myAsyncTask = null;
	private boolean myAsyncTaskIsRunning = true;

	private void initLoadData() {
		MyAsyncTask resp = new MyAsyncTask();
		resp.execute();
	}

	public class MyAsyncTask extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;
		// ======= Ui Newds ========
		private JSONObject jsonIndustrySetSectorSelect;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

			UiWatchlistDetail.dialogLoading.show();
			// UiWatchlistDetailTest.ckIndustrySetData = false;
			myAsyncTaskIsRunning = true;
		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// ======= Ui News ========
			String url_industrySetSector = "";

			// FragmentChangeActivity.url_bidschart+"/service/v2/symbolByIndustrySector?industry=TECH&sector=TECH&page=1&limit=10
			// url_industrySetSector = SplashScreen.url_bidschart
			// + "/service/v2/symbolByIndustrySector?industry="
			// + strIndustry + "&sector=" + strSector + "&page=1&limit=10";

			url_industrySetSector = SplashScreen.url_bidschart
					+ "/service/v2/symbolByIndustrySector?industry="
					+ UiWatchlistDetail.strIndustry + "&sector="
					+ UiWatchlistDetail.strIndustrySector + "&limit=10";

			if (UiWatchlistDetail.strIndustrySector == "ALL") {
				url_industrySetSector = SplashScreen.url_bidschart
						+ "/service/v2/symbolByIndustry?industry="
						+ UiWatchlistDetail.strIndustry + "&sector="
						+ UiWatchlistDetail.strIndustrySector + "&limit=40";
			}

			Log.v("url_industrySetSector", "" + url_industrySetSector);

			try {
				// ======= Ui News ========
				jsonIndustrySetSectorSelect = ReadJson
						.readJsonObjectFromUrl(url_industrySetSector);
			} catch (IOException e1) {
				connectionError = true;
				jsonIndustrySetSectorSelect = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonIndustrySetSectorSelect = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonIndustrySetSectorSelect = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (jsonIndustrySetSectorSelect != null) {
					try {

						UiWatchlistDetail.industryContentGetWatchlistNewsBySymbol = jsonIndustrySetSectorSelect
								.getJSONArray("dataAll");
						initSetData();

					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					UiWatchlistDetail.dialogLoading.dismiss();
					Log.v("json null", "jsonGetArticleTitleByType null");
				}

			} else {
				UiWatchlistDetail.dialogLoading.dismiss();
				Log.v("json newslist null", "jsonGetArticleTitleByType null");
			}
		}
	}

	public void initSetData() {

		myAsyncTaskIsRunning = false;
		myAsyncTask = null;

		try {
			LinearLayout list_symbol = (LinearLayout) rootView
					.findViewById(R.id.list_symbol);
			LinearLayout list_detail = (LinearLayout) rootView
					.findViewById(R.id.list_detail);
			list_symbol.removeAllViews();
			list_detail.removeAllViews();

			for (int i = 0; i < UiWatchlistDetail.industryContentGetWatchlistNewsBySymbol.length(); i++) {
				View viewSymbol = ((Activity) context).getLayoutInflater()
						.inflate(R.layout.row_watchlist_symbol, null);
				View viewDetail = ((Activity) context).getLayoutInflater()
						.inflate(R.layout.row_sector_detail, null);

				JSONObject jsoIndex = UiWatchlistDetail.industryContentGetWatchlistNewsBySymbol
						.optJSONObject(i);

				// symbol
				final String symbol_name = jsoIndex.getString("symbol_name");
				String turnover_list_level = jsoIndex
						.getString("turnover_list_level");
				String status = jsoIndex.getString("status");
				String status_xd = jsoIndex.getString("status_xd");

				TextView tv_symbol_name = (TextView) viewSymbol
						.findViewById(R.id.tv_symbol_name);

				tv_symbol_name.setText(Html.fromHtml(FunctionFormatData
						.checkStatusSymbol(symbol_name, turnover_list_level,
								status, status_xd)));

				((TextView) viewSymbol
						.findViewById(R.id.tv_symbol_fullname_eng))
						.setText(jsoIndex.getString("symbol_fullname_eng")
								.toString());

				((LinearLayout) viewSymbol.findViewById(R.id.row_symbol))
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								// switchFragment(new PagerWatchlistDetail());

								FragmentChangeActivity.strSymbolSelect = symbol_name;
//								PagerWatchListDetailChart.li_view_chart
//										.removeView(FragmentChangeActivity.wv_chartiq);
								
//								UiWatchlistDetail uwd = new UiWatchlistDetail();
//								uwd.loadDataDetail();
								
//								context.startActivity(new Intent(context,
//										UiWatchlistDetail.class));
//								getActivity().finish();
								
//								UiWatchlistDetail.handlerInitView.sendEmptyMessage(FragmentChangeActivity.strSymbolSelect);

							}
						});

				// detail
				((LinearLayout) viewDetail.findViewById(R.id.row_detail))
						.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								// switchFragment(new PagerWatchlistDetail());

								FragmentChangeActivity.strSymbolSelect = symbol_name;
//								PagerWatchListDetailChart.li_view_chart
//										.removeView(FragmentChangeActivity.wv_chartiq);
								
//								context.startActivity(new Intent(context,
//										UiWatchlistDetail.class));
//								getActivity().finish();
								
							}
						});
				// img chart
				ImageView img_chart = (ImageView) viewDetail
						.findViewById(R.id.img_chart);
				FragmentChangeActivity.imageLoader.displayImage(
						SplashScreen.url_bidschart_chart
								+ jsoIndex.getString("symbol_name") + ".png",
						img_chart);

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
				tv_change.setText(FunctionSetBg.setStrDetailList(strChange));

				if ((strPercentChange == "0") || (strPercentChange == "")
						|| (strPercentChange == "0.00")) {
					tv_percentChange.setText("0.00");
				} else {
					tv_percentChange.setText(strPercentChange + "%");
				}

				// เซตสี change , lasttrade, percentchange เป็นสีตาม
				// change โดยเอา change เทียบกับ 0
				if (strChange != "") {
					tv_change.setTextColor(context.getResources().getColor(
							FunctionSetBg.setColor(strChange)));
					tv_last_trade.setTextColor(context.getResources().getColor(
							FunctionSetBg.setColor(strChange)));
					tv_percentChange.setTextColor(context.getResources()
							.getColor(FunctionSetBg.setColor(strChange)));
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
								FunctionSetBg.setStrCheckSet(strPe, strPe_set)));
					}
					if (!(strPbv.equals("")) && (strPbv != null)
							&& !(strPbv_set.equals(""))
							&& (strPbv_set.equals(null))) {
						tv_p_bv.setTextColor(getResources().getColor(
								FunctionSetBg
										.setStrCheckSet(strPbv, strPbv_set)));
					}
					if (!(strPeg.equals("")) && (strPeg != null)
							&& !(strPeg_set.equals(""))
							&& (strPeg_set.equals(null))) {
						tv_peg.setTextColor(getResources().getColor(
								FunctionSetBg
										.setStrCheckSet(strPeg, strPeg_set)));
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
			UiWatchlistDetail.dialogLoading.dismiss();
		} catch (JSONException e) {
			UiWatchlistDetail.dialogLoading.dismiss();
			e.printStackTrace();
		}
		UiWatchlistDetail.dialogLoading.dismiss();
		// UiWatchlistDetailTest.ckIndustrySetData = true;
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