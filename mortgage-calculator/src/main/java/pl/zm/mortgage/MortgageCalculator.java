package pl.zm.mortgage;

import org.springframework.context.MessageSource;

import pl.zm.mortgage.calc.Controller;
import pl.zm.mortgage.calc.InputData;
import pl.zm.mortgage.calc.MoneySeries;
import pl.zm.mortgage.calc.TimeSeries;

import com.vaadin.Application;
import com.vaadin.data.util.BeanItem;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class MortgageCalculator extends Application {

	private static final long serialVersionUID = -7128604913110613494L;
	
	private VerticalLayout charts;
	
	MessageSource messageSource;
	Controller controller;
	
	@Override
	public void init() {
		
		SpringContextHelper sch = new SpringContextHelper(this);
		controller = sch.getBean(Controller.class);
		messageSource = sch.getBean(MessageSource.class);
		
		Window window = new Window();
		setMainWindow(window);
		window.setSizeFull();
		VerticalLayout mainLayout = new VerticalLayout();
		window.setContent(mainLayout);
		mainLayout.setSizeFull();
		
		
		HorizontalSplitPanel split = new HorizontalSplitPanel();
		split.setSizeFull();
		mainLayout.addComponent(split);
		mainLayout.setExpandRatio(split, 1);
		
		
		Panel dataPanel = new Panel();
		dataPanel.setWidth("350px");
		dataPanel.setHeight("100%");

		Form form = new Form();
		form.setFormFieldFactory(new TextFieldFactory());
		InputData input = new InputData();
		form.setItemDataSource(new BeanItem<InputData>(input));
		dataPanel.addComponent(form);
		split.setFirstComponent(dataPanel);
		
		charts = new VerticalLayout();
		charts.setSizeFull();
		split.setSecondComponent(charts);
		charts.setWidth("100%");
		split.setSplitPosition(350, Sizeable.UNITS_PIXELS);

		charts.addComponent(new Chart<MoneySeries>(MoneySeries.class, controller, messageSource));
		charts.addComponent(new Chart<TimeSeries>(TimeSeries.class, controller, messageSource));
	}


	

}
