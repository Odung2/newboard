package com.wemade.newboard.service;

import com.wemade.newboard.dto.FrkConstants;
import com.wemade.newboard.dto.UserDTO;
import com.wemade.newboard.exception.PasswordRegexException;
import com.wemade.newboard.mapper.UserMapper;
import com.wemade.newboard.param.FindPasswordParam;
import com.wemade.newboard.param.SignupParam;
import com.wemade.newboard.param.UpdateUserParam;
import com.wemade.newboard.response.MyInfoRes;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.webjars.NotFoundException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;
import java.util.regex.Pattern;


@Service
@RequiredArgsConstructor
public class UserService {
    private final UserMapper userMapper;
    private final EmailService emailService;

    /**
     * 유저 정보
      * @param userId
     * @return
     */
    public UserDTO getUser(String userId) {
        UserDTO user = userMapper.getUserByUserId(userId);
        if (user == null) {
            throw new NotFoundException("사용자를 찾을 수 없습니다.");
        }
        return user;
    }

    /**
     * 유저 정보
     * @param userNo
     * @return
     */
    public UserDTO getUser(int userNo) {
        UserDTO user = userMapper.getUserByUserNo(userNo);
        if (user == null) {
            throw new NotFoundException("사용자를 찾을 수 없습니다.");
        }
        return user;
    }

    /**
     * 유저 정보
     * @param email
     * @return
     */
    public UserDTO getUserByEmail(String email) {
        UserDTO user = userMapper.getUserByEmail(email);
        if (user == null) {
            throw new NotFoundException("사용자를 찾을 수 없습니다.");
        }
        return user;
    }

    /**
     * 새로운 비밀번호 발급(비밀번호 찾기)
     * @param findPasswordParam
     * @return
     */
    public String findPassword(FindPasswordParam findPasswordParam) throws NoSuchAlgorithmException {
        UserDTO user = userMapper.getUserByUserId(findPasswordParam.getUserId());

        // 저장된 이메일과 입력된 이메일이 다를 시
        if( !(findPasswordParam.getEmail().equals(user.getEmail()))){
            throw new NotFoundException("해당되는 이메일이 아닙니다.");
        }

        // 임시 비밀번호 발급
        String tempPassword = generateTemporaryPassword();
        user.setPassword(plainToSha256(tempPassword));

        // 비밀번호 유효성 검사를 하지 않고 바로 수정함.
        userMapper.update(user);

        // 임시 비밀번호를 메일로 발송
        emailService.sendEmail(user.getEmail(), "Password Reset", "Your new password is: " + tempPassword);

        return "이메일로 임시 비밀번호를 발송했습니다.";
    }

    private String generateTemporaryPassword() {
        return UUID.randomUUID().toString().substring(0, 12);
    }

    /**
     * 새로운 사용자를 데이터베이스에 삽입합니다.
     * @param signupParam 사용자 정보
     * @return 삽입된 사용자 정보
     * @throws Exception 비밀번호 검증 실패 시 예외 발생
     */
    @Transactional
    public String insertUser(SignupParam signupParam) throws NoSuchAlgorithmException {

        // 중복 아이디, 이메일 체크
        validateDuplicateUser(signupParam.getUserId());
        validateDuplicateEmail(signupParam.getEmail());
        // 비밀번호 유효성 체크
        validatePassword(signupParam.getPassword());
        signupParam.setPassword(plainToSha256(signupParam.getPassword()));

        userMapper.insert(signupParam);
        return signupParam.getName();
    }


    /**
     * 주어진 ID의 사용자 정보를 업데이트합니다.
     * @param updateUserParam 사용자 정보
     * @param userNo          사용자 ID
     * @return 업데이트된 사용자 정보
     */
    @Transactional
    public String updateUser(UpdateUserParam updateUserParam, int userNo) throws NoSuchAlgorithmException {

        // 존재하는 유저인지 검사
        getUser(userNo);

        // 비밀번호를 바꾸려고 하는 경우
        if (updateUserParam.getPassword() != null) {
            // 비밀번호 유효성 체크
            validatePassword(updateUserParam.getPassword());
            // 비밀번호 암호화 - sha256
            updateUserParam.setPassword(plainToSha256(updateUserParam.getPassword()));
        }

        UserDTO user = new UserDTO(updateUserParam, userNo);
        userMapper.update(user);
        return user.getName();
    }

    /**
     * 사용자를 데이터베이스에서 삭제합니다.
     * @param userNo 삭제할 사용자 ID
     * @return 삭제 결과
     */
    @Transactional
    public int deleteUser(int userNo) {
        // 존재하지 않는 유저를 없애려고 하는지 검사
        getUser(userNo);
        return userMapper.delete(userNo);
    }

    /**
     * 중복 유저 확인
     * @param userId
     */
    public void validateDuplicateUser(String userId){
        // 중복 유저아이디 검사
        try {
            getUser(userId);
        } catch (NotFoundException e){
            return;
        }
        throw new DuplicateKeyException("중복된 아이디입니다. 다른 아이디를 입력해주세요.");
    }

    /**
     * 중복 이메일 확인
     * @param email
     */
    public void validateDuplicateEmail(String email){
        // 중복 이메일 검사
        try{
            getUserByEmail(email);
        }catch (NotFoundException e){
            return;
        }
        throw new DuplicateKeyException("중복된 이메일입니다. 다른 이메일을 입력해주세요.");
    }

    /**
     * 평문을 SHA-256 해시로 변환합니다.
     * @param plaintext 평문 비밀번호
     * @return 해시된 비밀번호
     */
    public String plainToSha256(String plaintext) throws NoSuchAlgorithmException {
        MessageDigest mdSHA256 = null;
        try {
            mdSHA256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new NoSuchAlgorithmException(e);
        }
        byte[] sha256Password = mdSHA256.digest(plaintext.getBytes(StandardCharsets.UTF_8));
        StringBuilder hexString = new StringBuilder();
        for (byte b : sha256Password) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    /**
     * 비밀번호 regex 만족하는지 확인, 불만족시 PasswordRegexException
     * @param password 검사할 비밀번호
     * @return 검사 결과
     * @throws Exception 비밀번호 길이 또는 형식 불일치로 인한 예외 발생
     */
    public boolean validatePassword(String password) {
        // 12자 미만의 비밀번호일 시 유효성 체크(정규표현식 검사)
        if(password.length() < 12 && Pattern.matches(FrkConstants.passwordRegexUnder12, password)) return true;
        // 12자 이상의 비밀번호일 시 유효성 체크(정규표현식 검사)
        if(Pattern.matches(FrkConstants.passwordRegex12orMore, password)) return true;

        throw new PasswordRegexException("비밀번호는 12자 미만의 경우 영문 대문자, 소문자, 숫자, 특수문자의 조합으로, 12자 이상인 경우 영문, 숫자, 특수문자의 조합으로 입력해주세요.");
    }

    /**
     * 개인정보 조회
     * @param userNo
     * @return
     */
    public MyInfoRes getPrivateUser(int userNo){

        // 비밀번호를 제외하고 반환
        return new MyInfoRes(getUser(userNo));
    }

}
