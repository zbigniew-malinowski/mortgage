package pl.zm.mortgage;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.format.Formatter;
import org.springframework.format.number.NumberFormatter;
import org.vaadin.addon.customfield.CustomField;

import pl.zm.mortgage.calc.InputData;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.Validator;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.util.PropertyFormatter;
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

	private Formatter<Number> intFormatter = new NumberFormatter("#,###");
	private Formatter<Number> doubleFormatter = new NumberFormatter("#0.00");

	private static final long serialVersionUID = 1887933063943628642L;

	private List<FormEditedListener> listeners = new LinkedList<TextFieldFactory.FormEditedListener>();
	private MessageSource messageSource;
	private InputData inputData;

	public TextFieldFactory(MessageSource messageSource, InputData inputData) {
		this.messageSource = messageSource;
		this.inputData = inputData;
	}

	@Override
	public Field createField(Item item, Object propertyId, Component uiContext) {

		final FormField tf = new FormField(item, propertyId);
		// tf.addListener(new ValueChangeListener() {
		//
		// /**
		// *
		// */
		// private static final long serialVersionUID = 2441370384729839091L;
		//
		// public void valueChange(ValueChangeEvent event) {
		// try {
		// tf.validate();
		// } catch (InvalidValueException e) {
		// return;
		// }
		// fireValueChange();
		//
		// }
		// });

		return tf;
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

		private static final long serialVersionUID = -5021367534319732659L;

		private Class<?> type;

		private TextField wrapped;

		@Override
		public boolean isValid() {
			return wrapped.isValid();
		}
		
		@Override
		public void validate() throws InvalidValueException {
			requestRepaint();
			wrapped.validate();
		}
		
		@Override
		public void commit() throws SourceException, InvalidValueException {
			wrapped.commit();
		}

		public FormField(Item item, Object propertyId) {
			String propertyName = (String) propertyId;
			Property originalProperty = item.getItemProperty(propertyId);
			this.type = (Class<?>) originalProperty.getType();

			HorizontalLayout hl = new HorizontalLayout();
			hl.setWidth("100%");
			Label caption = new Label(getMessage(propertyName));
			hl.addComponent(caption);
			// BeanPropertyFormatter dataSource = new
			// BeanPropertyFormatter(originalProperty);
			Property dataSource = originalProperty;
			wrapped = new TextField(dataSource);
			wrapped.setImmediate(true);
			wrapped.setRequired(true);
			wrapped.setWriteThrough(true);
			setPropertyDataSource(dataSource);
			wrapped.setTextChangeEventMode(TextChangeEventMode.TIMEOUT);
			wrapped.setTextChangeTimeout(500);
			wrapped.addListener(new TextChangeListener() {

				public void textChange(TextChangeEvent event) {
					wrapped.setValue(event.getText());
//					form.commit()
					if (wrapped.isValid())
						TextFieldFactory.this.fireValueChange();
				}
			});
			wrapped.addListener(new ValueChangeListener() {

				public void valueChange(com.vaadin.data.Property.ValueChangeEvent event) {
					// tf.validate();
					// tf.setValue(event.getText());
					// tf.commit();
					// commit();
//					if (wrapped.isValid())
//						TextFieldFactory.this.fireValueChange();

				}

			});

			wrapped.addValidator(getValidator(type));
			if("flatPrice".equals(propertyName))
				wrapped.addValidator(new PriceValidator(inputData));
			if("budget".equals(propertyName))
				wrapped.addValidator(new BudgetValidator(inputData));

			wrapped.setValidationVisible(true);
			
			wrapped.setWidth("80px");
			hl.addComponent(wrapped);
			Label suffix = new Label(getSuffix(propertyName));
			suffix.setWidth("30px");
			hl.addComponent(suffix);
			hl.setSpacing(true);
			hl.setExpandRatio(caption, 1.0f);
			setCompositionRoot(hl);

			setImmediate(true);
			setWriteThrough(true);
			setWidth("300px");
		}

		private Validator getValidator(Class<?> type2) {
			if (Integer.class.equals(type2))
				return new IntegerValidator("Zły integer");
			if (Double.class.equals(type2))
				return new DoubleValidator("Zły double");
			return new NullValidator("null!", false);
		}

		private String getSuffix(String propertyName) {
			return getMessage("suffix." + propertyName);
		}

		private String getMessage(String code) {
			try {
				return messageSource.getMessage(code, null, Locale.getDefault());
			} catch (NoSuchMessageException e) {
				return code;
			}
		}

		@Override
		public Class<?> getType() {
			return type;
		}

	}

	@SuppressWarnings("unchecked")
	private class BeanPropertyFormatter extends PropertyFormatter {

		private Class<Number> type = Number.class;

		private static final long serialVersionUID = -4406098394615950241L;

		public BeanPropertyFormatter(Property itemProperty) {
			super(itemProperty);
			this.type = (Class<Number>) itemProperty.getType();

		}

		@Override
		public Object parse(String formattedValue) throws Exception {
			return getFormatter(type).parse(formattedValue, Locale.getDefault());
		}

		private Formatter<Number> getFormatter(Class<Number> type) {
			if ((Double.class.equals(type)))
				return doubleFormatter;
			return intFormatter;

		}

		@Override
		public String format(Object value) {
			Number number = (Number) value;
			return getFormatter(type).print(number, Locale.getDefault());
		}

	}

	public interface FormEditedListener extends ValueChangeListener {

	}
}