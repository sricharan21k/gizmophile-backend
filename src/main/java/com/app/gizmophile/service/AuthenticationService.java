package com.app.gizmophile.service;


import com.app.gizmophile.dto.AuthResponse;
import com.app.gizmophile.dto.LoginUserData;
import com.app.gizmophile.model.User;
import com.app.gizmophile.repository.UserRepository;
import com.app.gizmophile.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationService(UserRepository userRepository,
                                 JwtService jwtService,
                                 AuthenticationManager authenticationManager)
    {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponse authenticate(LoginUserData request){
        User user;

        if(request.getUsername().contains("@")){
            user = userRepository.findByEmail(request.getUsername()).orElseThrow();

        }else{
            user = userRepository.findByUsername(request.getUsername()).orElseThrow();
        }
        System.out.println("user "+ user.getUsername());


        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getUsername(), request.getPassword()
                )
        );

        return AuthResponse.builder()
                .authToken(jwtService.generateToken(user))
                .username(user.getUsername()).build();
    }

}
