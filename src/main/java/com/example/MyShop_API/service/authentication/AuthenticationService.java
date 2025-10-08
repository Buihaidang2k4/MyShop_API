package com.example.MyShop_API.service.authentication;

import com.example.MyShop_API.dto.request.AuthenticationRequest;
import com.example.MyShop_API.dto.request.IntrosprectRequest;
import com.example.MyShop_API.dto.response.AuthenticationResponse;
import com.example.MyShop_API.dto.response.IntrospectResponse;
import com.example.MyShop_API.entity.User;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.exception.ErrorCode;
import com.example.MyShop_API.repo.UserRepository;
import com.example.MyShop_API.service.token_blacklist.TokenBlacklistService;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService implements IAuthenticationService {
    private static final String REFRESH_COOKIES_NAME = "refresh_token";
    private static final Duration REFRESH_COOKIES_EXPIRATION = Duration.ofDays(7);

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    TokenBlacklistService blacklistService;

    @NonFinal
    @Value("${jwt.secret}")
    protected String TOKEN_KEY;
    @NonFinal
    @Value("${jwt.expiration}")
    protected Long ACCESS_TOKEN_DURATION;
    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected Long REFRESH_TOKEN_DURATION;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        var user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_EXISTED));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // Thu hồi toàn bộ token cũ trước khi sinh token mới
        blacklistService.revokeAllTokensForUser(user.getId(), Duration.ofMillis(REFRESH_TOKEN_DURATION));

        // Sinh token mới
        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);

        // Lưu token mới vào Redis (không ghi DB)
        blacklistService.storeRefreshToken(user.getId(), refreshToken);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }


    public String refreshToken(String token) throws ParseException, JOSEException {
        // check token revoked
        blacklistService.validate(token);

        SignedJWT signedJWT = verifyToken(token, true);

        String username = signedJWT.getJWTClaimsSet().getSubject();

        User user = userRepository.findByUsername(username).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_EXISTED));

        try {
            return generateAccessToken(user);
        } catch (Exception e) {
            throw new RuntimeException("Cannot create access token from refresh token: " + e);
        }
    }

    public IntrospectResponse introspect(IntrosprectRequest request) throws ParseException, JOSEException {
        boolean isValid = true;

        try {
            SignedJWT signedJWT = verifyToken(request.getToken(), false);
            blacklistService.validate(request.getToken());
        } catch (AppException e) {
            isValid = false;
        }

        return IntrospectResponse.builder()
                .valid(isValid)
                .build();
    }

    public void logout(String refreshToken) throws ParseException, JOSEException {
        try {
            // revoke then add blacklist
            blacklistService.blacklist(refreshToken, Duration.ofMillis(REFRESH_TOKEN_DURATION));

        } catch (AppException e) {
            log.error("Logout failed for token [{}]: {}", refreshToken, e.getMessage());
            throw new AppException(ErrorCode.TOKEN_REVOKED);
        }
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws ParseException, JOSEException {
        // Parse token thanh signedJWT
        SignedJWT signedJWT = SignedJWT.parse(token);

        // Veryfier chu ky voi key
        JWSVerifier verifier = new MACVerifier(TOKEN_KEY.getBytes());
        boolean verified = signedJWT.verify(verifier);
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();
        String type = signedJWT.getJWTClaimsSet().getClaim("type").toString();

        // Neu chu ky khong dung && het han
        if (!verified) throw new AppException(ErrorCode.UNAUTHENTICATED);
        if (expiryTime.before(new Date())) throw new AppException(ErrorCode.TOKEN_EXPIRED);
        if (isRefresh && !"refresh_token".equals(type)) throw new AppException(ErrorCode.UNAUTHENTICATED);
        if (!isRefresh && !"access_token".equals(type)) throw new AppException(ErrorCode.UNAUTHENTICATED);

        return signedJWT;
    }


    private String generateAccessToken(User user) {
        return buildToken(user, Duration.ofMillis(ACCESS_TOKEN_DURATION), "access_token");
    }

    private String generateRefreshToken(User user) {
        return buildToken(user, Duration.ofMillis(REFRESH_TOKEN_DURATION), "refresh_token");
    }

    private String buildToken(User user, Duration duration, String type) {
        try {
            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .subject(user.getUsername())
                    .issuer("ANH DANG")
                    .issueTime(new Date())
                    .expirationTime(new Date(Instant.now().plus(duration).toEpochMilli()))
                    .jwtID(UUID.randomUUID().toString())
                    .claim("scope", buildScope(user))
                    .claim("type", type)
                    .build();

            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

            log.info("JWT payload: {}", claimsSet.toJSONObject().toString());
            signedJWT.sign(new MACSigner(TOKEN_KEY.getBytes(StandardCharsets.UTF_8)));
            return signedJWT.serialize();
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }
    }

    private String buildScope(User user) {
        // Phan cach nhau boi dau cach
        StringJoiner stringJoiner = new StringJoiner(" ");

        if (!CollectionUtils.isEmpty(user.getRoles()))
            user.getRoles().forEach(role -> stringJoiner.add("ROLE_" + role.getRoleName()));

        return stringJoiner.toString();
    }

    public ResponseCookie logoutAndGetCookie(String refreshToken) {
        if (refreshToken != null && !refreshToken.isBlank()) {
            try {
                logout(refreshToken); // blacklist token
            } catch (ParseException e) {
                throw new RuntimeException(e);
            } catch (JOSEException e) {
                throw new RuntimeException(e);
            }
        }

        // tạo cookie xóa trên client
        return ResponseCookie.from(REFRESH_COOKIES_NAME, "")
                .httpOnly(true)
                .maxAge(0)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .build();
    }

    public ResponseCookie buildRefreshCookie(String refreshToken) {
        return ResponseCookie.from(REFRESH_COOKIES_NAME, refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .maxAge(REFRESH_COOKIES_EXPIRATION)
                .path("/")
                .build();
    }

    public String getRefreshTokenFromRequest(HttpServletRequest request) {
        return Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(c -> REFRESH_COOKIES_NAME.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);

    }
}



