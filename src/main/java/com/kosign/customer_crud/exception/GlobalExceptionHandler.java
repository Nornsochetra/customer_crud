package com.kosign.customer_crud.exception;

import com.kosign.customer_crud.dto.model.ErrorDetails;
import com.kosign.customer_crud.dto.model.LockedData;
import com.kosign.customer_crud.dto.model.StatusInfo;
import com.kosign.customer_crud.dto.model.ValidationErrorData;
import com.kosign.customer_crud.dto.request.CustomerRequest;
import com.kosign.customer_crud.dto.response.APIResponse.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // handle invalid credential http 401
    @ExceptionHandler(InvalidCredentialException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentials(InvalidCredentialException ex){
        StatusInfo statusInfo = StatusInfo.builder()
                .code("INVALID_CREDENTIALS")
                .message(ex.getMessage())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ApiResponse<>(statusInfo,null));
    }

    // handle account locked http 403
    @ExceptionHandler(AccountLockedException.class)
    public ResponseEntity<ApiResponse<LockedData>> handleLockedAccount(AccountLockedException ex) {

        LockedData lockedData = LockedData.builder()
                .lockedUntil(ex.getLockedUntil())
                .build();

        StatusInfo statusInfo = StatusInfo.builder()
                .code("ACCOUNT_LOCKED")
                .message(ex.getMessage())
                .build();

        ApiResponse<LockedData> response = ApiResponse.<LockedData>builder()
                .status(statusInfo)
                .data(lockedData)
                .build();

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
    }


    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<ApiResponse<Object>> handleInactiveAccount(DisabledException ex){
        StatusInfo statusInfo = StatusInfo.builder()
                .code("ACCOUNT_INACTIVE")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ApiResponse<>(statusInfo,null));
    }

    // handle customer not found
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleCustomerNotFound(NotFoundException ex) {
        StatusInfo statusInfo = StatusInfo.builder()
                .code("CUSTOMER_NOT_FOUND")
                .message(ex.getMessage())
                .build();
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiResponse<>(statusInfo,null));
    }

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

        return buildValidationResponse(errors);
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
}
