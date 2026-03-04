package com.kosign.customer_crud.dto.request;

import com.kosign.customer_crud.dto.enumeration.Status;
import com.kosign.customer_crud.dto.enumeration.Types;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class FullUpdateCustomerRequest {

    @NotBlank(message = "Customer name is required.")
    private String username;

    @NotNull(message = "Type is required")
    private Types type;

    @Email(message = "Email format is invalid.")
    private String email;
    private String phone;

    @NotNull(message = "Status is required")
    private Status status;

    public boolean hasContact() {
        return (email != null && !email.isBlank()) || (phone != null && !phone.isBlank());
    }
}
