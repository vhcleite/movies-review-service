package com.reactivespring.moviesreviewservice.router;

import com.reactivespring.moviesreviewservice.handler.MovieReviewHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@AllArgsConstructor
public class ReviewsRouter {

    private final MovieReviewHandler handler;

    @Bean
    public RouterFunction<ServerResponse> reviewRouter() {
        return route()
                .nest(path("v1/reviews"), builder -> {
                    builder
                            .GET("", (handler::getReviews))
                            .POST("", (handler::addReview))
                            .PUT("/{id}", (handler::updateReview))
                            .DELETE("/{id}", handler::deleteReview);
                })
                .GET("v1/helloword", (request -> ServerResponse.ok().bodyValue("hellow world")))
                .build();
    }
}
