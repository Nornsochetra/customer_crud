package com.kosign.customer_crud.dto.response.APIResponse;

import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PayloadResponse<T>{
    private List<T> items;
    private PaginationResponse paginationResponse;
}
