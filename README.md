# 🖼️ Spring AI Image Analysis: Building Powerful Multimodal LLM APIs

This repository demonstrates how to build a comprehensive multimodal AI image analysis API using Spring AI and Google Gemini 2.0. The application can process images from multiple sources (file uploads, URLs, Base64, classpath) and provide intelligent insights by combining text prompts with visual analysis. This project showcases the power of multimodal AI that processes both text and images simultaneously, just like human cognition.

📖 **Dive Deeper**: For a complete walkthrough, detailed explanations of multimodal AI concepts, and step-by-step instructions for building this comprehensive image analysis service, read our in-depth tutorial.<br>
👉 [Spring AI Image Analysis: Building Powerful Multimodal LLM APIs](https://bootcamptoprod.com/spring-ai-image-analysis-guide/)

🎥 **Visual Learning**: Prefer video tutorials? Watch our step-by-step implementation guide on YouTube.<br>
👉 YouTube Tutorial - Coming Soon

---

## 📦 Environment Variables

Make sure to provide this Java environment variable when running the application:

- `GEMINI_API_KEY`: Your Google Gemini API key.

---

## 💡 About This Project

This project implements a **comprehensive Image Analysis API** to demonstrate the power of multimodal AI with Spring AI. It showcases how to:

*   Process images from 4 different input sources: classpath resources, file uploads, web URLs, and Base64 strings.
*   Configure Spring AI to work with Google's Gemini 2.0 Flash through OpenAI-compatible endpoints.
*   Build REST API with proper error handling and validation.
*   Combine text prompts with visual content for intelligent, context-aware responses.

The application exposes four REST endpoints that accept images in different formats along with text prompts, sends them to Gemini for multimodal analysis, and returns AI-generated insights about the visual content.

---