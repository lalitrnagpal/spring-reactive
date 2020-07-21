package com.spring.reactive.fluxandmono;

import java.time.Duration;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoErrorTest {

	@Test
	public void fluxErrorHandlingOnErrorResume() {
		
		Flux<String> stringFlux = Flux.just("A", "B", "C")
									  .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
									  .concatWith(Flux.just("D"))
									  .onErrorResume( e -> {
											  System.out.println( e.getMessage() );
											  return Flux.just("default", "default1");
									  })
									  .log();
		
		StepVerifier.create( stringFlux )
							.expectSubscription()
							.expectNext("A", "B", "C")
							// .expectError(RuntimeException.class)
							.expectNext("default", "default1")
							.verifyComplete(); 
	}
	
	
	@Test
	public void fluxErrorHandlingOnErrorReturn() {
		
		Flux<String> stringFlux = Flux.just("A", "B", "C")
									  .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
									  .concatWith(Flux.just("D"))
									  .onErrorReturn( "default" )				// return a fall back value
									  .log();
		
		StepVerifier.create( stringFlux )
							.expectSubscription()
							.expectNext("A", "B", "C")
							.expectNext("default")
							.verifyComplete(); 
	}	
	
	@Test
	public void fluxErrorHandlingOnErrorMap() {
		
		Flux<String> stringFlux = Flux.just("A", "B", "C")
									  .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
									  .concatWith(Flux.just("D"))
									  .onErrorMap( e -> new CustomException("Custom Exception") )	// assign exception from 1 type to another
									  .log();
		
		StepVerifier.create( stringFlux )
							.expectSubscription()
							.expectNext("A", "B", "C")
							.expectError(CustomException.class)
							.verify(); 
	}
	
	private class CustomException extends RuntimeException {
		public CustomException(String message) {
			super(message);
		}
		public CustomException(Throwable t) {
			super(t);
		}
	}
	
	@Test
	public void fluxErrorHandlingWithRetry() {
		
		Flux<String> stringFlux = Flux.just("A", "B", "C")
									  .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
									  .concatWith(Flux.just("D"))
									  .onErrorMap( e -> new CustomException(e) )	
									  .retry(2)
									  .log();
		
		StepVerifier.create( stringFlux )
							.expectSubscription()
							.expectNext("A", "B", "C")
							.expectNext("A", "B", "C")
							.expectNext("A", "B", "C")
							.expectError(CustomException.class)
							.verify(); 
	}	

	@Test
	public void fluxErrorHandlingWithRetryBackoff() {
		
		Flux<String> stringFlux = Flux.just("A", "B", "C")
									  .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
									  .concatWith(Flux.just("D"))
									  .onErrorMap( e -> new CustomException(e) )	
									  .retryBackoff(2, Duration.ofSeconds(5), Duration.ofSeconds(6))
									  .log();
		
		StepVerifier.create( stringFlux )
							.expectSubscription()
							.expectNext("A", "B", "C")
							.expectNext("A", "B", "C")
							.expectNext("A", "B", "C")
							.expectError(IllegalStateException.class)
							.verify(); 
	}		
	
}
