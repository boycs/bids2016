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

public class DialogDetailChartiqSytle {

	private static Dialog dialog;
	private static Context context;

	public DialogDetailChartiqSytle(Context context2) {
		this.context = context2;
		this.dialog = new Dialog(context2);

		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE); // before
		dialog.setContentView(R.layout.dialog_chartiq_style);

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
		final TextView tv_chartstyle, tv_charttype, tv_chartscale;
		final LinearLayout li_chartstyle, li_charttype, li_chartscale;
		tv_chartstyle = (TextView) dialog.findViewById(R.id.tv_chartstyle);
		tv_charttype = (TextView) dialog.findViewById(R.id.tv_charttype);
		tv_chartscale = (TextView) dialog.findViewById(R.id.tv_chartscale);

		li_chartstyle = (LinearLayout) dialog.findViewById(R.id.li_chartstyle);
		li_charttype = (LinearLayout) dialog.findViewById(R.id.li_charttype);
		li_chartscale = (LinearLayout) dialog.findViewById(R.id.li_chartscale);

		tv_chartstyle.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tv_chartstyle.setTextColor(context.getResources().getColor(
						R.color.c_content));
				tv_charttype.setTextColor(context.getResources().getColor(
						R.color.c_title));
				tv_chartscale.setTextColor(context.getResources().getColor(
						R.color.c_title));

				li_chartstyle.setVisibility(View.VISIBLE);
				li_charttype.setVisibility(View.GONE);
				li_chartscale.setVisibility(View.GONE);
			}
		});
		tv_charttype.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tv_chartstyle.setTextColor(context.getResources().getColor(
						R.color.c_title));
				tv_charttype.setTextColor(context.getResources().getColor(
						R.color.c_content));
				tv_chartscale.setTextColor(context.getResources().getColor(
						R.color.c_title));

				li_chartstyle.setVisibility(View.GONE);
				li_charttype.setVisibility(View.VISIBLE);
				li_chartscale.setVisibility(View.GONE);
			}
		});
		tv_chartscale.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				tv_chartstyle.setTextColor(context.getResources().getColor(
						R.color.c_title));
				tv_charttype.setTextColor(context.getResources().getColor(
						R.color.c_title));
				tv_chartscale.setTextColor(context.getResources().getColor(
						R.color.c_content));

				li_chartstyle.setVisibility(View.GONE);
				li_charttype.setVisibility(View.GONE);
				li_chartscale.setVisibility(View.VISIBLE);
			}
		});

		// style
		((LinearLayout) dialog.findViewById(R.id.li_candle))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) dialog.findViewById(R.id.li_bar))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) dialog.findViewById(R.id.li_coloredbar))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) dialog.findViewById(R.id.li_line))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) dialog.findViewById(R.id.li_hollowcandle))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) dialog.findViewById(R.id.li_mountain))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) dialog.findViewById(R.id.li_coloredline))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) dialog.findViewById(R.id.li_baseelinedelta))
				.setOnClickListener(onClickListenerChartIqStyle);
		// type
		((LinearLayout) dialog.findViewById(R.id.li_heikinashi))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) dialog.findViewById(R.id.li_kagi))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) dialog.findViewById(R.id.li_linebreak))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) dialog.findViewById(R.id.li_pointfigure))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) dialog.findViewById(R.id.li_rangebars))
				.setOnClickListener(onClickListenerChartIqStyle);
		((LinearLayout) dialog.findViewById(R.id.li_renko))
				.setOnClickListener(onClickListenerChartIqStyle);
	}

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