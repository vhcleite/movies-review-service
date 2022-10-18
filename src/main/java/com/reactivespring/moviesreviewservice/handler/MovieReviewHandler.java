package com.reactivespring.moviesreviewservice.handler;

import com.reactivespring.moviesreviewservice.domain.Review;
import com.reactivespring.moviesreviewservice.exceptions.ReviewDataException;
import com.reactivespring.moviesreviewservice.repository.MovieReviewRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class MovieReviewHandler {
    private final MovieReviewRepository repository;

    private final Validator validator;


    public Mono<ServerResponse> addReview(ServerRequest request) {
        return request.bodyToMono(Review.class)
                .doOnNext(this::validate)
                .flatMap(repository::save)
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
    }

    private void validate(Review review) {
        var violations = validator.validate(review);
        if (!violations.isEmpty()) {
            var message = violations
                    .stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            throw new ReviewDataException(message);
        }
    }

    public Mono<ServerResponse> getReviews(ServerRequest serverRequest) {
        var movieInfoId = serverRequest.queryParam("movieInfoId");

        Flux<Review> reviewsFlux;
        if (movieInfoId.isPresent()) {
            reviewsFlux = repository.findByMovieInfoId(Long.parseLong(movieInfoId.get()));
        } else {
            reviewsFlux = repository.findAll();
        }
        return ServerResponse.status(HttpStatus.OK).body(reviewsFlux, Review.class);

    }

    public Mono<ServerResponse> updateReview(ServerRequest serverRequest) {
        var reviewId = serverRequest.pathVariable("id");
        return repository.findById(reviewId)
                .flatMap(savedReview -> serverRequest.bodyToMono(Review.class)
                        .map(reqReview -> {
                            savedReview.setRating(reqReview.getRating());
                            savedReview.setComment(reqReview.getComment());
                            return savedReview;
                        }))
                .flatMap(repository::save)
                .flatMap(ServerResponse.ok()::bodyValue);
    }

    public Mono<ServerResponse> deleteReview(ServerRequest serverRequest) {
        var reviewId = serverRequest.pathVariable("id");

        return repository.deleteById(reviewId)
                .then(ServerResponse.noContent().build());
    }
}
