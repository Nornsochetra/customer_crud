package com.kosign.customer_crud.dto.model.exceptionModel;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class StatusInfo {
    private String code;
    private String message;
}
