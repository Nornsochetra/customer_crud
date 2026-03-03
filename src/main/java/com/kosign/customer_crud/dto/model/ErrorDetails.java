package com.kosign.customer_crud.dto.model;

import lombok.*;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class ErrorDetails {
    private String field;
    private String message;
}
