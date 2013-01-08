package pl.zm.mortgage;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.PropertyFormatter;
import com.vaadin.ui.Component;
import com.vaadin.ui.DefaultFieldFactory;
import com.vaadin.ui.Field;
import com.vaadin.ui.TextField;

class TextFieldFactory extends DefaultFieldFactory {

	private static final DecimalFormat DEFAULT_FORMAT = new DecimalFormat("#.## zł");

	private static final long serialVersionUID = 1887933063943628642L;

	private List<FormEditedListener> listeners = new LinkedList<TextFieldFactory.FormEditedListener>();
	private boolean readOnly;
	
	public TextFieldFactory() {
	}

	public TextFieldFactory(boolean readOnly) {
		this.readOnly = readOnly;
	}

	@Override
	public Field createField(Item item, Object propertyId, Component uiContext) {

		TextField tf = (TextField) super.createField(item, propertyId, uiContext);
		Property itemProperty = item.getItemProperty(propertyId);
		tf.setPropertyDataSource(new BeanPropertyFormatter(itemProperty, (String) propertyId));
		tf.addListener(new ValueChangeListener() {

			/**
			 * 
			 */
			private static final long serialVersionUID = 2441370384729839091L;

			public void valueChange(ValueChangeEvent event) {
				fireValueChange();

			}
		});
		tf.setReadOnly(readOnly);
		tf.setImmediate(true);
		tf.setWriteThrough(true);
		tf.setWidth("60px");
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

	@SuppressWarnings("unchecked")
	private class BeanPropertyFormatter extends PropertyFormatter {

		
		/**
		 * 
		 */
		private static final long serialVersionUID = -4406098394615950241L;
		private DecimalFormat df = DEFAULT_FORMAT;

		public BeanPropertyFormatter(Property itemProperty, String propertyName) {
			super(itemProperty);
		}

		@Override
		public Object parse(String formattedValue) throws Exception {
			if (df == null)
				return formattedValue;
			return df.parseObject(formattedValue);
		}

		@Override
		public String format(Object value) {
			if (value == null)
				return null;
			if (df == null)
				return value.toString();
			return df.format(value);
		}

	}

	public interface FormEditedListener extends ValueChangeListener {

	}
}