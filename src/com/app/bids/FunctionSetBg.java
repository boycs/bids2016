package com.app.bids;

import android.R.string;
import android.graphics.Color;
import android.util.Log;

import com.app.bids.R;

public class FunctionSetBg {

	// set color main
	public static int[] arrColor = { R.color.c_danger, R.color.c_warning,
			R.color.c_success, R.color.c_content, R.color.c_blue,
			R.color.c_title };

	public static int setColor(String strColor) {
		int c = arrColor[3];
		if ((strColor.trim() != "") && (strColor.trim() != "-")) {
			double dColor = Double.parseDouble(strColor);
			if (dColor < 0) {
				c = arrColor[0];
			} else if (dColor == 0) {
				c = arrColor[1];
			} else {
				c = arrColor[2];
			}
		}
		return c;
	}

	// เปรียบเที่ยบกับ 0
	public static int setColorCompareWithZero(String strColor) {
		int c = arrColor[3];
		if ((!strColor.equals("")) && (!strColor.equals("-"))
				&& (!strColor.equals("null")) && (!strColor.equals("N/A"))) {
			double dColor = Double.parseDouble(strColor.replaceAll(",", ""));
			// Log.v("dColor", "" + dColor);
			if (dColor < 0) {
				c = arrColor[0];
			} else if (dColor == 0) {
				c = arrColor[1];
			} else {
				c = arrColor[2];
			}
		}
		return c;
	}

	// เปรียบเที่ยบกับ กัน 2 แอทริบิ้ว (ตัวตัง เป็น 1, เทียบกับ เป็น 2)
	public static int setColorCompare2Attribute(String str1, String str2) {
		int c = arrColor[3];
		if ((!str1.equals("")) && (!str1.equals("-")) && (!str1.equals("null"))
				&& (!str1.equals("N/A")) && (!str2.equals(""))
				&& (!str2.equals("-")) && (!str2.equals("null"))
				&& (!str2.equals("N/A"))) {
			double d1 = Double.parseDouble(str1.replaceAll(",", ""));
			double d2 = Double.parseDouble(str2.replaceAll(",", ""));
			// Log.v("dColor", "" + dColor);
			if (d1 < d2) {
				c = arrColor[0];
			} else if (d1 == d2) {
				c = arrColor[1];
			} else {
				c = arrColor[2];
			}
		}
		return c;
	}

	// symbol Plus Minus (+, -) เช็คว่าเป็นค่า บวกหรือลบ
	public static String setSymbolPlusMinus(String strColor) {
		String c = "";
		if ((!strColor.equals("")) && (!strColor.equals("-"))
				&& (!strColor.equals("null")) && (!strColor.equals("N/A"))) {
			double dColor = Double.parseDouble(strColor.replaceAll(",", ""));
			// Log.v("dColor", "" + dColor);
			if (dColor < 0) {
				c = "";
			} else if (dColor == 0) {
				c = "";
			} else {
				c = "+";
			}
		}
		return c;
	}

	// ใส่ % ต่อท้าย ถ้ามีข้อมูล ให้ใส่ %
	public static String setSymbolPercent(String strColor) {
		String c = "";
		if ((!strColor.equals("")) && (!strColor.equals("-"))
				&& (!strColor.equals("null")) && (!strColor.equals("N/A"))) {
			c = "%";
		} else {

		}
		return c;
	}

	// set img updown symbol
	public static int setImgUpDown(String strColor) {
		int c = R.drawable.icon_empty;
		double dColor = Double.parseDouble(strColor.replaceAll(",", ""));
		if (dColor < 0) {
			c = R.drawable.icon_down;
		} else if (dColor == 0) {
			c = R.drawable.icon_empty;
		} else {
			c = R.drawable.icon_up;
		}
		return c;
	}

	// set trend signal
	public static int setImgTrendSignal(String strTrend) {
		int c = R.drawable.icon_empty;
		if (strTrend.equals("up")) {
			c = R.drawable.trend_up_system;
		} else if (strTrend.equals("down")) {
			c = R.drawable.trend_down_system;
		} else if (strTrend.equals("sideway")) {
			c = R.drawable.trend_sideway_system;
		} else {
			c = R.drawable.icon_empty;
		}
		return c;
	}

