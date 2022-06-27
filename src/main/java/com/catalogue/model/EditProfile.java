package com.catalogue.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class EditProfile {
    @Getter @Setter MultipartFile profilePhoto;
    @Getter @Setter String name, email,profilePhotoURL,userId;
}