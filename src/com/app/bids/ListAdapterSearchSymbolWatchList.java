package com.app.bids;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.app.bids.R;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ListAdapterSearchSymbolWatchList extends ArrayAdapter {
	ArrayList<CatalogGetSymbol> arl;

	public static PagerWatchList pagerWatchList;
	
	public ListAdapterSearchSymbolWatchList(Context context,
			int textViewResourceId, ArrayList<CatalogGetSymbol> arl) {
		super(context, textViewResourceId);
		this.arl = arl;
		
		pagerWatchList = new PagerWatchList();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arl.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View v = convertView;
		if (v == null) {
			LayoutInflater vi;
			vi = LayoutInflater.from(getContext());
			v = vi.inflate(R.layout.row_watchlist_search_symbol, null);
		}
		final TextView tv_symbol = (TextView) v.findViewById(R.id.tv_symbol);
		final TextView tv_market_id = (TextView) v
				.findViewById(R.id.tv_market_id);
		final TextView tv_symbol_fullname_eng = (TextView) v
				.findViewById(R.id.tv_symbol_fullname_eng);
		final ImageView img_add_symbol = (ImageView) v
				.findViewById(R.id.img_add_symbol);
		final LinearLayout li_row = (LinearLayout) v.findViewById(R.id.li_row);

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
		img_add_symbol.setTag("" + arl.get(position).symbol);
		li_row.setTag("" + arl.get(position).symbol);

		final String symbolSelect = arl.get(position).symbol;
		img_add_symbol.setBackgroundResource(FollowSymbol
				.setColorFollowSearchSymbol(symbolSelect));

		img_add_symbol.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				FragmentChangeActivity.strSymbolSelect = symbolSelect;
				boolean ckFollow = FollowSymbol
						.checkFollowSymbol(FragmentChangeActivity.strSymbolSelect);

//				Log.v("FragmentChangeActivity.strFavoriteNumber",""+FragmentChangeActivity.strFavoriteNumber);
				
				if (ckFollow) { // unfollow
					img_add_symbol
							.setBackgroundResource(R.drawable.icon_plus_blue);
					FollowSymbol.sendSymbolRemoveFavorite(); // send remove favorite
//					pagerWatchList.sendRemoveFavorite();
				} else {
					if (FollowSymbol
							.checkFollowCount(FragmentChangeActivity.strFavoriteNumber)) {
						img_add_symbol
								.setBackgroundResource(R.drawable.icon_check_green);
						FollowSymbol.sendSymbolAddFavorite(); // send add favorite
//						sendAddFavorite();
					} else {
						Toast.makeText(
								getContext(),
								"Over Limit Favorite "
										+ FragmentChangeActivity.strFavoriteNumber,
								0).show();
					}
				}
			}
		});

		li_row.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// ----- hide list search --------
				PagerWatchList.hideListSearch();
				// ----- startActivity detail symbol
				FragmentChangeActivity.strSymbolSelect = symbolSelect;
				FragmentChangeActivity.startUiDetail();
			}
		});

		return v;
	}

//	 ============== send add favorite ===============
//	public static void sendAddFavorite() {
//		setFavorite resp = new setFavorite();
//		resp.execute();
//	}
//
//	public static class setFavorite extends AsyncTask<Void, Void, Void> {
//
//		boolean connectionError = false;
//
//		String temp = "";
//
//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//			// progress.show();
//
//		}
//
//		@Override
//		protected Void doInBackground(Void... params) {
//
//			String url = SplashScreen.url_bidschart + "/service/addFavorite";
//
//			String json = "";
//			InputStream inputStream = null;
//			String result = "";
//
//			try {
//				HttpClient httpclient = new DefaultHttpClient();
//				HttpPost httppost = new HttpPost(url);
//
//				// 3. build jsonObject
//				JSONObject jsonObject = new JSONObject();
//				jsonObject.accumulate("favorite_number",
//						FragmentChangeActivity.strFavoriteNumber);
//				jsonObject.accumulate("favorite_symbol",
//						FragmentChangeActivity.strSymbolSelect);
//				jsonObject
//						.accumulate("user_id", SplashScreen.userModel.user_id);
//
//				// 4. convert JSONObject to JSON to String
//				json = jsonObject.toString();
//
//				// 5. set json to StringEntity
//				StringEntity se = new StringEntity(json, "UTF-8");
//
//				// 6. set httpPost Entity
//				httppost.setEntity(se);
//
//				// 7. Set some headers to inform server about the type of the
//				// content
//				httppost.setHeader("Accept", "application/json");
//				httppost.setHeader("Content-type", "application/json");
//
//				// 8. Execute POST request to the given URL
//				HttpResponse httpResponse = httpclient.execute(httppost);
//
//				// 9. receive response as inputStream
//				inputStream = httpResponse.getEntity().getContent();
//
//				// 10. convert inputstream to string
//				if (inputStream != null)
//					result = AFunctionOther.convertInputStreamToString(inputStream);
//				else
//					result = "Did not work!";
//
////				Log.v("sendAddFavorite", ""+result);
//
//			} catch (IOException e) {
//				connectionError = true;
//				e.printStackTrace();
//			} catch (RuntimeException e) {
//				connectionError = true;
//				e.printStackTrace();
//			} catch (JSONException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void result) {
//			super.onPostExecute(result);
//			pagerWatchList.getDataFavorite();
//			// updateDataFavorite();
//		}
//	}

