package com.app.gizmophile.controller;

import com.app.gizmophile.dto.ValidateOTP;
import com.app.gizmophile.service.VerificationService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

@RestController
public class VerificationController {

    private final VerificationService verificationService;

    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @PostMapping("/send-otp")
    public boolean sendOtp(@RequestBody String email) {
        return verificationService.sendOtpMessage(email);
    }

    @PostMapping("/validate-otp")
    public boolean validateOtp(@RequestBody ValidateOTP otp) {
        return verificationService.validateOtp(otp);
    }

    @PostMapping("send-verification-link")
    public boolean sendEmail(@RequestBody String email){
        return verificationService.sendVerificationLink(email);
    }

    @GetMapping("verify-link")
    public String validateLink(@RequestParam String token, HttpServletResponse response){
        boolean verified = verificationService.verifyLink(token);

        Cookie cookie = new Cookie("verificationResult", verified?"success":"failure");
        cookie.setPath("/");
        cookie.setMaxAge(-1);
        response.addCookie(cookie);

        return verified?"Successfully verified":"Link expired!";
    }


}
