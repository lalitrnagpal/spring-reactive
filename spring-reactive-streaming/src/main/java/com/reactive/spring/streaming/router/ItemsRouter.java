package com.reactive.spring.streaming.router;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import com.reactive.spring.streaming.ItemConstants;

@Configuration
public class ItemsRouter {

	@Bean
	public RouterFunction<ServerResponse> itemStreamRouter(ItemsHandler itemsHandler) {
		return RouterFunctions.route(
					GET(ItemConstants.ITEM_STREAM_FUNCTIONAL_ENDPOINT).and(accept(APPLICATION_JSON)), itemsHandler::itemsStream);
	}
}
