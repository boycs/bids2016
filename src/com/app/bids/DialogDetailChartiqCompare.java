package com.app.bids;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.app.bids.R;
import com.app.bids.colorpicker.ColorPickerAdapter;

public class DialogDetailChartiqCompare {

	private static Dialog dialog;
	private static Context context;

	public DialogDetailChartiqCompare(Context context2) {
		this.context = context2;
		this.dialog = new Dialog(context2);

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
		dialog.setContentView(R.layout.dialog_chartiq_compare);

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
		lp.width = (int) (ScreenWidth * 0.7);
		lp.height = (int) (ScreenHeight * 0.85);
		dialog.getWindow().setAttributes(lp);
		dialog.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
	}

	public static void dissmiss() {
		dialog.dismiss();
	}

	// ========= dialog compare ========
	public static Dialog dialogChartIqCompareSearch; // search

	// private EditText dl_compare_et_search;
	public static TextView dl_tv_compare_search_select, dl_tv_compare_done;
	public static GridView gridViewColors;

	public static void initMenu() {
		gridViewColors = (GridView) dialog.findViewById(R.id.gridViewColors);

		final TextView dl_tv_color = (TextView) dialog
				.findViewById(R.id.dl_tv_color);

		dl_tv_compare_search_select = (TextView) dialog
				.findViewById(R.id.dl_tv_compare_search);
		dl_tv_compare_done = (TextView) dialog
				.findViewById(R.id.dl_tv_compare_done);
		dl_tv_compare_search_select.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialogChartIqCompareSearch(); // search symbol
			}
		});
		dl_tv_compare_done.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String strSymbolSearch = dl_tv_compare_search_select.getText()
						.toString().trim();
				String strColor = dl_tv_color.getTag().toString().trim();

				if ((!strSymbolSearch.equals("")) && (!strColor.equals(""))) {
					dialog.dismiss();
					FragmentChangeActivity.wv_chartiq
							.loadUrl("javascript:(function () { "
									+ "mobileControl.addCompare('"
									+ strSymbolSearch + "','rgb(" + strColor
									+ ")');" + "})()");
				} else {
				}
			}
		});

		gridViewColors.setAdapter(new ColorPickerAdapter(context));

		// close the dialog on item click
		gridViewColors.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final int i = position;
				Integer intColor = ColorPickerAdapter.colorList.get(i);
				String hexColor = "#"
						+ Integer.toHexString(intColor).substring(2);

				int red = Color.red(intColor);
				int green = Color.green(intColor);
				int blue = Color.blue(intColor);

				String rgbColor = red + "," + green + "," + blue;

				dl_tv_color.setBackgroundColor(Color.parseColor(hexColor));
				dl_tv_color.setTag(rgbColor);

				// mobileControl.addCompare('ptt','rgb(%.0f,%.0f,%.0f)')

				// FragmentChangeActivity.wv_chartiq.loadUrl("javascript:(function () { "
				// + "mobileControl.addCompare('"
				// + FragmentChangeActivity.strSymbolSelect + "','rgb("
				// + rgbColor + ")');" + "})()");
				// dialogChartIqCompare.dismiss();

			}
		});
	}

	// ------ ChartIq Compare Search
	public static ListView dl_list_compare_search;
	public static EditText dl_et_compare_search;
	public static TextView dl_tv_compare_search_common, dl_tv_compare_search_warrant,
			dl_tv_compare_search_dw, dl_tv_compare_search_close;

	public static String status_segmentId = "COMMON"; // COMMON, FOREIGN_STOCK,
														// WARRENT, DW
	public static String status_segmentIdDot = "EQUITY_INDEX";

	public static void showDialogChartIqCompareSearch() {

		LayoutInflater li = LayoutInflater.from(context);
		View dialog = li.inflate(R.layout.dialog_chartiq_compare_search, null);

		final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
				context);
		alertDialogBuilder.setView(dialog);

		dl_list_compare_search = (ListView) dialog
				.findViewById(R.id.dl_list_compare_search);
		dl_et_compare_search = (EditText) dialog
				.findViewById(R.id.dl_et_compare_search);
		dl_tv_compare_search_common = (TextView) dialog
				.findViewById(R.id.dl_tv_compare_search_common);
		dl_tv_compare_search_warrant = (TextView) dialog
				.findViewById(R.id.dl_tv_compare_search_warrant);
		dl_tv_compare_search_dw = (TextView) dialog
				.findViewById(R.id.dl_tv_compare_search_dw);
		dl_tv_compare_search_close = (TextView) dialog
				.findViewById(R.id.dl_tv_compare_search_close);

		final ChartIqCompateListAdapter ListAdapterSearch;
		final ArrayList<CatalogGetSymbol> original_list;
		final ArrayList<CatalogGetSymbol> second_list;

		original_list = new ArrayList<CatalogGetSymbol>();
		original_list.addAll(FragmentChangeActivity.list_getSymbol);
		second_list = new ArrayList<CatalogGetSymbol>();
		second_list.addAll(FragmentChangeActivity.list_getSymbol);

		ListAdapterSearch = new ChartIqCompateListAdapter(context, 0,
				second_list);
		dl_list_compare_search.setAdapter(ListAdapterSearch);

		dl_et_compare_search.addTextChangedListener(new TextWatcher() {
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
				String text = dl_et_compare_search.getText().toString();

				second_list.clear();
				for (int i = 0; i < original_list.size(); i++) {
					if (original_list.get(i).symbol.toLowerCase().contains(
							text.toString().toLowerCase())) {
						if (((original_list.get(i).status_segmentId)
								.equals(status_segmentId)) || ((original_list.get(i).status_segmentId)
										.equals(status_segmentIdDot))) {
							second_list.add(original_list.get(i));
						}
					}
				}
				ListAdapterSearch.notifyDataSetChanged();
			}
		});

		// -------------------------
		dl_tv_compare_search_common.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				status_segmentId = "COMMON"; // COMMON, FOREIGN_STOCK, WARRENT,
												// DW
				setTitleSearch();
				dl_tv_compare_search_common.setTextColor(context.getResources()
						.getColor(R.color.bg_default));
				dl_tv_compare_search_common
						.setBackgroundResource(R.drawable.border_search_activeleft);

				second_list.clear();
				for (int i = 0; i < original_list.size(); i++) {
					if (original_list.get(i).symbol.toLowerCase().contains(
							dl_et_compare_search.getText().toString()
									.toLowerCase())) {
						if (((original_list.get(i).status_segmentId)
								.equals(status_segmentId)) || ((original_list.get(i).status_segmentId)
										.equals(status_segmentIdDot))) {
							second_list.add(original_list.get(i));
						}
					}
				}
				ListAdapterSearch.notifyDataSetChanged();
			}
		});
		dl_tv_compare_search_warrant.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				status_segmentId = "WARRENT"; // COMMON, FOREIGN_STOCK, WARRENT,
												// DW
				setTitleSearch();
				dl_tv_compare_search_warrant.setTextColor(context.getResources()
						.getColor(R.color.bg_default));
				dl_tv_compare_search_warrant
						.setBackgroundResource(R.drawable.border_search_activecenter);

				second_list.clear();
				for (int i = 0; i < original_list.size(); i++) {
					if (original_list.get(i).symbol.toLowerCase().contains(
							dl_et_compare_search.getText().toString()
									.toLowerCase())) {
						if (((original_list.get(i).status_segmentId)
								.equals(status_segmentId)) || ((original_list.get(i).status_segmentId)
										.equals(status_segmentIdDot))) {
							second_list.add(original_list.get(i));
						}
					}
				}
				ListAdapterSearch.notifyDataSetChanged();
			}
		});
		dl_tv_compare_search_dw.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				status_segmentId = "DW"; // COMMON, FOREIGN_STOCK, WARRENT, DW
				setTitleSearch();
				dl_tv_compare_search_dw.setTextColor(context.getResources().getColor(
						R.color.bg_default));
				dl_tv_compare_search_dw
						.setBackgroundResource(R.drawable.border_search_activeright);

				second_list.clear();
				for (int i = 0; i < original_list.size(); i++) {
					if (original_list.get(i).symbol.toLowerCase().contains(
							dl_et_compare_search.getText().toString()
									.toLowerCase())) {
						if (((original_list.get(i).status_segmentId)
								.equals(status_segmentId)) || ((original_list.get(i).status_segmentId)
										.equals(status_segmentIdDot))) {
							second_list.add(original_list.get(i));
						}
					}
				}
				ListAdapterSearch.notifyDataSetChanged();
			}
		});

		// -------------------------
		dl_tv_compare_search_close.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialogChartIqCompareSearch.dismiss();
			}
		});

		// create alert dialog
		dialogChartIqCompareSearch = alertDialogBuilder.create();
		// show it
		dialogChartIqCompareSearch.show();

		// set width height dialog
		DisplayMetrics displaymetrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(displaymetrics);

		int ScreenHeight = displaymetrics.heightPixels;
		int ScreenWidth = displaymetrics.widthPixels;

		WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
		lp.width = (int) (ScreenWidth * 0.7);
		lp.height = (int) (ScreenHeight * 0.83);
		dialogChartIqCompareSearch.getWindow().setAttributes(lp);
		dialogChartIqCompareSearch.getWindow().setBackgroundDrawable(
				new ColorDrawable(android.graphics.Color.TRANSPARENT));
	}

	// ---- chartiq compare adapter
	public static class ChartIqCompateListAdapter extends ArrayAdapter {
		ArrayList<CatalogGetSymbol> arl;

		public ChartIqCompateListAdapter(Context context,
				int textViewResourceId, ArrayList<CatalogGetSymbol> arl) {
			super(context, textViewResourceId);
			this.arl = arl;
		}

		@Override
		public int getCount() {
			return arl.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			View v = convertView;
			if (v == null) {
				LayoutInflater vi;
				vi = LayoutInflater.from(getContext());
				v = vi.inflate(R.layout.row_chartiq_search_compare_symbol, null);
			}
			final TextView tv_symbol = (TextView) v
					.findViewById(R.id.tv_symbol);
			final TextView tv_market_id = (TextView) v
					.findViewById(R.id.tv_market_id);
			final TextView tv_symbol_fullname_eng = (TextView) v
					.findViewById(R.id.tv_symbol_fullname_eng);
			final LinearLayout li_row = (LinearLayout) v
					.findViewById(R.id.li_row);

			// text
			tv_symbol.setText("" + arl.get(position).symbol);
			tv_market_id.setText("" + arl.get(position).market_id);
			tv_symbol_fullname_eng.setText(""
					+ arl.get(position).symbol_fullname_eng);
			// tag
			tv_symbol.setTag("" + arl.get(position).symbol);
			tv_market_id.setTag("" + arl.get(position).market_id);
			tv_symbol_fullname_eng.setTag(""
					+ arl.get(position).symbol_fullname_eng);
			li_row.setTag("" + arl.get(position).symbol);

			final String symbolSelect = arl.get(position).symbol;

			li_row.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialogChartIqCompareSearch.dismiss();
					dl_tv_compare_search_select.setText(symbolSelect);
				}
			});

			return v;
		}
	}

	// ------- set title compare search ------
	public static void setTitleSearch() {
		dl_tv_compare_search_common.setTextColor(context.getResources().getColor(
				R.color.c_content));
		dl_tv_compare_search_warrant.setTextColor(context.getResources().getColor(
				R.color.c_content));
		dl_tv_compare_search_dw.setTextColor(context.getResources().getColor(
				R.color.c_content));
		dl_tv_compare_search_common.setBackgroundColor(Color.TRANSPARENT);
		dl_tv_compare_search_warrant.setBackgroundColor(Color.TRANSPARENT);
		dl_tv_compare_search_dw.setBackgroundColor(Color.TRANSPARENT);
	}

	protected static void switchFragment(PagerWatchList fragment) {
		if (context == null)
			return;
		if (context instanceof FragmentChangeActivity) {
			FragmentChangeActivity fca = (FragmentChangeActivity) context;
			fca.switchContent(fragment);
		}
	}

}