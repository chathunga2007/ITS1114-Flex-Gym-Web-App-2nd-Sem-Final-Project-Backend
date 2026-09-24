package lk.ijse.Flex_Gym_Management_System_Backend.service.impl;

import java.util.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.PackageRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.ProductRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.TrainerRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.service.ChatbotService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ChatbotServiceImpl implements ChatbotService {
    @Value("${groq.api.key:${GROK_API_KEY:}}")
    private String rawApiKey;

    @Value("${groq.api.url:${GROK_API_URL:https://api.groq.com/openai/v1/chat/completions}}")
    private String rawApiUrl;

    @Value("${groq.model:${GROK_MODEL:llama-3.1-8b-instant}}")
    private String rawModel;

    private final PackageRepository packageRepository;
    private final TrainerRepository trainerRepository;
    private final ProductRepository productRepository;
    private final RestTemplate restTemplate;

    private static volatile String activeWorkingModel = null;

    private static final List<String> FALLBACK_MODELS = List.of(
            "llama-3.1-8b-instant",
            "llama-3.2-3b-preview",
            "llama-3.2-1b-preview",
            "mixtral-8x7b-32768",
            "gemma2-9b-it",
            "llama-3.3-70b-versatile"
    );

    public ChatbotServiceImpl(PackageRepository packageRepository,
                              TrainerRepository trainerRepository,
                              ProductRepository productRepository) {
        this.packageRepository = packageRepository;
        this.trainerRepository = trainerRepository;
        this.productRepository = productRepository;

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(8000);
        requestFactory.setReadTimeout(15000);
        this.restTemplate = new RestTemplate(requestFactory);
    }

    private String buildGymContext() {
        StringBuilder sb = new StringBuilder();
        sb.append("Flex Gym Live System Data:\n");

        try {
            sb.append("Packages: ");
            packageRepository.findAll().forEach(pkg ->
                    sb.append(String.format("[%s, Price: LKR %s] ",
                            pkg.getPackageName() != null ? pkg.getPackageName() : "N/A",
                            pkg.getPackagePrice() != null ? pkg.getPackagePrice() : "0"))
            );

            sb.append("\nProducts: ");
            productRepository.findAll().forEach(prod ->
                    sb.append(String.format("[%s, Price: LKR %s, Stock: %s] ",
                            prod.getProductName() != null ? prod.getProductName() : "N/A",
                            prod.getProductPrice() != null ? prod.getProductPrice() : "0",
                            prod.getStockQuantity() != null ? prod.getStockQuantity() : "0"))
            );

            sb.append("\nTrainers: ");
            trainerRepository.findAll().forEach(t ->
                    sb.append(String.format("[%s, Specialization: %s] ",
                            t.getTrainerName() != null ? t.getTrainerName() : "N/A",
                            t.getSpecialization() != null ? t.getSpecialization() : "General"))
            );
        } catch (Exception ex) {
            log.error("Error reading DB for gym context: {}", ex.getMessage());
        }

        return sb.toString();
    }

    private String resolveBestModel(String apiKey, String preferredModel, String apiUrl) {
        if (activeWorkingModel != null) {
            return activeWorkingModel;
        }

        try {
            String modelsEndpoint = apiUrl.contains("/chat/completions")
                    ? apiUrl.replace("/chat/completions", "/models")
                    : "https://api.groq.com/openai/v1/models";

            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(apiKey);
            HttpEntity<Void> entity = new HttpEntity<>(headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    modelsEndpoint,
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Object dataObj = response.getBody().get("data");
                if (dataObj instanceof List<?> dataList) {
                    List<String> availableModelIds = new ArrayList<>();
                    for (Object item : dataList) {
                        if (item instanceof Map<?, ?> itemMap) {
                            Object id = itemMap.get("id");
                            if (id instanceof String idStr) {
                                if (!idStr.contains("whisper") && !idStr.contains("guard") && !idStr.contains("embed")) {
                                    availableModelIds.add(idStr);
                                }
                            }
                        }
                    }

                    log.info("Discovered active Groq models: {}", availableModelIds);

                    if (preferredModel != null && availableModelIds.contains(preferredModel)) {
                        activeWorkingModel = preferredModel;
                        return activeWorkingModel;
                    }

                    for (String candidate : FALLBACK_MODELS) {
                        if (availableModelIds.contains(candidate)) {
                            activeWorkingModel = candidate;
                            log.info("Automatically selected verified Groq model: {}", candidate);
                            return activeWorkingModel;
                        }
                    }

                    if (!availableModelIds.isEmpty()) {
                        activeWorkingModel = availableModelIds.get(0);
                        log.info("Automatically selected available Groq model: {}", activeWorkingModel);
                        return activeWorkingModel;
                    }
                }
            }
        } catch (Exception e) {
            log.warn("Could not query /models endpoint ({}). Falling back to preferred or default model.", e.getMessage());
        }

        return (preferredModel != null && !preferredModel.isBlank()) ? preferredModel : "llama-3.1-8b-instant";
    }

    private String sendChatRequest(String apiUrl, String apiKey, String model, List<Map<String, String>> messages) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put("model", model);
        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.3);
        requestBody.put("stream", false);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                apiUrl,
                HttpMethod.POST,
                entity,
                Map.class
        );

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            List choices = (List) response.getBody().get("choices");
            if (choices != null && !choices.isEmpty()) {
                Map firstChoice = (Map) choices.get(0);
                Map message = (Map) firstChoice.get("message");
                if (message != null && message.get("content") != null) {
                    activeWorkingModel = model;
                    return (String) message.get("content");
                }
            }
        }
        return null;
    }

    @Override
    public String generateChatResponse(String userMessage) {
        if (rawApiKey == null || rawApiKey.trim().isEmpty()) {
            return "AI Assistant is not configured yet. Please get a free API key from https://console.groq.com and paste it in the backend .env file as GROQ_API_KEY.";
        }

        String apiKey = rawApiKey.trim();
        String apiUrl = (rawApiUrl != null && !rawApiUrl.isBlank()) ? rawApiUrl.trim() : "https://api.groq.com/openai/v1/chat/completions";
        String requestedModel = (rawModel != null && !rawModel.isBlank()) ? rawModel.trim() : "llama-3.1-8b-instant";

        if (apiKey.startsWith("gsk_")) {
            apiUrl = "https://api.groq.com/openai/v1/chat/completions";
        }

        String dbContext = buildGymContext();

        String systemInstruction =
                "You are FlexBot, the official high-performance AI fitness assistant for Flex Gym.\n\n" +
                        "GUIDELINES:\n" +
                        "1. Answer ONLY questions related to Flex Gym, fitness, workouts, nutrition, memberships, packages, trainers, and store products.\n" +
                        "2. Utilize the real-time database context below to provide accurate answers:\n" + dbContext + "\n" +
                        "3. If asked about unrelated topics (such as general politics, unrelated coding, celebrity gossip, etc.), politely decline by saying: 'I apologize, but I am solely dedicated to answering questions about Flex Gym and your fitness journey.'\n" +
                        "4. Be energetic, motivating, concise, and professional in English.";

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemInstruction));
        messages.add(Map.of("role", "user", "content", userMessage));

        String modelToUse = resolveBestModel(apiKey, requestedModel, apiUrl);

        try {
            String answer = sendChatRequest(apiUrl, apiKey, modelToUse, messages);
            if (answer != null) {
                return answer;
            }
            return "I apologize, but I am unable to generate a response at the moment.";

        } catch (HttpClientErrorException e) {
            log.error("AI API Client Error ({}): {}", e.getStatusCode(), e.getResponseBodyAsString());
            if (e.getStatusCode() == HttpStatus.UNAUTHORIZED) {
                return "API Error: Invalid or expired API key. Please check your GROQ_API_KEY in .env.";
            }

            // If model is not found, automatically attempt fallback models
            if (e.getStatusCode() == HttpStatus.NOT_FOUND && e.getResponseBodyAsString().contains("model")) {
                log.warn("Model '{}' returned 404 Not Found. Trying fallback candidate models...", modelToUse);
                activeWorkingModel = null;
                for (String fallback : FALLBACK_MODELS) {
                    if (fallback.equals(modelToUse)) continue;
                    try {
                        log.info("Attempting fallback model: {}", fallback);
                        String fallbackAnswer = sendChatRequest(apiUrl, apiKey, fallback, messages);
                        if (fallbackAnswer != null) {
                            activeWorkingModel = fallback;
                            log.info("Fallback succeeded with model: {}", fallback);
                            return fallbackAnswer;
                        }
                    } catch (Exception ex) {
                        log.debug("Fallback model {} failed: {}", fallback, ex.getMessage());
                    }
                }
            }

            return "AI API returned an error (" + e.getStatusCode() + "). Please verify your API key or model settings.";
        } catch (HttpServerErrorException e) {
            log.error("AI API Server Error: {}", e.getMessage());
            return "AI Service is temporarily experiencing heavy load. Please try again in a few moments.";
        } catch (Exception e) {
            log.error("Chatbot unexpected error: {}", e.getMessage());
            return "Unable to connect to AI assistant. Please check your internet connection or server logs.";
        }
    }
}