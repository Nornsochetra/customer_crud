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

    @ExceptionHandler(DuplicatedEmailException.class)
    public ResponseEntity<ApiResponse<EmailConflict>> handleDuplicatedEmail(DuplicatedEmailException ex){
        EmailConflict emailConflict = EmailConflict.builder()
                .email(ex.getMessage())
                .build();

        StatusInfo statusInfo = StatusInfo.builder()
                .code("DUPLICATED_CUSTOMER_EMAIL")
                .message("A customer with this email already exists.")
                .build();

        ApiResponse<EmailConflict> response = ApiResponse.<EmailConflict>builder()
                .status(statusInfo)
                .data(emailConflict)
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(ActiveOrderException.class)
    public ResponseEntity<ApiResponse<ActiveOrderDetails>> handleActiveOrder(ActiveOrderException ex){
        ActiveOrderDetails activeOrderDetails = ActiveOrderDetails.builder()
                .customerId(ex.getCustomerId())
                .activeOrderCount(ex.getActiveOrderCount())
                .build();
        StatusInfo statusInfo = StatusInfo.builder()
                .code("CUSTOMER_HAS_ACTIVE_ORDERS")
                .message("Customer cannot be deleted because they have active orders.")
                .build();
        ApiResponse<ActiveOrderDetails> response = ApiResponse.<ActiveOrderDetails>builder()
                .status(statusInfo)
                .data(activeOrderDetails)
                .build();
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
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
}
