package com.reactive.spring.itemclient.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.reactive.spring.itemclient.domain.Item;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ItemControllerClient {

	// Item Reactive APIs host and port 
	WebClient webClient = WebClient.create("http://localhost:8080");
	
	@GetMapping("/client/retrieve")
	public Flux<Item> getAllItemsUsingRetrieve() {
		return webClient.get()
						.uri("/v1/items")
						.retrieve()
						.bodyToFlux(Item.class)
						.log("Items in the client project - retrieve way");
	}
	
	@GetMapping("/client/exchange")
	public Flux<Item> getAllItemsUsingExchange() {
		return webClient.get()
						.uri("/v1/items")
						.exchange()
						.flatMapMany( clientResponse -> clientResponse.bodyToFlux(Item.class))
						.log("Items in the client project - exchange way");
	}	

	@GetMapping("/client/singleitem")
	public Flux<Item> getOneItemUsingExchangeAndPathVariable() {
		String id = "MBZ";
		return webClient.get()
						.uri("/v1/fun/items/{id}", id)
						.exchange()
						.flatMapMany( clientResponse -> clientResponse.bodyToFlux(Item.class))
						.log("Items in the client project - exchange way");
	}	
	
	@PostMapping("/client/createItem")
	public Mono<Item> createItemRequestBody(@RequestBody Item item) {
		
		Mono<Item> monoItem = Mono.just(item);
		
		return webClient.post().uri("/v1/fun/items")
				.contentType(MediaType.APPLICATION_JSON)
				.body(monoItem, Item.class)
				.retrieve()
				.bodyToMono(Item.class)
				.log("Created a Item");
		
	}
	
	@PutMapping("/client/updateItem/{id}")
	public Mono<Item> updateItem(@PathVariable String id, @RequestBody Item item) {
		
		Mono<Item> monoItem = Mono.just(item);
		
		return webClient.put().uri("/v1/fun/items/{id}", id)
				.body(monoItem, Item.class)
				.retrieve()
				.bodyToMono(Item.class)
				.log("Updated Item is: ");
		
	}
	
	
	@DeleteMapping("/client/deleteItem/{id}")
	public Mono<Void> deleteItem(@PathVariable String id) {
		
		return webClient.delete().uri("/v1/fun/items/{id}", id)
				.retrieve()
				.bodyToMono(Void.class)
				.log("Updated Item is: ");
		
	}
	
	@GetMapping("/client/retrieve/error")
	public Flux<Item> errorRetrieve() {
		return webClient.get().uri("/fun/items/runtimeException")
				.retrieve()
				.onStatus(HttpStatus::is5xxServerError, clientResponse -> {
					Mono<String> errorMono = clientResponse.bodyToMono(String.class);
					return errorMono.flatMap(errorMessage -> {
						//TODO: Change this to log.error()
						System.err.println("ItemControllerCLient caught this exception: "+errorMessage);
						// throw new RuntimeException(errorMessage);
						throw new RuntimeException("A Runtime Exception has Occurred");
					});
				})
				.bodyToFlux(Item.class);
	}

	@GetMapping("/client/exchange/error")
	public Flux<Item> errorExchange() {
		return webClient.get()
				.uri("/fun/items/runtimeException")
				.exchange()
				.flatMapMany( clientResponse -> {
					if (clientResponse.statusCode().is5xxServerError()) {
						
						return clientResponse.bodyToMono(String.class)
								.flatMap( errorMessage -> {
									log.error("Error message in errorExchange method: "+ errorMessage);
									throw new RuntimeException(errorMessage);
								});
					} else {
						return clientResponse.bodyToFlux(Item.class);
					}
				});
	}
	
}
