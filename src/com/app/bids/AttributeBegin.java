package com.app.bids;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.support.v4.view.ViewPager;

public class AttributeBegin {

	// ============ News ==============
	// -------- carousel
	// public final static int PAGES = 5;
	public final static int LOOPS = 1000;
	// public final static int FIRST_PAGE = PAGES * LOOPS / 2;
	public final static float BIG_SCALE = 1f; // old 1.0f;
	public final static float SMALL_SCALE = 0f; // old 0.8f;
	public final static float DIFF_SCALE = BIG_SCALE - SMALL_SCALE;
	public static int PAGES = 0;
	public static int FIRST_PAGE = 0;
	// -------- data	
	public static JSONArray contentGetNews = null; // FragmentChangeActivity.url_bidschart+"/service/getNews?offset=1&limit=10
	public static JSONArray contentGetArticleByCategoryHot = null; // FragmentChangeActivity.url_bidschart+"/service/getArticleByCategory?offset=1&limit=3&category=2

	// FragmentChangeActivity.url_bidschart+"/service/getHotSymbol
	public static JSONArray contentGetHotSymbol = null; // carousel
	public static JSONObject contentGetNewsSelect = null;
	public static ArrayList<CatalogNews> list_getNews = new ArrayList<CatalogNews>();

	// ============ Status Loading ==============
	// 0 ให้โหลดใหม่
	// 1 ไม่ต้องโหลดใหม่ เช็คข้อมูลเดิม
	// *** ถ้ามีการอัพเดท ให้ status เป็น 0 เพื่อโหลดใหม่ เช่น like, comment,
	// follow

	public static String statusLoadNews = "0";
	public static String statusLoadFav = "0";
	public static String statusLoadSystemTradeCdc = "0";
	public static String statusLoadSystemTradeBreakOut = "0";
	public static String statusLoadSystemTradeEsu = "0";
	
	
	

}
