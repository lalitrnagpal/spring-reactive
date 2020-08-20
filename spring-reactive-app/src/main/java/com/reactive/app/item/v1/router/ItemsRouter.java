package com.reactive.app.item.v1.router;

import static com.reactive.app.item.ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.reactive.app.item.v1.handler.ItemsHandler;

@Configuration
public class ItemsRouter {
	
	@Bean
	public RouterFunction<ServerResponse> itemsRoute(ItemsHandler itemsHandler) {
		return RouterFunctions.route(RequestPredicates.GET(	 ITEM_FUNCTIONAL_END_POINT_V1)
																.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), itemsHandler::getAllItems )
																.andRoute( RequestPredicates.GET(ITEM_FUNCTIONAL_END_POINT_V1 + "/{id}")
																			.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), itemsHandler::getOneItem )
																.andRoute( RequestPredicates.POST(ITEM_FUNCTIONAL_END_POINT_V1 )
																			.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), itemsHandler::createItem )
																.andRoute( RequestPredicates.DELETE(ITEM_FUNCTIONAL_END_POINT_V1 )
																		.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), itemsHandler::deleteItem )
																.andRoute( RequestPredicates.PUT(ITEM_FUNCTIONAL_END_POINT_V1 + "/{id}")
																		.and(RequestPredicates.accept(MediaType.APPLICATION_JSON)), itemsHandler::updateItem );
	}

}
