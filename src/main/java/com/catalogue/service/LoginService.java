package com.catalogue.service;


import com.catalogue.model.LoginModel;
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

@Service
public class LoginService {

    @Autowired
    Environment environment;

    @Autowired
    UserInfoRepo userInfoRepo;

    @Autowired
    UserInfoModel userInfoModel;

    @Autowired
    AppUtils appUtils;

    public JsonNode logInResponse(LoginModel loginModel) throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {

        String responseAsString;

        //hashes the password to compare with database
        String secret=environment.getProperty("secret");
        String saltedProvidedPassword;
        userInfoModel=userInfoRepo.getUserInfo_Email(loginModel.getEmail());
        try {
            saltedProvidedPassword=loginModel.getPassword()+userInfoModel.getPasswordSalt();
        }
        catch (NullPointerException e){
            responseAsString="{\n \"isSuccessful\": false,\n \"message\": \"email or password is invalid\"\n }";
            return new ObjectMapper().readValue(responseAsString,JsonNode.class);
        }

        assert secret != null;
        String providedPasswordHash=appUtils.hash(secret,saltedProvidedPassword);

        //queries database with the login details
        if(userInfoRepo.canLogIn(loginModel.getEmail(), providedPasswordHash)){

            //sets session active for the user in the userInfoDetail
            userInfoModel.setInSession(true);

            //sets other useful values eg, name @profile
            loginModel.setUserFirstName(userInfoModel.getName().split(" ")[0]);

            //gives out the userId
            String userId=userInfoRepo.getUserInfo_Email(loginModel.getEmail()).getUserId();

            responseAsString=String.format("{\n \"isSuccessful\": true,\n \"message\": \"user logged in successfully\", \"userId\" : \"%s\", \"userFirstName\":\"%s\" \n}",userId,loginModel.getUserFirstName());
        }
        else
            responseAsString="{\n \"isSuccessful\": false,\n \"message\": \"email or password is invalid\"\n }";

        return new ObjectMapper().readValue(responseAsString,JsonNode.class);
    }
}