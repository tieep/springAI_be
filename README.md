🖼️ Spring AI Image Analysis: Building Powerful Multimodal LLM APIs

This project is based on the original repository from BootcampToProd’s Spring AI Image Analysis Guide(https://github.com/BootcampToProd/spring-ai-image-analysis-cookbook)
, which I have cloned and customized.

⚙️ Environment Setup

Before running the application, make sure to provide the following environment variable:

GEMINI_API_KEY → Your Google Gemini API key
.

You can set it in your IDE or as a system environment variable.

🚀 API Endpoints
1. Analyze Image from Files

POST /api/v1/image/analysis/from-files
Upload an image file directly from your local system.

2. Analyze Image from URLs

POST /api/v1/image/analysis/from-urls
Provide an image URL for analysis.
You can use this test image: https://postimg.cc/9DT2sPTS/dfd792fa ( select link in Direct link)

3. Analyze Image from Base64

POST /api/v1/image/analysis/from-base64
Send the image encoded in Base64 format.
You can generate Base64 images here: https://www.base64-image.de/

4. Analyze Image from Classpath

POST /api/v1/image/analysis/from-classpath
Use an image stored in the project’s resources/images folder.
Example file: Spring-AI-Chat-Client-Metrics.jpg

🧠 About

This project demonstrates how to integrate Spring AI with Google Gemini to create a multimodal image analysis API.
It supports file uploads, URLs, Base64 data, and classpath resources for image input.











