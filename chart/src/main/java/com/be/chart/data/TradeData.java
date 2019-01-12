package com.be.chart.data;

import java.util.Date;

public class TradeData {
    private Date tradeDate;
    private double tradePrice;
    private double tradeSize;

    public TradeData() {
    }

    public TradeData(Date tradeDate, double tradePrice, double tradeSize) {
        this.tradeDate = tradeDate;
        this.tradePrice = tradePrice;
        this.tradeSize = tradeSize;
    }

    public Date getTradeDate() {
        return tradeDate;
    }

    public void setTradeDate(Date tradeDate) {
        this.tradeDate = tradeDate;
    }

    public double getTradePrice() {
        return tradePrice;
    }

    public void setTradePrice(double tradePrice) {
        this.tradePrice = tradePrice;
    }

    public double getTradeSize() {
        return tradeSize;
    }

    public void setTradeSize(double tradeSize) {
        this.tradeSize = tradeSize;
    }
}
