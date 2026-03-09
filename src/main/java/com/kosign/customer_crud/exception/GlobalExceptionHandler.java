package com.kosign.customer_crud.exception;

import com.kosign.customer_crud.dto.model.exceptionModel.*;
import com.kosign.customer_crud.dto.request.CustomerRequest;
import com.kosign.customer_crud.dto.request.FullUpdateCustomerRequest;
import com.kosign.customer_crud.dto.response.APIResponse.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<ValidationErrorData>> handleValidationErrors(
            MethodArgumentNotValidException ex) {

        List<ErrorDetails> errors = new ArrayList<>(ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> ErrorDetails.builder()
                        .field(error.getField())
                        .message(error.getDefaultMessage())
                        .build()
                )
                .toList());

        //  check hasContact() here and add to same error list
        Object target = ex.getBindingResult().getTarget();


        if (target instanceof CustomerRequest request && !request.hasContact()) {
            errors.add(ErrorDetails.builder()
                    .field("email|phone")
                    .message("Either email or phone must be provided.")
                    .build());
        }

        if (target instanceof FullUpdateCustomerRequest request && !request.hasContact()) {
            errors.add(ErrorDetails.builder()
                    .field("email|phone")
                    .message("Either email or phone must be provided.")
                    .build());
        }

        return buildValidationResponse(errors);
    }

    @ExceptionHandler(ContactValidationException.class)
    public ResponseEntity<ApiResponse<ValidationErrorData>> handleContactValidationErrors(ContactValidationException ex){
        List<ErrorDetails> errors = new ArrayList<>();
        errors.add(ErrorDetails.builder()
                .field("email|phone")
                .message(ex.getMessage())
                .build());

        return buildValidationResponse(errors);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDenied(AccessDeniedException ex){
        StatusInfo statusInfo = StatusInfo.builder()
                .code("ACCESS_DENIED")
                .message("You do not have permission to perform this action.")
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(ApiResponse.<Void>builder()
                        .status(statusInfo)
                        .build());
    }

    private ResponseEntity<ApiResponse<ValidationErrorData>> buildValidationResponse(List<ErrorDetails> errors) {
        StatusInfo statusInfo = StatusInfo.builder()
                .code("REQUIRED_FIELD_MISSING")
                .message("Invalid request data.")
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ApiResponse.<ValidationErrorData>builder()
                        .status(statusInfo)
                        .data(ValidationErrorData.builder()
                                .errors(errors)
                                .build())
                        .build());
    }

    @ExceptionHandler(CustomerException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomerException(CustomerException ex){

        StatusInfo statusInfo = StatusInfo.builder()
                .code(ex.getStatus().name())
                .message(ex.getMessage())
                .build();

        ApiResponse<Object> response = ApiResponse.builder()
                .status(statusInfo)
                .data(ex.getData()) // dynamic data
                .build();

        return ResponseEntity
                .status(ex.getStatus().getHttpStatus())
                .body(response);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiResponse<Object>> handleAuthenticationException(AuthenticationException ex){

        StatusInfo statusInfo = StatusInfo.builder()
                .code(ex.getStatus().name())
                .message(ex.getMessage())
                .build();

        ApiResponse<Object> response = ApiResponse.builder()
                .status(statusInfo)
                .data(ex.getData()) // dynamic data
                .build();

        return ResponseEntity
                .status(ex.getStatus().getHttpStatus())
                .body(response);
    }
}
