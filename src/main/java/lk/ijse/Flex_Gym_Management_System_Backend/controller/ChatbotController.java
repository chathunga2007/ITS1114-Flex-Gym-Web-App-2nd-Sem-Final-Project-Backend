package lk.ijse.Flex_Gym_Management_System_Backend.controller;

import lk.ijse.Flex_Gym_Management_System_Backend.constant.CommonResponse;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.ChatRequestDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.ChatResponseDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.service.ChatbotService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import static lk.ijse.Flex_Gym_Management_System_Backend.constant.ResponseMessage.SUCCESS_MESSAGE;
import static lk.ijse.Flex_Gym_Management_System_Backend.constant.ResponseStatusCode.OPERATION_SUCCESS;

@RestController
@RequestMapping("api/chatbot")
@CrossOrigin
public class ChatbotController {
    private final ChatbotService chatbotService;

    public ChatbotController(ChatbotService chatbotService) {
        this.chatbotService = chatbotService;
    }

    @PostMapping(value = "/ask", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse askChatbot(@RequestBody ChatRequestDTO requestDTO) {
        String answer = chatbotService.generateChatResponse(requestDTO.getMessage());
        ChatResponseDTO chatResponseDTO = new ChatResponseDTO(answer);
        return new CommonResponse(OPERATION_SUCCESS, chatResponseDTO, SUCCESS_MESSAGE);
    }
}