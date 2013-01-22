package pl.zm.mortgage.ui;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import org.springframework.beans.factory.annotation.Configurable;

import pl.zm.mortgage.calc.Controller;
import pl.zm.mortgage.calc.InputData;
import pl.zm.mortgage.ui.TextFieldFactory.FormEditedListener;
import pl.zm.mortgage.util.SerializableMessageSource;

import com.invient.vaadin.charts.Color;
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
import com.invient.vaadin.charts.InvientChartsConfig.SeriesConfig;
import com.invient.vaadin.charts.InvientChartsConfig.Stacking;
import com.invient.vaadin.charts.InvientChartsConfig.SymbolMarker;
import com.invient.vaadin.charts.InvientChartsConfig.SymbolMarker.Symbol;
import com.invient.vaadin.charts.InvientChartsConfig.Tooltip;
import com.invient.vaadin.charts.InvientChartsConfig.XAxis;
import com.invient.vaadin.charts.InvientChartsConfig.YAxis;
import com.invient.vaadin.charts.InvientChartsConfig.YAxisDataLabel;
import com.invient.vaadin.charts.Paint;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Window.Notification;

@Configurable(preConstruction = true)
public class Chart<T extends Enum<T>> extends Panel implements FormEditedListener {

	private static final String SERIES_ = "series.";
	private static final String SERIES_COLOR = "series.color.";
	private static final String FORMATTER_TOOLTIP = "formatter.tooltip";
	private static final String FORMATTER_LABEL = "formatter.label";
	private static final String AXISY_TITLE = "axisy.title";
	private static final String AXISX_TITLE = "axisx.title";
	private static final String TITLE = "title";

	private static final long serialVersionUID = 5458031501384091462L;

	private Controller controller;
	private SerializableMessageSource messageSource;
	private Class<T> type;

	private InvientCharts wrappedChart;
	private InputData data;

	public Chart(BeanItem<InputData> item, Class<T> type, Controller controller, SerializableMessageSource messageSource) {

		this.controller = controller;
		this.messageSource = messageSource;
		this.type = type;
		this.data = item.getBean();

		InvientChartsConfig chartConfig = new InvientChartsConfig();
		chartConfig.getGeneralChartConfig().setType(SeriesType.AREA);

		chartConfig.getTitle().setText(getMessage(TITLE));

		CategoryAxis xAxis = new CategoryAxis();
		xAxis.setCategories(getCategories());
		xAxis.setTitle(new AxisTitle(getMessage(AXISX_TITLE)));

		Tick tick = new Tick();
		tick.setPlacement(TickmarkPlacement.ON);
		xAxis.setTick(tick);
		LinkedHashSet<XAxis> xAxesSet = new LinkedHashSet<InvientChartsConfig.XAxis>();
		xAxesSet.add(xAxis);
		chartConfig.setXAxes(xAxesSet);

		NumberYAxis yAxis = new NumberYAxis();

		yAxis.setTitle(new AxisTitle(getMessage(AXISY_TITLE)));
		yAxis.setLabel(new YAxisDataLabel());
		yAxis.getLabel().setFormatterJsFunc(getMessage(FORMATTER_LABEL));

		LinkedHashSet<YAxis> yAxesSet = new LinkedHashSet<InvientChartsConfig.YAxis>();
		yAxesSet.add(yAxis);
		chartConfig.setYAxes(yAxesSet);

		Tooltip tooltip = new Tooltip();
		tooltip.setFormatterJsFunc(getMessage(FORMATTER_TOOLTIP));
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

		wrappedChart = new InvientCharts(chartConfig);

		for (T series : type.getEnumConstants()) {

			List<Integer> dataY = controller.calculate(new InputData(), type).getDataSeries(series);
			addSeries(wrappedChart, getSeriesName(series), dataY, getSeriesColor(series));
		}

		addComponent(wrappedChart);
		wrappedChart.setSizeFull();
		setWidth("1024px");
	}

	private Paint getSeriesColor(T series) {
		return createColor(getMessage(SERIES_COLOR + series.name().toLowerCase()));
	}

	private static Paint createColor(String hex) {

		int red = Integer.decode("#" + hex.substring(0, 2));
		int blue = Integer.decode("#" + hex.substring(4, 6));
		int green = Integer.decode("#" + hex.substring(2, 4));
		return new Color.RGBA(red, green, blue, 0.5f);
	}

	private String getSeriesName(T series) {
		return getMessage(SERIES_ + series.name().toLowerCase());
	}

	public void update() {
		try {
			for (T series : type.getEnumConstants()) {
				List<Integer> dataY = controller.calculate(data, type).getDataSeries(series);
				updateSeries(wrappedChart, getSeriesName(series), dataY);
			}
		} catch (IllegalStateException e) {
			getWindow().showNotification(e.getMessage(), Notification.TYPE_WARNING_MESSAGE);
		}

	}

	private String getKey(String code) {
		return code + "." + type.getSimpleName().toLowerCase();
	}

	private List<String> getCategories() {
		return Arrays.asList(getArrayMessage("categories"));
	}

	private String[] getArrayMessage(String code) {
		String message = getMessage(code);
		return message.split(";");
	}

	private String getMessage(String code) {
		return messageSource.getMessage(getKey(code));
	}

	private void addSeries(InvientCharts chart, String seriesTitle, List<Integer> dataY, Paint color) {
		SeriesConfig config = new SeriesConfig();
		config.setColor(color);
		XYSeries series = new XYSeries(seriesTitle, config);
		series.setSeriesPoints(getPoints(series, dataY));
		chart.addSeries(series);
	}

	private void updateSeries(InvientCharts chart, String seriesTitle, List<Integer> dataY) {
		XYSeries series = (XYSeries) chart.getSeries(seriesTitle);
		SeriesConfig c = series.getConfig();

		chart.removeSeries(seriesTitle);
		series = new XYSeries(seriesTitle, c);
		series.setSeriesPoints(getPoints(series, dataY));
		chart.addSeries(series);
	}

	private static LinkedHashSet<DecimalPoint> getPoints(Series series, List<Integer> values) {
		LinkedHashSet<DecimalPoint> points = new LinkedHashSet<DecimalPoint>();
		for (double value : values) {
			points.add(new DecimalPoint(series, value));
		}
		return points;
	}

	public void valueChange(ValueChangeEvent event) {
		update();

	}

}
