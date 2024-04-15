package com.app.gizmophile.repository;

import com.app.gizmophile.model.OTP;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OTPRepository extends JpaRepository<OTP, Long> {
    Optional<OTP> findByCode(String token);

    Optional<OTP> findByEmail(String email);

    List<OTP> findAllByEmail(String email);
}
