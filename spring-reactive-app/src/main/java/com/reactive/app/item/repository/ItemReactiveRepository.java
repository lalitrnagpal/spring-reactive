package com.reactive.app.item.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.reactive.app.item.Item;

public interface ItemReactiveRepository extends ReactiveMongoRepository<Item, String> {
	 
}
