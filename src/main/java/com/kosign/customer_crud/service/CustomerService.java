package com.kosign.customer_crud.service;

import com.kosign.customer_crud.dto.enumeration.Status;
import com.kosign.customer_crud.dto.enumeration.Types;
import com.kosign.customer_crud.dto.request.AuthRequest;
import com.kosign.customer_crud.dto.request.CustomerRequest;
import com.kosign.customer_crud.dto.request.FullUpdateCustomerRequest;
import com.kosign.customer_crud.dto.request.PartialUpdateCustomerRequest;
import com.kosign.customer_crud.dto.response.APIResponse.PayloadResponse;
import com.kosign.customer_crud.dto.response.ModelResponse.AuthResponse;
import com.kosign.customer_crud.dto.response.ModelResponse.CustomerResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface CustomerService extends UserDetailsService {
    AuthResponse signInUser(@Valid AuthRequest authRequest);

    PayloadResponse<CustomerResponse> retrieveAllCustomers(
            String search,
            Types types,
            Status status,
            int page,
            int size
    );

    CustomerResponse retrieveCustomerById(@NotNull(message = "Customer id can not be null") Long customerId);

    CustomerResponse createCustomer(CustomerRequest request);

    CustomerResponse changeCustomerById(@NotNull(message = "Customer id can not be null") Long customerId, @Valid FullUpdateCustomerRequest request);

    CustomerResponse changeCustomerPhoneAndStatus(@NotNull(message = "Customer id can not be null") Long customerId, @Valid PartialUpdateCustomerRequest request);

    void deleteCustomerById(@NotNull(message = "Customer id can not be null") Long customerId);
}
