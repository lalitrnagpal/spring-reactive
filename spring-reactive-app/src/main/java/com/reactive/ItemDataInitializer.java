package com.reactive;

import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.reactive.app.item.Item;
import com.reactive.app.item.v1.repository.ItemReactiveRepository;

import reactor.core.publisher.Flux;

@Component
@Profile("!test")
public class ItemDataInitializer implements CommandLineRunner {

	@Autowired
	ItemReactiveRepository itemReactiveRepository;
	
	@Override
	public void run(String... args) throws Exception {
		initialDataSetup();
	}
	
	public List<Item> data() {
		List<Item> dataList = Arrays.asList(
			new Item(null, "Apple IPhone", 92.00),
			new Item(null, "HP Laptop", 92.00),
			new Item(null, "Bose Headphone", 92.00),
			new Item(null, "Mercedez Benz", 999992.00)
		);
		return dataList;
	} 
	
	private void initialDataSetup() {
		
		itemReactiveRepository.deleteAll()
								.thenMany( Flux.fromIterable( data() ))
								.log()
								.flatMap( itemReactiveRepository::save )
								.thenMany( itemReactiveRepository.findAll() )
								.subscribe( item -> System.out.println("Item inserted -> "+item) );
	}

}
