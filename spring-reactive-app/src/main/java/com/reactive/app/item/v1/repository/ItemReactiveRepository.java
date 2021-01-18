package com.reactive.app.item.v1.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.reactive.app.item.Item;

import reactor.core.publisher.Flux;

public interface ItemReactiveRepository extends ReactiveMongoRepository<Item, String> {
	 
	Flux<Item> findByDescription(String description);
}
