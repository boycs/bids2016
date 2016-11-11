package com.app.bids;

import java.text.ParseException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
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

public class GridviewAdapterNews extends BaseAdapter {
	private ArrayList<CatalogNews> listGridNews;
	// private ArrayList<CatalogUiView> listChangePercent;
	// private ArrayList<CatalogUiView> listSymbol;
	// private ArrayList<CatalogUiView> listLastTrade;
	private Activity activity;
	private Boolean isLongClick = false;

	public GridviewAdapterNews(Activity activity,
			ArrayList<CatalogNews> list_getNews, boolean isLongClick) {
		super();
		this.listGridNews = list_getNews;
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
		return listGridNews.size();
	}

	public long getItemId(int position) {
		return 0;
	}

	@Override
	public int getItemViewType(int position) {
		return 0;
	}

//	public static class ViewHolder {
//		public TextView tv_article_keyword;
//		public TextView tv_article_title;
//		public TextView tv_created_at;
//		public TextView tv_views_count;
//		public TextView tv_likes_count;
//		public TextView tv_comments_count;
//		public LinearLayout li_content;
//	}

	public View getView(int position, View convertView, ViewGroup parent) {
		
		TextView tv_symbol,tv_article_title,tv_created_at,tv_views_count,tv_likes_count,tv_comments_count;
		final LinearLayout li_content;
		
		LayoutInflater inflator = activity.getLayoutInflater();
		final CatalogNews cgn = listGridNews.get(position);

		convertView = inflator.inflate(R.layout.row_news_grid, null);
		tv_symbol = (TextView) convertView
				.findViewById(R.id.tv_symbol);
		tv_article_title = (TextView) convertView
				.findViewById(R.id.tv_article_title);
		tv_created_at = (TextView) convertView
				.findViewById(R.id.tv_created_at);
		tv_views_count = (TextView) convertView
				.findViewById(R.id.tv_views_count);
		tv_likes_count = (TextView) convertView
				.findViewById(R.id.tv_likes_count);
		tv_comments_count = (TextView) convertView
				.findViewById(R.id.tv_comments_count);
		li_content = (LinearLayout) convertView
				.findViewById(R.id.li_content);

		tv_symbol.setText(cgn.symbol.toString());
		tv_article_title.setText(cgn.article_title.toString());
//		tv_created_at.setText(cgn.created_at.toString());
		tv_views_count.setText(cgn.views_count.toString());
		tv_likes_count.setText(cgn.likes_count.toString());
		tv_comments_count.setText(cgn.comments_count.toString());
		
		// 1 เดือน 2015-10-01
		try {
			String strDateAgo = ""
					+ DateTimeAgo.CalAgoTime2(cgn.created_at.toString());
			String strDateYmd = "" + (cgn.created_at.toString()).split(" ")[0];
			tv_created_at.setText(strDateAgo + "  " + strDateYmd);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if ((cgn.content.toString())
				.equals("http://service.bidschart.com/undefined")) {
		} else {
			FragmentChangeActivity.imageLoader.loadImage(""
					+ cgn.content.toString(),
					FragmentChangeActivity.options,
					new SimpleImageLoadingListener() {
						@Override
						public void onLoadingComplete(String imageUri,
								View view, Bitmap loadedImage) {
							BitmapDrawable background = new BitmapDrawable(
									loadedImage);
							li_content.setBackgroundDrawable(background);
							super.onLoadingComplete(imageUri, view,
									loadedImage);
						}
					});
		}

		// if (getItemViewType(position) == 1) {
		// if (convertView == null) {
		// view = new ViewHolder();
		// convertView = inflator.inflate(R.layout.row_news,
		// null);
		// view.tv_article_keyword = (TextView) convertView
		// .findViewById(R.id.tv_article_keyword);
		// view.tv_article_title = (TextView) convertView
		// .findViewById(R.id.tv_article_title);
		// view.tv_created_at = (TextView) convertView
		// .findViewById(R.id.tv_created_at);
		// view.tv_views_count = (TextView) convertView
		// .findViewById(R.id.tv_views_count);
		// view.tv_likes_count = (TextView) convertView
		// .findViewById(R.id.tv_likes_count);
		// view.tv_comments_count = (TextView) convertView
		// .findViewById(R.id.tv_comments_count);
		//
		// convertView.setTag(view);
		//
		// } else {
		// view = (ViewHolder) convertView.getTag();
		// }
		//
		// // view.tv_article_keyword.setText("00");
		// // view.tv_article_title.setText("11");
		// // view.tv_created_at.setText("22");
		// // view.tv_views_count.setText("33");
		// // view.tv_likes_count.setText("44");
		// // view.tv_comments_count.setText("55");
		//
		// try {
		// view.tv_article_keyword.setText(listGridNews.getJSONObject(position).getString("article_keyword"));
		// view.tv_article_title.setText(listGridNews.getJSONObject(position).getString("article_title"));
		// view.tv_created_at.setText(listGridNews.getJSONObject(position).getString("created_at"));
		// view.tv_views_count.setText(listGridNews.getJSONObject(position).getString("views_count"));
		// view.tv_likes_count.setText(""+listGridNews.getJSONObject(position).getString("likes_count"));
		// view.tv_comments_count.setText(""+listGridNews.getJSONObject(position).getString("comments_count"));
		//
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		//
		// } else {
		// convertView = inflator.inflate(R.layout.row_news, null);
		// }

		return convertView;
	}

	@Override
	public int getViewTypeCount() {
		// TODO Auto-generated method stub
		return 2;
	}

	@Override
	public Object getItem(int position) {
		return listGridNews.get(position);
	}
}