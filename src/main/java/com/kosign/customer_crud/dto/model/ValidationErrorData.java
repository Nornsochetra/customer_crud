package com.kosign.customer_crud.dto.model;

import lombok.*;

import java.util.List;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class ValidationErrorData {
    private List<ErrorDetails> errors;
}
