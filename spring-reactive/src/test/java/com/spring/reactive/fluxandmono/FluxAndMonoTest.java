package com.spring.reactive.fluxandmono;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoTest {
	
	@Test
	public void fluxTestWithoutError() {
		
		Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Spring Reactive")
							.log();
		
		StepVerifier.create(stringFlux)
					.expectNext("Spring")
					.expectNext("Spring Boot")
					.expectNext("Spring Reactive")
					.verifyComplete();
		
	}

	@Test
	public void fluxTestElementsWithoutError() {
		
		Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Spring Reactive")
									.concatWith(Flux.error(new RuntimeException("Runtime Exception occurred")))
									.log();
		
		StepVerifier.create(stringFlux)
					.expectNext("Spring")
					.expectNext("Spring Boot")
					.expectNext("Spring Reactive")
					.verifyError();
		
		StepVerifier.create(stringFlux)
					.expectNext("Spring")
					.expectNext("Spring Boot")
					.expectNext("Spring Reactive")
					.expectError(RuntimeException.class)
					.verify();
		
		StepVerifier.create(stringFlux)
					.expectNext("Spring")
					.expectNext("Spring Boot")
					.expectNext("Spring Reactive")
					.expectErrorMessage("Runtime Exception occurred")
					.verify();		
		
	}
	
	@Test
	public void fluxTestElementsCount() {
		
		Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Spring Reactive")
									.concatWith(Flux.error(new RuntimeException("Runtime Exception occurred")))
									.log();
		
		StepVerifier.create(stringFlux)
					.expectNextCount(5)
					.expectError();
		
	}	
	
	@Test
	public void fluxTestElements() {
		
		Flux<String> stringFlux = Flux.just("Spring", "Spring Boot", "Spring Reactive")
									.log();
		
		StepVerifier.create(stringFlux)
					.expectNext("Spring", "Spring Boot", "Spring Reactive")
					.verifyComplete();
		
	}	
}
