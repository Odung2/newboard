package com.wemade.newboard.controller;

import com.wemade.newboard.dto.TokensDTO;
import com.wemade.newboard.param.FindPasswordParam;
import com.wemade.newboard.param.LoginParam;
import com.wemade.newboard.param.SignupParam;
import com.wemade.newboard.param.UpdateUserParam;
import com.wemade.newboard.response.CommentRes;
import com.wemade.newboard.response.DetailPostRes;
import com.wemade.newboard.response.MyInfoRes;
import com.wemade.newboard.service.AuthService;
import com.wemade.newboard.service.CommentService;
import com.wemade.newboard.service.PostService;
import com.wemade.newboard.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import org.springframework.ui.Model;


import java.util.List;

@RequestMapping("/newboard")
//@RestController
@Controller
@RequiredArgsConstructor
public class UserController extends BaseController{
    private final UserService userService;
    private final AuthService authService;
    private final PostService postService;
    private final CommentService commentService;

    /**
     * html 로그인
     * @return
     */
    @GetMapping("/public/login")
    public String showLoginPage() {
        return "login";
    }

    /**
     * html 회원가입
     * @return
     */
    @GetMapping("/public/sign-up")
    public String showSignupPage() {
        return "signup";
    }

    /**
     * html 개인정보 수정
     * @return
     */
    @GetMapping("/public/show-my-page")
    public String showMyPage() {
        return "profileEditingPage";
    }

    /**
     * 회원가입
     * @param signupParam 등록할 사용자의 데이터를 담은 DTO(userId, nickname, password)
     * @return user 등록된 유저 정보
     */
    @Operation(summary = "회원가입(새로운 사용자 등록)", description = "새로운 사용자를 등록합니다.")
    @PostMapping("/public/sign-up")
    public ResponseEntity<ApiResponse<String>> SignUp(
            @RequestBody @Valid SignupParam signupParam) throws Exception {
        return ok(userService.insertUser(signupParam));
    }

    /**
     * 로그인
     * @param loginParam [userId, password]
     * @return [액세스 토큰, 리프레시 토큰]
     * @throws Exception
     */
    @Operation(summary = "로그인", description = "로그인을 처리합니다.")
    @PostMapping("/public/login")
    public ResponseEntity<ApiResponse<TokensDTO>> login(
            @RequestBody @Valid LoginParam loginParam) throws Exception {
        return ok(authService.loginAndIssueTokens(loginParam));
    }

    /**
     * 비밀번호 찾기
     */
    @Operation(summary = "비밀번호 찾기", description = "이메일로 새 비밀번호 발송")
    @GetMapping("/public/find-password")
    public String showFindPassword() {
        return "findPassword";
    }

    /**
     * 비밀번호 찾기
     * @param findPasswordParam 새 비밀번호를 발급받기 위한 유저 정보
     * @return user 등록된 유저 정보
     */
    @Operation(summary = "비밀번호 찾기", description = "이메일로 새 비밀번호 발송")
    @PostMapping("/find-password")
    public ResponseEntity<ApiResponse<String>> findPassword(
            @RequestBody @Valid FindPasswordParam findPasswordParam) throws Exception {
        return ok(userService.findPassword(findPasswordParam));
    }

    /**
     * 개인정보 조회, 액세스 토큰을 통해 인증된 사용자의 개인정보 조회 요청 처리
     * @param userNo 액세스 토큰
     * @return user
     */
    @Operation(summary = "개인정보 조회", description = "사용자의 개인정보를 액세스 토큰을 통해 조회합니다.")
    @GetMapping("/my-page")
    public ResponseEntity<ApiResponse<MyInfoRes>> getMyPage(
            @RequestAttribute("reqId") @Min(value = 1, message = "1 이상부터 입력가능합니다.") int userNo) {
        return ok(userService.getPrivateUser(userNo));
    }

