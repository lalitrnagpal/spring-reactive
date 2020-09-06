package com.reactive.spring.streaming;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reactive.spring.streaming.domain.ItemCapped;
import com.reactive.spring.streaming.repository.ItemReactiveCappedRepository;

import reactor.core.publisher.Flux;

@RestController
public class ItemStreamController {

	@Autowired
	ItemReactiveCappedRepository itemReactiveCappedRepository;
	
	@GetMapping(value = ItemConstants.ITEM_STREAM_END_POINT, produces=MediaType.APPLICATION_STREAM_JSON_VALUE)
	public Flux<ItemCapped> getItemsStream() {
		return itemReactiveCappedRepository.findItemsBy();
	}
	
}
