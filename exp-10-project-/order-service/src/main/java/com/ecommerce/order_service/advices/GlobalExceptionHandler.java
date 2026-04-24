package com.ecommerce.order_service.advices;

import com.ecommerce.order_service.exceptions.InventoryReservationException;
import com.ecommerce.order_service.exceptions.PriceMismatchException;
import com.ecommerce.order_service.exceptions.ProductNotFoundException;
import io.jsonwebtoken.JwtException;
import org.springframework.dao.DataIntegrityViolationException;
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

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse<?>> handleJwtException(JwtException ex) {
        ApiError apiError = ApiError.builder()
                .message(ex.getLocalizedMessage())
                .status(HttpStatus.UNAUTHORIZED)
                .build();

        return buildErrorResponseEntity(apiError);
    }
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<ApiResponse<?>> handleProductNotFoundException(ProductNotFoundException ex) {
        ApiError apiError = ApiError.builder()
                .message(ex.getLocalizedMessage())
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(PriceMismatchException.class)
    public ResponseEntity<ApiResponse<?>> handlePriceMismatchException(PriceMismatchException ex) {
        ApiError apiError = ApiError.builder()
                .message(ex.getLocalizedMessage())
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(InventoryReservationException.class)
    public ResponseEntity<ApiResponse<?>> handleInventoryReservationException(InventoryReservationException ex) {
        ApiError apiError = ApiError.builder()
                .message(ex.getLocalizedMessage())
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();

        return buildErrorResponseEntity(apiError);
    }


    // DB constraint errors
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<?>> handleDBException(DataIntegrityViolationException ex) {

        ApiError apiError = ApiError.builder()
                .message("Database constraint violation")
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return buildErrorResponseEntity(apiError);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntime(RuntimeException ex) {

        ApiError apiError = ApiError.builder()
                .message(ex.getMessage())
                .status(HttpStatus.BAD_REQUEST)
                .build();

        return buildErrorResponseEntity(apiError);
    }

    // 💥 Fallback
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleGeneric(Exception ex) {

        ApiError apiError = ApiError.builder()
                .message("Internal server error")
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .build();

        return buildErrorResponseEntity(apiError);
    }

    private ResponseEntity<ApiResponse<?>> buildErrorResponseEntity(ApiError apiError) {
        return new ResponseEntity<>(new ApiResponse<>(apiError),  apiError.getStatus());
    }
}
