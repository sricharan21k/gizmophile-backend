package com.app.gizmophile.controller;

import com.app.gizmophile.dto.AuthResponse;
import com.app.gizmophile.dto.LoginUserData;
import com.app.gizmophile.service.AuthenticationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginUserData request) {
        var res = authenticationService.authenticate(request);
        return ResponseEntity.ok(res);
    }
}
