package pl.zm.mortgage.ui;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import pl.zm.mortgage.calc.Controller;
import pl.zm.mortgage.calc.InputData;
import pl.zm.mortgage.calc.Money;
import pl.zm.mortgage.calc.Time;
import pl.zm.mortgage.ui.TextFieldFactory.FormEditedListener;
import pl.zm.mortgage.util.SerializableMessageSource;
import pl.zm.mortgage.util.SpringContextHelper;

import com.vaadin.Application;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.BeanItem;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Form;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.Runo;

public class MortgageCalculator extends Application {

	private static final long serialVersionUID = -7128604913110613494L;

	private static final Collection<?> FORM_FIELDS = Arrays.asList("budget", "flatPrice", "savings", "flatCost", "flatRent", "creditInterestRate",
			"depositInterestRate");

	private VerticalLayout charts;

	private SerializableMessageSource messageSource;
	private Controller controller;

	@Override
	public void init() {

		SpringContextHelper sch = new SpringContextHelper(this);
		controller = sch.getBean(Controller.class);
		messageSource = sch.getBean(SerializableMessageSource.class);

		Window window = new Window();
		setMainWindow(window);
		window.setSizeUndefined();
		VerticalLayout mainLayout = new VerticalLayout();
		window.setContent(mainLayout);
		mainLayout.setWidth("100%");
		mainLayout.setHeight(null);
		setTheme("mortgage");

		createTitle(mainLayout);

		BeanItem<InputData> item = new BeanItem<InputData>(new InputData());

		Chart<Money> moneyChart = new Chart<Money>(item, Money.class, controller, messageSource);
		Chart<Time> timeChart = new Chart<Time>(item, Time.class, controller, messageSource);

		FormWindow subwindow = new FormWindow(item);
		window.addWindow(subwindow);
		subwindow.init();
		subwindow.addFormEditedListener(timeChart);
		subwindow.addFormEditedListener(moneyChart);
		

		charts = new VerticalLayout();
		charts.setWidth("1024px");
		charts.addComponent(moneyChart);
		charts.addComponent(timeChart);

		mainLayout.addComponent(charts);
		mainLayout.setExpandRatio(charts, 1);
		mainLayout.setComponentAlignment(charts, Alignment.MIDDLE_CENTER);

	}

	private void createTitle(VerticalLayout mainLayout) {
		HorizontalLayout title = new HorizontalLayout();
		title.setHeight("100px");
		Label titleLabel = new Label(messageSource.getMessage("title"));
		titleLabel.setContentMode(Label.CONTENT_XHTML);
		titleLabel.setSizeUndefined();
		titleLabel.setStyleName("title-label");
		title.addComponent(titleLabel);
		title.setComponentAlignment(titleLabel, Alignment.MIDDLE_CENTER);
		mainLayout.addComponent(title);
		mainLayout.setComponentAlignment(title, Alignment.MIDDLE_CENTER);
	}

	private class FormWindow extends Window {

		private static final long serialVersionUID = -3280944599776142720L;

		private List<FormEditedListener> listeners = new LinkedList<FormEditedListener>();
		
		private BeanItem<InputData> item;
		
		public FormWindow(BeanItem<InputData> item) {
			this.item = item;

		}

		public void init() {
			VerticalLayout dataPanel = new VerticalLayout();
			dataPanel.setWidth("350px");
			dataPanel.setHeight("100%");

			final Form form = new Form();

			TextFieldFactory tff = new TextFieldFactory(messageSource, item.getBean());
			tff.addListener(new FormEditedListener() {

				private static final long serialVersionUID = 6375678755905041849L;

				public void valueChange(ValueChangeEvent event) {
					try {
						form.commit();
					} catch (InvalidValueException e) {
						return;
					}
					for (FormEditedListener l : listeners)
						l.valueChange(null);
				}
			});

			form.setFormFieldFactory(tff);
			form.setItemDataSource(item);
			form.setVisibleItemProperties(FORM_FIELDS);
			form.setInvalidCommitted(false);
			form.setValidationVisible(false);
			form.setValidationVisibleOnCommit(false);
			dataPanel.addComponent(form);
			setContent(dataPanel);
			setClosable(false);
			setResizable(false);
			setStyleName(Runo.WINDOW_DIALOG);
			center();
		}

		public void addFormEditedListener(FormEditedListener listener) {
			listeners.add(listener);
		}
	}

}
