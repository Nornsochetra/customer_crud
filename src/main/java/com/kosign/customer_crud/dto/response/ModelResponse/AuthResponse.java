package com.kosign.customer_crud.dto.response.ModelResponse;

import com.kosign.customer_crud.dto.model.UserInfo;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthResponse {
    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private UserInfo userInfo;
}
