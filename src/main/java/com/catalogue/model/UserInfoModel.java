package com.catalogue.model;

import com.catalogue.others.AppUtils;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Table
@Entity
@Component
public class UserInfoModel {

    //id cannot have @Setter, once created, always constant
   @JsonIgnore @Id @Getter final private String userId= new AppUtils().stringGenerator((byte) 16,(byte) 1);

    @Getter @Setter private  String name,email,profilePhotoUrl;
    @Getter @Setter @Transient @JsonIgnore private MultipartFile profilePhoto;
    @Getter @Setter @JsonIgnore private String  passwordSalt,passwordHash;

    @JsonIgnore  private boolean inSession;

    public boolean getInSession() {
        return inSession;
    }

    public void setInSession(boolean inSession) {
        this.inSession = inSession;
    }
}