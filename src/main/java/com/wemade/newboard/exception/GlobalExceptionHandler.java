package com.wemade.newboard.exception;

import java.util.List;
import java.util.UUID;

import com.wemade.newboard.controller.ApiResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 서비스단에서의 @Valid 체크 에러
     * @param e
     * @return
     */
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> constraintViolationException(ConstraintViolationException e) {
        log.error("==> constraintViolationException", e);

        Object body = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), e.getMessage());
        return handleExceptionInternal(e, body);
    }

    /**
     * 중복키 에러
     * @param de
     * @return
     */
    @ExceptionHandler(DuplicateKeyException.class)
    protected ResponseEntity<Object> duplicateKeyException(DuplicateKeyException de) {
        log.error("==> duplicateKeyException", de);

        int errorCd = HttpStatus.CONFLICT.value();
        Object body = null;

        try {
            String errMsg = de.getMessage();
            int startIdx = errMsg.indexOf("Duplicate entry");
            if(startIdx >= 0) {
                startIdx += 16;
                int endIdx = errMsg.indexOf("for key ");
                errMsg = errMsg.substring(startIdx, endIdx);
                body = new ApiResponse<>(errorCd, errMsg + " ==> param values[" + errMsg + "] are duplicated.");
            }
        } catch(Exception e) {
            log.error("ERROR occurred while parsing error message", e);
        }

        if(body == null) {
            body = new ApiResponse<>(errorCd, "");
        }

        return handleExceptionInternal(de, body);
    }

    /**
     * Enum의 타입 체크 에러
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException hmnre) {

        log.error("==> handleHttpMessageNotReadable", hmnre);

        Object body = null;
        String errMsg = null;
        try {
            errMsg = hmnre.getMessage();
            int startIdx = errMsg.indexOf("for Enum class:");

            if (startIdx > 0) {
                startIdx += 16;
                int endIdx = errMsg.indexOf("; nested exception is");
                errMsg = errMsg.substring(startIdx, endIdx);
            }
        } catch (Exception e) {
            log.error("ERROR occurred while parsing error message", e);
        }


        body = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(),   " ==> parameters must belong to " + errMsg + ".");

        return handleExceptionInternal(hmnre, body);
    }

    /**
     *
     * @Valid (javax.valid)에 의한 유효성 검증 에러
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException e
                                                               ) {
        log.error("==> handleMethodArgumentNotValid", e);

        List<ObjectError> errList = e.getBindingResult().getAllErrors();
        String errorMessage = null;
        if(errList != null && errList.size() > 0) {
            StringBuilder stb = new StringBuilder(" ==> ");
            for(ObjectError error : errList) {
                stb.append(createDetailMessage(error)).append(" , ");
            }
            errorMessage =  stb.toString();
        }

        Object body = new ApiResponse<>(HttpStatus.BAD_REQUEST.value(), errorMessage);

        return handleExceptionInternal(e, body);
    }

    @ExceptionHandler(Exception.class)
    protected ResponseEntity<Object> defaultException(Exception e) {
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        log.error("==> defaultException[{}]", uuid, e);
        Object body = new ApiResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), "");
        return handleExceptionInternal(e, body);
    }

    /**
     * httpClientErrorException (http 통신을 할때 발생하는 에러)
     * @param e
     * @return
     */
    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
    protected ResponseEntity<Exception> handleBadRequestException(HttpClientErrorException e) {
        log.error("==> HttpClientErrorException.BadRequest", e);

        Object body = new ApiResponse<>(500, e.getMessage());
        return null;
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
            return String.format("[Object Name : %s, Message : %s]", objErr.getObjectName(), objErr.getDefaultMessage());
        }
    }


    private ResponseEntity<Object> handleExceptionInternal(Exception e, Object body) {
        log.error("", e);
        return  new ResponseEntity<Object>(body, HttpStatus.OK);

    }


}

