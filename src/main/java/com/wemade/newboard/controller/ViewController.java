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
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequestMapping("")
//@RestController
@Controller
@RequiredArgsConstructor
public class ViewController {

    /**
     * html 로그인
     * @return
     */
    @GetMapping("")
    public String indexView() {
        return "index";
    }

    /**
     * html 로그인
     * @return
     */
    @GetMapping("/view/login")
    public String signupLogin() {
        return "/auth/login";
    }

    /**
     * html 회원가입
     * @return
     */
    @GetMapping("/view/signup")
    public String signupView() {
        return "/auth/signup";
    }

    /**
     * html 개인정보 수정
     * @return
     */
    @GetMapping("/view/mypage")
    public String mypageView() {
        return "/user/mypage";
    }

}
