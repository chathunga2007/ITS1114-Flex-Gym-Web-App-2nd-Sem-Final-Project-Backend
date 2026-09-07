package lk.ijse.Flex_Gym_Management_System_Backend.controller;

import lk.ijse.Flex_Gym_Management_System_Backend.constant.CommonResponse;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.PaymentDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.service.PaymentService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import static lk.ijse.Flex_Gym_Management_System_Backend.constant.ResponseMessage.SUCCESS_MESSAGE;
import static lk.ijse.Flex_Gym_Management_System_Backend.constant.ResponseStatusCode.OPERATION_SUCCESS;

@RestController
@RequestMapping("api/payments")
public class PaymentController {
    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping(value = "/savePayment", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse savePayment(@RequestBody PaymentDTO paymentDTO) {
        PaymentDTO savedPaymentDTO = paymentService.savePayment(paymentDTO);
        return new CommonResponse(OPERATION_SUCCESS, savedPaymentDTO, SUCCESS_MESSAGE);
    }

    @PutMapping(value = "/updatePaymentStatus/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse updatePaymentStatus(@PathVariable Long id, @RequestParam(name = "paymentStatus", defaultValue = "PAID") lk.ijse.Flex_Gym_Management_System_Backend.enumeration.PaymentStatus paymentStatus) {
        PaymentDTO updatedPaymentDTO = paymentService.updatePaymentStatus(id, paymentStatus);
        return new CommonResponse(OPERATION_SUCCESS, updatedPaymentDTO, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getPayment/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getPaymentById(@PathVariable Long id) {
        PaymentDTO paymentDTO = paymentService.getPaymentById(id);
        return new CommonResponse(OPERATION_SUCCESS, paymentDTO, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getAllPayments", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getAllPayments() {
        List<PaymentDTO> paymentDTOList = paymentService.getAllPayments();
        return new CommonResponse(OPERATION_SUCCESS, paymentDTOList, SUCCESS_MESSAGE);
    }

    @GetMapping(value = "/getPaymentsByMember/{memberId}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse getPaymentsByMemberId(@PathVariable Long memberId) {
        List<PaymentDTO> paymentDTOList = paymentService.getPaymentsByMemberId(memberId);
        return new CommonResponse(OPERATION_SUCCESS, paymentDTOList, SUCCESS_MESSAGE);
    }

    @DeleteMapping(value = "/deletePayment/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public CommonResponse deletePayment(@PathVariable Long id) {
        String deleteMessage = paymentService.deletePayment(id);
        return new CommonResponse(OPERATION_SUCCESS, deleteMessage, SUCCESS_MESSAGE);
    }
}