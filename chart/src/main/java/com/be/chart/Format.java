package com.be.chart;

import java.util.Locale;

public class Format {

    public static final Locale Culture = Locale.US;

    public static final double Epsilon = 0.0001d;

    public static final String nanValue = " \u2212 ";

    public static final String minus = "\u2212";


    public static String formatPositionVolume(long volume) {
        if (volume > 0) {
            return String.format(Culture, "+%d", volume);
        } else {
            return String.format(Culture, "-%d",  Math.abs(volume));
        }
    }

    public static String formatPriceWithCurrency(double price)
    {
        if (Double.isNaN(price) || Double.isInfinite(price)) return nanValue;
        double absPrice = Math.abs(price);
        if (price < 0)
            return String.format(Culture, minus + "$%.02f", absPrice);
        else
            return String.format(Culture, "$%.02f", absPrice);
    }

    public static String formatPriceWithPercent(double price)
    {
        if (Double.isNaN(price) || Double.isInfinite(price)) return nanValue;
        double absPrice = Math.abs(price);
        if (price < 0)
            return String.format(Culture, minus + "%.02f %%", absPrice);
        else
            return String.format(Culture, "%.02f %%", absPrice);
    }

}
