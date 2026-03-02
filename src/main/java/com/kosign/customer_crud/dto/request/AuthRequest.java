package com.kosign.customer_crud.dto.request;

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
public class AuthRequest {

    @NotBlank(message = "customer name can not be blank")
    private String username;

    @NotNull(message = "password can not be null")
    @NotBlank(message = "password can not be blank")
    private String password;

}
