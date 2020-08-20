package com.reactive.app.item.v1.repository;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.reactive.app.item.Item;
import com.reactive.app.item.v1.repository.ItemReactiveRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/*
 * 
 * Reactive Mongo Test cases using JUnit
 * Start MongoDB for this to work
 * 	
 */

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
	
	@Test
	public void findItemByDescription() {
		StepVerifier.create(itemReactiveRepository.findByDescription("Bose Headphones").log("findItemByDescription: "))
										.expectSubscription()
										.expectNextCount(1)
										.verifyComplete();
	}
	
	@Test
	public void saveItem() {
		Item item = new Item(null, "Google Home Mini", 30.00);
		// The save method of repository returns a Mono<Item>
		Mono<Item> savedItemMono = itemReactiveRepository.save(item).log("Saved Item");
		
		StepVerifier.create(savedItemMono)
					.expectSubscription()
					.expectNextMatches( itemCtr -> itemCtr.getId() != null && itemCtr.getDescription().equalsIgnoreCase("Google Home Mini") )
					.verifyComplete();
		
		
	}

	@Test
	public void updateItem() {
		
		double itemPrice = 520.00;
		
		Flux<Item> itemFlux = itemReactiveRepository.findByDescription("LG TV")
													.log("Updating Item")
													.map( item -> {
														item.setPrice(itemPrice);
														return item;
													})
													.flatMap( item -> itemReactiveRepository.save(item));
		
		StepVerifier.create(itemFlux)
					.expectSubscription()
					.expectNextMatches( itemCtr -> itemCtr.getPrice() == itemPrice )
					.verifyComplete();
	}	
	
	@Test
	public void deleteItem() {
		
		Mono<Void> itemMono = itemReactiveRepository.findById("ABC").log("Deleting Item")
													.map( item -> item.getId())
													.flatMap( id -> itemReactiveRepository.deleteById(id));    // delete returns Mono<Void>
		
//		StepVerifier.create(itemMono)				// We get a void it returns nothing
//					.expectSubscription()
//					.verifyComplete();
		
		// Checking result for null after deletion of item with id ABC  
//		StepVerifier.create(itemReactiveRepository.findById("ABC").log("Deleting Item"))
//							.expectSubscription()
//							.expectNextMatches( i -> i == null );
		
		// Checking result for 0 count when finding item with id ABC  
		StepVerifier.create(itemReactiveRepository.findById("ABC").log("Searching Item"))
							.expectSubscription()
							.expectNextCount( 0 );
	}
}
