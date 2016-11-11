package com.app.bids;

import android.webkit.WebView;

public class CatalogGetSymbol {

	public String symbol;
	public WebView webview_chartiq;

	public WebView getWebview_chartiq() {
		return webview_chartiq;
	}

	public void setWebview_chartiq(WebView webview_chartiq) {
		this.webview_chartiq = webview_chartiq;
	}

	public String getOrderbook_id() {
		return orderbook_id;
	}

	public void setOrderbook_id(String orderbook_id) {
		this.orderbook_id = orderbook_id;
	}

	public String getColor_macd() {
		return color_macd;
	}

	public void setColor_macd(String color_macd) {
		this.color_macd = color_macd;
	}

	public String getColor_bb() {
		return color_bb;
	}

	public void setColor_bb(String color_bb) {
		this.color_bb = color_bb;
	}

	public String getColor_sto() {
		return color_sto;
	}

	public void setColor_sto(String color_sto) {
		this.color_sto = color_sto;
	}

	public String getColor_rsi() {
		return color_rsi;
	}

	public void setColor_rsi(String color_rsi) {
		this.color_rsi = color_rsi;
	}

	public String getColor_ema() {
		return color_ema;
	}

	public void setColor_ema(String color_ema) {
		this.color_ema = color_ema;
	}

	public String market_id;
	public String orderbook_id;
	public String symbol_fullname_eng;
	public String symbol_fullname_thai;
	public String last_trade;
	public String volume;
	public String change;
	public String status_segmentId;

	public String color_macd;
	public String color_bb;
	public String color_sto;
	public String color_rsi;
	public String color_ema;

	public String getTrade_signal() {
		return trade_signal;
	}

	public void setTrade_signal(String trade_signal) {
		this.trade_signal = trade_signal;
	}

	public String getTrade_date() {
		return trade_date;
	}

	public void setTrade_date(String trade_date) {
		this.trade_date = trade_date;
	}

	public String trade_signal;
	public String trade_date;

	public String getStatus_segmentId() {
		return status_segmentId;
	}

	public void setStatus_segmentId(String status_segmentId) {
		this.status_segmentId = status_segmentId;
	}

	public String percentChange;

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getMarket_id() {
		return market_id;
	}

	public void setMarket_id(String market_id) {
		this.market_id = market_id;
	}

	public String getSymbol_fullname_eng() {
		return symbol_fullname_eng;
	}

	public void setSymbol_fullname_eng(String symbol_fullname_eng) {
		this.symbol_fullname_eng = symbol_fullname_eng;
	}

	public String getSymbol_fullname_thai() {
		return symbol_fullname_thai;
	}

	public void setSymbol_fullname_thai(String symbol_fullname_thai) {
		this.symbol_fullname_thai = symbol_fullname_thai;
	}

	public String getLast_trade() {
		return last_trade;
	}

	public void setLast_trade(String last_trade) {
		this.last_trade = last_trade;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getChange() {
		return change;
	}

	public void setChange(String change) {
		this.change = change;
	}

	public String getPercentChange() {
		return percentChange;
	}

	public void setPercentChange(String percentChange) {
		this.percentChange = percentChange;
	}

}
