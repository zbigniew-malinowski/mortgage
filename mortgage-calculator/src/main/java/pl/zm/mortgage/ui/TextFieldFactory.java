package pl.zm.mortgage.ui;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.springframework.context.NoSuchMessageException;
import org.vaadin.addon.customfield.CustomField;

import pl.zm.mortgage.calc.InputData;
import pl.zm.mortgage.util.SerializableMessageSource;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.validator.DoubleValidator;
import com.vaadin.data.validator.IntegerValidator;
import com.vaadin.data.validator.NullValidator;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;

class TextFieldFactory extends DefaultFieldFactory {

	private static final long serialVersionUID = 1887933063943628642L;

	private List<FormEditedListener> listeners = new LinkedList<TextFieldFactory.FormEditedListener>();
	private SerializableMessageSource messageSource;
	private InputData inputData;

	public TextFieldFactory(SerializableMessageSource messageSource, InputData inputData) {
		this.messageSource = messageSource;
		this.inputData = inputData;
	}

	@Override
	public Field createField(Item item, Object propertyId, Component uiContext) {

		return new FormField(item, propertyId);
	}

	protected void fireValueChange() {
		for (FormEditedListener l : listeners) {
			l.valueChange(null);
		}

	}

	public void addListener(FormEditedListener listener) {
		listeners.add(listener);
	}

	private class FormField extends CustomField {

		private static final String ERROR_INTEGER = "error.integer";
		private static final String ERROR_DOUBLE = "error.double";
		private static final String SUFFIX = "suffix.";

		private static final long serialVersionUID = -5021367534319732659L;

		private Class<?> type;
		private TextField wrapped;

		@Override
		public boolean isValid() {
			return wrapped.isValid();
		}

		@Override
		public void validate() throws InvalidValueException {
			wrapped.validate();
		}

		@Override
		public void commit() throws SourceException, InvalidValueException {
			wrapped.commit();
		}

		public FormField(Item item, Object propertyId) {
			String propertyName = (String) propertyId;
			Property dataSource = item.getItemProperty(propertyId);
			this.type = (Class<?>) dataSource.getType();

			wrapped = new TextField(dataSource);
			wrapped.setImmediate(true);
			wrapped.setRequired(true);
			wrapped.setWriteThrough(true);
			wrapped.setPropertyDataSource(dataSource);
			wrapped.setTextChangeEventMode(TextChangeEventMode.TIMEOUT);
			wrapped.setTextChangeTimeout(1000);
			wrapped.addListener(new TextChangeListener() {

				private static final long serialVersionUID = 6554742203409307004L;

				public void textChange(TextChangeEvent event) {
					wrapped.setValue(event.getText());
					if (wrapped.isValid())
						TextFieldFactory.this.fireValueChange();
				}
			});

			addValidation(propertyName);

			wrapped.setWidth("80px");
			
			Label suffix = new Label(messageSource.getMessage(SUFFIX + propertyName));
			suffix.setWidth("30px");
			
			HorizontalLayout hl = new HorizontalLayout();
			Label caption = new Label(messageSource.getMessage(propertyName));
			hl.addComponent(caption);
			hl.setWidth("100%");
			hl.addComponent(wrapped);
			hl.addComponent(suffix);
			hl.setSpacing(true);
			hl.setExpandRatio(caption, 1.0f);
			setCompositionRoot(hl);

			setWidth("300px");
		}

		private void addValidation(String propertyName) {
			if (Integer.class.equals(type))
				wrapped.addValidator(new IntegerValidator(messageSource.getMessage(ERROR_INTEGER)));
			if (Double.class.equals(type))
				wrapped.addValidator(new DoubleValidator(ERROR_DOUBLE));
			if ("flatPrice".equals(propertyName))
				wrapped.addValidator(new PriceValidator(inputData));
			if ("budget".equals(propertyName))
				wrapped.addValidator(new BudgetValidator(inputData));
			wrapped.setValidationVisible(true);
		}

		@Override
		public Class<?> getType() {
			return type;
		}

	}

	public interface FormEditedListener extends ValueChangeListener {

	}
}