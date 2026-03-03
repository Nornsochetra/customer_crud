package com.kosign.customer_crud.dto.response.ModelResponse;

import com.kosign.customer_crud.dto.enumeration.Status;
import com.kosign.customer_crud.dto.enumeration.Types;
import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class CustomerResponse {
    private Long customerId;
    private String username;
    private Types types;
    private String email;
    private String phone;
    private Status status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
