package com.example.MyShop_API.service.authentication;

import com.example.MyShop_API.anotation.AllAccess;
import com.example.MyShop_API.dto.request.AuthenticationRequest;
import com.example.MyShop_API.dto.request.IntrospectRequest;
import com.example.MyShop_API.dto.request.UserCreationRequest;
import com.example.MyShop_API.dto.response.AuthenticationResponse;
import com.example.MyShop_API.dto.response.IntrospectResponse;
import com.example.MyShop_API.entity.User;
import com.example.MyShop_API.exception.AppException;
import com.example.MyShop_API.exception.ErrorCode;
import com.example.MyShop_API.mapper.UserMapper;
import com.example.MyShop_API.repo.UserRepository;
import com.example.MyShop_API.service.token_blacklist.TokenBlacklistService;
import com.example.MyShop_API.service.user.UserService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
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

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class AuthenticationService implements IAuthenticationService {
    private static final Duration REFRESH_COOKIES_EXPIRATION = Duration.ofDays(7);
    private final static String REFRESH_COOKIES_NAME = "refresh_token";

    UserRepository userRepository;
    UserService userService;
    UserMapper userMapper;
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
    @NonFinal
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    protected String GOOGLE_CLIENT_ID;

    /**
     * Login with jwt && redis
     *
     * @param request
     * @return
     */
    public AuthenticationResponse authenticate(AuthenticationRequest request) {

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow(
                () -> new AppException(ErrorCode.ACCOUNT_NOT_EXISTED)
        );

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

    /**
     * Login with google && jwt && redis
     *
     * @param request
     * @return {@link AuthenticationResponse}
     * @throws ParseException
     * @throws JOSEException
     */
    public AuthenticationResponse authenticateGoogle(IntrospectRequest request) throws GeneralSecurityException, IOException {
        GoogleIdToken idToken = verifyIdToken(request.getToken());

        String email = idToken.getPayload().getEmail();
        log.info("Google user verified successfully: {}", email);

        if (email == null || email.trim().isEmpty()) {
            log.error("Email is null or empty from Google token");
            throw new AppException(ErrorCode.USER_INVALID);
        }

        User user = userRepository.findByEmail(email).orElseGet(() -> {
            // Lấy name từ Google token trong lambda để tránh lỗi final
            String userName = (String) idToken.getPayload().get("name");
            if (userName == null || userName.trim().isEmpty()) {
                userName = email.split("@")[0];
            }
            // neu chua co tao moi
            UserCreationRequest userCreationRequest = UserCreationRequest.builder()
                    .email(email)
                    .username(userName)
                    .password(passwordEncoder.encode(UUID.randomUUID().toString()))
                    .build();
            return userMapper.toEntity(userService.createUser(userCreationRequest));
        });

        blacklistService.revokeAllTokensForUser(user.getId(), Duration.ofMillis(REFRESH_TOKEN_DURATION));

        String accessToken = generateAccessToken(user);
        String refreshToken = generateRefreshToken(user);

        log.info("Google ID token verification successfully: {}", accessToken);
        log.info("Google ID token verification successfully: {}", refreshToken);

        blacklistService.storeRefreshToken(user.getId(), refreshToken);

        return new AuthenticationResponse(accessToken, refreshToken);
    }

    public String refreshToken(String token) throws ParseException, JOSEException {
        // check token revoked
        blacklistService.validate(token);

        SignedJWT signedJWT = verifyToken(token, true);

        String email = signedJWT.getJWTClaimsSet().getSubject();

        User user = userRepository.findByEmail(email).orElseThrow(() ->
                new AppException(ErrorCode.USER_NOT_EXISTED));

        try {
            return generateAccessToken(user);
        } catch (Exception e) {
            throw new RuntimeException("Cannot create access token from refresh token: " + e);
        }
    }

    public IntrospectResponse introspect(IntrospectRequest request)
            throws JOSEException, ParseException {
        boolean isValid = true;
        String token = request.getToken();

        JWTClaimsSet claimsSet = decodeToken(token);
        Date expiration = claimsSet.getExpirationTime();
        Instant exp = expiration.toInstant();

        try {
            verifyToken(token, false);
        } catch (AppException e) {
            isValid = false;
            log.warn("Token introspection failed: {}", e.getMessage());
        }

        return IntrospectResponse.builder()
                .valid(isValid)
                .exp(exp)
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

    public GoogleIdToken verifyIdToken(String token) throws GeneralSecurityException, IOException {
        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                GoogleNetHttpTransport.newTrustedTransport(),
                JacksonFactory.getDefaultInstance()
        ).setAudience(Collections.singletonList(GOOGLE_CLIENT_ID)).build();

        log.info("Google verifier built successfully");

        GoogleIdToken idToken = verifier.verify(token);
        if (idToken == null) {
            log.error("Google ID token verification failed - token is null");
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return idToken;
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
                    .subject(user.getEmail())
                    .issuer("ANH DANG")
                    .issueTime(new Date())
                    .expirationTime(new Date(Instant.now().plus(duration).toEpochMilli()))
                    .jwtID(UUID.randomUUID().toString())
                    .claim("scope", buildScope(user))
                    .claim("type", type)
                    .build();

            SignedJWT signedJWT = new SignedJWT(new JWSHeader(JWSAlgorithm.HS256), claimsSet);

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

    public ResponseCookie clearCookie(String cookieName) {
        return ResponseCookie.from(cookieName, "")
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .build();
    }

    public ResponseCookie buildCookie(String refreshToken, String cookieName) {
        return ResponseCookie.from(cookieName, refreshToken)
                .httpOnly(true)
                .secure(true)
                .sameSite("Strict")
                .maxAge(REFRESH_COOKIES_EXPIRATION)
                .path("/")
                .build();
    }

    public String getTokenFromCookie(HttpServletRequest request, String cookieName) {
        return Arrays.stream(Optional.ofNullable(request.getCookies()).orElse(new Cookie[0]))
                .filter(c -> cookieName.equals(c.getName()))
                .map(Cookie::getValue)
                .findFirst()
                .orElse(null);
    }

    @Override
    public JWTClaimsSet decodeToken(String token) throws ParseException {
        SignedJWT signedJWT = SignedJWT.parse(token);
        return signedJWT.getJWTClaimsSet();
    }
}



