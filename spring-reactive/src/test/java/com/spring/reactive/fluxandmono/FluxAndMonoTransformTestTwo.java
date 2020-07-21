package com.spring.reactive.fluxandmono;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

public class FluxAndMonoTransformTestTwo {

    @Test
    public void tranformUsingFlatMapUsingParallel(){

        Flux<String> stringFlux = Flux.fromIterable( Arrays.asList("A","B","C","D","E","F") ) 	// Flux<String>
                .window(2) 																		// Flux<Flux<String> -> (A,B), (C,D), (E,F)
                .flatMap(s -> s.map(this::convertToList).subscribeOn(Schedulers.parallel())) 	// Flux<List<String>>
                .flatMap(s -> Flux.fromIterable(s)) 											// Flux<String>
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void tranformUsingFlatMapUsingParallelTwo(){

        Flux<String> stringFlux = Flux.fromIterable( Arrays.asList("A","B","C","D","E","F") ) 	// Flux<String>
                .window(2) 																		// Flux<Flux<String> -> (A,B), (C,D), (E,F)
                .concatMap( s -> s.map(this::convertToList).subscribeOn(Schedulers.parallel())) // Flux<List<String>>
                .flatMap(s -> Flux.fromIterable(s)) 											// Flux<String>
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }    
    
    @Test
    public void tranformUsingFlatMapUsingParallelAndMaintainOrder(){

        Flux<String> stringFlux = Flux.fromIterable( Arrays.asList("A","B","C","D","E","F") ) 	// Flux<String>
                .window(2) 																		// Flux<Flux<String> -> (A,B), (C,D), (E,F)
                .flatMapSequential(s -> s.map(this::convertToList).subscribeOn( Schedulers.parallel() ))
                .flatMap(s -> Flux.fromIterable(s)) 											// Flux<String>
                .log();

        StepVerifier.create(stringFlux)
                .expectNextCount(12)
                .verifyComplete();
    }
    
    
    @Test
    public void tranformUsingFlatMapJustABreakUp(){

        Flux<String> stringFlux = Flux.fromIterable( Arrays.asList("A","B","C","D","E","F") );
        Flux<Flux<String>> fluxOfFluxOfString = stringFlux.window(2);
        Flux<List<String>> fluxOfListOfString = fluxOfFluxOfString.flatMapSequential(s -> s.map(this::convertToList)
        																				   .subscribeOn( Schedulers.parallel() ));
        Flux<String> fluxOfString = fluxOfListOfString.flatMap(s -> Flux.fromIterable(s));
        fluxOfString.log();

        StepVerifier.create(fluxOfString.log())
                .expectNextCount(12)
                .verifyComplete();
    }    
	
	
	private List<String> convertToList(String s) {
		
		// Introducing delay of 1 second for each element, notice how consumption is slowed down
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return Arrays.asList(s, "newValue");
	}	
	
	@Test
	public void testingThis() {
		
		List<List<String>> myList = Arrays.asList( Arrays.asList("a", "b"), Arrays.asList("c", "d") );
		
		Flux<String> myStringFlux = Flux.fromIterable( myList )
										.flatMap( s -> Flux.fromIterable( s ) );
		
		myStringFlux.subscribe( System.out::println );
		
	}

	
}
