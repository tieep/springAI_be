package com.bootcamptoprod.service;

import com.bootcamptoprod.dto.Base64Image;
import com.bootcamptoprod.dto.ImageAnalysisResponse;
import com.bootcamptoprod.exception.ImageProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.content.Media;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.net.URL;
import java.net.URLConnection;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;


@Service
public class ImageAnalysisService {

    private static final Logger log = LoggerFactory.getLogger(ImageAnalysisService.class);

    // A single, reusable system prompt that defines the AI's persona and rules.
    private static final String SYSTEM_PROMPT_TEMPLATE = getSystemPrompt();

    // A constant to programmatically check if the AI followed our rules.
    private static final String AI_ERROR_RESPONSE = "Error: I can only analyze images and answer related questions.";

    private final ChatClient chatClient;

    // The ChatClient.Builder is injected by Spring, allowing us to build the client.
    public ImageAnalysisService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    // --- IMPLEMENTATION FOR SCENARIO 1: CLASSPATH ---
    public ImageAnalysisResponse analyzeImageFromClasspath(String fileName, String prompt) {
        validatePrompt(prompt);

        if (!StringUtils.hasText(fileName)) {
            throw new ImageProcessingException("File name cannot be empty.");
        }

        // Assumes images are in `src/main/resources/images/`
        Resource imageResource = new ClassPathResource("images/" + fileName);
        if (!imageResource.exists()) {
            throw new ImageProcessingException("File not found in classpath: images/" + fileName);
        }

        // We assume JPEG for this example, but you could determine this dynamically.
        Media imageMedia = new Media(MimeTypeUtils.IMAGE_JPEG, imageResource);

        // Call the core analysis method with a list containing our single image.
        return performAnalysis(prompt, List.of(imageMedia));
    }

    // --- IMPLEMENTATION FOR SCENARIO 2: MULTIPART FILES ---
    public ImageAnalysisResponse analyzeImagesFromMultipart(List<MultipartFile> files, String prompt) {
        validatePrompt(prompt);

        if (files == null || files.isEmpty() || files.stream().allMatch(MultipartFile::isEmpty)) {
            throw new ImageProcessingException("Image files list cannot be empty.");
        }

        List<Media> mediaList = files.stream()
                .filter(file -> !file.isEmpty())
                .map(this::convertMultipartFileToMedia)
                .collect(Collectors.toList());

        return performAnalysis(prompt, mediaList);
    }

    // --- IMPLEMENTATION FOR SCENARIO 3: IMAGE URLS ---
    public ImageAnalysisResponse analyzeImagesFromUrls(List<String> urls, String prompt) {
        validatePrompt(prompt);

        if (urls == null || urls.isEmpty()) {
            throw new ImageProcessingException("Image URL list cannot be empty.");
        }

        List<Media> mediaList = urls.stream()
                .map(this::convertUrlToMedia)
                .collect(Collectors.toList());

        return performAnalysis(prompt, mediaList);
    }

    // --- IMPLEMENTATION FOR SCENARIO 4: BASE64 ---
    public ImageAnalysisResponse analyzeImagesFromBase64(List<Base64Image> base64Images, String prompt) {
        validatePrompt(prompt);

        if (base64Images == null || base64Images.isEmpty()) {
            throw new ImageProcessingException("Base64 image list cannot be empty.");
        }

        List<Media> mediaList = base64Images.stream()
                .map(this::convertBase64ToMedia)
                .collect(Collectors.toList());

        return performAnalysis(prompt, mediaList);
    }

    // ===================================================================
    //               COMMON/REUSABLE PRIVATE METHODS
    // ===================================================================

