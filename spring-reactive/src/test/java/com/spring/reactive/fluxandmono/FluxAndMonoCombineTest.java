package com.spring.reactive.fluxandmono;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoCombineTest {

	@Test
	public void combineUsingMerge() {
		Flux<String> flux1 = Flux.just("A", "B", "C");
		Flux<String> flux2 = Flux.just("D", "E", "F");
				
		Flux<String> mergedFlux = Flux.merge(flux1, flux2);
		
		// Printing all elements with subscribe method
		// mergedFlux.subscribe(System.out::println);
		
		StepVerifier.create(mergedFlux)
					.expectSubscription()
					.expectNext("A", "B", "C", "D", "E", "F")
					.verifyComplete();
		
	}
	
	@Test
	public void combineUsingMergeWithDelay() {
		Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
		Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));
				
		Flux<String> mergedFlux = Flux.merge(flux1, flux2);
		
		StepVerifier.create(mergedFlux)
					.expectSubscription()
					.expectNext("A", "B", "C", "D", "E", "F")
					.verifyComplete();

	}	
	
	@Test
	public void combineUsingConcatWithDelay() {
		Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
		Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));
				
		Flux<String> mergedFlux = Flux.concat(flux1, flux2).log();
		
		StepVerifier.create(mergedFlux)
					.expectSubscription()
					.expectNext("A", "B", "C", "D", "E", "F")
				//	.expectNextCount(10)
					.verifyComplete();

	}	

	
	@Test
	public void combineUsingZipWithDelay() {
		Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
		Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));
				
		Flux<String> mergedFlux = Flux.zip(flux1, flux2, (f1, f2) -> {		// A,D : B,E : C,F
			return f1.concat(f2);						// returns AD, BE, CF
		}).log();
		
		StepVerifier.create(mergedFlux)
					.expectSubscription()
					.expectNext("AD", "BE", "CF")
				//	.expectNextCount(10)
					.verifyComplete();

	}		
}
