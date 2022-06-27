package com.catalogue.service;

import com.catalogue.repository.RecordsRepo;
import com.catalogue.repository.UserInfoRepo;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DashboardDetailsService {

    @Autowired
    RecordsRepo recordsRepo;

    @Autowired
    UserInfoRepo userInfoRepo;

    public JsonNode dashboardDetailsResponse(String userId) throws JsonProcessingException {

        Map<String, Integer> dashboardDetails=new HashMap<>();

        int numberOfMaleRecords=recordsRepo.numberOfMaleRecords(userId,"male");
        int numberOfFemaleRecords=recordsRepo.numberOfFemaleRecords(userId,"female");

        dashboardDetails.put("numberOfMaleRecords",numberOfMaleRecords);
        dashboardDetails.put("numberOfFemaleRecords",numberOfFemaleRecords);
        dashboardDetails.put("totalNumberOfRecords",numberOfFemaleRecords+numberOfMaleRecords);

        JsonNode node= new ObjectMapper().convertValue(dashboardDetails,JsonNode.class);
       String responseAsString = String.format("{\n  \"dashboardDetails\":%s \n}", node);

        return new ObjectMapper().readValue(responseAsString,JsonNode.class);
    }
}