package com.catalogue.service;

import com.catalogue.model.EditProfile;
import com.catalogue.model.UserInfoModel;
import com.catalogue.others.AppUtils;
import com.catalogue.repository.UserInfoRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@Component
public class ProfileService {

    @Autowired
    UserInfoRepo userInfoRepo;

    @Autowired
    UserInfoModel userInfoModel;

    @Autowired
    AppUtils appUtils;

    String responseAsString;

    public JsonNode viewProfile(String userId) throws JsonProcessingException {

        JsonNode node=new ObjectMapper().convertValue(userInfoRepo.getUserInfo_ID(userId),JsonNode.class);
        String a=node.toString();
        System.out.println(a);

        if (String.valueOf(node).equals("null")) // this is quite amazing
            responseAsString="{\n \"isSuccessful\":false, \n \"profile\":\"invalid userID\" \n}";
        else
            responseAsString=String.format("{\n \"isSuccessful\":true, \n \"profile\":%s \n}",node);
        return new ObjectMapper().readValue(responseAsString,JsonNode.class);
    }

    public JsonNode editProfile(EditProfile editProfile) throws IOException {

        //to validate the fields in the edit profile field
        Map<String, String> fieldsValidityMessages=new HashMap<>();

        if(!String.valueOf (userInfoRepo.getUserInfo_ID(editProfile.getUserId())).equals("null")){

            //validate name
            String nameValidityMessage=appUtils.validateField(editProfile.getName(), "name");
            if (!nameValidityMessage.contains("valid")){
                fieldsValidityMessages.put("name",nameValidityMessage);
            }

            // validate email
            String emailValidityMessage=appUtils.validateField(editProfile.getEmail(), "email");
            if (!emailValidityMessage.contains("valid")){
                fieldsValidityMessages.put("name",nameValidityMessage);
            }

            //validate profile picture
            if(!(editProfile.getProfilePhoto().getSize()<500000)){
                fieldsValidityMessages.put("profileImage","image size too large");
            }
            else {
                MultipartFile file= editProfile.getProfilePhoto();

                //the long string is used to get the extension
                String imageLocation="E:\\TestImages\\"+"profileImage_"+editProfile.getUserId()+"."+ Objects.requireNonNull(file.getOriginalFilename()).substring(file.getOriginalFilename().lastIndexOf('.')+1);

                //saves in the file
                file.transferTo(new File(imageLocation));

                //save url to database
                editProfile.setProfilePhotoURL(imageLocation);
            }
        }
        else {
            responseAsString=String.format("{\n \"isSuccessful\":false, \n \"message\":\"userID or field(s) not valid\",\n \"errorDescription\":%s \n}",fieldsValidityMessages);
            return new ObjectMapper().readValue(responseAsString,JsonNode.class);
        }
        userInfoRepo.updateProfile(editProfile.getName(), editProfile.getEmail(),
                editProfile.getProfilePhotoURL(), editProfile.getUserId());
        responseAsString="{\n \"isSuccessful\":true, \n \"message\":\"profile successfully updated\" \n}";
        return new ObjectMapper().readValue(responseAsString,JsonNode.class);
    }
}