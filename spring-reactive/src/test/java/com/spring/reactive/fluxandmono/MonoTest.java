package com.spring.reactive.fluxandmono;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class MonoTest {

	@Test
	public void monoTest() {
		Mono<String> stringMono = Mono.just("Spring");
		
		StepVerifier.create(stringMono.log()).expectNext("Spring").verifyComplete();
	}
	
	@Test
	public void monoTestWithError() {
		StepVerifier.create(Mono.error(new RuntimeException("My Error")).log())
								.expectError(RuntimeException.class)
								.verify();
	}	
}
