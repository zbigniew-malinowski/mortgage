package pl.zm.mortgage;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.format.Formatter;
import org.springframework.format.number.NumberFormatter;
import org.vaadin.addon.customfield.CustomField;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.PropertyFormatter;
import com.vaadin.event.FieldEvents.TextChangeEvent;
import com.vaadin.event.FieldEvents.TextChangeListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.AbstractTextField.TextChangeEventMode;

class TextFieldFactory extends DefaultFieldFactory {

	private Formatter<Number> intFormatter = new NumberFormatter("#,###");
	private Formatter<Number> doubleFormatter = new NumberFormatter("#0.00");

	private static final long serialVersionUID = 1887933063943628642L;

	private List<FormEditedListener> listeners = new LinkedList<TextFieldFactory.FormEditedListener>();
	private MessageSource messageSource;

	public TextFieldFactory(MessageSource messageSource) {
		this.messageSource = messageSource;
	}

	@Override
	public Field createField(Item item, Object propertyId, Component uiContext) {

		FormField tf = new FormField(item, propertyId);
		tf.addListener(new ValueChangeListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 2441370384729839091L;

			public void valueChange(ValueChangeEvent event) {
				fireValueChange();

			}
		});

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

		public FormField(Item item, Object propertyId) {
			String propertyName = (String) propertyId;
			Property originalProperty = item.getItemProperty(propertyId);
			this.type = (Class<?>) originalProperty.getType();

			HorizontalLayout hl = new HorizontalLayout();
			hl.setWidth("100%");
			Label caption = new Label(getMessage(propertyName));
			hl.addComponent(caption);
			BeanPropertyFormatter dataSource = new BeanPropertyFormatter(originalProperty);
			final TextField tf = new TextField(dataSource);
			tf.setImmediate(true);
			setPropertyDataSource(dataSource);
			tf.setTextChangeEventMode(TextChangeEventMode.TIMEOUT);
			tf.setTextChangeTimeout(500);
			tf.addListener(new TextChangeListener() {
				
				private static final long serialVersionUID = 3713406843576017401L;

				public void textChange(TextChangeEvent event) {
					TextFieldFactory.this.fireValueChange();
				}
			});
			
			tf.setWidth("80px");
			hl.addComponent(tf);
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