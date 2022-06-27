package com.catalogue.model;

import com.catalogue.others.AppUtils;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Component
@Table
@Entity
@ToString
public class RecordsModel {

    //use the generator class to generate id
    @Id @Getter @Setter private String id=new AppUtils().stringGenerator((byte) 16,(byte) 1);
    @Getter @Setter private String name, email, phoneNumber,dateOfBirth,gender,
            employmentType, address, userId;
}