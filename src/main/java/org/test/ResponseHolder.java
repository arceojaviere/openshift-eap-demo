package org.test;

import javax.enterprise.context.ConversationScoped;
import javax.inject.Named;
import java.io.Serializable;

@ConversationScoped
@Named("holder")
public class ResponseHolder implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8622178361546075237L;

	private String response;

	public String getResponse() {
		return response;
	}

	public void setResponse(String response) {
		this.response = response;
	}
	
	
}
