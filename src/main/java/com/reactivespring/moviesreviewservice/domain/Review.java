package com.reactivespring.moviesreviewservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document
public class Review {
    @Id
    private String reviewId;
    @NotNull(message = "movie info id should not be null")
    private Long movieInfoId;
    private String Comment;
    @Min(value = 0L, message = "rating should not be negative")
    private Double rating;

}
