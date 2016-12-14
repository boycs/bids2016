package com.app.bids;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
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

import com.app.bids.R;
import com.app.bids.PagerSetAlert.setRemoveSetAlert;
import com.app.bids.PagerWatchlistDetail.loadData;

import android.R.anim;
import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Toast;

@SuppressLint("NewApi")
public class PagerSetAlertSelect extends Fragment {

	static Context context;
	public static View rootView;

	Dialog dialogLoading;

	// --------- google analytics
	// private Tracker mTracker;
	// String nameTracker = new String("Hit");

	// activity listener interface
	private OnPageListener pageListener;

	public static JSONArray contentGetDetail = null;
	public static JSONObject contentGetRetrieveSetAlert = null;

	public static boolean ckAddSetAlertTp = true;

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
				R.layout.pager_setalert_select, container, false);

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

		dialogLoading.show();

		((TextView) rootView.findViewById(R.id.tv_close))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						switchFragment(new PagerSetAlert());
					}
				});

		loadDataDetail();
		// initView();

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

	// =============== Init view ==================
	private ViewGroup rl_container;
	ScrollView sv_list;
	// private ImageView imagen;
	private int yDelta_tp;
	private int yDelta_sl;

	LinearLayout li_current_price, li_list;
	TextView tv_current_price, tv_title_symbol;

	// ------- target price ------
	LinearLayout li_drag_drop_tp;
	TextView tv_symbol_tp, tv_last_trade_tp, tv_percent_tp, tv_drag_add_tp,
			tv_drag_add_tp_sl;

	LinearLayout li_up_tp, li_down_tp;
	ImageView img_up_tp, img_down_tp;

	int rl_container_wTp, rl_container_hTp, li_drag_drop_hTp, li_drag_drop_cTp;
	int rl_containerHeightPercentTp;

	int indexPriceTp, marGinsTopTp, marGinsTopLiTouchTp, percentViewGroupHTp,
			percentPriceTp;
	double dLastTradeTp, dCeillingTp, dFloorTp, dPrice1Tp;

	List<Double> listPricePeroidTp;
	List<Integer> listContainerTp;

	double dPriceTp;

	// ------- edit stop lose ------
	public static boolean liEditSlHide = true;
	public static boolean tvEditSlHide = true;
	LinearLayout li_edit_sl;
	TextView tv_edit_sl;
	public static double dCurrentPrice;

	// ------- stop lose ------
	LinearLayout li_drag_drop_sl, li_row_detail_sl;
	TextView tv_symbol_sl, tv_last_trade_sl, tv_percent_sl,
			tv_current_price_sl, tv_drag_add_sl;

	LinearLayout li_up_sl, li_down_sl;
	ImageView img_up_sl, img_down_sl;

	int rl_container_wSl, rl_container_hSl, li_drag_drop_hSl, li_drag_drop_cSl;
	int rl_containerHeightPercentSl;

	int indexPriceSl, marGinsTopSl, marGinsTopLiTouchSl, percentViewGroupHSl,
			percentPriceSl;
	double dLastTradeSl, dCeillingSl, dFloorSl, dPrice1Sl;

	List<Double> listPricePeroidSl;
	List<Integer> listContainerSl;

	double dPriceSl;
	// ------------------

	String strOrderBookId, strSymbol, strType;

	String strMarketListId;

	private void initView() {
		dialogLoading.dismiss();
		try {
			tv_title_symbol = (TextView) rootView
					.findViewById(R.id.tv_title_symbol);
			// current_price
			li_current_price = (LinearLayout) rootView
					.findViewById(R.id.li_current_price);
			tv_current_price = (TextView) rootView
					.findViewById(R.id.tv_current_price);

			// ----- list data ----
			sv_list = (ScrollView) rootView.findViewById(R.id.sv_list);
			li_list = (LinearLayout) rootView.findViewById(R.id.li_list);

			// --------------------------
			// ------- edit stop loss -----
			liEditSlHide = true;
			tvEditSlHide = true;
			li_edit_sl = (LinearLayout) rootView.findViewById(R.id.li_edit_sl);
			tv_edit_sl = (TextView) rootView.findViewById(R.id.tv_edit_sl);
			li_row_detail_sl = (LinearLayout) rootView
					.findViewById(R.id.li_row_detail_sl);
			li_drag_drop_sl = (LinearLayout) rootView
					.findViewById(R.id.li_drag_drop_sl);

			li_edit_sl.setVisibility(View.GONE);
			tv_edit_sl.setVisibility(View.GONE);
			li_row_detail_sl.setVisibility(View.GONE);
			li_drag_drop_sl.setVisibility(View.GONE);

			// --------------------------
			// ------- target price -----
			// on touch
			tv_drag_add_tp = (TextView) rootView
					.findViewById(R.id.tv_drag_add_tp);
			tv_drag_add_tp_sl = (TextView) rootView
					.findViewById(R.id.tv_drag_add_tp_sl);
			rl_container = (ViewGroup) rootView.findViewById(R.id.rl_container);
			li_drag_drop_tp = (LinearLayout) rootView
					.findViewById(R.id.li_drag_drop_tp);

			li_up_tp = (LinearLayout) rootView.findViewById(R.id.li_up_tp);
			li_down_tp = (LinearLayout) rootView.findViewById(R.id.li_down_tp);
			img_up_tp = (ImageView) rootView.findViewById(R.id.img_up_tp);
			img_down_tp = (ImageView) rootView.findViewById(R.id.img_down_tp);
			tv_symbol_tp = (TextView) rootView.findViewById(R.id.tv_symbol_tp);

			tv_last_trade_tp = (TextView) rootView
					.findViewById(R.id.tv_last_trade_tp);
			tv_percent_tp = (TextView) rootView
					.findViewById(R.id.tv_percent_tp);

			// -------- color init
			tv_last_trade_tp.setTextColor(getResources().getColor(
					R.color.c_warning));
			tv_percent_tp.setTextColor(getResources().getColor(
					R.color.c_warning));
			tv_drag_add_tp.setBackground(getResources().getDrawable(
					R.drawable.bg_setalert_add_warning));

			final JSONObject objDetail = contentGetDetail.getJSONObject(0);

			strSymbol = objDetail.getString("symbol_name");
			strOrderBookId = objDetail.getString("orderbook_id");
			strMarketListId = objDetail.getString("marketListId");

			tv_symbol_tp.setText(objDetail.getString("symbol_name"));

			tv_last_trade_tp.setText(objDetail.getString("last_trade"));
			dPriceTp = Double.parseDouble(objDetail.getString("last_trade")
					.replaceAll(",", ""));
			tv_percent_tp.setText("(0.00%)");

			tv_current_price.setText(objDetail.getString("last_trade"));
			dCurrentPrice = Double.parseDouble(objDetail
					.getString("last_trade").replaceAll(",", ""));

			// ---------- cal Gap --------
			dLastTradeTp = Double.parseDouble((objDetail
					.getString("last_trade")).replaceAll(",", ""));
			dCeillingTp = Double.parseDouble((objDetail.getString("ceiling"))
					.replaceAll(",", ""));
			dFloorTp = Double.parseDouble((objDetail.getString("floor"))
					.replaceAll(",", ""));

			listPricePeroidTp = new ArrayList<Double>();
			double priceUpTp = dLastTradeTp;
			listPricePeroidTp.add(dLastTradeTp);
			do {
				double gap = calGapUp(priceUpTp);
				priceUpTp = priceUpTp + gap;

				listPricePeroidTp.add(0, priceUpTp);
			} while (priceUpTp < dCeillingTp);

			double priceDownTp = dLastTradeTp;
			do {
				double gap = calGapDown(priceDownTp);
				priceDownTp = priceDownTp - gap;

				listPricePeroidTp.add(priceDownTp);
			} while (priceDownTp > dFloorTp);

			// on touch
			li_drag_drop_hTp = li_drag_drop_tp.getHeight();
			li_drag_drop_cTp = li_drag_drop_tp.getHeight() / 2;

			rl_container_wTp = rl_container.getWidth();
			rl_container_hTp = rl_container.getHeight() - li_drag_drop_hTp;
			rl_containerHeightPercentTp = rl_container_hTp
					/ listPricePeroidTp.size();

			listContainerTp = new ArrayList<Integer>();
			for (int i = 1; i <= listPricePeroidTp.size(); i++) { // ค่าจะเป็น
																	// บน
																	// ลง ล่าง
				listContainerTp.add(rl_containerHeightPercentTp * i);
			}

			// Log.v("listContainer", "" + listContainer);
			// **** OnTouchListener
			li_drag_drop_tp
					.setOnTouchListener(new MyTouchListenerTargetPrice());

			// --------------------
			for (int i = 0; i < listPricePeroidTp.size(); i++) {
				if (listPricePeroidTp.get(i) == dLastTradeTp) {
					indexPriceTp = i;
					break;
				}
			}
			// indexPrice = listPricePeroid.indexOf(dLastTrade); // ค่าจะเป็น บน
			// ลง ล่าง
			marGinsTopTp = rl_containerHeightPercentTp * (indexPriceTp + 1); // marGinsTop
			// น้อย
			// จะอยู่
			// บน
			marGinsTopLiTouchTp = marGinsTopTp; // marGinsTop + li_drag_drop_c;

			// -------- begin price -------
			int marginTopCurrent = listContainerTp.get(indexPriceTp)
					+ li_drag_drop_cTp;
			android.view.ViewGroup.LayoutParams layoutParamsPrice = li_current_price
					.getLayoutParams();
			((MarginLayoutParams) layoutParamsPrice).setMargins(0,
					marginTopCurrent, 0, 0);
			li_current_price.setLayoutParams(layoutParamsPrice);

			// -------- begin li touch -------
			final android.view.ViewGroup.LayoutParams layoutParamsTouch = li_drag_drop_tp
					.getLayoutParams();
			((MarginLayoutParams) layoutParamsTouch).setMargins(0,
					listContainerTp.get(indexPriceTp), 0, 0);
			li_drag_drop_tp.setLayoutParams(layoutParamsTouch);

			// Log.v("li_drag_drop", "" + li_drag_drop.getTop());
			// -------- click Up Down
			li_up_tp.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (li_drag_drop_tp.getTop() > 5) {
						marGinsTopLiTouchTp = marGinsTopLiTouchTp
								- rl_containerHeightPercentTp;
						setDataTouchTp(marGinsTopLiTouchTp);

						((MarginLayoutParams) layoutParamsTouch).setMargins(0,
								marGinsTopLiTouchTp, 0, 0);
						li_drag_drop_tp.setLayoutParams(layoutParamsTouch);
					}
				}
			});

			li_down_tp.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (li_drag_drop_tp.getTop() < rl_container_hTp) {
						marGinsTopLiTouchTp = marGinsTopLiTouchTp
								+ rl_containerHeightPercentTp;
						setDataTouchTp(marGinsTopLiTouchTp);

						((MarginLayoutParams) layoutParamsTouch).setMargins(0,
								marGinsTopLiTouchTp, 0, 0);
						li_drag_drop_tp.setLayoutParams(layoutParamsTouch);
					}
				}
			});

			tv_drag_add_tp.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (dPrice1Tp != dLastTradeTp) {
						if (dPrice1Tp > dLastTradeTp) {
							strType = "0"; // up
						} else if (dPrice1Tp < dLastTradeTp) {
							strType = "2"; // down
						} else {
							Toast.makeText(context, "Select Price.", 0).show();
						}
						ckAddSetAlertTp = true;
						sendAddSetAlert();
					}
				}
			});

			// -------- edit show hide -------
			tv_drag_add_tp_sl.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (tvEditSlHide) {
						tvEditSlHide = false;
						tv_edit_sl.setVisibility(View.VISIBLE);
						li_row_detail_sl.setVisibility(View.VISIBLE);
						li_drag_drop_sl.setVisibility(View.VISIBLE);
						tv_edit_sl.setText("Edit");
					} else {
						tvEditSlHide = true;
						liEditSlHide = true;
						tv_edit_sl.setVisibility(View.GONE);
						li_edit_sl.setVisibility(View.GONE);
						li_row_detail_sl.setVisibility(View.GONE);
						li_drag_drop_sl.setVisibility(View.GONE);
						tv_edit_sl.setText("Edit");
						tv_title_symbol.setText("BIDs Hit!");
					}
				}
			});

			tv_edit_sl.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (liEditSlHide) {
						liEditSlHide = false;
						li_row_detail_sl.setVisibility(View.GONE);
						li_edit_sl.setVisibility(View.VISIBLE);
						tv_edit_sl.setText("Done");
						tv_title_symbol.setText("Position Sizing Setting");
					} else {
						liEditSlHide = true;
						li_row_detail_sl.setVisibility(View.VISIBLE);
						li_edit_sl.setVisibility(View.GONE);
						tv_edit_sl.setText("Edit");
						tv_title_symbol.setText("BIDs Hit!");
					}
				}
			});

			// ------------------------------------------
			// ------- Stop loss -----
			// on touch
			tv_drag_add_sl = (TextView) rootView
					.findViewById(R.id.tv_drag_add_sl);

			li_up_sl = (LinearLayout) rootView.findViewById(R.id.li_up_sl);
			li_down_sl = (LinearLayout) rootView.findViewById(R.id.li_down_sl);
			img_up_sl = (ImageView) rootView.findViewById(R.id.img_up_sl);
			img_down_sl = (ImageView) rootView.findViewById(R.id.img_down_sl);
			tv_symbol_sl = (TextView) rootView.findViewById(R.id.tv_symbol_sl);

			tv_last_trade_sl = (TextView) rootView
					.findViewById(R.id.tv_last_trade_sl);
			tv_percent_sl = (TextView) rootView
					.findViewById(R.id.tv_percent_sl);

			// -------- color init
			tv_last_trade_sl.setTextColor(getResources().getColor(
					R.color.c_warning));
			tv_percent_sl.setTextColor(getResources().getColor(
					R.color.c_warning));
			tv_drag_add_sl.setBackground(getResources().getDrawable(
					R.drawable.bg_setalert_add_warning));

			tv_symbol_sl.setText(objDetail.getString("symbol_name"));

			tv_last_trade_sl.setText(objDetail.getString("last_trade"));
			dPriceSl = Double.parseDouble(objDetail.getString("last_trade")
					.replaceAll(",", ""));
			tv_percent_sl.setText("(0.00%)");

			// ---------- cal Gap --------
			dLastTradeSl = Double.parseDouble((objDetail
					.getString("last_trade")).replaceAll(",", ""));
			dCeillingSl = Double.parseDouble((objDetail.getString("ceiling"))
					.replaceAll(",", ""));
			dFloorSl = Double.parseDouble((objDetail.getString("floor"))
					.replaceAll(",", ""));

			listPricePeroidSl = new ArrayList<Double>();
			double priceUpSl = dLastTradeSl;

			listPricePeroidSl.add(dLastTradeSl);
			do {
				double gap = calGapUp(priceUpSl);
				priceUpSl = priceUpSl + gap;

				listPricePeroidSl.add(0, priceUpSl);
			} while (priceUpSl < dCeillingSl);
			double priceDownSp = dLastTradeSl;
			do {
				double gap = calGapDown(priceDownSp);
				priceDownSp = priceDownSp - gap;

				listPricePeroidSl.add(priceDownSp);
			} while (priceDownSp > dFloorSl);

			// on touch
			li_drag_drop_hSl = li_drag_drop_sl.getHeight();
			li_drag_drop_cSl = li_drag_drop_sl.getHeight() / 2;

			rl_container_wSl = rl_container.getWidth();
			rl_container_hSl = rl_container.getHeight() - li_drag_drop_hSl;
			rl_containerHeightPercentSl = rl_container_hSl
					/ listPricePeroidSl.size();

			listContainerSl = new ArrayList<Integer>();
			for (int i = 1; i <= listPricePeroidSl.size(); i++) { // ค่าจะเป็น
																	// บน
																	// ลง ล่าง
				listContainerSl.add(rl_containerHeightPercentSl * i);
			}

			// Log.v("listContainer", "" + listContainer);
			// **** OnTouchListener
			li_drag_drop_sl.setOnTouchListener(new MyTouchListenerStopLoss());

			// --------------------
			for (int i = 0; i < listPricePeroidSl.size(); i++) {
				if (listPricePeroidSl.get(i) == dLastTradeSl) {
					indexPriceSl = i;
					break;
				}
			}
			// indexPrice = listPricePeroid.indexOf(dLastTrade); // ค่าจะเป็น บน
			// ลง ล่าง
			marGinsTopSl = rl_containerHeightPercentSl * (indexPriceSl + 1); // marGinsTop
			// น้อย
			// จะอยู่
			// บน
			marGinsTopLiTouchSl = marGinsTopSl; // marGinsTop + li_drag_drop_c;

			// -------- begin li touch -------
			final android.view.ViewGroup.LayoutParams layoutParamsTouchSl = li_drag_drop_sl
					.getLayoutParams();
			((MarginLayoutParams) layoutParamsTouchSl).setMargins(0,
					listContainerSl.get(indexPriceSl), 0, 0);
			li_drag_drop_sl.setLayoutParams(layoutParamsTouchSl);

			// Log.v("li_drag_drop", "" + li_drag_drop.getTop());
			// -------- click Up Down
			li_up_sl.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (li_drag_drop_sl.getTop() > 5) {
						marGinsTopLiTouchSl = marGinsTopLiTouchSl
								- rl_containerHeightPercentSl;
						setDataTouchSl(marGinsTopLiTouchSl);

						((MarginLayoutParams) layoutParamsTouchSl).setMargins(
								0, marGinsTopLiTouchSl, 0, 0);
						li_drag_drop_sl.setLayoutParams(layoutParamsTouchSl);
					}
				}
			});

			li_down_sl.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (li_drag_drop_sl.getTop() < rl_container_hSl) {
						marGinsTopLiTouchSl = marGinsTopLiTouchSl
								+ rl_containerHeightPercentSl;
						setDataTouchSl(marGinsTopLiTouchSl);

						((MarginLayoutParams) layoutParamsTouchSl).setMargins(
								0, marGinsTopLiTouchSl, 0, 0);
						li_drag_drop_sl.setLayoutParams(layoutParamsTouchSl);
					}
				}
			});

			tv_drag_add_sl.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (dPrice1Sl != dLastTradeSl) {
						if (dPrice1Sl > dLastTradeSl) {
							strType = "0"; // up
						} else if (dPrice1Sl < dLastTradeSl) {
							strType = "2"; // down
						} else {
							Toast.makeText(context, "Select Price.", 0).show();
						}
						ckAddSetAlertTp = false;
						sendAddSetAlert();
					}
				}
			});

			// ------- stoploss show hide ----------
			viewEditStopLoss();
			// ------- load data retrieve ----------
			loadDataRetrieveSetAlertList(); // load setalert add

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	// =============== view stop loss Edit ==================
	public static EditText et_credit, et_risk;
	public static TextView tv_credit_minus, tv_credit_plus, tv_risk_minus,
			tv_risk_plus, tv_rr, tv_position_sizing, row_tv_credit,
			row_tv_risk, row_tv_rr, row_tv_position_sizing;

	public static double d_credit, d_risk, d_rr, d_position_sizing;

	private void viewEditStopLoss() {
		et_credit = (EditText) rootView.findViewById(R.id.et_credit);
		et_risk = (EditText) rootView.findViewById(R.id.et_risk);

		tv_credit_minus = (TextView) rootView
				.findViewById(R.id.tv_credit_minus);
		tv_credit_plus = (TextView) rootView.findViewById(R.id.tv_credit_plus);
		tv_risk_minus = (TextView) rootView.findViewById(R.id.tv_risk_minus);
		tv_risk_plus = (TextView) rootView.findViewById(R.id.tv_risk_plus);

		tv_rr = (TextView) rootView.findViewById(R.id.tv_rr);
		tv_position_sizing = (TextView) rootView
				.findViewById(R.id.tv_position_sizing);

		// ถ้า marketListId == ETF เพิ่ม ลด ทีละ 0.1

		// ----- credit
		tv_credit_plus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = et_credit.getText().toString();
				double dText = Double.parseDouble(text);
				dText = dText + 100;
				et_credit.setText("" + dText);
			}
		});
		tv_credit_minus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = et_credit.getText().toString();
				double dText = Double.parseDouble(text);
				if (dText >= 0) {
					dText = dText - 100;
					et_credit.setText("" + dText);
				}
			}
		});
		// ----- risk
		tv_risk_plus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = et_risk.getText().toString();
				double dText = Double.parseDouble(text);
				dText = dText + 1;
				et_risk.setText("" + dText);
			}
		});
		tv_risk_minus.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = et_risk.getText().toString();
				double dText = Double.parseDouble(text);
				if (dText > 0) {
					dText = dText - 1;
					et_risk.setText("" + dText);
				}
			}
		});

		et_credit.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String text = et_credit.getText().toString();
				if (text.equals("0")) {
					d_credit = 0;
				} else {
					d_credit = Double.parseDouble(text);
				}
				row_tv_credit.setText("" + d_credit);
				setCalEditStopLoss();
			}
		});

		et_risk.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
				String text = et_risk.getText().toString();
				if (text.equals("0")) {
					d_risk = 0;
				} else {
					d_risk = Double.parseDouble(text);
				}
				row_tv_risk.setText(d_risk + "%");
				setCalEditStopLoss();
			}
		});

		// -------- row stop loss
		row_tv_credit = (TextView) rootView.findViewById(R.id.row_tv_credit);
		row_tv_risk = (TextView) rootView.findViewById(R.id.row_tv_risk);
		row_tv_rr = (TextView) rootView.findViewById(R.id.row_tv_rr);
		row_tv_position_sizing = (TextView) rootView
				.findViewById(R.id.row_tv_position_sizing);
	}

	// cal set data
	private void setCalEditStopLossRR() {

		double dRr = (dPrice1Tp - dCurrentPrice) / (dCurrentPrice - dPrice1Sl);
		String sRr = String.format("%,.2f", dRr);

		if (dPrice1Sl == dLastTradeSl) {
			tv_rr.setText("NaN:-∞");
			row_tv_rr.setText("NaN:-∞");
		} else {
			tv_rr.setText("1:" + sRr);
			row_tv_rr.setText("1:" + sRr);
		}
	}

	// cal set data
	private void setCalEditStopLoss() {
		// takeprofit -> tagetprice
		// entry -> curent price
		// rr = 1:((takeprofit-entry)/(entry-stoploss))
		// position sizing = (risk%(หาriskเป็นเปอร์เซนต์) *
		// credit)/(entry-stoploss)

		double dRr = (dPrice1Tp - dCurrentPrice) / (dCurrentPrice - dPrice1Sl);
		String sRr = String.format("%,.2f", dRr);

		String txtCredit = et_credit.getText().toString();
		String txtRisk = et_risk.getText().toString();
		double dCredit = Double.parseDouble(txtCredit);
		double dRisk = Double.parseDouble(txtRisk);

		double dPositionSizing = ((dRisk * 0.01) * dCredit)
				/ (dCurrentPrice - dPrice1Sl);
		String sPositionSizing = FunctionFormatData.setFormatNumber(""
				+ dPositionSizing);

		if (dPrice1Sl == dLastTradeSl) {
			tv_rr.setText("NaN:-∞");
			tv_position_sizing.setText("+∞");
			row_tv_rr.setText("NaN:-∞");
			row_tv_position_sizing.setText("+∞");
			tv_position_sizing.setTextColor(getResources().getColor(
					R.color.c_success));
			row_tv_position_sizing.setTextColor(getResources().getColor(
					R.color.c_success));
		} else if (dPrice1Sl > dLastTradeSl) {
			tv_rr.setText("1:" + sRr);
			tv_position_sizing.setText("" + sPositionSizing);
			row_tv_rr.setText("1:" + sRr);
			row_tv_position_sizing.setText("" + sPositionSizing);
			tv_position_sizing.setTextColor(getResources().getColor(
					R.color.c_danger));
			row_tv_position_sizing.setTextColor(getResources().getColor(
					R.color.c_danger));
		} else {
			tv_rr.setText("1:" + sRr);
			tv_position_sizing.setText("" + sPositionSizing);
			row_tv_rr.setText("1:" + sRr);
			row_tv_position_sizing.setText("" + sPositionSizing);
			tv_position_sizing.setTextColor(getResources().getColor(
					R.color.c_success));
			row_tv_position_sizing.setTextColor(getResources().getColor(
					R.color.c_success));
		}
	}

	// =============== data target price ==================
	double dCalPerCentTp;
	String strPlusTp = "";

	private void setDataTouchTp(int topY) { // topY = 606
		for (int i = 0; i < listContainerTp.size(); i++) {
			strPlusTp = "";
			if (i == listContainerTp.size() - 1) {
				if (topY > listContainerTp.get(listContainerTp.size() - 1)) {
					dPrice1Tp = listPricePeroidTp
							.get(listContainerTp.size() - 1);

					tv_last_trade_tp.setText(String.format("%,.2f", dPrice1Tp));
					dCalPerCentTp = (((dPrice1Tp - dPriceTp) * 100) / dPriceTp);
					strPlusTp = (dCalPerCentTp > 0) ? "+" : "";
					tv_percent_tp.setText("(" + strPlusTp
							+ String.format("%,.2f", dCalPerCentTp) + "%)");

					setColorTvTp(dPrice1Tp);
				} else if (topY < listContainerTp.get(0)) {
					dPrice1Tp = listPricePeroidTp.get(0);

					tv_last_trade_sl.setText(String.format("%,.2f", dPrice1Tp));
					dCalPerCentTp = (((dPrice1Tp - dPriceTp) * 100) / dPriceTp);
					strPlusTp = (dCalPerCentTp > 0) ? "+" : "";
					tv_percent_tp.setText("(" + strPlusTp
							+ String.format("%,.2f", dCalPerCentTp) + "%)");
					setColorTvTp(dPrice1Tp);
				}
				break;
			} else {
				if (topY > listContainerTp.get(i)
						&& topY <= listContainerTp.get(i + 1)) {

					dPrice1Tp = listPricePeroidTp.get(i);

					tv_last_trade_tp.setText(String.format("%,.2f", dPrice1Tp));
					dCalPerCentTp = (((dPrice1Tp - dPriceTp) * 100) / dPriceTp);
					strPlusTp = (dCalPerCentTp > 0) ? "+" : "";
					tv_percent_tp.setText("(" + strPlusTp
							+ String.format("%,.2f", dCalPerCentTp) + "%)");
					setColorTvTp(dPrice1Tp);
					break;
				}
			}
		}
	}

	// =============== color price target price ==================
	private void setColorTvTp(Double cPrice) {
		if (cPrice == dLastTradeTp) {
			tv_last_trade_tp.setTextColor(getResources().getColor(
					R.color.c_warning));
			tv_percent_tp.setTextColor(getResources().getColor(
					R.color.c_warning));
			tv_drag_add_tp.setBackground(getResources().getDrawable(
					R.drawable.bg_setalert_add_warning));
		} else {
			if (cPrice > dLastTradeTp) {
				tv_last_trade_tp.setTextColor(getResources().getColor(
						R.color.c_success));
				tv_percent_tp.setTextColor(getResources().getColor(
						R.color.c_success));
				tv_drag_add_tp.setBackground(getResources().getDrawable(
						R.drawable.bg_setalert_add_success));
			} else if (cPrice < dLastTradeTp) {
				tv_last_trade_tp.setTextColor(getResources().getColor(
						R.color.c_danger));
				tv_percent_tp.setTextColor(getResources().getColor(
						R.color.c_danger));
				tv_drag_add_tp.setBackground(getResources().getDrawable(
						R.drawable.bg_setalert_add_danger));
			}
		}
		setCalEditStopLossRR();
	}

	// =============== on touch target price ==================

	// -------- target price
	int li_w_tp, li_h_tp;
	int ckTop_tp = 0;
	int ckBottom_tp = 0;
	int ckCenter_tp = 0; // 0 touch ได้ , 1 touch ไม่ได้

	private final class MyTouchListenerTargetPrice implements OnTouchListener {
		public boolean onTouch(View view, MotionEvent event) {
			// final int X = (int) event.getRawX();

			final int Y = (int) event.getRawY();

			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
					.getLayoutParams();

			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			// Al tocar la pantalla
			case MotionEvent.ACTION_DOWN:
				RelativeLayout.LayoutParams Params = (RelativeLayout.LayoutParams) view
						.getLayoutParams();
				yDelta_tp = Y - Params.topMargin;

				break;
			case MotionEvent.ACTION_UP:

				if (li_drag_drop_tp.getTop() < 5) {
					ckTop_tp = 1;
					ckBottom_tp = 0;
					marGinsTopLiTouchTp = rl_container.getTop();
					setDataTouchTp(marGinsTopLiTouchTp);
					layoutParams.topMargin = marGinsTopLiTouchTp;
				} else if (li_drag_drop_tp.getTop() >= rl_container_hTp) {
					ckTop_tp = 0;
					ckBottom_tp = 1;
					// marGinsTopLiTouch = rl_container_h;
					marGinsTopLiTouchTp = rl_container_hTp - 3;
					setDataTouchTp(marGinsTopLiTouchTp);
					layoutParams.topMargin = marGinsTopLiTouchTp;
				} else {

				}
				break;
			case MotionEvent.ACTION_MOVE:
				// float y = event.getY();

				if ((li_drag_drop_tp.getTop() < 5)
						|| (li_drag_drop_tp.getTop() >= rl_container_hTp)) {

					if ((li_drag_drop_tp.getTop() < 5) && (ckTop_tp == 0)) {
						// Log.v("iffff", "iffff");

						ckTop_tp = 1;
						ckBottom_tp = 0;
						marGinsTopLiTouchTp = rl_container.getTop();
						setDataTouchTp(marGinsTopLiTouchTp);
						layoutParams.topMargin = marGinsTopLiTouchTp;
					} else if ((li_drag_drop_tp.getTop() >= rl_container_hTp)
							&& (ckBottom_tp == 0)) {
						// Log.v("else if", "else if else if");

						ckTop_tp = 0;
						ckBottom_tp = 1;
						// marGinsTopLiTouch = rl_container_h;
						marGinsTopLiTouchTp = rl_container_hTp - 3;
						setDataTouchTp(marGinsTopLiTouchTp);
						layoutParams.topMargin = marGinsTopLiTouchTp;
					}
				} else {
					ckTop_tp = 0;
					ckBottom_tp = 0;
					marGinsTopLiTouchTp = Y - yDelta_tp;
					setDataTouchTp(marGinsTopLiTouchTp);
					layoutParams.topMargin = marGinsTopLiTouchTp;
				}

				view.setLayoutParams(layoutParams);
				break;
			}

			rl_container.invalidate();
			return true;
		}
	}

	// =============== on touch Stop Loss ==================
	// -------- stop loss
	int li_w_sl, li_h_sp;
	int ckTop_sl = 0;
	int ckBottom_sl = 0;
	int ckCenter_sl = 0; // 0 touch ได้ , 1 touch ไม่ได้

	private final class MyTouchListenerStopLoss implements OnTouchListener {
		public boolean onTouch(View view, MotionEvent event) {
			// final int X = (int) event.getRawX();

			final int Y = (int) event.getRawY();

			RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) view
					.getLayoutParams();

			switch (event.getAction() & MotionEvent.ACTION_MASK) {
			// Al tocar la pantalla
			case MotionEvent.ACTION_DOWN:
				RelativeLayout.LayoutParams Params = (RelativeLayout.LayoutParams) view
						.getLayoutParams();
				yDelta_sl = Y - Params.topMargin;

				break;
			case MotionEvent.ACTION_UP:

				if (li_drag_drop_sl.getTop() < 5) {
					ckTop_sl = 1;
					ckBottom_sl = 0;
					marGinsTopLiTouchSl = rl_container.getTop();
					setDataTouchSl(marGinsTopLiTouchSl);
					layoutParams.topMargin = marGinsTopLiTouchSl;
				} else if (li_drag_drop_sl.getTop() >= rl_container_hSl) {
					ckTop_sl = 0;
					ckBottom_sl = 1;
					// marGinsTopLiTouch = rl_container_h;
					marGinsTopLiTouchSl = rl_container_hSl - 3;
					setDataTouchSl(marGinsTopLiTouchSl);
					layoutParams.topMargin = marGinsTopLiTouchSl;
				} else {

				}
				break;
			case MotionEvent.ACTION_MOVE:
				// float y = event.getY();

				if ((li_drag_drop_sl.getTop() < 5)
						|| (li_drag_drop_sl.getTop() >= rl_container_hSl)) {

					if ((li_drag_drop_sl.getTop() < 5) && (ckTop_sl == 0)) {
						// Log.v("iffff", "iffff");

						ckTop_sl = 1;
						ckBottom_sl = 0;
						marGinsTopLiTouchSl = rl_container.getTop();
						setDataTouchTp(marGinsTopLiTouchSl);
						layoutParams.topMargin = marGinsTopLiTouchSl;
					} else if ((li_drag_drop_sl.getTop() >= rl_container_hSl)
							&& (ckBottom_sl == 0)) {
						// Log.v("else if", "else if else if");

						ckTop_sl = 0;
						ckBottom_sl = 1;
						// marGinsTopLiTouch = rl_container_h;
						marGinsTopLiTouchSl = rl_container_hSl - 3;
						setDataTouchSl(marGinsTopLiTouchSl);
						layoutParams.topMargin = marGinsTopLiTouchSl;
					}
				} else {
					ckTop_sl = 0;
					ckBottom_sl = 0;
					marGinsTopLiTouchSl = Y - yDelta_sl;
					setDataTouchSl(marGinsTopLiTouchSl);
					layoutParams.topMargin = marGinsTopLiTouchSl;
				}

				view.setLayoutParams(layoutParams);
				break;
			}

			rl_container.invalidate();
			return true;
		}
	}

	// =============== data Stop loss ==================
	double dCalPerCentSl;
	String strPlusSl = "";

	private void setDataTouchSl(int topY) { // topY = 606
		for (int i = 0; i < listContainerSl.size(); i++) {
			strPlusSl = "";
			if (i == listContainerSl.size() - 1) {
				if (topY > listContainerSl.get(listContainerSl.size() - 1)) {
					dPrice1Sl = listPricePeroidSl
							.get(listContainerSl.size() - 1);

					tv_last_trade_sl.setText(String.format("%,.2f", dPrice1Sl));
					dCalPerCentSl = (((dPrice1Sl - dPriceSl) * 100) / dPriceSl);
					strPlusSl = (dCalPerCentSl > 0) ? "+" : "";
					tv_percent_sl.setText("(" + strPlusSl
							+ String.format("%,.2f", dCalPerCentSl) + "%)");

					setColorTvSl(dPrice1Sl);
				} else if (topY < listContainerSl.get(0)) {
					dPrice1Sl = listPricePeroidSl.get(0);

					tv_last_trade_sl.setText(String.format("%,.2f", dPrice1Sl));
					dCalPerCentSl = (((dPrice1Sl - dPriceSl) * 100) / dPriceSl);
					strPlusSl = (dCalPerCentSl > 0) ? "+" : "";
					tv_percent_sl.setText("(" + strPlusSl
							+ String.format("%,.2f", dCalPerCentSl) + "%)");
					setColorTvSl(dPrice1Sl);
				}
				break;
			} else {
				if (topY > listContainerSl.get(i)
						&& topY <= listContainerSl.get(i + 1)) {

					dPrice1Sl = listPricePeroidSl.get(i);

					tv_last_trade_sl.setText(String.format("%,.2f", dPrice1Sl));
					dCalPerCentSl = (((dPrice1Sl - dPriceSl) * 100) / dPriceSl);
					strPlusSl = (dCalPerCentSl > 0) ? "+" : "";
					tv_percent_sl.setText("(" + strPlusSl
							+ String.format("%,.2f", dCalPerCentSl) + "%)");
					setColorTvSl(dPrice1Sl);
					break;
				}
			}
		}
	}

	// =============== color price Stop Loss ==================
	private void setColorTvSl(Double cPrice) {
		if (cPrice == dLastTradeSl) {
			tv_last_trade_sl.setTextColor(getResources().getColor(
					R.color.c_warning));
			tv_percent_sl.setTextColor(getResources().getColor(
					R.color.c_warning));
			tv_drag_add_sl.setBackground(getResources().getDrawable(
					R.drawable.bg_setalert_add_warning));
		} else {
			if (cPrice > dLastTradeSl) {
				tv_last_trade_sl.setTextColor(getResources().getColor(
						R.color.c_success));
				tv_percent_sl.setTextColor(getResources().getColor(
						R.color.c_success));
				tv_drag_add_sl.setBackground(getResources().getDrawable(
						R.drawable.bg_setalert_add_success));
			} else if (cPrice < dLastTradeTp) {
				tv_last_trade_sl.setTextColor(getResources().getColor(
						R.color.c_danger));
				tv_percent_sl.setTextColor(getResources().getColor(
						R.color.c_danger));
				tv_drag_add_sl.setBackground(getResources().getDrawable(
						R.drawable.bg_setalert_add_danger));
			}
		}
		setCalEditStopLoss();
	}

	// ============ calculas price ใช้ do while }
	// เอา return มาบวก price แล้วมาใส่เอาเรย์ข้างหน้า ไม่เกินค่า celling
	private double calGapUp(double price) {
		double gap;
		if (strMarketListId.equals("ETF")) {
			gap = 0.1;
		} else {
			if (price < 2) {
				gap = 0.01;
			} else if (price >= 2 && price < 5) {
				gap = 0.02;
			} else if (price >= 5 && price < 10) {
				gap = 0.05;
			} else if (price >= 10 && price < 25) {
				gap = 0.10;
			} else if (price >= 25 && price < 100) {
				gap = 0.25;
			} else if (price >= 100 && price < 200) {
				gap = 0.50;
			} else if (price >= 200 && price < 400) {
				gap = 1.00;
			} else {
				gap = 2.00;
			}
		}
		return gap;
	}

	// เอา price มาลบ return แล้วมาใส่เอาเรย์ข้างหน้า ไม่น้อยกว่าค่า foor
	private double calGapDown(double price) {
		double gap;
		if (strMarketListId.equals("ETF")) {
			gap = 0.1;
		} else {
			if (price < 2.02) {
				gap = 0.01;
			} else if (price >= 2 && price < 5.05) {
				gap = 0.02;
			} else if (price >= 5 && price < 10.10) {
				gap = 0.05;
			} else if (price >= 10 && price < 25.25) {
				gap = 0.10;
			} else if (price >= 25 && price < 100.50) {
				gap = 0.25;
			} else if (price >= 100 && price < 201) {
				gap = 0.50;
			} else if (price >= 200 && price < 402) {
				gap = 1.00;
			} else {
				gap = 2.00;
			}
		}
		return gap;
	}

	// ============== Load Data all =============
	public void loadDataDetail() {
		loadData resp = new loadData();
		resp.execute();
	}

	public class loadData extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;
		// ======= json ========
		private JSONObject jsonGetDetail;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// ======= url ========

			// http://www.bidschart.com/service/v2/watchlistSymbol?user_id=104&symbol=ptt
			String url_GetDetail = SplashScreen.url_bidschart
					+ "/service/v2/watchlistSymbolV2?user_id="
					+ SplashScreen.userModel.user_id + "&symbol="
					+ FragmentChangeActivity.strSymbolSelect + "&timestamp="
					+ timestamp;
			try {
				// ======= Ui Home ========
				jsonGetDetail = ReadJson.readJsonObjectFromUrl(url_GetDetail);
			} catch (IOException e1) {
				connectionError = true;
				jsonGetDetail = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonGetDetail = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonGetDetail = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (jsonGetDetail != null) {
					try {
						// get content
						contentGetDetail = jsonGetDetail
								.getJSONArray("dataAll");

						// Log.v("contentGetDetail", ""+contentGetDetail);

						initView();

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

	// ============== Load Data RetrieveSetAlert =============
	public void loadDataRetrieveSetAlertList() {
		loadDataRetrieveSetAler resp = new loadDataRetrieveSetAler();
		resp.execute();
	}

	public class loadDataRetrieveSetAler extends AsyncTask<Void, Void, Void> {
		boolean connectionError = false;
		// ======= json ========
		private JSONObject jsonGetDetail;

		@Override
		protected void onPreExecute() {
			super.onPreExecute();

		}

		@Override
		protected Void doInBackground(Void... params) {

			java.util.Date date = new java.util.Date();
			long timestamp = date.getTime();
			// ======= url ========

			// http://bidschart.com/bidsMaster/retrieveSetAlertList?user_id=104&orderbook_id=2099
			String url_GetDetail = SplashScreen.url_bidschart
					+ "/bidsMaster/retrieveSetAlertList?user_id="
					+ SplashScreen.userModel.user_id + "&orderbook_id="
					+ strOrderBookId + "&timestamp=" + timestamp;

			try {
				// ======= Ui Home ========
				jsonGetDetail = ReadJson.readJsonObjectFromUrl(url_GetDetail);
			} catch (IOException e1) {
				connectionError = true;
				jsonGetDetail = null;
				e1.printStackTrace();
			} catch (JSONException e1) {
				connectionError = true;
				jsonGetDetail = null;
				e1.printStackTrace();
			} catch (RuntimeException e) {
				connectionError = true;
				jsonGetDetail = null;
				e.printStackTrace();
			}
			return null;
		}

		@Override
		protected void onPostExecute(Void result) {
			super.onPostExecute(result);

			if (connectionError == false) {
				if (jsonGetDetail != null) {
					contentGetRetrieveSetAlert = jsonGetDetail;

					setDataSetAlert();

				} else {
					Log.v("json newslist null", "newslist null");
				}
			} else {
			}
			dialogLoading.dismiss();
		}
	}

	// ============== add setalert ===============
	String resultAddSetAlert = "";

	public void sendAddSetAlert() {

		setAddSetAlert resp = new setAddSetAlert();
		resp.execute();
	}

	public class setAddSetAlert extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		String temp = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialogLoading.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			String url = SplashScreen.url_bidschart + "/bidsMaster/addSetAlert";

			String json = "";
			InputStream inputStream = null;

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();

				jsonObject
						.accumulate("user_id", SplashScreen.userModel.user_id);
				jsonObject.accumulate("orderbook_id", strOrderBookId);
				jsonObject.accumulate("symbol_name", strSymbol);
				jsonObject.accumulate("type", strType);
				if (ckAddSetAlertTp) {
					jsonObject.accumulate("price1", dPrice1Tp);
				} else {
					jsonObject.accumulate("price1", dPrice1Sl);
					jsonObject.accumulate("is_stoploss", "1");
				}

				// add target price
				// parameter:
				// user_id=53&orderbook_id=%@&symbol_name=%@&type=%@&price1=%.2f

				// add stop loss
				// parameter:
				// user_id=53&orderbook_id=%@&symbol_name=%@&type=%@&price1=%.2f&is_stoploss=1

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
					resultAddSetAlert = AFunctionOther
							.convertInputStreamToString(inputStream);
				else
					resultAddSetAlert = "Did not work!";

				Log.v("result addSetAlert : ", "" + resultAddSetAlert);

				// {"result":0,"data":{}}

				// {"result":1,"data":{"id":"117","user_id":"104","type":"0","orderbook_id":"2099","hit_status":"0","symbol_name":"PTT","price1":"272","price2":"0","create_datetime":"2016-02-18 15:00:58","hit_datetime":"","first_hit_datetime":null,"update_datetime":"2016-02-18 15:00:58"}}
				// {"result":1,"data":{"id":"118","user_id":"104","type":"2","orderbook_id":"2099","hit_status":"0","symbol_name":"PTT","price1":"197","price2":"0","create_datetime":"2016-02-18 15:02:27","hit_datetime":"","first_hit_datetime":null,"update_datetime":"2016-02-18 15:02:27"}}

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
				try {
					JSONObject jsoAddSetAlert = new JSONObject(
							resultAddSetAlert);
					if (jsoAddSetAlert.getString("result").equals("0")) {
						// prepare the alert box
						final AlertDialog.Builder alertbox = new AlertDialog.Builder(
								context);
						alertbox.setTitle("  Bids");
						alertbox.setMessage("Quata is full or Condition is duplicate.");
						alertbox.setNeutralButton("OK",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface arg0,
											int arg1) {
										dialogLoading.dismiss();
									}
								});
						alertbox.show();
					} else {
						PagerSetAlert.setALertNewLoad = true;
						loadDataDetail();
					}
				} catch (JSONException e1) {
					e1.printStackTrace();
				}
			}
		}
	}

	// ============== add setalert ===============
	String strId, strOrderbookId;
	Dialog alertDialogSetAlert;

	private void setDataSetAlert() {
		// dialogLoading.dismiss();

		LinearLayout li_list = (LinearLayout) rootView
				.findViewById(R.id.li_list);
		LinearLayout li_list_hit = (LinearLayout) rootView
				.findViewById(R.id.li_list_hit);
		ScrollView sv_list = (ScrollView) rootView.findViewById(R.id.sv_list);

		li_list.setVisibility(View.GONE);
		li_list_hit.setVisibility(View.GONE);

		try {
			JSONArray jsaList1 = contentGetRetrieveSetAlert
					.getJSONArray("list1");
			JSONArray jsaList2 = contentGetRetrieveSetAlert
					.getJSONArray("list2");

			LinearLayout li_list1 = (LinearLayout) rootView
					.findViewById(R.id.li_list1);
			LinearLayout li_list2 = (LinearLayout) rootView
					.findViewById(R.id.li_list2);
			li_list1.removeAllViews();
			li_list2.removeAllViews();

			// ---- list 1 -----
			if (jsaList1.length() > 0) {
				li_list.setVisibility(View.VISIBLE);

				for (int i = 0; i < jsaList1.length(); i++) {
					JSONObject jsoIndex = jsaList1.getJSONObject(i);

					View viewSetAlert = ((Activity) context)
							.getLayoutInflater().inflate(
									R.layout.row_setalert_select_list1, null);

					TextView tv_symbol, tv_price1, tv_date, tv_time, tv_running, tv_delete;
					ImageView img_type, img_manage, img_stoploss;

					tv_symbol = (TextView) viewSetAlert
							.findViewById(R.id.tv_symbol);
					tv_price1 = (TextView) viewSetAlert
							.findViewById(R.id.tv_price1);
					tv_date = (TextView) viewSetAlert
							.findViewById(R.id.tv_date);
					tv_time = (TextView) viewSetAlert
							.findViewById(R.id.tv_time);
					tv_running = (TextView) viewSetAlert
							.findViewById(R.id.tv_running);
					img_type = (ImageView) viewSetAlert
							.findViewById(R.id.img_type);
					img_manage = (ImageView) viewSetAlert
							.findViewById(R.id.img_manage);
					img_stoploss = (ImageView) viewSetAlert
							.findViewById(R.id.img_stoploss);
					tv_delete = (TextView) viewSetAlert
							.findViewById(R.id.tv_delete);

					// ----------
					final String strId_index = jsoIndex.getString("id");
					final String strOrderbookId_index = jsoIndex
							.getString("orderbook_id");
					final String strSymbolName_index = jsoIndex
							.getString("symbol_name");
					final String strType_index = jsoIndex.getString("type");
					final String strSymbol = jsoIndex.getString("symbol_name");

					tv_symbol.setText(jsoIndex.getString("symbol_name"));
					tv_price1.setText(jsoIndex.getString("price1"));
					String strIs_stoploss = jsoIndex.getString("is_stoploss");

					if (strIs_stoploss.equals("0")) {
						img_stoploss
								.setBackgroundResource(R.drawable.img_bidshit_takeprofit);
					} else {
						img_stoploss
								.setBackgroundResource(R.drawable.img_bidshit_stoploss);
					}

					tv_date.setText(DateTimeCreate.DateDmyThaiCreate(jsoIndex
							.getString("create_datetime")));
					tv_time.setText(DateTimeCreate.TimeCreate(jsoIndex
							.getString("create_datetime")));

					if (jsoIndex.getString("type").equals("0")) {
						tv_price1.setTextColor(getResources().getColor(
								R.color.c_success));
						img_type.setBackgroundResource(R.drawable.icon_alert_up);
					} else if (jsoIndex.getString("type").equals("2")) {
						tv_price1.setTextColor(getResources().getColor(
								R.color.c_danger));
						img_type.setBackgroundResource(R.drawable.icon_alert_down);
					} else {
						tv_price1.setTextColor(getResources().getColor(
								R.color.c_content));
						img_type.setBackgroundResource(R.drawable.icon_alert_default);
					}

					tv_delete.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							strId = strId_index;
							strOrderbookId = strOrderbookId_index;
							// strSymbol = strSymbolName_index;
							strType = strType_index;
							FragmentChangeActivity.strSymbolSelect = strSymbol;
							sendRemoveSetAlert();
						}
					});
					img_manage.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							alertDialogSetAlert = new Dialog(context);
							LayoutInflater inflater = LayoutInflater
									.from(context);
							View dlView = inflater.inflate(
									R.layout.dialog_setalert_row_select, null);
							// perform operation on elements in Layout
							alertDialogSetAlert
									.requestWindowFeature(Window.FEATURE_NO_TITLE);
							alertDialogSetAlert.setContentView(dlView);

							LinearLayout li_delete = (LinearLayout) dlView
									.findViewById(R.id.li_delete);

							li_delete.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									alertDialogSetAlert.dismiss();
									strId = strId_index;
									strOrderbookId = strOrderbookId_index;
									// strSymbol = strSymbolName_index;
									strType = strType_index;
									FragmentChangeActivity.strSymbolSelect = strSymbol;
									sendRemoveSetAlert();
								}
							});

							alertDialogSetAlert.show();

							// set width height dialog
							DisplayMetrics displaymetrics = new DisplayMetrics();
							WindowManager windowManager = (WindowManager) context
									.getSystemService(Context.WINDOW_SERVICE);
							windowManager.getDefaultDisplay().getMetrics(
									displaymetrics);

							int ScreenHeight = displaymetrics.heightPixels;
							int ScreenWidth = displaymetrics.widthPixels;

							WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
							lp.gravity = Gravity.RIGHT;
							// lp.width = (int) (ScreenWidth * 0.87);
							// lp.height = (int) (ScreenHeight * 0.67);
							// alertDialogSetAlert.getWindow().setAttributes(lp);

							alertDialogSetAlert
									.getWindow()
									.setBackgroundDrawable(
											new ColorDrawable(
													android.graphics.Color.TRANSPARENT));

						}
					});

					li_list1.addView(viewSetAlert);
				}
			}

			// ---- list 2 -----
			if (jsaList2.length() > 0) {
				li_list_hit.setVisibility(View.VISIBLE);

				for (int i = 0; i < jsaList2.length(); i++) {
					JSONObject jsoIndex = jsaList2.getJSONObject(i);

					View viewSetAlert = ((Activity) context)
							.getLayoutInflater().inflate(
									R.layout.row_setalert_select_list2, null);

					TextView tv_symbol, tv_price1, tv_date, tv_time, tv_time_ago, tv_delete;
					ImageView img_type, img_manage, img_stoploss;

					tv_symbol = (TextView) viewSetAlert
							.findViewById(R.id.tv_symbol);
					tv_price1 = (TextView) viewSetAlert
							.findViewById(R.id.tv_price1);
					tv_date = (TextView) viewSetAlert
							.findViewById(R.id.tv_date);
					tv_time = (TextView) viewSetAlert
							.findViewById(R.id.tv_time);
					tv_time_ago = (TextView) viewSetAlert
							.findViewById(R.id.tv_time_ago);
					img_type = (ImageView) viewSetAlert
							.findViewById(R.id.img_type);
					img_manage = (ImageView) viewSetAlert
							.findViewById(R.id.img_manage);
					img_stoploss = (ImageView) viewSetAlert
							.findViewById(R.id.img_stoploss);
					tv_delete = (TextView) viewSetAlert
							.findViewById(R.id.tv_delete);

					// ----------
					final String strId_index = jsoIndex.getString("id");
					final String strOrderbookId_index = jsoIndex
							.getString("orderbook_id");
					final String strSymbolName_index = jsoIndex
							.getString("symbol_name");
					final String strType_index = jsoIndex.getString("type");
					final String strSymbol = jsoIndex.getString("symbol_name");
					String strIs_stoploss = jsoIndex.getString("is_stoploss");

					if (strIs_stoploss.equals("0")) {
						img_stoploss
								.setBackgroundResource(R.drawable.img_bidshit_takeprofit);
					} else {
						img_stoploss
								.setBackgroundResource(R.drawable.img_bidshit_stoploss);
					}

					tv_symbol.setText(jsoIndex.getString("symbol_name"));
					tv_price1.setText(jsoIndex.getString("price1"));

					tv_date.setText(DateTimeCreate.DateDmyThaiCreate(jsoIndex
							.getString("create_datetime")));
					tv_time.setText(DateTimeCreate.TimeCreate(jsoIndex
							.getString("create_datetime")));

					try {
						tv_time_ago.setText(DateTimeAgo
								.CalDifferentTimeAgoSetAlert(jsoIndex
										.getString("hit_datetime")));
					} catch (ParseException e) {
						e.printStackTrace();
					}

					if (jsoIndex.getString("type").equals("0")) {
						tv_price1.setTextColor(getResources().getColor(
								R.color.c_success));
						img_type.setBackgroundResource(R.drawable.icon_alert_up);
					} else if (jsoIndex.getString("type").equals("2")) {
						tv_price1.setTextColor(getResources().getColor(
								R.color.c_danger));
						img_type.setBackgroundResource(R.drawable.icon_alert_down);
					} else {
						tv_price1.setTextColor(getResources().getColor(
								R.color.c_content));
						img_type.setBackgroundResource(R.drawable.icon_alert_default);
					}

					tv_delete.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							strId = strId_index;
							strOrderbookId = strOrderbookId_index;
							// strSymbol = strSymbolName_index;
							strType = strType_index;
							FragmentChangeActivity.strSymbolSelect = strSymbol;
							sendRemoveSetAlert();
						}
					});
					img_manage.setOnClickListener(new OnClickListener() {
						@Override
						public void onClick(View v) {
							LayoutInflater layoutInflater = LayoutInflater
									.from(context);
							View dlView = layoutInflater.inflate(
									R.layout.dialog_setalert_row_select, null);
							final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
									context);
							alertDialogBuilder.setView(dlView);

							LinearLayout li_delete = (LinearLayout) dlView
									.findViewById(R.id.li_delete);

							li_delete.setOnClickListener(new OnClickListener() {
								@Override
								public void onClick(View v) {
									alertDialogSetAlert.dismiss();
									strId = strId_index;
									strOrderbookId = strOrderbookId_index;
									// strSymbol = strSymbolName_index;
									strType = strType_index;
									FragmentChangeActivity.strSymbolSelect = strSymbol;
									sendRemoveSetAlert();
								}
							});

							alertDialogSetAlert.show();

							// set width height dialog
							DisplayMetrics displaymetrics = new DisplayMetrics();
							WindowManager windowManager = (WindowManager) context
									.getSystemService(Context.WINDOW_SERVICE);
							windowManager.getDefaultDisplay().getMetrics(
									displaymetrics);

							int ScreenHeight = displaymetrics.heightPixels;
							int ScreenWidth = displaymetrics.widthPixels;

							WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
							lp.gravity = Gravity.RIGHT;
							// lp.width = (int) (ScreenWidth * 0.87);
							// lp.height = (int) (ScreenHeight * 0.67);
							// alertDialogSetAlert.getWindow().setAttributes(lp);

							alertDialogSetAlert
									.getWindow()
									.setBackgroundDrawable(
											new ColorDrawable(
													android.graphics.Color.TRANSPARENT));

						}
					});

					li_list2.addView(viewSetAlert);

					// // --------- set params pager ------
					// int sv_width = sv_list.getWidth();
					// int sv_height = sv_list.getHeight();
					// if (sv_height > 380) {
					// LinearLayout.LayoutParams layoutParams = new
					// LinearLayout.LayoutParams(
					// sv_width, 380);
					// sv_list.setLayoutParams(layoutParams);
					// }
				}
			}

			if ((jsaList1.length() + jsaList2.length()) > 4) {
				Log.v("(jsaList1 jsaList2 length", ""
						+ (jsaList1.length() + jsaList2.length()));

				// --------- set params pager ------
				int sv_width = sv_list.getWidth();
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
						sv_width, 500);
				sv_list.setLayoutParams(layoutParams);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	// ============== remove setalert ===============
	public void sendRemoveSetAlert() {

		setRemoveSetAlert resp = new setRemoveSetAlert();
		resp.execute();
	}

	public class setRemoveSetAlert extends AsyncTask<Void, Void, Void> {

		boolean connectionError = false;

		String temp = "";

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialogLoading.show();
		}

		@Override
		protected Void doInBackground(Void... params) {

			String url = SplashScreen.url_bidschart
					+ "/bidsMaster/removeSetAlertById";

			String json = "";
			InputStream inputStream = null;
			String result = "";

			try {
				HttpClient httpclient = new DefaultHttpClient();
				HttpPost httppost = new HttpPost(url);

				// 3. build jsonObject
				JSONObject jsonObject = new JSONObject();
				jsonObject.accumulate("id", strId);

				// jsonObject.accumulate("user_id",
				// FragmentChangeActivity.userModel.user_id);
				// jsonObject.accumulate("orderbook_id", strOrderbookId);
				// jsonObject.accumulate("symbol_name", strSymbolName);
				// jsonObject.accumulate("type", strType);

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

				// Log.v("XXXXX", "" +
				// FragmentChangeActivity.userModel.user_id+"_"+strOrderbookId+"_"+strSymbolName+"_"+strType);
				// Log.v("result addSetAlert : ", "" + result);

				// {"result":1,"data":{"id":"117","user_id":"104","type":"0","orderbook_id":"2099","hit_status":"0","symbol_name":"PTT","price1":"272","price2":"0","create_datetime":"2016-02-18 15:00:58","hit_datetime":"","first_hit_datetime":null,"update_datetime":"2016-02-18 15:00:58"}}
				// {"result":1,"data":{"id":"118","user_id":"104","type":"2","orderbook_id":"2099","hit_status":"0","symbol_name":"PTT","price1":"197","price2":"0","create_datetime":"2016-02-18 15:02:27","hit_datetime":"","first_hit_datetime":null,"update_datetime":"2016-02-18 15:02:27"}}

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
			PagerSetAlert.setALertNewLoad = true;
			loadDataRetrieveSetAlertList(); // load setalert add
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