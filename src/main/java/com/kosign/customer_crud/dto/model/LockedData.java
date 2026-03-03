package com.kosign.customer_crud.dto.model;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Builder
public class LockedData {
    private LocalDateTime lockedUntil;
}
