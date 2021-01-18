package com.spring.reactive.fluxandmono;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndMonoFactoryMethods {

	List<String> namesList = Arrays.asList("Adam", "Anna", "Jack", "Jenny"); 
			
	@Test
	public void fluxUsingIterable() {
		Flux<String> namesFlux = Flux.fromIterable(namesList);
		
		StepVerifier.create(namesFlux.log())
		  			.expectNext("Adam", "Anna", "Jack", "Jenny")
		  			.verifyComplete();
	}
	
	@Test
	public void fluxUsingArray() {
		
		String[] namesStrArray = new String[]{"Adam", "Anna", "Jack", "Jenny"};
		
		Flux<String> namesFlux = Flux.fromArray(namesStrArray);
		
		StepVerifier.create(namesFlux.log())
		  			.expectNext("Adam", "Anna", "Jack", "Jenny")
		  			.verifyComplete();
	}	

	@Test
	public void fluxUsingStream() {
		
		List<String> theNamesList = Arrays.asList("Adam", "Anna", "Jack", "Jenny");
		
		Flux<String> namesFlux = Flux.fromStream(theNamesList.stream());
		
		StepVerifier.create(namesFlux.log())
		  			.expectNext("Adam", "Anna", "Jack", "Jenny")
		  			.verifyComplete();
	}	
	
	@Test
	public void monoUsingJustEmpty() {
		
		Mono<String> mono = Mono.justOrEmpty(null);
		
		// There is no data to emit so can't do expect, just do verifyComplete
		StepVerifier.create(mono)
					.verifyComplete();
	}

	
	@Test
	public void monoUsingSupplier() {
		
		Supplier<String> stringSupplier = () -> "Returned String";
		
		// This is how we get a value from a supplier usually
		System.out.println(stringSupplier.get());
		
		Mono<String> stringMono = Mono.fromSupplier( stringSupplier ).log();
		
		// There is no data to emit so can't do expect, just do verifyComplete
		StepVerifier.create( stringMono )
					.expectNext("Returned String")
					.verifyComplete();
	}	
	
	@Test
	public void fluxUsingRange() {
		
		Flux<Integer> integerFlux = Flux.range(1, 5).log();

		StepVerifier.create(integerFlux)
					.expectNext(1, 2, 3, 4, 5)
					.verifyComplete();
		
	}
}
