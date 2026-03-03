package com.kosign.customer_crud.dto.response.APIResponse;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PaginationResponse {
    private Integer page;
    private Integer size;
    private Long totalItems;
    private Integer totalPages;
}
