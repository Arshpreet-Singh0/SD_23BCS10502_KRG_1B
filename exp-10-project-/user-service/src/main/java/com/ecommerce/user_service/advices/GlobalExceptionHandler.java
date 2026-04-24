package com.ecommerce.user_service.advices;

import com.ecommerce.user_service.exceptions.UserAlreadyExistsException;
import io.jsonwebtoken.JwtException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(NoSuchFieldException.class)
    public ResponseEntity<String> handleNoSuchFieldException(NoSuchFieldException ex) {
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<?>> handleAuthenticationException(AuthenticationException ex) {
        ApiError apiError = ApiError.builder()
                .message(ex.getLocalizedMessage())
                .status(HttpStatus.UNAUTHORIZED)
                .build();

        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleInputValidationErrors(MethodArgumentNotValidException exception){
        List<String> errors = exception.getBindingResult().getAllErrors().stream()
                .map(err -> err.getDefaultMessage())
                .collect(Collectors.toList());

        ApiError apiError = ApiError.builder().status(HttpStatus.BAD_REQUEST).message("Input validation failed.").subErrors(errors).build();
        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<?>> handleUserAlreadyExistsException(UserAlreadyExistsException ex) {
        ApiError apiError = ApiError.builder()
                .message(ex.getLocalizedMessage())
                .status(HttpStatus.CONFLICT)
                .build();

        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<?>> handleJwtException(JwtException ex) {
        ApiError apiError = ApiError.builder()
                .message(ex.getLocalizedMessage())
                .status(HttpStatus.UNAUTHORIZED)
                .build();

        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException ex) {
        ApiError apiError = ApiError.builder()
                .message(ex.getLocalizedMessage())
                .status(HttpStatus.FORBIDDEN)
                .build();

        return buildErrorResponseEntity(apiError);
    }

    private ResponseEntity<ApiResponse<?>> buildErrorResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(new ApiResponse<>(apiError),HttpStatus.NOT_FOUND);
    }
}
