package com.reactivespring.moviesreviewservice.router;

import com.reactivespring.moviesreviewservice.handler.MovieReviewHandler;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
@AllArgsConstructor
public class ReviewsRouter {

    private final MovieReviewHandler handler;

    @Bean
    public RouterFunction<ServerResponse> reviewRouter() {
        return route()
                .GET("v1/helloword", (request -> ServerResponse.ok().bodyValue("hellow world")))
                .GET("v1/reviews", (handler::getReviews))
                .POST("v1/reviews", (handler::addReview))
                .build();
    }
}
