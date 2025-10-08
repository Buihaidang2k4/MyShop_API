package com.example.MyShop_API.service.token_blacklist;

import com.example.MyShop_API.entity.RevokedToken;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.exception.ErrorCode;
import com.example.MyShop_API.repo.RevokedTokenRepository;
import jakarta.annotation.PostConstruct;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class TokenBlacklistService {
    private static final String PREFIX = "blacklist:refresh_token:";
    private static final String USER_TOKEN_LIST_PREFIX = "refresh_tokens:user:";
    RedisTemplate<String, Object> redisTemplate;
    RevokedTokenRepository revokedTokenRepository;

    /**
     * Thêm token vào blacklist với TTL
     */
//    public void blacklist(String token, Duration duration) {
//        String key = PREFIX + token;
//        try {
//            // Kiểm tra xem token đã bị thu hồi chưa
//            boolean alreadyRevokedInRedis = Boolean.TRUE.equals(redisTemplate.hasKey(key));
//            boolean alreadyRevokedInDb = revokedTokenRepository.existsByToken(token);
//
//            // Luôn ghi vào Redis để đảm bảo TTL cập nhật
//            redisTemplate.opsForValue().set(key, true, duration);
//
//            // Chỉ ghi vào DB nếu chưa có
//            if (!alreadyRevokedInDb) {
//                revokedTokenRepository.save(RevokedToken.builder()
//                        .token(token)
//                        .revokedAt(Instant.now())
//                        .expiresAt(Instant.now().plus(duration))
//                        .build());
//            } else {
//                log.info("Token [{}] already exists in DB, skipped insert", token);
//            }
//
//        } catch (Exception e) {
//            log.error("Failed to blacklist token [{}]: {}", token, e.getMessage());
//            throw new AppException(ErrorCode.REDIS_ERROR);
//        }
//    }
    public void blacklist(String token, Duration duration) {
        String key = PREFIX + token;
        try {
            // Luôn ghi vào Redis để đảm bảo TTL cập nhật
            redisTemplate.opsForValue().set(key, true, duration);

            // Kiểm tra DB: chỉ ghi nếu chưa có hoặc đã hết hạn
            RevokedToken existing = revokedTokenRepository.findByToken(token).orElse(null);
            Instant now = Instant.now();

            if (existing == null || existing.getExpiresAt().isBefore(now)) {
                revokedTokenRepository.save(RevokedToken.builder()
                        .token(token)
                        .revokedAt(now)
                        .expiresAt(now.plus(duration))
                        .build());
                log.info("Token [{}] saved to DB", token);
            } else {
                log.info("Token [{}] already exists and valid in DB, skipped insert", token);
            }

        } catch (Exception e) {
            log.error("Failed to blacklist token [{}]: {}", token, e.getMessage());
            throw new AppException(ErrorCode.REDIS_ERROR);
        }
    }

    /**
     * Kiểm tra token có bị thu hồi không
     */
    public boolean isBlacklisted(String token) {
        String key = PREFIX + token;
        try {
            Boolean exists = redisTemplate.hasKey(key);
            if (Boolean.TRUE.equals(exists)) return true;
            return revokedTokenRepository.existsByToken(token);
        } catch (Exception e) {
            log.info("Redis failed to blacklist token [{}]: {}", token, e.getMessage());
            return false;
        }
    }

    /**
     * Nếu token bị thu hồi thì ném lỗi xác thực
     */
    public void validate(String token) {
        if (isBlacklisted(token)) {
            log.warn("Token [{}] is blacklisted", token);
            throw new AppException(ErrorCode.TOKEN_REVOKED);
        }
    }

    /**
     * Lưu refresh token mới vào danh sách của user
     */
    public void storeRefreshToken(Long userId, String token) {
        String listKey = USER_TOKEN_LIST_PREFIX + userId;
        redisTemplate.opsForList().rightPush(listKey, token);
        log.info("Stored refresh token for user [{}]", userId);
    }

    /**
     * Thu hồi toàn bộ token cũ của user trước khi login mới
     */
    @Transactional
    public void revokeAllTokensForUser(Long userId, Duration ttl) {
        String listKey = USER_TOKEN_LIST_PREFIX + userId;
        List<Object> tokens = redisTemplate.opsForList().range(listKey, 0, -1);

        if (tokens != null && !tokens.isEmpty()) {
            for (Object tokenObj : tokens) {
                String token = tokenObj.toString();
                blacklist(token, ttl);
            }
            redisTemplate.delete(listKey);
        } else {
            log.info("No previous tokens found for user [{}]", userId);
        }
    }

    /**
     * Đồng bộ lại Redis từ database khi server khởi động lại
     */
    @PostConstruct
    public void reloadFromDatabase() {
        try {
            List<RevokedToken> tokens = revokedTokenRepository.findAllValid(Instant.now());
            for (RevokedToken token : tokens) {
                long ttl = Duration.between(Instant.now(), token.getExpiresAt()).toMillis();
                if (ttl > 0) {
                    redisTemplate.opsForValue().set(PREFIX + token.getToken(), true, Duration.ofMillis(ttl));
                }
            }
        } catch (Exception e) {
            log.error("Failed to reload revoked tokens: {}", e.getMessage());
        }
    }

    /**
     * Auto delete token expiredToken
     */
    @Scheduled(cron = "0 0 3 * * *")
    @Transactional
    public void cleanExpiredTokens() {
        Instant now = Instant.now();
        int deletedCount = revokedTokenRepository.deleteExpiredTokens(now);
        log.info("🧹 Cleaned {} expired tokens at {}", deletedCount, now);
    }

}
