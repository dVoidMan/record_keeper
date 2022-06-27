package com.catalogue.service;

import com.catalogue.model.ChangePasswordModel;
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
public class ChangePasswordService {
    @Autowired
    UserInfoRepo userInfoRepo;

    @Autowired
    Environment environment;

    @Autowired
    AppUtils appUtils;

    @Autowired
    UserInfoModel userInfoModel;


    public JsonNode changePasswordResponse(ChangePasswordModel changePasswordModel) throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {

        String responseAsString;

        //be sure that new password and confirm password is valid
        boolean matches=changePasswordModel.getNewPassword().equals(changePasswordModel.getConfirmPassword());

        if (matches){

            //check the previous password(current password) is

            //gets the user info
            userInfoModel=userInfoRepo.getUserInfo_ID(changePasswordModel.getUserId());

            //gets user secret and salt from DB
            String secret=environment.getProperty("secret");
            String salt;

            try {
                salt= userInfoModel.getPasswordSalt();
            }
            catch (NullPointerException e){
                responseAsString="{\n \"isSuccessful\": false,\n \"message\": \"userID is invalid\"\n }";
                return new ObjectMapper().readValue(responseAsString,JsonNode.class);
            }

            //hash of previous password(current password) in DB
            String prevPasswordHashDB= userInfoModel.getPasswordHash();

            //hash of previous  password (current password) from user
            String saltedPassword= changePasswordModel.getCurrentPassword()+salt;
            assert secret != null;
            String passwordHash_user=appUtils.hash(secret,saltedPassword);

            //check if it matches
            boolean hashMatch=passwordHash_user.equals(prevPasswordHashDB);

            //means supplied password is correct
            if (hashMatch){

                //validates the new password
                String passwordValidityMessage= appUtils.validateField(changePasswordModel.getNewPassword(),"password");

                if (passwordValidityMessage.contains("valid")){

                    //generates new password for the
                    String newPasswordSalt=appUtils.stringGenerator((byte) 10,(byte) 3);
                    String saltedPasswordHash= changePasswordModel.getNewPassword()+newPasswordSalt;
                    String newPasswordHash=appUtils.hash(secret,saltedPasswordHash);

                    //calls a query
                    userInfoRepo.changePassword(newPasswordHash, newPasswordSalt,changePasswordModel.getUserId());

                    responseAsString="{\n   \"isSuccessful\":true, \n   \"message\":\"user password has been changed\"\n}";
                }
                else
                    responseAsString="{\n   \"isSuccessful\":true, \n   \"message\":\"password should contain at least one special character\"\n}";
            }
            else
                responseAsString="{\n   \"isSuccessful\":false, \n   \"message\":\"current password is incorrect\"\n}";
        }
        else
            responseAsString="{\n   \"isSuccessful\":false, \n   \"message\":\"mismatch for new and confirm password\"\n}";

        return new ObjectMapper().readValue(responseAsString,JsonNode.class);
    }
}
