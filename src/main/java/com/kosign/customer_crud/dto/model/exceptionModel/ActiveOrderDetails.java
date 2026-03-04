package com.kosign.customer_crud.dto.model.exceptionModel;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActiveOrderDetails {
    private Long customerId;
    private Integer activeOrderCount;
}
