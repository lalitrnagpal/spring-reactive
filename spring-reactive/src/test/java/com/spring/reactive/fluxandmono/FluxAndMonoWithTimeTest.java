package com.spring.reactive.fluxandmono;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoWithTimeTest {

	@Test
	public void infiniteSequence() {
		// Infinity sequence starting at 0 ....
		Flux<Long> infiniteFlux = Flux.interval( Duration.ofMillis(200) )
										.log();
		
		infiniteFlux.subscribe( l -> System.out.println("Value is -> " + l) );
		
		try {
			Thread.sleep(3000);
		} catch(Exception e) { }
	}
	
	
	@Test
	public void infiniteSequenceTest() {
		// Infinity sequence starting at 0 ....
		Flux<Long> infiniteFlux = Flux.interval( Duration.ofMillis(200) )
										.take(3)
										.log();
		
		StepVerifier.create(infiniteFlux)
						.expectSubscription()
						.expectNext(0L, 1L, 2L)
						.verifyComplete();
		
	}
	
	@Test
	public void infiniteSequenceWithMap_WithDelay() {
		// Infinity sequence starting at 0 ....
		// Converting these longs to integer values before take 
		Flux<Integer> infiniteFlux = Flux.interval( Duration.ofMillis(200) )
										.delayElements( Duration.ofSeconds(1) )
										.map( l -> l.intValue() )
										.take(3)
										.log();
		
		StepVerifier.create(infiniteFlux)
						.expectSubscription()
						.expectNext(0, 1, 2)
						.verifyComplete();
		
	}	
}