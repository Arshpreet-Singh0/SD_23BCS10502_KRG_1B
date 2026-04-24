package com.ecommerce.user_service.controllers;

import com.ecommerce.user_service.dto.LoginDTO;
import com.ecommerce.user_service.dto.LoginResponseDTO;
import com.ecommerce.user_service.dto.SignUpDTO;
import com.ecommerce.user_service.dto.SignUpResponseDTO;
import com.ecommerce.user_service.services.AuthService;
import com.ecommerce.user_service.services.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class AuthController {
    private final UserService userService;
    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDTO> signup(@RequestBody @Valid SignUpDTO signUpDTO) {
        SignUpResponseDTO signUpResponseDTO = userService.signUp(signUpDTO);

        return new ResponseEntity<>(signUpResponseDTO, HttpStatus.CREATED);
    }
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody @Valid LoginDTO loginDTO, HttpServletRequest request, HttpServletResponse response) {
        LoginResponseDTO loginResponseDTO = authService.login(loginDTO);
        Cookie cookie = new Cookie("refreshToken", loginResponseDTO.getRefreshToken());
        cookie.setHttpOnly(true);
        response.addCookie(cookie);

        return new ResponseEntity<>(loginResponseDTO, HttpStatus.OK);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refresh(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        String refreshToken = Arrays.stream(cookies).filter(cookie -> cookie.getName().equals("refreshToken"))
                .findFirst()
                .map(Cookie::getValue)
                .orElseThrow(() -> new AuthenticationServiceException("Refresh token not found"));


        LoginResponseDTO loginResponseDTO = authService.refreshToken(refreshToken);

        return new ResponseEntity<>(loginResponseDTO, HttpStatus.OK);
    }
}
