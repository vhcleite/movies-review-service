package com.reactivespring.moviesreviewservice.repository;

import com.reactivespring.moviesreviewservice.domain.Review;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface MovieReviewRepository extends ReactiveMongoRepository<Review, String> {
    Flux<Review> findByMovieInfoId(Long movieInfoId);
}
