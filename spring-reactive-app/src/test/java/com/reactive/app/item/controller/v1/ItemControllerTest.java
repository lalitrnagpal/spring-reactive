package com.reactive.app.item.controller.v1;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.reactive.app.item.Item;
import com.reactive.app.item.ItemConstants;
import com.reactive.app.item.v1.repository.ItemReactiveRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemControllerTest {

	@Autowired
	WebTestClient webTestClient;
	
	@Autowired
	ItemReactiveRepository itemReactiveRepository;
	
	private List<Item> data() {
		return Arrays.asList(
				new Item(null, "Apple IPhone", 92.00),
				new Item(null, "HP Laptop", 92.00),
				new Item(null, "Bose Headphone", 92.00),
				new Item("MBZ", "Mercedez Benz", 999.00)
			);
	}
	
	@Before
	public void setup() {
		itemReactiveRepository.deleteAll()
								.thenMany(Flux.fromIterable( data() ) )
								.log()
								.flatMap( itemReactiveRepository::save )
								.blockLast();
	}
	
	@Test
	public void getAllItems() {
		
		webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1)
							.exchange()
							.expectStatus().isOk()
							.expectHeader().contentType(MediaType.APPLICATION_JSON)
							.expectBodyList(Item.class)
							.hasSize(4);
	}

	
	@Test
	public void getAllItemsApproachTwo() {
		
		webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1)
							.exchange()
							.expectStatus().isOk()
							.expectHeader().contentType(MediaType.APPLICATION_JSON)
							.expectBodyList(Item.class)
							.consumeWith( response -> {
								List<Item> itemList = response.getResponseBody();
								itemList.forEach( item -> assertTrue( item.getId() != null ) );
							});
	}	
	
	@Test
	public void getAllItemsApproachThree() {
		
		Flux<Item> itemsFlux = webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1)
											.exchange()
											.expectStatus().isOk()
											.expectHeader().contentType(MediaType.APPLICATION_JSON)
											.returnResult(Item.class)
											.getResponseBody();
		
		StepVerifier.create(itemsFlux.log())
					.expectSubscription()
					.expectNextCount(4)
					.verifyComplete();
		
	}	
	
	@Test
	public void getOneItem() {

		webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1.concat("/MBZ"))
							.exchange()
							.expectStatus().isOk()
							.expectBody()
							.jsonPath("$.price", 999.00);
	}
	
	@Test
	public void getNonExistingItem() {

		webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1.concat("{id}"), "/SOMEID")
							.exchange()
							.expectStatus().is4xxClientError();
	}	

	@Test
	public void getNonExistingItemTwo() {

		webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1.concat("{id}"), "/SOMEID")
							.exchange()
							.expectStatus().isNotFound();
	}	
	
	@Test
	public void createItem() {

		Item item = new Item(null, "A New Item", 21.11);
		
		webTestClient.post().uri(ItemConstants.ITEM_END_POINT_V1)
							.contentType(MediaType.APPLICATION_JSON)
							.body(Mono.just(item), Item.class)
							.exchange()
							.expectStatus().isCreated()
							.expectBody()
							.jsonPath("$.id").isNotEmpty()
							.jsonPath("$.description").isEqualTo("A New Item")
							.jsonPath("$.price").isEqualTo(21.11);
	}	
	
	@Test
	public void deleteItem() {
		webTestClient.post().uri(ItemConstants.ITEM_END_POINT_V1.concat("{id}"), "/MBZ")
							.accept(MediaType.APPLICATION_JSON)
							.exchange()
							.expectStatus().isOk()
							.expectBody(Void.class);
	}
	
	@Test
	public void updateItem() {
		double newPrice = 129.99;
		Item item = new Item(null, "Beats Headphones", newPrice);
		webTestClient.put().uri(ItemConstants.ITEM_END_POINT_V1.concat("{id}"), "MBZ")
							.contentType(MediaType.APPLICATION_JSON)
							.body(Mono.just(item), Item.class)
							.exchange()
							.expectStatus().isOk()
							.expectBody()
							.jsonPath("$.price").isEqualTo(129.99);
	}
	
	@Test
	public void updateNonExistingItem() {
		double newPrice = 129.99;
		Item item = new Item(null, "Beats Headphones", newPrice);
		webTestClient.put().uri(ItemConstants.ITEM_END_POINT_V1.concat("{id}"), "SOMEID")
							.contentType(MediaType.APPLICATION_JSON)
							.body(Mono.just(item), Item.class)
							.exchange()
							.expectStatus().isNotFound();
	}
}
