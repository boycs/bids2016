package com.app.bids;

import android.graphics.Color;

public class CatalogGridFav {
	
	public String id;
	public String favorite_number;
	public String favorite_symbol;
	public String user_id;
	public String created_at;
	public String updated_at;
	public String symbol_name;
	public String symbol_pk;
	public String market_id;
	public String symbol_fullname_eng;
	public String symbol_fullname_thai;
	public String last_trade;
	public String average_price;
	public String average_buy;
	public String average_sell;
	public String prev_close;
	public String open1;
	public String open2;
	public String close;
	public String adj_close;
	public String high;
	public String high52W;
	public String low;
	public String low52W;
	public String volume;
	public String value;
	public String change;
	public String percentChange;
	public String ceiling;
	public String floor;
	public String eps;
	public String yield;
	public String p_bv;
	public String p_e;
	public String percentChange1W;
	public String percentChange1M;
	public String percentChange3M;
	public String status;
	public String industry;
	public String SET50;
	public String SET100;
	public String SETHD;
	public String sector;
	public String security_type;
	public String benefit;
	public String listed_share;
	public String bv_nv;
	public String qp_e;
	public String financial_statement_date;
	public String dps;
	public String ending_date;
	public String pod;
	public String par_value;
	public String market_capitalization;
	public String tr_bv;
	public String npg_flag;
	public String acc_ds;
	public String acc_py;
	public String dpr;
	public String earning_date;
	public String allow_short_sell;
	public String allow_nvdr;
	public String allow_short_sell_on_nvdr;
	public String allow_ttf;
	public String stabilization_flag;
	public String notification_type;
	public String non_compliance;
	public String parent_symbol;
	public String orderbook_id;
	public String subscriptionGroupId;
	public String beneficial_signs;
	public String updateDatetime;
	public String split_flag;
	public String cg_score;
	public String y_rtn;
	
