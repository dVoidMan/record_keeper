package com.catalogue.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class LoginModel {
    @Getter @Setter private String email, password, userFirstName;
    @Getter @Setter private MultipartFile multipartFile;
}
