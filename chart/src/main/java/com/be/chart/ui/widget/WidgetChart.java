package com.be.chart.ui.widget;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;

import com.be.ui.widget.BaseWidget;
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

import element.business.charts.PriceSeries;
import element.business.charts.R;
import element.business.charts.databinding.WidgetChartBinding;
import element.business.helpers.Format;
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
    //private CandlePeriod currentPeriod;
    //private Map<SimpleButton, CandlePeriod> periodButtons;

    public WidgetChartBinding b;
    //public ViewDataBinding b;

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
        //currentPeriod = CandlePeriod.YEAR1;
        //periodButtons = new LinkedHashMap<>();
    }

    private void applyTheme() {
        //sciChartSurface.setTheme(R.style.SciChartTheme_Light);
        sciChartSurface.setBackgroundColor(getResources().getColor(R.color.white));
    }

  /*  public void updateChartData(AppCompatActivity activity, PriceSeries priceData) {
        activity.runOnUiThread(() -> {
            SciChartBuilder.init(getContext());
            sciChartBuilder = SciChartBuilder.instance();
            final IAxis xBottomAxis = sciChartBuilder.newDateAxis().withGrowBy(0.1d, 0.1d).build();
            final IAxis yRightAxis = sciChartBuilder.newNumericAxis().withGrowBy(0.1d, 0.1d).build();
            //final PriceSeries priceData = DataManager.getInstance().getPriceDataIndu(activity);
            dataSeries = sciChartBuilder.newXyDataSeries(Date.class, Double.class).build();
            dataSeries.append(priceData.getDateData(), priceData.getCloseData());

            final IRenderableSeries rs1 = sciChartBuilder.newMountainSeries()
                    .withDataSeries(dataSeries)
                    .withStrokeStyle(element.business.ui.R.color.accent, 1, true)
                    //.withStrokeStyle(Ui.getColor(activity, R.color.accent), 1, true)
                    //Bug in sci_charts use later for right color
                    //.withAreaFillLinearGradientColors(Ui.getColor(activity, R.color.transparent), Ui.getColor(activity, R.color.white))
                    .build();

            UpdateSuspender.using(sciChartSurface, () -> {
                Collections.addAll(sciChartSurface.getXAxes(), xBottomAxis);
                Collections.addAll(sciChartSurface.getYAxes(), yRightAxis);
                Collections.addAll(sciChartSurface.getRenderableSeries(), rs1);
                Collections.addAll(getAnnotations());
                Collections.addAll(sciChartSurface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());
            });
        });

    }*/

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

        //xAxisDates.setDrawMajorGridLines(false);
        //xAxisDates.setDrawMinorGridLines(false);

        yAxisPrice = sciChartBuilder.newNumericAxis()
                .withGrowBy(0.1d, 0.1d)
                .withDrawMajorBands(false)
                .withDrawMajorGridLines(false)
                .withDrawMinorGridLines(false)
                .withLabelProvider(getValueAxisLabelProvider())
                .build();

        yAxisPrice.setAutoRange(AutoRange.Always);
        yAxisPrice.setMaxAutoTicks(5);

        //yAxisPrice.setDrawMajorGridLines(false);
        //yAxisPrice.setDrawMinorGridLines(false);

        mountainSeries = sciChartBuilder.newMountainSeries()
                //.withStrokeStyle(R.color.white, 1, true)
                //.withAreaFillLinearGradientColors(R.color.accent, R.color.black)
                //.withStrokeStyle(0xAAFFC9A8)
                //.withAreaFillLinearGradientColors(0xAAFF8D42,0x88090E11)
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

    public synchronized void updateChartFromCandles(AppCompatActivity activity, PriceSeries priceData) {
        activity.runOnUiThread(() -> {
            //if (!destroyed) {
            //busyNeedUpdate = true;
            // Delay needed to avoid flickering of previous data before new data is rendered visible
            //getHandler().postDelayed(this::scheduledSetBusyToFalse, UPDATE_CHART_VIEW_TIMEOUT_MS);
            //containerNoData.setVisibility(GONE);
            //!!! updateDateAxisLabelProvider();
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
            //  }
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