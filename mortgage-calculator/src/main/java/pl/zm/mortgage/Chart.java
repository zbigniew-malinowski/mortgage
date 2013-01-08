package pl.zm.mortgage;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import pl.zm.mortgage.calc.ChartData;

import com.invient.vaadin.charts.Color.RGB;
import com.invient.vaadin.charts.InvientCharts;
import com.invient.vaadin.charts.InvientCharts.DecimalPoint;
import com.invient.vaadin.charts.InvientCharts.Series;
import com.invient.vaadin.charts.InvientCharts.SeriesType;
import com.invient.vaadin.charts.InvientCharts.XYSeries;
import com.invient.vaadin.charts.InvientChartsConfig;
import com.invient.vaadin.charts.InvientChartsConfig.AreaConfig;
import com.invient.vaadin.charts.InvientChartsConfig.AxisBase.AxisTitle;
import com.invient.vaadin.charts.InvientChartsConfig.AxisBase.Tick;
import com.invient.vaadin.charts.InvientChartsConfig.AxisBase.TickmarkPlacement;
import com.invient.vaadin.charts.InvientChartsConfig.CategoryAxis;
import com.invient.vaadin.charts.InvientChartsConfig.MarkerState;
import com.invient.vaadin.charts.InvientChartsConfig.NumberYAxis;
import com.invient.vaadin.charts.InvientChartsConfig.Stacking;
import com.invient.vaadin.charts.InvientChartsConfig.SymbolMarker;
import com.invient.vaadin.charts.InvientChartsConfig.SymbolMarker.Symbol;
import com.invient.vaadin.charts.InvientChartsConfig.Tooltip;
import com.invient.vaadin.charts.InvientChartsConfig.XAxis;
import com.invient.vaadin.charts.InvientChartsConfig.YAxis;
import com.invient.vaadin.charts.InvientChartsConfig.YAxisDataLabel;
import com.vaadin.ui.Panel;

public class Chart<T extends Enum<T>> extends Panel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5458031501384091462L;

	public Chart(String chartTitle, String titleY, ChartData<T> data) {
		List<Integer> dataX = data.getxAxis();

		T[] seriesNames = data.getSeriesNames();

		InvientChartsConfig chartConfig = new InvientChartsConfig();
		chartConfig.getGeneralChartConfig().setType(SeriesType.AREA);

		chartConfig.getTitle().setText(chartTitle);

		CategoryAxis xAxis = new CategoryAxis();
        xAxis.setCategories(Arrays.asList("", "1 rok", "2 lata", "3 lata", "4 lata", "5 lat",
                "6 lat", "7 lat", "8 lat", "9 lat", "10 lat"));
        xAxis.setTitle(new AxisTitle("Czas do kupna mieszkania"));
		
		Tick tick = new Tick();
		tick.setPlacement(TickmarkPlacement.ON);
		xAxis.setTick(tick);
		LinkedHashSet<XAxis> xAxesSet = new LinkedHashSet<InvientChartsConfig.XAxis>();
		xAxesSet.add(xAxis);
		chartConfig.setXAxes(xAxesSet);

		NumberYAxis yAxis = new NumberYAxis();

		yAxis.setTitle(new AxisTitle(titleY));
		yAxis.setLabel(new YAxisDataLabel());
		yAxis.getLabel().setFormatterJsFunc(
				"function() {" + " return this.value + ' 000 zł'; " + "}");

		LinkedHashSet<YAxis> yAxesSet = new LinkedHashSet<InvientChartsConfig.YAxis>();
		yAxesSet.add(yAxis);
		chartConfig.setYAxes(yAxesSet);

		Tooltip tooltip = new Tooltip();
//		tooltip.setFormatterJsFunc(String
//				.format("function() {"
//						+ " return '%s'+ this.series +': '+ $wnd.Highcharts.numberFormat(this.y, 0, ',') +'%s';"
//						+ "}", "Rok ", " miesięcy"));
		tooltip.setCrosshairs(true);
		tooltip.setShared(true);
		chartConfig.setTooltip(tooltip);

		AreaConfig areaCfg = new AreaConfig();
		areaCfg.setStacking(Stacking.NORMAL);
		areaCfg.setLineColor(new RGB(102, 102, 102));
		areaCfg.setLineWidth(1);

		SymbolMarker marker = new SymbolMarker();
		marker.setLineColor(new RGB(102, 102, 102));
		marker.setEnabled(false);
		marker.setSymbol(Symbol.CIRCLE);
		marker.setRadius(2);
		marker.setHoverState(new MarkerState(true));
		areaCfg.setMarker(marker);

		chartConfig.addSeriesConfig(areaCfg);

		InvientCharts chart = new InvientCharts(chartConfig);

		for (T series : seriesNames) {

			List<Integer> dataY = data.getDataSeries(series);
			addSeries(chart, series.name(), dataX, dataY);
		}

		addComponent(chart);
		chart.setWidth("100%");
		chart.setHeight("60%");
		setWidth("100%");
		setHeight("100%");
	}

	private void addSeries(InvientCharts chart, String seriesTitle,
			List<Integer> dataX, List<Integer> dataY) {
		XYSeries series = new XYSeries(seriesTitle);
		series.setSeriesPoints(getPoints(series, dataX, dataY));
		chart.addSeries(series);
	}

	private static LinkedHashSet<DecimalPoint> getPoints(Series series,
			List<Integer> values) {
		LinkedHashSet<DecimalPoint> points = new LinkedHashSet<DecimalPoint>();
		for (double value : values) {
			points.add(new DecimalPoint(series, value));
		}
		return points;
	}

	private static LinkedHashSet<DecimalPoint> getPoints(Series series,
			List<Integer> dataX, List<Integer> dataY) {
		LinkedHashSet<DecimalPoint> points = new LinkedHashSet<DecimalPoint>();
		for (int i = 0; i < dataX.size(); i++) {
			int x = dataX.get(i);
			int y = dataY.get(i);
			points.add(new DecimalPoint(series, x, y));
		}
		return points;
	}
}
