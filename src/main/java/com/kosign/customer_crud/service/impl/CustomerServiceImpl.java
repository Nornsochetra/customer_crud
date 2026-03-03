package com.kosign.customer_crud.service.impl;

import com.kosign.customer_crud.dto.enumeration.Roles;
import com.kosign.customer_crud.dto.enumeration.Status;
import com.kosign.customer_crud.dto.enumeration.Types;
import com.kosign.customer_crud.dto.model.CustomerModel;
import com.kosign.customer_crud.dto.model.UserInfo;
import com.kosign.customer_crud.dto.request.AuthRequest;
import com.kosign.customer_crud.dto.request.CustomerRequest;
import com.kosign.customer_crud.dto.response.APIResponse.PaginationResponse;
import com.kosign.customer_crud.dto.response.APIResponse.PayloadResponse;
import com.kosign.customer_crud.dto.response.ModelResponse.AuthResponse;
import com.kosign.customer_crud.dto.response.ModelResponse.CustomerResponse;
import com.kosign.customer_crud.exception.InvalidCredentialException;
import com.kosign.customer_crud.exception.NotFoundException;
import com.kosign.customer_crud.repository.CustomerRepository;
import com.kosign.customer_crud.service.CustomerService;
import com.kosign.customer_crud.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws InvalidCredentialException {
        CustomerModel customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidCredentialException("Username not found"));

        return User.builder()
                .username(customer.getUsername())
                .password(customer.getPassword())
                .authorities(customer.getAuthorities())
                .build();
    }

    @Override
    public AuthResponse signInUser(AuthRequest authRequest) {
        CustomerModel customerModel = customerRepository.findByUsername(authRequest.getUsername())
                .orElseThrow(() -> new InvalidCredentialException("Username not found"));

        if(customerModel.getStatus() == Status.INACTIVE){
            throw new DisabledException("Account is inactive. Please contact support!");
        }

        if (!passwordEncoder.matches(authRequest.getPassword(), customerModel.getPassword())) {
            throw new InvalidCredentialException("Invalid username or password");
        }

        String token = jwtService.generateToken(customerModel.getUsername());

        UserInfo userInfo = UserInfo.builder()
                .customerId(customerModel.getCustomerId())
                .username(customerModel.getUsername())
                .roles(customerModel.getRoles() != null
                        ? customerModel.getRoles().stream().map(Enum::name).toList() : null
                )
                .build();

        return AuthResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(3600)
                .userInfo(userInfo)
                .build();
    }

    @Override
    public PayloadResponse<CustomerResponse> retrieveAllCustomers(String search, Types types, Status status, int page, int size) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        Page<CustomerModel> customerModelPage = customerRepository.findAllWithFilters(
                search, types, status, pageable
        );

        if (customerModelPage.isEmpty()) {
            throw new NotFoundException("Customer not found.");
        }

        List<CustomerResponse> customerResponses = customerModelPage.stream()
                .map(CustomerModel::toCustomerResponse)
                .toList();

        PaginationResponse paginationResponse = PaginationResponse.builder()
                .page(page)
                .size(size)
                .totalItems(customerModelPage.getTotalElements())
                .totalPages(customerModelPage.getTotalPages())
                .build();

        return PayloadResponse.<CustomerResponse>builder()
                .items(customerResponses)
                .paginationResponse(paginationResponse)
                .build();
    }

    @Override
    public CustomerResponse retrieveCustomerById(Long customerId) {
        CustomerModel customerModel = customerRepository.findById(customerId)
                .orElseThrow(() -> new NotFoundException("Customer with id " + customerId + " not found"));
        return customerModel.toCustomerResponse();
    }

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {
        CustomerModel customerModel = new CustomerModel();
        customerModel.setUsername(request.getUsername());
        customerModel.setTypes(request.getType());
        customerModel.setEmail(request.getEmail());
        customerModel.setPhone(request.getPhone());
        customerModel.setStatus(Status.ACTIVE);
        customerModel.setPassword(passwordEncoder.encode("123"));
        customerModel.setRoles(List.of(Roles.CUSTOMER_WRITE));
        customerModel.setCreatedAt(LocalDateTime.now());
        customerModel.setUpdatedAt(LocalDateTime.now());

        return customerRepository.save(customerModel).toCustomerResponse();
    }
}
