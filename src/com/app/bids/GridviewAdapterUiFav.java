package com.app.bids;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GridviewAdapterUiFav extends BaseAdapter {
	private ArrayList<CatalogGridFav> listUiGridFav;
	// private ArrayList<CatalogUiView> listChangePercent;
	// private ArrayList<CatalogUiView> listSymbol;
	// private ArrayList<CatalogUiView> listLastTrade;
	private Activity activity;
	private Boolean isLongClick = false;

	public GridviewAdapterUiFav(Activity activity,
			ArrayList<CatalogGridFav> listUiGridFav, boolean isLongClick) {
		super();
		this.listUiGridFav = listUiGridFav;
		// this.listChangePercent = listChangePercent;
		// this.listSymbol = listSymbol;
		// this.listLastTrade = listLastTrade;
		this.activity = activity;
		this.isLongClick = isLongClick;
	}

	public Boolean getLongClickState() {
		return isLongClick;
	}

	public void setLongClickState(boolean state) {
		this.isLongClick = state;
		notifyDataSetChanged();
	}

	public int getCount() {
		return listUiGridFav.size();
	}

	public CatalogGridFav getItem(int position) {
		return listUiGridFav.get(position);
	}

	public long getItemId(int position) {
		return 0;
	}

	@Override
	public int getItemViewType(int position) {
		if (listUiGridFav.get(position).grid_empty) {
			return 0;
		} else {
			return 1;
		}
	}

	public static class ViewHolder {
		public TextView txtChange;
		public TextView txtChangePercent;
		public TextView txtSymbol;
		public TextView txtLastTrade;
		public LinearLayout liGrid;
		public ImageView imgDel;
	}

	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflator = activity.getLayoutInflater();
		ViewHolder view = null;
		final CatalogGridFav cgf = listUiGridFav.get(position);

		if (getItemViewType(position) == 1) {
			if (convertView == null) {
				view = new ViewHolder();
//				convertView = inflator.inflate(R.layout.index_gridview_fav,
//						null);
//				view.txtChange = (TextView) convertView
//						.findViewById(R.id.tv_change);
//				view.txtChangePercent = (TextView) convertView
//						.findViewById(R.id.tv_change_percent);
//				view.txtSymbol = (TextView) convertView
//						.findViewById(R.id.tv_symbol);
//				view.txtLastTrade = (TextView) convertView
//						.findViewById(R.id.tv_last_trade);
//				view.liGrid = (LinearLayout) convertView
//						.findViewById(R.id.li_index_gridview_uiview);
//				view.imgDel = (ImageView) convertView
//						.findViewById(R.id.img_del);

				convertView.setTag(view);

			} else {
				view = (ViewHolder) convertView.getTag();
			}

			if (isLongClick == true) {
				view.imgDel.setVisibility(View.VISIBLE);
			} else {
				view.imgDel.setVisibility(View.GONE);
			}
			
			view.txtChange.setText(cgf.change);
			view.txtChangePercent.setText(cgf.percentChange);
			view.txtSymbol.setText(cgf.favorite_symbol);
			view.txtLastTrade.setText(cgf.last_trade);
			view.imgDel.setTag(""+cgf.favorite_symbol);
			if (cgf.grid_color != -1) {
				view.liGrid.setBackgroundColor(cgf.grid_color);
				view.txtChange.setTextColor(cgf.grid_color);
			} else {
//				view.liGrid.setBackgroundResource(R.drawable.border_grid_empty);
			}
			
			// select index grid for delete
			final ViewHolder v2 = view;
			final String roundPosition = ""+position;
			view.imgDel.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
//					if (Fragment1UiView.listSelectDelSym.contains(roundPosition)) {
//						v2.imgDel.setBackgroundResource(R.drawable.img_true_n);
//						Fragment1UiView.listSelectDelSym.remove(""+roundPosition);
//					} else {
//						Fragment1UiView.listSelectDelSym.add("" +roundPosition);
//						v2.imgDel.setBackgroundResource(R.drawable.img_true_y);
//					}
//					Fragment1UiView.setTitleDel(); // set name title
				}
			});

		} else {
			// grid empty
//			convertView = inflator.inflate(R.layout.index_gridview_empty, null);
//			LinearLayout liDel2 = (LinearLayout) convertView
//					.findViewById(R.id.li_del);
//			if (isLongClick == true) {
//				liDel2.setVisibility(View.VISIBLE);
//			} else {
//				liDel2.setVisibility(View.GONE);
//			}
		}

		return convertView;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}
}