package com.auriga_tt.dto;

import com.auriga_tt.model.UserContactMethod;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserContactMethodDTO {
    private Long contactId;
    private UserDTO user;
    private UserContactMethod.ContactType contactType;
    private String contactValue;
    private Boolean isVerified;
    private Boolean isPrimary;
    private LocalDateTime createdAt;
}
