package com.be.chart.ui.widget;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;

import com.be.chart.Format;
import com.be.chart.R;
import com.be.chart.charts.PriceSeries;
import com.be.ui.widget.BaseWidget;
import com.be.chart.databinding.WidgetChartBinding;

import com.scichart.charting.model.AnnotationCollection;
import com.scichart.charting.model.dataSeries.IXyDataSeries;
import com.scichart.charting.numerics.labelProviders.ILabelProvider;
import com.scichart.charting.numerics.labelProviders.NumericLabelProvider;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.annotations.HorizontalLineAnnotation;
import com.scichart.charting.visuals.annotations.LabelPlacement;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastMountainRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.drawing.canvas.RenderSurface;
import com.scichart.drawing.common.BrushStyle;
import com.scichart.drawing.common.LinearGradientBrushStyle;
import com.scichart.drawing.common.PenStyle;
import com.scichart.drawing.common.SolidPenStyle;
import com.scichart.extensions.builders.SciChartBuilder;

import java.util.Collections;
import java.util.Date;

import lombok.Getter;

public class WidgetChart extends BaseWidget {

    @Getter
    private SciChartSurface sciChartSurface;
    private SciChartBuilder sciChartBuilder;
    private IAxis xAxisDates;
    private IAxis yAxisPrice;
    private AnnotationCollection annotations;
    private IXyDataSeries<Date, Double> dataSeries;
    private FastMountainRenderableSeries mountainSeries;
    public WidgetChartBinding b;

    public WidgetChart(Context context) {
        super(context);
    }

    public WidgetChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WidgetChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void init(Context context, @Nullable AttributeSet attrs) {
        super.init(context, attrs);
        b = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.widget_chart, this, true);
        sciChartSurface = b.chart;
        //Hack - prevent black flick with chart, change render surface to canvas
        sciChartSurface.setRenderSurface(new RenderSurface(getContext()));
        applyTheme();
        initChartSurface();
    }

    private void applyTheme() {
        sciChartSurface.setBackgroundColor(getResources().getColor(R.color.white));
    }

    private void initChartSurface() {
        sciChartSurface.setRenderSurface(new RenderSurface(getContext()));
        SciChartBuilder.init(sciChartSurface.getContext());
        sciChartBuilder = SciChartBuilder.instance();
        xAxisDates = sciChartBuilder
                .newCategoryDateAxis()
                .withDrawMajorBands(false)
                .withDrawMajorGridLines(false)
                .withDrawMinorGridLines(false)
                .withGrowBy(0.01d, 0.03d)
                .build();
        xAxisDates.setAutoRange(AutoRange.Always);
        xAxisDates.setMaxAutoTicks(6);
        yAxisPrice = sciChartBuilder.newNumericAxis()
                .withGrowBy(0.1d, 0.1d)
                .withDrawMajorBands(false)
                .withDrawMajorGridLines(false)
                .withDrawMinorGridLines(false)
                .withLabelProvider(getValueAxisLabelProvider())
                .build();

        yAxisPrice.setAutoRange(AutoRange.Always);
        yAxisPrice.setMaxAutoTicks(5);
        mountainSeries = sciChartBuilder.newMountainSeries()
                .build();
        applySeriesTheme();
        sciChartSurface.getXAxes().add(xAxisDates);
        sciChartSurface.getYAxes().add(yAxisPrice);
        sciChartSurface.getRenderableSeries().add(mountainSeries);
    }

    private void applySeriesTheme() {
        // Create a PenStyle for Stroke
        PenStyle strokeStyle = new SolidPenStyle(getResources().getColor(R.color.accent), true, 2, null);

        // Create a BrushStyle for Fill
        BrushStyle fillStyle = new LinearGradientBrushStyle(0, 0, 0, 1, getResources().getColor(R.color.accent), getResources().getColor(R.color.white));

        mountainSeries.setStrokeStyle(strokeStyle);
        mountainSeries.setAreaStyle(fillStyle);

    }

    @SuppressWarnings("unused")
    public synchronized void drawMountainSeries(AppCompatActivity activity, PriceSeries priceData) {
        activity.runOnUiThread(() -> {
            UpdateSuspender suspender = (UpdateSuspender) sciChartSurface.suspendUpdates();
            dataSeries = sciChartBuilder.newXyDataSeries(Date.class, Double.class).build();
            dataSeries.append(priceData.getDateData(), priceData.getCloseData());
            mountainSeries.setDataSeries(dataSeries);
            sciChartSurface.getChartModifiers().add(sciChartBuilder.newModifierGroupWithDefaultModifiers().build());
            sciChartSurface.setAnnotations(getAnnotations());
            sciChartSurface.zoomExtents();
            applySeriesTheme();
            sciChartSurface.resumeUpdates(suspender);
            suspender.dispose();
        });
    }

    @SuppressWarnings("unused")
    public synchronized void drawCandleStickSeries(AppCompatActivity activity, PriceSeries priceData) {
        activity.runOnUiThread(() -> {
            UpdateSuspender suspender = (UpdateSuspender) sciChartSurface.suspendUpdates();
            dataSeries = sciChartBuilder.newXyDataSeries(Date.class, Double.class).build();
            dataSeries.append(priceData.getDateData(), priceData.getCloseData());
            mountainSeries.setDataSeries(dataSeries);
            sciChartSurface.getChartModifiers().add(sciChartBuilder.newModifierGroupWithDefaultModifiers().build());
            sciChartSurface.setAnnotations(getAnnotations());
            sciChartSurface.zoomExtents();
            applySeriesTheme();
            sciChartSurface.resumeUpdates(suspender);
            suspender.dispose();
        });
    }

    private ILabelProvider getValueAxisLabelProvider() {
        return new NumericLabelProvider() {
            @Override
            public CharSequence formatLabel(Comparable dataValue) {
                return Format.formatPriceWithPercent((Double) dataValue);
            }
        };
    }

    private AnnotationCollection getAnnotations() {
        annotations = new AnnotationCollection();
        if (dataSeries.getCount() < 2) {
            return annotations;
        }
        Double lastPrice = dataSeries.getYValues().get(dataSeries.getCount() - 1);

        //   if (position == null) {
        Collections.addAll(annotations, getHorizontalLineLastPrice(lastPrice), getHorizontalLineLabel(lastPrice));
   /*     } else {
            if (position.getAveragePrice() != null) {
                Collections.addAll(annotations, getHorizontalLineLastPrice(lastPrice), getHorizontalLineOpenPrice(position), getHorizontalLineLabel(lastPrice));
            } else {
                Collections.addAll(annotations, getHorizontalLineLastPrice(lastPrice), getHorizontalLineLabel(lastPrice));
            }
        }*/
        return annotations;
    }

    private HorizontalLineAnnotation getHorizontalLineLastPrice(Double doubleValue) {
        return sciChartBuilder.newHorizontalLineAnnotation()
                .withStroke(1, getResources().getColor(R.color.accent))
                .withBackgroundColor(getResources().getColor(R.color.accent))
                .withY1(doubleValue).
                        build();
    }

    private HorizontalLineAnnotation getHorizontalLineLabel(Double doubleValue) {
        return sciChartBuilder.newHorizontalLineAnnotation()
                .withStroke(1, getResources().getColor(R.color.accent))
                .withY1(doubleValue)
                .withHorizontalGravity(Gravity.RIGHT)
                .withAnnotationLabel(LabelPlacement.Axis, Format.formatPriceWithPercent(doubleValue))
                .build();
    }

}