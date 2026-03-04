package com.kosign.customer_crud.dto.request;

import com.kosign.customer_crud.dto.enumeration.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PartialUpdateCustomerRequest {

    private String phone;

    @NotNull(message = "Status is required")
    private Status status;
}