	// set trend signal
	public static int setImgBroutOutType(String strBreakOut) {
		int c = R.drawable.icon_empty;
		if ((strBreakOut.equals("Highest High Breakout"))
				|| (strBreakOut.equals("high"))) {
			c = R.drawable.icon_breakout_highest_high;
		} else if ((strBreakOut.equals("Highest High Value Breakout"))
				|| (strBreakOut.equals("value"))) {
			c = R.drawable.icon_breakout_highest_high_value;
		} else if ((strBreakOut.equals("Peak & Trough Breakout"))
				|| (strBreakOut.equals("peak"))) {
			c = R.drawable.icon_breakout_peak_trough;
		} else if ((strBreakOut.equals("Price & Volume Breakout"))
				|| (strBreakOut.equals("price"))) {
			c = R.drawable.icon_breakout_price_volume;
		} else if ((strBreakOut.equals("Short Term Breakout"))) {
			c = R.drawable.icon_empty;
		} else {
			c = R.drawable.icon_empty;
		}
		return c;
	}

	// set img follow number
	public static int setFollowNumber(String strColor) {
		int c = R.drawable.icon_favorite_default;
		if (strColor == "1") {
			c = R.drawable.icon_favorite_1;
		} else if (strColor == "2") {
			c = R.drawable.icon_favorite_2;
		} else if (strColor == "3") {
			c = R.drawable.icon_favorite_3;
		} else if (strColor == "4") {
			c = R.drawable.icon_favorite_4;
		} else if (strColor == "5") {
			c = R.drawable.icon_favorite_5;
		}
		return c;
	}

	// set str detail list
	public static String setStrDetailList(String str) {
		if ((str.equals("0.00")) || (str.equals("0")) || (str.equals(""))
				|| (str == null) || (str.isEmpty()) || (str.equals("null"))
				&& (!str.equals("-"))) {
			str = "-";
		} else {
			str = FunctionFormatData.setFormatNumber(str);
			return str;
		}
		return str;
	}

	// set - is color write
	public static int setStrColorWriteDetailDanger(String str) {
		int c = R.color.c_danger;
		if ((str.equals("0.00")) || (str.equals("0")) || (str.equals(""))
				|| (str == null) || (str.isEmpty()) || (str.equals("null"))
				|| (str.equals("-"))) {
			c = R.color.c_content;
		}
		return c;
	}

	public static int setStrColorWriteDetailSuccess(String str) {
		int c = R.color.c_success;
		if ((str.equals("0.00")) || (str.equals("0")) || (str.equals(""))
				|| (str == null) || (str.isEmpty()) || (str.equals("null"))
				|| (str.equals("-"))) {
			c = R.color.c_content;
		}
		return c;
	}

	public static int setStrColorWriteDetailWaring(String str) {
		int c = R.color.c_warning;
		if ((str.equals("0.00")) || (str.equals("0")) || (str.equals(""))
				|| (str == null) || (str.isEmpty()) || (str.equals("null"))
				|| (str.equals("-"))) {
			c = R.color.c_content;
		}
		return c;
	}

	public static int setStrColorWriteDetailBlue(String str) {
		int c = R.color.c_blue;
		if ((str.equals("0.00")) || (str.equals("0")) || (str.equals(""))
				|| (str == null) || (str.isEmpty()) || (str.equals("null"))
				|| (str.equals("-"))) {
			c = R.color.c_content;
		}
		return c;
	}

	public static int setStrColorWriteDetailPink(String str) {
		int c = R.color.c_pink;
		if ((str.equals("0.00")) || (str.equals("0")) || (str.equals(""))
				|| (str == null) || (str.isEmpty()) || (str.equals("null"))
				|| (str.equals("-"))) {
			c = R.color.c_content;
		}
		return c;
	}

	// set str detail list color
	public static int setStrDetailListColor(String str) {
		int c = R.color.c_blue;
		if ((str.equals("0.00")) || (str.equals("0")) || (str.equals(""))
				|| (str == null) || (str.isEmpty()) || (str.equals("null"))) {
			c = arrColor[3];
		}
		return c;
	}

