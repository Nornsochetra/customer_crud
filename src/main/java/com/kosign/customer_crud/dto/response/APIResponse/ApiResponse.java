package com.kosign.customer_crud.dto.response.APIResponse;

import com.kosign.customer_crud.dto.model.exceptionModel.StatusInfo;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class ApiResponse<T> {
    private StatusInfo status;
    private T data;
}
