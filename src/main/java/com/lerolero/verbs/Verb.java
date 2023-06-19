package com.lerolero.verbs.models;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("verbs")
public class Verb {

	@Id
	private String id;
	private String continuous;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getContinuous() {
		return continuous;
	}

	public void setContinuous(String continuous) {
		this.continuous = continuous;
	}

}
