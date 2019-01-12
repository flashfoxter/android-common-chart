package com.be.chart.data;

import com.scichart.core.common.Action1;

import element.business.charts.PriceBar;

public interface IMarketDataService {
    void subscribePriceUpdate(Action1<PriceBar> callback);

    void clearSubscriptions();

    PriceSeries getHistoricalData(int numberBars);

    void stopGenerator();
}
