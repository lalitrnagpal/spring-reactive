package com.spring.reactive.fluxandmono;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.test.scheduler.VirtualTimeScheduler;

public class VirtualizingTimeInJUnit {

	/*
	 * Takes the specified interval multiplied by number of values to finish
	 */
	@Test
	public void testWithoutVirtualTime() {
	
		Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1))
								  .take(3)
								  .log();
		
		StepVerifier.create(longFlux)
					.expectSubscription()
					.expectNext(0L, 1L, 2L)
					.verifyComplete();
	}
	
	/*
	 * Doesn't block, uses virtual time and finishes instantly
	 */
	@Test
	public void testWithtVirtualTime() {
	
		VirtualTimeScheduler.getOrSet();
		
		Flux<Long> longFlux = Flux.interval(Duration.ofSeconds(1))
								  .take(3);
		
		StepVerifier.withVirtualTime( () -> longFlux.log() )
						.expectSubscription()
						.thenAwait(Duration.ofSeconds(3))
						.expectNext(0l, 1l, 2l)
						.verifyComplete();
	}	
	
	/*
	 * This will take just a second to complete and execute even though we have a 1 second delay for each element
	 * This blocks if we give a thenAwait of less than 6 seconds, got stuck when i gave 1 second 
	 */
	
	@Test
	public void combineUsingConcatWithDelay() {
		
		VirtualTimeScheduler.getOrSet();
		
		Flux<String> flux1 = Flux.just("A", "B", "C").delayElements(Duration.ofSeconds(1));
		Flux<String> flux2 = Flux.just("D", "E", "F").delayElements(Duration.ofSeconds(1));
				
		Flux<String> mergedFlux = Flux.concat(flux1, flux2).log();
		
		StepVerifier.withVirtualTime(() -> mergedFlux.log())
					.expectSubscription()
					.thenAwait(Duration.ofSeconds(6))
					.expectNextCount(6)
					.verifyComplete();

	}	
}
