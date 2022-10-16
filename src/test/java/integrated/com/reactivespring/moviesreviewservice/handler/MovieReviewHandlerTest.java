package com.reactivespring.moviesreviewservice.handler;

import com.reactivespring.moviesreviewservice.domain.Review;
import com.reactivespring.moviesreviewservice.repository.MovieReviewRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class MovieReviewHandlerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    MovieReviewRepository repository;

    static String REVIEWS_URL = "/v1/reviews";
    private static final String REVIEW_ID = "id";

    @BeforeEach
    void setUp() {

        var reviewsList = List.of(
                new Review(null, 1L, "Awesome Movie", 9.0),
                new Review(null, 1L, "Awesome Movie1", 9.0),
                new Review(null, 2L, "Excellent Movie", 8.0),
                new Review(REVIEW_ID, 2L, "Excellent Movie", 8.0));
        repository.saveAll(reviewsList)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        repository.deleteAll().block();
    }

    @Test
    void getReviews() {
        //given

        //when
        webTestClient
                .get()
                .uri(REVIEWS_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .value(reviews -> {
                    assertEquals(3, reviews.size());
                });
    }

    @Test
    void getReviewsByMovieInfoId() {
        //given

        //when
        webTestClient
                .get()
                .uri(uriBuilder -> {
                    return uriBuilder.path(REVIEWS_URL)
                            .queryParam("movieInfoId", 1L)
                            .build();
                })
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .value(reviews -> {
                    assertEquals(2, reviews.size());
                });
    }

    @Test
    void addReview() {
        var review = new Review(null, 1L, "Awesome Movie", 9.0);
        //when
        webTestClient
                .post()
                .uri(REVIEWS_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Review.class)
                .consumeWith(reviewResponse -> {
                    var savedReview = reviewResponse.getResponseBody();
                    assert savedReview != null;
                    assertNotNull(savedReview.getReviewId());
                });
    }

    @Test
    void updateReview() {
        var review = new Review(REVIEW_ID, 1L, "Awesome Movie", 10.0);
        //when
        webTestClient
                .put()
                .uri(REVIEWS_URL + "/" + REVIEW_ID)
                .bodyValue(review)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Review.class)
                .consumeWith(reviewResponse -> {
                    var savedReview = reviewResponse.getResponseBody();
                    assert savedReview != null;
                    assertEquals(10.0, savedReview.getRating());
                    assertEquals("Awesome Movie", savedReview.getComment());
                });
    }

    @Test
    void deleteReview() {
        //when
        webTestClient
                .delete()
                .uri(REVIEWS_URL + "/" + REVIEW_ID)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(Review.class);
    }
}