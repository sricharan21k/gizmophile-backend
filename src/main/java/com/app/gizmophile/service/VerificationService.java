package com.app.gizmophile.service;

import com.app.gizmophile.dto.ValidateOTP;
import com.app.gizmophile.model.OTP;
import com.app.gizmophile.repository.OTPRepository;
import org.modelmapper.internal.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Service
public class VerificationService {
    private final JavaMailSender mailSender;
    private final OTPRepository otpRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("app.mail.sender")
    private String sender;

    @Value("app.mail.subject")
    private String subject;

    public VerificationService(JavaMailSender mailSender, OTPRepository otpRepository, PasswordEncoder passwordEncoder) {
        this.mailSender = mailSender;
        this.otpRepository = otpRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean sendOtpMessage(String email) {
        String messageBody = "Hi, here is the otp. \n" + generateOtp(email);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("sricharanata19@gmail.com");
        message.setSubject("subject");
        message.setTo(email);
        message.setText(messageBody);
        try {
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean sendVerificationLink(String email) {
        String messageBody = "Hi, here is the verification link. \n" + "http://localhost:8080/verify-link?token=" + generateLink(email);
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("gizmoholic.app@gmail.com");
        message.setSubject("subject");
        message.setTo(email);
        message.setText(messageBody);
        try {
            mailSender.send(message);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Transactional
    public boolean validateOtp(ValidateOTP otp) {
        OTP existingOtp = otpRepository.findByEmail(otp.getEmail()).orElseThrow();

        boolean valid = passwordEncoder.matches(otp.getCode(), existingOtp.getCode())
                && existingOtp.getExpiry().isAfter(LocalDateTime.now());

        otpRepository.deleteById(existingOtp.getId());
        return valid;
    }

    public boolean verifyLink(String token) {

        OTP existingOtp = otpRepository.findByCode(token).orElseThrow();

        boolean verified = existingOtp.getExpiry().isAfter(LocalDateTime.now());

        otpRepository.deleteById(existingOtp.getId());
        return verified;
    }

    private String generateOtp(String email) {
        String code = new DecimalFormat("000000")
                .format(new Random().nextInt(999999));
        OTP otp = OTP.builder().email(email)
                .code(passwordEncoder.encode(code))
                .expiry(LocalDateTime.now().plusMinutes(2))
                .build();

        List<OTP> previousOTPs = otpRepository.findAllByEmail(email);
        if (!previousOTPs.isEmpty()) {
            previousOTPs.forEach(x -> {
                otpRepository.deleteById(x.getId());
            });
        }
        otpRepository.save(otp);
        return code;
    }

    private String generateLink(String email) {
        String code = RandomString.make(64);
        OTP otp = OTP.builder().email(email)
                .code(code)
                .expiry(LocalDateTime.now().plusMinutes(2))
                .build();
        List<OTP> previousOTPs = otpRepository.findAllByEmail(email);
        if (!previousOTPs.isEmpty()) {
            previousOTPs.forEach(x -> {
                otpRepository.deleteById(x.getId());
            });
        }
        otpRepository.save(otp);
        return code;
    }
}
