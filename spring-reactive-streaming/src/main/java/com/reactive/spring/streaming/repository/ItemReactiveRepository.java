package com.reactive.spring.streaming.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;

import com.reactive.spring.streaming.domain.Item;
import com.reactive.spring.streaming.domain.ItemCapped;

import reactor.core.publisher.Flux;

public interface ItemReactiveRepository extends ReactiveMongoRepository<Item, String> {
	 
	Flux<Item> findItemsBy();
}
