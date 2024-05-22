package com.wemade.newboard.exception;

import com.wemade.newboard.controller.ApiResponse;
import com.wemade.newboard.controller.BaseController;
import com.wemade.newboard.dto.FrkConstants;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springdoc.api.ErrorMessage;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.auth.login.CredentialException;
import java.security.SignatureException;

@RequiredArgsConstructor
@RestControllerAdvice
public class GlobalExceptionHandler extends BaseController {

    private final MessageSource messageSource;

    @ExceptionHandler(SignatureException.class)
    public ResponseEntity<ApiResponse<Object>> handleJwtSignatureException(SignatureException e){
//        ApiResponse apiResponse= new ApiResponse(400, "토큰의 서명이 올바르지 않습니다.", null);
//        return ResponseEntity.status(400).body(apiResponse);
        return nok(400, "토큰의 서명이 올바르지 않습니다.",null);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponse<Object>> handleExpiredJwtException(ExpiredJwtException e){
//        ApiResponse apiResponse = new ApiResponse(403, "토큰이 만료되었습니다", null);
//        return ResponseEntity.status(403).body(apiResponse);
        return nok(403, e.getMessage(), null);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public ResponseEntity<ApiResponse<Object>> handleMalformedJwtException (MalformedJwtException e){
//        ApiResponse apiResponse = new ApiResponse(400, "토큰의 형식이 올바르지 않습니다", null);
//        return ResponseEntity.status(400).body(apiResponse);
        return nok(401, e.getMessage(), null);
    }
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<Object>> handleJwtException(JwtException e){
//        ApiResponse apiResponse = new ApiResponse(500, "토큰 처리 중 문제가 생겼습니다", null);
//        return ResponseEntity.status(500).body(apiResponse);
        return nok(400, "토큰 처리 중 문제가 생겼습니다.", null);
    }

    @ExceptionHandler(io.jsonwebtoken.security.SignatureException.class)
    public ResponseEntity<ApiResponse<Object>> handleSignatureException(io.jsonwebtoken.security.SignatureException e){
        return nok(401, "검증되지 않은 토큰입니다.", null);
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadRequestException(BadRequestException e){
        return nok(400, e.getMessage(), null);
    }

    @ExceptionHandler(ShortPasswordException.class)
    public ResponseEntity<ApiResponse<Object>> handleShortPasswordException(ShortPasswordException e){
        return nok(403, "비밀번호는 8~16자의 영문 대/소문자, 숫자, 특수문자의 조합으로 입력해주세요.");
    }

    @ExceptionHandler(PasswordRegexException.class)
    public ResponseEntity<ApiResponse<Object>> handlePasswordRegexException(PasswordRegexException e){
        return nok(403, e.getMessage(), null);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException e){
        return nok(500, "실행 과정 중 오류가 발생했습니다.",e.getClass());
//        return nok(500, e.getMessage(),e.getClass()); // 디버깅 용
    }

    @ExceptionHandler(DuplicateKeyException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateKeyException(DuplicateKeyException e){
        return nok(409, e.getMessage());
    }

    @ExceptionHandler(GenerateNewAccessJWTException.class)
    public ResponseEntity<ApiResponse<Object>> handleGenerateNewAccessJWTException(GenerateNewAccessJWTException e){
        return ok("발급한 새 Access 토큰으로 접속해주세요." , e.getMessage());
    }

    @ExceptionHandler(TokenNotIssueException.class)
    public ResponseEntity<ApiResponse<Object>> handleTokenNotIssueExceptionException(TokenNotIssueException e){
        //e.getMessage()는 access token 을 발급해줄 수 없다는 메시지
        return nok(403, e.getMessage());
    }
    @ExceptionHandler(InvalidFileException.class)
    public ResponseEntity<ApiResponse<Object>> handleInvalidFileException(InvalidFileException e){
        return nok(400, "올바른 파일 형식이 아닙니다.", e.getMessage());
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public ResponseEntity<ApiResponse<Object>> handleUnauthorizedAccessException(UnauthorizedAccessException e){
        return nok(403, e.getMessage());
    }
    @ExceptionHandler(CredentialException.class)
    public ResponseEntity<ApiResponse<Object>> handleCredentialException(CredentialException e){
        return nok(403, e.getMessage());
    }

    @ExceptionHandler(org.webjars.NotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNotFoundException(org.webjars.NotFoundException e){
        return nok(404, "해당 정보가 존재하지 않습니다.", e.getMessage());
    }


    /**
     * javax valid에 의한 유효성 검증 에러
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
//
        ObjectError objErr = e.getBindingResult().getAllErrors().get(0);
        String detailMsg = createDetailMessage(objErr);
//        String errorMessage = getMessage(FrkConstants.CD_PARAM_ERR + "") + detailMsg;
//        String errorMessage = getMessage(FrkConstants.CD_PARAM_ERR+" ");
        Object body = new ErrorMessage(detailMsg);

//        return handleExceptionInternal(e, body, new HttpHeaders(), HttpStatus.OK, request);
        return nok(FrkConstants.CD_PARAM_ERR, body);
    }

    /**
     * javax valid에서 발생한 에러의 메세지 생성
     * @param objErr
     * @return
     */
    private String createDetailMessage(ObjectError objErr) {
        if (objErr instanceof FieldError) {
            FieldError fieldErr = (FieldError) objErr;
            return String.format("[Field : %s, Message : %s]", fieldErr.getField(), fieldErr.getDefaultMessage());
        } else {
            return String.format("[Object Name : %s, Message : %s]", objErr.getObjectName(),
                    objErr.getDefaultMessage());
        }
    }

    private String getMessage(String code) {
        return getMessage(code, null);
    }

    private String getMessage(String code, Object[] args) {
        return messageSource.getMessage(code, args, LocaleContextHolder.getLocale());
    }
}