    /**
     * 개인정보 수정, 액세스 토큰을 통해 인증된 사용자의 개인정보 수정 요청 처리
     * @param userNo
     * @param updateUserParam [nickname, password], 둘 다 nullable
     * @return
     * @throws Exception Password 양식 Exception, 암호화 알고리즘 Exception
     */
    @Operation(summary = "개인정보 수정", description = "개인정보(nickname 또는 password)를 수정할 수 있습니다.")
    @PutMapping("/my-page")
    public ResponseEntity<ApiResponse<String>> updateMyPage(
            @RequestAttribute("reqId") int userNo,
            @RequestBody @Valid UpdateUserParam updateUserParam) throws Exception {
        return ok(userService.updateUser(updateUserParam, userNo));
    }

    /**
     * 회원 탈퇴 (DB에 저장된 개인정보를 삭제합니다.)
     * @param userNo 본인의 userNo
     * @return
     */
    @Operation(summary = "회원탈퇴(개인정보 삭제)", description = "권한이 있는 유저의 개인정보를 삭제할 수 있습니다.")
    @DeleteMapping("/my-page")
    public ResponseEntity<ApiResponse<Integer>> deleteUser(
            @RequestAttribute("reqId") int userNo){
        return ok(userService.deleteUser(userNo));
    }

    /**
     * 내가 작성한 게시글 조회
     * @param userNo 액세스 토큰
     * @return user
     */
    @Operation(summary = "내가 작성한 게시글 조회", description = "액세스 토큰을 통해 유저가 작성한 게시글을 조회합니다.")
    @GetMapping("/my-page/posts")
    public ResponseEntity<ApiResponse<List<DetailPostRes>>> getMyPosts(
            @RequestAttribute("reqId") @Min(value = 1, message = "1 이상부터 입력가능합니다.") int userNo) {
        return ok(postService.getUserPosts(userNo));
    }

    /**
     * 내가 작성한 댓글 조회
     * @param userNo 액세스 토큰
     * @return user
     */
    @Operation(summary = "내가 작성한 댓글 조회", description = "액세스 토큰을 통해 유저가 작성한 댓글을 조회합니다.")
    @GetMapping("/my-page/comments")
    public ResponseEntity<ApiResponse<List<CommentRes>>> getMyComments(
            @RequestAttribute("reqId") @Min(value = 1, message = "1 이상부터 입력가능합니다.") int userNo) {
        return ok(commentService.getUserComments(userNo));
    }

    /**
     * (개인) 임시 저장글 조회
     * @param userNo 액세스 토큰
     * @return user
     */
    @Operation(summary = "임시 저장글 조회", description = "액세스 토큰을 통해 유저가 임시 저장한 게시글을 조회합니다.")
    @GetMapping("/my-page/temp-posts")
    public ResponseEntity<ApiResponse<List<DetailPostRes>>> getMyTempPosts(
            @RequestAttribute("reqId") @Min(value = 1, message = "1 이상부터 입력가능합니다.") int userNo) {
        return ok(postService.getUserTempPosts(userNo));
    }

    /**
     * 새 액세스 토큰 발급(액세스 토큰 만료 && 리프레시 토큰 유효 시)
     * @param accessJWT 만료된 액세스 토큰
     * @param refreshJWT 유효한 리프레시 토큰
     * @return 새 액세스 토큰 발급
     * @throws Exception 캐시 Not Found / 리프레시 만료 / 액세스 유효
     */
    @Operation(summary = "refresh token API", description = "access token 만료 시 refresh token을 받습니다.")
    @PostMapping("/public/refresh-token")
    public ResponseEntity<ApiResponse<String>> refreshTokenAuth(
            @RequestHeader("Authorization") String accessJWT,
            @RequestHeader(value="Refresh-token", defaultValue = "") String refreshJWT
    ) throws Exception {
        return ok("새로 발급된 액세스 토큰으로 접속해주세요.", authService.issueNewAccessToken(accessJWT, refreshJWT));
    }
}
