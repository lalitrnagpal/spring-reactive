package com.reactive.spring.streaming;

import java.time.Duration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.reactive.spring.streaming.domain.ItemCapped;
import com.reactive.spring.streaming.repository.ItemReactiveCappedRepository;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
public class ItemStreamControllerTest {

    @Autowired
    ItemReactiveCappedRepository itemReactiveCappedRepository;

    @Autowired
    MongoOperations mongoOperations;

    @Autowired
    WebTestClient webTestClient;

    @Before
    public void setUp() {

        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped());

        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofMillis(100))
                .map(i -> new ItemCapped(null, "Random Item " + i, (100.00 + i)))
                .take(5);

        itemReactiveCappedRepository
                .insert(itemCappedFlux)
                .doOnNext((itemCapped -> {
                    System.out.println("Inserted Item in setUp " + itemCapped);
                }))
                .blockLast();


    }

    @Test
    public void testStreamAllItems(){

        Flux<ItemCapped> itemCappedFlux = webTestClient.get().uri(ItemConstants.ITEM_STREAM_END_POINT)
                .exchange()
                .expectStatus().isOk()
                .returnResult((ItemCapped.class))
                .getResponseBody()
                .take(5);

        StepVerifier.create(itemCappedFlux)
                .expectNextCount(5)
                .thenCancel()
                .verify();

    }



}
