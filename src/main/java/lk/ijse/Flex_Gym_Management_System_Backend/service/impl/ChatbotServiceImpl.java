package lk.ijse.Flex_Gym_Management_System_Backend.service.impl;

import lk.ijse.Flex_Gym_Management_System_Backend.repository.PackageRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.ProductRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.TrainerRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.service.ChatbotService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.*;

@Service
public class ChatbotServiceImpl implements ChatbotService {
    @Value("${gemini.api.key}")
    private String apiKey;

    @Value("${gemini.api.url}")
    private String apiUrl;

    private final PackageRepository packageRepository;
    private final TrainerRepository trainerRepository;
    private final ProductRepository productRepository;

    public ChatbotServiceImpl(PackageRepository packageRepository,
                              TrainerRepository trainerRepository,
                              ProductRepository productRepository) {
        this.packageRepository = packageRepository;
        this.trainerRepository = trainerRepository;
        this.productRepository = productRepository;
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
            System.err.println("Error reading DB for context: " + ex.getMessage());
        }

        return sb.toString();
    }

    @Override
    public String generateChatResponse(String userMessage) {
//        System.out.println("Using API Key: " + apiKey);
//        System.out.println("Using URL: " + apiUrl);
        String dbContext = buildGymContext();

        String systemInstruction =
                "You are the official AI Assistant of Flex Gym Management System.\n\n" +
                        "MANDATORY INSTRUCTIONS:\n" +
                        "1. Respond STRICTLY and ONLY in ENGLISH, regardless of the language used by the user.\n" +
                        "2. Answer ONLY questions related to Flex Gym, including memberships, packages, products, trainers, gym services, workouts, and fitness.\n" +
                        "3. Use the following real-time database context to answer accurately: \n" + dbContext + "\n" +
                        "4. If the user asks ANY question outside of Flex Gym or fitness (e.g., general programming, politics, movies, random trivia), you MUST STRICTLY decline by saying:\n" +
                        "'I apologize, but I cannot assist with that topic. I am only trained to provide information and assistance related to Flex Gym Management System and fitness services.'\n" +
                        "5. Maintain a polite, helpful, and professional tone at all times.";

        RestTemplate restTemplate = new RestTemplate();
        String fullUrl = apiUrl + "?key=" + apiKey;

        Map<String, Object> textPart = Collections.singletonMap("text", systemInstruction + "\n\nUser Question: " + userMessage);
        Map<String, Object> contentPart = Collections.singletonMap("parts", Collections.singletonList(textPart));
        Map<String, Object> requestBody = Collections.singletonMap("contents", Collections.singletonList(contentPart));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(fullUrl, HttpMethod.POST, entity, Map.class);
            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                List candidates = (List) response.getBody().get("candidates");
                if (candidates != null && !candidates.isEmpty()) {
                    Map firstCandidate = (Map) candidates.get(0);
                    Map content = (Map) firstCandidate.get("content");
                    List parts = (List) content.get("parts");
                    Map firstPart = (Map) parts.get(0);
                    return (String) firstPart.get("text");
                }
            }
        }   catch (org.springframework.web.client.HttpClientErrorException e) {
            System.err.println("Gemini API Error Response: " + e.getResponseBodyAsString());
            e.printStackTrace();
            return "Gemini API Error: " + e.getStatusCode() + " - " + e.getResponseBodyAsString();
        } catch (Exception e) {
            e.printStackTrace();
            return "Internal Error: " + e.getMessage();
        }
        return "I apologize, but I am unable to generate a response to this question at the moment.";
    }
}