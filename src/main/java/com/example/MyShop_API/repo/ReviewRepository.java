package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    boolean existsByProfileProfileIdAndProductProductIdAndDeletedFalse(Long profileId, Long productId);

    Page<Review> findByProductProductIdAndDeletedFalse(Long productId, Pageable pageable);
}
