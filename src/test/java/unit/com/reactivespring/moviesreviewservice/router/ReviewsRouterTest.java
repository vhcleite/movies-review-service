package com.reactivespring.moviesreviewservice.router;

import com.reactivespring.moviesreviewservice.domain.Review;
import com.reactivespring.moviesreviewservice.exceptions.GlobalErrorHandler;
import com.reactivespring.moviesreviewservice.handler.MovieReviewHandler;
import com.reactivespring.moviesreviewservice.repository.MovieReviewRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.when;

@WebFluxTest
@ContextConfiguration(classes = {ReviewsRouter.class, MovieReviewHandler.class, GlobalErrorHandler.class})
@AutoConfigureWebTestClient
class ReviewsRouterTest {

    @MockBean
    private MovieReviewRepository reviewReactiveRepository;

    @Autowired
    private WebTestClient webTestClient;

    @Test
    void addReview() {
        //given
        var review = new Review(null, 1L, "Awesome Movie", 9.0);

        when(reviewReactiveRepository.save(isA(Review.class))).thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));
        //when
        webTestClient
                .post()
                .uri("/v1/reviews")
                .bodyValue(review)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(Review.class)
                .consumeWith(reviewResponse -> {
                    var savedReview = reviewResponse.getResponseBody();
                    assert savedReview != null;
                    assertNotNull(savedReview.getReviewId());
                    assertEquals("abc", savedReview.getReviewId());

                });
    }

    @Test
    void addReview_validation() {
        //given
        var review = new Review(null, null, "Awesome Movie", -9.0);

        when(reviewReactiveRepository.save(isA(Review.class))).thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));
        //when
        webTestClient
                .post()
                .uri("/v1/reviews")
                .bodyValue(review)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(String.class)
                .isEqualTo("rating should not be negative, movie info id should not be null");
    }

    @Test
    void getAllReviews() {
        //given
        var reviewList = List.of(
                new Review(null, 1L, "Awesome Movie", 9.0),
                new Review(null, 1L, "Awesome Movie1", 9.0),
                new Review(null, 2L, "Excellent Movie", 8.0));

        when(reviewReactiveRepository.findAll()).thenReturn(Flux.fromIterable(reviewList));

        //when
        webTestClient
                .get()
                .uri("/v1/reviews")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .value(reviews -> {
                    assertEquals(3, reviews.size());
                });
    }

    @Test
    void getAllReviewsByMovieInfoId() {
        //given
        var movieInfoId = 1L;
        var reviewList = List.of(
                new Review(null, movieInfoId, "Awesome Movie", 9.0),
                new Review(null, movieInfoId, "Awesome Movie1", 9.0),
                new Review(null, movieInfoId, "Excellent Movie", 8.0));

        when(reviewReactiveRepository.findByMovieInfoId(movieInfoId)).thenReturn(Flux.fromIterable(reviewList));

        //when
        webTestClient
                .get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/reviews")
                        .queryParam("movieInfoId", movieInfoId)
                        .build())
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .value(reviews -> {
                    assertEquals(3, reviews.size());
                });
    }

    @Test
    void updateReview() {
        //given
        String reviewId = "review id";

        var reviewUpdate = new Review(null, 1L, "Not an Awesome Movie", 8.0);

        when(reviewReactiveRepository.save(isA(Review.class))).thenReturn(Mono.just(new Review(reviewId, 1L, "Not an Awesome Movie", 8.0)));
        when(reviewReactiveRepository.findById((String) any())).thenReturn(Mono.just(new Review(reviewId, 1L, "Awesome Movie", 9.0)));

        webTestClient
                .put()
                .uri("/v1/reviews/{id}", reviewId)
                .bodyValue(reviewUpdate)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Review.class)
                .consumeWith(reviewResponse -> {
                    var updatedReview = reviewResponse.getResponseBody();
                    assert updatedReview != null;
                    System.out.println("updatedReview : " + updatedReview);
                    assertEquals(8.0, updatedReview.getRating());
                    assertEquals("Not an Awesome Movie", updatedReview.getComment());
                });
    }

    @Test
    void deleteReview() {
        //given
        var reviewId = "review id";
        when(reviewReactiveRepository.findById((String) any())).thenReturn(Mono.just(new Review(reviewId, 1L, "Awesome Movie", 9.0)));
        when(reviewReactiveRepository.deleteById((String) any())).thenReturn(Mono.empty());

        //when
        webTestClient
                .delete()
                .uri("/v1/reviews/{id}", reviewId)
                .exchange()
                .expectStatus().isNoContent();
    }
}