package com.app.bids;

import org.json.JSONException;
import com.app.bids.R;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MyFragment extends Fragment {

	public static Fragment newInstance(Context context, int pos, float scale) {
		Bundle b = new Bundle();
		b.putInt("pos", pos);
		b.putFloat("scale", scale);
		return Fragment.instantiate(context, MyFragment.class.getName(), b);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (container == null) {
			return null;
		}
		
		LinearLayout l = (LinearLayout) inflater.inflate(
				R.layout.row_news_carousel, container, false);

		final int pos = this.getArguments().getInt("pos");

		final RelativeLayout rl_carousel = (RelativeLayout) l
				.findViewById(R.id.content);

		final TextView tv_symbol = (TextView) l.findViewById(R.id.tv_symbol);
		final TextView tv_symbol_fullname_eng = (TextView) l
				.findViewById(R.id.tv_symbol_fullname_eng);
		final TextView tv_last_trade = (TextView) l
				.findViewById(R.id.tv_last_trade);

		final TextView tv_volume = (TextView) l.findViewById(R.id.tv_volume);
		final TextView tv_change = (TextView) l.findViewById(R.id.tv_change);
		final TextView tv_percentChange = (TextView) l
				.findViewById(R.id.tv_percentChange);

		final TextView tv_count_facebook = (TextView) l
				.findViewById(R.id.tv_count_facebook);
		final TextView tv_count_pantip = (TextView) l
				.findViewById(R.id.tv_count_pantip);
		final TextView tv_count_news = (TextView) l
				.findViewById(R.id.tv_count_news);

		final TextView marque_scrolling_text = (TextView) l
				.findViewById(R.id.marque_scrolling_text);

		for (int i = 0; i < AttributeBegin.contentGetHotSymbol.length(); i++) {
			if (pos == i) {
				try {
					tv_symbol.setText(AttributeBegin.contentGetHotSymbol
							.getJSONObject(i).get("symbol").toString());
					tv_symbol_fullname_eng
							.setText(AttributeBegin.contentGetHotSymbol
									.getJSONObject(i)
									.get("symbol_fullname_eng").toString());
					tv_last_trade.setText(AttributeBegin.contentGetHotSymbol
							.getJSONObject(i).get("last_trade").toString());

					tv_volume.setText(AttributeBegin.contentGetHotSymbol
							.getJSONObject(i).get("volume").toString());
					tv_change.setText(AttributeBegin.contentGetHotSymbol
							.getJSONObject(i).get("change").toString());
					tv_percentChange.setText(AttributeBegin.contentGetHotSymbol
							.getJSONObject(i).get("percentChange").toString());

					tv_count_facebook.setText(AttributeBegin.contentGetHotSymbol
							.getJSONObject(i).get("count_facebook").toString());
					tv_count_pantip.setText(AttributeBegin.contentGetHotSymbol
							.getJSONObject(i).get("count_pantip").toString());
					tv_count_news.setText(AttributeBegin.contentGetHotSymbol
							.getJSONObject(i).get("count_news").toString());

					String strSliding = "";
					if (!(AttributeBegin.contentGetHotSymbol.getJSONObject(i)
							.get("title").toString().equals(""))) {
						strSliding += "  #"
								+ AttributeBegin.contentGetHotSymbol
										.getJSONObject(i).get("title")
										.toString();
					}
					if (!(AttributeBegin.contentGetHotSymbol.getJSONObject(i)
							.get("text1").toString().equals(""))) {
						strSliding += "  #"
								+ AttributeBegin.contentGetHotSymbol
										.getJSONObject(i).get("text1")
										.toString();
					}
					if (!(AttributeBegin.contentGetHotSymbol.getJSONObject(i)
							.get("text2").toString().equals(""))) {
						strSliding += "  #"
								+ AttributeBegin.contentGetHotSymbol
										.getJSONObject(i).get("text2")
										.toString();
					}
					if (!(AttributeBegin.contentGetHotSymbol.getJSONObject(i)
							.get("text3").toString().equals(""))) {
						strSliding += "  #"
								+ AttributeBegin.contentGetHotSymbol
										.getJSONObject(i).get("text3")
										.toString();
					}
					
					marque_scrolling_text.setText(strSliding);
					marque_scrolling_text.setSelected(true);

				} catch (JSONException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}

		rl_carousel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				// FragmentActivity.LOTBANK = 1;
				//
				// FragmentActivity.SET_MENU_SELECT = "news";
				// FragmentActivity.SET_SELECT_NEWS_LOT = "lot_select";
				//
				// FragmentActivity.handlerShowMenu.sendEmptyMessage(0); // show
				// tab bottom menu
				//
				// FragmentActivity.tdedIdSelect =
				// rl_carousel.getTag().toString();
				//
				// FragmentActivity.SET_LOT_MAIN_SELECT = true;
				//
				// FragmentActivity.tdedIdSelect =
				// rl_carousel.getTag().toString();
				//
				// FragmentActivity.activeNewsLot = 2;
				// FragmentActivity.selectLinearNewsLot = 3;
				//
				// // fragment page --> ContentCheck for check
				// switchFragment(new ContentNewsLotList());
			}
		});

		MyLinearLayout root = (MyLinearLayout) l.findViewById(R.id.root);
		float scale = this.getArguments().getFloat("scale");
		root.setScaleBoth(scale);

		return l;
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
