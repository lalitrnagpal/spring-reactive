package com.reactive.spring.streaming;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

import com.reactive.spring.streaming.domain.Item;
import com.reactive.spring.streaming.domain.ItemCapped;
import com.reactive.spring.streaming.repository.ItemReactiveCappedRepository;
import com.reactive.spring.streaming.repository.ItemReactiveRepository;

import reactor.core.publisher.Flux;

@Component
@Profile("!test")
public class ItemDataInitializer implements CommandLineRunner {

	@Autowired
	ItemReactiveRepository itemReactiveRepository;
	
	@Autowired
	ItemReactiveCappedRepository itemReactiveCappedRepository;
	
	@Autowired
	MongoOperations mongoOperations;
	
	@Override
	public void run(String... args) throws Exception {
		initialDataSetup();
		createCappedCollection();
		dataSetupForCappedCollection();
	}
	
	public List<Item> data() {
		List<Item> dataList = Arrays.asList(
			new Item(null, "Apple IPhone", 92.00),
			new Item(null, "HP Laptop", 92.00),
			new Item(null, "Bose Headphone", 92.00),
			new Item("MBZ", "Mercedez Benz", 999992.00)
		);
		return dataList;
	} 

	private void dataSetupForCappedCollection() {

		Flux<ItemCapped> itemCappedFlux = Flux.interval( Duration.ofSeconds(1))
											  .map(i -> new ItemCapped(null, "Random Item "+i, 100.00 + i));
		
		itemReactiveCappedRepository.insert(itemCappedFlux)
									.subscribe( itemCapped -> System.out.println("Inserted item is: " + itemCapped) );
		
	}	
	
	private void initialDataSetup() {
		
		itemReactiveRepository.deleteAll()
								.thenMany( Flux.fromIterable( data() ))
								.log()
								.flatMap( itemReactiveRepository::save )
								.thenMany( itemReactiveRepository.findAll() )
								.subscribe( item -> System.out.println("Item inserted -> "+item) );
	}
	
    private void createCappedCollection() {
		mongoOperations.dropCollection(ItemCapped.class);
		// build dependency error somehow
		mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped());
	}

}
