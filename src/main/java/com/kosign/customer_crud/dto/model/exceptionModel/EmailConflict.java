package com.kosign.customer_crud.dto.model.exceptionModel;

import lombok.*;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class EmailConflict {
    private String email;
}
