package com.app.gizmophile.repository;

import com.app.gizmophile.model.Review;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    @Modifying
    @Transactional
    @Query("DELETE FROM Review r WHERE r.id = :id")
    void deleteReviewById(@Param("id") Long id);
}
