package com.reactivespring.moviesreviewservice.repository;

import com.reactivespring.moviesreviewservice.domain.Review;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface MovieReviewRepository extends ReactiveMongoRepository<Review, String> {
}
