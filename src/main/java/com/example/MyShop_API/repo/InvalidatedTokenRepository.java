package com.example.MyShop_API.repo;

import com.example.MyShop_API.entity.InvaildatedToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;

@Repository
public interface InvalidatedTokenRepository extends JpaRepository<InvaildatedToken, String> {
    void deleteAllByExpiryTimeBefore(Date expiryTime);
}
