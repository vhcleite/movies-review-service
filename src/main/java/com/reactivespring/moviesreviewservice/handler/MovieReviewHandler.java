package com.reactivespring.moviesreviewservice.handler;

import com.reactivespring.moviesreviewservice.domain.Review;
import com.reactivespring.moviesreviewservice.repository.MovieReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
@AllArgsConstructor
public class MovieReviewHandler {
    private final MovieReviewRepository repository;

    public Mono<ServerResponse> addReview(ServerRequest request) {
        return request.bodyToMono(Review.class)
                .flatMap(repository::save)
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
    }

    public Mono<ServerResponse> getReviews(ServerRequest serverRequest) {
        var reviewsFlux = repository.findAll();
        return ServerResponse.status(HttpStatus.OK).body(reviewsFlux, Review.class);
    }
}
