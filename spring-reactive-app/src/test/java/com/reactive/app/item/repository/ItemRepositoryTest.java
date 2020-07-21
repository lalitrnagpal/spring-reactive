package com.reactive.app.item.repository;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.reactive.app.item.Item;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@DataMongoTest
@RunWith(SpringRunner.class)
public class ItemRepositoryTest {

	@Autowired
	ItemReactiveRepository itemReactiveRepository;
	
	/* Ids for Item are auto-generated so passed as null here */
	List<Item> itemsList = Arrays.asList(
									new Item(null, "Samsung TV", 400.0),
									new Item(null, "LG TV", 420.0),
									new Item(null, "Apple Watch", 299.99),
									new Item(null, "Beats Headphones", 149.99),
									new Item("ABC", "Bose Headphones", 149.99)
							);
	
	@Before
	public void setup() {
		// clear all data first
		itemReactiveRepository
		.deleteAll()
		.thenMany(Flux.fromIterable(itemsList))										// Flux<Item>
		.flatMap(itemReactiveRepository::save)										// Item
		.doOnNext( item -> System.out.println("Item inserted is -> " +item) )
		.blockLast();																// Wait until all operations above are completed
	}
	
	@Test
	public void testGetAllItems() {
		
		StepVerifier.create(itemReactiveRepository.findAll())
					.expectSubscription()
					.expectNextCount(5)
					.verifyComplete();
	}
	
	@Test
	public void testGetItemById() {
		
		StepVerifier.create(itemReactiveRepository.findById("ABC"))				// findById returns a Mono
					.expectSubscription()										// Step<Item>
					.expectNextMatches( i -> i.getDescription().equalsIgnoreCase("Bose Headphones"))   // expectNextMatches takes a Predicate
					.verifyComplete();
	}	

}
