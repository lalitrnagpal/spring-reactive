package com.reactive.app.item.v1.handler;

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
public class ItemHandlerTest {
	@Autowired
	WebTestClient webTestClient;

	@Autowired
	ItemReactiveRepository itemReactiveRepository;

	private List<Item> data() {
		return Arrays.asList(new Item(null, "Apple IPhone", 92.00), new Item(null, "HP Laptop", 92.00),
				new Item(null, "Bose Headphone", 92.00), new Item("MBZ", "Mercedez Benz", 999.00));
	}

	@Before
	public void setup() {
		itemReactiveRepository.deleteAll().thenMany(Flux.fromIterable(data())).log()
				.flatMap(itemReactiveRepository::save).blockLast();
	}

	@Test
	public void getAllItems() {

		webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1).exchange().expectStatus().isOk()
				.expectHeader().contentType(MediaType.APPLICATION_JSON).expectBodyList(Item.class).hasSize(4);
	}

	@Test
	public void getAllItemsApproach2() {

		webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1).accept(MediaType.APPLICATION_JSON)
				.exchange().expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON)
				.expectBodyList(Item.class).hasSize(4).consumeWith(response -> {
					List<Item> items = response.getResponseBody();
					items.forEach(item1 -> {
						assertTrue(item1.getId() != null);
					});

				});
	}

	@Test
	public void getAllItemsApproach3() {

		Flux<Item> itemsFlux = webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1).accept(MediaType.APPLICATION_JSON)
				.exchange().expectStatus().isOk().expectHeader().contentType(MediaType.APPLICATION_JSON)
				.returnResult(Item.class).getResponseBody();

		StepVerifier.create(itemsFlux).expectNextCount(4).verifyComplete();
	}
	
	@Test
	public void createItemTest() {
		
		Item item = new Item(null, "A New Item", 100.11);
		
		webTestClient.post().uri( ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1 )
					 .accept(MediaType.APPLICATION_JSON)
					 .body(Mono.just(item), Item.class)
					 .exchange()
					 .expectStatus().isCreated()
					 .expectHeader().contentType(MediaType.APPLICATION_JSON)
		             .expectBody()
		             .jsonPath("$.id").isNotEmpty()
		             .jsonPath("$.description").isEqualTo("A New Item")
		             .jsonPath("$.price").isEqualTo("100.11");
		
	}

	 @Test
    public void testDeleteOneItem() {

        webTestClient.delete().uri( ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "MBZ")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);

    }	
	 
	 @Test
	 public void testUpdateItem() {
	     double newPrice=129.99;
	     Item item = new Item(null,"Beats HeadPhones", newPrice);
	     webTestClient.put().uri( ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "ABC")
	             .contentType(MediaType.APPLICATION_JSON )
	             .accept(MediaType.APPLICATION_JSON )
	             .body(Mono.just(item), Item.class)
	             .exchange()
	             .expectStatus().isOk()
	             .expectBody()
	             .jsonPath("$.price", newPrice).isEqualTo("ABC")
//	             .jsonPath("$.description").isEqualTo("Monkey")
//	             .jsonPath("$.price").isEqualTo(27.50)
	             ;
	     
	     webTestClient
         .get()
         .uri( ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "ABC")
         .exchange()
         .expectStatus().isOk();	     

	 }

	 @Test
	 public void testUpdateItem_notFound() {
	     double newPrice=129.99;
	     Item item = new Item(null,"Beats HeadPhones", newPrice);
	     webTestClient.put().uri( ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "DEF") 			//no record with this ids
	             .contentType(MediaType.APPLICATION_JSON )
	             .body(Mono.just(item), Item.class)
	             .exchange()
	             .expectStatus().isNotFound();
	 }	 
	
}
