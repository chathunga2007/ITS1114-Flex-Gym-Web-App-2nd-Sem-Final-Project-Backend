package lk.ijse.Flex_Gym_Management_System_Backend.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import lk.ijse.Flex_Gym_Management_System_Backend.dto.PaymentDTO;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Member;
import lk.ijse.Flex_Gym_Management_System_Backend.entity.Payment;
import lk.ijse.Flex_Gym_Management_System_Backend.enumeration.PaymentStatus;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.MemberRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.repository.PaymentRepository;
import lk.ijse.Flex_Gym_Management_System_Backend.service.PaymentService;
import lombok.extern.slf4j.Slf4j;

@Service
@Transactional
@Slf4j
public class PaymentServiceImpl implements PaymentService {
    private final PaymentRepository paymentRepository;
    private final MemberRepository memberRepository;

    public PaymentServiceImpl(PaymentRepository paymentRepository, MemberRepository memberRepository) {
        this.paymentRepository = paymentRepository;
        this.memberRepository = memberRepository;
    }

    @Override
    public PaymentDTO savePayment(PaymentDTO paymentDTO) {
        log.info("Execute savePayment()");
        if (paymentDTO == null || paymentDTO.getAmount() == null || paymentDTO.getMemberId() == null) {
            return null;
        }

        Optional<Member> optionalMember = memberRepository.findById(paymentDTO.getMemberId());
        if (optionalMember.isEmpty()) {
            return null;
        }

        Member member = optionalMember.get();

        Payment payment = new Payment();
        payment.setAmount(paymentDTO.getAmount());
        payment.setPaymentType(paymentDTO.getPaymentType());
        payment.setPaymentStatus(paymentDTO.getPaymentStatus() != null ? paymentDTO.getPaymentStatus() : PaymentStatus.PAID);
        payment.setMember(member);

        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment saved successfully!");

        PaymentDTO responseDTO = new PaymentDTO();
        responseDTO.setPaymentId(savedPayment.getPaymentId());
        responseDTO.setAmount(savedPayment.getAmount());
        responseDTO.setPaymentType(savedPayment.getPaymentType());
        responseDTO.setPaymentStatus(savedPayment.getPaymentStatus());
        if (savedPayment.getMember() != null) {
            responseDTO.setMemberId(savedPayment.getMember().getMemberId());
            responseDTO.setMemberName(savedPayment.getMember().getMemberFullName());
        }

        return responseDTO;
    }

    @Override
    public PaymentDTO getPaymentById(Long id) {
        log.info("Execute getPaymentById()");
        if (id == null) {
            return null;
        }

        Optional<Payment> optionalPayment = paymentRepository.findById(id);
        if (optionalPayment.isEmpty()) {
            return null;
        }

        Payment payment = optionalPayment.get();

        if (payment.getPaymentStatus() == PaymentStatus.DELETED) {
            return null;
        }

        PaymentDTO responseDTO = new PaymentDTO();
        responseDTO.setPaymentId(payment.getPaymentId());
        responseDTO.setAmount(payment.getAmount());
        responseDTO.setPaymentType(payment.getPaymentType());
        responseDTO.setPaymentStatus(payment.getPaymentStatus());
        if (payment.getMember() != null) {
            responseDTO.setMemberId(payment.getMember().getMemberId());
            responseDTO.setMemberName(payment.getMember().getMemberFullName());
        }

        return responseDTO;
    }

    @Override
    public List<PaymentDTO> getAllPayments() {
        log.info("Execute getAllPayments()");
        List<Payment> paymentList = paymentRepository.findAll();
        List<PaymentDTO> dtoList = new ArrayList<>();

        for (Payment payment : paymentList) {
            if (payment.getPaymentStatus() != PaymentStatus.DELETED) {
                PaymentDTO responseDTO = new PaymentDTO();
                responseDTO.setPaymentId(payment.getPaymentId());
                responseDTO.setAmount(payment.getAmount());
                responseDTO.setPaymentType(payment.getPaymentType());
                responseDTO.setPaymentStatus(payment.getPaymentStatus());
                if (payment.getMember() != null) {
                    responseDTO.setMemberId(payment.getMember().getMemberId());
                    responseDTO.setMemberName(payment.getMember().getMemberFullName());
                }
                dtoList.add(responseDTO);
            }
        }

        return dtoList;
    }

    @Override
    public List<PaymentDTO> getPaymentsByMemberId(Long memberId) {
        log.info("Execute getPaymentsByMemberId()");
        if (memberId == null) {
            return new ArrayList<>();
        }

        List<Payment> paymentList = paymentRepository.findAllByMember_MemberId(memberId);
        List<PaymentDTO> dtoList = new ArrayList<>();

        for (Payment payment : paymentList) {
            if (payment.getPaymentStatus() != PaymentStatus.DELETED) {
                PaymentDTO responseDTO = new PaymentDTO();
                responseDTO.setPaymentId(payment.getPaymentId());
                responseDTO.setAmount(payment.getAmount());
                responseDTO.setPaymentType(payment.getPaymentType());
                responseDTO.setPaymentStatus(payment.getPaymentStatus());
                if (payment.getMember() != null) {
                    responseDTO.setMemberId(payment.getMember().getMemberId());
                    responseDTO.setMemberName(payment.getMember().getMemberFullName());
                }
                dtoList.add(responseDTO);
            }
        }

        return dtoList;
    }

    @Override
    public String deletePayment(Long id) {
        log.info("Execute Soft Delete Payment()");
        if (id == null) {
            return "Payment ID cannot be null!";
        }

        Optional<Payment> optionalPayment = paymentRepository.findById(id);
        if (optionalPayment.isEmpty()) {
            return "Payment not found!";
        }

        Payment payment = optionalPayment.get();
        if (payment.getPaymentStatus() == PaymentStatus.DELETED) {
            return "Payment is already deleted!";
        }

        payment.setPaymentStatus(PaymentStatus.DELETED);
        paymentRepository.save(payment);

        log.info("Payment marked as DELETED successfully!");
        return "Payment deleted successfully!";
    }
}