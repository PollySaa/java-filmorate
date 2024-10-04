package ru.yandex.practicum.filmorate.service;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Event;
import ru.yandex.practicum.filmorate.model.EventType;
import ru.yandex.practicum.filmorate.model.Operation;
import ru.yandex.practicum.filmorate.model.Review;
import ru.yandex.practicum.filmorate.storage.EventStorage;
import ru.yandex.practicum.filmorate.storage.FilmStorage;
import ru.yandex.practicum.filmorate.storage.ReviewStorage;
import ru.yandex.practicum.filmorate.storage.UserStorage;

import java.util.List;

@Service
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ReviewService {
    ReviewStorage reviewStorage;
    UserStorage userStorage;
    FilmStorage filmStorage;
    EventStorage eventStorage;

    public ReviewService(ReviewStorage reviewStorage, UserStorage userStorage, FilmStorage filmStorage,
                         EventStorage eventStorage) {
        this.reviewStorage = reviewStorage;
        this.userStorage = userStorage;
        this.filmStorage = filmStorage;
        this.eventStorage = eventStorage;
    }

    public Review addReview(Review review) {
        performChecks(review);
        review = reviewStorage.addReview(review);
        Event event = new Event(System.currentTimeMillis(), review.getUserId(), EventType.REVIEW, Operation.ADD, review.getFilmId(), review.getReviewId());
        eventStorage.addEvent(event);
        return review;
    }

    public Review updateReview(Review review) {
        performChecks(review);
        review = reviewStorage.updateReview(review);
        Event event = new Event(System.currentTimeMillis(), review.getUserId(), EventType.REVIEW, Operation.UPDATE, review.getFilmId(), review.getReviewId());
        eventStorage.addEvent(event);
        return review;
    }

    public Review deleteReview(Integer id) {
        Review review = getReviewById(id);
        Event event =
                new Event(System.currentTimeMillis(), review.getUserId(), EventType.REVIEW, Operation.REMOVE, id, review.getReviewId());
        eventStorage.addEvent(event);
        return reviewStorage.deleteReview(id);
    }

    public Review getReviewById(Integer id) {
        return reviewStorage.getReviewById(id);
    }

    public List<Review> getReviewsByIdLimited(Integer filmId, int count) {
        return reviewStorage.getReviewsByIdLimited(filmId, count);
    }

    public void addLike(Integer reviewId, Integer userId) {
        Review review = reviewStorage.getReviewById(reviewId);
        performChecks(review);
        reviewStorage.addLike(reviewId, userId);
    }

    public void addDislike(Integer reviewId, Integer userId) {
        Review review = reviewStorage.getReviewById(reviewId);
        performChecks(review);
        reviewStorage.addDislike(reviewId, userId);
    }

    public void removeLike(Integer reviewId, Integer userId) {
        Review review = reviewStorage.getReviewById(reviewId);
        performChecks(review);
        reviewStorage.removeLike(reviewId, userId);
    }

    public void removeDislike(Integer reviewId, Integer userId) {
        Review review = reviewStorage.getReviewById(reviewId);
        performChecks(review);
        reviewStorage.removeDislike(reviewId, userId);
    }

    private void performChecks(Review review) {
        if (review.getContent() == null || review.getContent().isBlank()) {
            throw new ValidationException("Текст отзыва некорректный");
        }
        if (review.getUserId() == null || userStorage.getUserById(review.getUserId()) == null) {
            throw new ValidationException("Данные о пользователе заполнены некорректно");
        }
        if (review.getFilmId() == null || filmStorage.getFilmById(review.getFilmId()) == null) {
            throw new ValidationException("Данные о фильме заполнены некорректно");
        }
        if (review.getIsPositive() == null) {
            throw new ValidationException("Данные отзыва не заполнены");
        }
    }
}