//	// ============== update favorite =============
//	public static void updateDataFavorite() {
//		getUpdateFavorite resp = new getUpdateFavorite();
//		resp.execute();
//	}
//
//	public static class getUpdateFavorite extends AsyncTask<Void, Void, Void> {
//		boolean connectionError = false;
//		// ======= json ========
//		private JSONObject jsonFav;
//
//		@Override
//		protected void onPreExecute() {
//			super.onPreExecute();
//			// progress.show();
//		}
//
//		@Override
//		protected Void doInBackground(Void... params) {
//
//			java.util.Date date = new java.util.Date();
//			long timestamp = date.getTime();
//			// ======= url ========
//			// http://www.bidschart.com/service/v2/symbolFavorite?user_id=104
//			String url_fav = SplashScreen.url_bidschart
//					+ "/service/v2/symbolFavorite?user_id="
//					+ SplashScreen.userModel.user_id + "&timestamp="
//					+ timestamp;
//
//			Log.v("updateDataFavorite Adapter", "" + url_fav);
//
//			try {
//				jsonFav = ReadJson.readJsonObjectFromUrl(url_fav);
//			} catch (IOException e1) {
//				connectionError = true;
//				jsonFav = null;
//				e1.printStackTrace();
//			} catch (JSONException e1) {
//				connectionError = true;
//				jsonFav = null;
//				e1.printStackTrace();
//			} catch (RuntimeException e) {
//				connectionError = true;
//				jsonFav = null;
//				e.printStackTrace();
//			}
//			return null;
//		}
//
//		@Override
//		protected void onPostExecute(Void result) {
//			super.onPostExecute(result);
//
//			if (connectionError == false) {
//				if (jsonFav != null) {
//					try {
//						FragmentChangeActivity.contentGetSymbolFavorite = jsonFav
//								.getJSONArray("dataAll");
//
//						for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite
//								.length(); i++) {
//							JSONObject jsoIndex = FragmentChangeActivity.contentGetSymbolFavorite
//									.optJSONObject(i);
//
//							String strFav = jsoIndex
//									.getString("favorite_number");
//
//							if (FragmentChangeActivity.strFavoriteNumber
//									.equals(strFav)) {
//								if ((jsoIndex.getJSONArray("dataAll")) != null) {
//									FragmentChangeActivity.strGetListSymbol = "";
//									JSONArray jsaFavSymbol = jsoIndex
//											.getJSONArray("dataAll");
//
//									for (int j = 0; j < jsaFavSymbol.length(); j++) {
//										FragmentChangeActivity.strGetListSymbol += jsaFavSymbol
//												.getJSONObject(j).getString(
//														"symbol_name");
//										if (j != (jsaFavSymbol.length() - 1)) {
//											FragmentChangeActivity.strGetListSymbol += ",";
//										}
//									}
//									// getWatchlistSymbol(); // get watchlist
//									// symbol
//
//									Log.v("strGetListSymbol Adapter",
//											""
//													+ FragmentChangeActivity.strGetListSymbol);
//									pagerWatchList.getWatchlistSymbol();
//								}
//								break;
//							}
//						}
//
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//				}
//			} else {
//			}
//		}
//	}

	// ============== set =============
	// private void getWatchlistSymbol() {
	// getSet resp = new getSet();
	// resp.execute();
	// }
	//
	// public class getSet extends AsyncTask<Void, Void, Void> {
	// boolean connectionError = false;
	// // ======= json ========
	// private JSONObject jsonGetWatchlistSymbol;
	//
	// @Override
	// protected void onPreExecute() {
	// super.onPreExecute();
	// }
	//
	// @Override
	// protected Void doInBackground(Void... params) {
	//
	// java.util.Date date = new java.util.Date();
	// long timestamp = date.getTime();
	// // ======= url ========
	// String url_GetWatchlistSymbol = SplashScreen.url_bidschart
	// + "/service/v2/watchlistSymbol?symbol="
	// + FragmentChangeActivity.strGetListSymbol;
	//
	// try {
	// // ======= Ui Home ========
	// jsonGetWatchlistSymbol = ReadJson
	// .readJsonObjectFromUrl(url_GetWatchlistSymbol);
	//
	// } catch (IOException e1) {
	// connectionError = true;
	// jsonGetWatchlistSymbol = null;
	// e1.printStackTrace();
	// } catch (JSONException e1) {
	// connectionError = true;
	// jsonGetWatchlistSymbol = null;
	// e1.printStackTrace();
	// } catch (RuntimeException e) {
	// connectionError = true;
	// jsonGetWatchlistSymbol = null;
	// e.printStackTrace();
	// }
	// return null;
	// }
	//
	// @Override
	// protected void onPostExecute(Void result) {
	// super.onPostExecute(result);
	//
	// if (connectionError == false) {
	//
	// if (jsonGetWatchlistSymbol != null) {
	// try {
	// // get content
	// FragmentChangeActivity.contentGetWatchlistSymbol = jsonGetWatchlistSymbol
	// .getJSONArray("dataAll");
	//
	// } catch (JSONException e) {
	// e.printStackTrace();
	// }
	// } else {
	// Log.v("json newslist null", "newslist null");
	// }
	// } else {
	// }
	// }
	// }
	//
	// }

	// /////////////////////////////////////////////
//	public static String convertInputStreamToString(InputStream inputStream)
//			throws IOException {
//		BufferedReader bufferedReader = new BufferedReader(
//				new InputStreamReader(inputStream));
//		String line = "";
//		String result = "";
//		while ((line = bufferedReader.readLine()) != null)
//			result += line;
//
//		inputStream.close();
//		return result;
//
//	}

}