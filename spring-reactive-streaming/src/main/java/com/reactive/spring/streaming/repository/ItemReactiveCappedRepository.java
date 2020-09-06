package com.reactive.spring.streaming.repository;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;

import com.reactive.spring.streaming.domain.ItemCapped;

import reactor.core.publisher.Flux;

public interface ItemReactiveCappedRepository extends ReactiveMongoRepository<ItemCapped, String> {
	 
	@Tailable
	Flux<ItemCapped> findItemsBy();
}
