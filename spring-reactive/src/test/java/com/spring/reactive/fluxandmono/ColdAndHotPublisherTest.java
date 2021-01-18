package com.spring.reactive.fluxandmono;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.ConnectableFlux;
import reactor.core.publisher.Flux;

public class ColdAndHotPublisherTest {

	
	/* 
	 * Cold Publisher - Emit the values for subsequent subscribers from beginning 
	*/
	
	@Test
	public void coldPublisherTest() {
		Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
									  .delayElements(Duration.ofSeconds(1))
									  .log();
		stringFlux.subscribe( s -> System.out.println("Subscriber 1: "+s));
		
		try {
		Thread.sleep(2000);
		} catch(Exception e) { }
		
		stringFlux.subscribe( s2 -> System.out.println("Subscriber 2: "+s2));
		
		try {
		Thread.sleep(4000);
		} catch(Exception e) { }
		
	}
	
	
	/* 
	 * Hot Publisher - Does not emit the values for subsequent subscribers from the beginning 
	*/
	
	@Test
	public void hotPublisherTest() {
		Flux<String> stringFlux = Flux.just("A", "B", "C", "D", "E", "F")
									  .delayElements(Duration.ofSeconds(1))
									  .log();
		
		ConnectableFlux<String> connectableFlux = stringFlux.publish();
		connectableFlux.connect();
		
		connectableFlux.subscribe( s -> System.out.println("Subscriber 1: "+s));
		
		try {
		Thread.sleep(3000);
		} catch(Exception e) { }
		
		connectableFlux.subscribe( s2 -> System.out.println("Subscriber 2: "+s2));
		
		try {
		Thread.sleep(4000);
		} catch(Exception e) { }
		
	}	
}
