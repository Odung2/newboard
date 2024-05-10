package com.wemade.newboard.controller;

import com.wemade.newboard.dto.FrkConstants;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/**
 * 상속받은 컨트롤러의 응답 양식
 */
public class BaseController {

    private <T> ResponseEntity<ApiResponse<T>> responseEntity(int resultCode, String resultMessage, T data) {
        ApiResponse<T> resp = new ApiResponse<T>(resultCode, resultMessage, data);
        return new ResponseEntity<ApiResponse<T>>(resp, HttpStatus.OK);
    }

    protected <T> ResponseEntity<ApiResponse<T>> ok(T data) {

        return responseEntity(FrkConstants.CD_OK, FrkConstants.SUCCESS, data);

    }

    protected <T> ResponseEntity<ApiResponse<T>> ok(String message, T data) {

        return responseEntity(FrkConstants.CD_OK, message, data);

    }

    protected <T> ResponseEntity<ApiResponse<T>> nok(int resultCode, String message, T data) {
        return responseEntity(resultCode, message, data);
    }

    protected <T> ResponseEntity<ApiResponse<T>> nok(int resultCode, T data) {
        return nok(resultCode, FrkConstants.FAIL, data);
    }

    protected ResponseEntity<ApiResponse<Void>> ok() {

        return responseEntity(FrkConstants.CD_OK, FrkConstants.SUCCESS, null);

    }

    protected <T> ResponseEntity<ApiResponse<T>> nok(T body) {
        return responseEntity(FrkConstants.CD_NOK, FrkConstants.FAIL, body);

    }

    protected <T> ResponseEntity<ApiResponse<T>> nok(int resultCode) {
        return nok(resultCode, null);
    }

    protected ResponseEntity<ApiResponse<Void>> nok() {
        return nok(FrkConstants.CD_NOK);
    }
}
