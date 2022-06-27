package com.catalogue.service;

import com.catalogue.model.RecordsModel;
import com.catalogue.model.UserInfoModel;
import com.catalogue.others.AppUtils;
import com.catalogue.repository.RecordsRepo;
import com.catalogue.repository.UserInfoRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class RecordsService {

    @Autowired
    RecordsRepo recordsRepo;

    @Autowired
    AppUtils appUtils;

    @Autowired
    UserInfoModel userInfoModel;

    @Autowired
    UserInfoRepo userInfoRepo;

    public JsonNode addNewRecord(RecordsModel recordsModel) throws JsonProcessingException {

        //validate the record fields
        String responseAsString;

        //to validate the fields in the signup details
        Map<String, String> fieldsValidityMessages=new HashMap<>();

        //first check if the email does not exist in the database
        boolean emailExits= recordsRepo.cannotAddRecord(recordsModel.getEmail());

        if (!emailExits){

            //validate name
            String nameValidityMessage=appUtils.validateField(recordsModel.getName(), "name");
            if (!nameValidityMessage.contains("valid"))
                fieldsValidityMessages.put("name",nameValidityMessage);

            //validate email
            String emailValidityMessage=appUtils.validateField(recordsModel.getEmail(),"email");
            if(!emailValidityMessage.contains("valid"))
                fieldsValidityMessages.put("email",emailValidityMessage);

            //validate DOB
            String DOBValidityMessage=appUtils.validateField(recordsModel.getDateOfBirth(),"DOB");
            if (!DOBValidityMessage.contains("valid"))
                fieldsValidityMessages.put("dateOfBirth",DOBValidityMessage);

            //phone number validation
            String phoneNoValidityMessage=appUtils.validateField(recordsModel.getPhoneNumber(),"phone");
            if (!phoneNoValidityMessage.contains("valid"))
                fieldsValidityMessages.put("phoneNumber",phoneNoValidityMessage);

            //address validation
            String addressValidityMessage=appUtils.validateField(recordsModel.getAddress(),"address");
            if (!addressValidityMessage.contains("valid"))
                fieldsValidityMessages.put("address",addressValidityMessage);

            if (nameValidityMessage.contains("valid") && emailValidityMessage.contains("valid") &&
                    DOBValidityMessage.contains("valid") && phoneNoValidityMessage.contains("valid")
                    &&addressValidityMessage.contains("valid")){

                //save to database
                recordsRepo.save(recordsModel);

                responseAsString="{\n   \"isSuccessful\":true, \n   \"message\":\"new record successfully added\"\n}";
            }
            else{
                JsonNode node= new ObjectMapper().convertValue(fieldsValidityMessages,JsonNode.class);
                responseAsString=String.format("{\n   \"isSuccessful\":false, \n \"message\":\"unable to create record\", \n \"errorDescription\":%s, \n \"errorCount\":%s \n}", node, fieldsValidityMessages.size());
            }
        }
        else
            responseAsString="{\n   \"isSuccessful\":false, \n \"message\":\"this email is already tied to a record\" \n}";

        return new ObjectMapper().readValue(responseAsString,JsonNode.class);
    }
    String responseAsString;

    public JsonNode viewRecords (String userId, int recPageNum) throws JsonProcessingException {

        try {
            Page<RecordsModel> page=recordsRepo.getAddedRecords(PageRequest.of(recPageNum,5),userId);
            JsonNode node= new ObjectMapper().convertValue(page.getContent(),JsonNode.class);
            responseAsString=String.format("{\n \"isSuccessful\":\"true\", \n \"records\": %s}",node.toString());
            return new ObjectMapper().readValue(responseAsString,JsonNode.class);
        }
        catch (NullPointerException | JsonProcessingException e){
            responseAsString="{\n \"isSuccessful\":\"false\", \n \"message\": \"error fetching records\" \n}";
            return new ObjectMapper().readValue(responseAsString,JsonNode.class);
        }
    }

    public JsonNode editRecord (String recordId,RecordsModel recordsModel) throws JsonProcessingException {

        //gets the userId of the post
        String userId=recordsRepo.getARecord(recordId).getUserId();

        //validate name
        String nameValidityMessage=appUtils.validateField(recordsModel.getName(), "name");
        if (!nameValidityMessage.contains("valid")){
            responseAsString=String.format("{\n \"isSuccessful\": false,\n \"message\": \"%s\"\n }",nameValidityMessage);
            return new ObjectMapper().readValue(responseAsString,JsonNode.class);
        }

        //validate email
        String emailValidityMessage=appUtils.validateField(recordsModel.getEmail(), "email");
        if (!emailValidityMessage.contains("valid")){
            responseAsString=String.format("{\n \"isSuccessful\": false,\n \"message\": \"%s\"\n }",emailValidityMessage);
            return new ObjectMapper().readValue(responseAsString,JsonNode.class);
        }

        //validate phone
        String phoneValidityMessage=appUtils.validateField(recordsModel.getPhoneNumber(), "phone");
        if (!phoneValidityMessage.contains("valid")){
            responseAsString=String.format("{\n \"isSuccessful\": false,\n \"message\": \"%s\"\n }",phoneValidityMessage);
            return new ObjectMapper().readValue(responseAsString,JsonNode.class);
        }

        //validate DOB
        String DOBValidityMessage=appUtils.validateField(recordsModel.getDateOfBirth(), "DOB");
        if (!DOBValidityMessage.contains("valid")){
            responseAsString=String.format("{\n \"isSuccessful\": false,\n \"message\": \"%s\"\n }",DOBValidityMessage);
            return new ObjectMapper().readValue(responseAsString,JsonNode.class);
        }

        //validate address
        String addressValidityMessage=appUtils.validateField(recordsModel.getAddress(), "address");
        if (!addressValidityMessage.contains("valid")){
            responseAsString=String.format("{\n \"isSuccessful\": false,\n \"message\": \"%s\"\n }",addressValidityMessage);
            return new ObjectMapper().readValue(responseAsString,JsonNode.class);
        }

        //if code gets here, all is valid.Then
        recordsModel.setUserId(userId);

        recordsRepo.save(recordsModel);

        responseAsString="{\n \"isSuccessful\": true,\n \"message\": \"record successfully updated\"\n }";
        return new ObjectMapper().readValue(responseAsString,JsonNode.class);
    }

    public JsonNode deleteRecord (String recordId) throws JsonProcessingException {
        try {
            recordsRepo.deleteRecord(recordId);
            responseAsString="{\n \"isSuccessful\": true,\n \"message\": \"record successfully deleted\"\n }";
            return new ObjectMapper().readValue(responseAsString,JsonNode.class);
        } catch (Exception e) {
            e.printStackTrace();
            responseAsString="{\n \"isSuccessful\": false,\n \"message\": \"invalid recordID\" \n }";
            return new ObjectMapper().readValue(responseAsString,JsonNode.class);
        }
    }
}