package com.catalogue.service;

import com.catalogue.model.SignupModel;
import com.catalogue.model.UserInfoModel;
import com.catalogue.others.AppUtils;
import com.catalogue.repository.UserInfoRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

@Service
public class SignUpService {

    @Autowired
    Environment environment;

    @Autowired
    AppUtils appUtils;

    @Autowired
    UserInfoRepo userInfoRepo;

    @Autowired
    UserInfoModel userInfoModel;

    public JsonNode signupResponse(SignupModel signupModel) throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
        String responseAsString;

        //validate plane password
        boolean passwordMatches=signupModel.getPassword().equals(signupModel.getConfirmPassword());

        //to validate the fields in the signup details
        Map<String, String> fieldsValidityMessages=new HashMap<>();

        //validate name
        String nameValidityMessage=appUtils.validateField(signupModel.getName(), "name");
        if (!nameValidityMessage.contains("valid"))
            fieldsValidityMessages.put("name",nameValidityMessage);

        //validate email
        String emailValidityMessage=appUtils.validateField(signupModel.getEmail(),"email");
        if (!emailValidityMessage.contains("valid"))
            fieldsValidityMessages.put("email",emailValidityMessage);

        //validate password
        String passwordValidityMessage=appUtils.validateField(signupModel.getPassword(),"password");
        if (passwordMatches){

            //validate actual message
            if (!passwordValidityMessage.contains("valid"))
                fieldsValidityMessages.put("password",passwordValidityMessage);
        }
        else
            fieldsValidityMessages.put("password","password mismatch");

        if (nameValidityMessage.contains("valid") && emailValidityMessage.contains("valid")
                &&passwordValidityMessage.contains("valid")
                && passwordMatches){

            //perform hashing on password
            String secret=environment.getProperty("secret");
            String salt=appUtils.stringGenerator((byte) 10,(byte) 3);
            String saltedPassword=signupModel.getPassword()+salt;
            assert secret != null;


            //then ensure that the new info does not exist already in the DB(use email key)
            boolean detailExits= userInfoRepo.cannotSignup(signupModel.getEmail());

            //set response message
            if (!detailExits){

                //populate the userInfoModel and save the new info to the database(userInfo database)
                userInfoModel.setName(signupModel.getName());
                userInfoModel.setEmail(signupModel.getEmail());
                userInfoModel.setPasswordSalt(salt);
                userInfoModel.setPasswordHash(appUtils.hash(secret,saltedPassword));
                userInfoRepo.save(userInfoModel);

                responseAsString="{\n   \"isSuccessful\":true, \n   \"message\":\"user successfully created\"\n}";
            }
            else
                responseAsString="{\n   \"isSuccessful\":false, \n \"message\":\"this email is already tied to an account\" \n}";
        }
        else {
            JsonNode node= new ObjectMapper().convertValue(fieldsValidityMessages,JsonNode.class);
            responseAsString = String.format("{\n   \"isSuccessful\":false, \n \"message\":\"unable to create user\", \"errorDescription\":%s, \n \"errorCount\":%s \n}", node, fieldsValidityMessages.size());
        }

        //returns a jsonNode
        return new ObjectMapper().readValue(responseAsString,JsonNode.class);
    }
}