	public Boolean grid_empty;
	public String grid_favview;
	public Boolean grid_isCheck = false;
	public int grid_color = Color.RED;
	public int grid_originalColor = Color.parseColor("#2e9440");
	
		
	public Boolean getGrid_empty() {
		return grid_empty;
	}
	public void setGrid_empty(Boolean grid_empty) {
		this.grid_empty = grid_empty;
	}
	public String getGrid_favview() {
		return grid_favview;
	}
	public void setGrid_favview(String grid_favview) {
		this.grid_favview = grid_favview;
	}
	public Boolean getGrid_isCheck() {
		return grid_isCheck;
	}
	public void setGrid_isCheck(Boolean grid_isCheck) {
		this.grid_isCheck = grid_isCheck;
	}
	public int getGrid_color() {
		return grid_color;
	}
	public void setGrid_color(int grid_color) {
		this.grid_color = grid_color;
	}
	public int getGrid_originalColor() {
		return grid_originalColor;
	}
	public void setGrid_originalColor(int grid_originalColor) {
		this.grid_originalColor = grid_originalColor;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getFavorite_number() {
		return favorite_number;
	}
	public void setFavorite_number(String favorite_number) {
		this.favorite_number = favorite_number;
	}
	public String getFavorite_symbol() {
		return favorite_symbol;
	}
	public void setFavorite_symbol(String favorite_symbol) {
		this.favorite_symbol = favorite_symbol;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getCreated_at() {
		return created_at;
	}
	public void setCreated_at(String created_at) {
		this.created_at = created_at;
	}
	public String getUpdated_at() {
		return updated_at;
	}
	public void setUpdated_at(String updated_at) {
		this.updated_at = updated_at;
	}
	public String getSymbol_name() {
		return symbol_name;
	}
	public void setSymbol_name(String symbol_name) {
		this.symbol_name = symbol_name;
	}
	public String getSymbol_pk() {
		return symbol_pk;
	}
	public void setSymbol_pk(String symbol_pk) {
		this.symbol_pk = symbol_pk;
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
	public String getAverage_price() {
		return average_price;
	}
	public void setAverage_price(String average_price) {
		this.average_price = average_price;
	}
	public String getAverage_buy() {
		return average_buy;
	}
	public void setAverage_buy(String average_buy) {
		this.average_buy = average_buy;
	}
	public String getAverage_sell() {
		return average_sell;
	}
	public void setAverage_sell(String average_sell) {
		this.average_sell = average_sell;
	}
	public String getPrev_close() {
		return prev_close;
	}
	public void setPrev_close(String prev_close) {
		this.prev_close = prev_close;
	}
	public String getOpen1() {
		return open1;
	}
	public void setOpen1(String open1) {
		this.open1 = open1;
	}
	public String getOpen2() {
		return open2;
	}
	public void setOpen2(String open2) {
		this.open2 = open2;
	}
	public String getClose() {
		return close;
	}
	public void setClose(String close) {
		this.close = close;
	}
	public String getAdj_close() {
		return adj_close;
	}
	public void setAdj_close(String adj_close) {
		this.adj_close = adj_close;
	}
	public String getHigh() {
		return high;
	}
	public void setHigh(String high) {
		this.high = high;
	}
	public String getHigh52W() {
		return high52W;
	}
	public void setHigh52W(String high52w) {
		high52W = high52w;
	}
	public String getLow() {
		return low;
	}
	public void setLow(String low) {
		this.low = low;
	}
	public String getLow52W() {
		return low52W;
	}
	public void setLow52W(String low52w) {
		low52W = low52w;
	}
	public String getVolume() {
		return volume;
	}
	public void setVolume(String volume) {
		this.volume = volume;
	}
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
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
	public String getCeiling() {
		return ceiling;
	}
	public void setCeiling(String ceiling) {
		this.ceiling = ceiling;
	}
	public String getFloor() {
		return floor;
	}
	public void setFloor(String floor) {
		this.floor = floor;
	}
	public String getEps() {
		return eps;
	}
	public void setEps(String eps) {
		this.eps = eps;
	}
	public String getYield() {
		return yield;
	}
	public void setYield(String yield) {
		this.yield = yield;
	}
	public String getP_bv() {
		return p_bv;
	}
	public void setP_bv(String p_bv) {
		this.p_bv = p_bv;
	}
	public String getP_e() {
		return p_e;
	}
	public void setP_e(String p_e) {
		this.p_e = p_e;
	}
	public String getPercentChange1W() {
		return percentChange1W;
	}
	public void setPercentChange1W(String percentChange1W) {
		this.percentChange1W = percentChange1W;
	}
	public String getPercentChange1M() {
		return percentChange1M;
	}
	public void setPercentChange1M(String percentChange1M) {
		this.percentChange1M = percentChange1M;
	}
	public String getPercentChange3M() {
		return percentChange3M;
	}
	public void setPercentChange3M(String percentChange3M) {
		this.percentChange3M = percentChange3M;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getIndustry() {
		return industry;
	}
	public void setIndustry(String industry) {
		this.industry = industry;
	}
	public String getSET50() {
		return SET50;
	}
	public void setSET50(String sET50) {
		SET50 = sET50;
	}
	public String getSET100() {
		return SET100;
	}
	public void setSET100(String sET100) {
		SET100 = sET100;
	}
	public String getSETHD() {
		return SETHD;
	}
	public void setSETHD(String sETHD) {
		SETHD = sETHD;
	}
	public String getSector() {
		return sector;
	}
	public void setSector(String sector) {
		this.sector = sector;
	}
	public String getSecurity_type() {
		return security_type;
	}
	public void setSecurity_type(String security_type) {
		this.security_type = security_type;
	}
	public String getBenefit() {
		return benefit;
	}
	public void setBenefit(String benefit) {
		this.benefit = benefit;
	}
	public String getListed_share() {
		return listed_share;
	}
	public void setListed_share(String listed_share) {
		this.listed_share = listed_share;
	}
	public String getBv_nv() {
		return bv_nv;
	}
	public void setBv_nv(String bv_nv) {
		this.bv_nv = bv_nv;
	}
	public String getQp_e() {
		return qp_e;
	}
	public void setQp_e(String qp_e) {
		this.qp_e = qp_e;
	}
	public String getFinancial_statement_date() {
		return financial_statement_date;
	}
	public void setFinancial_statement_date(String financial_statement_date) {
		this.financial_statement_date = financial_statement_date;
	}
	public String getDps() {
		return dps;
	}
	public void setDps(String dps) {
		this.dps = dps;
	}
	public String getEnding_date() {
		return ending_date;
	}
	public void setEnding_date(String ending_date) {
		this.ending_date = ending_date;
	}
	public String getPod() {
		return pod;
	}
	public void setPod(String pod) {
		this.pod = pod;
	}
	public String getPar_value() {
		return par_value;
	}
	public void setPar_value(String par_value) {
		this.par_value = par_value;
	}
	public String getMarket_capitalization() {
		return market_capitalization;
	}
	public void setMarket_capitalization(String market_capitalization) {
		this.market_capitalization = market_capitalization;
	}
	public String getTr_bv() {
		return tr_bv;
	}
	public void setTr_bv(String tr_bv) {
		this.tr_bv = tr_bv;
	}
	public String getNpg_flag() {
		return npg_flag;
	}
	public void setNpg_flag(String npg_flag) {
		this.npg_flag = npg_flag;
	}
	public String getAcc_ds() {
		return acc_ds;
	}
	public void setAcc_ds(String acc_ds) {
		this.acc_ds = acc_ds;
	}
	public String getAcc_py() {
		return acc_py;
	}
	public void setAcc_py(String acc_py) {
		this.acc_py = acc_py;
	}
	public String getDpr() {
		return dpr;
	}
	public void setDpr(String dpr) {
		this.dpr = dpr;
	}
	public String getEarning_date() {
		return earning_date;
	}
	public void setEarning_date(String earning_date) {
		this.earning_date = earning_date;
	}
	public String getAllow_short_sell() {
		return allow_short_sell;
	}
	public void setAllow_short_sell(String allow_short_sell) {
		this.allow_short_sell = allow_short_sell;
	}
	public String getAllow_nvdr() {
		return allow_nvdr;
	}
	public void setAllow_nvdr(String allow_nvdr) {
		this.allow_nvdr = allow_nvdr;
	}
	public String getAllow_short_sell_on_nvdr() {
		return allow_short_sell_on_nvdr;
	}
	public void setAllow_short_sell_on_nvdr(String allow_short_sell_on_nvdr) {
		this.allow_short_sell_on_nvdr = allow_short_sell_on_nvdr;
	}
	public String getAllow_ttf() {
		return allow_ttf;
	}
	public void setAllow_ttf(String allow_ttf) {
		this.allow_ttf = allow_ttf;
	}
	public String getStabilization_flag() {
		return stabilization_flag;
	}
	public void setStabilization_flag(String stabilization_flag) {
		this.stabilization_flag = stabilization_flag;
	}
	public String getNotification_type() {
		return notification_type;
	}
	public void setNotification_type(String notification_type) {
		this.notification_type = notification_type;
	}
	public String getNon_compliance() {
		return non_compliance;
	}
	public void setNon_compliance(String non_compliance) {
		this.non_compliance = non_compliance;
	}
	public String getParent_symbol() {
		return parent_symbol;
	}
	public void setParent_symbol(String parent_symbol) {
		this.parent_symbol = parent_symbol;
	}
	public String getOrderbook_id() {
		return orderbook_id;
	}
	public void setOrderbook_id(String orderbook_id) {
		this.orderbook_id = orderbook_id;
	}
	public String getSubscriptionGroupId() {
		return subscriptionGroupId;
	}
	public void setSubscriptionGroupId(String subscriptionGroupId) {
		this.subscriptionGroupId = subscriptionGroupId;
	}
	public String getBeneficial_signs() {
		return beneficial_signs;
	}
	public void setBeneficial_signs(String beneficial_signs) {
		this.beneficial_signs = beneficial_signs;
	}
	public String getUpdateDatetime() {
		return updateDatetime;
	}
	public void setUpdateDatetime(String updateDatetime) {
		this.updateDatetime = updateDatetime;
	}
	public String getSplit_flag() {
		return split_flag;
	}
	public void setSplit_flag(String split_flag) {
		this.split_flag = split_flag;
	}
	public String getCg_score() {
		return cg_score;
	}
	public void setCg_score(String cg_score) {
		this.cg_score = cg_score;
	}
	public String getY_rtn() {
		return y_rtn;
	}
	public void setY_rtn(String y_rtn) {
		this.y_rtn = y_rtn;
	}
	public String getRoe() {
		return roe;
	}
	public void setRoe(String roe) {
		this.roe = roe;
	}
	public String getMagic1() {
		return magic1;
	}
	public void setMagic1(String magic1) {
		this.magic1 = magic1;
	}
	public String getMagic2() {
		return magic2;
	}
	public void setMagic2(String magic2) {
		this.magic2 = magic2;
	}
	public String getMg() {
		return mg;
	}
	public void setMg(String mg) {
		this.mg = mg;
	}
	public String getD_e() {
		return d_e;
	}
	public void setD_e(String d_e) {
		this.d_e = d_e;
	}
	public String getNpm() {
		return npm;
	}
	public void setNpm(String npm) {
		this.npm = npm;
	}
	public String getRoa() {
		return roa;
	}
	public void setRoa(String roa) {
		this.roa = roa;
	}
	public String getFfloat() {
		return ffloat;
	}
	public void setFfloat(String ffloat) {
		this.ffloat = ffloat;
	}
	public String getPeg() {
		return peg;
	}
	public void setPeg(String peg) {
		this.peg = peg;
	}
	public String roe;
	public String magic1;
	public String magic2;
	public String mg;
	public String d_e;
	public String npm;
	public String roa;
	public String ffloat;
	public String peg;

}
