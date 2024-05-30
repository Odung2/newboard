package com.wemade.newboard.service;

import com.wemade.newboard.dto.TokensDTO;
import com.wemade.newboard.dto.UserDTO;
import com.wemade.newboard.exception.*;
import com.wemade.newboard.param.LoginParam;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.webjars.NotFoundException;

import javax.security.auth.login.CredentialException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Date;

import static com.wemade.newboard.dto.FrkConstants.*;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final RedisService redisService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-expiration}")
    private int accessJWTExpirationMs;

    @Value("${jwt.refresh-expiration}")
    private int refreshJWTExpirationMs;

    /**
     * 로그인(credential 확인) 및 [액세스 토큰, 리프레시 토큰] 발급
     * @param loginParam [userId, password]
     * @return issueTokens[accessJWT, refreshJWT]
     * @throws Exception
     */
    public TokensDTO loginAndIssueTokens(LoginParam loginParam) throws Exception { //순서에 영향을 받음 PARAM을 .. 재사용성이 있으면 parameter을 써라

        // DB에 저장된 유저 정보 존재 확인
        UserDTO user = userService.getUser(loginParam.getUserId());
        validatePassword(user, loginParam.getPassword());
        TokensDTO issueTokens = issueTokens(user);

        return issueTokens;
    }

    /**
     * DB에 저장된 유저의 비밀번호 일치 확인, 실패 시 로그인 실패 횟수 +1
     * @param user // DB에 저장된 유저 정보
     * @param loginPassword // 로그인 시 입력한 비밀번호
     * @throws CredentialException // 비밀번호 불일치 시 예외처리
     * @throws NoSuchAlgorithmException // 비밀번호 암호화 알고리즘 존재 확인
     */
    public void validatePassword(UserDTO user, String loginPassword) throws CredentialException, NoSuchAlgorithmException {
        String hashedPassword = userService.plainToSha256(loginPassword, user.getUserId());
        // 저장된 비밀번호와 입력된 비밀번호가 일치하지 않으면
        if (!user.getPassword().equals(hashedPassword)) {
            throw new CredentialException("비밀번호가 일치하지 않습니다.");
        }
    }

    /**
     * 로그인 후, 액세스 토큰과 리프레시 토큰을 발급함.
     * @param user // 액세스 토큰 발급에 필요한 유저 정보
     * @return issuedTokens // accessJWT, refreshJWT
     */
    public TokensDTO issueTokens(UserDTO user) {
        TokensDTO issuedTokens = new TokensDTO();
        // 액세스 토큰 발급 후 set
        issuedTokens.setAccessToken(generateAccessJWT(user));
        // 리프레시 토큰 발급 후 set
        issuedTokens.setRefreshToken(generateRefreshJWT());
        // redis 에 액세스 토큰, 리프레시 토큰 저장
        redisService.setValues(issuedTokens.getAccessToken(), issuedTokens.getRefreshToken());
        // 토큰을 발급해줌.
        return issuedTokens;
    }


    /**
     * 액세스 토큰과 리프레시 토큰을 받고 유효성 검증 후 새 액세스 토큰 발급
     * @param accessJWT
     * @param refreshJWT
     * @return newAccessToken
     * @throws RuntimeException
     * @throws TokenNotIssueException
     */
    public String issueNewAccessToken(String accessJWT, String refreshJWT) throws RuntimeException, TokenNotIssueException {
        // access token은 어차피 만료이므로 format만 먼저 확인
        String onlyJWT = validateAccessTokenFormat(accessJWT);
        // access token - refresh token pair가 redis에 존재하는지 확인, refresh token 유효 확인
        validateRefreshToken(onlyJWT, refreshJWT);
        // refresh token 유효, redis에 존재하고, access token 만료 시 새 액세스 토큰 발급
        return attemptReissueAccessToken(onlyJWT, refreshJWT);
    }

    /**
     * Jwt Interceptor 에서 액세스 토큰 검증
     * @param accessJWT
     */
    public void validateAccessToken(String accessJWT) {
        validateAccessTokenClaim(validateAccessTokenFormat(accessJWT));
    }

    /**
     * 액세스 토큰 format 검증. 액세스 토큰은 "Bearer "로 와야 함
     * @param accessJWT
     * @return
     * @throws MalformedJwtException
     */
    public String validateAccessTokenFormat(String accessJWT) throws MalformedJwtException {
        if(StringUtils.isBlank(accessJWT) || !accessJWT.startsWith("Bearer ")) { // 7자 이상 조건도 만족
            throw new MalformedJwtException("유효하지 않은 토큰 형식입니다.");
        }
        String jwt = accessJWT.substring(7);
        // 만약 "Bearer "로 토큰을 보내면  "" 만 다음 로직으로 전달되는 것을 방지
        if (jwt.isBlank()) {
            throw new MalformedJwtException("유효하지 않은 토큰 형식입니다.");
        }
        return jwt;
    }

    /**
     * 리프레시 토큰의 만료/유효성 확인
     * @param refreshJWT
     */
    public void validateRefreshTokenClaim(String refreshJWT) {
        validateTokenClaim(refreshJWT, "refresh token이 만료되었습니다. 다시 로그인해주세요.");
    }

    /**
     * 액세스 토큰의 만료/유효성 확인
     * @param accessJWT
     */
    public void validateAccessTokenClaim(String accessJWT) {
        validateTokenClaim(accessJWT, "access token이 만료되었습니다. 새로운 액세스 토큰을 발급받으세요.");
    }

    /**
     * 전달받은 토큰의 만료/유효성 확인, 만료 시 전달받은 문구로 예외 처리(access/refresh 문구)
     * @param jwt
     * @param errorMessage
     */
    public void validateTokenClaim(String jwt, String errorMessage) {
        try {
            extractJWTClaims(jwt);
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(), errorMessage);
        } catch ( MalformedJwtException e){
            throw new MalformedJwtException("토큰 형식이 올바르지 않습니다.");
        } catch (SignatureException e) {
            throw new SignatureException(e.getMessage());
        } catch (ClaimJwtException e){
            throw new JwtException("Jwt 권한 claim 검사를 실패했습니다.");
        }
    }

    /**
     * redis에 저장된 리프레시 토큰, 입력받은 리프레시 토큰 검증, 리프레시 토큰 만료 확인
     * @param accessJWT
     * @param refreshJWT
     */
    public void validateRefreshToken(String accessJWT, String refreshJWT) {
        // redis에 저장된 refresh token 가져오기
        String storedRefreshJWT = redisService.getValues(accessJWT);
        // redis와 입력받은 refresh token이 일치하지 않는 경우
        if(!refreshJWT.equals(storedRefreshJWT)) throw new NotFoundException("access token에 해당하는 refresh token이 존재하지 않습니다. 다시 로그인 해주세요.");
        // refresh token도 만료되지 않았는지 확인
        validateRefreshTokenClaim(refreshJWT);
    }

    /**
     * 액세스 토큰, 리프레시 토큰 재발급 시도, 만약 액세스 토큰 유효하면 발급 불가능
     * @param accessJWT
     * @param refreshJWT
     * @return
     * @throws TokenNotIssueException
     */
    public String attemptReissueAccessToken(String accessJWT, String refreshJWT) throws TokenNotIssueException {
        try {
            extractJWTClaims(accessJWT);
        } catch (ExpiredJwtException e) {
            return reissueAccessToken(e, refreshJWT, accessJWT);
        }
        throw new TokenNotIssueException("액세스 토큰이 만료되지 않아 새 액세스 토큰을 발급할 수 없습니다.");
    }

    /**
     * 새 액세스 토큰 발급, redis에 반영
     * @param e
     * @param refreshJWT
     * @return
     */
    public String reissueAccessToken(ExpiredJwtException e, String refreshJWT, String accessToken) {
        // access token은 만료되어야 새로 access token을 발급 해줌.(무한 발급 방지)
        int id = Integer.parseInt(e.getClaims().getSubject());
        String userId = e.getClaims().get("userId", String.class);
        // 기존 accessJWT(key), refresh(value) pair는 redis에서 삭제
        redisService.deleteValues(accessToken);
        // 새로운 Access JWT 발급
        String newAccessJWT = generateAccessJWT(id, userId);
        redisService.setValues(newAccessJWT, refreshJWT); // redis에 새 조합 등록
        return newAccessJWT;
    }

    /**
     * 사용자 정보 기반 액세스 토큰 생성
     * @param user 이 중 [id, userId]를 토큰에 반영
     * @return accessJWT
     */
    private String generateAccessJWT(UserDTO user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessJWTExpirationMs);

        return Jwts.builder()
                .setSubject(Long.toString(user.getUserNo()))
                .claim("userId", user.getUserId())
                .claim("type", "access")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 사용자 id, userId 기반 액세스 토큰 생성
     * @param id id
     * @param userId userId
     * @return accessJWT
     */
    private String generateAccessJWT(int id, String userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessJWTExpirationMs);

        return Jwts.builder()
                .setSubject(String.valueOf(id))
                .claim("userId", userId)
                .claim("type", "access")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * 리프레시 토큰 생성 - 개인정보 없음
     * @return refreshJWT
     */
    private String generateRefreshJWT() {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshJWTExpirationMs);

        return Jwts.builder()
                .setSubject("weboard")
                .claim("type", "refresh")
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS512)
                .compact();
    }

    /**
     * JWT에서 클레임 추출(for 유효성 검사)
     * @param JWT 분석할 JWT("Bearer " 삭제된 것)
     * @return 추출된 JWT 클레임
     */
    public Jws<Claims> extractJWTClaims(String JWT){
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(JWT);
    }

    /**
     * JWT애서 사용자의 id 추출
     * @param JWT 검증할 JWT 토큰
     * @return id
     */
    public Integer getIdFromToken(String JWT) {
        if (JWT == null || !JWT.startsWith("Bearer ")) {
            throw new IllegalArgumentException("토큰이 없거나 유효하지 않습니다.");
        }
        String jwtToken = JWT.substring(7);
        try {
            return Integer.parseInt(extractJWTClaims(jwtToken).getBody().getSubject());
        } catch (ExpiredJwtException e) {
            throw new RuntimeException("액세스 토큰이 만료되었습니다.");
        } catch (MalformedJwtException e) {
            throw new MalformedJwtException("액세스 토큰이 유효하지 않습니다.");
        }
    }

    /**
     * JWT 서명 키를 생성합니다.
     * JWT 서명에 사용될 비밀키를 생성하고 반환합니다.
     * @return 생성된 서명 키
     */
    private Key getSigningKey() {
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
