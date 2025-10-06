package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.RevokedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface RevokedTokenRepository extends JpaRepository<RevokedToken, Long> {
    boolean existsByToken(String token);

    @Query("SELECT r FROM RevokedToken r WHERE r.expiresAt > :now")
    List<RevokedToken> findAllValid(@Param("now") Instant now);

}
