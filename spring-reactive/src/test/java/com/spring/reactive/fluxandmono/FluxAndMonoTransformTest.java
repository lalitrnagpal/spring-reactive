package com.spring.reactive.fluxandmono;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

public class FluxAndMonoTransformTest {

	List<String> namesList = Arrays.asList("Adam", "Anna", "Jack", "Jenny"); 
	
	@Test
	public void transformUsingMap() {
		
		// Using map function from Project Reactor to convert Flux from one form to another.
		Flux<String> stringFlux = Flux.fromIterable(namesList)
									  .filter( e -> e.toLowerCase().startsWith("a") )
									  .map( e -> e.toUpperCase())
									  .log();
		
		StepVerifier.create(stringFlux)
					.expectNext("ADAM", "ANNA")
					.verifyComplete();
	}
	
	@Test
	public void transformUsingMapExampleTwo() {
		
		// Using map function from Project Reactor to convert Flux from one form to another.
		Flux<Integer> stringFlux = Flux.fromIterable(namesList)
									  .filter( e -> e.toLowerCase().startsWith("a") )
									  .map( e -> e.length())
									  .log();
		
		StepVerifier.create(stringFlux)
					.expectNext(4, 4)
					.verifyComplete();
	}	
	
	@Test
	public void repeatFluxFailingExample() {
		
		// Using map function from Project Reactor to convert Flux from one form to another.
		Flux<Integer> stringFlux = Flux.fromIterable(namesList)
									  .filter( e -> e.toLowerCase().startsWith("a") )
									  .map( e -> e.length())
									  .log();
		
		StepVerifier.create(stringFlux.repeat(1))
					.expectNext(4, 4)			// due to repeat values wont be 4, 4
					.expectError();
	}		
	
	@Test
	public void repeatFluxPassingExample() {
		
		// Using map function from Project Reactor to convert Flux from one form to another.
		Flux<Integer> stringFlux = Flux.fromIterable(namesList)
									  .filter( e -> e.toLowerCase().startsWith("a") )
									  .map( e -> e.length())
									  .log();
		
		StepVerifier.create(stringFlux.repeat(1))
					.expectNext(4, 4, 4, 4)			// due to repeat values would be 4, 4, 4, 4
					.verifyComplete();
	}		
	
	@Test
	public void transformUsingFlatMap() {
		
		Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A", "B", "C"))				// Flux<String>
									   .log()
									   .flatMap( s -> Flux.fromIterable(convertToList(s)) );	// Flux<String>
		
		StepVerifier.create(stringFlux)
					.expectNextCount(6)
					.verifyComplete();
	}
	
	private Iterable convertToList(String s) {
		
		// Introducing delay of 1 second for each element, notice how consumption is slowed down
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Arrays.asList(s, "newValue");
	}
	
	@Test
	public void transformUsingFlatMapAnotherExample() {
		
	}
	
}
