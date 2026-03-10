package com.kosign.customer_crud.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class GoogleRequest {
    @Email
    @NotBlank(message = "Email must not be blank")
    @Schema(description = "your email", example = "nornsochetra@gmail.com")
    private String email;
}
