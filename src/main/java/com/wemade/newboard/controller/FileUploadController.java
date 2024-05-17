package com.wemade.newboard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;

@RestController
@RequestMapping("/newboard")
@RequiredArgsConstructor
public class FileUploadController {
    @PostMapping("/upload")
    public String fileUploadMultiple(@RequestParam("uploadFileMulti") ArrayList<MultipartFile> files) throws IOException {
        String savedFileName = "";
        // 1. 파일 저장 경로 설정 : 실제 서비스되는 위치(프로젝트 외부에 저장)
        String uploadPath = "/Users/wm-id002518/newboardfiles/";
        // 여러 개의 원본 파일을 저장할 리스트 생성
        ArrayList<String> originalFileNameList = new ArrayList<String>();
        for(MultipartFile file : files) {
            // 2. 원본 파일 이름 알아오기
            String originalFileName = file.getOriginalFilename();
            // 3. 파일 이름을 리스트에 추가
            originalFileNameList.add(originalFileName);
            // 4. 파일 이름 중복되지 않게 이름 변경(서버에 저장할 이름) UUID 사용
            UUID uuid = UUID.randomUUID();
            savedFileName = uuid.toString() + "_" + originalFileName;
            // 5. 파일 생성
            File file1 = new File(uploadPath + savedFileName);
            // 6. 서버로 전송
            file.transferTo(file1);
        }
        // model로 저장
//        model.addAttribute("originalFileNameList", originalFileNameList);
        return "upload/fileUploadMultipleResult";
    }
}
