package pl.zm.mortgage.util;

import java.io.Serializable;

import org.springframework.context.support.ResourceBundleMessageSource;

public class SerializableMessageSource extends ResourceBundleMessageSource

 implements Serializable {

	private static final long serialVersionUID = 3981349563177943595L;

	public SerializableMessageSource() {
		super();
	}

}
