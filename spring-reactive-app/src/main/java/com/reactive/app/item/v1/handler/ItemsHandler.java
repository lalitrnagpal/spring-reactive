package com.reactive.app.item.v1.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.reactive.app.item.Item;
import com.reactive.app.item.v1.repository.ItemReactiveRepository;

import reactor.core.publisher.Mono;

@Component
public class ItemsHandler {

	@Autowired
	ItemReactiveRepository itemReactiveRepository;
	
	static Mono<ServerResponse> notFound = ServerResponse.notFound().build();
	
	public Mono<ServerResponse> getAllItems(ServerRequest serverRequest) {
		
		return ServerResponse.ok()
							.contentType(MediaType.APPLICATION_JSON)
							 .body(itemReactiveRepository.findAll(), Item.class);
	}

	public Mono<ServerResponse> getOneItem(ServerRequest serverRequest) {
		
		String id = serverRequest.pathVariable("id");
		return ServerResponse.ok()
							.contentType(MediaType.APPLICATION_JSON)
							 .body(itemReactiveRepository.findById(id), Item.class)
							 .switchIfEmpty( notFound );
	}	
	
	public Mono<ServerResponse> createItem(ServerRequest serverRequest) {
		Mono<Item> itemToCreate = serverRequest.bodyToMono( Item.class );
		
		return itemToCreate.flatMap( item -> ServerResponse.ok()
											.contentType( MediaType.APPLICATION_JSON )
											.body( itemReactiveRepository.save(item), Item.class));
	}  

	public Mono<ServerResponse> deleteItem(ServerRequest serverRequest) {
		String idOfItemToDelete = serverRequest.pathVariable("id");
		Mono<Void> deleteItem = itemReactiveRepository.deleteById(idOfItemToDelete);
		return ServerResponse.ok().contentType( MediaType.APPLICATION_JSON ).body(deleteItem, Void.class);
	}  

	public Mono<ServerResponse> updateItem(ServerRequest serverRequest) {
		String idOfItemToUpdate = serverRequest.pathVariable("id");
		
		Mono<Item> itemToUpdate = serverRequest.bodyToMono( Item.class )
															.flatMap( item -> {
																			Mono<Item> monoItem = itemReactiveRepository.findById(idOfItemToUpdate)
																									.flatMap( currentItem -> {
																										currentItem.setDescription( item.getDescription() );
																										currentItem.setPrice( item.getPrice() );
																										return itemReactiveRepository.save(currentItem);
																									});
																			return monoItem;
																		});
		
		return ServerResponse.ok()
				.contentType( MediaType.APPLICATION_JSON )
				.body(itemToUpdate, Item.class)
				.switchIfEmpty(notFound);
	}  
	
	public Mono<ServerResponse> itemsException(ServerRequest serverRequest) {
		System.err.println("From ItemsHandler.itemsException method: Runtime Exception Occurred");
		throw new RuntimeException("From ItemsHandler.itemsException method: Runtime Exception Occurred");
	}
	
}