    /**
     * This is the CORE method that communicates with the AI.
     * It is called by all the public service methods.
     */
    private ImageAnalysisResponse performAnalysis(String prompt, List<Media> mediaList) {

        if (mediaList.isEmpty()) {
            throw new ImageProcessingException("No valid images were provided for analysis.");
        }

        // This is where the magic happens: combining text and media in one call.
        String response = chatClient.prompt()
                .system(SYSTEM_PROMPT_TEMPLATE)
                .user(userSpec -> userSpec
                        .text(prompt)                           // The user's text question
                        .media(mediaList.toArray(new Media[0]))) // The list of images
                .call()
                .content();

        // Check if the AI responded with our predefined error message.
        if (AI_ERROR_RESPONSE.equalsIgnoreCase(response)) {
            throw new ImageProcessingException("The provided prompt is not related to image analysis.");
        }

        return new ImageAnalysisResponse(response);
    }

    /**
     * Logic for converting an uploaded MultipartFile into a Spring AI Media object.
     */
    private Media convertMultipartFileToMedia(MultipartFile file) {
        // Determine the image's MIME type from the file upload data.
        String contentType = file.getContentType();
        MimeType mimeType = determineMimeType(contentType);

        // Create a new Media object using the detected MIME type and the file's resource.
        return new Media(mimeType, file.getResource());
    }

    /**
     * Logic for downloading an image from a URL and converting it into a Media object.
     */
    private Media convertUrlToMedia(String imageUrl) {
        try {
            log.info("Downloading image from URL: {}", imageUrl);
            URL url = new URL(imageUrl);
            URLConnection connection = url.openConnection();
            connection.setConnectTimeout(5000); // 5-second timeout
            connection.setReadTimeout(5000);    // 5-second timeout

            String mimeType = connection.getContentType(); // Get MIME type from the connection
            if (mimeType == null || !mimeType.startsWith("image/")) {
                throw new ImageProcessingException("Invalid or non-image MIME type for URL: " + imageUrl);
            }

            Resource imageResource = new UrlResource(imageUrl);

            return new Media(MimeType.valueOf(mimeType), imageResource);
        } catch (Exception e) {
            throw new ImageProcessingException("Failed to download or process image from URL: " + imageUrl, e);
        }
    }

    /**
     * Logic for decoding a Base64 string into a Media object.
     */
    private Media convertBase64ToMedia(Base64Image base64Image) {
        if (!StringUtils.hasText(base64Image.mimeType()) || !StringUtils.hasText(base64Image.data())) {
            throw new ImageProcessingException("Base64 image data and MIME type cannot be empty.");
        }
        try {
            // Decode the Base64 string back into its original binary format.
            byte[] decodedBytes = Base64.getDecoder().decode(base64Image.data());

            // Wrap the byte array in a resource and create the Media object.
            return new Media(MimeType.valueOf(base64Image.mimeType()), new ByteArrayResource(decodedBytes));
        } catch (Exception e) {
            throw new ImageProcessingException("Invalid Base64 data provided.", e);
        }
    }

    /**
     * Basic validation for the user's prompt.
     */
    private void validatePrompt(String prompt) {
        if (!StringUtils.hasText(prompt)) {
            throw new ImageProcessingException("Prompt cannot be empty.");
        }
    }

    /**
     * System prompt that defines the AI's behavior and boundaries.
     */
    private static String getSystemPrompt() {
        return """
                You are an AI assistant that specializes in image analysis.
                Your task is to analyze the provided image(s) and answer the user's question about them.
                If the user's prompt is not related to analyzing the image(s),
                respond with the exact phrase: 'Error: I can only analyze images and answer related questions.'
                """;
    }

    /**
     * Helper method to determine MimeType from a content type string.
     */
    private MimeType determineMimeType(String contentType) {
        if (contentType == null) {
            return MimeTypeUtils.IMAGE_PNG; // Default fallback
        }

        return switch (contentType.toLowerCase()) {
            case "image/jpeg", "image/jpg" -> MimeTypeUtils.IMAGE_JPEG;
            case "image/png" -> MimeTypeUtils.IMAGE_PNG;
            case "image/gif" -> MimeTypeUtils.IMAGE_GIF;
            case "image/webp" -> MimeType.valueOf("image/webp");
            case "image/bmp" -> MimeType.valueOf("image/bmp");
            case "image/tiff" -> MimeType.valueOf("image/tiff");
            default -> MimeTypeUtils.IMAGE_PNG; // Default fallback
        };
    }
}
