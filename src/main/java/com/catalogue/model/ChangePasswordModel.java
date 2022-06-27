package com.catalogue.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
public class ChangePasswordModel {
    @Getter @Setter private String currentPassword, newPassword, confirmPassword, userId;
}