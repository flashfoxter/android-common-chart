package com.be.chart.charts;

import com.scichart.core.model.DoubleValues;

/**
 * Created by Levashkin Konstantin on 10/5/17.
 */

public class DoubleSeries {

    public final DoubleValues xValues;
    public final DoubleValues yValues;

    public DoubleSeries(int capacity){
        xValues = new DoubleValues(capacity);
        yValues = new DoubleValues(capacity);
    }

    public void add(double x, double y){
        xValues.add(x);
        yValues.add(y);
    }
}
