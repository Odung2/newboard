package com.wemade.newboard.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import static com.wemade.newboard.dto.FrkConstants.uploadPath;

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
    public ResponseEntity<Resource> getImage(@RequestParam String imageName) throws UnsupportedEncodingException {
        String decodedImageName = URLDecoder.decode(imageName, StandardCharsets.UTF_8.name());
        Resource resource = new FileSystemResource(uploadPath+decodedImageName);

        if (!resource.exists()) {
            return ResponseEntity.notFound().build();
        }

        // Determine the content type based on file extension
        String contentType = "application/octet-stream"; // Default to binary if type is unknown
        if (decodedImageName.endsWith(".png")) {
            contentType = MediaType.IMAGE_PNG_VALUE;
        } else if (decodedImageName.endsWith(".jpg") || decodedImageName.endsWith(".jpeg")) {
            contentType = MediaType.IMAGE_JPEG_VALUE;
        } else if (decodedImageName.endsWith(".gif")) {
            contentType = MediaType.IMAGE_GIF_VALUE;
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .body(resource);
    }


}