	// str ck .set // { R.color.c_danger, R.color.c_warning,R.color.c_success,
	// R.color.c_content };
	public static int setStrCheckSet(String srtOther, String strSet) {
		int c = arrColor[3];

		if ((srtOther.equals("")) || (srtOther == null) || (srtOther.isEmpty())
				|| (srtOther.equals("null")) || (strSet.equals(""))
				|| (strSet == null) || (strSet.isEmpty())
				|| (strSet.equals("null"))) {
			c = arrColor[3];
		} else {
			double dOther = Double.parseDouble(srtOther.replaceAll(",", ""));
			double dSet = Double.parseDouble(strSet.replaceAll(",", ""));

			if (dOther < dSet) { // green
				c = arrColor[2];
			} else if (dOther > dSet) { // danger
				c = arrColor[0];
			} else if (dOther == dSet) { // warning
				c = arrColor[1];
			}
		}
		return c;
	}

	// set color other
	public static int setColorPrev(String strColorPrev, String strColorOther) {
		int c = arrColor[3];
		if ((!strColorPrev.trim().equals(""))
				&& (!strColorOther.trim().equals(""))) {
			double dColorPrev, dColorOther;

			dColorPrev = Double.parseDouble(strColorPrev.replaceAll(",", ""));
			dColorOther = Double.parseDouble(strColorOther.replaceAll(",", ""));
			if (dColorOther < dColorPrev) {
				c = arrColor[0];
			} else if (dColorOther == dColorPrev) {
				c = arrColor[1];
			} else if (dColorOther > dColorPrev) {
				c = arrColor[2];
			}
		}
		return c;
	}

	// set bg
	public static int setEsuBgImb(String strImb) {
		int c = R.drawable.bg_imb;
		if (strImb.equals("Y")) {
			c = R.drawable.bg_imb_buy;
		} else if (strImb.equals("N")) {
			c = R.drawable.bg_imb_sell;
		} else {
			c = R.drawable.bg_imb;
		}
		return c;
	}

	public static String setEsuStrImb(String strImb) {
		String c = "";
		if (strImb.equals("Y")) {
			c = "Yes";
		} else if (strImb.equals("N")) {
			c = "No";
		}
		return c;
	}

	// watchlistSymbol color
	// [T] : trendSignal_avg_percentChange ส่งค่าไปเป็น avg %change
	// [F]: fundamental ส่งค่าไปเป็นคะแนน เต็ม 5
	// [S]:color_macd ส่งตัวอักษรสีไปเป็น
	// ['GREEN','BLUE','RED','ORANGE','BLACK']

	// color watchlist symbol =======================

	// public static String arrColorST_Code[] = { "#95dd33", "#eb4848",
	// "#ffc909",
	// "#478ecb", "#1b212b" };
	public static String arrColorST_Code[] = { "#2D9650", "#8B434E", "#FCB913",
			"#478ecb", "#1B2126" };

	// เขียว(149,221,51) แดง(235,72,72) ส้ม(255,201,9) ฟ้า(71,142,203)
	// ดำ(27,33,43)

	public static int setColorWatchListSymbolTrendSignal(String strColor) {
		int c = Color.parseColor(arrColorST_Code[4]);
		if (!(strColor.equals("null")) && !(strColor.equals(""))) {
			double dColor = Double.parseDouble(strColor.replaceAll(",", ""));
			if (dColor > 2) {
				c = Color.parseColor(arrColorST_Code[0]);
			} else if ((dColor >= -2) && (dColor <= 2)) {
				c = Color.parseColor(arrColorST_Code[2]);
			} else if (dColor < -2) {
				c = Color.parseColor(arrColorST_Code[1]);
			} else {
				c = Color.parseColor(arrColorST_Code[4]);
			}

			// if (dColor == 0) {
			// c = Color.parseColor(arrColorST_Code[4]);
			// } else if (dColor < -2) {
			// c = Color.parseColor(arrColorST_Code[1]);
			// } else if ((dColor >= -2) && (dColor <= 2)) {
			// c = Color.parseColor(arrColorST_Code[2]);
			// } else if (dColor > 2) {
			// c = Color.parseColor(arrColorST_Code[0]);
			// }
		}

		return c;
	}

	public static String arrColorMacd[] = { "GREEN", "RED", "ORANGE", "BLUE",
			"BLACK", "YELLOW" };

	public static String arrColorMacd_Code[] = { "#2A9650", "#8B434E",
			"#FFC909", "#478ECB", "#1B2126", "#FCB913" };

