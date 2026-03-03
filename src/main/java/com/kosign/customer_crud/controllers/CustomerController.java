package com.kosign.customer_crud.controllers;

import com.kosign.customer_crud.dto.enumeration.Status;
import com.kosign.customer_crud.dto.enumeration.Types;
import com.kosign.customer_crud.dto.model.StatusInfo;
import com.kosign.customer_crud.dto.request.CustomerRequest;
import com.kosign.customer_crud.dto.response.APIResponse.ApiResponse;
import com.kosign.customer_crud.dto.response.APIResponse.PayloadResponse;
import com.kosign.customer_crud.dto.response.ModelResponse.CustomerResponse;
import com.kosign.customer_crud.service.CustomerService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/customers")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    public ResponseEntity<ApiResponse<PayloadResponse<CustomerResponse>>> getAllCustomers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Types types,
            @RequestParam(required = false)  Status status,
            @RequestParam(defaultValue = "1")@Min(value = 1, message = "Page number must be at least 1") int page,
            @RequestParam(defaultValue = "10") @Min(value = 1, message = "Size number must be at least 1") int size
    ){
        PayloadResponse<CustomerResponse> payloadResponse = customerService.retrieveAllCustomers(search,types,status,page,size);

        StatusInfo statusInfo = StatusInfo.builder()
                .code("SUCCESS")
                .message("Customer list retrieved successfully.")
                .build();
        return ResponseEntity.ok().body(
                ApiResponse.<PayloadResponse<CustomerResponse>>builder()
                        .status(statusInfo)
                        .data(payloadResponse)
                        .build()
        );
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CustomerResponse>> insertCustomer(@RequestBody @Valid CustomerRequest request) {

        CustomerResponse customerResponse = customerService.createCustomer(request);

        StatusInfo statusInfo = StatusInfo.builder()
                .code("SUCCESS")
                .message("Customer created successfully.")
                .build();
        ApiResponse<CustomerResponse> response = ApiResponse.<CustomerResponse>builder()
                .status(statusInfo)
                .data(customerResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{customerId}")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerById(
            @PathVariable @NotNull(message = "Customer id can not be null") Long customerId
    ) {
        StatusInfo statusInfo = StatusInfo.builder()
                .code("SUCCESS")
                .message("Customer retrieved successfully.")
                .build();

        ApiResponse<CustomerResponse> response = ApiResponse.<CustomerResponse>builder()
                .status(statusInfo)
                .data(customerService.retrieveCustomerById(customerId))
                .build();
        return ResponseEntity.ok(response);
    }


}
