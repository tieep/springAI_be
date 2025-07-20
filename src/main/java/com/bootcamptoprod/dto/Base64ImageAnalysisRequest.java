package com.bootcamptoprod.dto;

import java.util.List;

/**
 * Defines the API request body for analyzing one or more Base64 encoded images.
 */
public record Base64ImageAnalysisRequest(
        List<Base64Image> images,
        String prompt
) {
}