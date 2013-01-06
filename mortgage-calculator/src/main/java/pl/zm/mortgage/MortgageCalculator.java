package pl.zm.mortgage;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;

import pl.zm.mortgage.calc.Calculations;
import pl.zm.mortgage.calc.ChartData;
import pl.zm.mortgage.calc.Controller;
import pl.zm.mortgage.calc.InputData;

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
import com.invient.vaadin.charts.InvientChartsConfig.NumberYAxis;
import com.invient.vaadin.charts.InvientChartsConfig.Stacking;
import com.invient.vaadin.charts.InvientChartsConfig.SymbolMarker;
import com.invient.vaadin.charts.InvientChartsConfig.XAxis;
import com.invient.vaadin.charts.InvientChartsConfig.YAxis;
import com.invient.vaadin.charts.InvientChartsConfig.YAxisDataLabel;
import com.vaadin.Application;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class MortgageCalculator extends Application {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7128604913110613494L;
	private HorizontalLayout center;
	private Controller controller = new Controller();
	
	@Override
	public void init() {
		Window window = new Window();
		setMainWindow(window);
		VerticalLayout vl = new VerticalLayout();
		window.addComponent(vl);
		center = new HorizontalLayout();
		vl.addComponent(center);
		vl.setComponentAlignment(center, Alignment.TOP_CENTER);
		center.setWidth("1200px");

		Panel dataPanel = new Panel();
		dataPanel.setWidth("320px");
		dataPanel.setHeight("350px");

		Form form = new Form();
		InputData input = new InputData();
		form.setItemDataSource(new BeanItem<InputData>(input));
		dataPanel.addComponent(form);
		center.addComponent(dataPanel);

		ChartData<?>[] data = controller.calculate(input);
		
		center.addComponent(new Chart("Total flat cost", "PLN", data[0]));
		center.addComponent(new Chart("Total time", "Years", data[1]));
	}


	

}
