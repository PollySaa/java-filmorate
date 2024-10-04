package ru.yandex.practicum.filmorate.storage;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exceptions.NotFoundException;
import ru.yandex.practicum.filmorate.exceptions.ValidationException;
import ru.yandex.practicum.filmorate.model.Review;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
@Component("reviewDbStorage")
public class ReviewDbStorage implements ReviewStorage {
    final JdbcTemplate jdbcTemplate;
    String sql;
    Review review;

    public ReviewDbStorage(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Review addReview(Review review) {
        if (review.getUserId() != null && review.getFilmId() != null) {
            SimpleJdbcInsert simpleJdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                    .withTableName("reviews")
                    .usingGeneratedKeyColumns("review_id");
            review.setReviewId(simpleJdbcInsert.executeAndReturnKey(review.toMap()).intValue());
            log.info("Добавлен новый отзыв с id = {}", review.getReviewId());
            return review;
        } else {
            throw new ValidationException("Не указан id отзыва");
        }
    }

    @Override
    public Review updateReview(Review review) {
        sql = "UPDATE reviews SET content = ?, is_positive = ?, useful = ?" +
                " WHERE review_id = ?";
        jdbcTemplate.update(sql,
                review.getContent(),
                review.getIsPositive(),
                review.getUseful(),
                review.getReviewId());
        log.info("Отзыв с id = {} был обновлён", review.getReviewId());
        return review;
    }

    @Override
    public Review deleteReview(Integer id) {
        review = getReviewById(id);
        sql = "DELETE FROM reviews WHERE review_id = ?";
        jdbcTemplate.update(sql, id);
        log.info("Отзыв с id = {} был удалён", review.getReviewId());
        return review;
    }

    @Override
    public Review getReviewById(Integer id) {
        SqlRowSet reviewRows = jdbcTemplate.queryForRowSet("SELECT * FROM reviews WHERE review_id = ?", id);
        if (reviewRows.first()) {
            review = new Review(
                    reviewRows.getInt("review_id"),
                    reviewRows.getString("content"),
                    reviewRows.getBoolean("is_positive"),
                    reviewRows.getInt("user_id"),
                    reviewRows.getInt("film_id"),
                    reviewRows.getInt("useful"));
        } else {
            throw new NotFoundException("Отзыв с id = " + id + " не найден!");
        }
        return review;
    }

    @Override
    public List<Review> getReviewsByIdLimited(Integer filmId, int count) {
        if (filmId == null) {
            sql = "SELECT * FROM reviews ORDER BY useful DESC LIMIT ?";
            return jdbcTemplate.query(sql, this::mapRowToReview, count);
        }

        sql = "SELECT * FROM reviews WHERE film_id = ? ORDER BY useful DESC LIMIT ?";
        return jdbcTemplate.query(sql, this::mapRowToReview, filmId, count);
    }

    @Override
    public void addLike(Integer reviewId, Integer userId) {
        sql = "INSERT INTO review_likes(review_id, user_id, like_type) VALUES (?, ?, 'like')";
        jdbcTemplate.update(sql, reviewId, userId);
        updateUsefulCount(reviewId, 1);
    }

    @Override
    public void addDislike(Integer reviewId, Integer userId) {
        sql = "SELECT * FROM review_likes WHERE review_id = ? AND user_id = ? AND like_type = 'dislike'";
        SqlRowSet dislikeRows = jdbcTemplate.queryForRowSet(sql, reviewId, userId);
        if (dislikeRows.first()) {
            removeDislike(reviewId, userId);
            addLike(reviewId, userId);
            return;
        }

        sql = "SELECT * FROM review_likes WHERE review_id = ? AND user_id = ? AND like_type = 'like'";
        SqlRowSet likeRows = jdbcTemplate.queryForRowSet(sql, reviewId, userId);
        if (likeRows.first()) {
            removeLike(reviewId, userId);
            addDislike(reviewId, userId);
            return;
        }

        sql = "INSERT INTO review_likes(review_id, user_id, like_type) VALUES (?, ?, 'dislike')";
        jdbcTemplate.update(sql, reviewId, userId);
        updateUsefulCount(reviewId, -1);
    }

    @Override
    public void removeLike(Integer reviewId, Integer userId) {
        sql = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, reviewId, userId);
        updateUsefulCount(reviewId, -1);
    }

    @Override
    public void removeDislike(Integer reviewId, Integer userId) {
        sql = "DELETE FROM review_likes WHERE review_id = ? AND user_id = ?";
        jdbcTemplate.update(sql, reviewId, userId);
        updateUsefulCount(reviewId, 1);
    }

    private void updateUsefulCount(Integer reviewId, int delta) {
        sql = "UPDATE reviews SET useful = useful + ? WHERE review_id = ?";
        jdbcTemplate.update(sql, delta, reviewId);
    }

    private Review mapRowToReview(ResultSet rs, int rowNum) throws SQLException {
        return new Review(
                rs.getInt("review_id"),
                rs.getString("content"),
                rs.getBoolean("is_positive"),
                rs.getInt("user_id"),
                rs.getInt("film_id"),
                rs.getInt("useful"));
    }
}