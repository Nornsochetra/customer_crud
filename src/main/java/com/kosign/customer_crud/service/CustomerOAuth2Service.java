package com.kosign.customer_crud.service;

import com.kosign.customer_crud.dto.enumeration.AuthProvider;
import com.kosign.customer_crud.dto.enumeration.Roles;
import com.kosign.customer_crud.dto.enumeration.Status;
import com.kosign.customer_crud.dto.enumeration.Types;
import com.kosign.customer_crud.dto.model.CustomerModel;
import com.kosign.customer_crud.dto.model.CustomerOAuth2User;
import com.kosign.customer_crud.repository.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerOAuth2Service implements OAuth2UserService {

    private final CustomerRepository customerRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = new DefaultOAuth2UserService().loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");
        if (email == null){
            throw new OAuth2AuthenticationException("Email not found");
        }

        CustomerModel customerModel = customerRepository.findByEmail(email)
                .orElseGet(() -> createGoogleUser(email,name));
        return new CustomerOAuth2User(customerModel, oAuth2User.getAttributes());
    }

    private CustomerModel createGoogleUser(String email, String name){
        CustomerModel customerModel = new CustomerModel();

        customerModel.setUsername(name);
        customerModel.setTypes(Types.INDIVIDUAL);
        customerModel.setEmail(email);
        customerModel.setPhone(null);
        customerModel.setStatus(Status.ACTIVE);
        customerModel.setPassword(null);
        customerModel.setRoles(List.of(Roles.CUSTOMER_READ));
        customerModel.setCreatedAt(LocalDateTime.now());
        customerModel.setUpdatedAt(LocalDateTime.now());
        customerModel.setAuthProvider(AuthProvider.GOOGLE);

        return customerRepository.save(customerModel);
    }
}
