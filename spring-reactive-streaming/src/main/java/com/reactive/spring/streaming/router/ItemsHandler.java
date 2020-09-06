package com.reactive.spring.streaming.router;

import static org.springframework.http.MediaType.APPLICATION_STREAM_JSON;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.reactive.spring.streaming.domain.ItemCapped;
import com.reactive.spring.streaming.repository.ItemReactiveCappedRepository;

import reactor.core.publisher.Mono;

@Component
public class ItemsHandler {

	@Autowired
	ItemReactiveCappedRepository itemReactiveCappedRepository; 
	
	public Mono<ServerResponse> itemsStream(ServerRequest request) {
		return ServerResponse.ok()
				.contentType(APPLICATION_STREAM_JSON)
				.body(itemReactiveCappedRepository.findItemsBy(), ItemCapped.class);
	}
	
}
