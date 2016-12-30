package com.app.bids;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.NumberFormat;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.app.bids.R;

public class FunctionFormatData extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

	}

	// check str null
	public static String checkNull(String str_ck) {
		if (str_ck.equals("")) {
			str_ck = "N/a";
		} else if (str_ck.equals(null)) {
			str_ck = "N/a";
		} else if (str_ck.equals("null")) {
			str_ck = "N/a";
		} else {
			str_ck = FunctionFormatData.setFormatNumber(str_ck);
		}

		return str_ck;
	}

	// formatter = new DecimalFormat("0.00");
	// number = formatter.format(-0.123);

	// เช็คว่า format DecimalFormat & .2 digit
	// https://examples.javacodegeeks.com/core-java/text/format-number-with-custom-numberformat/
	public static String setFormatNumber(String strNumber) {
		String c = "";
		if ((!strNumber.equals("")) && (!strNumber.equals("0"))
				&& (!strNumber.equals("0.00")) && (!strNumber.equals("-"))) {
			float value = Float.parseFloat(strNumber.replaceAll(",", ""));
			// DecimalFormat myFormatter = new DecimalFormat("#,###.00");
			// c = myFormatter.format(value);

			// Log.v("setFormatNumber", "_"+value);

			if ((value < 1) && (value > -1) && (value > -1000)) {
				DecimalFormat myFormatter2 = new DecimalFormat("0.00");
				c = myFormatter2.format(value);
			} else {
				DecimalFormat myFormatter = new DecimalFormat("#,###.##");
				c = myFormatter.format(value);
			}
		} else {
			c = "0.00";
		}
		return c;
	}

	// return - ไม่ใส่จุด
	public static String setFormatNumberEtc(String strNumber) {
		String c = "";
		if ((!strNumber.equals("")) && (!strNumber.equals("0"))
				&& (!strNumber.equals("0.00")) && (!strNumber.equals("-"))) {
			c = strNumber;
		} else {
			c = "-";
		}
		return c;
	}

	// ค่าเป็น 0 ให้ใส่ - ไม่ใส่จุด
	public static String setFormatNumberBar(String strNumber) {
		String c = "";
		if ((strNumber.equals("")) || (strNumber.equals("0"))
				|| (strNumber.equals("0.00")) || (strNumber.equals("-"))) {
			c = "-";
		} else {
			c = strNumber;
		}
		return c;
	}

	// return 0
	public static String setFormatNumber0(String strNumber) {
		String c = "0";
		if ((!strNumber.equals("")) && (!strNumber.equals("0"))
				&& (!strNumber.equals("0.00")) && (!strNumber.equals("-"))) {
			c = strNumber;
		} else {
			c = "0";
		}
		return c;
	}

	// แปลงสตริงเป็น float
	public static float setStringPaseFloat(String strNumber) {
		float fNum = 0;
		if (!strNumber.equals("")) {
			fNum = Float.parseFloat(strNumber.replaceAll(",", ""));
		}
		return fNum;
	}

	// เช็คว่า ว่ามีข้อมูลใน (วงเล็บ) ไหม
	public static String setBracket(String strNumber) {
		String c = "";
		if ((!strNumber.equals("")) && (!strNumber.equals("0"))
				&& (!strNumber.equals("0.00")) && (!strNumber.equals("-"))) {
			c = "(" + setFormatNumber(strNumber) + ")";
		} else {
		}
		return c;
	}

	// เช็คว่า format Decimal
	public static String setFormatDecimal(String strNumber) {
		String c = "";
		float value = Float.parseFloat(strNumber.replaceAll(",", ""));
		DecimalFormat myFormatter = new DecimalFormat("#,###");
		c = myFormatter.format(value);
		return c;
	}

	// เช็คว่า format Decimal ใส่ , และ .
	public static String setFormatDecimal2(String strNumber) {
		String c = "";
		float value = Float.parseFloat(strNumber.replaceAll(",", ""));
		DecimalFormat myFormatter = new DecimalFormat("#,###,###.###");
		c = myFormatter.format(value);
		return c;
	}

	// เช็คว่า format Decimal ใส่ , ทศนิยม 2 ตำแหน่ง
	public static String setFormatAnd2Digit(String strNumber) {
		String c = "0.00";
		if (strNumber.equals("N/A")) {
			c = "N/A";
		} else if (!(strNumber.equals("")) && !(strNumber.equals("0"))) {
			float value = Float.parseFloat(strNumber.replaceAll(",", ""));
			if (value > 0.00) {
				DecimalFormat myFormatter = new DecimalFormat("#,###,###.##");
				c = myFormatter.format(value);
				if (value < 1) {
					c = "0" + c;
				}
			}
		}

		return c;
	}

	// return เป็น ทศนิยม 2 ตำแหน่ง
	public static String setFormat2Digit(String strNumber) {
		String c = "";
		if (!(strNumber.equals(""))
				&& !(strNumber.equals("0") && !(strNumber.equals("N/A")))) {
			float value = Float.parseFloat(strNumber.replaceAll(",", ""));
			c = String.format("%.02f", value);
		} else {
			c = "0.00";
		}
		return c;
	}

	// เช็คว่า status symbol => turnover_list_level, status, status_xd
	// symbol_name (staus)
	// @"T%@",symbol[@"turnover_list_level”], เป็น 1,2,3 ใส่
	// T ข้างหน้า
	// symbol[@"status”], ถ้า status เป็น SP สี text
	// เป็นสีขาวหมด กดดูรายละเอียดไม่ได้
	// symbol[@"status_xd"] ถ้าเป็น XD ใส่ (XD) ต่อท้
	// เช่น (T1,SP,XD)
	public static String checkStatusSymbol(String symbol_name,
			String turnover_list_level, String status, String status_xd) {
		String strStatus = "";
		if ((!turnover_list_level.equals("0"))
				&& (!turnover_list_level.equals("null"))) {
			strStatus = "(T" + turnover_list_level;
			if ((!status.equals("")) && (!status.equals("null"))) {
				strStatus = "," + status;
				if ((!status_xd.equals("")) && (!status_xd.equals("null"))) {
					strStatus = "," + status_xd;
				}
			} else if ((!status_xd.equals("")) && (!status_xd.equals("null"))) {
				strStatus = "," + status_xd;
			}
			strStatus += ")";
		} else if ((!status.equals("")) && (!status.equals("null"))) {
			strStatus = "(" + status;
			if ((!status_xd.equals("")) && (!status_xd.equals("null"))) {
				strStatus = "," + status_xd;
			}
			strStatus += ")";
		} else if ((!status_xd.equals("")) && (!status_xd.equals("null"))) {
			strStatus = "(" + status_xd + ")";
		} else {

		}

		String cStatus = symbol_name + "<font color='#478ecb'>" + strStatus
				+ "</font>";

		return cStatus;
	}

	public static String checkStatusSymbolDetail(String turnover_list_level,
			String status, String status_xd) {
		String strStatus = "";
		if ((!turnover_list_level.equals("0"))
				&& (!turnover_list_level.equals("null"))) {
			strStatus = "(T" + turnover_list_level;
			if ((!status.equals("")) && (!status.equals("null"))) {
				strStatus = "," + status;
				if ((!status_xd.equals("")) && (!status_xd.equals("null"))) {
					strStatus = "," + status_xd;
				}
			} else if ((!status_xd.equals("")) && (!status_xd.equals("null"))) {
				strStatus = "," + status_xd;
			}
			strStatus += ")";
		} else if ((!status.equals("")) && (!status.equals("null"))) {
			strStatus = "(" + status;
			if ((!status_xd.equals("")) && (!status_xd.equals("null"))) {
				strStatus = "," + status_xd;
			}
			strStatus += ")";
		} else if ((!status_xd.equals("")) && (!status_xd.equals("null"))) {
			strStatus = "(" + status_xd + ")";
		} else {

		}

		String cStatus = strStatus;

		return cStatus;
	}
}
