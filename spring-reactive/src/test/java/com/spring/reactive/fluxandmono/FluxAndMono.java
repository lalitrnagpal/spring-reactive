package com.spring.reactive.fluxandmono;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;

/*
 * This just gives a taste of Flux and is not really a test case
 * We do not have any assert conditions etc here.
 * 
 */

public class FluxAndMono {

	@Test
	public void fluxTest() {
		Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring");
		
		stringFlux.subscribe(System.out::println, e -> System.err.println(e.getMessage()));
	}
	
	@Test
	public void fluxTestError() {
		Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
									.concatWith(Flux.error(new RuntimeException("Some Error Occurred")))
									.concatWith(Flux.just("After Error Element"))
									.log();
		
		stringFlux.subscribe(System.out::println, e -> System.err.println("Exception: "+e.getMessage()));
	}

	@Test
	public void fluxTestComplete() {
		Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Reactive Spring")
									// .concatWith(Flux.error(new RuntimeException("Some Error Occurred")))
									.concatWith(Flux.just("After Error Element"))
									.log();
		
		stringFlux.subscribe(System.out::println, 
									e -> System.err.println("Exception: "+e.getMessage()),
									() -> System.out.println("Event Completed"));
	}	
}
