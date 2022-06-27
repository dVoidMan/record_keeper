package com.catalogue.controller;

import com.catalogue.model.*;
import com.catalogue.service.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

@RestController
@RequestMapping("/recordKeeper")
public class Controller {

 @Autowired
 LoginService loginService;

 @Autowired
 SignUpService signUpService;

 @Autowired
 ChangePasswordService changePasswordService;

 @Autowired
 RecordsService recordsService;

 @Autowired
 DashboardDetailsService dashboardDetailsService;

 @Autowired
 ProfileService profileService;

 @Autowired
 EditProfile editProfile;

 @PostMapping("/login")
 public ResponseEntity<JsonNode> login (@RequestBody LoginModel loginModel)
         throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {
  return new ResponseEntity<>(loginService.logInResponse(loginModel),HttpStatus.OK);
 }

 @PostMapping("/signup")
 public ResponseEntity<JsonNode> signup(@RequestBody SignupModel signupModel)
         throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {

  return new ResponseEntity<>(signUpService.signupResponse(signupModel), HttpStatus.OK);
 }

 @PatchMapping("/changePassword/{userId}")
 public ResponseEntity<JsonNode> changePassword(
         @RequestBody ChangePasswordModel changePasswordModel, @PathVariable String userId)
         throws NoSuchAlgorithmException, InvalidKeyException, JsonProcessingException {

  changePasswordModel.setUserId(userId);
  return new ResponseEntity<>(changePasswordService.changePasswordResponse(changePasswordModel),HttpStatus.OK);
 }

 @GetMapping("/dashboardDetails/{userId}")
 public ResponseEntity<JsonNode> dashboardDetails(@PathVariable String userId) throws JsonProcessingException {
  return new ResponseEntity<>(dashboardDetailsService.dashboardDetailsResponse(userId),HttpStatus.OK);
 }

 @PostMapping("/addNewRecord/{userId}")
 public ResponseEntity<JsonNode> addNewRecord(@RequestBody RecordsModel recordsModel, @PathVariable String userId) throws JsonProcessingException {

  recordsModel.setUserId(userId);
  return new ResponseEntity<>(recordsService.addNewRecord(recordsModel), HttpStatus.OK);
 }

 @GetMapping("/profile/{userId}")
 public ResponseEntity<JsonNode> getUserProfile(@PathVariable String userId) throws JsonProcessingException {
  return new ResponseEntity<>(profileService.viewProfile(userId), HttpStatus.OK);
 }

 @GetMapping("/viewAddedRecords/{userId}")
 public ResponseEntity<JsonNode> viewAddedRecord(@PathVariable String userId, @RequestParam int recPageNumber) throws JsonProcessingException {
  return new ResponseEntity<>(recordsService.viewRecords(userId, recPageNumber), HttpStatus.OK);
 }

 @PutMapping("/editAddedRecords/{recordId}")
 public ResponseEntity<JsonNode> editAddedRecords (
         @PathVariable String recordId, @RequestBody RecordsModel recordsModel) throws JsonProcessingException {
  recordsModel.setId(recordId);
  return new ResponseEntity<>(recordsService.editRecord(recordId,recordsModel), HttpStatus.OK);
 }

 @PatchMapping(value = "/editProfile/{userId}")
 public ResponseEntity<JsonNode> editProfile(@PathVariable String userId,
                                             @RequestParam MultipartFile profilePhoto,String name,String email) throws IOException {
  editProfile.setUserId(userId);
  editProfile.setProfilePhoto(profilePhoto);
  editProfile.setName(name);
  editProfile.setEmail(email);
  return new ResponseEntity<>(profileService.editProfile(editProfile),HttpStatus.OK);
 }

 @DeleteMapping("/deleteRecord/{recordId}")
 public ResponseEntity<JsonNode> deleteRequest(@PathVariable String recordId) throws JsonProcessingException {
  return new ResponseEntity<>(recordsService.deleteRecord(recordId),HttpStatus.OK);
 }
}