package com.kosign.customer_crud.service.impl;

import com.kosign.customer_crud.dto.enumeration.*;
import com.kosign.customer_crud.dto.model.CustomerModel;
import com.kosign.customer_crud.dto.model.UserInfo;
import com.kosign.customer_crud.dto.model.exceptionModel.ActiveOrderDetails;
import com.kosign.customer_crud.dto.request.AuthRequest;
import com.kosign.customer_crud.dto.request.CustomerRequest;
import com.kosign.customer_crud.dto.request.FullUpdateCustomerRequest;
import com.kosign.customer_crud.dto.request.PartialUpdateCustomerRequest;
import com.kosign.customer_crud.dto.response.APIResponse.PaginationResponse;
import com.kosign.customer_crud.dto.response.APIResponse.PayloadResponse;
import com.kosign.customer_crud.dto.response.ModelResponse.AuthResponse;
import com.kosign.customer_crud.dto.response.ModelResponse.CustomerResponse;
import com.kosign.customer_crud.exception.*;
import com.kosign.customer_crud.repository.CustomerRepository;
import com.kosign.customer_crud.service.CustomerService;
import com.kosign.customer_crud.service.JwtService;
import com.kosign.customer_crud.service.RedisTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final JwtService jwtService;
    private final RedisTokenService redisTokenService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws AuthenticationException {
        CustomerModel customer = customerRepository.findByUsername(username)
                .orElseThrow(() -> new AuthenticationException(AuthStatus.INVALID_CREDENTIALS,"Username not found"));

        if(customer.getStatus() == Status.INACTIVE){
            throw new AuthenticationException(AuthStatus.ACCOUNT_INACTIVE,"Account is inactive. Please contact support!");
        }

        return User.builder()
                .username(customer.getUsername())
                .password(customer.getPassword())
                .authorities(customer.getAuthorities())

                .build();
    }

    @Override
    public AuthResponse signInUser(AuthRequest authRequest) {
        CustomerModel customerModel = customerRepository.findByUsername(authRequest.getUsername())
                .orElseThrow(() -> new AuthenticationException(AuthStatus.INVALID_CREDENTIALS,"Username not found"));

        if(customerModel.getStatus() == Status.INACTIVE){
            throw new AuthenticationException(AuthStatus.ACCOUNT_INACTIVE,"Account is inactive. Please contact support!");
        }

        if (!passwordEncoder.matches(authRequest.getPassword(), customerModel.getPassword())) {
            throw new AuthenticationException(AuthStatus.INVALID_CREDENTIALS,"Invalid username or password.");
        }

        String accessToken = jwtService.generateToken(customerModel.getUsername());
        String refreshToken = jwtService.generateRefreshToken(customerModel.getUsername());

        redisTokenService.saveAccessToken(accessToken, customerModel.getUsername(), jwtService.getAccessTokenExpiration());
        redisTokenService.saveRefreshToken(refreshToken,customerModel.getUsername(),jwtService.getRefreshTokenExpiration());

        UserInfo userInfo = UserInfo.builder()
                .customerId(customerModel.getCustomerId())
                .username(customerModel.getUsername())
                .roles(customerModel.getRoles() != null
                        ? customerModel.getRoles().stream().map(Enum::name).toList() : null
                )
                .build();

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiration())
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
            throw new CustomerException(CustomerStatus.CUSTOMER_NOT_FOUND,"Customer not found.");
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
                .orElseThrow(() -> new CustomerException(CustomerStatus.CUSTOMER_NOT_FOUND,"Customer with id " + customerId + " not found"));
        return customerModel.toCustomerResponse();
    }

    @Override
    public CustomerResponse createCustomer(CustomerRequest request) {

        if (!request.hasContact()){
            throw new ContactValidationException("Either email or phone must be provided.");
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()){
            if (customerRepository.existsByEmail(request.getEmail())){
                throw new CustomerException(CustomerStatus.DUPLICATE_CUSTOMER_EMAIL,"A customer with this email already exists." ,request.getEmail());
            }
        }
        CustomerModel customerModel = new CustomerModel();
        customerModel.setUsername(request.getUsername());
        customerModel.setTypes(request.getType());
        customerModel.setEmail(request.getEmail());
        customerModel.setPhone(request.getPhone());
        customerModel.setStatus(Status.ACTIVE);
        customerModel.setPassword(passwordEncoder.encode("123"));
        customerModel.setRoles(List.of(Roles.CUSTOMER_READ));
        customerModel.setCreatedAt(LocalDateTime.now());
        customerModel.setUpdatedAt(LocalDateTime.now());

        return customerRepository.save(customerModel).toCustomerResponse();
    }

    @Override
    public CustomerResponse changeCustomerById(Long customerId, FullUpdateCustomerRequest request) {
        CustomerModel customerModel = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerException(CustomerStatus.CUSTOMER_NOT_FOUND,"Customer with id " + customerId + " not found"));

        if (!request.hasContact()){
            throw new ContactValidationException("Either email or phone must be provided.");
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()){
            if (customerRepository.existsByEmailAndCustomerIdNot(request.getEmail(),customerId)){
                throw new CustomerException(CustomerStatus.DUPLICATE_CUSTOMER_EMAIL, request.getEmail());
            }
        }

        customerModel.setUsername(request.getUsername());
        customerModel.setTypes(request.getType());
        customerModel.setEmail(request.getEmail());
        customerModel.setPhone(request.getPhone());
        customerModel.setStatus(request.getStatus());

        return customerRepository.save(customerModel).toCustomerResponse();
    }

    @Override
    public CustomerResponse changeCustomerPhoneAndStatus(Long customerId, PartialUpdateCustomerRequest request) {
        CustomerModel customerModel = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerException(CustomerStatus.CUSTOMER_NOT_FOUND,"Customer with id " + customerId + " not found"));

        customerModel.setPhone(request.getPhone());
        customerModel.setStatus(request.getStatus());
        return customerRepository.save(customerModel).toCustomerResponse();
    }

    @Override
    public void deleteCustomerById(Long customerId) {
        CustomerModel customerModel = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerException(CustomerStatus.CUSTOMER_NOT_FOUND,"Customer with id " + customerId + " not found"));

        if(customerModel.getActiveOrderCount() != null && customerModel.getActiveOrderCount() > 0){
            throw new CustomerException(CustomerStatus.CUSTOMER_HAS_ACTIVE_ORDERS,"Customer cannot be deleted because they have active orders.",
                    ActiveOrderDetails.builder()
                    .customerId(customerId)
                    .activeOrderCount(customerModel.getActiveOrderCount())
                    .build());
        }
        customerRepository.deleteById(customerId);
    }

    @Override
    public AuthResponse refreshToken(String refreshToken) {
        if (!redisTokenService.isRefreshTokenValid(refreshToken)){
            throw new AuthenticationException(AuthStatus.INVALID_CREDENTIALS, "Refresh token expired or invalid");
        }

        String username = redisTokenService.getUsernameFromRefreshToken(refreshToken);
        String newAccessToken = jwtService.generateToken(username);

        redisTokenService.saveAccessToken(newAccessToken, username, jwtService.getAccessTokenExpiration());

        return AuthResponse.builder()
                .accessToken(newAccessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getAccessTokenExpiration())
                .build();
    }

    @Override
    public void logout(String accessToken, String refreshToken) {
        redisTokenService.deleteTokens(accessToken,refreshToken);
        redisTokenService.blacklistToken(accessToken, jwtService.getAccessTokenExpiration());
    }
}
