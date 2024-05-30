package com.wemade.newboard.interceptors;

import com.wemade.newboard.service.AuthService;
import com.wemade.newboard.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.cors.CorsUtils;
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
    private final UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if(CorsUtils.isPreFlightRequest(request)){
            return true;
        }

        String accessJWT = request.getHeader("Authorization");
//        String refreshJWT = request.getHeader("Refresh-token");

        if(StringUtils.isBlank(accessJWT) ){ // access token만 확인
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "No Authorization token provided");
//            return false;
        }

        authService.validateAccessToken(accessJWT);
        int id = authService.getIdFromToken(accessJWT);
        request.setAttribute("reqId", id);
        // user 존재 검증, 만약 없으면 에러 발생.
        userService.getUser(id);
        //RequestContextHolder.


        return true;
    }
}