	public static int setColorWatchListSymbolColorMacd(String strColor) {
		int c = Color.parseColor(arrColorMacd_Code[4]);
		if (!(strColor.equals("null")) && !(strColor.equals(""))) {
			for (int i = 0; i < arrColorMacd.length; i++) {
				if (arrColorMacd[i].equals(strColor)) {
					c = Color.parseColor(arrColorMacd_Code[i]);
					break;
				}
			}
		}

		return c;
	}

	// public static String arrColorFund_Code[] = { "#8B434E", "#594556",
	// "#3e7556", "#2d9650", "#35cc4b" };
	// แดงเข้ม, ม่วงเข้ม, เขียวเข้มๆ, เขียวเข้ม, เขียว

	public static String arrColorFund_Code[] = { "#8B4344", "#5A4556",
			"#3E7556", "#2D964E", "#35CC4E", "#1B2126" };

	public static int setColorWatchListSymbolFundamental(String strColor) {
		int c = Color.parseColor(arrColorST_Code[0]);

		if (!(strColor.equals("null")) && !(strColor.equals(""))) {
			double dColor = Double.parseDouble(strColor.replaceAll(",", ""));
			if ((dColor >= 0.00) && (dColor < 1.00)) {
				c = Color.parseColor(arrColorFund_Code[0]);
			} else if ((dColor >= 1.00) && (dColor < 2.00)) {
				c = Color.parseColor(arrColorFund_Code[1]);
			} else if ((dColor >= 2.00) && (dColor < 3.00)) {
				c = Color.parseColor(arrColorFund_Code[2]);
			} else if ((dColor >= 3.00) && (dColor < 4.00)) {
				c = Color.parseColor(arrColorFund_Code[3]);
			} else if ((dColor >= 4.00)) {
				c = Color.parseColor(arrColorFund_Code[4]);
			} else {
				c = Color.parseColor(arrColorFund_Code[5]);
			}
		}

		return c;
	}

	// set color TxtSliding
	public static String[] arrColorTxtSliding = { "#eb4848", "#FFC125",
			"#95dd33", "#ffffff" };

	public static String setColorTxtSliding(String strSet, String strOther) {

		double dSet = Double.parseDouble(strSet.replaceAll(",", ""));
		double dOther = Double.parseDouble(strOther.replaceAll(",", ""));

		String txtO = String.format(" %,.2f", dOther);
		String sDefault = "(<font color='" + arrColorTxtSliding[3] + "'>"
				+ txtO.trim() + "%</font>)";

		if (dSet == dOther) {
			sDefault = "(<font color='" + arrColorTxtSliding[1] + "'>"
					+ txtO.trim() + "%</font>)";
		} else if (dSet < dOther) {
			sDefault = "(<font color='" + arrColorTxtSliding[2] + "'>"
					+ txtO.trim() + "%</font>)";
		} else if (dSet > dOther) {
			sDefault = "(<font color='" + arrColorTxtSliding[0] + "'>"
					+ txtO.trim() + "%</font>)";
		} else {
			sDefault = "(<font color='" + arrColorTxtSliding[3] + "'>"
					+ txtO.trim() + "%</font>)";
		}
		return sDefault;
	}

	public static String setColorTxtSlidingSet(String strSet) {

		double dSet = Double.parseDouble(strSet);
		String txtS = String.format(" %,.2f", dSet);
		String sDefault = "(<font color='" + arrColorTxtSliding[3] + "'>"
				+ txtS.trim() + "%</font>)";
		if (dSet == 0) {
			sDefault = "(<font color='" + arrColorTxtSliding[1] + "'>"
					+ txtS.trim() + "%</font>)";
		} else if (dSet < 0) {
			sDefault = "(<font color='" + arrColorTxtSliding[0] + "'>"
					+ txtS.trim() + "%</font>)";
		} else if (dSet > 0) {
			sDefault = "(<font color='" + arrColorTxtSliding[2] + "'>"
					+ txtS.trim() + "%</font>)";
		} else {
			sDefault = "(<font color='" + arrColorTxtSliding[3] + "'>"
					+ txtS.trim() + "%</font>)";
		}
		return sDefault;
	}

	// set fundamental activity, profit, lev, liq
	public static int setImgFunGraph(String strFun) {
		int c = R.drawable.icon_fundamental_graph0;
		if (strFun.equals("1")) {
			c = R.drawable.icon_fundamental_graph1;
		} else if (strFun.equals("2")) {
			c = R.drawable.icon_fundamental_graph2;
		} else if (strFun.equals("3")) {
			c = R.drawable.icon_fundamental_graph3;
		} else if (strFun.equals("4")) {
			c = R.drawable.icon_fundamental_graph4;
		}
		return c;
	}

