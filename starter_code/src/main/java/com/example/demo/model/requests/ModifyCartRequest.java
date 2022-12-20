package com.example.demo.model.requests;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor @NoArgsConstructor
public class ModifyCartRequest {
	@JsonProperty
	private String username;
	@JsonProperty
	private long itemId;
	@JsonProperty
	private int quantity;
}
