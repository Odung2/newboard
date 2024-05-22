package com.wemade.newboard.dto;

public class FrkConstants {
    public static final int CD_OK = 0;
    public static final int CD_NOK = 300;
    public static final String FAIL = "Fail";
    public static final String SUCCESS = "SUCCESS";
    /**
     * 비밀번호 조건은 다음과 같음
     * 1. 8자 이상 16자 이하
     * 2. 숫자 포함
     * 3. 특수문자 포함
     * 4. 12자 미만인 경우 영문 대/소문자 둘 다 포함
     * 5. 12자 이상인 경우 영문 대소문자 구분없이 포함
     */
    public static final String passwordRegexUnder12 = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[\\{\\}\\[\\]\\/?.,;:|\\)*~`!^\\-_+<>@#$%&\\\\=\\(\\'\\\"]).{8,11}$";
    public static final String passwordRegex12orMore = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[\\{\\}\\[\\]\\/?.,;:|\\)*~`!^\\-_+<>@#$%&\\\\=\\(\\'\\\"]).{12,16}$";
    public final static int CD_PARAM_ERR = 400;


}