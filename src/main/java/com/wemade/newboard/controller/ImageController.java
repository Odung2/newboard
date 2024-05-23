package com.wemade.newboard.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/newboard")
@RequiredArgsConstructor
public class ImageController {

    @GetMapping("/public/get-image")
    public String showImagePage() {
        return "displayImage";
    }

    @GetMapping("/get-image")
    @ResponseBody
    public ResponseEntity<Resource> getImage(@RequestParam String imageName) {
        // 이미지 파일의 실제 경로
//        String imagePath = "/newboardfiles/" + imageName; // 이미지 파일의 실제 경로로 수정해야 합니다.

        // 이미지 파일을 Resource 객체로 로드
        Resource resource = new FileSystemResource(imageName);

        // 이미지 파일을 응답으로 반환
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG) // 이미지 타입에 따라 변경
                .body(resource);
    }

}

