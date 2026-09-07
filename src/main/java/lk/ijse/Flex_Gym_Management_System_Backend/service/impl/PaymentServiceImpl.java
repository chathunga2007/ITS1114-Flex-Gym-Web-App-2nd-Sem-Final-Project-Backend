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
import lk.ijse.Flex_Gym_Management_System_Backend.exception.CustomException;
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
        if (paymentDTO == null) {
            throw new CustomException(400, "Payment data cannot be null!");
        }
        if (paymentDTO.getAmount() == null) {
            throw new CustomException(400, "Payment amount cannot be null!");
        }
        if (paymentDTO.getMemberId() == null) {
            throw new CustomException(400, "Member ID cannot be null!");
        }

        Optional<Member> optionalMember = memberRepository.findById(paymentDTO.getMemberId());
        if (optionalMember.isEmpty()) {
            throw new CustomException(404, "Member not found with ID: " + paymentDTO.getMemberId());
        }

        Member member = optionalMember.get();

        Payment payment = new Payment();
        if (paymentDTO.getPaymentId() != null) {
            Optional<Payment> existingPayment = paymentRepository.findById(paymentDTO.getPaymentId());
            if (existingPayment.isPresent()) {
                payment = existingPayment.get();
            }
        }
        payment.setAmount(paymentDTO.getAmount());
        if (paymentDTO.getPaymentType() != null) {
            payment.setPaymentType(paymentDTO.getPaymentType());
        }
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
    public PaymentDTO updatePaymentStatus(Long id, PaymentStatus paymentStatus) {
        log.info("Execute updatePaymentStatus() for paymentId: {}, status: {}", id, paymentStatus);
        if (id == null) {
            throw new CustomException(400, "Payment ID cannot be null!");
        }
        Optional<Payment> optionalPayment = paymentRepository.findById(id);
        if (optionalPayment.isEmpty()) {
            throw new CustomException(404, "Payment not found with ID: " + id);
        }
        Payment payment = optionalPayment.get();
        payment.setPaymentStatus(paymentStatus != null ? paymentStatus : PaymentStatus.PAID);
        Payment savedPayment = paymentRepository.save(payment);
        log.info("Payment #{} status updated to {}", id, savedPayment.getPaymentStatus());

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
            throw new CustomException(400, "Payment ID cannot be null!");
        }

        Optional<Payment> optionalPayment = paymentRepository.findById(id);
        if (optionalPayment.isEmpty()) {
            throw new CustomException(404, "Payment not found with ID: " + id);
        }

        Payment payment = optionalPayment.get();

        if (payment.getPaymentStatus() == PaymentStatus.DELETED) {
            throw new CustomException(404, "Payment not found with ID: " + id);
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
        List<Payment> paymentList = paymentRepository.findAllByPaymentStatus(PaymentStatus.PAID);
        List<Payment> pendingList = paymentRepository.findAllByPaymentStatus(PaymentStatus.PENDING);

        List<Payment> allActivePayments = new ArrayList<>();
        allActivePayments.addAll(paymentList);
        allActivePayments.addAll(pendingList);

        List<PaymentDTO> dtoList = new ArrayList<>();
        for (Payment payment : allActivePayments) {
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

        return dtoList;
    }

    @Override
    public List<PaymentDTO> getPaymentsByMemberId(Long memberId) {
        log.info("Execute getPaymentsByMemberId()");
        if (memberId == null) {
            throw new CustomException(400, "Member ID cannot be null!");
        }

        List<Payment> paidList = paymentRepository.findAllByMember_MemberIdAndPaymentStatus(memberId, PaymentStatus.PAID);
        List<Payment> pendingList = paymentRepository.findAllByMember_MemberIdAndPaymentStatus(memberId, PaymentStatus.PENDING);

        List<Payment> memberActivePayments = new ArrayList<>();
        memberActivePayments.addAll(paidList);
        memberActivePayments.addAll(pendingList);

        List<PaymentDTO> dtoList = new ArrayList<>();
        for (Payment payment : memberActivePayments) {
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

        return dtoList;
    }

    @Override
    public String deletePayment(Long id) {
        log.info("Execute deletePayment()");
        if (id == null) {
            throw new CustomException(400, "Payment ID cannot be null!");
        }

        Optional<Payment> optionalPayment = paymentRepository.findById(id);
        if (optionalPayment.isEmpty()) {
            throw new CustomException(404, "Payment not found with ID: " + id);
        }

        Payment payment = optionalPayment.get();
        if (payment.getPaymentStatus() == PaymentStatus.DELETED) {
            throw new CustomException(400, "Payment is already deleted!");
        }

        payment.setPaymentStatus(PaymentStatus.DELETED);
        paymentRepository.save(payment);

        log.info("Payment marked as DELETED successfully!");
        return "Payment deleted successfully!";
    }
}