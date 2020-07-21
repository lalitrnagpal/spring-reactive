package com.spring.reactive.fluxandmono;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoFilterTest {

	@Test
	public void filterTest() {
		
		List<String> names = Arrays.asList( "Amit", "Ravi", "Kiran", "Sumit", "Aaryan" );
		
		Flux<String> stringFlux = Flux.fromIterable(names)
									  .filter( e -> e.toLowerCase().startsWith("a") )
									  .log();
		
		StepVerifier.create(stringFlux)
					.expectNext("Amit", "Aaryan")
					.verifyComplete();
		
		
	}
}
