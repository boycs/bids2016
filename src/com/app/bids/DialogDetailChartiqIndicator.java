package com.app.bids;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.drawable.ColorDrawable;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.bids.R;

public class DialogDetailChartiqIndicator {

	private static Dialog dialog;
	private static Context context;

	public DialogDetailChartiqIndicator(Context context2) {
		this.context = context2;
		this.dialog = new Dialog(context2);

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
		dialog.setContentView(R.layout.dialog_chartiq_indicator_list);

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

	// =========init view ========
	public static ArrayList<ChartIqGetIndicatorValueCatalog> list_getIndicatorValue = new ArrayList<ChartIqGetIndicatorValueCatalog>();
	public static ArrayList<TextView> row_indicator_title;

	public static void initMenu() {
		try {
			final EditText et_search = (EditText) dialog
					.findViewById(R.id.et_search);

			LinearLayout li_list_title = (LinearLayout) dialog
					.findViewById(R.id.li_list_title);
			final LinearLayout li_list_value = (LinearLayout) dialog
					.findViewById(R.id.li_list_value);
			li_list_title.removeAllViews();

			row_indicator_title = new ArrayList<TextView>();
			row_indicator_title.clear();

			for (int i = 0; i < PagerWatchListDetailChart.jsonIndicator
					.length(); i++) {
				View view2 = ((Activity) context).getLayoutInflater().inflate(
						R.layout.row_dialog_studies, null);

				TextView tv_row = (TextView) view2.findViewById(R.id.tv_row);
				// row_color_list_select.add(tv_row);

				JSONObject jsoIndex = PagerWatchListDetailChart.jsonIndicator
						.getJSONObject(i);
				final JSONArray jsaValue = jsoIndex.getJSONArray("value");

				tv_row.setText("" + jsoIndex.getString("title") + " ("
						+ jsaValue.length() + ")");
				tv_row.setTag("" + jsoIndex.getString("key"));

				// list value begin
				if (i == 0) {
					li_list_value.removeAllViews();
					for (int j = 0; j < jsaValue.length(); j++) {

						View viewValue = ((Activity) context)
								.getLayoutInflater().inflate(
										R.layout.row_dialog_studies, null);

						final TextView tv_row_value = (TextView) viewValue
								.findViewById(R.id.tv_row);

						tv_row_value.setText(""
								+ jsaValue.getJSONObject(j).getString("title"));
						tv_row_value.setTag(""
								+ jsaValue.getJSONObject(j).getString("key"));
						tv_row_value.setOnClickListener(new OnClickListener() {
							@Override
							public void onClick(View v) {
								FragmentChangeActivity.wv_chartiq
										.loadUrl("javascript:(function () { "
												+ "mobileControl.addStudy('"
												+ tv_row_value.getTag() + "');"
												+ "})()");
								dialog.dismiss();
							}
						});
						li_list_value.addView(viewValue);
					}
				}

				final int num_row = i;
				tv_row.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						changeRowIndicatorTitle(v, num_row);
						li_list_value.removeAllViews();
						try {
							for (int j = 0; j < jsaValue.length(); j++) {
								View view2 = ((Activity) context)
										.getLayoutInflater().inflate(
												R.layout.row_dialog_studies,
												null);

								final TextView tv_row_value = (TextView) view2
										.findViewById(R.id.tv_row);

								tv_row_value.setText(""
										+ jsaValue.getJSONObject(j).getString(
												"title"));
								tv_row_value.setTag(""
										+ jsaValue.getJSONObject(j).getString(
												"key"));
								tv_row_value
										.setOnClickListener(new OnClickListener() {
											@Override
											public void onClick(View v) {
												FragmentChangeActivity.wv_chartiq.loadUrl("javascript:(function () { "
														+ "mobileControl.addStudy('"
														+ tv_row_value.getTag()
														+ "');" + "})()");
												dialog.dismiss();
											}
										});
								li_list_value.addView(view2);
							}
						} catch (JSONException e2) {
							e2.printStackTrace();
						}
					}
				});

				row_indicator_title.add(tv_row);

				li_list_title.addView(view2);
			}

			// ********* search studiea value *********
			final ArrayList<ChartIqGetIndicatorValueCatalog> original_list;
			final ArrayList<ChartIqGetIndicatorValueCatalog> second_list;

			original_list = new ArrayList<ChartIqGetIndicatorValueCatalog>();
			original_list.addAll(list_getIndicatorValue);
			second_list = new ArrayList<ChartIqGetIndicatorValueCatalog>();
			second_list.addAll(list_getIndicatorValue);
			
			et_search.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					String text = et_search.getText().toString();
					if (text.length() > 0) {
						second_list.clear();
						for (int i = 0; i < original_list.size(); i++) {
							if (original_list.get(i).title.toLowerCase()
									.contains(text.toString().toLowerCase())) {
								second_list.add(original_list.get(i));
							}
						}
						li_list_value.removeAllViews();
						for (int j = 0; j < second_list.size(); j++) {
							View viewValue = ((Activity) context)
									.getLayoutInflater().inflate(
											R.layout.row_dialog_studies, null);

							final TextView tv_row_value = (TextView) viewValue
									.findViewById(R.id.tv_row);

							tv_row_value.setText(""
									+ second_list.get(j).getTitle());
							tv_row_value.setTag(""
									+ second_list.get(j).getKey());
							tv_row_value
									.setOnClickListener(new OnClickListener() {
										@Override
										public void onClick(View v) {
											FragmentChangeActivity.wv_chartiq
													.loadUrl("javascript:(function () { "
															+ "mobileControl.addStudy('"
															+ tv_row_value
																	.getTag()
															+ "');" + "})()");
											dialog.dismiss();
										}
									});
							li_list_value.addView(viewValue);
						}
					} else {
					}
				}
			});
		} catch (JSONException e2) {
			e2.printStackTrace();
		}
	}

	public static void changeRowIndicatorTitle(View v, int num_row) {
		for (int i = 0; i < row_indicator_title.size(); i++) {
			row_indicator_title.get(i).setTextColor(
					context.getResources().getColor(R.color.c_title));
		}
		row_indicator_title.get(num_row).setTextColor(
				context.getResources().getColor(R.color.c_content));
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