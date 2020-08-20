package com.spring.reactive.rest;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;

import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

@WebFluxTest							// <- will not scan classes for @Component, @Service etc
public class FluxAndMonoControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    public void flux_approach1(){

        Flux<Integer> streamFlux = webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()								// invokes the endpoint
                .expectStatus().isOk()					// expecting status to be OK
                .returnResult(Integer.class)			// expecting return as Integer
                .getResponseBody();						// get response body – we get Flux here

        StepVerifier.create(streamFlux)
                .expectSubscription()
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectNext(4)
                .verifyComplete();
    }
    
    /* 
     * Another approach to test is as below 
    */
    @Test
    public void flux_approach2(){

        webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()								
                .expectStatus().isOk()					
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Integer.class)
                .hasSize(4);
    }
    
    /* 
     * Another approach to test is as below 
    */
    @Test
    public void flux_approach3(){

    	List<Integer> expectedIntegerList = Arrays.asList(1,2,3,4);
    	
        EntityExchangeResult<List<Integer>> entityExchangeResult = webTestClient.get().uri("/flux")
														                .accept(MediaType.APPLICATION_JSON)
														                .exchange()								
														                .expectStatus().isOk()					
														                .expectHeader().contentType(MediaType.APPLICATION_JSON)
														                .expectBodyList(Integer.class)
														                .returnResult();
        
        assertEquals(expectedIntegerList, entityExchangeResult.getResponseBody());
        
    }
    
    @Test
    public void flux_approach4(){

    	List<Integer> expectedIntegerList = Arrays.asList(1,2,3,4);
    	
        webTestClient.get().uri("/flux")
			                .accept(MediaType.APPLICATION_JSON)
			                .exchange()								
			                .expectStatus().isOk()					
			                .expectHeader().contentType(MediaType.APPLICATION_JSON)
							.expectBodyList(Integer.class)
							.consumeWith(
									response -> assertEquals(expectedIntegerList, response.getResponseBody())
							);
        
    }  
    
    @Test
    public void fluxStream() {
        Flux<Long> longStreamFlux = webTestClient.get().uri("/fluxstreamtwo")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()								// invokes the endpoint
                .expectStatus().isOk()					// expecting status to be OK
                .returnResult(Long.class)				// expecting return as Long
                .getResponseBody();						// get response body – we get Flux here   
        
        StepVerifier.create(longStreamFlux)
        			.expectNext(0l)
        			.expectNext(1l)
        			.expectNext(2l)
        			.thenCancel()
        			.verify();
    }
    
    @Test
    public void monoTest() {
    	webTestClient.get().uri("/mono")
    	  				   .accept(MediaType.APPLICATION_JSON)
    					   .exchange()
    					   .expectStatus()
    					   .isOk()
    					   .expectBody(Integer.class)
    					   .consumeWith( response -> assertEquals( Integer.valueOf(1), response.getResponseBody()));
    }
}

