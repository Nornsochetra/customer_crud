package com.kosign.customer_crud.dto.response.APIResponse;

import com.kosign.customer_crud.dto.model.StatusInfo;
import lombok.*;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class ApiResponse<T> {
    private StatusInfo status;
    private T data;
}
