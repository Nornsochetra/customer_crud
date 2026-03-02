package com.kosign.customer_crud.dto.model;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class StatusInfo {
    private String code;
    private String message;
}
