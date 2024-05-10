package com.wemade.newboard.interceptors;

import com.wemade.newboard.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
@Component
@RequiredArgsConstructor
public class JwtInterceptor implements HandlerInterceptor {

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.access-expiration}")
    private int jwtExpirationMs;

    @Value("${jwt.refresh-expiration}")
    private int refreshJWTExpiration;

    private final AuthService authService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURL = request.getRequestURL().toString();

        // FIXME : request uri를 사용해야한다.
        // FIXME : api uri를 따로 만드는것이 더 좋음

        String accessJWT = request.getHeader("Authorization");
//        String refreshJWT = request.getHeader("Refresh-token");

        if(StringUtils.isBlank(accessJWT) ){ // access token만 확인
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "No Authorization token provided");
            return false;
        }
        authService.validateAccessToken(accessJWT);
        int id = authService.getIdFromToken(accessJWT);
        request.setAttribute("reqId", id);
        //RequestContextHolder.


        return true;
    }
}
