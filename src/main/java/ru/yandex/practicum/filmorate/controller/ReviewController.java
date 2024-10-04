package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.service.ReviewService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;

    @PostMapping
    public Review addReview(@RequestBody Review review) {
        return reviewService.addReview(review);
    }

    @PutMapping
    public Review updateReview(@RequestBody Review review) {
        return reviewService.updateReview(review);
    }

    @DeleteMapping("/{id}")
    public Review deleteReview(@PathVariable Integer id) {
        return reviewService.deleteReview(id);
    }

    @GetMapping
    public List<Review> getReviewsByIdLimited(@RequestParam(required = false) Integer filmId,
                                              @RequestParam(defaultValue = "10") int count) {
        return reviewService.getReviewsByIdLimited(filmId, count);
    }

    @GetMapping("/{id}")
    public Review getReviewById(@PathVariable Integer id) {
        return reviewService.getReviewById(id);
    }

    @PutMapping("/{id}/like/{user-id}")
    public void addLike(@PathVariable("id") Integer id, @PathVariable("user-id") Integer userId) {
        reviewService.addLike(id, userId);
    }

    @DeleteMapping("/{id}/like/{user-id}")
    public void removeLike(@PathVariable("id") Integer id, @PathVariable("user-id") Integer userId) {
        reviewService.removeLike(id, userId);
    }

    @PutMapping("/{id}/dislike/{user-id}")
    public void addDislike(@PathVariable("id") Integer id, @PathVariable("user-id") Integer userId) {
        reviewService.addDislike(id, userId);
    }

    @DeleteMapping("/{id}/dislike/{user-id}")
    public void removeDislike(@PathVariable("id") Integer id, @PathVariable("user-id") Integer userId) {
        reviewService.removeDislike(id, userId);
    }
}