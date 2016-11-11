package com.app.bids;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.app.bids.R;

public class DialogDetailChartiqDraw {

	private static Dialog dialog;
	private static Context context;

	public DialogDetailChartiqDraw(Context context2) {
		this.context = context2;
		this.dialog = new Dialog(context2);

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
		dialog.setContentView(R.layout.dialog_chartiq_draw);

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

	public static void initMenu() {
		// -------- coll 1 -------
		((TextView) dialog.findViewById(R.id.tv_segment))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) dialog.findViewById(R.id.tv_line))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) dialog.findViewById(R.id.tv_measure))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) dialog.findViewById(R.id.tv_fibonacci))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) dialog.findViewById(R.id.tv_rectangle))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) dialog.findViewById(R.id.tv_annotation))
				.setOnClickListener(onClickListenerChartIqDraw);

		// -------- coll 2 -------
		((TextView) dialog.findViewById(R.id.tv_continuous))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) dialog.findViewById(R.id.tv_horizontal))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) dialog.findViewById(R.id.tv_channel))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) dialog.findViewById(R.id.tv_gartley))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) dialog.findViewById(R.id.tv_ellipse))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) dialog.findViewById(R.id.tv_callout))
				.setOnClickListener(onClickListenerChartIqDraw);

		// -------- coll 3 -------
		((TextView) dialog.findViewById(R.id.tv_ray))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) dialog.findViewById(R.id.tv_vertical))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) dialog.findViewById(R.id.tv_freeform))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) dialog.findViewById(R.id.tv_pitchfork))
				.setOnClickListener(onClickListenerChartIqDraw);
		((TextView) dialog.findViewById(R.id.tv_shape))
				.setOnClickListener(onClickListenerChartIqDraw);

		((TextView) dialog.findViewById(R.id.tv_clear))
				.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						FragmentChangeActivity.wv_chartiq
								.loadUrl("javascript:(function () { "
										+ "mobileControl.clearDrawing();"
										+ "})()");
						dialog.dismiss();
					}
				});
	}

	// -------- on click Draw -----------
	public static OnClickListener onClickListenerChartIqDraw = new OnClickListener() {
		@Override
		public void onClick(final View v) {

			String tag = v.getTag().toString();
			// Toast.makeText(context, "" + tag,
			// Toast.LENGTH_SHORT).show();

			FragmentChangeActivity.wv_chartiq
					.loadUrl("javascript:(function () { "
							+ "mobileControl.setDrawingType('" + tag + "');"
							+ "})()");
			dialog.dismiss();

		}
	};

	// -------- on click style -----------
	public static OnClickListener onClickListenerChartIqStyle = new OnClickListener() {
		@Override
		public void onClick(final View v) {

			String tag = v.getTag().toString();

			FragmentChangeActivity.wv_chartiq
					.loadUrl("javascript:(function () { "
							+ "mobileControl.changeChartType('" + tag + "');"
							+ "})()");
			dialog.dismiss();

		}
	};

	protected static void switchFragment(PagerWatchList fragment) {
		if (context == null)
			return;
		if (context instanceof FragmentChangeActivity) {
			FragmentChangeActivity fca = (FragmentChangeActivity) context;
			fca.switchContent(fragment);
		}
	}

}