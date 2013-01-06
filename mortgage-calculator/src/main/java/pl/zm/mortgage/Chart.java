package pl.zm.mortgage;

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
import com.invient.vaadin.charts.InvientChartsConfig.NumberXAxis;
import com.invient.vaadin.charts.InvientChartsConfig.NumberYAxis;
import com.invient.vaadin.charts.InvientChartsConfig.Stacking;
import com.invient.vaadin.charts.InvientChartsConfig.SymbolMarker;
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
		// List<String> x = Arrays.asList("1750", "1800", "1850", "1900",
		// "1950", "1999", "2050");
		// double[] data = {502, 635, 809, 947, 1402, 3634, 5268};
		List<Integer> dataX = data.getxAxis();

		T[] seriesNames = data.getSeriesNames();
		Double min = dataX.get(0).doubleValue();
		Double max = dataX.get(dataX.size() - 1).doubleValue();

		InvientChartsConfig chartConfig = new InvientChartsConfig();
		chartConfig.getGeneralChartConfig().setType(SeriesType.AREA);

		chartConfig.getTitle().setText(chartTitle);

		NumberXAxis xAxis = new NumberXAxis();

		xAxis.setMin(min);
		xAxis.setMax(max);

		Tick tick = new Tick();
		tick.setPlacement(TickmarkPlacement.ON);
		xAxis.setTick(tick);
		LinkedHashSet<XAxis> xAxesSet = new LinkedHashSet<InvientChartsConfig.XAxis>();
		xAxesSet.add(xAxis);
		chartConfig.setXAxes(xAxesSet);

		NumberYAxis yAxis = new NumberYAxis();

		yAxis.setTitle(new AxisTitle(titleY));
		yAxis.setLabel(new YAxisDataLabel());
		// yAxis.getLabel().setFormatterJsFunc("function() {" +
		// " return this.value / 1000; " + "}");

		LinkedHashSet<YAxis> yAxesSet = new LinkedHashSet<InvientChartsConfig.YAxis>();
		yAxesSet.add(yAxis);
		chartConfig.setYAxes(yAxesSet);

		// chartConfig.getTooltip().setFormatterJsFunc(
		// "function() {" +
		// " return ''+ this.x +': '+ $wnd.Highcharts.numberFormat(this.y, 0, ',') +' millions';"
		// + "}");

		AreaConfig areaCfg = new AreaConfig();
		areaCfg.setStacking(Stacking.NORMAL);
		areaCfg.setLineColor(new RGB(102, 102, 102));
		areaCfg.setLineWidth(1);

		SymbolMarker marker = new SymbolMarker();
		marker.setLineColor(new RGB(102, 102, 102));
		marker.setLineWidth(1);
		areaCfg.setMarker(marker);

		chartConfig.addSeriesConfig(areaCfg);

		InvientCharts chart = new InvientCharts(chartConfig);

		for (T series : seriesNames) {

			List<Integer> dataY = data.getDataSeries(series);
			addSeries(chart, series.name(), dataX, dataY);
		}

		addComponent(chart);
		chart.setWidth("100%");
		chart.setHeight("300px");
		setWidth("400px");
		setHeight("350px");
	}

	private void addSeries(InvientCharts chart, String seriesTitle, List<Integer> dataX, List<Integer> dataY) {
		XYSeries series = new XYSeries(seriesTitle);
		series.setSeriesPoints(getPoints(series, dataX, dataY));
		chart.addSeries(series);
	}

	private static LinkedHashSet<DecimalPoint> getPoints(Series series, List<Integer> values) {
		LinkedHashSet<DecimalPoint> points = new LinkedHashSet<DecimalPoint>();
		for (double value : values) {
			points.add(new DecimalPoint(series, value));
		}
		return points;
	}

	private static LinkedHashSet<DecimalPoint> getPoints(Series series, List<Integer> dataX, List<Integer> dataY) {
		LinkedHashSet<DecimalPoint> points = new LinkedHashSet<DecimalPoint>();
		for (int i = 0; i < dataX.size(); i++) {
			int x = dataX.get(i);
			int y = dataY.get(i);
			points.add(new DecimalPoint(series, x, y));
		}
		return points;
	}
}
