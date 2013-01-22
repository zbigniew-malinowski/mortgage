package pl.zm.mortgage;

import java.util.Arrays;
import java.util.Collection;

import org.springframework.context.MessageSource;

import pl.zm.mortgage.TextFieldFactory.FormEditedListener;
import pl.zm.mortgage.calc.Controller;
import pl.zm.mortgage.calc.InputData;
import pl.zm.mortgage.calc.Money;
import pl.zm.mortgage.calc.Time;

import com.vaadin.Application;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.terminal.Sizeable;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class MortgageCalculator extends Application {

	private static final long serialVersionUID = -7128604913110613494L;

	private static final Collection<?> FORM_FIELDS = Arrays.asList("budget", "flatPrice", "savings", "flatCost", "flatRent", "creditInterestRate", "depositInterestRate");
	
	private VerticalLayout charts;
	
	private MessageSource messageSource;
	private Controller controller;
	
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
		setTheme("mortgage");
		
		HorizontalSplitPanel split = new HorizontalSplitPanel();
		split.setSizeFull();
		HorizontalLayout title = new HorizontalLayout();
		title.setHeight("100px");
		
		Label titleLabel = new Label("<big>Kupić czy wynająć?</big>");
		titleLabel.setContentMode(Label.CONTENT_XHTML);
		titleLabel.setSizeUndefined();
		titleLabel.setStyleName("title-label"); 
        title.addComponent(titleLabel);
		
        mainLayout.addComponent(title);
        mainLayout.setComponentAlignment(title, Alignment.MIDDLE_CENTER);
        title.setComponentAlignment(titleLabel, Alignment.MIDDLE_CENTER);
        mainLayout.addComponent(split);
		mainLayout.setExpandRatio(split, 1);
		
		
		Panel dataPanel = new Panel();
		dataPanel.setWidth("350px");
		dataPanel.setHeight("100%");

		final Form form = new Form();
		InputData input = new InputData();
		TextFieldFactory tff = new TextFieldFactory(messageSource, input);
		form.setFormFieldFactory(tff);
		BeanItem<InputData> item = new BeanItem<InputData>(input);
		form.setItemDataSource(item);
		form.setVisibleItemProperties(FORM_FIELDS);
//		form.setImmediate(true);
		form.setInvalidCommitted(false);
		form.setValidationVisible(false);
		dataPanel.addComponent(form);
		
		
		split.setFirstComponent(dataPanel);
	
		charts = new VerticalLayout();
		charts.setSizeFull();
		split.setSecondComponent(charts);
		charts.setWidth("100%");
		split.setSplitPosition(350, Sizeable.UNITS_PIXELS);

		final Chart<Money> moneyChart = new Chart<Money>(item, Money.class, controller, messageSource);
		final Chart<Time> timeChart = new Chart<Time>(item, Time.class, controller, messageSource);
		
//		tff.addListener(moneyChart);
//		tff.addListener(timeChart);
		tff.addListener(new FormEditedListener() {
			
			public void valueChange(ValueChangeEvent event) {
				try {
					form.commit();
				} catch (InvalidValueException e) {
					return;
				}
				moneyChart.valueChange(null);
				timeChart.valueChange(null);
			}
		});
		
		charts.addComponent(moneyChart);
		charts.addComponent(timeChart);
		
	}
	
	


	

}
