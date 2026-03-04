package com.kosign.customer_crud.dto.model;

import com.kosign.customer_crud.dto.enumeration.Roles;
import com.kosign.customer_crud.dto.enumeration.Status;
import com.kosign.customer_crud.dto.enumeration.Types;
import com.kosign.customer_crud.dto.response.ModelResponse.CustomerResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jspecify.annotations.Nullable;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "customer_entity")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class CustomerModel implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "customer_id")
    private Long customerId;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    private String email;

    private String phone;

    @Column(name = "customer_roles")
    @Enumerated(EnumType.STRING)
    private List<Roles> roles;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Types types;

    @Enumerated(EnumType.STRING)
    private Status status = Status.ACTIVE;

    private Integer activeOrderCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public UserInfo toUserInfo() {
        return UserInfo.builder()
                .customerId(this.customerId)
                .username(this.username)
                .roles(this.roles != null ?
                        this.roles.stream().map(Enum::name).toList() :
                        List.of())
                .build();
    }

    public CustomerResponse toCustomerResponse() {
        return CustomerResponse.builder()
                .customerId(this.customerId)
                .username(this.username)
                .types(this.types)
                .email(this.email)
                .phone(this.phone)
                .status(this.status)
                .createdAt(this.createdAt)
                .updatedAt(this.updatedAt)
                .build();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .toList();
    }

    @Override
    public String getUsername() { return username; }

    @Override
    public String getPassword() { return password; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return status == Status.ACTIVE; }
}