	// set fundamental text color
	public static int setFundamentalTextColor(String strColor) {
		int c = arrColor[3];
		if ((!strColor.trim().equals("")) && (!strColor.trim().equals(""))) {
			if (strColor.equals("down")) {
				c = arrColor[0];
			} else if (strColor.equals("normal")) {
				c = arrColor[1];
			} else if (strColor.equals("up")) {
				c = arrColor[2];
			}
		}
		return c;
	}

	// set status market
	// "Closed","Closed2","Intermission","OffHour","Open1","Open2"
	// "Pre-close","Pre-Open1","Pre-Open2"
	public static int setMarketStatus(String strMarket) {
		int c = R.drawable.icon_status_m;
		if (strMarket.equals("Closed")) {
			c = R.drawable.icon_status_m;
		} else if (strMarket.equals("Closed2")) {
			c = R.drawable.icon_status_m;
			
		} else if (strMarket.equals("Intermission")) {
			c = R.drawable.icon_status_m_yellow;
		} else if (strMarket.equals("OffHour")) {
			c = R.drawable.icon_status_m_yellow;
			
		} else if (strMarket.equals("Pre-close")) {
			c = R.drawable.icon_status_m_yellow;
		} else if (strMarket.equals("Pre-Open1")) {
			c = R.drawable.icon_status_m_yellow;
		} else if (strMarket.equals("Pre-Open2")) {
			c = R.drawable.icon_status_m_yellow;
			
		} else if (strMarket.equals("Open1")) {
			c = R.drawable.icon_status_m_green;
		} else if (strMarket.equals("Open2")) {
			c = R.drawable.icon_status_m_green;
		} 
		return c;
	}

	// // set fundamental activity
	// public static int setImgFunActivity(String strTrend) {
	// int c = R.drawable.icon_fundamental_graph0;
	// if (strTrend.equals("1")) {
	// c = R.drawable.icon_fundamental_graph1;
	// } else if (strTrend.equals("2")) {
	// c = R.drawable.icon_fundamental_graph2;
	// } else if (strTrend.equals("3")) {
	// c = R.drawable.icon_fundamental_graph3;
	// } else if (strTrend.equals("4")) {
	// c = R.drawable.icon_fundamental_graph4;
	// }
	// return c;
	// }
	//
	// // set fundamental profit
	// public static int setImgFunProfit(String strTrend) {
	// int c = R.drawable.icon_fundamental_graph0;
	// if (strTrend.equals("1")) {
	// c = R.drawable.icon_fundamental_graph1;
	// } else if (strTrend.equals("2")) {
	// c = R.drawable.icon_fundamental_graph2;
	// } else if (strTrend.equals("3")) {
	// c = R.drawable.icon_fundamental_graph3;
	// } else if (strTrend.equals("4")) {
	// c = R.drawable.icon_fundamental_graph4;
	// }
	// return c;
	// }
	//
	// // set fundamental lev
	// public static int setImgFunLev(String strTrend) {
	// int c = R.drawable.icon_fundamental_graph0;
	// if (strTrend.equals("1")) {
	// c = R.drawable.icon_fundamental_graph1;
	// } else if (strTrend.equals("2")) {
	// c = R.drawable.icon_fundamental_graph2;
	// } else if (strTrend.equals("3")) {
	// c = R.drawable.icon_fundamental_graph3;
	// } else if (strTrend.equals("4")) {
	// c = R.drawable.icon_fundamental_graph4;
	// }
	// return c;
	// }
	//
	// // set fundamental liq
	// public static int setImgFunLeq(String strTrend) {
	// int c = R.drawable.icon_fundamental_graph0;
	// if (strTrend.equals("1")) {
	// c = R.drawable.icon_fundamental_graph1;
	// } else if (strTrend.equals("2")) {
	// c = R.drawable.icon_fundamental_graph2;
	// } else if (strTrend.equals("3")) {
	// c = R.drawable.icon_fundamental_graph3;
	// } else if (strTrend.equals("4")) {
	// c = R.drawable.icon_fundamental_graph4;
	// }
	// return c;
	// }

}
