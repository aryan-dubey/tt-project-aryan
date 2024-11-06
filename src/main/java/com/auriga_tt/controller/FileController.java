package com.auriga_tt.controller;

import com.auriga_tt.model.ApiResponse;
import com.auriga_tt.model.User;
import com.auriga_tt.model.UserPrincipal;
import com.auriga_tt.service.FileUploadService;
import com.auriga_tt.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
@RequestMapping("/files")
public class FileController {

    @Autowired
    private FileUploadService fileUploadService;

    @Autowired
    private UserService userService;

    @PostMapping("/upload/profile-image")
    public String uploadProfileImage(@RequestParam("file") MultipartFile file,
                                                @AuthenticationPrincipal UserPrincipal currentUser) {
        try {
            User updatedUser = userService.updateProfileImage(currentUser.getId(), file);
            return "redirect:/profile?image-uploaded";
        } catch (IOException ex) {
            return "redirect:/profile?image-upload-unsuccessful";
        }
    }

    @GetMapping("/profile-image/{fileName:.+}")
    public ResponseEntity<Resource> getProfileImage(@PathVariable String fileName, HttpServletRequest request) {
        try {
            Resource resource = fileUploadService.loadFileAsResource(fileName);
            String contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
            if(contentType == null) {
                contentType = "application/octet-stream";
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (IOException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}