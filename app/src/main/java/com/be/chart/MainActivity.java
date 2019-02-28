package com.be.chart;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.DecelerateInterpolator;

import com.be.appchart.BuildConfig;
import com.be.appchart.R;
import com.be.chart.charts.DataManager;
import com.be.chart.charts.PriceSeries;
import com.scichart.charting.model.dataSeries.IOhlcDataSeries;
import com.scichart.charting.model.dataSeries.OhlcDataSeries;
import com.scichart.charting.visuals.SciChartSurface;
import com.scichart.charting.visuals.axes.AutoRange;
import com.scichart.charting.visuals.axes.IAxis;
import com.scichart.charting.visuals.renderableSeries.FastCandlestickRenderableSeries;
import com.scichart.core.framework.UpdateSuspender;
import com.scichart.extensions.builders.SciChartBuilder;

import java.util.Collections;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private SciChartSurface sciChartSurface;
    private SciChartBuilder sciChartBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initLicense();
    }

    @Override
    protected void onResume() {
        super.onResume();
        initExample();
    }

    private void initLicense() {
        String license = BuildConfig.ApiKey;
        try {
            SciChartSurface.setRuntimeLicenseKey(license);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected void initExample() {
        PriceSeries priceSeries = DataManager.getInstance().getPriceDataIndu(this);
        int size = priceSeries.size();
        sciChartSurface = findViewById(R.id.wt_chart);
        SciChartBuilder.init(sciChartSurface.getContext());
        sciChartBuilder = SciChartBuilder.instance();
        sciChartBuilder = SciChartBuilder.instance();
        final IAxis xAxis = sciChartBuilder.newCategoryDateAxis().withVisibleRange(size - 30, size).withGrowBy(0, 0.1).build();
        final IAxis yAxis = sciChartBuilder.newNumericAxis().withGrowBy(0d, 0.1d).withAutoRangeMode(AutoRange.Always).build();
        IOhlcDataSeries<Date, Double> dataSeries = new OhlcDataSeries<>(Date.class, Double.class);
        dataSeries.append(priceSeries.getDateData(), priceSeries.getOpenData(), priceSeries.getHighData(), priceSeries.getLowData(), priceSeries.getCloseData());
        final FastCandlestickRenderableSeries rSeries = sciChartBuilder.newCandlestickSeries()
                .withStrokeUp(0xFF00AA00)
                .withFillUpColor(0x8800AA00)
                .withStrokeDown(0xFFFF0000)
                .withFillDownColor(0x88FF0000)
                .withDataSeries(dataSeries)
                .build();
        UpdateSuspender.using(sciChartSurface, () -> {
            Collections.addAll(sciChartSurface.getXAxes(), xAxis);
            Collections.addAll(sciChartSurface.getYAxes(), yAxis);
            Collections.addAll(sciChartSurface.getRenderableSeries(), rSeries);
            Collections.addAll(sciChartSurface.getChartModifiers(), sciChartBuilder.newModifierGroupWithDefaultModifiers().build());

            sciChartBuilder.newAnimator(rSeries).withWaveTransformation().withInterpolator(new DecelerateInterpolator()).withDuration(3000).withStartDelay(350).start();
        });
    }

}
