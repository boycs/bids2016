package com.app.bids;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

public class DateTimeAgo {
	// timestamp
	public static String CalAgoTime(String str_date) throws ParseException {

		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = (Date) formatter.parse(str_date);
		long output = date.getTime() / 1000L;
		String str = Long.toString(output);
		long timestamp = Long.parseLong(str);
		String str_timestamp = "" + timestamp;

		Timestamp stamp1 = new Timestamp(Long.parseLong(str_timestamp) * 1000);
		Date date1 = new Date(stamp1.getTime());

		Timestamp stamp2 = new Timestamp(System.currentTimeMillis());
		Date date2 = new Date(stamp2.getTime());

		try {
			// in milliseconds
			long diff = date2.getTime() - date1.getTime();

			long diffSeconds = diff / 1000 % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000) % 24;
			long diffDays = diff / (24 * 60 * 60 * 1000);

			Log.v("S M H D", "" + diffSeconds + "_" + diffMinutes + "_"
					+ diffHours + "_" + diffDays);

			// 2015-04-07 20:50:20 ==> -8_-41_-5_-16516043
			// 08/04/2015 16.41 ==> -40_-11_-5_-16516043
			// -22_-23_-17_-16556794

			if (diffDays > 0) {
				return diffDays + " " + "วัน";
			} else if (diffHours > 0) {
				return diffHours + " " + "ชั่วโมง";
			} else if (diffMinutes > 0) {
				return diffMinutes + " " + "นาที";
			} else {
				if (diffSeconds < 0)
					diffSeconds = 0;
				return "ไม่กี่วินาทีที่แล้ว";
				// return diffSeconds + " วินาทีที่แล้ว";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "0";
	}

	// timestamp
	public static String CalAgoTime2(String str_date) throws ParseException {

		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = (Date) formatter.parse(str_date);
		long output = date.getTime() / 1000L;
		String str = Long.toString(output);
		long timestamp = Long.parseLong(str);
		String str_timestamp = "" + timestamp;

		Timestamp stamp1 = new Timestamp(Long.parseLong(str_timestamp) * 1000);
		Date date1 = new Date(stamp1.getTime());

		Timestamp stamp2 = new Timestamp(System.currentTimeMillis());
		Date date2 = new Date(stamp2.getTime());

		try {
			// in milliseconds
			long diff = date2.getTime() - date1.getTime();

			long diffSeconds = diff / 1000 % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000) % 24;
			long diffDays = diff / (24 * 60 * 60 * 1000);
			long diffMonths = diff / (24 * 60 * 60 * 1000) / 30;
			long diffYears = diff / (24 * 60 * 60 * 1000) / 365;

			Log.v("S M H D", "" + diffSeconds + "_" + diffMinutes + "_"
					+ diffHours + "_" + diffDays);

			// 2015-04-07 20:50:20 ==> -8_-41_-5_-16516043
			// 08/04/2015 16.41 ==> -40_-11_-5_-16516043
			// -22_-23_-17_-16556794

			if (diffYears > 0) {
				return diffYears + " ปีที่แล้ว";
			} else if (diffMonths > 0) {
				return diffMonths + " เดือนที่แล้ว";
			} else if (diffDays > 0) {
				return diffDays + " วันที่แล้ว";
			} else if (diffHours > 0) {
				return diffHours + " ชั่วโมงที่แล้ว";
			} else if (diffMinutes > 0) {
				return " ไม่กี่นาทีที่แล้ว";
			} else {
				if (diffSeconds < 0)
					diffSeconds = 0;
				return "ไม่กี่วินาทีที่แล้ว";
				// return diffSeconds + " วินาทีที่แล้ว";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "0";
	}

	// time start stop
	// http://stackoverflow.com/questions/5351483/calculate-date-time-difference-in-java
	public static String CalDifferentTimeProfileRemining(String str_start,
			String str_stop) throws ParseException {

		String dateStart = str_start;
		String dateStop = str_stop;

		// Custom date format
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		Date dateReal = new Date();

		Date d1 = null;
		Date d2 = null;
		try {
			d1 = format.parse(format.format(dateReal));
			d2 = format.parse(dateStop);

			// Get msec from each, and subtract.
			long diff = d2.getTime() - d1.getTime();

			long diffSeconds = diff / 1000 % 60;
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000) % 24;
			long diffDays = diff / (24 * 60 * 60 * 1000);
			long diffMonths = diff / (24 * 60 * 60 * 1000) / 30;
			long diffYears = diff / (24 * 60 * 60 * 1000) / 365;

			Log.v("S M H D", "" + diffSeconds + "_" + diffMinutes + "_"
					+ diffHours + "_" + diffDays);

			// 2015-04-07 20:50:20 ==> -8_-41_-5_-16516043
			// 08/04/2015 16.41 ==> -40_-11_-5_-16516043
			// -22_-23_-17_-16556794

			if (diffYears > 0) {
				return diffYears + "_Years";
			} else if (diffMonths > 0) {
				return diffMonths + "_Months";
			} else if (diffDays > 0) {
				return diffDays + "_Days";
			} else if (diffHours > 0) {
				return diffHours + "_Hours";
			} else if (diffMinutes > 0) {
				return diffMinutes + "_Minutes";
			} else {
				if (diffSeconds < 0)
					diffSeconds = 0;
				return diffSeconds + "_Second";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "0";
	}

	// time remining Percent
	// http://stackoverflow.com/questions/5351483/calculate-date-time-difference-in-java
	public static String CalDifferentTimeProfileReminingPercent(String str_start, String str_stop) throws ParseException {

		String dateStart = str_start;
		String dateStop = str_stop;

		// Custom date format
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		Date dateReal = new Date();

		Date dReal = null;
		Date dStop = null;
		Date dStart = null;
		try {
			dReal = format.parse(format.format(dateReal));
			dStop = format.parse(dateStop);
			dStart = format.parse(dateStart);

			// Get msec from each, and subtract.
			long diffAll = dStop.getTime() - dStart.getTime();
			long diffRemining = dStop.getTime() - dReal.getTime();
			
			long diffDaysAll = diffAll / (24 * 60 * 60 * 1000);
			long diffDaysRemining = diffRemining / (24 * 60 * 60 * 1000);
			
//			Log.v("diffAll", ""+diffDaysAll); // 51
//			Log.v("diffRemaining", ""+diffDaysRemining); // 38
//			Log.v("diffCal", ""+diffCal); // 74
//			Log.v("diffPercent360", ""+diffPercent360); // 266
			
			if(diffDaysAll <= 0){
				return "0";
			}else{
				int diffCal = (int) ((100*diffDaysRemining)/diffDaysAll);
				int diffPercent360 = (int) ((360*diffCal)/100);
				
				return ""+diffPercent360;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "0";
	}

	// timestamp Vi
	public static String CalAgoTimeVi(String str_date) throws ParseException {

		DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date date = (Date) formatter.parse(str_date + " 00:00:00");
		long output = date.getTime() / 1000L;
		String str = Long.toString(output);
		long timestamp = Long.parseLong(str);
		String str_timestamp = "" + timestamp;

		Timestamp stamp1 = new Timestamp(Long.parseLong(str_timestamp) * 1000);
		Date date1 = new Date(stamp1.getTime());

		Timestamp stamp2 = new Timestamp(System.currentTimeMillis());
		Date date2 = new Date(stamp2.getTime());

		try {
			// in milliseconds
			long diff = date2.getTime() - date1.getTime();
			// 2008-06-12
			long diffDays = diff / (24 * 60 * 60 * 1000);
			long diffMonth = diff / (30 * 24 * 60 * 60 * 1000);
			long diffYears = diff / (365 * 24 * 60 * 60 * 1000);

			// Log.v("S M H D", "" + diffSeconds + "_" + diffMinutes + "_"
			// + diffHours + "_" + diffDays);

			Log.v("S M H D", "" + diffDays + "_" + diffMonth + "_" + diffYears);

			// 2015-04-07 20:50:20 ==> -8_-41_-5_-16516043
			// 08/04/2015 16.41 ==> -40_-11_-5_-16516043
			// -22_-23_-17_-16556794

			if (diffYears > 0) {
				return diffYears + " " + "ปี";
			} else if (diffMonth > 0) {
				return diffMonth + " " + "เดือน";
			} else if (diffDays > 0) {
				return diffDays + " " + "วัน";
			} else {
				return "0";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "0";
	}

	// time start stop
	// http://stackoverflow.com/questions/5351483/calculate-date-time-difference-in-java
	public static String CalDifferentTimeAgoSetAlert(String str_start)
			throws ParseException {

		
        
		String dateStart = str_start;
		// Custom date format
		SimpleDateFormat format = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
		Date dateReal = new Date();
		
		Date d1 = null;
		Date dReal = null;
		try {
			dReal = format.parse(format.format(dateReal));
			d1 = format.parse(dateStart);

			// Get msec from each, and subtract.
			long diff = dReal.getTime()-d1.getTime();

			long diffSeconds = diff / (1000 % 60);
			long diffMinutes = diff / (60 * 1000) % 60;
			long diffHours = diff / (60 * 60 * 1000) % 24;
			long diffDays = diff / (24 * 60 * 60 * 1000);
			long diffMonths = diff / (24 * 60 * 60 * 1000) / 30;
			long diffYears = diff / (24 * 60 * 60 * 1000) / 365;

			Log.v("diff", format.format(dateReal)+"_"+dateStart+"_" + diff);
			Log.v("S M H D", "" + diffSeconds + "_" + diffMinutes + "_"
					+ diffHours + "_" + diffDays + "_" + diffMonths + "_" + diffYears);

			// 2016-03-31 10:30:24_1459395024000
			// 24_30_3_16891_563_46

			if (diffYears > 0) {
				return diffYears + " Years";
			} else if (diffMonths > 0) {
				return diffMonths + " Months";
			} else if (diffDays > 0) {
				return diffDays + " Days";
			} else if (diffHours > 0) {
				return diffHours + " Hours";
			} else if (diffMinutes > 0) {
				return diffMinutes + " Minutes";
			} else {
				if (diffSeconds < 0)
					diffSeconds = 0;
				return diffSeconds + " Second";
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return "0";
	}

}
