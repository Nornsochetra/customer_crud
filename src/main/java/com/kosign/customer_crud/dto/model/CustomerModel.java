package com.kosign.customer_crud.dto.model;

import com.kosign.customer_crud.dto.enumeration.Roles;
import com.kosign.customer_crud.dto.enumeration.Status;
import com.kosign.customer_crud.dto.enumeration.Types;
import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "customer_entity")
public class CustomerModel {

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

    @CreatedDate
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "update_at")
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
}
