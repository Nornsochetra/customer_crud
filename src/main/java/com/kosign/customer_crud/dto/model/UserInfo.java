package com.kosign.customer_crud.dto.model;

import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class UserInfo {
    private long customerId;
    private String username;
    private List<String> roles;
}
