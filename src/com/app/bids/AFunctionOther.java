package com.app.bids;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class AFunctionOther extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	// ได้ข้อมูล favorite ทั้งหมดมา แล้วปั่นเอา symbol เพื่อไป getSymbol
	private void getFavorite() {
		try {
			for (int i = 0; i < FragmentChangeActivity.contentGetSymbolFavorite
					.length(); i++) {
				JSONObject jsoIndex = FragmentChangeActivity.contentGetSymbolFavorite
						.optJSONObject(i);

				// get favorite number
				String strFav = jsoIndex.getString("favorite_number"); 

				// ถ้าตรงกับ favorite ที่เลือกก็ get เอา symbol
				if (FragmentChangeActivity.strFavoriteNumber.equals(strFav)) {
					if ((jsoIndex.getJSONArray("dataAll")) != null) {
						FragmentChangeActivity.strGetListSymbol = "";
						JSONArray jsaFavSymbol = jsoIndex
								.getJSONArray("dataAll");

						for (int j = 0; j < jsaFavSymbol.length(); j++) {
							FragmentChangeActivity.strGetListSymbol += jsaFavSymbol
									.getJSONObject(j).getString("symbol_name");
							if (j != (jsaFavSymbol.length() - 1)) {
								// เก็บ symbol ต่อกันเป็นสตริง ,
								// เพื่อเอาไป get ข้อมูล fav นั้นในฟังก์ชัน
								// getWatchlistSymbol
								FragmentChangeActivity.strGetListSymbol += ",";
							}
						}
					}
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	// แปลงข้อมูลเป็น string
	public static String convertInputStreamToString(InputStream inputStream)
			throws IOException {
		BufferedReader bufferedReader = new BufferedReader(
				new InputStreamReader(inputStream));
		String line = "";
		String result = "";
		while ((line = bufferedReader.readLine()) != null)
			result += line;

		inputStream.close();
		return result;

	}

}
