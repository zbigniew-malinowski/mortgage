package pl.zm.mortgage;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Configurable;
import org.springframework.context.MessageSource;

import pl.zm.mortgage.TextFieldFactory.FormEditedListener;
import pl.zm.mortgage.calc.Controller;
import pl.zm.mortgage.calc.InputData;
import pl.zm.mortgage.calc.Calculations.InvalidDataException;

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

	private static final long serialVersionUID = 5458031501384091462L;

	private Controller controller;
	private MessageSource messageSource;
	private Class<T> type;

	private InvientCharts wrappedChart;
	private InputData data;
	
	public Chart(BeanItem<InputData> item, Class<T> type, Controller controller, MessageSource messageSource) {

		this.controller = controller;
		this.messageSource = messageSource;
		this.type = type;
		this.data = item.getBean();

		InvientChartsConfig chartConfig = new InvientChartsConfig();
		chartConfig.getGeneralChartConfig().setType(SeriesType.AREA);

		chartConfig.getTitle().setText(getChartTitle());

		CategoryAxis xAxis = new CategoryAxis();
		xAxis.setCategories(getCategories());
		xAxis.setTitle(new AxisTitle(getAxixXTitle()));

		Tick tick = new Tick();
		tick.setPlacement(TickmarkPlacement.ON);
		xAxis.setTick(tick);
		LinkedHashSet<XAxis> xAxesSet = new LinkedHashSet<InvientChartsConfig.XAxis>();
		xAxesSet.add(xAxis);
		chartConfig.setXAxes(xAxesSet);

		NumberYAxis yAxis = new NumberYAxis();

		yAxis.setTitle(new AxisTitle(getAxisYTitle()));
		yAxis.setLabel(new YAxisDataLabel());
		yAxis.getLabel().setFormatterJsFunc(getAxisLabelFormatter());

		LinkedHashSet<YAxis> yAxesSet = new LinkedHashSet<InvientChartsConfig.YAxis>();
		yAxesSet.add(yAxis);
		chartConfig.setYAxes(yAxesSet);

		Tooltip tooltip = new Tooltip();
		tooltip.setFormatterJsFunc(getTooltipFormatter());
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
		return createColor(getMessage("series.color." + series.name().toLowerCase(), null));
	}
	
	private static Paint createColor(String hex){
		
		int red = Integer.decode("#" + hex.substring(0, 2));
		int blue= Integer.decode("#" + hex.substring(4, 6));
		int green = Integer.decode("#" + hex.substring(2, 4));
		return new Color.RGBA(red, green, blue, 0.5f);
	}

	private String getSeriesName(T series) {
		String name = series.name().toLowerCase();
		return getMessage("series." + name, null);
	}
	
	public void update(){
		try {
			for (T series : type.getEnumConstants()) {
				List<Integer> dataY = controller.calculate(data, type).getDataSeries(series);
				updateSeries(wrappedChart, getSeriesName(series), dataY);
			}
		} catch (InvalidDataException e) {
			getWindow().showNotification(e.getMessage(), Notification.TYPE_WARNING_MESSAGE);
		}
		
	}

	private String getAxixXTitle() {
		return getMessage("axisx.title", null);
	}

	private String getKey(String code) {
		return String.format(code + ".%s", type.getSimpleName().toLowerCase());
	}

	private List<String> getCategories() {
		return Arrays.asList(getArrayMessage("categories", null));
	}

	private String getTooltipFormatter() {
		return getMessage("formatter.tooltip", null);
	}

	private String getAxisLabelFormatter() {
		
		return getMessage("formatter.label", null); 
	}

	private String[] getArrayMessage(String code, String[] params) {
		String message = getMessage(code, params);
		return message.split(";");
	}
	
	private String getMessage(String code, String[] params) {
		return messageSource.getMessage(getKey(code), params, Locale.getDefault());
	}

	private String getAxisYTitle() {
		return getMessage("axisy.title", null);
	}

	private String getChartTitle() {
		return getMessage("title", null);
	}

	private void addSeries(InvientCharts chart, String seriesTitle, List<Integer> dataY, Paint color) {
		SeriesConfig config = new SeriesConfig();
		config.setColor(color);
		XYSeries series = new XYSeries(seriesTitle, config );
		series.setSeriesPoints(getPoints(series, dataY));
		chart.addSeries(series);
	}
	
	private void updateSeries(InvientCharts chart, String seriesTitle, List<Integer> dataY) {
		XYSeries series = (XYSeries) chart.getSeries(seriesTitle);
//		series.removeAllPoints();
//		series.setSeriesPoints(getPoints(series, dataY));
//		chart.refresh();
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
