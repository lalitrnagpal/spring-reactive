package com.spring.reactive.fluxandmono;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoBackPressureTest {

	@Test
	public void backPressureTest() {
		Flux<Integer> integerFlux = Flux.range(1, 10)
										.log();
		
		StepVerifier.create(integerFlux)
					.expectSubscription()
					.thenRequest(1)
					.expectNext(1)
					.thenRequest(1)
					.expectNext(2)
					.thenCancel()
					.verify();
	}
	
	@Test
	public void backPressure() {
		// This wont be a test case, just showing how to use a subscriber
		Flux<Integer> finiteFlux = Flux.range(1, 10)
										.log();
		finiteFlux.subscribe( e -> System.out.println("Element is: "+e),					// Do this with the elements 
							  exception -> System.err.println("Exception is "+exception), 	// When we get a exception
							  () -> System.out.println("Done!"),							// After it is complete
							  subscription -> subscription.request(2)
							);
	}	
	
	@Test
	public void backPressureWithCancel() {
		// This wont be a test case, just showing how to use a subscriber
		Flux<Integer> finiteFlux = Flux.range(1, 10)
										.log();
		finiteFlux.subscribe( e -> System.out.println("Element is: "+e),					// Do this with the elements 
							  exception -> System.err.println("Exception is "+exception), 	// When we get a exception
							  () -> System.out.println("Done!"),							// After it is complete
							  subscription -> subscription.cancel()
							);
	}	
	
	@Test
	public void customizedBackPressure() {
		// This wont be a test case, just showing how to use a subscriber
		Flux<Integer> finiteFlux = Flux.range(1, 10)
										.log();
		finiteFlux.subscribe(
								new BaseSubscriber<Integer>() {
									@Override
									protected void hookOnNext(Integer value) {
										// super.hookOnNext(value);
										request(1);
										System.out.println("Value received is: " + value);
										if (value == 4) {
											cancel();
										}
									};
								} 
							);
	}		
}
