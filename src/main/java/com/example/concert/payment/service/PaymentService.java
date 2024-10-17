package com.example.concert.payment.service;

import com.example.concert.concertschedule.domain.ConcertSchedule;
import com.example.concert.payment.domain.Payment;
import com.example.concert.payment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository){
        this.paymentRepository = paymentRepository;
    }

    public void createPayment(ConcertSchedule concertSchedule, UUID uuid, long amount){
        Payment payment = Payment.of(concertSchedule, uuid, amount);
        paymentRepository.save(payment);
    }
}
