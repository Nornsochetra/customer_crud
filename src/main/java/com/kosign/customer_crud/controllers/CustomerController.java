package com.kosign.customer_crud.controllers;

import com.kosign.customer_crud.dto.enumeration.CustomerStatus;
import com.kosign.customer_crud.dto.enumeration.Status;
import com.kosign.customer_crud.dto.enumeration.Types;
import com.kosign.customer_crud.dto.model.exceptionModel.StatusInfo;
import com.kosign.customer_crud.dto.request.CustomerRequest;
import com.kosign.customer_crud.dto.request.FullUpdateCustomerRequest;
import com.kosign.customer_crud.dto.request.PartialUpdateCustomerRequest;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/customers")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class CustomerController {

    private final CustomerService customerService;

    @GetMapping
    @PreAuthorize("hasAnyAuthority('CUSTOMER_READ', 'CUSTOMER_WRITE')")
    public ResponseEntity<ApiResponse<PayloadResponse<CustomerResponse>>> getAllCustomers(
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Types types,
            @RequestParam(required = false)  Status status,
            @RequestParam(defaultValue = "1")@Min(value = 1) @NotNull int page,
            @RequestParam(defaultValue = "10") @Min(value = 1) @NotNull int size
    ){
        PayloadResponse<CustomerResponse> payloadResponse = customerService.retrieveAllCustomers(search,types,status,page,size);

        StatusInfo statusInfo = StatusInfo.builder()
                .code(String.valueOf(CustomerStatus.SUCCESS))
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
    @PreAuthorize("hasAuthority('CUSTOMER_WRITE')")
    public ResponseEntity<ApiResponse<CustomerResponse>> insertCustomer(@RequestBody @Valid CustomerRequest request) {

        CustomerResponse customerResponse = customerService.createCustomer(request);

        StatusInfo statusInfo = StatusInfo.builder()
                .code(String.valueOf(CustomerStatus.SUCCESS))
                .message("Customer created successfully.")
                .build();
        ApiResponse<CustomerResponse> response = ApiResponse.<CustomerResponse>builder()
                .status(statusInfo)
                .data(customerResponse)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{customerId}")
    @PreAuthorize("hasAnyAuthority('CUSTOMER_READ', 'CUSTOMER_WRITE')")
    public ResponseEntity<ApiResponse<CustomerResponse>> getCustomerById(
            @PathVariable @NotNull Long customerId
    ) {
        StatusInfo statusInfo = StatusInfo.builder()
                .code(String.valueOf(CustomerStatus.SUCCESS))
                .message("Customer retrieved successfully.")
                .build();

        ApiResponse<CustomerResponse> response = ApiResponse.<CustomerResponse>builder()
                .status(statusInfo)
                .data(customerService.retrieveCustomerById(customerId))
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{customerId}")
    @PreAuthorize("hasAuthority('CUSTOMER_WRITE')")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomerById(
            @PathVariable @NotNull Long customerId,
            @RequestBody @Valid FullUpdateCustomerRequest request
    ) {
        CustomerResponse customerResponse = customerService.changeCustomerById(customerId,request);

        StatusInfo statusInfo = StatusInfo.builder()
                .code(String.valueOf(CustomerStatus.SUCCESS))
                .message("Customer updated successfully.")
                .build();
        ApiResponse<CustomerResponse> response = ApiResponse.<CustomerResponse>builder()
                .status(statusInfo)
                .data(customerResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @PatchMapping("/{customerId}")
    @PreAuthorize("hasAuthority('CUSTOMER_WRITE')")
    public ResponseEntity<ApiResponse<CustomerResponse>> updateCustomerPhoneAndStatus(
            @PathVariable @NotNull Long customerId,
            @RequestBody @Valid PartialUpdateCustomerRequest request
    ){
        CustomerResponse customerResponse = customerService.changeCustomerPhoneAndStatus(customerId,request);
        StatusInfo statusInfo = StatusInfo.builder()
                .code(String.valueOf(CustomerStatus.SUCCESS))
                .message("Customer updated successfully.")
                .build();
        ApiResponse<CustomerResponse> response = ApiResponse.<CustomerResponse>builder()
                .status(statusInfo)
                .data(customerResponse)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @DeleteMapping("/{customerId}")
    @PreAuthorize("hasAuthority('CUSTOMER_WRITE')")
    public ResponseEntity<ApiResponse<Void>> deleteCustomerById(
            @PathVariable @NotNull Long customerId
    ){
        customerService.deleteCustomerById(customerId);
        StatusInfo statusInfo = StatusInfo.builder()
                .code(String.valueOf(CustomerStatus.SUCCESS))
                .message("Customer deleted successfully.")
                .build();
        ApiResponse<Void> response = ApiResponse.<Void>builder()
                .status(statusInfo)
                .build();
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }


}
