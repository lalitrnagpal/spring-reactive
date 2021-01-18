package com.reactive.app.item.controller.v1;

import static com.reactive.app.item.ItemConstants.ITEM_END_POINT_V1;

import java.time.Duration;
import java.util.Random;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.reactive.app.item.Item;
import com.reactive.app.item.v1.repository.ItemReactiveRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
public class ItemController {

	@Autowired
	ItemReactiveRepository itemReactiveRepository;

//  Put centrally into a ControllerAdvice	
//	@ExceptionHandler(RuntimeException.class)
//	public ResponseEntity<String> handleRuntimeException(RuntimeException rtException) {
//		log.error("Exception caught in handleRuntimeException {} ", rtException);
//		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(rtException.getMessage());
//	}
	
	@GetMapping(ITEM_END_POINT_V1)
	public Flux<Item> getAllItems() {
		System.out.println("Invoked!");
		return itemReactiveRepository.findAll().delayElements(Duration.ofSeconds(2));
	}
	
	@GetMapping(ITEM_END_POINT_V1 + "/id")
	public Mono<ResponseEntity<Item>> getOneItem(@PathVariable String id) {
		return itemReactiveRepository.findById(id)
				.map(item -> new ResponseEntity<>(item, HttpStatus.OK))
				.defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	@PostMapping(ITEM_END_POINT_V1)
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<Item> createItem(@RequestBody Item item) {
		return itemReactiveRepository.save(item);
	}
	
	@DeleteMapping(ITEM_END_POINT_V1 + "/id")
	public Mono<Void> deleteItem(@PathVariable String id) {
		return itemReactiveRepository.deleteById(id);
	}
	
	@GetMapping(ITEM_END_POINT_V1 + "/runtimeException")
	public Flux<Item> runtimeException() {
		return itemReactiveRepository.findAll()
				.concatWith(Mono.error(new RuntimeException("Runtime Exception occurred")));
	}
	
	@PutMapping(ITEM_END_POINT_V1 + "/id")
	public Mono<ResponseEntity<Item>> updateItem(@PathVariable String id, @RequestBody Item item) {
		return itemReactiveRepository.findById(id)
									  .flatMap( currentItem -> {
										  currentItem.setPrice(item.getPrice());
										  currentItem.setDescription(item.getDescription());
										  return itemReactiveRepository.save(currentItem);
									  })
									  .map(updatedItem -> new ResponseEntity<>(updatedItem, HttpStatus.OK))
									  .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}
	
	
	/* A Continous Stream Example */
    @GetMapping(value = "/temperatures", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Integer> getTemperature() {
        Random r = new Random();
        int low = 0;
        int high = 50;
        return Flux.fromStream(Stream.generate(() -> r.nextInt(high - low) + low)
            .map(s -> String.valueOf(s))
            .peek((msg) -> {
                System.out.println(msg);
            }))
            .map(s -> Integer.valueOf(s))
            .delayElements(Duration.ofSeconds(1));
    }	
}
