package pl.zm.mortgage.util;

import java.io.Serializable;
import java.util.Locale;

import org.springframework.context.support.ResourceBundleMessageSource;

public class SerializableMessageSource extends ResourceBundleMessageSource

 implements Serializable {

	private static final long serialVersionUID = 3981349563177943595L;
	private static final Locale locale = new Locale("pl", "PL");

	public SerializableMessageSource() {
		super();
	}

	public String getMessage(String key) {
		return super.getMessage(key, null, key, locale );
	}
	
	public String getMessage(String key, Object[] args) {
		return super.getMessage(key, args, key, locale );
	}
}
