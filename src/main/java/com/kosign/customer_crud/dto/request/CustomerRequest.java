package com.kosign.customer_crud.dto.request;

import com.kosign.customer_crud.dto.enumeration.Types;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class CustomerRequest {

    @NotBlank(message = "Customer name is required.")
    private String username;

    @NotNull(message = "Type is required")
    private Types type;

    private String email;
    private String phone;

    public boolean hasContact() {
        return (email != null && !email.isBlank()) || (phone != null && !phone.isBlank());
    }
}
