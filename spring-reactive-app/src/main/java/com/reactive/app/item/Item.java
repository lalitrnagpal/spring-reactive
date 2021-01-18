package com.reactive.app.item;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
	
	@Id
	private String id;
	private String description;
	private Double price;
	
	// @Data annotation gives the getters/ setters
}
