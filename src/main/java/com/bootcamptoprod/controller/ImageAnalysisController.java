package com.bootcamptoprod.controller;

import com.bootcamptoprod.dto.Base64ImageAnalysisRequest;
import com.bootcamptoprod.dto.ImageAnalysisResponse;
import com.bootcamptoprod.dto.UrlAnalysisRequest;
import com.bootcamptoprod.exception.ImageProcessingException;
import com.bootcamptoprod.service.ImageAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/image/analysis")
public class ImageAnalysisController {

    private final ImageAnalysisService imageAnalysisService;

    public ImageAnalysisController(ImageAnalysisService imageAnalysisService) {
        this.imageAnalysisService = imageAnalysisService;
    }

    /**
     * SCENARIO 1: Analyze a single file from the classpath (e.g., src/main/resources/images).
     */
    @PostMapping("/from-classpath")
    public ResponseEntity<ImageAnalysisResponse> analyzeFromClasspath(@RequestBody UrlAnalysisRequest request) {
        ImageAnalysisResponse imageAnalysisResponse = imageAnalysisService.analyzeImageFromClasspath(request.fileName(), request.prompt());
        return ResponseEntity.ok(imageAnalysisResponse);
    }

    /**
     * SCENARIO 2: Analyze multiple image files uploaded by the user.
     * This endpoint handles multipart/form-data requests.
     */
    @PostMapping("/from-files")
    public ResponseEntity<ImageAnalysisResponse> analyzeFromMultipart(@RequestParam("images") List<MultipartFile> images, @RequestParam("prompt") String prompt) {
        ImageAnalysisResponse imageAnalysisResponse = imageAnalysisService.analyzeImagesFromMultipart(images, prompt);
        return ResponseEntity.ok(imageAnalysisResponse);
    }

    /**
     * SCENARIO 3: Analyze multiple images from a list of URLs provided in a JSON body.
     */
    @PostMapping("/from-urls")
    public ResponseEntity<ImageAnalysisResponse> analyzeFromUrls(@RequestBody UrlAnalysisRequest request) {
        ImageAnalysisResponse imageAnalysisResponse = imageAnalysisService.analyzeImagesFromUrls(request.imageUrls(), request.prompt());
        return ResponseEntity.ok(imageAnalysisResponse);
    }

    /**
     * SCENARIO 4: Analyze multiple images from Base64-encoded strings in a JSON body.
     */
    @PostMapping("/from-base64")
    public ResponseEntity<ImageAnalysisResponse> analyzeFromBase64(@RequestBody Base64ImageAnalysisRequest request) {
        ImageAnalysisResponse imageAnalysisResponse = imageAnalysisService.analyzeImagesFromBase64(request.images(), request.prompt());
        return ResponseEntity.ok(imageAnalysisResponse);
    }

    /**
     * Centralized exception handler for this controller.
     * Catches our custom exception from the service layer and returns a clean
     * HTTP 400 Bad Request with the error message.
     */
    @ExceptionHandler(ImageProcessingException.class)
    public ResponseEntity<ImageAnalysisResponse> handleImageProcessingException(ImageProcessingException ex) {
        return ResponseEntity.badRequest().body(new ImageAnalysisResponse(ex.getMessage()));
    }
}
