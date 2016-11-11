package com.app.bids;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class DateTimeCreate {
	// http://alvinalexander.com/java/simpledateformat-convert-date-to-string-formatted-parse

	// Format formatter;
	// formatter = new SimpleDateFormat("EEEE ที่ dd เดือน MMMM พ.ศ. yyyy", new
	// Locale("th", "TH"));
	// System.out.println(formatter.format(new Date()));
	// //วันจันทร์ ที่ 12 เดือน พฤศจิกายน พ.ศ. 2555

	// array month Th
	private static final String[] arrMonthEng = { "Jan", "Feb", "Mar", "Apr",
			"May", "Jun", "Jul", "Aug", "Sept", "Oct", "Nov", "Dec", "" };
	// array month Th
	private static final String[] arrMonthTh = { "ม.ค.", "ก.พ.", "มี.ค.",
			"เม.ย.", "พ.ค.", "มิ.ย.", "ก.ค.", "ส.ค.", "ก.ย.", "ต.ค.", "พ.ย.",
			"ธ.ค.", "" };

	// return day
	public static String DateDayCreate(String strDate) {
		// created_at: "2015-06-04 14:19:36"

		String dateReturn = "";
		if ((strDate != null) && (strDate != "null") && (strDate != "")) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			int year = 0, month = 12, day = 0;
			try {
				Date date = df.parse(strDate);
				Calendar c = Calendar.getInstance();
				c.setTime(date);

				year = c.get(Calendar.YEAR);
				month = c.get(Calendar.MONTH);
				day = c.get(Calendar.DATE);

			} catch (ParseException e) {
				e.printStackTrace();
			}

			dateReturn = "" + day;

			if ((day == 0) || (year == 0)) {
				dateReturn = "";
			}
		}
		return dateReturn;
	}

	// return month
	public static String DateMonthCreate(String strDate) {
		// created_at: "2015-06-04 14:19:36"
		String dateReturn = "";
		if ((strDate != null) && (strDate != "null") && (strDate != "")) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			int year = 0, month = 12, day = 0;
			try {
				Date date = df.parse(strDate);
				Calendar c = Calendar.getInstance();
				c.setTime(date);

				year = c.get(Calendar.YEAR);
				month = c.get(Calendar.MONTH);
				day = c.get(Calendar.DATE);

			} catch (ParseException e) {
				e.printStackTrace();
			}

			dateReturn = "" + month;

			if ((day == 0) || (year == 0)) {
				dateReturn = "";
			}
		}
		return dateReturn;
	}

	// return d-m
	public static String DateDmCreate(String strDate) {
		// created_at: "2015-06-04 14:19:36"
		String dateReturn = "";
		if ((strDate != null) && (strDate != "null") && (strDate != "")) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			int year = 0, month = 12, day = 0;
			try {
				Date date = df.parse(strDate);
				Calendar c = Calendar.getInstance();
				c.setTime(date);

				year = c.get(Calendar.YEAR);
				month = c.get(Calendar.MONTH);
				day = c.get(Calendar.DATE);

			} catch (ParseException e) {
				e.printStackTrace();
			}

			dateReturn = day + " " + arrMonthEng[month];

			if ((day == 0) || (year == 0)) {
				dateReturn = "";
			}
		}
		return dateReturn;
	}

	// return d-m-y
	public static String DateDmyCreate(String strDate) {
		// created_at: "2015-06-04 14:19:36"
		String dateReturn = "";
		if ((strDate != null) && (strDate != "null") && (strDate != "")) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			int year = 0, month = 12, day = 0;
			try {
				Date date = df.parse(strDate);
				Calendar c = Calendar.getInstance();
				c.setTime(date);

				year = c.get(Calendar.YEAR);
				month = c.get(Calendar.MONTH);
				day = c.get(Calendar.DATE);

			} catch (ParseException e) {
				e.printStackTrace();
			}

			dateReturn = day + " " + arrMonthEng[month] + " " + year;

			if ((day == 0) || (year == 0)) {
				dateReturn = "";
			}
		}
		return dateReturn;
	}

	// return d-m-y thai
	public static String DateDmyThaiCreate(String strDate) {
		// created_at: "2015-06-04 14:19:36"
		String dateReturn = "";
		if ((strDate != null) && (strDate != "null") && (strDate != "")) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			int year = 0, month = 12, day = 0;
			try {
				Date date = df.parse(strDate);
				Calendar c = Calendar.getInstance();
				c.setTime(date);

				year = c.get(Calendar.YEAR);
				month = c.get(Calendar.MONTH);
				day = c.get(Calendar.DATE);

			} catch (ParseException e) {
				e.printStackTrace();
			}

			dateReturn = day + " " + arrMonthTh[month] + " " + year;

			if ((day == 0) || (year == 0)) {
				dateReturn = "";
			}
		}
		return dateReturn;

	}

	// return time thai 14:19:36
	public static String TimeCreate(String strDate) {
		// created_at: "2015-06-04 14:19:36"
		String dateReturn = "";
		if ((strDate != null) && (strDate != "null") && (strDate != "")) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			// Calendar c = Calendar.getInstance();

			dateReturn = strDate.split(" ")[1];
		}
		return dateReturn;

	}

	// return d-m-y thai yy 2 digit 11 ม.ค. 16
	public static String DateDmyThaiCreateShot(String strDate) {
		// created_at: "2015-06-04 14:19:36"
		String dateReturn = "";
		if ((strDate != null) && (strDate != "null") && (strDate != "")) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			int year = 0, month = 12, day = 0;
			try {
				Date date = df.parse(strDate);
				Calendar c = Calendar.getInstance();
				c.setTime(date);

				year = c.get(Calendar.YEAR) - 2000;
				month = c.get(Calendar.MONTH);
				day = c.get(Calendar.DATE);

			} catch (ParseException e) {
				e.printStackTrace();
			}

			dateReturn = day + " " + arrMonthTh[month] + " " + year;

			if ((day == 0) || (year == 0)) {
				dateReturn = "";
			}
		}
		return dateReturn;

	}

	// return d-m-y thai nottime 11 ม.ค. 2016
	public static String DateDmySystemTradeSearch(String strDate) {

		// created_at: "2015-06-04
		String dateReturn = "";
		if ((strDate != null) && (strDate != "null") && (strDate != "")) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			int year = 0, month = 12, day = 0;
			try {
				Date date = df.parse(strDate);
				Calendar c = Calendar.getInstance();
				c.setTime(date);

				year = c.get(Calendar.YEAR) - 2000;
				month = c.get(Calendar.MONTH);
				day = c.get(Calendar.DATE);

			} catch (ParseException e) {
				e.printStackTrace();
			}

			dateReturn = day + " " + arrMonthTh[month] + " " + year;

			if ((day == 0) || (year == 0)) {
				dateReturn = "";
			}
		}
		return dateReturn;

	}

	// return d-m-y thai 11 ม.ค. 2016
	public static String DateDmyWatchlistPortfolio(String strDate) {

		// created_at: "2015-06-04
		String dateReturn = "";
		if ((strDate != null) && (strDate != "null") && (strDate != "")) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			int year = 0, month = 12, day = 0;
			try {
				Date date = df.parse(strDate);
				Calendar c = Calendar.getInstance();
				c.setTime(date);

				year = c.get(Calendar.YEAR);
				month = c.get(Calendar.MONTH);
				day = c.get(Calendar.DATE);

			} catch (ParseException e) {
				e.printStackTrace();
			}

			dateReturn = day + " " + arrMonthTh[month] + " " + year;

			if ((day == 0) || (year == 0)) {
				dateReturn = "";
			}
		}
		return dateReturn;

	}

	// return 13 mar 2016
	public static String DateCreateMutualFun(String strDate) {
		// date_at: "1/6/2559"
		String dateReturn = "";
		if ((strDate != null) && (strDate != "null") && (strDate != "")) {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			int year = 0, month = 12, day = 0;
			try {
				Date date = df.parse(strDate);
				Calendar c = Calendar.getInstance();
				c.setTime(date);

				year = c.get(Calendar.YEAR);
				month = c.get(Calendar.MONTH);
				day = c.get(Calendar.DATE);

			} catch (ParseException e) {
				e.printStackTrace();
			}

			dateReturn = day + " " + arrMonthEng[month] + " " + (year - 543);

			if ((day == 0) || (year == 0)) {
				dateReturn = "";
			}
		}
		return dateReturn;

	}

	// return 13 ม.ค. 2016
	public static String DateCreateMutualFunTh(String strDate) {
		// date_at: "1/6/2559"
		String dateReturn = "";
		if ((strDate != null) && (strDate != "null") && (strDate != "")) {
			DateFormat df = new SimpleDateFormat("dd/MM/yyyy");
			int year = 0, month = 12, day = 0;
			try {
				Date date = df.parse(strDate);
				Calendar c = Calendar.getInstance();
				c.setTime(date);

				year = c.get(Calendar.YEAR);
				month = c.get(Calendar.MONTH);
				day = c.get(Calendar.DATE);

			} catch (ParseException e) {
				e.printStackTrace();
			}

			dateReturn = day + " " + arrMonthTh[month] + " " + year;

			if ((day == 0) || (year == 0)) {
				dateReturn = "";
			}
		}

		return dateReturn;
	}

	// return d-m-y number
	public static String DateDmyNumberCreate(String strDate) {
		// created_at: "2015-06-04 14:19:36"
		String dateReturn = "";
		if ((strDate != null) && (strDate != "null") && (strDate != "")) {
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			int year = 0, month = 0, day = 0;
			try {
				Date date = df.parse(strDate);
				Calendar c = Calendar.getInstance();
				c.setTime(date);

				year = c.get(Calendar.YEAR);
				month = c.get(Calendar.MONTH);
				day = c.get(Calendar.DATE);

			} catch (ParseException e) {
				e.printStackTrace();
			}

			dateReturn = day + " " + month + " " + year;

			if ((day == 0) || (year == 0)) {
				dateReturn = "";
			}
		}
		return dateReturn;

	}

}
