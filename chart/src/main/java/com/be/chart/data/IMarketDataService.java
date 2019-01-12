package com.be.chart.data;

import com.be.chart.charts.PriceBar;
import com.scichart.core.common.Action1;

public interface IMarketDataService {
    void subscribePriceUpdate(Action1<PriceBar> callback);

    void clearSubscriptions();

    PriceSeries getHistoricalData(int numberBars);

    void stopGenerator();
}
