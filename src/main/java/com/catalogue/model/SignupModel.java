package com.catalogue.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;

@Component
public class SignupModel {

    @Getter @Setter private  String name,email, password, confirmPassword;
}
